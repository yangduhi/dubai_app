# task_02_design_system.md

## 1. Goal (목표)
* Apple 스타일의 커스텀 디자인 시스템을 구축한다.
* Color, Typography, Shape를 정의하고 Theme.kt에 통합한다.
* Glassmorphism (Blur) 효과 유틸리티를 구현한다.

---

## 2. Tech Spec & Setup (기술 명세)

### Apple Style 디자인 가이드
| 요소 | 값 |
|------|-----|
| Background | `#FFFFFF`, `#F5F5F7` |
| Text Primary | `#1D1D1F` |
| Text Secondary | `#86868B` |
| Accent | `#0071E3` |
| Corner Radius | 20dp ~ 24dp |
| Shadow | Soft, Wide spread (elevation 2-4dp) |

### 수정/생성 대상 파일
```
app/src/main/java/com/example/dubaicookiefinder/ui/theme/
├── Color.kt      [MODIFY]
├── Type.kt       [MODIFY]
├── Shape.kt      [NEW]
└── Theme.kt      [MODIFY]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: Color.kt 수정
기존 파일을 아래 내용으로 교체:

```kotlin
package com.example.dubaicookiefinder.ui.theme

import androidx.compose.ui.graphics.Color

// Apple Style Colors
object AppleColors {
    // Backgrounds
    val White = Color(0xFFFFFFFF)
    val LightGray = Color(0xFFF5F5F7)
    
    // Text
    val TextPrimary = Color(0xFF1D1D1F)
    val TextSecondary = Color(0xFF86868B)
    
    // Accent
    val Blue = Color(0xFF0071E3)
    val Green = Color(0xFF34C759)
    val Red = Color(0xFFFF3B30)
    val Orange = Color(0xFFFF9500)
    
    // Stock Status
    val StockHigh = Color(0xFF34C759)    // 재고 많음
    val StockMedium = Color(0xFFFF9500)  // 재고 보통
    val StockLow = Color(0xFFFF3B30)     // 재고 적음
    val StockEmpty = Color(0xFF86868B)   // 재고 없음
    
    // Glass Effect
    val GlassBackground = Color(0x99FFFFFF)  // 60% white
    val GlassBorder = Color(0x33FFFFFF)      // 20% white
}
```

### Step 3.2: Type.kt 수정
기존 파일을 아래 내용으로 교체:

```kotlin
package com.example.dubaicookiefinder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppleTypography = Typography(
    // Large Title (Apple Style)
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = 0.25.sp
    ),
    
    // Title 1
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    
    // Title 2
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    
    // Title 3
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    
    // Body
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    
    // Callout
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    
    // Subhead
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    
    // Caption 1
    labelLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    
    // Caption 2
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 13.sp
    )
)
```

### Step 3.3: Shape.kt 생성 (새 파일)
`theme/` 폴더에 새 파일 생성:

```kotlin
package com.example.dubaicookiefinder.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppleShapes = Shapes(
    // Card, BottomSheet
    extraLarge = RoundedCornerShape(24.dp),
    
    // Button, InfoWindow
    large = RoundedCornerShape(20.dp),
    
    // Chip, Tag
    medium = RoundedCornerShape(16.dp),
    
    // Small elements
    small = RoundedCornerShape(12.dp),
    
    // Very small
    extraSmall = RoundedCornerShape(8.dp)
)
```

### Step 3.4: Theme.kt 수정
기존 파일을 아래 내용으로 교체:

```kotlin
package com.example.dubaicookiefinder.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppleColors.Blue,
    onPrimary = AppleColors.White,
    secondary = AppleColors.TextSecondary,
    onSecondary = AppleColors.White,
    background = AppleColors.White,
    onBackground = AppleColors.TextPrimary,
    surface = AppleColors.LightGray,
    onSurface = AppleColors.TextPrimary,
    error = AppleColors.Red,
    onError = AppleColors.White
)

private val DarkColorScheme = darkColorScheme(
    primary = AppleColors.Blue,
    onPrimary = AppleColors.White,
    secondary = AppleColors.TextSecondary,
    onSecondary = AppleColors.White,
    background = AppleColors.TextPrimary,
    onBackground = AppleColors.White,
    surface = AppleColors.TextPrimary,
    onSurface = AppleColors.White,
    error = AppleColors.Red,
    onError = AppleColors.White
)

@Composable
fun DubaiCookieFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppleTypography,
        shapes = AppleShapes,
        content = content
    )
}
```

### Step 3.5: GlassModifier.kt 생성 (새 파일)
`ui/components/` 폴더 생성 후 새 파일 생성:

```kotlin
package com.example.dubaicookiefinder.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

/**
 * Apple 스타일 Glassmorphism 효과 Modifier
 * 
 * ⚠️ 호환성 주의:
 * - API 31+ (Android 12+): RenderEffect 기반 실시간 블러
 * - API 31 미만: 반투명 Alpha 처리로 대체 (성능 최적화)
 */
fun Modifier.glassEffect(
    blurRadius: Dp = 20.dp,
    backgroundColor: Color = AppleColors.GlassBackground,
    borderColor: Color = AppleColors.GlassBorder
): Modifier = this
    .clip(AppleShapes.large)
    .then(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+: 실제 블러 효과
            Modifier.blur(blurRadius)
        } else {
            // API 31 미만: 블러 없이 반투명 배경으로 대체
            Modifier
        }
    )
    .background(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            backgroundColor
        } else {
            // 하위 버전에서는 더 불투명하게 처리하여 가독성 확보
            backgroundColor.copy(alpha = 0.85f)
        }
    )
    .border(
        width = 1.dp,
        color = borderColor,
        shape = AppleShapes.large
    )
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] Color.kt 컴파일 에러 없음
- [ ] Type.kt 컴파일 에러 없음
- [ ] Shape.kt 파일 생성 및 컴파일 성공
- [ ] Theme.kt에서 AppleTypography, AppleShapes 사용
- [ ] MainActivity에서 테마 적용 확인

### Preview 테스트
MainActivity.kt에 Preview 추가하여 확인:

```kotlin
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DubaiCookieFinderTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Large Title",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Body Text",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
```

---

## 5. Next
✅ 완료 후 → `task_03_layout_skeleton.md` 진행
