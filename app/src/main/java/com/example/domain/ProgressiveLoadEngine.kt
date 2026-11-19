package com.example.domain

import kotlin.math.roundToInt

data class PrescribedExerciseTarget(
    val sets: Int,
    val repsOrSeconds: Int,
    val isTimedSeconds: Boolean,
    val loadKg: Float,
    val repPrescription: String,
    val setsAndRepsLabel: String,
    val progressionBadge: String,
    val trackWeek: Int = 1
)

/**
 * Explicit 4-Week Visible Transformation Track descriptor:
 * - Week 1: Foundation (clean form, manageable base reps & starting weight)
 * - Week 2: Rep Boost (+2 reps per set on every movement to build volume)
 * - Week 3: Fresh Angles + Load (rotates exercise variations to hit new muscle fibers + steps up weight)
 * - Week 4: Peak Sculpt (rotated exercises + peak reps & sets for visible 4-week change)
 */
data class FourWeekTrackStatus(
    val currentAutoWeek: Int,       // 1..4 based on completedWorkoutsCount
    val activeViewedWeek: Int,      // 1..4 (either auto or user-inspected week on track)
    val rotationIndex: Int,         // 0 for Weeks 1–2, 1 for Weeks 3–4 (fresh exercise angles!)
    val cycleNumber: Int,           // 1, 2, 3... for multi-month users
    val completedDaysInCycle: Int,
    val totalDaysInCycle: Int,
    val progressFraction: Float,
    val weekHeadline: String,
    val weekRuleSubtitle: String
)

object ProgressiveLoadEngine {

