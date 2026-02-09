# task_05_api_key_setup.md

## 1. Goal (목표)
* Google Cloud Console에서 Maps SDK for Android를 활성화한다.
* API Key를 발급하고 SHA-1 지문을 등록하여 보안을 설정한다.
* `local.properties`에 키를 저장하고 BuildConfig로 주입한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 필수 사전 준비
- Google 계정
- 결제 정보 등록 (월 $200 무료 크레딧)

### 생성/수정 대상 파일
```
android/
├── local.properties          [MODIFY] - API Key 저장
├── app/build.gradle.kts      [MODIFY] - BuildConfig 설정
└── gradle.properties         [MODIFY] - 보안 설정
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: SHA-1 지문 추출 (디버그용)

⚠️ **중요**: SHA-1 지문이 없으면 앱에서 지도가 로드되지 않습니다.

**Windows PowerShell에서 실행:**
```powershell
# Android Studio의 기본 디버그 키스토어 경로
keytool -list -v -keystore "$env:USERPROFILE\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android
```

**출력에서 SHA1 값 복사:**
```
SHA1: XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX:XX
```

### Step 3.2: Google Cloud Console 설정

1. **[console.cloud.google.com](https://console.cloud.google.com) 접속**

2. **새 프로젝트 생성**
   - 프로젝트 이름: `DubaiCookieFinder`
   - `만들기` 클릭

3. **Maps SDK for Android 활성화**
   - 좌측 메뉴 → `API 및 서비스` → `라이브러리`
   - `Maps SDK for Android` 검색 → `사용` 클릭

4. **API Key 발급**
   - 좌측 메뉴 → `API 및 서비스` → `사용자 인증 정보`
   - `+ 사용자 인증 정보 만들기` → `API 키`
   - 발급된 키 복사

5. **키 제한 설정 (보안)**
   - 생성된 키 우측 `연필` 아이콘 클릭
   - `애플리케이션 제한사항` → `Android 앱` 선택
   - `패키지 이름 및 지문 추가`:
     - 패키지 이름: `com.example.dubaicookiefinder`
     - SHA-1 인증서 지문: Step 3.1에서 추출한 값 입력
   - `저장`

### Step 3.3: local.properties에 키 저장

`android/local.properties` 파일에 추가:
```properties
# Google Maps API Key (Git 제외됨)
MAPS_API_KEY=AIzaSy...발급받은_키_붙여넣기...
```

### Step 3.4: build.gradle.kts 수정 (app 수준)

`app/build.gradle.kts`에 추가:

```kotlin
import java.util.Properties

// local.properties에서 API Key 읽기
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    // ... 기존 설정 유지 ...
    
    defaultConfig {
        // ... 기존 설정 유지 ...
        
        // BuildConfig에 API Key 주입
        buildConfigField("String", "MAPS_API_KEY", "\"${localProperties["MAPS_API_KEY"]}\"")
        
        // Manifest placeholder
        manifestPlaceholders["MAPS_API_KEY"] = localProperties["MAPS_API_KEY"] ?: ""
    }
    
    buildFeatures {
        compose = true
        buildConfig = true  // BuildConfig 생성 활성화
    }
}
```

### Step 3.5: .gitignore 확인

`android/.gitignore`에 `local.properties`가 포함되어 있는지 확인:
```gitignore
local.properties
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] Google Cloud Console에서 API 키 발급 완료
- [ ] SHA-1 지문 등록 완료
- [ ] `local.properties`에 MAPS_API_KEY 저장
- [ ] `./gradlew assembleDebug` 빌드 성공
- [ ] BuildConfig.MAPS_API_KEY 접근 가능

### 확인 방법
MainActivity에서 테스트 로그:
```kotlin
Log.d("API_KEY", "Loaded: ${BuildConfig.MAPS_API_KEY.take(10)}...")
```

---

## 5. Next
✅ 완료 후 → `task_06_maps_integration.md` 진행
