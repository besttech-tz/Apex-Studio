package com.example.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.IsoFields
import java.util.Locale

enum class PlanStartOption(
    val id: String,
    val label: String,
    val shortSub: String
) {
    START_TODAY("START_TODAY", "Start Today", "Today = Day 1"),
    START_TOMORROW("START_TOMORROW", "Start Tomorrow", "Tomorrow = Day 1"),
    START_MONDAY("START_MONDAY", "Start Monday", "Next Mon = Day 1");

    fun resolveDate(today: LocalDate = LocalDate.now()): LocalDate = when (this) {
        START_TODAY -> today
        START_TOMORROW -> today.plusDays(1)
        START_MONDAY -> if (today.dayOfWeek == DayOfWeek.MONDAY) {
            today
        } else {
            today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        }
    }

    companion object {
        private val SHORT_DATE_FMT = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)

        fun matchOptionOrNull(planStartDateIso: String, today: LocalDate = LocalDate.now()): PlanStartOption? {
            if (planStartDateIso.isBlank()) return START_TODAY
            val parsed = runCatching { LocalDate.parse(planStartDateIso) }.getOrNull() ?: return START_TODAY
            val nextMon = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
            return when (parsed) {
                today -> START_TODAY
                today.plusDays(1) -> START_TOMORROW
                nextMon -> START_MONDAY
                else -> null
            }
        }

        fun matchOption(planStartDateIso: String, today: LocalDate = LocalDate.now()): PlanStartOption {
            return matchOptionOrNull(planStartDateIso, today) ?: START_TODAY
        }

        fun formatPlanStartSummary(planStartDateIso: String, today: LocalDate = LocalDate.now()): String {
            val parsed = planStartDateIso.takeIf { it.isNotBlank() }
                ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?: today
            val dateText = parsed.format(SHORT_DATE_FMT)
            return when (parsed) {
                today -> "Starts Today ($dateText)"
                today.plusDays(1) -> "Starts Tomorrow ($dateText)"
                else -> "Starts $dateText"
            }
        }

        fun formatShortCustomDateLabel(planStartDateIso: String, today: LocalDate = LocalDate.now()): String {
            val parsed = planStartDateIso.takeIf { it.isNotBlank() }
                ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                ?: return "Pick Date"
            return parsed.format(SHORT_DATE_FMT)
        }
    }
}

data class WorkoutHistoryRecord(
    val id: String,
    val dateIso: String,
    val epochMs: Long,
    val title: String,
    val durationMinutes: Int,
    val setsCompleted: Int,
    val estimatedKcal: Int
) {
    fun encode(): String {
        val safeTitle = title.replace("|", "-").replace("\n", " ").trim()
        return "$id|$dateIso|$epochMs|$durationMinutes|$setsCompleted|$estimatedKcal|$safeTitle"
    }

    companion object {
        fun decode(raw: String): WorkoutHistoryRecord? {
            val parts = raw.split("|", limit = 7)
            if (parts.size < 7) return null
            val epoch = parts[2].toLongOrNull() ?: return null
            val mins = parts[3].toIntOrNull() ?: return null
            val sets = parts[4].toIntOrNull() ?: return null
            val kcal = parts[5].toIntOrNull() ?: return null
            return WorkoutHistoryRecord(
                id = parts[0],
                dateIso = parts[1],
                epochMs = epoch,
                durationMinutes = mins,
                setsCompleted = sets,
                estimatedKcal = kcal,
                title = parts[6]
            )
        }
    }
}

data class DayScheduleSlot(
    val dayOfWeekIso: Int, // 1 = Monday .. 7 = Sunday
    val shortLabel: String, // MON .. SUN
    val singleLetter: String, // M, T, W, T, F, S, S
    val fullDayName: String, // Monday .. Sunday
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isPastThisWeek: Boolean,
    val isRestDay: Boolean,
    val assignedSplitIndex: Int?,
    val assignedDaySpec: CombatDaySpec?,
    val shortPillTag: String, // Chest, Back, Legs, Arms, Upper, Glutes, Rest, —
    val outcomeTitle: String,
    val outcomeSubtitle: String,
    val isCompleted: Boolean,
    val isBeforePlanStart: Boolean = false,
    val dateIso: String = ""
)

