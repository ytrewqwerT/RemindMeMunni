package com.example.remindmemunni.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.remindmemunni.R
import com.example.remindmemunni.data.Item

class NotificationScheduler(context: Context) {
    private val appContext = context.applicationContext

    fun createNotification(title: String): Notification {
        val channelId = appContext.getString(R.string.notification_channel_id)
        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .build()
    }

    fun scheduleNotificationForItem(item: Item) {
        val notif = createNotification(item.name)

        val intent = Intent(appContext, NotificationPublisher::class.java)
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, item.id)
        intent.putExtra(NotificationPublisher.NOTIFICATION_CONTENT, notif)
        val pendingIntent = PendingIntent.getBroadcast(appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC, item.time * 1000, pendingIntent)
    }
}