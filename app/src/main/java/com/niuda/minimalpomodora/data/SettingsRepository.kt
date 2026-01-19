package com.niuda.minimalpomodora.data

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("pomodoro_settings", Context.MODE_PRIVATE)

    fun getFocusDuration() = prefs.getInt("focus", 25)
    fun getShortBreakDuration() = prefs.getInt("short_break", 5)
    fun getLongBreakDuration() = prefs.getInt("long_break", 15)

    fun saveDurations(focus: Int, shortBreak: Int, longBreak: Int) {
        prefs.edit().apply {
            putInt("focus", focus)
            putInt("short_break", shortBreak)
            putInt("long_break", longBreak)
            apply()
        }
    }
}
