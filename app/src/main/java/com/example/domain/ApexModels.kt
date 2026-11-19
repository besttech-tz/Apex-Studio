package com.example.domain

enum class BiologicalSex(val id: String, val label: String) {
    MALE("MALE", "MALE"),
    FEMALE("FEMALE", "FEMALE");

    companion object {
        fun fromId(id: String?): BiologicalSex =
            entries.firstOrNull { it.id == id } ?: MALE
    }
}

enum class AgeTier(
    val id: String,
    val label: String,
    val representativeAge: Int,
    val metabolicFactor: Float
) {
    UNDER_30("UNDER_30", "< 30", 25, 1.00f),
    AGE_30_45("AGE_30_45", "30–45", 37, 0.96f),
    AGE_45_PLUS("AGE_45_PLUS", "45+", 52, 0.91f);

    companion object {
        fun fromId(id: String?): AgeTier =
            entries.firstOrNull { it.id == id } ?: UNDER_30
    }
}

enum class ExperienceLevel(
    val id: String,
    val label: String,
    val shortTag: String,
    val challengeLabel: String
) {
    NEW_BEGINNER(
        "NEW_BEGINNER",
        "Never Trained",
        "New to exercise • Light affordable kg, zero-jump impact & easier angles",
        "First Time"
    ),
    BEGINNER_JOINT_SAFE(
        "BEGINNER_JOINT_SAFE",
        "Trained Some",
        "Know basic moves • Moderate load & natural rhythm",
        "Regular"
    ),
    INTERMEDIATE(
        "INTERMEDIATE",
        "Experienced",
        "Lift regularly • Full load, full sets & controlled tension",
        "Advanced"
    );

    val isBeginnerSafe: Boolean
        get() = this != INTERMEDIATE

    /**
     * Extra pre-set setup time BEFORE Rep 1 starts so beginners can get into position calmly
     * without ever forcing slow-motion reps during the set.
     */
    val prepLeadInMs: Long
        get() = when (this) {
            NEW_BEGINNER -> 3200L
            BEGINNER_JOINT_SAFE -> 2600L
            INTERMEDIATE -> 2100L
        }

    /**
     * Bodyweight movements (push-ups, squats, lunges, dips, crunches) become much harder when
     * performed slowly due to high time-under-tension. Beginners move at a crisp, natural
     * ~2.05s rhythm, while experienced lifters hold slightly more deliberate tension.
     */
    val bodyweightRepDurationMs: Long
        get() = when (this) {
            NEW_BEGINNER -> 2050L
            BEGINNER_JOINT_SAFE -> 2150L
            INTERMEDIATE -> 2350L
        }

    /**
     * Guided weighted lift duration: natural, fluid cadence for beginners and controlled
     * hypertrophy tension for experienced lifters.
     */
    val strengthRepDurationMs: Long
        get() = when (this) {
            NEW_BEGINNER -> 2250L
            BEGINNER_JOINT_SAFE -> 2400L
            INTERMEDIATE -> 2550L
        }

    val cardioRepDurationMs: Long
        get() = when (this) {
            NEW_BEGINNER -> 1150L
            BEGINNER_JOINT_SAFE -> 1150L
            INTERMEDIATE -> 1100L
        }

    fun repDurationMsFor(animationType: ExerciseAnimationType, isBodyweight: Boolean): Long = when {
        animationType == ExerciseAnimationType.JUMPING_JACKS -> cardioRepDurationMs
        isBodyweight -> bodyweightRepDurationMs
        else -> strengthRepDurationMs
    }

    val paceBadgeLabel: String
        get() = when (this) {
            NEW_BEGINNER -> "Light Start"
            BEGINNER_JOINT_SAFE -> "Moderate Load"
            INTERMEDIATE -> "Full Load"
        }

    companion object {
        fun fromId(id: String?): ExperienceLevel =
            entries.firstOrNull { it.id == id } ?: NEW_BEGINNER
    }
}

enum class WeeklyFrequency(
    val id: String,
    val daysCount: Int,
    val title: String,
    val subtitle: String,
    val schedule: String,
    val expectedHorizon: String,
    val tag: String
) {
    THREE_DAYS(
        id = "THREE_DAYS",
        daysCount = 3,
        title = "3 DAYS / WEEK",
        subtitle = "Busy Schedule Essentials",
        schedule = "Day 1: Push • Day 2: Pull • Day 3: Legs & Stomach (4 Days Off)",
        expectedHorizon = "12–14 Week Steady Build",
        tag = "MINIMUM EFFECTIVE"
    ),
    FOUR_DAYS(
        id = "FOUR_DAYS",
        daysCount = 4,
        title = "4 DAYS / WEEK",
        subtitle = "Smart Recovery & Fat Loss",
        schedule = "Chest/Tri • Back/Bi • Legs/Stomach • Shoulders/Core (3 Days Off)",
        expectedHorizon = "10–12 Week Transformation",
        tag = "GREAT BALANCE"
    ),
    FIVE_DAYS(
        id = "FIVE_DAYS",
        daysCount = 5,
        title = "5 DAYS / WEEK",
        subtitle = "Best Muscle Combination Split",
        schedule = "Chest/Tri • Back/Bi • Legs/Stomach • Shoulders/Arms • Upper/Core",
        expectedHorizon = "8–10 Week Optimal Recomp",
        tag = "★ COACH BEST CHOICE"
    ),
    SIX_DAYS(
        id = "SIX_DAYS",
        daysCount = 6,
        title = "6 DAYS / WEEK",
        subtitle = "High-Volume Double Rotation",
        schedule = "Push • Pull • Legs • Shoulders/Chest • Back/Arms • Legs/Core",
        expectedHorizon = "Advanced Volume (Needs 7.5h+ Sleep)",
        tag = "ADVANCED VOLUME"
    );

    companion object {
        fun fromId(id: String?): WeeklyFrequency =
            entries.firstOrNull { it.id == id } ?: FOUR_DAYS
    }
}

enum class CurrentBfBracket(
    val id: String,
    val sex: BiologicalSex,
    val stageNumber: Int,
    val bfTag: String,
    val label: String,
    val shortTitle: String,
    val bodyFatPercent: Float,
    val code: String,
    val descriptor: String,
    val waistToShoulderRatio: Float,
    val visceralFatLoad: String,
    val directBiologyCue: String
) {
    // MALE 4 BIOLOGICAL STAGES
    BF_30_PLUS(
        id = "BF_30_PLUS",
        sex = BiologicalSex.MALE,
        stageNumber = 1,
        bfTag = "30%+ Body Fat",
        label = "High Visceral Adipose",
        shortTitle = "Stage 1: 30%+ Body Fat (High Visceral Adipose)",
        bodyFatPercent = 32.0f,
        code = "30%+",
        descriptor = "High Visceral Adipose",
        waistToShoulderRatio = 1.04f,
        visceralFatLoad = "HIGH (ELEVATED VISCERAL RESERVE)",
        directBiologyCue = "Priority: Mobilize deep visceral adipose tissue with a sustained 450 kcal deficit, 10,000 daily steps, and joint-safe resistance training to preserve lean mass."
    ),
    BF_24_28(
        id = "BF_24_28",
        sex = BiologicalSex.MALE,
        stageNumber = 2,
        bfTag = "22–26% Body Fat",
        label = "Protruding Abdomen / Soft Flanks",
        shortTitle = "Stage 2: 22–26% Body Fat (Protruding Abdomen / Soft Flanks)",
        bodyFatPercent = 24.0f,
        code = "22–26%",
        descriptor = "Protruding Abdomen / Soft Flanks",
        waistToShoulderRatio = 0.92f,
        visceralFatLoad = "MODERATE-HIGH (ABDOMINAL STORAGE)",
        directBiologyCue = "Priority: Strip abdominal fat reserve with 400 kcal deficit while lifting to prevent loose muscle mass."
    ),
    BF_18_22(
        id = "BF_18_22",
        sex = BiologicalSex.MALE,
        stageNumber = 3,
        bfTag = "18–20% Body Fat",
        label = "Skinny-Fat / Low Muscle Density",
        shortTitle = "Stage 3: 18–20% Body Fat (Skinny-Fat / Low Muscle Density)",
        bodyFatPercent = 19.0f,
        code = "18–20%",
        descriptor = "Skinny-Fat / Low Muscle Density",
        waistToShoulderRatio = 0.85f,
        visceralFatLoad = "MODERATE (LOW MYOFIBRILLAR DENSITY)",
        directBiologyCue = "Priority: Body recomposition. Lock in a strict 2.0g/kg protein floor and overload upper chest, lats, and lateral delts to replace soft midsection with dense muscle."
    ),
    BF_15(
        id = "BF_15",
        sex = BiologicalSex.MALE,
        stageNumber = 4,
        bfTag = "12–15% Body Fat",
        label = "Lean Athletic Base",
        shortTitle = "Stage 4: 12–15% Body Fat (Lean Athletic Base)",
        bodyFatPercent = 13.5f,
        code = "12–15%",
        descriptor = "Lean Athletic Base",
        waistToShoulderRatio = 0.75f,
        visceralFatLoad = "LOW (ATHLETIC BASELINE)",
        directBiologyCue = "Priority: Precision V-taper hypertrophy. Maintain high protein intake and progressive tension on clavicular pectorals and lateral deltoids."
    ),

    // FEMALE 4 BIOLOGICAL STAGES
    FEMALE_BF_32_PLUS(
        id = "FEMALE_BF_32_PLUS",
        sex = BiologicalSex.FEMALE,
        stageNumber = 1,
        bfTag = "30%+ Body Fat",
        label = "High Visceral Adipose",
        shortTitle = "Stage 1: 30%+ Body Fat (High Visceral Adipose)",
        bodyFatPercent = 33.0f,
        code = "30%+",
        descriptor = "High Visceral Adipose",
        waistToShoulderRatio = 0.96f,
        visceralFatLoad = "HIGH (ELEVATED ADIPOSE RESERVE)",
        directBiologyCue = "Priority: Establish a sustainable 400 kcal daily deficit with 10,000 steps and joint-safe resistance training to steadily reduce visceral and subcutaneous fat."
    ),
    FEMALE_BF_25_29(
        id = "FEMALE_BF_25_29",
        sex = BiologicalSex.FEMALE,
        stageNumber = 2,
        bfTag = "22–26% Body Fat",
        label = "Protruding Abdomen / Soft Flanks",
        shortTitle = "Stage 2: 22–26% Body Fat (Protruding Abdomen / Soft Flanks)",
        bodyFatPercent = 25.0f,
        code = "22–26%",
        descriptor = "Protruding Abdomen / Soft Flanks",
        waistToShoulderRatio = 0.84f,
        visceralFatLoad = "MODERATE (LOWER ABDOMEN & FLANK STORAGE)",
        directBiologyCue = "Priority: Strip abdominal fat reserve with 400 kcal deficit while lifting to prevent loose muscle mass."
    ),
    FEMALE_BF_20_24(
        id = "FEMALE_BF_20_24",
        sex = BiologicalSex.FEMALE,
        stageNumber = 3,
        bfTag = "18–20% Body Fat",
        label = "Skinny-Fat / Low Muscle Density",
        shortTitle = "Stage 3: 18–20% Body Fat (Skinny-Fat / Low Muscle Density)",
        bodyFatPercent = 20.0f,
        code = "18–20%",
        descriptor = "Skinny-Fat / Low Muscle Density",
        waistToShoulderRatio = 0.77f,
        visceralFatLoad = "LOW-MODERATE (LOW MUSCLE TONE)",
        directBiologyCue = "Priority: Body recomposition. Hit your daily protein floor and overload glutes, hamstrings, and shoulders to tighten waist-to-hip symmetry."
    ),
    FEMALE_BF_LEAN_BASE(
        id = "FEMALE_BF_LEAN_BASE",
        sex = BiologicalSex.FEMALE,
        stageNumber = 4,
        bfTag = "12–15% Body Fat",
        label = "Lean Athletic Base",
        shortTitle = "Stage 4: 12–15% Body Fat (Lean Athletic Base)",
        bodyFatPercent = 16.5f,
        code = "12–15%",
        descriptor = "Lean Athletic Base",
        waistToShoulderRatio = 0.70f,
        visceralFatLoad = "LOW (ATHLETIC BASELINE)",
        directBiologyCue = "Priority: Precision sculpting and muscular density. Fuel recovery with high protein intake while progressing glute, hamstring, and shoulder loads."
    );

    companion object {
        fun entriesForSex(sex: BiologicalSex): List<CurrentBfBracket> =
            entries.filter { it.sex == sex }.sortedBy { it.stageNumber }

        fun defaultForSex(sex: BiologicalSex): CurrentBfBracket =
            if (sex == BiologicalSex.FEMALE) FEMALE_BF_25_29 else BF_24_28

        fun fromId(id: String?, sex: BiologicalSex = BiologicalSex.MALE): CurrentBfBracket {
            val found = entries.firstOrNull { it.id == id }
            return if (found != null && found.sex == sex) {
                found
            } else {
                defaultForSex(sex)
            }
        }
    }
}

