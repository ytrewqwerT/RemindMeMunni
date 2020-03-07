package com.example.remindmemunni

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class NotificationScheduler(context: Context) {
    private val appContext = context.applicationContext

    fun createNotification(title: String): Notification {
        val channelId = appContext.getString(R.string.notification_channel_id)
        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .build()
    }

    fun scheduleNotification(notifId: Int, notifTime: Long, notif: Notification) {
        Log.e("NotificationScheduler", "Scheduling notification for $notifTime at ${Calendar.getInstance().timeInMillis}")
        val intent = Intent(appContext, NotificationPublisher::class.java)
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notifId)
        intent.putExtra(NotificationPublisher.NOTIFICATION_CONTENT, notif)
        val pendingIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC, notifTime, pendingIntent)
    }
}