package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.domain.BiologicalSex
import com.example.domain.DayScheduleSlot
import com.example.domain.ExerciseAnimationType
import com.example.domain.ExperienceLevel
import com.example.domain.FourWeekTrackStatus
import com.example.domain.HomeWorkoutCatalog
import com.example.domain.PackEquipmentFilter
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.QuickStrikeWorkout
import com.example.domain.SpecialtyTrackCategory
import com.example.domain.WeeklySchedulePlanner
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutIntensityMode
import com.example.ui.MidwayWorkoutSnapshot
import com.example.ui.components.AnimatedWorkoutCharacter
import com.example.ui.components.ExerciseMasteryCoachDialog
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.MidwayWorkoutResumeCard
import com.example.ui.components.TargetExercisesTopDirectoryCard
import com.example.ui.components.WorkoutCoverResolver
import com.example.ui.components.WorkoutIntensitySelectorBar
import com.example.ui.components.WorkoutMusicStudioCard
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import java.time.LocalDate

private val HubInk = Color(0xFF0F172A)
private val HubSlate = Color(0xFF475569)
private val HubMuted = Color(0xFF64748B)
private val HubHairline = CardBorderLight
private val HubDivider = SubtleDivider
private val HubLightBox = InnerFrameSurface

private data class HubCoachInspectTarget(
    val name: String,
    val primaryMuscle: String,
    val animationType: ExerciseAnimationType,
    val cues: List<String> = emptyList(),
    val breathing: String = "",
    val mistake: String = ""
)

/**
 * MONOCHROME, TRACKED 4-WEEK WORKOUT PAGE:
 * 1. Explicit 4-Week Visible-Change Track (`Week 1` → `Week 2` → `Week 3` → `Week 4`) that visibly steps up
 *    reps in Week 2 and rotates to fresh exercise variations in Weeks 3 & 4 (zero "Week A / Week B" jargon).
 * 2. Zero duplicate Experience Level questions on scheduled workouts (already calibrated in Onboarding).
 * 3. Cohesive monochrome Deep Ink & Pure White exercise list (no scattered light-blue pills).
 * 4. Standalone 7-Day & 28-Day Challenges + Extra Workouts where users pick their intensity inside each challenge.
 */
