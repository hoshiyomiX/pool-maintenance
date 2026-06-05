package com.poolmaintenance.app.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VillaRepository @Inject constructor(
    private val villaDao: VillaDao,
    private val scheduleDao: ScheduleDao
) {
    // ── MaintenanceRecord operations ──────────────────────────

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

    suspend fun getRecordById(id: Long): MaintenanceRecord? {
        return villaDao.getRecordById(id)
    }

    suspend fun getPreviousCompletedRecord(scheduleId: Long, beforeDate: Long): MaintenanceRecord? {
        if (scheduleId == 0L) return null
        return villaDao.getPreviousCompletedRecord(scheduleId, beforeDate)
    }

    suspend fun getPreviousRecordByType(villaNumber: Int, scheduleType: String, beforeDate: Long): MaintenanceRecord? {
        return villaDao.getPreviousRecordByType(villaNumber, scheduleType, beforeDate)
    }

    suspend fun updateRecordData(
        id: Long,
        granular: Double, tablet: Double, hcl: Double, trusi: Double,
        sodaAsh: Double, pac: Double, tesPh: Double, tesChlorine: Double,
        vakum: Double, brushing: Double, kurasBalancing: Double,
        checkStatus: String
    ) {
        villaDao.updateRecordData(id, granular, tablet, hcl, trusi, sodaAsh, pac, tesPh, tesChlorine, vakum, brushing, kurasBalancing, checkStatus)
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

    // ── Schedule operations ────────────────────────────────────

    suspend fun insertSchedule(schedule: Schedule): Long {
        return scheduleDao.insertSchedule(schedule)
    }

    suspend fun getActiveSchedules(): List<Schedule> {
        return scheduleDao.getActiveSchedules()
    }

    fun getActiveSchedulesFlow(): Flow<List<Schedule>> {
        return scheduleDao.getActiveSchedulesFlow()
    }

    suspend fun getSchedulesForVilla(villaNumber: Int): List<Schedule> {
        return scheduleDao.getSchedulesForVilla(villaNumber)
    }

    suspend fun getScheduleById(id: Long): Schedule? {
        return scheduleDao.getScheduleById(id)
    }

    suspend fun getDueSchedules(): List<Schedule> {
        val (startOfDay, endOfDay) = getTodayRange()
        return scheduleDao.getDueSchedules(startOfDay, endOfDay)
    }

    suspend fun updateNextDueDate(id: Long, nextDueDate: Long) {
        scheduleDao.updateNextDueDate(id, nextDueDate)
    }

    suspend fun deactivateSchedule(id: Long) {
        scheduleDao.deactivateSchedule(id)
    }

    suspend fun deleteSchedule(id: Long) {
        scheduleDao.deleteSchedule(id)
    }

    // ── Recurrence calculation ─────────────────────────────────

    /**
     * Calculate the next due date based on the recurrence rule.
     * - Monitoring: every 4 days
     * - Treatment Mingguan: weekly (7 days)
     * - Deep Treatment: monthly (same date next month)
     */
    fun calculateNextDueDate(currentDueDate: Long, recurrenceRule: String): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentDueDate

        when (recurrenceRule) {
            RecurrenceRule.EVERY_4_DAYS -> calendar.add(Calendar.DAY_OF_MONTH, 4)
            RecurrenceRule.WEEKLY -> calendar.add(Calendar.DAY_OF_MONTH, 7)
            RecurrenceRule.MONTHLY -> calendar.add(Calendar.MONTH, 1)
        }

        return calendar.timeInMillis
    }

    /**
     * Get the recurrence rule for a schedule type.
     */
    fun getRecurrenceRule(scheduleType: String): String {
        return when (scheduleType) {
            ScheduleType.MONITORING -> RecurrenceRule.EVERY_4_DAYS
            ScheduleType.TREATMENT_MINGGUAN -> RecurrenceRule.WEEKLY
            ScheduleType.DEEP_TREATMENT -> RecurrenceRule.MONTHLY
            else -> RecurrenceRule.EVERY_4_DAYS
        }
    }

    // ── Date utility ───────────────────────────────────────────

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
        val LINE_A: List<Int> = (2..26).toList()
        val LINE_B: List<Int> = (28..49).toList()
        val LINE_C: List<Int> = (50..63).toList()
        val MAP_VILLA_NUMBERS: List<Int> = LINE_A + LINE_B + LINE_C
        val VILLA_NUMBERS: List<Int> = (1..63).filter { it != 27 }
    }
}
