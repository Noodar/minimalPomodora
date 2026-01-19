# Minimal Pomodoro Watch App Requirements

## Overview
A simple Pomodoro timer app for smartwatch with customizable durations and basic timer controls.

## Core Features

### 1. Main Menu Screen
- **Layout**: 4 vertically stacked long pill-shaped buttons with distinct colors
- **Buttons**:
  - **Settings** (Color: Gray) - Navigate to settings screen
  - **Focus** (Color: Red) - Display "Focus (25m)" with current duration
  - **Short Break** (Color: Green) - Display "Short Break (5m)" with current duration  
  - **Long Break** (Color: Blue) - Display "Long Break (15m)" with current duration

### 2. Settings Screen
- **Purpose**: Configure timer durations
- **Controls**: 
  - Time input fields for Focus, Short Break, and Long Break
  - **Save** button (long pill-shaped, bottom of screen, Color: Gray) - Save changes and return to menu
- **Navigation**:
  - Save button: Apply changes and return to menu
  - Watch button 2: Discard changes and return to menu

### 3. Timer Screens (Focus/Break)
- **Display**: Large countdown timer showing remaining time
- **Controls**:
  - **Pause** button (long pill-shaped, bottom) - Pause timer and show Resume/Reset options
  - **Resume** button (long pill-shaped) - Continue paused timer
  - **Reset** button (long pill-shaped) - Reset to original duration and restart
- **Navigation**:
  - Watch button 2: Stop timer and return to menu
- **Always-on Display**: Timer remains visible when watch face is covered

### 4. Timer Completion
- **Notification**: Watch vibration when countdown reaches zero
- **Action**: Automatic return to main menu

## Default Settings
- Focus: 25 minutes
- Short Break: 5 minutes  
- Long Break: 15 minutes

## Technical Notes
- Persistent settings storage required
- Background timer support for always-on display
- Hardware button integration (button 2 for back navigation)