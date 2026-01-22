package com.niuda.minimalpomodora.presentation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niuda.minimalpomodora.model.TimerState
import com.niuda.minimalpomodora.model.TimerType
import com.niuda.minimalpomodora.service.TimerService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var service: TimerService? = null
    private var isBound = false
    private var serviceStateJob: Job? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val timerBinder = binder as? TimerService.TimerBinder ?: return
            service = timerBinder.getService()
            serviceStateJob?.cancel()
            serviceStateJob = viewModelScope.launch {
                service?.state?.collect { _state.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceStateJob?.cancel()
            service = null
        }
    }

    fun bind(context: Context) {
        if (isBound) return
        val intent = Intent(context, TimerService::class.java)
        context.applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        isBound = true
    }

    fun unbind(context: Context) {
        if (!isBound) return
        context.applicationContext.unbindService(connection)
        isBound = false
        serviceStateJob?.cancel()
        service = null
    }

    fun start(context: Context, durationMinutes: Int, type: TimerType) {
        val intent = Intent(context, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_DURATION_MINUTES, durationMinutes)
            putExtra(TimerService.EXTRA_TIMER_TYPE, type.name)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun pause() {
        service?.pause()
    }

    fun resume() {
        service?.resume()
    }

    fun reset() {
        service?.reset()
    }

    fun stop() {
        service?.stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        serviceStateJob?.cancel()
        service = null
    }
}
