# task_09_integration.md

## 1. Goal (목표)
* 전체 앱 플로우를 통합 테스트한다.
* Edge Case(GPS 꺼짐, 재고 0개, 네트워크 오류)를 처리한다.
* 로딩/에러 상태 UI를 구현한다.

---

## 2. Tech Spec & Setup (기술 명세)

### Edge Case 목록
| 케이스 | 대응 |
|--------|------|
| GPS 권한 거부 | 기본 위치(강남역) 사용 + 권한 요청 배너 |
| 위치 서비스 OFF | 설정 이동 안내 |
| 재고 0개 매장 | 회색 마커 + "품절" 배지 |
| 매장 목록 비어있음 | Empty State UI |
| 로딩 중 | Skeleton/Shimmer 효과 |

### 수정 대상 파일
```
ui/screens/MapScreen.kt         [MODIFY]
ui/components/EmptyState.kt     [NEW]
ui/components/LoadingOverlay.kt [NEW]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: EmptyState.kt 생성

```kotlin
package com.example.dubaicookiefinder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors

@Composable
fun EmptyState(
    emoji: String = "🍪",
    title: String = "주변에 매장이 없습니다",
    subtitle: String = "다른 지역으로 이동해 보세요",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = AppleColors.TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = AppleColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
```

### Step 3.2: LoadingOverlay.kt 생성

```kotlin
package com.example.dubaicookiefinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors

@Composable
fun LoadingOverlay(
    message: String = "매장 정보를 불러오는 중...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppleColors.White.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = AppleColors.Blue,
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = AppleColors.TextSecondary
            )
        }
    }
}
```

### Step 3.3: MapScreen.kt에 상태 처리 추가

```kotlin
// MapScreen 함수 내 BottomSheetScaffold 내부에 추가:

// 로딩 상태
val isLoading by viewModel.isLoading.collectAsState()

// sheetContent 수정
sheetContent = {
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppleColors.Blue)
            }
        }
        stores.isEmpty() -> {
            EmptyState(
                emoji = "🔍",
                title = "주변에 매장이 없습니다",
                subtitle = "검색 범위를 넓혀보세요"
            )
        }
        else -> {
            StoreListSheet(
                stores = stores,
                viewModel = viewModel,
                selectedStore = selectedStore,
                onStoreClick = onStoreClick
            )
        }
    }
}
```

### Step 3.4: 위치 서비스 비활성화 처리

```kotlin
// MapScreen.kt에 추가

@Composable
private fun LocationDisabledBanner(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = AppleShapes.medium,
        color = AppleColors.StockLow.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📍 위치 서비스가 꺼져 있습니다",
                style = MaterialTheme.typography.bodyMedium,
                color = AppleColors.White,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onOpenSettings) {
                Text("설정", color = AppleColors.White)
            }
        }
    }
}

// 설정 화면 이동 함수
private fun openLocationSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}
```

---

## 4. Validation (검증)

### 테스트 시나리오
| # | 시나리오 | 예상 동작 |
|---|----------|-----------|
| 1 | GPS 권한 거부 | 강남역 기본 위치 + 권한 요청 배너 |
| 2 | 모든 마커 클릭 | InfoWindow + 리스트 하이라이트 |
| 3 | 품절 매장 | 회색 마커 + "품절" 배지 |
| 4 | 앱 재시작 | 데이터 정상 로드 |

### 에뮬레이터 테스트 팁
```
GPS 끄기: Settings → Location → OFF
권한 초기화: Settings → Apps → DubaiCookieFinder → Permissions → Revoke
```

---

## 5. Next
✅ 완료 후 → `task_10_final_build.md` 진행