    fun computeFourWeekTrack(
        completedWorkoutsCount: Int,
        daysPerWeek: Int,
        viewedWeekOverride: Int? = null
    ): FourWeekTrackStatus {
        val safeDaysPerWeek = daysPerWeek.coerceIn(3, 6)
        val totalCycleDays = safeDaysPerWeek * 4
        val safeCompleted = completedWorkoutsCount.coerceAtLeast(0)
        val cycleNumber = (safeCompleted / totalCycleDays) + 1
        val completedInCycle = safeCompleted % totalCycleDays
        val autoWeek = ((completedInCycle / safeDaysPerWeek) + 1).coerceIn(1, 4)
        val activeWeek = (viewedWeekOverride ?: autoWeek).coerceIn(1, 4)
        val rotationIndex = if (activeWeek <= 2) 0 else 1

        val (headline, ruleSubtitle) = when (activeWeek) {
            1 -> "Week 1 of 4 • Foundation" to "Lock in clean form with steady base reps"
            2 -> "Week 2 of 4 • Rep Boost" to "+2 reps per set to build endurance & volume"
            3 -> "Week 3 of 4 • Fresh Angles" to "New exercise variations + stepped-up load"
            else -> "Week 4 of 4 • Peak Sculpt" to "Peak sets & reps for visible 4-week change"
        }

        val fraction = if (totalCycleDays > 0) {
            (completedInCycle.toFloat() / totalCycleDays.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }

        return FourWeekTrackStatus(
            currentAutoWeek = autoWeek,
            activeViewedWeek = activeWeek,
            rotationIndex = rotationIndex,
            cycleNumber = cycleNumber,
            completedDaysInCycle = completedInCycle,
            totalDaysInCycle = totalCycleDays,
            progressFraction = fraction,
            weekHeadline = headline,
            weekRuleSubtitle = ruleSubtitle
        )
    }

    fun computeTarget(
        defaultLoadKg: Float,
        specTotalSets: Int,
        baseRepsOrSeconds: Int,
        isTimedSeconds: Boolean,
        isBodyweight: Boolean,
        bodyweightKg: Float,
        sex: BiologicalSex,
        experience: ExperienceLevel,
        completedWorkoutsCount: Int,
        manualOffsetKg: Float = 0f,
        daysPerWeek: Int = 5,
        trackWeekOverride: Int? = null,
        intensityMode: WorkoutIntensityMode = WorkoutIntensityMode.MODERATE
    ): PrescribedExerciseTarget {
        val safeSessions = completedWorkoutsCount.coerceAtLeast(0)
        val track = computeFourWeekTrack(
            completedWorkoutsCount = safeSessions,
            daysPerWeek = daysPerWeek,
            viewedWeekOverride = trackWeekOverride
        )
        val activeWeek = track.activeViewedWeek

        // 1. Progressive Working Sets across the 4-Week Track + Simple/Moderate/Extensive scaling
        // Beginners adapt via safe lighter weights (0.36x) and joint-friendly angles — never tiny 2-set exercises.
        val baseSetsForLevel = when (experience) {
            ExperienceLevel.NEW_BEGINNER -> {
                if (activeWeek >= 4 && specTotalSets >= 4) 4 else 3
            }
            ExperienceLevel.BEGINNER_JOINT_SAFE -> {
                if (activeWeek >= 3 && specTotalSets >= 4) 4 else 3
            }
            ExperienceLevel.INTERMEDIATE -> {
                specTotalSets.coerceIn(3, 4)
            }
        }
        val prescribedSets = when (intensityMode) {
            WorkoutIntensityMode.SIMPLE -> 3
            WorkoutIntensityMode.MODERATE -> baseSetsForLevel.coerceIn(3, 4)
            WorkoutIntensityMode.EXTENSIVE -> (baseSetsForLevel + 1).coerceIn(3, 4)
        }

        // 2. Explicit Week-by-Week Rep / Hold Progression + Intensity Mode adjustment
        val weekRepDelta = when (activeWeek) {
            1 -> 0
            2 -> 2
            3 -> 1
            else -> 3
        } + intensityMode.repDelta

        val weekSecDelta = when (activeWeek) {
            1 -> 0
            2 -> 5
            3 -> 5
            else -> 10
        } + (intensityMode.repDelta * 3)

        val prescribedCount = if (isTimedSeconds) {
            when (experience) {
                ExperienceLevel.NEW_BEGINNER -> {
                    val startSec = (baseRepsOrSeconds * 0.70f).roundToInt().coerceIn(20, 30)
                    (startSec + weekSecDelta).coerceIn(18, 60)
                }
                ExperienceLevel.BEGINNER_JOINT_SAFE -> {
                    val startSec = (baseRepsOrSeconds * 0.85f).roundToInt().coerceAtLeast(25)
                    (startSec + weekSecDelta).coerceIn(20, 65)
                }
                ExperienceLevel.INTERMEDIATE -> {
                    (baseRepsOrSeconds + weekSecDelta).coerceIn(25, 70)
                }
            }
        } else {
            when (experience) {
                ExperienceLevel.NEW_BEGINNER -> {
                    val base = if (isBodyweight) {
                        (baseRepsOrSeconds - 2).coerceIn(6, 8)
                    } else {
                        baseRepsOrSeconds.coerceIn(8, 10)
                    }
                    (base + weekRepDelta).coerceIn(if (isBodyweight) 5 else 7, 15)
                }
                ExperienceLevel.BEGINNER_JOINT_SAFE -> {
                    val base = baseRepsOrSeconds.coerceIn(9, 11)
                    (base + weekRepDelta).coerceIn(8, 16)
                }
                ExperienceLevel.INTERMEDIATE -> {
                    val base = baseRepsOrSeconds.coerceIn(8, 12)
                    (base + weekRepDelta).coerceIn(8, 16)
                }
            }
        }

        // 3. Bodyweight-Scaled Safe Load + 4-Week Load Steps + Simple/Moderate/Extensive Load Multiplier
        val safeLoadKg = if (isBodyweight || defaultLoadKg <= 0f) {
            0f
        } else {
            val referenceWeight = if (sex == BiologicalSex.FEMALE) 62f else 80f
            val bodyRatio = (bodyweightKg / referenceWeight).coerceIn(0.65f, 1.25f)

            val levelMultiplier = when (experience) {
                ExperienceLevel.NEW_BEGINNER -> 0.36f
                ExperienceLevel.BEGINNER_JOINT_SAFE -> 0.68f
                ExperienceLevel.INTERMEDIATE -> 1.00f
            }

            val stepIncrementKg = when {
                defaultLoadKg >= 40f -> 2.5f
                defaultLoadKg <= 12f -> 0.5f
                else -> 1.0f
            }
            val completedCyclesBonus = ((track.cycleNumber - 1).coerceAtMost(4)) * (stepIncrementKg * 2f)
            val weekLoadSteps = when (activeWeek) {
                1 -> 0
                2 -> if (safeSessions >= 4) 1 else 0
                3 -> 1
                else -> 2
            }
            val trackBonusKg = completedCyclesBonus + (weekLoadSteps * stepIncrementKg)

            val rawKg = ((defaultLoadKg * bodyRatio * levelMultiplier) + trackBonusKg) * intensityMode.loadMultiplier + manualOffsetKg
            val floorKg = when {
                defaultLoadKg <= 12f -> 2f
                defaultLoadKg <= 24f -> 3f
                else -> 5f
            }
            val clamped = rawKg.coerceAtLeast(floorKg)

            if (clamped < 15f) {
                clamped.roundToInt().toFloat()
            } else {
                ((clamped / 2.5f).roundToInt() * 2.5f)
            }
        }

        val repLabel = if (isTimedSeconds) {
            String.format("%02d:%02d", prescribedCount / 60, prescribedCount % 60)
        } else {
            "$prescribedCount reps"
        }

        val setsRepsLabel = if (isTimedSeconds) {
            "$prescribedSets Sets × ${prescribedCount}s"
        } else {
            "$prescribedSets Sets × $prescribedCount"
        }

        val badge = "${intensityMode.label} • $prescribedSets Sets"

        return PrescribedExerciseTarget(
            sets = prescribedSets,
            repsOrSeconds = prescribedCount,
            isTimedSeconds = isTimedSeconds,
            loadKg = safeLoadKg,
            repPrescription = repLabel,
            setsAndRepsLabel = setsRepsLabel,
            progressionBadge = badge,
            trackWeek = activeWeek
        )
    }

    fun weightStepDeltaKg(currentLoadKg: Float): Float =
        if (currentLoadKg > 0f && currentLoadKg <= 12f) 1f else 2f

    fun estimateSessionCalories(
        intensityMode: WorkoutIntensityMode,
        isHomeMode: Boolean,
        bodyweightKg: Float
    ): Int {
        val weightScale = (bodyweightKg / 78f).coerceIn(0.75f, 1.35f)
        val envFactor = if (isHomeMode) 0.90f else 1.0f
        return ((intensityMode.baseKcalBurn * weightScale * envFactor) / 5f).roundToInt() * 5
    }

    fun scaleStandaloneChallengeMove(
        baseSets: Int,
        baseRepsOrSec: Int,
        isTimedSeconds: Boolean,
        challengeLevel: ExperienceLevel
    ): Pair<Int, Int> {
        val scaledSets = if (baseSets <= 1) {
            1
        } else {
            when (challengeLevel) {
                ExperienceLevel.NEW_BEGINNER -> 3
                ExperienceLevel.BEGINNER_JOINT_SAFE -> baseSets.coerceIn(3, 4)
                ExperienceLevel.INTERMEDIATE -> 4
            }
        }
        val scaledCount = if (isTimedSeconds) {
            when (challengeLevel) {
                ExperienceLevel.NEW_BEGINNER -> (baseRepsOrSec * 0.70f).roundToInt().coerceAtLeast(18)
                ExperienceLevel.BEGINNER_JOINT_SAFE -> baseRepsOrSec
                ExperienceLevel.INTERMEDIATE -> (baseRepsOrSec + 10).coerceAtMost(60)
            }
        } else {
            when (challengeLevel) {
                ExperienceLevel.NEW_BEGINNER -> (baseRepsOrSec - 4).coerceIn(6, 10)
                ExperienceLevel.BEGINNER_JOINT_SAFE -> baseRepsOrSec
                ExperienceLevel.INTERMEDIATE -> (baseRepsOrSec + 4).coerceAtMost(20)
            }
        }
        return scaledSets to scaledCount
    }

    fun formatWeightShort(loadKg: Float, isBodyweight: Boolean = false): String {
        if (isBodyweight || loadKg <= 0f) return "Bodyweight"
        return if (loadKg % 1f == 0f) {
            "${loadKg.roundToInt()} kg"
        } else {
            "${"%.1f".format(loadKg)} kg"
        }
    }
}
