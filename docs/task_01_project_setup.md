# task_01_project_setup.md

## 1. Goal (목표)
* Android Studio에서 새 프로젝트를 생성하고, 에뮬레이터에서 "Hello Dubai Cookie! 🍪" 텍스트가 표시되는 앱을 실행한다.
* 이후 모든 Phase의 기반이 되는 빌드 환경을 완성한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 프로젝트 생성 설정
| 항목 | 값 |
|------|-----|
| Template | Empty Activity (Compose) |
| Name | DubaiCookieFinder |
| Package | com.example.dubaicookiefinder |
| Language | Kotlin |
| Minimum SDK | API 26 (Android 8.0) |
| Build Config | Kotlin DSL (build.gradle.kts) |
| 프로젝트 위치 | `d:\vscode\dubai_app\android` |

### 수정 대상 파일
1. `gradle/libs.versions.toml` - 버전 통합 관리
2. `app/build.gradle.kts` - 의존성 설정
3. `app/src/main/java/.../MainActivity.kt` - 기본 UI

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: 프로젝트 생성
1. Android Studio 실행 → `New Project` 선택
2. `Empty Activity` (Compose 로고 있는 것) 선택
3. 위 표의 설정값 입력
4. **Save location**: `d:\vscode\dubai_app\android` 로 지정
5. `Finish` 클릭 후 Gradle Sync 완료 대기 (최초 5~10분)

### Step 3.2: Version Catalog 확인
`gradle/libs.versions.toml` 파일을 열고 아래 버전들이 있는지 확인:

```toml
[versions]
agp = "8.7.3"
kotlin = "2.1.0"
composeBom = "2024.12.01"
coreKtx = "1.15.0"
lifecycleRuntimeKtx = "2.8.7"
activityCompose = "1.9.3"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
```

### Step 3.3: build.gradle.kts (app) 확인
`app/build.gradle.kts`에서 SDK 버전 확인:

```kotlin
android {
    namespace = "com.example.dubaicookiefinder"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dubaicookiefinder"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
```

### Step 3.4: MainActivity.kt 수정
`MainActivity.kt`를 열고 Greeting 함수의 텍스트를 수정:

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello Dubai Cookie! 🍪",
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium
    )
}
```

### Step 3.5: 에뮬레이터 설정
1. `Device Manager` (우측 상단 또는 Tools → Device Manager)
2. `Create Virtual Device` 클릭
3. `Pixel 7` 또는 유사 디바이스 선택
4. System Image: `API 35` 또는 `API 34` 선택
5. `Finish` 후 에뮬레이터 실행

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] Gradle Sync가 에러 없이 완료됨
- [ ] 에뮬레이터가 정상 부팅됨
- [ ] ▶️ Run 버튼 클릭 시 앱이 설치됨
- [ ] 화면에 "Hello Dubai Cookie! 🍪" 텍스트가 표시됨

### 흔한 에러 & 해결법
| 에러 | 해결 |
|------|------|
| `SDK location not found` | File → Project Structure → SDK Location 확인 |
| `Gradle sync failed` | VPN 끄기, 프록시 설정 확인 |
| `compileSdk 35 not found` | SDK Manager → API 35 다운로드 |

---

## 5. Next
✅ 완료 후 → `task_02_design_system.md` 진행
