package com.poolmaintenance.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Schedule type constants used in scheduleType column.
 */
object ScheduleType {
    const val MONITORING = "Monitoring"
    const val TREATMENT_MINGGUAN = "Treatment Mingguan"
    const val DEEP_TREATMENT = "Deep Treatment"
}

/**
 * Represents a single pool maintenance record for a villa.
 * Each record captures chemical usage, tasks performed, check status, and scheduling information.
 *
 * Schedule types:
 * - Monitoring: granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine
 * - Treatment Mingguan: vakum, brushing
 * - Deep Treatment: kurasBalancing
 */
@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val villaNumber: Int,
    val date: Long,
    val scheduledDate: Long = date,
    val scheduleType: String = ScheduleType.MONITORING,
    val scheduleId: Long = 0,
    val checkStatus: String,
    val isCompleted: Boolean = false,
    // Monitoring fields
    val granular: Double = 0.0,
    val tablet: Double = 0.0,
    val hcl: Double = 0.0,
    val trusi: Double = 0.0,
    val sodaAsh: Double = 0.0,
    val pac: Double = 0.0,
    val tesPh: Double = 0.0,
    val tesChlorine: Double = 0.0,
    // Treatment Mingguan fields
    val vakum: Double = 0.0,
    val brushing: Double = 0.0,
    // Deep Treatment fields
    val kurasBalancing: Double = 0.0,
    // Legacy fields kept for migration compatibility (unused in v4+)
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
    val totalTesPh: Double,
    val totalTesChlorine: Double,
    val totalVakum: Double,
    val totalBrushing: Double,
    val totalKurasBalancing: Double,
    val totalChecks: Int
)

/**
 * Per-villa aggregated statistics with all chemicals and tasks.
 */
data class VillaStats(
    val villaNumber: Int,
    val totalGranular: Double,
    val totalTablet: Double,
    val totalHcl: Double,
    val totalTrusi: Double,
    val totalSodaAsh: Double,
    val totalPac: Double,
    val totalTesPh: Double,
    val totalTesChlorine: Double,
    val totalVakum: Double,
    val totalBrushing: Double,
    val totalKurasBalancing: Double,
    val totalChecks: Int
)
