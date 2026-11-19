package com.example.data.local

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.AgeTier
import com.example.domain.BiologicalSex
import com.example.domain.CurrentBfBracket
import com.example.domain.ExperienceLevel
import com.example.domain.GoalBfBracket
import com.example.domain.StructuralWeakpoint
import com.example.domain.SupplementPreference
import com.example.domain.WeeklyFrequency
import com.example.domain.WeeklySchedulePlanner
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutHistoryRecord
import com.example.domain.WorkoutIntensityMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

private val Context.calibrationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "apex_calibration_preferences"
)

enum class TrainingEnvironment(
    val id: String,
    val shortTitle: String,
    val subtitle: String
) {
    GYM(
        id = "GYM",
        shortTitle = "AT THE GYM",
        subtitle = "Machines & DBs"
    ),
    HOME(
        id = "HOME",
        shortTitle = "AT HOME",
        subtitle = "Bodyweight & Bands"
    );

    companion object {
        fun fromId(raw: String?): TrainingEnvironment =
            entries.firstOrNull { it.id == raw } ?: GYM
    }
}

data class CalibrationProfileState(
    val biologicalSex: BiologicalSex = BiologicalSex.MALE,
    val ageTier: AgeTier = AgeTier.UNDER_30,
    val experienceLevel: ExperienceLevel = ExperienceLevel.NEW_BEGINNER,
    val weeklyFrequency: WeeklyFrequency = WeeklyFrequency.FIVE_DAYS,
    val restDaysIso: Set<Int> = WeeklySchedulePlanner.defaultRestDaysFor(WeeklyFrequency.FIVE_DAYS),
    val currentSpecimenPhotoPath: String? = null,
    val targetBenchmarkPhotoPath: String? = null,
    val currentBfBracket: CurrentBfBracket = CurrentBfBracket.BF_24_28,
    val goalBfBracket: GoalBfBracket = GoalBfBracket.GOAL_10_12,
    val selectedStartingShapeCardId: String = "shape_2_soft_midsection",
    val selectedGoalCardId: String = "goal_2_vtaper",
    val bodyweightKg: Float = 84.0f,
    val heightCm: Float = 178.0f,
    val age: Int = 25,
    val selectedWeakpointIds: Set<String> = StructuralWeakpoint.entries.map { it.id }.toSet(),
    val activeCombatDayIndex: Int = 0,
    val backgroundAlarmsArmed: Boolean = false,
    // Training Environment Toggle (AT THE GYM vs AT HOME)
    val trainingEnvironment: TrainingEnvironment = TrainingEnvironment.GYM,
    // Phase 1: Pre-Session Briefing (30 Mins Prior)
    val preWaterElectrolyteChecked: Boolean = false,
    val preProteinCarbChecked: Boolean = false,
    val preEquipmentScoutedChecked: Boolean = false,
    // Phase 3: Post-Session & Daily Lockdown + Protein Floor & Anti-Junk Shield
    val postWorkoutProtein40gChecked: Boolean = false,
    val supplementPreference: SupplementPreference = SupplementPreference.WITH_SUPPLEMENTS,
    val creatine5gChecked: Boolean = false,
    val consumedProteinGrams: Int = 0,
    val breakfastProteinGrams: Int = 0,
    val lunchProteinGrams: Int = 0,
    val snackProteinGrams: Int = 0,
    val dinnerProteinGrams: Int = 0,
    val extraFoodTableProteinGrams: Int = 0,
    val waterMilliliters: Int = 0,
    val voiceCoachEnabled: Boolean = true,
    val steps10kChecked: Boolean = false,
    val zeroLiquidSugarChecked: Boolean = false,
    val zeroCandySweetsChecked: Boolean = false,
    val zeroFriedJunkChecked: Boolean = false,
    val kitchenClosedBy8pmChecked: Boolean = false,
    val cravingsDefeatedToday: Int = 0,
    val persistedCompletedSetKeys: Set<String> = emptySet(),
    // Weekly Physical Audit persisted in DataStore
    val lastSundayWeightKg: Float = 84.0f,
    val lastSundayWaistCm: Float = 89.8f,
    // 4-Week Progressive Track & Custom Reminder Schedule
    val completedWorkoutsCount: Int = 0,
    val weekRotationOverride: Int? = null, // 1..4 viewed week on 4-Week Track (null = auto)
    val morningReminderEnabled: Boolean = true,
    val morningReminderHour: Int = 8,
    val morningReminderMinute: Int = 0,
    val workoutReminderEnabled: Boolean = true,
    val workoutReminderHour: Int = 17,
    val workoutReminderMinute: Int = 30,
    val eveningReminderEnabled: Boolean = true,
    val eveningReminderHour: Int = 20,
    val eveningReminderMinute: Int = 30,
    // Settings, Language, Units, Display, Haptics & Session Customization
    val appLanguageCode: String = "en",
    val useMetricUnits: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val measurementsKnown: Boolean = true,
    val exercisesPerDay: Int = 5,
    val workoutIntensity: WorkoutIntensityMode = WorkoutIntensityMode.MODERATE,
    val restDurationSeconds: Int = 90,
    val includeWarmupAndStretch: Boolean = true,
    val autoPlayWorkoutMusic: Boolean = true,
    val audioCoachMode: WorkoutAudioCoachMode = WorkoutAudioCoachMode.BOTH_ACTIVE,
    val userFirstName: String = "",
    val planStartDateIso: String = "",
    val workoutHistory: List<WorkoutHistoryRecord> = emptyList(),
    val challengeCompletedDays: Map<String, Set<Int>> = emptyMap()
) {
    val phase1CompletedCount: Int
        get() = listOf(
            preWaterElectrolyteChecked,
            preProteinCarbChecked,
            preEquipmentScoutedChecked
        ).count { it }

    val phase3CompletedCount: Int
        get() = listOf(
            postWorkoutProtein40gChecked,
            steps10kChecked,
            zeroLiquidSugarChecked,
            kitchenClosedBy8pmChecked
        ).count { it }

    val cleanRestraintLockedCount: Int
        get() = listOf(
            zeroCandySweetsChecked,
            zeroLiquidSugarChecked,
            zeroFriedJunkChecked,
            kitchenClosedBy8pmChecked
        ).count { it }
}

