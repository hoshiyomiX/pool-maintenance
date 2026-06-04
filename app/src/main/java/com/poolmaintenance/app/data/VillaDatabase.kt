package com.poolmaintenance.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MaintenanceRecord::class],
    version = 2,
    exportSchema = false
)
abstract class VillaDatabase : RoomDatabase() {
    abstract fun villaDao(): VillaDao

    companion object {
        /**
         * Migration from v1 to v2: add scheduledDate and isCompleted columns.
         * scheduledDate defaults to the existing date value.
         * isCompleted defaults to false (0).
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN scheduledDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
                // Copy existing date to scheduledDate for old records
                db.execSQL("UPDATE maintenance_records SET scheduledDate = date WHERE scheduledDate = 0")
            }
        }
    }
}
