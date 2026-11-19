package com.example.domain

import com.example.data.local.WeeklyAuditEntity
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

enum class MetabolicDirection(
    val id: String,
    val badgeTag: String,
    val shortActionTitle: String,
    val accentColorHex: Long
) {
    CUT_FAT(
        id = "CUT_FAT",
        badgeTag = "REDUCE WEIGHT • STRIP FAT",
        shortActionTitle = "Caloric Deficit + Muscle Protection",
        accentColorHex = 0xFFF59E0B
    ),
    BODY_RECOMP(
        id = "BODY_RECOMP",
        badgeTag = "BODY RECOMPOSITION",
        shortActionTitle = "Hold Scale Weight • Swap Belly Fat for Muscle",
        accentColorHex = 0xFF0EA5E9
    ),
    GAIN_LEAN_MASS(
        id = "GAIN_LEAN_MASS",
        badgeTag = "INCREASE WEIGHT • LEAN MASS",
        shortActionTitle = "Clean Caloric Surplus + Progressive Overload",
        accentColorHex = 0xFF10B981
    )
}

/**
 * Un-sugarcoated biological timeline calculated directly from the user's selected
 * Starting Body -> Target Goal + Weekly Training Days + Age Tier.
 */
data class HonestTimelineEstimate(
    val minWeeks: Int,
    val maxWeeks: Int,
    val weeksRangeLabel: String,
    val monthsRangeLabel: String,
    val firstVisibleMilestone: String,
    val monthThreeFourCheckpoint: String = "",
    val honestTruthHeadline: String,
    val honestTruthBody: String
)

data class CalibrationGapReport(
    val biologicalSex: BiologicalSex = BiologicalSex.MALE,
    val ageTier: AgeTier = AgeTier.UNDER_30,
    val experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE,
    val measurementsKnown: Boolean = true,
    val currentWeightKg: Float,
    val heightCm: Float,
    val age: Int,
    val currentBracket: CurrentBfBracket,
    val goalBracket: GoalBfBracket,
    val currentFatMassKg: Float,
    val leanBodyMassKg: Float,
    val targetBodyweightKg: Float,
    val fatMassToShedKg: Float,
    val estimatedWeeks: Float,
    val estimatedMonths: Float,
    val dailyProteinFloorGrams: Int,
    val perMealProteinBolusGrams: Int,
    val bmrKcal: Int,
    val estimatedTdeeKcal: Int,
    val caloricDeficitCeilingKcal: Int,
    val stagnationAdjustedCeilingKcal: Int,
    val priorityWeakpoints: List<StructuralWeakpoint>,
    val programTitle: String = "4-Day V-Taper Split",
    val nutritionPlanSummary: String = "Recomposition Deficit (2,150 kcal / day)",
    val proteinFloorSummary: String = "180g / day (2.1g/kg)",
    val stepAndActivitySummary: String = "Joint-Safe Cables & Leg Press • 10,000 Steps",
    val timelineRangeSummary: String = "18–24 Weeks of Consistent Execution",
    val shortTimelineLabel: String = "18–24 Weeks",
    val priorityFocusSummary: String = "Incline Press (Clavicular), Lat Pulldown & Lateral Delts",
    val metabolicDirection: MetabolicDirection = MetabolicDirection.CUT_FAT,
    val bmi: Float = 24.5f,
    val weightDeltaKg: Float = -8.0f,
    val muscleMassToGainKg: Float = 0f,
    val calorieAdjustmentDeltaKcal: Int = -450,
    val physiologicalRealityNote: String = "Consistent calorie control and daily protein floor required.",
    val honestTimeline: HonestTimelineEstimate = HonestTimelineEstimate(
        minWeeks = 14,
        maxWeeks = 20,
        weeksRangeLabel = "14–20 Weeks",
        monthsRangeLabel = "3.5–5 Months",
        firstVisibleMilestone = "Weeks 3–4: Better posture, strength & looser waist",
        honestTruthHeadline = "Honest Timeline: 3.5–5 Months",
        honestTruthBody = "Real body change happens at 0.5–0.7 kg of fat loss per week or ~0.8 kg of new muscle per month. Stay consistent 80% of days."
    )
)

