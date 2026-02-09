# task_03_layout_skeleton.md

## 1. Goal (목표)
* 앱의 메인 화면 레이아웃 스켈레톤을 구현한다.
* 상단 검색 바 (Glassmorphism), 지도 영역 Placeholder, 하단 BottomSheet 구조를 완성한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 레이아웃 구조
```
┌─────────────────────────────┐
│  🔍 Search Bar (Blur)       │  ← 상단 오버레이
├─────────────────────────────┤
│                             │
│      Google Map Area        │  ← 전체 화면
│      (Placeholder)          │
│                             │
├─────────────────────────────┤
│  ▲ Drag Handle              │  ← BottomSheet
│  ┌───────────────────────┐  │
│  │ Store Card 1          │  │
│  ├───────────────────────┤  │
│  │ Store Card 2          │  │
│  └───────────────────────┘  │
└─────────────────────────────┘
```

### 수정/생성 대상 파일
```
app/src/main/java/com/example/dubaicookiefinder/
├── MainActivity.kt           [MODIFY]
└── ui/
    └── screens/
        └── MapScreen.kt      [NEW]
```

### 의존성 추가 (libs.versions.toml)
```toml
[versions]
material3 = "1.3.1"

[libraries]
androidx-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "material3" }
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: MapScreen.kt 생성
`ui/screens/` 폴더 생성 후 새 파일:

```kotlin
package com.example.dubaicookiefinder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {
    val sheetState = rememberBottomSheetScaffoldState()
    
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = AppleColors.White,
        sheetShadowElevation = 8.dp,
        sheetDragHandle = {
            DragHandle()
        },
        sheetContent = {
            StoreListSheet()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 지도 Placeholder
            MapPlaceholder()
            
            // 상단 검색 바 오버레이
            SearchBarOverlay(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(36.dp)
            .height(5.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .background(AppleColors.TextSecondary.copy(alpha = 0.3f))
    )
}

@Composable
private fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppleColors.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🗺️",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Google Map\n(Phase 3에서 연동)",
                style = MaterialTheme.typography.bodyLarge,
                color = AppleColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SearchBarOverlay(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = AppleShapes.large,
        color = AppleColors.GlassBackground,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "주변 쿠키 매장 검색...",
                style = MaterialTheme.typography.bodyLarge,
                color = AppleColors.TextSecondary
            )
        }
    }
}

@Composable
private fun StoreListSheet() {
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
        
        // Placeholder 카드들
        repeat(5) { index ->
            PlaceholderStoreCard(index + 1)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PlaceholderStoreCard(index: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = AppleShapes.large,
        color = AppleColors.LightGray
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 영역
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(AppleShapes.medium)
                    .background(AppleColors.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🍪", style = MaterialTheme.typography.headlineMedium)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 정보 영역
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "매장 $index",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AppleColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "재고: --개 | 거리: --m",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleColors.TextSecondary
                )
            }
        }
    }
}
```

### Step 3.2: MainActivity.kt 수정
MapScreen을 호출하도록 수정:

```kotlin
package com.example.dubaicookiefinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dubaicookiefinder.ui.screens.MapScreen
import com.example.dubaicookiefinder.ui.theme.DubaiCookieFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DubaiCookieFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MapScreen()
                }
            }
        }
    }
}
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] 앱 실행 시 지도 Placeholder 영역 표시
- [ ] 상단에 검색 바 오버레이 표시
- [ ] 하단 BottomSheet 스와이프 동작
- [ ] BottomSheet에 Placeholder 카드 5개 표시

---

## 5. Next
✅ 완료 후 → `task_04_ui_components.md` 진행