/**
 * Maps a user's calibrated workout split across Monday..Sunday while respecting
 * user-chosen Training/Rest Days and the user's selected Plan Start Date.
 */
object WeeklySchedulePlanner {

    val ISO_DAYS: List<Pair<Int, String>> = listOf(
        1 to "Mon",
        2 to "Tue",
        3 to "Wed",
        4 to "Thu",
        5 to "Fri",
        6 to "Sat",
        7 to "Sun"
    )

    private val FULL_DAY_NAMES = mapOf(
        1 to "Monday",
        2 to "Tuesday",
        3 to "Wednesday",
        4 to "Thursday",
        5 to "Friday",
        6 to "Saturday",
        7 to "Sunday"
    )

    private val SHORT_LABELS = mapOf(
        1 to "MON",
        2 to "TUE",
        3 to "WED",
        4 to "THU",
        5 to "FRI",
        6 to "SAT",
        7 to "SUN"
    )

    private val SINGLE_LETTERS = mapOf(
        1 to "M",
        2 to "T",
        3 to "W",
        4 to "T",
        5 to "F",
        6 to "S",
        7 to "S"
    )

    /**
     * Default rest days per WeeklyFrequency:
     * - 5 Days/Week -> Friday (5) & Sunday (7) rest!
     * - 4 Days/Week -> Wednesday (3), Friday (5) & Sunday (7) rest
     * - 3 Days/Week -> Tuesday (2), Thursday (4), Friday (5) & Sunday (7) rest (Mon/Wed/Sat train)
     * - 6 Days/Week -> Sunday (7) rest
     */
    fun defaultRestDaysFor(frequency: WeeklyFrequency): Set<Int> {
        return when (frequency) {
            WeeklyFrequency.SIX_DAYS -> setOf(7)
            WeeklyFrequency.FIVE_DAYS -> setOf(5, 7)
            WeeklyFrequency.FOUR_DAYS -> setOf(3, 5, 7)
            WeeklyFrequency.THREE_DAYS -> setOf(2, 4, 5, 7)
        }
    }

    /**
     * Normalizes persisted rest days so they always stay within 1..7 and match
     * valid 3..6 workout days/week (i.e., 1..4 rest days).
     */
    fun normalizeRestDays(rawRestDays: Set<Int>, fallbackFrequency: WeeklyFrequency): Set<Int> {
        val valid = rawRestDays.filter { it in 1..7 }.toSortedSet()
        return if (valid.size in 1..4) {
            valid
        } else {
            defaultRestDaysFor(fallbackFrequency)
        }
    }

    /**
     * Ensures a target start day (e.g. Friday or Sunday when user picks "Start Today")
     * is an active Training Day while preserving the user's chosen weekly frequency.
     */
    fun ensureDayIsTrainingDay(
        currentRestDays: Set<Int>,
        dayIso: Int,
        fallbackFrequency: WeeklyFrequency
    ): Set<Int> {
        val normalized = normalizeRestDays(currentRestDays, fallbackFrequency).toMutableSet()
        if (dayIso !in 1..7 || dayIso !in normalized) return normalized.toSortedSet()
        normalized.remove(dayIso)
        val preferredRestCandidates = listOf(7, 5, 3, 4, 2, 6, 1)
        val replacement = preferredRestCandidates.firstOrNull { it != dayIso && it !in normalized }
        if (replacement != null && normalized.size < 4) {
            normalized.add(replacement)
        }
        return normalizeRestDays(normalized, fallbackFrequency)
    }

    /**
     * Toggles a single ISO day (1=Mon..7=Sun) between Train and Rest while keeping
     * between 1 and 4 rest days (so user always has 3..6 training days/week).
     */
    fun toggleRestDay(currentRestDays: Set<Int>, dayIso: Int, fallbackFrequency: WeeklyFrequency): Set<Int> {
        if (dayIso !in 1..7) return normalizeRestDays(currentRestDays, fallbackFrequency)
        val normalized = normalizeRestDays(currentRestDays, fallbackFrequency).toMutableSet()
        if (dayIso in normalized) {
            if (normalized.size > 1) {
                normalized.remove(dayIso)
            }
        } else {
            if (normalized.size < 4) {
                normalized.add(dayIso)
            } else {
                // Replace the earliest rest day other than Sunday so toggling never feels stuck
                val removable = normalized.firstOrNull { it != 7 } ?: normalized.first()
                normalized.remove(removable)
                normalized.add(dayIso)
            }
        }
        return normalized.toSortedSet()
    }

