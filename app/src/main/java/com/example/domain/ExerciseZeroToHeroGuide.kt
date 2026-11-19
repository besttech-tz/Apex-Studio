package com.example.domain

/**
 * Step in a 4-stage "0 -> Hero" mastery ladder for users who don't know how to perform
 * an exercise yet or failed mid-set and need an immediate, realistic progression path.
 */
data class ZeroToHeroLadderStep(
    val stepBadge: String,
    val title: String,
    val prescription: String,
    val coachingCue: String
)

/**
 * Complete biomechanical & beginner-rescue coaching guide for any exercise across
 * all targets, all goals, and both Male & Female profiles.
 */
data class ZeroToHeroCoachingGuide(
    val movementFamilyTitle: String,
    val plainPurpose: String = "",
    val whyPeopleStruggle: String,
    val setupSteps: List<String>,
    val breathingRule: String,
    val mistakeToAvoid: String,
    val zeroToHeroLadder: List<ZeroToHeroLadderStep>,
    val failedSetRescueTips: List<String>
)

object ExerciseZeroToHeroGuide {

    fun resolveGuide(
        exerciseName: String,
        primaryMuscle: String = "",
        sex: BiologicalSex = BiologicalSex.MALE,
        existingCues: List<String> = emptyList(),
        existingBreathing: String = "",
        existingMistake: String = ""
    ): ZeroToHeroCoachingGuide {
        val lower = exerciseName.trim().lowercase()
        val muscleLower = primaryMuscle.trim().lowercase()

        val baseGuide = when {
            // 0A. Calisthenics Elite Skill: Planche & Pseudo-Planche Push-Ups
            "planche" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Full Planche & Straight-Arm Strength",
                    whyPeopleStruggle = "Bending the elbows or letting shoulder blades pinch together kills straight-arm leverage and strains wrists.",
                    setupSteps = listOf(
                        "Turn parallettes or fingers ~45° outward so wrists stay comfortable as shoulders lean forward.",
                        "Lock elbows 100% straight and push the floor AWAY to dome your upper back (scapular protraction).",
                        "Tuck tailbone under (posterior pelvic tilt) and shift shoulders smoothly past your wrists."
                    ),
                    breathingRule = "Shallow rhythmic breaths behind a braced core—never relax shoulder protraction on exhale.",
                    mistakeToAvoid = "Micro-bending elbows or arching your lower back to fake lean angle.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Scapular Protracted Plank Hold",
                            prescription = "3 × 30 sec",
                            coachingCue = "Lock elbows straight and push upper back tall toward the ceiling."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • FORWARD LEVERAGE",
                            title = "Parallettes Planche Lean",
                            prescription = "4 × 15–20 sec",
                            coachingCue = "Lean shoulders 10–15 cm past wrists on toes while keeping arms bone-straight."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • FLOAT TUCK",
                            title = "Frog Stand / Tuck Planche Hold",
                            prescription = "4 × 10–15 sec",
                            coachingCue = "Compress knees tightly into chest and float feet cleanly off the floor."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Straddle / Full Parallettes Planche",
                            prescription = "4 × 8–12 sec",
                            coachingCue = "Extend hips horizontally with locked elbows, protracted shoulders, and pointed toes."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Wrists feeling pressure? Use push-up handles/parallettes instead of flat palms on the floor.",
                        "Elbows bending automatically? Reduce forward lean by 5 cm—100% straight arms build tendon strength.",
                        "Feet won't float yet? Hold a crisp toes-down Planche Lean—that exact angle builds your tuck planche."
                    )
                )
            }

            // 0B. Calisthenics Skill: Handstand & Handstand Walk
            "handstand" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Freestanding Handstand & Walk",
                    whyPeopleStruggle = "Looking forward with an arched banana-back shifts hips past balance instead of stacking wrists, shoulders, and hips.",
                    setupSteps = listOf(
                        "Grip the floor actively with spread fingertips ('spider fingers') shoulder-width apart.",
                        "Push shoulders UP into your ears (full elevation) and lock elbows completely straight.",
                        "Tuck ribs down and squeeze glutes so ankles, hips, shoulders, and wrists form one vertical line."
                    ),
                    breathingRule = "Calm nasal breaths while keeping lower ribs knitted down.",
                    mistakeToAvoid = "Kicking up wildly back-to-wall with a sagging lower back instead of controlled chest-to-wall alignment.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Elevated Pike Shoulder Hold",
                            prescription = "3 × 30 sec",
                            coachingCue = "Feet on a chair/couch with hips stacked directly above hands and locked arms."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • STACKED INVERSION",
                            title = "Chest-to-Wall Handstand Line Hold",
                            prescription = "4 × 25–35 sec",
                            coachingCue = "Walk feet up the wall facing it until nose and toes lightly kiss the wall."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • WEIGHT TRANSFER",
                            title = "Wall Handstand Shoulder Taps",
                            prescription = "4 × 12 alternating taps",
                            coachingCue = "Shift weight over one locked arm before lifting the opposite palm—prepares handstand walking."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Freestanding Handstand & Controlled Walk",
                            prescription = "4 × 15–25 sec",
                            coachingCue = "Press fingertips down to stop over-falling; bend palm heel slightly if under-balanced."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Afraid of falling over? Practice Chest-to-Wall walks and step down one foot at a time whenever tired.",
                        "Shoulders fatiguing fast? Drop feet onto a couch/box for Elevated Pike Holds to finish your set.",
                        "Wrists stiff? Warm up palms with 20s gentle wrist circles and grip hard through your fingertips."
                    )
                )
            }

            // 0C. Calisthenics Advanced Skill: Bar Muscle-Up (Explosive Pull & Straight-Bar Dip)
            "muscle-up" in lower || "muscle up" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Strict Bar Muscle-Up Mastery",
                    whyPeopleStruggle = "Pulling straight vertically into the bar blocks your chest—a muscle-up requires an arc pull around the bar to your lower sternum.",
                    setupSteps = listOf(
                        "Grip the bar slightly wider than shoulders with knuckles angled slightly over the top (semi-false grip).",
                        "Initiate a slight hollow swing forward, then pull explosively in a C-arc toward your lower ribs/navel.",
                        "Snap shoulders and chest forward OVER the bar at peak height and lock out a clean straight-bar dip."
                    ),
                    breathingRule = "Sharp powerful exhale as you pull and transition over the bar; inhale on controlled descent.",
                    mistakeToAvoid = "Chicken-winging one elbow over the bar at a time—risks shoulder and elbow tendon strain.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Explosive Chest-to-Bar Pull-Ups",
                            prescription = "4 × 6–8 explosive reps",
                            coachingCue = "Pull fast enough that your lower chest—not just your chin—reaches bar height."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • TOP LOCKOUT",
                            title = "Deep Straight-Bar Dip Lockout",
                            prescription = "4 × 8–10 reps",
                            coachingCue = "Start on top of a waist-high bar, lower chest to bar, and press cleanly to locked elbows."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • TRANSITION TURNOVER",
                            title = "Band-Assisted Bar Muscle-Up Transition",
                            prescription = "4 × 4–5 crisp reps",
                            coachingCue = "Loop a resistance band or use a low box jump to groove snapping both wrists over together."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Strict Unassisted Bar Muscle-Up",
                            prescription = "4 × 3–5 reps",
                            coachingCue = "Explosive hollow C-pull, simultaneous two-arm turnover, and full top lockout."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Getting stuck right at chest height? Pull slightly AWAY from the bar first, then whip head & shoulders forward over top.",
                        "Can't clear the bar today? Finish your set with Explosive High Pull-Ups to your sternum.",
                        "Never force a one-arm 'chicken wing'—keep both elbows symmetrical to protect your shoulders."
                    )
                )
            }

            // 0D. Calisthenics Advanced Skill: Front Lever
            "front lever" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Full Horizontal Front Lever",
                    whyPeopleStruggle = "Trying to bend the arms turns it into a row—front lever demands locked elbows and powerful straight-arm lat pushdown.",
                    setupSteps = listOf(
                        "Hang from a bar shoulder-width apart with locked elbows and active shoulder depression.",
                        "Drive straight arms DOWN toward your hips as if pushing the bar to your thighs.",
                        "Brace abs & glutes rock-solid so shoulders and hips float at the exact same horizontal height."
                    ),
                    breathingRule = "Short controlled breaths keeping lats and abs locked continuously.",
                    mistakeToAvoid = "Bending elbows, craning neck upward, or letting hips sag below shoulder height.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Active Scapular Depression Hang",
                            prescription = "3 × 25 sec",
                            coachingCue = "Hang with locked arms and pull shoulder blades firmly down away from ears."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • COMPACT LEVER",
                            title = "Tight Tuck Front Lever Hold",
                            prescription = "4 × 15–20 sec",
                            coachingCue = "Knees tucked to chest while your back floats completely flat and parallel to the floor."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • EXTENDED LEVER",
                            title = "Advanced Tuck / One-Leg Front Lever",
                            prescription = "4 × 10–12 sec",
                            coachingCue = "Push knees out to 90° or extend one leg straight while keeping hips horizontal."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Full Straight-Body Front Lever",
                            prescription = "4 × 6–10 sec",
                            coachingCue = "Full horizontal body line from shoulders to ankles with bone-straight elbows."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Hips dropping below shoulders? Pull one or both knees closer to your chest immediately to keep your back horizontal.",
                        "Elbows starting to bend? End the hold, shake out 15s, and regress one step—straight arms are non-negotiable.",
                        "No overhead bar at home? Use sturdy parallel chairs or table edge for Tuck Front Lever holds."
                    )
                )
            }

            // 0E. Calisthenics Elite Skill: Human Flag & Side Lever
            "human-flag" in lower || "human flag" in lower || "side-flag" in lower || "side lever" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Horizontal Human Flag Mastery",
                    whyPeopleStruggle = "Most athletes try to hold up with their top arm alone instead of pushing violently into the pole with a locked bottom arm.",
                    setupSteps = listOf(
                        "Top hand grips high overhead (pulling); bottom hand grips below shoulder height with thumb pointing DOWN (pushing).",
                        "Lock BOTH elbows 100% straight—push the pole away with your bottom shoulder while pulling with your top lat.",
                        "Stack hips and shoulders sideways facing forward without letting your torso twist toward the ground."
                    ),
                    breathingRule = "Steady braced breathing through clenched obliques and lats.",
                    mistakeToAvoid = "Bending the bottom elbow—a bent bottom arm instantly collapses your flag leverage.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "High-Tension Oblique Side Plank",
                            prescription = "3 × 35 sec / side",
                            coachingCue = "Drive bottom shoulder tall and lift hips high to build iron lateral core strength."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • POLE SUPPORT",
                            title = "Vertical Pole Push-Pull Support Hold",
                            prescription = "4 × 15–20 sec / side",
                            coachingCue = "Grip vertical pole/stall bar and float feet 30° off the ground with locked bottom arm."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • REDUCED LEVER",
                            title = "Tucked / Straddle Human Flag Hold",
                            prescription = "4 × 8–12 sec / side",
                            coachingCue = "Chamber knees in or open legs wide in straddle to cut rotational torque in half."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Full Horizontal Human Flag",
                            prescription = "4 × 5–8 sec / side",
                            coachingCue = "Legs together, locked bottom pushing arm, and horizontal body line parallel to the ground."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "No vertical pole today? Build the exact lateral chain with Star Side Planks (top leg lifted high).",
                        "Bottom wrist hurting? Point your bottom thumb straight DOWN the pole so wrist stacks cleanly under forearm.",
                        "Always train both left and right sides equally so obliques and shoulders develop symmetrically."
                    )
                )
            }

            // 0F. Calisthenics Skill: Parallettes / Floor L-Sit
            "l-sit" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Straight-Leg Parallettes L-Sit",
                    whyPeopleStruggle = "Shoulders shrug up to the ears and tight hamstrings pull the knees down when hip flexors cramp.",
                    setupSteps = listOf(
                        "Grip parallettes, dip bars, or sturdy chair edges right beside your mid-thighs (slightly forward of hips).",
                        "Lock elbows straight and press shoulders forcefully DOWN to lift your hips off the surface.",
                        "Point toes forward, lock quads tight, and lift straight legs to a crisp 90° L-angle."
                    ),
                    breathingRule = "Short calm breaths into upper chest while lower abs stay compressed.",
                    mistakeToAvoid = "Leaning shoulders backward or letting shoulders shrug up into your neck.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Seated Compression Leg Lifts",
                            prescription = "3 × 12 reps",
                            coachingCue = "Sit tall on floor with hands by knees and lift straight heels 10 cm off the mat."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • TUCK FLOAT",
                            title = "Two-Foot Tucked Parallettes Hold",
                            prescription = "4 × 15–20 sec",
                            coachingCue = "Push shoulders down and float tucked knees cleanly at hip height."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • SINGLE-LEG EXTENSION",
                            title = "One-Leg Extended L-Sit Switch",
                            prescription = "4 × 12–16 sec",
                            coachingCue = "Extend left leg straight for 6s, then switch to right leg straight for 6s."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Full Locked-Knee Parallettes L-Sit",
                            prescription = "4 × 15–25 sec",
                            coachingCue = "Both knees locked, toes pointed, and shoulders pressed tall away from ears."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Quads or hip flexors cramping? Shake legs out 10s, stretch hamstrings lightly, and switch to One-Leg L-Sit.",
                        "Arms feel too short on the floor? Elevate hands on parallettes, push-up bars, or two sturdy chairs.",
                        "Press hands slightly in front of your hip bones—placing hands behind hips makes lifting legs 2× harder."
                    )
                )
            }

            // 0G. Calisthenics Skill: Single-Leg Pistol Squat
            "pistol" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Full-Depth Single-Leg Pistol Squat",
                    whyPeopleStruggle = "Stiff ankles make you fall backward at the bottom before single-leg quad strength can take over.",
                    setupSteps = listOf(
                        "Root your standing foot flat (big toe, pinky toe, and heel glued down) and reach both arms straight forward as counterbalance.",
                        "Extend your non-working leg straight in front with quad locked and toes pulled up.",
                        "Sit hips smoothly down and forward over your mid-foot for 3 seconds, then drive straight up through your whole foot."
                    ),
                    breathingRule = "Inhale smoothly as you lower under control; exhale powerfully driving out of the bottom.",
                    mistakeToAvoid = "Dropping uncontrolled into the bottom bounce or letting your standing knee cave inward.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Deep Narrow Ankle-Mobility Squat",
                            prescription = "3 × 12 reps",
                            coachingCue = "Feet close together, sit all the way down keeping heels flat on the floor."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • TARGET DEPTH",
                            title = "Chair / Box Single-Leg Sit-to-Stand",
                            prescription = "3 × 8 reps / leg",
                            coachingCue = "Lower on one leg to touch a chair lightly without plopping, then stand right back up."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • ASSISTED BALANCE",
                            title = "Pole / Doorframe-Assisted Pistol Squat",
                            prescription = "4 × 6–8 reps / leg",
                            coachingCue = "Lightly hold a doorframe or pole with two fingers for ankle balance through full depth."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Unassisted Full-Depth Pistol Squat",
                            prescription = "4 × 5–8 reps / leg",
                            coachingCue = "Hamstring covers calf at bottom, heel stays flat, and front leg never touches the floor."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Falling backward at the bottom? Elevate your standing heel 2 cm on a folded mat edge or light plate while ankle mobility improves.",
                        "Front leg keeps dropping? Hold a light 2 kg bottle/book straight out in front as a natural counterbalance!",
                        "Knee wobbling? Use light fingertip support on a doorframe so your knee tracks straight over your middle toes."
                    )
                )
            }

            // 0H. Jawline, Neck & Tongue Posture (Mewing & Cervical Anti-Tech-Neck)
            "tongue" in lower || "mewing" in lower || "chin-tuck" in lower || "chin tuck" in lower ||
                "hyoid" in lower || "submental" in lower || "neck-flexor" in lower || "neck flexor" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Tall Cervical Neck, Jawline & Tongue Posture",
                    whyPeopleStruggle = "Clenching the back molars or tipping the head upward strains the jaw joint (TMJ) instead of lifting the back third of the tongue and deep neck flexors.",
                    setupSteps = listOf(
                        "Glide your ears straight back over your shoulders (gentle double-chin tuck) without tilting your nose up or down.",
                        "Flatten the ENTIRE tongue—especially the back third—gently against the roof of your mouth right behind your front teeth (never pushing teeth forward).",
                        "Keep lips sealed, teeth lightly resting (zero clenching), and breathe calmly through your nose."
                    ),
                    breathingRule = "100% quiet nasal breathing—your tongue stays suctioned gently to the palate as you inhale and exhale.",
                    mistakeToAvoid = "Biting your molars hard, jutting your lower jaw forward, or pressing the tongue tip against your front teeth.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Wall Cervical Chin-Tuck Alignment",
                            prescription = "3 × 20 sec",
                            coachingCue = "Back of head kisses the wall lightly while shoulders drop down away from ears."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • PALATE SUCTION",
                            title = "Swallow-and-Hold Full Tongue Roof Seal",
                            prescription = "3 × 30 sec",
                            coachingCue = "Swallow saliva once with lips closed and hold the natural suction of the whole tongue on the roof."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • DEEP NECK TONE",
                            title = "Supine Deep Neck-Flexor Chin Nod",
                            prescription = "3 × 12 slow reps",
                            coachingCue = "Lie flat, nod chin 2 cm inward, and hover head just 1 cm off the mat without neck strain."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Effortless All-Day Nasal + Palate Posture",
                            prescription = "4 × 45 sec + daily habit",
                            coachingCue = "Tall crown of head, crisp under-chin hyoid lift, zero jaw clenching during work or training."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Jaw joint feeling tight? Separate your molars by 2 mm immediately—tongue posture uses suction, never jaw biting!",
                        "Can't lift the back of your tongue? Say the sound 'SING' or 'KING'—the 'NG' sound places the back of your tongue right on the soft palate.",
                        "Front neck tiring fast on supine nods? Keep the back of your head sliding on the mat instead of lifting it into the air."
                    )
                )
            }

            // 0I. Diaphragm, Breath Control & Physical Voice Projection
            "diaphragmatic" in lower || "sibilant" in lower || "hiss" in lower ||
                "humming" in lower || "resonance" in lower || "box-breath" in lower ||
                "box breath" in lower || "co2" in lower || "intercostal" in lower || "ribcage" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Deep Diaphragm Stamina & Commanding Voice Power",
                    whyPeopleStruggle = "Shallow upper-chest breathing tightens the throat and neck, cutting off vocal resonance and causing early breathlessness.",
                    setupSteps = listOf(
                        "Stand or sit tall with relaxed shoulders so your lower ribcage can expand 360° sideways and forward.",
                        "Inhale silently through your nose into your lower ribs and belly—shoulders must NOT shrug upward.",
                        "Support every exhale or vocal sound smoothly from your lower abdominal wall while keeping your throat completely open and relaxed."
                    ),
                    breathingRule = "Silent 4-second nasal inhale into 360° lower ribs; steady controlled exhale without throat pinching.",
                    mistakeToAvoid = "Gasping through the mouth, shrugging collarbones up, or straining from your throat.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Lying 360° Belly-Rib Nasal Breathing",
                            prescription = "3 × 30 sec",
                            coachingCue = "One hand on chest (stays still), one hand on lower ribs/belly (rises smoothly)."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • AIRFLOW CONTROL",
                            title = "20-Second Steady Sibilant 'SSS' Hiss",
                            prescription = "3 × 20–25 sec",
                            coachingCue = "Release one rock-steady stream of air without wavering—trains true diaphragmatic vocal support."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • CHEST RESONANCE",
                            title = "Open-Throat Chest & Mask Humming ('MMM-AH')",
                            prescription = "3 × 30 sec",
                            coachingCue = "Feel warm vibration across your lips, nose bridge, and sternum with zero throat tension."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "4-4-6-2 Box Breath & 35s Supported Projection",
                            prescription = "4 × 40 sec",
                            coachingCue = "Calm CO2 tolerance, lower resting heart rate, and deep effortless voice projection."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Feeling lightheaded? Breathe more gently—never force aggressive over-breathing; slow and quiet wins.",
                        "Throat feeling scratchy on humming? Yawn gently first to drop the back of your tongue and open your airway.",
                        "Running out of air early on the 'SSS' hiss? Let less air escape through your teeth—aim for a soft, laser-even stream."
                    )
                )
            }

            // 1. Pull-Ups, Chin-Ups, Dead Hangs & Lat Pulldowns (Vertical Pull)
            "pull-up" in lower || "pullup" in lower || "chin-up" in lower ||
                "dead hang" in lower || "bar hang" in lower || "pulldown" in lower -> {
                val isMachinePulldown = "pulldown" in lower
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if (isMachinePulldown) {
                        "Lat Pulldown & V-Taper Control"
                    } else {
                        "0 → First Pull-Up & Grip Mastery"
                    },
                    whyPeopleStruggle = "Arms tire out before the wide back muscles (lats) ever turn on, or grip slips early.",
                    setupSteps = listOf(
                        if (isMachinePulldown) "Lock thighs snugly under the pad and grip slightly wider than shoulder-width."
                        else "Wrap thumbs firmly around the bar slightly wider than your shoulders.",
                        "Pull your shoulder blades DOWN away from your ears before bending your elbows.",
                        "Drive elbows straight down toward your ribs and pause 1 second at chest height."
                    ),
                    breathingRule = "Exhale firmly as you pull down/up; inhale slowly as your arms extend overhead.",
                    mistakeToAvoid = "Shrugging shoulders up by your ears or swinging your legs/lower back for momentum.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Active Bar Dead Hang + Scapular Shrugs",
                            prescription = "3 × 20–30 sec",
                            coachingCue = "Hang with straight arms and practice pulling shoulders down 3 cm without bending elbows."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • ASSISTED PULL",
                            title = "Doorframe / Elevated Inverted Rows",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Feet on the floor lets you pull 50% of your bodyweight while teaching clean lat squeeze."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • ECCENTRIC STRENGTH",
                            title = "5-Second Slow Lowering (Negative Pull-Up)",
                            prescription = "3 × 4–5 slow reps",
                            coachingCue = "Step up so chin starts above bar, then lower yourself as slowly as possible for 5 seconds."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if (isMachinePulldown) "Full Strict Lat Pulldown + 1s Chest Pause" else "Strict Dead-Hang Pull-Up",
                            prescription = "3–4 × 6–10 reps",
                            coachingCue = "Zero leg kick — pull collarbones to the bar and control all the way down."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Grip gave out first? Shake hands out 10s, wrap thumbs over the bar, and pull from your elbows—not your wrists.",
                        "Stuck halfway? Finish your remaining reps with 4-second slow negatives or switch to Doorframe/Cable Rows.",
                        "Biceps burning instead of back? Imagine driving your elbows into your back pockets rather than curling with hands."
                    )
                )
            }

            // 2. Kegel & Pelvic Floor Control (Men & Women)
            "kegel" in lower || "pelvic" in lower -> {
                val sexSpecificTip = if (sex == BiologicalSex.FEMALE) {
                    "Imagine gently lifting a marble upward and inward with your pelvic floor while keeping tummy & thighs relaxed."
                } else {
                    "Contract the exact muscle used to pause urine mid-stream—lift inward without clenching buttocks or holding breath."
                }
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Pelvic Floor & Kegel Isolation",
                    whyPeopleStruggle = "Most beginners accidentally squeeze their buttocks, inner thighs, or abs instead of isolating the deep pelvic sling.",
                    setupSteps = listOf(
                        "Lie comfortably with knees bent and feet flat so gravity isn't fighting your pelvic floor.",
                        sexSpecificTip,
                        "Fully relax and let the pelvic floor drop completely for 3 seconds between every squeeze."
                    ),
                    breathingRule = "Exhale gently as you lift the pelvic floor; inhale deeply to let the muscles fully release.",
                    mistakeToAvoid = "Bearing down/pushing outward or holding your breath—full relaxation between reps is just as important as the squeeze.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Lying Pelvic Awareness Squeeze",
                            prescription = "3 × 8 reps (2s hold)",
                            coachingCue = "Place one hand on belly to verify your stomach stays soft while your pelvic floor lifts."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • ENDURANCE HOLD",
                            title = "5-Second Controlled Kegel Hold + Full Release",
                            prescription = "3 × 8 reps (5s hold / 5s relax)",
                            coachingCue = "Keep breathing normally while holding a steady 70% inward lift."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • REFLEX PULSES",
                            title = "Quick Flick Contract-and-Drop Pulses",
                            prescription = "3 × 12 crisp 1s pulses",
                            coachingCue = "Trains fast-twitch pelvic control for coughing, jumping, and core bracing."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Seated / Standing Functional Kegel + Glute Bridge Sync",
                            prescription = "3–4 × 10s holds anywhere",
                            coachingCue = "Maintain clean pelvic control upright during daily life and heavy lifts."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Can't feel the right muscle? Lie flat, relax thighs completely, and use a gentle 50% effort—harder squeezing recruits wrong muscles.",
                        "Pelvic area feels tired or crampy? Stop squeezing and take 5 slow belly breaths to let the pelvic floor lengthen.",
                        "Holding your breath? Count out loud '1-2-3' during each hold so your diaphragm stays relaxed."
                    )
                )
            }

            // 3. Push-Ups, Bench Press, Incline Press & Chest Fly
            "push-up" in lower || "pushup" in lower ||
                (("press" in lower || "fly" in lower) && ("chest" in lower || "bench" in lower || "incline" in lower || "pec" in lower)) -> {
                val isGymPress = "dumbbell" in lower || "machine" in lower || "bench press" in lower || "pec" in lower
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if (isGymPress) "0 → Hero Chest Press & Shoulder-Safe Drive" else "0 → Hero Strict Push-Up Ladder",
                    whyPeopleStruggle = "Flaring elbows straight out to 90° strains front shoulders, or hips sag when core tension drops.",
                    setupSteps = listOf(
                        if (isGymPress) "Pin shoulder blades together against the bench pad and plant feet firmly."
                        else "Set hands slightly wider than shoulders and squeeze glutes so your body forms one straight plank.",
                        "Tuck elbows to a comfortable 45° arrow angle (never a wide T-shape).",
                        "Lower smoothly for 2 seconds, then press palms firmly to full arm extension."
                    ),
                    breathingRule = "Inhale smoothly as you lower; exhale strongly as you press away.",
                    mistakeToAvoid = "Dropping hips toward the floor or flaring elbows high toward your neck.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Wall or High-Counter Push-Ups",
                            prescription = "3 × 12 reps",
                            coachingCue = "Hands at chest height on a wall—builds painless pressing coordination with 25% load."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • INCLINE LEVERAGE",
                            title = "Bench / Bed-Edge Incline Push-Ups (or Light Machine Press)",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Hands on a sturdy bench or couch edge—trains full plank alignment at 50% load."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • ECCENTRIC / KNEE ASSIST",
                            title = "3-Second Lowering + Knee Push-Up Drive",
                            prescription = "3 × 8–10 reps",
                            coachingCue = "Lower slowly from toes for 3 seconds, tap knees gently, and press cleanly back up."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if (isGymPress) "Full-Range Controlled Incline / Chest Press" else "Strict Full-Range Floor Push-Ups",
                            prescription = "3–4 × 10–15 reps",
                            coachingCue = "Chest 3 cm from floor, rigid core, crisp lockout on every single rep."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Collapsed mid-set? Immediately elevate your hands onto a bench/bed edge (Incline Push-Up) to finish your reps cleanly!",
                        "Wrists hurting? Grip dumbbell handles or place palms slightly forward so wrists stay less bent.",
                        "Shoulders hurting instead of chest? Tuck elbows closer to your ribs (45°) and pinch shoulder blades back."
                    )
                )
            }

            // 4. Horizontal Rows, Scapular Openers & Rear Delts
            "row" in lower || "face pull" in lower || "rear-delt" in lower || "rear delt" in lower || "scapular" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Upper-Back & Posture Pull",
                    whyPeopleStruggle = "Pulling with hands and biceps while rounding shoulders forward leaves the upper back unworked.",
                    setupSteps = listOf(
                        "Stand or sit tall with proud chest and neutral neck.",
                        "Start every rep by gliding your shoulder blades back and down first.",
                        "Pull elbows past your ribs and pause 1 full second squeezing between your shoulder blades."
                    ),
                    breathingRule = "Exhale as you pull toward your torso; inhale slowly as your arms reach forward.",
                    mistakeToAvoid = "Jerking torso backward for momentum or shrugging traps toward your ears.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Standing Scapular Squeeze (No Weight)",
                            prescription = "3 × 12 reps (2s squeeze)",
                            coachingCue = "Learn to pinch a pencil between your shoulder blades without shrugging."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • SHALLOW ANGLE",
                            title = "Upright Doorframe Pulls (Small Lean)",
                            prescription = "3 × 12 reps",
                            coachingCue = "Keep feet just 20 cm forward so you master pure back tension with light resistance."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • FULL ROW",
                            title = "Deep Doorframe Row / Chest-Supported Row",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Walk feet further forward (or use moderate gym load) with a strict 1-second back pause."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Slow-Eccentric Row (3s Return)",
                            prescription = "4 × 10–12 reps",
                            coachingCue = "Control the return phase for 3 full seconds to build posture & V-taper density."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Forearms tiring first? Loosen your death-grip slightly and think of your hands as simple hooks.",
                        "On Doorframe/Inverted Rows, step your heels 15 cm closer to the door to instantly make the remaining reps 30% lighter.",
                        "Lower back tired? Brace chest against an incline bench or stagger one foot back for support."
                    )
                )
            }

            // 5. Dips & Tricep Pushdowns / Extensions
            "dip" in lower || "pushdown" in lower || "tricep" in lower || "pressdown" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Sleek Tricep & Arm Lockout",
                    whyPeopleStruggle = "Drifting hips far forward or dropping below 90° puts stress on front shoulders instead of triceps.",
                    setupSteps = listOf(
                        "Keep shoulders pressed down away from your ears and chest proud.",
                        "Point elbows straight back (or pinned tightly to your sides on cable pushdowns).",
                        "Stop lowering at a clean 90° elbow bend, then straighten arms fully to squeeze the back of your arms."
                    ),
                    breathingRule = "Inhale as elbows bend; exhale crisply as you lock your arms straight.",
                    mistakeToAvoid = "Dropping too deep past 90° on dips or swinging your torso on pushdowns.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Short-Range Floor Tricep Dips / Light Rope Pushdown",
                            prescription = "3 × 12 reps",
                            coachingCue = "Knees bent, feet flat close to hips—safe 45°–75° elbow bend with zero shoulder strain."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • BENT-KNEE BENCH DIP",
                            title = "Chair / Bench Dips (Knees Bent 90°)",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Keep your back skimming 3 cm from the chair edge and use legs lightly if needed."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • STRAIGHT-LEG / FULL TENSION",
                            title = "Extended-Leg Bench Dips / Pause Pushdowns",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Pause 1 full second at full arm lockout to sculpt the back of the arm."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Slow 3-Second Lowering Dips / Close-Grip Push-Ups",
                            prescription = "3–4 × 12 reps",
                            coachingCue = "Full tricep control with zero shoulder shrug."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Front of shoulder pinching on dips? Switch immediately to Floor Tricep Dips, Close-Grip Knee Push-Ups, or Cable Pushdowns.",
                        "Arms shaking mid-set? Walk your feet closer to your hips on dips so your legs assist 20% on the final reps.",
                        "Focus on the top 1-second straightening squeeze—that's where triceps tighten most."
                    )
                )
            }

            // 6. Shoulders: Pike Push-Ups, Overhead Press & Lateral Raises
            "pike" in lower || "shoulder press" in lower || "lateral raise" in lower -> {
                val isLateral = "lateral raise" in lower
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if (isLateral) "0 → Hero 3D Side-Shoulder Control" else "0 → Hero Overhead Shoulder Strength",
                    whyPeopleStruggle = if (isLateral) {
                        "Using heavy weight forces upper neck/traps to shrug instead of isolating side delts."
                    } else {
                        "Tight hamstrings or overarching the lower back makes overhead pressing feel awkward."
                    },
                    setupSteps = listOf(
                        if (isLateral) "Stand tall, soften elbows slightly, and relax your neck completely."
                        else "Brace abs tight so your lower back never arches.",
                        if (isLateral) "Sweep arms wide toward the walls—stop right at shoulder height."
                        else "Move elbows slightly forward (30° scapular plane) for smooth shoulder comfort.",
                        "Lower smoothly for 2 full seconds without dropping the weight."
                    ),
                    breathingRule = "Exhale as you press or raise; inhale slowly on the controlled descent.",
                    mistakeToAvoid = "Shrugging shoulders into your ears or swinging your hips to heave the load up.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = if (isLateral) "Bent-Elbow Side Raises (Light / No Weight)" else "High-Incline Pike / Wall Shoulder Press",
                            prescription = "3 × 12 reps",
                            coachingCue = "Short lever builds mind-muscle connection in delts with zero neck tension."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • CONTROLLED RANGE",
                            title = if (isLateral) "Light Dumbbell / Water-Bottle Lateral Raises" else "Bent-Knee Pike Push-Ups / Seated DB Press",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Bend knees on Pike Push-Ups if hamstrings feel tight so hips stay high."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • PAUSE REPS",
                            title = if (isLateral) "Lateral Raise with 1s Top Hold" else "Full Pike Shoulder Push-Up",
                            prescription = "3 × 10 reps",
                            coachingCue = "Zero momentum—own the top position smoothly."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if (isLateral) "Strict Cable / DB Lateral Raise (3s Negative)" else "Elevated Pike / Strict Overhead Press",
                            prescription = "3–4 × 10–12 reps",
                            coachingCue = "Crisp shoulder cap tension from first rep to last."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Neck feeling tight? Drop weight by 30% immediately and push your shoulders DOWN before lifting.",
                        "Pike Push-Up feels too heavy? Walk hands 20 cm forward into a High Incline Push-Up to finish your set.",
                        "Lower back arching? Squeeze glutes and exhale ribs down before pressing."
                    )
                )
            }

            // 7. Biceps, Hammer Curls & Forearms / Farmer Hold
            ("curl" in lower && "leg" !in lower && "hamstring" !in lower) || "farmer" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Arm & Forearm Strength",
                    whyPeopleStruggle = "Swinging elbows forward or rocking hips shifts tension off the biceps and forearms.",
                    setupSteps = listOf(
                        "Stand tall and pin your upper arms/elbows glued against your ribcage.",
                        "Curl smoothly using only your elbow hinge and squeeze 1 second at the top.",
                        "Lower all the way down for 2–3 seconds to a complete arm stretch."
                    ),
                    breathingRule = "Exhale as you curl up; inhale smoothly as you lower all the way down.",
                    mistakeToAvoid = "Cutting the bottom half of the rep short or leaning backward to heave weight.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Neutral Hammer Grip Curl (Light Load / Backpack)",
                            prescription = "3 × 12 reps",
                            coachingCue = "Thumbs-up hammer grip is the most wrist-friendly way to build arm & grip strength."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • STRICT PINNED CURL",
                            title = "Back-Against-Wall Strict Curl",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Stand with upper back against a wall so zero body swing can happen."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • SLOW ECCENTRIC",
                            title = "3-Second Slow Lowering Curl",
                            prescription = "3 × 10 reps",
                            coachingCue = "Fight gravity on the way down—this doubles arm growth & tendon strength."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Full-Stretch Incline / EZ-Bar Curl",
                            prescription = "3–4 × 10–12 reps",
                            coachingCue = "Full stretch at bottom, peak squeeze at top, zero elbow drift."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Stuck on final 2 reps? Rotate palms inward to a Hammer Grip (thumbs up)—your brachialis will help finish cleanly!",
                        "Wrists aching? Keep knuckles aligned straight with your forearm instead of curling wrists inward.",
                        "No dumbbells at home? Fill a sturdy backpack with books/water bottles and grip the top handle!"
                    )
                )
            }

            // 8. Glute Bridges, Hip Thrusts, Hip Abductions & Kickbacks (Booty, Hips & Posterior)
            "hip thrust" in lower || "glute bridge" in lower || "abductor" in lower ||
                "hip abduction" in lower || "kickback" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if ("abduct" in lower) {
                        "0 → Hero Outer Hip Curves & Side-Glute Sculpt"
                    } else {
                        "0 → Hero Glute Lift & Lower-Back-Safe Drive"
                    },
                    whyPeopleStruggle = "Arching the lower back or pushing through toes makes quads/lower back take over instead of glutes & hips.",
                    setupSteps = listOf(
                        if ("abduct" in lower) "Stand tall (or sit upright with slight forward lean) and brace your tummy flat."
                        else "Place shins vertical at the top—heels planted firmly where fingertips almost brush them.",
                        if ("abduct" in lower) "Lead outward with your heel (toes slightly inward) to hit the outer hip shelf directly."
                        else "Tuck your chin slightly and tilt pelvis slightly upward so ribs stay connected to hips.",
                        "Pause 2 full seconds at peak contraction squeezing glutes/outer hips—never hyperextend your spine."
                    ),
                    breathingRule = "Exhale sharply at the top 2-second glute squeeze; inhale smoothly lowering down.",
                    mistakeToAvoid = "Feeling it in your lower back (arch) or front thighs (feet too close) instead of your buttocks/hips.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = if ("abduct" in lower) "Lying Side-Hip Leg Raises (Clamshell / Side Lift)" else "Bodyweight Floor Glute Bridge + 3s Squeeze",
                            prescription = "3 × 15 reps",
                            coachingCue = "Zero spinal load—teaches your brain to fire glutes & outer hips before adding height or load."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • FULL RANGE",
                            title = if ("abduct" in lower) "Wall-Supported Standing Hip Abductions" else "Couch / Bench Bodyweight Hip Thrust",
                            prescription = "3 × 15 reps",
                            coachingCue = "Upper back on couch edge doubles glute stretch and roundness."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • PAUSE & PULSE",
                            title = if ("abduct" in lower) "1.5-Rep Hip Curve Abductions" else "1.5-Rep Glute Thrust / Bridge",
                            prescription = "3–4 × 12 reps",
                            coachingCue = "Go all the way up, lower halfway, push back up—intense hip & booty sculpt without heavy weights."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if ("abduct" in lower) "Weighted Abductor / Banded Hip Curves" else "Weighted Padded Hip Thrust / Single-Leg Bridge",
                            prescription = "4 × 10–15 reps",
                            coachingCue = "Maximum glute & hip shelf projection with locked-in pelvic control."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Feeling thighs (quads) instead of glutes? Walk your feet 5–8 cm further forward and lift your toes slightly inside your shoes!",
                        "Feeling hamstrings cramp? Move your feet 5 cm closer to your hips and press firmly through whole heels.",
                        "Lower back feeling tight? Keep chin tucked looking forward (not at ceiling) and stop hips at flat tabletop height."
                    )
                )
            }

            // 9. Split Squats, Bulgarian Split Squats & Lunges
            "split squat" in lower || "lunge" in lower || "bulgarian" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Knee-Safe Lunge & Balance Ladder",
                    whyPeopleStruggle = "Walking feet on a narrow 'tightrope' ruins balance, or pushing off toes stresses the front knee.",
                    setupSteps = listOf(
                        "Set feet hip-width apart like train tracks—never one foot directly behind the other.",
                        "Keep chest proud with a slight 10° forward torso lean (loads glutes & protects knees).",
                        "Lower straight down like an elevator, then push 80% of your weight through your FRONT heel."
                    ),
                    breathingRule = "Inhale lowering straight down; exhale driving firmly through your front heel.",
                    mistakeToAvoid = "Wobbling on a narrow stance or letting your front heel peel off the floor.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Hand-Supported Split Squat (Hold Wall or Chair)",
                            prescription = "3 × 10 reps / leg",
                            coachingCue = "Holding a wall removes 100% of balance fear and lets you choose a comfortable depth."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • STATIC SPLIT SQUAT",
                            title = "Stationary Split Squat (Feet Stay Planted)",
                            prescription = "3 × 12 reps / leg",
                            coachingCue = "Much friendlier on knees than stepping forward—just move smoothly up and down."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • REVERSE LUNGE",
                            title = "Controlled Step-Back Reverse Lunges",
                            prescription = "3 × 12 reps",
                            coachingCue = "Stepping backward keeps your front shin vertical and protects knee joints."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Weighted Dumbbell Lunges / Bulgarian Split Squat",
                            prescription = "3–4 × 10–12 reps",
                            coachingCue = "Deep athletic leg, hip, and glute strength with rock-solid stability."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Losing balance? Lightly touch two fingers against a wall, rack, or chair—you'll get 100% of the muscle benefit!",
                        "Front knee uncomfortable? Switch forward lunges to Stationary Split Squats or Reverse Lunges and shorten depth by 30%.",
                        "Legs burning out? Pause 3 breaths standing tall, then finish your final reps cleanly."
                    )
                )
            }

            // 10. Romanian Deadlifts (RDL) & Hip Hinges
            "romanian deadlift" in lower || "rdl" in lower || "hinge" in lower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Spine-Safe Hip Hinge (RDL)",
                    whyPeopleStruggle = "Bending from the lower waist instead of pushing hips straight backward strains the lower back.",
                    setupSteps = listOf(
                        "Unlock knees softly (15° bend) and freeze that knee angle.",
                        "Imagine closing a car door behind you with your hips while shaving weights close down your thighs.",
                        "Stop the instant your hips stop moving backward (usually mid-shin), then squeeze glutes to stand."
                    ),
                    breathingRule = "Brace abs and inhale as hips push back; exhale standing tall.",
                    mistakeToAvoid = "Rounding your spine to touch the floor—depth comes from hip push-back, NOT reaching down.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Wall Hip-Tap Drill (Bodyweight Hinge)",
                            prescription = "3 × 12 reps",
                            coachingCue = "Stand 20 cm in front of a wall and push hips back until buttocks tap the wall with flat back."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • HANDS-ON-THIGHS HINGE",
                            title = "Bodyweight RDL Sliding Palms to Knees",
                            prescription = "3 × 12–15 reps",
                            coachingCue = "Feel pure hamstring & glute stretch before adding external load."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • CONTROLLED DUMBBELL RDL",
                            title = "Light Dumbbell Romanian Deadlift (Stop Mid-Shin)",
                            prescription = "3 × 10–12 reps",
                            coachingCue = "Keep dumbbells glued against your legs—never let them drift forward."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Heavy Controlled RDL (3s Stretch Lowering)",
                            prescription = "4 × 8–12 reps",
                            coachingCue = "Elite hamstring & glute tie-in builder with bulletproof spinal bracing."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Feeling lower back instead of hamstrings? Stop lowering right below your knees and keep weights touching your legs!",
                        "Grip slipping? Switch to Floor or Dumbbell Glute Bridges to finish posterior-chain sets with zero grip limit.",
                        "Brace your stomach as if about to cough before hinging backward."
                    )
                )
            }

            // 11. Squats, Goblet Squats, Leg Press, Leg Extensions/Curls & Calves
            "squat" in lower || "leg press" in lower || "leg extension" in lower || "leg curl" in lower || "calf" in lower -> {
                val isCalf = "calf" in lower
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if (isCalf) "0 → Hero Lower-Leg & Ankle Strength" else "0 → Hero Deep Knee-Safe Squat Ladder",
                    whyPeopleStruggle = if (isCalf) {
                        "Bouncing fast at the ankles uses Achilles tendon recoil instead of working calf muscle fibers."
                    } else {
                        "Tight ankles make heels lift off the floor, or knees cave inward on the way up."
                    },
                    setupSteps = listOf(
                        if (isCalf) "Stand tall holding wall/machine support with balls of feet planted firmly."
                        else "Set feet shoulder-width apart with toes turned slightly outward (15°–20°).",
                        if (isCalf) "Rise smoothly onto big-toe mound and pause 1.5 seconds at peak height."
                        else "Sit hips back and down between your heels while keeping chest tall.",
                        if (isCalf) "Lower heels slowly for 2 seconds to a clean stretch."
                        else "Push knees gently outward in line with your toes and drive through mid-foot/heels."
                    ),
                    breathingRule = "Inhale on the lowering phase; exhale strongly driving up.",
                    mistakeToAvoid = if (isCalf) "Fast bouncy half-reps with zero pause at the top." else "Heels lifting off the floor or knees collapsing inward.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = if (isCalf) "Two-Foot Floor Calf Raise (Wall Support)" else "Chair / Box Tap Squat",
                            prescription = "3 × 12 reps",
                            coachingCue = if (isCalf) "Pause 2s at the top of every rep." else "Sit back until hips lightly touch a chair, then stand tall—100% safe & confidence-building."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • COUNTER-BALANCED SQUAT",
                            title = if (isCalf) "Slow-Tempo Calf Raises (2s Up / 2s Top / 2s Down)" else "Arms-Forward Bodyweight Squat",
                            prescription = "3 × 15 reps",
                            coachingCue = "Reaching arms forward acts as a natural counterbalance so your chest stays upright."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • PAUSE / GOBLET LOAD",
                            title = if (isCalf) "Elevated Step Calf Raises" else "Bottom-Pause Squat / Light Goblet Squat",
                            prescription = "3 × 12 reps",
                            coachingCue = "Holding 1s at parallel builds rock-solid knee and hip control."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if (isCalf) "Single-Leg / Weighted Calf Raise" else "Full-Depth Goblet Squat / 45° Leg Press",
                            prescription = "3–4 × 12–15 reps",
                            coachingCue = "Smooth, powerful leg & glute drive from bottom to top."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Heels lifting during squats? Widen your stance 5 cm and slide two small weight plates (or folded mat) under your heels!",
                        "Knees feeling stiff? Place a chair behind you and squat only to chair-tap depth until warm.",
                        "Lower back rounding at bottom? Stop 5 cm higher where your spine stays tall and proud."
                    )
                )
            }

            // 12. Planks, Crunches, Leg Raises & Core Vacuum
            "plank" in lower || "crunch" in lower || "leg raise" in lower || "knee raise" in lower || "vacuum" in lower || "deadbug" in lower -> {
                val isPlank = "plank" in lower || "vacuum" in lower
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = if (isPlank) "0 → Hero Flat Waist & Corset Plank" else "0 → Hero Neck-Safe Core & Lower Abs",
                    whyPeopleStruggle = if (isPlank) {
                        "Hips sag when abs tire, transferring stress straight to the lower back."
                    } else {
                        "Pulling on the back of the head strains the neck, or arched lower back makes hip flexors take over."
                    },
                    setupSteps = listOf(
                        if (isPlank) "Stack elbows directly beneath shoulders and squeeze glutes + quads tight."
                        else "Press your lower back flat into the mat FIRST so zero gap exists under your waist.",
                        if (isPlank) "Exhale and draw your belly button gently inward toward your spine like a corset."
                        else "Keep chin a fist-width from chest and curl ribs toward hips using pure stomach tension.",
                        "Breathe steadily—never hold your breath to fake core tension."
                    ),
                    breathingRule = "Exhale all air on the squeeze/brace to flatten deep transverse abs.",
                    mistakeToAvoid = if (isPlank) "Letting hips sag into a banana arch." else "Yanking on your neck with your hands or arching lower back off the mat.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = if (isPlank) "Elevated / Kneeling Forearm Plank" else "Bent-Knee Heel Slides / Mini Crunch",
                            prescription = if (isPlank) "3 × 20 sec" else "3 × 12 reps",
                            coachingCue = "Zero lower-back or neck strain—pure deep tummy engagement."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • CONTROLLED CORE",
                            title = if (isPlank) "Standard 30s Forearm Plank" else "Bent-Knee Reverse Crunches",
                            prescription = if (isPlank) "3 × 30 sec" else "3 × 12–15 reps",
                            coachingCue = "Knees bent keeps lever short so lower abs work without hip-flexor pain."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • EXTENDED TENSION",
                            title = if (isPlank) "Core Vacuum Plank / Shoulder Taps" else "Slow Lying Leg Raises (Knees Soft)",
                            prescription = if (isPlank) "3 × 40 sec" else "3 × 12 reps",
                            coachingCue = "Only lower legs as far as your lower back stays 100% glued to the floor."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = if (isPlank) "60s RKC High-Tension Plank" else "Strict Straight-Leg / Cable Crunch",
                            prescription = "3–4 sets",
                            coachingCue = "Complete waist & six-pack control."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Neck hurting on crunches? Cross arms over your chest or lightly touch ears—never lace fingers behind head!",
                        "Lower back arching on leg raises? Bend knees to 90° and stop heels 25 cm above the floor.",
                        "Shaking in plank? Gently drop knees to the mat for the final 10 seconds while keeping abs braced tight!"
                    )
                )
            }

            // 13. Cardio, Jumping Jacks & Stamina Intervals
            "jack" in lower || "jump" in lower || "pulse" in lower || "stamina" in muscleLower -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Stamina, Breathing & Joint-Safe Cardio",
                    whyPeopleStruggle = "Starting too fast spikes heart rate & shin impact in the first 15 seconds, causing early gasping.",
                    setupSteps = listOf(
                        "Keep knees soft and land whisper-quietly on the balls of your feet.",
                        "Lock into a smooth rhythmic breathing pattern from second 1.",
                        "Keep chest upright so lungs can expand fully."
                    ),
                    breathingRule = "Inhale through nose for 2 beats, exhale through mouth for 2 beats—never hold breath.",
                    mistakeToAvoid = "Landing stiff-legged on flat heels or sprinting the first 10 seconds.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Low-Impact Step Jacks (Zero Jumping)",
                            prescription = "3 × 30 sec",
                            coachingCue = "Step one foot out at a time while sweeping arms overhead—100% knee, ankle & apartment friendly."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • RHYTHM PACE",
                            title = "Smooth Conversational Jumping Jacks",
                            prescription = "3 × 35 sec",
                            coachingCue = "Soft, springy ankle bounce at a steady rhythm you can maintain cleanly."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • ATHLETIC ENGINE",
                            title = "45-Second Continuous Stamina Intervals",
                            prescription = "3–4 × 45 sec",
                            coachingCue = "Full overhead arm sweep to maximize heart-lung oxygen capacity."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "60-Second High-Output Conditioning + Fast Recovery",
                            prescription = "4 × 60 sec",
                            coachingCue = "Crisp athletic pace with calm nasal recovery between sets."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Out of breath mid-set? Switch instantly from jumping to Low-Impact Step-Outs while keeping arms moving—don't stop dead!",
                        "Calves or shins tight? Keep heels kissing the mat softly and shorten jump width.",
                        "Slow exhale for 4 counts during rest lowers heart rate twice as fast."
                    )
                )
            }

            // 14. Flexibility, Splits, Decompression & Posture Stretches
            else -> {
                ZeroToHeroCoachingGuide(
                    movementFamilyTitle = "0 → Hero Flexibility, Spine Length & Joint Unlocker",
                    whyPeopleStruggle = "Forcing a cold muscle into pain triggers the stretch reflex—making the muscle tighten up even harder.",
                    setupSteps = listOf(
                        "Ease slowly to a gentle 6/10 stretch tension—never sharp pain or bouncing.",
                        "Drop shoulders down away from your ears and unclench your jaw.",
                        "Sink 1–2 cm deeper ONLY as you exhale slowly."
                    ),
                    breathingRule = "Inhale 4 seconds through nose; exhale 6 slow seconds to signal nervous-system release.",
                    mistakeToAvoid = "Bouncing aggressively or holding your breath against tight hips/hamstrings.",
                    zeroToHeroLadder = listOf(
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 0 • ZERO START",
                            title = "Supported Shallow-Range Unlocker",
                            prescription = "2–3 × 25 sec",
                            coachingCue = "Use hands on blocks, chair, or thighs so stiff joints feel 100% supported and safe."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 1 • BREATH-LED RELEASE",
                            title = "35-Second Calm Exhale Hold",
                            prescription = "3 × 35 sec",
                            coachingCue = "Every slow 6-second exhale unlocks natural spine & hip length."
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 2 • CONTRACT-RELAX MOBILITY",
                            title = "5s Gentle Push + 25s Deeper Stretch (PNF)",
                            prescription = "3 × 30 sec",
                            coachingCue = "Gently tense the stretched muscle for 5s, then relax—instantly unlocks 15% more flexibility!"
                        ),
                        ZeroToHeroLadderStep(
                            stepBadge = "STEP 3 • HERO MASTERY",
                            title = "Full Unassisted Splits / Tall Decompressed Posture",
                            prescription = "Daily 45s flow",
                            coachingCue = "Fluid joint freedom, taller standing posture, and zero morning stiffness."
                        )
                    ),
                    failedSetRescueTips = listOf(
                        "Feeling pinching or trembling? Back out of the stretch by 20% immediately—gentle consistency beats forcing.",
                        "Knees sensitive on floor stretches? Fold your mat or place a pillow beneath your knee.",
                        "Unclench your hands and jaw—facial tension directly locks up neck, shoulder, and hip fascia."
                    )
                )
            }
        }

        return baseGuide.copy(
            plainPurpose = WeeklySchedulePlanner.plainPurposeForExercise(
                exerciseName = exerciseName,
                primaryMuscle = primaryMuscle
            ),
            setupSteps = if (existingCues.size >= 2) existingCues else baseGuide.setupSteps,
            breathingRule = existingBreathing.ifBlank { baseGuide.breathingRule },
            mistakeToAvoid = existingMistake.ifBlank { baseGuide.mistakeToAvoid }
        )
    }
}
