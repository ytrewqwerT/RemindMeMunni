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

    private fun createNotification(title: String): Notification {
        val channelId = appContext.getString(R.string.alerts)
        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .build()
    }

    fun scheduleNotificationForItem(notificationId: Int, item: Item) {
        val notif = createNotification(item.name)

        val intent = Intent(appContext, NotificationPublisher::class.java)
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
        intent.putExtra(NotificationPublisher.NOTIFICATION_CONTENT, notif)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC, item.time * 1000, pendingIntent)
    }
}