    fun frequencyForRestDays(restDays: Set<Int>): WeeklyFrequency {
        val workoutDays = (7 - restDays.count { it in 1..7 }).coerceIn(3, 6)
        return WeeklyFrequency.entries.firstOrNull { it.daysCount == workoutDays }
            ?: WeeklyFrequency.FOUR_DAYS
    }

    fun formatRestDaysSummary(restDays: Set<Int>): String {
        val sorted = restDays.filter { it in 1..7 }.sorted()
        if (sorted.isEmpty()) return "Sun"
        return sorted.joinToString(", ") { iso ->
            ISO_DAYS.first { it.first == iso }.second
        }
    }

    fun formatTrainDaysSummary(restDays: Set<Int>): String {
        val trainDays = (1..7).filter { it !in restDays }
        if (trainDays.isEmpty()) return "Mon, Wed, Fri"
        return trainDays.joinToString(", ") { iso ->
            ISO_DAYS.first { it.first == iso }.second
        }
    }

    /**
     * Computes consecutive active streak days. Scheduled rest days between completed workouts
     * protect the streak rather than breaking it.
     */
    fun computeStreakDays(
        completedDatesIso: Set<String>,
        restDaysIso: Set<Int> = emptySet(),
        today: LocalDate = LocalDate.now()
    ): Int {
        if (completedDatesIso.isEmpty()) return 0
        var cursor: LocalDate = when {
            today.toString() in completedDatesIso -> today
            today.minusDays(1).toString() in completedDatesIso -> today.minusDays(1)
            today.dayOfWeek.value in restDaysIso && today.minusDays(1).toString() in completedDatesIso -> today.minusDays(1)
            else -> return 0
        }
        var streak = 0
        var safety = 0
        while (safety < 366) {
            safety += 1
            val isoStr = cursor.toString()
            if (isoStr in completedDatesIso) {
                streak += 1
                cursor = cursor.minusDays(1)
            } else if (cursor.dayOfWeek.value in restDaysIso && cursor.minusDays(1).toString() in completedDatesIso) {
                cursor = cursor.minusDays(1)
            } else {
                break
            }
        }
        return streak
    }

