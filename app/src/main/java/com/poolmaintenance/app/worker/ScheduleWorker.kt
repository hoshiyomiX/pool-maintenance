package com.poolmaintenance.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poolmaintenance.app.data.MaintenanceRecord
import com.poolmaintenance.app.data.ScheduleType
import com.poolmaintenance.app.data.VillaRepository
import com.poolmaintenance.app.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager worker that runs daily to check for due schedules.
 * For each due schedule:
 * 1. Create a new MaintenanceRecord (empty data, isCompleted = false)
 * 2. Update the schedule's nextDueDate
 * 3. Send a push notification
 */
@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: VillaRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val dueSchedules = repository.getDueSchedules()

            for (schedule in dueSchedules) {
                // Create empty MaintenanceRecord for the due date
                val record = MaintenanceRecord(
                    villaNumber = schedule.villaNumber,
                    date = System.currentTimeMillis(),
                    scheduledDate = schedule.nextDueDate,
                    scheduleType = schedule.scheduleType,
                    scheduleId = schedule.id,
                    checkStatus = "Belum Dicek",
                    isCompleted = false
                )

                val recordId = repository.insertRecord(record)

                // Calculate and update next due date
                val nextDue = repository.calculateNextDueDate(schedule.nextDueDate, schedule.recurrenceRule)
                repository.updateNextDueDate(schedule.id, nextDue)

                // Send notification
                NotificationHelper.showScheduleNotification(
                    applicationContext,
                    schedule.villaNumber,
                    schedule.scheduleType,
                    recordId
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