enum class GoalBfBracket(
    val id: String,
    val sex: BiologicalSex,
    val cardCode: String,
    val bfTag: String,
    val label: String,
    val shortTitle: String,
    val bodyFatPercent: Float,
    val code: String,
    val descriptor: String,
    val waistToShoulderRatio: Float
) {
    // MALE TARGET OBJECTIVES (CARDS A, B, C)
    GOAL_LEAN_FLAT(
        id = "GOAL_LEAN_FLAT",
        sex = BiologicalSex.MALE,
        cardCode = "CARD A",
        bfTag = "13–15% BF",
        label = "Lean & Flat",
        shortTitle = "Lean & Flat",
        bodyFatPercent = 14.0f,
        code = "13–15%",
        descriptor = "Focus: Belly fat reduction, tight waistline",
        waistToShoulderRatio = 0.74f
    ),
    GOAL_10_12(
        id = "GOAL_10_12",
        sex = BiologicalSex.MALE,
        cardCode = "CARD B",
        bfTag = "10–12% BF",
        label = "Athletic V-Taper",
        shortTitle = "Athletic V-Taper",
        bodyFatPercent = 11.0f,
        code = "10–12%",
        descriptor = "Focus: 10-12% body fat, 6-pack definition, capped shoulders",
        waistToShoulderRatio = 0.66f
    ),
    GOAL_MASS_SIZE(
        id = "GOAL_MASS_SIZE",
        sex = BiologicalSex.MALE,
        cardCode = "CARD C",
        bfTag = "15% BF",
        label = "Dense Mass",
        shortTitle = "Dense Mass",
        bodyFatPercent = 15.0f,
        code = "15%",
        descriptor = "Focus: Heavy chest, thick back, muscular density",
        waistToShoulderRatio = 0.72f
    ),

    // FEMALE TARGET OBJECTIVES (FEMININE SHAPE, LIFTED GLUTES & SLIM WAIST — ZERO BULK)
    FEMALE_GOAL_SIMPLE_FAT_LOSS(
        id = "FEMALE_GOAL_SIMPLE_FAT_LOSS",
        sex = BiologicalSex.FEMALE,
        cardCode = "CARD A",
        bfTag = "20–22% BF",
        label = "Slim & Flat Tummy",
        shortTitle = "Slim & Flat",
        bodyFatPercent = 21.0f,
        code = "20–22%",
        descriptor = "Focus: Waist & hip slimming, flat tummy, lighter frame",
        waistToShoulderRatio = 0.71f
    ),
    FEMALE_GOAL_TONED_LEAN(
        id = "FEMALE_GOAL_TONED_LEAN",
        sex = BiologicalSex.FEMALE,
        cardCode = "CARD B",
        bfTag = "18–20% BF",
        label = "Toned Waist & Glutes",
        shortTitle = "Toned & Sleek",
        bodyFatPercent = 19.0f,
        code = "18–20%",
        descriptor = "Focus: Flat waist, lifted glutes, sleek toned legs & posture",
        waistToShoulderRatio = 0.66f
    ),
    FEMALE_GOAL_HOURGLASS(
        id = "FEMALE_GOAL_HOURGLASS",
        sex = BiologicalSex.FEMALE,
        cardCode = "CARD C",
        bfTag = "19–21% BF",
        label = "Round Booty & Hourglass",
        shortTitle = "Booty & Curves",
        bodyFatPercent = 20.0f,
        code = "19–21%",
        descriptor = "Focus: Fuller lifted buttocks, curvy hips & cinched narrow waist",
        waistToShoulderRatio = 0.64f
    ),

    // Legacy compatibility alias
    GOAL_13_15(
        id = "GOAL_13_15",
        sex = BiologicalSex.MALE,
        cardCode = "CARD B",
        bfTag = "10–12% BF",
        label = "Athletic V-Taper",
        shortTitle = "Athletic V-Taper",
        bodyFatPercent = 11.0f,
        code = "10–12%",
        descriptor = "Focus: 10-12% body fat, 6-pack definition, capped shoulders",
        waistToShoulderRatio = 0.66f
    );

    companion object {
        val selectableEntries: List<GoalBfBracket> = listOf(
            GOAL_LEAN_FLAT,
            GOAL_10_12,
            GOAL_MASS_SIZE
        )

        fun entriesForSex(sex: BiologicalSex): List<GoalBfBracket> = when (sex) {
            BiologicalSex.MALE -> listOf(GOAL_LEAN_FLAT, GOAL_10_12, GOAL_MASS_SIZE)
            BiologicalSex.FEMALE -> listOf(
                FEMALE_GOAL_SIMPLE_FAT_LOSS,
                FEMALE_GOAL_TONED_LEAN,
                FEMALE_GOAL_HOURGLASS
            )
        }

        fun defaultForSex(sex: BiologicalSex): GoalBfBracket =
            if (sex == BiologicalSex.FEMALE) FEMALE_GOAL_TONED_LEAN else GOAL_10_12

        fun fromId(id: String?, sex: BiologicalSex = BiologicalSex.MALE): GoalBfBracket {
            if (id == "GOAL_13_15") return GOAL_10_12
            val found = entries.firstOrNull { it.id == id }
            return if (found != null && found.sex == sex) {
                found
            } else {
                defaultForSex(sex)
            }
        }
    }
}

enum class StructuralWeakpoint(
    val id: String,
    val title: String,
    val code: String,
    val anatomicalZone: String,
    val kineticPrescription: String,
    val targetAdaptation: String
) {
    UPPER_CLAVICULAR_CHEST(
        id = "UPPER_CLAVICULAR_CHEST",
        title = "Upper Chest (Clavicular Head)",
        code = "CHEST",
        anatomicalZone = "Upper Chest",
        kineticPrescription = "Incline Press with controlled stretch",
        targetAdaptation = "Builds upper chest fullness."
    ),
    LATERAL_DELTOID_WIDTH(
        id = "LATERAL_DELTOID_WIDTH",
        title = "Lateral Shoulders",
        code = "SHOULDERS",
        anatomicalZone = "Side Deltoids",
        kineticPrescription = "Cable Lateral Raises & Seated Shoulder Press",
        targetAdaptation = "Widens shoulder frame for a tapered waist."
    ),
    LAT_SWEEP(
        id = "LAT_SWEEP",
        title = "Lat Pulldown & Sweep",
        code = "BACK",
        anatomicalZone = "Upper Back & Lats",
        kineticPrescription = "Lat Pulldown and Seated Cable Row",
        targetAdaptation = "Creates a wider V-taper silhouette."
    );

    companion object {
        fun fromIds(ids: Set<String>): List<StructuralWeakpoint> {
            val matched = entries.filter { it.id in ids }
            return matched.ifEmpty { entries }
        }
    }
}

data class CombatExerciseSpec(
    val id: String,
    val orderIndex: Int,
    val name: String,
    val modalityTag: String,
    val targetMuscle: String,
    val primaryMuscle: String = targetMuscle,
    val secondaryMuscle: String = "Core & Stabilizers",
    val totalSets: Int = 4,
    val targetRepsCount: Int = 10,
    val repPrescription: String = "10 reps",
    val defaultLoadKg: Float = 20f,
    val executionCue: String = "",
    val formTeacherCues: List<String> = listOf(
        "Retract shoulder blades and brace core",
        "Control the eccentric phase for 2 seconds",
        "Pause briefly at peak contraction"
    ),
    val startPositionLabel: String = "Start: Full Stretch",
    val finishPositionLabel: String = "Finish: Peak Contraction"
) {
    val muscleFocusPill: String
        get() = "Primary: $primaryMuscle | Secondary: $secondaryMuscle"
}

data class CombatDaySpec(
    val dayIndex: Int,
    val id: String,
    val code: String,
    val title: String,
    val subtitle: String,
    val focusTag: String,
    val exercises: List<CombatExerciseSpec>
) {
    val heroSessionTitle: String
        get() = "$code: $title"
}

enum class DailyExerciseDose(
    val count: Int,
    val shortTitle: String,
    val durationTag: String,
    val description: String
) {
    QUICK_3(
        count = 3,
        shortTitle = "3 Exercises",
        durationTag = "~18 min",
        description = "Short & focused — best for busy schedules or low-motivation days"
    ),
    STANDARD_5(
        count = 5,
        shortTitle = "5 Exercises",
        durationTag = "~35 min",
        description = "Balanced full muscle session — recommended daily volume"
    ),
    COMPLETE_6(
        count = 6,
        shortTitle = "6 Exercises",
        durationTag = "~45 min",
        description = "Extra finisher exercise for maximum muscle & core volume"
    );

    companion object {
        fun fromCount(count: Int?): DailyExerciseDose = when (count) {
            3 -> QUICK_3
            6 -> COMPLETE_6
            else -> STANDARD_5
        }
    }
}

enum class WorkoutIntensityMode(
    val id: String,
    val label: String,
    val shortBadge: String,
    val exercisesCount: Int,
    val setDelta: Int,
    val repDelta: Int,
    val loadMultiplier: Float,
    val restSecondsDefault: Int,
    val estMinutes: Int,
    val baseKcalBurn: Int,
    val summaryLine: String
) {
    SIMPLE(
        id = "SIMPLE",
        label = "Simple",
        shortBadge = "Focused 3×3",
        exercisesCount = 3,
        setDelta = 0,
        repDelta = -1,
        loadMultiplier = 0.88f,
        restSecondsDefault = 60,
        estMinutes = 18,
        baseKcalBurn = 155,
        summaryLine = "3 essential exercises • 3 full sets each • ~18 min"
    ),
    MODERATE(
        id = "MODERATE",
        label = "Moderate",
        shortBadge = "Balanced Build",
        exercisesCount = 5,
        setDelta = 0,
        repDelta = 0,
        loadMultiplier = 1.00f,
        restSecondsDefault = 90,
        estMinutes = 34,
        baseKcalBurn = 275,
        summaryLine = "5 full exercises • Balanced sets & weights • ~34 min"
    ),
    EXTENSIVE(
        id = "EXTENSIVE",
        label = "Extensive",
        shortBadge = "High Volume",
        exercisesCount = 6,
        setDelta = 1,
        repDelta = 2,
        loadMultiplier = 1.08f,
        restSecondsDefault = 90,
        estMinutes = 48,
        baseKcalBurn = 410,
        summaryLine = "6 exercises + extra working set • Maximum burn • ~48 min"
    );

    companion object {
        fun fromId(raw: String?): WorkoutIntensityMode =
            entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: MODERATE

        fun fromExercisesCount(count: Int): WorkoutIntensityMode = when {
            count <= 3 -> SIMPLE
            count >= 6 -> EXTENSIVE
            else -> MODERATE
        }
    }
}

/**
 * 4-Way Workout Motivation Audio Mode:
 * Lets the user independently keep Music + Voice Coach active together, Music Only,
 * Voice Coach Only, or Mute All.
 */
