# Minimal Pomodoro Watch App - Complete Specification

## Project Overview
A minimal Pomodoro timer application for Wear OS smartwatches built with Jetpack Compose. The app provides customizable timer durations for focus sessions and breaks with a clean, simple interface.

## Project Information
- **Package Name**: `com.niuda.minimalpomodora`
- **Project Name**: minimalPomodora
- **Platform**: Wear OS (Android Smartwatch)
- **Min SDK**: API 30 (Wear OS 3.0+)
- **Framework**: Jetpack Compose for Wear OS
- **Storage**: SharedPreferences
- **UI Design**: Material Design default colors

## Core Features

### 1. Main Menu Screen
**Purpose**: Primary navigation hub for the app

**Layout**:
- 4 vertically stacked long pill-shaped buttons
- Each button displays timer type and current duration

**Buttons**:
1. **Settings** (Gray) - Navigate to settings screen
2. **Focus** (Red) - Display "Focus (25m)" with current duration, starts focus timer
3. **Short Break** (Green) - Display "Short Break (5m)" with current duration, starts short break timer
4. **Long Break** (Blue) - Display "Long Break (15m)" with current duration, starts long break timer

**Behavior**:
- Buttons show current duration from saved settings
- Tapping Focus/Short Break/Long Break starts respective timer
- Tapping Settings navigates to settings screen

### 2. Settings Screen
**Purpose**: Configure timer durations for all three timer types

**Controls**:
- 3 time adjusters (minutes only) for:
  - Focus duration
  - Short Break duration
  - Long Break duration
- Each time adjuster has:
  - **-** button (left side) - Decrease time by 1 minute
  - Time display (center) - Shows current value (e.g., "25m")
  - **+** button (right side) - Increase time by 1 minute
- **Save** button (long pill-shaped, bottom of screen, Gray color)

**Navigation**:
- Save button: Apply changes, save to SharedPreferences, return to main menu
- Watch hardware button 2: Discard changes, return to main menu without saving

**Input Format**:
- Minutes only (no seconds)
- Range: 1-60 minutes
- +/- button UI components

### 3. Timer Screen (Focus/Short Break/Long Break)
**Purpose**: Display countdown and control active timer

**Display**:
- Large countdown timer in MM:SS format (e.g., "25:00", "04:32")
- Timer type label (Focus/Short Break/Long Break)

**Controls - Running State**:
- **Pause** button (long pill-shaped, bottom) - Pauses the timer

**Controls - Paused State**:
- **Resume** button (long pill-shaped) - Continue paused timer
- **Reset** button (long pill-shaped) - Reset to original duration AND immediately start running again
- Pause button disappears when paused

**Navigation**:
- Watch hardware button 2: Stop timer and return to main menu

**Always-on Display**:
- Simplified version of timer screen when watch face is covered
- Lower power consumption mode
- Timer continues counting in background

### 4. Timer Completion
**Notification**:
- Watch vibration when countdown reaches 0:00

**Action**:
- Automatic return to main menu after vibration

## Default Settings
- **Focus**: 25 minutes
- **Short Break**: 5 minutes
- **Long Break**: 15 minutes

## Technical Implementation Details

### Architecture
```
MainActivity.kt - Entry point & navigation setup
├── MenuScreen.kt - Main menu with 4 buttons
├── SettingsScreen.kt - Duration configuration
├── TimerScreen.kt - Countdown with pause/resume/reset
├── TimerViewModel.kt - Timer logic & state management
└── SettingsRepository.kt - SharedPreferences wrapper
```

### Technology Stack
- **UI Framework**: Jetpack Compose for Wear OS
- **Navigation**: Wear Compose Navigation
- **State Management**: ViewModel with Compose State
- **Persistence**: SharedPreferences
- **Permissions**: WAKE_LOCK, VIBRATE

### Key Technical Requirements
1. **Persistent Settings Storage**: Use SharedPreferences to store three integer values (focus, short_break, long_break durations in minutes)
2. **Background Timer Support**: Timer must continue running when screen is off or app is in background
3. **Always-on Display**: Implement ambient mode with simplified UI for power efficiency
4. **Hardware Button Integration**: Watch button 2 acts as back/cancel button
5. **Vibration**: Use system vibrator service for timer completion notification

### UI Specifications
- **Button Style**: Long pill-shaped (rounded rectangular) buttons spanning most of screen width
- **Colors**: Material Design defaults
  - Settings: Gray
  - Focus: Red
  - Short Break: Green
  - Long Break: Blue
- **Timer Display**: Large, easily readable text in MM:SS format
- **Layout**: Optimized for round watch faces

### State Management
**Timer States**:
- IDLE: No timer running
- RUNNING: Timer actively counting down
- PAUSED: Timer paused, showing Resume/Reset options
- COMPLETED: Timer reached 0:00, triggering vibration and navigation

**Settings State**:
- Load from SharedPreferences on app start
- Update in memory when user changes settings
- Persist to SharedPreferences only when Save button pressed

