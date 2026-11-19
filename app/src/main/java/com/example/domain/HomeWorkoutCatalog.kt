package com.example.domain

import com.example.data.local.TrainingEnvironment

enum class BodyFocusCategory(
    val id: String,
    val label: String,
    val subtitle: String,
    val durationMinutes: Int,
    val caloriesEstimate: Int,
    val levelBadge: String
) {
    ABS(
        id = "ABS",
        label = "Abs",
        subtitle = "Core definition, lower abs & waist tightening",
        durationMinutes = 14,
        caloriesEstimate = 135,
        levelBadge = "CORE & ABS"
    ),
    ARM(
        id = "ARM",
        label = "Arm",
        subtitle = "Triceps, biceps & forearm sculpting",
        durationMinutes = 12,
        caloriesEstimate = 115,
        levelBadge = "ARMS & SHOULDERS"
    ),
    CHEST(
        id = "CHEST",
        label = "Chest",
        subtitle = "Progressive push-up & press mastery",
        durationMinutes = 15,
        caloriesEstimate = 150,
        levelBadge = "UPPER CHEST"
    ),
    LEG(
        id = "LEG",
        label = "Leg",
        subtitle = "Quads, glutes, hamstrings & calf endurance",
        durationMinutes = 16,
        caloriesEstimate = 175,
        levelBadge = "LOWER BODY"
    ),
    SHOULDER_BACK(
        id = "SHOULDER_BACK",
        label = "Shoulder & Back",
        subtitle = "V-taper posture, rear delts & upper back width",
        durationMinutes = 14,
        caloriesEstimate = 140,
        levelBadge = "SHOULDER & BACK"
    )
}

enum class ExerciseAnimationType {
    JUMPING_JACKS,
    INCLINE_PUSH_UPS,
    KNEE_PUSH_UPS,
    STANDARD_PUSH_UPS,
    ABDOMINAL_CRUNCHES,
    PLANK_HOLD,
    BODYWEIGHT_SQUATS,
    FORWARD_LUNGES,
    TRICEP_DIPS,
    PIKE_SHOULDER_PRESS,
    COBRA_STRETCH,
    INCLINE_DB_PRESS,
    LAT_PULLDOWN,
    SEATED_CABLE_ROW,
    CABLE_LATERAL_RAISE,
    INCLINE_DB_CURL,
    DOORFRAME_ROW
}

data class HomeExerciseItem(
    val id: String,
    val name: String, // Default Home (Bodyweight) name
    val targetDisplay: String, // e.g. "00:20", "x12", "x10"
    val isTimedSeconds: Boolean,
    val targetValue: Int,
    val progressionStageLabel: String, // e.g. "1. Warm-up", "2. Activation / Regression", "3. Main Target Movement", "4. Core Burnout / Finisher"
    val animationType: ExerciseAnimationType,
    val primaryMuscle: String,
    val formCues: List<String>,
    val swapAlternativeName: String, // Gym (Machine / Cable) alternative name
    val swapAlternativeTargetDisplay: String,
    val swapAlternativeValue: Int
)

data class HomeWorkoutPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationMinutes: Int,
    val caloriesKcal: Int,
    val badgeText: String,
    val exercises: List<HomeExerciseItem>
)

object HomeWorkoutCatalog {

    val CHALLENGE_DAY_1_PLAN = HomeWorkoutPlan(
        id = "SIX_PACK_PROGRESSION",
        title = "6-PACK PROGRESSION",
        subtitle = "Step-by-step core & upper-body progression ladder",
        durationMinutes = 14,
        caloriesKcal = 135,
        badgeText = "CORE & ABS",
        exercises = listOf(
            HomeExerciseItem(
                id = "ex_jumping_jacks",
                name = "Jumping Jacks",
                targetDisplay = "00:20",
                isTimedSeconds = true,
                targetValue = 20,
                progressionStageLabel = "1. Warm-up",
                animationType = ExerciseAnimationType.JUMPING_JACKS,
                primaryMuscle = "Full Body Warm-Up",
                formCues = listOf(
                    "Land softly on the balls of your feet",
                    "Sweep arms overhead in rhythm with your stride",
                    "Keep core braced throughout the warm-up"
                ),
                swapAlternativeName = "Cable Rope / Rower Warm-Up (Gym)",
                swapAlternativeTargetDisplay = "00:20",
                swapAlternativeValue = 20
            ),
            HomeExerciseItem(
                id = "ex_incline_pushups",
                name = "Incline Push-Ups",
                targetDisplay = "x12",
                isTimedSeconds = false,
                targetValue = 12,
                progressionStageLabel = "2. Activation / Regression",
                animationType = ExerciseAnimationType.INCLINE_PUSH_UPS,
                primaryMuscle = "Upper Core & Chest Stabilization",
                formCues = listOf(
                    "Place hands shoulder-width on a bench or elevated surface",
                    "Hold a rigid plank line from shoulders to ankles",
                    "Lower chest with control and press smoothly to lockout"
                ),
                swapAlternativeName = "Kneeling Cable Rope Crunch (Gym)",
                swapAlternativeTargetDisplay = "x12",
                swapAlternativeValue = 12
            ),
            HomeExerciseItem(
                id = "ex_ab_crunches",
                name = "Abdominal Crunches",
                targetDisplay = "x12",
                isTimedSeconds = false,
                targetValue = 12,
                progressionStageLabel = "3. Main Target Movement",
                animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES,
                primaryMuscle = "Upper & Mid Rectus Abdominis",
                formCues = listOf(
                    "Press lower back flat into the floor with knees bent",
                    "Curl shoulder blades up and squeeze abs for 1 full second",
                    "Lower slowly under full abdominal tension"
                ),
                swapAlternativeName = "Captain's Chair Leg Raise (Gym)",
                swapAlternativeTargetDisplay = "x12",
                swapAlternativeValue = 12
            ),
            HomeExerciseItem(
                id = "ex_plank_hold",
                name = "Forearm Plank Hold",
                targetDisplay = "00:30",
                isTimedSeconds = true,
                targetValue = 30,
                progressionStageLabel = "4. Core Burnout / Finisher",
                animationType = ExerciseAnimationType.PLANK_HOLD,
                primaryMuscle = "Deep Transverse Abdominis",
                formCues = listOf(
                    "Stack elbows directly beneath your shoulders",
                    "Brace abs and glutes with zero hip sag",
                    "Breathe steadily while holding rigid tension"
                ),
                swapAlternativeName = "Weighted Cable Plank Hold (Gym)",
                swapAlternativeTargetDisplay = "00:30",
                swapAlternativeValue = 30
            ),
            HomeExerciseItem(
                id = "ex_cobra_stretch",
                name = "Cobra Abdominal Stretch",
                targetDisplay = "00:20",
                isTimedSeconds = true,
                targetValue = 20,
                progressionStageLabel = "5. Cool-down & Mobility",
                animationType = ExerciseAnimationType.COBRA_STRETCH,
                primaryMuscle = "Abdominal Wall Decompression",
                formCues = listOf(
                    "Place hands under shoulders and gently lift chest",
                    "Keep hips relaxed on the mat and shoulders down",
                    "Take slow deep breaths to lengthen the core"
                ),
                swapAlternativeName = "Incline Bench Torso Stretch (Gym)",
                swapAlternativeTargetDisplay = "00:20",
                swapAlternativeValue = 20
            )
        )
    )

    fun getPlanForCategory(category: BodyFocusCategory): HomeWorkoutPlan {
        return when (category) {
            BodyFocusCategory.ABS -> CHALLENGE_DAY_1_PLAN
            BodyFocusCategory.CHEST -> HomeWorkoutPlan(
                id = "PLAN_CHEST",
                title = "CHEST PROGRESSION",
                subtitle = "Step-by-step push-up & press progression ladder",
                durationMinutes = 15,
                caloriesKcal = 150,
                badgeText = "UPPER CHEST",
                exercises = listOf(
                    CHALLENGE_DAY_1_PLAN.exercises[0],
                    HomeExerciseItem(
                        id = "chest_incline_pushup",
                        name = "Incline Push-Ups",
                        targetDisplay = "x12",
                        isTimedSeconds = false,
                        targetValue = 12,
                        progressionStageLabel = "2. Activation / Regression",
                        animationType = ExerciseAnimationType.INCLINE_PUSH_UPS,
                        primaryMuscle = "Lower & Mid Pectorals",
                        formCues = listOf(
                            "Hands slightly wider than shoulders on a bench",
                            "Lower chest with elbows tucked at 45 degrees"
                        ),
                        swapAlternativeName = "30° Incline Dumbbell Press (Gym)",
                        swapAlternativeTargetDisplay = "x10",
                        swapAlternativeValue = 10
                    ),
                    HomeExerciseItem(
                        id = "chest_standard_pushup",
                        name = "Standard Push-Ups",
                        targetDisplay = "x10",
                        isTimedSeconds = false,
                        targetValue = 10,
                        progressionStageLabel = "3. Main Target Movement",
                        animationType = ExerciseAnimationType.STANDARD_PUSH_UPS,
                        primaryMuscle = "Full Pectorals & Triceps",
                        formCues = listOf(
                            "Maintain a straight plank from head to heels",
                            "Drive palms firmly through the floor to lockout"
                        ),
                        swapAlternativeName = "Seated Machine Chest Press (Gym)",
                        swapAlternativeTargetDisplay = "x10",
                        swapAlternativeValue = 10
                    ),
                    HomeExerciseItem(
                        id = "chest_knee_burnout",
                        name = "Knee Push-Up Burnout",
                        targetDisplay = "x12",
                        isTimedSeconds = false,
                        targetValue = 12,
                        progressionStageLabel = "4. Core Burnout / Finisher",
                        animationType = ExerciseAnimationType.KNEE_PUSH_UPS,
                        primaryMuscle = "Pectoral Burnout",
                        formCues = listOf(
                            "Anchor knees and focus on a 2-second lowering phase",
                            "Squeeze chest at the top of every rep"
                        ),
                        swapAlternativeName = "Cable Pec Fly Burnout (Gym)",
                        swapAlternativeTargetDisplay = "x12",
                        swapAlternativeValue = 12
                    ),
                    CHALLENGE_DAY_1_PLAN.exercises[4]
                )
            )
            BodyFocusCategory.ARM -> HomeWorkoutPlan(
                id = "PLAN_ARM",
                title = "ARM & TRICEP PROGRESSION",
                subtitle = "Sculpt triceps, biceps & shoulders",
                durationMinutes = 12,
                caloriesKcal = 115,
                badgeText = "ARMS & SHOULDERS",
                exercises = listOf(
                    CHALLENGE_DAY_1_PLAN.exercises[0],
                    HomeExerciseItem(
                        id = "arm_tricep_dips",
                        name = "Bench Tricep Dips",
                        targetDisplay = "x12",
                        isTimedSeconds = false,
                        targetValue = 12,
                        progressionStageLabel = "2. Activation / Regression",
                        animationType = ExerciseAnimationType.TRICEP_DIPS,
                        primaryMuscle = "Triceps Brachii",
                        formCues = listOf(
                            "Grip edge of bench and lower until elbows reach 90°",
                            "Press up to full tricep lockout"
                        ),
                        swapAlternativeName = "Cable Rope Tricep Pushdown (Gym)",
                        swapAlternativeTargetDisplay = "x12",
                        swapAlternativeValue = 12
                    ),
                    HomeExerciseItem(
                        id = "arm_diamond_pushups",
                        name = "Close-Grip Push-Ups",
                        targetDisplay = "x10",
                        isTimedSeconds = false,
                        targetValue = 10,
                        progressionStageLabel = "3. Main Target Movement",
                        animationType = ExerciseAnimationType.STANDARD_PUSH_UPS,
                        primaryMuscle = "Triceps & Inner Chest",
                        formCues = listOf(
                            "Keep elbows tucked close to your ribs",
                            "Drive straight up with full arm tension"
                        ),
                        swapAlternativeName = "EZ-Bar / Dumbbell Curl (Gym)",
                        swapAlternativeTargetDisplay = "x10",
                        swapAlternativeValue = 10
                    ),
                    CHALLENGE_DAY_1_PLAN.exercises[3],
                    CHALLENGE_DAY_1_PLAN.exercises[4]
                )
            )
            BodyFocusCategory.LEG -> HomeWorkoutPlan(
                id = "PLAN_LEG",
                title = "LOWER BODY PROGRESSION",
                subtitle = "Quads, glutes & athletic knee stability",
                durationMinutes = 16,
                caloriesKcal = 175,
                badgeText = "LOWER BODY",
                exercises = listOf(
                    CHALLENGE_DAY_1_PLAN.exercises[0],
                    HomeExerciseItem(
                        id = "leg_squats",
                        name = "Bodyweight Squats",
                        targetDisplay = "x15",
                        isTimedSeconds = false,
                        targetValue = 15,
                        progressionStageLabel = "2. Activation / Regression",
                        animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS,
                        primaryMuscle = "Quadriceps & Glutes",
                        formCues = listOf(
                            "Sit hips back and down to parallel depth",
                            "Drive through mid-foot to stand tall"
                        ),
                        swapAlternativeName = "Dumbbell Goblet Squat (Gym)",
                        swapAlternativeTargetDisplay = "x12",
                        swapAlternativeValue = 12
                    ),
                    HomeExerciseItem(
                        id = "leg_lunges",
                        name = "Forward Split Squats",
                        targetDisplay = "x12",
                        isTimedSeconds = false,
                        targetValue = 12,
                        progressionStageLabel = "3. Main Target Movement",
                        animationType = ExerciseAnimationType.FORWARD_LUNGES,
                        primaryMuscle = "Glutes & Hamstrings",
                        formCues = listOf(
                            "Lower back knee toward the mat with tall posture",
                            "Push firmly through front heel"
                        ),
                        swapAlternativeName = "45° Sled Leg Press (Gym)",
                        swapAlternativeTargetDisplay = "x12",
                        swapAlternativeValue = 12
                    ),
                    CHALLENGE_DAY_1_PLAN.exercises[3],
                    CHALLENGE_DAY_1_PLAN.exercises[4]
                )
            )
            BodyFocusCategory.SHOULDER_BACK -> HomeWorkoutPlan(
                id = "PLAN_SHOULDER_BACK",
                title = "V-TAPER PROGRESSION",
                subtitle = "Broaden shoulders & upper back posture",
                durationMinutes = 14,
                caloriesKcal = 140,
                badgeText = "SHOULDER & BACK",
                exercises = listOf(
                    CHALLENGE_DAY_1_PLAN.exercises[0],
                    CHALLENGE_DAY_1_PLAN.exercises[1],
                    HomeExerciseItem(
                        id = "sh_pike_press",
                        name = "Pike Shoulder Push-Ups",
                        targetDisplay = "x10",
                        isTimedSeconds = false,
                        targetValue = 10,
                        progressionStageLabel = "3. Main Target Movement",
                        animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS,
                        primaryMuscle = "Deltoids & Upper Back",
                        formCues = listOf(
                            "Lift hips into an inverted V position",
                            "Press smoothly to full overhead lockout"
                        ),
                        swapAlternativeName = "Wide-Grip Lat Pulldown (Gym)",
                        swapAlternativeTargetDisplay = "x10",
                        swapAlternativeValue = 10
                    ),
                    CHALLENGE_DAY_1_PLAN.exercises[3],
                    CHALLENGE_DAY_1_PLAN.exercises[4]
                )
            )
        }
    }

    val GYM_FOUR_MOVEMENTS = listOf(
        ApexCoachMovementSpec(
            id = "gym_incline_db_press",
            stationOrder = 1,
            name = "30° Incline Dumbbell Press",
            stationBadge = "1. Warm-up & Activation",
            targetMusclePill = "Upper Pectorals & Delts",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 22.5f,
            formCueSummary = "Set bench to 30°. Tuck elbows at 45° and drive dumbbells smoothly up.",
            animationType = ExerciseAnimationType.INCLINE_DB_PRESS
        ),
        ApexCoachMovementSpec(
            id = "gym_lat_pulldown_machine",
            stationOrder = 2,
            name = "Wide-Grip Cable Lat Pulldown",
            stationBadge = "2. Activation / Regression",
            targetMusclePill = "Latissimus Dorsi • V-Taper",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 55.0f,
            formCueSummary = "Depress shoulder blades first, then drive elbows down toward hips.",
            animationType = ExerciseAnimationType.LAT_PULLDOWN
        ),
        ApexCoachMovementSpec(
            id = "gym_chest_supported_row",
            stationOrder = 3,
            name = "Chest-Supported Pin Machine Row",
            stationBadge = "3. Main Target Movement",
            targetMusclePill = "Mid-Back & Rear Delts",
            setsAndRepsLabel = "x10",
            totalSets = 3,
            defaultLoadKg = 50.0f,
            formCueSummary = "Brace sternum against pad and squeeze shoulder blades for 1 second.",
            animationType = ExerciseAnimationType.SEATED_CABLE_ROW
        ),
        ApexCoachMovementSpec(
            id = "gym_leg_press_or_goblet",
            stationOrder = 4,
            name = "Controlled Dumbbell Goblet Squat",
            stationBadge = "4. Core Burnout / Finisher",
            targetMusclePill = "Quads, Glutes & Core",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 24.0f,
            formCueSummary = "Hold dumbbell against sternum and sit hips down with a tall spine.",
            animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
        )
    )