data class StagnationAuditResult(
    val isStagnant14Days: Boolean,
    val spanDays: Int,
    val baselineWaistCm: Float?,
    val latestWaistCm: Float?,
    val waistDeltaCm: Float,
    val latestWeightKg: Float?
)

object CalibrationCalculator {

    private const val REALISTIC_WEEKLY_FAT_LOSS_KG = 0.5f
    private const val REALISTIC_WEEKLY_LEAN_GAIN_KG = 0.28f
    private const val WEEKS_PER_MONTH = 4.333f
    private const val ACTIVITY_MULTIPLIER_TACTICAL = 1.48f
    private const val STAGNATION_CUT_KCAL = 150

    /**
     * Estimates internal biological weight (kg) and height (cm) when a user skips exact
     * measurements and chooses their body purely by visual resemblance.
     */
    fun estimateBaselineFromVisualShape(
        startingShapeId: String,
        sex: BiologicalSex
    ): Pair<Float, Float> {
        return if (sex == BiologicalSex.FEMALE) {
            when (startingShapeId) {
                "shape_5_underweight" -> 49f to 164f
                "shape_3_skinny_fat" -> 58f to 164f
                "shape_4_average_untrained" -> 62f to 164f
                "shape_7_short_stocky" -> 76f to 156f
                "shape_2_soft_midsection" -> 76f to 164f
                "shape_1_high_bf", "shape_6_very_obese" -> 96f to 164f
                else -> 64f to 164f
            }
        } else {
            when (startingShapeId) {
                "shape_5_underweight" -> 58f to 176f
                "shape_3_skinny_fat" -> 71f to 176f
                "shape_4_average_untrained" -> 76f to 176f
                "shape_7_short_stocky" -> 88f to 166f
                "shape_2_soft_midsection" -> 89f to 176f
                "shape_1_high_bf", "shape_6_very_obese" -> 114f to 176f
                else -> 82f to 176f
            }
        }
    }

