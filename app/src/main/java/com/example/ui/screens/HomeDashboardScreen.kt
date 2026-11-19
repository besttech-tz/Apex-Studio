package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.CalibrationProfileState
import com.example.data.local.TrainingEnvironment
import com.example.domain.ApexCoachMovementSpec
import com.example.domain.BiologicalSex
import com.example.domain.BodyFocusCategory
import com.example.domain.CoachTruthAndSplitAdvisor
import com.example.domain.CombatDaySpec
import com.example.domain.DayScheduleSlot
import com.example.domain.FourWeekTrackStatus
import com.example.domain.HomeWorkoutCatalog
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.QuickStrikeWorkout
import com.example.domain.SupplementPreference
import com.example.domain.UnitFormatter
import com.example.domain.WeeklySchedulePlanner
import com.example.domain.WorkoutIntensityMode
import com.example.system.OperationalCheckpoint
import com.example.system.WorkoutMusicEngine
import com.example.ui.ApexUiState
import com.example.ui.RestTimerState
import com.example.ui.components.AnimatedWorkoutCharacter
import com.example.ui.components.ExerciseMasteryCoachDialog
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.MidwayWorkoutResumeCard
import com.example.ui.components.TargetExercisesTopDirectoryCard
import com.example.ui.components.WorkoutCoverResolver
import com.example.ui.components.WorkoutIntensitySelectorBar
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import java.time.LocalDate
import kotlin.math.roundToInt

private val DashInk = Color(0xFF0F172A)
private val DashBodySlate = Color(0xFF475569)
private val DashMutedSlate = Color(0xFF64748B)
private val DashHairline = CardBorderLight
private val DashDivider = SubtleDivider
private val DashSubtleBox = InnerFrameSurface

/**
 * MONOCHROME, VISUAL-FIRST HOME DASHBOARD:
 * - Explicit Mon–Sun muscle tags on calendar pills (Chest, Back, Legs, Arms, Rest, Upper, Rest).
 * - Explicit 4-Week Visible-Change Track status (zero "Week A / Week B" jargon, zero multi-color clutter).
 * - Active visual 2D trainer pose strip on Training & Rest cards instead of bland numbered text lists.
 * - Direct access to 7-Day & 28-Day Challenges + Quick Sessions.
 */
