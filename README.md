# Android Screen Caster (ScreenMirror)

A minimal Android app that captures the device screen using `MediaProjection`
and displays a live preview inside the app. Built as a prototype toward
screen-casting on Android Auto (note: Android Auto's public SDK does not
support arbitrary screen mirroring; this app mirrors locally on the phone).

Package: `io.android.screen.caster`

## Requirements

- Android SDK Platform 35 (`android-35`)
- Build-Tools 35.0.0
- JDK 17+ (tested with OpenJDK 21)
- `adb` (for install/test)

Set environment variables (PowerShell example):

```powershell
$env:ANDROID_HOME = "C:\Users\<you>\AppData\Local\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
```

## Project layout

```
app/src/main/
  AndroidManifest.xml
  java/io/android/screen/caster/
    MainActivity.java
    CaptureService.java
  res/
    layout/activity_main.xml
    values/strings.xml
```

## Build the APK (manual, no Gradle)

From the project root:

```powershell
$SDK   = "$env:ANDROID_HOME"
$BUILD = "$SDK\build-tools\35.0.0"
$PLAT  = "$SDK\platforms\android-35\android.jar"
$OUT   = "$PWD\build"
New-Item -ItemType Directory -Force -Path "$OUT\gen","$OUT\classes" | Out-Null

# 1. Compile resources
& "$BUILD\aapt2.exe" compile --dir app/src/main/res -o "$OUT\res.zip"

# 2. Link resources + manifest, generate R.java
& "$BUILD\aapt2.exe" link -I $PLAT --manifest app/src/main/AndroidManifest.xml `
    -o "$OUT\base.apk" "$OUT\res.zip" --auto-add-overlay --java "$OUT\gen"

# 3. Compile Java
$src = Get-ChildItem app/src/main/java -Recurse -Filter *.java | % FullName
$r   = Get-ChildItem "$OUT\gen" -Recurse -Filter R.java | % FullName
javac -d "$OUT\classes" -cp $PLAT ($src + $r)

# 4. Convert to DEX
$cls = Get-ChildItem "$OUT\classes" -Recurse -Filter *.class | % FullName
& "$BUILD\d8.bat" --lib $PLAT --output "$OUT" $cls

# 5. Add classes.dex to APK
Push-Location $OUT
& "$BUILD\aapt.exe" add base.apk classes.dex
Pop-Location

# 6. Align + sign
& "$BUILD\zipalign.exe" -f 4 "$OUT\base.apk" "$OUT\aligned.apk"

# Generate a debug keystore once:
# keytool -genkeypair -v -keystore "$OUT\debug.keystore" -storepass android `
#   -alias androiddebugkey -keypass android -keyalg RSA -keysize 2048 -validity 10000 `
#   -dname "CN=Android Debug,O=Android,C=US"

& "$BUILD\apksigner.bat" sign --ks "$OUT\debug.keystore" --ks-pass pass:android `
    --key-pass pass:android --out "$OUT\ScreenMirror.apk" "$OUT\aligned.apk"
```

Output APK: `build/ScreenMirror.apk`

## Install & run

```powershell
adb install -r build\ScreenMirror.apk
adb shell am start -n io.android.screen.caster/.MainActivity
```

On first launch, tap **Start mirroring** and accept the screen-capture permission dialog.

## Notes

- Requires Android 7.0+ (minSdk 24) and targets SDK 35.
- Uses a foreground service with `mediaProjection` type for Android 14+ compatibility.
- No Gradle project is included; the build is performed directly with SDK tools.
