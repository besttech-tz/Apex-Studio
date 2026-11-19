package com.example.system

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.domain.ExperienceLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Hardware-level haptics, acoustic ticks, and Motivational Personal Trainer Voice Coach.
 * Coordinates seamlessly with WorkoutMusicEngine (automatic audio ducking) so Music + Voice
 * work together like a live studio workout partner.
 */
object TacticalAudioHaptics {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isVibrationMuted: Boolean = false

    @Volatile
    var isVoiceCoachMuted: Boolean = false

    private fun getVibrator(context: Context): Vibrator? {
        if (isVibrationMuted) return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                manager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun triggerClickPulse(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(25L, 140))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(25L)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Gentle tactile pulse at the start of each guided rep so the user feels the rhythm in their hand/pocket.
     */
    fun triggerRepBeatPulse(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20L, 125))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20L)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Crisp micro-tick at the midpoint turnaround of each rep (transitioning between down & drive up)
     * so the user feels the coach's directional cue even without looking at the screen.
     */
    fun triggerTurnaroundPulse(context: Context) {
        val vibrator = getVibrator(context) ?: return
        try {
            if (!vibrator.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(14L, 90))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(14L)
            }
        } catch (_: Exception) {
        }
    }

    fun triggerSetLoggedPulse(context: Context) {
        val vibrator = getVibrator(context)
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0L, 45L, 60L, 90L),
                            intArrayOf(0, 180, 0, 255),
                            -1
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0L, 45L, 60L, 90L), -1)
                }
            }
        } catch (_: Exception) {
        }
        playShortTone(ToneGenerator.TONE_PROP_ACK, 90)
    }

    fun triggerCountdownTick(context: Context) {
        val vibrator = getVibrator(context)
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(20L, 120))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(20L)
                }
            }
        } catch (_: Exception) {
        }
        playShortTone(ToneGenerator.TONE_PROP_BEEP, 75)
    }

    /**
     * Triggered when the Rest Engine reaches 00:00.
     */
    fun triggerRestCompleteAlarm(context: Context) {
        val vibrator = getVibrator(context)
        try {
            if (vibrator?.hasVibrator() == true) {
                val pattern = longArrayOf(0L, 260L, 100L, 340L)
                val amplitudes = intArrayOf(0, 255, 0, 255)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, -1)
                }
            }
        } catch (_: Exception) {
        }

        if (!isVoiceCoachMuted) {
            audioScope.launch {
                try {
                    val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 95)
                    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 160)
                    delay(200)
                    toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 210)
                    delay(240)
                    toneGen.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun playShortTone(toneType: Int, durationMs: Int) {
        if (isVoiceCoachMuted) return
        audioScope.launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                toneGen.startTone(toneType, durationMs)
                delay(durationMs.toLong() + 40L)
                toneGen.release()
            } catch (_: Exception) {
            }
        }
    }

    // =========================================================================
    // MOTIVATIONAL PERSONAL COACH VOICE ENGINE (Android TextToSpeech)
    // =========================================================================
    @Volatile
    private var textToSpeech: android.speech.tts.TextToSpeech? = null

    @Volatile
    private var isTtsInitialized = false

    fun initVoiceCoach(context: Context) {
        if (textToSpeech != null) return
        try {
            textToSpeech = android.speech.tts.TextToSpeech(context.applicationContext) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    textToSpeech?.language = java.util.Locale.US
                    textToSpeech?.setSpeechRate(1.03f)
                    textToSpeech?.setPitch(0.98f)
                    isTtsInitialized = true
                }
            }
        } catch (_: Exception) {
        }
    }

    fun syncCoachSpeechSpeed(experience: ExperienceLevel) {
        try {
            if (isTtsInitialized) {
                val rate = when (experience) {
                    ExperienceLevel.NEW_BEGINNER -> 1.02f
                    ExperienceLevel.BEGINNER_JOINT_SAFE -> 1.04f
                    ExperienceLevel.INTERMEDIATE -> 1.06f
                }
                textToSpeech?.setSpeechRate(rate)
            }
        } catch (_: Exception) {
        }
    }

    fun speakTacticalCue(
        context: Context,
        text: String,
        queueMode: Int = android.speech.tts.TextToSpeech.QUEUE_FLUSH,
        duckDurationMs: Long = (text.length * 62L).coerceIn(700L, 3600L)
    ) {
        if (isVoiceCoachMuted) return
        if (textToSpeech == null) {
            initVoiceCoach(context)
        }
        WorkoutMusicEngine.duckForVoice(duckDurationMs)
        try {
            if (isTtsInitialized) {
                textToSpeech?.speak(text, queueMode, null, "coach_cue_${System.currentTimeMillis()}")
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Spoken during the "READY TO GO!" screen so the user hears the exact exercise & duration/reps
     * while watching the human coach demonstrate.
     */
    fun triggerGetReadyLeadInVoice(
        context: Context,
        setNumber: Int,
        exerciseName: String,
        isTimed: Boolean,
        targetCount: Int,
        experience: ExperienceLevel,
        setupTip: String = "",
        isFirstInSession: Boolean = false
    ) {
        syncCoachSpeechSpeed(experience)
        val amountPhrase = if (isTimed) "$targetCount seconds" else "$targetCount reps"
        val text = when {
            isFirstInSession && setNumber == 1 -> "The first: $amountPhrase, $exerciseName."
            setNumber > 1 -> "Set $setNumber: $amountPhrase, $exerciseName."
            else -> "Next: $amountPhrase, $exerciseName."
        }
        speakTacticalCue(
            context = context,
            text = text,
            duckDurationMs = 2300L
        )
    }

    /**
     * Spoken right when transitioning into the active exercise:
     * - Timed exercise: "Start! 30 seconds, Jumping Jacks."
     * - Rep exercise: crisp "Go!" so Rep 1 ("One") counts on beat without speech overlap.
     */
    fun triggerActiveExerciseKickoffVoice(
        context: Context,
        exerciseName: String,
        isTimed: Boolean,
        targetCount: Int
    ) {
        if (isTimed) {
            speakTacticalCue(
                context = context,
                text = "Start! $targetCount seconds, $exerciseName.",
                duckDurationMs = 1900L
            )
        } else {
            speakTacticalCue(
                context = context,
                text = "Go!",
                duckDurationMs = 550L
            )
        }
    }

    /**
     * Spoken ~5 seconds into a timed exercise (just like a live studio trainer coaching breathing & form).
     */
    fun triggerLiveFormAndBreathingVoice(
        context: Context,
        spokenTip: String
    ) {
        if (spokenTip.isBlank()) return
        speakTacticalCue(
            context = context,
            text = spokenTip,
            duckDurationMs = (spokenTip.length * 58L).coerceIn(1600L, 3400L)
        )
    }

    /**
     * Live rep-by-rep personal trainer voice counting synced with the human coach's movement.
     * Every phrase is rhythm-budgeted (< 1.1s) so every rep from 1..N speaks clearly without clipping.
     */
    fun triggerGuidedRepCountVoice(
        context: Context,
        repNumber: Int,
        totalReps: Int,
        driveCue: String = ""
    ) {
        val halfway = (totalReps / 2).coerceAtLeast(4)
        val phrase = when {
            repNumber == 1 -> "One"
            repNumber == 2 -> "Two"
            repNumber == 3 && totalReps >= 7 -> "Three, nice pace!"
            repNumber == totalReps -> "$repNumber!"
            totalReps >= 6 && repNumber == halfway -> "$repNumber, halfway!"
            totalReps >= 5 && repNumber == totalReps - 1 -> "$repNumber, one more!"
            else -> "$repNumber"
        }
        val duckMs = if (phrase.length <= 5) 550L else 1200L
        speakTacticalCue(context, phrase, duckDurationMs = duckMs)
    }

    /**
     * Live encouragement & countdown for timed holds / cardio exercises ("30 seconds left, good job!").
     */
    fun triggerGuidedTimedMilestoneVoice(context: Context, remainingSec: Int, totalSec: Int) {
        when {
            remainingSec == totalSec / 2 && totalSec >= 24 ->
                speakTacticalCue(context, "Halfway there! Keep your rhythm!", duckDurationMs = 1900L)
            remainingSec == 10 && totalSec >= 22 ->
                speakTacticalCue(context, "10 seconds left! Almost there!", duckDurationMs = 1800L)
            remainingSec in 1..3 ->
                triggerCountdownVoice(context, remainingSec)
        }
    }

    fun triggerWorkoutStartVoice(context: Context, exerciseName: String) {
        speakTacticalCue(context, "Let's train together! First up: $exerciseName. Ready in 3, 2, 1, Go!")
    }

    fun triggerSetCompletedVoice(context: Context, setNumber: Int, nextLabel: String, restSeconds: Int = 60) {
        speakTacticalCue(
            context = context,
            text = "Awesome job on Set $setNumber! Breathe and recover for $restSeconds seconds. $nextLabel.",
            duckDurationMs = 3100L
        )
    }

    /**
     * Spoken during rest to keep the user engaged and ready for the next exercise.
     */
    fun triggerRestNextExerciseReminderVoice(
        context: Context,
        remainingSec: Int,
        nextExerciseName: String
    ) {
        when (remainingSec) {
            30 -> speakTacticalCue(
                context = context,
                text = "30 seconds left, good job! Shake it out — next up is $nextExerciseName.",
                duckDurationMs = 2700L
            )
            15 -> speakTacticalCue(
                context = context,
                text = "15 seconds left. Get into position for $nextExerciseName.",
                duckDurationMs = 2400L
            )
        }
    }

    fun triggerCountdownVoice(context: Context, number: Int) {
        val word = when (number) {
            3 -> "Three"
            2 -> "Two"
            1 -> "One"
            0 -> "Let's go!"
            else -> "$number"
        }
        speakTacticalCue(context, word, duckDurationMs = 700L)
    }

    fun triggerRestCompleteVoice(context: Context, nextExercise: String) {
        speakTacticalCue(context, "Rest complete! Let's hit $nextExercise together!", duckDurationMs = 2200L)
    }

    fun triggerSetStartVoice(context: Context, setNumber: Int, exerciseName: String, isTimed: Boolean, targetCount: Int) {
        val targetPhrase = if (isTimed) "$targetCount seconds together" else "$targetCount reps together"
        speakTacticalCue(context, "Set $setNumber, $exerciseName, $targetPhrase. Ready, go!")
    }

    fun triggerMidSetFormVoice(context: Context, shortCue: String) {
        speakTacticalCue(context, "Halfway there, good job! $shortCue.")
    }

    fun triggerFinalRepsVoice(context: Context) {
        speakTacticalCue(context, "Final two reps! Stay strong and squeeze.")
    }

    fun triggerReadyToGoVoice(context: Context, exerciseName: String, prepSeconds: Int) {
        speakTacticalCue(context, "Ready to go! $prepSeconds seconds to get into position. First up: $exerciseName.")
    }

    fun triggerRemainingSecondsVoice(context: Context, remainingSec: Int) {
        speakTacticalCue(context, "$remainingSec seconds left! Good job, stay locked in!")
    }

    fun triggerCravingDefeatedVoice(context: Context) {
        playShortTone(ToneGenerator.TONE_PROP_ACK, 120)
        speakTacticalCue(context, "Craving defeated! Great discipline — your body goal stays right on track.")
    }

    fun triggerTargetRepsCompleteVoice(context: Context) {
        playShortTone(ToneGenerator.TONE_PROP_ACK, 110)
        speakTacticalCue(context, "Awesome set! All reps done. Let's rest and recover.")
    }

    fun releaseVoiceCoach() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            isTtsInitialized = false
        } catch (_: Exception) {
        }
    }
}
