package com.niuda.minimalpomodora.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.niuda.minimalpomodora.data.SettingsRepository
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
    val navController = rememberSwipeDismissableNavController()
    val settingsRepo = remember { SettingsRepository(navController.context) }
    val timerViewModel: TimerViewModel = viewModel()
    
    var focusDuration by remember { mutableStateOf(settingsRepo.getFocusDuration()) }
    var shortBreakDuration by remember { mutableStateOf(settingsRepo.getShortBreakDuration()) }
    var longBreakDuration by remember { mutableStateOf(settingsRepo.getLongBreakDuration()) }

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
                    timerViewModel.start(focusDuration)
                    navController.navigate("timer/Focus")
                },
                onShortBreakClick = {
                    timerViewModel.start(shortBreakDuration)
                    navController.navigate("timer/Short Break")
                },
                onLongBreakClick = {
                    timerViewModel.start(longBreakDuration)
                    navController.navigate("timer/Long Break")
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
            val timerType = backStackEntry.arguments?.getString("type") ?: "Timer"
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