enum class WorkoutAudioCoachMode(
    val id: String,
    val label: String,
    val shortLabel: String,
    val musicEnabled: Boolean,
    val voiceEnabled: Boolean,
    val description: String
) {
    BOTH_ACTIVE(
        id = "BOTH_ACTIVE",
        label = "Music + Voice",
        shortLabel = "Both On",
        musicEnabled = true,
        voiceEnabled = true,
        description = "Motivational music tempo + spoken rep counts & encouragement"
    ),
    MUSIC_ONLY(
        id = "MUSIC_ONLY",
        label = "Music Only",
        shortLabel = "Music",
        musicEnabled = true,
        voiceEnabled = false,
        description = "Energizing workout music on • Spoken voice coach muted"
    ),
    VOICE_ONLY(
        id = "VOICE_ONLY",
        label = "Voice Only",
        shortLabel = "Voice",
        musicEnabled = false,
        voiceEnabled = true,
        description = "Spoken rep counting, time alerts & motivation • Music muted"
    ),
    MUTED(
        id = "MUTED",
        label = "Mute All",
        shortLabel = "Muted",
        musicEnabled = false,
        voiceEnabled = false,
        description = "Silent mode — visual guided trainer & haptics only"
    );

    companion object {
        fun fromId(raw: String?): WorkoutAudioCoachMode =
            entries.firstOrNull { it.id.equals(raw, ignoreCase = true) } ?: BOTH_ACTIVE
    }
}

enum class StartingFrameCategory(
    val shortLabel: String
) {
    SLIM_UNDERWEIGHT("Slim / Lean Frame"),
    AVERAGE_RECOMP("Balanced / Soft-Mid Frame"),
    HEAVY_OBESE("Overweight / Heavy Frame");

    companion object {
        fun resolve(
            startingShapeId: String?,
            currentBracket: CurrentBfBracket?
        ): StartingFrameCategory {
            val id = startingShapeId?.trim()?.lowercase().orEmpty()
            return when {
                "shape_5" in id || "underweight" in id -> SLIM_UNDERWEIGHT
                "shape_1" in id || "shape_2" in id || "shape_7" in id ||
                    "high_bf" in id || "soft_midsection" in id || "short_stocky" in id -> HEAVY_OBESE
                "shape_3" in id || "shape_4" in id ||
                    "skinny_fat" in id || "average_untrained" in id -> AVERAGE_RECOMP
                currentBracket == CurrentBfBracket.BF_15 ||
                    currentBracket == CurrentBfBracket.FEMALE_BF_LEAN_BASE -> SLIM_UNDERWEIGHT
                currentBracket == CurrentBfBracket.BF_30_PLUS ||
                    currentBracket == CurrentBfBracket.BF_24_28 ||
                    currentBracket == CurrentBfBracket.FEMALE_BF_32_PLUS ||
                    currentBracket == CurrentBfBracket.FEMALE_BF_25_29 -> HEAVY_OBESE
                else -> AVERAGE_RECOMP
            }
        }
    }
}

enum class GoalTrainingArchetype(
    val id: String
) {
    FAT_LOSS_SLIM("fat_loss_slim"),
    ATHLETIC_SLEEK_TONE("athletic_sleek_tone"),
    PROPORTION_SCULPT("proportion_sculpt"),
    CURVY_OR_MASS_BUILD("curvy_or_mass_build");

    fun displayTitle(sex: BiologicalSex): String = when (this) {
        FAT_LOSS_SLIM -> if (sex == BiologicalSex.FEMALE) "Slim Down & Flat Tummy" else "Slim Down & Flat Stomach"
        ATHLETIC_SLEEK_TONE -> if (sex == BiologicalSex.FEMALE) "Toned Waist & Sleek Shape" else "Lean Six-Pack & Stamina"
        PROPORTION_SCULPT -> if (sex == BiologicalSex.FEMALE) "Lifted Booty & Slim Waist" else "Broad Shoulders & V-Shape"
        CURVY_OR_MASS_BUILD -> if (sex == BiologicalSex.FEMALE) "Curvy Hourglass & Full Booty" else "Thick Size & Heavy Strength"
    }

    companion object {
        fun resolve(
            goalCardId: String?,
            goalBracket: GoalBfBracket?,
            startingFrame: StartingFrameCategory
        ): GoalTrainingArchetype {
            val id = goalCardId?.trim()?.lowercase().orEmpty()
            return when {
                "goal_1" in id || "fat_loss" in id -> FAT_LOSS_SLIM
                "goal_3" in id || "athletic" in id -> ATHLETIC_SLEEK_TONE
                "goal_2" in id || "vtaper" in id -> PROPORTION_SCULPT
                "goal_4" in id || "power_mass" in id -> CURVY_OR_MASS_BUILD
                startingFrame == StartingFrameCategory.SLIM_UNDERWEIGHT &&
                    (goalBracket == GoalBfBracket.GOAL_MASS_SIZE || goalBracket == GoalBfBracket.FEMALE_GOAL_HOURGLASS) ->
                    CURVY_OR_MASS_BUILD
                goalBracket == GoalBfBracket.GOAL_LEAN_FLAT ||
                    goalBracket == GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS -> FAT_LOSS_SLIM
                goalBracket == GoalBfBracket.FEMALE_GOAL_TONED_LEAN -> ATHLETIC_SLEEK_TONE
                goalBracket == GoalBfBracket.GOAL_MASS_SIZE -> CURVY_OR_MASS_BUILD
                goalBracket == GoalBfBracket.FEMALE_GOAL_HOURGLASS ->
                    if (startingFrame == StartingFrameCategory.SLIM_UNDERWEIGHT) CURVY_OR_MASS_BUILD else PROPORTION_SCULPT
                startingFrame == StartingFrameCategory.SLIM_UNDERWEIGHT -> CURVY_OR_MASS_BUILD
                startingFrame == StartingFrameCategory.HEAVY_OBESE -> FAT_LOSS_SLIM
                else -> PROPORTION_SCULPT
            }
        }
    }
}

data class PersonalTrainingBlueprint(
    val sex: BiologicalSex,
    val startingFrame: StartingFrameCategory,
    val archetype: GoalTrainingArchetype,
    val headlineBadge: String,
    val coachingSummary: String,
    val repSchemeBadge: String
)

object CombatSplitCatalog {

    val FOUR_DAY_SPLIT: List<CombatDaySpec> = buildMaleFourDaySplit(
        isJointSafe = true,
        startingFrame = StartingFrameCategory.AVERAGE_RECOMP,
        archetype = GoalTrainingArchetype.PROPORTION_SCULPT
    )

    fun describeActiveBlueprint(
        sex: BiologicalSex,
        startingShapeId: String? = null,
        goalCardId: String? = null,
        currentBracket: CurrentBfBracket? = null,
        goalBracket: GoalBfBracket? = null,
        environment: com.example.data.local.TrainingEnvironment = com.example.data.local.TrainingEnvironment.GYM
    ): PersonalTrainingBlueprint {
        val frame = StartingFrameCategory.resolve(startingShapeId, currentBracket)
        val archetype = GoalTrainingArchetype.resolve(goalCardId, goalBracket, frame)
        val sexLabel = if (sex == BiologicalSex.FEMALE) "Female" else "Male"
        val envLabel = if (environment == com.example.data.local.TrainingEnvironment.GYM) "Gym Stations" else "Home Bodyweight"
        val headline = "$sexLabel • ${frame.shortLabel} → ${archetype.displayTitle(sex)} ($envLabel)"

        val repScheme = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> "3 Sets × 15 Reps • Metabolic Calorie Burn"
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> "3 Sets × 12–15 Reps • Sleek Definition"
            GoalTrainingArchetype.PROPORTION_SCULPT -> "3–4 Sets × 10–12 Reps • Shape & Proportion"
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> if (sex == BiologicalSex.FEMALE) {
                "4 Sets × 10–12 Reps • Progressive Curve Hypertrophy"
            } else {
                "4 Sets × 8–10 Reps • Progressive Mass & Strength"
            }
        }

        val summary = when (sex) {
            BiologicalSex.FEMALE -> when (archetype) {
                GoalTrainingArchetype.FAT_LOSS_SLIM -> when (frame) {
                    StartingFrameCategory.HEAVY_OBESE ->
                        "Joint-protected sleds, seated cables, and standing core tension keep heart rate burning fat safely without knee impact."
                    StartingFrameCategory.SLIM_UNDERWEIGHT ->
                        "Gentle toning tension firms the waist and lifts posture without burning away healthy feminine curves."
                    StartingFrameCategory.AVERAGE_RECOMP ->
                        "High-output lower-body & posture stations maximize calorie burn while cinching the waistline."
                }
                GoalTrainingArchetype.ATHLETIC_SLEEK_TONE ->
                    "Balanced glute-hamstring lines, sleek shoulder posture, and weighted core control without bulky upper-body mass."
                GoalTrainingArchetype.PROPORTION_SCULPT ->
                    "Upper-glute shelf builders + back-and-shoulder posture create a lifted booty and visibly narrower waist ratio."
                GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> when (frame) {
                    StartingFrameCategory.SLIM_UNDERWEIGHT ->
                        "Built for slim frames adding feminine curves: progressive hip thrusts, abductors, RDLs, and zero calorie-wasting cardio circuits."
                    StartingFrameCategory.HEAVY_OBESE ->
                        "Guided glute & back stations reshape hips and posture cleanly while protecting knees and lower back."
                    StartingFrameCategory.AVERAGE_RECOMP ->
                        "4-set progressive glute, thigh, and hourglass upper-posture programming for fuller lower-body curves."
                }
            }
            BiologicalSex.MALE -> when (archetype) {
                GoalTrainingArchetype.FAT_LOSS_SLIM -> when (frame) {
                    StartingFrameCategory.HEAVY_OBESE ->
                        "Guided chest-supported rows, sled leg presses, and cable core stations torch calories while protecting knees and spine."
                    StartingFrameCategory.SLIM_UNDERWEIGHT ->
                        "Clean compound tension builds lean chest, shoulder, and abs definition without wasting muscle."
                    StartingFrameCategory.AVERAGE_RECOMP ->
                        "15-rep compound gym stations keep metabolic output high while preserving chest, back, and shoulder muscle."
                }
                GoalTrainingArchetype.ATHLETIC_SLEEK_TONE ->
                    "Athletic upper-lower balance with crisp 12–15 rep tension and weighted cable core definition."
                GoalTrainingArchetype.PROPORTION_SCULPT ->
                    "Prioritizes side-shoulder width, upper-chest shelf, and wide lats for an unmistakable V-taper waist ratio."
                GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> when (frame) {
                    StartingFrameCategory.SLIM_UNDERWEIGHT ->
                        "Hardgainer mass blueprint: 4x8–10 heavy compounds for thick chest, back, arms, and legs—zero filler cardio."
                    StartingFrameCategory.HEAVY_OBESE ->
                        "Heavy guided machines and supported rows channel body mass into dense chest, back, shoulder, and leg strength."
                    StartingFrameCategory.AVERAGE_RECOMP ->
                        "4-set progressive overload across primary presses, rows, and leg compounds for dense size and strength."
                }
            }
        }

