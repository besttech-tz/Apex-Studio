package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "combat_set_logs")
data class CombatSetLogEntity(
    @PrimaryKey val setKey: String,
    val dayId: String,
    val exerciseId: String,
    val setNumber: Int,
    val loadKg: Float,
    val isCompleted: Boolean,
    val completedAtEpochMs: Long
) {
    companion object {
        fun buildKey(dayId: String, exerciseId: String, setNumber: Int): String =
            "${dayId}_${exerciseId}_set_$setNumber"
    }
}

@Entity(tableName = "daily_compliance_ledger")
data class DailyComplianceEntity(
    @PrimaryKey val dateIso: String,
    val steps10kReached: Boolean = false,
    val zeroSugarZeroFried: Boolean = false,
    val proteinFloorLocked: Boolean = false,
    val kitchenClosedBy8pm: Boolean = false,
    val updatedAtEpochMs: Long = System.currentTimeMillis()
) {
    val completedCount: Int
        get() = listOf(
            steps10kReached,
            zeroSugarZeroFried,
            proteinFloorLocked,
            kitchenClosedBy8pm
        ).count { it }

    val compliancePercentage: Int
        get() = completedCount * 25
}

@Entity(tableName = "weekly_audit_matrix")
data class WeeklyAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val recordedDateIso: String,
    val recordedEpochMs: Long,
    val morningDryWeightKg: Float,
    val navelCircumferenceCm: Float,
    val auditTag: String = "SUNDAY CHECK-IN"
)
