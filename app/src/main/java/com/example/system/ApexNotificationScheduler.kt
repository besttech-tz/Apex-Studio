package com.example.system

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.MainActivity
import com.example.R
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

data class OperationalCheckpoint(
    val id: Int,
    val code: String,
    val timeLabel: String,
    val hourOfDay: Int,
    val minute: Int,
    val title: String,
    val body: String,
    val enabled: Boolean = true
)

/**
 * Clean, user-controlled Daily Reminder Scheduler:
 * - Never arms background alarms or requests notification permission uninvited on first launch.
 * - Stores user-chosen custom times and per-reminder toggles so AlarmManager, BootReceiver,
 *   and WorkManager respect the exact hours & minutes the user selected.
 */
object ApexNotificationScheduler {

    const val CHANNEL_ID = "APEX_OPERATIONS"
    const val ACTION_APEX_OPERATION_ALARM = "com.example.action.APEX_OPERATION_ALARM"
    const val EXTRA_CHECKPOINT_ID = "extra_checkpoint_id"
    private const val WORK_NAME_WATCHDOG = "apex_operations_watchdog_work"
    private const val REMINDER_PREFS = "apex_custom_reminders_v1"

    const val ID_MORNING_PROTEIN = 1001
    const val ID_WORKOUT_TIME = 1003
    const val ID_EVENING_RESET = 1004

    val DAILY_CHECKPOINTS = listOf(
        OperationalCheckpoint(
            id = ID_MORNING_PROTEIN,
            code = "MORNING",
            timeLabel = "08:00 AM",
            hourOfDay = 8,
            minute = 0,
            title = "Morning Protein & Hydration",
            body = "Start strong: drink 500ml water and get 35–40g protein with breakfast."
        ),
        OperationalCheckpoint(
            id = ID_WORKOUT_TIME,
            code = "WORKOUT",
            timeLabel = "05:30 PM",
            hourOfDay = 17,
            minute = 30,
            title = "Workout Reminder",
            body = "Time for today's scheduled workout — or run a quick 7-min home session to stay consistent."
        ),
        OperationalCheckpoint(
            id = ID_EVENING_RESET,
            code = "EVENING",
            timeLabel = "08:30 PM",
            hourOfDay = 20,
            minute = 30,
            title = "Evening Recovery & Protein Check",
            body = "Hit your daily protein target, skip late-night sugar, and let your muscles recover."
        )
    )

    fun formatTime12h(hourOfDay: Int, minute: Int): String {
        val safeHour = ((hourOfDay % 24) + 24) % 24
        val safeMin = ((minute % 60) + 60) % 60
        val amPm = if (safeHour < 12) "AM" else "PM"
        val hour12 = when {
            safeHour == 0 -> 12
            safeHour > 12 -> safeHour - 12
            else -> safeHour
        }
        return String.format(Locale.US, "%02d:%02d %s", hour12, safeMin, amPm)
    }

    fun isArmedInPrefs(context: Context): Boolean {
        val prefs = context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean("armed", false)
    }

    fun setArmedInPrefs(context: Context, armed: Boolean) {
        context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean("armed", armed)
            .apply()
    }

    fun saveCustomCheckpointConfig(
        context: Context,
        checkpointId: Int,
        hourOfDay: Int,
        minute: Int,
        enabled: Boolean
    ) {
        val safeH = ((hourOfDay % 24) + 24) % 24
        val safeM = ((minute % 60) + 60) % 60
        context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt("hour_$checkpointId", safeH)
            .putInt("min_$checkpointId", safeM)
            .putBoolean("enabled_$checkpointId", enabled)
            .apply()
    }

    fun getConfiguredCheckpoints(context: Context): List<OperationalCheckpoint> {
        val prefs = context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
        return DAILY_CHECKPOINTS.map { base ->
            val h = prefs.getInt("hour_${base.id}", base.hourOfDay).coerceIn(0, 23)
            val m = prefs.getInt("min_${base.id}", base.minute).coerceIn(0, 59)
            val en = prefs.getBoolean("enabled_${base.id}", true)
            base.copy(
                hourOfDay = h,
                minute = m,
                timeLabel = formatTime12h(h, m),
                enabled = en
            )
        }
    }

    fun ensureNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_apex_operations_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_apex_operations_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0L, 350L, 120L, 500L)
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(channel)
        }
    }

    fun syncScheduledRemindersIfArmed(context: Context) {
        if (isArmedInPrefs(context)) {
            scheduleAllDailyOperations(context)
        } else {
            cancelAllDailyOperations(context)
        }
    }

    fun scheduleAllDailyOperations(context: Context) {
        ensureNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val configured = getConfiguredCheckpoints(context)
        var anyEnabled = false
        configured.forEach { checkpoint ->
            if (checkpoint.enabled) {
                anyEnabled = true
                scheduleSingleCheckpoint(context, checkpoint)
            } else {
                alarmManager?.cancel(buildPendingIntent(context, checkpoint.id))
            }
        }
        if (anyEnabled) {
            enqueueWatchdogWorker(context)
        } else {
            try {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_WATCHDOG)
            } catch (_: Exception) {
            }
        }
    }

    fun scheduleSingleCheckpoint(context: Context, checkpoint: OperationalCheckpoint) {
        if (!checkpoint.enabled) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = buildPendingIntent(context, checkpoint.id)

        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, checkpoint.hourOfDay)
            set(Calendar.MINUTE, checkpoint.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now) || timeInMillis <= now.timeInMillis + 1000L) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    target.timeInMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                target.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelAllDailyOperations(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        (DAILY_CHECKPOINTS.map { it.id } + 1002).forEach { id ->
            val pendingIntent = buildPendingIntent(context, id)
            alarmManager.cancel(pendingIntent)
        }
        try {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_WATCHDOG)
        } catch (_: Exception) {
        }
    }

    private fun enqueueWatchdogWorker(context: Context) {
        try {
            val request = PeriodicWorkRequestBuilder<ApexOperationWorker>(6, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME_WATCHDOG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        } catch (_: Exception) {
        }
    }

    private fun buildPendingIntent(context: Context, checkpointId: Int): PendingIntent {
        val intent = Intent(context, ApexOperationReceiver::class.java).apply {
            action = ACTION_APEX_OPERATION_ALARM
            putExtra(EXTRA_CHECKPOINT_ID, checkpointId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, checkpointId, intent, flags)
    }

    fun dispatchOperationNotification(context: Context, checkpoint: OperationalCheckpoint): Boolean {
        ensureNotificationChannel(context)
        TacticalAudioHaptics.triggerRestCompleteAlarm(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                return false
            }
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            checkpoint.id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("${checkpoint.title} (${checkpoint.timeLabel})")
            .setContentText(checkpoint.body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(checkpoint.body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(longArrayOf(0L, 350L, 120L, 500L))
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(checkpoint.id, notification)
            true
        } catch (_: SecurityException) {
            false
        }
    }
}
