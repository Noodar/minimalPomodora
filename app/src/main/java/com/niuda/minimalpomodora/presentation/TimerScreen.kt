package com.niuda.minimalpomodora.presentation

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(
    timerType: String,
    viewModel: TimerViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.isRunning, viewModel.isPaused) {
        while (viewModel.isRunning && !viewModel.isPaused) {
            delay(1000)
            viewModel.tick()
            if (viewModel.isCompleted()) {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                onComplete()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = timerType, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = formatTime(viewModel.remainingSeconds),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (!viewModel.isPaused) {
            Button(
                onClick = { viewModel.pause() },
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("Pause")
            }
        } else {
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
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(mins, secs)
}