    /**
     * Computes an honest, zero-sugarcoating biological timeline from ANY Starting Body to ANY Target Goal.
     */
    fun computeHonestTimeline(
        startingShapeId: String,
        goalCardId: String,
        daysPerWeek: Int = 5,
        ageTier: AgeTier = AgeTier.UNDER_30,
        weightKg: Float? = null,
        heightCm: Float? = null,
        measurementsKnown: Boolean = false
    ): HonestTimelineEstimate {
        val basePairWeeks: Pair<Int, Int> = when (startingShapeId) {
            "shape_1_high_bf", "shape_6_very_obese" -> when (goalCardId) {
                "goal_1_fat_loss" -> 34 to 48
                "goal_5_recomp" -> 36 to 50
                "goal_2_vtaper" -> 38 to 52
                "goal_3_athletic" -> 42 to 58
                "goal_4_power_mass" -> 32 to 46
                else -> 36 to 50
            }
            "shape_7_short_stocky" -> when (goalCardId) {
                "goal_1_fat_loss" -> 18 to 26
                "goal_5_recomp" -> 20 to 28
                "goal_2_vtaper" -> 24 to 34
                "goal_3_athletic" -> 28 to 38
                "goal_4_power_mass" -> 20 to 28
                else -> 20 to 28
            }
            "shape_2_soft_midsection" -> when (goalCardId) {
                "goal_1_fat_loss" -> 20 to 28
                "goal_5_recomp" -> 22 to 30
                "goal_2_vtaper" -> 24 to 34
                "goal_3_athletic" -> 26 to 38
                "goal_4_power_mass" -> 18 to 26
                else -> 20 to 28
            }
            "shape_3_skinny_fat" -> when (goalCardId) {
                "goal_1_fat_loss" -> 10 to 14
                "goal_5_recomp" -> 12 to 18
                "goal_2_vtaper" -> 16 to 24
                "goal_3_athletic" -> 16 to 22
                "goal_4_power_mass" -> 22 to 32
                else -> 14 to 20
            }
            "shape_4_average_untrained" -> when (goalCardId) {
                "goal_1_fat_loss" -> 8 to 12
                "goal_5_recomp" -> 10 to 16
                "goal_2_vtaper" -> 14 to 20
                "goal_3_athletic" -> 14 to 20
                "goal_4_power_mass" -> 20 to 28
                else -> 12 to 18
            }
            "shape_5_underweight" -> when (goalCardId) {
                "goal_1_fat_loss" -> 8 to 12
                "goal_5_recomp" -> 12 to 18
                "goal_2_vtaper" -> 18 to 26
                "goal_3_athletic" -> 16 to 24
                "goal_4_power_mass" -> 26 to 38
                else -> 18 to 26
            }
            else -> 14 to 20
        }

        val freqFactor = when (daysPerWeek) {
            3 -> 1.18f
            4 -> 1.06f
            5 -> 1.00f
            else -> 0.96f
        }
        val ageFactor = when (ageTier) {
            AgeTier.UNDER_30 -> 1.00f
            AgeTier.AGE_30_45 -> 1.06f
            AgeTier.AGE_45_PLUS -> 1.14f
        }

        val bmiExtraWeeks = if (measurementsKnown && weightKg != null && heightCm != null) {
            val hM = (heightCm / 100f).coerceAtLeast(1.3f)
            val bmi = weightKg / (hM * hM)
            if (bmi >= 36f && goalCardId != "goal_4_power_mass") {
                ((bmi - 34f) * 1.6f).roundToInt().coerceIn(0, 16)
            } else {
                0
            }
        } else {
            0
        }

        val minW = ((basePairWeeks.first * freqFactor * ageFactor).roundToInt() + bmiExtraWeeks).coerceAtLeast(8)
        val maxW = ((basePairWeeks.second * freqFactor * ageFactor).roundToInt() + bmiExtraWeeks).coerceAtLeast(minW + 4)

        val minMonths = (minW / WEEKS_PER_MONTH).coerceAtLeast(1.8f)
        val maxMonths = (maxW / WEEKS_PER_MONTH).coerceAtLeast(minMonths + 0.8f)

        val monthsRange = if (minMonths >= 6f) {
            "${minMonths.roundToInt()}–${maxMonths.roundToInt()} Months"
        } else {
            val m1 = ((minMonths * 2f).roundToInt() / 2f)
            val m2 = ((maxMonths * 2f).roundToInt() / 2f)
            val fmt1 = if (m1 % 1f == 0f) "${m1.roundToInt()}" else "$m1"
            val fmt2 = if (m2 % 1f == 0f) "${m2.roundToInt()}" else "$m2"
            "$fmt1–$fmt2 Months"
        }

        val weeksRange = "$minW–$maxW Weeks"

        val month34Checkpoint = when {
            startingShapeId in setOf("shape_1_high_bf", "shape_6_very_obese") ->
                "Months 3–4 Checkpoint: –8 to 12 kg fat lost & 1–2 waist sizes down (major visible progress, while full slim goal takes $monthsRange)."
            startingShapeId in setOf("shape_2_soft_midsection", "shape_7_short_stocky") ->
                "Months 3–4 Checkpoint: –6 to 9 kg belly fat stripped, visibly flatter stomach & stronger muscle lines."
            goalCardId == "goal_4_power_mass" ->
                "Months 3–4 Checkpoint: +2.5 to 3.5 kg new lean muscle across shoulders, chest/glutes & arms."
            else ->
                "Months 3–4 Checkpoint: Crisp waist definition, tall posture & full athletic conditioning."
        }

        val (headline, truthBody, firstMilestone) = when {
            startingShapeId == "shape_5_underweight" && goalCardId == "goal_1_fat_loss" -> Triple(
                "Honest Truth: You Need Muscle Tone, Not Weight Loss ($monthsRange)",
                "Your frame is already very lean. Cutting calories will only make you tired and stringy. We keep calories steady and protein high so your waist stays flat while building firm muscle.",
                "Weeks 3–4: Noticeable strength & fuller posture"
            )

            startingShapeId in setOf("shape_1_high_bf", "shape_6_very_obese") -> Triple(
                "No Sugarcoating: Full Goal Takes $monthsRange ($weeksRange)",
                "Going from Very Obese to truly slim safely happens at 0.6–0.9 kg per week so your skin, joints, and muscle stay healthy. You will NOT finish in 3–4 months—by Month 4 you'll drop 8–12 kg, and full transformation arrives in $monthsRange.",
                "Weeks 3–4: Lighter joints, higher stamina & first 2–3 kg waist drop"
            )

            startingShapeId == "shape_7_short_stocky" -> Triple(
                "Honest Timeline: $monthsRange ($weeksRange)",
                "On a shorter or compact frame, every 2–3 kg of belly fat lost shows visibly fast, though daily calorie margin is tighter. Expect tighter shirts and waist drop within 4 weeks, and full change in $monthsRange.",
                "Weeks 3–4: Flatter upper stomach & noticeably tighter fit"
            )

            startingShapeId == "shape_2_soft_midsection" -> Triple(
                "Honest Fat-Loss Timeline: $monthsRange ($weeksRange)",
                "Losing a full round belly without rebound starvation takes steady 0.5–0.8 kg/week progress. Expect visible waist reduction by Week 4, major shirt-size change at Month 3, and full lean finish in $monthsRange.",
                "Weeks 3–4: Reduced stomach bloat, looser belt notch & stronger lifts"
            )

            goalCardId == "goal_4_power_mass" -> Triple(
                "Honest Muscle Timeline: $monthsRange ($weeksRange)",
                "Natural muscle tissue grows at roughly 0.7–1.0 kg per month with progressive lifting and daily protein. Strength jumps fast in the first 3 weeks; thick visual size takes $monthsRange.",
                "Weeks 3–4: +15–25% lifting strength & fuller shoulders/arms"
            )

            goalCardId == "goal_3_athletic" -> Triple(
                "Honest Lean & Abs Timeline: $monthsRange ($weeksRange)",
                "Visible abs require stripping lower-stomach fat slowly while lifting to hold shoulder and chest muscle. No crash starving—steady execution delivers your goal in $monthsRange.",
                "Weeks 3–4: Upper stomach tightens & lifts feel crisp"
            )

            else -> Triple(
                "Realistic Timeline: $monthsRange ($weeksRange)",
                "Consistent workouts ($daysPerWeek days/week) plus hitting your daily protein floor reshapes your waist and muscle tone steadily without burnout.",
                "Weeks 3–4: Looser waist, better posture & steady habit"
            )
        }

        return HonestTimelineEstimate(
            minWeeks = minW,
            maxWeeks = maxW,
            weeksRangeLabel = weeksRange,
            monthsRangeLabel = monthsRange,
            firstVisibleMilestone = firstMilestone,
            monthThreeFourCheckpoint = month34Checkpoint,
            honestTruthHeadline = headline,
            honestTruthBody = truthBody
        )
    }

