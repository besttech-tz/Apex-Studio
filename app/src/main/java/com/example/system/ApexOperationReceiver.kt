package com.example.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ApexOperationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                ApexNotificationScheduler.syncScheduledRemindersIfArmed(context)
            }
            ApexNotificationScheduler.ACTION_APEX_OPERATION_ALARM -> {
                if (!ApexNotificationScheduler.isArmedInPrefs(context)) return
                val checkpointId = intent.getIntExtra(
                    ApexNotificationScheduler.EXTRA_CHECKPOINT_ID,
                    -1
                )
                val configured = ApexNotificationScheduler.getConfiguredCheckpoints(context)
                val checkpoint = configured.firstOrNull { it.id == checkpointId }
                    ?: configured.first()

                if (checkpoint.enabled) {
                    ApexNotificationScheduler.dispatchOperationNotification(context, checkpoint)
                    ApexNotificationScheduler.scheduleSingleCheckpoint(context, checkpoint)
                }
            }
        }
    }
}
