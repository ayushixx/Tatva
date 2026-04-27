package com.tatva.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.tatva.app.R

class NotificationHelper(private val context: Context) {
    private val channelId = "tatva_alerts"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Tatva Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showVictimDetected(distance: Double) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("VICTIM DETECTED")
            .setContentText("Victim detected at ${String.format("%.2f", distance)} meters")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1, notification)
    }

    fun showBeaconActive() {
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("RESCUE BEACON ACTIVE")
            .setContentText("You are currently in Victim Mode")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        notificationManager.notify(2, notification)
    }

    fun cancelBeaconActive() {
        notificationManager.cancel(2)
    }
}
