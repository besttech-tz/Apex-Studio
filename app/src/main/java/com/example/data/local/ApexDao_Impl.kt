@file:Suppress("RestrictedApi", "UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL")

package com.example.data.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

public class ApexDao_Impl(
    private val __db: RoomDatabase,
) : ApexDao {

    private val __insertAdapterOfCombatSetLogEntity: EntityInsertAdapter<CombatSetLogEntity> =
        object : EntityInsertAdapter<CombatSetLogEntity>() {
            protected override fun createQuery(): String =
                "INSERT OR REPLACE INTO `combat_set_logs` (`setKey`,`dayId`,`exerciseId`,`setNumber`,`loadKg`,`isCompleted`,`completedAtEpochMs`) VALUES (?,?,?,?,?,?,?)"

            protected override fun bind(statement: SQLiteStatement, entity: CombatSetLogEntity) {
                statement.bindText(1, entity.setKey)
                statement.bindText(2, entity.dayId)
                statement.bindText(3, entity.exerciseId)
                statement.bindLong(4, entity.setNumber.toLong())
                statement.bindDouble(5, entity.loadKg.toDouble())
                val _tmp: Int = if (entity.isCompleted) 1 else 0
                statement.bindLong(6, _tmp.toLong())
                statement.bindLong(7, entity.completedAtEpochMs)
            }
        }

    private val __insertAdapterOfDailyComplianceEntity: EntityInsertAdapter<DailyComplianceEntity> =
        object : EntityInsertAdapter<DailyComplianceEntity>() {
            protected override fun createQuery(): String =
                "INSERT OR REPLACE INTO `daily_compliance_ledger` (`dateIso`,`steps10kReached`,`zeroSugarZeroFried`,`proteinFloorLocked`,`kitchenClosedBy8pm`,`updatedAtEpochMs`) VALUES (?,?,?,?,?,?)"

            protected override fun bind(statement: SQLiteStatement, entity: DailyComplianceEntity) {
                statement.bindText(1, entity.dateIso)
                statement.bindLong(2, if (entity.steps10kReached) 1L else 0L)
                statement.bindLong(3, if (entity.zeroSugarZeroFried) 1L else 0L)
                statement.bindLong(4, if (entity.proteinFloorLocked) 1L else 0L)
                statement.bindLong(5, if (entity.kitchenClosedBy8pm) 1L else 0L)
                statement.bindLong(6, entity.updatedAtEpochMs)
            }
        }

    private val __insertAdapterOfWeeklyAuditEntity: EntityInsertAdapter<WeeklyAuditEntity> =
        object : EntityInsertAdapter<WeeklyAuditEntity>() {
            protected override fun createQuery(): String =
                "INSERT OR REPLACE INTO `weekly_audit_matrix` (`id`,`recordedDateIso`,`recordedEpochMs`,`morningDryWeightKg`,`navelCircumferenceCm`,`auditTag`) VALUES (nullif(?, 0),?,?,?,?,?)"

            protected override fun bind(statement: SQLiteStatement, entity: WeeklyAuditEntity) {
                statement.bindLong(1, entity.id)
                statement.bindText(2, entity.recordedDateIso)
                statement.bindLong(3, entity.recordedEpochMs)
                statement.bindDouble(4, entity.morningDryWeightKg.toDouble())
                statement.bindDouble(5, entity.navelCircumferenceCm.toDouble())
                statement.bindText(6, entity.auditTag)
            }
        }

    public override suspend fun upsertSetLog(log: CombatSetLogEntity): Unit =
        performSuspending(__db, false, true) { _connection ->
            __insertAdapterOfCombatSetLogEntity.insert(_connection, log)
        }

    public override suspend fun upsertDailyCompliance(compliance: DailyComplianceEntity): Unit =
        performSuspending(__db, false, true) { _connection ->
            __insertAdapterOfDailyComplianceEntity.insert(_connection, compliance)
        }

    public override suspend fun insertWeeklyAudit(audit: WeeklyAuditEntity): Unit =
        performSuspending(__db, false, true) { _connection ->
            __insertAdapterOfWeeklyAuditEntity.insert(_connection, audit)
        }

    public override suspend fun insertWeeklyAudits(audits: List<WeeklyAuditEntity>): Unit =
        performSuspending(__db, false, true) { _connection ->
            __insertAdapterOfWeeklyAuditEntity.insert(_connection, audits)
        }

    public override fun observeAllSetLogs(): Flow<List<CombatSetLogEntity>> {
        val _sql = "SELECT * FROM combat_set_logs"
        return createFlow(__db, false, arrayOf("combat_set_logs")) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                val _columnIndexOfSetKey = getColumnIndexOrThrow(_stmt, "setKey")
                val _columnIndexOfDayId = getColumnIndexOrThrow(_stmt, "dayId")
                val _columnIndexOfExerciseId = getColumnIndexOrThrow(_stmt, "exerciseId")
                val _columnIndexOfSetNumber = getColumnIndexOrThrow(_stmt, "setNumber")
                val _columnIndexOfLoadKg = getColumnIndexOrThrow(_stmt, "loadKg")
                val _columnIndexOfIsCompleted = getColumnIndexOrThrow(_stmt, "isCompleted")
                val _columnIndexOfCompletedAtEpochMs = getColumnIndexOrThrow(_stmt, "completedAtEpochMs")
                val _result: MutableList<CombatSetLogEntity> = mutableListOf()
                while (_stmt.step()) {
                    val _item = CombatSetLogEntity(
                        setKey = _stmt.getText(_columnIndexOfSetKey),
                        dayId = _stmt.getText(_columnIndexOfDayId),
                        exerciseId = _stmt.getText(_columnIndexOfExerciseId),
                        setNumber = _stmt.getLong(_columnIndexOfSetNumber).toInt(),
                        loadKg = _stmt.getDouble(_columnIndexOfLoadKg).toFloat(),
                        isCompleted = _stmt.getLong(_columnIndexOfIsCompleted).toInt() != 0,
                        completedAtEpochMs = _stmt.getLong(_columnIndexOfCompletedAtEpochMs)
                    )
                    _result.add(_item)
                }
                _result
            } finally {
                _stmt.close()
            }
        }
    }

    public override fun observeAllDailyCompliance(): Flow<List<DailyComplianceEntity>> {
        val _sql = "SELECT * FROM daily_compliance_ledger ORDER BY dateIso DESC"
        return createFlow(__db, false, arrayOf("daily_compliance_ledger")) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                val _columnIndexOfDateIso = getColumnIndexOrThrow(_stmt, "dateIso")
                val _columnIndexOfSteps10kReached = getColumnIndexOrThrow(_stmt, "steps10kReached")
                val _columnIndexOfZeroSugarZeroFried = getColumnIndexOrThrow(_stmt, "zeroSugarZeroFried")
                val _columnIndexOfProteinFloorLocked = getColumnIndexOrThrow(_stmt, "proteinFloorLocked")
                val _columnIndexOfKitchenClosedBy8pm = getColumnIndexOrThrow(_stmt, "kitchenClosedBy8pm")
                val _columnIndexOfUpdatedAtEpochMs = getColumnIndexOrThrow(_stmt, "updatedAtEpochMs")
                val _result: MutableList<DailyComplianceEntity> = mutableListOf()
                while (_stmt.step()) {
                    val _item = DailyComplianceEntity(
                        dateIso = _stmt.getText(_columnIndexOfDateIso),
                        steps10kReached = _stmt.getLong(_columnIndexOfSteps10kReached).toInt() != 0,
                        zeroSugarZeroFried = _stmt.getLong(_columnIndexOfZeroSugarZeroFried).toInt() != 0,
                        proteinFloorLocked = _stmt.getLong(_columnIndexOfProteinFloorLocked).toInt() != 0,
                        kitchenClosedBy8pm = _stmt.getLong(_columnIndexOfKitchenClosedBy8pm).toInt() != 0,
                        updatedAtEpochMs = _stmt.getLong(_columnIndexOfUpdatedAtEpochMs)
                    )
                    _result.add(_item)
                }
                _result
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun getComplianceForDate(dateIso: String): DailyComplianceEntity? {
        val _sql = "SELECT * FROM daily_compliance_ledger WHERE dateIso = ? LIMIT 1"
        return performSuspending(__db, true, false) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.bindText(1, dateIso)
                val _columnIndexOfDateIso = getColumnIndexOrThrow(_stmt, "dateIso")
                val _columnIndexOfSteps10kReached = getColumnIndexOrThrow(_stmt, "steps10kReached")
                val _columnIndexOfZeroSugarZeroFried = getColumnIndexOrThrow(_stmt, "zeroSugarZeroFried")
                val _columnIndexOfProteinFloorLocked = getColumnIndexOrThrow(_stmt, "proteinFloorLocked")
                val _columnIndexOfKitchenClosedBy8pm = getColumnIndexOrThrow(_stmt, "kitchenClosedBy8pm")
                val _columnIndexOfUpdatedAtEpochMs = getColumnIndexOrThrow(_stmt, "updatedAtEpochMs")
                if (_stmt.step()) {
                    DailyComplianceEntity(
                        dateIso = _stmt.getText(_columnIndexOfDateIso),
                        steps10kReached = _stmt.getLong(_columnIndexOfSteps10kReached).toInt() != 0,
                        zeroSugarZeroFried = _stmt.getLong(_columnIndexOfZeroSugarZeroFried).toInt() != 0,
                        proteinFloorLocked = _stmt.getLong(_columnIndexOfProteinFloorLocked).toInt() != 0,
                        kitchenClosedBy8pm = _stmt.getLong(_columnIndexOfKitchenClosedBy8pm).toInt() != 0,
                        updatedAtEpochMs = _stmt.getLong(_columnIndexOfUpdatedAtEpochMs)
                    )
                } else {
                    null
                }
            } finally {
                _stmt.close()
            }
        }
    }

    public override fun observeAllWeeklyAudits(): Flow<List<WeeklyAuditEntity>> {
        val _sql = "SELECT * FROM weekly_audit_matrix ORDER BY recordedEpochMs DESC"
        return createFlow(__db, false, arrayOf("weekly_audit_matrix")) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                val _columnIndexOfId = getColumnIndexOrThrow(_stmt, "id")
                val _columnIndexOfRecordedDateIso = getColumnIndexOrThrow(_stmt, "recordedDateIso")
                val _columnIndexOfRecordedEpochMs = getColumnIndexOrThrow(_stmt, "recordedEpochMs")
                val _columnIndexOfMorningDryWeightKg = getColumnIndexOrThrow(_stmt, "morningDryWeightKg")
                val _columnIndexOfNavelCircumferenceCm = getColumnIndexOrThrow(_stmt, "navelCircumferenceCm")
                val _columnIndexOfAuditTag = getColumnIndexOrThrow(_stmt, "auditTag")
                val _result: MutableList<WeeklyAuditEntity> = mutableListOf()
                while (_stmt.step()) {
                    val _item = WeeklyAuditEntity(
                        id = _stmt.getLong(_columnIndexOfId),
                        recordedDateIso = _stmt.getText(_columnIndexOfRecordedDateIso),
                        recordedEpochMs = _stmt.getLong(_columnIndexOfRecordedEpochMs),
                        morningDryWeightKg = _stmt.getDouble(_columnIndexOfMorningDryWeightKg).toFloat(),
                        navelCircumferenceCm = _stmt.getDouble(_columnIndexOfNavelCircumferenceCm).toFloat(),
                        auditTag = _stmt.getText(_columnIndexOfAuditTag)
                    )
                    _result.add(_item)
                }
                _result
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun getWeeklyAuditCount(): Int {
        val _sql = "SELECT COUNT(*) FROM weekly_audit_matrix"
        return performSuspending(__db, true, false) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                if (_stmt.step()) {
                    _stmt.getLong(0).toInt()
                } else {
                    0
                }
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun resetDaySetLogs(dayId: String) {
        val _sql = "DELETE FROM combat_set_logs WHERE dayId = ?"
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.bindText(1, dayId)
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun deleteWeeklyAuditById(id: Long) {
        val _sql = "DELETE FROM weekly_audit_matrix WHERE id = ?"
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.bindLong(1, id)
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun clearAllWeeklyAudits() {
        val _sql = "DELETE FROM weekly_audit_matrix"
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public companion object {
        public fun getRequiredConverters(): List<KClass<*>> = emptyList()
    }
}
