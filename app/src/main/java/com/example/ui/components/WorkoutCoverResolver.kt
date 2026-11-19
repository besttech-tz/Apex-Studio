package com.example.ui.components

import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.local.TrainingEnvironment
import com.example.domain.BiologicalSex
import com.example.domain.CombatDaySpec
import com.example.domain.QuickStrikeWorkout

/**
 * Resolves 100% gender-matched studio athletic photography across Splash, Onboarding,
 * Dashboard, Workout Hub, Quick/Challenge Packs, Rest Days, and Progress.
 */
object WorkoutCoverResolver {

    @DrawableRes
    fun bannerForSplash(sex: BiologicalSex): Int {
        return if (sex == BiologicalSex.FEMALE) {
            R.drawable.img_female_stretch_1795026870917
        } else {
            R.drawable.img_hero_idol_splash_1794508264671
        }
    }

    @DrawableRes
    fun bannerForWorkoutHub(sex: BiologicalSex): Int {
        return if (sex == BiologicalSex.FEMALE) {
            R.drawable.img_cover_back_shoulders_1794761630996
        } else {
            R.drawable.img_workout_hub_banner_1794761668870
        }
    }

    @DrawableRes
    fun bannerForProgress(sex: BiologicalSex): Int {
        return if (sex == BiologicalSex.FEMALE) {
            R.drawable.img_female_stretch_1795026870917
        } else {
            R.drawable.img_cover_raw_strength_1794572460981
        }
    }

    @DrawableRes
    fun coverForRestDay(sex: BiologicalSex): Int {
        return if (sex == BiologicalSex.FEMALE) {
            R.drawable.img_female_stretch_1795026870917
        } else {
            R.drawable.img_cover_posture_stretch_1794570082806
        }
    }

    @DrawableRes
    fun bannerForOnboardingStep(
        step: Int,
        sex: BiologicalSex,
        environment: TrainingEnvironment = TrainingEnvironment.GYM
    ): Int {
        return if (sex == BiologicalSex.FEMALE) {
            when (step) {
                1 -> R.drawable.img_female_stretch_1795026870917
                2 -> R.drawable.img_cover_leg_day_power_1794761630544
                3 -> R.drawable.img_cover_back_shoulders_1794761630996
                4 -> if (environment == TrainingEnvironment.HOME) {
                    R.drawable.img_female_home_1795026870729
                } else {
                    R.drawable.img_cover_leg_day_power_1794761630544
                }
                else -> R.drawable.img_female_stretch_1795026870917
            }
        } else {
            when (step) {
                1 -> R.drawable.img_workout_hub_banner_1794761668870
                2 -> R.drawable.img_cover_chest_arms_1794570080759
                3 -> R.drawable.img_male_back_1795026866340
                4 -> if (environment == TrainingEnvironment.HOME) {
                    R.drawable.img_cover_home_wakeup_1794570079145
                } else {
                    R.drawable.img_cover_gym_program_1794570078368
                }
                else -> R.drawable.img_workout_hub_banner_1794761668870
            }
        }
    }

