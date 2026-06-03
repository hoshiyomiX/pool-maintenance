package com.poolmaintenance.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VillaDao {

    @Insert
    suspend fun insertRecord(record: MaintenanceRecord): Long

    @Query("SELECT * FROM maintenance_records WHERE villaNumber = :villaNumber ORDER BY date DESC")
    suspend fun getRecordsForVilla(villaNumber: Int): List<MaintenanceRecord>

    @Query("SELECT * FROM maintenance_records WHERE villaNumber = :villaNumber ORDER BY date DESC")
    fun getRecordsForVillaFlow(villaNumber: Int): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    @Query("SELECT * FROM maintenance_records ORDER BY date DESC")
    fun getAllRecordsFlow(): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    /**
     * Aggregated stats for a specific time range.
     */
    @Query("""
        SELECT COALESCE(SUM(obatAmount), 0.0) AS totalObat,
               COALESCE(SUM(hclAmount), 0.0) AS totalHcl,
               COUNT(*) AS totalChecks
        FROM maintenance_records
        WHERE date BETWEEN :startMs AND :endMs
    """)
    suspend fun getStatsInRange(startMs: Long, endMs: Long): AggregatedStats

    /**
     * Per-villa aggregated stats for a specific time range.
     */
    @Query("""
        SELECT villaNumber,
               COALESCE(SUM(obatAmount), 0.0) AS totalObat,
               COALESCE(SUM(hclAmount), 0.0) AS totalHcl,
               COUNT(*) AS totalChecks
        FROM maintenance_records
        WHERE date BETWEEN :startMs AND :endMs
        GROUP BY villaNumber
        ORDER BY villaNumber
    """)
    suspend fun getPerVillaStatsInRange(startMs: Long, endMs: Long): List<VillaStats>

    @Query("DELETE FROM maintenance_records WHERE id = :id")
    suspend fun deleteRecord(id: Long)
}

/**
 * Per-villa aggregated statistics.
 */
data class VillaStats(
    val villaNumber: Int,
    val totalObat: Double,
    val totalHcl: Double,
    val totalChecks: Int
)
