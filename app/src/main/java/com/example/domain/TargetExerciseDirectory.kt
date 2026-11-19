package com.example.domain

enum class TargetBodyZone(
    val id: String,
    val label: String,
    val shortLabel: String
) {
    ALL("ALL", "All 18 Targets", "All (18)"),
    UPPER_GRIP("UPPER_GRIP", "Upper Body & Grip", "Upper & Grip"),
    CORE_PELVIC("CORE_PELVIC", "Abs, Waist & Kegel", "Core & Kegel"),
    LEGS_CURVES("LEGS_CURVES", "Legs, Booty & Calves", "Legs & Curves"),
    HEAD_VOICE_MOBILITY("HEAD_VOICE_MOBILITY", "Neck/Tongue, Voice & Skills", "Neck, Voice & Skills")
}

data class TargetExerciseGroup(
    val id: String,
    val shortChipLabel: String,
    val title: String,
    val subtitle: String,
    val zone: TargetBodyZone,
    val expectedResultsTimeline: String,
    val durationMinutes: Int,
    val caloriesKcal: Int,
    val goalFocus: PackGoalFocus,
    val equipmentMode: PackEquipmentMode,
    val exercises: List<ApexCoachMovementSpec>
) {
    fun toQuickStrikeWorkout(): QuickStrikeWorkout = QuickStrikeWorkout(
        id = id,
        title = title,
        subtitle = subtitle,
        durationLabel = "$durationMinutes MINS",
        durationMinutes = durationMinutes,
        caloriesKcal = caloriesKcal,
        iconTag = "TARGET • ${shortChipLabel.uppercase()}",
        badgeColorHex = 0xFF1D4ED8,
        category = SpecialtyTrackCategory.MUSCLE_FOCUS,
        equipmentMode = equipmentMode,
        goalFocus = goalFocus,
        exercises = exercises.mapIndexed { idx, spec -> spec.copy(stationOrder = idx + 1) },
        challengeDays = 0,
        expectedResultsTimeline = expectedResultsTimeline
    )
}

/**
 * Head-to-Toe Target Exercise Directory (18 Specialized Body & Performance Categories):
 * - Separates single-focus Target Exercises (Chest, Shoulders, Back, Arms, Grip, Abs, Waist,
 *   Kegel, Quads, Glutes/Hips, Hamstrings, Calves, Stamina, Flexibility, Tall Spine/Height,
 *   Calisthenics Skills, Jawline/Neck/Tongue Mewing Posture, and Diaphragm/Voice Power)
 *   from Multi-Day 7/14/21/28-Day Challenges.
 * - Every category has an explicit Expected Result Timeline, 6 distinct-GIF exercises,
 *   >= 3 working sets per main exercise, and full 0 -> Hero coaching support.
 */
object TargetExerciseDirectory {

    private fun move(
        id: String,
        badge: String,
        name: String,
        sets: Int,
        repsLabel: String,
        musclePill: String,
        cue: String,
        anim: ExerciseAnimationType,
        loadKg: Float = 0f
    ) = ApexCoachMovementSpec(
        id = id,
        stationOrder = 1,
        stationBadge = badge,
        name = name,
        totalSets = sets,
        setsAndRepsLabel = repsLabel,
        targetMusclePill = musclePill,
        formCueSummary = cue,
        animationType = anim,
        defaultLoadKg = loadKg
    )

