# minimalPomodora
Minimal Pomodoro timer for Wear OS. Simple focus and break sessions, no clutter.

## Features
- **Minimal UI**: Clean, distraction-free watch interface
- **Pomodoro modes**: Focus, Short Break, Long Break
- **Focus controls**: Pause/Resume/Reset (Focus only)
- **Watch-only**: Runs independently, no phone connection required
- **Screen-off safe**: Timer keeps running when the watch screen turns off
- **Haptic finish**: Vibrates when the timer completes

## Screens
- **Menu**: Start Focus/Short Break/Long Break or open Settings
- **Settings**: Adjust durations (1–60 minutes) and Save
- **Timer**: Countdown display; controls only for Focus

## How it works (dev summary)
- Foreground service owns the timer for background reliability.
- Remaining time is computed from `SystemClock.elapsedRealtime()`.
- AlarmManager schedules completion so it fires even when the screen is off.

## Permissions
- `WAKE_LOCK`: Keep CPU awake for reliable timing
- `VIBRATE`: Haptic completion signal
- `FOREGROUND_SERVICE`: Run the timer in the background
- `POST_NOTIFICATIONS`: Show the required foreground notification
- `FOREGROUND_SERVICE_DATA_SYNC`: Foreground service type for this device

## Build & Run
```bash
./gradlew :app:assembleDebug
```
Install to a Wear OS emulator or physical watch.

## Tests
```bash
./gradlew :app:connectedAndroidTest
```

## Customization
- Default durations: `app/src/main/java/com/niuda/minimalpomodora/data/SettingsRepository.kt`
- Button colors: `app/src/main/java/com/niuda/minimalpomodora/presentation/MenuScreen.kt`

## License
See `LICENSE`.
