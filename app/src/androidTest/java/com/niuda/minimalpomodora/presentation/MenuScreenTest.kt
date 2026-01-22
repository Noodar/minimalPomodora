package com.niuda.minimalpomodora.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class MenuScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun menuDisplaysDurationsAndHandlesClicks() {
        var settingsClicks = 0
        var focusClicks = 0
        var shortBreakClicks = 0
        var longBreakClicks = 0

        composeRule.setContent {
            MenuScreen(
                focusDuration = 25,
                shortBreakDuration = 5,
                longBreakDuration = 15,
                onSettingsClick = { settingsClicks++ },
                onFocusClick = { focusClicks++ },
                onShortBreakClick = { shortBreakClicks++ },
                onLongBreakClick = { longBreakClicks++ }
            )
        }

        composeRule.onNodeWithText("Settings").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Focus (25m)").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Short Break (5m)").assertIsDisplayed().performClick()
        composeRule.onNodeWithText("Long Break (15m)").assertIsDisplayed().performClick()

        assertEquals(1, settingsClicks)
        assertEquals(1, focusClicks)
        assertEquals(1, shortBreakClicks)
        assertEquals(1, longBreakClicks)
    }
}
