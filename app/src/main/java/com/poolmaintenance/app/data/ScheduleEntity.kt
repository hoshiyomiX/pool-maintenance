package com.poolmaintenance.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Recurrence rule constants for schedule loop.
 */
object RecurrenceRule {
    const val EVERY_4_DAYS = "every_4_days"
    const val WEEKLY = "weekly"
    const val MONTHLY = "monthly"
}

/**
 * Represents a recurring maintenance schedule for a villa.
 * Each schedule generates automatic reminders based on its recurrence rule.
 *
 * Flow:
 * - User creates a schedule from the MapScreen (villa → type → start date/time → data)
 * - ScheduleWorker checks daily for due schedules
 * - When nextDueDate arrives: create MaintenanceRecord, send notification, update nextDueDate
 * - User sees reminder in Tab 1, taps to view previous data + edit current data
 *
 * scheduledTime: Only used for Monitoring type — stores hour (0-23) and minute (0-59).
 * For non-Monitoring types, default is -1 (not set).
 */
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val villaNumber: Int,
    val scheduleType: String,
    val startDate: Long,
    val nextDueDate: Long,
    val recurrenceRule: String,
    val isActive: Boolean = true,
    val scheduledHour: Int = -1,
    val scheduledMinute: Int = -1,
    val createdAt: Long = System.currentTimeMillis()
)