        return PersonalTrainingBlueprint(
            sex = sex,
            startingFrame = frame,
            archetype = archetype,
            headlineBadge = headline,
            coachingSummary = summary,
            repSchemeBadge = repScheme
        )
    }

    fun getSplitForProfile(
        sex: BiologicalSex,
        ageTier: AgeTier,
        experience: ExperienceLevel,
        frequency: WeeklyFrequency = WeeklyFrequency.FOUR_DAYS,
        rotationWeek: Int = 0,
        exercisesPerDay: Int = 5,
        startingShapeId: String? = null,
        goalCardId: String? = null,
        currentBracket: CurrentBfBracket? = null,
        goalBracket: GoalBfBracket? = null
    ): List<CombatDaySpec> {
        val startingFrame = StartingFrameCategory.resolve(startingShapeId, currentBracket)
        val archetype = GoalTrainingArchetype.resolve(goalCardId, goalBracket, startingFrame)
        val isJointSafe = ageTier == AgeTier.AGE_45_PLUS ||
            experience.isBeginnerSafe ||
            startingFrame == StartingFrameCategory.HEAVY_OBESE
        val normalizedRotation = ((rotationWeek % 2) + 2) % 2

        val baseSplit = when (sex) {
            BiologicalSex.MALE -> buildMaleSplitForFrequency(frequency, isJointSafe, startingFrame, archetype)
            BiologicalSex.FEMALE -> buildFemaleSplitForFrequency(frequency, isJointSafe, startingFrame, archetype)
        }
        val rotated = if (normalizedRotation == 0) {
            baseSplit
        } else {
            baseSplit.map { day -> rotateDayForWeekB(day, sex, isJointSafe, startingFrame, archetype) }
        }
        val safeDose = DailyExerciseDose.fromCount(exercisesPerDay).count
        return rotated.map { day -> adjustDayExerciseVolume(day, safeDose) }
    }

    private fun adjustDayExerciseVolume(day: CombatDaySpec, targetCount: Int): CombatDaySpec {
        return when {
            targetCount <= 3 -> day.copy(
                exercises = day.exercises.take(3).mapIndexed { idx, ex -> ex.copy(orderIndex = idx + 1) }
            )
            targetCount >= 6 && day.exercises.size < 6 -> {
                val hasPallof = day.exercises.any { "pallof" in it.name.lowercase() }
                val sixthSpec = if (hasPallof) {
                    CombatExerciseSpec(
                        id = "${day.id.lowercase()}_ex6_bar_hang",
                        orderIndex = day.exercises.size + 1,
                        name = "Active Bar Hang & Grip Finisher",
                        modalityTag = "Pull-Up Station Decompression",
                        targetMuscle = "Forearms & Spine Decompression",
                        primaryMuscle = "Forearms & Lats",
                        secondaryMuscle = "Shoulders & Core",
                        totalSets = 3,
                        targetRepsCount = 35,
                        repPrescription = "00:35",
                        defaultLoadKg = 0f,
                        executionCue = "Hold the pull-up bar with active shoulders and steady breathing for 35 seconds."
                    )
                } else {
                    CombatExerciseSpec(
                        id = "${day.id.lowercase()}_ex6_pallof_finisher",
                        orderIndex = day.exercises.size + 1,
                        name = "Standing Cable Pallof Core Finisher",
                        modalityTag = "Cable Anti-Rotation Core",
                        targetMuscle = "Deep Waist & Obliques",
                        primaryMuscle = "Deep Core & Waist",
                        secondaryMuscle = "Obliques",
                        totalSets = 3,
                        targetRepsCount = 40,
                        repPrescription = "00:40",
                        defaultLoadKg = 10f,
                        executionCue = "Press cable handle straight out from chest and resist rotation for 40 seconds."
                    )
                }
                day.copy(exercises = day.exercises + sixthSpec)
            }
            else -> day
        }
    }

    // =========================================================================
    // REUSABLE PURE-GYM EXERCISE BUILDERS (100% GYM STATIONS — ZERO HOME FLOOR)
    // =========================================================================

    private fun exInclinePress(
        id: String,
        order: Int,
        sets: Int,
        reps: Int,
        useMachine: Boolean
    ) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (useMachine) "Incline Machine Chest Press" else "30° Incline Dumbbell Press",
        modalityTag = if (useMachine) "Guided Incline Station" else "30° Bench • Dumbbells",
        targetMuscle = "Upper Chest",
        primaryMuscle = "Upper Chest",
        secondaryMuscle = "Front Shoulders & Triceps",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = if (useMachine) 35f else 22f,
        executionCue = "Set 30° angle, tuck elbows 45°, and press smoothly to squeeze upper chest.",
        formTeacherCues = listOf(
            "Pinch shoulder blades firmly against the 30° backrest.",
            "Lower over 2 seconds with elbows angled 45° to protect shoulder cuffs.",
            "Press smoothly overhead and squeeze upper chest for 1 second."
        ),
        startPositionLabel = "Hands at upper-chest stretch",
        finishPositionLabel = "Full collarbone press"
    )

    private fun exMidChestPress(
        id: String,
        order: Int,
        sets: Int,
        reps: Int,
        useMachine: Boolean
    ) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (useMachine) "Seated Machine Chest Press" else "Flat Dumbbell Bench Press",
        modalityTag = if (useMachine) "Guided Chest Machine" else "Flat Bench • Dumbbells",
        targetMuscle = "Mid Chest",
        primaryMuscle = "Mid Chest",
        secondaryMuscle = "Triceps & Front Delts",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = if (useMachine) 42f else 24f,
        executionCue = "Plant feet firmly, keep chest proud, and press handles in a controlled arc.",
        formTeacherCues = listOf(
            "Keep shoulder blades retracted into the pad throughout every rep.",
            "Lower under 2-second control until you feel a clean chest stretch.",
            "Press smoothly and pause 1 second at full chest contraction."
        ),
        startPositionLabel = "Mid-chest stretch",
        finishPositionLabel = "Full chest squeeze"
    )

    private fun exPecDeckFly(id: String, order: Int, sets: Int, reps: Int) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Pec Deck Inner-Chest Fly",
        modalityTag = "Seated Fly Machine",
        targetMuscle = "Inner & Mid Chest",
        primaryMuscle = "Chest",
        secondaryMuscle = "Front Shoulders",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = 30f,
        executionCue = "Keep a soft bend in your elbows and bring handles together with a 1s chest squeeze."
    )

    private fun exTricepRopePushdown(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 20f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Cable Tricep Rope Pushdown",
        modalityTag = "High Cable Pulley",
        targetMuscle = "Triceps",
        primaryMuscle = "Triceps",
        secondaryMuscle = "Forearm Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Pin elbows tightly against your ribs and spread the rope apart at lockout."
    )

    private fun exOverheadCableTricep(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 18f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Overhead Cable Tricep Extension",
        modalityTag = "Cable Rope / V-Bar",
        targetMuscle = "Long-Head Triceps",
        primaryMuscle = "Triceps",
        secondaryMuscle = "Core Brace",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep elbows pointing forward and extend smoothly to stretch and build the full tricep."
    )

    private fun exAssistedOrBenchDips(id: String, order: Int, sets: Int, reps: Int, isJointSafe: Boolean) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (isJointSafe) "Assisted Dip Machine" else "Parallel-Bar / Bench Chest Dips",
        modalityTag = if (isJointSafe) "Counterweight Dip Station" else "Gym Dip Station",
        targetMuscle = "Lower Chest & Triceps",
        primaryMuscle = "Triceps & Lower Chest",
        secondaryMuscle = "Front Shoulders",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = 0f,
        executionCue = "Keep shoulders down away from ears, lower to 90° elbows, and press tall."
    )

    private fun exLatPulldown(
        id: String,
        order: Int,
        sets: Int,
        reps: Int,
        neutralGrip: Boolean = false,
        loadKg: Float = 45f
    ) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (neutralGrip) "Neutral-Grip Lat Pulldown" else "Wide-Grip Lat Pulldown",
        modalityTag = "Cable Pulldown Station",
        targetMuscle = "Lats (Upper-Back Taper)",
        primaryMuscle = "Lats",
        secondaryMuscle = "Biceps & Mid-Back",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Lock thighs under pad and drive elbows straight down toward your hips."
    )

    private fun exChestSupportedRow(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 40f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Chest-Supported Machine Row",
        modalityTag = "Joint-Safe Chest Pad",
        targetMuscle = "Mid-Back Thickness",
        primaryMuscle = "Mid-Back",
        secondaryMuscle = "Rear Delts & Biceps",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep chest braced against pad, pull elbows past ribs, and pinch shoulder blades 1s."
    )

    private fun exSeatedCableRow(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 42f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Seated Cable Row",
        modalityTag = "Low Pulley V-Handle",
        targetMuscle = "Mid-Back & Posture",
        primaryMuscle = "Mid-Back & Lats",
        secondaryMuscle = "Biceps",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Sit tall with chest proud and pull handle cleanly into lower ribs."
    )

    private fun exFacePulls(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 16f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Cable Rear-Delt Face Pulls",
        modalityTag = "Eye-Level Rope Pulley",
        targetMuscle = "Rear Shoulders & Posture",
        primaryMuscle = "Rear Delts & Upper Back",
        secondaryMuscle = "Rotator Cuff",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Pull rope apart toward temples with thumbs back and pause 1 second."
    )

    private fun exShoulderPress(
        id: String,
        order: Int,
        sets: Int,
        reps: Int,
        useMachine: Boolean,
        loadKg: Float = 16f
    ) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (useMachine) "Seated Machine Shoulder Press" else "Seated Dumbbell Shoulder Press",
        modalityTag = if (useMachine) "Guided Overhead Station" else "75° Bench • Dumbbells",
        targetMuscle = "Shoulders",
        primaryMuscle = "Shoulders",
        secondaryMuscle = "Triceps",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep back supported against pad and press smoothly overhead without arching."
    )

    private fun exCableLateralRaise(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 8f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Cable Lateral Raise",
        modalityTag = "Constant-Tension Pulley",
        targetMuscle = "Side Shoulders (Taper)",
        primaryMuscle = "Side Shoulders",
        secondaryMuscle = "Upper Back",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Sweep arm out to shoulder height with relaxed traps and lower over 2 seconds."
    )

    private fun exInclineDbCurl(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 10f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Incline Dumbbell Curl",
        modalityTag = "45° Bench • Full Stretch",
        targetMuscle = "Biceps Peak",
        primaryMuscle = "Biceps",
        secondaryMuscle = "Forearms",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep elbows pinned behind torso on the bench and curl smoothly."
    )

    private fun exHammerCurl(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 12f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Dumbbell Hammer Curl",
        modalityTag = "Neutral-Grip Dumbbells",
        targetMuscle = "Biceps & Forearms",
        primaryMuscle = "Biceps & Forearms",
        secondaryMuscle = "Grip",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep palms facing inward and curl smoothly with stationary elbows."
    )

    private fun exPreacherOrCableCurl(id: String, order: Int, sets: Int, reps: Int, usePreacher: Boolean, loadKg: Float = 20f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (usePreacher) "Machine Preacher Curl" else "Cable EZ-Bar Bicep Curl",
        modalityTag = if (usePreacher) "Padded Preacher Station" else "Low Cable Pulley",
        targetMuscle = "Biceps",
        primaryMuscle = "Biceps",
        secondaryMuscle = "Forearms",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Squeeze biceps 1 full second at the top and lower under strict 2s control."
    )

    private fun exSledLegPress(id: String, order: Int, sets: Int, reps: Int, gluteBiased: Boolean = false, loadKg: Float = 95f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (gluteBiased) "High-Foot 45° Sled Leg Press" else "45° Sled Leg Press",
        modalityTag = "Guided Sled Machine",
        targetMuscle = if (gluteBiased) "Glutes & Thighs" else "Quads & Glutes",
        primaryMuscle = if (gluteBiased) "Glutes & Quads" else "Quadriceps",
        secondaryMuscle = "Glutes & Hamstrings",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Keep lower back pressed flat into pad, lower to 90°, and drive through heels."
    )

    private fun exGobletSquat(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 22f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Dumbbell Goblet Squat",
        modalityTag = "Dumbbell • Full Depth",
        targetMuscle = "Quads & Glutes",
        primaryMuscle = "Quads & Glutes",
        secondaryMuscle = "Core Brace",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Hold dumbbell close to chest, sit hips down smoothly, and drive tall."
    )

    private fun exHipThrust(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 55f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Padded Bench / Machine Hip Thrust",
        modalityTag = "Glute Thrust Station",
        targetMuscle = "Glutes (Lift & Curve)",
        primaryMuscle = "Glutes",
        secondaryMuscle = "Hamstrings & Core",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Drive through heels, tuck chin slightly, and lock full glute squeeze for 2 seconds."
    )

    private fun exDbGluteBridge(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 26f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Dumbbell Glute Bridge",
        modalityTag = "Weighted Hip Extension",
        targetMuscle = "Glute Shelf",
        primaryMuscle = "Glutes",
        secondaryMuscle = "Hamstrings",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Brace heavy dumbbell across hips, drive heels down, and hold top squeeze 2s."
    )

    private fun exRomanianDeadlift(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 22f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Dumbbell Romanian Deadlift",
        modalityTag = "Dumbbells • Hip Hinge",
        targetMuscle = "Hamstrings & Glute Tie-In",
        primaryMuscle = "Hamstrings & Glutes",
        secondaryMuscle = "Lower-Back Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Push hips straight back with flat spine until hamstrings stretch, then drive hips through."
    )

    private fun exSeatedLegCurl(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 36f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Seated Hamstring Leg Curl",
        modalityTag = "Pin-Loaded Machine",
        targetMuscle = "Hamstrings",
        primaryMuscle = "Hamstrings",
        secondaryMuscle = "Knee Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Pull heels smoothly under seat and control the return over 3 full seconds."
    )

    private fun exLegExtension(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 35f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Seated Leg Extension",
        modalityTag = "Pin-Loaded Machine",
        targetMuscle = "Quad Definition",
        primaryMuscle = "Quadriceps",
        secondaryMuscle = "Knee Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Extend smoothly to full quad lock and hold 1 second at the top."
    )

    private fun exHipAbductor(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 42f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Seated Hip Abductor Machine",
        modalityTag = "Pin-Loaded Glute Machine",
        targetMuscle = "Upper & Outer Glutes",
        primaryMuscle = "Upper Glutes",
        secondaryMuscle = "Outer Hip",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Press knees outward smoothly and hold outer-glute contraction 1 second."
    )

    private fun exCableKickback(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 14f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Cable Glute Kickbacks",
        modalityTag = "Low Pulley Ankle Cuff",
        targetMuscle = "Upper Glute Shelf",
        primaryMuscle = "Glutes",
        secondaryMuscle = "Hamstrings",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Brace torso on cable tower, kick heel back & slightly out, and pause 1s."
    )

    private fun exSplitSquat(id: String, order: Int, sets: Int, reps: Int, isSupported: Boolean, loadKg: Float = 12f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = if (isSupported) "Supported Dumbbell Split Squat" else "Bulgarian Dumbbell Split Squat",
        modalityTag = if (isSupported) "Rack-Balanced Dumbbell" else "Bench-Elevated Dumbbell",
        targetMuscle = "Glutes & Thighs",
        primaryMuscle = "Glutes & Quads",
        secondaryMuscle = "Hip Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Lean torso slightly forward over front leg and press smoothly through front heel."
    )

    private fun exCalfRaise(id: String, order: Int, sets: Int, reps: Int, loadKg: Float = 50f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Standing Machine Calf Raise",
        modalityTag = "Calf Station / Platform",
        targetMuscle = "Calves",
        primaryMuscle = "Calves",
        secondaryMuscle = "Ankle Stabilizers",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Pause 2 seconds in deep heel stretch, then press high onto toes."
    )

    private fun exCaptainsChairKneeRaise(id: String, order: Int, sets: Int, reps: Int = 15) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Captain's Chair Knee Raise",
        modalityTag = "Back-Supported Core Tower",
        targetMuscle = "Lower Abs & Flat Stomach",
        primaryMuscle = "Lower Abs",
        secondaryMuscle = "Obliques",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = 0f,
        executionCue = "Brace lower back against pad, curl pelvis upward, and lower over 2 seconds."
    )

    private fun exKneelingCableCrunch(id: String, order: Int, sets: Int, reps: Int = 15, loadKg: Float = 24f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Kneeling Cable Rope Crunch",
        modalityTag = "Weighted Cable Abs",
        targetMuscle = "Six-Pack & Flat Waist",
        primaryMuscle = "Upper & Lower Abs",
        secondaryMuscle = "Obliques",
        totalSets = sets,
        targetRepsCount = reps,
        repPrescription = "$reps reps",
        defaultLoadKg = loadKg,
        executionCue = "Exhale all air as you curl ribcage down toward pelvis—hips stay stationary."
    )

    private fun exCablePallofHold(id: String, order: Int, sets: Int, seconds: Int = 40, loadKg: Float = 10f) = CombatExerciseSpec(
        id = id,
        orderIndex = order,
        name = "Standing Cable Pallof Core Hold",
        modalityTag = "Cable Waist Cincher",
        targetMuscle = "Deep Core & Waistline",
        primaryMuscle = "Deep Core & Obliques",
        secondaryMuscle = "Glutes & Posture",
        totalSets = sets,
        targetRepsCount = seconds,
        repPrescription = "00:$seconds",
        defaultLoadKg = loadKg,
        executionCue = "Stand sideways to cable pulley, press handle straight in front of sternum, and brace tight."
    )

    // =========================================================================
    // MALE PHYSIOLOGICAL SPLITS (TAILORED BY GOAL ARCHETYPE & STARTING FRAME)
    // =========================================================================

    private fun buildMaleFourDaySplit(
        isJointSafe: Boolean,
        startingFrame: StartingFrameCategory,
        archetype: GoalTrainingArchetype
    ): List<CombatDaySpec> {
        val isHeavyFrame = startingFrame == StartingFrameCategory.HEAVY_OBESE
        val isSlimFrame = startingFrame == StartingFrameCategory.SLIM_UNDERWEIGHT
        val useGuidedPresses = isJointSafe || isHeavyFrame

        val primarySets = when (archetype) {
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> 4
            GoalTrainingArchetype.PROPORTION_SCULPT -> 4
            else -> 3
        }
        val compoundReps = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> if (isSlimFrame) 12 else 15
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> 12
            GoalTrainingArchetype.PROPORTION_SCULPT -> 10
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> if (isHeavyFrame) 10 else 8
        }
        val accessoryReps = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> 15
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> 15
            GoalTrainingArchetype.PROPORTION_SCULPT -> 12
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> 10
        }

        val focusTag = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM ->
                if (isHeavyFrame) "Joint-Safe Metabolic Gym Stations" else "High-Output Calorie & Muscle Retention"
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> "Lean Athletic Tension & Six-Pack Core"
            GoalTrainingArchetype.PROPORTION_SCULPT -> "Upper-Chest, Wide-Lat & Side-Delt V-Taper"
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD ->
                if (isSlimFrame) "Hardgainer Compound Mass & Size" else "Heavy Progressive Strength & Thickness"
        }

        // Day 1: Chest & Triceps (1. Mid-Chest Compound -> 2. Upper-Chest Angle -> 3. Inner Sculpt -> 4. Tricep -> 5. Finisher)
        val day1Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exMidChestPress("d1_m_fl_chest", 1, 3, compoundReps, useMachine = true),
                exInclinePress("d1_m_fl_inc", 2, 3, compoundReps, useMachine = useGuidedPresses),
                exPecDeckFly("d1_m_fl_fly", 3, 3, 15),
                exTricepRopePushdown("d1_m_fl_pushdown", 4, 3, 15, 20f),
                exCaptainsChairKneeRaise("d1_m_fl_knee_raise", 5, 3, 15)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exInclinePress("d1_m_ath_inc", 1, 3, 12, useMachine = useGuidedPresses),
                exPecDeckFly("d1_m_ath_fly", 2, 3, 15),
                exTricepRopePushdown("d1_m_ath_pushdown", 3, 3, 15, 20f),
                exAssistedOrBenchDips("d1_m_ath_dips", 4, 3, 12, isJointSafe = useGuidedPresses),
                exKneelingCableCrunch("d1_m_ath_crunch", 5, 3, 15, 25f)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exInclinePress("d1_m_vt_inc", 1, 4, 10, useMachine = isHeavyFrame),
                exMidChestPress("d1_m_vt_flat", 2, 3, 10, useMachine = useGuidedPresses),
                exPecDeckFly("d1_m_vt_fly", 3, 3, 12),
                exTricepRopePushdown("d1_m_vt_pushdown", 4, 3, 12, 22f),
                if (isSlimFrame) exOverheadCableTricep("d1_m_vt_ohtri", 5, 3, 12, 18f)
                else exCaptainsChairKneeRaise("d1_m_vt_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exMidChestPress("d1_m_mass_flat", 1, 4, compoundReps, useMachine = isHeavyFrame),
                exInclinePress("d1_m_mass_inc", 2, 4, compoundReps, useMachine = isHeavyFrame),
                exPecDeckFly("d1_m_mass_fly", 3, 3, 10),
                exOverheadCableTricep("d1_m_mass_ohtri", 4, 3, 10, 22f),
                if (isHeavyFrame) exCablePallofHold("d1_m_mass_pallof", 5, 3, 40, 12f)
                else exTricepRopePushdown("d1_m_mass_pushdown", 5, 3, 10, 24f)
            )
        }

        // Day 2: Back & Biceps (adapted by Goal & Frame)
        val day2Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exLatPulldown("d2_m_fl_pull", 1, 3, compoundReps, neutralGrip = false, loadKg = 48f),
                exChestSupportedRow("d2_m_fl_csrow", 2, 3, compoundReps, 42f),
                exFacePulls("d2_m_fl_face", 3, 3, 15, 16f),
                exHammerCurl("d2_m_fl_hammer", 4, 3, 15, 12f),
                exKneelingCableCrunch("d2_m_fl_crunch", 5, 3, 15, 25f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exLatPulldown("d2_m_ath_pull", 1, 3, 12, neutralGrip = true, loadKg = 48f),
                exSeatedCableRow("d2_m_ath_row", 2, 3, 12, 46f),
                exFacePulls("d2_m_ath_face", 3, 3, 15, 18f),
                exInclineDbCurl("d2_m_ath_inccurl", 4, 3, 12, 10f),
                exCaptainsChairKneeRaise("d2_m_ath_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exLatPulldown("d2_m_vt_pull", 1, 4, 10, neutralGrip = false, loadKg = 52f),
                exChestSupportedRow("d2_m_vt_csrow", 2, 3, 10, 48f),
                exSeatedCableRow("d2_m_vt_row", 3, 3, 12, 50f),
                exInclineDbCurl("d2_m_vt_inccurl", 4, 3, 12, 12f),
                exPreacherOrCableCurl("d2_m_vt_curl", 5, 3, 12, usePreacher = false, loadKg = 22f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exChestSupportedRow("d2_m_mass_csrow", 1, 4, compoundReps, 52f),
                exLatPulldown("d2_m_mass_pull", 2, 4, compoundReps, neutralGrip = true, loadKg = 55f),
                exSeatedCableRow("d2_m_mass_row", 3, 3, 10, 52f),
                exHammerCurl("d2_m_mass_hammer", 4, 3, 10, 14f),
                exPreacherOrCableCurl("d2_m_mass_preacher", 5, 3, 10, usePreacher = true, loadKg = 24f)
            )
        }

        // Day 3: Legs & Core (adapted by Goal & Frame — never pairs Sled Leg Press + Goblet Squat)
        val day3Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exSledLegPress("d3_m_fl_press", 1, 3, compoundReps, gluteBiased = false, loadKg = 100f),
                exSeatedLegCurl("d3_m_fl_curl", 2, 3, 15, 40f),
                exLegExtension("d3_m_fl_ext", 3, 3, 15, 38f),
                exCalfRaise("d3_m_fl_calf", 4, 3, 15, 50f),
                exCablePallofHold("d3_m_fl_pallof", 5, 3, 45, 12f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                if (isHeavyFrame) exSledLegPress("d3_m_ath_press", 1, 3, 12, loadKg = 105f)
                else exGobletSquat("d3_m_ath_goblet", 1, 3, 12, 24f),
                exRomanianDeadlift("d3_m_ath_rdl", 2, 3, 12, 24f),
                exSplitSquat("d3_m_ath_split", 3, 3, 12, isSupported = useGuidedPresses, loadKg = 14f),
                exCalfRaise("d3_m_ath_calf", 4, 3, 15, 50f),
                exCaptainsChairKneeRaise("d3_m_ath_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exSledLegPress("d3_m_vt_press", 1, 4, 12, gluteBiased = false, loadKg = 110f),
                if (useGuidedPresses) exSeatedLegCurl("d3_m_vt_curl", 2, 3, 12, 42f)
                else exRomanianDeadlift("d3_m_vt_rdl", 2, 3, 10, 26f),
                exLegExtension("d3_m_vt_ext", 3, 3, 12, 42f),
                exCalfRaise("d3_m_vt_calf", 4, 3, 15, 55f),
                exKneelingCableCrunch("d3_m_vt_crunch", 5, 3, 15, 26f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exSledLegPress("d3_m_mass_press", 1, 4, compoundReps, gluteBiased = false, loadKg = 125f),
                exRomanianDeadlift("d3_m_mass_rdl", 2, 4, 10, 28f),
                exSeatedLegCurl("d3_m_mass_curl", 3, 3, 10, 45f),
                exLegExtension("d3_m_mass_ext", 4, 3, 12, 45f),
                exCalfRaise("d3_m_mass_calf", 5, 4, 15, 60f)
            )
        }

        // Day 4: Shoulders, Upper Taper & Arms / Core (Zero duplicate back-to-back ab fillers)
        val day4Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exShoulderPress("d4_m_fl_shpress", 1, 3, compoundReps, useMachine = true, loadKg = 24f),
                exCableLateralRaise("d4_m_fl_lat", 2, 3, 15, 8f),
                exSeatedCableRow("d4_m_fl_row", 3, 3, 15, 44f),
                exOverheadCableTricep("d4_m_fl_ohtri", 4, 3, 15, 18f),
                exKneelingCableCrunch("d4_m_fl_crunch", 5, 3, 15, 25f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exShoulderPress("d4_m_ath_shpress", 1, 3, 12, useMachine = useGuidedPresses, loadKg = 18f),
                exCableLateralRaise("d4_m_ath_lat", 2, 3, 15, 9f),
                exFacePulls("d4_m_ath_face", 3, 3, 15, 18f),
                exOverheadCableTricep("d4_m_ath_ohtri", 4, 3, 12, 18f),
                exCablePallofHold("d4_m_ath_pallof", 5, 3, 45, 12f)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exShoulderPress("d4_m_vt_shpress", 1, primarySets, 10, useMachine = useGuidedPresses, loadKg = 20f),
                exCableLateralRaise("d4_m_vt_lat", 2, 4, 15, 10f),
                exInclinePress("d4_m_vt_inc", 3, 3, accessoryReps, useMachine = true),
                exFacePulls("d4_m_vt_face", 4, 3, 15, 18f),
                exKneelingCableCrunch("d4_m_vt_crunch", 5, 3, 15, 26f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exShoulderPress("d4_m_mass_shpress", 1, 4, compoundReps, useMachine = useGuidedPresses, loadKg = 22f),
                exCableLateralRaise("d4_m_mass_lat", 2, 4, 12, 10f),
                exFacePulls("d4_m_mass_face", 3, 3, 12, 20f),
                exInclineDbCurl("d4_m_mass_curl", 4, 3, 10, 12f),
                exAssistedOrBenchDips("d4_m_mass_dips", 5, 3, 10, isJointSafe = useGuidedPresses)
            )
        }

        return listOf(
            CombatDaySpec(
                dayIndex = 0,
                id = "DAY_1_UPPER_ALPHA",
                code = "Day 1",
                title = when (archetype) {
                    GoalTrainingArchetype.FAT_LOSS_SLIM -> "Chest, Triceps & Flat Stomach"
                    GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> "Heavy Chest & Thick Triceps"
                    else -> "Chest & Triceps"
                },
                subtitle = when (archetype) {
                    GoalTrainingArchetype.FAT_LOSS_SLIM -> "Metabolic Pressing & Supported Waist Tightening"
                    GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> "Progressive Upper-Body Mass & Arm Lockout"
                    else -> "Upper Chest, Mid Chest & Tricep Lockout"
                },
                focusTag = focusTag,
                exercises = day1Exercises
            ),
            CombatDaySpec(
                dayIndex = 1,
                id = "DAY_2_LOWER_ALPHA",
                code = "Day 2",
                title = when (archetype) {
                    GoalTrainingArchetype.FAT_LOSS_SLIM -> "Back, Posture & Core Burn"
                    GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> "Thick Back & Heavy Biceps"
                    else -> "Back & Biceps"
                },
                subtitle = "Wide Lats, Supported Mid-Back & Arm Strength",
                focusTag = focusTag,
                exercises = day2Exercises
            ),
            CombatDaySpec(
                dayIndex = 2,
                id = "DAY_3_UPPER_BETA",
                code = "Day 3",
                title = if (archetype == GoalTrainingArchetype.CURVY_OR_MASS_BUILD) "Heavy Legs & Posterior Chain" else "Legs & Stomach",
                subtitle = "Quads, Hamstrings, Calves & Tight Waistline",
                focusTag = focusTag,
                exercises = day3Exercises
            ),
            CombatDaySpec(
                dayIndex = 3,
                id = "DAY_4_LOWER_BETA",
                code = "Day 4",
                title = when (archetype) {
                    GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> "Shoulders, Rear Delts & Arms"
                    GoalTrainingArchetype.FAT_LOSS_SLIM -> "Shoulders, Back & Six-Pack Core"
                    else -> "Shoulders, Upper Chest & Core"
                },
                subtitle = "Capped Delts, Upright Posture & Weighted Abs",
                focusTag = focusTag,
                exercises = day4Exercises
            )
        )
    }

    private fun buildMaleSplitForFrequency(
        frequency: WeeklyFrequency,
        isJointSafe: Boolean,
        startingFrame: StartingFrameCategory,
        archetype: GoalTrainingArchetype
    ): List<CombatDaySpec> {
        val fourDay = buildMaleFourDaySplit(isJointSafe, startingFrame, archetype)
        val d1 = fourDay[0]
        val d2 = fourDay[1]
        val d3 = fourDay[2]
        val d4 = fourDay[3]

        return when (frequency) {
            WeeklyFrequency.THREE_DAYS -> listOf(
                d1.copy(
                    dayIndex = 0,
                    code = "Day 1",
                    title = "Chest, Shoulders & Triceps",
                    subtitle = "Upper Push Synergy (Chest + Side Delts + Triceps)",
                    exercises = listOf(
                        d1.exercises[0].copy(orderIndex = 1),
                        d1.exercises[1].copy(orderIndex = 2),
                        d4.exercises[0].copy(orderIndex = 3),
                        d4.exercises[1].copy(orderIndex = 4),
                        exTricepRopePushdown("d1_3d_pushdown", 5, 3, 12, 20f)
                    )
                ),
                d2.copy(
                    dayIndex = 1,
                    code = "Day 2",
                    title = "Back, Rear Delts & Biceps",
                    subtitle = "Upper Pull V-Taper Synergy"
                ),
                d3.copy(
                    dayIndex = 2,
                    code = "Day 3",
                    title = "Legs, Calves & Stomach",
                    subtitle = "Lower-Body Strength & Core"
                )
            )
            WeeklyFrequency.FOUR_DAYS -> fourDay
            WeeklyFrequency.FIVE_DAYS -> {
                val day5 = CombatDaySpec(
                    dayIndex = 4,
                    id = "DAY_5_UPPER_BODY_PUMP",
                    code = "Day 5",
                    title = "Upper V-Taper & Six-Pack Abs",
                    subtitle = "Incline Chest, Wide Lats, Side Delts & Core",
                    focusTag = d1.focusTag,
                    exercises = listOf(
                        exInclinePress("d5_m_inc", 1, 3, 12, useMachine = isJointSafe),
                        exLatPulldown("d5_m_pull", 2, 3, 12, neutralGrip = true, loadKg = 48f),
                        exCableLateralRaise("d5_m_lat", 3, 3, 15, 9f),
                        exFacePulls("d5_m_face", 4, 3, 15, 18f),
                        exKneelingCableCrunch("d5_m_crunch", 5, 3, 15, 25f)
                    )
                )
                fourDay + day5
            }
            WeeklyFrequency.SIX_DAYS -> {
                val day5 = CombatDaySpec(
                    dayIndex = 4,
                    id = "DAY_5_UPPER_BODY_PUMP",
                    code = "Day 5",
                    title = "Back Width & Arm Sculpt",
                    subtitle = "Lat Sweep, Rear Delts, Biceps & Triceps",
                    focusTag = d1.focusTag,
                    exercises = listOf(
                        exLatPulldown("d5_m6_pull", 1, 3, 12, neutralGrip = true, loadKg = 48f),
                        exSeatedCableRow("d5_m6_row", 2, 3, 12, 46f),
                        exFacePulls("d5_m6_face", 3, 3, 15, 18f),
                        exHammerCurl("d5_m6_hammer", 4, 3, 12, 12f),
                        exTricepRopePushdown("d5_m6_tri", 5, 3, 12, 22f)
                    )
                )
                val day6 = CombatDaySpec(
                    dayIndex = 5,
                    id = "DAY_6_LOWER_BODY_PUMP",
                    code = "Day 6",
                    title = "Legs, Glutes & Core",
                    subtitle = "Unilateral Leg Strength, Hamstrings & Waist",
                    focusTag = d1.focusTag,
                    exercises = listOf(
                        if (isJointSafe) exSledLegPress("d6_m6_press", 1, 3, 12, loadKg = 100f)
                        else exGobletSquat("d6_m6_goblet", 1, 3, 12, 24f),
                        exSplitSquat("d6_m6_split", 2, 3, 10, isSupported = isJointSafe, loadKg = 14f),
                        exSeatedLegCurl("d6_m6_curl", 3, 3, 12, 40f),
                        exCalfRaise("d6_m6_calf", 4, 3, 15, 50f),
                        exCaptainsChairKneeRaise("d6_m6_knee", 5, 3, 15)
                    )
                )
                fourDay + day5 + day6
            }
        }
    }

    // =========================================================================
    // FEMALE PHYSIOLOGICAL SPLITS (GLUTE/WAIST/POSTURE — ZERO MALE BRO DAYS)
    // =========================================================================

    private fun buildFemaleFourDaySplit(
        isJointSafe: Boolean,
        startingFrame: StartingFrameCategory,
        archetype: GoalTrainingArchetype
    ): List<CombatDaySpec> {
        val isHeavyFrame = startingFrame == StartingFrameCategory.HEAVY_OBESE
        val isSlimFrame = startingFrame == StartingFrameCategory.SLIM_UNDERWEIGHT
        val useGuidedStations = isJointSafe || isHeavyFrame

        val focusTag = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM ->
                if (isHeavyFrame) "Joint-Safe Calorie Burn & Flat Waist" else "Metabolic Lower-Body & Flat Tummy"
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> "Sleek Waistline, Posture & Toned Lines"
            GoalTrainingArchetype.PROPORTION_SCULPT -> "Upper-Glute Lift & Hourglass Waist Ratio"
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD ->
                if (isSlimFrame) "Slim-to-Curvy Progressive Glute & Hip Builder" else "Full Glute Curve & Feminine Shape"
        }

        // Day 1: Glutes, Hamstrings & Waist
        val day1Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exSledLegPress("d1_f_fl_press", 1, 3, if (isSlimFrame) 12 else 15, gluteBiased = true, loadKg = 75f),
                exSeatedLegCurl("d1_f_fl_curl", 2, 3, 15, 30f),
                exCableKickback("d1_f_fl_kick", 3, 3, 15, 12f),
                exHipAbductor("d1_f_fl_abd", 4, 3, 18, 38f),
                exKneelingCableCrunch("d1_f_fl_crunch", 5, 3, 15, 18f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exHipThrust("d1_f_ath_thrust", 1, 3, 12, 50f),
                exSeatedLegCurl("d1_f_ath_curl", 2, 3, 12, 32f),
                exSplitSquat("d1_f_ath_split", 3, 3, 12, isSupported = useGuidedStations, loadKg = 10f),
                exHipAbductor("d1_f_ath_abd", 4, 3, 15, 40f),
                exCablePallofHold("d1_f_ath_pallof", 5, 3, 40, 10f)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exHipThrust("d1_f_pr_thrust", 1, 4, 12, 55f),
                if (isHeavyFrame) exSledLegPress("d1_f_pr_press", 2, 3, 12, gluteBiased = true, loadKg = 80f)
                else exRomanianDeadlift("d1_f_pr_rdl", 2, 3, 12, 20f),
                exHipAbductor("d1_f_pr_abd", 3, 4, 15, 42f),
                exCableKickback("d1_f_pr_kick", 4, 3, 15, 14f),
                if (isSlimFrame) exSeatedLegCurl("d1_f_pr_curl", 5, 3, 12, 32f)
                else exCablePallofHold("d1_f_pr_pallof", 5, 3, 45, 10f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exHipThrust("d1_f_cv_thrust", 1, 4, 10, 60f),
                if (isHeavyFrame) exSledLegPress("d1_f_cv_press", 2, 4, 12, gluteBiased = true, loadKg = 90f)
                else exRomanianDeadlift("d1_f_cv_rdl", 2, 4, 10, 22f),
                exHipAbductor("d1_f_cv_abd", 3, 4, 12, 45f),
                exSeatedLegCurl("d1_f_cv_curl", 4, 3, 10, 35f),
                if (isHeavyFrame) exCablePallofHold("d1_f_cv_pallof", 5, 3, 40, 10f)
                else exCableKickback("d1_f_cv_kick", 5, 3, 12, 15f)
            )
        }

        // Day 2: Hourglass Back Taper, Sleek Shoulders & Posture (Feminine shape — never male arm-bro day)
        val day2Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exLatPulldown("d2_f_fl_pull", 1, 3, 15, neutralGrip = false, loadKg = 30f),
                exSeatedCableRow("d2_f_fl_row", 2, 3, 15, 28f),
                exShoulderPress("d2_f_fl_sh", 3, 3, 15, useMachine = true, loadKg = 12f),
                exTricepRopePushdown("d2_f_fl_tri", 4, 3, 15, 14f),
                exCaptainsChairKneeRaise("d2_f_fl_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exLatPulldown("d2_f_ath_pull", 1, 3, 12, neutralGrip = true, loadKg = 32f),
                exCableLateralRaise("d2_f_ath_lat", 2, 3, 15, 5f),
                exFacePulls("d2_f_ath_face", 3, 3, 15, 12f),
                exTricepRopePushdown("d2_f_ath_tri", 4, 3, 15, 14f),
                exKneelingCableCrunch("d2_f_ath_crunch", 5, 3, 15, 18f)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exLatPulldown("d2_f_pr_pull", 1, 3, 12, neutralGrip = false, loadKg = 32f),
                exCableLateralRaise("d2_f_pr_lat", 2, 4, 15, 6f),
                exChestSupportedRow("d2_f_pr_csrow", 3, 3, 12, 28f),
                exFacePulls("d2_f_pr_face", 4, 3, 15, 12f),
                exKneelingCableCrunch("d2_f_pr_crunch", 5, 3, 15, 20f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exLatPulldown("d2_f_cv_pull", 1, 3, 10, neutralGrip = true, loadKg = 34f),
                exShoulderPress("d2_f_cv_sh", 2, 3, 10, useMachine = useGuidedStations, loadKg = 10f),
                exChestSupportedRow("d2_f_cv_csrow", 3, 3, 10, 30f),
                exCableLateralRaise("d2_f_cv_lat", 4, 3, 12, 6f),
                exDbGluteBridge("d2_f_cv_bridge", 5, 4, 12, 28f)
            )
        }

        // Day 3: Glute Shelf, Sculpted Thighs & Flat Stomach
        val day3Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exHipThrust("d3_f_fl_thrust", 1, 3, 15, 50f),
                if (isHeavyFrame) exLegExtension("d3_f_fl_ext", 2, 3, 15, 26f)
                else exSplitSquat("d3_f_fl_split", 2, 3, 12, isSupported = true, loadKg = 8f),
                exRomanianDeadlift("d3_f_fl_rdl", 3, 3, 15, 18f),
                exCalfRaise("d3_f_fl_calf", 4, 3, 15, 35f),
                exCablePallofHold("d3_f_fl_pallof", 5, 3, 40, 10f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                if (useGuidedStations) exSledLegPress("d3_f_ath_press", 1, 3, 12, gluteBiased = true, loadKg = 80f)
                else exGobletSquat("d3_f_ath_goblet", 1, 3, 12, 18f),
                exRomanianDeadlift("d3_f_ath_rdl", 2, 3, 12, 20f),
                exCableKickback("d3_f_ath_kick", 3, 3, 15, 14f),
                exLegExtension("d3_f_ath_ext", 4, 3, 15, 28f),
                exCaptainsChairKneeRaise("d3_f_ath_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exSledLegPress("d3_f_pr_press", 1, 4, 12, gluteBiased = true, loadKg = 85f),
                exSeatedLegCurl("d3_f_pr_curl", 2, 3, 12, 32f),
                exSplitSquat("d3_f_pr_split", 3, 3, 10, isSupported = useGuidedStations, loadKg = 10f),
                exHipAbductor("d3_f_pr_abd", 4, 3, 15, 42f),
                exCaptainsChairKneeRaise("d3_f_pr_knee", 5, 3, 15)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                if (useGuidedStations) exSledLegPress("d3_f_cv_press", 1, 4, 10, gluteBiased = true, loadKg = 95f)
                else exGobletSquat("d3_f_cv_goblet", 1, 4, 10, 22f),
                exSplitSquat("d3_f_cv_split", 2, 4, 10, isSupported = useGuidedStations, loadKg = 12f),
                exCableKickback("d3_f_cv_kick", 3, 3, 12, 15f),
                exLegExtension("d3_f_cv_ext", 4, 3, 12, 30f),
                exHipAbductor("d3_f_cv_abd", 5, 4, 15, 45f)
            )
        }

        // Day 4: Posture, Booty Lift Finisher & Deep Core
        val day4Exercises = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> listOf(
                exChestSupportedRow("d4_f_fl_row", 1, 3, 15, 26f),
                exCableLateralRaise("d4_f_fl_lat", 2, 3, 15, 5f),
                exDbGluteBridge("d4_f_fl_bridge", 3, 3, 15, 24f),
                exFacePulls("d4_f_fl_face", 4, 3, 15, 12f),
                exKneelingCableCrunch("d4_f_fl_crunch", 5, 3, 15, 18f)
            )
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> listOf(
                exShoulderPress("d4_f_ath_sh", 1, 3, 12, useMachine = useGuidedStations, loadKg = 10f),
                exSeatedCableRow("d4_f_ath_row", 2, 3, 12, 28f),
                exDbGluteBridge("d4_f_ath_bridge", 3, 3, 15, 26f),
                exHammerCurl("d4_f_ath_curl", 4, 3, 12, 7f),
                exCablePallofHold("d4_f_ath_pallof", 5, 3, 45, 10f)
            )
            GoalTrainingArchetype.PROPORTION_SCULPT -> listOf(
                exShoulderPress("d4_f_pr_sh", 1, 3, 12, useMachine = useGuidedStations, loadKg = 10f),
                exSeatedCableRow("d4_f_pr_row", 2, 3, 12, 30f),
                exDbGluteBridge("d4_f_pr_bridge", 3, 4, 12, 28f),
                exTricepRopePushdown("d4_f_pr_tri", 4, 3, 15, 14f),
                exKneelingCableCrunch("d4_f_pr_crunch", 5, 3, 15, 20f)
            )
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> listOf(
                exHipThrust("d4_f_cv_thrust", 1, 4, 10, 60f),
                exSeatedCableRow("d4_f_cv_row", 2, 3, 12, 30f),
                exFacePulls("d4_f_cv_face", 3, 3, 15, 12f),
                exCableKickback("d4_f_cv_kick", 4, 3, 12, 15f),
                exCablePallofHold("d4_f_cv_pallof", 5, 3, 40, 10f)
            )
        }

        return listOf(
            CombatDaySpec(
                dayIndex = 0,
                id = "DAY_1_UPPER_ALPHA",
                code = "Day 1",
                title = when (archetype) {
                    GoalTrainingArchetype.FAT_LOSS_SLIM -> "Metabolic Glutes, Legs & Tummy"
                    GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> "Full Glute Curve & Hip Sculpt"
                    else -> "Glutes, Hamstrings & Waist"
                },
                subtitle = "Glute Lift, Hamstring Shape & Flat Waistline",
                focusTag = focusTag,
                exercises = day1Exercises
            ),
            CombatDaySpec(
                dayIndex = 1,
                id = "DAY_2_LOWER_ALPHA",
                code = "Day 2",
                title = if (archetype == GoalTrainingArchetype.CURVY_OR_MASS_BUILD) {
                    "Hourglass Posture & Glute Bridge"
                } else {
                    "Hourglass Back, Sleek Shoulders & Waist"
                },
                subtitle = "Upright Posture, Sleek Arms & Narrower Waist Ratio",
                focusTag = focusTag,
                exercises = day2Exercises
            ),
            CombatDaySpec(
                dayIndex = 2,
                id = "DAY_3_UPPER_BETA",
                code = "Day 3",
                title = if (archetype == GoalTrainingArchetype.CURVY_OR_MASS_BUILD) {
                    "Curvy Thighs, Split Squats & Glute Shelf"
                } else {
                    "Glute Shelf, Legs & Flat Stomach"
                },
                subtitle = "Sculpted Lower Curves, Kickbacks & Core Control",
                focusTag = focusTag,
                exercises = day3Exercises
            ),
            CombatDaySpec(
                dayIndex = 3,
                id = "DAY_4_LOWER_BETA",
                code = "Day 4",
                title = if (archetype == GoalTrainingArchetype.CURVY_OR_MASS_BUILD) {
                    "Booty Lift Finisher & Posture"
                } else {
                    "Shoulders, Glute Bridge & Deep Core"
                },
                subtitle = "Sleek Upper Posture, Booty Squeeze & Flat Midsection",
                focusTag = focusTag,
                exercises = day4Exercises
            )
        )
    }

    private fun buildFemaleSplitForFrequency(
        frequency: WeeklyFrequency,
        isJointSafe: Boolean,
        startingFrame: StartingFrameCategory,
        archetype: GoalTrainingArchetype
    ): List<CombatDaySpec> {
        val fourDay = buildFemaleFourDaySplit(isJointSafe, startingFrame, archetype)
        val useGuided = isJointSafe || startingFrame == StartingFrameCategory.HEAVY_OBESE
        return when (frequency) {
            WeeklyFrequency.THREE_DAYS -> listOf(
                fourDay[0].copy(
                    dayIndex = 0,
                    code = "Day 1",
                    title = "Glutes, Hamstrings & Flat Waist",
                    subtitle = "Primary Booty Lift, Hamstring Shape & Waistline"
                ),
                fourDay[1].copy(
                    dayIndex = 1,
                    code = "Day 2",
                    title = "Hourglass Back, Sleek Shoulders & Arms",
                    subtitle = "Lat Taper, Collarbone Line, Firm Under-Arms & Core",
                    exercises = listOf(
                        exLatPulldown("d2_f3_pull", 1, 3, 12, neutralGrip = true, loadKg = 32f),
                        exShoulderPress("d2_f3_sh", 2, 3, 12, useMachine = useGuided, loadKg = 10f),
                        exChestSupportedRow("d2_f3_row", 3, 3, 12, 28f),
                        exCableLateralRaise("d2_f3_lat", 4, 3, 15, 5f),
                        exTricepRopePushdown("d2_f3_tri", 5, 3, 15, 14f)
                    )
                ),
                fourDay[2].copy(
                    dayIndex = 2,
                    code = "Day 3",
                    title = "Glute Shelf, Sculpted Legs & Tummy",
                    subtitle = "Lower Curves, Unilateral Leg Tone & Deep Core"
                )
            )
            WeeklyFrequency.FOUR_DAYS -> fourDay
            WeeklyFrequency.FIVE_DAYS -> {
                val day5 = CombatDaySpec(
                    dayIndex = 4,
                    id = "DAY_5_UPPER_BODY_PUMP",
                    code = "Day 5",
                    title = "Booty Shelf & Flat Waist Finisher",
                    subtitle = "Hip Thrusts, Abductors, Kickbacks, Hamstrings & Core",
                    focusTag = fourDay[0].focusTag,
                    exercises = listOf(
                        exHipThrust("d5_f_thrust", 1, 4, 12, 55f),
                        exHipAbductor("d5_f_abd", 2, 3, 15, 42f),
                        exCableKickback("d5_f_kick", 3, 3, 15, 14f),
                        exSeatedLegCurl("d5_f_curl", 4, 3, 12, 32f),
                        exKneelingCableCrunch("d5_f_crunch", 5, 3, 15, 18f)
                    )
                )
                fourDay + day5
            }
            WeeklyFrequency.SIX_DAYS -> {
                val day5 = CombatDaySpec(
                    dayIndex = 4,
                    id = "DAY_5_UPPER_BODY_PUMP",
                    code = "Day 5",
                    title = "Booty Shelf & Hip Curves Finisher",
                    subtitle = "Hip Thrusts, Abductors, Kickbacks, Hamstrings & Core",
                    focusTag = fourDay[0].focusTag,
                    exercises = listOf(
                        exHipThrust("d5_f6_thrust", 1, 4, 12, 55f),
                        exHipAbductor("d5_f6_abd", 2, 3, 15, 42f),
                        exCableKickback("d5_f6_kick", 3, 3, 15, 14f),
                        exSeatedLegCurl("d5_f6_curl", 4, 3, 12, 32f),
                        exKneelingCableCrunch("d5_f6_crunch", 5, 3, 15, 18f)
                    )
                )
                val day6 = CombatDaySpec(
                    dayIndex = 5,
                    id = "DAY_6_LOWER_BODY_PUMP",
                    code = "Day 6",
                    title = "Hourglass Posture & Core Sculpt",
                    subtitle = "Lat Taper, Sleek Delts, Firm Arms & Lower Abs",
                    focusTag = fourDay[0].focusTag,
                    exercises = listOf(
                        exLatPulldown("d6_f6_pull", 1, 3, 12, neutralGrip = true, loadKg = 30f),
                        exCableLateralRaise("d6_f6_lat", 2, 3, 15, 5f),
                        exFacePulls("d6_f6_face", 3, 3, 15, 12f),
                        exTricepRopePushdown("d6_f6_tri", 4, 3, 15, 14f),
                        exCaptainsChairKneeRaise("d6_f6_knee", 5, 3, 15)
                    )
                )
                fourDay + day5 + day6
            }
        }
    }

    /**
     * Week B Rotation:
     * Rotates every split day (Day 1..Day 6) to fresh angles & grips for BOTH Male and Female
     * with ZERO cross-day clones in 5-day or 6-day schedules and 100% pure-Gym equipment.
     */
    private fun rotateDayForWeekB(
        day: CombatDaySpec,
        sex: BiologicalSex,
        isJointSafe: Boolean,
        startingFrame: StartingFrameCategory,
        archetype: GoalTrainingArchetype
    ): CombatDaySpec {
        val lower = day.title.lowercase()
        val reps = when (archetype) {
            GoalTrainingArchetype.FAT_LOSS_SLIM -> 15
            GoalTrainingArchetype.ATHLETIC_SLEEK_TONE -> 12
            GoalTrainingArchetype.PROPORTION_SCULPT -> 12
            GoalTrainingArchetype.CURVY_OR_MASS_BUILD -> 10
        }
        val primarySets = if (archetype == GoalTrainingArchetype.CURVY_OR_MASS_BUILD ||
            archetype == GoalTrainingArchetype.PROPORTION_SCULPT
        ) 4 else 3
        val useGuided = isJointSafe || startingFrame == StartingFrameCategory.HEAVY_OBESE

        if (sex == BiologicalSex.FEMALE) {
            val rotatedFemale = when (day.dayIndex) {
                0 -> listOf(
                    exHipThrust("d1_fb_thrust", 1, primarySets, reps, 55f),
                    exSeatedLegCurl("d1_fb_curl", 2, 3, reps, 32f),
                    exCableKickback("d1_fb_kick", 3, 3, 15, 14f),
                    exHipAbductor("d1_fb_abd", 4, 3, 15, 42f),
                    exKneelingCableCrunch("d1_fb_crunch", 5, 3, 15, 18f)
                )
                1 -> listOf(
                    exLatPulldown("d2_fb_pull", 1, 3, reps, neutralGrip = true, loadKg = 32f),
                    exShoulderPress("d2_fb_sh", 2, 3, reps, useMachine = useGuided, loadKg = 10f),
                    exChestSupportedRow("d2_fb_row", 3, 3, reps, 28f),
                    exTricepRopePushdown("d2_fb_tri", 4, 3, 15, 14f),
                    exCablePallofHold("d2_fb_pallof", 5, 3, 40, 10f)
                )
                2 -> listOf(
                    exSledLegPress("d3_fb_press", 1, primarySets, reps, gluteBiased = true, loadKg = 85f),
                    exRomanianDeadlift("d3_fb_rdl", 2, 3, reps, 20f),
                    exSplitSquat("d3_fb_split", 3, 3, 10, isSupported = useGuided, loadKg = 10f),
                    exCalfRaise("d3_fb_calf", 4, 3, 15, 38f),
                    exCaptainsChairKneeRaise("d3_fb_knee", 5, 3, 15)
                )
                3 -> listOf(
                    exSeatedCableRow("d4_fb_row", 1, 3, reps, 28f),
                    exCableLateralRaise("d4_fb_lat", 2, 3, 15, 6f),
                    exDbGluteBridge("d4_fb_bridge", 3, primarySets, 12, 28f),
                    exFacePulls("d4_fb_face", 4, 3, 15, 12f),
                    exKneelingCableCrunch("d4_fb_crunch", 5, 3, 15, 18f)
                )
                4 -> listOf(
                    exSledLegPress("d5_fb_press", 1, primarySets, reps, gluteBiased = true, loadKg = 85f),
                    exHipAbductor("d5_fb_abd", 2, 4, 15, 44f),
                    exDbGluteBridge("d5_fb_bridge", 3, 3, 15, 28f),
                    exCableKickback("d5_fb_kick", 4, 3, 15, 14f),
                    exCablePallofHold("d5_fb_pallof", 5, 3, 40, 10f)
                )
                else -> listOf(
                    exLatPulldown("d6_fb_pull", 1, 3, reps, neutralGrip = false, loadKg = 32f),
                    exShoulderPress("d6_fb_sh", 2, 3, reps, useMachine = true, loadKg = 12f),
                    exFacePulls("d6_fb_face", 3, 3, 15, 12f),
                    exOverheadCableTricep("d6_fb_ohtri", 4, 3, 15, 12f),
                    exKneelingCableCrunch("d6_fb_crunch", 5, 3, 15, 18f)
                )
            }
            return day.copy(
                subtitle = "${day.subtitle} • Fresh Muscle Angles",
                exercises = rotatedFemale
            )
        }

        val rotatedMale = when {
            day.dayIndex == 0 && "shoulder" in lower -> listOf(
                exInclinePress("d1_m3b_inc", 1, primarySets, reps, useMachine = useGuided),
                exPecDeckFly("d1_m3b_fly", 2, 3, 12),
                exShoulderPress("d1_m3b_sh", 3, 3, reps, useMachine = true, loadKg = 20f),
                exCableLateralRaise("d1_m3b_lat", 4, 3, 15, 9f),
                exOverheadCableTricep("d1_m3b_ohtri", 5, 3, 12, 20f)
            )
            day.dayIndex == 0 -> listOf(
                exMidChestPress("d1_mb_chest", 1, primarySets, reps, useMachine = useGuided),
                exPecDeckFly("d1_mb_fly", 2, 3, 12),
                exOverheadCableTricep("d1_mb_ohtri", 3, 3, 12, 20f),
                exAssistedOrBenchDips("d1_mb_dips", 4, 3, 12, isJointSafe = useGuided),
                exKneelingCableCrunch("d1_mb_crunch", 5, 3, 15, 25f)
            )
            day.dayIndex == 1 -> listOf(
                exLatPulldown("d2_mb_closepull", 1, primarySets, reps, neutralGrip = true, loadKg = 50f),
                exSeatedCableRow("d2_mb_row", 2, 3, reps, 48f),
                exFacePulls("d2_mb_face", 3, 3, 15, 18f),
                exHammerCurl("d2_mb_hammer", 4, 3, 12, 12f),
                exPreacherOrCableCurl("d2_mb_preacher", 5, 3, 12, usePreacher = true, loadKg = 22f)
            )
            day.dayIndex == 2 -> listOf(
                if (useGuided) exSledLegPress("d3_mb_press", 1, primarySets, reps, loadKg = 105f)
                else exGobletSquat("d3_mb_goblet", 1, primarySets, reps, 24f),
                exSplitSquat("d3_mb_split", 2, 3, 10, isSupported = useGuided, loadKg = 14f),
                exRomanianDeadlift("d3_mb_rdl", 3, 3, 12, 24f),
                exCalfRaise("d3_mb_calf", 4, 3, 15, 52f),
                exCaptainsChairKneeRaise("d3_mb_knee", 5, 3, 15)
            )
            day.dayIndex == 3 -> listOf(
                exShoulderPress("d4_mb_sh", 1, primarySets, reps, useMachine = useGuided, loadKg = 18f),
                exCableLateralRaise("d4_mb_lat", 2, 4, 15, 9f),
                exFacePulls("d4_mb_face", 3, 3, 15, 18f),
                exInclineDbCurl("d4_mb_inccurl", 4, 3, 12, 12f),
                exCablePallofHold("d4_mb_pallof", 5, 3, 45, 12f)
            )
            day.dayIndex == 4 && "back" in lower -> listOf(
                exChestSupportedRow("d5_m6b_csrow", 1, primarySets, reps, 48f),
                exLatPulldown("d5_m6b_widepull", 2, 3, reps, neutralGrip = false, loadKg = 50f),
                exFacePulls("d5_m6b_face", 3, 3, 15, 18f),
                exPreacherOrCableCurl("d5_m6b_curl", 4, 3, 12, usePreacher = false, loadKg = 22f),
                exOverheadCableTricep("d5_m6b_ohtri", 5, 3, 12, 20f)
            )
            day.dayIndex == 4 -> listOf(
                exMidChestPress("d5_mb_chest", 1, 3, reps, useMachine = true),
                exChestSupportedRow("d5_mb_csrow", 2, 3, reps, 46f),
                exCableLateralRaise("d5_mb_lat", 3, 3, 15, 9f),
                exHammerCurl("d5_mb_hammer", 4, 3, 12, 12f),
                exCaptainsChairKneeRaise("d5_mb_knee", 5, 3, 15)
            )
            else -> listOf(
                exSledLegPress("d6_m6b_press", 1, primarySets, reps, loadKg = 110f),
                exRomanianDeadlift("d6_m6b_rdl", 2, 3, 12, 26f),
                exLegExtension("d6_m6b_ext", 3, 3, 15, 42f),
                exCalfRaise("d6_m6b_calf", 4, 3, 15, 55f),
                exKneelingCableCrunch("d6_m6b_crunch", 5, 3, 15, 25f)
            )
        }

        return day.copy(
            subtitle = "${day.subtitle} • Fresh Muscle Angles",
            exercises = rotatedMale
        )
    }
}

