package com.example.domain

import com.example.data.local.TrainingEnvironment

enum class SessionStageType(
    val shortBadge: String,
    val sectionLabel: String,
    val phaseTitle: String,
    val phasePurposeSubtitle: String,
    val doneButtonLabel: String
) {
    PRE_WARMUP(
        shortBadge = "WARM-UP",
        sectionLabel = "Phase 1 • Pre-Workout Warm-Up",
        phaseTitle = "WARM-UP PREP",
        phasePurposeSubtitle = "Warming joints & raising blood flow before working sets",
        doneButtonLabel = "✓ DONE WARM-UP"
    ),
    MAIN_WORKOUT(
        shortBadge = "MAIN WORKOUT",
        sectionLabel = "Phase 2 • Main Workout",
        phaseTitle = "MAIN WORKOUT",
        phasePurposeSubtitle = "Primary muscle sculpting & progressive strength",
        doneButtonLabel = "✓ DONE SET"
    ),
    POST_STRETCH(
        shortBadge = "COOL-DOWN",
        sectionLabel = "Phase 3 • Cool-Down Stretch",
        phaseTitle = "COOL-DOWN STRETCH",
        phasePurposeSubtitle = "Slow breathing — releasing muscle tension after training",
        doneButtonLabel = "✓ DONE STRETCH"
    )
}

data class CoachRealityTruth(
    val id: String,
    val badge: String,
    val headline: String,
    val realityBody: String,
    val actionRule: String
)

data class FrequencyAdvice(
    val recommendedFrequency: WeeklyFrequency,
    val badgeTitle: String,
    val whyBestSummary: String,
    val selectedSplitNote: String
)

/**
 * Human-first exercise physiology advisor:
 * 1. Advises what weekly frequency & muscle combination is biologically best for the user's goal,
 *    while calibrating ANY chosen schedule (3, 4, 5, or 6 days/week).
 * 2. Prescribes environment-matched Pre-Workout Dynamic Warm-Ups (Gym Mobility vs Home Cardio Prep)
 *    and Post-Workout Static Posture Stretches.
 * 3. Surfaces no-nonsense Reality Truths users need most to actually transform.
 */
object CoachTruthAndSplitAdvisor {

    val REALITY_TRUTHS: List<CoachRealityTruth> = listOf(
        CoachRealityTruth(
            id = "truth_48h_recovery",
            badge = "REALITY #1 • MUSCLE GROWTH",
            headline = "Muscles Grow During 48h Recovery — Not While Lifting",
            realityBody = "Lifting weights only creates microscopic signals in muscle fibers. Actual size and firmness are built over the next 48 hours while you sleep and eat protein. Training the same muscle hard every single day blocks growth.",
            actionRule = "Follow our rotated muscle combinations and respect your 1–2 weekly rest days."
        ),
        CoachRealityTruth(
            id = "truth_muscle_synergy",
            badge = "REALITY #2 • SMART COMBINATIONS",
            headline = "Why Chest + Triceps & Back + Biceps Belong Together",
            realityBody = "Every chest press already works your triceps, and every back row already works your biceps. Separating Chest today and Arms tomorrow forces tired arms to fail early and strains elbow joints.",
            actionRule = "Pair pushing muscles together, pulling muscles together, and give legs + core their own day."
        ),
        CoachRealityTruth(
            id = "truth_belly_fat",
            badge = "REALITY #3 • STOMACH & ABS",
            headline = "A 45-Min 'Stomach-Only Day' Does Not Burn Belly Fat",
            realityBody = "Your body cannot spot-melt fat off your stomach from 200 crunches alone. Belly fat drops when large muscles (Legs, Back, Chest) burn heavy energy alongside a clean calorie target—while 8 minutes of core tightens the waist underneath.",
            actionRule = "Never skip Leg or Back day chasing abs; train big muscles + targeted core finishers."
        ),
        CoachRealityTruth(
            id = "truth_warmup_poststretch",
            badge = "REALITY #4 • WARM-UP & POST-STRETCH",
            headline = "Dynamic Motion Before Gym • Static Holds After Gym",
            realityBody = "Cold heavy lifting irritates shoulder and knee tendons, while skipping post-gym stretching leaves pumped chest and hip muscles shortened—pulling shoulders into a slouch and tilting your lower belly outward.",
            actionRule = "Spend 2–3 mins warming up joints first, and hold 30s static stretches after your final set."
        ),
        CoachRealityTruth(
            id = "truth_nutrition_math",
            badge = "REALITY #5 • FOOD & PROTEIN",
            headline = "4 Minutes of Liquid Sugar Cancels 45 Minutes of Lifting",
            realityBody = "One large sugary soda and fried snack adds 650+ calories with zero muscle repair. Without hitting your daily protein floor, a calorie deficit forces your body to burn muscle tissue and stay 'skinny-fat'.",
            actionRule = "Hit your protein gram target daily and keep liquid calories at zero."
        ),
        CoachRealityTruth(
            id = "truth_consistency_beats_burnout",
            badge = "REALITY #6 • HONEST CONSISTENCY",
            headline = "4–5 Honest Days Beat 6 Burnout Days Every Time",
            realityBody = "Most people pick 6 days out of excitement, get exhausted by Week 2, and quit. Busy day at work or stuck at home? A 6-minute At-Home Keep-Active session keeps muscle signals alive until tomorrow.",
            actionRule = "Pick a schedule you can repeat for 10 straight weeks—use Home Keep-Active on hectic days."
        )
    )

