# SynchronizedClock

SynchronizedClock is an Android application that provides a synchronized and accurate time display with internet time synchronization. It allows you to view the current time, synchronize with a Network Time Protocol (NTP) server, and toggle between 12-hour and 24-hour time formats.

## Features

- **Accurate Time Display:** The app fetches the current time from an NTP server to ensure precision and accuracy in timekeeping.

- **NTP Time Synchronization:** It synchronizes with a specified NTP server ("1.se.pool.ntp.org") to obtain the exact time.

- **12-Hour and 24-Hour Formats:** You can switch between 12-hour and 24-hour time formats, depending on your preference.

- **Internet Status Indicator:** The app displays an indicator that changes color to reflect the status of your internet connection, green for connected and red for disconnected.

- **Pause/Resume Feature:** You can pause and resume the clock updates at any time to freeze the display or restart it.

## Usage

1. Launch the app on your Android device.

2. The clock display will automatically update every second, showing either the synchronized NTP time or your system time based on your internet connectivity.

3. You can toggle between 12-hour and 24-hour time formats using the "Toggle Format" button.

4. Use the "Pause/Resume" button to freeze or restart the clock updates.

## Requirements

- Android device running Android OS (minimum version required).
- `minSdkVersion`: 29
- `compileSdkVersion`: 34

## Development and Testing

This app was developed and tested on a Google Pixel 3a device.

## Installation

Clone or download this repository and open the project in Android Studio. Build and run the app on your Android device or emulator.

## Author

- Timmie
