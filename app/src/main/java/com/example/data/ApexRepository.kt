package com.example.data

import android.net.Uri
import com.example.data.local.ApexDao
import com.example.data.local.CalibrationPreferencesRepository
import com.example.data.local.CalibrationProfileState
import com.example.data.local.CombatSetLogEntity
import com.example.data.local.DailyComplianceEntity
import com.example.data.local.WeeklyAuditEntity
import com.example.domain.AgeTier
import com.example.domain.BiologicalSex
import com.example.domain.CurrentBfBracket
import com.example.domain.ExperienceLevel
import com.example.domain.GoalBfBracket
import com.example.domain.WeeklyFrequency
import com.example.domain.WorkoutAudioCoachMode
import com.example.domain.WorkoutHistoryRecord
import com.example.domain.WorkoutIntensityMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApexRepository(
    private val dao: ApexDao,
    private val prefsRepo: CalibrationPreferencesRepository
) {
    val calibrationProfileFlow: Flow<CalibrationProfileState> = prefsRepo.profileFlow
    val allSetLogsFlow: Flow<List<CombatSetLogEntity>> = dao.observeAllSetLogs()
    val allDailyComplianceFlow: Flow<List<DailyComplianceEntity>> = dao.observeAllDailyCompliance()
    val allWeeklyAuditsFlow: Flow<List<WeeklyAuditEntity>> = dao.observeAllWeeklyAudits()

    fun todayIso(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date())
    }

    private fun formatEpochToIso(epochMs: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return formatter.format(Date(epochMs))
    }

    suspend fun ensureSeededTelemetryHistory() {
        // Purge any legacy fake seeded demo rows so the Progress tab only shows real user check-ins!
        val legacyFakeTags = setOf("WEEK 01 BASELINE", "WEEK 02 AUDIT", "WEEK 03 AUDIT")
        val existing = dao.observeAllWeeklyAudits().first()
        existing.filter { it.auditTag in legacyFakeTags }.forEach { fakeRow ->
            dao.deleteWeeklyAuditById(fakeRow.id)
        }
    }

    suspend fun updateBiologicalSex(sex: BiologicalSex) =
        prefsRepo.updateBiologicalSex(sex)

    suspend fun updateAgeTier(ageTier: AgeTier) =
        prefsRepo.updateAgeTier(ageTier)

    suspend fun updateExperienceLevel(experience: ExperienceLevel) =
        prefsRepo.updateExperienceLevel(experience)

    suspend fun updateWeeklyFrequency(frequency: WeeklyFrequency) =
        prefsRepo.updateWeeklyFrequency(frequency)

    suspend fun toggleRestDayIso(dayIso: Int) =
        prefsRepo.toggleRestDayIso(dayIso)

    suspend fun updateCurrentBracket(bracket: CurrentBfBracket) =
        prefsRepo.updateCurrentBracket(bracket)

    suspend fun updateGoalBracket(bracket: GoalBfBracket) =
        prefsRepo.updateGoalBracket(bracket)

    suspend fun updateBiometrics(weightKg: Float, heightCm: Float, age: Int) =
        prefsRepo.updateBiometrics(weightKg, heightCm, age)

    suspend fun toggleWeakpoint(weakpointId: String) =
        prefsRepo.toggleWeakpoint(weakpointId)

    suspend fun updateActiveCombatDay(dayIndex: Int) =
        prefsRepo.updateActiveCombatDay(dayIndex)

    suspend fun setAlarmsArmed(armed: Boolean) =
        prefsRepo.setAlarmsArmed(armed)

    suspend fun persistPickedPhoto(slotNumber: Int, uri: Uri): String? =
        prefsRepo.persistPickedPhoto(slotNumber, uri)

    suspend fun clearPickedPhoto(slotNumber: Int) =
        prefsRepo.clearPickedPhoto(slotNumber)

    suspend fun setExerciseSetCompletion(
        dayId: String,
        exerciseId: String,
        setNumber: Int,
        loadKg: Float,
        isCompleted: Boolean
    ) {
        val key = CombatSetLogEntity.buildKey(dayId, exerciseId, setNumber)
        prefsRepo.setExerciseSetCompletedInDataStore(key, isCompleted)
        dao.upsertSetLog(
            CombatSetLogEntity(
                setKey = key,
                dayId = dayId,
                exerciseId = exerciseId,
                setNumber = setNumber,
                loadKg = loadKg,
                isCompleted = isCompleted,
                completedAtEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetCombatDay(dayId: String) {
        prefsRepo.clearDaySetsInDataStore(dayId)
        dao.resetDaySetLogs(dayId)
    }

    suspend fun updateConsumedProteinGrams(grams: Int, dailyFloorGrams: Int) {
        val safeGrams = grams.coerceIn(0, 400)
        prefsRepo.updateConsumedProteinGrams(safeGrams)
        val reachedFloor = safeGrams >= dailyFloorGrams
        updateTodayCompliance { current ->
            current.copy(proteinFloorLocked = reachedFloor)
        }
    }

    suspend fun setSteps10kChecked(checked: Boolean) {
        prefsRepo.setSteps10kChecked(checked)
        updateTodayCompliance { current ->
            current.copy(steps10kReached = checked)
        }
    }

    suspend fun setZeroLiquidSugarChecked(checked: Boolean) {
        prefsRepo.setZeroLiquidSugarChecked(checked)
        updateTodayCompliance { current ->
            current.copy(zeroSugarZeroFried = checked)
        }
    }

    suspend fun setTrainingEnvironment(environment: com.example.data.local.TrainingEnvironment) =
        prefsRepo.updateTrainingEnvironment(environment)

    suspend fun setPreWaterElectrolyteChecked(checked: Boolean) =
        prefsRepo.setPreWaterElectrolyteChecked(checked)

    suspend fun setPreProteinCarbChecked(checked: Boolean) =
        prefsRepo.setPreProteinCarbChecked(checked)

    suspend fun setPreEquipmentScoutedChecked(checked: Boolean) =
        prefsRepo.setPreEquipmentScoutedChecked(checked)

    suspend fun setPostWorkoutProtein40gChecked(checked: Boolean) =
        prefsRepo.setPostWorkoutProtein40gChecked(checked)

    suspend fun setKitchenClosedBy8pmChecked(checked: Boolean) {
        prefsRepo.setKitchenClosedBy8pmChecked(checked)
        updateTodayCompliance { current ->
            current.copy(kitchenClosedBy8pm = checked)
        }
    }

    suspend fun updateTodayCompliance(
        transform: (DailyComplianceEntity) -> DailyComplianceEntity
    ) {
        val date = todayIso()
        val current = dao.getComplianceForDate(date) ?: DailyComplianceEntity(dateIso = date)
        val updated = transform(current).copy(updatedAtEpochMs = System.currentTimeMillis())
        dao.upsertDailyCompliance(updated)
    }

    suspend fun logWeeklyAudit(
        morningDryWeightKg: Float,
        navelCircumferenceCm: Float,
        auditTag: String = "WEEKLY CHECK-IN",
        syncBodyweight: Boolean = true
    ) {
        prefsRepo.saveSundayAuditSnapshot(
            weightKg = morningDryWeightKg,
            waistCm = navelCircumferenceCm,
            syncBodyweight = syncBodyweight
        )
        val now = System.currentTimeMillis()
        dao.insertWeeklyAudit(
            WeeklyAuditEntity(
                recordedDateIso = formatEpochToIso(now),
                recordedEpochMs = now,
                morningDryWeightKg = morningDryWeightKg.coerceIn(40f, 220f),
                navelCircumferenceCm = navelCircumferenceCm.coerceIn(50f, 180f),
                auditTag = auditTag
            )
        )
    }

    suspend fun simulateFourteenDayStagnation(currentWeightKg: Float, stalledWaistCm: Float) {
        val now = System.currentTimeMillis()
        val dayMs = 24L * 60L * 60L * 1000L
        val tMinus15 = now - (15L * dayMs)
        val tMinus7 = now - (7L * dayMs)

        dao.clearAllWeeklyAudits()
        dao.insertWeeklyAudits(
            listOf(
                WeeklyAuditEntity(
                    recordedDateIso = formatEpochToIso(tMinus15),
                    recordedEpochMs = tMinus15,
                    morningDryWeightKg = currentWeightKg + 0.3f,
                    navelCircumferenceCm = stalledWaistCm,
                    auditTag = "DAY -15 ANCHOR"
                ),
                WeeklyAuditEntity(
                    recordedDateIso = formatEpochToIso(tMinus7),
                    recordedEpochMs = tMinus7,
                    morningDryWeightKg = currentWeightKg + 0.1f,
                    navelCircumferenceCm = stalledWaistCm + 0.1f,
                    auditTag = "DAY -07 CHECK-IN"
                ),
                WeeklyAuditEntity(
                    recordedDateIso = formatEpochToIso(now),
                    recordedEpochMs = now,
                    morningDryWeightKg = currentWeightKg,
                    navelCircumferenceCm = stalledWaistCm,
                    auditTag = "DAY 00 STALLED"
                )
            )
        )
    }

    suspend fun deleteWeeklyAudit(id: Long) {
        dao.deleteWeeklyAuditById(id)
    }

    suspend fun logMealProtein(mealTag: String, grams: Int) =
        prefsRepo.logMealProtein(mealTag, grams)

    suspend fun addFoodTableProtein(grams: Int, dailyFloorGrams: Int) {
        prefsRepo.addFoodTableProtein(grams)
    }

    suspend fun updateSelectedShapeCardIds(startCardId: String?, goalCardId: String?) =
        prefsRepo.updateSelectedShapeCardIds(startCardId, goalCardId)

    suspend fun updateSupplementPreference(preference: com.example.domain.SupplementPreference) =
        prefsRepo.updateSupplementPreference(preference)

    suspend fun setCreatine5gChecked(checked: Boolean) =
        prefsRepo.setCreatine5gChecked(checked)

    suspend fun setZeroCandySweetsChecked(checked: Boolean) =
        prefsRepo.setZeroCandySweetsChecked(checked)

    suspend fun setZeroFriedJunkChecked(checked: Boolean) =
        prefsRepo.setZeroFriedJunkChecked(checked)

    suspend fun incrementCravingsDefeated() =
        prefsRepo.incrementCravingsDefeated()

    suspend fun addWaterMilliliters(additionalMl: Int) =
        prefsRepo.addWaterMilliliters(additionalMl)

    suspend fun resetWaterMilliliters() =
        prefsRepo.resetWaterMilliliters()

    suspend fun resetDailyNutrition() =
        prefsRepo.resetDailyNutrition()

    suspend fun setVoiceCoachEnabled(enabled: Boolean) =
        prefsRepo.setVoiceCoachEnabled(enabled)

    suspend fun incrementCompletedWorkoutsCount() =
        prefsRepo.incrementCompletedWorkoutsCount()

    suspend fun setWeekRotationOverride(weekOrNull: Int?) =
        prefsRepo.setWeekRotationOverride(weekOrNull)

    suspend fun updateReminderSchedule(
        checkpointId: Int,
        hourOfDay: Int,
        minute: Int,
        enabled: Boolean
    ) = prefsRepo.updateReminderSchedule(checkpointId, hourOfDay, minute, enabled)

    suspend fun updateAppLanguageCode(code: String) =
        prefsRepo.updateAppLanguageCode(code)

    suspend fun setMeasurementsKnown(known: Boolean) =
        prefsRepo.setMeasurementsKnown(known)

    suspend fun updateExercisesPerDay(count: Int) =
        prefsRepo.updateExercisesPerDay(count)

    suspend fun updateWorkoutIntensity(mode: WorkoutIntensityMode) =
        prefsRepo.updateWorkoutIntensity(mode)

    suspend fun setAutoPlayWorkoutMusic(enabled: Boolean) =
        prefsRepo.setAutoPlayWorkoutMusic(enabled)

    suspend fun updateAudioCoachMode(mode: WorkoutAudioCoachMode) =
        prefsRepo.updateAudioCoachMode(mode)

    suspend fun updateRestDurationSeconds(seconds: Int) =
        prefsRepo.updateRestDurationSeconds(seconds)

    suspend fun setIncludeWarmupAndStretch(included: Boolean) =
        prefsRepo.setIncludeWarmupAndStretch(included)

    suspend fun setUseMetricUnits(metric: Boolean) =
        prefsRepo.setUseMetricUnits(metric)

    suspend fun setKeepScreenAwake(awake: Boolean) =
        prefsRepo.setKeepScreenAwake(awake)

    suspend fun setVibrationEnabled(enabled: Boolean) =
        prefsRepo.setVibrationEnabled(enabled)

    suspend fun updateUserFirstName(name: String) =
        prefsRepo.updateUserFirstName(name)

    suspend fun updatePlanStartDate(startDateIso: String, ensureTrainingDayIso: Int? = null) =
        prefsRepo.updatePlanStartDate(startDateIso, ensureTrainingDayIso)

    suspend fun appendWorkoutHistoryRecord(record: WorkoutHistoryRecord) =
        prefsRepo.appendWorkoutHistoryRecord(record)

    suspend fun markChallengeDayCompleted(challengeId: String, dayNumber: Int) =
        prefsRepo.markChallengeDayCompleted(challengeId, dayNumber)

    suspend fun resetAllProgressData() {
        dao.clearAllWeeklyAudits()
        val existingDayIds = dao.observeAllSetLogs().first().map { it.dayId }.toSet()
        existingDayIds.forEach { dayId ->
            dao.resetDaySetLogs(dayId)
        }
        prefsRepo.resetAllProgressInDataStore()
        updateTodayCompliance { current ->
            current.copy(
                steps10kReached = false,
                zeroSugarZeroFried = false,
                proteinFloorLocked = false,
                kitchenClosedBy8pm = false
            )
        }
    }
}

