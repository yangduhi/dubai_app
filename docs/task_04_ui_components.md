# task_04_ui_components.md

## 1. Goal (목표)
* 실제 데이터를 표시할 UI 컴포넌트들을 구현한다.
* StoreCard (Bento Grid 스타일), MarkerInfoWindow 컴포넌트를 완성한다.
* 재고 상태에 따른 색상 코드를 적용한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 재고 상태 색상 규칙
| 상태 | 조건 | 색상 |
|------|------|------|
| High | 10개 이상 | `#34C759` (Green) |
| Medium | 5~9개 | `#FF9500` (Orange) |
| Low | 1~4개 | `#FF3B30` (Red) |
| Empty | 0개 | `#86868B` (Gray) |

### 생성 대상 파일
```
ui/components/
├── StoreCard.kt          [NEW]
├── MarkerInfoWindow.kt   [NEW]
└── StockBadge.kt         [NEW]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: StockBadge.kt 생성

```kotlin
package com.example.dubaicookiefinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

enum class StockStatus {
    HIGH, MEDIUM, LOW, EMPTY
}

fun getStockStatus(count: Int): StockStatus = when {
    count >= 10 -> StockStatus.HIGH
    count >= 5 -> StockStatus.MEDIUM
    count >= 1 -> StockStatus.LOW
    else -> StockStatus.EMPTY
}

fun getStockColor(status: StockStatus): Color = when (status) {
    StockStatus.HIGH -> AppleColors.StockHigh
    StockStatus.MEDIUM -> AppleColors.StockMedium
    StockStatus.LOW -> AppleColors.StockLow
    StockStatus.EMPTY -> AppleColors.StockEmpty
}

fun getStockLabel(status: StockStatus): String = when (status) {
    StockStatus.HIGH -> "재고 많음"
    StockStatus.MEDIUM -> "재고 보통"
    StockStatus.LOW -> "재고 적음"
    StockStatus.EMPTY -> "품절"
}

@Composable
fun StockBadge(
    stockCount: Int,
    modifier: Modifier = Modifier
) {
    val status = getStockStatus(stockCount)
    val color = getStockColor(status)
    
    Box(
        modifier = modifier
            .clip(AppleShapes.small)
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (status == StockStatus.EMPTY) "품절" else "${stockCount}개",
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}
```

### Step 3.2: StoreCard.kt 생성

```kotlin
package com.example.dubaicookiefinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

data class StoreUiModel(
    val id: String,
    val name: String,
    val address: String,
    val distance: String,
    val stockCount: Int
)

@Composable
fun StoreCard(
    store: StoreUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = AppleShapes.large,
        color = AppleColors.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 쿠키 아이콘
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(AppleShapes.medium)
                    .background(AppleColors.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🍪",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 정보 영역
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 매장명 + 거리
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.headlineSmall,
                        color = AppleColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = store.distance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppleColors.TextSecondary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 주소
                Text(
                    text = store.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 재고 배지
                StockBadge(stockCount = store.stockCount)
            }
        }
    }
}
```

### Step 3.3: MarkerInfoWindow.kt 생성

```kotlin
package com.example.dubaicookiefinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

/**
 * 지도 마커 클릭 시 표시되는 InfoWindow
 * Google Maps Compose의 MarkerInfoWindowContent에서 사용
 */
@Composable
fun MarkerInfoWindow(
    store: StoreUiModel,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = AppleShapes.medium,
        color = AppleColors.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .widthIn(min = 160.dp, max = 240.dp)
        ) {
            // 매장명
            Text(
                text = store.name,
                style = MaterialTheme.typography.headlineSmall,
                color = AppleColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 거리
            Text(
                text = store.distance,
                style = MaterialTheme.typography.bodyMedium,
                color = AppleColors.TextSecondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 재고 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🍪",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                StockBadge(stockCount = store.stockCount)
            }
        }
    }
}
```

### Step 3.4: MapScreen.kt 업데이트
기존 PlaceholderStoreCard를 StoreCard로 교체:

```kotlin
// StoreListSheet() 함수 내용 수정

@Composable
private fun StoreListSheet() {
    val mockStores = remember {
        listOf(
            StoreUiModel("1", "두바이 쿠키 강남점", "서울시 강남구 테헤란로 123", "350m", 15),
            StoreUiModel("2", "두바이 쿠키 역삼점", "서울시 강남구 역삼로 456", "520m", 7),
            StoreUiModel("3", "두바이 쿠키 선릉점", "서울시 강남구 선릉로 789", "800m", 3),
            StoreUiModel("4", "두바이 쿠키 삼성점", "서울시 강남구 삼성로 321", "1.2km", 0),
            StoreUiModel("5", "두바이 쿠키 청담점", "서울시 강남구 청담로 654", "1.5km", 22)
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "주변 매장",
            style = MaterialTheme.typography.headlineMedium,
            color = AppleColors.TextPrimary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        mockStores.forEach { store ->
            StoreCard(
                store = store,
                onClick = { /* TODO: 마커 이동 */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] StoreCard 컴포넌트 렌더링 성공
- [ ] 재고 상태별 색상 배지 표시
- [ ] 15개 → 초록색, 7개 → 주황색, 3개 → 빨간색, 0개 → 회색
- [ ] 카드 클릭 시 ripple 효과

---

## 5. Next
✅ 완료 후 → `task_05_api_key_setup.md` 진행
