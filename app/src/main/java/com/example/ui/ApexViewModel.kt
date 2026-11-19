package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ApexRepository
import com.example.data.local.ApexDatabase
import com.example.data.local.CalibrationPreferencesRepository
import com.example.data.local.CalibrationProfileState
import com.example.data.local.CombatSetLogEntity
import com.example.data.local.DailyComplianceEntity
import com.example.data.local.WeeklyAuditEntity
import com.example.domain.AgeTier
import com.example.domain.BiologicalSex
import com.example.domain.CalibrationCalculator
import com.example.domain.CalibrationGapReport
import com.example.domain.CombatDaySpec
import com.example.domain.CombatExerciseSpec
import com.example.domain.CombatSplitCatalog
import com.example.domain.CurrentBfBracket
import com.example.domain.DayScheduleSlot
import com.example.domain.ExperienceLevel
import com.example.domain.ExerciseAnimationType
import com.example.domain.FourWeekTrackStatus
import com.example.domain.GoalBfBracket
import com.example.domain.PlanStartOption
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.QuickStrikeWorkout
import com.example.domain.SessionStageType
import com.example.domain.WeeklyFrequency
import com.example.domain.WeeklySchedulePlanner
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutHistoryRecord
import com.example.domain.WorkoutIntensityMode
import com.example.domain.StagnationAuditResult
import java.time.LocalDate
import com.example.system.ApexNotificationScheduler
import com.example.system.OperationalCheckpoint
import com.example.system.TacticalAudioHaptics
import com.example.system.WorkoutMusicEngine
import com.example.ui.i18n.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object ApexRoutes {
    const val ROUTE_HOME_DASHBOARD = "home_dashboard"
    const val ROUTE_ACTIVE_WORKOUT_COACH = "active_workout_coach"
    const val ROUTE_BODY_CALIBRATION = "body_calibration"
}

enum class ApexNavDestination(
    val index: Int,
    val route: String,
    val code: String,
    val label: String
) {
    HOME_DASHBOARD(0, ApexRoutes.ROUTE_HOME_DASHBOARD, "[01]", "HOME DASHBOARD"),
    COMBAT_DECK(1, ApexRoutes.ROUTE_ACTIVE_WORKOUT_COACH, "[02]", "ACTIVE WORKOUT"),
    CALIBRATION(2, ApexRoutes.ROUTE_BODY_CALIBRATION, "[03]", "CALIBRATION")
}