    fun adviseBestFrequency(
        direction: MetabolicDirection,
        experience: ExperienceLevel,
        selectedFrequency: WeeklyFrequency
    ): FrequencyAdvice {
        val recommended = when {
            experience == ExperienceLevel.NEW_BEGINNER ->
                WeeklyFrequency.FOUR_DAYS
            experience == ExperienceLevel.BEGINNER_JOINT_SAFE && direction == MetabolicDirection.CUT_FAT ->
                WeeklyFrequency.FOUR_DAYS
            else ->
                WeeklyFrequency.FIVE_DAYS
        }

        val whyBest = if (recommended == WeeklyFrequency.FOUR_DAYS) {
            "4 Days/Week gives beginners strong progress across all major muscles with 3 recovery days so joints and muscles adapt without soreness overload."
        } else {
            "5 Days/Week hits one clear muscle pair per workout with 2 dedicated recovery days for steady muscle and waist progress."
        }

        val selectedNote = when (selectedFrequency) {
            WeeklyFrequency.THREE_DAYS ->
                "3 Days Selected: Calibrated so every target muscle group still gets trained cleanly on a compact schedule."
            WeeklyFrequency.FOUR_DAYS ->
                "4 Days Selected: Ideal balance of focused workouts with 3 recovery days."
            WeeklyFrequency.FIVE_DAYS ->
                "5 Days Selected (★ Optimal Split): Maximum focus per session (~35m) with 2 full weekly recovery days."
            WeeklyFrequency.SIX_DAYS ->
                "6 Days Selected: High-frequency split rotation. Sleep 7.5h+ and protect your 1 mandatory rest day."
        }

        return FrequencyAdvice(
            recommendedFrequency = recommended,
            badgeTitle = "★ BEST FOR YOU: ${recommended.daysCount} DAYS / WEEK",
            whyBestSummary = whyBest,
            selectedSplitNote = selectedNote
        )
    }

