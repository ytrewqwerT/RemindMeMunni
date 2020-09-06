package com.example.remindmemunni

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val id = getString(R.string.notification_alerts_id)
        val name = getString(R.string.alerts)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var context: Context private set
    }
}