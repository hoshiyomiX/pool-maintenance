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

    @Query("""
        SELECT * FROM maintenance_records
        WHERE scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY villaNumber ASC
    """)
    suspend fun getTodayReminders(startOfDay: Long, endOfDay: Long): List<MaintenanceRecord>

    @Query("""
        SELECT * FROM maintenance_records
        WHERE scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY villaNumber ASC
    """)
    fun getTodayRemindersFlow(startOfDay: Long, endOfDay: Long): kotlinx.coroutines.flow.Flow<List<MaintenanceRecord>>

    @Query("""
        SELECT * FROM maintenance_records
        WHERE villaNumber = :villaNumber AND scheduledDate >= :startOfDay AND scheduledDate < :endOfDay
        ORDER BY scheduledDate DESC
    """)
    suspend fun getScheduleForVillaOnDate(villaNumber: Int, startOfDay: Long, endOfDay: Long): List<MaintenanceRecord>

    @Query("UPDATE maintenance_records SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("UPDATE maintenance_records SET isCompleted = 0 WHERE id = :id")
    suspend fun markIncomplete(id: Long)

    @Query("""
        SELECT COALESCE(SUM(granular), 0.0) AS totalGranular,
               COALESCE(SUM(tablet), 0.0) AS totalTablet,
               COALESCE(SUM(hcl), 0.0) AS totalHcl,
               COALESCE(SUM(trusi), 0.0) AS totalTrusi,
               COALESCE(SUM(sodaAsh), 0.0) AS totalSodaAsh,
               COALESCE(SUM(pac), 0.0) AS totalPac,
               COALESCE(SUM(tesPh), 0.0) AS totalTesPh,
               COALESCE(SUM(tesChlorine), 0.0) AS totalTesChlorine,
               COALESCE(SUM(vakum), 0.0) AS totalVakum,
               COALESCE(SUM(brushing), 0.0) AS totalBrushing,
               COALESCE(SUM(kurasBalancing), 0.0) AS totalKurasBalancing,
               COUNT(*) AS totalChecks
        FROM maintenance_records
        WHERE date BETWEEN :startMs AND :endMs
    """)
    suspend fun getStatsInRange(startMs: Long, endMs: Long): AggregatedStats

    @Query("""
        SELECT villaNumber,
               COALESCE(SUM(granular), 0.0) AS totalGranular,
               COALESCE(SUM(tablet), 0.0) AS totalTablet,
               COALESCE(SUM(hcl), 0.0) AS totalHcl,
               COALESCE(SUM(trusi), 0.0) AS totalTrusi,
               COALESCE(SUM(sodaAsh), 0.0) AS totalSodaAsh,
               COALESCE(SUM(pac), 0.0) AS totalPac,
               COALESCE(SUM(tesPh), 0.0) AS totalTesPh,
               COALESCE(SUM(tesChlorine), 0.0) AS totalTesChlorine,
               COALESCE(SUM(vakum), 0.0) AS totalVakum,
               COALESCE(SUM(brushing), 0.0) AS totalBrushing,
               COALESCE(SUM(kurasBalancing), 0.0) AS totalKurasBalancing,
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