class CalibrationPreferencesRepository(private val appContext: Context) {

    private object Keys {
        val BIOLOGICAL_SEX = stringPreferencesKey("biological_sex")
        val AGE_TIER = stringPreferencesKey("age_tier")
        val EXPERIENCE_LEVEL = stringPreferencesKey("experience_level")
        val SPECIMEN_PHOTO_PATH = stringPreferencesKey("specimen_photo_path")
        val BENCHMARK_PHOTO_PATH = stringPreferencesKey("benchmark_photo_path")
        val CURRENT_BF_BRACKET = stringPreferencesKey("current_bf_bracket")
        val GOAL_BF_BRACKET = stringPreferencesKey("goal_bf_bracket")
        val STARTING_SHAPE_CARD_ID = stringPreferencesKey("starting_shape_card_id")
        val GOAL_CARD_ID = stringPreferencesKey("goal_card_id")
        val BODYWEIGHT_KG = floatPreferencesKey("bodyweight_kg")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val AGE_YEARS = intPreferencesKey("age_years")
        val PRIORITY_WEAKPOINTS = stringSetPreferencesKey("priority_weakpoints")
        val ACTIVE_COMBAT_DAY = intPreferencesKey("active_combat_day")
        val ALARMS_ARMED = booleanPreferencesKey("alarms_armed")
        val TRAINING_ENVIRONMENT = stringPreferencesKey("training_environment")
        val PRE_WATER_ELECTROLYTE_CHECKED = booleanPreferencesKey("pre_water_electrolyte_checked")
        val PRE_PROTEIN_CARB_CHECKED = booleanPreferencesKey("pre_protein_carb_checked")
        val PRE_EQUIPMENT_SCOUTED_CHECKED = booleanPreferencesKey("pre_equipment_scouted_checked")
        val POST_WORKOUT_PROTEIN_40G_CHECKED = booleanPreferencesKey("post_workout_protein_40g_checked")
        val SUPPLEMENT_PREFERENCE = stringPreferencesKey("supplement_preference")
        val CREATINE_5G_CHECKED = booleanPreferencesKey("creatine_5g_checked")
        val CONSUMED_PROTEIN_GRAMS = intPreferencesKey("consumed_protein_grams")
        val BREAKFAST_PROTEIN_GRAMS = intPreferencesKey("breakfast_protein_grams")
        val LUNCH_PROTEIN_GRAMS = intPreferencesKey("lunch_protein_grams")
        val SNACK_PROTEIN_GRAMS = intPreferencesKey("snack_protein_grams")
        val DINNER_PROTEIN_GRAMS = intPreferencesKey("dinner_protein_grams")
        val EXTRA_FOOD_TABLE_PROTEIN_GRAMS = intPreferencesKey("extra_food_table_protein_grams")
        val WATER_MILLILITERS = intPreferencesKey("water_milliliters")
        val VOICE_COACH_ENABLED = booleanPreferencesKey("voice_coach_enabled")
        val STEPS_10K_CHECKED = booleanPreferencesKey("steps_10k_checked")
        val ZERO_LIQUID_SUGAR_CHECKED = booleanPreferencesKey("zero_liquid_sugar_checked")
        val ZERO_CANDY_SWEETS_CHECKED = booleanPreferencesKey("zero_candy_sweets_checked")
        val ZERO_FRIED_JUNK_CHECKED = booleanPreferencesKey("zero_fried_junk_checked")
        val KITCHEN_CLOSED_8PM_CHECKED = booleanPreferencesKey("kitchen_closed_8pm_checked")
        val CRAVINGS_DEFEATED_TODAY = intPreferencesKey("cravings_defeated_today")
        val COMPLETED_SET_KEYS = stringSetPreferencesKey("completed_set_keys")
        val LAST_SUNDAY_WEIGHT_KG = floatPreferencesKey("last_sunday_weight_kg")
        val LAST_SUNDAY_WAIST_CM = floatPreferencesKey("last_sunday_waist_cm")
        val WEEKLY_FREQUENCY = stringPreferencesKey("weekly_frequency")
        val REST_DAYS_ISO = stringSetPreferencesKey("rest_days_iso")
        val COMPLETED_WORKOUTS_COUNT = intPreferencesKey("completed_workouts_count")
        val WEEK_ROTATION_OVERRIDE = intPreferencesKey("week_rotation_override_v2")
        val ALARMS_ARMED_V2 = booleanPreferencesKey("alarms_armed_opt_in_v2")
        val MORNING_REM_ENABLED = booleanPreferencesKey("morning_rem_enabled")
        val MORNING_REM_HOUR = intPreferencesKey("morning_rem_hour")
        val MORNING_REM_MIN = intPreferencesKey("morning_rem_min")
        val WORKOUT_REM_ENABLED = booleanPreferencesKey("workout_rem_enabled")
        val WORKOUT_REM_HOUR = intPreferencesKey("workout_rem_hour")
        val WORKOUT_REM_MIN = intPreferencesKey("workout_rem_min")
        val EVENING_REM_ENABLED = booleanPreferencesKey("evening_rem_enabled")
        val EVENING_REM_HOUR = intPreferencesKey("evening_rem_hour")
        val EVENING_REM_MIN = intPreferencesKey("evening_rem_min")
        val APP_LANGUAGE_CODE = stringPreferencesKey("app_language_code")
        val USE_METRIC_UNITS = booleanPreferencesKey("use_metric_units")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val MEASUREMENTS_KNOWN = booleanPreferencesKey("measurements_known")
        val EXERCISES_PER_DAY = intPreferencesKey("exercises_per_day")
        val WORKOUT_INTENSITY_MODE = stringPreferencesKey("workout_intensity_mode")
        val REST_DURATION_SECONDS = intPreferencesKey("rest_duration_seconds")
        val INCLUDE_WARMUP_STRETCH = booleanPreferencesKey("include_warmup_stretch")
        val AUTO_PLAY_WORKOUT_MUSIC = booleanPreferencesKey("auto_play_workout_music")
        val AUDIO_COACH_MODE = stringPreferencesKey("audio_coach_mode_v1")
        val USER_FIRST_NAME = stringPreferencesKey("user_first_name_v1")
        val PLAN_START_DATE_ISO = stringPreferencesKey("plan_start_date_iso_v1")
        val WORKOUT_HISTORY_RECORDS = stringPreferencesKey("workout_history_records_v1")
        val CHALLENGE_COMPLETED_DAYS = stringSetPreferencesKey("challenge_completed_days_v1")
    }

