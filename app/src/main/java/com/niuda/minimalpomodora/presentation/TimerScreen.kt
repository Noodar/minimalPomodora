package com.niuda.minimalpomodora.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.niuda.minimalpomodora.model.TimerType

@Composable
fun TimerScreen(
    timerType: TimerType,
    viewModel: TimerViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val timerState by viewModel.state.collectAsState()
    val isBreak = timerType != TimerType.FOCUS
    val timerLabel = when (timerType) {
        TimerType.FOCUS -> "Focus"
        TimerType.SHORT_BREAK -> "Short Break"
        TimerType.LONG_BREAK -> "Long Break"
    }

    LaunchedEffect(timerState.isCompleted) {
        if (timerState.isCompleted) {
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = timerLabel, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                        append("%02d".format(timerState.remainingSeconds / 60))
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.onBackground)) {
                        append(":")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.secondary)) {
                        append("%02d".format(timerState.remainingSeconds % 60))
                    }
                },
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!isBreak) {
                if (timerState.isPaused) {
                    Button(
                        onClick = { viewModel.resume() },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
                    ) {
                        Text("Resume")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                    ) {
                        Text("Reset")
                    }
                } else {
                    Button(
                        onClick = { viewModel.pause() },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("Pause")
                    }
                }
            }
        }
    }
}
