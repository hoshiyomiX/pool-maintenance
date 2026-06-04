package com.poolmaintenance.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single pool maintenance record for a villa.
 * Each record captures chemical usage, check status, and scheduling information.
 */
@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val villaNumber: Int,
    val date: Long,
    val obatAmount: Double,
    val hclAmount: Double,
    val checkStatus: String,
    val scheduledDate: Long = date,
    val isCompleted: Boolean = false
)

/**
 * Data class for aggregated statistics result from Room queries.
 */
data class AggregatedStats(
    val totalObat: Double,
    val totalHcl: Double,
    val totalChecks: Int
)
