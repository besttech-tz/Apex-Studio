package com.example.system

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tanh
import kotlin.random.Random

/**
 * High-energy workout soundtracks with distinct athletic genres and clean titles (zero BPM jargon).
 */
enum class WorkoutMusicTrack(
    val id: String,
    val title: String,
    val shortLabel: String,
    val bpm: Int,
    val vibeSubtitle: String
) {
    POWER_DRIVE(
        id = "POWER_DRIVE",
        title = "High Voltage",
        shortLabel = "Hype",
        bpm = 130,
        vibeSubtitle = "Festival gym drop with pumping bass & stadium synth hook"
    ),
    FOCUS_GROOVE(
        id = "FOCUS_GROOVE",
        title = "Power Bounce",
        shortLabel = "Bounce",
        bpm = 108,
        vibeSubtitle = "Hard hip-hop 808 bounce, brass stabs & crisp trap hats"
    ),
    RECOVERY_FLOW(
        id = "RECOVERY_FLOW",
        title = "Afro Pulse",
        shortLabel = "Pulse",
        bpm = 118,
        vibeSubtitle = "Upbeat Afro-fitness log-drum groove & bright stereo rhythm"
    );

    companion object {
        fun fromId(raw: String?): WorkoutMusicTrack =
            entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: POWER_DRIVE
    }
}

data class WorkoutMusicState(
    val selectedTrack: WorkoutMusicTrack = WorkoutMusicTrack.POWER_DRIVE,
    val isPlaying: Boolean = false,
    val musicAllowed: Boolean = true,
    val autoPlayDuringSets: Boolean = true
)

/**
 * True Stereo (44.1 kHz 16-bit Stereo PCM) Multi-Genre Workout Synthesizer:
 * - PolyBLEP anti-aliased multi-voice stereo Supersaws, Brass Stabs & Kalimba Plucks
 * - 2-Pole Resonant Filter Envelopes + Dotted-8th Stereo Ping-Pong Delay Echo
 * - Punchy Club / 808 Sub-Kicks, 3-Tap Stereo Handclaps, Off-Beat Open Hi-Hats & Trap Rolls
 * - 8-Bar Evolving Catchy Melodic Hooks + Rolling Basslines (never a flat single-note beep!)
 * - Smooth sidechain pumping + automatic ducking when the Voice Coach speaks
 */
object WorkoutMusicEngine {

    private const val SAMPLE_RATE = 44100
    private val engineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow(WorkoutMusicState())
    val state: StateFlow<WorkoutMusicState> = _state.asStateFlow()

    @Volatile
    private var playbackJob: Job? = null

    @Volatile
    private var duckUntilEpochMs: Long = 0L

    fun duckForVoice(durationMs: Long = 1350L) {
        val targetEnd = System.currentTimeMillis() + durationMs.coerceIn(300L, 4500L)
        if (targetEnd > duckUntilEpochMs) {
            duckUntilEpochMs = targetEnd
        }
    }

    fun setMusicAllowed(allowed: Boolean) {
        _state.update { it.copy(musicAllowed = allowed) }
        if (!allowed && _state.value.isPlaying) {
            stop()
        }
    }

    fun selectTrack(track: WorkoutMusicTrack, autoPlay: Boolean = false) {
        val wasPlaying = _state.value.isPlaying
        _state.update { it.copy(selectedTrack = track) }
        if ((autoPlay || wasPlaying) && _state.value.musicAllowed) {
            start(track)
        }
    }

