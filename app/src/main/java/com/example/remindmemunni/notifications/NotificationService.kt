package com.example.remindmemunni.notifications

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.remindmemunni.data.ItemRoomDatabase

class NotificationService : JobIntentService() {
    override fun onHandleWork(intent: Intent) {
        Log.d("NotificationService", "Creating Notifications?")
        val scheduler =
            NotificationScheduler(
                applicationContext
            )
        val itemDao = ItemRoomDatabase.getDatabase(applicationContext).itemDao()
        val items = itemDao.getNotifyItems()
        for (item in items) { scheduler.scheduleNotificationForItem(item) }
    }

    companion object {
        private const val JOB_ID = 1
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, NotificationService::class.java,
                JOB_ID, intent)
        }
    }
}
