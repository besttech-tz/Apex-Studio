package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CombatSetLogEntity::class,
        DailyComplianceEntity::class,
        WeeklyAuditEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ApexDatabase : RoomDatabase() {

    abstract fun apexDao(): ApexDao

    companion object {
        @Volatile
        private var INSTANCE: ApexDatabase? = null

        fun getInstance(context: Context): ApexDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ApexDatabase::class.java,
                    "apex_protocol_telemetry.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
