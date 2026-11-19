package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.TrainingEnvironment
import com.example.domain.AgeTier
import com.example.domain.BiologicalSex
import com.example.domain.CalibrationCalculator
import com.example.domain.CoachTruthAndSplitAdvisor
import com.example.domain.CurrentBfBracket
import com.example.domain.ExperienceLevel
import com.example.domain.GoalBfBracket
import com.example.domain.MetabolicDirection
import com.example.domain.PlanStartOption
import com.example.domain.WeeklyFrequency
import com.example.domain.WeeklySchedulePlanner
import com.example.system.ApexNotificationScheduler
import com.example.ui.ApexUiState
import com.example.ui.components.AnatomicalSilhouetteBadge
import com.example.ui.components.BiometricSilhouetteMath
import com.example.ui.components.ExactReminderTimePickerDialog
import com.example.ui.components.FitnessAtmosphericContainer
import com.example.ui.components.LiveBiometricSilhouetteStudio
import com.example.ui.components.ParametricStartingBodyBadge
import com.example.ui.components.PhysiqueSilhouettePreset
import com.example.ui.components.PlanStartDatePickerDialog
import com.example.ui.components.StudioPageBannerHeader
import com.example.ui.components.TransformationSilhouetteComparisonBanner
import com.example.ui.components.WorkoutCoverResolver
import java.time.LocalDate
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.HomeBackground
import com.example.ui.theme.InnerFrameBorder
import com.example.ui.theme.InnerFrameSurface
import com.example.ui.theme.PureWhiteCard
import com.example.ui.theme.RoyalBlueSoft
import com.example.ui.theme.SoftSurfaceTint
import com.example.ui.theme.SubtleDivider
import com.example.ui.theme.TextSecondarySlate
import kotlin.math.roundToInt

private val OnboardingInk = Color(0xFF0F172A)
private val OnboardingDivider = SubtleDivider

data class StartingBodyOption(
    val id: String,
    val simpleTitle: String,
    val plainCheck: String,
    val badgeColor: Color,
    val currentBracketMale: CurrentBfBracket,
    val currentBracketFemale: CurrentBfBracket
)

data class TargetBodyOption(
    val id: String,
    val badgeText: String,
    val badgeColor: Color,
    val titleMale: String,
    val titleFemale: String,
    val summaryMale: String,
    val summaryFemale: String,
    val musclesMale: String,
    val musclesFemale: String,
    val goalBracketMale: GoalBfBracket,
    val goalBracketFemale: GoalBfBracket
) {
    fun titleFor(sex: BiologicalSex): String =
        if (sex == BiologicalSex.FEMALE) titleFemale else titleMale

    fun summaryFor(sex: BiologicalSex): String =
        if (sex == BiologicalSex.FEMALE) summaryFemale else summaryMale

    fun musclesFor(sex: BiologicalSex): String =
        if (sex == BiologicalSex.FEMALE) musclesFemale else musclesMale
}

object SmartBodyMatcher {

    /**
     * All 6 realistic, relatable starting body types.
     * Displayed openly with ZERO "Recommended" or "Closest Match" bias so every user can honestly identify their current body.
     */
    val ALL_STARTING_BODIES: List<StartingBodyOption> = listOf(
        StartingBodyOption(
            id = "shape_5_underweight",
            simpleTitle = "Very Thin / Skinny",
            plainCheck = "Narrow frame, slim arms and chest, visible collarbones, hard to keep weight on.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_15,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_LEAN_BASE
        ),
        StartingBodyOption(
            id = "shape_3_skinny_fat",
            simpleTitle = "Slim with Belly",
            plainCheck = "Looks slim in clothes, with narrow arms/shoulders and a soft lower stomach.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_18_22,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_20_24
        ),
        StartingBodyOption(
            id = "shape_4_average_untrained",
            simpleTitle = "Normal / Everyday Build",
            plainCheck = "Healthy normal weight (e.g. 165 cm / 67 kg) without heavy belly fat or sharp muscle yet.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_18_22,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_20_24
        ),
        StartingBodyOption(
            id = "shape_7_short_stocky",
            simpleTitle = "Short / Stocky Belly",
            plainCheck = "Compact, thicker build with broad waist and noticeable fat around the stomach.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_24_28,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_25_29
        ),
        StartingBodyOption(
            id = "shape_2_soft_midsection",
            simpleTitle = "Overweight / Round Belly",
            plainCheck = "Noticeable round stomach, fuller chest, and extra midsection weight ready to burn off.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_24_28,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_25_29
        ),
        StartingBodyOption(
            id = "shape_1_high_bf",
            simpleTitle = "Very Obese / Heavy Frame",
            plainCheck = "Heavy weight carried across stomach, chest, waist, and face. Ready for steady, joint-safe change.",
            badgeColor = OnboardingInk,
            currentBracketMale = CurrentBfBracket.BF_30_PLUS,
            currentBracketFemale = CurrentBfBracket.FEMALE_BF_32_PLUS
        )
    )

    /**
     * 4 genuinely distinct target goals — mapped 1-to-1 to the 4 unique target figures (0, 1, 2, 3)
     * with ZERO duplicate images, ZERO "Recommended" bias, and completely distinct purpose & wording.
     */
    val ALL_TARGET_GOALS: List<TargetBodyOption> = listOf(
        TargetBodyOption(
            id = "goal_1_fat_loss",
            badgeText = "",
            badgeColor = OnboardingInk,
            titleMale = "Slim Down & Flat Stomach",
            titleFemale = "Slim Down & Flat Tummy",
            summaryMale = "Burn belly and waist fat steadily so clothes fit looser and everyday movement feels lighter.",
            summaryFemale = "Burn waist, belly, and thigh fat steadily for a lighter, slimmer feminine silhouette.",
            musclesMale = "Weight Loss  •  Smaller Waist  •  Lighter Body",
            musclesFemale = "Smaller Waist  •  Slim Thighs  •  Lighter Body",
            goalBracketMale = GoalBfBracket.GOAL_LEAN_FLAT,
            goalBracketFemale = GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS
        ),
        TargetBodyOption(
            id = "goal_3_athletic",
            badgeText = "",
            badgeColor = OnboardingInk,
            titleMale = "Lean Six-Pack & Stamina",
            titleFemale = "Toned Waist & Sleek Shape",
            summaryMale = "Stay light and agile with sharp visible abs, quick recovery, and high cardio endurance.",
            summaryFemale = "Tighten your waistline and tone your legs, glutes, and posture smoothly without muscle bulk.",
            musclesMale = "Six-Pack Abs  •  Agility  •  Stamina",
            musclesFemale = "Flat Tummy  •  Sleek Tone  •  Zero Bulk",
            goalBracketMale = GoalBfBracket.GOAL_10_12,
            goalBracketFemale = GoalBfBracket.FEMALE_GOAL_TONED_LEAN
        ),
        TargetBodyOption(
            id = "goal_2_vtaper",
            badgeText = "",
            badgeColor = OnboardingInk,
            titleMale = "Broad Shoulders & V-Shape",
            titleFemale = "Lifted Booty & Slim Waist",
            summaryMale = "Widen your upper back and shoulders so your upper frame tapers down to a tight waist.",
            summaryFemale = "Lift and round your buttocks while keeping your waist narrow and upper body slender.",
            musclesMale = "Wide Shoulders  •  Upper Back  •  V-Shape",
            musclesFemale = "Lifted Buttocks  •  Cinched Waist  •  Toned Legs",
            goalBracketMale = GoalBfBracket.GOAL_10_12,
            goalBracketFemale = GoalBfBracket.FEMALE_GOAL_HOURGLASS
        ),
        TargetBodyOption(
            id = "goal_4_power_mass",
            badgeText = "",
            badgeColor = OnboardingInk,
            titleMale = "Thick Size & Heavy Strength",
            titleFemale = "Curvy Hourglass & Full Booty",
            summaryMale = "Pack on dense, heavy muscle mass to fill out sleeves and push serious gym weights.",
            summaryFemale = "Sculpt fuller, rounder buttocks and curvy feminine hips & thighs with a cinched hourglass waist.",
            musclesMale = "Maximum Mass  •  Thick Limbs  •  Raw Strength",
            musclesFemale = "Rounder Booty  •  Curvy Hips  •  Hourglass Shape",
            goalBracketMale = GoalBfBracket.GOAL_MASS_SIZE,
            goalBracketFemale = GoalBfBracket.FEMALE_GOAL_HOURGLASS
        )
    )

