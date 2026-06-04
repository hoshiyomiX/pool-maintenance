package com.poolmaintenance.app.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VillaRepository @Inject constructor(
    private val villaDao: VillaDao
) {
    suspend fun insertRecord(record: MaintenanceRecord): Long {
        return villaDao.insertRecord(record)
    }

    suspend fun getRecordsForVilla(villaNumber: Int): List<MaintenanceRecord> {
        return villaDao.getRecordsForVilla(villaNumber)
    }

    fun getRecordsForVillaFlow(villaNumber: Int): Flow<List<MaintenanceRecord>> {
        return villaDao.getRecordsForVillaFlow(villaNumber)
    }

    fun getAllRecordsFlow(): Flow<List<MaintenanceRecord>> {
        return villaDao.getAllRecordsFlow()
    }

    suspend fun getTodayReminders(): List<MaintenanceRecord> {
        val (startOfDay, endOfDay) = getTodayRange()
        return villaDao.getTodayReminders(startOfDay, endOfDay)
    }

    fun getTodayRemindersFlow(): Flow<List<MaintenanceRecord>> {
        val (startOfDay, endOfDay) = getTodayRange()
        return villaDao.getTodayRemindersFlow(startOfDay, endOfDay)
    }

    suspend fun getScheduleForVillaOnDate(villaNumber: Int, dateMs: Long): List<MaintenanceRecord> {
        val (startOfDay, endOfDay) = getDayRange(dateMs)
        return villaDao.getScheduleForVillaOnDate(villaNumber, startOfDay, endOfDay)
    }

    suspend fun markCompleted(id: Long) {
        villaDao.markCompleted(id)
    }

    suspend fun markIncomplete(id: Long) {
        villaDao.markIncomplete(id)
    }

    suspend fun getStatsInRange(startMs: Long, endMs: Long): AggregatedStats {
        return villaDao.getStatsInRange(startMs, endMs)
    }

    suspend fun getPerVillaStatsInRange(startMs: Long, endMs: Long): List<VillaStats> {
        return villaDao.getPerVillaStatsInRange(startMs, endMs)
    }

    suspend fun deleteRecord(id: Long) {
        villaDao.deleteRecord(id)
    }

    private fun getTodayRange(): Pair<Long, Long> {
        return getDayRange(System.currentTimeMillis())
    }

    private fun getDayRange(timestampMs: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestampMs
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    companion object {
        /**
         * Line A: Villa 02 to Villa 26 (25 villas).
         * Upper horizontal row, pools facing down toward partition.
         */
        val LINE_A: List<Int> = (2..26).toList()

        /**
         * Line B: Villa 28 to Villa 49 (22 villas).
         * Middle horizontal row, pools facing up toward Line A partition.
         * Offset 3 villa positions so V49 aligns with V26.
         */
        val LINE_B: List<Int> = (28..49).toList()

        /**
         * Line C: Villa 50 to Villa 63 (14 villas).
         * Lower horizontal row, pools facing down (same as Line A).
         * Offset 5 villa positions so V50 aligns with V30.
         */
        val LINE_C: List<Int> = (50..63).toList()

        /**
         * All villa numbers on the denah (map).
         * Total: 61 villas (25 + 22 + 14).
         */
        val MAP_VILLA_NUMBERS: List<Int> = LINE_A + LINE_B + LINE_C

        /**
         * Legacy: All valid villa numbers (1-63 excluding 2 and 27).
         * Kept for backward compatibility with stats queries.
         */
        val VILLA_NUMBERS: List<Int> = (1..63).filter { it != 27 }
    }
}
