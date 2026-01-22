package com.niuda.minimalpomodora.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.VibratorManager
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.niuda.minimalpomodora.model.TimerState
import com.niuda.minimalpomodora.model.TimerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerService : Service() {
    private val binder = TimerBinder()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()
    private var tickJob: Job? = null
    private var cpuWakeLock: PowerManager.WakeLock? = null
    private var endElapsedRealtime: Long = 0L

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        intent?.let { handleStartIntent(it) }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "timer_channel",
            "Timer",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "timer_channel")
            .setContentTitle("Pomodoro Timer")
            .setContentText("Timer running")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .build()
    }

    fun startTimer(durationMinutes: Int, type: TimerType) {
        val totalSeconds = durationMinutes * 60
        val now = SystemClock.elapsedRealtime()
        endElapsedRealtime = now + totalSeconds * 1000L
        _state.value = TimerState(
            remainingSeconds = totalSeconds,
            totalSeconds = totalSeconds,
            isRunning = true,
            isPaused = false,
            isCompleted = false,
            timerType = type
        )
        acquireCpuWakeLock()
        scheduleCompletionAlarm(totalSeconds)
        startTicking()
    }

    fun pause() {
        if (_state.value.isRunning) {
            updateRemaining()
            _state.value = _state.value.copy(isPaused = true)
            tickJob?.cancel()
            cancelCompletionAlarm()
        }
    }

    fun resume() {
        if (_state.value.isRunning && _state.value.isPaused) {
            val now = SystemClock.elapsedRealtime()
            endElapsedRealtime = now + _state.value.remainingSeconds * 1000L
            _state.value = _state.value.copy(isPaused = false)
            scheduleCompletionAlarm(_state.value.remainingSeconds)
            startTicking()
        }
    }

    fun reset() {
        if (_state.value.totalSeconds > 0) {
            val now = SystemClock.elapsedRealtime()
            endElapsedRealtime = now + _state.value.totalSeconds * 1000L
            _state.value = _state.value.copy(
                remainingSeconds = _state.value.totalSeconds,
                isPaused = false,
                isRunning = true,
                isCompleted = false
            )
            scheduleCompletionAlarm(_state.value.totalSeconds)
            startTicking()
        }
    }

    fun stopTimer() {
        tickJob?.cancel()
        _state.value = TimerState()
        cancelCompletionAlarm()
        releaseCpuWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        tickJob?.cancel()
        releaseCpuWakeLock()
        serviceScope.cancel()
    }

    private fun startTicking() {
        tickJob?.cancel()
        tickJob = serviceScope.launch {
            while (isActive) {
                delay(1000)
                updateRemaining()
            }
        }
    }

    private fun completeTimer() {
        if (_state.value.isCompleted) return
        _state.value = _state.value.copy(
            remainingSeconds = 0,
            isRunning = false,
            isPaused = false,
            isCompleted = true
        )
        val vibrator = getSystemService(VibratorManager::class.java)?.defaultVibrator
        vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        cancelCompletionAlarm()
        releaseCpuWakeLock()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @Suppress("DEPRECATION")
    private fun acquireCpuWakeLock() {
        if (cpuWakeLock?.isHeld == true) return
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        cpuWakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MinimalPomodoro::CpuWakeLock"
        ).apply { acquire(60 * 60 * 1000L) }
    }

    private fun releaseCpuWakeLock() {
        cpuWakeLock?.let { lock ->
            if (lock.isHeld) lock.release()
        }
        cpuWakeLock = null
    }

    private fun handleStartIntent(intent: Intent) {
        when (intent.action) {
            ACTION_COMPLETE_TIMER -> {
                if (_state.value.isRunning && !_state.value.isPaused) {
                    completeTimer()
                }
            }
            else -> {
                val duration = intent.getIntExtra(EXTRA_DURATION_MINUTES, 0)
                val typeName = intent.getStringExtra(EXTRA_TIMER_TYPE)
                val type = typeName?.let { runCatching { TimerType.valueOf(it) }.getOrNull() }
                if (duration > 0 && type != null) {
                    startTimer(duration, type)
                }
            }
        }
    }

    private fun updateRemaining() {
        val current = _state.value
        if (!current.isRunning || current.isPaused || current.isCompleted) return
        val now = SystemClock.elapsedRealtime()
        val remaining = ((endElapsedRealtime - now) / 1000L).toInt().coerceAtLeast(0)
        if (remaining <= 0) {
            completeTimer()
        } else if (remaining != current.remainingSeconds) {
            _state.value = current.copy(remainingSeconds = remaining)
        }
    }

    private fun scheduleCompletionAlarm(remainingSeconds: Int) {
        if (remainingSeconds <= 0) return
        val alarmManager = getSystemService(AlarmManager::class.java)
        val triggerAt = SystemClock.elapsedRealtime() + remainingSeconds * 1000L
        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAt,
                completionAlarmIntent()
            )
        } catch (e: SecurityException) {
            alarmManager.set(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerAt,
                completionAlarmIntent()
            )
        }
    }

    private fun cancelCompletionAlarm() {
        val alarmManager = getSystemService(AlarmManager::class.java)
        alarmManager.cancel(completionAlarmIntent())
    }

    private fun completionAlarmIntent(): PendingIntent {
        val intent = Intent(this, TimerAlarmReceiver::class.java).apply {
            action = ACTION_COMPLETE_TIMER
        }
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_TIMER_TYPE = "extra_timer_type"
        const val ACTION_COMPLETE_TIMER = "com.niuda.minimalpomodora.ACTION_COMPLETE_TIMER"
    }
}
