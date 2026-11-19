@file:Suppress("RestrictedApi", "UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL")

package com.example.data.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlin.reflect.KClass

public class ApexDatabase_Impl : ApexDatabase() {
    private val _apexDao: Lazy<ApexDao> = lazy {
        ApexDao_Impl(this)
    }

    protected override fun createOpenDelegate(): RoomOpenDelegate {
        val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(
            1,
            "f9e68491f0f6940e9b0435bb2b036152",
            "6d44336ce57cb0cbea56722e76253f29"
        ) {
            public override fun createAllTables(connection: SQLiteConnection) {
                connection.execSQL("CREATE TABLE IF NOT EXISTS `combat_set_logs` (`setKey` TEXT NOT NULL, `dayId` TEXT NOT NULL, `exerciseId` TEXT NOT NULL, `setNumber` INTEGER NOT NULL, `loadKg` REAL NOT NULL, `isCompleted` INTEGER NOT NULL, `completedAtEpochMs` INTEGER NOT NULL, PRIMARY KEY(`setKey`))")
                connection.execSQL("CREATE TABLE IF NOT EXISTS `daily_compliance_ledger` (`dateIso` TEXT NOT NULL, `steps10kReached` INTEGER NOT NULL, `zeroSugarZeroFried` INTEGER NOT NULL, `proteinFloorLocked` INTEGER NOT NULL, `kitchenClosedBy8pm` INTEGER NOT NULL, `updatedAtEpochMs` INTEGER NOT NULL, PRIMARY KEY(`dateIso`))")
                connection.execSQL("CREATE TABLE IF NOT EXISTS `weekly_audit_matrix` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recordedDateIso` TEXT NOT NULL, `recordedEpochMs` INTEGER NOT NULL, `morningDryWeightKg` REAL NOT NULL, `navelCircumferenceCm` REAL NOT NULL, `auditTag` TEXT NOT NULL)")
                connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
                connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f9e68491f0f6940e9b0435bb2b036152')")
            }

            public override fun dropAllTables(connection: SQLiteConnection) {
                connection.execSQL("DROP TABLE IF EXISTS `combat_set_logs`")
                connection.execSQL("DROP TABLE IF EXISTS `daily_compliance_ledger`")
                connection.execSQL("DROP TABLE IF EXISTS `weekly_audit_matrix`")
            }

            public override fun onCreate(connection: SQLiteConnection) {
            }

            public override fun onOpen(connection: SQLiteConnection) {
                internalInitInvalidationTracker(connection)
            }

            public override fun onPreMigrate(connection: SQLiteConnection) {
                dropFtsSyncTriggers(connection)
            }

            public override fun onPostMigrate(connection: SQLiteConnection) {
            }

            public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
                val _columnsCombatSetLogs: MutableMap<String, TableInfo.Column> = mutableMapOf()
                _columnsCombatSetLogs["setKey"] = TableInfo.Column("setKey", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["dayId"] = TableInfo.Column("dayId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["exerciseId"] = TableInfo.Column("exerciseId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["setNumber"] = TableInfo.Column("setNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["loadKg"] = TableInfo.Column("loadKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["isCompleted"] = TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsCombatSetLogs["completedAtEpochMs"] = TableInfo.Column("completedAtEpochMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                val _foreignKeysCombatSetLogs: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
                val _indicesCombatSetLogs: MutableSet<TableInfo.Index> = mutableSetOf()
                val _infoCombatSetLogs = TableInfo("combat_set_logs", _columnsCombatSetLogs, _foreignKeysCombatSetLogs, _indicesCombatSetLogs)
                val _existingCombatSetLogs = read(connection, "combat_set_logs")
                if (_infoCombatSetLogs != _existingCombatSetLogs) {
                    return RoomOpenDelegate.ValidationResult(
                        false,
                        "combat_set_logs(com.example.data.local.CombatSetLogEntity).\n Expected:\n$_infoCombatSetLogs\n Found:\n$_existingCombatSetLogs"
                    )
                }

                val _columnsDailyComplianceLedger: MutableMap<String, TableInfo.Column> = mutableMapOf()
                _columnsDailyComplianceLedger["dateIso"] = TableInfo.Column("dateIso", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsDailyComplianceLedger["steps10kReached"] = TableInfo.Column("steps10kReached", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsDailyComplianceLedger["zeroSugarZeroFried"] = TableInfo.Column("zeroSugarZeroFried", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsDailyComplianceLedger["proteinFloorLocked"] = TableInfo.Column("proteinFloorLocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsDailyComplianceLedger["kitchenClosedBy8pm"] = TableInfo.Column("kitchenClosedBy8pm", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsDailyComplianceLedger["updatedAtEpochMs"] = TableInfo.Column("updatedAtEpochMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                val _foreignKeysDailyComplianceLedger: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
                val _indicesDailyComplianceLedger: MutableSet<TableInfo.Index> = mutableSetOf()
                val _infoDailyComplianceLedger = TableInfo("daily_compliance_ledger", _columnsDailyComplianceLedger, _foreignKeysDailyComplianceLedger, _indicesDailyComplianceLedger)
                val _existingDailyComplianceLedger = read(connection, "daily_compliance_ledger")
                if (_infoDailyComplianceLedger != _existingDailyComplianceLedger) {
                    return RoomOpenDelegate.ValidationResult(
                        false,
                        "daily_compliance_ledger(com.example.data.local.DailyComplianceEntity).\n Expected:\n$_infoDailyComplianceLedger\n Found:\n$_existingDailyComplianceLedger"
                    )
                }

                val _columnsWeeklyAuditMatrix: MutableMap<String, TableInfo.Column> = mutableMapOf()
                _columnsWeeklyAuditMatrix["id"] = TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsWeeklyAuditMatrix["recordedDateIso"] = TableInfo.Column("recordedDateIso", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsWeeklyAuditMatrix["recordedEpochMs"] = TableInfo.Column("recordedEpochMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsWeeklyAuditMatrix["morningDryWeightKg"] = TableInfo.Column("morningDryWeightKg", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsWeeklyAuditMatrix["navelCircumferenceCm"] = TableInfo.Column("navelCircumferenceCm", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsWeeklyAuditMatrix["auditTag"] = TableInfo.Column("auditTag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                val _foreignKeysWeeklyAuditMatrix: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
                val _indicesWeeklyAuditMatrix: MutableSet<TableInfo.Index> = mutableSetOf()
                val _infoWeeklyAuditMatrix = TableInfo("weekly_audit_matrix", _columnsWeeklyAuditMatrix, _foreignKeysWeeklyAuditMatrix, _indicesWeeklyAuditMatrix)
                val _existingWeeklyAuditMatrix = read(connection, "weekly_audit_matrix")
                if (_infoWeeklyAuditMatrix != _existingWeeklyAuditMatrix) {
                    return RoomOpenDelegate.ValidationResult(
                        false,
                        "weekly_audit_matrix(com.example.data.local.WeeklyAuditEntity).\n Expected:\n$_infoWeeklyAuditMatrix\n Found:\n$_existingWeeklyAuditMatrix"
                    )
                }
                return RoomOpenDelegate.ValidationResult(true, null)
            }
        }
        return _openDelegate
    }

    protected override fun createInvalidationTracker(): InvalidationTracker {
        val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
        val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
        return InvalidationTracker(
            this,
            _shadowTablesMap,
            _viewTables,
            "combat_set_logs",
            "daily_compliance_ledger",
            "weekly_audit_matrix"
        )
    }

    public override fun clearAllTables() {
        super.performClear(false, "combat_set_logs", "daily_compliance_ledger", "weekly_audit_matrix")
    }

    protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
        val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
        _typeConvertersMap[ApexDao::class] = ApexDao_Impl.getRequiredConverters()
        return _typeConvertersMap
    }

    public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
        return mutableSetOf()
    }

    public override fun createAutoMigrations(
        autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>
    ): List<Migration> {
        return mutableListOf()
    }

    public override fun apexDao(): ApexDao = _apexDao.value
}
