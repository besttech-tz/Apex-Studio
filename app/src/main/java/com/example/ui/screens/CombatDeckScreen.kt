package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.TrainingEnvironment
import com.example.domain.ApexCoachMovementSpec
import com.example.domain.CoachTruthAndSplitAdvisor
import com.example.domain.CombatDaySpec
import com.example.domain.CombatExerciseSpec
import com.example.domain.CombatSplitCatalog
import com.example.domain.BiologicalSex
import com.example.domain.ExerciseAnimationType
import com.example.domain.ExperienceLevel
import com.example.domain.HomeWorkoutCatalog
import com.example.domain.HomeWorkoutPlan
import com.example.domain.PersonalTrainingBlueprint
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.QuickStrikeWorkout
import com.example.domain.SessionStageType
import com.example.domain.WeeklySchedulePlanner
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutIntensityMode
import com.example.system.TacticalAudioHaptics
import com.example.system.WorkoutMusicEngine
import com.example.ui.ApexUiState
import com.example.ui.MidwayWorkoutSnapshot
import com.example.ui.RestTimerState
import com.example.ui.WorkoutLaunchMode
import com.example.ui.WorkoutOriginPage
import com.example.ui.components.AnimatedWorkoutCharacter
import com.example.ui.components.CompactMusicPill
import com.example.ui.components.ExerciseMasteryCoachDialog
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.FramedStatCapsule
import com.example.ui.components.HeroMovementAnimationContainer
import com.example.ui.components.WorkoutCoverResolver
import com.example.ui.components.WorkoutIntensitySelectorBar
import com.example.ui.components.WorkoutMusicStudioCard
import com.example.ui.components.resolveLiveCoachGuide
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.NominalGreen
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalBlueSoft
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.TextPrimarySlate
import com.example.ui.theme.TextSecondarySlate
import com.example.ui.theme.TimerAmber
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val ExerciseTitleDark = Color(0xFF0F172A)
private val BorderMuted = CardBorderLight

enum class ActiveSessionState {
    WORKOUT_HUB,
    PRE_WORKOUT_OVERVIEW,
    ACTIVE_SET,
    RESTING,
    SESSION_COMPLETED
}

data class ExerciseFallbackVariant(
    val name: String,
    val setsAndRepsLabel: String,
    val totalSets: Int,
    val repPrescription: String,
    val targetRepsCount: Int,
    val modality: String,
    val primaryMuscle: String,
    val secondaryMuscle: String,
    val singleFormTip: String,
    val breathingPattern: String,
    val commonMistake: String,
    val startPosition: String,
    val finishPosition: String,
    val cues: List<String>,
    val animationType: ExerciseAnimationType,
    val isTimedSeconds: Boolean = false,
    val isBodyweight: Boolean = false,
    val swapCategoryBadge: String = "Standard",
    val swapReasonNote: String = "",
    val loadMultiplier: Float = 1.0f
)

private data class SessionPlaylistItem(
    val stageType: SessionStageType,
    val stageLocalIndex: Int,
    val stageTotalCount: Int,
    val spec: CombatExerciseSpec,
    val quickStrikeIndex: Int = -1
)

private fun inferQuickStrikeStage(badge: String): SessionStageType {
    val lower = badge.lowercase()
    return when {
        "warm-up" in lower || "wake up" in lower || "blood flow" in lower ->
            SessionStageType.PRE_WARMUP
        "stretch" in lower || "cooldown" in lower || "cool-down" in lower ->
            SessionStageType.POST_STRETCH
        else ->
            SessionStageType.MAIN_WORKOUT
    }
}

/**
 * WORKOUT LAUNCH FLOW & 3-STAGE HUMAN PERSONAL COACH ENGINE:
 * - Stage 1: Pre-Gym Dynamic Warm-Up (2 moves: light pulse raiser + joint opener)
 * - Stage 2: Main Muscle Combination Sets (exact decisive rep counts, zero ranges)
 * - Stage 3: Post-Gym Static Posture Stretch (2 × 30s holds so muscles grow tall & loose)
 * - Visually distinct Phase UI so users immediately know whether they are in Warm-Up, Main Workout, or Stretch.
 * - Back button ALWAYS returns directly to Home Dashboard.
 */