    val HOME_FOUR_MOVEMENTS = listOf(
        ApexCoachMovementSpec(
            id = "home_incline_pushups",
            stationOrder = 1,
            name = "Incline Push-Ups",
            stationBadge = "1. Warm-up",
            targetMusclePill = "Upper & Mid Chest",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 0f,
            formCueSummary = "Hands slightly wider than shoulders on elevated edge.",
            animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
        ),
        ApexCoachMovementSpec(
            id = "home_doorframe_rows",
            stationOrder = 2,
            name = "Doorframe / Band Rows",
            stationBadge = "2. Activation / Regression",
            targetMusclePill = "Lats & Rear Delts",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 0f,
            formCueSummary = "Lean back with straight torso and pull chest forward.",
            animationType = ExerciseAnimationType.DOORFRAME_ROW
        ),
        ApexCoachMovementSpec(
            id = "home_split_squats",
            stationOrder = 3,
            name = "Split Squats",
            stationBadge = "3. Main Target Movement",
            targetMusclePill = "Quadriceps & Glutes",
            setsAndRepsLabel = "x12",
            totalSets = 3,
            defaultLoadKg = 0f,
            formCueSummary = "Lower back knee toward the floor while keeping front shin vertical.",
            animationType = ExerciseAnimationType.FORWARD_LUNGES
        ),
        ApexCoachMovementSpec(
            id = "home_plank_core_lock",
            stationOrder = 4,
            name = "Forearm Plank Hold",
            stationBadge = "4. Core Burnout / Finisher",
            targetMusclePill = "Deep Core & Abs",
            setsAndRepsLabel = "00:30",
            totalSets = 3,
            defaultLoadKg = 0f,
            formCueSummary = "Brace abs and glutes tightly in a straight line.",
            animationType = ExerciseAnimationType.PLANK_HOLD
        )
    )

    // =========================================================================
    // AT-HOME KEEP-ACTIVE QUICK SESSIONS (6–9 MINS • ZERO EQUIPMENT)
    // =========================================================================
    val QUICK_STRIKE_WORKOUTS: List<QuickStrikeWorkout> = listOf(
        QuickStrikeWorkout(
            id = "quick_home_wake_up",
            title = "Full-Body Home Fat Burn",
            subtitle = "Warm-up jacks, squats, incline push-ups, plank & spine cooldown",
            durationLabel = "9 MINS",
            durationMinutes = 9,
            caloriesKcal = 95,
            iconTag = "FAT BURN",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.FAT_BURN,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "All Levels • Home Zero-Equipment",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "qs_jumping_jacks",
                    stationOrder = 1,
                    name = "Jumping Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Full Body",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Light rhythm on the balls of your feet to get blood flowing.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "qs_air_squats",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Legs & Glutes",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lower smoothly to parallel and stand tall through your heels.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "qs_wake_pushups",
                    stationOrder = 3,
                    name = "Incline Push-Ups",
                    stationBadge = "3. Chest & Arms",
                    targetMusclePill = "Chest & Arms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hands on bed or bench edge, lower smoothly and press.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "qs_wake_plank",
                    stationOrder = 4,
                    name = "Forearm Plank Hold",
                    stationBadge = "4. Core Metabolic Lock",
                    targetMusclePill = "Core & Waist",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace abs and glutes tightly with steady nasal breathing.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "qs_wake_cobra",
                    stationOrder = 5,
                    name = "Cobra Stretch",
                    stationBadge = "5. Cooldown",
                    targetMusclePill = "Lower Back & Abs",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open chest gently and breathe deeply.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        )
    )

