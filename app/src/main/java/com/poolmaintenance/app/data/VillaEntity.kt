package com.poolmaintenance.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single pool maintenance record for a villa.
 * Each record captures chemical usage, check status, and scheduling information.
 *
 * Chemical fields with units:
 * - granular (kg) — chlorine granules
 * - tablet (tablet) — chlorine tablets
 * - hcl (liter) — hydrochloric acid
 * - trusi (kg) — trusi/tawas (alum)
 * - sodaAsh (kg) — sodium carbonate
 * - pac (liter) — poly aluminum chloride
 */
@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val villaNumber: Int,
    val date: Long,
    val scheduledDate: Long = date,
    val checkStatus: String,
    val isCompleted: Boolean = false,
    // Chemical fields
    val granular: Double = 0.0,
    val tablet: Double = 0.0,
    val hcl: Double = 0.0,
    val trusi: Double = 0.0,
    val sodaAsh: Double = 0.0,
    val pac: Double = 0.0,
    // Legacy fields kept for migration compatibility (unused in v3+)
    val obatAmount: Double = 0.0,
    val hclAmount: Double = 0.0
)

/**
 * Data class for aggregated statistics result from Room queries.
 */
data class AggregatedStats(
    val totalGranular: Double,
    val totalTablet: Double,
    val totalHcl: Double,
    val totalTrusi: Double,
    val totalSodaAsh: Double,
    val totalPac: Double,
    val totalChecks: Int
)

/**
 * Per-villa aggregated statistics with all 6 chemicals.
 */
data class VillaStats(
    val villaNumber: Int,
    val totalGranular: Double,
    val totalTablet: Double,
    val totalHcl: Double,
    val totalTrusi: Double,
    val totalSodaAsh: Double,
    val totalPac: Double,
    val totalChecks: Int
)