### Navigation Flow
```
Main Menu
├── Settings Button → Settings Screen
│   ├── Save Button → Main Menu (save changes)
│   └── Button 2 → Main Menu (discard changes)
├── Focus Button → Timer Screen (Focus)
├── Short Break Button → Timer Screen (Short Break)
└── Long Break Button → Timer Screen (Long Break)

Timer Screen
├── Pause Button → Show Resume/Reset (hide Pause)
├── Resume Button → Continue timer (show Pause)
├── Reset Button → Restart timer from original duration
├── Timer Completion → Main Menu (with vibration)
└── Button 2 → Main Menu (stop timer)
```

### Data Model
```kotlin
// SharedPreferences keys
"focus" -> Int (minutes)
"short_break" -> Int (minutes)
"long_break" -> Int (minutes)

// Timer types
enum class TimerType {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK
}

// Timer state
data class TimerState(
    val remainingSeconds: Int,
    val isRunning: Boolean,
    val isPaused: Boolean
)
```

## Design Decisions Summary

| Aspect | Decision | Rationale |
|--------|----------|-----------|
| Framework | Jetpack Compose | Modern, less boilerplate, better state management |
| Time Input | Minutes only with +/- buttons | Sufficient for Pomodoro, simpler UX, better for watch |
| Timer Format | MM:SS | Standard for 5-45 minute sessions |
| Pause UI | Both Resume/Reset visible when paused | Clear options, no hidden functionality |
| Always-on | Simplified version | Better battery life, follows Wear OS guidelines |
| Storage | SharedPreferences | Simplest for 3 integer values |
| Colors | Material Design defaults | Accessible, less code, professional |
| Completion | No visual indicator | Minimal implementation, vibration is sufficient |
| Min SDK | API 30+ | Modern features, good device compatibility |

## User Interaction Patterns

### Starting a Timer
1. User opens app → Main Menu displayed
2. User taps Focus/Short Break/Long Break button
3. Timer Screen appears with countdown starting immediately
4. Timer counts down in MM:SS format

### Pausing and Resuming
1. Timer is running
2. User taps Pause button
3. Pause button disappears, Resume and Reset buttons appear
4. User taps Resume → Timer continues from paused time
5. OR User taps Reset → Timer resets to original duration and starts running

### Changing Settings
1. User taps Settings button on Main Menu
2. Settings Screen shows 3 time adjusters with current values
3. User adjusts durations using +/- buttons:
   - Tap **-** to decrease by 1 minute (minimum 1)
   - Tap **+** to increase by 1 minute (maximum 60)
4. User taps Save → Changes persist, return to Main Menu
5. OR User presses Button 2 → Changes discarded, return to Main Menu

### Timer Completion
1. Timer reaches 0:00
2. Watch vibrates
3. App automatically returns to Main Menu

## Implementation Notes

### Minimal Code Principles
- No unnecessary abstractions
- Direct implementations without over-engineering
- Single responsibility per file
- Minimal comments (code should be self-explanatory)
- No test code unless explicitly requested

### Always-on Display Implementation
- Use `AmbientState` from Wear Compose
- Show simplified timer UI in ambient mode
- Reduce brightness and remove colors in ambient mode
- Timer continues counting in background

### Timer Implementation
- Use `LaunchedEffect` with `delay(1000)` for countdown
- Store remaining seconds in ViewModel state
- Update UI every second when running
- Persist timer state across configuration changes

### Vibration Implementation
- Use `Vibrator` system service
- Trigger on timer completion (0:00)
- Single vibration pattern (no custom patterns needed)

## File Structure
```
app/src/main/
├── AndroidManifest.xml (WAKE_LOCK, VIBRATE permissions)
├── java/com/niuda/minimalpomodora/
│   ├── data/
│   │   └── SettingsRepository.kt
│   ├── presentation/
│   │   ├── MainActivity.kt
│   │   ├── MenuScreen.kt
│   │   ├── SettingsScreen.kt
│   │   ├── TimerScreen.kt
│   │   ├── TimerViewModel.kt
│   │   └── theme/
│   └── res/
│       ├── values/
│       │   └── strings.xml
│       └── (other resources)
└── build.gradle.kts (dependencies)
```

## Dependencies Required
```kotlin
// Wear Compose
implementation("androidx.wear.compose:compose-material:1.2.1")
implementation("androidx.wear.compose:compose-foundation:1.2.1")
implementation("androidx.wear.compose:compose-navigation:1.2.0")

// Compose
implementation(platform("androidx.compose:compose-bom:2024.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.activity:activity-compose:1.8.0")

// Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

// Core
implementation("androidx.core:core-splashscreen:1.0.1")
implementation("com.google.android.gms:play-services-wearable:18.0.0")
```

## Testing Considerations
- Test on round watch face (primary use case)
- Verify timer continues in background
- Test always-on display mode
- Verify vibration on timer completion
- Test hardware button 2 navigation
- Verify settings persistence across app restarts

## Future Enhancements (Not in Scope)
- Session history/statistics
- Custom notification sounds
- Multiple timer presets
- Auto-start next session
- Complications for watch faces
- Phone companion app

---

**Document Version**: 1.0  
**Last Updated**: Initial specification  
**Status**: Ready for implementation
