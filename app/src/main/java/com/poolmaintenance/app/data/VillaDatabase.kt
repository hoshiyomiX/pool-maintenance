package com.poolmaintenance.app.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MaintenanceRecord::class],
    version = 1,
    exportSchema = false
)
abstract class VillaDatabase : RoomDatabase() {
    abstract fun villaDao(): VillaDao
}
