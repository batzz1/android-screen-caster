# Android Screen Caster

Two modules in one repo:

| Module | Package | Purpose |
|--------|---------|---------|
| `:app` | `io.android.screen.caster` | Phone screen mirror app — captures screen via `MediaProjection` and shows a live preview locally on the phone |
| `:auto` | `com.example.autocar` | Android Auto media app — registers a `MediaBrowserServiceCompat` so it appears in Android Auto / DHU media apps |

> Note: Android Auto's public SDK does **not** allow arbitrary phone-screen mirroring. `:app` mirrors locally on the phone. `:auto` is a media app for the car interface.

## Requirements

- Android SDK Platform 35 (`android-35`)
- Build-Tools 35.0.0
- JDK 17+

## Build both modules

```sh
./gradlew assembleDebug
```

Output APKs:

| Module | APK path |
|--------|----------|
| `:app` | `app/build/outputs/apk/debug/app-debug.apk` |
| `:auto` | `auto/build/outputs/apk/debug/app-debug.apk` |

## Install & run

### Phone screen mirror (`:app`)

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n io.android.screen.caster/.MainActivity
```

Tap **Start mirroring** and accept the screen-capture permission dialog.

### Android Auto media app (`:auto`)

```sh
adb install -r auto/build/outputs/apk/debug/app-debug.apk
```

This app appears in Android Auto / Desktop Head Unit as a media source ("Screen Caster"). On first launch, it shows a welcome onboarding screen with instructions. It does **not** mirror the phone screen — Android Auto does not allow that from regular apps.

## Project layout

```
app/                          # Phone screen mirror
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    kotlin/io/android/screen/caster/
      MainActivity.kt
      CaptureService.kt
    res/

auto/                         # Android Auto media app
  build.gradle.kts
  src/main/
    AndroidManifest.xml
    kotlin/com/example/autocar/
      MainActivity.kt
      MyMediaBrowserService.kt
    res/

build.gradle.kts              # Root build config
settings.gradle.kts
gradle.properties
gradlew / gradlew.bat
```
