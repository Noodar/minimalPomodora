package com.niuda.minimalpomodora.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class TimerType { FOCUS, SHORT_BREAK, LONG_BREAK }

class TimerViewModel : ViewModel() {
    var remainingSeconds by mutableStateOf(0)
    var isRunning by mutableStateOf(false)
    var isPaused by mutableStateOf(false)
    var totalSeconds by mutableStateOf(0)

    fun start(durationMinutes: Int) {
        totalSeconds = durationMinutes * 60
        remainingSeconds = totalSeconds
        isRunning = true
        isPaused = false
    }

    fun tick() {
        if (isRunning && !isPaused && remainingSeconds > 0) {
            remainingSeconds--
        }
    }

    fun pause() {
        isPaused = true
    }

    fun resume() {
        isPaused = false
    }

    fun reset() {
        remainingSeconds = totalSeconds
        isPaused = false
        isRunning = true
    }

    fun stop() {
        isRunning = false
        isPaused = false
        remainingSeconds = 0
    }

    fun isCompleted() = remainingSeconds == 0 && isRunning
}
