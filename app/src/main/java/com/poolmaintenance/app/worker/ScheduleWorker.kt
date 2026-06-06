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
import java.util.Calendar

/**
 * WorkManager worker that runs daily to check for due schedules.
 * For each due schedule:
 * 1. Create a new MaintenanceRecord (empty data, isCompleted = false)
 * 2. Update the schedule's nextDueDate
 * 3. Send a push notification
 *
 * For Monitoring schedules with a set time, the notification is triggered
 * only when the current hour matches the scheduled time.
 * For other types, notifications are sent on the daily check.
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
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            for (schedule in dueSchedules) {
                // For Monitoring with a set time: only notify at the scheduled hour
                // For other types: always notify on the daily check
                val shouldNotify = when (schedule.scheduleType) {
                    ScheduleType.MONITORING -> {
                        if (schedule.scheduledHour >= 0) {
                            // Only notify if current hour matches scheduled hour (within 1-hour window)
                            currentHour == schedule.scheduledHour
                        } else {
                            true // No time set, notify on daily check
                        }
                    }
                    else -> true // Treatment Mingguan & Deep Treatment always notify
                }

                // Always create the record regardless of notification timing
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

                // Send notification only if time matches
                if (shouldNotify) {
                    NotificationHelper.showScheduleNotification(
                        applicationContext,
                        schedule.villaNumber,
                        schedule.scheduleType,
                        recordId
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
