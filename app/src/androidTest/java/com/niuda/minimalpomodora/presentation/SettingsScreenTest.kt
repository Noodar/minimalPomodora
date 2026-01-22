package com.niuda.minimalpomodora.presentation

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun adjustersUpdateValuesAndSave() {
        var savedFocus = 0
        var savedShortBreak = 0
        var savedLongBreak = 0

        lateinit var listState: ScalingLazyListState
        composeRule.setContent {
            listState = rememberScalingLazyListState()
            SettingsScreen(
                initialFocus = 25,
                initialShortBreak = 5,
                initialLongBreak = 15,
                onSave = { focus, shortBreak, longBreak ->
                    savedFocus = focus
                    savedShortBreak = shortBreak
                    savedLongBreak = longBreak
                },
                listState = listState
            )
        }

        composeRule.onNodeWithTag("adjuster-focus-increment").performClick()
        composeRule.onNodeWithTag("adjuster-short_break-decrement").performClick()
        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(8) }
        }
        composeRule.onNodeWithTag("adjuster-long_break-increment").performClick()

        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(2) }
        }
        composeRule.onNodeWithTag("adjuster-focus-value").assertTextEquals("26m")
        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(5) }
        }
        composeRule.onNodeWithTag("adjuster-short_break-value").assertTextEquals("4m")
        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(8) }
        }
        composeRule.onNodeWithTag("adjuster-long_break-value").assertTextEquals("16m")

        composeRule.onNodeWithText("Save").performClick()

        assertEquals(26, savedFocus)
        assertEquals(4, savedShortBreak)
        assertEquals(16, savedLongBreak)
    }

    @Test
    fun adjustersRespectBounds() {
        lateinit var listState: ScalingLazyListState
        composeRule.setContent {
            listState = rememberScalingLazyListState()
            SettingsScreen(
                initialFocus = 1,
                initialShortBreak = 60,
                initialLongBreak = 15,
                onSave = { _, _, _ -> },
                listState = listState
            )
        }

        composeRule.onNodeWithTag("adjuster-focus-decrement").performClick()
        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(5) }
        }
        composeRule.onNodeWithTag("adjuster-short_break-increment").performClick()

        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(2) }
        }
        composeRule.onNodeWithTag("adjuster-focus-value").assertTextEquals("1m")
        composeRule.runOnIdle {
            runBlocking { listState.scrollToItem(5) }
        }
        composeRule.onNodeWithTag("adjuster-short_break-value").assertTextEquals("60m")
    }
}