    fun computeBmi(weightKg: Float, heightCm: Float): Float {
        val hM = (heightCm / 100f).coerceAtLeast(1.2f)
        return weightKg / (hM * hM)
    }

    fun recommendStartingShapeId(weightKg: Float, heightCm: Float): String {
        val bmi = computeBmi(weightKg, heightCm)
        return when {
            bmi < 18.8f -> "shape_5_underweight"
            bmi < 22.0f -> "shape_3_skinny_fat"
            bmi < 25.4f -> "shape_4_average_untrained"
            bmi < 30.5f -> if (heightCm < 168f) "shape_7_short_stocky" else "shape_2_soft_midsection"
            else -> "shape_1_high_bf"
        }
    }

    fun relevantStartingBodiesFor(
        weightKg: Float,
        heightCm: Float,
        currentSelectedId: String
    ): List<StartingBodyOption> = ALL_STARTING_BODIES

    fun targetBodiesForStartingShape(startingShapeId: String): List<TargetBodyOption> = ALL_TARGET_GOALS
}

/**
 * UNBIASED, HONEST 5-STEP ONBOARDING FLOW:
 * - Step 0: Full-bleed photographic Hero Splash
 * - Step 1: About You (Optional Weight & Height — users who don't know their measurements can skip!)
 * - Step 2: Current Body (All 6 realistic body categories openly shown, ZERO "Recommended" or "Closest Match" bias)
 * - Step 3: Your Goal (All 5 direct goals openly shown, ZERO "Recommended" badges + live Honest Timeline)
 * - Step 4: Weekly Schedule & Training Place (3–6 Days + Rest Day toggles)
 * - Step 5: Honest Plan Summary -> Start My Plan
 */
