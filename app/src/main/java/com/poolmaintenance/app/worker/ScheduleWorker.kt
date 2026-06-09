package com.poolmaintenance.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.poolmaintenance.app.data.ScheduleType
import com.poolmaintenance.app.data.VillaRepository
import com.poolmaintenance.app.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

/**
 * WorkManager worker that runs periodically to process due schedules.
 *
 * Flow:
 * 1. ensureTodayRecords() — create missing MaintenanceRecords for due schedules
 *    and advance nextDueDate (idempotent — won't duplicate records)
 * 2. Send push notifications for today's records at the scheduled time
 *    - Monitoring: only at the set hour
 *    - Treatment Mingguan & Deep Treatment: on daily check
 */
@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: VillaRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Step 1: Ensure records exist for all due schedules (idempotent)
            repository.ensureTodayRecords()

            // Step 2: Send notifications for today's reminders
            val todayReminders = repository.getTodayReminders()
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            for (record in todayReminders) {
                val shouldNotify = when (record.scheduleType) {
                    ScheduleType.MONITORING -> {
                        // Find the schedule to check scheduled time
                        val schedule = repository.getScheduleById(record.scheduleId)
                        if (schedule != null && schedule.scheduledHour >= 0) {
                            currentHour == schedule.scheduledHour
                        } else {
                            true // No time set, notify on daily check
                        }
                    }
                    else -> true // Treatment Mingguan & Deep Treatment always notify
                }

                if (shouldNotify) {
                    NotificationHelper.showScheduleNotification(
                        applicationContext,
                        record.villaNumber,
                        record.scheduleType,
                        record.id
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
