package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ApexDao {

    // Combat Deck Set Logs
    @Query("SELECT * FROM combat_set_logs")
    fun observeAllSetLogs(): Flow<List<CombatSetLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSetLog(log: CombatSetLogEntity)

    @Query("DELETE FROM combat_set_logs WHERE dayId = :dayId")
    suspend fun resetDaySetLogs(dayId: String)

    // Daily Compliance Ledger
    @Query("SELECT * FROM daily_compliance_ledger ORDER BY dateIso DESC")
    fun observeAllDailyCompliance(): Flow<List<DailyComplianceEntity>>

    @Query("SELECT * FROM daily_compliance_ledger WHERE dateIso = :dateIso LIMIT 1")
    suspend fun getComplianceForDate(dateIso: String): DailyComplianceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyCompliance(compliance: DailyComplianceEntity)

    // Weekly Audit Matrix
    @Query("SELECT * FROM weekly_audit_matrix ORDER BY recordedEpochMs DESC")
    fun observeAllWeeklyAudits(): Flow<List<WeeklyAuditEntity>>

    @Query("SELECT COUNT(*) FROM weekly_audit_matrix")
    suspend fun getWeeklyAuditCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyAudit(audit: WeeklyAuditEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyAudits(audits: List<WeeklyAuditEntity>)

    @Query("DELETE FROM weekly_audit_matrix WHERE id = :id")
    suspend fun deleteWeeklyAuditById(id: Long)

    @Query("DELETE FROM weekly_audit_matrix")
    suspend fun clearAllWeeklyAudits()
}