    fun computeGapReport(
        weightKg: Float,
        heightCm: Float,
        age: Int,
        currentBracket: CurrentBfBracket,
        goalBracket: GoalBfBracket,
        selectedWeakpointIds: Set<String>,
        biologicalSex: BiologicalSex = currentBracket.sex,
        ageTier: AgeTier = AgeTier.UNDER_30,
        experienceLevel: ExperienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE,
        startingShapeId: String? = null,
        goalCardId: String? = null,
        measurementsKnown: Boolean = true,
        daysPerWeek: Int = 5
    ): CalibrationGapReport {
        val safeWeight = weightKg.coerceIn(40f, 220f)
        val safeHeight = heightCm.coerceIn(130f, 230f)
        val effectiveAge = age.coerceIn(16, 90)
        val heightMeters = safeHeight / 100f
        val bmi = safeWeight / (heightMeters * heightMeters)

        val currentBfRatio = currentBracket.bodyFatPercent / 100f
        val goalBfRatio = goalBracket.bodyFatPercent / 100f

        val currentFatMassKg = safeWeight * currentBfRatio
        val leanBodyMassKg = safeWeight - currentFatMassKg

        val resolvedStartId = startingShapeId ?: when (currentBracket) {
            CurrentBfBracket.BF_30_PLUS, CurrentBfBracket.FEMALE_BF_32_PLUS -> "shape_1_high_bf"
            CurrentBfBracket.BF_24_28, CurrentBfBracket.FEMALE_BF_25_29 -> "shape_2_soft_midsection"
            CurrentBfBracket.BF_15, CurrentBfBracket.FEMALE_BF_LEAN_BASE -> "shape_5_underweight"
            else -> "shape_3_skinny_fat"
        }
        val resolvedGoalId = goalCardId ?: when (goalBracket) {
            GoalBfBracket.GOAL_LEAN_FLAT, GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS -> "goal_1_fat_loss"
            GoalBfBracket.GOAL_MASS_SIZE, GoalBfBracket.FEMALE_GOAL_HOURGLASS -> "goal_4_power_mass"
            else -> "goal_2_vtaper"
        }

        val isUnderweightStart = currentBracket == CurrentBfBracket.BF_15 ||
            currentBracket == CurrentBfBracket.FEMALE_BF_LEAN_BASE ||
            resolvedStartId == "shape_5_underweight" ||
            (measurementsKnown && bmi < 19.2f)

        val isMassGoal = goalBracket == GoalBfBracket.GOAL_MASS_SIZE ||
            goalBracket == GoalBfBracket.FEMALE_GOAL_HOURGLASS ||
            resolvedGoalId == "goal_4_power_mass"

        val isExplicitFatLossGoal = goalBracket == GoalBfBracket.GOAL_LEAN_FLAT ||
            goalBracket == GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS ||
            resolvedGoalId == "goal_1_fat_loss"

        val isHighAdiposeStart = currentBracket == CurrentBfBracket.BF_30_PLUS ||
            currentBracket == CurrentBfBracket.BF_24_28 ||
            currentBracket == CurrentBfBracket.FEMALE_BF_32_PLUS ||
            currentBracket == CurrentBfBracket.FEMALE_BF_25_29 ||
            resolvedStartId in setOf("shape_1_high_bf", "shape_6_very_obese", "shape_7_short_stocky", "shape_2_soft_midsection") ||
            (measurementsKnown && !isMassGoal && bmi >= 25.0f) ||
            (measurementsKnown && bmi >= 30.0f)

        val metabolicDirection = when {
            isUnderweightStart && isExplicitFatLossGoal -> MetabolicDirection.BODY_RECOMP
            isUnderweightStart -> MetabolicDirection.GAIN_LEAN_MASS
            isMassGoal && !isHighAdiposeStart -> MetabolicDirection.GAIN_LEAN_MASS
            isHighAdiposeStart || isExplicitFatLossGoal -> MetabolicDirection.CUT_FAT
            else -> MetabolicDirection.BODY_RECOMP
        }

        val sexOffset = if (biologicalSex == BiologicalSex.FEMALE) -161f else 5f
        val rawBmr = (10f * safeWeight) + (6.25f * safeHeight) - (5f * effectiveAge) + sexOffset
        val bmr = (rawBmr * ageTier.metabolicFactor).roundToInt()
        val tdee = (bmr * ACTIVITY_MULTIPLIER_TACTICAL).roundToInt()
        val minCalorieFloor = if (biologicalSex == BiologicalSex.FEMALE) 1300 else 1550

        val targetBodyweightKg: Float
        val fatMassToShedKg: Float
        val muscleMassToGainKg: Float
        val calorieAdjustmentDeltaKcal: Int
        val caloricTargetKcal: Int
        val dailyProteinFloorGrams: Int

        when (metabolicDirection) {
            MetabolicDirection.GAIN_LEAN_MASS -> {
                val idealAthleticBmi = if (biologicalSex == BiologicalSex.FEMALE) {
                    if (isMassGoal) 21.8f else 20.8f
                } else {
                    if (isMassGoal) 23.8f else 22.6f
                }
                val bmiTargetWeight = idealAthleticBmi * heightMeters * heightMeters
                targetBodyweightKg = max(safeWeight + 4.5f, bmiTargetWeight).coerceAtMost(safeWeight + 14.0f)
                fatMassToShedKg = 0f
                muscleMassToGainKg = (targetBodyweightKg - safeWeight).coerceAtLeast(4.0f)

                calorieAdjustmentDeltaKcal = if (biologicalSex == BiologicalSex.FEMALE) +250 else +300
                caloricTargetKcal = tdee + calorieAdjustmentDeltaKcal

                val proteinMult = if (biologicalSex == BiologicalSex.FEMALE) 1.95f else 2.15f
                dailyProteinFloorGrams = (targetBodyweightKg * proteinMult).roundToInt()
                    .coerceIn(if (biologicalSex == BiologicalSex.FEMALE) 115 else 145, 240)
            }

            MetabolicDirection.BODY_RECOMP -> {
                val slightTrim = if (bmi > 22.5f) 2.2f else 1.0f
                targetBodyweightKg = (safeWeight - slightTrim).coerceAtLeast(45f)
                fatMassToShedKg = (safeWeight * 0.06f).coerceIn(3.5f, 6.5f)
                muscleMassToGainKg = (fatMassToShedKg - slightTrim).coerceAtLeast(2.5f)

                calorieAdjustmentDeltaKcal = -250
                caloricTargetKcal = max(minCalorieFloor, tdee + calorieAdjustmentDeltaKcal)

                val proteinMult = if (biologicalSex == BiologicalSex.FEMALE) 2.0f else 2.2f
                dailyProteinFloorGrams = (safeWeight * proteinMult).roundToInt()
                    .coerceIn(if (biologicalSex == BiologicalSex.FEMALE) 120 else 155, 245)
            }

            MetabolicDirection.CUT_FAT -> {
                val rawTargetWeightKg = leanBodyMassKg / (1f - goalBfRatio)
                val healthyFloorWeight = (if (biologicalSex == BiologicalSex.FEMALE) 19.5f else 21.0f) *
                    heightMeters * heightMeters
                targetBodyweightKg = rawTargetWeightKg
                    .coerceAtLeast(healthyFloorWeight)
                    .coerceAtMost((safeWeight - 2.0f).coerceAtLeast(45f))

                fatMassToShedKg = max(2.0f, safeWeight - targetBodyweightKg)
                muscleMassToGainKg = 1.5f

                calorieAdjustmentDeltaKcal = if (biologicalSex == BiologicalSex.FEMALE) -400 else -450
                caloricTargetKcal = max(minCalorieFloor, tdee + calorieAdjustmentDeltaKcal)

                val referenceMass = max(targetBodyweightKg, leanBodyMassKg * 1.15f)
                val proteinMult = if (biologicalSex == BiologicalSex.FEMALE) 1.95f else 2.2f
                dailyProteinFloorGrams = (referenceMass * proteinMult).roundToInt()
                    .coerceIn(if (biologicalSex == BiologicalSex.FEMALE) 120 else 160, 250)
            }
        }

        val honestTimeline = computeHonestTimeline(
            startingShapeId = resolvedStartId,
            goalCardId = resolvedGoalId,
            daysPerWeek = daysPerWeek,
            ageTier = ageTier,
            weightKg = safeWeight,
            heightCm = safeHeight,
            measurementsKnown = measurementsKnown
        )

        val estimatedWeeks = ((honestTimeline.minWeeks + honestTimeline.maxWeeks) / 2f).coerceAtLeast(8f)
        val estimatedMonths = estimatedWeeks / WEEKS_PER_MONTH
        val weightDeltaKg = targetBodyweightKg - safeWeight

        val isJointSafe = ageTier == AgeTier.AGE_45_PLUS || experienceLevel.isBeginnerSafe
        val jointModeText = if (isJointSafe) {
            "Joint-Safe Cables & Controlled Tempo"
        } else {
            "Progressive Dumbbell & Cable Hypertrophy"
        }

        val programTitle = when (metabolicDirection) {
            MetabolicDirection.GAIN_LEAN_MASS -> "Lean Mass Hypertrophy & V-Taper Builder"
            MetabolicDirection.BODY_RECOMP -> "4-Day Upper/Lower Recomp Split"
            MetabolicDirection.CUT_FAT -> "4-Day Upper/Lower Fat-Strip Split"
        }

        val signedDeltaText = if (calorieAdjustmentDeltaKcal >= 0) {
            "+$calorieAdjustmentDeltaKcal kcal Clean Surplus"
        } else {
            "$calorieAdjustmentDeltaKcal kcal Deficit"
        }
        val nutritionPlanSummary = "$caloricTargetKcal kcal / day ($signedDeltaText)"
        val proteinFloorSummary = "${dailyProteinFloorGrams}g / day (4 Anchors • Zero Junk)"
        val stepAndActivitySummary = when {
            metabolicDirection == MetabolicDirection.GAIN_LEAN_MASS ->
                "7,500–8,500 Steps + $jointModeText"
            isExplicitFatLossGoal ->
                "8,000–10,000 Daily Steps + $jointModeText"
            else ->
                "10,000 Daily Steps + $jointModeText"
        }

        val timelineRangeSummary = "${honestTimeline.weeksRangeLabel} (${honestTimeline.monthsRangeLabel})"

        val priorityFocusSummary = when (goalBracket) {
            GoalBfBracket.GOAL_LEAN_FLAT,
            GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS ->
                "Midsection Fat Strip, Tight Waistline & Core Control"
            GoalBfBracket.GOAL_MASS_SIZE,
            GoalBfBracket.FEMALE_GOAL_HOURGLASS ->
                "Dense Chest/Glute Armor, Thick Back & Capped Shoulders"
            else ->
                "Capped Shoulders, Wide Lat Sweep & Flat Athletic Core"
        }

        val physiologicalRealityNote = honestTimeline.honestTruthBody

        val perMealProteinBolusGrams = (dailyProteinFloorGrams / 4.0f).roundToInt()
        val stagnationAdjustedCeiling = max(minCalorieFloor - 100, caloricTargetKcal - STAGNATION_CUT_KCAL)

        return CalibrationGapReport(
            biologicalSex = biologicalSex,
            ageTier = ageTier,
            experienceLevel = experienceLevel,
            measurementsKnown = measurementsKnown,
            currentWeightKg = safeWeight,
            heightCm = safeHeight,
            age = effectiveAge,
            currentBracket = currentBracket,
            goalBracket = goalBracket,
            currentFatMassKg = currentFatMassKg,
            leanBodyMassKg = leanBodyMassKg,
            targetBodyweightKg = targetBodyweightKg,
            fatMassToShedKg = fatMassToShedKg,
            estimatedWeeks = estimatedWeeks,
            estimatedMonths = estimatedMonths,
            dailyProteinFloorGrams = dailyProteinFloorGrams,
            perMealProteinBolusGrams = perMealProteinBolusGrams,
            bmrKcal = bmr,
            estimatedTdeeKcal = tdee,
            caloricDeficitCeilingKcal = caloricTargetKcal,
            stagnationAdjustedCeilingKcal = stagnationAdjustedCeiling,
            priorityWeakpoints = StructuralWeakpoint.fromIds(selectedWeakpointIds),
            programTitle = programTitle,
            nutritionPlanSummary = nutritionPlanSummary,
            proteinFloorSummary = proteinFloorSummary,
            stepAndActivitySummary = stepAndActivitySummary,
            timelineRangeSummary = timelineRangeSummary,
            shortTimelineLabel = "${honestTimeline.minWeeks}–${honestTimeline.maxWeeks}",
            priorityFocusSummary = priorityFocusSummary,
            metabolicDirection = metabolicDirection,
            bmi = bmi,
            weightDeltaKg = weightDeltaKg,
            muscleMassToGainKg = muscleMassToGainKg,
            calorieAdjustmentDeltaKcal = calorieAdjustmentDeltaKcal,
            physiologicalRealityNote = physiologicalRealityNote,
            honestTimeline = honestTimeline
        )
    }

