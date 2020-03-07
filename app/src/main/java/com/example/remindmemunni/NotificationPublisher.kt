package com.example.remindmemunni

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationPublisher : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = "NOTIFICATION_ID"
        const val NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("NotificationPublisher", "Publishing Notification")
        val id = intent?.getIntExtra(NOTIFICATION_ID, 0) ?: 0
        val notification = intent?.getParcelableExtra<Notification>(NOTIFICATION_CONTENT)

        val notificationManager = context?.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        notificationManager.notify(id, notification)
    }

}