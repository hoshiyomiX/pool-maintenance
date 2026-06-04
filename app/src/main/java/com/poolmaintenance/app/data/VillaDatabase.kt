package com.poolmaintenance.app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [MaintenanceRecord::class],
    version = 3,
    exportSchema = false
)
abstract class VillaDatabase : RoomDatabase() {
    abstract fun villaDao(): VillaDao

    companion object {
        /**
         * Migration from v1 to v2: add scheduledDate and isCompleted columns.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN scheduledDate INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE maintenance_records ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
                db.execSQL("UPDATE maintenance_records SET scheduledDate = date WHERE scheduledDate = 0")
            }
        }

        /**
         * Migration from v2 to v3: add 6 chemical columns (granular, tablet, hcl, trusi, sodaAsh, pac).
         * Old obatAmount and hclAmount columns are kept for backward compatibility but are no longer used in UI.
         */
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
    }
}
