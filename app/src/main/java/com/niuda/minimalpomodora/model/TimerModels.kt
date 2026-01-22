package com.niuda.minimalpomodora.model

enum class TimerType { FOCUS, SHORT_BREAK, LONG_BREAK }

data class TimerState(
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val timerType: TimerType = TimerType.FOCUS
)
