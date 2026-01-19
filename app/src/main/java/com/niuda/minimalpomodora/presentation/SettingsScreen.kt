package com.niuda.minimalpomodora.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text

@Composable
fun SettingsScreen(
    initialFocus: Int,
    initialShortBreak: Int,
    initialLongBreak: Int,
    onSave: (Int, Int, Int) -> Unit
) {
    var focus by remember { mutableStateOf(initialFocus) }
    var shortBreak by remember { mutableStateOf(initialShortBreak) }
    var longBreak by remember { mutableStateOf(initialLongBreak) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Focus")
        TimeAdjuster(value = focus, onValueChange = { focus = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text("Short Break")
        TimeAdjuster(value = shortBreak, onValueChange = { shortBreak = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text("Long Break")
        TimeAdjuster(value = longBreak, onValueChange = { longBreak = it })
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { onSave(focus, shortBreak, longBreak) },
            modifier = Modifier.fillMaxWidth(0.8f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
        ) {
            Text("Save")
        }
    }
}

@Composable
fun TimeAdjuster(value: Int, onValueChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { if (value > 1) onValueChange(value - 1) },
            modifier = Modifier.size(40.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("-")
        }
        Text(text = "${value}m", fontSize = 20.sp)
        Button(
            onClick = { if (value < 60) onValueChange(value + 1) },
            modifier = Modifier.size(40.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("+")
        }
    }
}