@Composable
internal fun WorkoutJourneyHubView(
    weekSchedule: List<DayScheduleSlot>,
    restDaysIso: Set<Int>,
    environment: TrainingEnvironment,
    profile: CalibrationProfileState = CalibrationProfileState(),
    activeRotationWeek: Int = 0,
    fourWeekTrack: FourWeekTrackStatus = ProgressiveLoadEngine.computeFourWeekTrack(0, 5),
    progressionOffsets: Map<String, Float> = emptyMap(),
    onSelectEnvironment: (TrainingEnvironment) -> Unit,
    onSelectIntensity: (WorkoutIntensityMode) -> Unit = {},
    onSelectAudioCoachMode: (WorkoutAudioCoachMode) -> Unit = {},
    onToggleRestDay: (Int) -> Unit,
    onSelectRotationWeek: (Int) -> Unit = {},
    onSelectExperienceLevel: (ExperienceLevel) -> Unit = {},
    onOpenScheduledDayBriefing: (Int) -> Unit,
    onOpenSpecialtyWorkout: (QuickStrikeWorkout) -> Unit,
    midwayWorkout: MidwayWorkoutSnapshot? = null,
    onResumeMidwayWorkout: () -> Unit = {},
    onRestartMidwayWorkout: () -> Unit = {},
    onCompleteMidwayWorkout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedCategory by rememberSaveable { mutableStateOf(SpecialtyTrackCategory.ALL) }
    var selectedEquipmentFilter by rememberSaveable { mutableStateOf(PackEquipmentFilter.MATCHED) }
    var showRestDayEditor by rememberSaveable { mutableStateOf(false) }
    var hubCoachTarget by remember { mutableStateOf<HubCoachInspectTarget?>(null) }

    hubCoachTarget?.let { target ->
        ExerciseMasteryCoachDialog(
            exerciseName = target.name,
            primaryMuscle = target.primaryMuscle,
            animationType = target.animationType,
            sex = profile.biologicalSex,
            existingCues = target.cues,
            existingBreathing = target.breathing,
            existingMistake = target.mistake,
            initialTabZeroToHero = false,
            onDismiss = { hubCoachTarget = null }
        )
    }

    val todayIso = remember { LocalDate.now().dayOfWeek.value }
    val defaultHubDayIso = remember(weekSchedule, todayIso) {
        val todaySlot = weekSchedule.firstOrNull { it.dayOfWeekIso == todayIso }
        if (todaySlot != null && todaySlot.isBeforePlanStart) {
            weekSchedule.firstOrNull { !it.isBeforePlanStart && !it.isRestDay }?.dayOfWeekIso
                ?: weekSchedule.firstOrNull { !it.isBeforePlanStart }?.dayOfWeekIso
                ?: todayIso
        } else {
            todayIso
        }
    }
    var selectedDayIso by rememberSaveable(profile.planStartDateIso) { mutableIntStateOf(defaultHubDayIso) }

    val scrollState = rememberScrollState()
    val categoryScrollState = rememberScrollState()

    val selectedSlot = weekSchedule.firstOrNull { it.dayOfWeekIso == selectedDayIso }
        ?: weekSchedule.firstOrNull { it.isToday }
        ?: weekSchedule.first()

    val sexCategories = remember(profile.biologicalSex) {
        HomeWorkoutCatalog.categoriesForSex(profile.biologicalSex)
    }
    val effectiveCategory = if (selectedCategory in sexCategories) {
        selectedCategory
    } else {
        SpecialtyTrackCategory.ALL
    }
    val filteredWorkouts = remember(
        effectiveCategory,
        selectedEquipmentFilter,
        environment,
        profile.biologicalSex,
        profile.selectedGoalCardId,
        profile.selectedStartingShapeCardId
    ) {
        HomeWorkoutCatalog.workoutsForProfile(
            sex = profile.biologicalSex,
            goalCardId = profile.selectedGoalCardId,
            startingShapeId = profile.selectedStartingShapeCardId,
            category = effectiveCategory,
            equipmentFilter = selectedEquipmentFilter,
            activeEnvironment = environment
        )
    }

    val restSummary = remember(restDaysIso) {
        WeeklySchedulePlanner.formatRestDaysSummary(restDaysIso)
    }

    val strings = LocalAppStrings.current
    val isHomeMode = environment == TrainingEnvironment.HOME
    val intensityMode = profile.workoutIntensity
    val estimatedDayKcal = remember(intensityMode, isHomeMode, profile.bodyweightKg) {
        ProgressiveLoadEngine.estimateSessionCalories(
            intensityMode = intensityMode,
            isHomeMode = isHomeMode,
            bodyweightKg = profile.bodyweightKg
        )
    }

    FitnessAtmosphericContainer(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .testTag("screen_workout_journey_hub"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Framed Athletic Studio Cover Banner + Gym / Home Switcher
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, HubHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x120F172A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                ) {
                    Image(
                        painter = painterResource(id = WorkoutCoverResolver.bannerForWorkoutHub(profile.biologicalSex)),
                        contentDescription = strings.navWorkout,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xEB090D16),
                                        Color(0xCC0F172A),
                                        Color(0x8A0F172A)
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = "${fourWeekTrack.weekHeadline.uppercase()}  •  ${fourWeekTrack.completedDaysInCycle}/${fourWeekTrack.totalDaysInCycle} DONE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = strings.navWorkout,
                                fontSize = 24.sp,
                                lineHeight = 28.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${intensityMode.label} Mode  •  ~$estimatedDayKcal kcal / session",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.96f),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                listOf(
                                    TrainingEnvironment.GYM to Pair("Gym", Icons.Default.FitnessCenter),
                                    TrainingEnvironment.HOME to Pair("Home", Icons.Default.Home)
                                ).forEach { (env, pair) ->
                                    val selected = environment == env
                                    Surface(
                                        onClick = { onSelectEnvironment(env) },
                                        shape = RoundedCornerShape(999.dp),
                                        color = if (selected) HubInk else Color.Transparent,
                                        modifier = Modifier.defaultMinSize(minHeight = 32.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = pair.second,
                                                contentDescription = null,
                                                tint = if (selected) Color.White else HubSlate,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = pair.first,
                                                fontSize = 11.sp,
                                                color = if (selected) Color.White else HubInk,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 1B. Midway / Left-Off Workout Card (Continue, Start New, or Mark Complete)
            midwayWorkout?.let { midwaySnapshot ->
                MidwayWorkoutResumeCard(
                    snapshot = midwaySnapshot,
                    onContinueWorkout = onResumeMidwayWorkout,
                    onStartNewWorkout = onRestartMidwayWorkout,
                    onMarkWorkoutComplete = onCompleteMidwayWorkout
                )
            }

            // 1C. Head-to-Toe Target Exercises Directory at the VERY TOP of Workouts
            TargetExercisesTopDirectoryCard(
                sex = profile.biologicalSex,
                onStartTargetWorkout = { targetPack ->
                    onOpenSpecialtyWorkout(targetPack)
                }
            )

        // 2. 4-Week Progressive Track + Weekly Schedule + Selected Day's Actual Exercises
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, HubHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
                .testTag("card_recommended_weekly_journey")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: 4-Week Track Title + Rest Day Editor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showRestDayEditor) {
                            "Tap days to set Rest"
                        } else {
                            "4-Week Visible-Change Track"
                        },
                        fontSize = 15.sp,
                        color = HubInk,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = if (showRestDayEditor) {
                            "Done"
                        } else {
                            "Rest: $restSummary"
                        },
                        fontSize = 12.sp,
                        color = HubInk,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showRestDayEditor = !showRestDayEditor }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("btn_hub_customize_rest_days")
                    )
                }

                // Explicit Week 1 – Week 4 Step Selector (Replaces confusing Week A / Week B & duplicate level bar!)
                if (!showRestDayEditor) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HubLightBox,
                            border = BorderStroke(1.dp, HubHairline),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_toggle_week_rotation")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (1..4).forEach { wk ->
                                    val wkSelected = fourWeekTrack.activeViewedWeek == wk
                                    Surface(
                                        onClick = { onSelectRotationWeek(wk) },
                                        shape = RoundedCornerShape(9.dp),
                                        color = if (wkSelected) HubInk else Color.Transparent,
                                        modifier = Modifier
                                            .weight(1f)
                                            .defaultMinSize(minHeight = 34.dp)
                                            .testTag("hub_track_week_$wk")
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                                        ) {
                                            Text(
                                                text = "Week $wk",
                                                fontSize = 11.sp,
                                                fontWeight = if (wkSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                                color = if (wkSelected) Color.White else HubSlate,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = fourWeekTrack.weekRuleSubtitle,
                            fontSize = 12.sp,
                            color = HubSlate,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Mon–Sun Calendar Pills with Explicit Muscle Focus ("Chest", "Back", "Legs", "Rest"...)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    weekSchedule.forEach { slot ->
                        val isSelected = !showRestDayEditor && slot.dayOfWeekIso == selectedSlot.dayOfWeekIso
                        val isLightPastOrPreStart = !showRestDayEditor &&
                            !isSelected &&
                            (slot.isBeforePlanStart || (slot.isPastThisWeek && !slot.isCompleted))
                        val pillBg = when {
                            showRestDayEditor && slot.isRestDay -> HubInk
                            isSelected -> HubInk
                            isLightPastOrPreStart -> Color(0xFFF8FAFC)
                            slot.isRestDay -> Color(0xFFF1F5F9)
                            else -> HubLightBox
                        }
                        val mainCol = when {
                            (showRestDayEditor && slot.isRestDay) || isSelected -> Color.White
                            isLightPastOrPreStart -> Color(0xFFCBD5E1)
                            slot.isRestDay -> HubMuted
                            else -> HubInk
                        }
                        val subCol = when {
                            (showRestDayEditor && slot.isRestDay) || isSelected -> Color.White.copy(alpha = 0.85f)
                            isLightPastOrPreStart -> Color(0xFFCBD5E1)
                            slot.isRestDay -> HubMuted
                            else -> HubSlate
                        }

                        Surface(
                            onClick = {
                                if (showRestDayEditor) {
                                    onToggleRestDay(slot.dayOfWeekIso)
                                } else {
                                    selectedDayIso = slot.dayOfWeekIso
                                }
                            },
                            enabled = showRestDayEditor || !slot.isBeforePlanStart,
                            shape = RoundedCornerShape(10.dp),
                            color = pillBg,
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 50.dp)
                                .testTag(
                                    if (showRestDayEditor) {
                                        "hub_rest_toggle_${slot.dayOfWeekIso}"
                                    } else {
                                        "journey_row_${slot.shortLabel.lowercase()}"
                                    }
                                )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = slot.shortLabel.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    color = mainCol,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = slot.shortPillTag,
                                    fontSize = 9.sp,
                                    color = subCol,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Clip
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = HubDivider)

                // Selected Day Direct View: PRE-START vs REST DAY vs TRAINING DAY
                val daySpec = selectedSlot.assignedDaySpec
                if (selectedSlot.isBeforePlanStart) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "${selectedSlot.fullDayName} — ${selectedSlot.outcomeTitle}",
                            fontSize = 16.sp,
                            color = HubInk,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Your Day 1 workout begins on your chosen start date. Tap any active training day above or preview Day 1 below.",
                            fontSize = 12.sp,
                            color = HubSlate
                        )
                        Button(
                            onClick = { onOpenScheduledDayBriefing(0) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HubInk,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_hub_start_selected_day")
                        ) {
                            Text(
                                text = "Preview Day 1 Workout",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (selectedSlot.isRestDay || daySpec == null) {
                    val restFlowWorkout = remember(selectedSlot.dayOfWeekIso, fourWeekTrack.rotationIndex) {
                        HomeWorkoutCatalog.getRestDayBloodFlowWorkout(
                            dayOfWeekIso = selectedSlot.dayOfWeekIso,
                            rotationWeek = fourWeekTrack.rotationIndex
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${selectedSlot.fullDayName} — Rest Day",
                                fontSize = 17.sp,
                                color = HubInk,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "No lifting today. Optional ${restFlowWorkout.title} (1 easy set each):",
                                fontSize = 12.sp,
                                color = HubSlate
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HubLightBox,
                            border = BorderStroke(1.dp, HubHairline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                restFlowWorkout.exercises.forEachIndexed { idx, move ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = HubDivider)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                hubCoachTarget = HubCoachInspectTarget(
                                                    name = move.name,
                                                    primaryMuscle = move.targetMusclePill,
                                                    animationType = move.animationType,
                                                    cues = listOf(move.formCueSummary)
                                                )
                                            }
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                            .testTag("hub_rest_exercise_row_$idx"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        AnimatedWorkoutCharacter(
                                            animationType = move.animationType,
                                            exerciseName = move.name,
                                            containerColor = Color.White,
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White)
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = move.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HubInk
                                            )
                                            Text(
                                                text = "${move.targetMusclePill}  •  Tap for 0→Hero tips",
                                                fontSize = 11.sp,
                                                color = HubSlate
                                            )
                                        }
                                        Text(
                                            text = move.setsAndRepsLabel,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = HubInk
                                        )
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { onOpenSpecialtyWorkout(restFlowWorkout) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSurfaceTint,
                                contentColor = HubInk
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_start_rest_blood_flow")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = HubInk,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Start ${restFlowWorkout.title}",
                                fontSize = 13.sp,
                                color = HubInk,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    // TRAINING DAY: Framed cover strip + Simple/Moderate/Extensive intensity bar + exercise list!
                    val dayCoverRes = WorkoutCoverResolver.coverForScheduledDay(
                        daySpec,
                        environment,
                        profile.biologicalSex
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(106.dp)
                                .clip(RoundedCornerShape(14.dp))
                        ) {
                            Image(
                                painter = painterResource(id = dayCoverRes),
                                contentDescription = selectedSlot.outcomeTitle,
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
                                                Color(0xDB090D16)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "${selectedSlot.fullDayName} — ${selectedSlot.outcomeTitle}",
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Week ${fourWeekTrack.activeViewedWeek}  •  ${daySpec.exercises.size} exercises${if (profile.includeWarmupAndStretch) " + warm-up" else ""}  •  ${profile.restDurationSeconds}s rest",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        // 1-Tap Workout Intensity Bar: [ Simple | Moderate | Extensive ]
                        WorkoutIntensitySelectorBar(
                            selectedMode = intensityMode,
                            estimatedKcal = estimatedDayKcal,
                            onSelectMode = onSelectIntensity,
                            testTagPrefix = "hub_intensity"
                        )

                        // Direct Monochrome Exercise List (Framed cleanly inside InnerFrameBorder)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HubLightBox,
                            border = BorderStroke(1.dp, InnerFrameBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                daySpec.exercises.forEachIndexed { idx, ex ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = Color.White)
                                    }
                                    val variants = buildFallbackVariantsFor(
                                        spec = ex,
                                        index = idx,
                                        dayIndex = daySpec.dayIndex,
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
                                    val loadBadge = ProgressiveLoadEngine.formatWeightShort(
                                        target.loadKg,
                                        displayVar.isBodyweight
                                    )
                                    val plainPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
                                        exerciseName = displayVar.name,
                                        primaryMuscle = displayVar.primaryMuscle,
                                        workoutContextTitle = selectedSlot.outcomeTitle
                                    )
                                    val rightTop = if (target.isTimedSeconds) {
                                        "${target.sets} × ${target.repsOrSeconds}s"
                                    } else {
                                        "${target.sets} × ${target.repsOrSeconds}"
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                hubCoachTarget = HubCoachInspectTarget(
                                                    name = displayVar.name,
                                                    primaryMuscle = displayVar.primaryMuscle,
                                                    animationType = displayVar.animationType,
                                                    cues = displayVar.cues,
                                                    breathing = displayVar.breathingPattern,
                                                    mistake = displayVar.commonMistake
                                                )
                                            }
                                            .padding(horizontal = 10.dp, vertical = 9.dp)
                                            .testTag("hub_exercise_row_$idx"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        AnimatedWorkoutCharacter(
                                            animationType = displayVar.animationType,
                                            exerciseName = displayVar.name,
                                            containerColor = Color.White,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White)
                                        )

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                text = displayVar.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HubInk,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = plainPurpose,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = HubSlate,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.spacedBy(1.dp)
                                        ) {
                                            Text(
                                                text = rightTop,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = HubInk
                                            )
                                            Text(
                                                text = loadBadge,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = HubSlate
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { onOpenScheduledDayBriefing(daySpec.dayIndex) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HubInk,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_hub_start_selected_day")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.startWorkoutCta,
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // 2C. 5-Minute Minimum Habit Rescue Strip ("No-Zero-Day" Rule for tired/busy days)
        Surface(
            onClick = { onOpenSpecialtyWorkout(HomeWorkoutCatalog.LOW_ENERGY_RESCUE_WORKOUT) },
            shape = RoundedCornerShape(16.dp),
            color = PureWhiteCard,
            border = BorderStroke(1.dp, HubHairline),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x0A0F172A))
                .testTag("hub_card_low_energy_rescue")
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
                                tint = HubInk,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(end = 8.dp)) {
                        Text(
                            text = strings.lowEnergyTitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HubInk
                        )
                        Text(
                            text = strings.lowEnergySubtitle,
                            fontSize = 11.sp,
                            color = HubSlate,
                            maxLines = 2
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = HubInk
                ) {
                    Text(
                        text = strings.lowEnergyAction,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // 3. Targeted Workout Packs & Challenges (Clean Single-Row Category Chips + Magazine Cards)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "Challenges & Targeted Packs",
                        fontSize = 17.sp,
                        color = HubInk,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (selectedEquipmentFilter == PackEquipmentFilter.ALL_EQUIPMENT) {
                            "Showing Gym & Home packs"
                        } else if (isHomeMode) {
                            "Matched for Home  •  Zero equipment"
                        } else {
                            "Matched for Full Gym  •  Machines & DBs"
                        },
                        fontSize = 12.sp,
                        color = HubSlate
                    )
                }

                // Subtle 1-tap toggle if user wants to browse All Gear vs Matched Gear
                val showingAllGear = selectedEquipmentFilter == PackEquipmentFilter.ALL_EQUIPMENT
                Surface(
                    onClick = {
                        selectedEquipmentFilter = if (showingAllGear) {
                            PackEquipmentFilter.MATCHED
                        } else {
                            PackEquipmentFilter.ALL_EQUIPMENT
                        }
                    },
                    shape = RoundedCornerShape(999.dp),
                    color = if (showingAllGear) HubInk else SoftSurfaceTint,
                    border = BorderStroke(1.dp, if (showingAllGear) HubInk else HubHairline),
                    modifier = Modifier.testTag("equipment_filter_toggle_all")
                ) {
                    Text(
                        text = if (showingAllGear) "All Gear ✓" else "All Gear",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showingAllGear) Color.White else HubInk,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Clean Single-Row Category Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(categoryScrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sexCategories.forEach { cat ->
                    val selected = effectiveCategory == cat
                    Surface(
                        onClick = { selectedCategory = cat },
                        shape = RoundedCornerShape(999.dp),
                        color = if (selected) HubInk else PureWhiteCard,
                        border = if (selected) null else BorderStroke(1.dp, HubHairline),
                        shadowElevation = if (selected) 2.dp else 0.dp,
                        modifier = Modifier
                            .defaultMinSize(minHeight = 38.dp)
                            .testTag("category_chip_${cat.id.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.labelFor(profile.biologicalSex),
                                fontSize = 12.sp,
                                color = if (selected) Color.White else HubInk,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = PureWhiteCard,
                border = BorderStroke(1.dp, HubHairline),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    filteredWorkouts.forEachIndexed { idx, track ->
                        androidx.compose.runtime.key(track.id) {
                            if (idx > 0) {
                                HorizontalDivider(color = HubDivider)
                            }
                            val isRecommended = HomeWorkoutCatalog.isRecommendedForProfile(
                                workout = track,
                                sex = profile.biologicalSex,
                                goalCardId = profile.selectedGoalCardId,
                                startingShapeId = profile.selectedStartingShapeCardId
                            )
                            val completedChallengeCount = profile.challengeCompletedDays[track.id]?.size ?: 0
                            StructuredWorkoutPackRow(
                                workout = track,
                                sex = profile.biologicalSex,
                                isRecommended = isRecommended,
                                completedChallengeDays = completedChallengeCount,
                                onClick = { onOpenSpecialtyWorkout(track) }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StructuredWorkoutPackRow(
    workout: QuickStrikeWorkout,
    sex: BiologicalSex,
    isRecommended: Boolean,
    completedChallengeDays: Int = 0,
    onClick: () -> Unit
) {
    val coverRes = WorkoutCoverResolver.coverForQuickWorkout(workout, sex)
    val cDays = workout.resolvedChallengeDays
    val isMultiDay = cDays > 0
    val badgeText = when {
        cDays == 7 -> if (completedChallengeDays > 0) "7-DAY • $completedChallengeDays/7 DONE" else "7-DAY CHALLENGE"
        cDays == 14 -> if (completedChallengeDays > 0) "14-DAY • $completedChallengeDays/14 DONE" else "14-DAY CHALLENGE"
        cDays == 21 -> if (completedChallengeDays > 0) "21-DAY • $completedChallengeDays/21 DONE" else "21-DAY CHALLENGE"
        cDays >= 28 -> if (completedChallengeDays > 0) "$cDays-DAY • $completedChallengeDays/$cDays DONE" else "$cDays-DAY PLAN"
        isRecommended -> "★ FOR YOUR GOAL"
        else -> workout.goalFocus.badge
    }
    val badgeBg = when {
        isMultiDay -> Color(0xFFEFF6FF)
        isRecommended -> Color(0xFFDCFCE7)
        else -> SoftSurfaceTint
    }
    val badgeFg = when {
        isMultiDay -> Color(0xFF1D4ED8)
        isRecommended -> Color(0xFF166534)
        else -> HubSlate
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 13.dp)
            .testTag("hub_track_card_${workout.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Image(
            painter = painterResource(id = coverRes),
            contentDescription = workout.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(14.dp))
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeBg
            ) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = badgeFg,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                )
            }

            Text(
                text = workout.title,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                color = HubInk,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = HubMuted,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${workout.durationMinutes} min  •  ${workout.exercises.size} moves  •  ~${workout.caloriesKcal} kcal",
                    fontSize = 11.sp,
                    color = HubMuted,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "Results: ${workout.resolvedResultsTimeline}",
                fontSize = 11.sp,
                color = Color(0xFF1D4ED8),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(HubInk, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start ${workout.title}",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