    @DrawableRes
    fun coverForQuickWorkout(
        workout: QuickStrikeWorkout,
        sex: BiologicalSex = BiologicalSex.MALE
    ): Int {
        if (sex == BiologicalSex.FEMALE) {
            if (workout.id.startsWith("target_ex_")) {
                val idLow = workout.id.lowercase()
                return when {
                    "calisthenics" in idLow || "handstand" in idLow -> R.drawable.img_calisthenics_female_handstand_1795114083854
                    "glutes" in idLow || "quads" in idLow || "hamstrings" in idLow || "calves" in idLow -> R.drawable.img_cover_leg_day_power_1794761630544
                    "abs" in idLow || "waist" in idLow || "kegel" in idLow -> R.drawable.img_cover_abs_core_1794570081800
                    "flexibility" in idLow || "height" in idLow || "jaw" in idLow || "voice" in idLow -> R.drawable.img_female_stretch_1795026870917
                    "stamina" in idLow -> R.drawable.img_female_home_1795026870729
                    else -> R.drawable.img_cover_back_shoulders_1794761630996
                }
            }
            return when (workout.id) {
                // 7-Day, 14-Day, 21-Day & 28-Day Plans (Female)
                "challenge_7d_fat_ignition" -> R.drawable.img_female_home_1795026870729
                "challenge_14d_fat_burnout" -> R.drawable.img_cover_abs_core_1794570081800
                "challenge_21d_waist_shred" -> R.drawable.img_cover_leg_day_power_1794761630544
                "challenge_7d_core_waist" -> R.drawable.img_cover_abs_core_1794570081800
                "challenge_7d_pushup_chest" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_7d_booty_lift" -> R.drawable.img_cover_leg_day_power_1794761630544
                "challenge_7d_toned_arms_collarbone" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_7d_joint_balance_knees" -> R.drawable.img_female_home_1795026870729
                "challenge_14d_hips_wider_curves" -> R.drawable.img_cover_leg_day_power_1794761630544
                "challenge_14d_flat_tummy_corset" -> R.drawable.img_cover_abs_core_1794570081800
                "challenge_14d_flexibility_mobility" -> R.drawable.img_female_stretch_1795026870917
                "challenge_14d_posture_height" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_14d_kegel_pelvic" -> R.drawable.img_cover_abs_core_1794570081800
                "challenge_14d_strength_power" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_14d_pullup_vtaper_mastery" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_14d_calisthenics_mastery" -> R.drawable.img_calisthenics_female_bars_1795114084511
                "challenge_14d_handstand_lsit_skill" -> R.drawable.img_calisthenics_female_handstand_1795114083854
                "challenge_28d_calisthenics_elite_skills" -> R.drawable.img_calisthenics_female_bars_1795114084511
                "challenge_14d_stamina_endurance" -> R.drawable.img_female_home_1795026870729
                "challenge_28d_full_body" -> R.drawable.img_cover_back_shoulders_1794761630996
                "challenge_28d_home_lean" -> R.drawable.img_female_home_1795026870729
                "challenge_28d_hourglass_curves" -> R.drawable.img_cover_leg_day_power_1794761630544
                "challenge_28d_female_gym_curves" -> R.drawable.img_cover_leg_day_power_1794761630544

                // Female Shaping, Booty, Arms, Calisthenics & Slimming Packs
                "track_glute_shelf_sculpt" -> R.drawable.img_cover_leg_day_power_1794761630544
                "track_female_dumbbell_curves" -> R.drawable.img_cover_leg_day_power_1794761630544
                "track_female_home_booty_sculpt" -> R.drawable.img_female_home_1795026870729
                "track_female_calisthenics_sculpt" -> R.drawable.img_calisthenics_female_handstand_1795114083854
                "track_female_toned_arms_posture" -> R.drawable.img_cover_back_shoulders_1794761630996
                "track_female_home_arms_posture" -> R.drawable.img_female_stretch_1795026870917
                "track_female_corset_waist" -> R.drawable.img_cover_abs_core_1794570081800
                "track_female_gym_fat_burn_sculpt" -> R.drawable.img_cover_back_shoulders_1794761630996
                "quick_slim_waist_legs" -> R.drawable.img_cover_abs_core_1794570081800

                // 4 Distinct Goal-Promise Covers for Female Calisthenics Stages 1–4!
                "track_calisthenics_stage1_foundation" -> R.drawable.img_female_home_1795026870729
                "track_gym_calisthenics_hybrid" -> R.drawable.img_cover_back_shoulders_1794761630996
                "track_home_calisthenics_strength" -> R.drawable.img_calisthenics_female_bars_1795114084511
                "track_calisthenics_stage4_elite" -> R.drawable.img_calisthenics_female_handstand_1795114083854

                // Shared Packs Viewed by Female Users -> 100% Female Studio Covers
                "track_zero_jump_fat_burn" -> R.drawable.img_female_home_1795026870729
                "track_calisthenics_fat_burn_flow" -> R.drawable.img_calisthenics_female_bars_1795114084511
                "track_gym_athletic_stamina" -> R.drawable.img_cover_leg_day_power_1794761630544
                "track_gym_functional_strength" -> R.drawable.img_cover_back_shoulders_1794761630996
                "track_grip_power" -> R.drawable.img_cover_back_shoulders_1794761630996
                "track_raw_strength" -> R.drawable.img_cover_leg_day_power_1794761630544
                "track_pelvic_kegel" -> R.drawable.img_cover_abs_core_1794570081800
                "track_full_body_evening_stretch" -> R.drawable.img_female_stretch_1795026870917

                // Quick Strike & Habit Rescue (Female)
                "rescue_5min_no_zero_day" -> R.drawable.img_female_home_1795026870729
                "quick_home_wake_up" -> R.drawable.img_female_home_1795026870729
                else -> R.drawable.img_cover_leg_day_power_1794761630544
            }
        }

        // Male Studio Covers (Zero cross-gender female covers + 4 distinct Calisthenics Stage 1-4 covers!)
        if (workout.id.startsWith("target_ex_")) {
            val idLow = workout.id.lowercase()
            return when {
                "calisthenics" in idLow -> R.drawable.img_calisthenics_male_planche_1795114083910
                "grip" in idLow -> R.drawable.img_cover_grip_power_1794572439024
                "back" in idLow || "height" in idLow -> R.drawable.img_male_back_1795026866340
                "chest" in idLow || "shoulders" in idLow || "arms" in idLow -> R.drawable.img_cover_chest_arms_1794570080759
                "abs" in idLow || "waist" in idLow -> R.drawable.img_male_abs_1795026870074
                "kegel" in idLow -> R.drawable.img_cover_core_vitality_1794572439738
                "quads" in idLow || "glutes" in idLow || "hamstrings" in idLow || "calves" in idLow -> R.drawable.img_male_legs_1795026867840
                "flexibility" in idLow || "jaw" in idLow || "voice" in idLow -> R.drawable.img_cover_posture_stretch_1794570082806
                "stamina" in idLow -> R.drawable.img_workout_hub_banner_1794761668870
                else -> R.drawable.img_cover_gym_program_1794570078368
            }
        }

        return when (workout.id) {
            "challenge_7d_fat_ignition" -> R.drawable.img_cover_home_wakeup_1794570079145
            "challenge_14d_fat_burnout" -> R.drawable.img_cover_raw_strength_1794572460981
            "challenge_21d_waist_shred" -> R.drawable.img_male_abs_1795026870074
            "challenge_7d_core_waist" -> R.drawable.img_male_abs_1795026870074
            "challenge_7d_pushup_chest" -> R.drawable.img_cover_chest_arms_1794570080759
            "challenge_7d_booty_lift" -> R.drawable.img_male_legs_1795026867840
            "challenge_7d_toned_arms_collarbone" -> R.drawable.img_cover_chest_arms_1794570080759
            "challenge_7d_joint_balance_knees" -> R.drawable.img_male_legs_1795026867840
            "challenge_14d_hips_wider_curves" -> R.drawable.img_male_legs_1795026867840
            "challenge_14d_flat_tummy_corset" -> R.drawable.img_male_abs_1795026870074
            "challenge_14d_flexibility_mobility" -> R.drawable.img_cover_posture_stretch_1794570082806
            "challenge_14d_posture_height" -> R.drawable.img_male_back_1795026866340
            "challenge_14d_kegel_pelvic" -> R.drawable.img_cover_core_vitality_1794572439738
            "challenge_14d_strength_power" -> R.drawable.img_cover_raw_strength_1794572460981
            "challenge_14d_pullup_vtaper_mastery" -> R.drawable.img_male_back_1795026866340
            "challenge_14d_calisthenics_mastery" -> R.drawable.img_calisthenics_male_bars_1795114084816
            "challenge_14d_handstand_lsit_skill" -> R.drawable.img_calisthenics_male_planche_1795114083910
            "challenge_28d_calisthenics_elite_skills" -> R.drawable.img_calisthenics_male_bars_1795114084816
            "challenge_14d_stamina_endurance" -> R.drawable.img_workout_hub_banner_1794761668870
            "challenge_28d_full_body" -> R.drawable.img_cover_gym_program_1794570078368
            "challenge_28d_home_lean" -> R.drawable.img_cover_home_wakeup_1794570079145
            "challenge_28d_hourglass_curves" -> R.drawable.img_male_legs_1795026867840
            "challenge_28d_female_gym_curves" -> R.drawable.img_male_legs_1795026867840

            "track_chest_power" -> R.drawable.img_cover_chest_arms_1794570080759
            "track_wide_back" -> R.drawable.img_male_back_1795026866340
            "track_leg_drive" -> R.drawable.img_male_legs_1795026867840
            "track_male_dumbbell_upper_lower" -> R.drawable.img_cover_gym_program_1794570078368
            "track_male_home_legs_glutes" -> R.drawable.img_male_legs_1795026867840
            "track_arms_3d_delts" -> R.drawable.img_cover_chest_arms_1794570080759
            "track_male_home_arms_delts" -> R.drawable.img_cover_chest_arms_1794570080759
            "track_male_gym_sixpack_core" -> R.drawable.img_male_abs_1795026870074
            "track_male_gym_fat_furnace" -> R.drawable.img_cover_raw_strength_1794572460981
            "track_zero_jump_fat_burn" -> R.drawable.img_cover_home_wakeup_1794570079145

            // 4 Distinct Goal-Promise Covers for Male Calisthenics Stages 1–4!
            "track_calisthenics_stage1_foundation" -> R.drawable.img_cover_home_wakeup_1794570079145
            "track_gym_calisthenics_hybrid" -> R.drawable.img_male_back_1795026866340
            "track_home_calisthenics_strength" -> R.drawable.img_calisthenics_male_bars_1795114084816
            "track_calisthenics_stage4_elite" -> R.drawable.img_calisthenics_male_planche_1795114083910

            "track_calisthenics_fat_burn_flow" -> R.drawable.img_cover_chest_arms_1794570080759
            "track_gym_athletic_stamina" -> R.drawable.img_workout_hub_banner_1794761668870
            "track_gym_functional_strength" -> R.drawable.img_cover_raw_strength_1794572460981
            "track_grip_power" -> R.drawable.img_cover_grip_power_1794572439024
            "track_raw_strength" -> R.drawable.img_cover_raw_strength_1794572460981
            "track_pelvic_kegel" -> R.drawable.img_cover_core_vitality_1794572439738
            "track_full_body_evening_stretch" -> R.drawable.img_cover_posture_stretch_1794570082806

            "rescue_5min_no_zero_day" -> R.drawable.img_cover_home_wakeup_1794570079145
            "quick_home_wake_up" -> R.drawable.img_cover_home_wakeup_1794570079145
            else -> R.drawable.img_cover_gym_program_1794570078368
        }
    }