data class RestTimerState(
    val isActive: Boolean = false,
    val remainingMillis: Long = 90_000L,
    val totalMillis: Long = 90_000L,
    val exerciseName: String = "",
    val setNumber: Int = 1,
    val completedAlertActive: Boolean = false
) {
    val remainingSeconds: Int
        get() = ((remainingMillis + 999L) / 1000L).toInt().coerceAtLeast(0)

    val progressFraction: Float
        get() = if (totalMillis > 0L) {
            (remainingMillis.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

    val formattedClock: String
        get() {
            val totalSec = remainingMillis / 1000L
            val mins = totalSec / 60L
            val secs = totalSec % 60L
            val tenths = (remainingMillis % 1000L) / 100L
            return String.format("%02d:%02d.%d", mins, secs, tenths)
        }
}

enum class WorkoutOriginPage {
    DASHBOARD,
    WORKOUT_HUB
}

enum class WorkoutLaunchMode {
    HUB,
    OVERVIEW,
    RESUME_ACTIVE,
    RESTART_FRESH
}

data class MidwayWorkoutSnapshot(
    val sessionKey: String,
    val isQuickStrike: Boolean,
    val quickStrike: QuickStrikeWorkout? = null,
    val scheduledDayIndex: Int = 0,
    val scheduledDayId: String = "",
    val workoutTitle: String,
    val currentExerciseName: String,
    val currentExerciseAnim: ExerciseAnimationType,
    val stageType: SessionStageType,
    val playlistIndex: Int,
    val totalPlaylistCount: Int,
    val stageLocalIndex: Int,
    val stageTotalCount: Int,
    val currentSetNumber: Int,
    val totalSetsForCurrent: Int,
    val mainWorkoutFinished: Boolean,
    val totalCompletedSetsCount: Int = 0,
    val totalAccumulatedVolumeKg: Float = 0f,
    val swappedVariantsMap: Map<String, Int> = emptyMap(),
    val originPage: WorkoutOriginPage = WorkoutOriginPage.DASHBOARD
) {
    val progressFraction: Float
        get() = if (totalPlaylistCount > 0) {
            val completedSetsFraction = (currentSetNumber - 1).coerceAtLeast(0).toFloat() /
                totalSetsForCurrent.coerceAtLeast(1).toFloat()
            ((playlistIndex.toFloat() + completedSetsFraction) / totalPlaylistCount.toFloat())
                .coerceIn(0.06f, 0.98f)
        } else {
            0f
        }
}

data class ApexUiState(
    val profile: CalibrationProfileState = CalibrationProfileState(),
    val gapReport: CalibrationGapReport = CalibrationCalculator.computeGapReport(
        weightKg = 84.0f,
        heightCm = 178.0f,
        age = 25,
        currentBracket = CurrentBfBracket.BF_24_28,
        goalBracket = GoalBfBracket.GOAL_10_12,
        selectedWeakpointIds = emptySet(),
        biologicalSex = BiologicalSex.MALE,
        ageTier = AgeTier.UNDER_30,
        experienceLevel = ExperienceLevel.NEW_BEGINNER
    ),
    val activeRotationWeek: Int = 0,
    val fourWeekTrack: FourWeekTrackStatus = ProgressiveLoadEngine.computeFourWeekTrack(
        completedWorkoutsCount = 0,
        daysPerWeek = 5
    ),
    val availableCombatDays: List<CombatDaySpec> = CombatSplitCatalog.FOUR_DAY_SPLIT,
    val activeCombatDay: CombatDaySpec = CombatSplitCatalog.FOUR_DAY_SPLIT.first(),
    val weekSchedule: List<DayScheduleSlot> = WeeklySchedulePlanner.buildWeekSchedule(
        splitDays = CombatSplitCatalog.FOUR_DAY_SPLIT,
        restDaysIso = WeeklySchedulePlanner.defaultRestDaysFor(WeeklyFrequency.FOUR_DAYS)
    ),
    val completedSetKeys: Set<String> = emptySet(),
    val setLoadsMap: Map<String, Float> = emptyMap(),
    val todayCompliance: DailyComplianceEntity = DailyComplianceEntity(dateIso = "2026-11-10"),
    val complianceHistory: List<DailyComplianceEntity> = emptyList(),
    val weeklyAudits: List<WeeklyAuditEntity> = emptyList(),
    val stagnationResult: StagnationAuditResult = StagnationAuditResult(
        isStagnant14Days = false,
        spanDays = 0,
        baselineWaistCm = null,
        latestWaistCm = null,
        waistDeltaCm = 0f,
        latestWeightKg = null
    ),
    val lastDispatchStatusMessage: String? = null,
    val activeQuickStrike: QuickStrikeWorkout? = null,
    val openBriefingDirectly: Boolean = false,
    val workoutOriginPage: WorkoutOriginPage = WorkoutOriginPage.DASHBOARD,
    val workoutLaunchMode: WorkoutLaunchMode = WorkoutLaunchMode.HUB,
    val workoutLaunchNonce: Long = 0L,
    val midwayWorkout: MidwayWorkoutSnapshot? = null,
    val currentStreakDays: Int = 0
)

class ApexViewModel(
    private val appContext: Context,
    private val repository: ApexRepository
) : ViewModel() {

    private data class WorkoutSessionNavState(
        val activeQuickStrike: QuickStrikeWorkout? = null,
        val openBriefingDirectly: Boolean = false,
        val originPage: WorkoutOriginPage = WorkoutOriginPage.DASHBOARD,
        val launchMode: WorkoutLaunchMode = WorkoutLaunchMode.HUB,
        val launchNonce: Long = 0L,
        val midwayWorkout: MidwayWorkoutSnapshot? = null
    )

    private val _currentDestination = MutableStateFlow(ApexNavDestination.HOME_DASHBOARD)
    val currentDestination: StateFlow<ApexNavDestination> = _currentDestination.asStateFlow()

    private val _restTimerState = MutableStateFlow(RestTimerState())
    val restTimerState: StateFlow<RestTimerState> = _restTimerState.asStateFlow()

    private val _dispatchBannerMessage = MutableStateFlow<String?>(null)
    private val _sessionNavState = MutableStateFlow(WorkoutSessionNavState())
    private val _settingsInitialTab = MutableStateFlow(0)
    val settingsInitialTab: StateFlow<Int> = _settingsInitialTab.asStateFlow()
    private var restTimerJob: Job? = null

    init {
        TacticalAudioHaptics.initVoiceCoach(appContext)
        viewModelScope.launch {
            repository.ensureSeededTelemetryHistory()
            ApexNotificationScheduler.ensureNotificationChannel(appContext)
            ApexNotificationScheduler.syncScheduledRemindersIfArmed(appContext)
        }
    }

    val uiState: StateFlow<ApexUiState> = combine(
        combine(
            repository.calibrationProfileFlow,
            repository.allSetLogsFlow,
            repository.allDailyComplianceFlow,
            repository.allWeeklyAuditsFlow
        ) { profile, setLogs, complianceList, auditList ->
            QuadrupleData(profile, setLogs, complianceList, auditList)
        },
        _dispatchBannerMessage,
        _sessionNavState
    ) { core, dispatchMsg, sessionNav ->
        val profile = core.profile
        TacticalAudioHaptics.isVoiceCoachMuted = !profile.audioCoachMode.voiceEnabled
        TacticalAudioHaptics.isVibrationMuted = !profile.vibrationEnabled
        TacticalAudioHaptics.syncCoachSpeechSpeed(profile.experienceLevel)
        WorkoutMusicEngine.setMusicAllowed(profile.audioCoachMode.musicEnabled)
        WorkoutMusicEngine.setAutoPlayDuringSets(profile.audioCoachMode.musicEnabled && profile.autoPlayWorkoutMusic)
        if (!profile.audioCoachMode.musicEnabled) {
            WorkoutMusicEngine.stop()
        }
        val setLogs = core.setLogs
        val complianceList = core.complianceList
        val auditList = core.auditList
        val gapReport = CalibrationCalculator.computeGapReport(
            weightKg = profile.bodyweightKg,
            heightCm = profile.heightCm,
            age = profile.age,
            currentBracket = profile.currentBfBracket,
            goalBracket = profile.goalBfBracket,
            selectedWeakpointIds = profile.selectedWeakpointIds,
            biologicalSex = profile.biologicalSex,
            ageTier = profile.ageTier,
            experienceLevel = profile.experienceLevel,
            startingShapeId = profile.selectedStartingShapeCardId,
            goalCardId = profile.selectedGoalCardId,
            measurementsKnown = profile.measurementsKnown,
            daysPerWeek = profile.weeklyFrequency.daysCount
        )
        val fourWeekTrack = ProgressiveLoadEngine.computeFourWeekTrack(
            completedWorkoutsCount = profile.completedWorkoutsCount,
            daysPerWeek = profile.weeklyFrequency.daysCount,
            viewedWeekOverride = profile.weekRotationOverride
        )
        val activeRotationWeek = fourWeekTrack.rotationIndex
        val splitDays = CombatSplitCatalog.getSplitForProfile(
            sex = profile.biologicalSex,
            ageTier = profile.ageTier,
            experience = profile.experienceLevel,
            frequency = profile.weeklyFrequency,
            rotationWeek = activeRotationWeek,
            exercisesPerDay = profile.exercisesPerDay,
            startingShapeId = profile.selectedStartingShapeCardId,
            goalCardId = profile.selectedGoalCardId,
            currentBracket = profile.currentBfBracket,
            goalBracket = profile.goalBfBracket
        )
        val activeDay = splitDays.getOrElse(profile.activeCombatDayIndex) {
            splitDays.first()
        }
        val roomCompletedKeys = setLogs.filter { it.isCompleted }.map { it.setKey }.toSet()
        val mergedCompletedKeys = roomCompletedKeys + profile.persistedCompletedSetKeys
        val loadsMap = setLogs.associate { it.setKey to it.loadKg }
        val weekSchedule = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = splitDays,
            restDaysIso = profile.restDaysIso,
            completedSetKeys = mergedCompletedKeys,
            planStartDateIso = profile.planStartDateIso.ifBlank { null },
            activeSplitIndex = profile.activeCombatDayIndex
        )

        val todayIso = repository.todayIso()
        val roomTodayComp = complianceList.firstOrNull { it.dateIso == todayIso }
        val todayComp = DailyComplianceEntity(
            dateIso = todayIso,
            steps10kReached = profile.steps10kChecked || (roomTodayComp?.steps10kReached == true),
            zeroSugarZeroFried = profile.zeroLiquidSugarChecked || (roomTodayComp?.zeroSugarZeroFried == true),
            proteinFloorLocked = (profile.consumedProteinGrams >= gapReport.dailyProteinFloorGrams) ||
                (roomTodayComp?.proteinFloorLocked == true),
            kitchenClosedBy8pm = profile.kitchenClosedBy8pmChecked || (roomTodayComp?.kitchenClosedBy8pm == true)
        )

        val stagnation = CalibrationCalculator.evaluateStagnation(auditList)
        val completedDatesIso = profile.workoutHistory.map { it.dateIso }.toSet()
        val streakDays = WeeklySchedulePlanner.computeStreakDays(
            completedDatesIso = completedDatesIso,
            restDaysIso = profile.restDaysIso
        )

        ApexUiState(
            profile = profile,
            gapReport = gapReport,
            activeRotationWeek = activeRotationWeek,
            fourWeekTrack = fourWeekTrack,
            availableCombatDays = splitDays,
            activeCombatDay = activeDay,
            weekSchedule = weekSchedule,
            completedSetKeys = mergedCompletedKeys,
            setLoadsMap = loadsMap,
            todayCompliance = todayComp,
            complianceHistory = complianceList.take(7),
            weeklyAudits = auditList,
            stagnationResult = stagnation,
            lastDispatchStatusMessage = dispatchMsg,
            activeQuickStrike = sessionNav.activeQuickStrike,
            openBriefingDirectly = sessionNav.openBriefingDirectly,
            workoutOriginPage = sessionNav.originPage,
            workoutLaunchMode = sessionNav.launchMode,
            workoutLaunchNonce = sessionNav.launchNonce,
            midwayWorkout = sessionNav.midwayWorkout,
            currentStreakDays = streakDays
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApexUiState()
    )

    private data class QuadrupleData(
        val profile: CalibrationProfileState,
        val setLogs: List<CombatSetLogEntity>,
        val complianceList: List<DailyComplianceEntity>,
        val auditList: List<WeeklyAuditEntity>
    )

    fun startQuickStrikeWorkout(
        workout: QuickStrikeWorkout,
        origin: WorkoutOriginPage = WorkoutOriginPage.DASHBOARD
    ) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = workout,
                openBriefingDirectly = true,
                originPage = origin,
                launchMode = WorkoutLaunchMode.OVERVIEW,
                launchNonce = state.launchNonce + 1L
            )
        }
        _currentDestination.value = ApexNavDestination.COMBAT_DECK
    }

    fun openScheduledWorkoutBriefing(
        dayIndex: Int? = null,
        origin: WorkoutOriginPage = WorkoutOriginPage.DASHBOARD
    ) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        if (dayIndex != null) {
            viewModelScope.launch {
                repository.updateActiveCombatDay(dayIndex)
            }
        }
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = null,
                openBriefingDirectly = true,
                originPage = origin,
                launchMode = WorkoutLaunchMode.OVERVIEW,
                launchNonce = state.launchNonce + 1L
            )
        }
        _currentDestination.value = ApexNavDestination.COMBAT_DECK
    }

    fun openWorkoutHub() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        WorkoutMusicEngine.stop()
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = null,
                openBriefingDirectly = false,
                originPage = WorkoutOriginPage.WORKOUT_HUB,
                launchMode = WorkoutLaunchMode.HUB,
                launchNonce = state.launchNonce + 1L
            )
        }
        _currentDestination.value = ApexNavDestination.COMBAT_DECK
    }

    fun returnToWorkoutHub() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        WorkoutMusicEngine.stop()
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = null,
                openBriefingDirectly = false,
                originPage = WorkoutOriginPage.WORKOUT_HUB,
                launchMode = WorkoutLaunchMode.HUB,
                launchNonce = state.launchNonce + 1L
            )
        }
    }

    fun clearQuickStrikeWorkout() {
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = null,
                openBriefingDirectly = false,
                launchMode = WorkoutLaunchMode.HUB
            )
        }
    }

    fun saveMidwayWorkout(snapshot: MidwayWorkoutSnapshot) {
        _sessionNavState.update { state ->
            state.copy(midwayWorkout = snapshot)
        }
    }

    fun clearMidwayWorkout() {
        _sessionNavState.update { state ->
            state.copy(midwayWorkout = null)
        }
    }

    fun resumeMidwayWorkout(origin: WorkoutOriginPage) {
        val snapshot = _sessionNavState.value.midwayWorkout ?: return
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        if (!snapshot.isQuickStrike) {
            viewModelScope.launch {
                repository.updateActiveCombatDay(snapshot.scheduledDayIndex)
            }
        }
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = if (snapshot.isQuickStrike) snapshot.quickStrike else null,
                openBriefingDirectly = false,
                originPage = origin,
                launchMode = WorkoutLaunchMode.RESUME_ACTIVE,
                launchNonce = state.launchNonce + 1L
            )
        }
        _currentDestination.value = ApexNavDestination.COMBAT_DECK
    }

    fun restartMidwayWorkoutFresh(origin: WorkoutOriginPage) {
        val snapshot = _sessionNavState.value.midwayWorkout ?: return
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        dismissRestTimer()
        viewModelScope.launch {
            if (!snapshot.isQuickStrike) {
                repository.updateActiveCombatDay(snapshot.scheduledDayIndex)
                if (snapshot.scheduledDayId.isNotBlank()) {
                    repository.resetCombatDay(snapshot.scheduledDayId)
                }
            }
        }
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = if (snapshot.isQuickStrike) snapshot.quickStrike else null,
                openBriefingDirectly = false,
                originPage = origin,
                launchMode = WorkoutLaunchMode.RESTART_FRESH,
                launchNonce = state.launchNonce + 1L,
                midwayWorkout = null
            )
        }
        _currentDestination.value = ApexNavDestination.COMBAT_DECK
    }

    fun completeMidwayWorkoutNow() {
        val snapshot = _sessionNavState.value.midwayWorkout ?: return
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        WorkoutMusicEngine.stop()
        dismissRestTimer()
        _sessionNavState.update { state ->
            state.copy(
                activeQuickStrike = null,
                openBriefingDirectly = false,
                launchMode = WorkoutLaunchMode.HUB,
                midwayWorkout = null
            )
        }
        val days = uiState.value.availableCombatDays
        val setsDone = snapshot.totalCompletedSetsCount.coerceAtLeast(snapshot.totalPlaylistCount.coerceAtLeast(4))
        val estMins = (setsDone * 2).coerceIn(6, 45)
        val estKcal = (setsDone * 12).coerceIn(45, 420)
        viewModelScope.launch {
            repository.incrementCompletedWorkoutsCount()
            val nowMs = System.currentTimeMillis()
            repository.appendWorkoutHistoryRecord(
                WorkoutHistoryRecord(
                    id = "hist_$nowMs",
                    dateIso = repository.todayIso(),
                    epochMs = nowMs,
                    title = snapshot.workoutTitle,
                    durationMinutes = estMins,
                    setsCompleted = setsDone,
                    estimatedKcal = estKcal
                )
            )
            if (!snapshot.isQuickStrike && days.isNotEmpty()) {
                val nextIdx = (snapshot.scheduledDayIndex + 1) % days.size
                repository.updateActiveCombatDay(nextIdx)
            }
            _dispatchBannerMessage.value = "Workout marked complete: ${snapshot.workoutTitle}"
        }
    }

    fun selectDestination(destination: ApexNavDestination) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        if (destination != ApexNavDestination.COMBAT_DECK) {
            _sessionNavState.update { state ->
                state.copy(
                    activeQuickStrike = null,
                    openBriefingDirectly = false,
                    launchMode = WorkoutLaunchMode.HUB
                )
            }
        }
        _currentDestination.value = destination
    }

    // Biological Calibration Switches
    fun selectBiologicalSex(sex: BiologicalSex) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateBiologicalSex(sex)
        }
    }

    fun selectAgeTier(ageTier: AgeTier) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateAgeTier(ageTier)
        }
    }

    fun selectExperienceLevel(experience: ExperienceLevel) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateExperienceLevel(experience)
        }
    }

    fun selectWeeklyFrequency(frequency: WeeklyFrequency) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateWeeklyFrequency(frequency)
            if (uiState.value.profile.activeCombatDayIndex >= frequency.daysCount) {
                repository.updateActiveCombatDay(0)
            }
        }
    }

    fun toggleRestDay(dayIso: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.toggleRestDayIso(dayIso)
        }
    }

    fun updateUserFirstName(name: String) {
        viewModelScope.launch {
            repository.updateUserFirstName(name)
        }
    }

    fun selectPlanStartOption(option: PlanStartOption) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val targetDate = option.resolveDate(LocalDate.now())
        val targetDayIso = targetDate.dayOfWeek.value
        viewModelScope.launch {
            repository.updatePlanStartDate(
                startDateIso = targetDate.toString(),
                ensureTrainingDayIso = targetDayIso
            )
        }
    }

    fun selectCustomPlanStartDate(date: LocalDate) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val today = LocalDate.now()
        val safeDate = if (date.isBefore(today)) today else date
        val targetDayIso = safeDate.dayOfWeek.value
        viewModelScope.launch {
            repository.updatePlanStartDate(
                startDateIso = safeDate.toString(),
                ensureTrainingDayIso = targetDayIso
            )
            _dispatchBannerMessage.value = PlanStartOption.formatPlanStartSummary(safeDate.toString(), today)
        }
    }

    fun ensurePlanStartInitializedOnOnboardingLock() {
        val currentStart = uiState.value.profile.planStartDateIso
        if (currentStart.isBlank()) {
            val today = LocalDate.now()
            viewModelScope.launch {
                repository.updatePlanStartDate(
                    startDateIso = today.toString(),
                    ensureTrainingDayIso = today.dayOfWeek.value
                )
            }
        }
    }

    fun selectRotationWeek(weekNumber: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            if (weekNumber <= 0) {
                repository.setWeekRotationOverride(null)
            } else {
                repository.setWeekRotationOverride(weekNumber.coerceIn(1, 4))
            }
        }
    }

    private val _exerciseProgressionOffsets = MutableStateFlow<Map<String, Float>>(emptyMap())
    val exerciseProgressionOffsets: StateFlow<Map<String, Float>> = _exerciseProgressionOffsets.asStateFlow()

    fun recordSetEffortAndProgression(exerciseId: String, deltaKg: Float) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        _exerciseProgressionOffsets.update { current ->
            val prev = current[exerciseId] ?: 0f
            current + (exerciseId to (prev + deltaKg))
        }
    }

    fun selectCurrentBfBracket(bracket: CurrentBfBracket) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateCurrentBracket(bracket)
        }
    }

    fun selectGoalBfBracket(bracket: GoalBfBracket) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateGoalBracket(bracket)
        }
    }

    fun updateBiometrics(weightKg: Float, heightCm: Float, age: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateBiometrics(weightKg, heightCm, age)
        }
    }

    fun toggleWeakpoint(weakpointId: String) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.toggleWeakpoint(weakpointId)
        }
    }

    fun onPhotoPicked(slotNumber: Int, uri: Uri?) {
        if (uri == null) return
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        viewModelScope.launch {
            repository.persistPickedPhoto(slotNumber, uri)
        }
    }

    fun clearPickedPhoto(slotNumber: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.clearPickedPhoto(slotNumber)
        }
    }

    // Active Workout Coach & 90-Second Rest Engine Actions
    fun selectCombatDay(dayIndex: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateActiveCombatDay(dayIndex)
        }
    }

    fun completeScheduledDayAndAdvance(
        setsCompletedOverride: Int? = null,
        estimatedKcalOverride: Int? = null
    ) {
        val days = uiState.value.availableCombatDays
        if (days.isEmpty()) return
        val activeDay = uiState.value.activeCombatDay
        val currentIdx = activeDay.dayIndex
        val nextIdx = (currentIdx + 1) % days.size
        val cleanTitle = WeeklySchedulePlanner.benefitHeadlineForDay(activeDay)
        val totalSets = setsCompletedOverride ?: activeDay.exercises.sumOf { it.totalSets }.coerceAtLeast(8)
        val estMins = (activeDay.exercises.size * 5).coerceIn(15, 45)
        val estKcal = estimatedKcalOverride ?: (totalSets * 13).coerceIn(110, 420)
        viewModelScope.launch {
            repository.incrementCompletedWorkoutsCount()
            val nowMs = System.currentTimeMillis()
            repository.appendWorkoutHistoryRecord(
                WorkoutHistoryRecord(
                    id = "hist_$nowMs",
                    dateIso = repository.todayIso(),
                    epochMs = nowMs,
                    title = "Day ${currentIdx + 1}: $cleanTitle",
                    durationMinutes = estMins,
                    setsCompleted = totalSets,
                    estimatedKcal = estKcal
                )
            )
            repository.updateActiveCombatDay(nextIdx)
        }
    }

    fun recordQuickRescueCompleted() {
        viewModelScope.launch {
            repository.incrementCompletedWorkoutsCount()
            val nowMs = System.currentTimeMillis()
            repository.appendWorkoutHistoryRecord(
                WorkoutHistoryRecord(
                    id = "hist_$nowMs",
                    dateIso = repository.todayIso(),
                    epochMs = nowMs,
                    title = "5-Min Minimum Habit Rescue",
                    durationMinutes = 5,
                    setsCompleted = 3,
                    estimatedKcal = 45
                )
            )
        }
    }

    fun recordQuickPackOrChallengeCompleted(
        workout: QuickStrikeWorkout,
        challengeDayNumber: Int? = null,
        setsCompleted: Int = workout.exercises.sumOf { it.totalSets }.coerceAtLeast(3),
        estimatedKcal: Int = workout.caloriesKcal
    ) {
        viewModelScope.launch {
            repository.incrementCompletedWorkoutsCount()
            if (workout.id.startsWith("challenge_") && challengeDayNumber != null) {
                repository.markChallengeDayCompleted(workout.id, challengeDayNumber)
            }
            val nowMs = System.currentTimeMillis()
            val displayTitle = if (workout.id.startsWith("challenge_") && challengeDayNumber != null) {
                "${workout.title} • Day $challengeDayNumber"
            } else {
                workout.title
            }
            repository.appendWorkoutHistoryRecord(
                WorkoutHistoryRecord(
                    id = "hist_$nowMs",
                    dateIso = repository.todayIso(),
                    epochMs = nowMs,
                    title = displayTitle,
                    durationMinutes = workout.durationMinutes,
                    setsCompleted = setsCompleted,
                    estimatedKcal = estimatedKcal
                )
            )
        }
    }

    fun onSetClicked(
        dayId: String,
        exerciseId: String,
        exerciseName: String,
        setNumber: Int,
        defaultLoadKg: Float
    ) {
        val key = CombatSetLogEntity.buildKey(dayId, exerciseId, setNumber)
        val currentlyCompleted = key in uiState.value.completedSetKeys
        val newCompletedState = !currentlyCompleted

        viewModelScope.launch {
            repository.setExerciseSetCompletion(
                dayId = dayId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                loadKg = defaultLoadKg,
                isCompleted = newCompletedState
            )
        }

        if (newCompletedState) {
            TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
            startNinetySecondRestEngine(exerciseName = exerciseName, setNumber = setNumber)
        } else {
            TacticalAudioHaptics.triggerClickPulse(appContext)
        }
    }

    fun logNextSetAndStartRest(exercise: CombatExerciseSpec) {
        val dayId = uiState.value.activeCombatDay.id
        val completedKeys = uiState.value.completedSetKeys
        val nextSetNumber = (1..exercise.totalSets).firstOrNull { setNum ->
            CombatSetLogEntity.buildKey(dayId, exercise.id, setNum) !in completedKeys
        } ?: exercise.totalSets

        viewModelScope.launch {
            repository.setExerciseSetCompletion(
                dayId = dayId,
                exerciseId = exercise.id,
                setNumber = nextSetNumber,
                loadKg = exercise.defaultLoadKg,
                isCompleted = true
            )
        }
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        startNinetySecondRestEngine(exerciseName = exercise.name, setNumber = nextSetNumber)
    }

    fun resetActiveCombatDay() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val dayId = uiState.value.activeCombatDay.id
        viewModelScope.launch {
            repository.resetCombatDay(dayId)
        }
        dismissRestTimer()
    }

    fun startNinetySecondRestEngine(
        exerciseName: String,
        setNumber: Int,
        durationMillis: Long = uiState.value.profile.restDurationSeconds * 1000L
    ) {
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(
            isActive = true,
            remainingMillis = durationMillis,
            totalMillis = durationMillis,
            exerciseName = exerciseName,
            setNumber = setNumber,
            completedAlertActive = false
        )

        restTimerJob = viewModelScope.launch {
            var lastWholeSecond = (durationMillis / 1000L).toInt()
            while (_restTimerState.value.remainingMillis > 0L) {
                delay(100L)
                val nextRemaining = (_restTimerState.value.remainingMillis - 100L).coerceAtLeast(0L)
                val currentWholeSecond = ((nextRemaining + 999L) / 1000L).toInt()

                if (currentWholeSecond != lastWholeSecond && currentWholeSecond in 1..3) {
                    TacticalAudioHaptics.triggerCountdownTick(appContext)
                    TacticalAudioHaptics.triggerCountdownVoice(appContext, currentWholeSecond)
                }
                lastWholeSecond = currentWholeSecond

                _restTimerState.update { state ->
                    state.copy(remainingMillis = nextRemaining)
                }
            }
            TacticalAudioHaptics.triggerRestCompleteAlarm(appContext)
            TacticalAudioHaptics.triggerRestCompleteVoice(appContext, exerciseName)
            _restTimerState.update { state ->
                state.copy(
                    remainingMillis = 0L,
                    completedAlertActive = true
                )
            }
        }
    }

    fun adjustRestTimerMillis(deltaMillis: Long) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        _restTimerState.update { state ->
            val adjusted = (state.remainingMillis + deltaMillis).coerceIn(0L, 300_000L)
            val newTotal = maxOf(state.totalMillis, adjusted)
            state.copy(
                remainingMillis = adjusted,
                totalMillis = newTotal,
                completedAlertActive = adjusted == 0L
            )
        }
    }

    fun forceCompleteRestTimerToZero() {
        restTimerJob?.cancel()
        TacticalAudioHaptics.triggerRestCompleteAlarm(appContext)
        _restTimerState.update { state ->
            state.copy(
                isActive = true,
                remainingMillis = 0L,
                completedAlertActive = true
            )
        }
    }

    fun dismissRestTimer() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(isActive = false)
    }

    // Home Dashboard: Daily Protein Progress & Compliance Actions
    fun addProteinGrams(deltaGrams: Int) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val current = uiState.value.profile.consumedProteinGrams
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        val updated = (current + deltaGrams).coerceIn(0, 400)
        viewModelScope.launch {
            repository.updateConsumedProteinGrams(updated, floor)
        }
    }

    fun setProteinGrams(grams: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        viewModelScope.launch {
            repository.updateConsumedProteinGrams(grams.coerceIn(0, 400), floor)
        }
    }

    fun toggleSteps10k() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.todayCompliance.steps10kReached
        viewModelScope.launch {
            repository.setSteps10kChecked(next)
        }
    }

    fun toggleZeroSugarZeroFried() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.todayCompliance.zeroSugarZeroFried
        viewModelScope.launch {
            repository.setZeroLiquidSugarChecked(next)
        }
    }

    fun toggleProteinFloorLocked() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        val currentlyLocked = uiState.value.todayCompliance.proteinFloorLocked
        val nextGrams = if (currentlyLocked) 0 else floor
        viewModelScope.launch {
            repository.updateConsumedProteinGrams(nextGrams, floor)
        }
    }

    fun selectTrainingEnvironment(environment: com.example.data.local.TrainingEnvironment) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.setTrainingEnvironment(environment)
        }
    }

    fun togglePreWaterElectrolyte() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.preWaterElectrolyteChecked
        viewModelScope.launch {
            repository.setPreWaterElectrolyteChecked(next)
        }
    }

    fun togglePreProteinCarb() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.preProteinCarbChecked
        viewModelScope.launch {
            repository.setPreProteinCarbChecked(next)
        }
    }

    fun togglePreEquipmentScouted() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.preEquipmentScoutedChecked
        viewModelScope.launch {
            repository.setPreEquipmentScoutedChecked(next)
        }
    }

    fun togglePostWorkoutProtein40g() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.postWorkoutProtein40gChecked
        val currentProtein = uiState.value.profile.consumedProteinGrams
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        viewModelScope.launch {
            repository.setPostWorkoutProtein40gChecked(next)
            val delta = if (next) 40 else -40
            repository.updateConsumedProteinGrams((currentProtein + delta).coerceIn(0, 400), floor)
        }
    }

    fun toggleKitchenClosedBy8pm() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.todayCompliance.kitchenClosedBy8pm
        viewModelScope.launch {
            repository.setKitchenClosedBy8pmChecked(next)
        }
    }

    fun logMealProtein(mealTag: String, grams: Int) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        viewModelScope.launch {
            repository.logMealProtein(mealTag, grams)
        }
    }

    fun addFoodItemProtein(foodName: String, grams: Int) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        viewModelScope.launch {
            repository.addFoodTableProtein(grams, floor)
            _dispatchBannerMessage.value = "+${grams}g protein logged ($foodName)"
        }
    }

    fun selectSupplementPreference(preference: com.example.domain.SupplementPreference) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateSupplementPreference(preference)
        }
    }

    fun toggleCreatine5g() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.creatine5gChecked
        viewModelScope.launch {
            repository.setCreatine5gChecked(next)
        }
    }

    fun toggleZeroCandySweets() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.zeroCandySweetsChecked
        viewModelScope.launch {
            repository.setZeroCandySweetsChecked(next)
        }
    }

    fun toggleZeroFriedJunk() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val next = !uiState.value.profile.zeroFriedJunkChecked
        viewModelScope.launch {
            repository.setZeroFriedJunkChecked(next)
        }
    }

    fun recordCravingDefeated(swapProteinGrams: Int = 0) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        TacticalAudioHaptics.triggerCravingDefeatedVoice(appContext)
        val floor = uiState.value.gapReport.dailyProteinFloorGrams
        viewModelScope.launch {
            repository.incrementCravingsDefeated()
            if (swapProteinGrams > 0) {
                repository.addFoodTableProtein(swapProteinGrams, floor)
            }
            _dispatchBannerMessage.value = if (swapProteinGrams > 0) {
                "Craving defeated! +${swapProteinGrams}g clean swap logged"
            } else {
                "Craving defeated! Clean streak protected"
            }
        }
    }

    fun updateSelectedShapeCardIds(startCardId: String?, goalCardId: String?) {
        viewModelScope.launch {
            repository.updateSelectedShapeCardIds(startCardId, goalCardId)
        }
    }

    fun addWater500ml() {
        addWaterMl(500)
    }

    fun addWaterMl(ml: Int) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        viewModelScope.launch {
            repository.addWaterMilliliters(ml)
        }
    }

    fun resetWater() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.resetWaterMilliliters()
        }
    }

    fun resetDailyNutrition() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.resetDailyNutrition()
        }
    }

    fun toggleVoiceCoach() {
        setVoiceCoachEnabled(!uiState.value.profile.voiceCoachEnabled)
    }

    fun setVoiceCoachEnabled(enabled: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        TacticalAudioHaptics.isVoiceCoachMuted = !enabled
        viewModelScope.launch {
            repository.setVoiceCoachEnabled(enabled)
        }
    }

    fun setSettingsInitialTab(tabIndex: Int) {
        _settingsInitialTab.value = tabIndex.coerceIn(0, 1)
    }

    fun selectAppLanguage(language: AppLanguage) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateAppLanguageCode(language.code)
        }
    }

    fun setMeasurementsKnown(known: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val currentProfile = uiState.value.profile
        viewModelScope.launch {
            repository.setMeasurementsKnown(known)
            if (!known) {
                val (estW, estH) = CalibrationCalculator.estimateBaselineFromVisualShape(
                    startingShapeId = currentProfile.selectedStartingShapeCardId,
                    sex = currentProfile.biologicalSex
                )
                repository.updateBiometrics(estW, estH, currentProfile.age)
            }
        }
    }

    fun selectVisualStartingBody(
        shapeId: String,
        bracket: CurrentBfBracket,
        goalIdToKeep: String? = null
    ) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val currentProfile = uiState.value.profile
        viewModelScope.launch {
            repository.updateCurrentBracket(bracket)
            repository.updateSelectedShapeCardIds(shapeId, goalIdToKeep)
            if (!currentProfile.measurementsKnown) {
                val (estW, estH) = CalibrationCalculator.estimateBaselineFromVisualShape(
                    startingShapeId = shapeId,
                    sex = currentProfile.biologicalSex
                )
                repository.updateBiometrics(estW, estH, currentProfile.age)
            }
        }
    }

    fun selectExercisesPerDay(count: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateExercisesPerDay(count)
        }
    }

    fun selectWorkoutIntensity(mode: WorkoutIntensityMode) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateWorkoutIntensity(mode)
        }
    }

    fun setAutoPlayWorkoutMusic(enabled: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        WorkoutMusicEngine.setAutoPlayDuringSets(enabled)
        if (enabled) {
            WorkoutMusicEngine.setMusicAllowed(true)
        }
        viewModelScope.launch {
            repository.setAutoPlayWorkoutMusic(enabled)
        }
    }

    fun selectAudioCoachMode(mode: WorkoutAudioCoachMode) {
        TacticalAudioHaptics.isVoiceCoachMuted = !mode.voiceEnabled
        WorkoutMusicEngine.setMusicAllowed(mode.musicEnabled)
        WorkoutMusicEngine.setAutoPlayDuringSets(mode.musicEnabled)
        TacticalAudioHaptics.triggerClickPulse(appContext)
        if (!mode.musicEnabled) {
            WorkoutMusicEngine.stop()
        }
        viewModelScope.launch {
            repository.updateAudioCoachMode(mode)
        }
    }

    fun selectRestDurationSeconds(seconds: Int) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.updateRestDurationSeconds(seconds)
        }
    }

    fun setIncludeWarmupAndStretch(included: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.setIncludeWarmupAndStretch(included)
        }
    }

    fun setUseMetricUnits(metric: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.setUseMetricUnits(metric)
        }
    }

    fun setKeepScreenAwake(awake: Boolean) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.setKeepScreenAwake(awake)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        TacticalAudioHaptics.isVibrationMuted = !enabled
        if (enabled) {
            TacticalAudioHaptics.triggerClickPulse(appContext)
        }
        viewModelScope.launch {
            repository.setVibrationEnabled(enabled)
        }
    }

    fun clearTodayDailyLogs() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.resetDailyNutrition()
            repository.setSteps10kChecked(false)
            repository.setZeroLiquidSugarChecked(false)
            repository.setKitchenClosedBy8pmChecked(false)
            _dispatchBannerMessage.value = "Today's daily logs cleared"
        }
    }

    fun resetAllAppProgress() {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        WorkoutMusicEngine.stop()
        dismissRestTimer()
        _exerciseProgressionOffsets.value = emptyMap()
        _sessionNavState.value = WorkoutSessionNavState()
        viewModelScope.launch {
            repository.resetAllProgressData()
            repository.updateActiveCombatDay(0)
            _dispatchBannerMessage.value = null
        }
    }

    fun resetForFreshBeginning() {
        WorkoutMusicEngine.stop()
        dismissRestTimer()
        _exerciseProgressionOffsets.value = emptyMap()
        _sessionNavState.value = WorkoutSessionNavState()
        _dispatchBannerMessage.value = null
        viewModelScope.launch {
            repository.resetAllProgressData()
            repository.updateActiveCombatDay(0)
        }
    }

    fun submitWeeklyAudit(dryWeightKg: Float, navelCm: Float) {
        submitWeeklyCheckIn(
            dryWeightKg = dryWeightKg,
            navelCm = navelCm,
            feelTag = "WEEKLY CHECK-IN",
            syncBodyweight = true
        )
    }

    fun submitWeeklyCheckIn(
        dryWeightKg: Float,
        navelCm: Float,
        feelTag: String,
        syncBodyweight: Boolean = true
    ) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        viewModelScope.launch {
            repository.logWeeklyAudit(
                morningDryWeightKg = dryWeightKg,
                navelCircumferenceCm = navelCm,
                auditTag = feelTag,
                syncBodyweight = syncBodyweight
            )
            _dispatchBannerMessage.value = "Weekly check-in saved"
        }
    }

    fun simulateStagnationScenario() {
        TacticalAudioHaptics.triggerRestCompleteAlarm(appContext)
        val weight = uiState.value.profile.bodyweightKg
        val currentWaist = uiState.value.weeklyAudits.firstOrNull()?.navelCircumferenceCm ?: 92.0f
        viewModelScope.launch {
            repository.simulateFourteenDayStagnation(
                currentWeightKg = weight,
                stalledWaistCm = currentWaist
            )
        }
    }

    fun logProgressiveDropScenario() {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        val latestWeight = uiState.value.weeklyAudits.firstOrNull()?.morningDryWeightKg
            ?: uiState.value.profile.bodyweightKg
        val latestWaist = uiState.value.weeklyAudits.firstOrNull()?.navelCircumferenceCm ?: 92.0f
        viewModelScope.launch {
            repository.logWeeklyAudit(
                morningDryWeightKg = (latestWeight - 0.5f).coerceAtLeast(50f),
                navelCircumferenceCm = (latestWaist - 0.7f).coerceAtLeast(65f),
                auditTag = "PROGRESSIVE DROP"
            )
        }
    }

    fun deleteAuditEntry(id: Long) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        viewModelScope.launch {
            repository.deleteWeeklyAudit(id)
        }
    }

    fun toggleBackgroundAlarms(armed: Boolean) {
        TacticalAudioHaptics.triggerSetLoggedPulse(appContext)
        viewModelScope.launch {
            repository.setAlarmsArmed(armed)
            ApexNotificationScheduler.setArmedInPrefs(appContext, armed)
            if (armed) {
                syncProfileRemindersToScheduler(uiState.value.profile)
                ApexNotificationScheduler.scheduleAllDailyOperations(appContext)
                _dispatchBannerMessage.value = "Daily reminders enabled"
            } else {
                ApexNotificationScheduler.cancelAllDailyOperations(appContext)
                _dispatchBannerMessage.value = "Daily reminders off"
            }
        }
    }

    fun updateCustomReminder(
        checkpointId: Int,
        hourOfDay: Int,
        minute: Int,
        enabled: Boolean
    ) {
        TacticalAudioHaptics.triggerClickPulse(appContext)
        val safeH = ((hourOfDay % 24) + 24) % 24
        val safeM = ((minute % 60) + 60) % 60
        viewModelScope.launch {
            repository.updateReminderSchedule(checkpointId, safeH, safeM, enabled)
            ApexNotificationScheduler.saveCustomCheckpointConfig(appContext, checkpointId, safeH, safeM, enabled)
            if (!uiState.value.profile.backgroundAlarmsArmed && enabled) {
                repository.setAlarmsArmed(true)
                ApexNotificationScheduler.setArmedInPrefs(appContext, true)
            }
            if (ApexNotificationScheduler.isArmedInPrefs(appContext)) {
                ApexNotificationScheduler.scheduleAllDailyOperations(appContext)
            }
            _dispatchBannerMessage.value = "Reminder set for ${ApexNotificationScheduler.formatTime12h(safeH, safeM)}"
        }
    }

    private fun syncProfileRemindersToScheduler(profile: CalibrationProfileState) {
        ApexNotificationScheduler.saveCustomCheckpointConfig(
            appContext,
            ApexNotificationScheduler.ID_MORNING_PROTEIN,
            profile.morningReminderHour,
            profile.morningReminderMinute,
            profile.morningReminderEnabled
        )
        ApexNotificationScheduler.saveCustomCheckpointConfig(
            appContext,
            ApexNotificationScheduler.ID_WORKOUT_TIME,
            profile.workoutReminderHour,
            profile.workoutReminderMinute,
            profile.workoutReminderEnabled
        )
        ApexNotificationScheduler.saveCustomCheckpointConfig(
            appContext,
            ApexNotificationScheduler.ID_EVENING_RESET,
            profile.eveningReminderHour,
            profile.eveningReminderMinute,
            profile.eveningReminderEnabled
        )
    }

    fun testFireCheckpointDispatch(checkpoint: OperationalCheckpoint) {
        val posted = ApexNotificationScheduler.dispatchOperationNotification(appContext, checkpoint)
        _dispatchBannerMessage.value = if (posted) {
            "Reminder sent: ${checkpoint.timeLabel} • ${checkpoint.title}"
        } else {
            "Allow notifications to preview alerts (${checkpoint.timeLabel})"
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val appCtx = context.applicationContext
            val db = ApexDatabase.getInstance(appCtx)
            val prefs = CalibrationPreferencesRepository(appCtx)
            val repo = ApexRepository(db.apexDao(), prefs)
            return ApexViewModel(appCtx, repo) as T
        }
    }
}