    /**
     * Stage 1: Quick 2-movement Dynamic Pre-Workout Warm-Up (~2–3 mins) matched to today's muscles AND environment.
     * - GYM mode: Pure gym-floor dynamic mobility & joint activation (zero awkward living-room jumping jacks!).
     * - HOME mode: Low-Impact Step Jacks (Beginners) or Jumping Jacks + joint mobility opener.
     */
    fun getPreWorkoutWarmupSpecs(
        day: CombatDaySpec,
        experience: ExperienceLevel = ExperienceLevel.INTERMEDIATE,
        environment: TrainingEnvironment = TrainingEnvironment.HOME
    ): List<CombatExerciseSpec> {
        val titleOnly = day.title.lowercase()
        val lowerTitle = (day.title + " " + day.subtitle).lowercase()
        val isLowerDay = "leg" in lowerTitle || "glute" in lowerTitle || "quad" in lowerTitle ||
            "booty" in lowerTitle || "hip" in lowerTitle || "thigh" in lowerTitle
        val isShoulderDay = titleOnly.startsWith("shoulder") || "hourglass" in titleOnly
        val isPullDay = !isShoulderDay && ("back" in lowerTitle || "pull" in lowerTitle) &&
            "chest" !in lowerTitle
        val isChestPushDay = !isShoulderDay && "chest" in lowerTitle && "v-taper" !in lowerTitle
        val isNewBeginner = experience == ExperienceLevel.NEW_BEGINNER

        if (environment == TrainingEnvironment.GYM) {
            return when {
                isLowerDay -> listOf(
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_hip_squat",
                        orderIndex = 1,
                        name = "Dynamic Squat Opener",
                        modalityTag = "Hip & Knee Prep • 10 Reps",
                        targetMuscle = "Hips, Knees & Ankles",
                        primaryMuscle = "Hips & Knees",
                        secondaryMuscle = "Ankle Mobility",
                        totalSets = 1,
                        targetRepsCount = 10,
                        repPrescription = "10 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Sit hips down smoothly for 2s to lubricate knees and hips before leg sets.",
                        formTeacherCues = listOf(
                            "Set feet shoulder-width apart with toes turned slightly outward.",
                            "Lower smoothly to comfortable depth without bouncing.",
                            "Drive tall through mid-foot and squeeze glutes lightly at the top."
                        ),
                        startPositionLabel = "Tall stance by rack",
                        finishPositionLabel = "Smooth hip & knee opener"
                    ),
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_thoracic_hip",
                        orderIndex = 2,
                        name = "Thoracic Hip Mobility Flow",
                        modalityTag = "Spine & Pelvis Prep • 8 Reps",
                        targetMuscle = "Thoracic Spine & Hips",
                        primaryMuscle = "Core & Hip Mobility",
                        secondaryMuscle = "Lower Back",
                        totalSets = 1,
                        targetRepsCount = 8,
                        repPrescription = "8 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Hinge and flow smoothly to wake up your hips, core brace, and lower spine.",
                        formTeacherCues = listOf(
                            "Move at a calm, unhurried pace with steady breathing.",
                            "Keep ribs braced lightly as hips open and lengthen.",
                            "Finish feeling loose across hips and hamstrings."
                        ),
                        startPositionLabel = "Hip hinge position",
                        finishPositionLabel = "Open spine & hips"
                    )
                )

                isPullDay -> listOf(
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_scapular",
                        orderIndex = 1,
                        name = "Upper-Back Scapular Opener",
                        modalityTag = "Lat & Blade Prep • 12 Reps",
                        targetMuscle = "Upper Back & Shoulder Blades",
                        primaryMuscle = "Upper Back & Lats",
                        secondaryMuscle = "Rotator Cuff",
                        totalSets = 1,
                        targetRepsCount = 12,
                        repPrescription = "12 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Draw elbows back and pinch shoulder blades 1s so lats fire instead of arms.",
                        formTeacherCues = listOf(
                            "Keep neck long and shoulders away from your ears.",
                            "Pull shoulder blades together and down for 1 second.",
                            "Feel upper-back stabilizers switch on before your first row or pulldown."
                        ),
                        startPositionLabel = "Arms extended forward",
                        finishPositionLabel = "Shoulder blades pinched"
                    ),
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_pull_flow",
                        orderIndex = 2,
                        name = "Thoracic Spine Mobility Flow",
                        modalityTag = "Upper-Spine Extension • 8 Reps",
                        targetMuscle = "Thoracic Spine & Lats",
                        primaryMuscle = "Mid-Back Mobility",
                        secondaryMuscle = "Shoulders",
                        totalSets = 1,
                        targetRepsCount = 8,
                        repPrescription = "8 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Extend upper back smoothly so rounded desk posture unlocks before pulling.",
                        formTeacherCues = listOf(
                            "Exhale as your chest opens forward and shoulders glide down.",
                            "Never jerk your lower back — move smoothly through mid-spine.",
                            "Prepares full range of motion for lat pulldowns and rows."
                        ),
                        startPositionLabel = "Neutral spine",
                        finishPositionLabel = "Extended upper back"
                    )
                )

                isChestPushDay -> listOf(
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_chest_cuff",
                        orderIndex = 1,
                        name = "Dynamic Chest & Shoulder Opener",
                        modalityTag = "Cuff & Pec Prep • 10 Reps",
                        targetMuscle = "Rotator Cuff & Chest",
                        primaryMuscle = "Shoulders & Chest",
                        secondaryMuscle = "Elbows",
                        totalSets = 1,
                        targetRepsCount = 10,
                        repPrescription = "10 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Sweep arms open in a controlled arc to warm shoulder cuffs before pressing.",
                        formTeacherCues = listOf(
                            "Stand tall with ribs down and shoulders relaxed.",
                            "Sweep arms wide at chest height without jerking.",
                            "Feel warm blood flow into front shoulders and chest."
                        ),
                        startPositionLabel = "Arms in front of chest",
                        finishPositionLabel = "Open chest & warm cuffs"
                    ),
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_push_scap",
                        orderIndex = 2,
                        name = "Upper-Back Scapular Opener",
                        modalityTag = "Bench Stability • 12 Reps",
                        targetMuscle = "Shoulder Blades & Cuff",
                        primaryMuscle = "Scapular Stability",
                        secondaryMuscle = "Rear Delts",
                        totalSets = 1,
                        targetRepsCount = 12,
                        repPrescription = "12 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Pinch shoulder blades back and down — this protects shoulders on every chest press.",
                        formTeacherCues = listOf(
                            "Keep shoulders depressed away from your ears.",
                            "Hold each rear pinch for 1 crisp second.",
                            "Use this exact shoulder-blade tuck on your pressing bench."
                        ),
                        startPositionLabel = "Arms forward",
                        finishPositionLabel = "Locked pressing posture"
                    )
                )

                else -> listOf(
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_delt_flow",
                        orderIndex = 1,
                        name = "Thoracic Shoulder Mobility Flow",
                        modalityTag = "Overhead Prep • 8 Reps",
                        targetMuscle = "Shoulders & Upper Spine",
                        primaryMuscle = "Shoulder Mobility",
                        secondaryMuscle = "Core & Posture",
                        totalSets = 1,
                        targetRepsCount = 8,
                        repPrescription = "8 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Open upper spine and shoulder overhead range smoothly before shoulder & posture sets.",
                        formTeacherCues = listOf(
                            "Move smoothly through full shoulder elevation with ribs tucked.",
                            "Keep neck relaxed and breathe steadily.",
                            "Unlocks smooth overhead pressing and lateral raise mechanics."
                        ),
                        startPositionLabel = "Neutral tall stance",
                        finishPositionLabel = "Open overhead alignment"
                    ),
                    CombatExerciseSpec(
                        id = "${day.id}_warmup_gym_collarbone",
                        orderIndex = 2,
                        name = "Dynamic Chest & Posture Opener",
                        modalityTag = "Collarbone & Cuff • 10 Reps",
                        targetMuscle = "Collarbone & Rotator Cuff",
                        primaryMuscle = "Front & Side Delts",
                        secondaryMuscle = "Upper Back",
                        totalSets = 1,
                        targetRepsCount = 10,
                        repPrescription = "10 reps",
                        defaultLoadKg = 0f,
                        executionCue = "Sweep arms wide with thumbs slightly up to prime all three shoulder heads.",
                        formTeacherCues = listOf(
                            "Stand tall with core gently braced.",
                            "Open arms smoothly without shrugging traps.",
                            "Shoulders should feel warm and fluid before working sets."
                        ),
                        startPositionLabel = "Arms in front",
                        finishPositionLabel = "Wide open posture"
                    )
                )
            }
        }

        val pulseRaiser = if (isNewBeginner) {
            CombatExerciseSpec(
                id = "${day.id}_warmup_pulse",
                orderIndex = 1,
                name = "Low-Impact Step Jacks",
                modalityTag = "Joint-Safe Blood Flow • 30s",
                targetMuscle = "Heart Rate & Joint Warmth",
                primaryMuscle = "Zero-Jump Blood Flow",
                secondaryMuscle = "Shoulders & Hips",
                totalSets = 1,
                targetRepsCount = 30,
                repPrescription = "00:30",
                defaultLoadKg = 0f,
                executionCue = "Step one foot out at a time while sweeping arms overhead — zero jumping impact.",
                formTeacherCues = listOf(
                    "Step side-to-side smoothly without jumping—protects cold knees and ankles.",
                    "Sweep both arms overhead in rhythm to pump warm blood to shoulders and hips.",
                    "Breathe calmly through your nose."
                ),
                startPositionLabel = "Stand tall, zero jump",
                finishPositionLabel = "Side step + overhead reach"
            )
        } else {
            CombatExerciseSpec(
                id = "${day.id}_warmup_pulse",
                orderIndex = 1,
                name = "Jumping Jacks",
                modalityTag = "Dynamic Warm-Up • 40s",
                targetMuscle = "Heart Rate & Blood Flow",
                primaryMuscle = "Full-Body Warm-Up",
                secondaryMuscle = "Calves & Shoulders",
                totalSets = 1,
                targetRepsCount = 40,
                repPrescription = "00:40",
                defaultLoadKg = 0f,
                executionCue = "Stay light and rhythmic on the balls of your feet to warm joints before training.",
                formTeacherCues = listOf(
                    "Move at an easy 60% pace—this warms synovial fluid in your joints.",
                    "Breathe smoothly through your nose and relax your neck.",
                    "Land softly on the balls of your feet."
                ),
                startPositionLabel = "Stand tall, relaxed shoulders",
                finishPositionLabel = "Light rhythmic bounce"
            )
        }

        val jointOpener = when {
            isLowerDay -> CombatExerciseSpec(
                id = "${day.id}_warmup_mobility",
                orderIndex = 2,
                name = "Dynamic Squat Opener",
                modalityTag = "Joint Mobility • 10 Reps",
                targetMuscle = "Hips, Knees & Ankles",
                primaryMuscle = "Hips & Quads",
                secondaryMuscle = "Core Brace",
                totalSets = 1,
                targetRepsCount = 10,
                repPrescription = "10 reps",
                defaultLoadKg = 0f,
                executionCue = "Sit hips down smoothly for 2 seconds to open knees and hips.",
                formTeacherCues = listOf(
                    "Set feet shoulder-width apart with toes turned slightly out.",
                    "Lower smoothly to comfortable depth—never bounce at the bottom.",
                    "Drive tall through your whole foot and squeeze glutes lightly."
                ),
                startPositionLabel = "Tall stance",
                finishPositionLabel = "Smooth hip & knee opener"
            )

            isPullDay -> CombatExerciseSpec(
                id = "${day.id}_warmup_mobility",
                orderIndex = 2,
                name = "Upper-Back Scapular Opener",
                modalityTag = "Joint Mobility • 12 Reps",
                targetMuscle = "Shoulder Blades & Lats",
                primaryMuscle = "Upper Back & Rotator Cuff",
                secondaryMuscle = "Biceps Tendons",
                totalSets = 1,
                targetRepsCount = 12,
                repPrescription = "12 reps",
                defaultLoadKg = 0f,
                executionCue = "Pull shoulder blades back and down smoothly to wake up lats and elbows.",
                formTeacherCues = listOf(
                    "Keep shoulders away from your ears.",
                    "Pull elbows back and pinch shoulder blades for 1 second.",
                    "Extend arms smoothly to feel a gentle upper-back stretch."
                ),
                startPositionLabel = "Arms extended forward",
                finishPositionLabel = "Shoulder blades pinched"
            )

            isChestPushDay -> CombatExerciseSpec(
                id = "${day.id}_warmup_mobility",
                orderIndex = 2,
                name = "Dynamic Chest & Shoulder Opener",
                modalityTag = "Joint Mobility • 10 Reps",
                targetMuscle = "Shoulders, Chest & Elbows",
                primaryMuscle = "Shoulder Cuff & Chest",
                secondaryMuscle = "Triceps & Core",
                totalSets = 1,
                targetRepsCount = 10,
                repPrescription = "10 reps",
                defaultLoadKg = 0f,
                executionCue = "Sweep arms open smoothly to warm shoulder joints and chest fibers.",
                formTeacherCues = listOf(
                    "Stand tall with ribs down and neck relaxed.",
                    "Open arms wide in a smooth, controlled rhythm without bouncing.",
                    "Feel a gentle stretch across your chest and front shoulders."
                ),
                startPositionLabel = "Arms in front of chest",
                finishPositionLabel = "Smooth chest & shoulder opener"
            )

            else -> CombatExerciseSpec(
                id = "${day.id}_warmup_mobility",
                orderIndex = 2,
                name = "Thoracic Pike-to-Cobra Mobility Flow",
                modalityTag = "Spine & Shoulder Flow • 8 Reps",
                targetMuscle = "Shoulders, Spine & Core",
                primaryMuscle = "Thoracic & Shoulder Mobility",
                secondaryMuscle = "Core",
                totalSets = 1,
                targetRepsCount = 8,
                repPrescription = "8 reps",
                defaultLoadKg = 0f,
                executionCue = "Flow smoothly from hip pike to open chest to prime shoulders, posture, and abs.",
                formTeacherCues = listOf(
                    "Move calmly with your breath without rushing.",
                    "Press shoulders away from ears at the bottom.",
                    "Wakes up shoulders, wrists, and core simultaneously."
                ),
                startPositionLabel = "Inverted hip pike",
                finishPositionLabel = "Open chest & shoulders"
            )
        }

        return listOf(pulseRaiser, jointOpener)
    }

    /**
     * Stage 3: Targeted 2-hold Static Post-Workout Cooldown Stretch (~1.5–2 mins).
     * Lengthens pumped fascia, prevents rounded shoulders / anterior pelvic tilt, and kickstarts recovery.
     */
    fun getPostWorkoutStretchSpecs(day: CombatDaySpec): List<CombatExerciseSpec> {
        val lowerTitle = (day.title + " " + day.subtitle).lowercase()
        val isLowerDay = "leg" in lowerTitle || "glute" in lowerTitle || "quad" in lowerTitle || "booty" in lowerTitle || "hip" in lowerTitle || "thigh" in lowerTitle
        val isPullDay = "back" in lowerTitle || "pull" in lowerTitle

        val targetedStretch = when {
            isLowerDay -> CombatExerciseSpec(
                id = "${day.id}_stretch_primary",
                orderIndex = 1,
                name = "Hip Flexor Lunge Stretch",
                modalityTag = "Post-Gym Static Hold • 30s",
                targetMuscle = "Hip Flexors & Quads",
                primaryMuscle = "Hip Flexors & Quads",
                secondaryMuscle = "Glutes & Pelvis",
                totalSets = 1,
                targetRepsCount = 30,
                repPrescription = "00:30",
                defaultLoadKg = 0f,
                executionCue = "Step into a long lunge, tuck tailbone slightly, and hold 30s so tight hips don't tilt your belly forward.",
                formTeacherCues = listOf(
                    "Take a long split stance and gently lower your back knee.",
                    "Squeeze your back glute slightly to unlock the front of your hip.",
                    "Take slow 4-second exhales so the muscle releases tension."
                ),
                startPositionLabel = "Tall split stance",
                finishPositionLabel = "30s hip flexor release"
            )

            isPullDay -> CombatExerciseSpec(
                id = "${day.id}_stretch_primary",
                orderIndex = 1,
                name = "Kneeling Lat & Back Stretch",
                modalityTag = "Post-Gym Static Hold • 30s",
                targetMuscle = "Lats & Biceps",
                primaryMuscle = "Lats & Upper Back",
                secondaryMuscle = "Biceps & Rear Delts",
                totalSets = 1,
                targetRepsCount = 30,
                repPrescription = "00:30",
                defaultLoadKg = 0f,
                executionCue = "Reach arms forward, sink hips back gently, and breathe deeply into your lats for 30s.",
                formTeacherCues = listOf(
                    "Kneel comfortably and extend hands forward on the mat or bench.",
                    "Sit hips back gently until you feel your side-back lengthen.",
                    "Exhale slowly to drop nervous-system tension after pulling."
                ),
                startPositionLabel = "Hands extended forward",
                finishPositionLabel = "30s full lat decompression"
            )

            else -> CombatExerciseSpec(
                id = "${day.id}_stretch_primary",
                orderIndex = 1,
                name = "Overhead Chest & Tricep Stretch",
                modalityTag = "Post-Gym Static Hold • 30s",
                targetMuscle = "Chest & Triceps",
                primaryMuscle = "Chest, Shoulders & Triceps",
                secondaryMuscle = "Thoracic Spine",
                totalSets = 1,
                targetRepsCount = 30,
                repPrescription = "00:30",
                defaultLoadKg = 0f,
                executionCue = "Open your chest and triceps overhead for 30s so pumped fibers never pull your shoulders forward.",
                formTeacherCues = listOf(
                    "Stand or sit tall and draw elbow gently overhead.",
                    "Relax your neck and keep ribs down.",
                    "Hold steady for 30 seconds—zero bouncing."
                ),
                startPositionLabel = "Tall posture, arm raised",
                finishPositionLabel = "30s open-shoulder & tricep hold"
            )
        }

        val spineAndCoreDecompression = CombatExerciseSpec(
            id = "${day.id}_stretch_cobra",
            orderIndex = 2,
            name = "Cobra Spine & Abs Stretch",
            modalityTag = "Post-Gym Static Hold • 30s",
            targetMuscle = "Spine & Abdominal Wall",
            primaryMuscle = "Abdominal Wall & Spine",
            secondaryMuscle = "Chest & Hip Flexors",
            totalSets = 1,
            targetRepsCount = 30,
            repPrescription = "00:30",
            defaultLoadKg = 0f,
            executionCue = "Lift chest gently while keeping hips relaxed on the mat—lengthens your waist and decompresses your spine.",
            formTeacherCues = listOf(
                "Lie prone and press chest gently up while keeping shoulders down.",
                "Inhale deeply into your stomach to stretch tight abs.",
                "Exhale slowly for 30 seconds to switch your body into recovery mode."
            ),
            startPositionLabel = "Prone on mat",
            finishPositionLabel = "30s cobra posture lock"
        )

        return listOf(targetedStretch, spineAndCoreDecompression)
    }
}
