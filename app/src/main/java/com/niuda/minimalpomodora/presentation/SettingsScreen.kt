package com.niuda.minimalpomodora.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text

@Composable
fun SettingsScreen(
    initialFocus: Int,
    initialShortBreak: Int,
    initialLongBreak: Int,
    onSave: (Int, Int, Int) -> Unit,
    listState: ScalingLazyListState = rememberScalingLazyListState()
) {
    var focus by remember { mutableStateOf(initialFocus) }
    var shortBreak by remember { mutableStateOf(initialShortBreak) }
    var longBreak by remember { mutableStateOf(initialLongBreak) }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("settings-list"),
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        item {
            Text("Focus")
        }
        item {
            TimeAdjuster(
                label = "focus",
                value = focus,
                onValueChange = { focus = it }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Text("Short Break")
        }
        item {
            TimeAdjuster(
                label = "short_break",
                value = shortBreak,
                onValueChange = { shortBreak = it }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            Text("Long Break")
        }
        item {
            TimeAdjuster(
                label = "long_break",
                value = longBreak,
                onValueChange = { longBreak = it }
            )
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            Button(
                onClick = { onSave(focus, shortBreak, longBreak) },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Save")
            }
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TimeAdjuster(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.8f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { if (value > 1) onValueChange(value - 1) },
            modifier = Modifier
                .size(40.dp)
                .testTag("adjuster-${label}-decrement"),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("-")
        }
        Text(
            text = "${value}m",
            fontSize = 20.sp,
            modifier = Modifier.testTag("adjuster-${label}-value")
        )
        Button(
            onClick = { if (value < 60) onValueChange(value + 1) },
            modifier = Modifier
                .size(40.dp)
                .testTag("adjuster-${label}-increment"),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("+")
        }
    }
}