@Composable
fun HomeDashboardScreen(
    uiState: ApexUiState,
    restTimerState: RestTimerState = RestTimerState(),
    progressionOffsets: Map<String, Float> = emptyMap(),
    selectedCategory: BodyFocusCategory = BodyFocusCategory.ABS,
    onSelectCategory: (BodyFocusCategory) -> Unit = {},
    onSelectTrainingEnvironment: (TrainingEnvironment) -> Unit = {},
    onSelectWorkoutIntensity: (WorkoutIntensityMode) -> Unit = {},
    onToggleRestDay: (Int) -> Unit = {},
    onSelectRotationWeek: (Int) -> Unit = {},
    onOpenWorkoutHubClick: () -> Unit = {},
    onTogglePreWaterElectrolyte: () -> Unit = {},
    onTogglePreProteinCarb: () -> Unit = {},
    onTogglePreEquipmentScouted: () -> Unit = {},
    onToggleSetCompleted: (String, String, String, Int, Float) -> Unit = { _, _, _, _, _ -> },
    onStartNinetySecRest: (String, Int) -> Unit = { _, _ -> },
    onAdjustRestMillis: (Long) -> Unit = {},
    onForceZeroRestAlarm: () -> Unit = {},
    onDismissRestTimer: () -> Unit = {},
    onTogglePostWorkoutProtein40g: () -> Unit = {},
    onOpenChallengePlan: () -> Unit = {},
    onOpenCategoryPlan: (BodyFocusCategory) -> Unit = {},
    onStartWorkoutClick: () -> Unit = {},
    onStartQuickStrikeWorkout: (QuickStrikeWorkout) -> Unit = {},
    onResumeMidwayWorkout: () -> Unit = {},
    onRestartMidwayWorkout: () -> Unit = {},
    onCompleteMidwayWorkout: () -> Unit = {},
    onOpenFoodGuideClick: () -> Unit = {},
    onRecalibrateClick: () -> Unit = {},
    onSelectCombatDay: (Int) -> Unit = {},
    onAddProteinGrams: (Int) -> Unit = {},
    onLogMealProtein: (String, Int) -> Unit = { _, _ -> },
    onAddFoodItemProtein: (String, Int) -> Unit = { _, _ -> },
    onSelectSupplementPreference: (SupplementPreference) -> Unit = {},
    onToggleCreatine5g: () -> Unit = {},
    onAddWater: () -> Unit = {},
    onAddWaterMl: (Int) -> Unit = { onAddWater() },
    onResetWater: () -> Unit = {},
    onResetProteinGrams: () -> Unit = {},
    onToggleSteps10k: () -> Unit = {},
    onToggleZeroCandy: () -> Unit = {},
    onToggleZeroLiquidSugar: () -> Unit = {},
    onToggleZeroFriedJunk: () -> Unit = {},
    onToggleKitchenClosed8pm: () -> Unit = {},
    onDefeatCraving: (Int) -> Unit = {},
    onToggleBackgroundAlarms: (Boolean) -> Unit = {},
    onTestCheckpointAlarm: (OperationalCheckpoint) -> Unit = {},
    onSubmitWeeklyAudit: (Float, Float) -> Unit = { _, _ -> },
    onSimulateStagnation: () -> Unit = {},
    onLogProgressiveDrop: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val profile = uiState.profile
    val report = uiState.gapReport
    val compliance = uiState.todayCompliance
    val weekSchedule = uiState.weekSchedule
    val fourWeekTrack = uiState.fourWeekTrack
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        WorkoutMusicEngine.stop()
    }

    val todayIsoNumber = remember { LocalDate.now().dayOfWeek.value }
    val defaultDayIso = remember(weekSchedule, todayIsoNumber) {
        val todaySlot = weekSchedule.firstOrNull { it.dayOfWeekIso == todayIsoNumber }
        if (todaySlot != null && todaySlot.isBeforePlanStart) {
            weekSchedule.firstOrNull { !it.isBeforePlanStart && !it.isRestDay }?.dayOfWeekIso
                ?: weekSchedule.firstOrNull { !it.isBeforePlanStart }?.dayOfWeekIso
                ?: todayIsoNumber
        } else {
            todayIsoNumber
        }
    }
    var selectedDayIso by rememberSaveable(profile.planStartDateIso) { mutableIntStateOf(defaultDayIso) }
    var isEditingRestDays by rememberSaveable { mutableStateOf(false) }

    val selectedSlot = weekSchedule.firstOrNull { it.dayOfWeekIso == selectedDayIso }
        ?: weekSchedule.firstOrNull { it.isToday }
        ?: weekSchedule.first()

    val greetingTitle = if (profile.userFirstName.isNotBlank()) {
        "Hi, ${profile.userFirstName}"
    } else {
        "Apex"
    }
    val headerSubtitle = if (profile.measurementsKnown) {
        "Week ${fourWeekTrack.activeViewedWeek} of 4  •  ${profile.weeklyFrequency.daysCount}d/wk  •  Goal ${UnitFormatter.formatWeightRounded(report.targetBodyweightKg, profile.useMetricUnits)}"
    } else {
        "Week ${fourWeekTrack.activeViewedWeek} of 4  •  ${profile.weeklyFrequency.daysCount}d/wk  •  ${report.honestTimeline.monthsRangeLabel} goal"
    }

    FitnessAtmosphericContainer(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .testTag("home_dashboard_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Framed Athlete Studio Header
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, DashHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_launcher_icon_1794226756788),
                            contentDescription = "Apex Crest",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, DashHairline, CircleShape)
                                .testTag("img_apex_header_crest")
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = greetingTitle,
                                    fontSize = 18.sp,
                                    color = DashInk,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = if (uiState.currentStreakDays > 0) Color(0xFFFFF7ED) else DashSubtleBox,
                                    border = BorderStroke(
                                        1.dp,
                                        if (uiState.currentStreakDays > 0) Color(0xFFFDBA74) else InnerFrameBorder
                                    ),
                                    modifier = Modifier.testTag("badge_dashboard_streak")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = if (uiState.currentStreakDays > 0) Color(0xFFEA580C) else DashInk,
                                            modifier = Modifier.size(11.dp)
                                        )
                                        Text(
                                            text = if (uiState.currentStreakDays > 0) {
                                                "${uiState.currentStreakDays}d Streak"
                                            } else {
                                                "${profile.completedWorkoutsCount} Done"
                                            },
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (uiState.currentStreakDays > 0) Color(0xFF9A3412) else DashInk
                                        )
                                    }
                                }
                            }
                            Text(
                                text = headerSubtitle,
                                fontSize = 11.sp,
                                color = DashBodySlate,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onRecalibrateClick,
                        modifier = Modifier
                            .size(48.dp)
                            .background(DashSubtleBox, CircleShape)
                            .border(1.dp, InnerFrameBorder, CircleShape)
                            .testTag("btn_recalibrate_physique")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = strings.navSettings,
                            tint = DashInk,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 1B. Midway / Left-Off Workout Card (Continue, Start New, or Mark Complete)
            uiState.midwayWorkout?.let { midwaySnapshot ->
                MidwayWorkoutResumeCard(
                    snapshot = midwaySnapshot,
                    onContinueWorkout = onResumeMidwayWorkout,
                    onStartNewWorkout = onRestartMidwayWorkout,
                    onMarkWorkoutComplete = onCompleteMidwayWorkout
                )
            }

        // 2. Mon–Sun Schedule Strip with Explicit Muscle Tags + Monochrome 4-Week Track Pill
        CleanWeeklyCalendarCard(
            weekSchedule = weekSchedule,
            restDaysIso = profile.restDaysIso,
            fourWeekTrack = fourWeekTrack,
            selectedDayIso = selectedSlot.dayOfWeekIso,
            isEditingRestDays = isEditingRestDays,
            onToggleEditMode = { isEditingRestDays = !isEditingRestDays },
            onSelectRotationWeek = onSelectRotationWeek,
            onSelectCalendarDay = { slot ->
                if (isEditingRestDays) {
                    onToggleRestDay(slot.dayOfWeekIso)
                } else {
                    selectedDayIso = slot.dayOfWeekIso
                    if (slot.assignedSplitIndex != null) {
                        onSelectCombatDay(slot.assignedSplitIndex)
                    }
                }
            }
        )

        // 3. Primary Day Card — Pre-Start Ready, Rest Recovery, or Guided Training Day
        if (selectedSlot.isBeforePlanStart) {
            val previewSpec = uiState.activeCombatDay
            PreStartReadyHeroCard(
                selectedSlot = selectedSlot,
                previewDaySpec = previewSpec,
                profile = profile,
                onStartDayOneEarly = {
                    onSelectCombatDay(0)
                    onStartWorkoutClick()
                }
            )
        } else if (selectedSlot.isRestDay || selectedSlot.assignedDaySpec == null) {
            val restFlowWorkout = remember(selectedSlot.dayOfWeekIso, fourWeekTrack.rotationIndex) {
                HomeWorkoutCatalog.getRestDayBloodFlowWorkout(
                    dayOfWeekIso = selectedSlot.dayOfWeekIso,
                    rotationWeek = fourWeekTrack.rotationIndex
                )
            }
            RestDayRecoveryHeroCard(
                selectedSlot = selectedSlot,
                sex = profile.biologicalSex,
                dailyProteinGrams = report.dailyProteinFloorGrams,
                restFlowWorkout = restFlowWorkout,
                onStartLightBloodFlow = { onStartQuickStrikeWorkout(restFlowWorkout) }
            )
        } else {
            val workoutDaySpec = selectedSlot.assignedDaySpec
            PrimaryGuidedWorkoutHeroCard(
                selectedSlot = selectedSlot,
                workoutDaySpec = workoutDaySpec,
                profile = profile,
                fourWeekTrack = fourWeekTrack,
                progressionOffsets = progressionOffsets,
                startWorkoutLabel = strings.startWorkoutCta,
                onSelectEnvironment = onSelectTrainingEnvironment,
                onSelectIntensity = onSelectWorkoutIntensity,
                onStartMyWorkout = {
                    onSelectCombatDay(workoutDaySpec.dayIndex)
                    onStartWorkoutClick()
                }
            )

            // 3B. Motivation & Habit Rescue Bar ("No-Zero-Day" 5-Minute Minimum for low-motivation days)
            LowEnergyHabitRescueCard(
                title = strings.lowEnergyTitle,
                subtitle = strings.lowEnergySubtitle,
                ctaText = strings.lowEnergyAction,
                onStartRescue = {
                    onStartQuickStrikeWorkout(HomeWorkoutCatalog.LOW_ENERGY_RESCUE_WORKOUT)
                }
            )
        }

        // 3C. Head-to-Toe Target Exercises Browser (18 Body Parts & Skills incl. Neck/Tongue & Voice)
        TargetExercisesTopDirectoryCard(
            sex = profile.biologicalSex,
            onStartTargetWorkout = onStartQuickStrikeWorkout,
            compactMode = true
        )

        // 4. Goal-Matched Extra Workout Packs (personalized by sex, body frame, goal & equipment)
        QuickGoalSessionsSheet(
            sex = profile.biologicalSex,
            environment = profile.trainingEnvironment,
            goalCardId = profile.selectedGoalCardId,
            startingShapeId = profile.selectedStartingShapeCardId,
            onStartQuickStrike = onStartQuickStrikeWorkout,
            onSeeAllWorkouts = onOpenWorkoutHubClick
        )

        // 5. Framed Daily Fuel, Hydration & Steps Sheet
        CleanDailyTargetsSheet(
            sectionTitle = strings.dailyTargetsTitle,
            consumedProteinGrams = profile.consumedProteinGrams,
            targetProteinGrams = report.dailyProteinFloorGrams,
            waterMilliliters = profile.waterMilliliters,
            steps10kChecked = compliance.steps10kReached,
            onAddProteinGrams = onAddProteinGrams,
            onResetProteinGrams = onResetProteinGrams,
            onAddWaterMl = onAddWaterMl,
            onResetWater = onResetWater,
            onToggleSteps10k = onToggleSteps10k,
            onOpenFoodGuideClick = onOpenFoodGuideClick
        )

        Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun CleanWeeklyCalendarCard(
    weekSchedule: List<DayScheduleSlot>,
    restDaysIso: Set<Int>,
    fourWeekTrack: FourWeekTrackStatus,
    selectedDayIso: Int,
    isEditingRestDays: Boolean,
    onToggleEditMode: () -> Unit,
    onSelectRotationWeek: (Int) -> Unit,
    onSelectCalendarDay: (DayScheduleSlot) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            .testTag("weekly_calendar_streak_card"),
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, DashHairline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isEditingRestDays) {
                            "Tap days to set Rest"
                        } else {
                            "4-Week Track"
                        },
                        fontSize = 14.sp,
                        color = DashInk,
                        fontWeight = FontWeight.ExtraBold
                    )

                    if (!isEditingRestDays) {
                        val nextWeek = (fourWeekTrack.activeViewedWeek % 4) + 1
                        Surface(
                            onClick = { onSelectRotationWeek(nextWeek) },
                            shape = RoundedCornerShape(999.dp),
                            color = SoftSurfaceTint,
                            border = BorderStroke(1.dp, DashHairline),
                            modifier = Modifier.testTag("btn_dashboard_week_rotation")
                        ) {
                            Text(
                                text = "Week ${fourWeekTrack.activeViewedWeek} of 4",
                                fontSize = 11.sp,
                                color = DashInk,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = if (isEditingRestDays) {
                        "Done"
                    } else {
                        "Rest: ${WeeklySchedulePlanner.formatRestDaysSummary(restDaysIso)}"
                    },
                    fontSize = 12.sp,
                    color = DashInk,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleEditMode() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("btn_toggle_rest_day_editor")
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                weekSchedule.forEach { slot ->
                    val isSelected = !isEditingRestDays && slot.dayOfWeekIso == selectedDayIso
                    val isLightPastOrPreStart = !isEditingRestDays &&
                        !isSelected &&
                        (slot.isBeforePlanStart || (slot.isPastThisWeek && !slot.isCompleted))
                    val pillBg = when {
                        isEditingRestDays && slot.isRestDay -> DashInk
                        isSelected -> DashInk
                        isLightPastOrPreStart -> Color(0xFFF8FAFC)
                        slot.isRestDay -> Color(0xFFF1F5F9)
                        else -> DashSubtleBox
                    }
                    val primaryTextCol = when {
                        (isEditingRestDays && slot.isRestDay) || isSelected -> Color.White
                        isLightPastOrPreStart -> Color(0xFFCBD5E1)
                        slot.isRestDay -> DashMutedSlate
                        else -> DashInk
                    }
                    val subTextCol = when {
                        (isEditingRestDays && slot.isRestDay) || isSelected -> Color.White.copy(alpha = 0.88f)
                        isLightPastOrPreStart -> Color(0xFFCBD5E1)
                        slot.isRestDay -> DashMutedSlate
                        else -> DashBodySlate
                    }

                    Surface(
                        onClick = { onSelectCalendarDay(slot) },
                        enabled = isEditingRestDays || !slot.isBeforePlanStart,
                        shape = RoundedCornerShape(12.dp),
                        color = pillBg,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 58.dp)
                            .testTag("calendar_day_${slot.shortLabel.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = slot.shortLabel.lowercase().replaceFirstChar { it.uppercase() },
                                fontSize = 10.sp,
                                color = subTextCol,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${slot.dayOfMonth}",
                                fontSize = 14.sp,
                                color = primaryTextCol,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = when {
                                    slot.isBeforePlanStart -> "—"
                                    slot.isRestDay -> "Rest"
                                    slot.isCompleted -> "Done"
                                    else -> slot.shortPillTag
                                },
                                fontSize = 9.sp,
                                color = subTextCol,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * HONEST REST-DAY CARD:
 * Explicitly shows "Rest Day" with visual 2D trainer figures for the optional 4-minute recovery moves.
 */
@Composable
private fun PreStartReadyHeroCard(
    selectedSlot: DayScheduleSlot,
    previewDaySpec: CombatDaySpec,
    profile: CalibrationProfileState,
    onStartDayOneEarly: () -> Unit
) {
    val coverRes = WorkoutCoverResolver.coverForScheduledDay(
        previewDaySpec,
        profile.trainingEnvironment,
        profile.biologicalSex
    )
    val dayOneTitle = WeeklySchedulePlanner.benefitHeadlineForDay(previewDaySpec)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x100F172A))
            .clip(RoundedCornerShape(20.dp))
            .testTag("card_todays_workout_hero"),
        shape = RoundedCornerShape(20.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, DashHairline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = dayOneTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${selectedSlot.fullDayName.uppercase()}  •  PRE-START",
                    fontSize = 11.sp,
                    color = DashBodySlate,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = selectedSlot.outcomeTitle,
                    fontSize = 20.sp,
                    color = DashInk,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Day 1 ($dayOneTitle) is queued for your chosen start date — or jump in early below.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = DashBodySlate
                )
                Button(
                    onClick = onStartDayOneEarly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_hero_start_workout"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DashInk,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Preview Day 1 Workout",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun RestDayRecoveryHeroCard(
    selectedSlot: DayScheduleSlot,
    sex: BiologicalSex = BiologicalSex.MALE,
    dailyProteinGrams: Int,
    restFlowWorkout: QuickStrikeWorkout,
    onStartLightBloodFlow: () -> Unit
) {
    var selectedRestMove by remember { mutableStateOf<ApexCoachMovementSpec?>(null) }

    selectedRestMove?.let { move ->
        ExerciseMasteryCoachDialog(
            exerciseName = move.name,
            primaryMuscle = move.targetMusclePill,
            animationType = move.animationType,
            sex = sex,
            existingCues = listOf(move.formCueSummary),
            onDismiss = { selectedRestMove = null }
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x100F172A))
            .clip(RoundedCornerShape(20.dp))
            .testTag("card_todays_workout_hero"),
        shape = RoundedCornerShape(20.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, DashHairline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
            ) {
                Image(
                    painter = painterResource(id = WorkoutCoverResolver.coverForRestDay(sex)),
                    contentDescription = "Rest Day Recovery",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${selectedSlot.fullDayName.uppercase()}  •  REST DAY",
                        fontSize = 11.sp,
                        color = DashBodySlate,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Rest & Muscle Recovery",
                        fontSize = 21.sp,
                        lineHeight = 26.sp,
                        color = DashInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "No lifting today. Let your muscles rebuild and hit ${dailyProteinGrams}g protein.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = DashBodySlate
                    )
                }

                // Visual 3-Move Light Blood-Flow Pose Strip (Tap any move for form & 0->Hero tips!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    restFlowWorkout.exercises.forEachIndexed { idx, move ->
                        Surface(
                            onClick = { selectedRestMove = move },
                            shape = RoundedCornerShape(12.dp),
                            color = DashSubtleBox,
                            border = BorderStroke(1.dp, DashHairline),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dash_rest_pose_card_$idx")
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                AnimatedWorkoutCharacter(
                                    animationType = move.animationType,
                                    exerciseName = move.name,
                                    containerColor = Color.White,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                )
                                Text(
                                    text = move.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "1 × ${move.setsAndRepsLabel}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DashBodySlate,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = onStartLightBloodFlow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_hero_start_workout"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SoftSurfaceTint,
                        contentColor = DashInk
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DashInk,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start ${restFlowWorkout.title}",
                        fontSize = 14.sp,
                        color = DashInk,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * PRIMARY TRAINING-DAY CARD:
 * Replaces bland text-only lists with an active horizontal strip of animated 2D trainer pose cards,
 * explicit 4-Week Track progression subtitle, and an authoritative monochrome Deep Ink CTA.
 */
@Composable
private fun PrimaryGuidedWorkoutHeroCard(
    selectedSlot: DayScheduleSlot,
    workoutDaySpec: CombatDaySpec,
    profile: CalibrationProfileState,
    fourWeekTrack: FourWeekTrackStatus,
    progressionOffsets: Map<String, Float>,
    startWorkoutLabel: String = "START WORKOUT",
    onSelectEnvironment: (TrainingEnvironment) -> Unit,
    onSelectIntensity: (WorkoutIntensityMode) -> Unit,
    onStartMyWorkout: () -> Unit
) {
    val environment = profile.trainingEnvironment
    val intensityMode = profile.workoutIntensity
    val coverRes = WorkoutCoverResolver.coverForScheduledDay(
        workoutDaySpec,
        environment,
        profile.biologicalSex
    )
    val headline = WeeklySchedulePlanner.benefitHeadlineForDay(workoutDaySpec)
    val isHomeMode = environment == TrainingEnvironment.HOME
    val poseStripScroll = rememberScrollState()
    var selectedDashVariant by remember { mutableStateOf<ExerciseFallbackVariant?>(null) }

    selectedDashVariant?.let { coachVar ->
        ExerciseMasteryCoachDialog(
            exerciseName = coachVar.name,
            primaryMuscle = coachVar.primaryMuscle,
            animationType = coachVar.animationType,
            sex = profile.biologicalSex,
            existingCues = coachVar.cues,
            existingBreathing = coachVar.breathingPattern,
            existingMistake = coachVar.commonMistake,
            onDismiss = { selectedDashVariant = null }
        )
    }

    val estimatedKcal = remember(intensityMode, isHomeMode, profile.bodyweightKg) {
        ProgressiveLoadEngine.estimateSessionCalories(
            intensityMode = intensityMode,
            isHomeMode = isHomeMode,
            bodyweightKg = profile.bodyweightKg
        )
    }
    val durationMinutes = intensityMode.estMinutes

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x140F172A))
            .clip(RoundedCornerShape(20.dp))
            .testTag("card_todays_workout_hero"),
        shape = RoundedCornerShape(20.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, DashHairline)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Studio Cover Photo with Dark Vignette Scrim & Framed Metadata Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(162.dp)
            ) {
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = headline,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x33090D16),
                                    Color.Transparent,
                                    Color(0xCC090D16)
                                )
                            )
                        )
                )

                // Bottom-Left Framed Session Burn & Focus Badge on Cover
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.62f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "~$estimatedKcal kcal  •  ~$durationMinutes min",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.55f)
                    ) {
                        Text(
                            text = "${workoutDaySpec.exercises.size} EXERCISES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.4.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .testTag("training_environment_toggle_card")
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        listOf(
                            TrainingEnvironment.GYM to Pair("Gym", Icons.Default.FitnessCenter),
                            TrainingEnvironment.HOME to Pair("Home", Icons.Default.Home)
                        ).forEach { (env, info) ->
                            val selected = environment == env
                            Surface(
                                onClick = { onSelectEnvironment(env) },
                                shape = RoundedCornerShape(999.dp),
                                color = if (selected) DashInk else Color.Transparent,
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 32.dp)
                                    .testTag("env_toggle_${env.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = info.second,
                                        contentDescription = null,
                                        tint = if (selected) Color.White else DashBodySlate,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = info.first,
                                        fontSize = 11.sp,
                                        color = if (selected) Color.White else DashInk,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Pure-White Workout Summary + Active Animated Trainer Strip
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${selectedSlot.fullDayName.uppercase()}  •  WEEK ${fourWeekTrack.activeViewedWeek} OF 4  •  ${if (isHomeMode) "AT HOME" else "GYM"}",
                        fontSize = 11.sp,
                        color = DashBodySlate,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Text(
                        text = headline,
                        fontSize = 21.sp,
                        lineHeight = 26.sp,
                        color = DashInk,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "${fourWeekTrack.weekRuleSubtitle}  •  ~$durationMinutes min",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = DashBodySlate
                    )
                }

                // Framed Targeted Muscle Focus Chips
                val muscleFocusTags = remember(workoutDaySpec) {
                    workoutDaySpec.exercises
                        .map { WeeklySchedulePlanner.whatExerciseDealsWith(it.name, it.primaryMuscle) }
                        .distinct()
                        .take(3)
                }
                if (muscleFocusTags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        muscleFocusTags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DashSubtleBox,
                                border = BorderStroke(1.dp, InnerFrameBorder)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 1-Tap Workout Intensity Selector: [ Simple | Moderate | Extensive ]
                WorkoutIntensitySelectorBar(
                    selectedMode = intensityMode,
                    estimatedKcal = estimatedKcal,
                    onSelectMode = onSelectIntensity,
                    testTagPrefix = "dash_intensity"
                )

                // Active Horizontal Visual Pose Strip (Replaces boring numbered text wall!)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(poseStripScroll),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    workoutDaySpec.exercises.forEachIndexed { idx, ex ->
                        val variants = buildFallbackVariantsFor(
                            spec = ex,
                            index = idx,
                            dayIndex = workoutDaySpec.dayIndex,
                            experienceLevel = profile.experienceLevel,
                            bodyweightKg = profile.bodyweightKg,
                            sex = profile.biologicalSex,
                            completedWorkoutsCount = profile.completedWorkoutsCount,
                            daysPerWeek = profile.weeklyFrequency.daysCount,
                            trackWeekOverride = fourWeekTrack.activeViewedWeek,
                            intensityMode = intensityMode,
                            environment = profile.trainingEnvironment
                        )
                        val displayVar = variants.first()
                        val target = ProgressiveLoadEngine.computeTarget(
                            defaultLoadKg = ex.defaultLoadKg,
                            specTotalSets = ex.totalSets,
                            baseRepsOrSeconds = ex.targetRepsCount,
                            isTimedSeconds = displayVar.isTimedSeconds,
                            isBodyweight = displayVar.isBodyweight,
                            bodyweightKg = profile.bodyweightKg,
                            sex = profile.biologicalSex,
                            experience = profile.experienceLevel,
                            completedWorkoutsCount = profile.completedWorkoutsCount,
                            manualOffsetKg = progressionOffsets[ex.id] ?: 0f,
                            daysPerWeek = profile.weeklyFrequency.daysCount,
                            trackWeekOverride = fourWeekTrack.activeViewedWeek,
                            intensityMode = intensityMode
                        )
                        val prescriptionChip = if (displayVar.isBodyweight || target.loadKg <= 0f) {
                            "${target.sets}×${if (target.isTimedSeconds) "${target.repsOrSeconds}s" else "${target.repsOrSeconds}"}"
                        } else {
                            "${target.sets}×${target.repsOrSeconds} • ${ProgressiveLoadEngine.formatWeightShort(target.loadKg)}"
                        }

                        Surface(
                            onClick = { selectedDashVariant = displayVar },
                            shape = RoundedCornerShape(14.dp),
                            color = DashSubtleBox,
                            border = BorderStroke(1.dp, InnerFrameBorder),
                            modifier = Modifier
                                .width(134.dp)
                                .testTag("dash_pose_card_$idx")
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(66.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .border(1.dp, InnerFrameBorder, RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AnimatedWorkoutCharacter(
                                        animationType = displayVar.animationType,
                                        exerciseName = displayVar.name,
                                        containerColor = Color.White,
                                        modifier = Modifier.size(62.dp)
                                    )
                                }

                                Text(
                                    text = displayVar.name,
                                    fontSize = 12.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashInk,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = prescriptionChip,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DashBodySlate,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Authoritative Monochrome Deep Ink CTA
                Button(
                    onClick = onStartMyWorkout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_hero_start_workout"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DashInk,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = startWorkoutLabel,
                        fontSize = 15.sp,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LowEnergyHabitRescueCard(
    title: String,
    subtitle: String,
    ctaText: String,
    onStartRescue: () -> Unit
) {
    Surface(
        onClick = onStartRescue,
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteCard,
        border = BorderStroke(1.dp, DashHairline),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0A0F172A))
            .testTag("card_low_energy_rescue")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = SoftSurfaceTint,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = DashInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DashInk
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = DashBodySlate,
                        maxLines = 2
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DashInk
            ) {
                Text(
                    text = ctaText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }
        }
    }
}

/**
 * CHALLENGES & QUICK SESSIONS SHEET
 */
@Composable
private fun QuickGoalSessionsSheet(
    sex: BiologicalSex = BiologicalSex.MALE,
    environment: TrainingEnvironment,
    goalCardId: String = "goal_3_athletic",
    startingShapeId: String = "shape_3_skinny_fat",
    onStartQuickStrike: (QuickStrikeWorkout) -> Unit,
    onSeeAllWorkouts: () -> Unit
) {
    val displayedWorkouts = remember(sex, environment, goalCardId, startingShapeId) {
        HomeWorkoutCatalog.dashboardBoostersForProfile(
            sex = sex,
            environment = environment,
            goalCardId = goalCardId,
            startingShapeId = startingShapeId
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recommended Packs for Your Goal",
                fontSize = 16.sp,
                color = DashInk,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "See All Packs →",
                fontSize = 13.sp,
                color = DashInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSeeAllWorkouts() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("btn_open_all_workout_categories")
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, DashHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                displayedWorkouts.forEachIndexed { idx, workout ->
                    androidx.compose.runtime.key(workout.id) {
                        if (idx > 0) {
                            HorizontalDivider(color = DashDivider)
                        }
                        val coverRes = WorkoutCoverResolver.coverForQuickWorkout(workout, sex)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onStartQuickStrike(workout) }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("card_home_workout_${workout.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = coverRes),
                                contentDescription = workout.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                val dashPackBadge = when {
                                    workout.id.startsWith("challenge_7d_") -> "7-DAY CHALLENGE"
                                    workout.id.startsWith("challenge_14d_") -> "14-DAY CHALLENGE"
                                    workout.id.startsWith("challenge_28d_") -> "28-DAY PLAN"
                                    else -> workout.goalFocus.badge
                                }
                                Text(
                                    text = dashPackBadge,
                                    fontSize = 10.sp,
                                    color = DashBodySlate,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = workout.title,
                                    fontSize = 15.sp,
                                    color = DashInk,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${workout.durationMinutes} min  •  ${workout.exercises.size} moves  •  ~${workout.caloriesKcal} kcal",
                                    fontSize = 11.sp,
                                    color = DashBodySlate,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(SoftSurfaceTint, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start ${workout.title}",
                                    tint = DashInk,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * CLEAN MONOCHROME DAILY TARGETS SHEET
 */
@Composable
private fun CleanDailyTargetsSheet(
    sectionTitle: String = "Daily Targets",
    consumedProteinGrams: Int,
    targetProteinGrams: Int,
    waterMilliliters: Int = 0,
    steps10kChecked: Boolean,
    onAddProteinGrams: (Int) -> Unit,
    onResetProteinGrams: () -> Unit,
    onAddWaterMl: (Int) -> Unit = {},
    onResetWater: () -> Unit = {},
    onToggleSteps10k: () -> Unit,
    onOpenFoodGuideClick: () -> Unit
) {
    val safeTarget = targetProteinGrams.coerceAtLeast(1)
    val progress = (consumedProteinGrams.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
    val remainingGrams = (targetProteinGrams - consumedProteinGrams).coerceAtLeast(0)
    val targetWaterMl = 2500
    val waterProgress = (waterMilliliters.toFloat() / targetWaterMl.toFloat()).coerceIn(0f, 1f)

    val truths = CoachTruthAndSplitAdvisor.REALITY_TRUTHS
    var truthIndex by rememberSaveable { mutableIntStateOf(0) }
    val activeTruth = truths[truthIndex.coerceIn(0, truths.lastIndex)]

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = sectionTitle,
                fontSize = 16.sp,
                color = DashInk,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Food Guide →",
                fontSize = 13.sp,
                color = DashInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenFoodGuideClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, DashHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Daily Protein Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("mini_card_protein_floor"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Daily Protein",
                                fontSize = 14.sp,
                                color = DashInk,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (remainingGrams == 0) {
                                    "Target hit today"
                                } else {
                                    "${remainingGrams}g left today"
                                },
                                fontSize = 12.sp,
                                color = DashBodySlate
                            )
                        }

                        Text(
                            text = "${consumedProteinGrams}g / ${targetProteinGrams}g",
                            fontSize = 16.sp,
                            color = DashInk,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = DashInk,
                        trackColor = SoftSurfaceTint,
                        strokeCap = StrokeCap.Round
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { onAddProteinGrams(30) },
                            shape = RoundedCornerShape(10.dp),
                            color = DashInk,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 36.dp)
                                .testTag("btn_add_protein_30g")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+30g Meal",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = { onAddProteinGrams(15) },
                            shape = RoundedCornerShape(10.dp),
                            color = SoftSurfaceTint,
                            modifier = Modifier.defaultMinSize(minHeight = 36.dp)
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+15g Snack",
                                    fontSize = 12.sp,
                                    color = DashInk,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (consumedProteinGrams > 0) {
                            Text(
                                text = "Reset",
                                fontSize = 12.sp,
                                color = DashBodySlate,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onResetProteinGrams() }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("btn_reset_protein")
                            )
                        }
                    }
                }

                HorizontalDivider(color = DashDivider)

                // 2. Daily Water Hydration Tracker (2.5L Target)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("mini_card_water_hydration"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = DashSubtleBox,
                                border = BorderStroke(1.dp, InnerFrameBorder),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = DashInk,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = "Daily Water Hydration",
                                    fontSize = 14.sp,
                                    color = DashInk,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (waterMilliliters >= targetWaterMl) {
                                        "Hydration goal hit today"
                                    } else {
                                        "${(targetWaterMl - waterMilliliters).coerceAtLeast(0)} ml left for peak recovery"
                                    },
                                    fontSize = 12.sp,
                                    color = DashBodySlate
                                )
                            }
                        }

                        Text(
                            text = "${"%.1f".format(waterMilliliters / 1000f)} / 2.5 L",
                            fontSize = 15.sp,
                            color = DashInk,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { waterProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = DashInk,
                        trackColor = SoftSurfaceTint,
                        strokeCap = StrokeCap.Round
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = { onAddWaterMl(250) },
                            shape = RoundedCornerShape(10.dp),
                            color = DashSubtleBox,
                            border = BorderStroke(1.dp, InnerFrameBorder),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 36.dp)
                                .testTag("btn_add_water_250ml")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+250ml Glass",
                                    fontSize = 12.sp,
                                    color = DashInk,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            onClick = { onAddWaterMl(500) },
                            shape = RoundedCornerShape(10.dp),
                            color = DashInk,
                            modifier = Modifier
                                .defaultMinSize(minHeight = 36.dp)
                                .testTag("btn_add_water_500ml")
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+500ml Bottle",
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (waterMilliliters > 0) {
                            Text(
                                text = "Reset",
                                fontSize = 12.sp,
                                color = DashBodySlate,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onResetWater() }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .testTag("btn_reset_water")
                            )
                        }
                    }
                }

                HorizontalDivider(color = DashDivider)

                // 3. Daily Steps Check Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleSteps10k() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .testTag("mini_card_daily_steps"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(SoftSurfaceTint, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsWalk,
                                contentDescription = "Daily Steps",
                                tint = DashInk,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Text(
                                text = "8,000 – 10,000 Steps",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = DashInk
                            )
                            Text(
                                text = if (steps10kChecked) {
                                    "Completed today"
                                } else {
                                    "Tap once walked"
                                },
                                fontSize = 12.sp,
                                color = DashBodySlate
                            )
                        }
                    }

                    if (steps10kChecked) {
                        Surface(
                            shape = CircleShape,
                            color = DashInk,
                            modifier = Modifier.size(26.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color(0xFFCBD5E1), CircleShape)
                        )
                    }
                }

                HorizontalDivider(color = DashDivider)

                // 4. Framed Coach Tip Capsule
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DashSubtleBox,
                        border = BorderStroke(1.dp, InnerFrameBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_reality_truth_spotlight")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = activeTruth.headline,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DashInk
                                )
                                Text(
                                    text = activeTruth.actionRule,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = DashBodySlate
                                )
                            }

                            Surface(
                                onClick = { truthIndex = (truthIndex + 1) % truths.size },
                                shape = RoundedCornerShape(8.dp),
                                color = PureWhiteCard,
                                border = BorderStroke(1.dp, InnerFrameBorder),
                                modifier = Modifier.testTag("btn_next_reality_truth")
                            ) {
                                Text(
                                    text = "Next →",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DashInk,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