@Composable
fun CombatDeckScreen(
    uiState: ApexUiState,
    restTimerState: RestTimerState = RestTimerState(),
    workoutPlan: HomeWorkoutPlan = HomeWorkoutCatalog.CHALLENGE_DAY_1_PLAN,
    progressionOffsets: Map<String, Float> = emptyMap(),
    onSelectDay: (Int) -> Unit = {},
    onSelectTrainingEnvironment: (TrainingEnvironment) -> Unit = {},
    onSelectWorkoutIntensity: (WorkoutIntensityMode) -> Unit = {},
    onSelectAudioCoachMode: (WorkoutAudioCoachMode) -> Unit = {},
    onToggleRestDay: (Int) -> Unit = {},
    onSelectRotationWeek: (Int) -> Unit = {},
    onStartQuickStrikeFromHub: (QuickStrikeWorkout) -> Unit = {},
    onOpenScheduledBriefingFromHub: (Int) -> Unit = {},
    onReturnToWorkoutHub: () -> Unit = {},
    onSelectExperienceLevel: (ExperienceLevel) -> Unit = {},
    onToggleSet: (String, String, String, Int, Float) -> Unit = { _, _, _, _, _ -> },
    onRecordSetEffortAndProgression: (String, Float) -> Unit = { _, _ -> },
    onLogSetAndRest: (CombatExerciseSpec) -> Unit = {},
    onStartManualRest: () -> Unit = {},
    onAdjustRestMillis: (Long) -> Unit = {},
    onForceZeroRestAlarm: () -> Unit = {},
    onDismissRestTimer: () -> Unit = {},
    onResetDay: () -> Unit = {},
    onScheduledWorkoutCompleted: () -> Unit = {},
    onRescueWorkoutCompleted: () -> Unit = {},
    onQuickPackOrChallengeCompleted: (QuickStrikeWorkout, Int?, Int, Int) -> Unit = { _, _, _, _ -> },
    onSaveMidwayWorkout: (MidwayWorkoutSnapshot) -> Unit = {},
    onClearMidwayWorkout: () -> Unit = {},
    onResumeMidwayFromHub: () -> Unit = {},
    onRestartMidwayFromHub: () -> Unit = {},
    onCompleteMidwayFromHub: () -> Unit = {},
    onNavigateBackHome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val quickStrike: QuickStrikeWorkout? = uiState.activeQuickStrike
    val challengeTotalDays = quickStrike?.resolvedChallengeDays ?: 0
    val isMultiDayChallenge = challengeTotalDays > 0
    val completedChallengeDays: Set<Int> = if (isMultiDayChallenge && quickStrike != null) {
        uiState.profile.challengeCompletedDays[quickStrike.id].orEmpty()
    } else {
        emptySet()
    }
    val nextUnfinishedChallengeDay = remember(quickStrike?.id, completedChallengeDays, challengeTotalDays) {
        if (challengeTotalDays > 0) {
            (1..challengeTotalDays).firstOrNull { it !in completedChallengeDays } ?: challengeTotalDays
        } else {
            1
        }
    }
    var selectedChallengeDay by rememberSaveable(quickStrike?.id ?: "scheduled", nextUnfinishedChallengeDay) {
        mutableIntStateOf(nextUnfinishedChallengeDay)
    }

    var completedDaySnapshot by remember { mutableStateOf<CombatDaySpec?>(null) }
    val sessionDay = completedDaySnapshot ?: uiState.activeCombatDay
    val nextScheduledDayTitle = remember(uiState.availableCombatDays, sessionDay) {
        val days = uiState.availableCombatDays
        if (days.isNotEmpty()) {
            val next = days[(sessionDay.dayIndex + 1) % days.size]
            "${next.code}: ${next.title}"
        } else {
            null
        }
    }

    val sessionKey = quickStrike?.id ?: sessionDay.id
    val expLevel = uiState.profile.experienceLevel
    val intensityMode = uiState.profile.workoutIntensity
    val activeTrackWeek = uiState.fourWeekTrack.activeViewedWeek
    val daysPerWeek = uiState.profile.weeklyFrequency.daysCount
    val initialChallengeLevel = when (quickStrike?.id) {
        "track_gym_calisthenics_hybrid",
        "track_female_calisthenics_sculpt",
        "challenge_14d_calisthenics_mastery",
        "challenge_14d_handstand_lsit_skill" ->
            if (expLevel == ExperienceLevel.INTERMEDIATE) expLevel else ExperienceLevel.BEGINNER_JOINT_SAFE
        "track_home_calisthenics_strength",
        "track_calisthenics_stage4_elite",
        "challenge_28d_calisthenics_elite_skills" ->
            ExperienceLevel.INTERMEDIATE
        else -> expLevel
    }
    var challengeLevel by rememberSaveable(sessionKey, uiState.workoutLaunchNonce) {
        mutableStateOf(initialChallengeLevel)
    }

    val activeEnvironment = uiState.profile.trainingEnvironment
    val includeWarmupStretch = uiState.profile.includeWarmupAndStretch
    val allQuickStrikeSpecs = remember(quickStrike) {
        quickStrike?.exercises?.mapIndexed { idx, qsSpec -> qsSpec.toCombatExerciseSpec(idx) }.orEmpty()
    }
    val warmupSpecs = remember(quickStrike, allQuickStrikeSpecs, sessionDay, includeWarmupStretch, expLevel, activeEnvironment) {
        when {
            quickStrike != null -> allQuickStrikeSpecs.filterIndexed { idx, _ ->
                inferQuickStrikeStage(quickStrike.exercises[idx].stationBadge) == SessionStageType.PRE_WARMUP
            }
            !includeWarmupStretch -> emptyList()
            else -> CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(sessionDay, expLevel, activeEnvironment)
        }
    }
    val mainSpecs = remember(quickStrike, allQuickStrikeSpecs, sessionDay) {
        if (quickStrike != null) {
            val mainOnly = allQuickStrikeSpecs.filterIndexed { idx, _ ->
                inferQuickStrikeStage(quickStrike.exercises[idx].stationBadge) == SessionStageType.MAIN_WORKOUT
            }
            mainOnly.ifEmpty { allQuickStrikeSpecs }
        } else {
            sessionDay.exercises
        }
    }
    val stretchSpecs = remember(quickStrike, allQuickStrikeSpecs, sessionDay, includeWarmupStretch) {
        when {
            quickStrike != null -> allQuickStrikeSpecs.filterIndexed { idx, _ ->
                inferQuickStrikeStage(quickStrike.exercises[idx].stationBadge) == SessionStageType.POST_STRETCH
            }
            !includeWarmupStretch -> emptyList()
            else -> CoachTruthAndSplitAdvisor.getPostWorkoutStretchSpecs(sessionDay)
        }
    }

    val playlist: List<SessionPlaylistItem> = remember(quickStrike, allQuickStrikeSpecs, warmupSpecs, mainSpecs, stretchSpecs) {
        if (quickStrike != null) {
            val stages = quickStrike.exercises.map { inferQuickStrikeStage(it.stationBadge) }
            val countsByStage = stages.groupingBy { it }.eachCount()
            val seenByStage = mutableMapOf<SessionStageType, Int>()
            allQuickStrikeSpecs.mapIndexed { idx, s ->
                val st = stages[idx]
                val localIdx = seenByStage.getOrDefault(st, 0)
                seenByStage[st] = localIdx + 1
                SessionPlaylistItem(
                    stageType = st,
                    stageLocalIndex = localIdx,
                    stageTotalCount = countsByStage[st] ?: 1,
                    spec = s,
                    quickStrikeIndex = idx
                )
            }
        } else {
            buildList {
                warmupSpecs.forEachIndexed { idx, s ->
                    add(SessionPlaylistItem(SessionStageType.PRE_WARMUP, idx, warmupSpecs.size, s))
                }
                mainSpecs.forEachIndexed { idx, s ->
                    add(SessionPlaylistItem(SessionStageType.MAIN_WORKOUT, idx, mainSpecs.size, s))
                }
                stretchSpecs.forEachIndexed { idx, s ->
                    add(SessionPlaylistItem(SessionStageType.POST_STRETCH, idx, stretchSpecs.size, s))
                }
            }
        }
    }

    val firstMainPlaylistIndex = playlist.indexOfFirst { it.stageType == SessionStageType.MAIN_WORKOUT }
        .let { if (it >= 0) it else 0 }

    val initialMidwayMatch = uiState.midwayWorkout?.takeIf {
        uiState.workoutLaunchMode == WorkoutLaunchMode.RESUME_ACTIVE && it.sessionKey == sessionKey
    }

    var sessionState by rememberSaveable(sessionKey, uiState.workoutLaunchNonce) {
        mutableStateOf(
            when (uiState.workoutLaunchMode) {
                WorkoutLaunchMode.RESUME_ACTIVE,
                WorkoutLaunchMode.RESTART_FRESH -> ActiveSessionState.ACTIVE_SET
                WorkoutLaunchMode.OVERVIEW -> ActiveSessionState.PRE_WORKOUT_OVERVIEW
                WorkoutLaunchMode.HUB -> if (uiState.openBriefingDirectly || quickStrike != null) {
                    ActiveSessionState.PRE_WORKOUT_OVERVIEW
                } else {
                    ActiveSessionState.WORKOUT_HUB
                }
            }
        )
    }
    var currentPlaylistIndex by rememberSaveable(sessionKey) {
        mutableIntStateOf(
            initialMidwayMatch?.playlistIndex?.coerceIn(0, (playlist.size - 1).coerceAtLeast(0)) ?: 0
        )
    }
    var currentSetNumber by rememberSaveable(sessionKey) {
        mutableIntStateOf(
            initialMidwayMatch?.currentSetNumber?.coerceAtLeast(1) ?: 1
        )
    }
    val audioCoachMode = uiState.profile.audioCoachMode
    val voiceEnabled = audioCoachMode.voiceEnabled
    val musicEnabled = audioCoachMode.musicEnabled

    fun handleToggleQuickMute() {
        val nextMode = if (audioCoachMode == WorkoutAudioCoachMode.MUTED) {
            WorkoutAudioCoachMode.BOTH_ACTIVE
        } else {
            WorkoutAudioCoachMode.MUTED
        }
        onSelectAudioCoachMode(nextMode)
    }
    var restRemainingMillis by rememberSaveable(sessionKey) {
        mutableLongStateOf(60_000L)
    }
    var totalRestMillis by rememberSaveable(sessionKey) {
        mutableLongStateOf(60_000L)
    }
    var totalCompletedSetsCount by rememberSaveable(sessionKey) {
        mutableIntStateOf(initialMidwayMatch?.totalCompletedSetsCount ?: 0)
    }
    var totalAccumulatedVolumeKg by rememberSaveable(sessionKey) {
        mutableFloatStateOf(initialMidwayMatch?.totalAccumulatedVolumeKg ?: 0f)
    }
    var hasRecordedDayAdvance by rememberSaveable(sessionKey) {
        mutableStateOf(false)
    }
    var handledLaunchNonce by rememberSaveable {
        mutableLongStateOf(uiState.workoutLaunchNonce)
    }

    val safePlaylistIndex = currentPlaylistIndex.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))
    val activeItem = playlist[safePlaylistIndex]
    val activeEx = activeItem.spec

    val defaultFallbackIndex = 0
    val fallbackIndexMap = remember(sessionKey, activeEnvironment) {
        mutableStateMapOf<String, Int>().apply {
            initialMidwayMatch?.swappedVariantsMap?.let { putAll(it) }
        }
    }
    var swapSheetTarget by remember { mutableStateOf<Pair<CombatExerciseSpec, Int>?>(null) }

    LaunchedEffect(uiState.workoutLaunchNonce, uiState.workoutLaunchMode, sessionKey) {
        if (handledLaunchNonce != uiState.workoutLaunchNonce) {
            when (uiState.workoutLaunchMode) {
                WorkoutLaunchMode.HUB -> {
                    handledLaunchNonce = uiState.workoutLaunchNonce
                    sessionState = ActiveSessionState.WORKOUT_HUB
                }
                WorkoutLaunchMode.OVERVIEW -> {
                    handledLaunchNonce = uiState.workoutLaunchNonce
                    completedDaySnapshot = null
                    sessionState = ActiveSessionState.PRE_WORKOUT_OVERVIEW
                }
                WorkoutLaunchMode.RESUME_ACTIVE -> {
                    val snap = uiState.midwayWorkout
                    if (snap == null || snap.sessionKey == sessionKey) {
                        handledLaunchNonce = uiState.workoutLaunchNonce
                        completedDaySnapshot = null
                        hasRecordedDayAdvance = false
                        if (snap != null) {
                            fallbackIndexMap.clear()
                            fallbackIndexMap.putAll(snap.swappedVariantsMap)
                            currentPlaylistIndex = snap.playlistIndex.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))
                            currentSetNumber = snap.currentSetNumber.coerceAtLeast(1)
                            totalCompletedSetsCount = snap.totalCompletedSetsCount
                            totalAccumulatedVolumeKg = snap.totalAccumulatedVolumeKg
                        }
                        sessionState = ActiveSessionState.ACTIVE_SET
                    }
                }
                WorkoutLaunchMode.RESTART_FRESH -> {
                    handledLaunchNonce = uiState.workoutLaunchNonce
                    completedDaySnapshot = null
                    hasRecordedDayAdvance = false
                    fallbackIndexMap.clear()
                    currentPlaylistIndex = 0
                    currentSetNumber = 1
                    totalCompletedSetsCount = 0
                    totalAccumulatedVolumeKg = 0f
                    sessionState = ActiveSessionState.ACTIVE_SET
                }
            }
        }
    }

    fun scaleVariantForChallengeDay(v: ExerciseFallbackVariant): ExerciseFallbackVariant {
        if (!isMultiDayChallenge || selectedChallengeDay <= 1) return v
        val weekBonus = ((selectedChallengeDay - 1) / 4).coerceIn(0, 5)
        if (weekBonus <= 0) return v
        return if (v.isTimedSeconds) {
            val nextSec = (v.targetRepsCount + weekBonus * 3).coerceAtMost(90)
            val repStr = String.format("%02d:%02d", nextSec / 60, nextSec % 60)
            v.copy(
                targetRepsCount = nextSec,
                repPrescription = repStr,
                setsAndRepsLabel = "${v.totalSets} Sets × $repStr Hold"
            )
        } else {
            val nextReps = (v.targetRepsCount + weekBonus).coerceAtMost(30)
            v.copy(
                targetRepsCount = nextReps,
                repPrescription = "$nextReps",
                setsAndRepsLabel = "${v.totalSets} Sets × $nextReps Reps"
            )
        }
    }

    fun resolveVariantsForItem(item: SessionPlaylistItem): List<ExerciseFallbackVariant> {
        return when {
            quickStrike != null -> {
                val qsIdx = item.quickStrikeIndex.coerceIn(0, (quickStrike.exercises.size - 1).coerceAtLeast(0))
                val qsSpec = quickStrike.exercises[qsIdx]
                if (item.stageType == SessionStageType.MAIN_WORKOUT) {
                    buildQuickStrikeVariantsList(
                        qsSpec = qsSpec,
                        challengeLevel = challengeLevel
                    ).map { scaleVariantForChallengeDay(it) }
                } else {
                    listOf(
                        buildQuickStrikeVariant(
                            qsSpec = qsSpec,
                            challengeLevel = challengeLevel
                        )
                    )
                }
            }
            item.stageType != SessionStageType.MAIN_WORKOUT -> listOf(buildWarmupOrStretchVariant(item.spec, item.stageType))
            else -> buildFallbackVariantsFor(
                spec = item.spec,
                index = item.stageLocalIndex,
                dayIndex = sessionDay.dayIndex,
                experienceLevel = expLevel,
                bodyweightKg = uiState.profile.bodyweightKg,
                sex = uiState.profile.biologicalSex,
                completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                daysPerWeek = daysPerWeek,
                trackWeekOverride = activeTrackWeek,
                intensityMode = intensityMode,
                environment = activeEnvironment
            )
        }
    }

    val activeVarIdx = fallbackIndexMap[activeEx.id] ?: defaultFallbackIndex
    val activeVariants = remember(
        activeEx.id,
        activeItem.stageType,
        quickStrike,
        challengeLevel,
        selectedChallengeDay,
        expLevel,
        intensityMode,
        activeEnvironment,
        uiState.profile.bodyweightKg,
        uiState.profile.biologicalSex,
        uiState.profile.completedWorkoutsCount,
        daysPerWeek,
        activeTrackWeek
    ) {
        resolveVariantsForItem(activeItem)
    }
    val baseActiveVar = activeVariants[activeVarIdx % activeVariants.size]
    val manualOffsetKg = progressionOffsets[activeEx.id] ?: 0f
    val adjustedBaseLoadKg = if (baseActiveVar.isBodyweight) {
        0f
    } else {
        (activeEx.defaultLoadKg.takeIf { it > 0f } ?: 18f) * baseActiveVar.loadMultiplier
    }
    val progressiveActiveTarget = remember(
        activeEx.id,
        baseActiveVar,
        quickStrike,
        activeItem.stageType,
        expLevel,
        intensityMode,
        uiState.profile.bodyweightKg,
        uiState.profile.biologicalSex,
        uiState.profile.completedWorkoutsCount,
        manualOffsetKg,
        daysPerWeek,
        activeTrackWeek
    ) {
        if (quickStrike == null && activeItem.stageType == SessionStageType.MAIN_WORKOUT) {
            ProgressiveLoadEngine.computeTarget(
                defaultLoadKg = adjustedBaseLoadKg,
                specTotalSets = activeEx.totalSets,
                baseRepsOrSeconds = activeEx.targetRepsCount,
                isTimedSeconds = baseActiveVar.isTimedSeconds,
                isBodyweight = baseActiveVar.isBodyweight,
                bodyweightKg = uiState.profile.bodyweightKg,
                sex = uiState.profile.biologicalSex,
                experience = expLevel,
                completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                manualOffsetKg = manualOffsetKg,
                daysPerWeek = daysPerWeek,
                trackWeekOverride = activeTrackWeek,
                intensityMode = intensityMode
            )
        } else {
            null
        }
    }
    val activeVar = if (progressiveActiveTarget != null) {
        baseActiveVar.copy(
            totalSets = progressiveActiveTarget.sets,
            targetRepsCount = progressiveActiveTarget.repsOrSeconds,
            repPrescription = if (progressiveActiveTarget.isTimedSeconds) {
                progressiveActiveTarget.repPrescription
            } else {
                "${progressiveActiveTarget.repsOrSeconds}"
            },
            setsAndRepsLabel = progressiveActiveTarget.setsAndRepsLabel
        )
    } else {
        baseActiveVar
    }

    // Rest duration matches the stage: quick 15s between warm-up/stretch items, 25s for Quick Home, user-configured seconds for heavy Main sets
    val stageRestMillis = when {
        quickStrike != null -> 25_000L
        activeItem.stageType == SessionStageType.PRE_WARMUP -> 15_000L
        activeItem.stageType == SessionStageType.POST_STRETCH -> 12_000L
        else -> uiState.profile.restDurationSeconds * 1000L
    }

    fun buildCurrentMidwaySnapshot(): MidwayWorkoutSnapshot {
        val targetPlaylistIdx: Int
        val targetSetNum: Int
        if (sessionState == ActiveSessionState.RESTING) {
            if (currentSetNumber < activeVar.totalSets) {
                targetPlaylistIdx = safePlaylistIndex
                targetSetNum = currentSetNumber + 1
            } else if (safePlaylistIndex < playlist.size - 1) {
                targetPlaylistIdx = safePlaylistIndex + 1
                targetSetNum = 1
            } else {
                targetPlaylistIdx = safePlaylistIndex
                targetSetNum = currentSetNumber
            }
        } else {
            targetPlaylistIdx = safePlaylistIndex
            targetSetNum = currentSetNumber
        }

        val targetItem = playlist[targetPlaylistIdx.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))]
        val targetVariants = resolveVariantsForItem(targetItem)
        val targetVarIdx = fallbackIndexMap[targetItem.spec.id] ?: defaultFallbackIndex
        val baseTargetVar = targetVariants[targetVarIdx % targetVariants.size]
        val resolvedTotalSets = if (targetPlaylistIdx == safePlaylistIndex) {
            activeVar.totalSets
        } else if (quickStrike == null && targetItem.stageType == SessionStageType.MAIN_WORKOUT) {
            ProgressiveLoadEngine.computeTarget(
                defaultLoadKg = if (baseTargetVar.isBodyweight) 0f else (targetItem.spec.defaultLoadKg.takeIf { it > 0f } ?: 18f) * baseTargetVar.loadMultiplier,
                specTotalSets = targetItem.spec.totalSets,
                baseRepsOrSeconds = targetItem.spec.targetRepsCount,
                isTimedSeconds = baseTargetVar.isTimedSeconds,
                isBodyweight = baseTargetVar.isBodyweight,
                bodyweightKg = uiState.profile.bodyweightKg,
                sex = uiState.profile.biologicalSex,
                experience = expLevel,
                completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                manualOffsetKg = progressionOffsets[targetItem.spec.id] ?: 0f,
                daysPerWeek = daysPerWeek,
                trackWeekOverride = activeTrackWeek,
                intensityMode = intensityMode
            ).sets
        } else {
            baseTargetVar.totalSets
        }

        val lastMainIdx = playlist.indexOfLast { it.stageType == SessionStageType.MAIN_WORKOUT }
        val isMainFinished = targetItem.stageType == SessionStageType.POST_STRETCH ||
            (lastMainIdx >= 0 && targetPlaylistIdx > lastMainIdx)

        val resolvedWorkoutTitle = quickStrike?.title
            ?: "Day ${sessionDay.dayIndex + 1} • ${WeeklySchedulePlanner.benefitHeadlineForDay(sessionDay)}"

        return MidwayWorkoutSnapshot(
            sessionKey = sessionKey,
            isQuickStrike = quickStrike != null,
            quickStrike = quickStrike,
            scheduledDayIndex = sessionDay.dayIndex,
            scheduledDayId = sessionDay.id,
            workoutTitle = resolvedWorkoutTitle,
            currentExerciseName = baseTargetVar.name,
            currentExerciseAnim = baseTargetVar.animationType,
            stageType = targetItem.stageType,
            playlistIndex = targetPlaylistIdx,
            totalPlaylistCount = playlist.size,
            stageLocalIndex = targetItem.stageLocalIndex,
            stageTotalCount = targetItem.stageTotalCount,
            currentSetNumber = targetSetNum.coerceIn(1, resolvedTotalSets.coerceAtLeast(1)),
            totalSetsForCurrent = resolvedTotalSets.coerceAtLeast(1),
            mainWorkoutFinished = isMainFinished,
            totalCompletedSetsCount = totalCompletedSetsCount,
            totalAccumulatedVolumeKg = totalAccumulatedVolumeKg,
            swappedVariantsMap = fallbackIndexMap.toMap(),
            originPage = uiState.workoutOriginPage
        )
    }

    val activeSwappedSignature = fallbackIndexMap.entries
        .sortedBy { it.key }
        .joinToString(",") { "${it.key}=${it.value}" }

    LaunchedEffect(
        sessionState,
        safePlaylistIndex,
        currentSetNumber,
        activeVar.name,
        totalCompletedSetsCount,
        activeSwappedSignature,
        handledLaunchNonce
    ) {
        if (handledLaunchNonce == uiState.workoutLaunchNonce &&
            (sessionState == ActiveSessionState.ACTIVE_SET || sessionState == ActiveSessionState.RESTING)
        ) {
            onSaveMidwayWorkout(buildCurrentMidwaySnapshot())
        } else if (sessionState == ActiveSessionState.SESSION_COMPLETED) {
            onClearMidwayWorkout()
        }
    }

    fun handleExitToOriginPage() {
        WorkoutMusicEngine.stop()
        if (sessionState == ActiveSessionState.ACTIVE_SET || sessionState == ActiveSessionState.RESTING) {
            onSaveMidwayWorkout(buildCurrentMidwaySnapshot())
        } else if (sessionState == ActiveSessionState.SESSION_COMPLETED) {
            onClearMidwayWorkout()
        }
        sessionState = ActiveSessionState.WORKOUT_HUB
        if (uiState.workoutOriginPage == WorkoutOriginPage.WORKOUT_HUB) {
            onReturnToWorkoutHub()
        } else {
            onNavigateBackHome()
        }
    }

    // Pressing Back exits directly to the page where the user launched the workout (Dashboard or Workout Hub) — never to the previous exercise.
    BackHandler {
        when (sessionState) {
            ActiveSessionState.ACTIVE_SET,
            ActiveSessionState.RESTING,
            ActiveSessionState.PRE_WORKOUT_OVERVIEW,
            ActiveSessionState.SESSION_COMPLETED -> {
                handleExitToOriginPage()
            }
            ActiveSessionState.WORKOUT_HUB -> {
                onNavigateBackHome()
            }
        }
    }

    // Auto-advance the user's scheduled day once when they finish a main split workout, or credit 5-Min Habit Rescue!
    // Auto-start motivational music on Active Set when Music is enabled; stop cleanly on completion
    LaunchedEffect(sessionState, musicEnabled) {
        if ((sessionState == ActiveSessionState.ACTIVE_SET || sessionState == ActiveSessionState.RESTING) &&
            musicEnabled &&
            !WorkoutMusicEngine.state.value.isPlaying
        ) {
            WorkoutMusicEngine.start()
        } else if (!musicEnabled && WorkoutMusicEngine.state.value.isPlaying) {
            WorkoutMusicEngine.stop()
        }
    }

    LaunchedEffect(sessionState) {
        if (sessionState == ActiveSessionState.SESSION_COMPLETED) {
            if (WorkoutMusicEngine.state.value.isPlaying) {
                WorkoutMusicEngine.stop()
            }
            onClearMidwayWorkout()
            if (!hasRecordedDayAdvance) {
                if (quickStrike == null) {
                    completedDaySnapshot = uiState.activeCombatDay
                    hasRecordedDayAdvance = true
                    onScheduledWorkoutCompleted()
                } else if (quickStrike.id == HomeWorkoutCatalog.LOW_ENERGY_RESCUE_WORKOUT.id) {
                    hasRecordedDayAdvance = true
                    onRescueWorkoutCompleted()
                } else {
                    hasRecordedDayAdvance = true
                    onQuickPackOrChallengeCompleted(
                        quickStrike,
                        if (isMultiDayChallenge) selectedChallengeDay else null,
                        totalCompletedSetsCount.coerceAtLeast(3),
                        quickStrike.caloriesKcal
                    )
                }
            }
        } else if (sessionState == ActiveSessionState.PRE_WORKOUT_OVERVIEW) {
            completedDaySnapshot = null
        }
    }

    val prescribedWeightKg = when {
        progressiveActiveTarget != null -> progressiveActiveTarget.loadKg
        activeVar.isBodyweight || activeEx.defaultLoadKg <= 0f -> 0f
        quickStrike != null -> {
            val mult = when (challengeLevel) {
                ExperienceLevel.NEW_BEGINNER -> 0.40f
                ExperienceLevel.BEGINNER_JOINT_SAFE -> 0.72f
                ExperienceLevel.INTERMEDIATE -> 1.00f
            }
            (((activeEx.defaultLoadKg * mult) + manualOffsetKg).coerceAtLeast(2f) * 2f).roundToInt() / 2f
        }
        else -> ((activeEx.defaultLoadKg + manualOffsetKg).coerceAtLeast(2f) * 2f).roundToInt() / 2f
    }

    var lastAlertSecond by remember { mutableIntStateOf(-1) }
    var announcedRest30 by remember { mutableStateOf(false) }
    var announcedRest15 by remember { mutableStateOf(false) }

    val upcomingExerciseTitleForRest = remember(
        currentSetNumber,
        activeVar.totalSets,
        activeVar.name,
        safePlaylistIndex,
        playlist,
        activeEnvironment,
        expLevel,
        challengeLevel
    ) {
        if (currentSetNumber < activeVar.totalSets) {
            "Set ${currentSetNumber + 1} of ${activeVar.totalSets} • ${activeVar.name}"
        } else {
            val nextIdx = (safePlaylistIndex + 1).coerceAtMost((playlist.size - 1).coerceAtLeast(0))
            val nextItem = playlist.getOrNull(nextIdx)
            if (nextItem != null) {
                val nextVariants = resolveVariantsForItem(nextItem)
                val nVarIdx = fallbackIndexMap[nextItem.spec.id] ?: 0
                nextVariants[nVarIdx % nextVariants.size].name
            } else {
                activeVar.name
            }
        }
    }

    LaunchedEffect(sessionState, restRemainingMillis) {
        if (sessionState == ActiveSessionState.RESTING) {
            if (restRemainingMillis <= 0L) {
                TacticalAudioHaptics.triggerRestCompleteAlarm(context)
                if (voiceEnabled) {
                    TacticalAudioHaptics.triggerRestCompleteVoice(context, upcomingExerciseTitleForRest)
                }
                if (currentSetNumber < activeVar.totalSets) {
                    currentSetNumber += 1
                    sessionState = ActiveSessionState.ACTIVE_SET
                } else if (safePlaylistIndex < playlist.size - 1) {
                    currentPlaylistIndex = safePlaylistIndex + 1
                    currentSetNumber = 1
                    sessionState = ActiveSessionState.ACTIVE_SET
                } else {
                    sessionState = ActiveSessionState.SESSION_COMPLETED
                }
            } else {
                val currentSecond = ((restRemainingMillis + 999L) / 1000L).toInt()
                val totalSecConfigured = (totalRestMillis / 1000L).toInt()
                if (currentSecond == 30 && totalSecConfigured >= 40 && !announcedRest30) {
                    announcedRest30 = true
                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerRestNextExerciseReminderVoice(
                            context = context,
                            remainingSec = 30,
                            nextExerciseName = upcomingExerciseTitleForRest
                        )
                    }
                }
                if (currentSecond == 15 && totalSecConfigured >= 24 && !announcedRest15) {
                    announcedRest15 = true
                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerRestNextExerciseReminderVoice(
                            context = context,
                            remainingSec = 15,
                            nextExerciseName = upcomingExerciseTitleForRest
                        )
                    }
                }
                if (currentSecond in 1..3 && currentSecond != lastAlertSecond) {
                    lastAlertSecond = currentSecond
                    TacticalAudioHaptics.triggerCountdownTick(context)
                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerCountdownVoice(context, currentSecond)
                    }
                }
                delay(100L)
                restRemainingMillis = (restRemainingMillis - 100L).coerceAtLeast(0L)
            }
        }
    }

    swapSheetTarget?.let { (targetSpec, targetIdx) ->
        val sheetVariants = if (quickStrike != null) {
            val qsSpec = quickStrike.exercises.firstOrNull { it.id == targetSpec.id }
                ?: quickStrike.exercises[targetIdx.coerceIn(0, (quickStrike.exercises.size - 1).coerceAtLeast(0))]
            buildQuickStrikeVariantsList(
                qsSpec = qsSpec,
                challengeLevel = challengeLevel
            )
        } else {
            buildFallbackVariantsFor(
                spec = targetSpec,
                index = targetIdx,
                dayIndex = sessionDay.dayIndex,
                experienceLevel = expLevel,
                bodyweightKg = uiState.profile.bodyweightKg,
                sex = uiState.profile.biologicalSex,
                completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                daysPerWeek = daysPerWeek,
                trackWeekOverride = activeTrackWeek,
                intensityMode = intensityMode,
                environment = activeEnvironment
            )
        }
        val selectedOptIdx = (fallbackIndexMap[targetSpec.id] ?: 0) % sheetVariants.size
        SwapExerciseModalSheet(
            targetMuscleLabel = targetSpec.primaryMuscle.ifBlank { targetSpec.targetMuscle },
            environment = activeEnvironment,
            variants = sheetVariants,
            selectedIndex = selectedOptIdx,
            onSelectIndex = { chosenIdx ->
                TacticalAudioHaptics.triggerClickPulse(context)
                fallbackIndexMap[targetSpec.id] = chosenIdx
                swapSheetTarget = null
            },
            onDismiss = { swapSheetTarget = null }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeBackground)
            .testTag("active_session_root")
    ) {
        AnimatedContent(
            targetState = sessionState,
            transitionSpec = { fadeIn(tween(140)) togetherWith fadeOut(tween(140)) },
            label = "session_state_anim"
        ) { state ->
            when (state) {
                ActiveSessionState.WORKOUT_HUB -> {
                    WorkoutJourneyHubView(
                        weekSchedule = uiState.weekSchedule,
                        restDaysIso = uiState.profile.restDaysIso,
                        environment = uiState.profile.trainingEnvironment,
                        profile = uiState.profile,
                        activeRotationWeek = uiState.activeRotationWeek,
                        fourWeekTrack = uiState.fourWeekTrack,
                        progressionOffsets = progressionOffsets,
                        onSelectEnvironment = onSelectTrainingEnvironment,
                        onSelectIntensity = onSelectWorkoutIntensity,
                        onSelectAudioCoachMode = onSelectAudioCoachMode,
                        onToggleRestDay = onToggleRestDay,
                        onSelectRotationWeek = onSelectRotationWeek,
                        onSelectExperienceLevel = onSelectExperienceLevel,
                        onOpenScheduledDayBriefing = { dayIdx ->
                            onOpenScheduledBriefingFromHub(dayIdx)
                        },
                        onOpenSpecialtyWorkout = { track ->
                            onStartQuickStrikeFromHub(track)
                        },
                        midwayWorkout = uiState.midwayWorkout,
                        onResumeMidwayWorkout = onResumeMidwayFromHub,
                        onRestartMidwayWorkout = onRestartMidwayFromHub,
                        onCompleteMidwayWorkout = onCompleteMidwayFromHub
                    )
                }

                ActiveSessionState.PRE_WORKOUT_OVERVIEW -> {
                    val isHomeMode = uiState.profile.trainingEnvironment == TrainingEnvironment.HOME
                    val coverResId = if (quickStrike != null) {
                        WorkoutCoverResolver.coverForQuickWorkout(quickStrike, uiState.profile.biologicalSex)
                    } else {
                        WorkoutCoverResolver.coverForScheduledDay(
                            sessionDay,
                            uiState.profile.trainingEnvironment,
                            uiState.profile.biologicalSex
                        )
                    }
                    val eyebrowText = when {
                        isMultiDayChallenge && quickStrike != null ->
                            "DAY $selectedChallengeDay OF $challengeTotalDays  •  ${completedChallengeDays.size}/$challengeTotalDays DONE"
                        quickStrike != null -> "${quickStrike.iconTag}  •  STANDALONE SESSION"
                        isHomeMode ->
                            "WEEK $activeTrackWeek OF 4  •  DAY ${sessionDay.dayIndex + 1}  •  AT HOME"
                        else ->
                            "WEEK $activeTrackWeek OF 4  •  DAY ${sessionDay.dayIndex + 1}  •  GYM"
                    }
                    val activeDayTitle = quickStrike?.title ?: WeeklySchedulePlanner.benefitHeadlineForDay(sessionDay)
                    val durationBadgeText = when {
                        quickStrike != null -> "${quickStrike.durationMinutes} min"
                        else -> "~${intensityMode.estMinutes} min"
                    }
                    val estimatedKcalBurn = if (quickStrike != null) {
                        quickStrike.caloriesKcal.coerceAtLeast(35)
                    } else {
                        ProgressiveLoadEngine.estimateSessionCalories(
                            intensityMode = intensityMode,
                            isHomeMode = isHomeMode,
                            bodyweightKg = uiState.profile.bodyweightKg
                        )
                    }

                    val activeBlueprint = if (quickStrike == null) {
                        CombatSplitCatalog.describeActiveBlueprint(
                            sex = uiState.profile.biologicalSex,
                            startingShapeId = uiState.profile.selectedStartingShapeCardId,
                            goalCardId = uiState.profile.selectedGoalCardId,
                            currentBracket = uiState.profile.currentBfBracket,
                            goalBracket = uiState.profile.goalBfBracket,
                            environment = activeEnvironment
                        )
                    } else {
                        null
                    }

                    PreWorkoutOverviewView(
                        coverResId = coverResId,
                        eyebrowText = eyebrowText,
                        activeDayTitle = activeDayTitle,
                        activeDaySubtitle = quickStrike?.subtitle ?: "${sessionDay.title}  •  ${sessionDay.subtitle}",
                        expectedResultsTimeline = quickStrike?.resolvedResultsTimeline.orEmpty(),
                        sex = uiState.profile.biologicalSex,
                        trackSummaryLine = "${uiState.fourWeekTrack.weekHeadline}  •  ${uiState.fourWeekTrack.weekRuleSubtitle}",
                        blueprint = activeBlueprint,
                        durationBadgeText = durationBadgeText,
                        estimatedKcalBurn = estimatedKcalBurn,
                        intensityMode = intensityMode,
                        onSelectIntensity = onSelectWorkoutIntensity,
                        isQuickHomeSession = quickStrike != null,
                        isMultiDayChallenge = isMultiDayChallenge,
                        challengeTotalDays = challengeTotalDays,
                        completedChallengeDays = completedChallengeDays,
                        selectedChallengeDay = selectedChallengeDay,
                        onSelectChallengeDay = { dayNum -> selectedChallengeDay = dayNum },
                        availableDays = uiState.availableCombatDays,
                        activeDayIndex = sessionDay.dayIndex,
                        onSelectDay = onSelectDay,
                        experienceLevel = if (quickStrike != null) challengeLevel else expLevel,
                        onSelectExperienceLevel = { lvl ->
                            if (quickStrike != null) {
                                challengeLevel = lvl
                            } else {
                                onSelectExperienceLevel(lvl)
                            }
                        },
                        warmupSpecs = warmupSpecs,
                        mainExercises = mainSpecs,
                        stretchSpecs = stretchSpecs,
                        getWarmupOrStretchVariant = { spec, stage ->
                            if (quickStrike != null) {
                                val qsSpec = quickStrike.exercises.firstOrNull { it.id == spec.id }
                                if (qsSpec != null) {
                                    buildQuickStrikeVariant(qsSpec, challengeLevel)
                                } else {
                                    buildWarmupOrStretchVariant(spec, stage)
                                }
                            } else {
                                buildWarmupOrStretchVariant(spec, stage)
                            }
                        },
                        getMainVariant = { spec, idx ->
                            val vIdx = fallbackIndexMap[spec.id] ?: defaultFallbackIndex
                            if (quickStrike != null) {
                                val qsSpec = quickStrike.exercises.firstOrNull { it.id == spec.id }
                                    ?: quickStrike.exercises[idx.coerceIn(0, (quickStrike.exercises.size - 1).coerceAtLeast(0))]
                                val vList = buildQuickStrikeVariantsList(
                                    qsSpec = qsSpec,
                                    challengeLevel = challengeLevel
                                ).map { scaleVariantForChallengeDay(it) }
                                vList[vIdx % vList.size]
                            } else {
                                val vList = buildFallbackVariantsFor(
                                    spec = spec,
                                    index = idx,
                                    dayIndex = sessionDay.dayIndex,
                                    experienceLevel = expLevel,
                                    bodyweightKg = uiState.profile.bodyweightKg,
                                    sex = uiState.profile.biologicalSex,
                                    completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                                    daysPerWeek = daysPerWeek,
                                    trackWeekOverride = activeTrackWeek,
                                    intensityMode = intensityMode,
                                    environment = activeEnvironment
                                )
                                vList[vIdx % vList.size]
                            }
                        },
                        getMainPrescribedLoadText = { spec, variant ->
                            if (variant.isBodyweight) {
                                "Bodyweight"
                            } else if (quickStrike != null) {
                                val baseKg = (spec.defaultLoadKg.takeIf { it > 0f } ?: 12f) * variant.loadMultiplier
                                val mult = when (challengeLevel) {
                                    ExperienceLevel.NEW_BEGINNER -> 0.40f
                                    ExperienceLevel.BEGINNER_JOINT_SAFE -> 0.72f
                                    ExperienceLevel.INTERMEDIATE -> 1.00f
                                }
                                val scaledKg = ((baseKg * mult).coerceAtLeast(2f) * 2f).roundToInt() / 2f
                                ProgressiveLoadEngine.formatWeightShort(scaledKg, false)
                            } else {
                                val target = ProgressiveLoadEngine.computeTarget(
                                    defaultLoadKg = if (variant.isBodyweight) 0f else (spec.defaultLoadKg.takeIf { it > 0f } ?: 18f) * variant.loadMultiplier,
                                    specTotalSets = spec.totalSets,
                                    baseRepsOrSeconds = spec.targetRepsCount,
                                    isTimedSeconds = variant.isTimedSeconds,
                                    isBodyweight = variant.isBodyweight,
                                    bodyweightKg = uiState.profile.bodyweightKg,
                                    sex = uiState.profile.biologicalSex,
                                    experience = expLevel,
                                    completedWorkoutsCount = uiState.profile.completedWorkoutsCount,
                                    manualOffsetKg = progressionOffsets[spec.id] ?: 0f,
                                    daysPerWeek = daysPerWeek,
                                    trackWeekOverride = activeTrackWeek,
                                    intensityMode = intensityMode
                                )
                                ProgressiveLoadEngine.formatWeightShort(target.loadKg, variant.isBodyweight)
                            }
                        },
                        onOpenSwapSheet = { spec, idx ->
                            swapSheetTarget = spec to idx
                        },
                        onStartFullWorkout = {
                            hasRecordedDayAdvance = false
                            totalCompletedSetsCount = 0
                            totalAccumulatedVolumeKg = 0f
                            currentPlaylistIndex = 0
                            currentSetNumber = 1
                            sessionState = ActiveSessionState.ACTIVE_SET
                        },
                        onStartSkipWarmup = {
                            hasRecordedDayAdvance = false
                            totalCompletedSetsCount = 0
                            totalAccumulatedVolumeKg = 0f
                            currentPlaylistIndex = firstMainPlaylistIndex.coerceAtMost(playlist.size - 1)
                            currentSetNumber = 1
                            sessionState = ActiveSessionState.ACTIVE_SET
                        },
                        onNavigateBack = {
                            handleExitToOriginPage()
                        },
                        onResetWorkout = onResetDay
                    )
                }

                ActiveSessionState.ACTIVE_SET -> {
                    val stageBadgeLabel = "${activeItem.stageType.shortBadge} • ${activeItem.stageLocalIndex + 1}/${activeItem.stageTotalCount}"
                    val phaseStartPlaylistIndex = playlist.indexOfFirst { it.stageType == activeItem.stageType }
                        .let { if (it >= 0) it else 0 }

                    ActiveSetScreenView(
                        playlistIndex = safePlaylistIndex,
                        totalPlaylistItems = playlist.size,
                        phaseStartPlaylistIndex = phaseStartPlaylistIndex,
                        stageType = activeItem.stageType,
                        stageLocalIndex = activeItem.stageLocalIndex,
                        stageTotalCount = activeItem.stageTotalCount,
                        stageBadgeLabel = stageBadgeLabel,
                        showSkipWarmupButton = activeItem.stageType == SessionStageType.PRE_WARMUP && firstMainPlaylistIndex > safePlaylistIndex,
                        activeVar = activeVar,
                        sex = uiState.profile.biologicalSex,
                        experienceLevel = if (quickStrike != null) challengeLevel else expLevel,
                        currentSetNumber = currentSetNumber,
                        prescribedWeightKg = prescribedWeightKg,
                        canAdjustWeight = quickStrike == null && activeItem.stageType == SessionStageType.MAIN_WORKOUT && !activeVar.isBodyweight && adjustedBaseLoadKg > 0f,
                        onAdjustWeightKg = { deltaKg ->
                            onRecordSetEffortAndProgression(activeEx.id, deltaKg)
                        },
                        audioCoachMode = audioCoachMode,
                        onSelectAudioCoachMode = onSelectAudioCoachMode,
                        soundEnabled = audioCoachMode != WorkoutAudioCoachMode.MUTED,
                        canSwapVariant = activeVariants.size > 1 && activeItem.stageType == SessionStageType.MAIN_WORKOUT,
                        onToggleSound = { handleToggleQuickMute() },
                        onNavigateBack = {
                            handleExitToOriginPage()
                        },
                        onSkipWarmupToMain = {
                            currentPlaylistIndex = firstMainPlaylistIndex.coerceAtMost(playlist.size - 1)
                            currentSetNumber = 1
                        },
                        onSelectPlaylistIndex = { targetIdx ->
                            currentPlaylistIndex = targetIdx.coerceIn(0, (playlist.size - 1).coerceAtLeast(0))
                            currentSetNumber = 1
                        },
                        onSwapVariant = {
                            if (activeVariants.size > 1) {
                                swapSheetTarget = activeEx to activeItem.stageLocalIndex
                            }
                        },
                        onCompleteSet = {
                            TacticalAudioHaptics.triggerSetLoggedPulse(context)
                            if (quickStrike == null && activeItem.stageType == SessionStageType.MAIN_WORKOUT) {
                                onToggleSet(
                                    sessionDay.id,
                                    activeEx.id,
                                    activeVar.name,
                                    currentSetNumber,
                                    prescribedWeightKg
                                )
                                totalCompletedSetsCount += 1
                                totalAccumulatedVolumeKg += (prescribedWeightKg * activeVar.targetRepsCount)
                            } else if (quickStrike != null) {
                                totalCompletedSetsCount += 1
                            }

                            if (safePlaylistIndex >= playlist.size - 1 && currentSetNumber >= activeVar.totalSets) {
                                sessionState = ActiveSessionState.SESSION_COMPLETED
                            } else {
                                val restSecs = (stageRestMillis / 1000L).toInt()
                                if (voiceEnabled) {
                                    val spokenUpNext = if (currentSetNumber < activeVar.totalSets) {
                                        "Next up: Set ${currentSetNumber + 1} of ${activeVar.totalSets}, ${activeVar.name}"
                                    } else {
                                        val nextItem = playlist[(safePlaylistIndex + 1).coerceAtMost(playlist.size - 1)]
                                        val nextVariants = resolveVariantsForItem(nextItem)
                                        val nVarIdx = fallbackIndexMap[nextItem.spec.id] ?: 0
                                        val nextName = nextVariants[nVarIdx % nextVariants.size].name
                                        "Next exercise: $nextName"
                                    }
                                    TacticalAudioHaptics.triggerSetCompletedVoice(
                                        context = context,
                                        setNumber = currentSetNumber,
                                        nextLabel = spokenUpNext,
                                        restSeconds = restSecs
                                    )
                                }
                                restRemainingMillis = stageRestMillis
                                totalRestMillis = stageRestMillis
                                lastAlertSecond = -1
                                announcedRest30 = false
                                announcedRest15 = false
                                sessionState = ActiveSessionState.RESTING
                            }
                        }
                    )
                }

                ActiveSessionState.RESTING -> {
                    val isNextSameEx = currentSetNumber < activeVar.totalSets
                    val upNextAnimType: ExerciseAnimationType
                    val upNextHeading: String
                    val upNextTitle: String
                    val upNextSubtitle: String

                    val completedStageType = activeItem.stageType
                    val upcomingStageType = if (isNextSameEx) {
                        activeItem.stageType
                    } else {
                        val nextIdx = (safePlaylistIndex + 1).coerceAtMost(playlist.size - 1)
                        playlist[nextIdx].stageType
                    }

                    if (isNextSameEx) {
                        upNextHeading = "UP NEXT  •  SET ${currentSetNumber + 1} OF ${activeVar.totalSets}"
                        upNextAnimType = activeVar.animationType
                        upNextTitle = activeVar.name
                        val unitLabel = if (activeVar.isTimedSeconds) "Hold" else "Reps"
                        upNextSubtitle = "Set ${currentSetNumber + 1} of ${activeVar.totalSets} • ${activeVar.repPrescription} $unitLabel"
                    } else {
                        val nextIdx = (safePlaylistIndex + 1).coerceAtMost(playlist.size - 1)
                        val nextItem = playlist[nextIdx]
                        val nextVariants = resolveVariantsForItem(nextItem)
                        val nVarIdx = fallbackIndexMap[nextItem.spec.id] ?: 0
                        val nextVar = nextVariants[nVarIdx % nextVariants.size]
                        upNextHeading = when (nextItem.stageType) {
                            SessionStageType.PRE_WARMUP -> "WARM-UP PREP  •  ${nextItem.stageLocalIndex + 1} OF ${nextItem.stageTotalCount}"
                            SessionStageType.POST_STRETCH -> "COOL-DOWN STRETCH  •  ${nextItem.stageLocalIndex + 1} OF ${nextItem.stageTotalCount}"
                            SessionStageType.MAIN_WORKOUT -> "MAIN WORKOUT  •  ${nextItem.stageLocalIndex + 1} OF ${nextItem.stageTotalCount}"
                        }
                        upNextAnimType = nextVar.animationType
                        upNextTitle = nextVar.name
                        upNextSubtitle = nextVar.setsAndRepsLabel
                    }

                    RestingScreenView(
                        restRemainingMillis = restRemainingMillis,
                        totalRestMillis = totalRestMillis,
                        completedStageType = completedStageType,
                        upcomingStageType = upcomingStageType,
                        audioCoachMode = audioCoachMode,
                        onSelectAudioCoachMode = onSelectAudioCoachMode,
                        soundEnabled = audioCoachMode != WorkoutAudioCoachMode.MUTED,
                        upNextHeading = upNextHeading,
                        upNextTitle = upNextTitle,
                        upNextSubtitle = upNextSubtitle,
                        upNextAnimType = upNextAnimType,
                        onNavigateBack = {
                            handleExitToOriginPage()
                        },
                        onToggleSound = { handleToggleQuickMute() },
                        onExtendRest = {
                            restRemainingMillis += 20_000L
                            totalRestMillis += 20_000L
                            lastAlertSecond = -1
                            TacticalAudioHaptics.triggerClickPulse(context)
                        },
                        onSkipRest = {
                            TacticalAudioHaptics.triggerClickPulse(context)
                            if (currentSetNumber < activeVar.totalSets) {
                                currentSetNumber += 1
                                sessionState = ActiveSessionState.ACTIVE_SET
                            } else if (safePlaylistIndex < playlist.size - 1) {
                                currentPlaylistIndex = safePlaylistIndex + 1
                                currentSetNumber = 1
                                sessionState = ActiveSessionState.ACTIVE_SET
                            } else {
                                sessionState = ActiveSessionState.SESSION_COMPLETED
                            }
                        }
                    )
                }

                ActiveSessionState.SESSION_COMPLETED -> {
                    val isHomeDone = uiState.profile.trainingEnvironment == TrainingEnvironment.HOME
                    val completedKcal = if (quickStrike != null) {
                        quickStrike.caloriesKcal.coerceAtLeast(35)
                    } else {
                        ProgressiveLoadEngine.estimateSessionCalories(
                            intensityMode = intensityMode,
                            isHomeMode = isHomeDone,
                            bodyweightKg = uiState.profile.bodyweightKg
                        )
                    }
                    val challengeDoneLabel = if (isMultiDayChallenge) {
                        "Day $selectedChallengeDay/$challengeTotalDays ✓"
                    } else {
                        null
                    }
                    SessionCompletedView(
                        dayTitle = if (quickStrike != null) quickStrike.title else sessionDay.heroSessionTitle,
                        isQuickHomeSession = quickStrike != null,
                        challengeDayBadge = challengeDoneLabel,
                        streakDays = maxOf(1, uiState.currentStreakDays),
                        workoutNumber = maxOf(1, uiState.profile.completedWorkoutsCount),
                        nextDayLabel = if (quickStrike == null) nextScheduledDayTitle else null,
                        totalVolumeKg = totalAccumulatedVolumeKg,
                        totalSetsCompleted = totalCompletedSetsCount.coerceAtLeast(3),
                        estimatedKcalBurned = completedKcal,
                        returnButtonLabel = if (uiState.workoutOriginPage == WorkoutOriginPage.WORKOUT_HUB) {
                            "BACK TO WORKOUT ->"
                        } else {
                            "BACK TO DASHBOARD ->"
                        },
                        onReturnHome = {
                            handleExitToOriginPage()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PreWorkoutOverviewView(
    coverResId: Int,
    eyebrowText: String,
    activeDayTitle: String,
    activeDaySubtitle: String,
    expectedResultsTimeline: String = "",
    sex: BiologicalSex = BiologicalSex.MALE,
    trackSummaryLine: String = "",
    blueprint: PersonalTrainingBlueprint? = null,
    durationBadgeText: String = "38 min",
    estimatedKcalBurn: Int = 285,
    intensityMode: WorkoutIntensityMode = WorkoutIntensityMode.MODERATE,
    onSelectIntensity: (WorkoutIntensityMode) -> Unit = {},
    isQuickHomeSession: Boolean = false,
    isMultiDayChallenge: Boolean = false,
    challengeTotalDays: Int = 0,
    completedChallengeDays: Set<Int> = emptySet(),
    selectedChallengeDay: Int = 1,
    onSelectChallengeDay: (Int) -> Unit = {},
    availableDays: List<CombatDaySpec> = emptyList(),
    activeDayIndex: Int = 0,
    onSelectDay: (Int) -> Unit = {},
    experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE,
    onSelectExperienceLevel: (ExperienceLevel) -> Unit = {},
    warmupSpecs: List<CombatExerciseSpec> = emptyList(),
    mainExercises: List<CombatExerciseSpec>,
    stretchSpecs: List<CombatExerciseSpec> = emptyList(),
    getWarmupOrStretchVariant: (CombatExerciseSpec, SessionStageType) -> ExerciseFallbackVariant,
    getMainVariant: (CombatExerciseSpec, Int) -> ExerciseFallbackVariant,
    getMainPrescribedLoadText: (CombatExerciseSpec, ExerciseFallbackVariant) -> String = { _, _ -> "Bodyweight" },
    onOpenSwapSheet: (CombatExerciseSpec, Int) -> Unit = { _, _ -> },
    onStartFullWorkout: () -> Unit,
    onStartSkipWarmup: () -> Unit = {},
    onNavigateBack: () -> Unit,
    onResetWorkout: () -> Unit
) {
    val allMainVariants = mainExercises.mapIndexed { idx, spec -> getMainVariant(spec, idx) }
    val totalExercisesCount = warmupSpecs.size + mainExercises.size + stretchSpecs.size
    val totalSets = allMainVariants.sumOf { it.totalSets }
    var expandedIndex by rememberSaveable { mutableIntStateOf(-1) }
    var overviewCoachTarget by remember {
        mutableStateOf<Triple<ExerciseFallbackVariant, Pair<CombatExerciseSpec, Int>?, Boolean>?>(null)
    }
    val scrollState = rememberScrollState()
    val hairlineSubtle = CardBorderLight

    overviewCoachTarget?.let { (coachVar, swapPair, startInZeroToHero) ->
        ExerciseMasteryCoachDialog(
            exerciseName = coachVar.name,
            primaryMuscle = coachVar.primaryMuscle,
            animationType = coachVar.animationType,
            sex = sex,
            existingCues = coachVar.cues,
            existingBreathing = coachVar.breathingPattern,
            existingMistake = coachVar.commonMistake,
            initialTabZeroToHero = startInZeroToHero,
            onOpenSwapExercise = swapPair?.let { (s, idx) -> { onOpenSwapSheet(s, idx) } },
            onDismiss = { overviewCoachTarget = null }
        )
    }

    FitnessAtmosphericContainer(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_pre_workout_overview")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(bottom = 20.dp)
            ) {
            // 1. Studio Cover Photo with Vignette Scrim + High-Contrast Pills
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(186.dp)
            ) {
                Image(
                    painter = painterResource(id = coverResId),
                    contentDescription = activeDayTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x55090D16),
                                    Color.Transparent,
                                    Color(0xCC090D16)
                                )
                            )
                        )
                )

                // Top Controls Row with Solid High-Contrast Pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.94f), CircleShape)
                            .shadow(2.dp, CircleShape)
                            .testTag("btn_overview_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ExerciseTitleDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (!isQuickHomeSession && availableDays.size > 1) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.94f),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                availableDays.forEachIndexed { idx, _ ->
                                    val selected = idx == activeDayIndex
                                    Surface(
                                        onClick = { onSelectDay(idx) },
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (selected) ExerciseTitleDark else Color.Transparent
                                    ) {
                                        Text(
                                            text = "D${idx + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (selected) Color.White else ExerciseTitleDark,
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    IconButton(
                        onClick = onResetWorkout,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.94f), CircleShape)
                            .shadow(2.dp, CircleShape)
                            .testTag("btn_overview_reset")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Progress",
                            tint = ExerciseTitleDark,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Solid Pure-White Title & Framed Stat Capsules Header
            Surface(
                color = PureWhiteCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = eyebrowText,
                            fontSize = 11.sp,
                            color = TextSecondarySlate,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.6.sp
                        )
                        Text(
                            text = activeDayTitle,
                            fontSize = 23.sp,
                            lineHeight = 28.sp,
                            color = ExerciseTitleDark,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FramedStatCapsule(
                            label = "TIME",
                            value = durationBadgeText,
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "MOVES",
                            value = "$totalExercisesCount",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "SETS",
                            value = "$totalSets",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "BURN",
                            value = "~$estimatedKcalBurn",
                            subTag = "kcal",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (expectedResultsTimeline.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEFF6FF),
                            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_overview_results_timeline")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EXPECTED RESULTS: $expectedResultsTimeline",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E3A8A)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(color = hairlineSubtle)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 2A. Scheduled Workout Intensity Selector: [ Simple | Moderate | Extensive ]
                if (!isQuickHomeSession) {
                    WorkoutIntensitySelectorBar(
                        selectedMode = intensityMode,
                        estimatedKcal = estimatedKcalBurn,
                        onSelectMode = onSelectIntensity,
                        testTagPrefix = "overview_intensity"
                    )
                }

                // 2B. Interactive Day 1 -> Day 7 / Day 28 Challenge Roadmap (when opening a Multi-Day Challenge!)
                if (isMultiDayChallenge && challengeTotalDays > 0) {
                    val dayChipScroll = rememberScrollState()
                    val challengeFrac = (completedChallengeDays.size.toFloat() / challengeTotalDays.toFloat()).coerceIn(0f, 1f)
                    val titleLower = activeDayTitle.lowercase()
                    val isCalisthenicsChallenge = "calisthenics" in titleLower ||
                        "planche" in titleLower || "handstand" in titleLower
                    val weekPhaseHint = when {
                        isCalisthenicsChallenge && challengeTotalDays >= 28 -> when {
                            selectedChallengeDay <= 7 -> "Stage 1 (Week 1) • Push-Ups, Assisted Pulls, Squats & Hollow Plank"
                            selectedChallengeDay <= 14 -> "Stage 2 (Week 2) • Strict Pull-Ups, Parallel Dips & Wall Handstand"
                            selectedChallengeDay <= 21 -> "Stage 3 (Week 3) • Bar Muscle-Up Power & Tuck Front Lever"
                            else -> "Stage 4 (Week 4) • Planche Lean, Human Flag, L-Sit & Pistol Squat"
                        }
                        isCalisthenicsChallenge -> when {
                            selectedChallengeDay <= 7 -> "Phase 1 (Days 1–7) • Scapular Control, Hollow Line & Skill Prep"
                            else -> "Phase 2 (Days 8–14) • Strict Skill Holds, Transitions & Full Lockout"
                        }
                        selectedChallengeDay <= 7 -> "Week 1 • Master clean form & full range"
                        selectedChallengeDay <= 14 -> "Week 2 • Steady tempo & crisp control"
                        selectedChallengeDay <= 21 -> "Week 3 • Peak muscle contraction"
                        else -> "Week 4 • Final sculpting push"
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, hairlineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_challenge_day_roadmap")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$challengeTotalDays-Day Challenge Roadmap",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ExerciseTitleDark
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = SoftSurfaceTint
                                ) {
                                    Text(
                                        text = "${completedChallengeDays.size} / $challengeTotalDays Done",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1D63ED),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                progress = { challengeFrac },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(999.dp)),
                                color = Color(0xFF16A34A),
                                trackColor = SoftSurfaceTint,
                                strokeCap = StrokeCap.Round
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(dayChipScroll),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                (1..challengeTotalDays).forEach { dayNum ->
                                    val isSelectedDay = dayNum == selectedChallengeDay
                                    val isDoneDay = dayNum in completedChallengeDays
                                    Surface(
                                        onClick = { onSelectChallengeDay(dayNum) },
                                        shape = RoundedCornerShape(10.dp),
                                        color = when {
                                            isSelectedDay -> ExerciseTitleDark
                                            isDoneDay -> Color(0xFFDCFCE7)
                                            else -> SoftSurfaceTint
                                        },
                                        border = BorderStroke(
                                            1.dp,
                                            when {
                                                isSelectedDay -> ExerciseTitleDark
                                                isDoneDay -> Color(0xFF86EFAC)
                                                else -> hairlineSubtle
                                            }
                                        ),
                                        modifier = Modifier
                                            .defaultMinSize(minWidth = 46.dp, minHeight = 42.dp)
                                            .testTag("challenge_day_pill_$dayNum")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "D$dayNum",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = when {
                                                    isSelectedDay -> Color.White
                                                    isDoneDay -> Color(0xFF15803D)
                                                    else -> ExerciseTitleDark
                                                }
                                            )
                                            Text(
                                                text = if (isDoneDay) "✓" else "•",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isSelectedDay -> Color.White.copy(alpha = 0.85f)
                                                    isDoneDay -> Color(0xFF15803D)
                                                    else -> TextSecondarySlate
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "Day $selectedChallengeDay  •  $weekPhaseHint",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondarySlate
                            )
                        }
                    }
                }

                // 2C. Compact Standalone Level Selector (for Challenges & Quick Packs)
                if (isQuickHomeSession) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, hairlineSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Difficulty Level",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExerciseTitleDark
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE5ECF4),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("challenge_experience_selector")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    ExperienceLevel.entries.forEach { lvl ->
                                        val selected = experienceLevel == lvl
                                        Surface(
                                            onClick = { onSelectExperienceLevel(lvl) },
                                            shape = RoundedCornerShape(9.dp),
                                            color = if (selected) ExerciseTitleDark else Color.Transparent,
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(38.dp)
                                                .testTag("challenge_level_${lvl.name.lowercase()}")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = lvl.challengeLabel,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                    color = if (selected) Color.White else ExerciseTitleDark,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Compact 1-line Blueprint Strip (zero multi-sentence word-walls above Warm-Up!)
                if (!isQuickHomeSession && blueprint != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, hairlineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("blueprint_overview_summary")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = blueprint.headlineBadge.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1D63ED),
                                letterSpacing = 0.4.sp,
                                maxLines = 1
                            )
                            Text(
                                text = blueprint.repSchemeBadge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExerciseTitleDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                val workoutSynergyNote = remember(activeDayTitle) {
                    WeeklySchedulePlanner.plainWhyWorkoutWorks(activeDayTitle)
                }
                if (workoutSynergyNote != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_workout_synergy_explain")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "HOW THIS WORKOUT WORKS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0369A1),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = workoutSynergyNote,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                // 3. Clean Warm-Up Prep Card (Neutral border, clean warm dot accent)
                if (warmupSpecs.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFFEA580C), CircleShape)
                                )
                                Text(
                                    text = "Warm-Up Prep",
                                    fontSize = 15.sp,
                                    color = ExerciseTitleDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "${warmupSpecs.size} light moves",
                                fontSize = 12.sp,
                                color = TextSecondarySlate,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PureWhiteCard,
                            border = BorderStroke(1.dp, hairlineSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                warmupSpecs.forEachIndexed { idx, wSpec ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = hairlineSubtle)
                                    }
                                    val wVar = getWarmupOrStretchVariant(wSpec, SessionStageType.PRE_WARMUP)
                                    val warmupPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
                                        exerciseName = wSpec.name,
                                        primaryMuscle = wSpec.primaryMuscle,
                                        workoutContextTitle = activeDayTitle
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                overviewCoachTarget = Triple(wVar, null, false)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                            .testTag("card_warmup_overview_$idx"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        AnimatedWorkoutCharacter(
                                            animationType = wVar.animationType,
                                            exerciseName = wSpec.name,
                                            containerColor = Color.White,
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = wSpec.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ExerciseTitleDark
                                            )
                                            Text(
                                                text = warmupPurpose,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = TextSecondarySlate,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = wSpec.repPrescription,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFEA580C)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Clean Main Workout Sheet + 1-Tap Switch Exercise Button on Every Row
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF1D63ED), CircleShape)
                            )
                            Text(
                                text = "Main Workout Sets",
                                fontSize = 16.sp,
                                color = ExerciseTitleDark,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "Tap for form • ⇄ to switch",
                            fontSize = 12.sp,
                            color = TextSecondarySlate
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = PureWhiteCard,
                        border = BorderStroke(1.dp, hairlineSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            mainExercises.forEachIndexed { idx, spec ->
                                if (idx > 0) {
                                    HorizontalDivider(color = hairlineSubtle)
                                }
                                val variant = getMainVariant(spec, idx)
                                val isExpanded = expandedIndex == idx

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expandedIndex = if (isExpanded) -1 else idx }
                                        .padding(horizontal = 14.dp, vertical = 14.dp)
                                        .testTag("card_exercise_overview_$idx"),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        AnimatedWorkoutCharacter(
                                            animationType = variant.animationType,
                                            exerciseName = variant.name,
                                            containerColor = Color.White,
                                            modifier = Modifier
                                                .size(62.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                        )

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            val muscleTag = WeeklySchedulePlanner.whatExerciseDealsWith(
                                                variant.name,
                                                variant.primaryMuscle
                                            )
                                            val rolePrefix = when {
                                                idx == 0 -> "1 • PRIMARY COMPOUND"
                                                idx == mainExercises.lastIndex && mainExercises.size >= 4 ->
                                                    "${idx + 1} • FINISHER"
                                                idx == 1 -> "2 • SECONDARY ANGLE"
                                                else -> "${idx + 1} • TARGETED SCULPT"
                                            }
                                            Text(
                                                text = "$rolePrefix  •  ${muscleTag.uppercase()}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color(0xFF1D63ED),
                                                letterSpacing = 0.4.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = variant.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ExerciseTitleDark,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            val plainPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
                                                exerciseName = variant.name,
                                                primaryMuscle = variant.primaryMuscle,
                                                workoutContextTitle = activeDayTitle
                                            )
                                            Text(
                                                text = plainPurpose,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF334155),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            val loadBadgeText = getMainPrescribedLoadText(spec, variant)
                                            Text(
                                                text = "${variant.setsAndRepsLabel}  •  $loadBadgeText",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextSecondarySlate,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Surface(
                                                onClick = {
                                                    overviewCoachTarget = Triple(variant, spec to idx, true)
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFFFF7ED),
                                                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .testTag("btn_overview_zero_to_hero_$idx")
                                            ) {
                                                Text(
                                                    text = "Can't do it? 0→Hero Tips →",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFF9A3412),
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            onClick = { onOpenSwapSheet(spec, idx) },
                                            shape = RoundedCornerShape(999.dp),
                                            color = SoftSurfaceTint,
                                            border = BorderStroke(1.dp, hairlineSubtle),
                                            modifier = Modifier
                                                .defaultMinSize(minHeight = 36.dp)
                                                .testTag("btn_overview_swap_$idx")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SwapHoriz,
                                                    contentDescription = "Switch exercise",
                                                    tint = ExerciseTitleDark,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Text(
                                                    text = "Switch",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ExerciseTitleDark
                                                )
                                            }
                                        }
                                    }

                                    if (isExpanded) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(SoftSurfaceTint, RoundedCornerShape(12.dp))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = variant.singleFormTip,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ExerciseTitleDark
                                            )
                                            variant.cues.forEachIndexed { stepIdx, cue ->
                                                Text(
                                                    text = "${stepIdx + 1}. $cue",
                                                    fontSize = 12.sp,
                                                    color = TextSecondarySlate,
                                                    lineHeight = 17.sp
                                                )
                                            }
                                            Text(
                                                text = "Avoid: ${variant.commonMistake}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ExerciseTitleDark
                                            )
                                            Surface(
                                                onClick = {
                                                    overviewCoachTarget = Triple(variant, spec to idx, true)
                                                },
                                                shape = RoundedCornerShape(9.dp),
                                                color = ExerciseTitleDark,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .defaultMinSize(minHeight = 38.dp)
                                                    .testTag("btn_expanded_zero_to_hero_$idx")
                                            ) {
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        text = "Open 4-Step 0 → Hero Mastery Guide",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Clean Cool-Down Stretch Sheet (Neutral border, calm teal dot accent)
                if (stretchSpecs.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF0D9488), CircleShape)
                                )
                                Text(
                                    text = "Cool-Down Stretch",
                                    fontSize = 15.sp,
                                    color = ExerciseTitleDark,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = "${stretchSpecs.size} static holds",
                                fontSize = 12.sp,
                                color = TextSecondarySlate,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = PureWhiteCard,
                            border = BorderStroke(1.dp, hairlineSubtle),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                stretchSpecs.forEachIndexed { idx, sSpec ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = hairlineSubtle)
                                    }
                                    val sVar = getWarmupOrStretchVariant(sSpec, SessionStageType.POST_STRETCH)
                                    val stretchPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
                                        exerciseName = sSpec.name,
                                        primaryMuscle = sSpec.primaryMuscle,
                                        workoutContextTitle = activeDayTitle
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                overviewCoachTarget = Triple(sVar, null, false)
                                            }
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                            .testTag("card_stretch_overview_$idx"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        AnimatedWorkoutCharacter(
                                            animationType = sVar.animationType,
                                            exerciseName = sSpec.name,
                                            containerColor = Color.White,
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                        )
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = sSpec.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ExerciseTitleDark
                                            )
                                            Text(
                                                text = stretchPurpose,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                color = TextSecondarySlate,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = sSpec.repPrescription,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF0D9488)
                                        )
                                    }
                                }

                                HorizontalDivider(color = hairlineSubtle)

                                Text(
                                    text = "Holding 30s stretches after lifting prevents shortened muscles and keeps your posture upright.",
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    color = TextSecondarySlate,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
                }
            }

            // Sticky Bottom Start Bar (non-overlapping so scroll items are never occluded)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PureWhiteCard,
                shadowElevation = 10.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = hairlineSubtle)
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onStartFullWorkout,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_start_workout_from_overview"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ExerciseTitleDark,
                                contentColor = PureWhiteCard
                            )
                        ) {
                            Text(
                                text = if (isMultiDayChallenge) "START DAY $selectedChallengeDay" else "START WORKOUT",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhiteCard,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (!isQuickHomeSession && warmupSpecs.isNotEmpty()) {
                            Text(
                                text = "Already warmed up? Skip to Main Workout →",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondarySlate,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onStartSkipWarmup() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("btn_skip_warmup_overview")
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class RepRhythmMode(val label: String, val multiplier: Float) {
    NATURAL("Natural Rhythm", 1.00f),
    BRISK("Brisk Rhythm", 0.85f),
    CONTROLLED("Controlled Tension", 1.22f);

    fun next(): RepRhythmMode = when (this) {
        NATURAL -> BRISK
        BRISK -> CONTROLLED
        CONTROLLED -> NATURAL
    }
}

/**
 * SCREEN 2: GUIDED WORKOUT-TOGETHER HUMAN COACH
 * - Top half: Bright athletic studio stage with anatomically welded human instructor moving WITH you.
 * - "READY TO GO!" only runs once at the very start of the session (never double-blocking after Rest).
 * - Counts every single rep clearly from 1..N in lockstep with the human coach, visual rep pips,
 *   set indicators, and live-progress "✓ DONE SET" button.
 */
@Composable
private fun ActiveSetScreenView(
    playlistIndex: Int,
    totalPlaylistItems: Int,
    phaseStartPlaylistIndex: Int = 0,
    stageType: SessionStageType = SessionStageType.MAIN_WORKOUT,
    stageLocalIndex: Int = 0,
    stageTotalCount: Int = 1,
    stageBadgeLabel: String,
    showSkipWarmupButton: Boolean,
    activeVar: ExerciseFallbackVariant,
    sex: BiologicalSex = BiologicalSex.MALE,
    experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE,
    currentSetNumber: Int,
    prescribedWeightKg: Float,
    canAdjustWeight: Boolean = false,
    onAdjustWeightKg: (Float) -> Unit = {},
    audioCoachMode: WorkoutAudioCoachMode = WorkoutAudioCoachMode.BOTH_ACTIVE,
    onSelectAudioCoachMode: (WorkoutAudioCoachMode) -> Unit = {},
    soundEnabled: Boolean,
    canSwapVariant: Boolean = true,
    onToggleSound: () -> Unit,
    onNavigateBack: () -> Unit,
    onSkipWarmupToMain: () -> Unit = {},
    onSelectPlaylistIndex: (Int) -> Unit = {},
    onSwapVariant: () -> Unit,
    onCompleteSet: () -> Unit
) {
    val context = LocalContext.current
    val voiceEnabled = audioCoachMode.voiceEnabled
    val isFirstExerciseKickoff = (playlistIndex == 0 && currentSetNumber == 1)
    // Full "READY TO GO!" ring ONLY on session kickoff; subsequent exercises/sets already had Rest countdown!
    val initialLeadInMs = if (isFirstExerciseKickoff) 8_000L else 1_400L
    val liveGuide = remember(activeVar.animationType) { resolveLiveCoachGuide(activeVar.animationType) }

    var pacerPlaying by remember(activeVar.name, currentSetNumber) { mutableStateOf(true) }
    var rhythmMode by rememberSaveable { mutableStateOf(RepRhythmMode.NATURAL) }
    var prepRemainingMs by remember(activeVar.name, currentSetNumber) { mutableLongStateOf(initialLeadInMs) }
    var elapsedMillis by remember(activeVar.name, currentSetNumber) { mutableLongStateOf(0L) }
    var activeDisplayRep by remember(activeVar.name, currentSetNumber) { mutableIntStateOf(1) }
    var coachLiveBanner by remember(activeVar.name, currentSetNumber) {
        mutableStateOf(
            if (isFirstExerciseKickoff) liveGuide.phaseOneCue
            else "Get set — ${liveGuide.phaseOneCue}"
        )
    }
    var showFormInfoDialog by remember { mutableStateOf(false) }
    var openCoachInZeroToHero by remember { mutableStateOf(false) }

    val baseRepDurationMs = experienceLevel.repDurationMsFor(
        animationType = activeVar.animationType,
        isBodyweight = activeVar.isBodyweight
    )
    val repDurationMs = (baseRepDurationMs * rhythmMode.multiplier).roundToInt().toLong().coerceAtLeast(1000L)
    val totalTargetCount = activeVar.targetRepsCount.coerceAtLeast(1)

    LaunchedEffect(activeVar.name, currentSetNumber, pacerPlaying, repDurationMs) {
        if (isFirstExerciseKickoff && prepRemainingMs >= initialLeadInMs - 80L && elapsedMillis == 0L && voiceEnabled) {
            TacticalAudioHaptics.triggerGetReadyLeadInVoice(
                context = context,
                setNumber = currentSetNumber,
                exerciseName = activeVar.name,
                isTimed = activeVar.isTimedSeconds,
                targetCount = totalTargetCount,
                experience = experienceLevel,
                setupTip = liveGuide.phaseOneCue,
                isFirstInSession = true
            )
        }

        var lastPrepSec = -1
        var hasAnnouncedStart = false
        var hasSpokenTimedBreathingTip = false
        var lastStartedRep = 0
        var lastTurnaroundRep = 0
        var lastTimedMilestoneSec = -1

        while (pacerPlaying) {
            delay(50L)

            // Prep Lead-In: Full READY TO GO! on Exercise 1, or quick 1.4s posture beat after Rest
            if (prepRemainingMs > 0L) {
                prepRemainingMs = (prepRemainingMs - 50L).coerceAtLeast(0L)
                if (isFirstExerciseKickoff) {
                    val prepSec = ((prepRemainingMs + 999L) / 1000L).toInt()
                    if (prepSec != lastPrepSec && prepSec > 0) {
                        lastPrepSec = prepSec
                        if (prepSec <= 3) {
                            coachLiveBanner = "Starting in $prepSec..."
                            TacticalAudioHaptics.triggerCountdownTick(context)
                            if (voiceEnabled) {
                                TacticalAudioHaptics.triggerCountdownVoice(context, prepSec)
                            }
                        } else {
                            coachLiveBanner = liveGuide.phaseOneCue
                        }
                    }
                }
                continue
            }

            // Kickoff cue the instant active exercise begins
            if (!hasAnnouncedStart) {
                hasAnnouncedStart = true
                TacticalAudioHaptics.triggerRepBeatPulse(context)
                coachLiveBanner = liveGuide.spokenFormBreathingTip
                if (voiceEnabled && elapsedMillis == 0L) {
                    TacticalAudioHaptics.triggerActiveExerciseKickoffVoice(
                        context = context,
                        exerciseName = activeVar.name,
                        isTimed = activeVar.isTimedSeconds,
                        targetCount = totalTargetCount
                    )
                }
            }

            // Live Synchronized Human Coach Execution
            if (activeVar.isTimedSeconds) {
                val totalMs = totalTargetCount * 1000L
                elapsedMillis += 50L
                val remMs = (totalMs - elapsedMillis).coerceAtLeast(0L)
                val remSec = ((remMs + 999L) / 1000L).toInt()

                // ~5s into timed exercise: natural trainer breathing & technique coaching cue
                if (!hasSpokenTimedBreathingTip && elapsedMillis >= 4800L && remSec >= 8) {
                    hasSpokenTimedBreathingTip = true
                    coachLiveBanner = liveGuide.spokenFormBreathingTip
                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerLiveFormAndBreathingVoice(
                            context = context,
                            spokenTip = liveGuide.spokenFormBreathingTip
                        )
                    }
                }

                if (remSec != lastTimedMilestoneSec) {
                    lastTimedMilestoneSec = remSec
                    val halfSec = totalTargetCount / 2
                    when {
                        totalTargetCount >= 24 && remSec == halfSec ->
                            coachLiveBanner = "Halfway there — keep that rhythm!"
                        remSec == 10 && totalTargetCount >= 18 ->
                            coachLiveBanner = "10 seconds left — stay right with me!"
                        remSec in 1..3 -> {
                            coachLiveBanner = "Final $remSec..."
                            TacticalAudioHaptics.triggerCountdownTick(context)
                        }
                    }
                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerGuidedTimedMilestoneVoice(
                            context = context,
                            remainingSec = remSec,
                            totalSec = totalTargetCount
                        )
                    }
                }

                if (remMs <= 0L) {
                    coachLiveBanner = "Well done! Entering rest..."
                    pacerPlaying = false
                    onCompleteSet()
                    break
                }
            } else {
                val currentRep = ((elapsedMillis / repDurationMs).toInt() + 1).coerceIn(1, totalTargetCount)
                val repFraction = ((elapsedMillis % repDurationMs).toFloat() / repDurationMs.toFloat()).coerceIn(0f, 1f)
                activeDisplayRep = currentRep
                val halfway = (totalTargetCount / 2).coerceAtLeast(4)

                // Start of Rep (0.00): Tactile beat + clear Phase 1 coaching subtitle
                if (currentRep != lastStartedRep) {
                    lastStartedRep = currentRep
                    if (currentRep > 1) {
                        TacticalAudioHaptics.triggerRepBeatPulse(context)
                    }

                    coachLiveBanner = when {
                        currentRep == totalTargetCount -> "Final rep — ${liveGuide.phaseOneCoachDialogue}"
                        totalTargetCount >= 6 && currentRep == halfway -> "Halfway — ${liveGuide.phaseOneCoachDialogue}"
                        else -> liveGuide.phaseOneCoachDialogue
                    }
                }

                // Mid-Rep Peak/Drive (~0.48): Tactile tick + Spoken Rep Count on EVERY rep from 1..N!
                if (repFraction >= 0.48f && lastTurnaroundRep != currentRep && elapsedMillis < totalTargetCount * repDurationMs) {
                    lastTurnaroundRep = currentRep
                    TacticalAudioHaptics.triggerTurnaroundPulse(context)

                    coachLiveBanner = when {
                        currentRep == totalTargetCount -> "${liveGuide.phaseTwoCoachDialogue} Strong finish!"
                        totalTargetCount >= 6 && currentRep == halfway -> "${liveGuide.phaseTwoCoachDialogue} Halfway!"
                        else -> liveGuide.phaseTwoCoachDialogue
                    }

                    if (voiceEnabled) {
                        TacticalAudioHaptics.triggerGuidedRepCountVoice(
                            context = context,
                            repNumber = currentRep,
                            totalReps = totalTargetCount,
                            driveCue = liveGuide.shortDriveVoiceCue
                        )
                    }
                }

                elapsedMillis += 50L

                if (elapsedMillis >= (totalTargetCount * repDurationMs) + 550L) {
                    coachLiveBanner = "Set complete! Great work."
                    pacerPlaying = false
                    onCompleteSet()
                    break
                }
            }
        }
    }

    val isPrepPhase = prepRemainingMs > 0L
    val showFullReadyToGoScreen = isPrepPhase && isFirstExerciseKickoff

    val cycleProgress: Float = when {
        isPrepPhase -> 0f
        activeVar.isTimedSeconds && activeVar.animationType == ExerciseAnimationType.JUMPING_JACKS ->
            ((elapsedMillis % 1050L).toFloat() / 1050f).coerceIn(0f, 1f)
        activeVar.isTimedSeconds && liveGuide.isHoldMove ->
            ((elapsedMillis % 3200L).toFloat() / 3200f).coerceIn(0f, 1f)
        else ->
            ((elapsedMillis % repDurationMs).toFloat() / repDurationMs.toFloat()).coerceIn(0f, 1f)
    }

    val timedRemainingSec = ((totalTargetCount * 1000L - elapsedMillis + 999L) / 1000L).toInt().coerceAtLeast(0)
    val prepSecDisplay = ((prepRemainingMs + 999L) / 1000L).toInt().coerceAtLeast(1)
    val prepRingFraction = (prepRemainingMs.toFloat() / initialLeadInMs.toFloat()).coerceIn(0f, 1f)

    val activeMainCounterText = if (activeVar.isTimedSeconds) {
        String.format("%02d:%02d", timedRemainingSec / 60, timedRemainingSec % 60)
    } else {
        "$activeDisplayRep / $totalTargetCount"
    }

    val setProgressFraction = when {
        isPrepPhase -> 0f
        activeVar.isTimedSeconds -> (elapsedMillis.toFloat() / (totalTargetCount * 1000f)).coerceIn(0f, 1f)
        else -> (elapsedMillis.toFloat() / (totalTargetCount * repDurationMs).toFloat()).coerceIn(0f, 1f)
    }

    val exactLoadText = if (activeVar.isBodyweight || prescribedWeightKg <= 0f) {
        "Bodyweight"
    } else if (prescribedWeightKg % 1f == 0f) {
        "${prescribedWeightKg.roundToInt()} kg"
    } else {
        "${"%.1f".format(prescribedWeightKg)} kg"
    }

    val livePhaseChipText = when {
        isPrepPhase -> "GET SET"
        !pacerPlaying -> "PAUSED"
        activeVar.isTimedSeconds -> if ((timedRemainingSec / 3) % 2 == 0) liveGuide.phaseOneCommand else liveGuide.phaseTwoCommand
        cycleProgress < 0.48f -> liveGuide.phaseOneCommand
        else -> liveGuide.phaseTwoCommand
    }

    // Clean, Breathable Phase Palette (NO dark horizontal bars or rainbow 9-segment strips!)
    val phasePrimaryAccent = when (stageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFEA580C)    // Warm Sunrise Coral-Amber
        SessionStageType.MAIN_WORKOUT -> Color(0xFF1D63ED)  // Crisp Athletic Royal Blue
        SessionStageType.POST_STRETCH -> Color(0xFF0D9488)  // Calm Recovery Teal
    }
    val phaseTrackAccent = when (stageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFFB923C)
        SessionStageType.MAIN_WORKOUT -> Color(0xFF3E7BF6)
        SessionStageType.POST_STRETCH -> Color(0xFF14B8A6)
    }
    val phaseTopPillBg = when (stageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFFFF7ED)
        SessionStageType.MAIN_WORKOUT -> Color(0xFFEFF6FF)
        SessionStageType.POST_STRETCH -> Color(0xFFF0FDFA)
    }
    val phaseTopPillText = when (stageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFC2410C)
        SessionStageType.MAIN_WORKOUT -> Color(0xFF1D4ED8)
        SessionStageType.POST_STRETCH -> Color(0xFF0F766E)
    }
    val phaseHeaderTint = when (stageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFFFF7ED)
        SessionStageType.MAIN_WORKOUT -> PureWhiteCard
        SessionStageType.POST_STRETCH -> Color(0xFFF0FDFA)
    }
    val readyHeadlineText = when (stageType) {
        SessionStageType.PRE_WARMUP -> "WARM-UP PREP"
        SessionStageType.MAIN_WORKOUT -> "READY TO GO!"
        SessionStageType.POST_STRETCH -> "COOL-DOWN STRETCH"
    }
    val weightStepDeltaKg = ProgressiveLoadEngine.weightStepDeltaKg(prescribedWeightKg)
    val weightStepText = if (weightStepDeltaKg % 1f == 0f) "${weightStepDeltaKg.roundToInt()}kg" else "${"%.1f".format(weightStepDeltaKg)}kg"

    if (showFormInfoDialog) {
        ExerciseMasteryCoachDialog(
            exerciseName = activeVar.name,
            primaryMuscle = "${activeVar.primaryMuscle} • ${activeVar.secondaryMuscle}",
            animationType = activeVar.animationType,
            sex = sex,
            existingCues = activeVar.cues,
            existingBreathing = activeVar.breathingPattern,
            existingMistake = activeVar.commonMistake,
            initialTabZeroToHero = openCoachInZeroToHero,
            onOpenSwapExercise = if (canSwapVariant) onSwapVariant else null,
            onDismiss = { showFormInfoDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhiteCard)
            .testTag("screen_active_set")
    ) {
        // =====================================================================
        // TOP HALF (~52%): CLEAN PURE-WHITE STUDIO STAGE + FLOATING PHASE PILL
        // =====================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.52f)
                .background(PureWhiteCard)
        ) {
            // Subtle top-edge phase atmosphere for Warm-Up & Cool-Down that melts into pure #FFFFFF before the figure
            if (stageType != SessionStageType.MAIN_WORKOUT) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(phaseHeaderTint, PureWhiteCard)
                            )
                        )
                )
            }

            HeroMovementAnimationContainer(
                animationType = activeVar.animationType,
                targetMuscle = activeVar.primaryMuscle,
                singleFormTip = activeVar.singleFormTip,
                exerciseName = activeVar.name,
                startPosition = activeVar.startPosition,
                finishPosition = activeVar.finishPosition,
                externalCycleProgress = cycleProgress,
                isPrepPhase = isPrepPhase,
                isPaused = !pacerPlaying,
                setProgressFraction = setProgressFraction,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 52.dp, bottom = 6.dp)
                    .testTag("visual_movement_container")
            )

            // Clean Top Controls + Phase Identity Pill (Zero dark horizontal bar!)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_back_to_dashboard")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ExerciseTitleDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = phaseTopPillBg,
                        modifier = Modifier.testTag("banner_workout_phase_identity")
                    ) {
                        Text(
                            text = stageBadgeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = phaseTopPillText,
                            letterSpacing = 0.3.sp,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    if (showSkipWarmupButton) {
                        Surface(
                            onClick = onSkipWarmupToMain,
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.testTag("btn_skip_warmup_active")
                        ) {
                            Text(
                                text = "Skip →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExerciseTitleDark,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    CompactMusicPill(
                        audioCoachMode = audioCoachMode,
                        onSelectAudioCoachMode = onSelectAudioCoachMode
                    )

                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_sound_toggle")
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (soundEnabled) "Mute Audio" else "Enable Audio",
                            tint = if (soundEnabled) ExerciseTitleDark else TextSecondarySlate,
                            modifier = Modifier.size(21.dp)
                        )
                    }

                    if (canSwapVariant) {
                        IconButton(
                            onClick = onSwapVariant,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_swap_variant")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Exercise",
                                tint = ExerciseTitleDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            // Floating "Can't do it? 0→Hero" Rescue Pill at bottom-right of studio stage (zero vertical squeeze on transport bar!)
            Surface(
                onClick = {
                    openCoachInZeroToHero = true
                    showFormInfoDialog = true
                },
                shape = RoundedCornerShape(999.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 6.dp)
                    .testTag("btn_cant_do_zero_to_hero")
            ) {
                Text(
                    text = "Can't do it? 0→Hero →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF9A3412),
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                )
            }
        }

        // =====================================================================
        // PHASE-SCOPED SINGLE-COLOR PROGRESS BAR (Shows ONLY current phase moves!)
        // =====================================================================
        val safeStageCount = stageTotalCount.coerceAtLeast(1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PureWhiteCard)
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (localIdx in 0 until safeStageCount) {
                val isDoneOrCurrent = localIdx <= stageLocalIndex
                val targetGlobalIdx = (phaseStartPlaylistIndex + localIdx).coerceIn(0, (totalPlaylistItems - 1).coerceAtLeast(0))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(14.dp)
                        .clickable { onSelectPlaylistIndex(targetGlobalIdx) }
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (isDoneOrCurrent) phasePrimaryAccent else phasePrimaryAccent.copy(alpha = 0.16f)
                        )
                )
            }
        }

        // =====================================================================
        // BOTTOM HALF (~48%): CLEAN PURE-WHITE GUIDED CONTROL DECK
        // =====================================================================
        if (showFullReadyToGoScreen) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.48f)
                    .background(PureWhiteCard)
                    .padding(horizontal = 24.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = readyHeadlineText,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black,
                        color = phasePrimaryAccent,
                        letterSpacing = 0.5.sp,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = activeVar.name.uppercase(),
                            fontSize = 18.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ExerciseTitleDark,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        IconButton(
                            onClick = {
                                openCoachInZeroToHero = false
                                showFormInfoDialog = true
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_form_info")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = "Exercise Instructions",
                                tint = TextSecondarySlate,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }

                    Text(
                        text = coachLiveBanner,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondarySlate,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("banner_live_coach_cue")
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(112.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                            val strokePx = 7.dp.toPx()
                            drawArc(
                                color = phasePrimaryAccent.copy(alpha = 0.16f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = phasePrimaryAccent,
                                startAngle = -90f,
                                sweepAngle = 360f * prepRingFraction,
                                useCenter = false,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "$prepSecDisplay",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            color = ExerciseTitleDark,
                            style = TextStyle(fontFeatureSettings = "tnum"),
                            modifier = Modifier.testTag("txt_live_rep_or_timer_counter")
                        )
                    }

                    IconButton(
                        onClick = { prepRemainingMs = 0L },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Skip Ready Countdown",
                            tint = TextSecondarySlate,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.48f)
                    .background(PureWhiteCard)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Phase-Appropriate Eyebrow + Bold Exercise Name + Coaching Cue
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val eyebrowStatusText = when (stageType) {
                            SessionStageType.PRE_WARMUP ->
                                "WARM-UP PREP  •  LIGHT JOINT MOBILITY"
                            SessionStageType.POST_STRETCH ->
                                "COOL-DOWN STRETCH  •  SLOW BREATHING"
                            SessionStageType.MAIN_WORKOUT ->
                                "SET $currentSetNumber OF ${activeVar.totalSets}  •  ${exactLoadText.uppercase()}"
                        }

                        Text(
                            text = eyebrowStatusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = phasePrimaryAccent,
                            letterSpacing = 0.5.sp
                        )

                        if (canAdjustWeight) {
                            Surface(
                                onClick = { onAdjustWeightKg(-weightStepDeltaKg) },
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.testTag("btn_weight_minus_2kg")
                            ) {
                                Text(
                                    text = "−$weightStepText",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExerciseTitleDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                onClick = { onAdjustWeightKg(weightStepDeltaKg) },
                                shape = RoundedCornerShape(999.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.testTag("btn_weight_plus_2kg")
                            ) {
                                Text(
                                    text = "+$weightStepText",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExerciseTitleDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = activeVar.name.uppercase(),
                            fontSize = 19.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ExerciseTitleDark,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        IconButton(
                            onClick = {
                                openCoachInZeroToHero = false
                                showFormInfoDialog = true
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_form_info")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = "Exercise Instructions",
                                tint = TextSecondarySlate,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = coachLiveBanner,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondarySlate,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("banner_live_coach_cue")
                    )
                }

                // 2. Hero Counter (00:30 or 1 / 12)
                Text(
                    text = activeMainCounterText,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = ExerciseTitleDark,
                    letterSpacing = (-0.5).sp,
                    style = TextStyle(fontFeatureSettings = "tnum"),
                    modifier = Modifier.testTag("txt_live_rep_or_timer_counter")
                )

                // 3. Phase-Colored Bottom Transport Bar
                val doneButtonText = when (stageType) {
                    SessionStageType.PRE_WARMUP -> stageType.doneButtonLabel
                    SessionStageType.POST_STRETCH -> stageType.doneButtonLabel
                    SessionStageType.MAIN_WORKOUT -> if (currentSetNumber < activeVar.totalSets) {
                        "✓ DONE SET $currentSetNumber"
                    } else {
                        "✓ DONE EXERCISE"
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (playlistIndex > 0) onSelectPlaylistIndex(playlistIndex - 1)
                        },
                        enabled = playlistIndex > 0,
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("btn_prev_exercise")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Exercise",
                            tint = if (playlistIndex > 0) TextSecondarySlate else Color(0xFFCBD5E1),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Surface(
                        onClick = { pacerPlaying = !pacerPlaying },
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier
                            .size(52.dp)
                            .testTag("btn_pause_pacer")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (pacerPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (pacerPlaying) "Pause Workout" else "Resume Workout",
                                tint = ExerciseTitleDark,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(phaseTrackAccent)
                            .clickable { onCompleteSet() }
                            .testTag("btn_complete_set")
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(setProgressFraction)
                                .background(phasePrimaryAccent)
                        )
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = doneButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PureWhiteCard,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (playlistIndex < totalPlaylistItems - 1) {
                                onSelectPlaylistIndex(playlistIndex + 1)
                            } else {
                                onCompleteSet()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .testTag("btn_next_exercise")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Exercise",
                            tint = TextSecondarySlate,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestingScreenView(
    restRemainingMillis: Long,
    totalRestMillis: Long,
    completedStageType: SessionStageType = SessionStageType.MAIN_WORKOUT,
    upcomingStageType: SessionStageType = SessionStageType.MAIN_WORKOUT,
    audioCoachMode: WorkoutAudioCoachMode = WorkoutAudioCoachMode.BOTH_ACTIVE,
    onSelectAudioCoachMode: (WorkoutAudioCoachMode) -> Unit = {},
    soundEnabled: Boolean,
    upNextHeading: String,
    upNextTitle: String,
    upNextSubtitle: String,
    upNextAnimType: ExerciseAnimationType,
    onNavigateBack: () -> Unit,
    onToggleSound: () -> Unit,
    onExtendRest: () -> Unit,
    onSkipRest: () -> Unit
) {
    val totalSec = ((restRemainingMillis + 999L) / 1000L).coerceAtLeast(0L)
    val mins = totalSec / 60L
    val secs = totalSec % 60L
    val formattedClock = String.format("%02d:%02d", mins, secs)

    val progressFraction = if (totalRestMillis > 0L) {
        (restRemainingMillis.toFloat() / totalRestMillis.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val isEnteringMainWorkout = completedStageType == SessionStageType.PRE_WARMUP &&
        upcomingStageType == SessionStageType.MAIN_WORKOUT
    val isEnteringStretch = completedStageType == SessionStageType.MAIN_WORKOUT &&
        upcomingStageType == SessionStageType.POST_STRETCH

    val restAccent = when (upcomingStageType) {
        SessionStageType.PRE_WARMUP -> Color(0xFFEA580C)
        SessionStageType.MAIN_WORKOUT -> Color(0xFF1D63ED)
        SessionStageType.POST_STRETCH -> Color(0xFF0D9488)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhiteCard)
            .testTag("screen_resting"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Minimal Borderless Top Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("btn_back_to_active_set")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = ExerciseTitleDark,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CompactMusicPill(
                        audioCoachMode = audioCoachMode,
                        onSelectAudioCoachMode = onSelectAudioCoachMode
                    )

                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_sound_toggle_rest")
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = if (soundEnabled) "Mute All" else "Enable Music & Voice",
                            tint = if (soundEnabled) ExerciseTitleDark else TextSecondarySlate,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }

            // Clean Soft Phase-Transition Milestone Pill (No dark harsh block)
            if (isEnteringMainWorkout || isEnteringStretch) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isEnteringMainWorkout) Color(0xFFEFF6FF) else Color(0xFFF0FDFA),
                    border = BorderStroke(
                        1.dp,
                        if (isEnteringMainWorkout) Color(0xFFBFDBFE) else Color(0xFFCCFBF1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("banner_rest_phase_transition")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (isEnteringMainWorkout) {
                                "WARM-UP COMPLETE  •  MAIN WORKOUT NEXT"
                            } else {
                                "MAIN WORKOUT COMPLETE  •  COOL-DOWN NEXT"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isEnteringMainWorkout) Color(0xFF1D4ED8) else Color(0xFF0F766E),
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isEnteringMainWorkout) {
                                "Joints are warm — moving into your working sets."
                            } else {
                                "Great work! Let's lengthen tight muscles and breathe."
                            },
                            fontSize = 12.sp,
                            color = ExerciseTitleDark,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // 2. Clean Center Rest Countdown & Pill Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = when {
                    isEnteringMainWorkout -> "GET READY FOR MAIN WORKOUT"
                    isEnteringStretch -> "TRANSITION TO STRETCH"
                    else -> "REST"
                },
                fontSize = if (isEnteringMainWorkout || isEnteringStretch) 16.sp else 22.sp,
                fontWeight = FontWeight.Black,
                color = restAccent,
                letterSpacing = 0.9.sp,
                textAlign = TextAlign.Center
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(162.dp)
                    .testTag("box_circular_timer")
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                    val strokePx = 8.dp.toPx()
                    drawArc(
                        color = Color(0xFFE8EEF5),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = restAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = formattedClock,
                    fontSize = 44.sp,
                    color = ExerciseTitleDark,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.86f),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onExtendRest,
                    shape = RoundedCornerShape(999.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("btn_extend_rest_20s")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "+20s",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ExerciseTitleDark
                        )
                    }
                }

                Button(
                    onClick = onSkipRest,
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = restAccent,
                        contentColor = PureWhiteCard
                    ),
                    modifier = Modifier
                        .weight(1.4f)
                        .height(50.dp)
                        .testTag("btn_skip_rest_begin_now")
                ) {
                    Text(
                        text = if (isEnteringMainWorkout) "START MAIN →" else "SKIP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PureWhiteCard,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }

        // 3. Clean Bottom "NEXT" Exercise Preview with Live Looping Human Demonstration
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_up_next_preview")
        ) {
            HorizontalDivider(color = Color(0xFFE8EEF5), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = upNextHeading,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = restAccent,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = upNextTitle.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ExerciseTitleDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = upNextSubtitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondarySlate
                    )
                }

                AnimatedWorkoutCharacter(
                    animationType = upNextAnimType,
                    exerciseName = upNextTitle,
                    containerColor = Color.White,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}

@Composable
private fun SessionCompletedView(
    dayTitle: String,
    isQuickHomeSession: Boolean,
    challengeDayBadge: String? = null,
    streakDays: Int = 1,
    workoutNumber: Int = 1,
    nextDayLabel: String?,
    totalVolumeKg: Float,
    totalSetsCompleted: Int,
    estimatedKcalBurned: Int = 275,
    returnButtonLabel: String = "BACK TO DASHBOARD ->",
    onReturnHome: () -> Unit
) {
    val scrollState = rememberScrollState()

    FitnessAtmosphericContainer(
        modifier = Modifier
            .fillMaxSize()
            .testTag("screen_workout_completed")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, CardBorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_victory_receipt")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Celebratory Streak & Workout # Header Pills
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFED7AA))
                        ) {
                            Text(
                                text = "🔥 $streakDays-DAY STREAK",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC2410C),
                                letterSpacing = 0.4.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = SoftSurfaceTint
                        ) {
                            Text(
                                text = challengeDayBadge ?: "WORKOUT #$workoutNumber",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ExerciseTitleDark,
                                letterSpacing = 0.4.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(InnerFrameSurface)
                            .border(2.dp, TimerAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = TimerAmber,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        text = if (isQuickHomeSession) "Session Complete!" else "Workout Complete!",
                        fontSize = 23.sp,
                        color = ExerciseTitleDark,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = dayTitle,
                        fontSize = 14.sp,
                        color = TextSecondarySlate,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FramedStatCapsule(
                            label = "SETS DONE",
                            value = "$totalSetsCompleted",
                            subTag = "logged",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = "EST. BURN",
                            value = "~$estimatedKcalBurned",
                            subTag = "kcal",
                            modifier = Modifier.weight(1f)
                        )
                        FramedStatCapsule(
                            label = if (isQuickHomeSession) "STATUS" else "VOLUME",
                            value = challengeDayBadge ?: if (isQuickHomeSession) "Done ✓" else "${totalVolumeKg.roundToInt()} kg",
                            subTag = if (isQuickHomeSession) "saved" else "lifted",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "Fuel with 30–40g protein within 60 mins for clean recovery.",
                        fontSize = 12.sp,
                        color = TextSecondarySlate,
                        textAlign = TextAlign.Center
                    )

                    if (nextDayLabel != null) {
                        HorizontalDivider(color = BorderMuted)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "UP NEXT IN YOUR PLAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextSecondarySlate,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = nextDayLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ExerciseTitleDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onReturnHome,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExerciseTitleDark,
                    contentColor = PureWhiteCard
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_return_to_dashboard")
            ) {
                Text(
                    text = returnButtonLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PureWhiteCard,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

private fun ApexCoachMovementSpec.toCombatExerciseSpec(index: Int): CombatExerciseSpec {
    val isTimed = setsAndRepsLabel.contains(":")
    val parsedValue = if (isTimed) {
        setsAndRepsLabel.substringAfter(":").toIntOrNull() ?: 30
    } else {
        setsAndRepsLabel.removePrefix("x").toIntOrNull() ?: 12
    }
    return CombatExerciseSpec(
        id = id,
        orderIndex = index,
        name = name,
        modalityTag = stationBadge,
        targetMuscle = targetMusclePill,
        primaryMuscle = targetMusclePill,
        secondaryMuscle = "Core & Stabilizers",
        totalSets = totalSets,
        targetRepsCount = parsedValue,
        repPrescription = setsAndRepsLabel,
        defaultLoadKg = defaultLoadKg,
        executionCue = formCueSummary,
        formTeacherCues = listOf(formCueSummary)
    )
}

private fun buildWarmupOrStretchVariant(
    spec: CombatExerciseSpec,
    stageType: SessionStageType
): ExerciseFallbackVariant {
    val isTimed = spec.repPrescription.contains(":") || "sec" in spec.repPrescription.lowercase()
    val exactCount = spec.targetRepsCount.coerceAtLeast(10)
    val anim = inferGymAnimationForSpec(spec, spec.orderIndex, 0)
    val setsRepsText = if (isTimed) {
        "${spec.totalSets} Set × ${spec.repPrescription} Hold"
    } else {
        "${spec.totalSets} Set × $exactCount Reps"
    }

    return ExerciseFallbackVariant(
        name = spec.name,
        setsAndRepsLabel = setsRepsText,
        totalSets = spec.totalSets,
        repPrescription = if (isTimed) spec.repPrescription else "$exactCount",
        targetRepsCount = exactCount,
        modality = spec.modalityTag,
        primaryMuscle = spec.primaryMuscle,
        secondaryMuscle = spec.secondaryMuscle,
        singleFormTip = spec.executionCue,
        breathingPattern = if (stageType == SessionStageType.POST_STRETCH) {
            "Take slow 4-second exhales so tight muscle fibers lengthen and relax."
        } else {
            "Breathe rhythmically through your nose—keep joints smooth and relaxed."
        },
        commonMistake = if (stageType == SessionStageType.POST_STRETCH) {
            "Bouncing in the stretch or holding your breath."
        } else {
            "Moving stiffly or rushing before joints feel warm."
        },
        startPosition = spec.startPositionLabel,
        finishPosition = spec.finishPositionLabel,
        cues = spec.formTeacherCues,
        animationType = anim,
        isTimedSeconds = isTimed,
        isBodyweight = true
    )
}

internal fun buildQuickStrikeVariant(
    qsSpec: ApexCoachMovementSpec,
    challengeLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE
): ExerciseFallbackVariant {
    val isTimed = qsSpec.setsAndRepsLabel.contains(":")
    val baseParsedValue = if (isTimed) {
        qsSpec.setsAndRepsLabel.substringAfter(":").toIntOrNull() ?: 30
    } else {
        qsSpec.setsAndRepsLabel.removePrefix("x").toIntOrNull() ?: 12
    }
    val (scaledSets, scaledValue) = ProgressiveLoadEngine.scaleStandaloneChallengeMove(
        baseSets = qsSpec.totalSets,
        baseRepsOrSec = baseParsedValue,
        isTimedSeconds = isTimed,
        challengeLevel = challengeLevel
    )
    val repPrescription = if (isTimed) {
        String.format("%02d:%02d", scaledValue / 60, scaledValue % 60)
    } else {
        "$scaledValue"
    }
    val setsRepsText = if (isTimed) {
        val setWord = if (scaledSets == 1) "Set" else "Sets"
        "$scaledSets $setWord × $repPrescription Hold"
    } else {
        val setWord = if (scaledSets == 1) "Set" else "Sets"
        "$scaledSets $setWord × $scaledValue Reps"
    }

    val qsLower = qsSpec.name.lowercase()
    val (adaptedName, adaptedAnim, adaptedTip, adaptedIsBodyweight) = if (challengeLevel == ExperienceLevel.NEW_BEGINNER) {
        when {
            "jumping jack" in qsLower && "step" !in qsLower && "easy" !in qsLower ->
                BeginnerMoveAdaptation(
                    "Low-Impact Step Jacks",
                    ExerciseAnimationType.JUMPING_JACKS,
                    "Step one foot out at a time while sweeping arms overhead — zero harsh knee impact.",
                    true
                )
            qsLower == "push-ups" || qsLower == "standard push-ups" ->
                BeginnerMoveAdaptation(
                    "Box Incline Push-Ups",
                    ExerciseAnimationType.INCLINE_PUSH_UPS,
                    "Hands on a sturdy couch or bench edge so wrists and shoulders adapt safely.",
                    true
                )
            "pike" in qsLower && "handstand" !in qsLower ->
                BeginnerMoveAdaptation(
                    "Wall Push-Ups",
                    ExerciseAnimationType.INCLINE_PUSH_UPS,
                    "Stand arm's length from a wall and press smoothly — zero neck or shoulder strain.",
                    true
                )
            "dip" in qsLower && "muscle-up" !in qsLower && "parallel" !in qsLower && "l-sit" !in qsLower ->
                BeginnerMoveAdaptation(
                    "Floor Tricep Dips",
                    ExerciseAnimationType.TRICEP_DIPS,
                    "Sit on mat with knees bent and hands behind hips — bend and lock elbows gently.",
                    true
                )
            "close-grip push" in qsLower ->
                BeginnerMoveAdaptation(
                    "Knee Push-Ups",
                    ExerciseAnimationType.KNEE_PUSH_UPS,
                    "Rest knees comfortably on mat and keep elbows tucked at 45°.",
                    true
                )
            "dead hang" in qsLower ->
                BeginnerMoveAdaptation(
                    "Feet-Supported Bar / Doorframe Hold",
                    ExerciseAnimationType.LAT_PULLDOWN,
                    "Keep toes lightly on the floor to take half your bodyweight off raw finger tendons.",
                    true
                )
            "knuckle" in qsLower ->
                BeginnerMoveAdaptation(
                    "Wall Plank Hold",
                    ExerciseAnimationType.INCLINE_PUSH_UPS,
                    "Brace hands flat against wall or high counter — protects beginner wrists.",
                    true
                )
            "walking lunge" in qsLower ->
                BeginnerMoveAdaptation(
                    "Supported Step Lunges",
                    ExerciseAnimationType.FORWARD_LUNGES,
                    "Step smoothly near a wall or chair for balance — bodyweight only.",
                    true
                )
            else ->
                BeginnerMoveAdaptation(qsSpec.name, qsSpec.animationType, qsSpec.formCueSummary, qsSpec.defaultLoadKg <= 0f)
        }
    } else {
        BeginnerMoveAdaptation(qsSpec.name, qsSpec.animationType, qsSpec.formCueSummary, qsSpec.defaultLoadKg <= 0f)
    }

    return ExerciseFallbackVariant(
        name = adaptedName,
        setsAndRepsLabel = setsRepsText,
        totalSets = scaledSets,
        repPrescription = repPrescription,
        targetRepsCount = scaledValue,
        modality = qsSpec.stationBadge,
        primaryMuscle = qsSpec.targetMusclePill,
        secondaryMuscle = "Core & Posture",
        singleFormTip = adaptedTip,
        breathingPattern = if (isTimed) {
            "Breathe steadily through your nose—never hold your breath."
        } else {
            "Inhale on the lowering phase • Exhale smoothly as you push."
        },
        commonMistake = "Rushing reps with momentum or letting your lower back sag.",
        startPosition = "Brace core & set posture",
        finishPosition = "Full muscle squeeze",
        cues = listOf(
            adaptedTip,
            "Match the trainer's smooth 2-second lowering speed.",
            "Keep your neck neutral and core braced on every rep."
        ),
        animationType = adaptedAnim,
        isTimedSeconds = isTimed,
        isBodyweight = adaptedIsBodyweight,
        swapCategoryBadge = "Pack Prescribed",
        swapReasonNote = "Primary movement programmed for this pack."
    )
}

internal fun buildQuickStrikeVariantsList(
    qsSpec: ApexCoachMovementSpec,
    challengeLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE
): List<ExerciseFallbackVariant> {
    val primary = buildQuickStrikeVariant(qsSpec, challengeLevel)
    val lower = qsSpec.name.lowercase()
    val muscleLower = qsSpec.targetMusclePill.lowercase()

    data class AltSpec(
        val name: String,
        val anim: ExerciseAnimationType,
        val tip: String,
        val isBodyweight: Boolean,
        val badge: String,
        val reason: String,
        val loadMult: Float = 0.7f
    )

    val (alt1, alt2) = when {
        // Calisthenics Progressive Skill Regressions (Stages 2–4)
        "planche" in lower -> Pair(
            AltSpec(
                name = if ("pseudo-planche" in lower) "Parallettes Planche Lean" else "Tuck Planche Hold",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Lock elbows 100% straight, protract shoulders tall, and lean forward under control.",
                isBodyweight = true,
                badge = "Stage 3 Skill Step",
                reason = "Builds exact straight-arm shoulder tendon strength toward full planche."
            ),
            AltSpec(
                name = "Scapular Protracted Plank",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Push the floor away with bone-straight arms and posterior pelvic tilt.",
                isBodyweight = true,
                badge = "Stage 1 Foundation",
                reason = "Safe wrist-friendly foundation for straight-arm planche mechanics."
            )
        )
        "handstand" in lower -> Pair(
            AltSpec(
                name = if ("tap" in lower) "Chest-to-Wall Handstand Hold" else "Elevated Pike Shoulder Press",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Stack hips above shoulders with locked arms and active fingertip grip.",
                isBodyweight = true,
                badge = "Stage 2 Skill Step",
                reason = "Builds overhead shoulder stacking before freestanding balance or walks."
            ),
            AltSpec(
                name = "Box Pike Shoulder Hold",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Place feet on a couch/chair and press shoulders tall into your ears.",
                isBodyweight = true,
                badge = "Stage 1 Foundation",
                reason = "Controlled inverted shoulder conditioning without kicking up onto a wall."
            )
        )
        "muscle-up" in lower || "muscle up" in lower -> Pair(
            AltSpec(
                name = if ("dip" in lower) "Parallel Bar Dips" else "Explosive Chest-to-Bar Pull-Ups",
                anim = if ("dip" in lower) ExerciseAnimationType.TRICEP_DIPS else ExerciseAnimationType.LAT_PULLDOWN,
                tip = if ("dip" in lower) "Lower chest forward between bars and lock out cleanly." else "Pull explosively in a C-arc until lower chest meets bar height.",
                isBodyweight = true,
                badge = "Stage 2 Skill Step",
                reason = "Builds the exact explosive pull or top dip lockout needed for bar muscle-ups."
            ),
            AltSpec(
                name = if ("dip" in lower) "Bench Tricep Dips" else "Doorframe / Inverted Rows",
                anim = if ("dip" in lower) ExerciseAnimationType.TRICEP_DIPS else ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Smooth controlled bodyweight reps with zero elbow tendon strain.",
                isBodyweight = true,
                badge = "Stage 1 Assisted",
                reason = "Accessible home/park regression while building muscle-up strength."
            )
        )
        "front lever" in lower -> Pair(
            AltSpec(
                name = if ("tuck" in lower) "Single-Leg Front Lever Hold" else "Tight Tuck Front Lever Hold",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Keep arms 100% straight and push the bar down toward your hips.",
                isBodyweight = true,
                badge = "Skill Lever Step",
                reason = "Adjusts horizontal lever length while preserving pure straight-arm lat tension."
            ),
            AltSpec(
                name = "Active Scapular Bar Hang",
                anim = ExerciseAnimationType.LAT_PULLDOWN,
                tip = "Hang with locked elbows and depress shoulder blades firmly away from ears.",
                isBodyweight = true,
                badge = "Stage 1 Foundation",
                reason = "Develops straight-arm lat depression and grip before horizontal levers."
            )
        )
        "human-flag" in lower || "human flag" in lower || "side-flag" in lower || "side lever" in lower -> Pair(
            AltSpec(
                name = "Vertical Pole Push-Pull Hold",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Lock bottom pushing arm straight with thumb down while pulling with top arm.",
                isBodyweight = true,
                badge = "Pole Flag Prep",
                reason = "Grooves the exact bottom-push + top-pull pole mechanics at a manageable angle."
            ),
            AltSpec(
                name = "High-Tension Star Side Plank",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Drive hips high on one locked arm to forge iron lateral obliques and shoulders.",
                isBodyweight = true,
                badge = "No-Pole Home Swap",
                reason = "Trains human-flag lateral core and shoulder stability anywhere with zero pole."
            )
        )
        "l-sit" in lower -> Pair(
            AltSpec(
                name = "Tucked Parallettes L-Sit",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Press shoulders down hard and float tucked knees cleanly off the floor.",
                isBodyweight = true,
                badge = "Stage 2 Tuck Step",
                reason = "Cuts hamstring & hip-flexor torque in half while building L-sit support."
            ),
            AltSpec(
                name = "Seated Compression Leg Lifts",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Sit tall with hands beside knees and lift locked straight heels off the mat.",
                isBodyweight = true,
                badge = "Stage 1 Foundation",
                reason = "Builds active quad & lower-ab compression when wrists need a break."
            )
        )
        "pistol" in lower -> Pair(
            AltSpec(
                name = "Box-Supported Pistol Squat",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Lower smoothly on one leg to tap a chair/bench, then drive right back up.",
                isBodyweight = true,
                badge = "Controlled Depth Step",
                reason = "Builds single-leg strength safely while ankle mobility catches up."
            ),
            AltSpec(
                name = "Doorframe-Assisted Pistol Squat",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Hold a pole or doorframe lightly for balance through full single-leg depth.",
                isBodyweight = true,
                badge = "Assisted Full Range",
                reason = "Removes falling-backward fear so your quad works through full depth."
            )
        )

        // Glutes & Booty
        "hip thrust" in lower -> Pair(
            AltSpec(
                name = "Dumbbell Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Rest dumbbell across hip crease on floor and squeeze glutes 2s at top.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "No hip-thrust machine or bench? Works in any small gym with 1 dumbbell.",
                loadMult = 0.55f
            ),
            AltSpec(
                name = "Floor Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Drive heels into mat and hold top glute contraction 2 full seconds.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Zero equipment, zero lower-back pressure — pure glute activation."
            )
        )
        "abductor" in lower || "hip abduction" in lower -> Pair(
            AltSpec(
                name = if ("machine" in lower) "Standing Hip Abductions" else "Dumbbell Goblet Squat",
                anim = if ("machine" in lower) ExerciseAnimationType.FORWARD_LUNGES else ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = if ("machine" in lower) "Hold rack/wall and sweep leg outward with 1s outer glute pause." else "Wide stance squat targeting outer hips and glutes.",
                isBodyweight = "machine" in lower,
                badge = "Small Gym / No Machine",
                reason = "Hits outer hip curves when an abductor machine isn't available.",
                loadMult = 0.5f
            ),
            AltSpec(
                name = "Floor Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Press knees slightly outward as you bridge hips up.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Comfortable floor option that still shapes hips and buttocks."
            )
        )
        "kickback" in lower -> Pair(
            AltSpec(
                name = "Dumbbell Reverse Lunges",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Step backward smoothly and drive through front heel to lift upper glutes.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "No ankle-cuff cable tower needed — shapes upper glutes with dumbbells.",
                loadMult = 0.8f
            ),
            AltSpec(
                name = if ("cable" in lower) "Standing Glute Kickbacks" else "Floor Glute Bridge",
                anim = if ("cable" in lower) ExerciseAnimationType.FORWARD_LUNGES else ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Brace stomach flat and squeeze upper glute cleanly at the top.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Zero equipment needed while keeping tension on the booty shelf."
            )
        )

        // Quads, Legs & Deadlifts
        "leg press" in lower || "leg extension" in lower -> Pair(
            AltSpec(
                name = "Dumbbell Goblet Squat",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Hold 1 dumbbell against sternum and sit smoothly between your heels.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "Fits any small hotel/apartment gym with just one dumbbell.",
                loadMult = 0.35f
            ),
            AltSpec(
                name = "Bodyweight Squats",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Control 2s down to parallel and stand tall through mid-foot.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Easy on knees while building leg endurance."
            )
        )
        "romanian deadlift" in lower || "rdl" in lower -> Pair(
            AltSpec(
                name = "Dumbbell Hip Thrust",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Upper back on bench, drive heels down to load glutes and hamstrings.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "Targets glutes & hamstrings with less lower-back hinge demand.",
                loadMult = 0.85f
            ),
            AltSpec(
                name = "Floor Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Dig heels into mat and squeeze hamstrings and glutes for 2s.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Zero spinal load while training the posterior chain."
            )
        )
        "lunge" in lower || "split squat" in lower -> Pair(
            AltSpec(
                name = "Dumbbell Goblet Squat",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Two-foot balanced stance holding a dumbbell at chest height.",
                isBodyweight = false,
                badge = "Balanced Two-Leg Swap",
                reason = "More stable than single-leg lunges while working quads & glutes.",
                loadMult = 0.9f
            ),
            AltSpec(
                name = "Bodyweight Squats",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Smooth knee-friendly squats at your comfortable depth.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Removes single-leg balance stress on knees and ankles."
            )
        )
        "squat" in lower || "glute bridge" in lower || "calf" in lower -> Pair(
            AltSpec(
                name = when {
                    "goblet" in lower -> "Dumbbell Reverse Lunges"
                    "bridge" in lower -> "Dumbbell Hip Thrust"
                    else -> "Dumbbell Goblet Squat"
                },
                anim = if ("goblet" in lower) ExerciseAnimationType.FORWARD_LUNGES else ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Controlled lower-body tempo with tall chest.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "Adds clean dumbbell resistance for faster leg & glute shaping.",
                loadMult = 0.75f
            ),
            AltSpec(
                name = if ("floor glute bridge" in lower) "Bodyweight Squats" else "Floor Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Smooth bodyweight reps squeezing glutes at the top.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Gentle on knees and doable anywhere."
            )
        )

        // Chest & Push
        "press" in lower && ("chest" in lower || "bench" in lower || "incline" in lower) || "pec fly" in lower -> Pair(
            AltSpec(
                name = if ("30° incline dumbbell press" in lower) "Flat Dumbbell Bench Press" else "30° Incline Dumbbell Press",
                anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                tip = "Set bench and press dumbbells smoothly with 45° elbows.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "Works with any pair of dumbbells when chest machines are busy.",
                loadMult = 0.75f
            ),
            AltSpec(
                name = "Box Incline Push-Ups",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Hands on bench or sturdy edge, lower chest with straight body.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Easy on shoulders and wrists while training the exact chest fibers."
            )
        )
        "push-up" in lower || "pushup" in lower -> Pair(
            AltSpec(
                name = if ("incline" in lower) "Knee Push-Ups" else "Box Incline Push-Ups",
                anim = if ("incline" in lower) ExerciseAnimationType.KNEE_PUSH_UPS else ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Smooth controlled chest press at a joint-friendly angle.",
                isBodyweight = true,
                badge = "Easier Leverage",
                reason = "Reduces wrist & shoulder load while keeping crisp chest tension."
            ),
            AltSpec(
                name = if ("wall" in lower) "Knee Push-Ups" else "Wall Push-Ups",
                anim = if ("wall" in lower) ExerciseAnimationType.KNEE_PUSH_UPS else ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Controlled press keeping core tight and shoulders relaxed.",
                isBodyweight = true,
                badge = "Joint-Easy / Beginner",
                reason = "Gentle variation anyone can complete with clean form."
            )
        )

        // Back, Rows, Pulldowns, Pull-Ups & Grip
        "pulldown" in lower || "row" in lower || "face pull" in lower || "dead hang" in lower || "farmer" in lower || "pull-up" in lower || "chin-up" in lower -> Pair(
            AltSpec(
                name = if ("one-arm dumbbell row" in lower) "Chest-Supported Dumbbell Row" else "One-Arm Dumbbell Row",
                anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                tip = "Brace torso steady and pull dumbbell cleanly toward your hip.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "No cable tower needed — fits even the smallest dumbbell corner.",
                loadMult = 0.55f
            ),
            AltSpec(
                name = if ("doorframe" in lower) "Prone Back Extensions" else "Doorframe Rows",
                anim = if ("doorframe" in lower) ExerciseAnimationType.COBRA_STRETCH else ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Squeeze shoulder blades together for 1 full second at the top.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Zero-equipment back & posture pull you can do anywhere."
            )
        )

        // Shoulders, Biceps & Triceps
        "lateral raise" in lower || "shoulder press" in lower || "pike" in lower -> Pair(
            AltSpec(
                name = if ("dumbbell lateral raise" in lower) "Seated Dumbbell Shoulder Press" else "Dumbbell Lateral Raise",
                anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                tip = "Controlled shoulder movement with zero lower-back arch.",
                isBodyweight = false,
                badge = "Small Gym / Dumbbells",
                reason = "Requires only light dumbbells in minimal floor space.",
                loadMult = 0.65f
            ),
            AltSpec(
                name = "Wall Push-Ups",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Controlled shoulder-friendly press against a wall.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Removes overhead impingement stress while keeping delts active."
            )
        )
        "pushdown" in lower || "tricep" in lower || "dip" in lower || "curl" in lower -> Pair(
            AltSpec(
                name = when {
                    "standing dumbbell curl" in lower -> "Dumbbell Hammer Curl"
                    "curl" in lower -> "Standing Dumbbell Curl"
                    "bench tricep dip" in lower -> "Overhead Dumbbell Tricep Extension"
                    else -> "Bench Tricep Dips"
                },
                anim = if ("curl" in lower) ExerciseAnimationType.INCLINE_DB_CURL else ExerciseAnimationType.TRICEP_DIPS,
                tip = "Keep elbows steady at your sides for isolated arm tension.",
                isBodyweight = "curl" !in lower && "bench tricep dip" !in lower,
                badge = "Small Gym / Dumbbells",
                reason = "Simple small-gym swap with zero cable pulley required.",
                loadMult = 0.75f
            ),
            AltSpec(
                name = if ("floor tricep dip" in lower) "Close-Grip Knee Push-Ups" else "Floor Tricep Dips",
                anim = if ("floor tricep dip" in lower) ExerciseAnimationType.KNEE_PUSH_UPS else ExerciseAnimationType.TRICEP_DIPS,
                tip = "Bend and lock elbows smoothly with shoulders down.",
                isBodyweight = true,
                badge = "Joint-Easy / Bodyweight",
                reason = "Safe short-range arm movement with zero shoulder strain."
            )
        )

        // Stamina, Cardio & Conditioning
        "jumping jack" in lower || "step jack" in lower || "shoulder tap" in lower -> Pair(
            AltSpec(
                name = if ("step jack" in lower) "Bodyweight Squats" else "Low-Impact Step Jacks",
                anim = if ("step jack" in lower) ExerciseAnimationType.BODYWEIGHT_SQUATS else ExerciseAnimationType.JUMPING_JACKS,
                tip = "Zero-jump rhythmic steps keeping heart rate up and knees soft.",
                isBodyweight = true,
                badge = "Low-Impact / Apartment",
                reason = "Silent on floors and gentle on knees & ankles while building cardio stamina."
            ),
            AltSpec(
                name = "Alternating Forward Lunges",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Smooth alternating steps keeping chest tall and breathing rhythmic.",
                isBodyweight = true,
                badge = "Controlled Stamina",
                reason = "Builds lower-body cardio endurance without high-impact bounding."
            )
        )

        // Flexibility, Posture & Spine Decompression
        "lengthening" in lower -> Pair(
            AltSpec(
                name = "Thoracic Pike-to-Cobra Flow",
                anim = ExerciseAnimationType.COBRA_STRETCH,
                tip = "Exhale slowly into a gentle thoracic extension while keeping hips heavy.",
                isBodyweight = true,
                badge = "Spine Mobility Flow",
                reason = "Opens chest and upper-back flexibility with zero shoulder impingement."
            ),
            AltSpec(
                name = "Doorframe Posture Rows",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Pull shoulder blades down and back to reinforce tall upright posture.",
                isBodyweight = true,
                badge = "Standing Mobility",
                reason = "Standing option when kneeling on the floor feels uncomfortable."
            )
        )

        // Kegel & Pelvic Floor Control
        "kegel" in lower || "pelvic" in lower -> Pair(
            AltSpec(
                name = "Floor Glute Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Drive heels down and gently lift your internal pelvic floor as hips rise.",
                isBodyweight = true,
                badge = "Guided Pelvic Lift",
                reason = "Easier to feel deep pelvic engagement by pairing it with a gentle hip lift."
            ),
            AltSpec(
                name = "Core Vacuum Plank Hold",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Exhale and draw lower belly + pelvic floor inward while breathing calmly.",
                isBodyweight = true,
                badge = "Deep Core & Pelvic",
                reason = "Links deep transverse core with pelvic floor endurance."
            )
        )

        // Jawline, Neck & Tongue Posture (Mewing / Cervical Alignment)
        "chin-tuck" in lower || "chin tuck" in lower || "tongue" in lower || "mewing" in lower ||
            "neck-flexor" in lower || "hyoid" in lower || "submental" in lower -> Pair(
            AltSpec(
                name = if ("wall" in lower) "Seated Tongue-Roof Posture Hold" else "Wall Cervical Chin-Tuck",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Keep lips sealed, back of tongue gently on palate, and glide head tall.",
                isBodyweight = true,
                badge = "Posture Alternative",
                reason = "Trains cervical & tongue posture upright anywhere with zero neck strain."
            ),
            AltSpec(
                name = "Doorframe Scapular Posture Pull",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Set shoulder blades down and back to support effortless upright head alignment.",
                isBodyweight = true,
                badge = "Upper-Back Support",
                reason = "Fixes rounded shoulders that pull the head and jawline forward."
            )
        )

        // Diaphragm, Breath & Voice Power
        "diaphragmatic" in lower || "sibilant" in lower || "hiss" in lower ||
            "humming" in lower || "resonance" in lower || "box-breath" in lower || "co2" in lower -> Pair(
            AltSpec(
                name = if ("diaphragmatic" in lower) "Calm Nasal Box-Breath Cadence" else "Seated Diaphragmatic 360° Breath",
                anim = ExerciseAnimationType.COBRA_STRETCH,
                tip = "Expand lower ribs 360° on a 4-second nasal inhale while keeping shoulders relaxed.",
                isBodyweight = true,
                badge = "Calm Diaphragm",
                reason = "Builds deep vocal & lung breath support with zero throat tension."
            ),
            AltSpec(
                name = "Standing Ribcage Expansion Hold",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Stand tall, inhale into side ribs, and exhale slowly for 6 seconds.",
                isBodyweight = true,
                badge = "Standing Option",
                reason = "Comfortable upright breath control you can practice before speaking or training."
            )
        )

        // Core & Waist
        else -> Pair(
            AltSpec(
                name = if ("crunch" in lower && "reverse" !in lower) "Reverse Crunches" else "Abdominal Crunches",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Keep lower back pressed flat into the mat and exhale at peak squeeze.",
                isBodyweight = true,
                badge = "Mat Core Alternative",
                reason = "No cable station or captain's chair needed — pure abdominal tension."
            ),
            AltSpec(
                name = if ("plank" in lower && "vacuum" !in lower) "Alternating Deadbug" else "Forearm Plank Hold",
                anim = if ("plank" in lower && "vacuum" !in lower) ExerciseAnimationType.ABDOMINAL_CRUNCHES else ExerciseAnimationType.PLANK_HOLD,
                tip = if ("plank" in lower && "vacuum" !in lower) "Lie on your back and alternate slow leg reaches with zero neck/wrist strain." else "Brace stomach inward and breathe calmly.",
                isBodyweight = true,
                badge = "Joint-Easy / Spine-Safe",
                reason = "Protects neck, wrists, and lower back while tightening the waist (${if (muscleLower.isNotBlank()) qsSpec.targetMusclePill else "Core"})."
            )
        )
    }

    val v1 = primary.copy(
        name = alt1.name,
        singleFormTip = alt1.tip,
        cues = listOf(
            alt1.tip,
            "Move smoothly for 2 seconds down and 1 second squeeze.",
            "Keep breathing steady without holding your breath."
        ),
        animationType = alt1.anim,
        isBodyweight = alt1.isBodyweight,
        swapCategoryBadge = alt1.badge,
        swapReasonNote = alt1.reason,
        loadMultiplier = alt1.loadMult
    )
    val v2 = primary.copy(
        name = alt2.name,
        singleFormTip = alt2.tip,
        cues = listOf(
            alt2.tip,
            "Focus on clean muscle tension rather than speed.",
            "Keep joints comfortable and spine neutral."
        ),
        animationType = alt2.anim,
        isBodyweight = alt2.isBodyweight,
        swapCategoryBadge = alt2.badge,
        swapReasonNote = alt2.reason,
        loadMultiplier = alt2.loadMult
    )

    return listOf(primary, v1, v2).distinctBy { it.name.lowercase() }
}

private data class BeginnerMoveAdaptation(
    val name: String,
    val anim: ExerciseAnimationType,
    val tip: String,
    val isBodyweight: Boolean
)

internal fun inferGymAnimationForSpec(
    spec: CombatExerciseSpec,
    index: Int,
    dayIndex: Int
): ExerciseAnimationType {
    val lower = (spec.name + " " + spec.primaryMuscle + " " + spec.targetMuscle).lowercase()
    return when {
        "cobra" in lower || ("spine" in lower && "stretch" in lower) ->
            ExerciseAnimationType.COBRA_STRETCH
        "jumping jack" in lower || "pulse raiser" in lower || "jog" in lower || "calf" in lower || "calves" in lower ->
            ExerciseAnimationType.JUMPING_JACKS
        "pike" in lower || ("chest" in lower && "stretch" in lower) || ("incline" in lower && "opener" in lower) ->
            if ("incline" in lower && "opener" in lower) ExerciseAnimationType.INCLINE_PUSH_UPS else ExerciseAnimationType.PIKE_SHOULDER_PRESS
        "scapular" in lower || "doorframe" in lower || ("lat" in lower && "stretch" in lower) ->
            ExerciseAnimationType.DOORFRAME_ROW
        "hip flexor" in lower || "lunge" in lower || "split squat" in lower || "rdl" in lower ||
            "deadlift" in lower || "kickback" in lower || "abductor" in lower || "hamstring" in lower ->
            ExerciseAnimationType.FORWARD_LUNGES
        "squat" in lower || "leg press" in lower || "leg extension" in lower || "hip thrust" in lower ||
            "glute bridge" in lower || "quad" in lower ->
            ExerciseAnimationType.BODYWEIGHT_SQUATS
        "pulldown" in lower || "pull-up" in lower || "pullup" in lower ->
            ExerciseAnimationType.LAT_PULLDOWN
        "row" in lower || "face pull" in lower || "mid-back" in lower || "rear delt" in lower ->
            ExerciseAnimationType.SEATED_CABLE_ROW
        "lateral" in lower || "shoulder press" in lower || "overhead" in lower || "side shoulder" in lower || "delt" in lower ->
            ExerciseAnimationType.CABLE_LATERAL_RAISE
        "curl" in lower || "bicep" in lower ->
            ExerciseAnimationType.INCLINE_DB_CURL
        "tricep" in lower || "dip" in lower || "pushdown" in lower || "skullcrusher" in lower ->
            ExerciseAnimationType.TRICEP_DIPS
        "crunch" in lower || "knee raise" in lower || "leg raise" in lower || "deadbug" in lower ->
            ExerciseAnimationType.ABDOMINAL_CRUNCHES
        "plank" in lower || "vacuum" in lower ->
            ExerciseAnimationType.PLANK_HOLD
        "incline push" in lower ->
            ExerciseAnimationType.INCLINE_PUSH_UPS
        "push-up" in lower || "pushup" in lower ->
            ExerciseAnimationType.STANDARD_PUSH_UPS
        else ->
            ExerciseAnimationType.INCLINE_DB_PRESS
    }
}

@Composable
private fun SwapExerciseModalSheet(
    targetMuscleLabel: String,
    environment: TrainingEnvironment,
    variants: List<ExerciseFallbackVariant>,
    selectedIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, CardBorderLight),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dialog_swap_exercise")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (environment == TrainingEnvironment.GYM) {
                                "SWITCH GYM EXERCISE"
                            } else {
                                "SWITCH HOME EXERCISE"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RoyalBlue,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Same Target: $targetMuscleLabel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ExerciseTitleDark
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("btn_close_swap_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close exercise switcher",
                            tint = TextSecondarySlate
                        )
                    }
                }

                Text(
                    text = if (environment == TrainingEnvironment.GYM) {
                        "Missing a machine in a small gym or want an easier station? Pick the fit that works today:"
                    } else {
                        "Pick the variation that feels best for your joints and space today:"
                    },
                    fontSize = 12.sp,
                    color = TextSecondarySlate,
                    lineHeight = 17.sp
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    variants.forEachIndexed { idx, opt ->
                        val isSelected = idx == selectedIndex
                        val badgeBg = when (idx) {
                            0 -> RoyalBlueSoft
                            1 -> Color(0xFFFEF3C7)
                            else -> Color(0xFFECFDF5)
                        }
                        val badgeFg = when (idx) {
                            0 -> RoyalBlue
                            1 -> Color(0xFFB45309)
                            else -> Color(0xFF047857)
                        }
                        Surface(
                            onClick = { onSelectIndex(idx) },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) Color(0xFFF8FAFF) else PureWhiteCard,
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) RoyalBlue else CardBorderLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 68.dp)
                                .testTag("swap_option_$idx")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AnimatedWorkoutCharacter(
                                    animationType = opt.animationType,
                                    exerciseName = opt.name,
                                    containerColor = Color.White,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                )

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = badgeBg
                                    ) {
                                        Text(
                                            text = opt.swapCategoryBadge,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = badgeFg,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = opt.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExerciseTitleDark,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (opt.swapReasonNote.isNotBlank()) {
                                        Text(
                                            text = opt.swapReasonNote,
                                            fontSize = 11.sp,
                                            color = TextSecondarySlate,
                                            lineHeight = 15.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected option",
                                        tint = RoyalBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Select option",
                                        tint = TextSecondarySlate,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SwapMoveSeed(
    val name: String,
    val modality: String,
    val anim: ExerciseAnimationType,
    val tip: String,
    val badge: String,
    val reason: String,
    val isBodyweight: Boolean = false,
    val loadMultiplier: Float = 1.0f
)

/**
 * Produces 100% DECISIVE, EXACT rep/time prescriptions (NEVER "10-12" or "12-15")
 * AND strict environment-pure 3-option Switch Exercise menus:
 * - GYM mode: [0] Standard Gym, [1] Small Gym • Dumbbells/Bench Only, [2] Easier to Handle (0 home floor moves!)
 * - HOME mode: [0] Standard Home, [1] Easier to Handle, [2] Focused Variation (0 gym machines!)
 */
internal fun buildFallbackVariantsFor(
    spec: CombatExerciseSpec,
    index: Int,
    dayIndex: Int = 0,
    experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE,
    bodyweightKg: Float = 80f,
    sex: BiologicalSex = BiologicalSex.MALE,
    completedWorkoutsCount: Int = 0,
    daysPerWeek: Int = 5,
    trackWeekOverride: Int? = null,
    intensityMode: WorkoutIntensityMode = WorkoutIntensityMode.MODERATE,
    environment: TrainingEnvironment = TrainingEnvironment.GYM
): List<ExerciseFallbackVariant> {
    val rawGymAnim = inferGymAnimationForSpec(spec, index, dayIndex)
    val specLower = spec.name.lowercase()
    val isNewBeg = experienceLevel == ExperienceLevel.NEW_BEGINNER
    val isTimed = spec.repPrescription.contains(":") || "sec" in spec.repPrescription.lowercase()

    val primaryMuscle = spec.primaryMuscle.ifBlank { "Target Muscle" }
    val secondaryMuscle = spec.secondaryMuscle.ifBlank { "Core & Stabilizers" }
    val baseTip = spec.executionCue.ifBlank {
        "Move at a smooth, natural rhythm and drive cleanly through full range."
    }

    fun buildVariantFromSeed(seed: SwapMoveSeed, isPrimarySpec: Boolean = false): ExerciseFallbackVariant {
        val effectiveDefaultLoad = if (seed.isBodyweight) {
            0f
        } else {
            (spec.defaultLoadKg.takeIf { it > 0f } ?: 18f) * seed.loadMultiplier
        }
        val progressive = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = effectiveDefaultLoad,
            specTotalSets = spec.totalSets,
            baseRepsOrSeconds = spec.targetRepsCount.coerceAtLeast(if (isTimed) 20 else 8),
            isTimedSeconds = isTimed,
            isBodyweight = seed.isBodyweight,
            bodyweightKg = bodyweightKg,
            sex = sex,
            experience = experienceLevel,
            completedWorkoutsCount = completedWorkoutsCount,
            daysPerWeek = daysPerWeek,
            trackWeekOverride = trackWeekOverride,
            intensityMode = intensityMode
        )
        val totalSets = progressive.sets
        val exactCount = progressive.repsOrSeconds
        val repPrescription = if (isTimed) {
            String.format("%02d:%02d", exactCount / 60, exactCount % 60)
        } else {
            "$exactCount"
        }
        val setsRepsText = if (isTimed) {
            "$totalSets Sets × $repPrescription Hold"
        } else {
            "$totalSets Sets × $exactCount Reps"
        }
        val breathing = when {
            isTimed ->
                "Breathe steadily into your core — never hold your breath during the hold."
            seed.anim == ExerciseAnimationType.LAT_PULLDOWN ||
                seed.anim == ExerciseAnimationType.SEATED_CABLE_ROW ||
                seed.anim == ExerciseAnimationType.DOORFRAME_ROW ->
                "Exhale as you pull toward your body • Inhale smoothly as your arms extend."
            seed.anim == ExerciseAnimationType.CABLE_LATERAL_RAISE ||
                seed.anim == ExerciseAnimationType.INCLINE_DB_CURL ->
                "Exhale as you lift up • Inhale smoothly on the return."
            else ->
                "Inhale smoothly on the way down • Exhale strongly as you drive up."
        }
        val mistake = when (seed.anim) {
            ExerciseAnimationType.INCLINE_DB_PRESS,
            ExerciseAnimationType.INCLINE_PUSH_UPS ->
                "Flaring elbows straight out to 90° or bouncing off the bottom."
            ExerciseAnimationType.LAT_PULLDOWN ->
                "Shrugging shoulders up by your ears or swinging your torso backward."
            ExerciseAnimationType.SEATED_CABLE_ROW ->
                "Rounding your lower back forward or jerking the weight with momentum."
            ExerciseAnimationType.CABLE_LATERAL_RAISE ->
                "Heaving with your hips or lifting hands higher than shoulder level."
            ExerciseAnimationType.INCLINE_DB_CURL ->
                "Swinging elbows forward in front of your ribs to cheat the weight up."
            ExerciseAnimationType.TRICEP_DIPS ->
                "Letting upper arms swing forward instead of keeping elbows tucked back."
            ExerciseAnimationType.BODYWEIGHT_SQUATS,
            ExerciseAnimationType.FORWARD_LUNGES ->
                "Letting knees cave inward or lifting heels off the floor."
            else ->
                "Cutting range of motion short or letting hips sag out of alignment."
        }
        val cues = if (isPrimarySpec && spec.formTeacherCues.size >= 3) {
            spec.formTeacherCues
        } else {
            listOf(
                seed.tip,
                "Keep a smooth, controlled 2-second lowering rhythm on every rep.",
                "Maintain tall posture and squeeze $primaryMuscle cleanly at the finish."
            )
        }
        return ExerciseFallbackVariant(
            name = seed.name,
            setsAndRepsLabel = setsRepsText,
            totalSets = totalSets,
            repPrescription = repPrescription,
            targetRepsCount = exactCount,
            modality = seed.modality,
            primaryMuscle = primaryMuscle,
            secondaryMuscle = secondaryMuscle,
            singleFormTip = seed.tip,
            breathingPattern = breathing,
            commonMistake = mistake,
            startPosition = if (isPrimarySpec) spec.startPositionLabel.removePrefix("Start:").trim() else "Braced setup & clean alignment",
            finishPosition = if (isPrimarySpec) spec.finishPositionLabel.removePrefix("Finish:").removePrefix("Peak:").trim() else "Full $primaryMuscle contraction",
            cues = cues,
            animationType = seed.anim,
            isTimedSeconds = isTimed,
            isBodyweight = seed.isBodyweight,
            swapCategoryBadge = seed.badge,
            swapReasonNote = seed.reason,
            loadMultiplier = seed.loadMultiplier
        )
    }

    if (environment == TrainingEnvironment.GYM) {
        val gymSeeds: List<SwapMoveSeed> = when {
            // 1. Incline Chest Press
            "incline" in specLower && "press" in specLower -> {
                if ("machine" in specLower || isNewBeg) {
                    listOf(
                        SwapMoveSeed(
                            name = "Incline Machine Chest Press",
                            modality = "Guided Incline Station",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Guided 30° path — safest upper-chest builder",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "30° Incline Dumbbell Press",
                            modality = "30° Bench • Dumbbells",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Set bench to 30°, tuck elbows 45°, and press dumbbells smoothly.",
                            badge = "Small Gym • Dumbbells Only",
                            reason = "Fits any small hotel or apartment gym with dumbbells & bench",
                            loadMultiplier = 0.55f
                        ),
                        SwapMoveSeed(
                            name = "Seated Machine Chest Press",
                            modality = "Guided Chest Machine",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Keep back anchored to the pad and press handles smoothly forward.",
                            badge = "Easier to Handle",
                            reason = "Upright seated machine if incline press is busy or missing",
                            loadMultiplier = 1.05f
                        )
                    )
                } else {
                    listOf(
                        SwapMoveSeed(
                            name = "30° Incline Dumbbell Press",
                            modality = spec.modalityTag,
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Full upper-chest stretch with independent arm control",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "Flat Dumbbell Bench Press",
                            modality = "Flat Bench • Dumbbells",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Plant feet, retract shoulder blades, and press dumbbells from mid-chest.",
                            badge = "Small Gym • Flat Bench Only",
                            reason = "Works even if your gym only has a flat bench and dumbbells",
                            loadMultiplier = 1.05f
                        ),
                        SwapMoveSeed(
                            name = "Incline Machine Chest Press",
                            modality = "Guided Incline Station",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Let the fixed track guide your path so zero shoulder balance is needed.",
                            badge = "Easier to Handle",
                            reason = "Guided machine track — easier on wrists and stabilizer muscles",
                            loadMultiplier = 1.35f
                        )
                    )
                }
            }

            // 2. Mid / Flat Chest Press
            ("flat" in specLower || "machine chest" in specLower) && "press" in specLower -> {
                if ("machine" in specLower || isNewBeg) {
                    listOf(
                        SwapMoveSeed(
                            name = "Seated Machine Chest Press",
                            modality = "Guided Chest Machine",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Stable guided chest press with zero balance strain",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "Flat Dumbbell Bench Press",
                            modality = "Flat Bench • Dumbbells",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Keep shoulder blades tucked into bench and press dumbbells smoothly.",
                            badge = "Small Gym • Dumbbells Only",
                            reason = "Fits any basic gym with dumbbells and a flat bench",
                            loadMultiplier = 0.55f
                        ),
                        SwapMoveSeed(
                            name = "Incline Machine Chest Press",
                            modality = "Guided Incline Station",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Press along the 30° guided path for comfortable shoulder alignment.",
                            badge = "Easier to Handle",
                            reason = "Gentler 30° shoulder angle when flat chest machine is taken",
                            loadMultiplier = 0.90f
                        )
                    )
                } else {
                    listOf(
                        SwapMoveSeed(
                            name = "Flat Dumbbell Bench Press",
                            modality = spec.modalityTag,
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Classic progressive chest builder with natural wrist rotation",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "30° Incline Dumbbell Press",
                            modality = "30° Bench • Dumbbells",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Set low 30° incline and press with elbows tucked at 45°.",
                            badge = "Small Gym • Dumbbells Only",
                            reason = "Upper-chest dumbbell angle using any adjustable bench",
                            loadMultiplier = 0.90f
                        ),
                        SwapMoveSeed(
                            name = "Seated Machine Chest Press",
                            modality = "Guided Chest Machine",
                            anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                            tip = "Anchor back to pad and focus 100% on squeezing chest.",
                            badge = "Easier to Handle",
                            reason = "Fixed machine path — no kicking heavy dumbbells into place",
                            loadMultiplier = 1.40f
                        )
                    )
                }
            }

            // 3. Pec Deck Inner-Chest Fly
            "pec" in specLower || ("fly" in specLower && "rear" !in specLower && "reverse" !in specLower) -> listOf(
                SwapMoveSeed(
                    name = "Pec Deck Inner-Chest Fly",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Constant inner-chest squeeze on seated fly station",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "30° Incline Dumbbell Press",
                    modality = "30° Bench • Dumbbells",
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = "Lower dumbbells into a deep chest stretch and press smoothly.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No pec-deck machine in your gym? Dumbbells cover the full chest",
                    loadMultiplier = 0.68f
                ),
                SwapMoveSeed(
                    name = "Seated Machine Chest Press",
                    modality = "Guided Chest Machine",
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = "Press smoothly with elbows comfortably tucked — gentler on front shoulders.",
                    badge = "Easier to Handle",
                    reason = "Less shoulder-stretch torque than wide chest flys",
                    loadMultiplier = 1.15f
                )
            )

            // 4. Overhead Cable Tricep Extension
            "overhead" in specLower && "tricep" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Overhead Cable Tricep Extension",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Full long-head tricep stretch on cable pulley",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Seated Bench Tricep Dips",
                    modality = "Gym Flat Bench",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Hands on gym bench edge with knees bent — lower smoothly to 90°.",
                    badge = "Small Gym • Bench Only",
                    reason = "Zero cable tower needed — works on any gym bench",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Cable Tricep Rope Pushdown",
                    modality = "High Cable Pulley",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Keep elbows pinned by your ribs and press down smoothly.",
                    badge = "Easier to Handle",
                    reason = "Keeps arms down by your sides — zero overhead shoulder mobility needed",
                    loadMultiplier = 1.05f
                )
            )

            // 5. Cable Tricep Pushdown
            "pushdown" in specLower || "pressdown" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Cable Tricep Rope Pushdown",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Crisp tricep lockout on high pulley",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Seated Bench Tricep Dips",
                    modality = "Gym Flat Bench",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Plant hands on bench edge, keep hips close to pad, and press up.",
                    badge = "Small Gym • Bench Only",
                    reason = "Fits small gyms without a cable pulley station",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Seated Tricep V-Bar Pressdown",
                    modality = "V-Bar Pulley",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Use a solid V-bar handle for firmer wrist stability.",
                    badge = "Easier to Handle",
                    reason = "Solid V-bar handle is easier on wrists and grip than rope",
                    loadMultiplier = 1.10f
                )
            )

            // 6. Assisted Dip Machine / Parallel-Bar Dips
            "dip" in specLower -> listOf(
                SwapMoveSeed(
                    name = if (isNewBeg) "Assisted Dip Machine" else spec.name,
                    modality = if (isNewBeg) "Counterweight Dip Station" else spec.modalityTag,
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Compound lower-chest & tricep station",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Flat Dumbbell Bench Press",
                    modality = "Flat Bench • Dumbbells",
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = "Keep elbows tucked closer to ribs to emphasize triceps & lower chest.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No dip bars or assisted dip station? Use dumbbells on a flat bench",
                    isBodyweight = false,
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Cable Tricep Rope Pushdown",
                    modality = "High Cable Pulley",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Pin elbows to sides and extend smoothly — zero bodyweight shoulder load.",
                    badge = "Easier to Handle",
                    reason = "Zero shoulder-joint bodyweight load while hitting triceps directly",
                    isBodyweight = false,
                    loadMultiplier = 0.95f
                )
            )

            // 7. Lat Pulldown (Wide or Neutral)
            "pulldown" in specLower -> {
                val isNeutral = "neutral" in specLower || "close" in specLower
                listOf(
                    SwapMoveSeed(
                        name = spec.name,
                        modality = spec.modalityTag,
                        anim = ExerciseAnimationType.LAT_PULLDOWN,
                        tip = baseTip,
                        badge = "Standard Gym",
                        reason = "Primary vertical lat builder for upper-back taper",
                        loadMultiplier = 1.0f
                    ),
                    SwapMoveSeed(
                        name = "One-Arm Dumbbell Row",
                        modality = "Bench-Supported Dumbbell",
                        anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                        tip = "Brace one hand and knee on bench and row dumbbell toward hip.",
                        badge = "Small Gym • Dumbbells Only",
                        reason = "No lat pulldown tower? Builds wide lats with one dumbbell & bench",
                        loadMultiplier = 0.42f
                    ),
                    SwapMoveSeed(
                        name = if (isNeutral) "Seated Machine Front Pulldown" else "Neutral-Grip Lat Pulldown",
                        modality = "Guided Lat Station",
                        anim = ExerciseAnimationType.LAT_PULLDOWN,
                        tip = "Drive elbows smoothly down toward ribs without leaning back.",
                        badge = "Easier to Handle",
                        reason = "Shoulder-friendly grip that reduces rotator-cuff strain",
                        loadMultiplier = 0.95f
                    )
                )
            }

            // 8. Chest-Supported Machine Row
            "chest-supported" in specLower || ("machine" in specLower && "row" in specLower) -> listOf(
                SwapMoveSeed(
                    name = "Chest-Supported Machine Row",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Chest pad removes lower-back fatigue completely",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Bent-Knee Dumbbell Mid-Back Row",
                    modality = "30° Bench • Dumbbells",
                    anim = ExerciseAnimationType.DOORFRAME_ROW,
                    tip = "Lean chest onto a 30° incline bench and row both dumbbells smoothly.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Recreates chest-supported row in any small gym with bench & dumbbells",
                    loadMultiplier = 0.45f
                ),
                SwapMoveSeed(
                    name = "Seated Cable Row",
                    modality = "Low Pulley V-Handle",
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = "Sit tall with knees soft and pull V-handle cleanly into lower ribs.",
                    badge = "Easier to Handle",
                    reason = "Smooth seated cable pulley found in almost every gym",
                    loadMultiplier = 1.0f
                )
            )

            // 9. Seated Cable Row
            "seated" in specLower && "row" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Seated Cable Row",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Constant cable tension across mid-back and lats",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Bent-Knee Dumbbell Mid-Back Row",
                    modality = "Dumbbells • Hinged or Bench",
                    anim = ExerciseAnimationType.DOORFRAME_ROW,
                    tip = "Brace core or chest on incline bench and pull elbows past ribs.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Works in small hotel/apartment gyms with zero cable row machine",
                    loadMultiplier = 0.45f
                ),
                SwapMoveSeed(
                    name = "Chest-Supported Machine Row",
                    modality = "Joint-Safe Chest Pad",
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = "Rest chest firmly on pad so lower back stays 100% relaxed.",
                    badge = "Easier to Handle",
                    reason = "Chest pad supports your torso — zero lower-back tiredness",
                    loadMultiplier = 0.95f
                )
            )

            // 10. Face Pulls / Rear Delts
            "face pull" in specLower || "rear-delt" in specLower || "rear delt" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Cable Rear-Delt Face Pulls",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Best posture & rear-shoulder builder on rope pulley",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Incline Bench Rear-Delt Dumbbell Fly",
                    modality = "30° Bench • Light Dumbbells",
                    anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                    tip = "Rest chest on 30° bench and sweep light dumbbells wide.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Hits exact same rear delts with light 3–6 kg dumbbells",
                    loadMultiplier = 0.42f
                ),
                SwapMoveSeed(
                    name = "Chest-Supported Machine Row",
                    modality = "High-Elbow Machine Row",
                    anim = ExerciseAnimationType.SEATED_CABLE_ROW,
                    tip = "Use wide grip on machine row to target upper back and posture.",
                    badge = "Easier to Handle",
                    reason = "Fixed machine path — simple to coordinate for beginners",
                    loadMultiplier = 1.65f
                )
            )

            // 11. Incline Dumbbell Curl
            "incline" in specLower && "curl" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Incline Dumbbell Curl",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Deep long-head bicep stretch on 45° bench",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Standing Dumbbell Curl",
                    modality = "Dumbbells • Standing",
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = "Stand tall with elbows pinned at ribs and curl smoothly.",
                    badge = "Small Gym • No Bench Needed",
                    reason = "Works anywhere in a crowded or small gym with just dumbbells",
                    loadMultiplier = 1.10f
                ),
                SwapMoveSeed(
                    name = "Machine Preacher Curl",
                    modality = "Padded Arm Station",
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = "Rest upper arms on pad and curl smoothly without swinging.",
                    badge = "Easier to Handle",
                    reason = "Arm pad locks your elbows in place so form stays effortless",
                    loadMultiplier = 1.45f
                )
            )

            // 12. Hammer Curl
            "hammer" in specLower && "curl" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Dumbbell Hammer Curl",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Neutral grip builds biceps & forearms with zero wrist twist",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Standing Dumbbell Curl",
                    modality = "Dumbbells • Supinated",
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = "Rotate palms upward as you curl and lower over 2 seconds.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Classic dumbbell curl using the exact same dumbbell rack",
                    loadMultiplier = 0.95f
                ),
                SwapMoveSeed(
                    name = "Cable EZ-Bar Bicep Curl",
                    modality = "Low Cable Pulley",
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = "Angled EZ-bar keeps wrists comfortable with smooth cable tension.",
                    badge = "Easier to Handle",
                    reason = "Two-hand EZ-bar shares the load evenly across both arms",
                    loadMultiplier = 1.50f
                )
            )

            // 13. Preacher / Cable EZ-Bar Curl
            "preacher" in specLower || "ez-bar" in specLower || ("curl" in specLower && "leg" !in specLower && "hamstring" !in specLower) -> {
                val isPreacher = "preacher" in specLower
                listOf(
                    SwapMoveSeed(
                        name = spec.name,
                        modality = spec.modalityTag,
                        anim = ExerciseAnimationType.INCLINE_DB_CURL,
                        tip = baseTip,
                        badge = "Standard Gym",
                        reason = "Strict bicep isolation with zero body swing",
                        loadMultiplier = 1.0f
                    ),
                    SwapMoveSeed(
                        name = "Standing Dumbbell Curl",
                        modality = "Dumbbells • Standing",
                        anim = ExerciseAnimationType.INCLINE_DB_CURL,
                        tip = "Keep elbows tucked at your sides and curl with zero torso sway.",
                        badge = "Small Gym • Dumbbells Only",
                        reason = "Fits any small gym without a preacher bench or EZ-bar cable",
                        loadMultiplier = 0.50f
                    ),
                    SwapMoveSeed(
                        name = if (isPreacher) "Cable EZ-Bar Bicep Curl" else "Machine Preacher Curl",
                        modality = "Guided Arm Station",
                        anim = ExerciseAnimationType.INCLINE_DB_CURL,
                        tip = "Smooth constant resistance from bottom stretch to top squeeze.",
                        badge = "Easier to Handle",
                        reason = "Comfortable wrist angle with guided resistance",
                        loadMultiplier = 0.95f
                    )
                )
            }

            // 14. Shoulder Press
            "shoulder press" in specLower -> {
                if ("machine" in specLower || isNewBeg) {
                    listOf(
                        SwapMoveSeed(
                            name = "Seated Machine Shoulder Press",
                            modality = "Guided Overhead Station",
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Guided overhead path keeps spine and shoulders safe",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "Seated Dumbbell Shoulder Press",
                            modality = "75° Bench • Dumbbells",
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = "Sit against upright bench pad and press dumbbells smoothly.",
                            badge = "Small Gym • Dumbbells Only",
                            reason = "Works in any small gym without an overhead press machine",
                            loadMultiplier = 0.65f
                        ),
                        SwapMoveSeed(
                            name = "Seated Dumbbell Lateral Raise",
                            modality = "Seated • Light Dumbbells",
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = "Raise light dumbbells only to shoulder level — zero overhead pressing.",
                            badge = "Easier to Handle",
                            reason = "Gentle on stiff necks or shoulders that dislike pressing overhead",
                            loadMultiplier = 0.38f
                        )
                    )
                } else {
                    listOf(
                        SwapMoveSeed(
                            name = "Seated Dumbbell Shoulder Press",
                            modality = spec.modalityTag,
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = baseTip,
                            badge = "Standard Gym",
                            reason = "Full shoulder development on 75° supported bench",
                            loadMultiplier = 1.0f
                        ),
                        SwapMoveSeed(
                            name = "Seated Dumbbell Lateral Raise",
                            modality = "Bench • Light Dumbbells",
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = "Sweep light dumbbells out to shoulder height with soft elbows.",
                            badge = "Small Gym • Light Dumbbells",
                            reason = "Zero heavy dumbbells needed — sculpts caps with light weights",
                            loadMultiplier = 0.45f
                        ),
                        SwapMoveSeed(
                            name = "Seated Machine Shoulder Press",
                            modality = "Guided Overhead Station",
                            anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                            tip = "Back flat against pad and press handles smoothly overhead.",
                            badge = "Easier to Handle",
                            reason = "Guided machine track removes dumbbell balancing effort",
                            loadMultiplier = 1.30f
                        )
                    )
                }
            }

            // 15. Lateral Raise
            "lateral raise" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Cable Lateral Raise",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Smooth constant side-delt tension from bottom to shoulder height",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Seated Dumbbell Lateral Raise",
                    modality = "Dumbbells • Seated or Standing",
                    anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                    tip = "Lift light dumbbells out to shoulder height without shrugging traps.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Cable tower busy or missing? Light dumbbells work anywhere",
                    loadMultiplier = 0.85f
                ),
                SwapMoveSeed(
                    name = "Seated Machine Lateral Raise",
                    modality = "Padded Side-Delt Machine",
                    anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                    tip = "Push elbows outward against pads to shoulder height.",
                    badge = "Easier to Handle",
                    reason = "Padded elbow levers remove wrist & grip fatigue completely",
                    loadMultiplier = 1.25f
                )
            )

            // 16. Sled Leg Press
            "leg press" in specLower -> listOf(
                SwapMoveSeed(
                    name = spec.name,
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Back-supported heavy leg & glute builder",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Goblet Squat",
                    modality = "Single Dumbbell • Chest-Held",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Hold one dumbbell close to chest, sit hips down smoothly, and drive tall.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Fits small hotel or apartment gyms without a 45° leg press sled",
                    loadMultiplier = 0.25f
                ),
                SwapMoveSeed(
                    name = "Seated Leg Extension",
                    modality = "Pin-Loaded Quad Machine",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Sit upright in pad and extend knees smoothly with 1s top hold.",
                    badge = "Easier to Handle",
                    reason = "Simple pin-loaded seat — zero plate loading or hip compression",
                    loadMultiplier = 0.40f
                )
            )

            // 17. Dumbbell Goblet Squat
            "goblet squat" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Dumbbell Goblet Squat",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Front-held dumbbell naturally keeps torso tall & knees happy",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Reverse Lunges",
                    modality = "Dumbbells • Controlled Step-Back",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Step backward smoothly and drive up through your front heel.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Unilateral leg & glute option using light dumbbells",
                    isBodyweight = isNewBeg,
                    loadMultiplier = if (isNewBeg) 0f else 0.65f
                ),
                SwapMoveSeed(
                    name = "45° Sled Leg Press",
                    modality = "Guided Sled Machine",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Back supported against pad — lower sled to 90° and drive through heels.",
                    badge = "Easier to Handle",
                    reason = "Guided back-supported machine — zero core or balance fatigue",
                    loadMultiplier = 3.20f
                )
            )

            // 18. Hip Thrust
            "hip thrust" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Padded Bench / Machine Hip Thrust",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "#1 upper- & full-glute curve builder",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Glute Bridge",
                    modality = "Single Dumbbell • Mat/Bench Area",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Hold one heavy dumbbell across hips, drive through heels, and squeeze 2s.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No hip-thrust machine or barbell pad? One dumbbell works anywhere",
                    loadMultiplier = 0.50f
                ),
                SwapMoveSeed(
                    name = "High-Foot 45° Sled Leg Press",
                    modality = "Glute-Biased Leg Sled",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Place feet higher and wider on sled platform and push through heels.",
                    badge = "Easier to Handle",
                    reason = "Quick pin/sled setup without setting up a hip-thrust bench",
                    loadMultiplier = 1.45f
                )
            )

            // 19. Dumbbell Glute Bridge
            "glute bridge" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Dumbbell Glute Bridge",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Zero-spinal-load glute contraction with a single dumbbell",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Padded Bench Dumbbell Hip Thrust",
                    modality = "Bench + Single Dumbbell",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Upper back on gym bench with dumbbell on hips for deeper glute range.",
                    badge = "Small Gym • Bench & Dumbbell",
                    reason = "Adds deeper stretch on any standard gym bench",
                    loadMultiplier = 1.15f
                ),
                SwapMoveSeed(
                    name = "Seated Hip Abductor Machine",
                    modality = "Pin-Loaded Glute Machine",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Sit tall and press knees outward smoothly with 1s outer-glute pause.",
                    badge = "Easier to Handle",
                    reason = "Seated pin-loaded machine — zero floor/bench setup",
                    loadMultiplier = 1.45f
                )
            )

            // 20. Romanian Deadlift (RDL)
            "romanian" in specLower || "rdl" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Dumbbell Romanian Deadlift",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Deep hamstring & glute tie-in stretch with dumbbells",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Padded Bench Dumbbell Hip Thrust",
                    modality = "Bench + Single Dumbbell",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Upper back on bench, drive heels down, and squeeze glutes & hamstrings.",
                    badge = "Small Gym • Less Back Hinge",
                    reason = "Fits any small gym while taking hinge pressure off your lower back",
                    loadMultiplier = 1.30f
                ),
                SwapMoveSeed(
                    name = "Seated Hamstring Leg Curl",
                    modality = "Pin-Loaded Machine",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Sit supported against pad and curl heels smoothly under seat.",
                    badge = "Easier to Handle",
                    reason = "Guided seated machine — zero hip-hinge technique required",
                    loadMultiplier = 1.45f
                )
            )

            // 21. Seated Hamstring Leg Curl
            "leg curl" in specLower || "hamstring" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Seated Hamstring Leg Curl",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Isolated hamstring curl with full back support",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Romanian Deadlift",
                    modality = "Dumbbells • Hip Hinge",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Push hips straight back with flat spine until hamstrings stretch.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No leg-curl machine in your gym? Dumbbells train hamstrings directly",
                    loadMultiplier = 0.65f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Glute Bridge",
                    modality = "Dumbbell Hip Extension",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Dig heels firmly down and bridge hips to fire hamstrings & glutes.",
                    badge = "Easier to Handle",
                    reason = "Gentle on sensitive knees while working posterior thighs & glutes",
                    loadMultiplier = 0.75f
                )
            )

            // 22. Seated Leg Extension
            "leg extension" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Seated Leg Extension",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Direct quad sculpt on pin-loaded machine",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Goblet Squat",
                    modality = "Single Dumbbell • Chest-Held",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Hold one dumbbell at chest and squat smoothly with upright knees.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Fits small gyms without a leg-extension machine",
                    loadMultiplier = 0.60f
                ),
                SwapMoveSeed(
                    name = "45° Sled Leg Press",
                    modality = "Guided Sled Machine",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Closed-chain press through full foot — gentler on kneecap tendons.",
                    badge = "Easier to Handle",
                    reason = "Gentler on knees than open-chain leg extensions",
                    loadMultiplier = 2.20f
                )
            )

            // 23. Split Squat / Lunges
            "split squat" in specLower || "bulgarian" in specLower || "lunge" in specLower -> listOf(
                SwapMoveSeed(
                    name = if (isNewBeg) "Rack-Supported Split Squat" else spec.name,
                    modality = if (isNewBeg) "Squat Rack Balance • Bodyweight" else spec.modalityTag,
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = if (isNewBeg) "Hold squat rack lightly for balance and master smooth depth before adding weight." else baseTip,
                    badge = "Standard Gym",
                    reason = "Unilateral leg & glute symmetry builder",
                    isBodyweight = isNewBeg,
                    loadMultiplier = if (isNewBeg) 0f else 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Goblet Squat",
                    modality = "Single Dumbbell • Two-Leg Stance",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Two-foot stance holding one dumbbell at chest — zero single-leg wobble.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Two-foot stability using one dumbbell in any small gym",
                    isBodyweight = false,
                    loadMultiplier = 1.35f
                ),
                SwapMoveSeed(
                    name = "45° Sled Leg Press",
                    modality = "Guided Sled Machine",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Sit supported in leg press sled and drive evenly through heels.",
                    badge = "Easier to Handle",
                    reason = "Seated guided machine — zero single-leg balance fatigue",
                    isBodyweight = false,
                    loadMultiplier = 3.50f
                )
            )

            // 24. Seated Hip Abductor Machine
            "abductor" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Seated Hip Abductor Machine",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Targets upper & outer glute shelf for hourglass hip curve",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Glute Bridge",
                    modality = "Dumbbell • Wide-Stance Bridge",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Set feet slightly wider with toes turned 15° out and squeeze upper glutes.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No hip-abductor machine? Wide-foot dumbbell bridge hits upper glutes",
                    loadMultiplier = 0.65f
                ),
                SwapMoveSeed(
                    name = "Cable Glute Kickbacks",
                    modality = "Low Pulley Ankle Cuff",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Kick heel back and 30° outward on low pulley to sculpt upper glute.",
                    badge = "Easier to Handle",
                    reason = "Smooth ankle-cuff pulley alternative when abductor seat is taken",
                    loadMultiplier = 0.35f
                )
            )

            // 25. Cable Glute Kickbacks
            "kickback" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Cable Glute Kickbacks",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Direct upper-glute shelf isolation on cable tower",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Padded Bench Dumbbell Hip Thrust",
                    modality = "Bench + Single Dumbbell",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Upper back on bench with dumbbell across hips — hold top squeeze 2s.",
                    badge = "Small Gym • Bench & Dumbbell",
                    reason = "No ankle cuff or low cable? Bench + dumbbell builds the same glute shelf",
                    loadMultiplier = 2.0f
                ),
                SwapMoveSeed(
                    name = "Seated Hip Abductor Machine",
                    modality = "Pin-Loaded Glute Machine",
                    anim = ExerciseAnimationType.FORWARD_LUNGES,
                    tip = "Sit supported and press knees wide for upper-glute fullness.",
                    badge = "Easier to Handle",
                    reason = "Quick seated machine — no ankle-strap setup needed",
                    loadMultiplier = 2.50f
                )
            )

            // 26. Calf Raise
            "calf" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Standing Machine Calf Raise",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Full ankle stretch & peak tiptoe contraction",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Dumbbell Goblet Squat",
                    modality = "Single Dumbbell • Lower Leg & Ankle",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Full-depth goblet squat strengthens ankles, calves, and lower legs in small gyms.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Fits small gyms without a dedicated standing calf machine",
                    loadMultiplier = 0.45f
                ),
                SwapMoveSeed(
                    name = "45° Sled Leg Press Toe Drive",
                    modality = "Leg Sled Platform",
                    anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                    tip = "Place balls of feet on bottom of leg-press sled and press smoothly.",
                    badge = "Easier to Handle",
                    reason = "Zero shoulder pad load on spine — uses standard leg-press sled",
                    loadMultiplier = 1.25f
                )
            )

            // 27. Captain's Chair Knee Raise
            "captain" in specLower || "knee raise" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Captain's Chair Knee Raise",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Forearm- & back-supported lower-ab curl",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Bench Lying Leg Raise",
                    modality = "Flat Gym Bench",
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = "Lie on flat gym bench holding pad behind head and curl legs smoothly.",
                    badge = "Small Gym • Flat Bench Only",
                    reason = "No Captain's Chair tower? Works on any flat gym bench",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Kneeling Cable Rope Crunch",
                    modality = "Weighted Cable Pulley",
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = "Hold rope by temples and curl ribcage down toward pelvis.",
                    badge = "Easier to Handle",
                    reason = "Adjustable light pin weight instead of lifting leg bodyweight",
                    isBodyweight = false,
                    loadMultiplier = 1.0f
                )
            )

            // 28. Kneeling Cable Rope Crunch
            "cable" in specLower && "crunch" in specLower -> listOf(
                SwapMoveSeed(
                    name = "Kneeling Cable Rope Crunch",
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Progressive weighted abs for visible waist definition",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Bench Reverse Crunch",
                    modality = "Flat Gym Bench",
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = "Hold top of flat bench behind head and curl knees & pelvis smoothly.",
                    badge = "Small Gym • Flat Bench Only",
                    reason = "Fits small gyms without an open high-pulley rope station",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Captain's Chair Knee Raise",
                    modality = "Back-Supported Core Tower",
                    anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                    tip = "Rest forearms on pads, keep back against pad, and lift knees smoothly.",
                    badge = "Easier to Handle",
                    reason = "Upright forearm pads — no kneeling on gym floor required",
                    isBodyweight = true,
                    loadMultiplier = 0f
                )
            )

            // 29. Standing Cable Pallof Hold / Finisher
            "pallof" in specLower -> listOf(
                SwapMoveSeed(
                    name = spec.name,
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.PLANK_HOLD,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Standing cable waist cincher — zero neck or lower-back crunch strain",
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Bench Forearm Plank Brace",
                    modality = "Elevated Gym Bench",
                    anim = ExerciseAnimationType.PLANK_HOLD,
                    tip = "Forearms braced on gym bench, draw navel inward, and squeeze glutes.",
                    badge = "Small Gym • Bench Only",
                    reason = "Works in any small gym when cable pulleys are busy or missing",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Plank Shoulder Tap Stability",
                    modality = "Bench-Elevated Anti-Rotation",
                    anim = ExerciseAnimationType.PLANK_HOLD,
                    tip = "Hands on gym bench edge and tap opposite shoulders without hip sway.",
                    badge = "Easier to Handle",
                    reason = "Elevated incline angle reduces wrist and lower-back load",
                    isBodyweight = true,
                    loadMultiplier = 0f
                )
            )

            // 30. Active Bar Hang & Grip Finisher
            "bar hang" in specLower || "dead hang" in specLower || "farmer" in specLower -> listOf(
                SwapMoveSeed(
                    name = spec.name,
                    modality = spec.modalityTag,
                    anim = ExerciseAnimationType.LAT_PULLDOWN,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Grip strength & spinal decompression on pull-up bar",
                    isBodyweight = true,
                    loadMultiplier = 0f
                ),
                SwapMoveSeed(
                    name = "Heavy Dumbbell Farmer Hold",
                    modality = "Dumbbells • Standing Grip Hold",
                    anim = ExerciseAnimationType.INCLINE_DB_CURL,
                    tip = "Stand tall holding dumbbells firmly at sides with proud shoulders.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "No pull-up bar available? Builds iron grip standing on two feet",
                    isBodyweight = false,
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "Wide-Grip Lat Pulldown",
                    modality = "Seated Pulldown Station",
                    anim = ExerciseAnimationType.LAT_PULLDOWN,
                    tip = "Control light-to-moderate cable weight to stretch lats without hanging bodyweight.",
                    badge = "Easier to Handle",
                    reason = "Seated cable control for beginners who cannot hang full bodyweight yet",
                    isBodyweight = false,
                    loadMultiplier = 1.50f
                )
            )

            else -> listOf(
                SwapMoveSeed(
                    name = spec.name,
                    modality = spec.modalityTag,
                    anim = rawGymAnim,
                    tip = baseTip,
                    badge = "Standard Gym",
                    reason = "Prescribed gym station",
                    isBodyweight = spec.defaultLoadKg <= 0f,
                    loadMultiplier = 1.0f
                ),
                SwapMoveSeed(
                    name = "30° Incline Dumbbell Press",
                    modality = "Dumbbells & Bench",
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = "Smooth controlled dumbbell reps on 30° bench.",
                    badge = "Small Gym • Dumbbells Only",
                    reason = "Universal dumbbell & bench station",
                    loadMultiplier = 0.70f
                ),
                SwapMoveSeed(
                    name = "Seated Machine Chest Press",
                    modality = "Guided Machine",
                    anim = ExerciseAnimationType.INCLINE_DB_PRESS,
                    tip = "Keep back anchored to pad and press smoothly.",
                    badge = "Easier to Handle",
                    reason = "Guided machine track",
                    loadMultiplier = 1.0f
                )
            )
        }

        return gymSeeds.mapIndexed { idx, seed -> buildVariantFromSeed(seed, isPrimarySpec = idx == 0) }
    }

    // =========================================================================
    // HOME BODYWEIGHT MODE: 3 PURE EQUIPMENT-FREE OPTIONS ([0] Standard Home,
    // [1] Easier to Handle, [2] Focused Variation) — ZERO GYM MACHINES!
    // =========================================================================
    val homeSeeds: List<SwapMoveSeed> = when {
        // 1. Chest & Triceps (1-to-1 injective primary Home moves so zero session GIFs collide)
        "pec" in specLower || ("fly" in specLower && "rear" !in specLower && "reverse" !in specLower) -> listOf(
            SwapMoveSeed(
                name = "Wide Kneeling Push-Ups",
                modality = "At-Home Bodyweight",
                anim = ExerciseAnimationType.KNEE_PUSH_UPS,
                tip = "Hands slightly wider with knees cushioned on mat • Deep chest stretch & press.",
                badge = "Standard Home",
                reason = "Joint-safe wide chest stretch with zero gym fly machine",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Incline Push-Ups (Hands Elevated)",
                modality = "Sturdy Couch / Counter Edge",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Place hands on couch or counter edge and press smoothly.",
                badge = "Easier to Handle",
                reason = "Higher hand elevation takes load off wrists and shoulders",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standard Push-Ups",
                modality = "Floor Bodyweight",
                anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                tip = "Straight line from head to heels • Press floor firmly away.",
                badge = "More Challenging",
                reason = "Full-bodyweight floor push-up progression",
                isBodyweight = true
            )
        )

        "incline" in specLower && "press" in specLower -> if (isNewBeg) {
            listOf(
                SwapMoveSeed(
                    name = "Incline Push-Ups (Hands Elevated)",
                    modality = "Couch / Bed Edge",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands shoulder-width on sturdy edge • Elbows tucked at 45°.",
                    badge = "Standard Home",
                    reason = "Beginner-calibrated chest & shoulder push-up",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Upright Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Stand arm's length from wall and press smoothly — lightest joint load.",
                    badge = "Easier to Handle",
                    reason = "Lightest upright angle if couch push-ups still feel heavy",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Kneeling Push-Ups",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.KNEE_PUSH_UPS,
                    tip = "Knees cushioned on mat with straight hip-to-shoulder line.",
                    badge = "Focused Variation",
                    reason = "Floor mat alternative when no sturdy bench/couch edge is nearby",
                    isBodyweight = true
                )
            )
        } else {
            listOf(
                SwapMoveSeed(
                    name = "Decline Push-Ups",
                    modality = "Feet on Low Step / Couch",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Feet elevated on low step • Targets upper-chest shelf directly.",
                    badge = "Standard Home",
                    reason = "Shifts bodyweight onto upper chest & front shoulders",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Incline Push-Ups (Hands Elevated)",
                    modality = "Hands on Couch Edge",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands elevated on couch or counter edge for smoother reps.",
                    badge = "Easier to Handle",
                    reason = "Reduces shoulder & wrist load while keeping clean chest tension",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Standard Push-Ups",
                    modality = "Floor Bodyweight",
                    anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                    tip = "Flat floor push-up with crisp 2-second lowering.",
                    badge = "Focused Variation",
                    reason = "Zero furniture required — flat floor execution",
                    isBodyweight = true
                )
            )
        }

        ("flat" in specLower || "machine chest" in specLower) && "press" in specLower -> if (isNewBeg) {
            listOf(
                SwapMoveSeed(
                    name = "Box Incline Push-Ups",
                    modality = "Sturdy Chair / Couch Edge",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands on sturdy low box or couch • Controlled 2s lowering.",
                    badge = "Standard Home",
                    reason = "Safe elevated chest press for first-time home training",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Upright Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands flat on wall at chest height • Zero wrist strain.",
                    badge = "Easier to Handle",
                    reason = "Easiest upright chest & arm press",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Kneeling Push-Ups",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.KNEE_PUSH_UPS,
                    tip = "Rest knees comfortably on folded towel or mat.",
                    badge = "Focused Variation",
                    reason = "Mat-only variation without needing a box or chair",
                    isBodyweight = true
                )
            )
        } else {
            listOf(
                SwapMoveSeed(
                    name = "Standard Push-Ups",
                    modality = "Floor Bodyweight",
                    anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                    tip = "Straight body line from head to heels • Press palms firmly.",
                    badge = "Standard Home",
                    reason = "Full mid-chest & tricep bodyweight press",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Incline Push-Ups (Hands Elevated)",
                    modality = "Couch / Counter Edge",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Elevate hands on couch edge so every rep stays crisp.",
                    badge = "Easier to Handle",
                    reason = "Lighter leverage when fatigued or protecting wrists",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Close-Grip Tricep Push-Ups",
                    modality = "Floor Bodyweight",
                    anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                    tip = "Tuck elbows closer to ribs for extra inner-chest & tricep drive.",
                    badge = "Focused Variation",
                    reason = "Tighter elbow tuck — gentler on wide shoulder joints",
                    isBodyweight = true
                )
            )
        }

        "overhead" in specLower && "tricep" in specLower -> if (isNewBeg) {
            listOf(
                SwapMoveSeed(
                    name = "Supported Bench Tricep Dips",
                    modality = "Sturdy Chair Edge • Bent Knees",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Keep feet flat close to chair so legs assist as elbows bend gently.",
                    badge = "Standard Home",
                    reason = "Leg-assisted tricep dip on any home chair",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Floor Tricep Dips",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Sit on mat with hands behind hips and bend elbows gently.",
                    badge = "Easier to Handle",
                    reason = "Short safe range right on the floor — zero chair needed",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Upright Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Elbows tucked close to ribs while pressing away from wall.",
                    badge = "Joint-Friendly",
                    reason = "Zero backward shoulder stretch if dips bother your shoulders",
                    isBodyweight = true
                )
            )
        } else {
            listOf(
                SwapMoveSeed(
                    name = "Floor Tricep Dips",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Hands planted behind hips on mat • Bend & lock elbows cleanly.",
                    badge = "Standard Home",
                    reason = "Zero-equipment tricep isolation right on the mat",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Close-Elbow Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands narrow on wall and press through triceps.",
                    badge = "Easier to Handle",
                    reason = "Upright wrist-friendly tricep press",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Chair / Bench Tricep Dips",
                    modality = "Sturdy Chair Edge",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Keep back close to chair edge • Lower to 90° elbows & press up.",
                    badge = "More Challenging",
                    reason = "Deeper tricep range using a sturdy chair or couch edge",
                    isBodyweight = true
                )
            )
        }

        "pushdown" in specLower || "pressdown" in specLower || "close-grip push" in specLower -> listOf(
            SwapMoveSeed(
                name = "Close-Grip Tricep Push-Ups",
                modality = if (isNewBeg) "Elevated / Knees Optional" else "Floor Bodyweight",
                anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                tip = "Elbows tucked tight to ribs • Drive palms straight up.",
                badge = "Standard Home",
                reason = "Direct tricep lockout without cables",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Tricep Dips",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.TRICEP_DIPS,
                tip = "Hands behind hips on mat • Bend & straighten elbows smoothly.",
                badge = "Easier to Handle",
                reason = "Lighter bodyweight leverage focused on back of arms",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Kneeling Push-Ups",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.KNEE_PUSH_UPS,
                tip = "Knees on mat with elbows at 45° for smooth chest & arm reps.",
                badge = "Focused Variation",
                reason = "Balanced chest & tricep mat press",
                isBodyweight = true
            )
        )

        "dip" in specLower -> if (isNewBeg) {
            listOf(
                SwapMoveSeed(
                    name = "Floor Tricep Dips",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Feet & hips near mat with knees bent • Gentle elbow bend.",
                    badge = "Standard Home",
                    reason = "Safe beginner tricep dip without hanging off high bars",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Upright Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Stand facing wall with tucked elbows and press smoothly.",
                    badge = "Easier to Handle",
                    reason = "Zero wrist extension or shoulder dip pressure",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Close-Grip Tricep Push-Ups",
                    modality = "Elevated Couch Edge",
                    anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                    tip = "Hands shoulder-width on couch edge with elbows tucked.",
                    badge = "Focused Variation",
                    reason = "Forward pressing angle for triceps & lower chest",
                    isBodyweight = true
                )
            )
        } else {
            listOf(
                SwapMoveSeed(
                    name = "Chair / Bench Tricep Dips",
                    modality = "Sturdy Chair Edge",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Keep back close to chair edge • Lower to 90° elbows & press up.",
                    badge = "Standard Home",
                    reason = "Full lower-chest & tricep bodyweight dip",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Floor Tricep Dips",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.TRICEP_DIPS,
                    tip = "Perform directly on floor mat for shorter, shoulder-gentle range.",
                    badge = "Easier to Handle",
                    reason = "Gentler on front shoulders & requires zero furniture",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Close-Grip Tricep Push-Ups",
                    modality = "Floor Bodyweight",
                    anim = ExerciseAnimationType.STANDARD_PUSH_UPS,
                    tip = "Tuck elbows against ribs and lock triceps cleanly at the top.",
                    badge = "Focused Variation",
                    reason = "Floor push-up alternative when no sturdy chair is handy",
                    isBodyweight = true
                )
            )
        }

        // 2. Back, Rear Delts, Biceps & Grip (Home)
        "bar hang" in specLower || "dead hang" in specLower || "farmer" in specLower -> listOf(
            SwapMoveSeed(
                name = "Feet-Supported Bar / Doorframe Hang",
                modality = "Doorframe / Pull-Up Bar",
                anim = ExerciseAnimationType.LAT_PULLDOWN,
                tip = "Grip sturdy bar or doorframe with toes lightly assisting on floor.",
                badge = "Standard Home",
                reason = "Decompresses spine & builds grip safely at home",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Doorframe Wide Lat Rows",
                modality = "Sturdy Doorframe",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Hold doorframe at chest height, lean back slightly, and pull chest in.",
                badge = "Easier to Handle",
                reason = "Works with any standard doorway — zero overhead bar needed",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Backpack / Towel Curls",
                modality = "Loaded Backpack",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Hold loaded backpack straps firmly for forearm & bicep endurance.",
                badge = "Focused Variation",
                reason = "Apartment-friendly grip & arm option",
                isBodyweight = true
            )
        )

        "pulldown" in specLower -> listOf(
            SwapMoveSeed(
                name = "Doorframe Wide Lat Rows",
                modality = "Sturdy Doorframe",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Grip sturdy doorframe at chest height, lean back & pull with lats.",
                badge = "Standard Home",
                reason = "Trains lats at home without a cable tower",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bent-Knee Mid-Back Rows",
                modality = "Loaded Backpack • Hinged",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Hinge hips with knees bent and row a book-filled backpack to ribs.",
                badge = "No Doorframe Needed",
                reason = "Uses any household backpack if you prefer not holding a doorframe",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Rear-Delt Posture Raises",
                modality = "Bodyweight Posture",
                anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                tip = "Hinge forward 45° and sweep arms wide, squeezing shoulder blades 2s.",
                badge = "Easier to Handle",
                reason = "Zero equipment or doorframe needed — pure upper-back activation",
                isBodyweight = true
            )
        )

        "chest-supported" in specLower || "one-arm" in specLower || ("wide" in specLower && "row" in specLower) -> listOf(
            SwapMoveSeed(
                name = "Bent-Knee Mid-Back Rows",
                modality = "Loaded Backpack / Towel Tension",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Knees bent, torso proud • Pull elbows past ribs & pinch blades.",
                badge = "Standard Home",
                reason = "Builds mid-back thickness at home",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Doorframe Wide Lat Rows",
                modality = "Sturdy Doorframe",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Stand upright leaning back slightly from doorframe — zero lower-back hinge.",
                badge = "Easier to Handle",
                reason = "Upright torso takes all hinge effort off your lower back",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Elevated Table / Bar Rows",
                modality = "Sturdy Table / Low Bar",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Body straight under sturdy bar or heavy table edge • Squeeze mid-back.",
                badge = "More Challenging",
                reason = "Stronger bodyweight pull angle",
                isBodyweight = true
            )
        )

        "seated" in specLower && "row" in specLower -> listOf(
            SwapMoveSeed(
                name = "Elevated Table / Bar Rows",
                modality = "Sturdy Bar / Doorframe",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Body aligned at 45° • Pull chest smoothly toward hands & squeeze back.",
                badge = "Standard Home",
                reason = "Horizontal back pull without a cable row machine",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bent-Knee Mid-Back Rows",
                modality = "Loaded Backpack",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Row a book-loaded backpack smoothly into your lower ribs.",
                badge = "Easier to Handle",
                reason = "Adjustable backpack weight — no table or bar required",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Doorframe Wide Lat Rows",
                modality = "Sturdy Doorframe",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Walk feet closer to doorframe for lighter, controlled reps.",
                badge = "Focused Variation",
                reason = "Instant leverage adjustment by stepping closer or farther",
                isBodyweight = true
            )
        )

        "face pull" in specLower || "rear-delt" in specLower || "rear delt" in specLower -> listOf(
            SwapMoveSeed(
                name = "Rear-Delt Posture Raises",
                modality = "Bodyweight / Water Bottles",
                anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                tip = "Hinge torso forward & sweep arms wide to squeeze rear shoulders.",
                badge = "Standard Home",
                reason = "Opens rounded shoulders & wakes up upper posture",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Doorframe Wide Lat Rows",
                modality = "High-Elbow Doorframe Pull",
                anim = ExerciseAnimationType.DOORFRAME_ROW,
                tip = "Pull chest forward with high elbows to squeeze rear delts.",
                badge = "Easier to Handle",
                reason = "Upright posture — no forward hip hinge needed",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Plank Shoulder Taps",
                modality = "Mat Core & Shoulder Stability",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Hold steady plank & tap opposite shoulders cleanly.",
                badge = "Focused Variation",
                reason = "Builds 360° shoulder-girdle & core stability",
                isBodyweight = true
            )
        )

        "hammer" in specLower && "curl" in specLower -> listOf(
            SwapMoveSeed(
                name = "Neutral Hammer Backpack Curls",
                modality = "Loaded Backpack Straps",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Grip loaded backpack side straps palms-in and curl with pinned elbows.",
                badge = "Standard Home",
                reason = "Hits biceps & forearms using any backpack + books/water bottles",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Backpack / Towel Curls",
                modality = "Top Handle Curl",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Hold top backpack loop with both hands for smooth two-arm curls.",
                badge = "Easier to Handle",
                reason = "Two-hand grip makes lighter loads easy to control",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Reverse-Grip Forearm Curls",
                modality = "Overhand Backpack Curl",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Overhand grip on backpack handle • Curl smoothly for forearms & outer bicep.",
                badge = "Focused Variation",
                reason = "Extra forearm & wrist-extensor balance",
                isBodyweight = true
            )
        )

        "preacher" in specLower || "ez-bar" in specLower -> listOf(
            SwapMoveSeed(
                name = "Reverse-Grip Forearm Curls",
                modality = "Overhand Loaded Backpack",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Overhand grip on loaded backpack • Curl smoothly with pinned elbows.",
                badge = "Standard Home",
                reason = "Targets brachialis, forearms, and full arm shape at home",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Backpack / Towel Curls",
                modality = "Underhand Backpack Curl",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Underhand grip on backpack straps • Squeeze biceps 1s at top.",
                badge = "Easier to Handle",
                reason = "Natural palms-up curl grip",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Neutral Hammer Backpack Curls",
                modality = "Neutral Strap Grip",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Palms facing inward for zero wrist torque.",
                badge = "Focused Variation",
                reason = "Most wrist-comfortable curl grip",
                isBodyweight = true
            )
        )

        "curl" in specLower && "leg" !in specLower && "hamstring" !in specLower -> listOf(
            SwapMoveSeed(
                name = "Standing Backpack / Towel Curls",
                modality = "Loaded Backpack",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Elbows pinned at sides • Curl loaded backpack up & lower slowly.",
                badge = "Standard Home",
                reason = "Direct home bicep builder using books/water in a backpack",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Neutral Hammer Backpack Curls",
                modality = "Neutral Strap Grip",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Hold side straps palms-in for comfortable wrist alignment.",
                badge = "Easier to Handle",
                reason = "Easier on wrists while hitting biceps & forearms",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Reverse-Grip Forearm Curls",
                modality = "Overhand Backpack Grip",
                anim = ExerciseAnimationType.INCLINE_DB_CURL,
                tip = "Overhand grip builds outer arm & grip strength.",
                badge = "Focused Variation",
                reason = "Forearm & brachialis focus",
                isBodyweight = true
            )
        )

        // 3. Shoulders (Home)
        "shoulder press" in specLower -> if (isNewBeg) {
            listOf(
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "Upright Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands shoulder-height on wall • Safe upright shoulder & arm press.",
                    badge = "Standard Home",
                    reason = "Beginner-safe shoulder builder with zero upside-down neck strain",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Incline Push-Ups (Hands Elevated)",
                    modality = "Couch / Counter Edge",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Hands on counter or couch edge and press tall.",
                    badge = "Focused Variation",
                    reason = "Adds front-shoulder & upper-chest engagement",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Plank Shoulder Taps",
                    modality = "Elevated or Mat Stability",
                    anim = ExerciseAnimationType.PLANK_HOLD,
                    tip = "Hold steady plank and tap opposite shoulders calmly.",
                    badge = "Isometric Option",
                    reason = "Zero elbow bending if joints feel tired",
                    isBodyweight = true
                )
            )
        } else {
            listOf(
                SwapMoveSeed(
                    name = "Pike Shoulder Push-Ups",
                    modality = "Mat Bodyweight",
                    anim = ExerciseAnimationType.PIKE_SHOULDER_PRESS,
                    tip = "Hips high in an inverted V • Lower head gently toward mat & press tall.",
                    badge = "Standard Home",
                    reason = "True vertical shoulder press using bodyweight",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Wall Push-Ups",
                    modality = "High-Hand Wall Press",
                    anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                    tip = "Place hands slightly higher on wall for gentle upright shoulder work.",
                    badge = "Easier to Handle",
                    reason = "Keeps you standing upright if inverted pike feels uncomfortable",
                    isBodyweight = true
                ),
                SwapMoveSeed(
                    name = "Plank Shoulder Taps",
                    modality = "Mat Core & Delts",
                    anim = ExerciseAnimationType.PLANK_HOLD,
                    tip = "Brace abs & glutes while tapping opposite shoulders smoothly.",
                    badge = "Focused Variation",
                    reason = "Builds shoulder cap endurance & core anti-rotation",
                    isBodyweight = true
                )
            )
        }

        "lateral raise" in specLower -> listOf(
            SwapMoveSeed(
                name = "Plank Shoulder Taps",
                modality = "Mat Shoulder & Core",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Hold steady high plank & tap opposite shoulders without hip sway.",
                badge = "Standard Home",
                reason = "Fires side & front shoulder stabilizers without dumbbells",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Wall Push-Ups",
                modality = "Upright Wall Press",
                anim = ExerciseAnimationType.INCLINE_PUSH_UPS,
                tip = "Stand facing wall and press smoothly — zero floor wrist pressure.",
                badge = "Easier to Handle",
                reason = "Standing upright alternative gentle on wrists",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Rear-Delt Posture Raises",
                modality = "Water Bottles / Bodyweight",
                anim = ExerciseAnimationType.CABLE_LATERAL_RAISE,
                tip = "Sweep arms out to shoulder height holding two water bottles if handy.",
                badge = "Focused Variation",
                reason = "Standing side- & rear-shoulder sculpting motion",
                isBodyweight = true
            )
        )

        // 4. Legs, Glutes & Calves (1-to-1 injective primary Home moves)
        "hip thrust" in specLower -> listOf(
            SwapMoveSeed(
                name = "Elevated Glute Hip Thrusts",
                modality = "Couch / Bed Edge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Upper back on couch edge • Drive through heels & squeeze glutes 2s.",
                badge = "Standard Home",
                reason = "Full-range booty builder using your couch or bed edge",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Lie flat on mat, dig heels down, and bridge hips high with 2s squeeze.",
                badge = "Easier to Handle",
                reason = "Zero couch setup needed — gentle on neck and upper back",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Glute Kickbacks",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Stand tall touching wall and kick leg back, squeezing upper glute 1s.",
                badge = "Focused Variation",
                reason = "Standing upper-booty shelf isolation",
                isBodyweight = true
            )
        )

        "glute bridge" in specLower || "leg curl" in specLower || "hamstring" in specLower -> listOf(
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Lie on mat, dig heels firmly down & bridge hips high for glutes & hamstrings.",
                badge = "Standard Home",
                reason = "Activates glutes & hamstrings with zero knee or back strain",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Glute Kickbacks",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Stand tall and extend leg smoothly behind you without arching lower back.",
                badge = "Easier to Handle",
                reason = "No lying on the floor required",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Elevated Glute Hip Thrusts",
                modality = "Couch / Bed Edge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Rest upper back on couch edge for deeper glute & hamstring stretch.",
                badge = "More Challenging",
                reason = "Deeper hip range for stronger curve stimulus",
                isBodyweight = true
            )
        )

        "abductor" in specLower -> listOf(
            SwapMoveSeed(
                name = "Standing Hip Abductions",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Stand tall holding wall support & sweep leg outward for outer glutes.",
                badge = "Standard Home",
                reason = "Sculpts upper & outer hip curves without a gym machine",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Wide-Foot Mat Bridge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Set feet slightly wider on mat and squeeze upper glutes 2s at top.",
                badge = "Easier to Handle",
                reason = "Two-foot floor stability — zero single-leg balance needed",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Glute Kickbacks",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Kick heel back and slightly outward to lift the upper glute shelf.",
                badge = "Focused Variation",
                reason = "Upper-booty lift angle",
                isBodyweight = true
            )
        )

        "kickback" in specLower -> listOf(
            SwapMoveSeed(
                name = "Standing Glute Kickbacks",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Brace core & extend leg backward squeezing glute 1s without arching back.",
                badge = "Standard Home",
                reason = "Direct upper-booty lift with zero equipment",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Lie comfortably on mat and bridge hips high with 2s squeeze.",
                badge = "Easier to Handle",
                reason = "Resting spine on mat removes all standing balance effort",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Standing Hip Abductions",
                modality = "Wall-Supported Standing",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Sweep leg outward 35° to target outer hip & side glute.",
                badge = "Focused Variation",
                reason = "Side-glute hourglass curve focus",
                isBodyweight = true
            )
        )

        "romanian" in specLower || "rdl" in specLower -> listOf(
            SwapMoveSeed(
                name = "Forward Lunges",
                modality = "Bodyweight Hip-Biased Stride",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Step forward with slight torso lean over front thigh & drive through heel.",
                badge = "Standard Home",
                reason = "Loads glutes & hamstrings deeply without heavy gym dumbbells",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Walk heels 4 inches farther forward on mat to emphasize hamstrings.",
                badge = "Easier to Handle",
                reason = "Zero knee lunge pressure — pure mat hamstring & glute bridge",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bodyweight Squats",
                modality = "Two-Leg Stance",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Feet shoulder-width apart • Sit hips back and drive tall through heels.",
                badge = "Focused Variation",
                reason = "Stable two-foot squat if single-leg lunges feel wobbly",
                isBodyweight = true
            )
        )

        "leg extension" in specLower -> listOf(
            SwapMoveSeed(
                name = if (isNewBeg) "Supported Step Lunges" else "Walking Lunges",
                modality = if (isNewBeg) "Wall / Chair Balanced" else "Controlled Strides",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = if (isNewBeg) "Touch wall lightly for balance and bend knees smoothly." else "Step in controlled upright strides to sculpt quads & thighs.",
                badge = "Standard Home",
                reason = "Builds quad definition at home without a leg-extension machine",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bodyweight Squats",
                modality = "Chair-Tap Optional",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Tap hips lightly to a chair behind you and stand tall.",
                badge = "Easier to Handle",
                reason = "Two-foot balance — gentler on knees than lunges",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Split Squats",
                modality = "Stationary Staggered Stance",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Keep feet planted in place and lower straight down & up.",
                badge = "Focused Variation",
                reason = "Fits tiny bedroom spaces without walking across the room",
                isBodyweight = true
            )
        )

        "split squat" in specLower || "bulgarian" in specLower || "lunge" in specLower -> listOf(
            SwapMoveSeed(
                name = "Split Squats",
                modality = if (isNewBeg) "Chair-Supported Stance" else "Stationary Bodyweight",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Keep front shin upright, drop back knee smoothly & drive through front heel.",
                badge = "Standard Home",
                reason = "Sculpts thighs & glutes evenly in place",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bodyweight Squats",
                modality = "Two-Foot Stance",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Both feet planted shoulder-width for effortless balance.",
                badge = "Easier to Handle",
                reason = "Easier two-leg balance for tired legs or sensitive knees",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Forward Lunges",
                modality = "Dynamic Step-Out",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Step forward cleanly and push back to tall stance.",
                badge = "Focused Variation",
                reason = "Alternating left/right rhythm",
                isBodyweight = true
            )
        )

        "calf" in specLower -> listOf(
            SwapMoveSeed(
                name = if (isNewBeg) "Two-Leg Standing Calf Raises" else "Single-Leg Calf Raises",
                modality = "Wall / Step Edge",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Pause 1s in bottom heel stretch and rise high onto tiptoes.",
                badge = "Standard Home",
                reason = "Direct calf & ankle definition anywhere at home",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Bodyweight Squats",
                modality = "Ankle & Leg Mobility",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Smooth full-foot squats if calves cramp easily.",
                badge = "Easier to Handle",
                reason = "Full-foot support without tiptoe cramping",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Forward Lunges",
                modality = "Dynamic Lower Leg",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Controlled step & push-off works ankles, calves, and thighs together.",
                badge = "Focused Variation",
                reason = "Higher calorie burn for lower body",
                isBodyweight = true
            )
        )

        "leg press" in specLower || "squat" in specLower -> listOf(
            SwapMoveSeed(
                name = "Bodyweight Squats",
                modality = "At-Home Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Sit hips to parallel at a smooth rhythm and drive tall through heels.",
                badge = "Standard Home",
                reason = "Foundational home quad & glute builder",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Glute Bridges",
                modality = "Mat Bodyweight",
                anim = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                tip = "Lie on mat and bridge hips upward — zero knee compression.",
                badge = "Easier to Handle",
                reason = "Best swap if deep squats bother your knees",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Forward Lunges",
                modality = "Alternating Stride",
                anim = ExerciseAnimationType.FORWARD_LUNGES,
                tip = "Alternating forward steps for extra single-leg toning.",
                badge = "More Challenging",
                reason = "Increases single-leg intensity without weights",
                isBodyweight = true
            )
        )

        // 5. Core & Abs (Home)
        "captain" in specLower || "knee raise" in specLower -> listOf(
            SwapMoveSeed(
                name = "Reverse Crunches",
                modality = "Mat Lower Abs",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Lie on mat & curl knees and pelvis smoothly toward your ribs.",
                badge = "Standard Home",
                reason = "Hits lower tummy directly without a gym knee-raise tower",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Abdominal Crunches",
                modality = "Mat Upper Abs",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Feet planted on mat and curl upper ribs smoothly — zero hip lift.",
                badge = "Easier to Handle",
                reason = "Feet stay planted on the mat — gentler on lower back",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = if (isNewBeg) "Deadbug Heel Taps" else "Lying Leg Raises",
                modality = "Mat Core Control",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Press lower back flat into mat and move legs under slow control.",
                badge = "Focused Variation",
                reason = "Deep lower-ab anti-arch control",
                isBodyweight = true
            )
        )

        "core finisher" in specLower || "vacuum" in specLower -> listOf(
            SwapMoveSeed(
                name = "Core Finisher Plank",
                modality = "Mat Deep Waist",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Draw belly button inward & lock elbows beneath shoulders.",
                badge = "Standard Home",
                reason = "Tightens deep transverse waist corset",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Forearm Plank Hold",
                modality = "Knees-Down Optional",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Drop knees lightly to mat anytime to keep lower back flat and safe.",
                badge = "Easier to Handle",
                reason = "Gentler leverage when core is fatigued at end of workout",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Reverse Crunches",
                modality = "Mat Lower Abs",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Lie on back and curl knees smoothly — zero shoulder plank fatigue.",
                badge = "Focused Variation",
                reason = "Takes weight off shoulders & wrists",
                isBodyweight = true
            )
        )

        "pallof" in specLower || "plank" in specLower -> listOf(
            SwapMoveSeed(
                name = "Forearm Plank Hold",
                modality = "Mat Core Brace",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Draw belly button toward spine and squeeze glutes tight.",
                badge = "Standard Home",
                reason = "360° flat-waist brace with zero spinal crunching",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Floor Abdominal Crunches",
                modality = "Mat Upper Abs",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Lie on mat with knees bent — zero forearm or shoulder hold.",
                badge = "Easier to Handle",
                reason = "Rests shoulders if planks tire your arms first",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Plank Shoulder Taps",
                modality = "Anti-Rotation Core",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Tap opposite shoulders slowly while keeping hips rock-still.",
                badge = "Focused Variation",
                reason = "Dynamic anti-rotation oblique challenge",
                isBodyweight = true
            )
        )

        else -> listOf(
            SwapMoveSeed(
                name = "Floor Abdominal Crunches",
                modality = "Mat Core",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Exhale and curl ribcage smoothly toward pelvis.",
                badge = "Standard Home",
                reason = "Direct upper- & mid-ab contraction on mat",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Forearm Plank Hold",
                modality = "Isometric Waist Brace",
                anim = ExerciseAnimationType.PLANK_HOLD,
                tip = "Hold straight line from shoulders to heels — zero neck pull.",
                badge = "Easier on Neck",
                reason = "Zero neck flexion if crunches tire your neck",
                isBodyweight = true
            ),
            SwapMoveSeed(
                name = "Reverse Crunches",
                modality = "Lower-Ab Curl",
                anim = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                tip = "Curl knees smoothly toward chest while head rests relaxed on mat.",
                badge = "Focused Variation",
                reason = "Shifts focus onto lower tummy",
                isBodyweight = true
            )
        )
    }

    return homeSeeds.mapIndexed { idx, seed -> buildVariantFromSeed(seed, isPrimarySpec = false) }
}

