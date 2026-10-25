# Android Screen Caster (ScreenMirror)

A minimal Android app that captures the device screen using `MediaProjection`
and displays a live preview inside the app.

Package: `io.android.screen.caster`

> Note: Android Auto's public SDK does not support arbitrary screen mirroring.
> This app mirrors the screen locally on the phone and is a foundation only.

## Requirements

- Android SDK Platform 35 (`android-35`)
- Build-Tools 35.0.0
- JDK 17+

## Build with Gradle

```sh
./gradlew assembleDebug
```

Output APK: `app/build/outputs/apk/debug/app-debug.apk`

## Install & run

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n io.android.screen.caster/.MainActivity
```

On first launch, tap **Start mirroring** and accept the screen-capture permission dialog.

## Project layout

```
app/
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    kotlin/io/android/screen/caster/
      MainActivity.kt
      CaptureService.kt
    res/
      layout/activity_main.xml
      values/strings.xml
```