    @DrawableRes
    fun coverForScheduledDay(
        day: CombatDaySpec,
        environment: TrainingEnvironment,
        sex: BiologicalSex = BiologicalSex.MALE
    ): Int {
        val lowerTitle = day.title.lowercase()
        val isLowerFocus = lowerTitle.contains("leg") ||
            lowerTitle.contains("glute") ||
            lowerTitle.contains("lower") ||
            lowerTitle.contains("quad") ||
            lowerTitle.contains("hamstring")
        val isPullBackFocus = lowerTitle.contains("back") ||
            lowerTitle.contains("pull") ||
            lowerTitle.contains("lat")
        val isCoreFocus = lowerTitle.contains("stomach") ||
            lowerTitle.contains("core") ||
            lowerTitle.contains("abs") ||
            lowerTitle.contains("waist")
        val isShoulderArm = lowerTitle.contains("shoulder") || lowerTitle.contains("arm")

        if (sex == BiologicalSex.FEMALE) {
            return if (environment == TrainingEnvironment.HOME) {
                when {
                    isLowerFocus -> R.drawable.img_female_home_1795026870729
                    isCoreFocus -> R.drawable.img_cover_abs_core_1794570081800
                    isPullBackFocus || isShoulderArm -> R.drawable.img_female_stretch_1795026870917
                    else -> R.drawable.img_female_home_1795026870729
                }
            } else {
                when {
                    isLowerFocus -> R.drawable.img_cover_leg_day_power_1794761630544
                    isCoreFocus -> R.drawable.img_cover_abs_core_1794570081800
                    isPullBackFocus || isShoulderArm -> R.drawable.img_cover_back_shoulders_1794761630996
                    else -> R.drawable.img_cover_leg_day_power_1794761630544
                }
            }
        }

        // Male Scheduled Day Studio Covers
        return if (environment == TrainingEnvironment.HOME) {
            when {
                isLowerFocus -> R.drawable.img_male_legs_1795026867840
                isCoreFocus -> R.drawable.img_male_abs_1795026870074
                isPullBackFocus -> R.drawable.img_male_back_1795026866340
                else -> R.drawable.img_cover_chest_arms_1794570080759
            }
        } else {
            when {
                isLowerFocus -> R.drawable.img_male_legs_1795026867840
                isPullBackFocus -> R.drawable.img_male_back_1795026866340
                isCoreFocus -> R.drawable.img_male_abs_1795026870074
                isShoulderArm -> R.drawable.img_cover_raw_strength_1794572460981
                else -> R.drawable.img_cover_gym_program_1794570078368
            }
        }
    }
}
