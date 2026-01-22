package com.niuda.minimalpomodora.presentation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.niuda.minimalpomodora.data.SettingsRepository
import com.niuda.minimalpomodora.model.TimerType
import com.niuda.minimalpomodora.presentation.theme.MinimalPomodoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MinimalPomodoraTheme {
                PomodoroApp()
            }
        }
    }
}

@Composable
fun PomodoroApp() {
    val context = LocalContext.current
    val activity = context as? Activity
    val navController = rememberSwipeDismissableNavController()
    val settingsRepo = remember { SettingsRepository(navController.context) }
    val timerViewModel: TimerViewModel = viewModel()
    
    var focusDuration by remember { mutableStateOf(settingsRepo.getFocusDuration()) }
    var shortBreakDuration by remember { mutableStateOf(settingsRepo.getShortBreakDuration()) }
    var longBreakDuration by remember { mutableStateOf(settingsRepo.getLongBreakDuration()) }
    var pendingStart by remember { mutableStateOf<(() -> Unit)?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(
                context,
                "Notifications are disabled. Background timing may be unreliable.",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingStart?.invoke()
        pendingStart = null
    }

    DisposableEffect(Unit) {
        timerViewModel.bind(context)
        onDispose { timerViewModel.unbind(context) }
    }

    fun startTimerWithPermission(type: TimerType, durationMinutes: Int) {
        val startAction = {
            timerViewModel.start(context, durationMinutes, type)
            navController.navigate("timer/${type.name}")
        }
        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        if (needsPermission && activity != null) {
            pendingStart = startAction
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            startAction()
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(
                focusDuration = focusDuration,
                shortBreakDuration = shortBreakDuration,
                longBreakDuration = longBreakDuration,
                onSettingsClick = { navController.navigate("settings") },
                onFocusClick = {
                    startTimerWithPermission(TimerType.FOCUS, focusDuration)
                },
                onShortBreakClick = {
                    startTimerWithPermission(TimerType.SHORT_BREAK, shortBreakDuration)
                },
                onLongBreakClick = {
                    startTimerWithPermission(TimerType.LONG_BREAK, longBreakDuration)
                }
            )
        }

        composable("settings") {
            BackHandler {
                navController.popBackStack()
            }
            SettingsScreen(
                initialFocus = focusDuration,
                initialShortBreak = shortBreakDuration,
                initialLongBreak = longBreakDuration,
                onSave = { focus, shortBreak, longBreak ->
                    settingsRepo.saveDurations(focus, shortBreak, longBreak)
                    focusDuration = focus
                    shortBreakDuration = shortBreak
                    longBreakDuration = longBreak
                    navController.popBackStack()
                }
            )
        }

        composable("timer/{type}") { backStackEntry ->
            val typeName = backStackEntry.arguments?.getString("type")
            val timerType = typeName?.let { runCatching { TimerType.valueOf(it) }.getOrNull() }
                ?: TimerType.FOCUS
            BackHandler {
                timerViewModel.stop()
                navController.popBackStack()
            }
            TimerScreen(
                timerType = timerType,
                viewModel = timerViewModel,
                onComplete = {
                    timerViewModel.stop()
                    navController.popBackStack()
                }
            )
        }
    }
}