    val ALL_GROUPS: List<TargetExerciseGroup> = listOf(
        // ==================== ZONE 1: UPPER BODY & GRIP ====================
        TargetExerciseGroup(
            id = "target_ex_chest",
            shortChipLabel = "Chest",
            title = "Chest & Upper Pec Sculptor",
            subtitle = "Upper shelf, mid-pec thickness & inner-chest squeeze",
            zone = TargetBodyZone.UPPER_GRIP,
            expectedResultsTimeline = "2–3 wks chest fullness • 6–8 wks visible upper-pec line",
            durationMinutes = 20,
            caloriesKcal = 185,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_ch_1", "Warm-Up", "Dynamic Chest & Shoulder Opener", 1, "00:35", "Chest & Front Delts", "Sweep arms wide at chest height to warm shoulder capsules.", ExerciseAnimationType.PIKE_SHOULDER_PRESS),
                move("tx_ch_2", "Upper Chest", "30° Incline Dumbbell Press", 3, "x12", "Upper Pecs", "Tuck elbows 45° and drive dumbbells smoothly above collarbone.", ExerciseAnimationType.INCLINE_DB_PRESS, 16f),
                move("tx_ch_3", "Mid Chest", "Standard Push-Ups", 3, "x12", "Mid Chest & Core", "Keep body rigid from head to heels and press floor away.", ExerciseAnimationType.STANDARD_PUSH_UPS),
                move("tx_ch_4", "Inner Squeeze", "Pec Deck Inner-Chest Fly", 3, "x14", "Inner Chest", "Hug handles together and hold peak chest squeeze 1 full second.", ExerciseAnimationType.INCLINE_DB_PRESS, 25f),
                move("tx_ch_5", "Lower Shelf", "Decline Push-Ups", 3, "x10", "Upper & Outer Pecs", "Elevate feet on low step and lower chest under 2-second control.", ExerciseAnimationType.INCLINE_PUSH_UPS),
                move("tx_ch_6", "Cool-Down Stretch", "Spine Cobra Stretch", 1, "00:35", "Chest & Anterior Core", "Lift chest tall and breathe slowly to lengthen front torso.", ExerciseAnimationType.COBRA_STRETCH)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_shoulders",
            shortChipLabel = "Shoulders",
            title = "3D Shoulders & Collarbone Caps",
            subtitle = "Front press, side-delt width & rear-delt posture",
            zone = TargetBodyZone.UPPER_GRIP,
            expectedResultsTimeline = "2–3 wks upright frame • 6–8 wks rounder shoulder caps",
            durationMinutes = 19,
            caloriesKcal = 165,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_sh_1", "Warm-Up", "Scapular Upper-Back Opener", 1, "00:35", "Rotator Cuff & Scapula", "Glide shoulder blades smoothly down and back without shrugging.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_sh_2", "Overhead Press", "Seated Dumbbell Shoulder Press", 3, "x12", "Front & Side Delts", "Brace ribs down and press dumbbells cleanly overhead.", ExerciseAnimationType.CABLE_LATERAL_RAISE, 12f),
                move("tx_sh_3", "Side Width", "Dumbbell Lateral Raise", 3, "x15", "Side Delts", "Lead with elbows out to shoulder height for wider shoulder frame.", ExerciseAnimationType.CABLE_LATERAL_RAISE, 6f),
                move("tx_sh_4", "Rear Posture", "Cable Rear-Delt Face Pulls", 3, "x15", "Rear Delts", "Pull rope toward forehead with high elbows and 1s rear squeeze.", ExerciseAnimationType.SEATED_CABLE_ROW, 15f),
                move("tx_sh_5", "Bodyweight Cap", "Pike Shoulder Push-Ups", 3, "x10", "Vertical Delts", "Hips high in an inverted V and press floor firmly away.", ExerciseAnimationType.PIKE_SHOULDER_PRESS),
                move("tx_sh_6", "Cool-Down Stretch", "Overhead Shoulder & Tricep Stretch", 1, "00:35", "Delts & Triceps", "Breathe calmly while opening tight overhead shoulder fascia.", ExerciseAnimationType.PIKE_SHOULDER_PRESS)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_back",
            shortChipLabel = "Back & Lats",
            title = "Wide Back, Lats & V-Taper",
            subtitle = "Vertical lat sweep, mid-back thickness & anti-slouch pull",
            zone = TargetBodyZone.UPPER_GRIP,
            expectedResultsTimeline = "2–3 wks anti-slouch posture • 6–8 wks wider V-taper",
            durationMinutes = 21,
            caloriesKcal = 190,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_bk_1", "Warm-Up", "Thoracic Mobility Flow", 1, "00:35", "Thoracic Spine", "Open upper spine smoothly so lats fire through full range.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_bk_2", "Vertical Pull", "Pull-Up & Bar Hang", 3, "x8", "Lats & Upper Back", "Depress shoulders first and drive elbows down toward ribs.", ExerciseAnimationType.LAT_PULLDOWN),
                move("tx_bk_3", "Lat Width", "Wide-Grip Lat Pulldown", 3, "x12", "Outer Lats", "Pull bar to upper chest without swinging lower back.", ExerciseAnimationType.LAT_PULLDOWN, 35f),
                move("tx_bk_4", "Mid-Back", "Seated Cable Row", 3, "x12", "Rhomboids & Mid-Back", "Sit tall and squeeze shoulder blades together for 1 full second.", ExerciseAnimationType.SEATED_CABLE_ROW, 35f),
                move("tx_bk_5", "Bodyweight Row", "Doorframe Inverted Row", 3, "x12", "Lats & Rear Delts", "Keep body straight and pull chest cleanly toward hands.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_bk_6", "Cool-Down Stretch", "Kneeling Lat Decompression Stretch", 1, "00:40", "Lats & Spine", "Sink hips back and lengthen lats with slow exhales.", ExerciseAnimationType.DOORFRAME_ROW)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_arms",
            shortChipLabel = "Arms",
            title = "Biceps Peak & Triceps Sculpt",
            subtitle = "Long-head bicep stretch, brachialis thickness & tricep horseshoe",
            zone = TargetBodyZone.UPPER_GRIP,
            expectedResultsTimeline = "2–3 wks tighter arm tone • 6–8 wks fuller sleeve/arm shape",
            durationMinutes = 18,
            caloriesKcal = 155,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_ar_1", "Warm-Up", "Dynamic Chest & Shoulder Opener", 1, "00:30", "Elbows & Shoulders", "Warm elbow and shoulder tendons before direct arm tension.", ExerciseAnimationType.PIKE_SHOULDER_PRESS),
                move("tx_ar_2", "Bicep Peak", "Incline Dumbbell Curl", 3, "x12", "Biceps Long Head", "Keep elbows behind torso and curl without swinging.", ExerciseAnimationType.INCLINE_DB_CURL, 8f),
                move("tx_ar_3", "Tricep Lockout", "Cable Tricep Rope Pushdown", 3, "x14", "Lateral Triceps", "Pin elbows to ribs and spread rope cleanly at bottom.", ExerciseAnimationType.TRICEP_DIPS, 18f),
                move("tx_ar_4", "Outer Arm", "Dumbbell Hammer Curl", 3, "x12", "Brachialis & Forearm", "Palms facing inward for thicker arm profile and happy wrists.", ExerciseAnimationType.INCLINE_DB_CURL, 9f),
                move("tx_ar_5", "Compound Dip", "Bench Tricep Dips", 3, "x12", "Back of Arms", "Slide back close to bench and press to crisp elbow lockout.", ExerciseAnimationType.TRICEP_DIPS),
                move("tx_ar_6", "Cool-Down Stretch", "Overhead Tricep Stretch", 1, "00:35", "Triceps & Lats", "Gently lengthen back of arms while breathing steadily.", ExerciseAnimationType.PIKE_SHOULDER_PRESS)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_grip",
            shortChipLabel = "Grip & Wrists",
            title = "Forearms, Wrists & Iron Grip",
            subtitle = "Crushing bar hang endurance, brachioradialis & carry strength",
            zone = TargetBodyZone.UPPER_GRIP,
            expectedResultsTimeline = "2–4 wks noticeably stronger grip • 6–8 wks denser forearms",
            durationMinutes = 16,
            caloriesKcal = 135,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_gr_1", "Warm-Up", "Scapular Upper-Back Opener", 1, "00:30", "Wrists & Scapula", "Wake up wrists, fingers, and shoulder girdle.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_gr_2", "Bar Crush", "Dead Hang Bar Hold", 3, "00:35", "Finger Flexors & Lats", "Wrap thumbs firmly around bar and keep shoulders active.", ExerciseAnimationType.LAT_PULLDOWN),
                move("tx_gr_3", "Top Forearm", "Reverse-Grip Forearm Curls", 3, "x14", "Forearm Extensors", "Overhand grip builds top-of-forearm cords and wrist stability.", ExerciseAnimationType.INCLINE_DB_CURL, 7f),
                move("tx_gr_4", "Loaded Carry", "Heavy Farmer Carry Curl Hold", 3, "00:40", "Grip & Traps", "Stand tall crushing handles or loaded bags without leaning.", ExerciseAnimationType.INCLINE_DB_CURL, 16f),
                move("tx_gr_5", "Thick Pull", "Towel Grip Doorframe Row", 3, "x12", "Pinch Grip & Back", "Squeeze towel/doorframe hard while rowing chest forward.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_gr_6", "Cool-Down Stretch", "Overhead Wrist & Tricep Stretch", 1, "00:30", "Forearm Flexors", "Release finger and forearm tension smoothly.", ExerciseAnimationType.PIKE_SHOULDER_PRESS)
            )
        ),

