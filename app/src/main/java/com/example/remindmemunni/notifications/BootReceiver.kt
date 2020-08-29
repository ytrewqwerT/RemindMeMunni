package com.example.remindmemunni.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

// TODO: Register/unregister the receiver as needed
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BootReceiver", "Received Broadcast")
        if (intent?.action != "android.intent.action.BOOT_COMPLETED") return

        Log.d("BootReceiver", "Starting service from context $context")
        if (context == null) return

        val notifyIntent = Intent(context, NotificationService::class.java)
        NotificationService.enqueueWork(context, notifyIntent)
    }
}