    fun currentIsoRotationWeek(referenceDate: LocalDate = LocalDate.now()): Int {
        return referenceDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) % 2
    }

    fun shortMusclePillForDay(day: CombatDaySpec): String {
        val lower = day.title.lowercase()
        return when {
            "chest" in lower && "shoulder" in lower -> "Push"
            "chest" in lower -> "Chest"
            "glute" in lower -> "Glutes"
            "quad" in lower || "leg" in lower -> "Legs"
            "back" in lower && "rear" in lower -> "Pull"
            "back" in lower -> "Back"
            "shoulder" in lower && "arm" in lower -> "Arms"
            "shoulder" in lower -> "Shoulders"
            "upper" in lower || "v-taper" in lower -> "Upper"
            else -> "Train"
        }
    }

    /**
     * Returns clean, universally known gym split titles without verbose filler.
     */
    fun benefitHeadlineForDay(day: CombatDaySpec): String {
        val lower = day.title.lowercase()
        return when {
            "chest" in lower && "shoulder" in lower -> "Chest, Shoulders & Triceps"
            "chest" in lower && "tricep" in lower -> "Chest & Triceps"
            "back" in lower && "rear" in lower -> "Back & Biceps"
            "back" in lower && "bicep" in lower -> "Back & Biceps"
            "back" in lower && "arm" in lower -> "Back & Arms"
            "glute" in lower && "hamstring" in lower -> "Glutes & Hamstrings"
            "glute" in lower && "stomach" in lower -> "Glutes & Abs"
            "leg" in lower -> "Legs & Abs"
            "quad" in lower && "glute" in lower -> "Legs & Glutes"
            "shoulder" in lower && "arm" in lower -> "Shoulders & Arms"
            "shoulder" in lower && "upper chest" in lower -> "Shoulders & Chest"
            "v-taper" in lower || ("upper" in lower && "stomach" in lower) -> "Upper Body & Abs"
            "back" in lower && "shoulder" in lower -> "Back & Shoulders"
            "posture" in lower || "hourglass" in lower || ("shoulder" in lower && "core" in lower) -> "Shoulders & Core"
            else -> day.title
        }
    }

    fun shortFrequencyPromise(frequency: WeeklyFrequency): String {
        return when (frequency) {
            WeeklyFrequency.THREE_DAYS -> "3 workouts, 4 rest days  •  Steady full-body start"
            WeeklyFrequency.FOUR_DAYS -> "4 workouts, 3 rest days  •  Balanced weekly pace"
            WeeklyFrequency.FIVE_DAYS -> "5 workouts, 2 rest days  •  Fastest visible change"
            WeeklyFrequency.SIX_DAYS -> "6 workouts, 1 rest day  •  High commitment"
        }
    }

    /**
     * Crisp, familiar gym muscle tags for exercise rows.
     */
    fun whatExerciseDealsWith(exerciseName: String, primaryMuscle: String): String {
        val lower = "$exerciseName $primaryMuscle".lowercase()
        return when {
            "planche" in lower -> "Planche & Straight-Arm"
            "handstand" in lower -> "Handstand & Shoulders"
            "muscle-up" in lower || "muscle up" in lower -> "Muscle-Up Power"
            "front lever" in lower -> "Front Lever & Lats"
            "human-flag" in lower || "human flag" in lower || "side-flag" in lower || "side lever" in lower -> "Human Flag & Obliques"
            "l-sit" in lower -> "L-Sit & Hip Flexors"
            "pistol" in lower -> "Single-Leg Pistol"
            "tongue" in lower || "mewing" in lower || "hyoid" in lower || "submental" in lower -> "Jawline & Tongue Posture"
            "chin-tuck" in lower || "chin tuck" in lower || "neck-flexor" in lower || "neck flexor" in lower || "cervical" in lower -> "Neck & Cervical Posture"
            "diaphragmatic" in lower || "sibilant" in lower || "hiss" in lower || "humming" in lower || "resonance" in lower || "box-breath" in lower || "intercostal" in lower || "ribcage" in lower -> "Diaphragm & Voice Power"
            "kegel" in lower || "pelvic" in lower -> "Pelvic Floor"
            "dead hang" in lower || "dead-hang" in lower || "farmer" in lower || "forearm" in lower || "knuckle" in lower -> "Forearms & Grip"
            "incline" in lower && ("press" in lower || "push" in lower || "fly" in lower) -> "Upper Chest"
            "pulldown" in lower || "pull-up" in lower || "pullup" in lower -> "Lats (Back)"
            "face pull" in lower || "rear" in lower -> "Rear Shoulders"
            "row" in lower -> "Mid-Back"
            "close-grip" in lower && "push" in lower -> "Triceps & Chest"
            "chest" in lower || "push-up" in lower || "bench press" in lower || "pec" in lower -> "Chest"
            "lateral raise" in lower || "pike" in lower -> "Side Shoulders"
            "shoulder press" in lower -> "Shoulders"
            "hammer" in lower -> "Biceps & Forearms"
            "curl" in lower || "bicep" in lower -> "Biceps"
            "tricep" in lower || "dip" in lower || "pushdown" in lower -> "Triceps"
            "hip thrust" in lower || "glute" in lower || "abductor" in lower || "kickback" in lower -> "Glutes"
            "squat" in lower || "leg press" in lower || "leg extension" in lower -> "Quads & Legs"
            "hamstring" in lower || "deadlift" in lower || "leg curl" in lower -> "Hamstrings"
            "lunge" in lower -> "Legs & Glutes"
            "calf" in lower -> "Calves"
            "plank" in lower -> "Core & Abs"
            "crunch" in lower || "knee raise" in lower || "leg raise" in lower -> "Abs"
            "cobra" in lower || "stretch" in lower -> "Stretching & Flexibility"
            "grip" in lower -> "Forearms & Grip"
            else -> primaryMuscle
        }
    }

    /**
     * Plain-English 1-line explanation of WHAT an exercise actually does for your body —
     * including context-aware explanation when compound leg/back exercises appear inside
     * Belly-Fat / Metabolic / Slimming circuits so users never wonder why a move is there.
     */
    fun plainPurposeForExercise(
        exerciseName: String,
        primaryMuscle: String,
        workoutContextTitle: String = ""
    ): String {
        val lower = "$exerciseName $primaryMuscle".lowercase()
        val ctx = workoutContextTitle.lowercase()
        val isFatBurnContext = "belly" in ctx || "fat" in ctx || "shred" in ctx ||
            "slim" in ctx || "metabolic" in ctx || "lean" in ctx

        return when {
            "pseudo-planche" in lower ->
                "Shifts bodyweight forward over wrists to build straight-arm & shoulder strength for full planche"
            "planche" in lower ->
                "Builds elite straight-arm shoulder & scapular protraction strength for horizontal planche"
            "handstand" in lower && "tap" in lower ->
                "Trains single-arm overhead weight shifts so you can balance and walk on your hands"
            "handstand" in lower ->
                "Builds stacked overhead shoulder balance, wrist control & inverted core stability"
            ("muscle-up" in lower || "muscle up" in lower) && "dip" in lower ->
                "Strengthens the top-of-bar press-out phase needed to finish a clean bar muscle-up"
            "muscle-up" in lower || "muscle up" in lower ->
                "Develops explosive waist-high pulling power to transition cleanly above the bar"
            "front lever" in lower ->
                "Builds straight-arm lat depression & rigid core tension to hold your body horizontal under the bar"
            "human-flag" in lower || "human flag" in lower || "side-flag" in lower || "side lever" in lower ->
                "Trains lateral oblique, bottom-arm push & top-arm pull tension for the horizontal human flag"
            "l-sit" in lower ->
                "Builds shoulder depression, quad compression & rock-solid abs to float straight legs at 90°"
            "pistol" in lower ->
                "Develops elite single-leg strength, ankle mobility & knee stability using pure bodyweight"
            "tongue" in lower || "mewing" in lower ->
                "Trains full-tongue roof posture & nasal breathing to support under-chin tone & neck alignment"
            "chin-tuck" in lower || "chin tuck" in lower || "neck-flexor" in lower || "neck flexor" in lower ->
                "Fixes forward 'tech-neck' head drift & strengthens deep cervical posture muscles"
            "hyoid" in lower || "submental" in lower ->
                "Tones under-chin hyoid musculature & sharpens upright jawline-neck angle"
            "sibilant" in lower || "hiss" in lower ->
                "Builds steady diaphragmatic air-pressure control for a strong, unwavering speaking voice"
            "humming" in lower || "resonance" in lower ->
                "Unlocks chest & nasal vocal resonance without straining or tightening your throat"
            "diaphragmatic" in lower || "box-breath" in lower || "co2" in lower || "intercostal" in lower || "ribcage" in lower ->
                "Expands 360° lung capacity, calms heart rate & boosts oxygen stamina"
            "kegel" in lower || ("pelvic" in lower && "tilt" !in lower) ->
                "Strengthens discreet pelvic-floor muscles for core control, bladder support & stamina"
            "dead hang" in lower || "doorway reach" in lower ->
                "Decompresses your spine to stand taller & builds iron grip for pull-ups"
            "farmer" in lower ->
                "Builds crushing hand grip, strong traps & upright core posture"
            "knuckle" in lower ->
                "Bulletproofs wrists, hands & full-body calisthenics tension"
            "scapular" in lower || "posture opener" in lower || "chest & posture" in lower || "chest & shoulder" in lower ->
                "Opens rounded shoulders & chest so you stand taller with zero neck stiffness"
            "pike-to-cobra" in lower || "spine & hip flow" in lower ->
                "Loosens stiff back, hips & stomach muscles before active work"
            "cobra" in lower ->
                "Stretches tight stomach & decompresses lower-back stiffness"
            "lat" in lower && "stretch" in lower ->
                "Releases tight upper-back & shoulder tension so arms move freely"
            ("lunge" in lower || "runner" in lower || "split" in lower) && ("stretch" in lower || "unlocker" in lower || "opener" in lower || "hold" in lower) ->
                "Unlocks stiff sitting hips & inner thighs to improve split flexibility & posture"
            "tricep" in lower && "stretch" in lower ->
                "Lengthens tight shoulders & back of arms after pressing"
            "pull-up" in lower || "pullup" in lower || "chin-up" in lower ->
                "Master bodyweight upper-body pulling for a wide V-taper back & strong arms"
            "pulldown" in lower ->
                "Widens your upper back (lats) so your waist looks smaller"
            "inverted row" in lower || "table" in lower && "row" in lower || "doorframe" in lower ->
                "Pulls shoulders back for tall posture & builds bodyweight pulling strength"
            "face pull" in lower ->
                "Fixes slouched posture & sculpts rear shoulders safely"
            "row" in lower ->
                if (isFatBurnContext) {
                    "Works large back muscles to boost calorie burn while improving tall posture"
                } else {
                    "Builds thick, toned upper-back muscles & strong posture"
                }
            "incline" in lower && ("press" in lower || "push" in lower) ->
                if (isFatBurnContext) {
                    "Keeps upper chest & arms firm and toned while your body burns fat"
                } else {
                    "Fills upper chest & collarbone line while toning front shoulders & arms"
                }
            "decline push" in lower || "diamond push" in lower || "close-grip push" in lower ->
                "Calisthenics upper-body builder for strong chest, shoulders & defined triceps"
            "wall push" in lower ->
                "Joint-gentle upper-body press that tones arms & chest with zero wrist strain"
            "push-up" in lower || "chest press" in lower || "bench press" in lower || "pec fly" in lower ->
                "Builds firm chest definition, pressing strength & toned triceps"
            "pike" in lower && ("push" in lower || "press" in lower) ->
                "Calisthenics bodyweight press that builds strong, capped shoulders without weights"
            "lateral raise" in lower ->
                "Sculpts outer shoulder caps to create a wider V-taper / narrower waist look"
            "shoulder press" in lower ->
                "Builds strong, rounded shoulders & overhead pressing power"
            "dip" in lower || "pushdown" in lower || "tricep" in lower ->
                "Tightens and defines the back of your arms (triceps) for firm lockout"
            "hammer" in lower || "reverse-grip" in lower || "forearm curl" in lower ->
                "Thickens forearms, wrists & outer arm width"
            "curl" in lower ->
                "Shapes front arm fullness (biceps) & pulling strength"
            "hip thrust" in lower || "glute bridge" in lower ->
                "Directly lifts, rounds & firms your glutes without stressing knees"
            "abduct" in lower ->
                "Shapes outer hip curves & stabilizes hip/knee joints"
            "kickback" in lower ->
                "Targets upper glute shelf for a lifted lower-body profile"
            "romanian deadlift" in lower || "rdl" in lower || "hinge" in lower ->
                "Lengthens tight hamstrings, lifts glutes & strengthens your posterior chain"
            "split squat" in lower || "bulgarian" in lower || "lunge" in lower ->
                if (isFatBurnContext) {
                    "Works single-leg & glute muscles to torch calories and tone thighs"
                } else {
                    "Builds balanced single-leg strength, hip stability & sculpted thighs/glutes"
                }
            "calf" in lower ->
                "Strengthens lower legs & ankles for balance and athletic spring"
            "squat" in lower || "leg press" in lower || "leg extension" in lower ->
                if (isFatBurnContext) {
                    "Fires your largest leg & glute muscles to burn 3× more calories & melt belly fat faster"
                } else {
                    "Builds strong thighs, firm glutes & full-body lower power"
                }
            "vacuum" in lower ->
                "Trains your deep inner corset muscle to pull your waistline naturally flatter"
            "reverse crunch" in lower || "leg raise" in lower || "knee raise" in lower || "deadbug" in lower ->
                "Directly flattens stubborn lower belly & strengthens lower abs"
            "shoulder tap" in lower ->
                "Tightens waist & obliques while keeping heart rate in the fat-burn zone"
            "plank" in lower || "hollow" in lower || "l-sit" in lower ->
                "Tightens your entire core wall & protects your lower back"
            "crunch" in lower ->
                "Sculpts visible upper abs & firms your front stomach wall"
            "jack" in lower || "step-out" in lower ->
                "Raises heart rate & warms joints smoothly to kickstart calorie burn"
            else ->
                "Builds targeted strength, joint control & muscle tone in $primaryMuscle"
        }
    }

    /**
     * Short 1-sentence coaching note shown on specialty/challenge workout briefings so
     * beginners immediately understand WHY the exercises in that pack were chosen together.
     */
    fun plainWhyWorkoutWorks(workoutTitle: String): String? {
        val lower = workoutTitle.lowercase()
        return when {
            "belly" in lower || "metabolic" in lower || ("slim" in lower && "circuit" in lower) || "zero-jump" in lower ->
                "Why legs + upper + abs? Crunches alone don't burn belly fat — working large leg & back muscles burns 3× more calories while core moves flatten your waist."
            "stage 1" in lower && "calisthenics" in lower ->
                "Stage 1 Beginner Calisthenics: Masters hollow push-ups, assisted bar pulls, deep squats & planks—the foundation for every advanced skill."
            "stage 2" in lower && "calisthenics" in lower ->
                "Stage 2 Intermediate Calisthenics: Unlocks strict pull-ups, parallel bar dips, hanging leg raises & wall handstand control."
            "stage 3" in lower && "calisthenics" in lower ->
                "Stage 3 Advanced Calisthenics: Builds explosive bar muscle-up power, handstand walking weight shifts & horizontal front-lever tension."
            "stage 4" in lower && "calisthenics" in lower ->
                "Stage 4 Elite Calisthenics: Peak gymnastics & street-workout mastery—parallettes planche, human-flag side lever, straight-leg L-sit & pistol squats."
            "28-day" in lower && ("planche" in lower || "calisthenics" in lower) ->
                "4-Week Progressive Skill Blueprint: Advances week-by-week from Stage 1 foundation to Handstand, Muscle-Up, Front Lever, Planche & Human Flag."
            "handstand" in lower && "l-sit" in lower ->
                "Pairs overhead handstand stacking with parallettes L-sit compression & pistol balance for total bodyweight control."
            "calisthenics" in lower ->
                "True Calisthenics: Uses bodyweight leverage & gravity to build real relative strength, skill control, balance & coordination."
            "flexibility" in lower || "stretching" in lower ->
                "Releases tight neck, shoulders, spine, hips & hamstrings in one smooth flow so your body feels loose, flexible & pain-free."
            "height" in lower || "taller" in lower ->
                "Decompresses spinal discs, opens slouched shoulders & aligns hip tilt so you reclaim your full natural standing height."
            "kegel" in lower || "pelvic" in lower ->
                "Pairs discreet pelvic-floor squeezes with full breath release & glute support for real core and pelvic vitality."
            "hips" in lower || "booty" in lower || "glute" in lower || "hourglass" in lower ->
                "Targets all 3 glute & outer-hip angles (thrust, side-abduction & hinge) to lift and shape curves without bulking your waist."
            "pull-up" in lower ->
                "Builds grip hang, shoulder-blade pull & back strength step-by-step so anyone can progress from 0 to strict pull-ups."
            "stamina" in lower ->
                "Keeps large muscle groups moving with short rests to expand heart-lung endurance and all-day energy."
            "jawline" in lower || "tongue" in lower || "neck" in lower ->
                "Corrects forward head posture, strengthens deep cervical flexors & trains proper palate tongue posture—zero jaw clenching."
            "voice" in lower || "diaphragm" in lower || "breath" in lower ->
                "Trains 360° ribcage expansion, steady diaphragmatic airflow & open-throat chest resonance for stronger voice and lung stamina."
            else -> null
        }
    }

    fun buildWeekSchedule(
        splitDays: List<CombatDaySpec>,
        restDaysIso: Set<Int>,
        completedSetKeys: Set<String> = emptySet(),
        referenceDate: LocalDate = LocalDate.now(),
        planStartDateIso: String? = null,
        activeSplitIndex: Int = 0
    ): List<DayScheduleSlot> {
        val fallbackFreq = WeeklyFrequency.entries.firstOrNull { it.daysCount == splitDays.size }
            ?: WeeklyFrequency.FOUR_DAYS
        val normalizedRest = normalizeRestDays(restDaysIso, fallbackFreq)
        val monday = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val parsedStartDate = planStartDateIso
            ?.takeIf { it.isNotBlank() }
            ?.let { raw -> runCatching { LocalDate.parse(raw) }.getOrNull() }

        var workoutCounter = if (parsedStartDate == null || splitDays.isEmpty()) {
            0
        } else {
            val priorTrainDaysInWeek = (1..7).count { i ->
                val d = monday.plusDays((i - 1).toLong())
                !d.isBefore(parsedStartDate) && d.isBefore(referenceDate) && i !in normalizedRest
            }
            val size = splitDays.size
            (((activeSplitIndex - priorTrainDaysInWeek) % size) + size) % size
        }

        return (1..7).map { iso ->
            val date = monday.plusDays((iso - 1).toLong())
            val isToday = date == referenceDate
            val isPast = date.isBefore(referenceDate)

            if (parsedStartDate != null && date.isBefore(parsedStartDate)) {
                val endOfShownWeek = monday.plusDays(6)
                val startDayLabel = if (!parsedStartDate.isAfter(endOfShownWeek)) {
                    FULL_DAY_NAMES[parsedStartDate.dayOfWeek.value] ?: parsedStartDate.toString()
                } else {
                    PlanStartOption.formatShortCustomDateLabel(parsedStartDate.toString(), referenceDate)
                }
                DayScheduleSlot(
                    dayOfWeekIso = iso,
                    shortLabel = SHORT_LABELS.getValue(iso),
                    singleLetter = SINGLE_LETTERS.getValue(iso),
                    fullDayName = FULL_DAY_NAMES.getValue(iso),
                    dayOfMonth = date.dayOfMonth,
                    isToday = isToday,
                    isPastThisWeek = isPast,
                    isRestDay = false,
                    assignedSplitIndex = null,
                    assignedDaySpec = null,
                    shortPillTag = "—",
                    outcomeTitle = "Starts $startDayLabel",
                    outcomeSubtitle = "Day 1 begins on $startDayLabel",
                    isCompleted = false,
                    isBeforePlanStart = true,
                    dateIso = date.toString()
                )
            } else {
                val isRest = iso in normalizedRest || splitDays.isEmpty()
                if (isRest) {
                    DayScheduleSlot(
                        dayOfWeekIso = iso,
                        shortLabel = SHORT_LABELS.getValue(iso),
                        singleLetter = SINGLE_LETTERS.getValue(iso),
                        fullDayName = FULL_DAY_NAMES.getValue(iso),
                        dayOfMonth = date.dayOfMonth,
                        isToday = isToday,
                        isPastThisWeek = isPast,
                        isRestDay = true,
                        assignedSplitIndex = null,
                        assignedDaySpec = null,
                        shortPillTag = "Rest",
                        outcomeTitle = "Rest Day",
                        outcomeSubtitle = "Muscle recovery & optional 4-min light stretch",
                        isCompleted = false,
                        isBeforePlanStart = false,
                        dateIso = date.toString()
                    )
                } else {
                    val splitIdx = workoutCounter % splitDays.size
                    val daySpec = splitDays[splitIdx]
                    workoutCounter += 1
                    val hasCompletedSets = completedSetKeys.any { it.startsWith(daySpec.id) }
                    val cleanTitle = benefitHeadlineForDay(daySpec)
                    DayScheduleSlot(
                        dayOfWeekIso = iso,
                        shortLabel = SHORT_LABELS.getValue(iso),
                        singleLetter = SINGLE_LETTERS.getValue(iso),
                        fullDayName = FULL_DAY_NAMES.getValue(iso),
                        dayOfMonth = date.dayOfMonth,
                        isToday = isToday,
                        isPastThisWeek = isPast,
                        isRestDay = false,
                        assignedSplitIndex = splitIdx,
                        assignedDaySpec = daySpec,
                        shortPillTag = shortMusclePillForDay(daySpec),
                        outcomeTitle = cleanTitle,
                        outcomeSubtitle = daySpec.exercises.take(3).joinToString(" • ") { it.name },
                        isCompleted = hasCompletedSets,
                        isBeforePlanStart = false,
                        dateIso = date.toString()
                    )
                }
            }
        }
    }
}
