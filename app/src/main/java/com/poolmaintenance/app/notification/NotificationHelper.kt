package com.poolmaintenance.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.poolmaintenance.app.MainActivity
import com.poolmaintenance.app.R

object NotificationHelper {

    const val CHANNEL_ID = "pool_maintenance_reminders"
    const val CHANNEL_NAME = "Pool Maintenance Reminders"
    const val NOTIFICATION_ID_BASE = 1000

    /**
     * Create the notification channel (required API 26+).
     * Call this from Application.onCreate().
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for pool maintenance schedules"
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a notification for a due schedule.
     */
    fun showScheduleNotification(
        context: Context,
        villaNumber: Int,
        scheduleType: String,
        recordId: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_reminder", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            recordId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Villa ${String.format("%02d", villaNumber)} - $scheduleType")
            .setContentText("Jadwal $scheduleType untuk Villa ${String.format("%02d", villaNumber)} hari ini")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_BASE + recordId.toInt(), notification)
    }
}