@Composable
fun CalibrationGapScreen(
    uiState: ApexUiState,
    onSelectBiologicalSex: (BiologicalSex) -> Unit = {},
    onSelectAgeTier: (AgeTier) -> Unit = {},
    onSelectExperienceLevel: (ExperienceLevel) -> Unit = {},
    onSetMeasurementsKnown: (Boolean) -> Unit = {},
    onSelectVisualStartingBody: (String, CurrentBfBracket, String?) -> Unit = { _, _, _ -> },
    onSelectWeeklyFrequency: (WeeklyFrequency) -> Unit = {},
    onSelectTrainingEnvironment: (TrainingEnvironment) -> Unit = {},
    onPickSpecimenPhoto: (Uri?) -> Unit = {},
    onPickBenchmarkPhoto: (Uri?) -> Unit = {},
    onClearPhotoSlot: (Int) -> Unit = {},
    onSelectCurrentBracket: (CurrentBfBracket) -> Unit,
    onSelectGoalBracket: (GoalBfBracket) -> Unit,
    onUpdateSelectedShapeCardIds: (String?, String?) -> Unit = { _, _ -> },
    onUpdateBiometrics: (Float, Float, Int) -> Unit,
    onToggleWeakpoint: (String) -> Unit = {},
    onToggleRestDay: (Int) -> Unit = {},
    onUpdateUserFirstName: (String) -> Unit = {},
    onSelectPlanStartOption: (PlanStartOption) -> Unit = {},
    onSelectCustomStartDate: (LocalDate) -> Unit = {},
    onSelectFreeTimeReminder: (Int, Int, Boolean) -> Unit = { _, _, _ -> },
    onLockInProtocolAndViewWorkout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalSteps = 5
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    val profile = uiState.profile
    val report = uiState.gapReport
    val isFemale = profile.biologicalSex == BiologicalSex.FEMALE

    var selectedStartingShapeId by rememberSaveable {
        mutableStateOf(
            profile.selectedStartingShapeCardId.takeIf { id ->
                SmartBodyMatcher.ALL_STARTING_BODIES.any { it.id == id }
            } ?: "shape_4_average_untrained"
        )
    }
    var selectedPrimaryGoalId by rememberSaveable {
        mutableStateOf(
            profile.selectedGoalCardId.takeIf { id ->
                SmartBodyMatcher.ALL_TARGET_GOALS.any { it.id == id }
            } ?: "goal_1_fat_loss"
        )
    }

    LaunchedEffect(currentStep) {
        scrollState.scrollTo(0)
    }

    BackHandler(enabled = currentStep > 0) {
        currentStep -= 1
    }

    if (currentStep == 0) {
        IconicWelcomeHeroSplash(
            selectedSex = profile.biologicalSex,
            onSelectSex = onSelectBiologicalSex,
            onStartClick = {
                currentStep = 1
            },
            modifier = modifier
        )
        return
    }

    val targetOptions = SmartBodyMatcher.ALL_TARGET_GOALS

    FitnessAtmosphericContainer(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("calibration_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 86.dp)
        ) {
            // Airy, Borderless Floating Progress Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    onClick = { currentStep -= 1 },
                    shape = CircleShape,
                    color = PureWhiteCard,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("onboarding_back_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnboardingInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { currentStep.toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .testTag("onboarding_progress_bar"),
                    color = OnboardingInk,
                    trackColor = Color(0xFFDCE3ED),
                    strokeCap = StrokeCap.Round
                )

                Text(
                    text = "$currentStep / $totalSteps",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnboardingInk
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (currentStep) {
                    1 -> Step1BasicProfileContent(
                        firstName = profile.userFirstName,
                        profileSex = profile.biologicalSex,
                        profileAgeTier = profile.ageTier,
                        experienceLevel = profile.experienceLevel,
                        measurementsKnown = profile.measurementsKnown,
                        bodyweightKg = profile.bodyweightKg,
                        heightCm = profile.heightCm,
                        age = profile.age,
                        onUpdateFirstName = onUpdateUserFirstName,
                        onSelectSex = { newSex ->
                            onSelectBiologicalSex(newSex)
                            val newIsFemale = newSex == BiologicalSex.FEMALE
                            SmartBodyMatcher.ALL_STARTING_BODIES
                                .firstOrNull { it.id == selectedStartingShapeId }
                                ?.let { startOpt ->
                                    val bracket = if (newIsFemale) startOpt.currentBracketFemale else startOpt.currentBracketMale
                                    onSelectCurrentBracket(bracket)
                                    onSelectVisualStartingBody(startOpt.id, bracket, selectedPrimaryGoalId)
                                }
                            targetOptions
                                .firstOrNull { it.id == selectedPrimaryGoalId }
                                ?.let { goalOpt ->
                                    onSelectGoalBracket(if (newIsFemale) goalOpt.goalBracketFemale else goalOpt.goalBracketMale)
                                }
                            onUpdateSelectedShapeCardIds(selectedStartingShapeId, selectedPrimaryGoalId)
                        },
                        onSelectAgeTier = onSelectAgeTier,
                        onSelectExperienceLevel = onSelectExperienceLevel,
                        onSetMeasurementsKnown = { known ->
                            onSetMeasurementsKnown(known)
                            if (known) {
                                val matchedId = SmartBodyMatcher.recommendStartingShapeId(profile.bodyweightKg, profile.heightCm)
                                selectedStartingShapeId = matchedId
                                SmartBodyMatcher.ALL_STARTING_BODIES.firstOrNull { it.id == matchedId }?.let { startOpt ->
                                    val bracket = if (isFemale) startOpt.currentBracketFemale else startOpt.currentBracketMale
                                    onSelectCurrentBracket(bracket)
                                    onSelectVisualStartingBody(startOpt.id, bracket, selectedPrimaryGoalId)
                                }
                                onUpdateSelectedShapeCardIds(matchedId, selectedPrimaryGoalId)
                            }
                        },
                        onUpdateBiometrics = { newW, newH, newAge ->
                            onSetMeasurementsKnown(true)
                            onUpdateBiometrics(newW, newH, newAge)
                            val matchedId = SmartBodyMatcher.recommendStartingShapeId(newW, newH)
                            selectedStartingShapeId = matchedId
                            SmartBodyMatcher.ALL_STARTING_BODIES.firstOrNull { it.id == matchedId }?.let { startOpt ->
                                val bracket = if (isFemale) startOpt.currentBracketFemale else startOpt.currentBracketMale
                                onSelectCurrentBracket(bracket)
                                onSelectVisualStartingBody(startOpt.id, bracket, selectedPrimaryGoalId)
                            }
                            onUpdateSelectedShapeCardIds(matchedId, selectedPrimaryGoalId)
                        }
                    )

                    2 -> Step2UnbiasedCurrentBodyContent(
                        measurementsKnown = profile.measurementsKnown,
                        weightKg = profile.bodyweightKg,
                        heightCm = profile.heightCm,
                        profileSex = profile.biologicalSex,
                        selectedShapeId = selectedStartingShapeId,
                        onSelectShape = { option ->
                            selectedStartingShapeId = option.id
                            val bracket = if (isFemale) option.currentBracketFemale else option.currentBracketMale
                            onSelectCurrentBracket(bracket)
                            onUpdateSelectedShapeCardIds(option.id, selectedPrimaryGoalId)
                            onSelectVisualStartingBody(option.id, bracket, selectedPrimaryGoalId)
                        }
                    )

                    3 -> Step3DirectGoalAndHonestTimelineContent(
                        startingShapeId = selectedStartingShapeId,
                        targetOptions = targetOptions,
                        selectedGoalId = selectedPrimaryGoalId,
                        measurementsKnown = profile.measurementsKnown,
                        weightKg = profile.bodyweightKg,
                        heightCm = profile.heightCm,
                        daysPerWeek = profile.weeklyFrequency.daysCount,
                        experienceLevel = profile.experienceLevel,
                        currentBracket = report.currentBracket,
                        goalBracket = report.goalBracket,
                        profileSex = profile.biologicalSex,
                        onSelectTarget = { target ->
                            selectedPrimaryGoalId = target.id
                            onUpdateSelectedShapeCardIds(selectedStartingShapeId, target.id)
                            onSelectGoalBracket(
                                if (isFemale) target.goalBracketFemale else target.goalBracketMale
                            )
                        }
                    )

                    4 -> Step4WeeklyRoutineContent(
                        report = report,
                        profileSex = profile.biologicalSex,
                        ageTier = profile.ageTier,
                        experienceLevel = profile.experienceLevel,
                        selectedFrequency = profile.weeklyFrequency,
                        restDaysIso = profile.restDaysIso,
                        trainingEnvironment = profile.trainingEnvironment,
                        planStartDateIso = profile.planStartDateIso,
                        workoutReminderEnabled = profile.backgroundAlarmsArmed && profile.workoutReminderEnabled,
                        workoutReminderHour = profile.workoutReminderHour,
                        workoutReminderMinute = profile.workoutReminderMinute,
                        onSelectFrequency = onSelectWeeklyFrequency,
                        onToggleRestDay = onToggleRestDay,
                        onSelectEnvironment = onSelectTrainingEnvironment,
                        onSelectPlanStartOption = onSelectPlanStartOption,
                        onSelectCustomStartDate = onSelectCustomStartDate,
                        onSelectFreeTimeReminder = onSelectFreeTimeReminder
                    )

                    else -> Step5BlueprintSummaryContent(
                        report = report,
                        measurementsKnown = profile.measurementsKnown,
                        profileSex = profile.biologicalSex,
                        ageTier = profile.ageTier,
                        experienceLevel = profile.experienceLevel,
                        startingShapeId = selectedStartingShapeId,
                        targetOption = targetOptions.firstOrNull { it.id == selectedPrimaryGoalId } ?: targetOptions.first(),
                        selectedFrequency = profile.weeklyFrequency,
                        restDaysIso = profile.restDaysIso,
                        trainingEnvironment = profile.trainingEnvironment,
                        planStartDateIso = profile.planStartDateIso
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Clean Bottom Action Bar
        Surface(
            color = PureWhiteCard,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(color = OnboardingDivider)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    val isFinalStep = currentStep >= totalSteps
                    Button(
                        onClick = {
                            // Persist current step selections even when the user keeps the default highlighted card
                            SmartBodyMatcher.ALL_STARTING_BODIES
                                .firstOrNull { it.id == selectedStartingShapeId }
                                ?.let { startOpt ->
                                    val bracket = if (isFemale) startOpt.currentBracketFemale else startOpt.currentBracketMale
                                    onSelectCurrentBracket(bracket)
                                    onSelectVisualStartingBody(startOpt.id, bracket, selectedPrimaryGoalId)
                                }
                            targetOptions
                                .firstOrNull { it.id == selectedPrimaryGoalId }
                                ?.let { goalOpt ->
                                    onSelectGoalBracket(if (isFemale) goalOpt.goalBracketFemale else goalOpt.goalBracketMale)
                                }
                            onUpdateSelectedShapeCardIds(selectedStartingShapeId, selectedPrimaryGoalId)

                            if (!isFinalStep) {
                                currentStep += 1
                            } else {
                                onLockInProtocolAndViewWorkout()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag(if (!isFinalStep) "btn_onboarding_continue" else "btn_lock_transformation_plan"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OnboardingInk,
                            contentColor = PureWhiteCard
                        )
                    ) {
                        Text(
                            text = if (!isFinalStep) "Continue" else "Start My Plan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhiteCard,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconicWelcomeHeroSplash(
    selectedSex: BiologicalSex = BiologicalSex.MALE,
    onSelectSex: (BiologicalSex) -> Unit = {},
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val splashRes = WorkoutCoverResolver.bannerForSplash(selectedSex)
    val isFemale = selectedSex == BiologicalSex.FEMALE

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070B14))
            .testTag("screen_onboarding_intro")
    ) {
        Image(
            painter = painterResource(id = splashRes),
            contentDescription = "Sculpted Athletic Physique",
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0x88070B14),
                        0.45f to Color.Transparent,
                        0.72f to Color(0xD9070B14),
                        1.0f to Color(0xFF070B14)
                    )
                )
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xA6070B14)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_launcher_icon_1794226756788),
                        contentDescription = "Apex Crest",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                    )
                    Text(
                        text = "APEX STUDIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            // Gender-Matched Splash Preview Switcher (Men | Women)
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color(0xBD0B1120)
            ) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    listOf(
                        BiologicalSex.MALE to "Men",
                        BiologicalSex.FEMALE to "Women"
                    ).forEach { (sex, label) ->
                        val active = selectedSex == sex
                        Surface(
                            onClick = { onSelectSex(sex) },
                            shape = RoundedCornerShape(999.dp),
                            color = if (active) Color.White else Color.Transparent,
                            modifier = Modifier.testTag("splash_sex_toggle_${sex.name.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (active) OnboardingInk else Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.16f)
            ) {
                Text(
                    text = if (isFemale) "WOMEN'S SCULPTING & TONE COACH" else "PERSONAL WORKOUT & MUSIC COACH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.9.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Text(
                text = if (isFemale) "SCULPT YOUR SHAPE.\nFEEL STRONG DAILY." else "TRAIN TOGETHER.\nBUILD REAL STRENGTH.",
                fontSize = 33.sp,
                lineHeight = 37.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Guided rep-by-rep workouts matched to your experience speed, motivating workout music + voice coaching, and an honest timeline.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color(0xFFE2E8F0),
                fontWeight = FontWeight.Medium
            )

            // Frosted 3-pillar studio capsule strip (borderless)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xBD0B1120),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        "PACE MATCHED" to "Counts With You",
                        "AUDIO COACH" to "Music + Voice",
                        "REAL TIMELINE" to "Zero Hype"
                    ).forEachIndexed { idx, (topTag, subLabel) ->
                        if (idx > 0) {
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(26.dp)
                                    .background(Color.White.copy(alpha = 0.18f))
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = topTag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = subLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_start_calibration_setup"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = OnboardingInk
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnboardingInk
                )
            }
        }
    }
}

/**
 * STEP 1: BASIC PROFILE WITH OPTIONAL WEIGHT & HEIGHT
 * Users who don't know their current weight or height can tap "I Don't Know Mine" and calibrate from body look alone.
 */
@Composable
private fun Step1BasicProfileContent(
    firstName: String = "",
    profileSex: BiologicalSex,
    profileAgeTier: AgeTier,
    experienceLevel: ExperienceLevel,
    measurementsKnown: Boolean,
    bodyweightKg: Float,
    heightCm: Float,
    age: Int,
    onUpdateFirstName: (String) -> Unit = {},
    onSelectSex: (BiologicalSex) -> Unit,
    onSelectAgeTier: (AgeTier) -> Unit,
    onSelectExperienceLevel: (ExperienceLevel) -> Unit,
    onSetMeasurementsKnown: (Boolean) -> Unit,
    onUpdateBiometrics: (Float, Float, Int) -> Unit
) {
    var localName by rememberSaveable(firstName) { mutableStateOf(firstName) }

    StudioPageBannerHeader(
        imageResId = WorkoutCoverResolver.bannerForOnboardingStep(1, profileSex),
        eyebrowBadge = "ATHLETE PROFILE",
        title = "About You",
        subtitle = "Personalize your plan, starting weights, and exercise angles.",
        bannerHeightDp = 124,
        trailingBadgeText = experienceLevel.paceBadgeLabel
    )

    // Clean Borderless Gender Segmented Track
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFE6ECF4),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                BiologicalSex.MALE to "Male",
                BiologicalSex.FEMALE to "Female"
            ).forEach { (sex, label) ->
                val selected = profileSex == sex
                Surface(
                    onClick = { onSelectSex(sex) },
                    shape = RoundedCornerShape(11.dp),
                    color = if (selected) PureWhiteCard else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("sex_toggle_${sex.name.lowercase()}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (selected) OnboardingInk else TextSecondarySlate
                        )
                    }
                }
            }
        }
    }

    // Borderless Elevated White Sheet
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(20.dp), spotColor = Color(0x0E0F172A))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Optional First Name Input for Personal Greeting
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "First Name (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                OutlinedTextField(
                    value = localName,
                    onValueChange = { next ->
                        val trimmed = next.take(28)
                        localName = trimmed
                        onUpdateFirstName(trimmed)
                    },
                    placeholder = {
                        Text(
                            text = "e.g. Alex",
                            fontSize = 13.sp,
                            color = TextSecondarySlate
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OnboardingInk,
                        unfocusedBorderColor = CardBorderLight,
                        focusedContainerColor = SoftSurfaceTint,
                        unfocusedContainerColor = SoftSurfaceTint,
                        focusedTextColor = OnboardingInk,
                        unfocusedTextColor = OnboardingInk
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_onboarding_first_name")
                )
            }

            HorizontalDivider(color = OnboardingDivider)

            // Optional Measurements Switcher ("Enter Weight & Height" vs "I Don't Know Mine")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Weight & Height (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftSurfaceTint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            true to "Enter Weight & Height",
                            false to "I Don't Know Mine"
                        ).forEach { (knownOption, label) ->
                            val selected = measurementsKnown == knownOption
                            Surface(
                                onClick = { onSetMeasurementsKnown(knownOption) },
                                shape = RoundedCornerShape(9.dp),
                                color = if (selected) OnboardingInk else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 40.dp)
                                    .testTag(if (knownOption) "btn_measurements_known" else "btn_measurements_skip")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else OnboardingInk,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                if (!measurementsKnown) {
                    Text(
                        text = "No problem — we won't ask for scale or tape numbers. On the next step, simply tap the body build that looks closest to yours.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondarySlate
                    )
                }
            }

            if (measurementsKnown) {
                HorizontalDivider(color = OnboardingDivider)

                LiveBiometricSilhouetteStudio(
                    sex = profileSex,
                    heightCm = heightCm,
                    weightKg = bodyweightKg
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CleanCompactMetricStepper(
                            label = "Height",
                            valueText = "${heightCm.roundToInt()} cm",
                            onMinus = { onUpdateBiometrics(bodyweightKg, (heightCm - 1f).coerceAtLeast(140f), age) },
                            onPlus = { onUpdateBiometrics(bodyweightKg, (heightCm + 1f).coerceAtMost(215f), age) }
                        )
                        HorizontalDivider(color = OnboardingDivider)
                        CleanCompactMetricStepper(
                            label = "Current Weight",
                            valueText = "${bodyweightKg.roundToInt()} kg",
                            onMinus = { onUpdateBiometrics((bodyweightKg - 1f).coerceAtLeast(40f), heightCm, age) },
                            onPlus = { onUpdateBiometrics((bodyweightKg + 1f).coerceAtMost(180f), heightCm, age) }
                        )
                    }
                }
            }

            HorizontalDivider(color = OnboardingDivider)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Age Range",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        AgeTier.UNDER_30 to "Under 30",
                        AgeTier.AGE_30_45 to "30 – 45",
                        AgeTier.AGE_45_PLUS to "45+"
                    ).forEach { (tier, label) ->
                        val selected = profileAgeTier == tier
                        Surface(
                            onClick = { onSelectAgeTier(tier) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) OnboardingInk else SoftSurfaceTint,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White else OnboardingInk
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = OnboardingDivider)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Have you trained before?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ExperienceLevel.entries.forEach { lvl ->
                        val selected = experienceLevel == lvl
                        Surface(
                            onClick = { onSelectExperienceLevel(lvl) },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) OnboardingInk else SoftSurfaceTint,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("onboarding_exp_${lvl.name.lowercase()}")
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 4.dp)) {
                                Text(
                                    text = lvl.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selected) Color.White else OnboardingInk,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "${experienceLevel.shortTag} • ${experienceLevel.paceBadgeLabel} — adjust any weight (+/- kg) anytime.",
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = TextSecondarySlate
                )
            }
        }
    }
}