    // =========================================================================
    // TARGETED MUSCLE, FAT-BURN, STRENGTH & VITALITY PACKS (STRUCTURED 3-PHASE)
    // =========================================================================
    val SPECIALTY_GOAL_WORKOUTS: List<QuickStrikeWorkout> = listOf(
        QuickStrikeWorkout(
            id = "track_chest_power",
            title = "Upper Chest & Tricep Armor (Gym)",
            subtitle = "Incline dumbbell press, machine press, pec fly & rope pushdowns",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 165,
            iconTag = "CHEST & PUSH",
            badgeColorHex = 0xFF2563EB,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Full Gym Upper-Chest & Push",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "ch_warmup_opener",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Chest & Rotator Cuff",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sweep arms back gently to prime shoulder capsules before pressing.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "ch_incline_press",
                    stationOrder = 2,
                    name = "30° Incline Dumbbell Press",
                    stationBadge = "2. Upper Chest Shelf",
                    targetMusclePill = "Upper Chest",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 22f,
                    formCueSummary = "Tuck elbows at 45° and squeeze upper chest at lockout.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "ch_machine_press",
                    stationOrder = 3,
                    name = "Machine Chest Press",
                    stationBadge = "3. Mid-Chest Mass",
                    targetMusclePill = "Mid Chest & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 45f,
                    formCueSummary = "Keep shoulder blades pinned to pad and press smoothly.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "ch_pec_fly",
                    stationOrder = 4,
                    name = "Machine Pec Fly",
                    stationBadge = "4. Inner Chest Squeeze",
                    targetMusclePill = "Pectorals",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 35f,
                    formCueSummary = "Bring handles together in a wide hug and hold squeeze 1s.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "ch_pushdown",
                    stationOrder = 5,
                    name = "Cable Rope Tricep Pushdown",
                    stationBadge = "5. Tricep Lockout",
                    targetMusclePill = "Triceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 20f,
                    formCueSummary = "Pin elbows to ribs and flare rope outward at the bottom.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "ch_tricep_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Shoulders & Triceps",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen triceps and chest fascia with calm breathing.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_wide_back",
            title = "V-Taper Wide Back & Rear Delts (Gym)",
            subtitle = "Lat pulldowns, chest-supported rows, face pulls & cable laterals",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 160,
            iconTag = "V-TAPER BACK",
            badgeColorHex = 0xFF0EA5E9,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Broad Lats & Shoulder Width",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "bk_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Scapula & Lats",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Activate mid-back retractors before loading heavy pulls.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "bk_lat_pull",
                    stationOrder = 2,
                    name = "Wide-Grip Lat Pulldown",
                    stationBadge = "2. V-Taper Width",
                    targetMusclePill = "Lats",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 48f,
                    formCueSummary = "Drive elbows down toward your hips with proud chest.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "bk_cable_row",
                    stationOrder = 3,
                    name = "Chest-Supported Machine Row",
                    stationBadge = "3. Mid-Back Thickness",
                    targetMusclePill = "Mid-Back",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 45f,
                    formCueSummary = "Brace chest on pad and pinch shoulder blades together 1s.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "bk_face_pull",
                    stationOrder = 4,
                    name = "Cable Rope Face Pull",
                    stationBadge = "4. Rear Delts & Posture",
                    targetMusclePill = "Rear Delts",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 18f,
                    formCueSummary = "Pull rope toward forehead while rotating thumbs backward.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "bk_side_delt",
                    stationOrder = 5,
                    name = "Cable Lateral Raise",
                    stationBadge = "5. Side Shoulder Cap",
                    targetMusclePill = "Side Shoulders",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 8f,
                    formCueSummary = "Sweep cable out to shoulder height with zero torso swing.",
                    animationType = ExerciseAnimationType.CABLE_LATERAL_RAISE
                ),
                ApexCoachMovementSpec(
                    id = "bk_lat_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Lats & Upper Back",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sit hips back and let your lats decompress fully.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_leg_drive",
            title = "Athletic Legs, Quads & Calves (Gym)",
            subtitle = "Sled leg press, Romanian deadlifts, leg extensions & standing calves",
            durationLabel = "17 MINS",
            durationMinutes = 17,
            caloriesKcal = 185,
            iconTag = "LOWER BODY",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Full Gym Lower-Body Power",
            recommendedGoalIds = setOf("goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "lg_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Knees",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hips and ankles smoothly before heavy leg sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "lg_squat",
                    stationOrder = 2,
                    name = "45° Sled Leg Press",
                    stationBadge = "2. Quad & Glute Drive",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 90f,
                    formCueSummary = "Lower sled under control to 90° knees without lifting hips.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "lg_rdl",
                    stationOrder = 3,
                    name = "Dumbbell Romanian Deadlift",
                    stationBadge = "3. Hamstrings & Glutes",
                    targetMusclePill = "Hamstrings",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 24f,
                    formCueSummary = "Hinge hips back with flat spine until deep hamstring stretch.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "lg_leg_ext",
                    stationOrder = 4,
                    name = "Machine Leg Extension",
                    stationBadge = "4. Quad Isolation",
                    targetMusclePill = "Quadriceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 40f,
                    formCueSummary = "Pause 1 full second at top quad contraction.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "lg_calves",
                    stationOrder = 5,
                    name = "Standing Calf Raises",
                    stationBadge = "5. Calves",
                    targetMusclePill = "Calves",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 20f,
                    formCueSummary = "Full heel stretch at bottom, high tiptoe squeeze at top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "lg_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hips & Quads",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen hip flexors and quads smoothly after leg work.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_male_dumbbell_upper_lower",
            title = "Small-Gym Dumbbell Muscle Builder",
            subtitle = "Flat DB press, 1-arm rows, shoulder press & goblet squats — no cables needed",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 170,
            iconTag = "DUMBBELLS ONLY",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Hotel / Apartment Gym or Home Dumbbells",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "mdb_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Back",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Loosen shoulder blades and thoracic spine.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "mdb_flat_press",
                    stationOrder = 2,
                    name = "Flat Dumbbell Bench Press",
                    stationBadge = "2. Chest Mass",
                    targetMusclePill = "Chest & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 22f,
                    formCueSummary = "Lower dumbbells with 45° elbows and drive up cleanly.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "mdb_row",
                    stationOrder = 3,
                    name = "One-Arm Dumbbell Row",
                    stationBadge = "3. Lat & Back Drive",
                    targetMusclePill = "Lats & Mid-Back",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 22f,
                    formCueSummary = "Pull dumbbell toward hip pocket and pause 1s.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "mdb_sh_press",
                    stationOrder = 4,
                    name = "Seated Dumbbell Shoulder Press",
                    stationBadge = "4. Deltoid Mass",
                    targetMusclePill = "Shoulders",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 16f,
                    formCueSummary = "Brace ribs down and press dumbbells overhead.",
                    animationType = ExerciseAnimationType.CABLE_LATERAL_RAISE
                ),
                ApexCoachMovementSpec(
                    id = "mdb_goblet",
                    stationOrder = 5,
                    name = "Dumbbell Goblet Squat",
                    stationBadge = "5. Legs & Core",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 24f,
                    formCueSummary = "Hold dumbbell tight to chest and sit deep between heels.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mdb_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Back & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress shoulders and lower back.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_male_home_legs_glutes",
            title = "Home Quads, Hamstrings & Calves",
            subtitle = "Zero-equipment squats, split squats, bridges & high-rep calf armor",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 145,
            iconTag = "HOME LEGS",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Home Zero-Equipment Leg Builder",
            recommendedGoalIds = setOf("goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "mhl_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Knees & Hips",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth bodyweight openers to lubricate knees and hips.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mhl_squat",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Quad Volume",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x18",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "2-second controlled descent, strong drive up through mid-foot.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mhl_split",
                    stationOrder = 3,
                    name = "Split Squats",
                    stationBadge = "3. Single-Leg Strength",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep torso tall and lower back knee smoothly toward floor.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "mhl_bridge",
                    stationOrder = 4,
                    name = "Floor Glute Bridge",
                    stationBadge = "4. Posterior Chain",
                    targetMusclePill = "Hamstrings & Glutes",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive heels down hard and lock hips for 1s at the top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mhl_calves",
                    stationOrder = 5,
                    name = "Standing Calf Raises",
                    stationBadge = "5. Lower-Leg Burn",
                    targetMusclePill = "Calves",
                    setsAndRepsLabel = "x20",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Rise onto balls of feet and hold peak contraction 1s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mhl_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open tight hips and quads with slow breathing.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_arms_3d_delts",
            title = "3D Shoulders, Biceps & Triceps (Gym)",
            subtitle = "Lateral raises, incline curls, overhead tricep extensions & hammer curls",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 140,
            iconTag = "ARMS & DELTS",
            badgeColorHex = 0xFF2563EB,
            category = SpecialtyTrackCategory.ARMS_POSTURE,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            audienceTag = "Men • Gym or Dumbbells Arm Specialization",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "arm_warmup",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Elbows",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open shoulder joints smoothly before isolation work.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "arm_lat_raise",
                    stationOrder = 2,
                    name = "Dumbbell Lateral Raise",
                    stationBadge = "2. 3D Side Delts",
                    targetMusclePill = "Side Shoulders",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 10f,
                    formCueSummary = "Lead with elbows to shoulder height with zero momentum.",
                    animationType = ExerciseAnimationType.CABLE_LATERAL_RAISE
                ),
                ApexCoachMovementSpec(
                    id = "arm_inc_curl",
                    stationOrder = 3,
                    name = "Incline Dumbbell Curl",
                    stationBadge = "3. Biceps Peak",
                    targetMusclePill = "Biceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 12f,
                    formCueSummary = "Keep elbows behind torso for a deep biceps stretch.",
                    animationType = ExerciseAnimationType.INCLINE_DB_CURL
                ),
                ApexCoachMovementSpec(
                    id = "arm_oh_tricep",
                    stationOrder = 4,
                    name = "Overhead Cable Tricep Extension",
                    stationBadge = "4. Long-Head Triceps",
                    targetMusclePill = "Triceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 20f,
                    formCueSummary = "Keep elbows pointed forward and lock out overhead.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "arm_hammer",
                    stationOrder = 5,
                    name = "Dumbbell Hammer Curl",
                    stationBadge = "5. Arm & Forearm Width",
                    targetMusclePill = "Brachialis & Forearms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 14f,
                    formCueSummary = "Neutral palms-in grip, curl cleanly without swinging.",
                    animationType = ExerciseAnimationType.INCLINE_DB_CURL
                ),
                ApexCoachMovementSpec(
                    id = "arm_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Arms & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Stretch triceps and delts calmly.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_male_home_arms_delts",
            title = "Home Arms, Shoulders & Upper Back",
            subtitle = "Pike shoulder presses, close-grip push-ups & chair dips — zero equipment",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 115,
            iconTag = "HOME ARMS",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.ARMS_POSTURE,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            audienceTag = "Men • Home Zero-Equipment Arm & Delt Pump",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "mha_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Warm up shoulder blades and wrists.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "mha_pike",
                    stationOrder = 2,
                    name = "Pike Shoulder Push-Ups",
                    stationBadge = "2. Deltoid Press",
                    targetMusclePill = "Shoulders",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hips high in inverted V, press head-to-floor smoothly.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "mha_close_push",
                    stationOrder = 3,
                    name = "Close-Grip Push-Ups",
                    stationBadge = "3. Tricep Mass",
                    targetMusclePill = "Triceps & Inner Chest",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep elbows grazing your ribs to load triceps heavily.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "mha_dips",
                    stationOrder = 4,
                    name = "Bench Tricep Dips",
                    stationBadge = "4. Arm Finisher",
                    targetMusclePill = "Triceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slide back close to chair edge and lock out elbows.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "mha_stretch",
                    stationOrder = 5,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Triceps & Delts",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Relax shoulders and lengthen upper arms.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_male_gym_sixpack_core",
            title = "Weighted Six-Pack & Core Armor (Gym)",
            subtitle = "Cable rope crunches, captain's chair knee raises & anti-rotation planks",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 120,
            iconTag = "GYM ABS",
            badgeColorHex = 0xFFF59E0B,
            category = SpecialtyTrackCategory.CORE_WAIST,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            audienceTag = "Men • Full Gym Weighted Ab Definition",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "mgc_warmup",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Core & Spine",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Mobilize spine and abdominal wall before weighted crunches.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "mgc_cable_crunch",
                    stationOrder = 2,
                    name = "Kneeling Cable Rope Crunch",
                    stationBadge = "2. Weighted Upper Abs",
                    targetMusclePill = "Rectus Abdominis",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 28f,
                    formCueSummary = "Lock hips still and curl ribs down toward pelvis.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "mgc_knee_raise",
                    stationOrder = 3,
                    name = "Captain's Chair Knee Raise",
                    stationBadge = "3. Lower Abs & V-Cut",
                    targetMusclePill = "Lower Abs",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Curl pelvis upward at the top — don't just swing knees.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "mgc_tap_plank",
                    stationOrder = 4,
                    name = "Plank Shoulder Taps",
                    stationBadge = "4. Anti-Rotation Core",
                    targetMusclePill = "Obliques & Deep Core",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Set feet wide and keep hips dead-level as hands tap.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "mgc_stretch",
                    stationOrder = 5,
                    name = "Cobra Abdominal Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Abdominal Wall",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Inhale deeply to stretch the entire abdominal sheet.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_male_gym_fat_furnace",
            title = "Gym Metabolic Belly-Fat Shred Circuit",
            subtitle = "3× calorie leg & back burners + hanging knee raises & plank to burn belly fat",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 195,
            iconTag = "METABOLIC SHRED",
            badgeColorHex = 0xFFDC2626,
            category = SpecialtyTrackCategory.FAT_BURN,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Men • High-Calorie Compound Burn + Direct Core (Knee-Safe)",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "mgf_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Joints & Heart Rate",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth continuous reps to elevate heart rate safely.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mgf_goblet",
                    stationOrder = 2,
                    name = "Dumbbell Goblet Squat",
                    stationBadge = "2. 3× Calorie Leg Furnace",
                    targetMusclePill = "Legs & Fat-Burn Engine",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 20f,
                    formCueSummary = "Largest muscles in your body burn 3× more calories to melt belly fat faster.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "mgf_row",
                    stationOrder = 3,
                    name = "Seated Cable Row",
                    stationBadge = "3. Back Calorie Engine",
                    targetMusclePill = "Back & Metabolism",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 40f,
                    formCueSummary = "Keep rest crisp and pull smoothly to sternum.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "mgf_press",
                    stationOrder = 4,
                    name = "30° Incline Dumbbell Press",
                    stationBadge = "4. Chest Muscle Retention",
                    targetMusclePill = "Chest & Shoulders",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 18f,
                    formCueSummary = "Protect chest muscle fullness while burning fat.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "mgf_knee_raise",
                    stationOrder = 5,
                    name = "Captain's Chair Knee Raise",
                    stationBadge = "5. Lower Belly & V-Cut",
                    targetMusclePill = "Lower Abs",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Curl knees upward to tighten lower stomach and V-cut.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "mgf_plank",
                    stationOrder = 6,
                    name = "Forearm Plank Hold",
                    stationBadge = "6. Flat Waist Finisher",
                    targetMusclePill = "Deep Core & Waist",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lock abs tight like a belt and control your breathing.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "mgf_stretch",
                    stationOrder = 7,
                    name = "Cobra Abdominal Stretch",
                    stationBadge = "7. Stretch",
                    targetMusclePill = "Core & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Bring heart rate down with slow nasal breaths.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_zero_jump_fat_burn",
            title = "Zero-Jump Belly Fat & Sweat (Knee-Safe)",
            subtitle = "Step jacks, squats, box push-ups & shoulder-tap planks — quiet & joint-safe",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 135,
            iconTag = "ZERO-JUMP BURN",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.FAT_BURN,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "All People • Heavy-Frame & Apartment Knee-Safe",
            recommendedGoalIds = setOf("goal_1_fat_loss"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "zjf_step_jacks",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Low-Impact Cardio",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Step wide left and right with fast overhead arm sweeps — zero jumping.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "zjf_squats",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Metabolic Leg Burn",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sit hips back smoothly and stand tall.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "zjf_box_push",
                    stationOrder = 3,
                    name = "Box Incline Push-Ups",
                    stationBadge = "3. Joint-Safe Upper Burn",
                    targetMusclePill = "Chest & Arms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hands elevated on bench or couch to protect wrists and shoulders.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "zjf_tap_plank",
                    stationOrder = 4,
                    name = "Plank Shoulder Taps",
                    stationBadge = "4. Core Sweat Finisher",
                    targetMusclePill = "Core & Waist",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace stomach tight while alternating controlled shoulder taps.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "zjf_stretch",
                    stationOrder = 5,
                    name = "Cobra Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Spine & Waist",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slow deep breaths to cool down smoothly.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_grip_power",
            title = "Forearm, Dead-Hang & Grip Armor",
            subtitle = "Dead hangs, farmer holds, reverse curls & wrist holds for crushing grip",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 105,
            iconTag = "GRIP & FOREARMS",
            badgeColorHex = 0xFFF59E0B,
            category = SpecialtyTrackCategory.STRENGTH_GRIP,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            audienceTag = "Men • Gym or Pull-Up Bar + Dumbbells",
            recommendedGoalIds = setOf("goal_4_power_mass", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "grip_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Wrists",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Set active shoulder sockets before hanging.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "grip_dead_hang",
                    stationOrder = 2,
                    name = "Bar Dead Hang",
                    stationBadge = "2. Grip Endurance",
                    targetMusclePill = "Forearms & Grip",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Grip bar firmly with active shoulders and hold steady.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "grip_farmer_hold",
                    stationOrder = 3,
                    name = "Dumbbell Farmer Hold",
                    stationBadge = "3. Crushing Grip",
                    targetMusclePill = "Forearms & Traps",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 20f,
                    formCueSummary = "Stand tall holding heavy dumbbells tightly at your sides.",
                    animationType = ExerciseAnimationType.INCLINE_DB_CURL
                ),
                ApexCoachMovementSpec(
                    id = "grip_reverse_curl",
                    stationOrder = 4,
                    name = "Reverse-Grip Forearm Curl",
                    stationBadge = "4. Upper Forearms",
                    targetMusclePill = "Brachioradialis",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 10f,
                    formCueSummary = "Palms facing down, curl cleanly to thicken outer forearms.",
                    animationType = ExerciseAnimationType.INCLINE_DB_CURL
                ),
                ApexCoachMovementSpec(
                    id = "grip_knuckle_plank",
                    stationOrder = 5,
                    name = "Knuckle Push-Up Hold",
                    stationBadge = "5. Wrist Strength",
                    targetMusclePill = "Wrists & Hands",
                    setsAndRepsLabel = "00:30",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep wrists locked straight in a rigid push-up plank.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_raw_strength",
            title = "Full-Body Compound Strength (Gym)",
            subtitle = "Heavy 8-rep incline press, cable rows, pause goblet squats & core brace",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 165,
            iconTag = "RAW STRENGTH",
            badgeColorHex = 0xFFDC2626,
            category = SpecialtyTrackCategory.STRENGTH_GRIP,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            audienceTag = "Men • Full Gym Heavy Compound Power",
            recommendedGoalIds = setOf("goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "str_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Core",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Prime hips, knees, and core bracing before heavy sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "str_heavy_press",
                    stationOrder = 2,
                    name = "Incline Dumbbell Press",
                    stationBadge = "2. Heavy Upper Push",
                    targetMusclePill = "Chest & Shoulders",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 24f,
                    formCueSummary = "Pause 1s at chest stretch, then drive forcefully overhead.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "str_heavy_pull",
                    stationOrder = 3,
                    name = "Seated Cable Row",
                    stationBadge = "3. Heavy Upper Pull",
                    targetMusclePill = "Back & Biceps",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 50f,
                    formCueSummary = "Pull elbows back and pinch shoulder blades hard for 1s.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "str_pause_squat",
                    stationOrder = 4,
                    name = "Goblet Squat",
                    stationBadge = "4. Pause Leg Strength",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 26f,
                    formCueSummary = "Sit to parallel, pause 1s, and drive tall through mid-foot.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "str_core_armor",
                    stationOrder = 5,
                    name = "Forearm Plank",
                    stationBadge = "5. Core Bracing",
                    targetMusclePill = "Abs & Core",
                    setsAndRepsLabel = "00:45",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace abs and glutes like taking a punch.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "str_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Spine & Lats",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress spine after heavy compound loading.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_calisthenics_stage1_foundation",
            title = "Stage 1: 14-Day Calisthenics Foundation Challenge",
            subtitle = "Push-ups, bodyweight squats, hollow planks & assisted pull-ups — build your base in 14 days",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 135,
            iconTag = "STAGE 1 • 14-DAY BASE",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 14-Day Beginner Calisthenics Challenge",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 14,
            expectedResultsTimeline = "Days 1–7 clean hollow form • Day 14: 15 strict push-ups, 45s plank & Stage 2 bar readiness",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "cal1_warmup",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Wrists & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Warm wrists, chest, and shoulder capsules before bodyweight leverage.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "cal1_pushups",
                    stationOrder = 2,
                    name = "Strict Hollow Push-Ups",
                    stationBadge = "2. Push Foundation",
                    targetMusclePill = "Chest, Shoulders & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Tuck ribs down into a straight hollow body line and press through palms.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "cal1_assisted_pull",
                    stationOrder = 3,
                    name = "Assisted Bar Pull-Ups",
                    stationBadge = "3. Assisted Pull Base",
                    targetMusclePill = "Lats, Biceps & Grip",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Use light foot or band support (or doorframe row) to pull chest cleanly to bar.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "cal1_squats",
                    stationOrder = 4,
                    name = "Full-Depth Bodyweight Squats",
                    stationBadge = "4. Leg & Ankle Control",
                    targetMusclePill = "Quads, Glutes & Mobility",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lower with a tall spine to full comfortable depth — prepares legs for pistol squats.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "cal1_plank",
                    stationOrder = 5,
                    name = "Hollow-Body Forearm Plank",
                    stationBadge = "5. Hollow Core Lock",
                    targetMusclePill = "Deep Core & Scapula",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Push floor away through elbows and squeeze glutes + abs into a rigid line.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "cal1_stretch",
                    stationOrder = 6,
                    name = "Cobra Spine Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Spine & Core",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen chest, abs, and wrists with slow breathing.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_gym_calisthenics_hybrid",
            title = "Stage 2: 14-Day Pull-Up, Dip & Handstand Challenge",
            subtitle = "Strict pull-ups, parallel dips, hanging leg raises & wall handstand practice across 14 days",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 165,
            iconTag = "STAGE 2 • 14-DAY BARS",
            badgeColorHex = 0xFF1D4ED8,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 14-Day Intermediate Bar & Handstand Challenge",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 14,
            expectedResultsTimeline = "Days 1–7 scapular & wall stack • Day 14: strict bar pull-ups, crisp dips & 30s wall handstand",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "gch_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Scapula & Wrists",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Prime shoulder blades and wrists before pull-ups, dips, and handstands.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "gch_pullup",
                    stationOrder = 2,
                    name = "Strict Bar Pull-Ups",
                    stationBadge = "2. Vertical Bar Pull",
                    targetMusclePill = "Lats, Biceps & Grip",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pull collarbones toward bar from a clean dead hang with zero leg swing.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "gch_dip",
                    stationOrder = 3,
                    name = "Parallel Bar Dips",
                    stationBadge = "3. Straight-Arm Lockout",
                    targetMusclePill = "Chest, Front Delts & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lower to 90° elbows and press to full depressed-shoulder lockout.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "gch_knee_raise",
                    stationOrder = 4,
                    name = "Hanging Leg & Knee Raises",
                    stationBadge = "4. Hanging Core Compression",
                    targetMusclePill = "Lower Abs & Hip Flexors",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Curl pelvis upward with zero swinging to build L-sit & lever core power.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "gch_handstand",
                    stationOrder = 5,
                    name = "Wall Handstand Practice Press",
                    stationBadge = "5. Handstand Inversion",
                    targetMusclePill = "Shoulders, Balance & Core",
                    setsAndRepsLabel = "x8",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Stack hips high over shoulders (pike or feet on wall) and push the floor tall.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "gch_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Shoulders & Lats",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress shoulders and triceps calmly after bar and inversion sets.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_home_calisthenics_strength",
            title = "Stage 3: 21-Day Muscle-Up & Front Lever Forge",
            subtitle = "3-week advanced progression: explosive muscle-up pulls, bar dips, handstand walk taps & front levers",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 175,
            iconTag = "STAGE 3 • 21-DAY FORGE",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 21-Day Advanced Muscle-Up & Front Lever Challenge",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 21,
            expectedResultsTimeline = "21-day tendon & explosive cycle • Day 21: sternum-high bar pull, handstand walk shifts & tuck front lever",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "cal_warmup",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders, Elbows & Wrists",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Prep wrists, elbows, and rotator cuffs before explosive bar & lever work.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "cal_muscleup_pull",
                    stationOrder = 2,
                    name = "Explosive Muscle-Up Bar Pull",
                    stationBadge = "2. Muscle-Up High Pull",
                    targetMusclePill = "Lats, Upper Chest & Explosiveness",
                    setsAndRepsLabel = "x6",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pull explosively toward lower sternum to master the muscle-up bar transition.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "cal_muscleup_dip",
                    stationOrder = 3,
                    name = "Straight-Bar Muscle-Up Dip",
                    stationBadge = "3. Top-of-Bar Lockout",
                    targetMusclePill = "Triceps, Chest & Front Delts",
                    setsAndRepsLabel = "x8",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lean chest slightly over the bar and press cleanly to top muscle-up lockout.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "cal_hs_walk_taps",
                    stationOrder = 4,
                    name = "Handstand Walk Shoulder Taps",
                    stationBadge = "4. Handstand Walking Skill",
                    targetMusclePill = "Single-Arm Balance & Delts",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Shift weight from palm to palm in wall handstand or high pike to unlock handstand walking.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "cal_front_lever",
                    stationOrder = 5,
                    name = "Tuck Front Lever Hold & Row",
                    stationBadge = "5. Front Lever Skill",
                    targetMusclePill = "Straight-Arm Lats & Core",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Depress lats hard with hips horizontal to build horizontal front-lever suspension.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "cal_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Lats, Wrists & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen lats and shoulder capsules calmly after lever & bar sets.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_calisthenics_stage4_elite",
            title = "Stage 4: 28-Day Elite Planche, Human Flag & Pistol Mastery",
            subtitle = "4-week elite gymnastics roadmap: parallettes planche holds, human-flag side levers, L-sits & pistol squats",
            durationLabel = "17 MINS",
            durationMinutes = 17,
            caloriesKcal = 180,
            iconTag = "STAGE 4 • 28-DAY ELITE",
            badgeColorHex = 0xFF7C3AED,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 28-Day Elite Gymnastics & Statics Challenge",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 28,
            expectedResultsTimeline = "28-day straight-arm mastery • Day 28: planche lean/tuck, pole human-flag tension, straight L-sit & full pistol squat",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "cal4_warmup",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Wrists, Biceps Tendon & Spine",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Warm wrists and straight-arm tendons thoroughly before planche & flag holds.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "cal4_planche_hold",
                    stationOrder = 2,
                    name = "Parallettes Planche Hold & Lean",
                    stationBadge = "2. Planche Straight-Arm Lock",
                    targetMusclePill = "Front Delts, Biceps Tendon & Core",
                    setsAndRepsLabel = "00:25",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lock elbows 100% straight, protract scapula, and lean shoulders past wrists.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "cal4_pseudo_planche",
                    stationOrder = 3,
                    name = "Pseudo-Planche Push-Ups",
                    stationBadge = "3. Dynamic Planche Strength",
                    targetMusclePill = "Anterior Delts & Upper Chest",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Turn fingers slightly outward at waist level and press with forward shoulder lean.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "cal4_human_flag",
                    stationOrder = 4,
                    name = "Human-Flag Side Lever Hold",
                    stationBadge = "4. Human Flag Lateral Core",
                    targetMusclePill = "Obliques, Lats & Pushing Shoulder",
                    setsAndRepsLabel = "00:25",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Bottom arm pushes hard while top lat pulls and lateral core locks horizontal.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "cal4_lsit",
                    stationOrder = 5,
                    name = "Parallettes Straight-Leg L-Sit",
                    stationBadge = "5. Gymnastics Core Compression",
                    targetMusclePill = "Abs, Quads & Triceps",
                    setsAndRepsLabel = "00:25",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Depress shoulders down on parallettes or floor and hold straight legs at 90°.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "cal4_pistol",
                    stationOrder = 6,
                    name = "Single-Leg Pistol Squat",
                    stationBadge = "6. Unilateral Gymnastics Leg",
                    targetMusclePill = "Single-Leg Quads, Glutes & Balance",
                    setsAndRepsLabel = "x8",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Extend free leg forward and lower on one leg under smooth control.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "cal4_stretch",
                    stationOrder = 7,
                    name = "Cobra Spine Stretch",
                    stationBadge = "7. Cooldown Stretch",
                    targetMusclePill = "Core, Wrists & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Release abdominal compression and wrist flexors with deep breaths.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_calisthenics_fat_burn_flow",
            title = "Athletic Calisthenics Shred & Stamina",
            subtitle = "Bodyweight squats, close-grip push-ups, walking lunges & shoulder-tap planks",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 150,
            iconTag = "CALISTHENICS SHRED",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Men & Women • Lean Calisthenics Fat Burn & Stamina",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "cfb_warmup",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Heart & Joints",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Crisp rhythmic step-outs to warm shoulders, hips, and heart rate.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "cfb_squats",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Lower-Body Burn",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth unbroken bodyweight squats driving tall through heels.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "cfb_close_push",
                    stationOrder = 3,
                    name = "Close-Grip Push-Ups",
                    stationBadge = "3. Upper Push & Triceps",
                    targetMusclePill = "Chest & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Tuck elbows close to ribs for crisp upper-body calisthenics tone.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "cfb_lunges",
                    stationOrder = 4,
                    name = "Walking Lunges",
                    stationBadge = "4. Athletic Stride Burn",
                    targetMusclePill = "Legs & Glutes",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep chest proud and step smoothly for athletic leg conditioning.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "cfb_taps",
                    stationOrder = 5,
                    name = "Plank Shoulder Taps",
                    stationBadge = "5. Core Control Finisher",
                    targetMusclePill = "Core & Waist",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lock hips completely still while tapping opposite shoulders.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "cfb_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slow deep breaths to open hips and bring heart rate down.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_pelvic_kegel",
            title = "Pelvic Floor, Kegel & Deep Core",
            subtitle = "Pelvic floor holds, glute bridges, diaphragmatic release & hip blood flow",
            durationLabel = "9 MINS",
            durationMinutes = 9,
            caloriesKcal = 70,
            iconTag = "PELVIC & CORE",
            badgeColorHex = 0xFF2563EB,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Private Mat Control & Stamina",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "kegel_deep_holds",
                    stationOrder = 1,
                    name = "Kegel Squeeze & Hold",
                    stationBadge = "1. Pelvic Floor",
                    targetMusclePill = "Pelvic Floor",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Squeeze pelvic floor 5s, relax 3s while breathing normally.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "kegel_glute_bridge",
                    stationOrder = 2,
                    name = "Glute Bridge",
                    stationBadge = "2. Hips & Glutes",
                    targetMusclePill = "Glutes & Pelvis",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive hips up through heels and hold 2s at the top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "kegel_reverse_release",
                    stationOrder = 3,
                    name = "Deep Belly Breath Release",
                    stationBadge = "3. Pelvic Relaxation",
                    targetMusclePill = "Pelvic Floor",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Inhale into lower belly and relax pelvic tension completely.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "kegel_hip_stamina_plank",
                    stationOrder = 4,
                    name = "Forearm Plank",
                    stationBadge = "4. Core Stamina",
                    targetMusclePill = "Lower Abs & Hips",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace lower abs and glutes steady while breathing calmly.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "kegel_hip_opener",
                    stationOrder = 5,
                    name = "Hip Lunge Stretch",
                    stationBadge = "5. Hip Stretch",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Step forward smoothly to open tight hips and boost circulation.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_full_body_evening_stretch",
            title = "Full-Body Stretching & Flexibility Flow",
            subtitle = "Stretch tight neck, upper back, chest, sitting hips & lower spine in one flow",
            durationLabel = "10 MINS",
            durationMinutes = 10,
            caloriesKcal = 55,
            iconTag = "STRETCH & FLEXIBLE",
            badgeColorHex = 0xFF0D9488,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Daily Stretching, Desk Posture & Soreness Relief",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "fbs_flow",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Spine & Shoulders",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Move slowly with your breath to warm stiff spinal joints.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "fbs_lat",
                    stationOrder = 2,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "2. Upper-Back Stretch",
                    targetMusclePill = "Lats & Upper Back",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale slowly and sink chest gently toward the mat.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "fbs_hip",
                    stationOrder = 3,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "3. Deep Hip Stretch",
                    targetMusclePill = "Hips & Quads",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Squeeze back glute lightly to unlock tight sitting hips.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "fbs_chest",
                    stationOrder = 4,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "4. Chest & Neck Stretch",
                    targetMusclePill = "Chest & Posture",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw shoulders down away from ears and open chest wide.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "fbs_spine",
                    stationOrder = 5,
                    name = "Cobra Spine Stretch",
                    stationBadge = "5. Cooldown Stretch",
                    targetMusclePill = "Lower Spine & Abs",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Take slow 4-second exhales to relax lower-back tension.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_gym_athletic_stamina",
            title = "Gym Athletic Stamina & Conditioning",
            subtitle = "Goblet squats, brisk dumbbell rows, reverse lunges & core conditioning",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 175,
            iconTag = "STAMINA ENGINE",
            badgeColorHex = 0xFF0EA5E9,
            category = SpecialtyTrackCategory.STAMINA_ENDURANCE,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.STAMINA_CONDITIONING,
            audienceTag = "Men & Women • Gym Stamina & All-Day Energy",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "gas_warmup_jacks",
                    stationOrder = 1,
                    name = "Jumping Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Heart & Lungs",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Light springy rhythm to open lungs and warm joints.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "gas_goblet_squat",
                    stationOrder = 2,
                    name = "Dumbbell Goblet Squat",
                    stationBadge = "2. Lower Stamina",
                    targetMusclePill = "Legs & Lungs",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 14f,
                    formCueSummary = "Continuous smooth reps with steady nasal-to-mouth breathing.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "gas_db_row",
                    stationOrder = 3,
                    name = "One-Arm Dumbbell Row",
                    stationBadge = "3. Upper Stamina Pull",
                    targetMusclePill = "Back & Posture",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 14f,
                    formCueSummary = "Brisk controlled rows keeping heart rate in the aerobic zone.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "gas_rev_lunge",
                    stationOrder = 4,
                    name = "Dumbbell Reverse Lunges",
                    stationBadge = "4. Stride Endurance",
                    targetMusclePill = "Glutes & Quads",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 10f,
                    formCueSummary = "Step back cleanly and drive tall through your front heel.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "gas_shoulder_taps",
                    stationOrder = 5,
                    name = "Plank Shoulder Taps",
                    stationBadge = "5. Core Conditioning",
                    targetMusclePill = "Core & Stamina",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep hips level while alternating crisp shoulder taps.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "gas_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Hips & Breathing",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slow 4-second exhales to bring heart rate down smoothly.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_gym_functional_strength",
            title = "Gym Functional Strength & Grip Builder",
            subtitle = "Incline dumbbell press, rows, RDLs & loaded carries for real-world power",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 170,
            iconTag = "STRENGTH & GRIP",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.STRENGTH_GRIP,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            audienceTag = "Men & Women • Dumbbells / Gym Strength",
            recommendedGoalIds = setOf("goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "gfs_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Scapula",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Prime shoulder blades and upper back before strength sets.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "gfs_incline_press",
                    stationOrder = 2,
                    name = "30° Incline Dumbbell Press",
                    stationBadge = "2. Upper Push Power",
                    targetMusclePill = "Chest & Shoulders",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 16f,
                    formCueSummary = "Control 2s down and drive dumbbells with crisp lockout.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "gfs_db_row",
                    stationOrder = 3,
                    name = "One-Arm Dumbbell Row",
                    stationBadge = "3. Back & Pulling Power",
                    targetMusclePill = "Lats & Mid-Back",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 18f,
                    formCueSummary = "Pull elbow toward hip and squeeze upper back 1 full second.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "gfs_rdl",
                    stationOrder = 4,
                    name = "Dumbbell Romanian Deadlift",
                    stationBadge = "4. Posterior Strength",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 18f,
                    formCueSummary = "Push hips straight back with flat spine and drive tall.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "gfs_farmer_hold",
                    stationOrder = 5,
                    name = "Heavy Dumbbell Farmer Hold",
                    stationBadge = "5. Grip & Core Carry",
                    targetMusclePill = "Forearms, Traps & Core",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 18f,
                    formCueSummary = "Stand tall with proud chest and crush the handles tightly.",
                    animationType = ExerciseAnimationType.INCLINE_DB_CURL
                ),
                ApexCoachMovementSpec(
                    id = "gfs_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Lats & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress shoulders and spine calmly.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        )
    )

    /**
     * Ultra-simple, 4-minute Active Rest / Blood-Flow routines (3 easy bodyweight moves, 1 set each).
     * Automatically varies by day of week & week rotation so Friday Rest and Sunday Rest
     * never show the exact same 3 moves!
     */
    fun getRestDayBloodFlowWorkout(dayOfWeekIso: Int, rotationWeek: Int = 0): QuickStrikeWorkout {
        val variantIndex = ((dayOfWeekIso + rotationWeek) % 3 + 3) % 3
        return when (variantIndex) {
            0 -> QuickStrikeWorkout(
                id = "rest_flow_upper_$dayOfWeekIso",
                title = "4-Min Light Blood Flow",
                subtitle = "Easy warm-up & upper-body stretch — zero fatigue",
                durationLabel = "4 MINS",
                durationMinutes = 4,
                caloriesKcal = 25,
                iconTag = "LIGHT RECOVERY",
                badgeColorHex = 0xFF10B981,
                category = SpecialtyTrackCategory.HOME_BACKUP,
                equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
                goalFocus = PackGoalFocus.MOBILITY_VITALITY,
                exercises = listOf(
                    ApexCoachMovementSpec(
                        id = "rf_a_jacks",
                        stationOrder = 1,
                        name = "Easy Jumping Jacks",
                        stationBadge = "1. Light Blood Flow",
                        targetMusclePill = "Circulation",
                        setsAndRepsLabel = "00:35",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Light, easy bounce on the balls of your feet to warm up circulation.",
                        animationType = ExerciseAnimationType.JUMPING_JACKS
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_a_incline",
                        stationOrder = 2,
                        name = "Easy Wall / Incline Push-Ups",
                        stationBadge = "2. Shoulder Loosen",
                        targetMusclePill = "Chest & Shoulders",
                        setsAndRepsLabel = "x10",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Easy pace against a wall or high counter to loosen chest.",
                        animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_a_cobra",
                        stationOrder = 3,
                        name = "Cobra Back Stretch",
                        stationBadge = "3. Relax Spine",
                        targetMusclePill = "Lower Back",
                        setsAndRepsLabel = "00:30",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Breathe deeply and let your shoulders drop.",
                        animationType = ExerciseAnimationType.COBRA_STRETCH
                    )
                )
            )

            1 -> QuickStrikeWorkout(
                id = "rest_flow_lower_$dayOfWeekIso",
                title = "4-Min Leg & Hip Stretch",
                subtitle = "Gentle squats & hip opener to flush sore legs",
                durationLabel = "4 MINS",
                durationMinutes = 4,
                caloriesKcal = 25,
                iconTag = "LIGHT RECOVERY",
                badgeColorHex = 0xFF10B981,
                category = SpecialtyTrackCategory.HOME_BACKUP,
                equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
                goalFocus = PackGoalFocus.MOBILITY_VITALITY,
                exercises = listOf(
                    ApexCoachMovementSpec(
                        id = "rf_b_squat",
                        stationOrder = 1,
                        name = "Easy Bodyweight Squats",
                        stationBadge = "1. Leg Circulation",
                        targetMusclePill = "Legs & Hips",
                        setsAndRepsLabel = "x10",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Comfortable squat depth at a natural rhythm to pump fresh blood through legs.",
                        animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_b_lunge",
                        stationOrder = 2,
                        name = "Hip Lunge Stretch",
                        stationBadge = "2. Unlock Hips",
                        targetMusclePill = "Hip Flexors",
                        setsAndRepsLabel = "x8",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Step forward gently and open tight hip flexors.",
                        animationType = ExerciseAnimationType.FORWARD_LUNGES
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_b_plank",
                        stationOrder = 3,
                        name = "Easy Core Plank",
                        stationBadge = "3. Light Core",
                        targetMusclePill = "Core",
                        setsAndRepsLabel = "00:25",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Hold a calm 25-second plank while breathing smoothly.",
                        animationType = ExerciseAnimationType.PLANK_HOLD
                    )
                )
            )

            else -> QuickStrikeWorkout(
                id = "rest_flow_posture_$dayOfWeekIso",
                title = "4-Min Posture & Mobility",
                subtitle = "Light movement & shoulder/spine stretch for rest days",
                durationLabel = "4 MINS",
                durationMinutes = 4,
                caloriesKcal = 25,
                iconTag = "LIGHT RECOVERY",
                badgeColorHex = 0xFF10B981,
                category = SpecialtyTrackCategory.HOME_BACKUP,
                equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
                goalFocus = PackGoalFocus.MOBILITY_VITALITY,
                exercises = listOf(
                    ApexCoachMovementSpec(
                        id = "rf_c_door",
                        stationOrder = 1,
                        name = "Easy Doorframe Pulls",
                        stationBadge = "1. Loosen Upper Back",
                        targetMusclePill = "Upper Back",
                        setsAndRepsLabel = "x10",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Lightly pull shoulder blades back to undo desk slouch.",
                        animationType = ExerciseAnimationType.DOORFRAME_ROW
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_c_pike",
                        stationOrder = 2,
                        name = "Overhead Shoulder Stretch",
                        stationBadge = "2. Open Chest",
                        targetMusclePill = "Shoulders & Chest",
                        setsAndRepsLabel = "00:30",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Push hips gently back and let shoulders lengthen.",
                        animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                    ),
                    ApexCoachMovementSpec(
                        id = "rf_c_cobra",
                        stationOrder = 3,
                        name = "Cobra Spine Stretch",
                        stationBadge = "3. Decompress Back",
                        targetMusclePill = "Spine & Abs",
                        setsAndRepsLabel = "00:30",
                        totalSets = 1,
                        defaultLoadKg = 0f,
                        formCueSummary = "Take slow deep breaths to relax your lower back.",
                        animationType = ExerciseAnimationType.COBRA_STRETCH
                    )
                )
            )
        }
    }

    // =========================================================================
    // 7-DAY & 28-DAY STANDALONE PLANS (NON-TRACKED • SELECT OWN LEVEL)
    // =========================================================================
    val CHALLENGE_WORKOUTS: List<QuickStrikeWorkout> = listOf(
        QuickStrikeWorkout(
            id = "challenge_7d_fat_ignition",
            title = "7-Day Intensive Belly-Bloat & Sweat Kickstart",
            subtitle = "Fast metabolic squats, incline presses, shoulder-tap planks & waist tightening to drop bloat in 1 week",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 145,
            iconTag = "7-DAY FAT IGNITION",
            badgeColorHex = 0xFFEA580C,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Men & Women • 7-Day Fast Sweat & Bloat Kickstart",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            challengeDays = 7,
            expectedResultsTimeline = "7 days: shed excess water bloat, wake up metabolism & feel lighter around the waist",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c7_fi_jacks",
                    stationOrder = 1,
                    name = "Jumping Jacks",
                    stationBadge = "1. Metabolic Ignition",
                    targetMusclePill = "Full Body Sweat",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Crisp rhythmic bounce to spike heart rate and sweat fast.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c7_fi_squats",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Calorie Engine",
                    targetMusclePill = "Legs & Glutes",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive fast and clean through heels to fire your largest calorie-burning muscles.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c7_fi_incline",
                    stationOrder = 3,
                    name = "Incline Push-Ups",
                    stationBadge = "3. Upper-Body Burn",
                    targetMusclePill = "Chest, Arms & Core",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep abs locked tight and press briskly without hip sag.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c7_fi_taps",
                    stationOrder = 4,
                    name = "Plank Shoulder Taps",
                    stationBadge = "4. Waist & Oblique Torch",
                    targetMusclePill = "Waist & Core",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace hips completely still as hands alternate crisp shoulder taps.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c7_fi_rev_crunch",
                    stationOrder = 5,
                    name = "Reverse Crunches",
                    stationBadge = "5. Lower-Belly Finish",
                    targetMusclePill = "Lower Abs",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale hard at top knee tuck to flatten lower stomach.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c7_fi_cobra",
                    stationOrder = 6,
                    name = "Cobra Stretch",
                    stationBadge = "6. Cooldown",
                    targetMusclePill = "Spine & Waist",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slow nasal breaths to bring heart rate smoothly down.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_fat_burnout",
            title = "14-Day Intensive Fat Burnout & Waist Torch",
            subtitle = "2-week high-tempo full-body calorie furnace: walking lunges, push-ups, rows & core vacuum lock",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 175,
            iconTag = "14-DAY FAT BURNOUT",
            badgeColorHex = 0xFFDC2626,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Men & Women • 14-Day High-Tempo Fat Burnout",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            challengeDays = 14,
            expectedResultsTimeline = "14 days: –1 to 2 kg real fat loss, noticeably tighter upper stomach & stronger cardio",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_fb_warmup",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Pulse Ignition",
                    targetMusclePill = "Heart & Joints",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Fast rhythmic step-outs to push heart rate into the fat-oxidation zone.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c14_fb_lunge",
                    stationOrder = 2,
                    name = "Walking Lunges",
                    stationBadge = "2. Lower-Body Furnace",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x14",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Long athletic strides that torch 3× more calories than floor crunches.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_fb_pushup",
                    stationOrder = 3,
                    name = "Push-Ups",
                    stationBadge = "3. Upper Push Density",
                    targetMusclePill = "Chest, Shoulders & Arms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Protect lean upper-body tone while stripping midsection fat.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c14_fb_row",
                    stationOrder = 4,
                    name = "Doorframe Rows",
                    stationBadge = "4. Posture & Back Burn",
                    targetMusclePill = "Lats & Mid-Back",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pull chest tall and squeeze shoulder blades 1s on every rep.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_fb_vacuum",
                    stationOrder = 5,
                    name = "Core Vacuum Plank Hold",
                    stationBadge = "5. Waist Corset Lock",
                    targetMusclePill = "Deep Waistline",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw navel inward toward spine while holding a rock-solid plank.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_fb_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown",
                    targetMusclePill = "Hips & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open tight hips and slow your breathing cleanly.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_21d_waist_shred",
            title = "21-Day Relentless Belly-Fat Shred & Core Cut",
            subtitle = "3-week metabolic escalation: split squats, close-grip presses, leg raises & plank burnouts",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 185,
            iconTag = "21-DAY WAIST SHRED",
            badgeColorHex = 0xFFB91C1C,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Men & Women • 21-Day Visible Waist-Size Reduction",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            challengeDays = 21,
            expectedResultsTimeline = "21 days: –2 to 3.5 kg fat stripped, 2–4 cm off navel waist & locked daily training habit",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c21_ws_jacks",
                    stationOrder = 1,
                    name = "Jumping Jacks",
                    stationBadge = "1. Cardio Ramp",
                    targetMusclePill = "Heart & Calves",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Elevate core temperature and prime ankle spring.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c21_ws_split",
                    stationOrder = 2,
                    name = "Split Squats",
                    stationBadge = "2. High-Output Legs",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Deep single-leg drive maximizes calorie expenditure per minute.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c21_ws_close_push",
                    stationOrder = 3,
                    name = "Close-Grip Push-Ups",
                    stationBadge = "3. Sleek Arm & Chest",
                    targetMusclePill = "Triceps & Chest",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Elbows tucked at 45° to carve arm & chest definition as fat drops.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c21_ws_leg_raise",
                    stationOrder = 4,
                    name = "Lying Straight-Leg Raises",
                    stationBadge = "4. Lower-Ab Cut",
                    targetMusclePill = "Lower Six-Pack",
                    setsAndRepsLabel = "x14",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Press lower back flat into mat and control legs down for 3 seconds.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c21_ws_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. Core Finisher",
                    targetMusclePill = "360° Waist Wall",
                    setsAndRepsLabel = "00:45",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Total-body brace from shoulders to heels.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c21_ws_cobra",
                    stationOrder = 6,
                    name = "Cobra Stretch",
                    stationBadge = "6. Cooldown",
                    targetMusclePill = "Abs & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen abdominal wall with calm exhales.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_7d_core_waist",
            title = "7-Day Intensive Waist-Tightening & Core Reset",
            subtitle = "Daily upper abs, lower tummy reverse crunches, planks & spine stretch",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 105,
            iconTag = "7-DAY CORE RESET",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            audienceTag = "Men & Women • Gym Mat or Home",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            challengeDays = 7,
            expectedResultsTimeline = "7 days: flatter morning waistline, stronger core brace & less lower-back fatigue",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c7_core_jacks",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Full Body",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth side steps and arm sweeps to raise core temperature safely.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c7_core_crunch",
                    stationOrder = 2,
                    name = "Abdominal Crunches",
                    stationBadge = "2. Upper Abs",
                    targetMusclePill = "Upper Abs",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale sharply at the top and hold contraction for 1 full second.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c7_core_rev_crunch",
                    stationOrder = 3,
                    name = "Reverse Crunches",
                    stationBadge = "3. Lower Abs",
                    targetMusclePill = "Lower Stomach",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Curl knees smoothly toward chest while keeping lower back glued down.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c7_core_plank",
                    stationOrder = 4,
                    name = "Forearm Plank Hold",
                    stationBadge = "4. Waist Lock",
                    targetMusclePill = "Core & Waist",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace abs and glutes tightly in a rigid straight line.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c7_core_cobra",
                    stationOrder = 5,
                    name = "Cobra Stretch",
                    stationBadge = "5. Cooldown",
                    targetMusclePill = "Abs & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open your chest gently and breathe slowly into your waist.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_7d_pushup_chest",
            title = "7-Day Intensive Push-Up & Upper-Chest Surge",
            subtitle = "Build chest fullness, triceps & upper-body pushing power at home",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 125,
            iconTag = "7-DAY CHEST SURGE",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Home Zero-Equipment Push Progression",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            challengeDays = 7,
            expectedResultsTimeline = "7 days: +5 to 8 clean push-up reps, fuller chest pump & stronger triceps",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c7_ch_warmup",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Chest & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open chest and shoulder joints before pressing volume.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c7_ch_incline",
                    stationOrder = 2,
                    name = "Incline Push-Ups",
                    stationBadge = "2. Upper Chest",
                    targetMusclePill = "Upper Chest",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hands slightly wider than shoulders, move at a smooth natural rhythm.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c7_ch_std",
                    stationOrder = 3,
                    name = "Push-Ups",
                    stationBadge = "3. Mid Chest",
                    targetMusclePill = "Chest & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep elbows at 45° and press palms firmly through the floor.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c7_ch_dips",
                    stationOrder = 4,
                    name = "Bench Tricep Dips",
                    stationBadge = "4. Triceps",
                    targetMusclePill = "Triceps",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lower smoothly until elbows reach 90° and press to lockout.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "c7_ch_knee",
                    stationOrder = 5,
                    name = "Knee Push-Up Finisher",
                    stationBadge = "5. Chest Burnout",
                    targetMusclePill = "Chest",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth, steady reps squeezing your chest cleanly at the top.",
                    animationType = ExerciseAnimationType.KNEE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c7_ch_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Shoulders & Triceps",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress shoulders and triceps after push-up sets.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_28d_full_body",
            title = "28-Day Total Gym Muscle & Recomp Overhaul",
            subtitle = "Incline DB press, lat pulldowns, goblet squats & weighted cable abs",
            durationLabel = "18 MINS",
            durationMinutes = 18,
            caloriesKcal = 190,
            iconTag = "28-DAY GYM OVERHAUL",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            audienceTag = "Men • Full Gym Body Recomposition",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 28,
            expectedResultsTimeline = "28 days: +20% gym lifting strength, wider V-taper lats & visibly tighter waist",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c28_fb_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Back",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Wake up upper-back posture and shoulder joints before lifting.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c28_fb_press",
                    stationOrder = 2,
                    name = "30° Incline Dumbbell Press",
                    stationBadge = "2. Upper Chest",
                    targetMusclePill = "Chest & Delts",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 20f,
                    formCueSummary = "Control 2s descent and drive dumbbells cleanly to lockout.",
                    animationType = ExerciseAnimationType.INCLINE_DB_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c28_fb_pulldown",
                    stationOrder = 3,
                    name = "Wide-Grip Lat Pulldown",
                    stationBadge = "3. V-Taper Back",
                    targetMusclePill = "Lats & Upper Back",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 45f,
                    formCueSummary = "Pull bar to upper chest while driving elbows down.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "c28_fb_squat",
                    stationOrder = 4,
                    name = "Dumbbell Goblet Squat",
                    stationBadge = "4. Legs & Glutes",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 22f,
                    formCueSummary = "Sit hips deep with upright chest and drive tall through heels.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c28_fb_cable_abs",
                    stationOrder = 5,
                    name = "Kneeling Cable Rope Crunch",
                    stationBadge = "5. Weighted Core",
                    targetMusclePill = "Six-Pack Abs",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 25f,
                    formCueSummary = "Exhale hard and crunch ribs toward pelvis.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c28_fb_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Lats & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen lats and lower back calmly.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_28d_home_lean",
            title = "28-Day Total Body Fat Overhaul & Lean Definition",
            subtitle = "4-week progressive fat-strip overhaul: lunges, pike shoulders, rows, abs & mobility",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 150,
            iconTag = "28-DAY FAT OVERHAUL",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "All Levels • 28-Day Full Home Fat-Loss Overhaul",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            challengeDays = 28,
            expectedResultsTimeline = "28 days: –3 to 4.5 kg fat loss, 1 clothing size tighter & full-body athletic definition",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c28_hl_jacks",
                    stationOrder = 1,
                    name = "Jumping Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Full Body",
                    setsAndRepsLabel = "00:40",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Light bounce to elevate heart rate and warm ankles and hips.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c28_hl_lunge",
                    stationOrder = 2,
                    name = "Forward Lunges",
                    stationBadge = "2. Legs & Glutes",
                    targetMusclePill = "Legs & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Step forward cleanly and push back through your front heel.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c28_hl_pike",
                    stationOrder = 3,
                    name = "Pike Shoulder Push-Ups",
                    stationBadge = "3. Shoulders",
                    targetMusclePill = "Shoulders",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lift hips into an inverted V and press smoothly overhead.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c28_hl_row",
                    stationOrder = 4,
                    name = "Doorframe Rows",
                    stationBadge = "4. Back & Posture",
                    targetMusclePill = "Upper Back",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lean back holding doorframe and squeeze shoulder blades 1s.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c28_hl_crunch",
                    stationOrder = 5,
                    name = "Abdominal Crunches",
                    stationBadge = "5. Abs",
                    targetMusclePill = "Abs",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep lower back pressed down and squeeze abs at the top.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c28_hl_cobra",
                    stationOrder = 6,
                    name = "Cobra Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Spine & Waist",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Relax shoulders down and lengthen your core.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_flexibility_mobility",
            title = "14-Day Increase Flexibility & Splits Flow",
            subtitle = "Unlock stiff hips, hamstrings, shoulders & full-body split mobility",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 75,
            iconTag = "14-DAY FLEXIBILITY",
            badgeColorHex = 0xFF0D9488,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Stiff Hips, Splits & Daily Flexibility",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_flx_warmup",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Spine & Shoulders",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Flow smoothly between hip pike and gentle chest lift.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "c14_flx_split_hold",
                    stationOrder = 2,
                    name = "Deep Split Squat Hold",
                    stationBadge = "2. Hip & Split Opener",
                    targetMusclePill = "Hips & Adductors",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sink gently into a long split stance and breathe slowly to unlock hips.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_flx_hinge",
                    stationOrder = 3,
                    name = "Bodyweight Romanian Deadlift Hinge",
                    stationBadge = "3. Hamstring Length",
                    targetMusclePill = "Hamstrings & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Push hips far back with flat spine to lengthen tight hamstrings.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_flx_lat_length",
                    stationOrder = 4,
                    name = "Kneeling Lat & Back Lengthening",
                    stationBadge = "4. Shoulder & Back Flow",
                    targetMusclePill = "Upper Back & Lats",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sink chest softly toward the mat with arms extended overhead.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_flx_runner",
                    stationOrder = 5,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "5. Cooldown Stretch",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Relax hip flexors with slow 6-second exhales.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_flx_cobra",
                    stationOrder = 6,
                    name = "Cobra Spine Stretch",
                    stationBadge = "6. Spine Stretch",
                    targetMusclePill = "Spine & Waist",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drop shoulders away from ears and lengthen front torso.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_posture_height",
            title = "14-Day Stand Taller & Height Posture Challenge",
            subtitle = "Spinal decompression, anti-slouch back pulls & pelvic alignment to reclaim tall posture",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 80,
            iconTag = "14-DAY TALL POSTURE",
            badgeColorHex = 0xFF4F46E5,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Decompress Spine & Stand Taller",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_ht_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Upper Back & Neck",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open rounded shoulders and lift crown of head tall.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_ht_hang",
                    stationOrder = 2,
                    name = "Bar Dead Hang / Doorway Reach",
                    stationBadge = "2. Spinal Decompression",
                    targetMusclePill = "Spine & Lats",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Let gravity gently traction and decompress your spine while breathing calmly.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "c14_ht_rows",
                    stationOrder = 3,
                    name = "Doorframe Posture Rows",
                    stationBadge = "3. Anti-Slouch Pull",
                    targetMusclePill = "Mid-Back & Rear Delts",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pull chest proud and lock shoulder blades back and down.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_ht_vacuum",
                    stationOrder = 4,
                    name = "Core Vacuum Plank Hold",
                    stationBadge = "4. Pelvic Tilt Fix",
                    targetMusclePill = "Deep Core & Pelvis",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Tuck pelvis neutral to eliminate lower-back arch that steals standing height.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_ht_hip_unlock",
                    stationOrder = 5,
                    name = "Hip Flexor Lunge Unlocker",
                    stationBadge = "5. Hip Alignment",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Release tight sitting hips so you stand effortlessly upright.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_ht_cobra",
                    stationOrder = 6,
                    name = "Cobra Spine Decompression",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Thoracic Spine",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen spine from tailbone through the crown of your head.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_kegel_pelvic",
            title = "14-Day Kegel & Pelvic Core Challenge",
            subtitle = "Discreet pelvic floor holds, pulses, glute-pelvic synergy & deep core control",
            durationLabel = "10 MINS",
            durationMinutes = 10,
            caloriesKcal = 65,
            iconTag = "14-DAY KEGEL",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Pelvic Floor Vitality & Core Control",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_kg_warmup",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Pelvis & Diaphragm",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sync calm breathing with gentle pelvic mobility.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "c14_kg_holds",
                    stationOrder = 2,
                    name = "Kegel Squeeze & Hold",
                    stationBadge = "2. Slow Kegel Holds",
                    targetMusclePill = "Pelvic Floor",
                    setsAndRepsLabel = "00:35",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lift pelvic floor inward for 5s, then relax completely for 5s while breathing.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c14_kg_bridge",
                    stationOrder = 3,
                    name = "Floor Glute Bridge Pelvic Squeeze",
                    stationBadge = "3. Pelvic-Glute Synergy",
                    targetMusclePill = "Pelvic Floor & Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale as hips rise, lifting pelvic floor and glutes together for 2s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c14_kg_vacuum",
                    stationOrder = 4,
                    name = "Core Vacuum Plank Hold",
                    stationBadge = "4. Deep Transverse Lock",
                    targetMusclePill = "Deep Lower Core",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw lower tummy gently inward with steady nasal breathing.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_kg_pulses",
                    stationOrder = 5,
                    name = "Lying Pelvic Tilt Crunches",
                    stationBadge = "5. Quick Reflex Pulses",
                    targetMusclePill = "Lower Core & Pelvic Sling",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Crisp 1-second pelvic tilt squeeze followed by full relaxation.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c14_kg_release",
                    stationOrder = 6,
                    name = "Cobra Belly Breath Release",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Pelvic Floor Release",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Inhale deep into your belly to let the pelvic floor completely lengthen.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_strength_power",
            title = "14-Day Increase Full-Body Strength Challenge",
            subtitle = "Progressive push-ups, rows, single-leg squats & high-tension core power",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 145,
            iconTag = "14-DAY STRENGTH",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.STRENGTH_GRIP,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            audienceTag = "Men & Women • Build Real Everyday Strength",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_str_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Joints & Nervous System",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Wake up hips, knees, and core bracing before strength sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c14_str_push",
                    stationOrder = 2,
                    name = "Push-Ups",
                    stationBadge = "2. Upper Push Strength",
                    targetMusclePill = "Chest, Shoulders & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "2-second controlled lowering and strong press through palms.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c14_str_pull",
                    stationOrder = 3,
                    name = "Doorframe Rows",
                    stationBadge = "3. Upper Pull Strength",
                    targetMusclePill = "Back, Lats & Grip",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lean back straight and pull chest firmly to your hands.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_str_legs",
                    stationOrder = 4,
                    name = "Split Squats",
                    stationBadge = "4. Single-Leg Power",
                    targetMusclePill = "Quads & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lower under control and drive powerfully through your front heel.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_str_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. High-Tension Core",
                    targetMusclePill = "Full Core",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Squeeze abs, quads, and glutes simultaneously for total-body tension.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_str_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Shoulders & Arms",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen upper body and slow your breathing.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_pullup_vtaper_mastery",
            title = "14-Day Zero-to-Pull-Up & Grip Challenge",
            subtitle = "Step-by-step dead hangs, inverted rows, lat squeezes & hollow core mastery",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 125,
            iconTag = "14-DAY PULL-UP",
            badgeColorHex = 0xFF1D4ED8,
            category = SpecialtyTrackCategory.STRENGTH_GRIP,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            audienceTag = "Men & Women • 0 to First Strict Pull-Up Ladder",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_pu_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Scapula & Shoulders",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Practice pulling shoulder blades down away from ears.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_pu_hang",
                    stationOrder = 2,
                    name = "Bar Dead Hang",
                    stationBadge = "2. Grip & Hang Base",
                    targetMusclePill = "Forearms & Lats",
                    setsAndRepsLabel = "00:30",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Wrap thumbs tight and hold active shoulder depression.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "c14_pu_inv_row",
                    stationOrder = 3,
                    name = "Elevated Bench Inverted Row",
                    stationBadge = "3. Assisted Pull Strength",
                    targetMusclePill = "Lats & Biceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive elbows past ribs and lower slowly for 3 full seconds.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_pu_door_row",
                    stationOrder = 4,
                    name = "Doorframe Rows",
                    stationBadge = "4. Lat Squeeze Volume",
                    targetMusclePill = "Mid-Back & Lats",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pause 1 full second squeezing lats hard at peak contraction.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_pu_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. Anti-Swing Hollow Core",
                    targetMusclePill = "Core Control",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Rigid core stops leg swinging when pulling on the bar.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_pu_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Lats & Forearms",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen lats and relax grip tension.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_calisthenics_mastery",
            title = "14-Day Explosive Street-Bar & L-Sit Conditioning",
            subtitle = "High-tempo hollow push-ups, bodyweight pulls, handstand presses & L-sit core endurance",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 145,
            iconTag = "14-DAY STREET BAR",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 14-Day Street-Workout Conditioning",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            challengeDays = 14,
            expectedResultsTimeline = "14 days: crisp hollow-body endurance, stronger bar pulls & L-sit hip compression",
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_cal_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Scapula",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Wake up shoulder blades and wrists before calisthenics leverage.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_cal_push",
                    stationOrder = 2,
                    name = "Strict Hollow Push-Ups",
                    stationBadge = "2. Hollow Push Base",
                    targetMusclePill = "Chest, Shoulders & Triceps",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Protract shoulder blades at the top with a straight hollow-body line.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c14_cal_row",
                    stationOrder = 3,
                    name = "Doorframe / Inverted Bar Rows",
                    stationBadge = "3. Bodyweight Pull Base",
                    targetMusclePill = "Lats & Upper Back",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lean back straight and pull sternum cleanly toward your hands.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c14_cal_pike",
                    stationOrder = 4,
                    name = "Wall Handstand Practice Press",
                    stationBadge = "4. Handstand Shoulder Base",
                    targetMusclePill = "Deltoids, Balance & Triceps",
                    setsAndRepsLabel = "x8",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Stack hips high in inverted V or against wall and press tall through palms.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c14_cal_leg_raise",
                    stationOrder = 5,
                    name = "Parallettes L-Sit Leg Lift",
                    stationBadge = "5. L-Sit Core Compression",
                    targetMusclePill = "Lower Abs & Hip Flexors",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Compress straight legs cleanly to build L-sit & hanging leg-raise strength.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c14_cal_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Lats & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen lats, shoulders, and spine calmly.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_handstand_lsit_skill",
            title = "14-Day Handstand, L-Sit & Balance Challenge",
            subtitle = "Step-by-step wall handstand balance, handstand walk taps, planche lean & L-sit",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 140,
            iconTag = "14-DAY HANDSTAND",
            badgeColorHex = 0xFF1D4ED8,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • Unlock Handstand Balance, Walk & L-Sit",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_hs_warmup",
                    stationOrder = 1,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Wrists & Overhead Mobility",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open wrists and shoulder flexion so your handstand line stacks straight.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c14_hs_press",
                    stationOrder = 2,
                    name = "Wall Handstand Practice Press",
                    stationBadge = "2. Inverted Overhead Stack",
                    targetMusclePill = "Shoulders, Wrists & Core",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Grip floor with fingertips to control balance while pressing shoulders tall.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "c14_hs_taps",
                    stationOrder = 3,
                    name = "Handstand Walk Shoulder Taps",
                    stationBadge = "3. Handstand Walk Weight-Shift",
                    targetMusclePill = "Single-Arm Balance & Obliques",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Shift weight calmly onto each hand to master freestanding & walking balance.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_hs_planche_lean",
                    stationOrder = 4,
                    name = "Parallettes Planche Hold & Lean",
                    stationBadge = "4. Straight-Arm Balance",
                    targetMusclePill = "Front Delts & Scapula",
                    setsAndRepsLabel = "00:25",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep elbows locked straight and lean shoulders forward under control.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_hs_lsit",
                    stationOrder = 5,
                    name = "Parallettes Straight-Leg L-Sit",
                    stationBadge = "5. L-Sit Core Compression",
                    targetMusclePill = "Abs, Hip Flexors & Triceps",
                    setsAndRepsLabel = "00:25",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Push down tall through straight arms and lift heels off the floor.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "c14_hs_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Wrists & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Shake out wrists and lengthen overhead shoulder line calmly.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_28d_calisthenics_elite_skills",
            title = "28-Day Planche, Muscle-Up, Lever & Flag Challenge",
            subtitle = "4-week calisthenics skill roadmap: Muscle-up pulls, planche leans, front lever & human flag",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 175,
            iconTag = "28-DAY SKILL PLAN",
            badgeColorHex = 0xFF7C3AED,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Men & Women • 4-Stage Beginner-to-Elite Calisthenics Roadmap",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c28_cal_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Wrists, Scapula & Tendons",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Prep wrists, elbows, and scapular control before skill sets.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c28_cal_muscleup",
                    stationOrder = 2,
                    name = "Explosive Muscle-Up Bar Pull",
                    stationBadge = "2. Muscle-Up Power",
                    targetMusclePill = "Lats, Chest & Transition",
                    setsAndRepsLabel = "x6",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive bar down toward lower ribs with explosive hollow-body control.",
                    animationType = ExerciseAnimationType.LAT_PULLDOWN
                ),
                ApexCoachMovementSpec(
                    id = "c28_cal_planche",
                    stationOrder = 3,
                    name = "Pseudo-Planche Push-Ups",
                    stationBadge = "3. Planche Push Leverage",
                    targetMusclePill = "Front Delts, Chest & Tendons",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lean shoulders forward past hands and protract hard at top lockout.",
                    animationType = ExerciseAnimationType.STANDARD_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "c28_cal_lever",
                    stationOrder = 4,
                    name = "Tuck Front Lever Hold & Row",
                    stationBadge = "4. Front Lever Suspension",
                    targetMusclePill = "Straight-Arm Lats & Core",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lock lats down and hold hips level with shoulders.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "c28_cal_flag",
                    stationOrder = 5,
                    name = "Human-Flag Side Lever Hold",
                    stationBadge = "5. Human Flag Oblique Lock",
                    targetMusclePill = "Lateral Core, Lats & Delts",
                    setsAndRepsLabel = "00:25",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Build rock-solid lateral push-pull tension for the human flag.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c28_cal_stretch",
                    stationOrder = 6,
                    name = "Kneeling Lat & Back Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Lats, Shoulders & Wrists",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Decompress lats, shoulders, and forearms calmly.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_stamina_endurance",
            title = "14-Day Increase Stamina & Cardio Challenge",
            subtitle = "Boost heart-lung endurance, stair-climbing stamina & all-day energy",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 155,
            iconTag = "14-DAY STAMINA",
            badgeColorHex = 0xFF0EA5E9,
            category = SpecialtyTrackCategory.STAMINA_ENDURANCE,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.STAMINA_CONDITIONING,
            audienceTag = "Men & Women • Never Run Out of Breath",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c14_stm_warmup",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Heart & Ankles",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth rhythmic steps to ramp heart rate gradually.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c14_stm_jacks",
                    stationOrder = 2,
                    name = "Jumping Jacks",
                    stationBadge = "2. Cardio Lung Engine",
                    targetMusclePill = "Cardio & Calves",
                    setsAndRepsLabel = "00:45",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Whisper-quiet landings with steady 2-in / 2-out breathing.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "c14_stm_squats",
                    stationOrder = 3,
                    name = "Bodyweight Squats",
                    stationBadge = "3. Leg Endurance",
                    targetMusclePill = "Quads, Glutes & Lungs",
                    setsAndRepsLabel = "x18",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Unbroken rhythmic squats without locking knees hard at top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c14_stm_lunges",
                    stationOrder = 4,
                    name = "Walking Lunges",
                    stationBadge = "4. Athletic Stride",
                    targetMusclePill = "Legs & Stamina",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep torso tall so lungs expand fully on every stride.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c14_stm_taps",
                    stationOrder = 5,
                    name = "Plank Shoulder Taps",
                    stationBadge = "5. Core Stamina Finisher",
                    targetMusclePill = "Core & Shoulders",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Breathe calmly under fatigue while keeping hips steady.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "c14_stm_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Hips & Recovery",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Long slow exhales to speed heart-rate recovery.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_7d_joint_balance_knees",
            title = "7-Day Bulletproof Knees, Hips & Balance",
            subtitle = "Strengthen knee tracking, outer hip stabilizers, glutes & ankle balance for daily life",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 90,
            iconTag = "7-DAY JOINT SHIELD",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.VITALITY_KEGEL,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            audienceTag = "Men & Women • Pain-Free Stairs, Knees & Balance",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "c7_jb_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Knees & Hips",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lubricate knee and hip joints with comfortable range.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c7_jb_split",
                    stationOrder = 2,
                    name = "Split Squats",
                    stationBadge = "2. Knee Tracking Control",
                    targetMusclePill = "Vastus Medialis & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hold wall lightly if needed and keep front knee aligned over mid-foot.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c7_jb_abduct",
                    stationOrder = 3,
                    name = "Standing Hip Abductions",
                    stationBadge = "3. Hip Stabilizer Shield",
                    targetMusclePill = "Gluteus Medius",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Strong outer hips stop knees from caving inward on stairs.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "c7_jb_bridge",
                    stationOrder = 4,
                    name = "Floor Glute Bridge",
                    stationBadge = "4. Posterior Support",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive heels into mat and hold top glute lock 2 seconds.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c7_jb_calf",
                    stationOrder = 5,
                    name = "Standing Calf Raises",
                    stationBadge = "5. Ankle Balance Lock",
                    targetMusclePill = "Calves & Ankles",
                    setsAndRepsLabel = "x18",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pause 1.5s at the top on big-toe mound for rock-solid ankle stability.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "c7_jb_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Quads & Hip Flexors",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Release front thigh tension that pulls on the kneecap.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        )
    )

    /**
     * 5-MINUTE MINIMUM RESCUE SESSION (Researched Habit-Retention Pattern):
     * Eliminates the "all-or-nothing" trap on tired or unmotivated days with zero-equipment moves.
     */
    val LOW_ENERGY_RESCUE_WORKOUT = QuickStrikeWorkout(
        id = "rescue_5min_no_zero_day",
        title = "5-Min Minimum Habit Rescue",
        subtitle = "Tired or unmotivated? Do this easy 3-phase flow to keep your consistency alive",
        durationLabel = "5 MINS",
        durationMinutes = 5,
        caloriesKcal = 40,
        iconTag = "HABIT RESCUE",
        badgeColorHex = 0xFF0F172A,
        category = SpecialtyTrackCategory.HOME_BACKUP,
        equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
        goalFocus = PackGoalFocus.MOBILITY_VITALITY,
        audienceTag = "All People • Tired or Busy Days",
        exercises = listOf(
            ApexCoachMovementSpec(
                id = "rescue_light_jacks",
                stationOrder = 1,
                name = "Easy Jumping Jacks / Step-Outs",
                stationBadge = "1. Warm-Up Blood Flow",
                targetMusclePill = "Full Body",
                setsAndRepsLabel = "00:40",
                totalSets = 1,
                defaultLoadKg = 0f,
                formCueSummary = "Calm, steady pace to wake up your breathing and joints.",
                animationType = ExerciseAnimationType.JUMPING_JACKS
            ),
            ApexCoachMovementSpec(
                id = "rescue_air_squat",
                stationOrder = 2,
                name = "Bodyweight Squats",
                stationBadge = "2. Main Activation",
                targetMusclePill = "Legs, Glutes & Core",
                setsAndRepsLabel = "x12",
                totalSets = 3,
                defaultLoadKg = 0f,
                formCueSummary = "Sit hips comfortably back and stand tall — habit protected!",
                animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
            ),
            ApexCoachMovementSpec(
                id = "rescue_cobra_stretch",
                stationOrder = 3,
                name = "Cobra Spine Stretch",
                stationBadge = "3. Stretch",
                targetMusclePill = "Spine & Breathing",
                setsAndRepsLabel = "00:30",
                totalSets = 1,
                defaultLoadKg = 0f,
                formCueSummary = "Take 3 slow breaths and finish proud.",
                animationType = ExerciseAnimationType.COBRA_STRETCH
            )
        )
    )

    /**
     * DEDICATED FEMALE BODY-SHAPING, GLUTES/BUTTOCKS, HOURGLASS WAIST, TONED ARMS & SLIMMING PACKS:
     * Built specifically around feminine proportions (lifted round buttocks, hip curves, flat lower tummy,
     * corset waistline, sleek arms/collarbone) across Full Gym, Small/Dumbbell Gym, and Zero-Equipment Home.
     */
    val FEMALE_SHAPING_WORKOUTS: List<QuickStrikeWorkout> = listOf(
        QuickStrikeWorkout(
            id = "track_glute_shelf_sculpt",
            title = "Gym Glute Shelf & Hip Curves",
            subtitle = "Padded hip thrusts, seated abductors, cable kickbacks & lunges for lifted buttocks",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 165,
            iconTag = "GLUTE SHELF",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Women • Full Gym Round Booty & Hip Curves",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_gs_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Glute Activation",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hip joints smoothly so glutes fire instead of lower back.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_gs_hip_thrust",
                    stationOrder = 2,
                    name = "Machine / Padded Hip Thrust",
                    stationBadge = "2. Primary Booty Builder",
                    targetMusclePill = "Glutes",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 40f,
                    formCueSummary = "Tuck chin slightly, drive through heels, and hold top glute lock 2s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_gs_abductor",
                    stationOrder = 3,
                    name = "Seated Hip Abductor Machine",
                    stationBadge = "3. Side Hip Curves",
                    targetMusclePill = "Upper & Side Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 30f,
                    formCueSummary = "Lean slightly forward and press knees wide with a 1s outer hold.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_gs_kickback",
                    stationOrder = 4,
                    name = "Cable Glute Kickbacks",
                    stationBadge = "4. Upper Glute Lift",
                    targetMusclePill = "Booty Shelf",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 10f,
                    formCueSummary = "Extend leg smoothly backward without arching your lower back.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_gs_lunges",
                    stationOrder = 5,
                    name = "Walking Lunges",
                    stationBadge = "5. Legs & Booty Tie-In",
                    targetMusclePill = "Glutes & Thighs",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 8f,
                    formCueSummary = "Long controlled step forward, driving tall through your front heel.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_gs_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hip Flexors & Glutes",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Release tight front hips so pelvis stays neutral and booty lifted.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_dumbbell_curves",
            title = "Dumbbell Booty & Sculpted Legs",
            subtitle = "Dumbbell hip thrusts, RDLs, Bulgarian split squats & goblet squats — small-gym ready",
            durationLabel = "16 MINS",
            durationMinutes = 16,
            caloriesKcal = 170,
            iconTag = "DUMBBELL CURVES",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Slim or Toned Women → Full Curves (Dumbbells Only)",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_db_warmup",
                    stationOrder = 1,
                    name = "Thoracic Hip Mobility Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Spine",
                    setsAndRepsLabel = "x8",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hips and lumbar hinge smoothly before dumbbell sets.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "f_db_thrust",
                    stationOrder = 2,
                    name = "Dumbbell Hip Thrust",
                    stationBadge = "2. Booty Projection",
                    targetMusclePill = "Gluteus Maximus",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 18f,
                    formCueSummary = "Rest upper back on bench, dumbbell on crease of hips, squeeze 2s at top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_db_rdl",
                    stationOrder = 3,
                    name = "Dumbbell Romanian Deadlift",
                    stationBadge = "3. Under-Booty & Hamstrings",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 14f,
                    formCueSummary = "Shave dumbbells down thighs while pushing hips far back.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_db_split",
                    stationOrder = 4,
                    name = "Dumbbell Bulgarian Split Squat",
                    stationBadge = "4. Deep Glute Roundness",
                    targetMusclePill = "Glutes & Legs",
                    setsAndRepsLabel = "x10",
                    totalSets = 3,
                    defaultLoadKg = 10f,
                    formCueSummary = "Slight forward torso lean puts maximum shaping tension on glutes.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_db_goblet",
                    stationOrder = 5,
                    name = "Dumbbell Goblet Squat",
                    stationBadge = "5. Toned Thighs",
                    targetMusclePill = "Legs & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 14f,
                    formCueSummary = "Sit smoothly between heels and press up without locking knees harshly.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_db_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hips & Legs",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen hips and thighs with slow calm breaths.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_home_booty_sculpt",
            title = "Home Booty Shelf & Side Curves",
            subtitle = "Couch hip thrusts, glute bridges, kickbacks & standing hip abductions — zero equipment",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 135,
            iconTag = "HOME BOOTY",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Women • Home Zero-Equipment Booty Sculpt",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_hb_warmup",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Blood Flow",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Step side-to-side smoothly to pump warm blood into glutes and hips.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "f_hb_thrust",
                    stationOrder = 2,
                    name = "Elevated Glute Hip Thrusts",
                    stationBadge = "2. Booty Fullness",
                    targetMusclePill = "Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Shoulders on couch edge, drive heels down and hold 2-second squeeze.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_hb_bridge",
                    stationOrder = 3,
                    name = "Floor Glute Bridge",
                    stationBadge = "3. Constant Glute Tension",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x18",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep ribs tucked down and squeeze buttocks every single rep.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_hb_kickback",
                    stationOrder = 4,
                    name = "Standing Glute Kickbacks",
                    stationBadge = "4. Upper Booty Shelf",
                    targetMusclePill = "Upper Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hold wall/chair lightly and lift heel back using upper glute only.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_hb_abduction",
                    stationOrder = 5,
                    name = "Standing Hip Abductions",
                    stationBadge = "5. Outer Hip Roundness",
                    targetMusclePill = "Side Glutes",
                    setsAndRepsLabel = "x16",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sweep straight leg out to the side with toes pointed forward.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_hb_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hips & Posture",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hip flexors gently to finish.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_toned_arms_posture",
            title = "Sleek Arms, Back & Collarbone (Gym)",
            subtitle = "Light high-rep cable rows, face pulls, lateral raises & rope tricep toning — zero bulk",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 125,
            iconTag = "TONED ARMS & BACK",
            badgeColorHex = 0xFF2563EB,
            category = SpecialtyTrackCategory.ARMS_POSTURE,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            audienceTag = "Women • Sleek Upper Body & Hourglass Posture",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_ta_warmup",
                    stationOrder = 1,
                    name = "Dynamic Chest & Posture Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Collarbone & Posture",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sweep arms wide and draw shoulders down away from your ears.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "f_ta_row",
                    stationOrder = 2,
                    name = "Seated Cable Row",
                    stationBadge = "2. Smooth Back & Bra-Line",
                    targetMusclePill = "Mid-Back Posture",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 22f,
                    formCueSummary = "Sit tall with long neck and glide elbows smoothly past ribs.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_ta_facepull",
                    stationOrder = 3,
                    name = "Cable Rope Face Pull",
                    stationBadge = "3. Rear Shoulder Posture",
                    targetMusclePill = "Rear Shoulders",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 12f,
                    formCueSummary = "Pull rope lightly toward temples for upright feminine posture.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_ta_lateral",
                    stationOrder = 4,
                    name = "Dumbbell Lateral Raise",
                    stationBadge = "4. Sleek Shoulder Line",
                    targetMusclePill = "Shoulders",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 4f,
                    formCueSummary = "Light dumbbells, smooth lift to make waist appear narrower.",
                    animationType = ExerciseAnimationType.CABLE_LATERAL_RAISE
                ),
                ApexCoachMovementSpec(
                    id = "f_ta_pushdown",
                    stationOrder = 5,
                    name = "Cable Rope Tricep Pushdown",
                    stationBadge = "5. Firm Under-Arms",
                    targetMusclePill = "Back of Arms",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 12f,
                    formCueSummary = "Keep elbows tucked and tighten the back of your arms cleanly.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "f_ta_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Shoulders & Arms",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen arms and neck gracefully.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_home_arms_posture",
            title = "Home Toned Arms & Swan Posture",
            subtitle = "Wall push-ups, floor tricep dips & doorframe pulls for sleek arms & tall posture",
            durationLabel = "10 MINS",
            durationMinutes = 10,
            caloriesKcal = 95,
            iconTag = "SLEEK ARMS",
            badgeColorHex = 0xFF6366F1,
            category = SpecialtyTrackCategory.ARMS_POSTURE,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            audienceTag = "Women • Home Zero-Equipment Arm & Back Tone",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_ha_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Upper Back & Neck",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Roll shoulders down and back to release phone/desk tension.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_ha_wall_push",
                    stationOrder = 2,
                    name = "Wall Push-Ups",
                    stationBadge = "2. Collarbone & Arm Tone",
                    targetMusclePill = "Arms & Upper Chest",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth high-rep presses against wall — tones arms without heavy joint load.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "f_ha_floor_dip",
                    stationOrder = 3,
                    name = "Floor Tricep Dips",
                    stationBadge = "3. Under-Arm Tightener",
                    targetMusclePill = "Back of Arms",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Bend and straighten elbows smoothly behind hips to firm triceps.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "f_ha_row",
                    stationOrder = 4,
                    name = "Doorframe Rows",
                    stationBadge = "4. Swan Back Posture",
                    targetMusclePill = "Upper Back",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw shoulder blades gently together for tall, open posture.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_ha_stretch",
                    stationOrder = 5,
                    name = "Standing Chest & Posture Opener",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Collarbone & Shoulders",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open collarbone wide with calm deep breaths.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_corset_waist",
            title = "Corset Waist & Flat Lower Tummy",
            subtitle = "Reverse crunches, deadbugs & vacuum planks to cinch waist without widening obliques",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 100,
            iconTag = "CORSET WAIST",
            badgeColorHex = 0xFFF59E0B,
            category = SpecialtyTrackCategory.CORE_WAIST,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            audienceTag = "Women • Gym Turf or Home Mat (Narrow Waist Focus)",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_cw_flow",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Warm abdominal wall and breath control smoothly.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "f_cw_rev",
                    stationOrder = 2,
                    name = "Reverse Crunches",
                    stationBadge = "2. Lower Tummy Flatten",
                    targetMusclePill = "Lower Tummy",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale all air as knees curl up so belly pulls inward, never outward.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_cw_deadbug",
                    stationOrder = 3,
                    name = "Alternating Deadbug",
                    stationBadge = "3. Deep Flat Stomach",
                    targetMusclePill = "Transverse Abdominis",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep lower back sealed flat against the mat throughout every reach.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_cw_vacuum",
                    stationOrder = 4,
                    name = "Stomach Vacuum Plank",
                    stationBadge = "4. Corset Cincher",
                    targetMusclePill = "Deep Waistline",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw navel gently toward spine like zipping tight jeans.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_cw_stretch",
                    stationOrder = 5,
                    name = "Cobra Abdominal Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen stomach and relax shoulders down.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_gym_fat_burn_sculpt",
            title = "Gym Metabolic Slim & Curve Circuit",
            subtitle = "High-rep sled leg press, cable rows, reverse lunges & waist plank for gym fat loss",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 175,
            iconTag = "SLIM & CURVE",
            badgeColorHex = 0xFF10B981,
            category = SpecialtyTrackCategory.FAT_BURN,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Women • Gym Fat Burn & Toned Shape (Knee-Safe)",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_gfb_warmup",
                    stationOrder = 1,
                    name = "Upper-Back Scapular Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Posture & Circulation",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open upper posture and brace core before metabolic stations.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_gfb_legpress",
                    stationOrder = 2,
                    name = "45° Sled Leg Press",
                    stationBadge = "2. Calorie-Torch Legs & Booty",
                    targetMusclePill = "Thighs & Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 50f,
                    formCueSummary = "High foot placement targets glutes and burns maximum calories safely.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_gfb_row",
                    stationOrder = 3,
                    name = "Seated Cable Row",
                    stationBadge = "3. Back & Waist Line",
                    targetMusclePill = "Upper Back",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 22f,
                    formCueSummary = "Brisk controlled rows to keep heart rate in the fat-burn zone.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_gfb_lunge",
                    stationOrder = 4,
                    name = "Dumbbell Reverse Lunges",
                    stationBadge = "4. Slim Thighs & Lift",
                    targetMusclePill = "Glutes & Legs",
                    setsAndRepsLabel = "x14",
                    totalSets = 3,
                    defaultLoadKg = 8f,
                    formCueSummary = "Step backward gently — much friendlier on knees than forward jumps.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_gfb_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. Flat Tummy Finisher",
                    targetMusclePill = "Deep Waist",
                    setsAndRepsLabel = "00:40",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Pull stomach inward and hold steady.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_gfb_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hips & Legs",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Cool down legs and open hips calmly.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "quick_slim_waist_legs",
            title = "Flat Tummy & Slim Thighs (Home)",
            subtitle = "Zero-jump step jacks, squats, glute bridges, crunches & waist stretch",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 115,
            iconTag = "SLIM & TONE",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.FAT_BURN,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
            audienceTag = "Women • Home Knee-Safe Slimming",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_sw_step_jacks",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Calorie Burn",
                    setsAndRepsLabel = "00:35",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Smooth low-impact step-outs to warm hips, waist, and legs.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "f_sw_squats",
                    stationOrder = 2,
                    name = "Bodyweight Squats",
                    stationBadge = "2. Legs & Booty",
                    targetMusclePill = "Thighs & Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Sit hips back smoothly and squeeze buttocks at the top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_sw_bridge",
                    stationOrder = 3,
                    name = "Floor Glute Bridge",
                    stationBadge = "3. Booty Lift",
                    targetMusclePill = "Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Press heels down and hold top glute squeeze 2s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_sw_crunch",
                    stationOrder = 4,
                    name = "Abdominal Crunches",
                    stationBadge = "4. Flat Tummy",
                    targetMusclePill = "Stomach",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale all air and curl ribs gently toward hips.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_sw_cobra",
                    stationOrder = 5,
                    name = "Cobra Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen stomach and breathe calmly.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_7d_booty_lift",
            title = "7-Day Booty Lift & Shape Challenge",
            subtitle = "Zero-equipment glute bridges, kickbacks & hip curves at home",
            durationLabel = "12 MINS",
            durationMinutes = 12,
            caloriesKcal = 120,
            iconTag = "7-DAY PLAN",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Women • Home 7-Day Booty Activation",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c7_step_jacks",
                    stationOrder = 1,
                    name = "Low-Impact Step Jacks",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Joints & Blood Flow",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Step side-to-side smoothly with overhead arm sweeps — zero harsh jumping.",
                    animationType = ExerciseAnimationType.JUMPING_JACKS
                ),
                ApexCoachMovementSpec(
                    id = "f_c7_glute_bridge",
                    stationOrder = 2,
                    name = "Floor Glute Bridge",
                    stationBadge = "2. Booty Lift",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drive through heels and squeeze buttocks tightly for 2 seconds at the top.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c7_kickbacks",
                    stationOrder = 3,
                    name = "Standing Glute Kickbacks",
                    stationBadge = "3. Upper Booty Shelf",
                    targetMusclePill = "Upper Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace stomach flat and extend leg back to lift the upper buttocks.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c7_abduction",
                    stationOrder = 4,
                    name = "Standing Hip Abductions",
                    stationBadge = "4. Hip Curves",
                    targetMusclePill = "Side Glutes & Hips",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Stand tall holding wall support and sweep leg outward to shape hip curves.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c7_hip_stretch",
                    stationOrder = 5,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "5. Stretch",
                    targetMusclePill = "Hips & Posture",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open tight hip flexors so your glutes stay lifted and active.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_28d_hourglass_curves",
            title = "28-Day Home Hourglass & Curves",
            subtitle = "Sculpt lifted buttocks, shapely legs & a cinched flat waistline at home",
            durationLabel = "14 MINS",
            durationMinutes = 14,
            caloriesKcal = 145,
            iconTag = "28-DAY HOME PLAN",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Women • Home 28-Day Hourglass Transformation",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c28_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lubricate hips and knees before sculpting sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c28_hip_thrust",
                    stationOrder = 2,
                    name = "Elevated Glute Hip Thrusts",
                    stationBadge = "2. Booty Roundness",
                    targetMusclePill = "Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Rest upper back on couch edge, drive through heels, and hold top squeeze 2s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c28_split_squat",
                    stationOrder = 3,
                    name = "Split Squats",
                    stationBadge = "3. Thighs & Booty",
                    targetMusclePill = "Glutes & Legs",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Keep front shin upright and press through front heel to sculpt legs and glutes.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c28_rev_crunch",
                    stationOrder = 4,
                    name = "Reverse Crunches",
                    stationBadge = "4. Flat Lower Tummy",
                    targetMusclePill = "Lower Tummy",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Curl knees smoothly toward ribs while keeping lower back glued to mat.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_c28_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. Cinched Waist",
                    targetMusclePill = "Deep Waistline",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw belly button inward like a corset and breathe calmly.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_c28_cobra",
                    stationOrder = 6,
                    name = "Cobra Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen stomach and relax shoulders down away from ears.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_28d_female_gym_curves",
            title = "28-Day Gym Curvy Hourglass Plan",
            subtitle = "Hip thrusts, dumbbell RDLs, seated hip abductors & cable posture rows",
            durationLabel = "18 MINS",
            durationMinutes = 18,
            caloriesKcal = 185,
            iconTag = "28-DAY GYM PLAN",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CHALLENGES,
            equipmentMode = PackEquipmentMode.FULL_GYM,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Slim or Toned Women → Curvy Hourglass (Gym)",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c28g_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hip joints smoothly before progressive glute loading.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c28g_thrust",
                    stationOrder = 2,
                    name = "Machine / Padded Hip Thrust",
                    stationBadge = "2. Heavy Booty Projection",
                    targetMusclePill = "Glutes",
                    setsAndRepsLabel = "x10",
                    totalSets = 4,
                    defaultLoadKg = 40f,
                    formCueSummary = "Drive heels into floor and hold full glute squeeze 2 seconds.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c28g_rdl",
                    stationOrder = 3,
                    name = "Dumbbell Romanian Deadlift",
                    stationBadge = "3. Glute-Hamstring Curve",
                    targetMusclePill = "Glutes & Hamstrings",
                    setsAndRepsLabel = "x12",
                    totalSets = 4,
                    defaultLoadKg = 14f,
                    formCueSummary = "Push hips far backward with neutral spine for deep glute stretch.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c28g_abductor",
                    stationOrder = 4,
                    name = "Seated Hip Abductor Machine",
                    stationBadge = "4. Outer Hip Curves",
                    targetMusclePill = "Side Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 30f,
                    formCueSummary = "Control both outward push and slow inward return.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c28g_row",
                    stationOrder = 5,
                    name = "Seated Cable Row",
                    stationBadge = "5. Hourglass Back Line",
                    targetMusclePill = "Upper Back & Waist Illusion",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 24f,
                    formCueSummary = "Tall chest, smooth pull to keep upper posture sleek and balanced.",
                    animationType = ExerciseAnimationType.SEATED_CABLE_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_c28g_stretch",
                    stationOrder = 6,
                    name = "Hip Flexor Lunge Stretch",
                    stationBadge = "6. Stretch",
                    targetMusclePill = "Hips & Lower Back",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Relax hip flexors and lower spine after training.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_hips_wider_curves",
            title = "14-Day Wider Hips & Side-Glute Curves Challenge",
            subtitle = "Target outer hip curves, side glutes, hip-dip fullness & upper booty projection",
            durationLabel = "13 MINS",
            durationMinutes = 13,
            caloriesKcal = 135,
            iconTag = "14-DAY HIP CURVES",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.MUSCLE_FOCUS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            audienceTag = "Women • Outer Hip Curves, Side Glutes & Feminine Shape",
            recommendedGoalIds = setOf("goal_2_vtaper", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c14h_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Hips & Glutes",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hip capsules smoothly before outer-hip sculpting sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c14h_abduct",
                    stationOrder = 2,
                    name = "Standing Hip Abductions",
                    stationBadge = "2. Outer Hip Curve",
                    targetMusclePill = "Side Glutes & Hips",
                    setsAndRepsLabel = "x16",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lead outward with your heel and pause 1s at peak side-hip squeeze.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c14h_thrust",
                    stationOrder = 3,
                    name = "Elevated Glute Hip Thrusts",
                    stationBadge = "3. Booty Roundness",
                    targetMusclePill = "Full Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Upper back on couch/bench edge, drive heels down and squeeze 2s.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_c14h_split",
                    stationOrder = 4,
                    name = "Split Squats",
                    stationBadge = "4. Hip-Glute Tie-In",
                    targetMusclePill = "Glutes & Outer Thigh",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Slight forward torso lean shifts tension straight into hips and glutes.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c14h_kickback",
                    stationOrder = 5,
                    name = "Standing Glute Kickbacks",
                    stationBadge = "5. Upper Shelf Lift",
                    targetMusclePill = "Upper Glutes",
                    setsAndRepsLabel = "x15",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Brace tummy flat and squeeze upper booty shelf cleanly.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_c14h_stretch",
                    stationOrder = 6,
                    name = "Runner's Hip Flexor Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Hip Flexors",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen front hips so your glutes stay lifted and active.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_14d_flat_tummy_corset",
            title = "14-Day Flat Lower Tummy & Cinched Waist Challenge",
            subtitle = "Flatten lower belly pooch & cinch natural corset muscles without bulking obliques",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 105,
            iconTag = "14-DAY FLAT TUMMY",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CORE_WAIST,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            audienceTag = "Women • Flat Lower Belly & Hourglass Waistline",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c14t_warmup",
                    stationOrder = 1,
                    name = "Thoracic Pike-to-Cobra Flow",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "x10",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Warm core wall and ribcage mobility gently.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                ),
                ApexCoachMovementSpec(
                    id = "f_c14t_rev_crunch",
                    stationOrder = 2,
                    name = "Reverse Crunches",
                    stationBadge = "2. Lower Tummy Flatten",
                    targetMusclePill = "Lower Tummy",
                    setsAndRepsLabel = "x15",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Exhale all air as knees curl up with lower back glued flat.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_c14t_vacuum",
                    stationOrder = 3,
                    name = "Core Vacuum Plank Hold",
                    stationBadge = "3. Corset Waist Cinch",
                    targetMusclePill = "Deep Waistline",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Draw belly button gently inward toward spine like a corset.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_c14t_leg_raise",
                    stationOrder = 4,
                    name = "Lying Leg Raises",
                    stationBadge = "4. Lower Belly Control",
                    targetMusclePill = "Lower Abs",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Soften knees slightly if needed so lower back stays pressed to mat.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_c14t_plank",
                    stationOrder = 5,
                    name = "Forearm Plank Hold",
                    stationBadge = "5. Flat Waist Finisher",
                    targetMusclePill = "Full Waist",
                    setsAndRepsLabel = "00:35",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Steady calm breathing with ribs pulled gently down.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_c14t_cobra",
                    stationOrder = 6,
                    name = "Cobra Spine Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Stomach & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen stomach muscles calmly.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        ),
        QuickStrikeWorkout(
            id = "challenge_7d_toned_arms_collarbone",
            title = "7-Day Sleek Arms, Swan Neck & Collarbone Challenge",
            subtitle = "Tighten under-arms, open rounded shoulders & sculpt graceful upper posture",
            durationLabel = "11 MINS",
            durationMinutes = 11,
            caloriesKcal = 100,
            iconTag = "7-DAY SLEEK ARMS",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.ARMS_POSTURE,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            audienceTag = "Women • Toned Triceps, Collarbone & Posture",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_3_athletic"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_c7a_warmup",
                    stationOrder = 1,
                    name = "Dynamic Scapular Back Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Shoulders & Neck",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Drop shoulders away from ears to lengthen neckline.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_c7a_dips",
                    stationOrder = 2,
                    name = "Floor Tricep Dips",
                    stationBadge = "2. Under-Arm Tightener",
                    targetMusclePill = "Back of Arms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Bend elbows smoothly and straighten fully to firm back of arms.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "f_c7a_incline",
                    stationOrder = 3,
                    name = "Incline Push-Ups",
                    stationBadge = "3. Collarbone Line",
                    targetMusclePill = "Upper Chest & Arms",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Hands on bench or bed edge, smooth controlled press.",
                    animationType = ExerciseAnimationType.INCLINE_PUSH_UPS
                ),
                ApexCoachMovementSpec(
                    id = "f_c7a_rows",
                    stationOrder = 4,
                    name = "Doorframe Rows",
                    stationBadge = "4. Graceful Back Line",
                    targetMusclePill = "Upper Back & Posture",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Gently pinch shoulder blades back to open chest and collarbones.",
                    animationType = ExerciseAnimationType.DOORFRAME_ROW
                ),
                ApexCoachMovementSpec(
                    id = "f_c7a_taps",
                    stationOrder = 5,
                    name = "Plank Shoulder Taps",
                    stationBadge = "5. Sleek Shoulder Tone",
                    targetMusclePill = "Shoulders & Core",
                    setsAndRepsLabel = "00:30",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Light, controlled taps keeping waist and shoulders steady.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_c7a_stretch",
                    stationOrder = 6,
                    name = "Overhead Shoulder & Tricep Stretch",
                    stationBadge = "6. Cooldown Stretch",
                    targetMusclePill = "Arms & Neckline",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Relax neck and lengthen arms gently.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                )
            )
        ),
        QuickStrikeWorkout(
            id = "track_female_calisthenics_sculpt",
            title = "Women's Calisthenics: Handstand, Pistol & L-Sit Flow",
            subtitle = "Wall handstand practice, handstand taps, pistol squats, parallettes L-sit & dips",
            durationLabel = "15 MINS",
            durationMinutes = 15,
            caloriesKcal = 150,
            iconTag = "WOMEN'S CALISTHENICS",
            badgeColorHex = 0xFF0F172A,
            category = SpecialtyTrackCategory.CALISTHENICS,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            audienceTag = "Women • Authentic Handstand, Pistol Squat & L-Sit Mastery",
            recommendedGoalIds = setOf("goal_1_fat_loss", "goal_2_vtaper", "goal_3_athletic", "goal_4_power_mass"),
            exercises = listOf(
                ApexCoachMovementSpec(
                    id = "f_cal_warmup",
                    stationOrder = 1,
                    name = "Dynamic Squat Opener",
                    stationBadge = "1. Warm-Up",
                    targetMusclePill = "Wrists, Hips & Ankles",
                    setsAndRepsLabel = "x12",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Open hips, ankles, and wrists smoothly before balance & skill sets.",
                    animationType = ExerciseAnimationType.BODYWEIGHT_SQUATS
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_handstand",
                    stationOrder = 2,
                    name = "Wall Handstand Practice Press",
                    stationBadge = "2. Handstand Inversion",
                    targetMusclePill = "Sculpted Shoulders & Balance",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Stack hips tall in pike or chest-to-wall handstand and push floor away.",
                    animationType = ExerciseAnimationType.PIKE_SHOULDER_PRESS
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_hs_taps",
                    stationOrder = 3,
                    name = "Handstand Walk Shoulder Taps",
                    stationBadge = "3. Handstand Balance Control",
                    targetMusclePill = "Core, Obliques & Delts",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Shift balance smoothly from hand to hand while keeping ribs tucked.",
                    animationType = ExerciseAnimationType.PLANK_HOLD
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_pistol",
                    stationOrder = 4,
                    name = "Assisted Single-Leg Pistol Squat",
                    stationBadge = "4. Single-Leg Pistol Skill",
                    targetMusclePill = "Glutes, Legs & Balance",
                    setsAndRepsLabel = "x8",
                    totalSets = 4,
                    defaultLoadKg = 0f,
                    formCueSummary = "Extend one leg forward (lightly hold pole/doorframe if needed) and sit deep.",
                    animationType = ExerciseAnimationType.FORWARD_LUNGES
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_lsit",
                    stationOrder = 5,
                    name = "Parallettes L-Sit Leg Lift",
                    stationBadge = "5. L-Sit Core Compression",
                    targetMusclePill = "Flat Waist & Hip Flexors",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Press shoulders down tall and lift straight legs cleanly off the mat.",
                    animationType = ExerciseAnimationType.ABDOMINAL_CRUNCHES
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_dips",
                    stationOrder = 6,
                    name = "Parallettes Floor Tricep Dips",
                    stationBadge = "6. Sleek Arm Lockout",
                    targetMusclePill = "Triceps & Collarbone",
                    setsAndRepsLabel = "x12",
                    totalSets = 3,
                    defaultLoadKg = 0f,
                    formCueSummary = "Bend and lock elbows cleanly for strong straight-arm support.",
                    animationType = ExerciseAnimationType.TRICEP_DIPS
                ),
                ApexCoachMovementSpec(
                    id = "f_cal_stretch",
                    stationOrder = 7,
                    name = "Cobra Spine Stretch",
                    stationBadge = "7. Cooldown Stretch",
                    targetMusclePill = "Waist & Spine",
                    setsAndRepsLabel = "00:30",
                    totalSets = 1,
                    defaultLoadKg = 0f,
                    formCueSummary = "Lengthen stomach and relax wrists and shoulders down.",
                    animationType = ExerciseAnimationType.COBRA_STRETCH
                )
            )
        )
    )

    private val MALE_BULK_ONLY_WORKOUT_IDS = setOf(
        "challenge_7d_pushup_chest",
        "challenge_28d_full_body",
        "track_chest_power",
        "track_wide_back",
        "track_leg_drive",
        "track_male_dumbbell_upper_lower",
        "track_male_home_legs_glutes",
        "track_arms_3d_delts",
        "track_male_home_arms_delts",
        "track_male_gym_sixpack_core",
        "track_male_gym_fat_furnace",
        "track_grip_power",
        "track_raw_strength"
    )

    private val FEMALE_SHAPING_WORKOUT_IDS: Set<String> by lazy {
        FEMALE_SHAPING_WORKOUTS.map { it.id }.toSet()
    }

    val GYM_DASHBOARD_BOOSTERS: List<QuickStrikeWorkout> = listOf(
        CHALLENGE_WORKOUTS[0],
        CHALLENGE_WORKOUTS[2],
        SPECIALTY_GOAL_WORKOUTS[0],
        SPECIALTY_GOAL_WORKOUTS[1]
    )

    val ALL_HUB_CATEGORY_WORKOUTS: List<QuickStrikeWorkout> =
        listOf(LOW_ENERGY_RESCUE_WORKOUT) +
            FEMALE_SHAPING_WORKOUTS +
            CHALLENGE_WORKOUTS +
            SPECIALTY_GOAL_WORKOUTS +
            QUICK_STRIKE_WORKOUTS

    fun workoutsForSex(sex: BiologicalSex): List<QuickStrikeWorkout> {
        // Deduplicate standalone Kegel twin ("track_pelvic_kegel") from general Hub cards since
        // "challenge_14d_kegel_pelvic" covers the 14-Day Challenge and TargetExerciseDirectory covers single Kegel sessions!
        val duplicateHubTwinIds = setOf(LOW_ENERGY_RESCUE_WORKOUT.id, "track_pelvic_kegel")
        val base = ALL_HUB_CATEGORY_WORKOUTS.filterNot { it.id in duplicateHubTwinIds }
        return if (sex == BiologicalSex.FEMALE) {
            base.filterNot { it.id in MALE_BULK_ONLY_WORKOUT_IDS }
        } else {
            base.filterNot { it.id in FEMALE_SHAPING_WORKOUT_IDS }
        }
    }

    fun categoriesForSex(sex: BiologicalSex): List<SpecialtyTrackCategory> {
        val hiddenInternal = setOf(SpecialtyTrackCategory.HOME_BACKUP)
        return SpecialtyTrackCategory.entries.filterNot { it in hiddenInternal }
    }

    fun isRecommendedForProfile(
        workout: QuickStrikeWorkout,
        sex: BiologicalSex,
        goalCardId: String,
        startingShapeId: String
    ): Boolean {
        val isHeavyStart = startingShapeId == "shape_1_high_bf" || startingShapeId == "shape_2_soft"
        val isSlimStart = startingShapeId == "shape_5_underweight"
        return when {
            isHeavyStart && goalCardId == "goal_1_fat_loss" ->
                workout.goalFocus == PackGoalFocus.FAT_LOSS_SLIMMING ||
                    workout.goalFocus == PackGoalFocus.CORE_WAIST_CINCH
            isSlimStart && (goalCardId == "goal_4_power_mass" || goalCardId == "goal_2_vtaper") ->
                if (sex == BiologicalSex.FEMALE) {
                    workout.goalFocus == PackGoalFocus.GLUTES_CURVES
                } else {
                    workout.goalFocus == PackGoalFocus.MUSCLE_HYPERTROPHY ||
                        workout.goalFocus == PackGoalFocus.STRENGTH_POWER
                }
            workout.recommendedGoalIds.contains(goalCardId) ->
                workout.goalFocus != PackGoalFocus.MOBILITY_VITALITY
            else -> false
        }
    }

    private fun profileRelevanceScore(
        workout: QuickStrikeWorkout,
        sex: BiologicalSex,
        goalCardId: String,
        startingShapeId: String,
        activeEnvironment: TrainingEnvironment
    ): Int {
        var score = 0
        if (isRecommendedForProfile(workout, sex, goalCardId, startingShapeId)) {
            score += 40
        }
        if (workout.recommendedGoalIds.contains(goalCardId)) {
            score += 20
        }
        val isHeavyStart = startingShapeId == "shape_1_high_bf" || startingShapeId == "shape_2_soft"
        val isSlimStart = startingShapeId == "shape_5_underweight"
        if (isHeavyStart && ("zero-jump" in workout.title.lowercase() || "knee-safe" in workout.audienceTag.lowercase() || workout.goalFocus == PackGoalFocus.FAT_LOSS_SLIMMING)) {
            score += 25
        }
        if (isSlimStart && (workout.goalFocus == PackGoalFocus.GLUTES_CURVES || workout.goalFocus == PackGoalFocus.MUSCLE_HYPERTROPHY)) {
            score += 25
        }
        when (activeEnvironment) {
            TrainingEnvironment.GYM -> {
                if (workout.equipmentMode == PackEquipmentMode.FULL_GYM) score += 18
                else if (workout.equipmentMode == PackEquipmentMode.DUMBBELL_SMALL_GYM) score += 14
                else if (workout.equipmentMode == PackEquipmentMode.ANYWHERE_MAT) score += 6
            }
            TrainingEnvironment.HOME -> {
                if (workout.equipmentMode == PackEquipmentMode.NO_EQUIPMENT_HOME) score += 18
                else if (workout.equipmentMode == PackEquipmentMode.ANYWHERE_MAT) score += 12
            }
        }
        return score
    }

    fun workoutsForProfile(
        sex: BiologicalSex,
        goalCardId: String = "goal_3_athletic",
        startingShapeId: String = "shape_3_skinny_fat",
        category: SpecialtyTrackCategory = SpecialtyTrackCategory.ALL,
        equipmentFilter: PackEquipmentFilter = PackEquipmentFilter.MATCHED,
        activeEnvironment: TrainingEnvironment = TrainingEnvironment.GYM
    ): List<QuickStrikeWorkout> {
        val sexPool = workoutsForSex(sex)
        val envFiltered = sexPool.filter { workout ->
            equipmentFilter.matches(workout.equipmentMode, activeEnvironment)
        }
        val workingPool = envFiltered.ifEmpty { sexPool }

        val catFiltered = when (category) {
            SpecialtyTrackCategory.ALL -> workingPool
            SpecialtyTrackCategory.FOR_YOU -> {
                val matched = workingPool.filter {
                    isRecommendedForProfile(it, sex, goalCardId, startingShapeId)
                }
                matched.ifEmpty { workingPool.take(6) }
            }
            SpecialtyTrackCategory.CHALLENGES -> {
                val direct = workingPool.filter {
                    it.category == SpecialtyTrackCategory.CHALLENGES || it.isMultiDayChallenge
                }
                direct.ifEmpty {
                    sexPool.filter {
                        it.category == SpecialtyTrackCategory.CHALLENGES || it.isMultiDayChallenge
                    }
                }
            }
            else -> {
                val direct = workingPool.filter { it.category == category }
                direct.ifEmpty { sexPool.filter { it.category == category } }
            }
        }

        if (category == SpecialtyTrackCategory.CALISTHENICS) {
            val stageOrder = mapOf(
                "track_calisthenics_stage1_foundation" to 1,
                "track_gym_calisthenics_hybrid" to 2,
                "track_home_calisthenics_strength" to 3,
                "track_calisthenics_stage4_elite" to 4,
                "track_female_calisthenics_sculpt" to 5,
                "challenge_14d_calisthenics_mastery" to 6,
                "challenge_14d_handstand_lsit_skill" to 7,
                "challenge_28d_calisthenics_elite_skills" to 8,
                "track_calisthenics_fat_burn_flow" to 9
            )
            return catFiltered.sortedBy { stageOrder[it.id] ?: 99 }
        }

        return catFiltered.sortedByDescending {
            profileRelevanceScore(it, sex, goalCardId, startingShapeId, activeEnvironment)
        }
    }

    fun dashboardBoostersForProfile(
        sex: BiologicalSex,
        environment: TrainingEnvironment,
        goalCardId: String = "goal_3_athletic",
        startingShapeId: String = "shape_3_skinny_fat"
    ): List<QuickStrikeWorkout> {
        return workoutsForProfile(
            sex = sex,
            goalCardId = goalCardId,
            startingShapeId = startingShapeId,
            category = SpecialtyTrackCategory.FOR_YOU,
            equipmentFilter = PackEquipmentFilter.MATCHED,
            activeEnvironment = environment
        ).take(4)
    }

    fun dashboardBoostersForSex(
        sex: BiologicalSex,
        environment: TrainingEnvironment
    ): List<QuickStrikeWorkout> {
        return if (sex == BiologicalSex.FEMALE) {
            if (environment == TrainingEnvironment.GYM) {
                listOf(
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "challenge_7d_booty_lift" },
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "track_glute_shelf_sculpt" },
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "track_female_dumbbell_curves" }
                )
            } else {
                listOf(
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "challenge_7d_booty_lift" },
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "challenge_28d_hourglass_curves" },
                    FEMALE_SHAPING_WORKOUTS.first { it.id == "quick_slim_waist_legs" }
                )
            }
        } else {
            if (environment == TrainingEnvironment.GYM) {
                listOf(
                    SPECIALTY_GOAL_WORKOUTS.first { it.id == "track_chest_power" },
                    SPECIALTY_GOAL_WORKOUTS.first { it.id == "track_wide_back" },
                    CHALLENGE_WORKOUTS.first { it.id == "challenge_28d_full_body" }
                )
            } else {
                listOf(
                    CHALLENGE_WORKOUTS.first { it.id == "challenge_7d_pushup_chest" },
                    CHALLENGE_WORKOUTS.first { it.id == "challenge_28d_home_lean" },
                    QUICK_STRIKE_WORKOUTS.first { it.id == "quick_home_wake_up" }
                )
            }
        }
    }

    fun defaultTimelineForWorkout(workout: QuickStrikeWorkout): String {
        val idLower = workout.id.lowercase()
        val titleLower = workout.title.lowercase()
        val days = workout.resolvedChallengeDays
        return when {
            "kegel" in idLower || "pelvic" in idLower ->
                "2–3 wks control • 6–8 wks full pelvic strength"
            "jaw" in idLower || "tongue" in idLower || "neck" in idLower ->
                "7–14 days posture • 6–8 wks neck & jawline tone"
            "voice" in idLower || "breath" in idLower ->
                "3–7 days vocal support • 3–4 wks lung stamina"
            "grip" in idLower ->
                "2–4 wks firmer grip & forearm endurance"
            "height" in idLower || "posture" in idLower ->
                "7–14 days +1–2 cm taller spinal posture"
            "flexibility" in idLower || "stretch" in idLower ->
                "2–3 wks joint looseness • 8–12 wks deep splits"
            "stamina" in idLower || "hiit" in idLower || "cardio" in idLower ->
                "10–14 days easier breathing • 4 wks stamina"
            "pullup" in idLower || "pull-up" in titleLower ->
                "2 wks bar control • 4–6 wks strict pull-up reps"
            "stage4" in idLower || "elite" in idLower || "planche" in titleLower ->
                "4 wks tuck/lean mastery • 3–6 mos full elite holds"
            "calisthenics" in idLower || "handstand" in idLower ->
                if (days >= 21) "3 wks lever & muscle-up power • 8–12 wks clean skill"
                else "14 days cleaner balance, scapular lock & bodyweight control"
            "booty" in idLower || "glute" in idLower || "hips" in idLower || "hourglass" in idLower ->
                if (days == 7) "7 days glute wake-up • 6–8 wks visible curve lift"
                else "3–4 wks firmer lift • 8–10 wks fuller hip & glute shape"
            days == 7 ->
                "7 days reduced bloat, sweat kickstart & muscle wake-up"
            days == 14 ->
                "14 days tighter waist control & strength endurance"
            days == 21 ->
                "21 days visible waist drop & sharper muscle tone"
            days >= 28 ->
                "4 wks noticeable body recomposition & stamina upgrade"
            else -> when (workout.goalFocus) {
                PackGoalFocus.GLUTES_CURVES -> "2–3 wks glute activation • 6–8 wks visible curve lift"
                PackGoalFocus.CORE_WAIST_CINCH -> "2–3 wks tighter core brace • 6–8 wks waist definition"
                PackGoalFocus.FAT_LOSS_SLIMMING -> "2 wks higher daily burn • 6–8 wks visible fat drop"
                PackGoalFocus.STRENGTH_POWER -> "2–3 wks neural strength • 6–8 wks heavier lifts"
                PackGoalFocus.CALISTHENICS_CONTROL -> "2–3 wks joint & hollow control • 6–10 wks skill unlock"
                PackGoalFocus.STAMINA_CONDITIONING -> "10–14 days recovery speed • 4 wks lung engine"
                PackGoalFocus.MOBILITY_VITALITY -> "7–14 days posture ease • 4–6 wks deep mobility"
                PackGoalFocus.TONE_POSTURE -> "2–3 wks taller posture • 6–8 wks sculpted arms & delts"
                PackGoalFocus.MUSCLE_HYPERTROPHY -> "2–3 wks muscle fullness • 6–8 wks visible size"
            }
        }
    }
}

enum class PackEquipmentMode(
    val id: String,
    val shortBadge: String
) {
    FULL_GYM("FULL_GYM", "GYM MACHINES"),
    DUMBBELL_SMALL_GYM("DUMBBELL_SMALL_GYM", "DUMBBELLS / SMALL GYM"),
    NO_EQUIPMENT_HOME("NO_EQUIPMENT_HOME", "HOME • NO EQUIP"),
    ANYWHERE_MAT("ANYWHERE_MAT", "MAT • ANYWHERE")
}

enum class PackEquipmentFilter(
    val id: String
) {
    MATCHED("MATCHED"),
    GYM_WEIGHTS("GYM_WEIGHTS"),
    HOME_NO_EQUIP("HOME_NO_EQUIP"),
    ALL_EQUIPMENT("ALL_EQUIPMENT");

    fun labelFor(activeEnv: TrainingEnvironment): String = when (this) {
        MATCHED -> if (activeEnv == TrainingEnvironment.GYM) "Matched • Gym" else "Matched • Home"
        GYM_WEIGHTS -> "Gym & Dumbbells"
        HOME_NO_EQUIP -> "Home • No Equip"
        ALL_EQUIPMENT -> "All Equipment"
    }

    fun matches(mode: PackEquipmentMode, activeEnv: TrainingEnvironment): Boolean = when (this) {
        MATCHED -> if (activeEnv == TrainingEnvironment.GYM) {
            mode == PackEquipmentMode.FULL_GYM ||
                mode == PackEquipmentMode.DUMBBELL_SMALL_GYM ||
                mode == PackEquipmentMode.ANYWHERE_MAT
        } else {
            mode == PackEquipmentMode.NO_EQUIPMENT_HOME ||
                mode == PackEquipmentMode.ANYWHERE_MAT
        }
        GYM_WEIGHTS ->
            mode == PackEquipmentMode.FULL_GYM || mode == PackEquipmentMode.DUMBBELL_SMALL_GYM
        HOME_NO_EQUIP ->
            mode == PackEquipmentMode.NO_EQUIPMENT_HOME || mode == PackEquipmentMode.ANYWHERE_MAT
        ALL_EQUIPMENT -> true
    }
}

enum class PackGoalFocus(
    val badge: String
) {
    GLUTES_CURVES("BOOTY & HIPS"),
    MUSCLE_HYPERTROPHY("MUSCLE & SIZE"),
    CORE_WAIST_CINCH("WAIST & CORE"),
    FAT_LOSS_SLIMMING("FAT BURN"),
    TONE_POSTURE("ARMS & POSTURE"),
    STRENGTH_POWER("STRENGTH & PULL-UP"),
    CALISTHENICS_CONTROL("CALISTHENICS"),
    STAMINA_CONDITIONING("STAMINA & CARDIO"),
    MOBILITY_VITALITY("STRETCHING & FLEXIBILITY")
}

enum class SpecialtyTrackCategory(
    val id: String,
    val label: String
) {
    ALL("ALL", "All Packs"),
    FOR_YOU("FOR_YOU", "For Your Goal"),
    CHALLENGES("CHALLENGES", "Goal Challenges"),
    CALISTHENICS("CALISTHENICS", "Calisthenics"),
    MUSCLE_FOCUS("MUSCLE_FOCUS", "Chest, Back & Legs"),
    ARMS_POSTURE("ARMS_POSTURE", "Arms & 3D Delts"),
    CORE_WAIST("CORE_WAIST", "Six-Pack & Core"),
    FAT_BURN("FAT_BURN", "Fat Burn & Shred"),
    STRENGTH_GRIP("STRENGTH_GRIP", "Strength & Pull-Up"),
    STAMINA_ENDURANCE("STAMINA_ENDURANCE", "Stamina & Cardio"),
    VITALITY_KEGEL("VITALITY_KEGEL", "Stretching, Height & Kegel"),
    HOME_BACKUP("HOME_BACKUP", "Quick Rescue");

    fun labelFor(sex: BiologicalSex): String = when (this) {
        ALL -> "All Packs"
        FOR_YOU -> "For Your Goal"
        CHALLENGES -> "Goal Challenges"
        CALISTHENICS -> "Calisthenics"
        MUSCLE_FOCUS -> if (sex == BiologicalSex.FEMALE) "Booty, Hips & Curves" else "Chest, Back & Legs"
        ARMS_POSTURE -> if (sex == BiologicalSex.FEMALE) "Sleek Arms & Collarbone" else "Arms & 3D Delts"
        CORE_WAIST -> if (sex == BiologicalSex.FEMALE) "Flat Tummy & Waist" else "Six-Pack & Core"
        FAT_BURN -> if (sex == BiologicalSex.FEMALE) "Slim & Fat Burn" else "Fat Burn & Shred"
        STRENGTH_GRIP -> "Strength & Pull-Up"
        STAMINA_ENDURANCE -> "Stamina & Cardio"
        VITALITY_KEGEL -> "Stretching, Height & Kegel"
        HOME_BACKUP -> "Quick Rescue"
    }
}

data class QuickStrikeWorkout(
    val id: String,
    val title: String,
    val subtitle: String,
    val durationLabel: String,
    val durationMinutes: Int,
    val caloriesKcal: Int,
    val iconTag: String,
    val badgeColorHex: Long,
    val exercises: List<ApexCoachMovementSpec>,
    val category: SpecialtyTrackCategory = SpecialtyTrackCategory.HOME_BACKUP,
    val equipmentMode: PackEquipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
    val goalFocus: PackGoalFocus = PackGoalFocus.FAT_LOSS_SLIMMING,
    val audienceTag: String = "All Levels",
    val recommendedGoalIds: Set<String> = emptySet(),
    val challengeDays: Int = 0,
    val expectedResultsTimeline: String = ""
) {
    val resolvedChallengeDays: Int
        get() = when {
            challengeDays > 0 -> challengeDays
            id.startsWith("challenge_28d_") -> 28
            id.startsWith("challenge_21d_") -> 21
            id.startsWith("challenge_14d_") -> 14
            id.startsWith("challenge_7d_") -> 7
            else -> 0
        }

    val isMultiDayChallenge: Boolean
        get() = resolvedChallengeDays > 0

    val resolvedResultsTimeline: String
        get() = expectedResultsTimeline.ifBlank {
            HomeWorkoutCatalog.defaultTimelineForWorkout(this)
        }
}

data class ApexCoachMovementSpec(
    val id: String,
    val stationOrder: Int,
    val name: String,
    val stationBadge: String,
    val targetMusclePill: String,
    val setsAndRepsLabel: String,
    val totalSets: Int,
    val defaultLoadKg: Float,
    val formCueSummary: String,
    val animationType: ExerciseAnimationType
)