    fun evaluateStagnation(logsDesc: List<WeeklyAuditEntity>): StagnationAuditResult {
        if (logsDesc.isEmpty()) {
            return StagnationAuditResult(
                isStagnant14Days = false,
                spanDays = 0,
                baselineWaistCm = null,
                latestWaistCm = null,
                waistDeltaCm = 0f,
                latestWeightKg = null
            )
        }

        val sortedAsc = logsDesc.sortedBy { it.recordedEpochMs }
        val latest = sortedAsc.last()

        if (sortedAsc.size < 2) {
            return StagnationAuditResult(
                isStagnant14Days = false,
                spanDays = 1,
                baselineWaistCm = latest.navelCircumferenceCm,
                latestWaistCm = latest.navelCircumferenceCm,
                waistDeltaCm = 0f,
                latestWeightKg = latest.morningDryWeightKg
            )
        }

        val fourteenDaysMs = 14L * 24L * 60L * 60L * 1000L
        val cutoffEpochMs = latest.recordedEpochMs - fourteenDaysMs

        val anchorAtLeast14DaysAgo = sortedAsc.lastOrNull { it.recordedEpochMs <= cutoffEpochMs }
            ?: sortedAsc.first()

        val spanMs = latest.recordedEpochMs - anchorAtLeast14DaysAgo.recordedEpochMs
        val spanDays = (spanMs / (24L * 60L * 60L * 1000L)).toInt()

        val waistDeltaCm = latest.navelCircumferenceCm - anchorAtLeast14DaysAgo.navelCircumferenceCm
        val logsInWindow = sortedAsc.filter { it.recordedEpochMs >= anchorAtLeast14DaysAgo.recordedEpochMs }
        val minWaistAfterAnchor = logsInWindow.drop(1).minOfOrNull { it.navelCircumferenceCm }
            ?: latest.navelCircumferenceCm

        val isStagnant = spanDays >= 14 && (latest.navelCircumferenceCm >= anchorAtLeast14DaysAgo.navelCircumferenceCm - 0.05f) &&
            (minWaistAfterAnchor >= anchorAtLeast14DaysAgo.navelCircumferenceCm - 0.05f)

        return StagnationAuditResult(
            isStagnant14Days = isStagnant,
            spanDays = spanDays,
            baselineWaistCm = anchorAtLeast14DaysAgo.navelCircumferenceCm,
            latestWaistCm = latest.navelCircumferenceCm,
            waistDeltaCm = waistDeltaCm,
            latestWeightKg = latest.morningDryWeightKg
        )
    }
}
