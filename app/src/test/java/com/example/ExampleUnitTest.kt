package com.example

import com.example.data.local.WeeklyAuditEntity
import com.example.domain.CalibrationCalculator
import com.example.domain.CombatSplitCatalog
import com.example.domain.CurrentBfBracket
import com.example.domain.ExperienceLevel
import com.example.domain.FuelAndRestraintCatalog
import com.example.domain.GoalBfBracket
import com.example.domain.HomeWorkoutCatalog
import com.example.domain.MetabolicDirection
import com.example.domain.ProgressiveLoadEngine
import com.example.domain.SpecialtyTrackCategory
import com.example.domain.SupplementPreference
import com.example.domain.WeeklySchedulePlanner
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.stringsForLanguage
import com.example.ui.screens.SmartBodyMatcher
import com.example.ui.screens.buildFallbackVariantsFor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun calibrationCalculator_routesWeightDirectionAndUncappedProteinDynamically() {
        val athleticCutReport = CalibrationCalculator.computeGapReport(
            weightKg = 92.0f,
            heightCm = 180.0f,
            age = 30,
            currentBracket = CurrentBfBracket.BF_24_28,
            goalBracket = GoalBfBracket.GOAL_10_12,
            selectedWeakpointIds = emptySet()
        )
        assertEquals(MetabolicDirection.CUT_FAT, athleticCutReport.metabolicDirection)
        assertTrue(athleticCutReport.targetBodyweightKg < athleticCutReport.currentWeightKg)
        assertTrue(athleticCutReport.dailyProteinFloorGrams >= 170)
        assertEquals(athleticCutReport.estimatedTdeeKcal - 450, athleticCutReport.caloricDeficitCeilingKcal)

        val thinGainReport = CalibrationCalculator.computeGapReport(
            weightKg = 57.0f,
            heightCm = 178.0f,
            age = 24,
            currentBracket = CurrentBfBracket.BF_15,
            goalBracket = GoalBfBracket.GOAL_10_12,
            selectedWeakpointIds = emptySet()
        )
        assertEquals(MetabolicDirection.GAIN_LEAN_MASS, thinGainReport.metabolicDirection)
        assertTrue(thinGainReport.targetBodyweightKg > thinGainReport.currentWeightKg + 4.0f)
        assertEquals(thinGainReport.estimatedTdeeKcal + 300, thinGainReport.caloricDeficitCeilingKcal)

        val recompReport = CalibrationCalculator.computeGapReport(
            weightKg = 74.0f,
            heightCm = 178.0f,
            age = 27,
            currentBracket = CurrentBfBracket.BF_18_22,
            goalBracket = GoalBfBracket.GOAL_10_12,
            selectedWeakpointIds = emptySet()
        )
        assertEquals(MetabolicDirection.BODY_RECOMP, recompReport.metabolicDirection)
        assertEquals(recompReport.estimatedTdeeKcal - 250, recompReport.caloricDeficitCeilingKcal)
    }

    @Test
    fun smartBodyMatcher_exposesAllSixRealisticStartingBodiesAndFiveUnbiasedDirectGoals() {
        // All 6 realistic starting builds (including Short / Stocky Belly and Very Obese) are openly shown
        val startIds = SmartBodyMatcher.ALL_STARTING_BODIES.map { it.id }
        assertEquals(6, startIds.size)
        assertTrue(startIds.contains("shape_7_short_stocky"))
        assertTrue(startIds.contains("shape_1_high_bf"))
        assertEquals(6, SmartBodyMatcher.relevantStartingBodiesFor(70f, 178f, "shape_3_skinny_fat").size)

        // All 4 distinct target goals map 1-to-1 to the 4 unique target figures (0..3) with ZERO duplicate images or bias badges
        SmartBodyMatcher.ALL_STARTING_BODIES.forEach { start ->
            val targets = SmartBodyMatcher.targetBodiesForStartingShape(start.id)
            assertEquals(4, targets.size)
            assertEquals(
                listOf("goal_1_fat_loss", "goal_3_athletic", "goal_2_vtaper", "goal_4_power_mass"),
                targets.map { it.id }
            )
            assertEquals(
                4,
                targets.map { com.example.ui.components.PhysiqueSilhouettePreset.fromCardId(it.id).figureIndex }.toSet().size
            )
            assertEquals(4, targets.map { it.titleMale }.toSet().size)
            assertEquals(4, targets.map { it.titleFemale }.toSet().size)
            assertEquals(4, targets.map { it.musclesMale }.toSet().size)
            targets.forEach { goal ->
                assertEquals("Zero Recommended badges allowed on goals", "", goal.badgeText)
            }
        }

        // Short stocky helper check
        assertEquals("shape_7_short_stocky", SmartBodyMatcher.recommendStartingShapeId(weightKg = 76f, heightCm = 162f))
    }

    @Test
    fun workoutCoach_prescribesExactDecisiveRepsWithoutRanges() {
        val day1Exercises = CombatSplitCatalog.FOUR_DAY_SPLIT[0].exercises
        day1Exercises.forEachIndexed { idx, spec ->
            val level1Variants = buildFallbackVariantsFor(
                spec = spec,
                index = idx,
                dayIndex = 0,
                experienceLevel = ExperienceLevel.BEGINNER_JOINT_SAFE
            )
            val level2Variants = buildFallbackVariantsFor(
                spec = spec,
                index = idx,
                dayIndex = 0,
                experienceLevel = ExperienceLevel.INTERMEDIATE
            )
            (level1Variants + level2Variants).forEach { v ->
                assertFalse("No indecisive dash ranges allowed: ${v.setsAndRepsLabel}", v.setsAndRepsLabel.contains("–"))
                assertFalse("No indecisive hyphen ranges allowed: ${v.repPrescription}", v.repPrescription.contains("-"))
                assertTrue(v.cues.size >= 3)
                assertTrue(v.breathingPattern.isNotBlank())
                assertTrue(v.commonMistake.isNotBlank())
            }
        }
    }

    @Test
    fun fuelAndRestraintCatalog_scales200gMealPropositionAndFoodTable() {
        val mealPlanWithWhey = FuelAndRestraintCatalog.buildDailyMealProposition(
            targetProteinGrams = 200,
            targetCaloriesKcal = 2350,
            direction = MetabolicDirection.BODY_RECOMP,
            supplementPref = SupplementPreference.WITH_SUPPLEMENTS
        )
        assertEquals(4, mealPlanWithWhey.size)
        assertEquals(200, mealPlanWithWhey.sumOf { it.proteinGrams })
    }

    @Test
    fun stagnationDetector_flagsFourteenDaysWithoutWaistDrop() {
        val dayMs = 24L * 60L * 60L * 1000L
        val now = 1_750_000_000_000L
        val stalledLogs = listOf(
            WeeklyAuditEntity(1, "2026-10-25", now - 15 * dayMs, 88.5f, 92.0f),
            WeeklyAuditEntity(2, "2026-11-01", now - 7 * dayMs, 88.3f, 92.1f),
            WeeklyAuditEntity(3, "2026-11-09", now, 88.2f, 92.0f)
        )
        assertTrue(CalibrationCalculator.evaluateStagnation(stalledLogs).isStagnant14Days)
    }

    @Test
    fun foodIntelligenceCatalog_mapsValidStudioPhotosAndCalculatesHealthyFatCarbSwap() {
        val entries = com.example.domain.FoodIntelligenceCatalog.FOOD_ENCYCLOPEDIA
        assertTrue(entries.size >= 16)
        entries.forEach { entry ->
            assertTrue(entry.photoGridIndex in 0..2)
            assertTrue(entry.photoQuadrant in 0..3)
        }

        val scenarios = com.example.domain.FoodIntelligenceCatalog.HIGH_FAT_MANAGEMENT_SCENARIOS
        assertEquals(4, scenarios.size)
        assertEquals(63, scenarios.first().carbGramsToTrim)
        assertEquals(5, com.example.domain.FoodIntelligenceCatalog.FOOD_COMBINATIONS.size)
        assertEquals(4, com.example.domain.FoodIntelligenceCatalog.INTAKE_MANAGEMENT_RULES.size)
    }

    @Test
    fun physiquePresetsAndBiomechanicalEngine_maintainDistinctMappings() {
        val distinctGoals = listOf("goal_1_fat_loss", "goal_3_athletic", "goal_2_vtaper", "goal_4_power_mass")
            .map { com.example.ui.components.PhysiqueSilhouettePreset.fromCardId(it) }
        assertEquals(4, distinctGoals.toSet().size)
        assertEquals(listOf(0, 1, 2, 3), distinctGoals.map { it.figureIndex })

        val distinctStarts = listOf(
            "shape_1_high_bf",
            "shape_2_soft_midsection",
            "shape_3_skinny_fat",
            "shape_4_average_untrained",
            "shape_5_underweight",
            "shape_7_short_stocky"
        ).map { com.example.ui.components.PhysiqueSilhouettePreset.fromCardId(it) }
        assertEquals(6, distinctStarts.toSet().size)
    }

    @Test
    fun exerciseIllustrationAtlas_coversAll17MovementsWithValidCropBounds() {
        com.example.domain.ExerciseAnimationType.entries.forEach { type ->
            val spec = com.example.ui.components.ExerciseIllustrationAtlas.resolveSpec(type)
            assertTrue("Invalid start X for $type", spec.startPose.minX >= 0f && spec.startPose.maxX <= 1f && spec.startPose.minX < spec.startPose.maxX)
            assertTrue("Invalid start Y for $type", spec.startPose.minY >= 0f && spec.startPose.maxY <= 1f && spec.startPose.minY < spec.startPose.maxY)
            assertTrue("Invalid peak X for $type", spec.peakPose.minX >= 0f && spec.peakPose.maxX <= 1f && spec.peakPose.minX < spec.peakPose.maxX)
            assertTrue("Invalid peak Y for $type", spec.peakPose.minY >= 0f && spec.peakPose.maxY <= 1f && spec.peakPose.minY < spec.peakPose.maxY)
            assertTrue(spec.defaultStartCue.isNotBlank())
            assertTrue(spec.defaultPeakCue.isNotBlank())
        }
    }

    @Test
    fun multiDaySplitAdvisor_calibrates3To6DaysWithWarmupPostStretchAndTruths() {
        val frequencies = com.example.domain.WeeklyFrequency.entries
        assertEquals(listOf(3, 4, 5, 6), frequencies.map { it.daysCount })

        com.example.domain.BiologicalSex.entries.forEach { sex ->
            frequencies.forEach { freq ->
                val split = CombatSplitCatalog.getSplitForProfile(
                    sex = sex,
                    ageTier = com.example.domain.AgeTier.UNDER_30,
                    experience = ExperienceLevel.INTERMEDIATE,
                    frequency = freq
                )
                assertEquals("Expected ${freq.daysCount} days for $sex / $freq", freq.daysCount, split.size)

                split.forEachIndexed { dayIdx, day ->
                    assertEquals(dayIdx, day.dayIndex)
                    assertTrue(day.title.isNotBlank())
                    assertEquals(5, day.exercises.size)

                    day.exercises.forEach { ex ->
                        assertFalse("No range allowed in ${ex.repPrescription}", ex.repPrescription.contains("–") || ex.repPrescription.contains("-"))
                    }

                    val warmups = com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(day)
                    val stretches = com.example.domain.CoachTruthAndSplitAdvisor.getPostWorkoutStretchSpecs(day)
                    assertEquals(2, warmups.size)
                    assertEquals(2, stretches.size)
                }
            }
        }

        val fiveDayMale = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        assertEquals("Chest & Triceps", fiveDayMale[0].title)
        assertEquals("Back & Biceps", fiveDayMale[1].title)
        assertEquals("Legs & Stomach", fiveDayMale[2].title)

        val truths = com.example.domain.CoachTruthAndSplitAdvisor.REALITY_TRUTHS
        assertEquals(6, truths.size)
        truths.forEach {
            assertTrue(it.headline.isNotBlank())
            assertTrue(it.realityBody.isNotBlank())
            assertTrue(it.actionRule.isNotBlank())
        }

        val recompAdvice = com.example.domain.CoachTruthAndSplitAdvisor.adviseBestFrequency(
            direction = MetabolicDirection.BODY_RECOMP,
            experience = ExperienceLevel.INTERMEDIATE,
            selectedFrequency = com.example.domain.WeeklyFrequency.THREE_DAYS
        )
        assertEquals(com.example.domain.WeeklyFrequency.FIVE_DAYS, recompAdvice.recommendedFrequency)

        assertEquals(1, HomeWorkoutCatalog.QUICK_STRIKE_WORKOUTS.size)
    }

    @Test
    fun weeklySchedulePlanner_mapsMonToSunWithCustomFridayAndSundayRestDays() {
        val fiveDaySplit = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        val defaultRest = WeeklySchedulePlanner.defaultRestDaysFor(
            com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        assertEquals(setOf(5, 7), defaultRest)

        val mondayRef = java.time.LocalDate.of(2026, 11, 9)
        val week = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = fiveDaySplit,
            restDaysIso = defaultRest,
            referenceDate = mondayRef
        )
        assertEquals(7, week.size)
        assertFalse(week[0].isRestDay)
        assertFalse(week[1].isRestDay)
        assertFalse(week[2].isRestDay)
        assertFalse(week[3].isRestDay)
        assertTrue(week[4].isRestDay)
        assertFalse(week[5].isRestDay)
        assertTrue(week[6].isRestDay)
        assertEquals(4, week[5].assignedSplitIndex)

        val threeRest = WeeklySchedulePlanner.toggleRestDay(
            currentRestDays = defaultRest,
            dayIso = 3,
            fallbackFrequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        assertEquals(setOf(3, 5, 7), threeRest)
        assertEquals(
            com.example.domain.WeeklyFrequency.FOUR_DAYS,
            WeeklySchedulePlanner.frequencyForRestDays(threeRest)
        )

        // Verify Wide-Grip Lat Pulldown maps to Lats (Back) and never Forearms & Grip!
        assertEquals(
            "Lats (Back)",
            WeeklySchedulePlanner.whatExerciseDealsWith("Wide-Grip Lat Pulldown", "Latissimus Dorsi")
        )

        val specialtyIds = HomeWorkoutCatalog.SPECIALTY_GOAL_WORKOUTS.map { it.id }.toSet()
        assertTrue(specialtyIds.contains("track_grip_power"))
        assertTrue(specialtyIds.contains("track_pelvic_kegel"))
        assertTrue(specialtyIds.contains("track_raw_strength"))

        // Verify 7-Day, 14-Day & 28-Day Challenges are registered in ALL_HUB_CATEGORY_WORKOUTS without rep ranges
        val challengeIds = HomeWorkoutCatalog.CHALLENGE_WORKOUTS.map { it.id }.toSet()
        assertTrue("Expected >= 11 challenges in CHALLENGE_WORKOUTS", challengeIds.size >= 11)
        assertTrue(challengeIds.contains("challenge_7d_core_waist"))
        assertTrue(challengeIds.contains("challenge_7d_pushup_chest"))
        assertTrue(challengeIds.contains("challenge_28d_full_body"))
        assertTrue(challengeIds.contains("challenge_28d_home_lean"))
        assertTrue(
            HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.any { it.category == SpecialtyTrackCategory.CHALLENGES }
        )

        HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.forEach { track ->
            track.exercises.forEach { ex ->
                assertFalse(
                    "Specialty/Challenge setsAndRepsLabel must be decisive without ranges: ${ex.setsAndRepsLabel}",
                    ex.setsAndRepsLabel.contains("–") || ex.setsAndRepsLabel.contains("-")
                )
            }
        }
    }

    @Test
    fun progressiveLoadEngine_tracksFourWeeksExplicitlyAndScalesStandaloneChallenges() {
        // Experience levels reflect intuitive training history ("Never Trained", "Trained Some", "Experienced")
        assertEquals("Never Trained", ExperienceLevel.NEW_BEGINNER.label)
        assertEquals("Trained Some", ExperienceLevel.BEGINNER_JOINT_SAFE.label)
        assertEquals("Experienced", ExperienceLevel.INTERMEDIATE.label)

        // Week 1 -> Week 2 -> Week 3 -> Week 4 progression on 5-day schedule
        val w1Status = ProgressiveLoadEngine.computeFourWeekTrack(completedWorkoutsCount = 0, daysPerWeek = 5)
        val w2Status = ProgressiveLoadEngine.computeFourWeekTrack(completedWorkoutsCount = 5, daysPerWeek = 5)
        val w3Status = ProgressiveLoadEngine.computeFourWeekTrack(completedWorkoutsCount = 10, daysPerWeek = 5)
        val w4Status = ProgressiveLoadEngine.computeFourWeekTrack(completedWorkoutsCount = 15, daysPerWeek = 5)

        assertEquals(1, w1Status.activeViewedWeek)
        assertEquals(0, w1Status.rotationIndex)
        assertEquals(2, w2Status.activeViewedWeek)
        assertEquals(0, w2Status.rotationIndex)
        assertEquals(3, w3Status.activeViewedWeek)
        assertEquals(1, w3Status.rotationIndex) // Rotates to fresh exercise angles in Week 3!
        assertEquals(4, w4Status.activeViewedWeek)
        assertEquals(1, w4Status.rotationIndex)

        val w1Target = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 20f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 67f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 0,
            daysPerWeek = 5,
            trackWeekOverride = 1
        )
        val w2Target = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 20f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 67f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 5,
            daysPerWeek = 5,
            trackWeekOverride = 2
        )
        val w3Target = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 20f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 67f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 10,
            daysPerWeek = 5,
            trackWeekOverride = 3
        )
        val w4Target = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 20f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 67f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 15,
            daysPerWeek = 5,
            trackWeekOverride = 4
        )

        // Never Trained (67 kg) starts at 3 full working sets with beginner-safe ~7 kg dumbbells in Week 1 (never 2-set exercises)
        assertEquals(3, w1Target.sets)
        assertTrue("Expected beginner-safe dumbbell weight <= 9 kg, got ${w1Target.loadKg}", w1Target.loadKg in 4f..9f)
        // Week 2 adds +2 reps per set while maintaining 3 solid sets
        assertEquals(w1Target.repsOrSeconds + 2, w2Target.repsOrSeconds)
        assertEquals(3, w2Target.sets)
        // Week 3 & Week 4 step up load safely and peak reps/sets in Week 4
        assertTrue(w3Target.loadKg >= w1Target.loadKg)
        assertTrue(w4Target.repsOrSeconds > w1Target.repsOrSeconds)
        assertTrue(w4Target.loadKg > w1Target.loadKg)
        assertEquals(4, w4Target.sets)

        // Standalone 7-Day / 28-Day Challenge scaling by user-selected challenge level
        val (firstTimeSets, firstTimeReps) = ProgressiveLoadEngine.scaleStandaloneChallengeMove(
            baseSets = 3,
            baseRepsOrSec = 14,
            isTimedSeconds = false,
            challengeLevel = ExperienceLevel.NEW_BEGINNER
        )
        val (advSets, advReps) = ProgressiveLoadEngine.scaleStandaloneChallengeMove(
            baseSets = 3,
            baseRepsOrSec = 14,
            isTimedSeconds = false,
            challengeLevel = ExperienceLevel.INTERMEDIATE
        )
        assertTrue(firstTimeSets < advSets)
        assertTrue(firstTimeReps < advReps)
    }

    @Test
    fun weeklyExerciseRotationAndRestDayFlows_rotateExercisesWithoutConfusingSchedule() {
        val weeks1To2Split = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS,
            rotationWeek = 0
        )
        val weeks3To4Split = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS,
            rotationWeek = 1
        )
        assertEquals(weeks1To2Split.size, weeks3To4Split.size)
        assertEquals("Chest & Triceps", weeks1To2Split[0].title)
        assertTrue(weeks1To2Split[0].exercises.map { it.name } != weeks3To4Split[0].exercises.map { it.name })
        weeks3To4Split.forEach { day ->
            day.exercises.forEach { ex ->
                assertFalse(ex.repPrescription.contains("–") || ex.repPrescription.contains("-"))
            }
        }

        val weekSlots = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = weeks1To2Split,
            restDaysIso = setOf(5, 7),
            referenceDate = java.time.LocalDate.of(2026, 11, 9)
        )
        assertEquals("Chest", weekSlots[0].shortPillTag)
        assertEquals("Back", weekSlots[1].shortPillTag)
        assertEquals("Legs", weekSlots[2].shortPillTag)
        assertEquals("Rest", weekSlots[4].shortPillTag)
        assertEquals("Rest", weekSlots[6].shortPillTag)

        val fridayFlow = HomeWorkoutCatalog.getRestDayBloodFlowWorkout(dayOfWeekIso = 5, rotationWeek = 0)
        val sundayFlow = HomeWorkoutCatalog.getRestDayBloodFlowWorkout(dayOfWeekIso = 7, rotationWeek = 0)
        assertTrue(fridayFlow.id != sundayFlow.id)
        assertEquals(3, fridayFlow.exercises.size)
        assertEquals(3, sundayFlow.exercises.size)
        assertTrue(fridayFlow.exercises.all { it.totalSets == 1 && it.defaultLoadKg == 0f })
        assertTrue(sundayFlow.exercises.all { it.totalSets == 1 && it.defaultLoadKg == 0f })
    }

    @Test
    fun honestTimelineOptionalMeasurementsAndSettingsDose_workWithoutSugarcoating() {
        // 1. Optional weight/height visual baseline estimation
        val (stockyW, stockyH) = CalibrationCalculator.estimateBaselineFromVisualShape(
            startingShapeId = "shape_7_short_stocky",
            sex = com.example.domain.BiologicalSex.MALE
        )
        val (obeseW, obeseH) = CalibrationCalculator.estimateBaselineFromVisualShape(
            startingShapeId = "shape_1_high_bf",
            sex = com.example.domain.BiologicalSex.MALE
        )
        assertTrue(stockyH < obeseH)
        assertTrue(obeseW > stockyW)

        // 2. Honest un-sugarcoated timeline: Very Obese -> Lean Abs takes meaningfully longer than Average -> Fat Loss
        val obeseToAbs = CalibrationCalculator.computeHonestTimeline(
            startingShapeId = "shape_1_high_bf",
            goalCardId = "goal_3_athletic",
            daysPerWeek = 4,
            weightKg = obeseW,
            heightCm = obeseH,
            measurementsKnown = false
        )
        val avgToFlatWaist = CalibrationCalculator.computeHonestTimeline(
            startingShapeId = "shape_4_average_untrained",
            goalCardId = "goal_1_fat_loss",
            daysPerWeek = 4,
            weightKg = 74f,
            heightCm = 176f,
            measurementsKnown = true
        )
        assertTrue("Very obese to abs must be >= 28 weeks, got ${obeseToAbs.minWeeks}", obeseToAbs.minWeeks >= 28)
        assertTrue(obeseToAbs.minWeeks > avgToFlatWaist.maxWeeks)
        assertTrue(obeseToAbs.monthsRangeLabel.contains("Months"))
        assertTrue(obeseToAbs.honestTruthBody.isNotBlank())

        // 3. Settings Daily Exercise Dose (3, 5, 6 exercises per day)
        listOf(3, 5, 6).forEach { count ->
            val split = CombatSplitCatalog.getSplitForProfile(
                sex = com.example.domain.BiologicalSex.MALE,
                ageTier = com.example.domain.AgeTier.UNDER_30,
                experience = ExperienceLevel.NEW_BEGINNER,
                frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
                exercisesPerDay = count
            )
            split.forEach { day ->
                assertEquals(count, day.exercises.size)
                day.exercises.forEach { ex ->
                    assertFalse(ex.repPrescription.contains("–") || ex.repPrescription.contains("-"))
                }
            }
        }

        // 4. 5-Minute Minimum Habit Rescue ("No-Zero-Day" Rule) + Multi-Language Strings
        val rescue = HomeWorkoutCatalog.LOW_ENERGY_RESCUE_WORKOUT
        assertEquals(5, rescue.durationMinutes)
        assertEquals(3, rescue.exercises.size)
        assertTrue(rescue.exercises.all { it.defaultLoadKg == 0f })

        assertEquals("Settings", stringsForLanguage(AppLanguage.ENGLISH).navSettings)
        assertEquals("Mipangilio", stringsForLanguage(AppLanguage.SWAHILI).navSettings)
        assertEquals("Réglages", stringsForLanguage(AppLanguage.FRENCH).navSettings)
        assertEquals("Ajustes", stringsForLanguage(AppLanguage.SPANISH).navSettings)

        assertEquals("80 kg", com.example.domain.UnitFormatter.formatWeightRounded(80f, useMetric = true))
        assertEquals("176 lb", com.example.domain.UnitFormatter.formatWeightRounded(80f, useMetric = false))
        assertEquals("180 cm", com.example.domain.UnitFormatter.formatHeight(180f, useMetric = true))
        assertEquals("5'11\"", com.example.domain.UnitFormatter.formatHeight(180f, useMetric = false))
    }

    @Test
    fun workoutIntensityAndFitnessMusicTracks_scaleExercisesSetsAndCaloriesAccurately() {
        val simple = com.example.domain.WorkoutIntensityMode.SIMPLE
        val moderate = com.example.domain.WorkoutIntensityMode.MODERATE
        val extensive = com.example.domain.WorkoutIntensityMode.EXTENSIVE

        assertEquals(3, simple.exercisesCount)
        assertEquals(5, moderate.exercisesCount)
        assertEquals(6, extensive.exercisesCount)
        assertEquals(simple, com.example.domain.WorkoutIntensityMode.fromExercisesCount(3))
        assertEquals(moderate, com.example.domain.WorkoutIntensityMode.fromExercisesCount(5))
        assertEquals(extensive, com.example.domain.WorkoutIntensityMode.fromExercisesCount(6))

        val simpleTarget = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 24f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 78f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.INTERMEDIATE,
            completedWorkoutsCount = 5,
            daysPerWeek = 5,
            trackWeekOverride = 2,
            intensityMode = simple
        )
        val moderateTarget = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 24f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 78f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.INTERMEDIATE,
            completedWorkoutsCount = 5,
            daysPerWeek = 5,
            trackWeekOverride = 2,
            intensityMode = moderate
        )
        val extensiveTarget = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 24f,
            specTotalSets = 4,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 78f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.INTERMEDIATE,
            completedWorkoutsCount = 5,
            daysPerWeek = 5,
            trackWeekOverride = 2,
            intensityMode = extensive
        )

        assertTrue(simpleTarget.sets < moderateTarget.sets)
        assertTrue(extensiveTarget.sets >= moderateTarget.sets)
        assertTrue(simpleTarget.loadKg <= moderateTarget.loadKg)
        assertTrue(extensiveTarget.loadKg >= moderateTarget.loadKg)

        val simpleKcal = ProgressiveLoadEngine.estimateSessionCalories(simple, isHomeMode = false, bodyweightKg = 78f)
        val modKcal = ProgressiveLoadEngine.estimateSessionCalories(moderate, isHomeMode = false, bodyweightKg = 78f)
        val extKcal = ProgressiveLoadEngine.estimateSessionCalories(extensive, isHomeMode = false, bodyweightKg = 78f)
        assertTrue(simpleKcal < modKcal && modKcal < extKcal)

        val tracks = com.example.system.WorkoutMusicTrack.entries
        assertEquals(3, tracks.size)
        assertEquals(listOf(130, 108, 118), tracks.map { it.bpm })
        tracks.forEach { track ->
            assertFalse("No ugly BPM jargon allowed in track title: ${track.title}", track.title.contains("BPM", ignoreCase = true))
            assertFalse("No ugly BPM jargon allowed in track shortLabel: ${track.shortLabel}", track.shortLabel.contains("BPM", ignoreCase = true))
            assertFalse("No ugly BPM jargon allowed in track subtitle: ${track.vibeSubtitle}", track.vibeSubtitle.contains("BPM", ignoreCase = true))
        }

        // Verify 4 independent Motivation Audio & Coach modes (Both On, Music Only, Voice Only, Mute All)
        val both = com.example.domain.WorkoutAudioCoachMode.BOTH_ACTIVE
        val musicOnly = com.example.domain.WorkoutAudioCoachMode.MUSIC_ONLY
        val voiceOnly = com.example.domain.WorkoutAudioCoachMode.VOICE_ONLY
        val muted = com.example.domain.WorkoutAudioCoachMode.MUTED
        assertTrue(both.musicEnabled && both.voiceEnabled)
        assertTrue(musicOnly.musicEnabled && !musicOnly.voiceEnabled)
        assertTrue(!voiceOnly.musicEnabled && voiceOnly.voiceEnabled)
        assertTrue(!muted.musicEnabled && !muted.voiceEnabled)

        // Verify Beginners NEVER get forced into slow-motion TUT reps on push-ups/squats:
        // Beginners move at a crisp, natural rhythm while receiving longer pre-set setup time,
        // lighter loads, fewer starting bodyweight reps, and easier incline angles.
        assertTrue(
            ExperienceLevel.NEW_BEGINNER.bodyweightRepDurationMs <=
                ExperienceLevel.INTERMEDIATE.bodyweightRepDurationMs
        )
        assertTrue(
            ExperienceLevel.NEW_BEGINNER.strengthRepDurationMs <=
                ExperienceLevel.INTERMEDIATE.strengthRepDurationMs
        )
        assertTrue(ExperienceLevel.NEW_BEGINNER.bodyweightRepDurationMs in 1900L..2250L)
        assertTrue(
            ExperienceLevel.NEW_BEGINNER.prepLeadInMs >
                ExperienceLevel.INTERMEDIATE.prepLeadInMs
        )

        val beginnerBodyweightSquat = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 0f,
            specTotalSets = 3,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = true,
            bodyweightKg = 75f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 0,
            daysPerWeek = 4,
            trackWeekOverride = 1
        )
        val experiencedBodyweightSquat = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 0f,
            specTotalSets = 3,
            baseRepsOrSeconds = 10,
            isTimedSeconds = false,
            isBodyweight = true,
            bodyweightKg = 75f,
            sex = com.example.domain.BiologicalSex.MALE,
            experience = ExperienceLevel.INTERMEDIATE,
            completedWorkoutsCount = 0,
            daysPerWeek = 4,
            trackWeekOverride = 1
        )
        assertTrue(beginnerBodyweightSquat.repsOrSeconds < experiencedBodyweightSquat.repsOrSeconds)

        // Verify continuous intermediate biomechanical contraction (no binary 0/1 start-end jump)
        val c0 = com.example.ui.components.computeBiomechanicalContraction(0.0f)
        val c15 = com.example.ui.components.computeBiomechanicalContraction(0.15f)
        val c30 = com.example.ui.components.computeBiomechanicalContraction(0.30f)
        val c50 = com.example.ui.components.computeBiomechanicalContraction(0.50f)
        val c75 = com.example.ui.components.computeBiomechanicalContraction(0.75f)
        assertTrue(c0 < 0.05f)
        assertTrue(c15 in 0.15f..0.40f)
        assertTrue(c30 in 0.60f..0.88f)
        assertTrue(c50 > 0.96f)
        assertTrue(c75 in 0.35f..0.65f)
    }

    @Test
    fun liveCoachGuide_all17MovementsHaveDirectionalPhysicalElevationAndDialogue() {
        for (type in com.example.domain.ExerciseAnimationType.entries) {
            val guide = com.example.ui.components.resolveLiveCoachGuide(type)
            assertTrue(guide.phaseOneCommand.isNotBlank())
            assertTrue(guide.phaseTwoCommand.isNotBlank())
            assertTrue(guide.phaseOneCoachDialogue.isNotBlank())
            assertTrue(guide.phaseTwoCoachDialogue.isNotBlank())
            assertTrue(guide.setupRule.length in 20..120)
        }
        // Squat & Push-Up start at TOP (1f -> 0f down, then 0f -> 1f drive UP)
        val squat = com.example.ui.components.resolveLiveCoachGuide(com.example.domain.ExerciseAnimationType.BODYWEIGHT_SQUATS)
        assertTrue(squat.startsAtTop)
        val pushup = com.example.ui.components.resolveLiveCoachGuide(com.example.domain.ExerciseAnimationType.STANDARD_PUSH_UPS)
        assertTrue(pushup.startsAtTop)

        // DB Press & DB Curl start at BOTTOM (0f -> 1f drive UP, then 1f -> 0f lower)
        val dbPress = com.example.ui.components.resolveLiveCoachGuide(com.example.domain.ExerciseAnimationType.INCLINE_DB_PRESS)
        assertTrue(!dbPress.startsAtTop)
        val dbCurl = com.example.ui.components.resolveLiveCoachGuide(com.example.domain.ExerciseAnimationType.INCLINE_DB_CURL)
        assertTrue(!dbCurl.startsAtTop)
    }

    @Test
    fun exerciseAnimationMediaResolver_everyWorkoutPlaylistResolvesDistinctUnifiedGifs() {
        val goals = listOf("goal_1_fat_loss", "goal_3_athletic", "goal_2_vtaper", "goal_4_power_mass")
        val starts = listOf("shape_5_underweight", "shape_4_average_untrained", "shape_1_high_bf")

        for (sex in com.example.domain.BiologicalSex.entries) {
            for (goalId in goals) {
                for (startId in starts) {
                    val split = CombatSplitCatalog.getSplitForProfile(
                        sex = sex,
                        ageTier = com.example.domain.AgeTier.UNDER_30,
                        experience = ExperienceLevel.NEW_BEGINNER,
                        frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
                        startingShapeId = startId,
                        goalCardId = goalId
                    )
                    for (day in split) {
                        for (env in com.example.data.local.TrainingEnvironment.entries) {
                            val warmups = com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(
                                day = day,
                                experience = ExperienceLevel.NEW_BEGINNER,
                                environment = env
                            )
                            val stretches = com.example.domain.CoachTruthAndSplitAdvisor.getPostWorkoutStretchSpecs(day)

                            val playlistPairs = buildList {
                                warmups.forEach { w ->
                                    val anim = com.example.ui.screens.inferGymAnimationForSpec(w, w.orderIndex, day.dayIndex)
                                    add(w.name to com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(w.name, anim))
                                }
                                day.exercises.forEachIndexed { idx, spec ->
                                    val variants = buildFallbackVariantsFor(
                                        spec = spec,
                                        index = idx,
                                        dayIndex = day.dayIndex,
                                        experienceLevel = ExperienceLevel.NEW_BEGINNER,
                                        sex = sex,
                                        daysPerWeek = 4,
                                        environment = env
                                    )
                                    assertEquals("Expected 3 swap options for ${spec.name} in $env", 3, variants.size)
                                    val swapOptionGifs = variants.map { v ->
                                        com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(v.name, v.animationType)
                                    }
                                    assertEquals(
                                        "All 3 Switch Exercise options must have distinct GIFs for ${spec.name} ($env): ${variants.map { it.name }} -> $swapOptionGifs",
                                        3,
                                        swapOptionGifs.distinct().size
                                    )

                                    val chosen = variants.first()
                                    add(chosen.name to swapOptionGifs.first())
                                }
                                stretches.forEach { s ->
                                    val anim = com.example.ui.screens.inferGymAnimationForSpec(s, s.orderIndex, day.dayIndex)
                                    add(s.name to com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(s.name, anim))
                                }
                            }

                            playlistPairs.forEach { (name, url) ->
                                assertTrue(
                                    "Expected ExerciseDB v1 GIF URL for $name, got $url",
                                    url.startsWith("https://static.exercisedb.dev/media/") && url.endsWith(".gif")
                                )
                            }

                            val urls = playlistPairs.map { it.second }
                            assertEquals(
                                "Duplicate GIF found in $sex / $goalId / $startId / $env / ${day.title}: $playlistPairs",
                                playlistPairs.size,
                                urls.distinct().size
                            )
                        }
                    }
                }
            }
        }

        // Also verify all frequencies (3, 4, 5, 6 days) and Week B rotation
        for (sex in com.example.domain.BiologicalSex.entries) {
            for (freq in com.example.domain.WeeklyFrequency.entries) {
                for (rotation in listOf(0, 1)) {
                    for (exp in ExperienceLevel.entries) {
                        val split = CombatSplitCatalog.getSplitForProfile(
                            sex = sex,
                            ageTier = com.example.domain.AgeTier.UNDER_30,
                            experience = exp,
                            frequency = freq,
                            rotationWeek = rotation
                        )
                        for (day in split) {
                            for (env in com.example.data.local.TrainingEnvironment.entries) {
                                val warmups = com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(day, exp, env)
                                val stretches = com.example.domain.CoachTruthAndSplitAdvisor.getPostWorkoutStretchSpecs(day)
                                val playlistPairs = buildList {
                                    warmups.forEach { w ->
                                        val anim = com.example.ui.screens.inferGymAnimationForSpec(w, w.orderIndex, day.dayIndex)
                                        add(w.name to com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(w.name, anim))
                                    }
                                    day.exercises.forEachIndexed { idx, spec ->
                                        val chosen = buildFallbackVariantsFor(
                                            spec = spec,
                                            index = idx,
                                            dayIndex = day.dayIndex,
                                            experienceLevel = exp,
                                            sex = sex,
                                            daysPerWeek = freq.daysCount,
                                            environment = env
                                        ).first()
                                        add(chosen.name to com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(chosen.name, chosen.animationType))
                                    }
                                    stretches.forEach { s ->
                                        val anim = com.example.ui.screens.inferGymAnimationForSpec(s, s.orderIndex, day.dayIndex)
                                        add(s.name to com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(s.name, anim))
                                    }
                                }
                                assertEquals(
                                    "Duplicate GIF found in $sex / $freq / rot=$rotation / $exp / $env / ${day.title}: $playlistPairs",
                                    playlistPairs.size,
                                    playlistPairs.map { it.second }.distinct().size
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun beginnerPhysiology_adaptsAffordableKilogramsZeroJumpImpactAndJointSafeMovements() {
        // 1. Affordable light starter weights (2.0–4.0 kg) for isolation dumbbells + ±1 kg micro-steps under 12 kg
        val beginnerLatRaiseFemale = ProgressiveLoadEngine.computeTarget(
            defaultLoadKg = 8f,
            specTotalSets = 3,
            baseRepsOrSeconds = 12,
            isTimedSeconds = false,
            isBodyweight = false,
            bodyweightKg = 58f,
            sex = com.example.domain.BiologicalSex.FEMALE,
            experience = ExperienceLevel.NEW_BEGINNER,
            completedWorkoutsCount = 0,
            daysPerWeek = 4,
            trackWeekOverride = 1
        )
        assertTrue(
            "Expected light affordable 2.0–3.5 kg dumbbells for first-time female lateral raise, got ${beginnerLatRaiseFemale.loadKg}",
            beginnerLatRaiseFemale.loadKg in 2.0f..3.5f
        )
        assertEquals(1f, ProgressiveLoadEngine.weightStepDeltaKg(beginnerLatRaiseFemale.loadKg), 0.001f)
        assertEquals(1f, ProgressiveLoadEngine.weightStepDeltaKg(12f), 0.001f)
        assertEquals(2f, ProgressiveLoadEngine.weightStepDeltaKg(16f), 0.001f)

        // 2. Home warm-up uses Low-Impact Step Jacks for NEW_BEGINNER; Gym warm-up NEVER uses Jumping Jacks!
        val day1 = CombatSplitCatalog.FOUR_DAY_SPLIT.first()
        val beginnerHomeWarmups = com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(
            day = day1,
            experience = ExperienceLevel.NEW_BEGINNER,
            environment = com.example.data.local.TrainingEnvironment.HOME
        )
        val gymWarmups = com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(
            day = day1,
            experience = ExperienceLevel.NEW_BEGINNER,
            environment = com.example.data.local.TrainingEnvironment.GYM
        )
        assertTrue(beginnerHomeWarmups.first().name.contains("Low-Impact Step Jacks"))
        assertEquals("00:30", beginnerHomeWarmups.first().repPrescription)
        assertTrue("Gym warm-up must NEVER use jumping jacks: ${gymWarmups.map { it.name }}", gymWarmups.none { "jack" in it.name.lowercase() })

        // 3. Home movement regressions for NEW_BEGINNER (Wall Push-Ups & Floor Tricep Dips in HOME mode)
        val athleticSplit = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
            goalCardId = "goal_3_athletic"
        )
        val allFourDaySpecs = athleticSplit.flatMap { day ->
            day.exercises.mapIndexed { idx, ex -> Triple(day.dayIndex, idx, ex) }
        }
        val (shDayIdx, shIdx, shoulderSpec) = allFourDaySpecs.first { (_, _, ex) ->
            ex.name.contains("Shoulder Press", ignoreCase = true)
        }
        val (dipDayIdx, dipIdx, dipSpec) = allFourDaySpecs.first { (_, _, ex) ->
            ex.name.contains("Dip", ignoreCase = true)
        }
        val shoulderHomeBeginner = buildFallbackVariantsFor(
            spec = shoulderSpec,
            index = shIdx,
            dayIndex = shDayIdx,
            experienceLevel = ExperienceLevel.NEW_BEGINNER,
            environment = com.example.data.local.TrainingEnvironment.HOME
        ).first()
        val tricepHomeBeginner = buildFallbackVariantsFor(
            spec = dipSpec,
            index = dipIdx,
            dayIndex = dipDayIdx,
            experienceLevel = ExperienceLevel.NEW_BEGINNER,
            environment = com.example.data.local.TrainingEnvironment.HOME
        ).first()
        assertEquals("Wall Push-Ups", shoulderHomeBeginner.name)
        assertEquals("Floor Tricep Dips", tricepHomeBeginner.name)

        // Unilateral split squats regress to rack-supported bodyweight balance mastery (0 kg) for NEW_BEGINNER in Gym
        val femaleSplit = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        val lungeSpec = femaleSplit.flatMap { it.exercises }.first {
            it.name.contains("Lunge", ignoreCase = true) || it.name.contains("Split Squat", ignoreCase = true)
        }
        val lungeGymBeginner = buildFallbackVariantsFor(
            spec = lungeSpec,
            index = 2,
            dayIndex = 2,
            experienceLevel = ExperienceLevel.NEW_BEGINNER,
            environment = com.example.data.local.TrainingEnvironment.GYM
        ).first()
        assertTrue("First-time beginner split squats must master bodyweight balance first", lungeGymBeginner.isBodyweight)

        // Standalone challenge regressions for NEW_BEGINNER
        val homeBurn = HomeWorkoutCatalog.QUICK_STRIKE_WORKOUTS.first()
        val jacksMove = homeBurn.exercises.first { it.animationType == com.example.domain.ExerciseAnimationType.JUMPING_JACKS }
        val regressedJacks = com.example.ui.screens.buildQuickStrikeVariant(jacksMove, ExperienceLevel.NEW_BEGINNER)
        assertEquals("Low-Impact Step Jacks", regressedJacks.name)

        val gripTrack = HomeWorkoutCatalog.SPECIALTY_GOAL_WORKOUTS.first { it.id == "track_grip_power" }
        val deadHangMove = gripTrack.exercises.first { it.name.contains("Dead Hang", ignoreCase = true) }
        val regressedHang = com.example.ui.screens.buildQuickStrikeVariant(deadHangMove, ExperienceLevel.NEW_BEGINNER)
        assertTrue(regressedHang.name.contains("Feet-Supported"))
    }

    @Test
    fun femaleBodyShapingAndWorkoutPhaseIdentity_curateFeminineCurvesAndDistinctPhaseHeaders() {
        val targets = SmartBodyMatcher.ALL_TARGET_GOALS
        assertEquals(
            listOf(
                "Slim Down & Flat Tummy",
                "Toned Waist & Sleek Shape",
                "Lifted Booty & Slim Waist",
                "Curvy Hourglass & Full Booty"
            ),
            targets.map { it.titleFemale }
        )
        targets.forEach { goal ->
            assertFalse(goal.titleFemale.contains("Strong & Powerful", ignoreCase = true))
            assertFalse(goal.musclesFemale.contains("Raw Strength", ignoreCase = true))
            val preset = com.example.ui.components.PhysiqueSilhouettePreset.fromCardId(goal.id)
            val femaleBadge = preset.badgeFor(com.example.domain.BiologicalSex.FEMALE)
            assertFalse(femaleBadge.contains("HEAVY MASS"))
            assertFalse(femaleBadge.contains("WIDE V-SHAPE"))
        }

        val femaleWorkoutIds = HomeWorkoutCatalog.workoutsForSex(com.example.domain.BiologicalSex.FEMALE).map { it.id }.toSet()
        assertTrue(femaleWorkoutIds.contains("challenge_7d_booty_lift"))
        assertTrue(femaleWorkoutIds.contains("challenge_28d_hourglass_curves"))
        assertTrue(femaleWorkoutIds.contains("track_glute_shelf_sculpt"))
        assertTrue(femaleWorkoutIds.contains("quick_slim_waist_legs"))
        assertFalse("Female hub should exclude male heavy chest push-up bulk challenge", femaleWorkoutIds.contains("challenge_7d_pushup_chest"))
        assertFalse("Female hub should exclude male forearm grip bulk track", femaleWorkoutIds.contains("track_grip_power"))

        val femaleGymBoosters = HomeWorkoutCatalog.dashboardBoostersForSex(
            com.example.domain.BiologicalSex.FEMALE,
            com.example.data.local.TrainingEnvironment.GYM
        ).map { it.id }
        assertTrue(femaleGymBoosters.contains("challenge_7d_booty_lift"))
        assertTrue(femaleGymBoosters.contains("track_glute_shelf_sculpt"))

        val pre = com.example.domain.SessionStageType.PRE_WARMUP
        val main = com.example.domain.SessionStageType.MAIN_WORKOUT
        val post = com.example.domain.SessionStageType.POST_STRETCH
        assertEquals("WARM-UP", pre.shortBadge)
        assertEquals("MAIN WORKOUT", main.shortBadge)
        assertEquals("COOL-DOWN", post.shortBadge)
        assertTrue(pre.phasePurposeSubtitle != main.phasePurposeSubtitle)
        assertTrue(main.phasePurposeSubtitle != post.phasePurposeSubtitle)
    }

    @Test
    fun physiologicalBlueprint_differentiatesSexStartingBodyGoalAndPureGymEquipment() {
        // 1. Slim Girl -> Curvy Hourglass vs Obese Woman -> Slim Down
        val slimGirlCurvy = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
            startingShapeId = "shape_5_underweight",
            goalCardId = "goal_4_power_mass"
        )
        val obeseWomanSlim = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
            startingShapeId = "shape_1_high_bf",
            goalCardId = "goal_1_fat_loss"
        )
        assertTrue(
            "Slim-to-Curvy Day 1 must differ from Obese-to-Slim Day 1",
            slimGirlCurvy[0].exercises.map { "${it.name}:${it.totalSets}x${it.targetRepsCount}" } !=
                obeseWomanSlim[0].exercises.map { "${it.name}:${it.totalSets}x${it.targetRepsCount}" }
        )
        assertEquals(4, slimGirlCurvy[0].exercises.first().totalSets)
        assertEquals(10, slimGirlCurvy[0].exercises.first().targetRepsCount)
        assertEquals(3, obeseWomanSlim[0].exercises.first().totalSets)
        assertEquals(15, obeseWomanSlim[0].exercises.first().targetRepsCount)

        // 2. Female vs Male with identical goal must have completely different workouts
        val maleMass = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
            startingShapeId = "shape_5_underweight",
            goalCardId = "goal_4_power_mass"
        )
        assertTrue(slimGirlCurvy[0].title != maleMass[0].title)
        assertTrue(slimGirlCurvy[0].exercises.map { it.name } != maleMass[0].exercises.map { it.name })

        // 3. Male all 4 goals produce 4 distinct Day 1 prescriptions
        val maleGoalSignatures = listOf("goal_1_fat_loss", "goal_3_athletic", "goal_2_vtaper", "goal_4_power_mass").map { gId ->
            CombatSplitCatalog.getSplitForProfile(
                sex = com.example.domain.BiologicalSex.MALE,
                ageTier = com.example.domain.AgeTier.UNDER_30,
                experience = ExperienceLevel.INTERMEDIATE,
                frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
                startingShapeId = "shape_4_average_untrained",
                goalCardId = gId
            ).flatMap { d -> d.exercises.map { "${it.name}:${it.totalSets}x${it.targetRepsCount}" } }
        }
        assertEquals("All 4 Male goals must produce 4 distinct workout plans", 4, maleGoalSignatures.distinct().size)

        val femaleGoalSignatures = listOf("goal_1_fat_loss", "goal_3_athletic", "goal_2_vtaper", "goal_4_power_mass").map { gId ->
            CombatSplitCatalog.getSplitForProfile(
                sex = com.example.domain.BiologicalSex.FEMALE,
                ageTier = com.example.domain.AgeTier.UNDER_30,
                experience = ExperienceLevel.INTERMEDIATE,
                frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS,
                startingShapeId = "shape_4_average_untrained",
                goalCardId = gId
            ).flatMap { d -> d.exercises.map { "${it.name}:${it.totalSets}x${it.targetRepsCount}" } }
        }
        assertEquals("All 4 Female goals must produce 4 distinct workout plans", 4, femaleGoalSignatures.distinct().size)

        // 4. Strict Gym Environment Isolation: ZERO living-room floor push-ups, floor dips, floor crunches, doorframe rows, or backpack curls in GYM mode (even for NEW_BEGINNER!)
        val bannedHomeTermsInGym = listOf(
            "push-up", "pushup", "floor tricep dip", "floor abdominal", "doorframe", "backpack", "couch"
        )
        for (sex in com.example.domain.BiologicalSex.entries) {
            val gymSplit = CombatSplitCatalog.getSplitForProfile(
                sex = sex,
                ageTier = com.example.domain.AgeTier.UNDER_30,
                experience = ExperienceLevel.NEW_BEGINNER,
                frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
            )
            gymSplit.forEach { day ->
                day.exercises.forEachIndexed { idx, spec ->
                    val gymOptions = buildFallbackVariantsFor(
                        spec = spec,
                        index = idx,
                        dayIndex = day.dayIndex,
                        experienceLevel = ExperienceLevel.NEW_BEGINNER,
                        sex = sex,
                        environment = com.example.data.local.TrainingEnvironment.GYM
                    )
                    assertEquals(3, gymOptions.size)
                    assertEquals(3, gymOptions.map { it.name }.distinct().size)
                    gymOptions.forEach { opt ->
                        val lower = opt.name.lowercase()
                        bannedHomeTermsInGym.forEach { banned ->
                            assertFalse(
                                "Home exercise '$banned' leaked into GYM option '${opt.name}' for ${spec.name}",
                                banned in lower
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun workoutPacksTaxonomy_adaptsAcrossGenderGoalsBodyFramesAndEquipmentWithoutEmptyCategories() {
        // 1. Rich catalog coverage and clean category taxonomy (no confusing "At Home" tab mixed with muscle groups)
        assertTrue(
            "Expected >= 30 structured workout packs, got ${HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.size}",
            HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.size >= 30
        )
        for (sex in com.example.domain.BiologicalSex.entries) {
            val cats = HomeWorkoutCatalog.categoriesForSex(sex)
            assertFalse("Internal HOME_BACKUP must not appear as a category chip", cats.contains(SpecialtyTrackCategory.HOME_BACKUP))
            assertTrue(cats.contains(SpecialtyTrackCategory.FOR_YOU))
            assertTrue(cats.contains(SpecialtyTrackCategory.MUSCLE_FOCUS))
            assertTrue(cats.contains(SpecialtyTrackCategory.ARMS_POSTURE))
            assertTrue(cats.contains(SpecialtyTrackCategory.CORE_WAIST))
            assertTrue(cats.contains(SpecialtyTrackCategory.FAT_BURN))
            assertTrue(cats.contains(SpecialtyTrackCategory.CHALLENGES))
            assertTrue(cats.contains(SpecialtyTrackCategory.VITALITY_KEGEL))
        }

        // 2. Strict Equipment Isolation under MATCHED filter (Gym vs Home) + Zero Empty Categories across all filters
        for (sex in com.example.domain.BiologicalSex.entries) {
            val gymMatchedAll = HomeWorkoutCatalog.workoutsForProfile(
                sex = sex,
                category = SpecialtyTrackCategory.ALL,
                equipmentFilter = com.example.domain.PackEquipmentFilter.MATCHED,
                activeEnvironment = com.example.data.local.TrainingEnvironment.GYM
            )
            assertTrue(gymMatchedAll.isNotEmpty())
            assertTrue(
                "GYM Matched filter must never leak NO_EQUIPMENT_HOME bedroom packs",
                gymMatchedAll.none { it.equipmentMode == com.example.domain.PackEquipmentMode.NO_EQUIPMENT_HOME }
            )

            val homeMatchedAll = HomeWorkoutCatalog.workoutsForProfile(
                sex = sex,
                category = SpecialtyTrackCategory.ALL,
                equipmentFilter = com.example.domain.PackEquipmentFilter.MATCHED,
                activeEnvironment = com.example.data.local.TrainingEnvironment.HOME
            )
            assertTrue(homeMatchedAll.isNotEmpty())
            assertTrue(
                "HOME Matched filter must never leak FULL_GYM or DUMBBELL_SMALL_GYM packs",
                homeMatchedAll.none {
                    it.equipmentMode == com.example.domain.PackEquipmentMode.FULL_GYM ||
                        it.equipmentMode == com.example.domain.PackEquipmentMode.DUMBBELL_SMALL_GYM
                }
            )

            for (env in com.example.data.local.TrainingEnvironment.entries) {
                for (eqFilter in com.example.domain.PackEquipmentFilter.entries) {
                    for (cat in HomeWorkoutCatalog.categoriesForSex(sex)) {
                        val packs = HomeWorkoutCatalog.workoutsForProfile(
                            sex = sex,
                            category = cat,
                            equipmentFilter = eqFilter,
                            activeEnvironment = env
                        )
                        assertTrue(
                            "Category $cat with equipmentFilter=$eqFilter in env=$env for sex=$sex must never be empty!",
                            packs.isNotEmpty()
                        )
                    }
                }
            }
        }

        // 3. Slim-to-Curvy Female vs Heavy-Frame Fat-Burn Ranking
        val slimFemaleCurvyPacks = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            goalCardId = "goal_4_power_mass",
            startingShapeId = "shape_5_underweight",
            category = SpecialtyTrackCategory.FOR_YOU,
            equipmentFilter = com.example.domain.PackEquipmentFilter.MATCHED,
            activeEnvironment = com.example.data.local.TrainingEnvironment.GYM
        )
        assertEquals(
            com.example.domain.PackGoalFocus.GLUTES_CURVES,
            slimFemaleCurvyPacks.first().goalFocus
        )

        val heavyMaleFatBurnPacks = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            goalCardId = "goal_1_fat_loss",
            startingShapeId = "shape_1_high_bf",
            category = SpecialtyTrackCategory.FOR_YOU,
            equipmentFilter = com.example.domain.PackEquipmentFilter.MATCHED,
            activeEnvironment = com.example.data.local.TrainingEnvironment.GYM
        )
        assertTrue(
            heavyMaleFatBurnPacks.first().goalFocus == com.example.domain.PackGoalFocus.FAT_LOSS_SLIMMING ||
                heavyMaleFatBurnPacks.first().id == "track_male_gym_fat_furnace"
        )

        // 4. Extra-Pack 3-Option Exercise Swap Coverage (Prescribed / Small-Gym Dumbbells / Joint-Easy Bodyweight)
        HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.forEach { pack ->
            pack.exercises.forEach { move ->
                val lowerBadge = move.stationBadge.lowercase()
                if (!("warm" in lowerBadge || "stretch" in lowerBadge || "cool" in lowerBadge)) {
                    val swapOptions = com.example.ui.screens.buildQuickStrikeVariantsList(move)
                    assertEquals("Expected 3 swap options for ${move.name} in ${pack.id}", 3, swapOptions.size)
                    assertEquals(
                        "Expected 3 distinct exercise names for ${move.name} (${swapOptions.map { it.name }})",
                        3,
                        swapOptions.map { it.name }.distinct().size
                    )
                }
            }
        }
    }

    @Test
    fun saturdayFirstTimeInstall_startsDayOneOnSaturdayAndMarksMonToFriPreStart() {
        val fourDaySplit = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        // 1. User installs app on Friday 2026-11-20 (ISO day 5, initially a Rest Day in default 4-day split)
        val fridayDate = java.time.LocalDate.of(2026, 11, 20)
        assertEquals(5, fridayDate.dayOfWeek.value)
        val defaultRest = WeeklySchedulePlanner.defaultRestDaysFor(com.example.domain.WeeklyFrequency.FOUR_DAYS)
        assertTrue("Friday is initially a rest day in default 4-day split", 5 in defaultRest)

        val fridayAdjustedRest = WeeklySchedulePlanner.ensureDayIsTrainingDay(
            currentRestDays = defaultRest,
            dayIso = fridayDate.dayOfWeek.value,
            fallbackFrequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        assertFalse("Friday must be converted to an active training day", 5 in fridayAdjustedRest)

        // 2. User installs app on Saturday 2026-11-21 (ISO day 6) -> Mon..Fri are "—", Saturday is Day 1 (index 0), NEVER Day 6!
        val saturdayDate = java.time.LocalDate.of(2026, 11, 21)
        assertEquals(6, saturdayDate.dayOfWeek.value)
        val saturdayAdjustedRest = WeeklySchedulePlanner.ensureDayIsTrainingDay(
            currentRestDays = defaultRest,
            dayIso = saturdayDate.dayOfWeek.value,
            fallbackFrequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        val week = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = fourDaySplit,
            restDaysIso = saturdayAdjustedRest,
            referenceDate = saturdayDate,
            planStartDateIso = saturdayDate.toString(),
            activeSplitIndex = 0
        )
        assertEquals(7, week.size)
        (0..4).forEach { idx ->
            assertTrue("Expected slot $idx to be before plan start", week[idx].isBeforePlanStart)
            assertEquals("—", week[idx].shortPillTag)
            org.junit.Assert.assertNull(week[idx].assignedSplitIndex)
        }
        assertFalse(week[5].isBeforePlanStart)
        assertFalse(week[5].isRestDay)
        assertTrue(week[5].isToday)
        assertEquals("Saturday first-time user must start on Day 1 (index 0)", 0, week[5].assignedSplitIndex)

        // Start Monday option from Saturday resolves cleanly to Monday 2026-11-23
        assertEquals(
            java.time.LocalDate.of(2026, 11, 23),
            com.example.domain.PlanStartOption.START_MONDAY.resolveDate(saturdayDate)
        )

        // WorkoutHistoryRecord encode/decode & streak computation
        val records = listOf(
            com.example.domain.WorkoutHistoryRecord(
                id = "rec_1",
                dateIso = "2026-11-21",
                epochMs = 1_795_200_000_000L,
                title = "Day 1 • Chest & Triceps",
                durationMinutes = 35,
                setsCompleted = 14,
                estimatedKcal = 260
            ),
            com.example.domain.WorkoutHistoryRecord(
                id = "rec_2",
                dateIso = "2026-11-20",
                epochMs = 1_795_113_600_000L,
                title = "5-Min Habit Rescue",
                durationMinutes = 5,
                setsCompleted = 3,
                estimatedKcal = 40
            )
        )
        val encoded = records.map { it.encode() }
        val decoded = encoded.mapNotNull { com.example.domain.WorkoutHistoryRecord.decode(it) }
        assertEquals(2, decoded.size)
        assertEquals("Day 1 • Chest & Triceps", decoded.first().title)
        assertEquals(
            2,
            WeeklySchedulePlanner.computeStreakDays(
                completedDatesIso = decoded.map { it.dateIso }.toSet(),
                restDaysIso = emptySet(),
                today = saturdayDate
            )
        )
    }

    @Test
    fun workoutCoverResolver_enforcesStrict100PercentGenderSeparationAcrossAllScreens() {
        val femaleOnlyDrawables = setOf(
            R.drawable.img_female_home_1795026870729,
            R.drawable.img_female_stretch_1795026870917,
            R.drawable.img_cover_back_shoulders_1794761630996,
            R.drawable.img_cover_leg_day_power_1794761630544,
            R.drawable.img_cover_abs_core_1794570081800,
            R.drawable.img_calisthenics_female_handstand_1795114083854,
            R.drawable.img_calisthenics_female_bars_1795114084511
        )
        val maleOnlyDrawables = setOf(
            R.drawable.img_hero_idol_splash_1794508264671,
            R.drawable.img_workout_hub_banner_1794761668870,
            R.drawable.img_male_back_1795026866340,
            R.drawable.img_male_legs_1795026867840,
            R.drawable.img_male_abs_1795026870074,
            R.drawable.img_cover_chest_arms_1794570080759,
            R.drawable.img_cover_raw_strength_1794572460981,
            R.drawable.img_cover_gym_program_1794570078368,
            R.drawable.img_cover_home_wakeup_1794570079145,
            R.drawable.img_cover_grip_power_1794572439024,
            R.drawable.img_cover_core_vitality_1794572439738,
            R.drawable.img_cover_posture_stretch_1794570082806,
            R.drawable.img_calisthenics_male_bars_1795114084816,
            R.drawable.img_calisthenics_male_planche_1795114083910
        )

        // 1. Male profile NEVER sees a female athlete photo anywhere
        val maleSex = com.example.domain.BiologicalSex.MALE
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForSplash(maleSex) in femaleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForWorkoutHub(maleSex) in femaleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForProgress(maleSex) in femaleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.coverForRestDay(maleSex) in femaleOnlyDrawables)
        (1..5).forEach { step ->
            assertFalse(
                "Male onboarding step $step leaked female photo",
                com.example.ui.components.WorkoutCoverResolver.bannerForOnboardingStep(step, maleSex) in femaleOnlyDrawables
            )
        }
        val maleSplit = CombatSplitCatalog.getSplitForProfile(
            sex = maleSex,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        maleSplit.forEach { day ->
            for (env in com.example.data.local.TrainingEnvironment.entries) {
                val res = com.example.ui.components.WorkoutCoverResolver.coverForScheduledDay(day, env, maleSex)
                assertFalse("Male scheduled day '${day.title}' ($env) leaked female photo!", res in femaleOnlyDrawables)
            }
        }
        HomeWorkoutCatalog.workoutsForSex(maleSex).forEach { pack ->
            val res = com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(pack, maleSex)
            assertFalse("Male pack '${pack.id}' leaked female photo!", res in femaleOnlyDrawables)
        }

        // 2. Female profile NEVER sees a male athlete photo anywhere
        val femaleSex = com.example.domain.BiologicalSex.FEMALE
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForSplash(femaleSex) in maleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForWorkoutHub(femaleSex) in maleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.bannerForProgress(femaleSex) in maleOnlyDrawables)
        assertFalse(com.example.ui.components.WorkoutCoverResolver.coverForRestDay(femaleSex) in maleOnlyDrawables)
        (1..5).forEach { step ->
            assertFalse(
                "Female onboarding step $step leaked male photo",
                com.example.ui.components.WorkoutCoverResolver.bannerForOnboardingStep(step, femaleSex) in maleOnlyDrawables
            )
        }
        val femaleSplit = CombatSplitCatalog.getSplitForProfile(
            sex = femaleSex,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
        )
        femaleSplit.forEach { day ->
            for (env in com.example.data.local.TrainingEnvironment.entries) {
                val res = com.example.ui.components.WorkoutCoverResolver.coverForScheduledDay(day, env, femaleSex)
                assertFalse("Female scheduled day '${day.title}' ($env) leaked male photo!", res in maleOnlyDrawables)
            }
        }
        HomeWorkoutCatalog.workoutsForSex(femaleSex).forEach { pack ->
            val res = com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(pack, femaleSex)
            assertFalse("Female pack '${pack.id}' leaked male photo!", res in maleOnlyDrawables)
        }
    }

    @Test
    fun customCalendarStartDateAndExactReminderTimes_scheduleCleanlyAcrossWeeks() {
        val wedToday = java.time.LocalDate.of(2026, 11, 18) // Wednesday
        val customSaturday = java.time.LocalDate.of(2026, 11, 21) // Saturday same week
        val customNextThursday = java.time.LocalDate.of(2026, 11, 26) // Next week Thursday

        assertEquals(null, com.example.domain.PlanStartOption.matchOptionOrNull(customSaturday.toString(), wedToday))
        assertEquals("Starts Sat, Nov 21", com.example.domain.PlanStartOption.formatPlanStartSummary(customSaturday.toString(), wedToday))
        assertEquals("Starts Today (Wed, Nov 18)", com.example.domain.PlanStartOption.formatPlanStartSummary(wedToday.toString(), wedToday))

        val split = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.NEW_BEGINNER,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        val ensuredRest = WeeklySchedulePlanner.ensureDayIsTrainingDay(
            currentRestDays = setOf(3, 5, 6),
            dayIso = customSaturday.dayOfWeek.value,
            fallbackFrequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        assertFalse("Picked Saturday start date must be an active training day", 6 in ensuredRest)

        val sameWeekSlots = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = split,
            restDaysIso = ensuredRest,
            referenceDate = wedToday,
            planStartDateIso = customSaturday.toString(),
            activeSplitIndex = 0
        )
        assertTrue(sameWeekSlots.subList(0, 5).all { it.isBeforePlanStart })
        assertFalse(sameWeekSlots[5].isBeforePlanStart)
        assertEquals(0, sameWeekSlots[5].assignedSplitIndex)

        val futureWeekSlots = WeeklySchedulePlanner.buildWeekSchedule(
            splitDays = split,
            restDaysIso = ensuredRest,
            referenceDate = wedToday,
            planStartDateIso = customNextThursday.toString(),
            activeSplitIndex = 0
        )
        assertTrue(futureWeekSlots.all { it.isBeforePlanStart })
        assertEquals("Starts Thu, Nov 26", futureWeekSlots[0].outcomeTitle)

        // Exact user clock reminder formatting (08:00 AM, 09:00 PM, 06:45 AM)
        assertEquals("08:00 AM", com.example.system.ApexNotificationScheduler.formatTime12h(8, 0))
        assertEquals("09:00 PM", com.example.system.ApexNotificationScheduler.formatTime12h(21, 0))
        assertEquals("06:45 AM", com.example.system.ApexNotificationScheduler.formatTime12h(6, 45))
    }

    @Test
    fun allMaleAndFemaleSplitsAndPacks_haveAtLeastThreeWorkingSetsDistinctWarmupsAndZeroClonedDays() {
        // 1. Every scheduled main exercise across Male & Female, all levels, and all intensity modes prescribes >= 3 sets
        for (sex in com.example.domain.BiologicalSex.entries) {
            for (freq in com.example.domain.WeeklyFrequency.entries) {
                for (rot in listOf(0, 1)) {
                    for (goal in listOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass")) {
                        val split = CombatSplitCatalog.getSplitForProfile(
                            sex = sex,
                            ageTier = com.example.domain.AgeTier.UNDER_30,
                            experience = ExperienceLevel.NEW_BEGINNER,
                            frequency = freq,
                            rotationWeek = rot,
                            startingShapeId = "shape_4_average_untrained",
                            goalCardId = goal
                        )
                        // Verify ZERO cloned days within the same week (even in 5-day and 6-day Week B!)
                        val signatures = split.map { day -> day.exercises.map { it.name } }
                        assertEquals(
                            "Duplicate workout day found in $sex $freq rot=$rot goal=$goal: $signatures",
                            signatures.size,
                            signatures.toSet().size
                        )

                        split.forEach { day ->
                            day.exercises.forEach { ex ->
                                assertTrue("Main spec '${ex.name}' had <3 sets", ex.totalSets >= 3)
                                for (lvl in ExperienceLevel.entries) {
                                    for (mode in com.example.domain.WorkoutIntensityMode.entries) {
                                        val target = ProgressiveLoadEngine.computeTarget(
                                            defaultLoadKg = ex.defaultLoadKg,
                                            specTotalSets = ex.totalSets,
                                            baseRepsOrSeconds = ex.targetRepsCount,
                                            isTimedSeconds = ex.repPrescription.contains(":"),
                                            isBodyweight = ex.defaultLoadKg <= 0f,
                                            bodyweightKg = if (sex == com.example.domain.BiologicalSex.FEMALE) 60f else 75f,
                                            sex = sex,
                                            experience = lvl,
                                            completedWorkoutsCount = 0,
                                            daysPerWeek = freq.daysCount,
                                            trackWeekOverride = 1,
                                            intensityMode = mode
                                        )
                                        assertTrue(
                                            "Expected >= 3 working sets for ${ex.name} ($sex, $lvl, $mode), got ${target.sets}",
                                            target.sets >= 3
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Verify Gym Warm-Ups are genuinely tailored & distinct across Push, Pull, Legs, and Shoulders
        val maleFourDay = CombatSplitCatalog.getSplitForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            ageTier = com.example.domain.AgeTier.UNDER_30,
            experience = ExperienceLevel.INTERMEDIATE,
            frequency = com.example.domain.WeeklyFrequency.FOUR_DAYS
        )
        val warmupPairs = maleFourDay.map { day ->
            com.example.domain.CoachTruthAndSplitAdvisor.getPreWorkoutWarmupSpecs(
                day = day,
                experience = ExperienceLevel.INTERMEDIATE,
                environment = com.example.data.local.TrainingEnvironment.GYM
            ).map { it.name }
        }
        assertEquals(
            "Expected 4 distinct warm-up pairings across 4-day split, got $warmupPairs",
            4,
            warmupPairs.toSet().size
        )

        // 3. Verify all Male & Female packs have >= 3 sets on working exercises (excluding pure flexibility pack)
        //    and 100% distinct GIFs inside every pack!
        val allPacks = (HomeWorkoutCatalog.workoutsForSex(com.example.domain.BiologicalSex.MALE) +
            HomeWorkoutCatalog.workoutsForSex(com.example.domain.BiologicalSex.FEMALE))
            .distinctBy { it.id }

        allPacks.forEach { pack ->
            val packGifs = pack.exercises.map { move ->
                com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(move.name, move.animationType)
            }
            assertEquals(
                "Duplicate GIF inside pack '${pack.id}' (${pack.title}): ${pack.exercises.map { it.name }}",
                packGifs.size,
                packGifs.toSet().size
            )
            if (pack.id != "track_full_body_evening_stretch") {
                pack.exercises.filter { it.totalSets > 1 }.forEach { move ->
                    assertTrue(
                        "Pack '${pack.id}' move '${move.name}' had only ${move.totalSets} sets",
                        move.totalSets >= 3
                    )
                }
            }
        }
    }

    @Test
    fun `verify all daily-life performance and sex-specific goal challenges and universal zero-to-hero ladders`() {
        val maleChallengeIds = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            goalCardId = "goal_3_athletic",
            startingShapeId = "shape_3_skinny_fat",
            category = com.example.domain.SpecialtyTrackCategory.CHALLENGES,
            equipmentFilter = com.example.domain.PackEquipmentFilter.ALL_EQUIPMENT,
            activeEnvironment = com.example.data.local.TrainingEnvironment.HOME
        ).map { it.id }.toSet()

        val femaleChallengeIds = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            goalCardId = "goal_3_athletic",
            startingShapeId = "shape_3_skinny_fat",
            category = com.example.domain.SpecialtyTrackCategory.CHALLENGES,
            equipmentFilter = com.example.domain.PackEquipmentFilter.ALL_EQUIPMENT,
            activeEnvironment = com.example.data.local.TrainingEnvironment.HOME
        ).map { it.id }.toSet()

        val sharedDailyGoalChallenges = listOf(
            "challenge_14d_flexibility_mobility",
            "challenge_14d_posture_height",
            "challenge_14d_kegel_pelvic",
            "challenge_14d_strength_power",
            "challenge_14d_pullup_vtaper_mastery",
            "challenge_14d_stamina_endurance",
            "challenge_7d_joint_balance_knees"
        )
        sharedDailyGoalChallenges.forEach { challengeId ->
            assertTrue("Expected Male CHALLENGES to include $challengeId", challengeId in maleChallengeIds)
            assertTrue("Expected Female CHALLENGES to include $challengeId", challengeId in femaleChallengeIds)
        }

        val femaleBodyContourChallenges = listOf(
            "challenge_14d_hips_wider_curves",
            "challenge_14d_flat_tummy_corset",
            "challenge_7d_toned_arms_collarbone",
            "challenge_7d_booty_lift",
            "challenge_28d_hourglass_curves"
        )
        femaleBodyContourChallenges.forEach { fId ->
            assertTrue("Expected Female CHALLENGES to include $fId", fId in femaleChallengeIds)
            assertFalse("Female contour challenge $fId must not leak into Male pool", fId in maleChallengeIds)
        }

        // Verify dedicated Pull-Up 0->Hero and sex-aware Kegel 0->Hero guides
        val pullUpGuide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(
            exerciseName = "Pull-Up / Dead Hang",
            primaryMuscle = "Lats & Grip",
            sex = com.example.domain.BiologicalSex.MALE
        )
        assertEquals(4, pullUpGuide.zeroToHeroLadder.size)
        assertTrue(pullUpGuide.zeroToHeroLadder.first().title.contains("Hang", ignoreCase = true) ||
            pullUpGuide.zeroToHeroLadder.first().title.contains("Row", ignoreCase = true))
        assertEquals(3, pullUpGuide.failedSetRescueTips.size)

        val maleKegelGuide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(
            exerciseName = "Kegel Squeeze & Hold",
            primaryMuscle = "Pelvic Floor",
            sex = com.example.domain.BiologicalSex.MALE
        )
        val femaleKegelGuide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(
            exerciseName = "Kegel Squeeze & Hold",
            primaryMuscle = "Pelvic Floor",
            sex = com.example.domain.BiologicalSex.FEMALE
        )
        assertEquals(4, maleKegelGuide.zeroToHeroLadder.size)
        assertEquals(4, femaleKegelGuide.zeroToHeroLadder.size)
        assertTrue(
            "Expected sex-specific Kegel isolation cues for Male vs Female",
            maleKegelGuide.setupSteps != femaleKegelGuide.setupSteps
        )

        // Verify EVERY exercise in ALL splits and ALL packs resolves a complete 4-step 0->Hero ladder & 3 rescue tips
        for (sex in com.example.domain.BiologicalSex.entries) {
            val split = CombatSplitCatalog.getSplitForProfile(
                sex = sex,
                ageTier = com.example.domain.AgeTier.UNDER_30,
                experience = ExperienceLevel.BEGINNER_JOINT_SAFE,
                frequency = com.example.domain.WeeklyFrequency.FIVE_DAYS
            )
            split.flatMap { it.exercises }.forEach { ex ->
                val guide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(
                    exerciseName = ex.name,
                    primaryMuscle = ex.primaryMuscle,
                    sex = sex
                )
                assertEquals("Expected 4 ladder steps for ${ex.name}", 4, guide.zeroToHeroLadder.size)
                assertTrue("Blank whyPeopleStruggle for ${ex.name}", guide.whyPeopleStruggle.isNotBlank())
                assertTrue("Expected >=3 rescue tips for ${ex.name}", guide.failedSetRescueTips.size >= 3)
            }

            HomeWorkoutCatalog.workoutsForSex(sex).flatMap { it.exercises }.forEach { move ->
                val guide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(
                    exerciseName = move.name,
                    primaryMuscle = move.targetMusclePill,
                    sex = sex
                )
                assertEquals("Expected 4 ladder steps for ${move.name}", 4, guide.zeroToHeroLadder.size)
                assertEquals(
                    "Expected 4 distinct ladder titles for ${move.name}",
                    4,
                    guide.zeroToHeroLadder.map { it.title }.toSet().size
                )
                assertTrue("Expected >=3 rescue tips for ${move.name}", guide.failedSetRescueTips.size >= 3)
                assertTrue("Expected non-blank plainPurpose for ${move.name}", guide.plainPurpose.isNotBlank())
            }
        }
    }

    @Test
    fun `verify calisthenics across both genders and goals, deduplicated stretching, and plain-English exercise purposes`() {
        val maleCalisthenicsList = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.MALE,
            category = com.example.domain.SpecialtyTrackCategory.CALISTHENICS,
            equipmentFilter = com.example.domain.PackEquipmentFilter.ALL_EQUIPMENT,
            activeEnvironment = com.example.data.local.TrainingEnvironment.HOME
        )
        val maleCalisthenicsIds = maleCalisthenicsList.map { it.id }.toSet()

        val femaleCalisthenicsList = HomeWorkoutCatalog.workoutsForProfile(
            sex = com.example.domain.BiologicalSex.FEMALE,
            category = com.example.domain.SpecialtyTrackCategory.CALISTHENICS,
            equipmentFilter = com.example.domain.PackEquipmentFilter.ALL_EQUIPMENT,
            activeEnvironment = com.example.data.local.TrainingEnvironment.HOME
        )
        val femaleCalisthenicsIds = femaleCalisthenicsList.map { it.id }.toSet()

        // Both Male & Female receive all 4 progressive Calisthenics stages + 3 deep Calisthenics skill challenges!
        val sharedFourStageAndChallengeIds = listOf(
            "track_calisthenics_stage1_foundation",
            "track_gym_calisthenics_hybrid",
            "track_home_calisthenics_strength",
            "track_calisthenics_stage4_elite",
            "challenge_14d_calisthenics_mastery",
            "challenge_14d_handstand_lsit_skill",
            "challenge_28d_calisthenics_elite_skills",
            "track_calisthenics_fat_burn_flow"
        )
        sharedFourStageAndChallengeIds.forEach { calId ->
            assertTrue("Male Calisthenics must include $calId", calId in maleCalisthenicsIds)
            assertTrue("Female Calisthenics must include $calId", calId in femaleCalisthenicsIds)
        }
        assertEquals("Stage 1 must appear first in Calisthenics category", "track_calisthenics_stage1_foundation", maleCalisthenicsList[0].id)
        assertEquals("Stage 2 must appear second in Calisthenics category", "track_gym_calisthenics_hybrid", maleCalisthenicsList[1].id)
        assertEquals("Stage 3 must appear third in Calisthenics category", "track_home_calisthenics_strength", maleCalisthenicsList[2].id)
        assertEquals("Stage 4 must appear fourth in Calisthenics category", "track_calisthenics_stage4_elite", maleCalisthenicsList[3].id)

        assertTrue("Female Calisthenics must include Women's Calisthenics Flow", "track_female_calisthenics_sculpt" in femaleCalisthenicsIds)
        assertFalse("Male Calisthenics must not include Women's Calisthenics Flow", "track_female_calisthenics_sculpt" in maleCalisthenicsIds)

        // Verify Calisthenics skills open dedicated 0->Hero skill ladders (never misrouted to generic Pull-Up guidance!)
        val skillChecks = listOf(
            "Parallettes Planche Hold & Lean" to "Planche",
            "Wall Handstand Practice Press" to "Handstand",
            "Explosive Muscle-Up Bar Pull" to "Muscle-Up",
            "Tuck Front Lever Hold & Row" to "Front Lever",
            "Human-Flag Side Lever Hold" to "Human Flag",
            "Parallettes Straight-Leg L-Sit" to "L-Sit",
            "Single-Leg Pistol Squat" to "Pistol"
        )
        skillChecks.forEach { (moveName, expectedSkillKeyword) ->
            val guide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(moveName, "Calisthenics")
            assertTrue(
                "Expected '$moveName' 0->Hero title '${guide.movementFamilyTitle}' to mention '$expectedSkillKeyword'",
                guide.movementFamilyTitle.contains(expectedSkillKeyword, ignoreCase = true)
            )
            assertFalse(
                "'$moveName' must NOT misroute to generic Pull-Up guidance!",
                guide.movementFamilyTitle.contains("First Pull-Up", ignoreCase = true)
            )
        }

        // Verify duplicate posture/stretching quick pack was consolidated cleanly
        val allIds = HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.map { it.id }.toSet()
        assertFalse("Duplicate quick_posture_reset should be consolidated", "quick_posture_reset" in allIds)
        val stretchFlow = HomeWorkoutCatalog.SPECIALTY_GOAL_WORKOUTS.first { it.id == "track_full_body_evening_stretch" }
        assertEquals("Full-Body Stretching & Flexibility Flow", stretchFlow.title)
        assertEquals("STRETCHING & FLEXIBILITY", stretchFlow.goalFocus.badge)

        // Verify Belly-Fat Shred Circuit combines high-calorie leg/back burners WITH direct lower-ab & waist moves
        val bellyShred = HomeWorkoutCatalog.SPECIALTY_GOAL_WORKOUTS.first { it.id == "track_male_gym_fat_furnace" }
        val bellyMoveNames = bellyShred.exercises.map { it.name }
        assertTrue(bellyMoveNames.any { it.contains("Goblet Squat", ignoreCase = true) })
        assertTrue(bellyMoveNames.any { it.contains("Knee Raise", ignoreCase = true) })
        assertTrue(bellyMoveNames.any { it.contains("Plank", ignoreCase = true) })

        val gobletInBellyPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
            exerciseName = "Dumbbell Goblet Squat",
            primaryMuscle = "Legs",
            workoutContextTitle = bellyShred.title
        )
        assertTrue(
            "Goblet Squat inside Belly-Fat Shred must explain 3x calorie burn for belly fat: $gobletInBellyPurpose",
            gobletInBellyPurpose.contains("3×", ignoreCase = true) && gobletInBellyPurpose.contains("belly fat", ignoreCase = true)
        )
        val whyBellyWorks = WeeklySchedulePlanner.plainWhyWorkoutWorks(bellyShred.title)
        org.junit.Assert.assertNotNull(whyBellyWorks)
        assertTrue(whyBellyWorks!!.contains("compound", ignoreCase = true) || whyBellyWorks.contains("calories", ignoreCase = true))
    }

    @Test
    fun `verify live silhouette math, honest 8-11 month obese timelines, 4 distinct calisthenics stage covers, and all 18 target exercise zones`() {
        // 1. Live Biometric Silhouette Math & SmartBodyMatcher accuracy:
        //    - 200 cm / 59 kg -> BMI 14.8 -> shape_5_underweight (Very Thin)
        //    - 165 cm / 67 kg -> BMI 24.6 -> shape_4_average_untrained (Normal / Everyday Build — NOT Overweight!)
        val ultraTallSlim = com.example.ui.components.BiometricSilhouetteMath.analyze(
            heightCm = 200f,
            weightKg = 59f,
            sex = com.example.domain.BiologicalSex.MALE
        )
        val everydayNormal = com.example.ui.components.BiometricSilhouetteMath.analyze(
            heightCm = 165f,
            weightKg = 67f,
            sex = com.example.domain.BiologicalSex.MALE
        )
        val heavyObese = com.example.ui.components.BiometricSilhouetteMath.analyze(
            heightCm = 165f,
            weightKg = 108f,
            sex = com.example.domain.BiologicalSex.MALE
        )
        assertEquals("shape_5_underweight", ultraTallSlim.matchedStartingShapeId)
        assertEquals("shape_4_average_untrained", everydayNormal.matchedStartingShapeId)
        assertEquals("shape_4_average_untrained", SmartBodyMatcher.recommendStartingShapeId(weightKg = 67f, heightCm = 165f))
        assertEquals("shape_1_high_bf", heavyObese.matchedStartingShapeId)
        assertTrue("200 cm silhouette must stand taller than 165 cm silhouette", ultraTallSlim.figureHeightRatio > everydayNormal.figureHeightRatio)
        assertTrue("108 kg waist width must be wider than 67 kg normal waist", heavyObese.waistWidthFactor > everydayNormal.waistWidthFactor)
        assertTrue("67 kg waist width must be wider than 59 kg/200 cm ultra-thin waist", everydayNormal.waistWidthFactor > ultraTallSlim.waistWidthFactor)

        // 2. Honest Un-Sugarcoated Transformation Timeline:
        //    Very Obese -> Slim Down / Fat Loss MUST be >= 34 weeks (8–11+ Months), NEVER 3–4 months!
        val veryObeseToSlim = CalibrationCalculator.computeHonestTimeline(
            startingShapeId = "shape_1_high_bf",
            goalCardId = "goal_1_fat_loss",
            daysPerWeek = 4,
            weightKg = 112f,
            heightCm = 175f,
            measurementsKnown = true
        )
        assertTrue("Very Obese -> Slim must take >= 34 weeks, got ${veryObeseToSlim.minWeeks}", veryObeseToSlim.minWeeks >= 34)
        assertFalse("Very Obese -> Slim must NEVER claim 3–4 Months: ${veryObeseToSlim.monthsRangeLabel}", veryObeseToSlim.monthsRangeLabel.startsWith("3–4"))
        assertTrue("Expected 3–4 month checkpoint milestone copy", veryObeseToSlim.monthThreeFourCheckpoint.contains("Months 3–4", ignoreCase = true))

        // 3. All 4 Calisthenics Stages are Multi-Day Challenges (14, 14, 21, 28 days) with 4 completely distinct Male covers & 4 distinct Female covers!
        val stageIds = listOf(
            "track_calisthenics_stage1_foundation",
            "track_gym_calisthenics_hybrid",
            "track_home_calisthenics_strength",
            "track_calisthenics_stage4_elite"
        )
        val stageWorkouts = stageIds.map { sId ->
            HomeWorkoutCatalog.ALL_HUB_CATEGORY_WORKOUTS.first { it.id == sId }
        }
        assertEquals(listOf(14, 14, 21, 28), stageWorkouts.map { it.resolvedChallengeDays })
        assertTrue(stageWorkouts.all { it.isMultiDayChallenge })

        val maleStageCovers = stageWorkouts.map {
            com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(it, com.example.domain.BiologicalSex.MALE)
        }
        val femaleStageCovers = stageWorkouts.map {
            com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(it, com.example.domain.BiologicalSex.FEMALE)
        }
        assertEquals("All 4 Male Calisthenics Stages must have 4 distinct covers!", 4, maleStageCovers.toSet().size)
        assertEquals("All 4 Female Calisthenics Stages must have 4 distinct covers!", 4, femaleStageCovers.toSet().size)

        // 4. Day-Scaled Fat Loss Challenges (7d vs 14d vs 21d vs 28d) + Deduplicated Kegel in Hub feed
        val challengeTitlesById = HomeWorkoutCatalog.CHALLENGE_WORKOUTS.associate { it.id to it.title }
        assertTrue(challengeTitlesById.containsKey("challenge_7d_fat_ignition"))
        assertTrue(challengeTitlesById.containsKey("challenge_14d_fat_burnout"))
        assertTrue(challengeTitlesById.containsKey("challenge_21d_waist_shred"))
        assertTrue(challengeTitlesById.containsKey("challenge_28d_home_lean"))
        assertEquals(
            4,
            listOf(
                challengeTitlesById["challenge_7d_fat_ignition"],
                challengeTitlesById["challenge_14d_fat_burnout"],
                challengeTitlesById["challenge_21d_waist_shred"],
                challengeTitlesById["challenge_28d_home_lean"]
            ).toSet().size
        )
        for (sex in com.example.domain.BiologicalSex.entries) {
            val sexFeedIds = HomeWorkoutCatalog.workoutsForSex(sex).map { it.id }.toSet()
            assertTrue("14-Day Kegel Challenge must remain in Hub feed", "challenge_14d_kegel_pelvic" in sexFeedIds)
            assertFalse("Duplicate standalone track_pelvic_kegel must not clutter Hub feed alongside 14d Kegel", "track_pelvic_kegel" in sexFeedIds)
        }

        // 5. All 18 Head-to-Toe TargetExerciseDirectory Categories (incl. Jawline/Neck/Tongue & Diaphragm/Voice Power)
        val allTargetGroups = com.example.domain.TargetExerciseDirectory.ALL_GROUPS
        assertEquals(18, allTargetGroups.size)
        val targetGroupIds = allTargetGroups.map { it.id }.toSet()
        assertTrue("Must include Jawline, Neck & Tongue Posture (Mewing)", "target_ex_jaw_neck_tongue" in targetGroupIds)
        assertTrue("Must include Diaphragm, Breath & Voice Power", "target_ex_voice_breath" in targetGroupIds)
        assertTrue("Must include Pelvic Floor & Kegel", "target_ex_kegel" in targetGroupIds)
        assertTrue("Must include Calisthenics Skill Lab", "target_ex_calisthenics" in targetGroupIds)

        val femaleOnlyDrawables = setOf(
            R.drawable.img_female_home_1795026870729,
            R.drawable.img_female_stretch_1795026870917,
            R.drawable.img_cover_back_shoulders_1794761630996,
            R.drawable.img_cover_leg_day_power_1794761630544,
            R.drawable.img_cover_abs_core_1794570081800,
            R.drawable.img_calisthenics_female_handstand_1795114083854,
            R.drawable.img_calisthenics_female_bars_1795114084511
        )
        val maleOnlyDrawables = setOf(
            R.drawable.img_hero_idol_splash_1794508264671,
            R.drawable.img_workout_hub_banner_1794761668870,
            R.drawable.img_male_back_1795026866340,
            R.drawable.img_male_legs_1795026867840,
            R.drawable.img_male_abs_1795026870074,
            R.drawable.img_cover_chest_arms_1794570080759,
            R.drawable.img_cover_raw_strength_1794572460981,
            R.drawable.img_cover_gym_program_1794570078368,
            R.drawable.img_cover_home_wakeup_1794570079145,
            R.drawable.img_cover_grip_power_1794572439024,
            R.drawable.img_cover_core_vitality_1794572439738,
            R.drawable.img_cover_posture_stretch_1794570082806,
            R.drawable.img_calisthenics_male_bars_1795114084816,
            R.drawable.img_calisthenics_male_planche_1795114083910
        )

        allTargetGroups.forEach { group ->
            assertTrue("Blank expectedResultsTimeline on ${group.id}", group.expectedResultsTimeline.isNotBlank())
            val qs = group.toQuickStrikeWorkout()
            assertEquals("Target exercise group must not be marked as multi-day challenge", 0, qs.resolvedChallengeDays)
            assertFalse(qs.isMultiDayChallenge)

            val maleRes = com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(qs, com.example.domain.BiologicalSex.MALE)
            val femaleRes = com.example.ui.components.WorkoutCoverResolver.coverForQuickWorkout(qs, com.example.domain.BiologicalSex.FEMALE)
            assertFalse("Male cover for ${group.id} leaked female drawable", maleRes in femaleOnlyDrawables)
            assertFalse("Female cover for ${group.id} leaked male drawable", femaleRes in maleOnlyDrawables)

            val gifs = group.exercises.map { m ->
                assertFalse("No dash range allowed in ${m.setsAndRepsLabel}", m.setsAndRepsLabel.contains("-") || m.setsAndRepsLabel.contains("–"))
                com.example.system.ExerciseAnimationMediaResolver.resolveStaticGifUrl(m.name, m.animationType)
            }
            assertEquals(
                "Duplicate GIF inside TargetExerciseGroup '${group.id}': ${group.exercises.map { it.name }} -> $gifs",
                gifs.size,
                gifs.toSet().size
            )

            group.exercises.forEach { m ->
                val lowerBadge = m.stationBadge.lowercase()
                if (!("warm" in lowerBadge || "stretch" in lowerBadge || "cool" in lowerBadge)) {
                    assertTrue("Working move '${m.name}' in ${group.id} must have >=3 sets", m.totalSets >= 3)
                    val swaps = com.example.ui.screens.buildQuickStrikeVariantsList(m)
                    assertEquals("Expected 3 distinct swap options for '${m.name}' in ${group.id}", 3, swaps.size)
                }
                val guide = com.example.domain.ExerciseZeroToHeroGuide.resolveGuide(m.name, m.targetMusclePill)
                assertEquals(4, guide.zeroToHeroLadder.size)
                assertTrue(guide.plainPurpose.isNotBlank())
            }
        }
    }
}

