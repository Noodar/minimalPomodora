package com.niuda.minimalpomodora.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class TimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != TimerService.ACTION_COMPLETE_TIMER) return
        val serviceIntent = Intent(context, TimerService::class.java).apply {
            action = TimerService.ACTION_COMPLETE_TIMER
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