    val profileFlow: Flow<CalibrationProfileState> = appContext.calibrationDataStore.data.map { prefs ->
        val sex = BiologicalSex.fromId(prefs[Keys.BIOLOGICAL_SEX])
        val ageTier = AgeTier.fromId(prefs[Keys.AGE_TIER])
        val experience = ExperienceLevel.fromId(prefs[Keys.EXPERIENCE_LEVEL])
        val weeklyFreq = prefs[Keys.WEEKLY_FREQUENCY]?.let { WeeklyFrequency.fromId(it) }
            ?: WeeklyFrequency.FIVE_DAYS
        val rawRestDays = prefs[Keys.REST_DAYS_ISO]
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet()
            .orEmpty()
        val resolvedRestDays = if (rawRestDays.size == (7 - weeklyFreq.daysCount)) {
            WeeklySchedulePlanner.normalizeRestDays(rawRestDays, weeklyFreq)
        } else {
            WeeklySchedulePlanner.defaultRestDaysFor(weeklyFreq)
        }
        val defaultWeakpoints = StructuralWeakpoint.entries.map { it.id }.toSet()

        val defaultWeight = if (sex == BiologicalSex.FEMALE) 64.0f else 84.0f
        val defaultHeight = if (sex == BiologicalSex.FEMALE) 165.0f else 178.0f
        val bodyweight = prefs[Keys.BODYWEIGHT_KG] ?: defaultWeight
        val currentBracket = CurrentBfBracket.fromId(prefs[Keys.CURRENT_BF_BRACKET], sex)
        val goalBracket = GoalBfBracket.fromId(prefs[Keys.GOAL_BF_BRACKET], sex)

        val defaultStartCardId = when (currentBracket) {
            CurrentBfBracket.BF_30_PLUS, CurrentBfBracket.FEMALE_BF_32_PLUS -> "shape_1_high_bf"
            CurrentBfBracket.BF_24_28, CurrentBfBracket.FEMALE_BF_25_29 -> "shape_2_soft_midsection"
            CurrentBfBracket.BF_15, CurrentBfBracket.FEMALE_BF_LEAN_BASE -> "shape_5_underweight"
            else -> "shape_3_skinny_fat"
        }
        val defaultGoalCardId = when (goalBracket) {
            GoalBfBracket.GOAL_LEAN_FLAT, GoalBfBracket.FEMALE_GOAL_SIMPLE_FAT_LOSS -> "goal_1_fat_loss"
            GoalBfBracket.GOAL_MASS_SIZE, GoalBfBracket.FEMALE_GOAL_HOURGLASS -> "goal_4_power_mass"
            else -> "goal_2_vtaper"
        }

        CalibrationProfileState(
            biologicalSex = sex,
            ageTier = ageTier,
            experienceLevel = experience,
            weeklyFrequency = weeklyFreq,
            restDaysIso = resolvedRestDays,
            currentSpecimenPhotoPath = prefs[Keys.SPECIMEN_PHOTO_PATH],
            targetBenchmarkPhotoPath = prefs[Keys.BENCHMARK_PHOTO_PATH],
            currentBfBracket = currentBracket,
            goalBfBracket = goalBracket,
            selectedStartingShapeCardId = prefs[Keys.STARTING_SHAPE_CARD_ID] ?: defaultStartCardId,
            selectedGoalCardId = prefs[Keys.GOAL_CARD_ID] ?: defaultGoalCardId,
            bodyweightKg = bodyweight,
            heightCm = prefs[Keys.HEIGHT_CM] ?: defaultHeight,
            age = prefs[Keys.AGE_YEARS] ?: ageTier.representativeAge,
            selectedWeakpointIds = prefs[Keys.PRIORITY_WEAKPOINTS] ?: defaultWeakpoints,
            activeCombatDayIndex = (prefs[Keys.ACTIVE_COMBAT_DAY] ?: 0).coerceIn(0, 5),
            backgroundAlarmsArmed = prefs[Keys.ALARMS_ARMED_V2] ?: false,
            trainingEnvironment = TrainingEnvironment.fromId(prefs[Keys.TRAINING_ENVIRONMENT]),
            preWaterElectrolyteChecked = prefs[Keys.PRE_WATER_ELECTROLYTE_CHECKED] ?: false,
            preProteinCarbChecked = prefs[Keys.PRE_PROTEIN_CARB_CHECKED] ?: false,
            preEquipmentScoutedChecked = prefs[Keys.PRE_EQUIPMENT_SCOUTED_CHECKED] ?: false,
            postWorkoutProtein40gChecked = prefs[Keys.POST_WORKOUT_PROTEIN_40G_CHECKED] ?: false,
            supplementPreference = SupplementPreference.fromId(prefs[Keys.SUPPLEMENT_PREFERENCE]),
            creatine5gChecked = prefs[Keys.CREATINE_5G_CHECKED] ?: false,
            consumedProteinGrams = (prefs[Keys.CONSUMED_PROTEIN_GRAMS] ?: 0).coerceIn(0, 400),
            breakfastProteinGrams = (prefs[Keys.BREAKFAST_PROTEIN_GRAMS] ?: 0).coerceIn(0, 150),
            lunchProteinGrams = (prefs[Keys.LUNCH_PROTEIN_GRAMS] ?: 0).coerceIn(0, 150),
            snackProteinGrams = (prefs[Keys.SNACK_PROTEIN_GRAMS] ?: 0).coerceIn(0, 150),
            dinnerProteinGrams = (prefs[Keys.DINNER_PROTEIN_GRAMS] ?: 0).coerceIn(0, 150),
            extraFoodTableProteinGrams = (prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] ?: 0).coerceIn(0, 300),
            waterMilliliters = (prefs[Keys.WATER_MILLILITERS] ?: 0).coerceIn(0, 6000),
            voiceCoachEnabled = prefs[Keys.VOICE_COACH_ENABLED] ?: true,
            steps10kChecked = prefs[Keys.STEPS_10K_CHECKED] ?: false,
            zeroLiquidSugarChecked = prefs[Keys.ZERO_LIQUID_SUGAR_CHECKED] ?: false,
            zeroCandySweetsChecked = prefs[Keys.ZERO_CANDY_SWEETS_CHECKED] ?: false,
            zeroFriedJunkChecked = prefs[Keys.ZERO_FRIED_JUNK_CHECKED] ?: false,
            kitchenClosedBy8pmChecked = prefs[Keys.KITCHEN_CLOSED_8PM_CHECKED] ?: false,
            cravingsDefeatedToday = (prefs[Keys.CRAVINGS_DEFEATED_TODAY] ?: 0).coerceIn(0, 99),
            persistedCompletedSetKeys = prefs[Keys.COMPLETED_SET_KEYS] ?: emptySet(),
            lastSundayWeightKg = prefs[Keys.LAST_SUNDAY_WEIGHT_KG] ?: bodyweight,
            lastSundayWaistCm = prefs[Keys.LAST_SUNDAY_WAIST_CM] ?: 89.8f,
            completedWorkoutsCount = (prefs[Keys.COMPLETED_WORKOUTS_COUNT] ?: 0).coerceAtLeast(0),
            weekRotationOverride = (prefs[Keys.WEEK_ROTATION_OVERRIDE] ?: -1).takeIf { it in 1..4 },
            morningReminderEnabled = prefs[Keys.MORNING_REM_ENABLED] ?: true,
            morningReminderHour = (prefs[Keys.MORNING_REM_HOUR] ?: 8).coerceIn(0, 23),
            morningReminderMinute = (prefs[Keys.MORNING_REM_MIN] ?: 0).coerceIn(0, 59),
            workoutReminderEnabled = prefs[Keys.WORKOUT_REM_ENABLED] ?: true,
            workoutReminderHour = (prefs[Keys.WORKOUT_REM_HOUR] ?: 17).coerceIn(0, 23),
            workoutReminderMinute = (prefs[Keys.WORKOUT_REM_MIN] ?: 30).coerceIn(0, 59),
            eveningReminderEnabled = prefs[Keys.EVENING_REM_ENABLED] ?: true,
            eveningReminderHour = (prefs[Keys.EVENING_REM_HOUR] ?: 20).coerceIn(0, 23),
            eveningReminderMinute = (prefs[Keys.EVENING_REM_MIN] ?: 30).coerceIn(0, 59),
            appLanguageCode = prefs[Keys.APP_LANGUAGE_CODE] ?: "en",
            useMetricUnits = prefs[Keys.USE_METRIC_UNITS] ?: true,
            keepScreenAwake = prefs[Keys.KEEP_SCREEN_AWAKE] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
            measurementsKnown = prefs[Keys.MEASUREMENTS_KNOWN] ?: true,
            exercisesPerDay = (prefs[Keys.EXERCISES_PER_DAY] ?: 5).let { if (it in listOf(3, 5, 6)) it else 5 },
            workoutIntensity = prefs[Keys.WORKOUT_INTENSITY_MODE]?.let { WorkoutIntensityMode.fromId(it) }
                ?: WorkoutIntensityMode.fromExercisesCount(
                    (prefs[Keys.EXERCISES_PER_DAY] ?: 5).let { if (it in listOf(3, 5, 6)) it else 5 }
                ),
            restDurationSeconds = (prefs[Keys.REST_DURATION_SECONDS] ?: 90).coerceIn(30, 180),
            includeWarmupAndStretch = prefs[Keys.INCLUDE_WARMUP_STRETCH] ?: true,
            autoPlayWorkoutMusic = prefs[Keys.AUTO_PLAY_WORKOUT_MUSIC] ?: true,
            audioCoachMode = prefs[Keys.AUDIO_COACH_MODE]?.let { WorkoutAudioCoachMode.fromId(it) }
                ?: run {
                    val voiceOn = prefs[Keys.VOICE_COACH_ENABLED] ?: true
                    val musicOn = prefs[Keys.AUTO_PLAY_WORKOUT_MUSIC] ?: true
                    when {
                        musicOn && voiceOn -> WorkoutAudioCoachMode.BOTH_ACTIVE
                        musicOn && !voiceOn -> WorkoutAudioCoachMode.MUSIC_ONLY
                        !musicOn && voiceOn -> WorkoutAudioCoachMode.VOICE_ONLY
                        else -> WorkoutAudioCoachMode.MUTED
                    }
                },
            userFirstName = prefs[Keys.USER_FIRST_NAME]?.trim().orEmpty(),
            planStartDateIso = prefs[Keys.PLAN_START_DATE_ISO]?.trim().orEmpty(),
            workoutHistory = prefs[Keys.WORKOUT_HISTORY_RECORDS]
                ?.lineSequence()
                ?.mapNotNull { line -> line.takeIf { it.isNotBlank() }?.let(WorkoutHistoryRecord::decode) }
                ?.sortedByDescending { it.epochMs }
                ?.toList()
                .orEmpty(),
            challengeCompletedDays = prefs[Keys.CHALLENGE_COMPLETED_DAYS]
                ?.mapNotNull { entry ->
                    val split = entry.split(":")
                    if (split.size == 2) {
                        val dayNum = split[1].toIntOrNull()
                        if (dayNum != null) split[0] to dayNum else null
                    } else null
                }
                ?.groupBy({ it.first }, { it.second })
                ?.mapValues { (_, days) -> days.toSet() }
                .orEmpty()
        )
    }

    suspend fun updateWeeklyFrequency(frequency: WeeklyFrequency) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.WEEKLY_FREQUENCY] = frequency.id
            val existing = prefs[Keys.REST_DAYS_ISO]
                ?.mapNotNull { it.toIntOrNull() }
                ?.filter { it in 1..7 }
                ?.toSet()
                .orEmpty()
            val targetRestCount = 7 - frequency.daysCount
            val nextRest = if (existing.size == targetRestCount) {
                existing
            } else {
                WeeklySchedulePlanner.defaultRestDaysFor(frequency)
            }
            prefs[Keys.REST_DAYS_ISO] = nextRest.map { it.toString() }.toSet()
        }
    }

    suspend fun toggleRestDayIso(dayIso: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            val currentFreq = prefs[Keys.WEEKLY_FREQUENCY]?.let { WeeklyFrequency.fromId(it) }
                ?: WeeklyFrequency.FIVE_DAYS
            val currentRest = prefs[Keys.REST_DAYS_ISO]
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet()
                .orEmpty()
            val updatedRest = WeeklySchedulePlanner.toggleRestDay(currentRest, dayIso, currentFreq)
            val newFreq = WeeklySchedulePlanner.frequencyForRestDays(updatedRest)
            prefs[Keys.REST_DAYS_ISO] = updatedRest.map { it.toString() }.toSet()
            prefs[Keys.WEEKLY_FREQUENCY] = newFreq.id
            val activeIdx = prefs[Keys.ACTIVE_COMBAT_DAY] ?: 0
            if (activeIdx >= newFreq.daysCount) {
                prefs[Keys.ACTIVE_COMBAT_DAY] = 0
            }
        }
    }

    suspend fun updateBiologicalSex(sex: BiologicalSex) {
        appContext.calibrationDataStore.edit { prefs ->
            val previousSex = BiologicalSex.fromId(prefs[Keys.BIOLOGICAL_SEX])
            prefs[Keys.BIOLOGICAL_SEX] = sex.id
            prefs[Keys.CURRENT_BF_BRACKET] = CurrentBfBracket.defaultForSex(sex).id
            prefs[Keys.GOAL_BF_BRACKET] = GoalBfBracket.defaultForSex(sex).id

            if (previousSex != sex) {
                val currentWeight = prefs[Keys.BODYWEIGHT_KG]
                if (sex == BiologicalSex.FEMALE && (currentWeight == null || currentWeight > 78f)) {
                    prefs[Keys.BODYWEIGHT_KG] = 64.0f
                    prefs[Keys.HEIGHT_CM] = 165.0f
                } else if (sex == BiologicalSex.MALE && (currentWeight == null || currentWeight < 70f)) {
                    prefs[Keys.BODYWEIGHT_KG] = 84.0f
                    prefs[Keys.HEIGHT_CM] = 178.0f
                }
            }
        }
    }

    suspend fun updateAgeTier(ageTier: AgeTier) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.AGE_TIER] = ageTier.id
            prefs[Keys.AGE_YEARS] = ageTier.representativeAge
        }
    }

    suspend fun updateExperienceLevel(experience: ExperienceLevel) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.EXPERIENCE_LEVEL] = experience.id
        }
    }

    suspend fun updateCurrentBracket(bracket: CurrentBfBracket) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.CURRENT_BF_BRACKET] = bracket.id
        }
    }

    suspend fun updateGoalBracket(bracket: GoalBfBracket) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.GOAL_BF_BRACKET] = bracket.id
        }
    }

    suspend fun updateBiometrics(weightKg: Float, heightCm: Float, age: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.BODYWEIGHT_KG] = weightKg.coerceIn(40f, 220f)
            prefs[Keys.HEIGHT_CM] = heightCm.coerceIn(130f, 230f)
            prefs[Keys.AGE_YEARS] = age.coerceIn(16, 90)
        }
    }

    suspend fun toggleWeakpoint(weakpointId: String) {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.PRIORITY_WEAKPOINTS]
                ?: StructuralWeakpoint.entries.map { it.id }.toSet()
            val updated = if (weakpointId in current && current.size > 1) {
                current - weakpointId
            } else {
                current + weakpointId
            }
            prefs[Keys.PRIORITY_WEAKPOINTS] = updated
        }
    }

    suspend fun updateActiveCombatDay(dayIndex: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.ACTIVE_COMBAT_DAY] = dayIndex.coerceIn(0, 5)
        }
    }

    suspend fun setAlarmsArmed(armed: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.ALARMS_ARMED_V2] = armed
        }
    }

    suspend fun updateTrainingEnvironment(environment: TrainingEnvironment) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.TRAINING_ENVIRONMENT] = environment.id
        }
    }

    suspend fun setPreWaterElectrolyteChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.PRE_WATER_ELECTROLYTE_CHECKED] = checked
        }
    }

    suspend fun setPreProteinCarbChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.PRE_PROTEIN_CARB_CHECKED] = checked
        }
    }

    suspend fun setPreEquipmentScoutedChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.PRE_EQUIPMENT_SCOUTED_CHECKED] = checked
        }
    }

    suspend fun setPostWorkoutProtein40gChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.POST_WORKOUT_PROTEIN_40G_CHECKED] = checked
        }
    }

    suspend fun updateConsumedProteinGrams(grams: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.CONSUMED_PROTEIN_GRAMS] = grams.coerceIn(0, 400)
        }
    }

    suspend fun setSteps10kChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.STEPS_10K_CHECKED] = checked
        }
    }

    suspend fun setZeroLiquidSugarChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.ZERO_LIQUID_SUGAR_CHECKED] = checked
        }
    }

    suspend fun setKitchenClosedBy8pmChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.KITCHEN_CLOSED_8PM_CHECKED] = checked
        }
    }

    suspend fun saveSundayAuditSnapshot(
        weightKg: Float,
        waistCm: Float,
        syncBodyweight: Boolean = true
    ) {
        appContext.calibrationDataStore.edit { prefs ->
            val safeWeight = weightKg.coerceIn(40f, 220f)
            prefs[Keys.LAST_SUNDAY_WEIGHT_KG] = safeWeight
            prefs[Keys.LAST_SUNDAY_WAIST_CM] = waistCm.coerceIn(50f, 180f)
            if (syncBodyweight) {
                prefs[Keys.BODYWEIGHT_KG] = safeWeight
                prefs[Keys.MEASUREMENTS_KNOWN] = true
            }
        }
    }

    suspend fun setExerciseSetCompletedInDataStore(setKey: String, isCompleted: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_SET_KEYS] ?: emptySet()
            prefs[Keys.COMPLETED_SET_KEYS] = if (isCompleted) {
                current + setKey
            } else {
                current - setKey
            }
        }
    }

    suspend fun clearDaySetsInDataStore(dayIdPrefix: String) {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_SET_KEYS] ?: emptySet()
            prefs[Keys.COMPLETED_SET_KEYS] = current.filterNot { it.startsWith(dayIdPrefix) }.toSet()
        }
    }

    suspend fun persistPickedPhoto(slotNumber: Int, uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val dir = File(appContext.filesDir, "specimen_slots").apply { mkdirs() }
            val destFile = File(dir, "slot_${slotNumber}_${System.currentTimeMillis()}.jpg")
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            val savedPath = destFile.absolutePath
            appContext.calibrationDataStore.edit { prefs ->
                if (slotNumber == 1) {
                    prefs[Keys.SPECIMEN_PHOTO_PATH] = savedPath
                } else {
                    prefs[Keys.BENCHMARK_PHOTO_PATH] = savedPath
                }
            }
            savedPath
        } catch (_: Exception) {
            null
        }
    }

    suspend fun clearPickedPhoto(slotNumber: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            if (slotNumber == 1) {
                prefs.remove(Keys.SPECIMEN_PHOTO_PATH)
            } else {
                prefs.remove(Keys.BENCHMARK_PHOTO_PATH)
            }
        }
    }

    suspend fun updateSelectedShapeCardIds(startCardId: String?, goalCardId: String?) {
        appContext.calibrationDataStore.edit { prefs ->
            if (startCardId != null) prefs[Keys.STARTING_SHAPE_CARD_ID] = startCardId
            if (goalCardId != null) prefs[Keys.GOAL_CARD_ID] = goalCardId
        }
    }

    suspend fun updateSupplementPreference(preference: SupplementPreference) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.SUPPLEMENT_PREFERENCE] = preference.id
        }
    }

    suspend fun setCreatine5gChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.CREATINE_5G_CHECKED] = checked
        }
    }

    suspend fun setZeroCandySweetsChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.ZERO_CANDY_SWEETS_CHECKED] = checked
        }
    }

    suspend fun setZeroFriedJunkChecked(checked: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.ZERO_FRIED_JUNK_CHECKED] = checked
        }
    }

    suspend fun incrementCravingsDefeated() {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.CRAVINGS_DEFEATED_TODAY] ?: 0
            prefs[Keys.CRAVINGS_DEFEATED_TODAY] = (current + 1).coerceIn(0, 99)
        }
    }

    suspend fun addFoodTableProtein(grams: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            val extra = ((prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] ?: 0) + grams).coerceIn(0, 350)
            prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] = extra
            val b = prefs[Keys.BREAKFAST_PROTEIN_GRAMS] ?: 0
            val l = prefs[Keys.LUNCH_PROTEIN_GRAMS] ?: 0
            val s = prefs[Keys.SNACK_PROTEIN_GRAMS] ?: 0
            val d = prefs[Keys.DINNER_PROTEIN_GRAMS] ?: 0
            prefs[Keys.CONSUMED_PROTEIN_GRAMS] = (b + l + s + d + extra).coerceIn(0, 400)
        }
    }

    suspend fun logMealProtein(mealTag: String, grams: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            when (mealTag.lowercase()) {
                "breakfast" -> {
                    val prev = prefs[Keys.BREAKFAST_PROTEIN_GRAMS] ?: 0
                    prefs[Keys.BREAKFAST_PROTEIN_GRAMS] = if (prev > 0) 0 else grams
                }
                "lunch" -> {
                    val prev = prefs[Keys.LUNCH_PROTEIN_GRAMS] ?: 0
                    prefs[Keys.LUNCH_PROTEIN_GRAMS] = if (prev > 0) 0 else grams
                }
                "snack" -> {
                    val prev = prefs[Keys.SNACK_PROTEIN_GRAMS] ?: 0
                    prefs[Keys.SNACK_PROTEIN_GRAMS] = if (prev > 0) 0 else grams
                }
                "dinner" -> {
                    val prev = prefs[Keys.DINNER_PROTEIN_GRAMS] ?: 0
                    prefs[Keys.DINNER_PROTEIN_GRAMS] = if (prev > 0) 0 else grams
                }
            }
            val b = prefs[Keys.BREAKFAST_PROTEIN_GRAMS] ?: 0
            val l = prefs[Keys.LUNCH_PROTEIN_GRAMS] ?: 0
            val s = prefs[Keys.SNACK_PROTEIN_GRAMS] ?: 0
            val d = prefs[Keys.DINNER_PROTEIN_GRAMS] ?: 0
            val extra = prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] ?: 0
            prefs[Keys.CONSUMED_PROTEIN_GRAMS] = (b + l + s + d + extra).coerceIn(0, 400)
        }
    }

    suspend fun addWaterMilliliters(additionalMl: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.WATER_MILLILITERS] ?: 0
            prefs[Keys.WATER_MILLILITERS] = (current + additionalMl).coerceIn(0, 6000)
        }
    }

    suspend fun resetWaterMilliliters() {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.WATER_MILLILITERS] = 0
        }
    }

    suspend fun resetDailyNutrition() {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.CONSUMED_PROTEIN_GRAMS] = 0
            prefs[Keys.BREAKFAST_PROTEIN_GRAMS] = 0
            prefs[Keys.LUNCH_PROTEIN_GRAMS] = 0
            prefs[Keys.SNACK_PROTEIN_GRAMS] = 0
            prefs[Keys.DINNER_PROTEIN_GRAMS] = 0
            prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] = 0
            prefs[Keys.WATER_MILLILITERS] = 0
            prefs[Keys.CREATINE_5G_CHECKED] = false
        }
    }

    suspend fun setVoiceCoachEnabled(enabled: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.VOICE_COACH_ENABLED] = enabled
            val currentMode = prefs[Keys.AUDIO_COACH_MODE]?.let { WorkoutAudioCoachMode.fromId(it) }
                ?: WorkoutAudioCoachMode.BOTH_ACTIVE
            val nextMode = when {
                enabled && currentMode.musicEnabled -> WorkoutAudioCoachMode.BOTH_ACTIVE
                enabled && !currentMode.musicEnabled -> WorkoutAudioCoachMode.VOICE_ONLY
                !enabled && currentMode.musicEnabled -> WorkoutAudioCoachMode.MUSIC_ONLY
                else -> WorkoutAudioCoachMode.MUTED
            }
            prefs[Keys.AUDIO_COACH_MODE] = nextMode.id
        }
    }

    suspend fun incrementCompletedWorkoutsCount() {
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.COMPLETED_WORKOUTS_COUNT] ?: 0
            prefs[Keys.COMPLETED_WORKOUTS_COUNT] = (current + 1).coerceIn(0, 9999)
            prefs.remove(Keys.WEEK_ROTATION_OVERRIDE)
        }
    }

    suspend fun setWeekRotationOverride(weekOrNull: Int?) {
        appContext.calibrationDataStore.edit { prefs ->
            if (weekOrNull == null || weekOrNull !in 1..4) {
                prefs.remove(Keys.WEEK_ROTATION_OVERRIDE)
            } else {
                prefs[Keys.WEEK_ROTATION_OVERRIDE] = weekOrNull
            }
        }
    }

    suspend fun updateReminderSchedule(
        checkpointId: Int,
        hourOfDay: Int,
        minute: Int,
        enabled: Boolean
    ) {
        val safeH = ((hourOfDay % 24) + 24) % 24
        val safeM = ((minute % 60) + 60) % 60
        appContext.calibrationDataStore.edit { prefs ->
            when (checkpointId) {
                1001 -> {
                    prefs[Keys.MORNING_REM_HOUR] = safeH
                    prefs[Keys.MORNING_REM_MIN] = safeM
                    prefs[Keys.MORNING_REM_ENABLED] = enabled
                }
                1003 -> {
                    prefs[Keys.WORKOUT_REM_HOUR] = safeH
                    prefs[Keys.WORKOUT_REM_MIN] = safeM
                    prefs[Keys.WORKOUT_REM_ENABLED] = enabled
                }
                1004 -> {
                    prefs[Keys.EVENING_REM_HOUR] = safeH
                    prefs[Keys.EVENING_REM_MIN] = safeM
                    prefs[Keys.EVENING_REM_ENABLED] = enabled
                }
            }
        }
    }

    suspend fun updateAppLanguageCode(code: String) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.APP_LANGUAGE_CODE] = code
        }
    }

    suspend fun setMeasurementsKnown(known: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.MEASUREMENTS_KNOWN] = known
        }
    }

    suspend fun updateExercisesPerDay(count: Int) {
        val safeCount = when {
            count <= 3 -> 3
            count >= 6 -> 6
            else -> 5
        }
        val syncedMode = WorkoutIntensityMode.fromExercisesCount(safeCount)
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.EXERCISES_PER_DAY] = safeCount
            prefs[Keys.WORKOUT_INTENSITY_MODE] = syncedMode.id
        }
    }

    suspend fun updateWorkoutIntensity(mode: WorkoutIntensityMode) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.WORKOUT_INTENSITY_MODE] = mode.id
            prefs[Keys.EXERCISES_PER_DAY] = mode.exercisesCount
            prefs[Keys.REST_DURATION_SECONDS] = mode.restSecondsDefault
        }
    }

    suspend fun setAutoPlayWorkoutMusic(enabled: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.AUTO_PLAY_WORKOUT_MUSIC] = enabled
            val currentMode = prefs[Keys.AUDIO_COACH_MODE]?.let { WorkoutAudioCoachMode.fromId(it) }
                ?: WorkoutAudioCoachMode.BOTH_ACTIVE
            val nextMode = when {
                enabled && currentMode.voiceEnabled -> WorkoutAudioCoachMode.BOTH_ACTIVE
                enabled && !currentMode.voiceEnabled -> WorkoutAudioCoachMode.MUSIC_ONLY
                !enabled && currentMode.voiceEnabled -> WorkoutAudioCoachMode.VOICE_ONLY
                else -> WorkoutAudioCoachMode.MUTED
            }
            prefs[Keys.AUDIO_COACH_MODE] = nextMode.id
        }
    }

    suspend fun updateAudioCoachMode(mode: WorkoutAudioCoachMode) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.AUDIO_COACH_MODE] = mode.id
            prefs[Keys.VOICE_COACH_ENABLED] = mode.voiceEnabled
            prefs[Keys.AUTO_PLAY_WORKOUT_MUSIC] = mode.musicEnabled
        }
    }

    suspend fun updateRestDurationSeconds(seconds: Int) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.REST_DURATION_SECONDS] = seconds.coerceIn(30, 180)
        }
    }

    suspend fun setIncludeWarmupAndStretch(included: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.INCLUDE_WARMUP_STRETCH] = included
        }
    }

    suspend fun setUseMetricUnits(metric: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.USE_METRIC_UNITS] = metric
        }
    }

    suspend fun setKeepScreenAwake(awake: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.KEEP_SCREEN_AWAKE] = awake
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun updateUserFirstName(name: String) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.USER_FIRST_NAME] = name.trim().take(28)
        }
    }

    suspend fun updatePlanStartDate(
        startDateIso: String,
        ensureTrainingDayIso: Int? = null
    ) {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.PLAN_START_DATE_ISO] = startDateIso.trim()
            prefs[Keys.ACTIVE_COMBAT_DAY] = 0
            if (ensureTrainingDayIso != null && ensureTrainingDayIso in 1..7) {
                val currentFreq = prefs[Keys.WEEKLY_FREQUENCY]?.let { WeeklyFrequency.fromId(it) }
                    ?: WeeklyFrequency.FIVE_DAYS
                val currentRest = prefs[Keys.REST_DAYS_ISO]
                    ?.mapNotNull { it.toIntOrNull() }
                    ?.toSet()
                    .orEmpty()
                val updatedRest = WeeklySchedulePlanner.ensureDayIsTrainingDay(
                    currentRestDays = currentRest,
                    dayIso = ensureTrainingDayIso,
                    fallbackFrequency = currentFreq
                )
                val newFreq = WeeklySchedulePlanner.frequencyForRestDays(updatedRest)
                prefs[Keys.REST_DAYS_ISO] = updatedRest.map { it.toString() }.toSet()
                prefs[Keys.WEEKLY_FREQUENCY] = newFreq.id
            }
        }
    }

    suspend fun appendWorkoutHistoryRecord(record: WorkoutHistoryRecord) {
        appContext.calibrationDataStore.edit { prefs ->
            val existing = prefs[Keys.WORKOUT_HISTORY_RECORDS]
                ?.lineSequence()
                ?.mapNotNull { line -> line.takeIf { it.isNotBlank() }?.let(WorkoutHistoryRecord::decode) }
                ?.toList()
                .orEmpty()
            val updated = (listOf(record) + existing.filterNot { it.id == record.id })
                .sortedByDescending { it.epochMs }
                .take(80)
            prefs[Keys.WORKOUT_HISTORY_RECORDS] = updated.joinToString("\n") { it.encode() }
        }
    }

    suspend fun markChallengeDayCompleted(challengeId: String, dayNumber: Int) {
        if (challengeId.isBlank() || dayNumber < 1) return
        appContext.calibrationDataStore.edit { prefs ->
            val current = prefs[Keys.CHALLENGE_COMPLETED_DAYS] ?: emptySet()
            prefs[Keys.CHALLENGE_COMPLETED_DAYS] = current + "$challengeId:$dayNumber"
        }
    }

    suspend fun resetAllProgressInDataStore() {
        appContext.calibrationDataStore.edit { prefs ->
            prefs[Keys.COMPLETED_SET_KEYS] = emptySet()
            prefs[Keys.COMPLETED_WORKOUTS_COUNT] = 0
            prefs.remove(Keys.WEEK_ROTATION_OVERRIDE)
            prefs.remove(Keys.WORKOUT_HISTORY_RECORDS)
            prefs.remove(Keys.CHALLENGE_COMPLETED_DAYS)
            prefs[Keys.CONSUMED_PROTEIN_GRAMS] = 0
            prefs[Keys.BREAKFAST_PROTEIN_GRAMS] = 0
            prefs[Keys.LUNCH_PROTEIN_GRAMS] = 0
            prefs[Keys.SNACK_PROTEIN_GRAMS] = 0
            prefs[Keys.DINNER_PROTEIN_GRAMS] = 0
            prefs[Keys.EXTRA_FOOD_TABLE_PROTEIN_GRAMS] = 0
            prefs[Keys.WATER_MILLILITERS] = 0
            prefs[Keys.STEPS_10K_CHECKED] = false
            prefs[Keys.ZERO_LIQUID_SUGAR_CHECKED] = false
            prefs[Keys.ZERO_CANDY_SWEETS_CHECKED] = false
            prefs[Keys.ZERO_FRIED_JUNK_CHECKED] = false
            prefs[Keys.KITCHEN_CLOSED_8PM_CHECKED] = false
            prefs[Keys.POST_WORKOUT_PROTEIN_40G_CHECKED] = false
            prefs[Keys.CREATINE_5G_CHECKED] = false
            prefs[Keys.CRAVINGS_DEFEATED_TODAY] = 0
        }
    }
}