    fun setAutoPlayDuringSets(enabled: Boolean) {
        _state.update { it.copy(autoPlayDuringSets = enabled) }
        if (!enabled && _state.value.isPlaying) {
            stop()
        }
    }

    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            stop()
        } else {
            if (!_state.value.musicAllowed) {
                _state.update { it.copy(musicAllowed = true) }
            }
            start(_state.value.selectedTrack)
        }
    }

    private fun midiToHz(midi: Double): Double = 440.0 * 2.0.pow((midi - 69.0) / 12.0)

    private fun wrapPhase(p: Double): Double = p - floor(p)

    private fun polyBlep(t: Double, dt: Double): Double {
        if (dt <= 0.0) return 0.0
        return when {
            t < dt -> {
                val x = t / dt
                x + x - x * x - 1.0
            }
            t > 1.0 - dt -> {
                val x = (t - 1.0) / dt
                x * x + x + x + 1.0
            }
            else -> 0.0
        }
    }

    private fun antiAliasedSaw(phase01: Double, dt: Double): Double {
        return (2.0 * phase01 - 1.0) - polyBlep(phase01, dt)
    }

    fun start(track: WorkoutMusicTrack = _state.value.selectedTrack) {
        if (!_state.value.musicAllowed) {
            stop()
            return
        }
        playbackJob?.cancel()
        _state.update { it.copy(selectedTrack = track, isPlaying = true) }

        playbackJob = engineScope.launch {
            var audioTrack: AudioTrack? = null
            try {
                val minBuf = AudioTrack.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (minBuf <= 0) {
                    _state.update { it.copy(isPlaying = false) }
                    return@launch
                }

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                            .build()
                    )
                    .setBufferSizeInBytes(max(minBuf * 2, 16384))
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack.play()

                val bpm = track.bpm.coerceIn(90, 145)
                val samplesPerStep = ((60.0 / bpm / 4.0) * SAMPLE_RATE).toInt().coerceAtLeast(1024)
                val stereoBuffer = ShortArray(samplesPerStep * 2)
                val rng = Random(404)

                // Dotted-eighth stereo ping-pong echo buffer (3 sixteenth-steps)
                val delaySamples = (samplesPerStep * 3).coerceIn(4096, SAMPLE_RATE)
                val delayL = DoubleArray(delaySamples)
                val delayR = DoubleArray(delaySamples)
                var delayPos = 0

                // =========================================================================
                // MUSICAL HOOKS & BASSLINES (MIDI note numbers; -1 = rest)
                // =========================================================================

                // 1. HIGH VOLTAGE (E Minor Festival Gym House — 130 BPM)
                // Progression: Em -> C -> G -> D (8-bar evolving stadium hook!)
                val hypeBassRoot = intArrayOf(40, 36, 43, 38, 40, 36, 45, 38) // E2, C2, G2, D2, E2, C2, A2, D2
                val hypeLeadBars = arrayOf(
                    // Bar 1 (Em): Catchy syncopated stadium lead
                    intArrayOf(64, -1, 64, 67, -1, 71, -1, 69, 67, -1, 64, -1, 67, -1, 71, -1),
                    // Bar 2 (C): Rising energy response
                    intArrayOf(72, -1, 71, -1, 67, -1, 64, -1, 67, 69, 71, -1, 69, -1, 67, -1),
                    // Bar 3 (G): Anthemic lift
                    intArrayOf(71, -1, 71, 74, -1, 76, -1, 74, 71, -1, 67, -1, 71, -1, 74, -1),
                    // Bar 4 (D): Turnaround hook
                    intArrayOf(74, -1, 72, 71, -1, 69, -1, 67, 69, -1, 71, 72, 74, -1, 76, -1),
                    // Bar 5 (Em): High-octave drop variation!
                    intArrayOf(76, -1, 76, 79, -1, 76, -1, 74, 71, -1, 67, -1, 71, -1, 74, -1),
                    // Bar 6 (C): Syncopated dance hook
                    intArrayOf(72, -1, 76, -1, 74, -1, 71, -1, 72, -1, 71, 69, 67, -1, 64, -1),
                    // Bar 7 (Am): Tension lift
                    intArrayOf(69, -1, 72, -1, 76, -1, 74, -1, 72, -1, 71, -1, 69, -1, 71, -1),
                    // Bar 8 (D): Big peak launch back into Bar 1
                    intArrayOf(74, -1, 74, 76, -1, 78, -1, 79, 78, -1, 76, -1, 74, 71, 69, 67)
                )
                val hypeChordMids = arrayOf(
                    intArrayOf(52, 55, 59), // Em
                    intArrayOf(48, 52, 55), // C
                    intArrayOf(55, 59, 62), // G
                    intArrayOf(50, 54, 57), // D
                    intArrayOf(52, 55, 59), // Em
                    intArrayOf(48, 52, 55), // C
                    intArrayOf(45, 52, 57), // Am
                    intArrayOf(50, 54, 57)  // D
                )

                // 2. POWER BOUNCE (C Minor Hard Hip-Hop / Trap Gym Anthem — 108 BPM)
                // Aggressive brass stabs + booming 808 bass glide + high bell counter-riff
                val bounce808Steps = arrayOf(
                    intArrayOf(36, -1, -1, 36, -1, -1, 36, -1, -1, -1, 39, -1, -1, -1, 41, -1),
                    intArrayOf(36, -1, -1, 36, -1, -1, 34, -1, -1, -1, 31, -1, -1, 34, 36, 43),
                    intArrayOf(32, -1, -1, 32, -1, -1, 32, -1, -1, -1, 34, -1, -1, -1, 36, -1),
                    intArrayOf(34, -1, -1, 34, -1, -1, 36, -1, 39, -1, 41, -1, 43, -1, 48, 43)
                )
                val bounceBrassStabs = arrayOf(
                    intArrayOf(60, -1, -1, 60, -1, -1, 63, -1, -1, -1, 65, -1, 63, -1, -1, -1),
                    intArrayOf(60, -1, -1, 60, -1, -1, 58, -1, -1, -1, 55, -1, 58, -1, 60, -1),
                    intArrayOf(56, -1, -1, 56, -1, -1, 60, -1, -1, -1, 63, -1, 62, -1, -1, -1),
                    intArrayOf(58, -1, -1, 62, -1, -1, 65, -1, -1, -1, 67, -1, 72, -1, 67, -1)
                )
                val bounceBellHook = intArrayOf(72, -1, 75, -1, 79, -1, 77, -1, 75, -1, 72, -1, 70, -1, 74, -1)

                // 3. AFRO PULSE (F Minor / Ab Major Upbeat Afro-Fitness Log-Drum Groove — 118 BPM)
                val afroLogDrumSteps = arrayOf(
                    intArrayOf(41, -1, -1, 41, -1, -1, 44, -1, -1, 41, -1, -1, 48, -1, 46, -1),
                    intArrayOf(37, -1, -1, 37, -1, -1, 44, -1, -1, 44, -1, -1, 48, -1, 44, -1),
                    intArrayOf(44, -1, -1, 44, -1, -1, 48, -1, -1, 46, -1, -1, 51, -1, 48, -1),
                    intArrayOf(39, -1, -1, 39, -1, -1, 43, -1, -1, 46, -1, 43, 46, -1, 51, -1)
                )
                val afroMarimbaBars = arrayOf(
                    intArrayOf(65, -1, 68, 72, -1, 68, 75, -1, 72, -1, 68, -1, 72, 75, -1, 72),
                    intArrayOf(65, -1, 68, 73, -1, 72, 68, -1, 65, -1, 68, -1, 72, -1, 75, -1),
                    intArrayOf(68, -1, 72, 75, -1, 79, 75, -1, 72, -1, 75, -1, 77, 75, -1, 72),
                    intArrayOf(67, -1, 70, 75, -1, 73, 70, -1, 67, -1, 70, 73, 75, -1, 79, -1)
                )

                var stepIndex = 0

                // Persistent oscillator phases (0..1) so transitions never click
                var bassPhase = 0.0
                var subPhase = 0.0
                var leadPhaseC = 0.0
                var leadPhaseL1 = 0.13
                var leadPhaseR1 = 0.37
                var leadPhaseL2 = 0.61
                var leadPhaseR2 = 0.83
                var chordP1 = 0.0
                var chordP2 = 0.25
                var chordP3 = 0.50
                var bellPhase = 0.0

                // Filter & envelope states
                var leadLpL1 = 0.0
                var leadLpL2 = 0.0
                var leadLpR1 = 0.0
                var leadLpR2 = 0.0
                var bassLp1 = 0.0
                var clapBpState = 0.0
                var hatHpStateL = 0.0
                var hatHpStateR = 0.0
                var currentBassHz = 55.0
                var activeLeadHz = 329.63
                var leadGateEnv = 0.0
                var duckEnvelope = 1.0

                while (
                    isActive &&
                    _state.value.musicAllowed &&
                    _state.value.selectedTrack == track &&
                    _state.value.isPlaying
                ) {
                    val stepInBar = stepIndex % 16
                    val bar8 = (stepIndex / 16) % 8
                    val bar4 = bar8 % 4

                    val isDuckingNow = System.currentTimeMillis() < duckUntilEpochMs
                    val targetDuck = if (isDuckingNow) 0.34 else 1.0

                    // =====================================================================
                    // PER-TRACK GROOVE & NOTE SCHEDULING
                    // =====================================================================
                    var kickTrigger = false
                    var clapTrigger = false
                    var closedHatTrigger = false
                    var openHatTrigger = false
                    var trapRatchetHat = false
                    var rimClaveTrigger = false
                    var bassTrigger = false
                    var targetBassHz = currentBassHz
                    var leadNoteMidi = -1
                    var bellNoteMidi = -1
                    var chordTriad: IntArray? = null

                    when (track) {
                        WorkoutMusicTrack.POWER_DRIVE -> {
                            val isTurnaroundFill = (bar4 == 3 && (stepInBar == 14 || stepInBar == 15))
                            kickTrigger = (stepInBar % 4 == 0) || isTurnaroundFill
                            clapTrigger = (stepInBar == 4 || stepInBar == 12) || (bar8 == 7 && stepInBar == 14)
                            openHatTrigger = (stepInBar % 4 == 2)
                            closedHatTrigger = !openHatTrigger

                            // Rolling off-beat & octave gym house bassline!
                            val rootMidi = hypeBassRoot[bar8]
                            if (stepInBar % 4 != 0) {
                                bassTrigger = true
                                val octaveLift = if (stepInBar % 4 == 2) 12 else 0
                                targetBassHz = midiToHz((rootMidi + octaveLift).toDouble())
                            } else {
                                bassTrigger = true
                                targetBassHz = midiToHz(rootMidi.toDouble())
                            }

                            leadNoteMidi = hypeLeadBars[bar8][stepInBar]
                            chordTriad = hypeChordMids[bar8]
                        }

                        WorkoutMusicTrack.FOCUS_GROOVE -> {
                            val bStep = bounce808Steps[bar4][stepInBar]
                            kickTrigger = (stepInBar == 0 || stepInBar == 6 || stepInBar == 10 || (bar4 == 3 && stepInBar == 14))
                            clapTrigger = (stepInBar == 4 || stepInBar == 12)
                            trapRatchetHat = (stepInBar == 7 || stepInBar == 15)
                            openHatTrigger = (stepInBar == 2 || stepInBar == 10)
                            closedHatTrigger = (stepInBar % 2 == 0) || trapRatchetHat

                            if (bStep > 0) {
                                bassTrigger = true
                                targetBassHz = midiToHz(bStep.toDouble())
                            }
                            leadNoteMidi = bounceBrassStabs[bar4][stepInBar]
                            if (bar8 >= 4) {
                                bellNoteMidi = bounceBellHook[stepInBar]
                            }
                        }

                        WorkoutMusicTrack.RECOVERY_FLOW -> {
                            kickTrigger = (stepInBar % 4 == 0)
                            rimClaveTrigger = (stepInBar == 0 || stepInBar == 3 || stepInBar == 6 || stepInBar == 10 || stepInBar == 12)
                            clapTrigger = (stepInBar == 4 || stepInBar == 12)
                            closedHatTrigger = true // Crisp 16th Afro shaker groove
                            openHatTrigger = (stepInBar == 2 || stepInBar == 14)

                            val logMidi = afroLogDrumSteps[bar4][stepInBar]
                            if (logMidi > 0) {
                                bassTrigger = true
                                targetBassHz = midiToHz(logMidi.toDouble())
                            }
                            leadNoteMidi = afroMarimbaBars[bar4][stepInBar]
                        }
                    }

                    if (leadNoteMidi > 0) {
                        activeLeadHz = midiToHz(leadNoteMidi.toDouble())
                        leadGateEnv = 1.0
                    }
                    val bellHz = if (bellNoteMidi > 0) midiToHz(bellNoteMidi.toDouble()) else 0.0

                    val cHz1 = chordTriad?.let { midiToHz(it[0].toDouble()) } ?: 0.0
                    val cHz2 = chordTriad?.let { midiToHz(it[1].toDouble()) } ?: 0.0
                    val cHz3 = chordTriad?.let { midiToHz(it[2].toDouble()) } ?: 0.0

                    var kickPhase = 0.0
                    var rimPhase = 0.0

                    for (i in 0 until samplesPerStep) {
                        val t = i.toDouble() / SAMPLE_RATE
                        duckEnvelope += (targetDuck - duckEnvelope) * 0.0022
                        currentBassHz += (targetBassHz - currentBassHz) * 0.0045

                        var left = 0.0
                        var right = 0.0

                        // -----------------------------------------------------------------
                        // 1. PUNCHY STUDIO KICK DRUM (Sub body + mid punch + transient click)
                        // -----------------------------------------------------------------
                        if (kickTrigger) {
                            val baseSub = if (track == WorkoutMusicTrack.FOCUS_GROOVE) 43.0 else 48.0
                            val sweepHz = baseSub + 135.0 * exp(-t * 46.0)
                            kickPhase += (2.0 * PI * sweepHz) / SAMPLE_RATE
                            val bodyEnv = exp(-t * (if (track == WorkoutMusicTrack.FOCUS_GROOVE) 8.5 else 12.5))
                            val punchClick = sin(2.0 * PI * 1250.0 * t) * exp(-t * 180.0) * 0.22
                            val kickSample = tanh(sin(kickPhase) * 1.45) * bodyEnv * 0.86 + punchClick
                            left += kickSample
                            right += kickSample
                        }

                        // -----------------------------------------------------------------
                        // 2. 3-TAP STEREO STUDIO HANDCLAP + SNARE SNAP
                        // -----------------------------------------------------------------
                        if (clapTrigger) {
                            val rawN = rng.nextDouble() * 2.0 - 1.0
                            clapBpState += 0.36 * (rawN - clapBpState)
                            val bandNoise = rawN - clapBpState * 0.55
                            val burstEnv = when {
                                t < 0.010 -> exp(-t * 130.0)
                                t < 0.021 -> exp(-(t - 0.010) * 130.0)
                                else -> exp(-(t - 0.021) * 20.0)
                            }
                            val snareBody = sin(2.0 * PI * 205.0 * t) * exp(-t * 38.0) * 0.28
                            val clapAmp = if (track == WorkoutMusicTrack.RECOVERY_FLOW) 0.24 else 0.40
                            val clapL = bandNoise * burstEnv * clapAmp + snareBody
                            val clapR = (bandNoise * 0.92 + clapBpState * 0.25) * burstEnv * clapAmp + snareBody
                            left += clapL
                            right += clapR
                        }

                        // -----------------------------------------------------------------
                        // 3. AFRO RIMSHOT / CLAVE KNOCK
                        // -----------------------------------------------------------------
                        if (rimClaveTrigger) {
                            rimPhase += (2.0 * PI * (690.0 + 380.0 * exp(-t * 95.0))) / SAMPLE_RATE
                            val rim = sin(rimPhase) * exp(-t * 52.0) * 0.22
                            left += rim * 0.85
                            right += rim * 1.15
                        }

                        // -----------------------------------------------------------------
                        // 4. CRISP STEREO HI-HATS, OFF-BEAT OPEN HATS & TRAP RATCHET ROLLS
                        // -----------------------------------------------------------------
                        if (closedHatTrigger || openHatTrigger) {
                            val nL = rng.nextDouble() * 2.0 - 1.0
                            val nR = rng.nextDouble() * 2.0 - 1.0
                            hatHpStateL += 0.14 * (nL - hatHpStateL)
                            hatHpStateR += 0.14 * (nR - hatHpStateR)
                            val hpL = nL - hatHpStateL
                            val hpR = nR - hatHpStateR

                            val localT = if (trapRatchetHat) {
                                // Triple-hit trap hi-hat roll inside a single 16th step!
                                val third = (samplesPerStep / 3).coerceAtLeast(1)
                                (i % third).toDouble() / SAMPLE_RATE
                            } else {
                                t
                            }

                            val hatDecay = when {
                                openHatTrigger -> 13.5
                                trapRatchetHat -> 68.0
                                track == WorkoutMusicTrack.RECOVERY_FLOW -> 46.0
                                else -> 55.0
                            }
                            val accent = when {
                                openHatTrigger -> 0.22
                                stepInBar % 2 == 1 -> 0.11
                                else -> 0.15
                            }
                            val hEnv = exp(-localT * hatDecay) * accent
                            left += hpL * hEnv * 1.08
                            right += hpR * hEnv * 0.94
                        }

                        // Pumping Sidechain Envelope on every Kick hit
                        val sidechain = if (kickTrigger) {
                            (0.14 + 0.86 * (1.0 - exp(-t * 14.0))).coerceIn(0.14, 1.0)
                        } else {
                            0.96
                        }

                        // -----------------------------------------------------------------
                        // 5. GENRE-SPECIFIC ENERGETIC BASSLINE (Saw-Sub / 808 / Log-Drum)
                        // -----------------------------------------------------------------
                        val bDt = currentBassHz / SAMPLE_RATE
                        bassPhase = wrapPhase(bassPhase + bDt)
                        subPhase = wrapPhase(subPhase + bDt * 0.5)

                        val bassSample = when (track) {
                            WorkoutMusicTrack.POWER_DRIVE -> {
                                val bEnv = if (bassTrigger) exp(-t * 7.5) else 0.45
                                val rawSaw = antiAliasedSaw(bassPhase, bDt)
                                val filtCut = (0.12 + 0.34 * exp(-t * 16.0)).coerceIn(0.05, 0.55)
                                bassLp1 += filtCut * (rawSaw - bassLp1)
                                val subSine = sin(2.0 * PI * bassPhase)
                                (bassLp1 * 0.58 + subSine * 0.62) * bEnv * sidechain * 0.50
                            }
                            WorkoutMusicTrack.FOCUS_GROOVE -> {
                                val env808 = if (bassTrigger) exp(-t * 3.2) else exp(-t * 1.8) * 0.65
                                val saturated808 = tanh(sin(2.0 * PI * bassPhase) * 2.1 + sin(4.0 * PI * bassPhase) * 0.35)
                                saturated808 * env808 * sidechain * 0.52
                            }
                            WorkoutMusicTrack.RECOVERY_FLOW -> {
                                // Percussive Amapiano / Afro Log-Drum pitch-drop knock!
                                if (bassTrigger) {
                                    val pitchDrop = 1.0 + 1.45 * exp(-t * 48.0)
                                    val logP = wrapPhase(bassPhase * pitchDrop)
                                    val knock = (sin(2.0 * PI * logP) * 0.78 + sin(6.0 * PI * logP) * exp(-t * 35.0) * 0.36)
                                    knock * exp(-t * 9.5) * sidechain * 0.54
                                } else {
                                    sin(2.0 * PI * bassPhase) * 0.16 * sidechain
                                }
                            }
                        }
                        left += bassSample
                        right += bassSample

                        // -----------------------------------------------------------------
                        // 6. CATCHY STEREO SYNTH LEAD / BRASS STABS / MARIMBA HOOK
                        // -----------------------------------------------------------------
                        leadGateEnv *= 0.99945
                        val dtLead = activeLeadHz / SAMPLE_RATE
                        leadPhaseC = wrapPhase(leadPhaseC + dtLead)
                        leadPhaseL1 = wrapPhase(leadPhaseL1 + dtLead * 0.9935)
                        leadPhaseR1 = wrapPhase(leadPhaseR1 + dtLead * 1.0065)
                        leadPhaseL2 = wrapPhase(leadPhaseL2 + dtLead * 0.9860)
                        leadPhaseR2 = wrapPhase(leadPhaseR2 + dtLead * 1.0140)

                        var dryLeadL = 0.0
                        var dryLeadR = 0.0

                        when (track) {
                            WorkoutMusicTrack.POWER_DRIVE -> {
                                // 5-Voice Stereo Detuned Supersaw Stadium Hook + Pumping Sidechain Chord Pad
                                val noteEnv = if (leadNoteMidi > 0) exp(-t * 6.8) else leadGateEnv * 0.55
                                val sawC = antiAliasedSaw(leadPhaseC, dtLead)
                                val sawL = antiAliasedSaw(leadPhaseL1, dtLead) * 0.75 + antiAliasedSaw(leadPhaseL2, dtLead) * 0.52
                                val sawR = antiAliasedSaw(leadPhaseR1, dtLead) * 0.75 + antiAliasedSaw(leadPhaseR2, dtLead) * 0.52

                                val fCut = (0.24 + 0.52 * exp(-t * 11.0)).coerceIn(0.12, 0.78)
                                leadLpL1 += fCut * ((sawC * 0.65 + sawL) - leadLpL1)
                                leadLpL2 += fCut * (leadLpL1 - leadLpL2)
                                leadLpR1 += fCut * ((sawC * 0.65 + sawR) - leadLpR1)
                                leadLpR2 += fCut * (leadLpR1 - leadLpR2)

                                dryLeadL = leadLpL2 * noteEnv * 0.25 * sidechain
                                dryLeadR = leadLpR2 * noteEnv * 0.25 * sidechain

                                // Warm Stereo Saw Chord Bed underneath
                                if (cHz1 > 0.0) {
                                    chordP1 = wrapPhase(chordP1 + cHz1 / SAMPLE_RATE)
                                    chordP2 = wrapPhase(chordP2 + (cHz2 * 1.003) / SAMPLE_RATE)
                                    chordP3 = wrapPhase(chordP3 + (cHz3 * 0.997) / SAMPLE_RATE)
                                    val padL = (antiAliasedSaw(chordP1, cHz1 / SAMPLE_RATE) + sin(2.0 * PI * chordP2)) * 0.065 * sidechain
                                    val padR = (antiAliasedSaw(chordP3, cHz3 / SAMPLE_RATE) + sin(2.0 * PI * chordP2)) * 0.065 * sidechain
                                    left += padL
                                    right += padR
                                }
                            }

                            WorkoutMusicTrack.FOCUS_GROOVE -> {
                                // Punchy Stereo Hip-Hop Brass Stabs + High Trap Bell Counter-Riff
                                if (leadNoteMidi > 0) {
                                    val brassAttack = (1.0 - exp(-t * 110.0)) * exp(-t * 9.5)
                                    val brL = antiAliasedSaw(leadPhaseC, dtLead) * 0.6 + antiAliasedSaw(leadPhaseL1, dtLead) * 0.75
                                    val brR = antiAliasedSaw(leadPhaseC, dtLead) * 0.6 + antiAliasedSaw(leadPhaseR1, dtLead) * 0.75
                                    val brCut = (0.14 + 0.58 * exp(-t * 14.0)).coerceIn(0.10, 0.75)
                                    leadLpL1 += brCut * (brL - leadLpL1)
                                    leadLpR1 += brCut * (brR - leadLpR1)
                                    dryLeadL = tanh(leadLpL1 * 1.5) * brassAttack * 0.30 * sidechain
                                    dryLeadR = tanh(leadLpR1 * 1.5) * brassAttack * 0.30 * sidechain
                                }
                                if (bellHz > 0.0) {
                                    bellPhase = wrapPhase(bellPhase + bellHz / SAMPLE_RATE)
                                    val fmBell = sin(2.0 * PI * bellPhase + sin(6.0 * PI * bellPhase) * exp(-t * 26.0) * 1.4)
                                    val bellSig = fmBell * exp(-t * 15.0) * 0.16
                                    dryLeadL += bellSig * 0.85
                                    dryLeadR += bellSig * 1.15
                                }
                            }

                            WorkoutMusicTrack.RECOVERY_FLOW -> {
                                // Bright Joyful Afro-Marimba / Kalimba Pluck Riff
                                if (leadNoteMidi > 0) {
                                    val kalimbaEnv = exp(-t * 14.0)
                                    val tineClick = sin(2.0 * PI * (leadPhaseC * 4.0)) * exp(-t * 75.0) * 0.32
                                    val marimbaL = (sin(2.0 * PI * leadPhaseL1) + tineClick) * kalimbaEnv * 0.28 * sidechain
                                    val marimbaR = (sin(2.0 * PI * leadPhaseR1) + tineClick) * kalimbaEnv * 0.28 * sidechain
                                    dryLeadL = marimbaL
                                    dryLeadR = marimbaR
                                }
                            }
                        }

                        // -----------------------------------------------------------------
                        // 7. DOTTED-8TH STEREO PING-PONG DELAY ECHO ON MELODY
                        // -----------------------------------------------------------------
                        val echoL = delayL[delayPos]
                        val echoR = delayR[delayPos]
                        delayL[delayPos] = (dryLeadR + echoR * 0.34).coerceIn(-1.0, 1.0)
                        delayR[delayPos] = (dryLeadL + echoL * 0.34).coerceIn(-1.0, 1.0)
                        delayPos++
                        if (delayPos >= delaySamples) delayPos = 0

                        left += dryLeadL + echoL * 0.24
                        right += dryLeadR + echoR * 0.24

                        // -----------------------------------------------------------------
                        // 8. MASTER ANALOG TAPE SATURATION & VOICE-DUCKING STAGE
                        // -----------------------------------------------------------------
                        val masterGain = 0.38 * duckEnvelope
                        val outL = tanh(left * 1.25) * masterGain
                        val outR = tanh(right * 1.25) * masterGain

                        stereoBuffer[i * 2] = (outL * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        stereoBuffer[i * 2 + 1] = (outR * Short.MAX_VALUE).toInt()
                            .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }

                    val written = audioTrack.write(stereoBuffer, 0, stereoBuffer.size)
                    if (written < 0) break
                    stepIndex++
                }
            } catch (_: Throwable) {
                // Safe no-op on headless JVM / unit tests without audio sink
            } finally {
                try {
                    audioTrack?.pause()
                    audioTrack?.flush()
                    audioTrack?.release()
                } catch (_: Throwable) {
                }
                if (playbackJob?.isCancelled != true) {
                    _state.update { it.copy(isPlaying = false) }
                }
            }
        }
    }

    fun stop() {
        _state.update { it.copy(isPlaying = false) }
        playbackJob?.cancel()
        playbackJob = null
    }

    fun openPhoneMusicApp(context: Context) {
        try {
            val intent = Intent.makeMainSelectorActivity(
                Intent.ACTION_MAIN,
                Intent.CATEGORY_APP_MUSIC
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallback = Intent(Intent.ACTION_VIEW).apply {
                    type = "audio/*"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            } catch (_: Exception) {
                // Gracefully ignore if no external music player is installed on emulator
            }
        }
    }
}
