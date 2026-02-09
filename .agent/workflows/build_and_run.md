---
description: Android 앱 빌드 및 실행 워크플로우
---

# Build and Run Workflow

// turbo-all

## 1. Gradle Sync
```powershell
cd d:\vscode\dubai_app\android
./gradlew --refresh-dependencies
```

## 2. Clean Build
```powershell
./gradlew clean
```

## 3. Debug Build
```powershell
./gradlew assembleDebug
```

## 4. Run on Emulator
```powershell
# 에뮬레이터 목록 확인
emulator -list-avds

# 에뮬레이터 실행 (예: Pixel_7_API_35)
emulator -avd Pixel_7_API_35 &

# APK 설치
adb install app/build/outputs/apk/debug/app-debug.apk

# 앱 실행
adb shell am start -n com.example.dubaicookiefinder/.MainActivity
```

## 5. Release Build
```powershell
./gradlew assembleRelease
```

## Troubleshooting

### Gradle Sync 실패
```powershell
# 캐시 삭제
./gradlew --stop
rm -r .gradle
rm -r ~/.gradle/caches
./gradlew clean
```

### 에뮬레이터 연결 안됨
```powershell
adb kill-server
adb start-server
adb devices
```
