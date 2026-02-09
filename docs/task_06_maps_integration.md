# task_06_maps_integration.md

## 1. Goal (목표)
* Google Maps Compose 라이브러리를 프로젝트에 추가한다.
* 지도 Placeholder를 실제 GoogleMap Composable로 교체한다.
* 위치 권한을 요청하고 현재 위치로 카메라를 이동시킨다.

---

## 2. Tech Spec & Setup (기술 명세)

### 의존성 추가 (libs.versions.toml)
```toml
[versions]
mapsCompose = "6.4.1"
playServicesMaps = "19.0.0"
playServicesLocation = "21.3.0"
accompanistPermissions = "0.36.0"

[libraries]
maps-compose = { group = "com.google.maps.android", name = "maps-compose", version.ref = "mapsCompose" }
play-services-maps = { group = "com.google.android.gms", name = "play-services-maps", version.ref = "playServicesMaps" }
play-services-location = { group = "com.google.android.gms", name = "play-services-location", version.ref = "playServicesLocation" }
accompanist-permissions = { group = "com.google.accompanist", name = "accompanist-permissions", version.ref = "accompanistPermissions" }
```

### 수정 대상 파일
```
app/
├── build.gradle.kts                    [MODIFY]
├── src/main/AndroidManifest.xml        [MODIFY]
└── src/main/java/.../ui/screens/
    └── MapScreen.kt                    [MODIFY]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: build.gradle.kts (app) 의존성 추가

```kotlin
dependencies {
    // ... 기존 의존성 유지 ...
    
    // Google Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    
    // Permissions
    implementation(libs.accompanist.permissions)
}
```

### Step 3.2: AndroidManifest.xml 수정

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 위치 권한 -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    
    <!-- 인터넷 (지도 타일 로드) -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.DubaiCookieFinder">
        
        <!-- Google Maps API Key -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
            
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.DubaiCookieFinder">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Step 3.3: MapScreen.kt 전체 수정

```kotlin
package com.example.dubaicookiefinder.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.components.StoreCard
import com.example.dubaicookiefinder.ui.components.StoreUiModel
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val sheetState = rememberBottomSheetScaffoldState()
    
    // 서울 강남역 기본 위치
    val defaultLocation = LatLng(37.4979, 127.0276)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }
    
    // 위치 권한 상태
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    // 권한 요청
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    // 현재 위치로 카메라 이동
    val context = LocalContext.current
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f)
                        )
                    }
                }
            } catch (e: SecurityException) {
                // 권한 거부 시 기본 위치 유지
            }
        }
    }
    
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = AppleColors.White,
        sheetShadowElevation = 8.dp,
        sheetDragHandle = { DragHandle() },
        sheetContent = { StoreListSheet() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 실제 Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissions.allPermissionsGranted
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false
                )
            )
            
            // 상단 검색 바 오버레이
            SearchBarOverlay(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )
            
            // 권한 미허용 시 안내
            if (!locationPermissions.allPermissionsGranted) {
                PermissionRequestBanner(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 110.dp, start = 16.dp, end = 16.dp),
                    onRequestPermission = {
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionRequestBanner(
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AppleShapes.medium,
        color = AppleColors.StockMedium.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍 위치 권한이 필요합니다",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleColors.White,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onRequestPermission) {
                Text("허용", color = AppleColors.White)
            }
        }
    }
}

// 기존 DragHandle, SearchBarOverlay, StoreListSheet 유지...
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] 앱 실행 시 Google 지도 표시
- [ ] 위치 권한 요청 다이얼로그 표시
- [ ] 권한 허용 후 현재 위치로 카메라 이동
- [ ] 현재 위치 표시 (파란 점)
- [ ] BottomSheet 정상 동작

### 에뮬레이터 위치 설정
1. 에뮬레이터 → Extended Controls (...)
2. Location → 원하는 좌표 입력
3. `SET LOCATION` 클릭

---

## 5. Next
✅ 완료 후 → `task_07_data_model.md` 진행