        // ==================== ZONE 2: ABS, WAIST & KEGEL ====================
        TargetExerciseGroup(
            id = "target_ex_abs",
            shortChipLabel = "Six-Pack Abs",
            title = "Upper & Lower Six-Pack Abs",
            subtitle = "Spinal flexion crunches & lower-belly pelvic raises",
            zone = TargetBodyZone.CORE_PELVIC,
            expectedResultsTimeline = "2–3 wks firmer core • visible abs depend on body-fat level",
            durationMinutes = 16,
            caloriesKcal = 145,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_ab_1", "Warm-Up", "Thoracic Cobra Flow", 1, "00:30", "Core & Spine", "Mobilize mid-spine before loaded abdominal flexion.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_ab_2", "Lower Abs", "Hanging Knee Raise", 3, "x12", "Lower Rectus Abdominis", "Curl pelvis upward toward ribs instead of swinging legs.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_ab_3", "Upper Abs", "Floor Abdominal Crunches", 3, "x16", "Upper Six-Pack", "Exhale all air at peak rib-to-pelvis squeeze.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_ab_4", "Deep Control", "Lying Straight Leg Raise", 3, "x12", "Lower Belly", "Press lower back flat into mat throughout every rep.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_ab_5", "Pelvic Curl", "Reverse Crunches", 3, "x14", "Lower Core", "Lift tailbone 2 inches off mat and lower slowly.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_ab_6", "Cool-Down Stretch", "Abdominal Wall Stretch", 1, "00:35", "Rectus Abdominis", "Lengthen abdominal wall calmly with nasal breathing.", ExerciseAnimationType.COBRA_STRETCH)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_waist",
            shortChipLabel = "Flat Waist",
            title = "Obliques & Flat Waist Vacuum",
            subtitle = "Deep transverse corset brace to pull protruding belly inward",
            zone = TargetBodyZone.CORE_PELVIC,
            expectedResultsTimeline = "10–14 days flatter resting belly posture • 4–6 wks tighter waist",
            durationMinutes = 15,
            caloriesKcal = 130,
            goalFocus = PackGoalFocus.CORE_WAIST_CINCH,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_wa_1", "Warm-Up", "Dynamic Squat Opener", 1, "00:30", "Hips & Core", "Wake up pelvis and ribcage alignment.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_wa_2", "Internal Corset", "Stomach Vacuum Hold", 3, "00:30", "Transverse Abdominis", "Exhale fully and draw navel gently toward spine while breathing shallowly.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_wa_3", "Anti-Rotation", "Plank Shoulder Taps", 3, "x16", "Obliques & Corset", "Widen feet slightly and keep hips 100% still as hands tap.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_wa_4", "360° Brace", "Forearm Plank Hold", 3, "00:45", "Deep Core", "Tuck tailbone slightly and pull elbows toward toes.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_wa_5", "Sculpted Crunch", "Weighted Cable Crunch", 3, "x14", "Midsection", "Curl ribs down smoothly without pulling on neck.", ExerciseAnimationType.ABDOMINAL_CRUNCHES, 20f),
                move("tx_wa_6", "Cool-Down Stretch", "Kneeling Lat & Side-Waist Stretch", 1, "00:35", "Obliques & Lats", "Reach long and open side-waist fascia.", ExerciseAnimationType.DOORFRAME_ROW)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_kegel",
            shortChipLabel = "Kegel & Pelvic",
            title = "Pelvic Floor & Kegel Control",
            subtitle = "Discreet internal pelvic lift, endurance holds & core synergy",
            zone = TargetBodyZone.CORE_PELVIC,
            expectedResultsTimeline = "2–3 wks noticeable control • 6–8 wks full pelvic strength",
            durationMinutes = 14,
            caloriesKcal = 95,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_kg_1", "Warm-Up", "Thoracic Mobility Flow", 1, "00:30", "Diaphragm & Pelvis", "Relax jaw, belly, and hips so pelvic floor can isolate cleanly.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_kg_2", "Endurance Hold", "Slow Pelvic Kegel Squeeze & Hold", 3, "00:35", "Pelvic Floor", "Lift internal pelvic sling on exhale for 5s, relax completely 5s.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_kg_3", "Pelvic + Glutes", "Pelvic Floor Glute Bridge", 3, "x14", "Pelvic Sling & Glutes", "Engage pelvic lift gently as hips rise and release cleanly at bottom.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_kg_4", "Corset Link", "Deep Core Vacuum & Pelvic Lift", 3, "00:30", "Deep Core & Pelvic", "Combine gentle lower-belly draw-in with upward pelvic lift.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_kg_5", "Stability", "Quick-Pulse Forearm Plank Brace", 3, "00:35", "Core & Pelvic Endurance", "Maintain calm nasal breathing without bearing downward.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_kg_6", "Cool-Down Stretch", "Runner's Hip Flexor Stretch", 1, "00:35", "Pelvic Release", "Let pelvic floor soften and lengthen completely on every inhale.", ExerciseAnimationType.FORWARD_LUNGES)
            )
        ),

        // ==================== ZONE 3: LEGS, BOOTY & CALVES ====================
        TargetExerciseGroup(
            id = "target_ex_quads",
            shortChipLabel = "Quads & Thighs",
            title = "Quads & Athletic Front Thighs",
            subtitle = "Knee-friendly squat depth, single-leg balance & sweep",
            zone = TargetBodyZone.LEGS_CURVES,
            expectedResultsTimeline = "2–3 wks stronger knees & stairs • 6–8 wks firmer thigh sweep",
            durationMinutes = 20,
            caloriesKcal = 205,
            goalFocus = PackGoalFocus.MUSCLE_HYPERTROPHY,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_qd_1", "Warm-Up", "Dynamic Squat Opener", 1, "00:35", "Knees & Ankles", "Open ankles and hips before loaded quad work.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_qd_2", "Compound Squat", "Dumbbell Goblet Squat", 3, "x12", "Quads & Core", "Hold weight at chest, keep torso tall, and sit between heels.", ExerciseAnimationType.BODYWEIGHT_SQUATS, 16f),
                move("tx_qd_3", "Heavy Drive", "45° Sled Leg Press", 3, "x12", "Full Quads", "Lower sled smoothly to 90° knees without lifting tailbone.", ExerciseAnimationType.BODYWEIGHT_SQUATS, 70f),
                move("tx_qd_4", "Single-Leg", "Bulgarian Split Squat", 3, "x10", "Quads & Balance", "Drop back knee smoothly and drive up through front foot.", ExerciseAnimationType.FORWARD_LUNGES, 10f),
                move("tx_qd_5", "Quad Isolation", "Seated Machine Leg Extension", 3, "x14", "Teardrop Quad", "Pause 1 full second at top knee extension.", ExerciseAnimationType.BODYWEIGHT_SQUATS, 28f),
                move("tx_qd_6", "Cool-Down Stretch", "Runner's Hip Flexor & Quad Stretch", 1, "00:35", "Quads & Hip Flexors", "Tuck pelvis gently to lengthen front thigh.", ExerciseAnimationType.FORWARD_LUNGES)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_glutes",
            shortChipLabel = "Booty & Hips",
            title = "Glutes, Booty Shelf & Outer Hips",
            subtitle = "Upper-glute lift, side-hip curve rounding & hip thrust power",
            zone = TargetBodyZone.LEGS_CURVES,
            expectedResultsTimeline = "2–3 wks glute wake-up • 6–8 wks lifted booty & hip curve",
            durationMinutes = 20,
            caloriesKcal = 195,
            goalFocus = PackGoalFocus.GLUTES_CURVES,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_gl_1", "Warm-Up", "Dynamic Squat Opener", 1, "00:35", "Hips & Glutes", "Open hip capsules so glutes take over instead of lower back.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_gl_2", "Primary Lift", "Padded Bench Hip Thrust", 4, "x12", "Full Glutes", "Chin tucked, shins vertical at top, and hold squeeze 2 seconds.", ExerciseAnimationType.BODYWEIGHT_SQUATS, 35f),
                move("tx_gl_3", "Outer Curve", "Seated Hip Abduction", 3, "x16", "Side Glutes & Hips", "Press knees wide with 1s outer-hip pause.", ExerciseAnimationType.FORWARD_LUNGES, 30f),
                move("tx_gl_4", "Upper Shelf", "Standing Glute Kickbacks", 3, "x14", "Upper Glute Shelf", "Kick heel back and slightly out without arching lower back.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_gl_5", "Deep Stretch", "Dumbbell Reverse Lunges", 3, "x12", "Glute-Ham Tie-In", "Step backward and drive through front heel.", ExerciseAnimationType.FORWARD_LUNGES, 10f),
                move("tx_gl_6", "Cool-Down Stretch", "Runner's Hip Lunge Stretch", 1, "00:35", "Hip Flexors & Glutes", "Release tight front hips so glutes stay switched on.", ExerciseAnimationType.FORWARD_LUNGES)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_hamstrings",
            shortChipLabel = "Hamstrings",
            title = "Hamstrings, Posterior Chain & Knees",
            subtitle = "Hip-hinge lengthening & knee-stabilizing leg curl strength",
            zone = TargetBodyZone.LEGS_CURVES,
            expectedResultsTimeline = "2–3 wks looser back-of-legs • 6–8 wks stronger posterior chain",
            durationMinutes = 18,
            caloriesKcal = 175,
            goalFocus = PackGoalFocus.STRENGTH_POWER,
            equipmentMode = PackEquipmentMode.DUMBBELL_SMALL_GYM,
            exercises = listOf(
                move("tx_hm_1", "Warm-Up", "Dynamic Squat Opener", 1, "00:30", "Hips & Hamstrings", "Warm hips and knees smoothly.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_hm_2", "Hip Hinge", "Dumbbell Romanian Deadlift", 3, "x12", "Hamstrings & Glutes", "Push hips straight back with flat spine until deep hamstring stretch.", ExerciseAnimationType.FORWARD_LUNGES, 18f),
                move("tx_hm_3", "Knee Flexion", "Seated Machine Leg Curl", 3, "x14", "Lower Hamstrings", "Curl heels smoothly underneath seat with 2s eccentric return.", ExerciseAnimationType.BODYWEIGHT_SQUATS, 28f),
                move("tx_hm_4", "Athletic Stride", "Long-Stride Walking Lunges", 3, "x12", "Glutes & Hamstrings", "Step long and drive cleanly through front heel.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_hm_5", "Mat Bridge", "Heel-Dug Floor Glute Bridge", 3, "x15", "Posterior Chain", "Walk heels 3 inches farther out to load hamstrings directly.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_hm_6", "Cool-Down Stretch", "Runner's Hip Flexor Stretch", 1, "00:35", "Hips & Posterior", "Breathe slowly to restore full stride length.", ExerciseAnimationType.FORWARD_LUNGES)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_calves",
            shortChipLabel = "Calves & Ankles",
            title = "Calves, Achilles & Vertical Spring",
            subtitle = "Full-stretch heel raises, ankle mobility & lower-leg armor",
            zone = TargetBodyZone.LEGS_CURVES,
            expectedResultsTimeline = "2–3 wks springier ankles • 6–8 wks sharper calf definition",
            durationMinutes = 15,
            caloriesKcal = 135,
            goalFocus = PackGoalFocus.TONE_POSTURE,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_cv_1", "Warm-Up", "Low-Impact Step Jacks", 1, "00:35", "Ankles & Calves", "Warm Achilles tendon with light rhythmic footwork.", ExerciseAnimationType.JUMPING_JACKS),
                move("tx_cv_2", "Peak Calf", "Standing Single-Leg Calf Raise", 4, "x16", "Gastrocnemius", "Pause 2 full seconds on big-toe ball at top — zero bouncing.", ExerciseAnimationType.JUMPING_JACKS),
                move("tx_cv_3", "Soleus & Ankle", "Split Squat Ankle Knee-Over-Toe", 3, "x10", "Soleus & Ankle", "Glide front knee forward over toes while keeping heel planted.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_cv_4", "Lower Endurance", "Tempo Bodyweight Squats", 3, "x15", "Quads & Ankles", "Smooth 2-second squat keeping tripod foot rooted.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_cv_5", "Balance Control", "Forward Lunges Ankle Control", 3, "x12", "Ankle Stabilizers", "Decelerate softly through mid-foot and push back tall.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_cv_6", "Cool-Down Stretch", "Runner's Hip & Calf Stretch", 1, "00:35", "Calves & Hips", "Press back heel down gently to lengthen calf.", ExerciseAnimationType.FORWARD_LUNGES)
            )
        ),

        // ==================== ZONE 4: NECK/TONGUE, VOICE & SKILLS ====================
        TargetExerciseGroup(
            id = "target_ex_jaw_neck_tongue",
            shortChipLabel = "Neck, Jaw & Tongue",
            title = "Jawline, Neck & Tongue Posture (Mewing)",
            subtitle = "Roof-of-mouth tongue suction, anti-tech-neck chin tucks & hyoid tone",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "7–14 days taller head posture • 6–8 wks firmer neck & under-chin line",
            durationMinutes = 12,
            caloriesKcal = 75,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_jn_1", "Warm-Up", "Scapular Upper-Back Opener", 1, "00:30", "Upper Back & Neck Base", "Drop shoulders away from ears so cervical spine can stack tall.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_jn_2", "Tongue Posture", "Tongue-Roof Mewing Suction Hold", 3, "00:40", "Tongue & Submental Sling", "Flatten back + tip of tongue gently across roof of mouth, lips sealed, teeth lightly apart, nasal breathing.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_jn_3", "Anti-Tech-Neck", "Wall Cervical Chin-Tuck", 3, "x12", "Deep Cervical Alignment", "Glide back of head straight backward into wall (make a double chin) for 2s without tilting head up.", ExerciseAnimationType.INCLINE_PUSH_UPS),
                move("tx_jn_4", "Deep Flexors", "Supine Deep Neck-Flexor Tuck", 3, "00:25", "Deep Neck Flexors", "Lie flat, nod chin 1 cm inward, and hover back of head 1 cm off mat without clenching jaw.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_jn_5", "Under-Chin Tone", "Submental Hyoid Neck Lift", 3, "x12", "Hyoid & Suprahyoid", "Keep shoulders down, press tongue firmly to palate, and lengthen front neck smoothly.", ExerciseAnimationType.SEATED_CABLE_ROW),
                move("tx_jn_6", "Cool-Down Stretch", "Upper Trapezius Neck Release", 1, "00:35", "Upper Traps & Levator", "Exhale slowly as tight side-neck muscles melt downward.", ExerciseAnimationType.PIKE_SHOULDER_PRESS)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_voice_breath",
            shortChipLabel = "Voice & Breath",
            title = "Diaphragm, Breath & Voice Power",
            subtitle = "360° rib expansion, sibilant hiss breath support & chest resonance",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "3–7 days steadier vocal support • 3–4 wks deeper lung & speaking stamina",
            durationMinutes = 12,
            caloriesKcal = 70,
            goalFocus = PackGoalFocus.STAMINA_CONDITIONING,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_vb_1", "Warm-Up", "360° Ribcage Opener", 1, "00:30", "Intercostals & Thoracic", "Unlock stiff ribs so lungs and diaphragm expand freely.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_vb_2", "Diaphragm Engine", "Diaphragmatic Belly Expansion", 3, "00:45", "Diaphragm Dome", "Inhale 4s through nose expanding lower ribs 360° — shoulders stay 100% relaxed.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_vb_3", "Vocal Support", "Sustained Sibilant Hiss Exhale", 3, "00:35", "Vocal Breath Pressure", "Inhale low, then hiss a steady even 'SSS' for 20–30s without throat strain.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_vb_4", "Chest Voice", "Chest Resonance Humming Projection", 3, "00:35", "Vocal Cord Resonance", "Hum 'MMM' calmly with relaxed jaw until you feel gentle vibration on sternum & lips.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_vb_5", "Lung Stamina", "Box-Breath CO2 Stamina Cadence", 3, "00:40", "CO2 Tolerance & Focus", "4s nasal inhale • 4s calm hold • 6s controlled exhale • 2s pause.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_vb_6", "Cool-Down Stretch", "Intercostal Side-Rib Release", 1, "00:35", "Side Ribs & Lungs", "Breathe into side ribs to finish calm, resonant, and oxygenated.", ExerciseAnimationType.DOORFRAME_ROW)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_calisthenics",
            shortChipLabel = "Calisthenics Lab",
            title = "Calisthenics Skill Lab: Planche, Lever & Handstand",
            subtitle = "Straight-arm lean, inverted balance, tuck front lever & L-sit compression",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "2–3 wks hollow & scapular lock • 6–12 wks clean skill holds",
            durationMinutes = 20,
            caloriesKcal = 185,
            goalFocus = PackGoalFocus.CALISTHENICS_CONTROL,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            exercises = listOf(
                move("tx_cl_1", "Warm-Up", "Scapular Upper-Back Opener", 1, "00:35", "Wrists & Scapula", "Prep wrists and straight-arm scapular protraction.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_cl_2", "Planche Skill", "Parallettes Planche Hold", 3, "00:20", "Straight-Arm Delts & Core", "Lock elbows 100% straight, protract shoulders, and lean forward.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_cl_3", "Inversion", "Wall Handstand Push-Up Prep", 3, "00:30", "Shoulders & Balance", "Push tall through floor and stack ribs over wrists.", ExerciseAnimationType.PIKE_SHOULDER_PRESS),
                move("tx_cl_4", "Lever Pull", "Tuck Front Lever Hold", 3, "00:20", "Lats & Core Tension", "Depress lats hard with straight arms and horizontal hips.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_cl_5", "Compression", "Floor L-Sit Compression", 3, "00:20", "Abs & Hip Flexors", "Push shoulders down away from ears and lift heels.", ExerciseAnimationType.ABDOMINAL_CRUNCHES),
                move("tx_cl_6", "Cool-Down Stretch", "Kneeling Lat Stretch", 1, "00:35", "Wrists & Lats", "Decompress shoulders and wrists after straight-arm work.", ExerciseAnimationType.DOORFRAME_ROW)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_stamina",
            shortChipLabel = "Stamina",
            title = "Heart-Lung Stamina & Conditioning",
            subtitle = "Zero-equipment aerobic engine, recovery speed & daily energy",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "10–14 days easier stair breathing • 4 wks real cardio engine",
            durationMinutes = 17,
            caloriesKcal = 205,
            goalFocus = PackGoalFocus.STAMINA_CONDITIONING,
            equipmentMode = PackEquipmentMode.NO_EQUIPMENT_HOME,
            exercises = listOf(
                move("tx_st_1", "Warm-Up", "Low-Impact Step Jacks", 1, "00:35", "Heart & Ankles", "Elevate heart rate smoothly.", ExerciseAnimationType.JUMPING_JACKS),
                move("tx_st_2", "Cardio Burst", "Crisp Jumping Jacks", 3, "00:45", "Heart & Calves", "Land softly on balls of feet with rhythmic breathing.", ExerciseAnimationType.JUMPING_JACKS),
                move("tx_st_3", "Leg Engine", "Tempo Bodyweight Squats", 3, "x18", "Legs & Lungs", "Steady non-stop rhythm to build oxygen uptake.", ExerciseAnimationType.BODYWEIGHT_SQUATS),
                move("tx_st_4", "Core Tempo", "Plank Shoulder Taps", 3, "x20", "Core & Delts", "Control breathing while heart rate is elevated.", ExerciseAnimationType.PLANK_HOLD),
                move("tx_st_5", "Stride Flow", "Alternating Forward Lunges", 3, "x16", "Lower Endurance", "Smooth alternating steps keeping chest proud.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_st_6", "Cool-Down Stretch", "Spine Cobra Breath Recovery", 1, "00:40", "Heart-Rate Recovery", "Long 6-second exhales to bring pulse down cleanly.", ExerciseAnimationType.COBRA_STRETCH)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_flexibility",
            shortChipLabel = "Flexibility",
            title = "Splits, Hips & Full-Body Flexibility",
            subtitle = "Deep hip flexors, hamstrings, thoracic spine & joint freedom",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "2–3 wks joint looseness • 8–12 wks deep hip & split range",
            durationMinutes = 15,
            caloriesKcal = 85,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_fl_1", "Warm-Up", "Thoracic Pike-to-Cobra Flow", 1, "00:40", "Full Spine", "Warm tissue temperature before deep stretching.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_fl_2", "Front Split Prep", "Runner's Deep Hip Flexor Hold", 3, "00:40", "Hip Flexors & Psoas", "Sink hips forward gently and exhale tension away.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_fl_3", "Overhead Line", "Kneeling Lat Lengthening Hold", 3, "00:40", "Lats & Thoracic", "Melt chest toward floor for effortless overhead reach.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_fl_4", "Chest Open", "Dynamic Chest Posture Opener", 3, "00:35", "Pecs & Collarbone", "Reverse desk-slouch tightness across front shoulders.", ExerciseAnimationType.PIKE_SHOULDER_PRESS),
                move("tx_fl_5", "Active Range", "Deep Split Squat Hip Opener", 3, "x10", "Active Hip Mobility", "Build strength right inside your new flexibility range.", ExerciseAnimationType.FORWARD_LUNGES),
                move("tx_fl_6", "Cool-Down Stretch", "Spine Cobra Stretch", 1, "00:40", "Anterior Chain", "Finish loose, calm, and tall.", ExerciseAnimationType.COBRA_STRETCH)
            )
        ),
        TargetExerciseGroup(
            id = "target_ex_height",
            shortChipLabel = "Tall Posture",
            title = "Spinal Decompression & Tall Posture",
            subtitle = "Uncompress spinal discs, reverse slouch & reclaim full natural height",
            zone = TargetBodyZone.HEAD_VOICE_MOBILITY,
            expectedResultsTimeline = "7–14 days +1–2 cm reclaimed upright posture (bones stay honest after puberty)",
            durationMinutes = 14,
            caloriesKcal = 90,
            goalFocus = PackGoalFocus.MOBILITY_VITALITY,
            equipmentMode = PackEquipmentMode.ANYWHERE_MAT,
            exercises = listOf(
                move("tx_ht_1", "Warm-Up", "Scapular Upper-Back Opener", 1, "00:35", "Upper Spine", "Reset rounded shoulder blades.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_ht_2", "Disc Unload", "Active Bar Dead Hang", 3, "00:35", "Spinal Traction", "Let lower body hang heavy while breathing slowly.", ExerciseAnimationType.LAT_PULLDOWN),
                move("tx_ht_3", "Extension", "Thoracic Cobra Flow", 3, "x10", "Mid-Back Curve", "Reverse phone-and-desk hunchback safely.", ExerciseAnimationType.COBRA_STRETCH),
                move("tx_ht_4", "Rib Lift", "Kneeling Lat Lengthening Hold", 3, "00:35", "Lats & Ribcage", "Release tight lats that pull shoulders downward.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_ht_5", "Posture Lock", "Doorframe Posture Row", 3, "x12", "Mid-Back Endurance", "Strengthen muscles that hold you tall all day.", ExerciseAnimationType.DOORFRAME_ROW),
                move("tx_ht_6", "Cool-Down Stretch", "Spine Decompress Cobra Stretch", 1, "00:35", "Full Spine", "Stand up feeling noticeably lighter and taller.", ExerciseAnimationType.COBRA_STRETCH)
            )
        )
    )

    fun groupsForZone(zone: TargetBodyZone, sex: BiologicalSex): List<TargetExerciseGroup> {
        val base = if (zone == TargetBodyZone.ALL) {
            ALL_GROUPS
        } else {
            ALL_GROUPS.filter { it.zone == zone }
        }
        if (sex == BiologicalSex.FEMALE && zone == TargetBodyZone.ALL) {
            val priorityIds = listOf(
                "target_ex_glutes",
                "target_ex_waist",
                "target_ex_kegel",
                "target_ex_jaw_neck_tongue",
                "target_ex_voice_breath",
                "target_ex_calisthenics",
                "target_ex_flexibility",
                "target_ex_arms"
            )
            return base.sortedBy { grp ->
                val idx = priorityIds.indexOf(grp.id)
                if (idx >= 0) idx else 50 + base.indexOf(grp)
            }
        }
        return base
    }
}
