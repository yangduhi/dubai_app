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
            backgroundColor.copy(alpha = 0.92f)
        }
    )
    .border(
        width = 1.dp,
        color = borderColor,
        shape = AppleShapes.large
    )
