package com.example.remindmemunni.notifications

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.example.remindmemunni.R
import com.example.remindmemunni.data.Item

class NotificationScheduler(context: Context) {
    private val appContext = context.applicationContext

    private fun createNotification(title: String, args: Bundle?): Notification {
        val channelId = appContext.getString(R.string.notification_alerts_id)
        val intent = NavDeepLinkBuilder(appContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.itemFragment)
            .setArguments(args)
            .createPendingIntent()
        return Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentIntent(intent)
            .setAutoCancel(true)
            .build()
    }

    fun scheduleNotificationForItem(itemId: Int, item: Item) {
        val notif = createNotification(item.name, bundleOf("ITEM_ID" to itemId))

        val intent = Intent(appContext, NotificationPublisher::class.java)
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, itemId)
        intent.putExtra(NotificationPublisher.NOTIFICATION_CONTENT, notif)
        val pendingIntent = PendingIntent.getBroadcast(
            appContext, itemId, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC, item.time * 1000, pendingIntent)
    }
}