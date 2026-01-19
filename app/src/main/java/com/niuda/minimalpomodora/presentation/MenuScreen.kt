package com.niuda.minimalpomodora.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text

@Composable
fun MenuScreen(
    focusDuration: Int,
    shortBreakDuration: Int,
    longBreakDuration: Int,
    onSettingsClick: () -> Unit,
    onFocusClick: () -> Unit,
    onShortBreakClick: () -> Unit,
    onLongBreakClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onFocusClick,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Focus (${focusDuration}m)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onShortBreakClick,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
        ) {
            Text("Short Break (${shortBreakDuration}m)")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onLongBreakClick,
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue)
        ) {
            Text("Long Break (${longBreakDuration}m)")
        }
    }
}
