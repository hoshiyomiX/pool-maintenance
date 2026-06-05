package com.poolmaintenance.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insertSchedule(schedule: Schedule): Long

    @Query("SELECT * FROM schedules WHERE isActive = 1")
    suspend fun getActiveSchedules(): List<Schedule>

    @Query("SELECT * FROM schedules WHERE isActive = 1")
    fun getActiveSchedulesFlow(): kotlinx.coroutines.flow.Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE villaNumber = :villaNumber ORDER BY createdAt DESC")
    suspend fun getSchedulesForVilla(villaNumber: Int): List<Schedule>

    @Query("SELECT * FROM schedules WHERE id = :id")
    suspend fun getScheduleById(id: Long): Schedule?

    @Query("""
        SELECT * FROM schedules
        WHERE isActive = 1 AND nextDueDate >= :startOfDay AND nextDueDate < :endOfDay
    """)
    suspend fun getDueSchedules(startOfDay: Long, endOfDay: Long): List<Schedule>

    @Query("""
        UPDATE schedules SET nextDueDate = :nextDueDate WHERE id = :id
    """)
    suspend fun updateNextDueDate(id: Long, nextDueDate: Long)

    @Query("UPDATE schedules SET isActive = 0 WHERE id = :id")
    suspend fun deactivateSchedule(id: Long)

    @Query("DELETE FROM schedules WHERE id = :id")
    suspend fun deleteSchedule(id: Long)
}
