package com.poolmaintenance.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MaintenanceRecord::class, Schedule::class],
    version = 5,
    exportSchema = false
)
abstract class VillaDatabase : RoomDatabase() {
    abstract fun villaDao(): VillaDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN scheduledDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE maintenance_records SET scheduledDate = date WHERE scheduledDate = 0")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN granular REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN tablet REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN hcl REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN trusi REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN sodaAsh REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN pac REAL NOT NULL DEFAULT 0.0")
            }
        }

        /**
         * Migration from v3 to v4:
         * - Add scheduleType column (default "Monitoring")
         * - Add tesPh, tesChlorine (Monitoring)
         * - Add vakum, brushing (Treatment Mingguan)
         * - Add kurasBalancing (Deep Treatment)
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN scheduleType TEXT NOT NULL DEFAULT 'Monitoring'")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN tesPh REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN tesChlorine REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN vakum REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN brushing REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN kurasBalancing REAL NOT NULL DEFAULT 0.0")
            }
        }

        /**
         * Migration from v4 to v5:
         * - Add scheduleId column to maintenance_records (default 0 = no schedule)
         * - Create schedules table for recurring schedule tracking
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add scheduleId to existing records
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN scheduleId INTEGER NOT NULL DEFAULT 0")

                // Create schedules table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS schedules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        villaNumber INTEGER NOT NULL,
                        scheduleType TEXT NOT NULL,
                        startDate INTEGER NOT NULL,
                        nextDueDate INTEGER NOT NULL,
                        recurrenceRule TEXT NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL
                    )
                """)
            }
        }
    }
}
