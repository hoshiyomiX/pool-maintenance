package com.poolmaintenance.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VillaDao {

    @Insert
    suspend fun insertRecord(record: MaintenanceRecord): Long

    @Query("SELECT * FROM maintenance_records WHERE villaNumber = :villaNumber ORDER BY scheduledDate DESC")
    suspend fun getRecordsForVilla(villaNumber: Int): List<MaintenanceRecord>

    @Query("SELECT * FROM maintenance_records WHERE villaNumber = :villaNumber ORDER BY scheduledDate DESC")
    fun getRecordsForVillaFlow(villaNumber: Int): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    @Query("SELECT * FROM maintenance_records ORDER BY scheduledDate DESC")
    fun getAllRecordsFlow(): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    /**
     * Get today's scheduled reminders.
     * Returns records where scheduledDate falls within today's date range (start of day to end of day).
     */
    @Query("""
        SELECT * FROM maintenance_records
        WHERE scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY villaNumber ASC
    """)
    suspend fun getTodayReminders(startOfDay: Long, endOfDay: Long): List<MaintenanceRecord>

    /**
     * Get today's scheduled reminders as Flow for reactive updates.
     */
    @Query("""
        SELECT * FROM maintenance_records
        WHERE scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY villaNumber ASC
    """)
    fun getTodayRemindersFlow(startOfDay: Long, endOfDay: Long): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    /**
     * Get schedule for a specific villa on a specific date.
     */
    @Query("""
        SELECT * FROM maintenance_records
        WHERE villaNumber = :villaNumber AND scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY scheduledDate DESC
    """)
    suspend fun getScheduleForVillaOnDate(villaNumber: Int, startOfDay: Long, endOfDay: Long): List<MaintenanceRecord>

    /**
     * Mark a record as completed.
     */
    @Query("UPDATE maintenance_records SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    /**
     * Mark a record as incomplete.
     */
    @Query("UPDATE maintenance_records SET isCompleted = 0 WHERE id = :id")
    suspend fun markIncomplete(id: Long)

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
