# task_10_final_build.md

## 1. Goal (목표)
* Release 빌드를 위한 설정을 완료한다.
* 서명된 APK를 생성한다.
* 최종 테스트를 수행하고 배포 준비를 완료한다.

---

## 2. Tech Spec & Setup (기술 명세)

### Release 빌드 체크리스트
- [ ] 디버깅 정보 제거
- [ ] 코드 난독화 (R8/ProGuard)
- [ ] 앱 서명 (Keystore)
- [ ] API Key 보안 확인

### 수정/생성 대상 파일
```
android/
├── app/
│   ├── build.gradle.kts           [MODIFY]
│   └── proguard-rules.pro         [MODIFY]
├── keystore/
│   └── release.keystore           [NEW] (Git 제외)
└── keystore.properties            [NEW] (Git 제외)
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: Release Keystore 생성

**PowerShell에서 실행:**
```powershell
cd d:\vscode\dubai_app\android
mkdir keystore

keytool -genkeypair -v `
  -keystore keystore/release.keystore `
  -alias dubai_cookie_key `
  -keyalg RSA `
  -keysize 2048 `
  -validity 10000 `
  -storepass <비밀번호입력> `
  -keypass <비밀번호입력> `
  -dname "CN=Dubai Cookie Finder, OU=Development, O=MyCompany, L=Seoul, ST=Seoul, C=KR"
```

### Step 3.2: keystore.properties 생성

`android/keystore.properties` (Git 제외됨):
```properties
storeFile=keystore/release.keystore
storePassword=<비밀번호>
keyAlias=dubai_cookie_key
keyPassword=<비밀번호>
```

### Step 3.3: .gitignore에 추가

```gitignore
# Keystore
keystore/
keystore.properties
```

### Step 3.4: build.gradle.kts (app) 수정

```kotlin
import java.util.Properties

// Keystore 설정 로드
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

android {
    namespace = "com.example.dubaicookiefinder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dubaicookiefinder"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        
        // ... 기존 설정 유지
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }
}
```

### Step 3.5: proguard-rules.pro 수정

```proguard
# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Data Models (필요 시)
-keep class com.example.dubaicookiefinder.data.model.** { *; }

# Compose (기본 유지)
-dontwarn androidx.compose.**
```

### Step 3.6: Release APK 빌드

```powershell
cd d:\vscode\dubai_app\android

# Clean 빌드
./gradlew clean

# Release APK 생성
./gradlew assembleRelease

# 생성된 APK 위치
# app/build/outputs/apk/release/app-release.apk
```

### Step 3.7: Release SHA-1 등록

Release Keystore의 SHA-1도 Google Cloud Console에 등록:
```powershell
keytool -list -v -keystore keystore/release.keystore -alias dubai_cookie_key
```

SHA-1 값을 Google Cloud Console → API 제한 → 추가 등록

---

## 4. Validation (검증)

### 빌드 성공 확인
- [ ] `./gradlew assembleRelease` 성공
- [ ] `app-release.apk` 생성 확인
- [ ] APK 크기 확인 (R8 적용 시 감소)

### 설치 테스트
```powershell
# 에뮬레이터/실기기에 설치
adb install app/build/outputs/apk/release/app-release.apk
```

### 최종 테스트 체크리스트
- [ ] 앱 정상 실행
- [ ] 지도 타일 로드 정상
- [ ] 마커 표시 정상
- [ ] 위치 권한 동작 정상
- [ ] 크래시 없음

---

## 5. 프로젝트 완료! 🎉

### 배포 옵션
| 방법 | 설명 |
|------|------|
| APK 직접 배포 | 생성된 APK 파일 공유 |
| Firebase App Distribution | 테스터 그룹에 배포 |
| Google Play Console | 정식 스토어 등록 |

### 향후 개선 사항
- [ ] 실제 API 연동 (Mock → Real)
- [ ] 사용자 즐겨찾기 기능
- [ ] 푸시 알림 (재고 알림)
- [ ] 다크 모드 지원

---

## 🏆 MVP 완성!

```
두바이 쫀득 쿠키 재고 앱
├── ✅ Phase 1: 프로젝트 기반 구축
├── ✅ Phase 2: Apple 스타일 UI
├── ✅ Phase 3: Google Maps 연동
├── ✅ Phase 4: 데이터 및 비즈니스 로직
└── ✅ Phase 5: 최종 빌드 및 배포 준비
```