@Composable
private fun CleanMetricRowStepper(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = label,
                fontSize = 13.sp,
                color = TextSecondarySlate,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = valueText,
                fontSize = 22.sp,
                color = OnboardingInk,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                onClick = onMinus,
                shape = CircleShape,
                color = SoftSurfaceTint,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease $label",
                        tint = OnboardingInk,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Surface(
                onClick = onPlus,
                shape = CircleShape,
                color = SoftSurfaceTint,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase $label",
                        tint = OnboardingInk,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CleanCompactMetricStepper(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondarySlate,
            fontWeight = FontWeight.Medium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valueText,
                fontSize = 20.sp,
                color = OnboardingInk,
                fontWeight = FontWeight.ExtraBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    onClick = onMinus,
                    shape = CircleShape,
                    color = SoftSurfaceTint,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease $label",
                            tint = OnboardingInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Surface(
                    onClick = onPlus,
                    shape = CircleShape,
                    color = SoftSurfaceTint,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase $label",
                            tint = OnboardingInk,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * STEP 2: CURRENT BODY — ALL 6 REALISTIC BODY BUILDS SHOWN WITH ACCURATE PARAMETRIC SILHOUETTES
 * Automatically synced with Step 1 Height & Weight when known, while keeping all 6 builds selectable.
 */
@Composable
private fun Step2UnbiasedCurrentBodyContent(
    measurementsKnown: Boolean,
    weightKg: Float,
    heightCm: Float,
    profileSex: BiologicalSex,
    selectedShapeId: String,
    onSelectShape: (StartingBodyOption) -> Unit
) {
    val matchedId = remember(measurementsKnown, weightKg, heightCm) {
        if (measurementsKnown) SmartBodyMatcher.recommendStartingShapeId(weightKg, heightCm) else null
    }
    val biometricSnapshot = remember(measurementsKnown, weightKg, heightCm) {
        if (measurementsKnown) BiometricSilhouetteMath.analyze(weightKg = weightKg, heightCm = heightCm) else null
    }

    StudioPageBannerHeader(
        imageResId = WorkoutCoverResolver.bannerForOnboardingStep(2, profileSex),
        eyebrowBadge = "STARTING POINT",
        title = "Current Body",
        subtitle = if (measurementsKnown) {
            "Auto-matched to ${heightCm.roundToInt()} cm & ${weightKg.roundToInt()} kg — adjust below if your mirror look differs."
        } else {
            "No scale needed — pick the body build that looks closest to you in the mirror."
        },
        bannerHeightDp = 126,
        trailingBadgeText = if (biometricSnapshot != null) "BMI ${biometricSnapshot.bmi}" else "6 Honest Builds"
    )

    if (measurementsKnown && matchedId != null && selectedShapeId != matchedId) {
        val matchedTitle = SmartBodyMatcher.ALL_STARTING_BODIES.firstOrNull { it.id == matchedId }?.simpleTitle ?: "Normal Build"
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFFBEB),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_biometric_mismatch_notice")
        ) {
            Text(
                text = "Note: ${heightCm.roundToInt()} cm & ${weightKg.roundToInt()} kg (BMI ${biometricSnapshot?.bmi}) aligns with '$matchedTitle'. Your timeline will calibrate using your selected build.",
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF92400E),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
            )
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SmartBodyMatcher.ALL_STARTING_BODIES.forEach { option ->
            val isSelected = selectedShapeId == option.id
            val isBiometricMatch = matchedId == option.id

            BodySelectionCard(
                testTagId = option.id,
                topBadge = if (isBiometricMatch) {
                    "Matches ${heightCm.roundToInt()}cm • ${weightKg.roundToInt()}kg"
                } else {
                    null
                },
                topBadgeColor = Color(0xFF166534),
                title = option.simpleTitle,
                description = option.plainCheck,
                footerLine = null,
                preset = PhysiqueSilhouettePreset.fromCardId(option.id),
                sex = profileSex,
                accentColor = OnboardingInk,
                isTargetCard = false,
                isSelected = isSelected,
                startingShapeIdForParametric = option.id,
                onClick = { onSelectShape(option) }
            )
        }
    }
}

/**
 * STEP 3: YOUR GOAL + LIVE HONEST UN-SUGARCOATED TIMELINE
 * Zero "Recommended" badges. Users can choose Fat Loss, Recomp, V-Taper, Lean Abs, or Big Muscle freely,
 * and see the exact realistic timeline from their starting body to that goal immediately.
 */
@Composable
private fun Step3DirectGoalAndHonestTimelineContent(
    startingShapeId: String,
    targetOptions: List<TargetBodyOption>,
    selectedGoalId: String,
    measurementsKnown: Boolean,
    weightKg: Float,
    heightCm: Float,
    daysPerWeek: Int,
    experienceLevel: ExperienceLevel,
    currentBracket: CurrentBfBracket,
    goalBracket: GoalBfBracket,
    profileSex: BiologicalSex,
    onSelectTarget: (TargetBodyOption) -> Unit
) {
    val startOption = SmartBodyMatcher.ALL_STARTING_BODIES.firstOrNull { it.id == startingShapeId }
        ?: SmartBodyMatcher.ALL_STARTING_BODIES[2]
    val activeTarget = targetOptions.firstOrNull { it.id == selectedGoalId } ?: targetOptions.first()

    val liveTimeline = remember(
        startingShapeId,
        activeTarget.id,
        measurementsKnown,
        weightKg,
        heightCm,
        daysPerWeek,
        experienceLevel,
        profileSex
    ) {
        CalibrationCalculator.computeHonestTimeline(
            startingShapeId = startingShapeId,
            goalCardId = activeTarget.id,
            daysPerWeek = daysPerWeek,
            weightKg = weightKg,
            heightCm = heightCm,
            measurementsKnown = measurementsKnown
        )
    }

    StudioPageBannerHeader(
        imageResId = WorkoutCoverResolver.bannerForOnboardingStep(3, profileSex),
        eyebrowBadge = "TARGET TRANSFORMATION",
        title = "Your Goal",
        subtitle = "Choose what you want to build from ${startOption.simpleTitle} — honest timeline below.",
        bannerHeightDp = 126,
        trailingBadgeText = liveTimeline.monthsRangeLabel
    )

    TransformationSilhouetteComparisonBanner(
        currentBracket = currentBracket,
        goalBracket = goalBracket,
        sex = profileSex,
        startPresetOverride = PhysiqueSilhouettePreset.fromCardId(startingShapeId),
        goalPresetOverride = PhysiqueSilhouettePreset.fromCardId(activeTarget.id),
        startTitleOverride = startOption.simpleTitle,
        goalTitleOverride = activeTarget.titleFor(profileSex),
        compactMode = true
    )

    // Live Honest, Un-Sugarcoated Biological Timeline Card (Borderless Elevated Surface)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
            .testTag("card_honest_timeline_truth")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HONEST REALISTIC TIMELINE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSecondarySlate,
                    letterSpacing = 0.6.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = OnboardingInk
                ) {
                    Text(
                        text = liveTimeline.monthsRangeLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = liveTimeline.honestTruthHeadline,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardingInk
            )

            Text(
                text = liveTimeline.honestTruthBody,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = TextSecondarySlate
            )

            Text(
                text = "First visible change: ${liveTimeline.firstVisibleMilestone}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = OnboardingInk
            )

            if (liveTimeline.monthThreeFourCheckpoint.isNotBlank()) {
                Text(
                    text = liveTimeline.monthThreeFourCheckpoint,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D4ED8)
                )
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        targetOptions.forEach { target ->
            val isSelected = activeTarget.id == target.id
            BodySelectionCard(
                testTagId = target.id,
                topBadge = null,
                topBadgeColor = OnboardingInk,
                title = target.titleFor(profileSex),
                description = target.summaryFor(profileSex),
                footerLine = target.musclesFor(profileSex),
                preset = PhysiqueSilhouettePreset.fromCardId(target.id),
                sex = profileSex,
                accentColor = OnboardingInk,
                isTargetCard = true,
                isSelected = isSelected,
                onClick = { onSelectTarget(target) }
            )
        }
    }
}

/**
 * STEP 4: DEDICATED WEEKLY ROUTINE & ENVIRONMENT STEP
 */
@Composable
private fun Step4WeeklyRoutineContent(
    report: com.example.domain.CalibrationGapReport,
    profileSex: BiologicalSex,
    ageTier: AgeTier,
    experienceLevel: ExperienceLevel,
    selectedFrequency: WeeklyFrequency,
    restDaysIso: Set<Int>,
    trainingEnvironment: TrainingEnvironment,
    planStartDateIso: String = "",
    workoutReminderEnabled: Boolean = false,
    workoutReminderHour: Int = 17,
    workoutReminderMinute: Int = 30,
    onSelectFrequency: (WeeklyFrequency) -> Unit,
    onToggleRestDay: (Int) -> Unit,
    onSelectEnvironment: (TrainingEnvironment) -> Unit,
    onSelectPlanStartOption: (PlanStartOption) -> Unit = {},
    onSelectCustomStartDate: (LocalDate) -> Unit = {},
    onSelectFreeTimeReminder: (Int, Int, Boolean) -> Unit = { _, _, _ -> }
) {
    val advice = remember(report.metabolicDirection, experienceLevel, selectedFrequency) {
        CoachTruthAndSplitAdvisor.adviseBestFrequency(
            direction = report.metabolicDirection,
            experience = experienceLevel,
            selectedFrequency = selectedFrequency
        )
    }

    var showStartDatePicker by rememberSaveable { mutableStateOf(false) }
    var showExactTimePicker by rememberSaveable { mutableStateOf(false) }

    val today = remember { LocalDate.now() }
    val matchedStartOption = remember(planStartDateIso, today) {
        PlanStartOption.matchOptionOrNull(planStartDateIso, today)
    }
    val isCustomCalendarDate = matchedStartOption == null || matchedStartOption == PlanStartOption.START_MONDAY
    val startSummaryBadge = remember(planStartDateIso, today) {
        PlanStartOption.formatPlanStartSummary(planStartDateIso, today)
    }

    if (showStartDatePicker) {
        PlanStartDatePickerDialog(
            initialDateIso = planStartDateIso,
            onDateSelected = { pickedDate ->
                showStartDatePicker = false
                onSelectCustomStartDate(pickedDate)
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    if (showExactTimePicker) {
        ExactReminderTimePickerDialog(
            title = "Workout Reminder",
            initialHour = workoutReminderHour,
            initialMinute = workoutReminderMinute,
            onTimeConfirmed = { h, m ->
                showExactTimePicker = false
                onSelectFreeTimeReminder(h, m, true)
            },
            onDismiss = { showExactTimePicker = false }
        )
    }

    val envCoverRes = WorkoutCoverResolver.bannerForOnboardingStep(4, profileSex, trainingEnvironment)

    StudioPageBannerHeader(
        imageResId = envCoverRes,
        eyebrowBadge = "YOUR SCHEDULE",
        title = "Schedule & Reminder",
        subtitle = "Set where you train, your Day 1 date, weekly free days, and exact workout time.",
        bannerHeightDp = 114,
        trailingBadgeText = "${selectedFrequency.daysCount}d / Wk"
    )

    // 1. Compact Gym vs Home Switcher
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFE6ECF4),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                TrainingEnvironment.GYM to Pair("Full Gym", Icons.Default.FitnessCenter),
                TrainingEnvironment.HOME to Pair("At Home", Icons.Default.Home)
            ).forEach { (env, pair) ->
                val selected = trainingEnvironment == env
                Surface(
                    onClick = { onSelectEnvironment(env) },
                    shape = RoundedCornerShape(11.dp),
                    color = if (selected) PureWhiteCard else Color.Transparent,
                    shadowElevation = if (selected) 2.dp else 0.dp,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = pair.second,
                            contentDescription = null,
                            tint = if (selected) OnboardingInk else TextSecondarySlate,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pair.first,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) OnboardingInk else TextSecondarySlate
                        )
                    }
                }
            }
        }
    }

    // 2. Starting Day (Today, Tomorrow, or Exact Calendar Date)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
            .testTag("card_onboarding_start_option")
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Day 1 Start Date",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                Text(
                    text = startSummaryBadge,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1D63ED)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isTodayActive = matchedStartOption == PlanStartOption.START_TODAY
                Surface(
                    onClick = { onSelectPlanStartOption(PlanStartOption.START_TODAY) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTodayActive) OnboardingInk else SoftSurfaceTint,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("chip_start_option_start_today")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isTodayActive) Color.White else OnboardingInk,
                            maxLines = 1
                        )
                        Text(
                            text = "Day 1 now",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTodayActive) Color.White.copy(alpha = 0.85f) else TextSecondarySlate,
                            maxLines = 1
                        )
                    }
                }

                val isTomorrowActive = matchedStartOption == PlanStartOption.START_TOMORROW
                Surface(
                    onClick = { onSelectPlanStartOption(PlanStartOption.START_TOMORROW) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isTomorrowActive) OnboardingInk else SoftSurfaceTint,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("chip_start_option_start_tomorrow")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tomorrow",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isTomorrowActive) Color.White else OnboardingInk,
                            maxLines = 1
                        )
                        Text(
                            text = "Fresh start",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isTomorrowActive) Color.White.copy(alpha = 0.85f) else TextSecondarySlate,
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    onClick = { showStartDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isCustomCalendarDate) OnboardingInk else SoftSurfaceTint,
                    border = if (isCustomCalendarDate) null else BorderStroke(1.dp, CardBorderLight),
                    modifier = Modifier
                        .weight(1.25f)
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("btn_onboarding_pick_start_date")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Choose calendar date",
                            tint = if (isCustomCalendarDate) Color.White else OnboardingInk,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = if (isCustomCalendarDate) {
                                    PlanStartOption.formatShortCustomDateLabel(planStartDateIso, today)
                                } else {
                                    "Calendar"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isCustomCalendarDate) Color.White else OnboardingInk,
                                maxLines = 1
                            )
                            Text(
                                text = "Pick exact date",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isCustomCalendarDate) Color.White.copy(alpha = 0.85f) else TextSecondarySlate,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    // 3. Unified Weekly Training Days & Pace Card (No duplicate 4-row radio list!)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
            .testTag("onboarding_rest_day_picker_card")
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Training Days",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                Text(
                    text = "Ideal: ${advice.recommendedFrequency.daysCount}d / wk",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondarySlate
                )
            }

            // Compact 1-Row Quick Pace Selector (3d / 4d / 5d / 6d)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                WeeklyFrequency.entries.forEach { freq ->
                    val selected = selectedFrequency == freq
                    val isIdeal = freq == advice.recommendedFrequency
                    Surface(
                        onClick = { onSelectFrequency(freq) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) OnboardingInk else SoftSurfaceTint,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 44.dp)
                            .testTag("frequency_card_${freq.daysCount}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = if (isIdeal) "${freq.daysCount} Days ★" else "${freq.daysCount} Days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selected) Color.White else OnboardingInk,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // 7-Day Tap-to-Toggle Strip (Mon..Sun)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                WeeklySchedulePlanner.ISO_DAYS.forEach { (iso, shortName) ->
                    val isRest = iso in restDaysIso
                    val isTrain = !isRest
                    Surface(
                        onClick = { onToggleRestDay(iso) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isTrain) OnboardingInk else SoftSurfaceTint,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("onboarding_rest_chip_$iso")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = shortName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTrain) Color.White else OnboardingInk
                            )
                            Text(
                                text = if (isTrain) "Train" else "Rest",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isTrain) Color.White.copy(alpha = 0.88f) else TextSecondarySlate
                            )
                        }
                    }
                }
            }

            Text(
                text = "Train: ${WeeklySchedulePlanner.formatTrainDaysSummary(restDaysIso)}  •  Rest: ${WeeklySchedulePlanner.formatRestDaysSummary(restDaysIso)}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondarySlate
            )
        }
    }

    // 4. Exact Workout Reminder Time (Interactive Clock Picker + Quick 8 AM / 9 PM Chips)
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0E0F172A))
            .testTag("card_onboarding_free_time")
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Workout Reminder Time",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnboardingInk
                )
                Surface(
                    onClick = {
                        onSelectFreeTimeReminder(
                            workoutReminderHour,
                            workoutReminderMinute,
                            !workoutReminderEnabled
                        )
                    },
                    shape = RoundedCornerShape(999.dp),
                    color = if (workoutReminderEnabled) Color(0xFFE8F1FE) else SoftSurfaceTint,
                    modifier = Modifier.testTag("chip_free_time_off")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!workoutReminderEnabled) {
                            Icon(
                                imageVector = Icons.Default.NotificationsOff,
                                contentDescription = null,
                                tint = TextSecondarySlate,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = if (workoutReminderEnabled) "Alert On" else "Silent",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (workoutReminderEnabled) Color(0xFF1D63ED) else TextSecondarySlate
                        )
                    }
                }
            }

            // Prominent Exact Clock Picker Button
            Surface(
                onClick = { showExactTimePicker = true },
                shape = RoundedCornerShape(12.dp),
                color = if (workoutReminderEnabled) OnboardingInk else SoftSurfaceTint,
                border = if (workoutReminderEnabled) null else BorderStroke(1.dp, CardBorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 50.dp)
                    .testTag("btn_onboarding_pick_exact_time")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Choose exact reminder time",
                            tint = if (workoutReminderEnabled) Color.White else OnboardingInk,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (workoutReminderEnabled) {
                                ApexNotificationScheduler.formatTime12h(workoutReminderHour, workoutReminderMinute)
                            } else {
                                "Reminders Off (${ApexNotificationScheduler.formatTime12h(workoutReminderHour, workoutReminderMinute)})"
                            },
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (workoutReminderEnabled) Color.White else OnboardingInk
                        )
                    }
                    Text(
                        text = "Pick Exact Time",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (workoutReminderEnabled) Color.White.copy(alpha = 0.88f) else Color(0xFF1D63ED)
                    )
                }
            }

            data class FreeTimePreset(val tag: String, val sub: String, val h: Int, val m: Int)
            val timePresets = listOf(
                FreeTimePreset("morning", "8:00 AM", 8, 0),
                FreeTimePreset("midday", "12:30 PM", 12, 30),
                FreeTimePreset("evening", "5:30 PM", 17, 30),
                FreeTimePreset("night", "9:00 PM", 21, 0)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                timePresets.forEach { preset ->
                    val isSelected = workoutReminderEnabled &&
                        workoutReminderHour == preset.h &&
                        workoutReminderMinute == preset.m
                    Surface(
                        onClick = { onSelectFreeTimeReminder(preset.h, preset.m, true) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0xFF1D63ED) else SoftSurfaceTint,
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 42.dp)
                            .testTag("chip_free_time_${preset.tag}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp)
                        ) {
                            Text(
                                text = preset.sub,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else OnboardingInk,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * STEP 5: HONEST PLAN & TIMELINE SUMMARY
 */
@Composable
private fun Step5BlueprintSummaryContent(
    report: com.example.domain.CalibrationGapReport,
    measurementsKnown: Boolean,
    profileSex: BiologicalSex,
    ageTier: AgeTier,
    experienceLevel: ExperienceLevel,
    startingShapeId: String,
    targetOption: TargetBodyOption,
    selectedFrequency: WeeklyFrequency,
    restDaysIso: Set<Int>,
    trainingEnvironment: TrainingEnvironment = TrainingEnvironment.GYM,
    planStartDateIso: String = ""
) {
    val startOption = SmartBodyMatcher.ALL_STARTING_BODIES.firstOrNull { it.id == startingShapeId }
        ?: SmartBodyMatcher.ALL_STARTING_BODIES[2]

    val goalDirectionLabel = when (report.metabolicDirection) {
        MetabolicDirection.GAIN_LEAN_MASS -> "Muscle Gain"
        MetabolicDirection.BODY_RECOMP -> "Lose Fat + Build Muscle"
        MetabolicDirection.CUT_FAT -> "Fat Loss & Muscle Tone"
    }

    val startBannerLabel = if (measurementsKnown) {
        "${report.currentWeightKg.roundToInt()} kg • ${startOption.simpleTitle}"
    } else {
        startOption.simpleTitle
    }
    val goalBannerLabel = if (measurementsKnown) {
        "${report.targetBodyweightKg.roundToInt()} kg • ${targetOption.titleFor(profileSex)}"
    } else {
        targetOption.titleFor(profileSex)
    }
    val startWhen = PlanStartOption.formatPlanStartSummary(planStartDateIso)

    StudioPageBannerHeader(
        imageResId = WorkoutCoverResolver.bannerForOnboardingStep(5, profileSex, trainingEnvironment),
        eyebrowBadge = "READY TO TRAIN TOGETHER",
        title = "Your Honest Plan",
        subtitle = "$startWhen  •  ${selectedFrequency.daysCount}d/wk  •  Train: ${WeeklySchedulePlanner.formatTrainDaysSummary(restDaysIso)}",
        bannerHeightDp = 128,
        trailingBadgeText = goalDirectionLabel
    )

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x100F172A))
            .testTag("compact_truth_box")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TransformationSilhouetteComparisonBanner(
                currentBracket = report.currentBracket,
                goalBracket = report.goalBracket,
                sex = profileSex,
                startPresetOverride = PhysiqueSilhouettePreset.fromCardId(startingShapeId),
                goalPresetOverride = PhysiqueSilhouettePreset.fromCardId(targetOption.id),
                startTitleOverride = startBannerLabel,
                goalTitleOverride = goalBannerLabel,
                compactMode = true
            )

            HorizontalDivider(color = OnboardingDivider)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BlueprintStatColumn(
                    label = "DAILY CALORIES",
                    value = "${report.caloricDeficitCeilingKcal}",
                    unit = "kcal / day",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(OnboardingDivider)
                )
                BlueprintStatColumn(
                    label = "DAILY PROTEIN",
                    value = "${report.dailyProteinFloorGrams}g",
                    unit = "minimum",
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(38.dp)
                        .background(OnboardingDivider)
                )
                BlueprintStatColumn(
                    label = "FULL GOAL TIME",
                    value = report.honestTimeline.monthsRangeLabel,
                    unit = "honest pace",
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = OnboardingDivider)

            // Un-sugarcoated reality note
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SoftSurfaceTint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = report.honestTimeline.honestTruthHeadline,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = OnboardingInk
                    )
                    Text(
                        text = report.honestTimeline.honestTruthBody,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = TextSecondarySlate
                    )
                }
            }

            HorizontalDivider(color = OnboardingDivider)

            // First 4-Week Block Roadmap
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FIRST 4-WEEK CONSISTENCY BLOCK",
                    fontSize = 10.sp,
                    color = TextSecondarySlate,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                listOf(
                    "Week 1" to "Safe form & base reps (${experienceLevel.label})",
                    "Week 2" to "+2 reps per set to build endurance",
                    "Week 3" to "Fresh exercise angles + load step-up",
                    "Week 4" to "First visible posture, waist & strength check"
                ).forEach { (wkLabel, wkDesc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SoftSurfaceTint,
                            modifier = Modifier.width(62.dp)
                        ) {
                            Text(
                                text = wkLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = OnboardingInk,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = wkDesc,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnboardingInk,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlueprintStatColumn(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondarySlate,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = OnboardingInk,
            maxLines = 1
        )
        Text(
            text = unit,
            fontSize = 10.sp,
            color = TextSecondarySlate
        )
    }
}

@Composable
private fun SelectionRadioIndicator(isSelected: Boolean) {
    if (isSelected) {
        Surface(
            shape = CircleShape,
            color = OnboardingInk,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = PureWhiteCard,
                    modifier = Modifier.size(14.dp)
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

@Composable
internal fun BodySelectionCard(
    testTagId: String,
    topBadge: String?,
    topBadgeColor: Color,
    title: String,
    description: String,
    footerLine: String?,
    preset: PhysiqueSilhouettePreset,
    sex: BiologicalSex,
    accentColor: Color,
    isTargetCard: Boolean,
    isSelected: Boolean,
    startingShapeIdForParametric: String? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFFF7FAFF) else PureWhiteCard,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 7.dp else 2.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isSelected) Color(0x1E0F172A) else Color(0x0B0F172A)
            )
            .testTag(testTagId)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(if (isTargetCard) 96.dp else 84.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(OnboardingInk)
                )
            }
            if (!isTargetCard && startingShapeIdForParametric != null) {
                ParametricStartingBodyBadge(
                    shapeId = startingShapeIdForParametric,
                    sex = sex,
                    isSelected = isSelected
                )
            } else {
                AnatomicalSilhouetteBadge(
                    preset = preset,
                    sex = sex,
                    accentColor = accentColor,
                    isSelected = isSelected,
                    largeShowcase = isTargetCard
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (topBadge != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalBlueSoft
                        ) {
                            Text(
                                text = topBadge,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = topBadgeColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(1.dp))
                    }

                    SelectionRadioIndicator(isSelected = isSelected)
                }

                Text(
                    text = title,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    color = OnboardingInk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = TextSecondarySlate
                )

                if (footerLine != null) {
                    Text(
                        text = footerLine,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnboardingInk.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun StatStepperCard(
    label: String,
    valueText: String,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = PureWhiteCard,
        modifier = modifier.shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x0A0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondarySlate,
                letterSpacing = 0.5.sp
            )
            Text(
                text = valueText,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                color = OnboardingInk
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Surface(
                    onClick = onMinus,
                    shape = CircleShape,
                    color = SoftSurfaceTint,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease $label",
                            tint = OnboardingInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Surface(
                    onClick = onPlus,
                    shape = CircleShape,
                    color = SoftSurfaceTint,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase $label",
                            tint = OnboardingInk,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
