package com.example.dubaicookiefinder.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes

/**
 * 재고 상태 enum
 */
enum class StockStatus {
    HIGH,    // 10개 이상
    MEDIUM,  // 5~9개
    LOW,     // 1~4개
    EMPTY    // 0개
}

/**
 * 재고 수량으로 상태 결정
 */
fun getStockStatus(count: Int): StockStatus = when {
    count >= 10 -> StockStatus.HIGH
    count >= 5 -> StockStatus.MEDIUM
    count >= 1 -> StockStatus.LOW
    else -> StockStatus.EMPTY
}

/**
 * 재고 상태별 색상
 */
fun getStockColor(status: StockStatus): Color = when (status) {
    StockStatus.HIGH -> AppleColors.StockHigh
    StockStatus.MEDIUM -> AppleColors.StockMedium
    StockStatus.LOW -> AppleColors.StockLow
    StockStatus.EMPTY -> AppleColors.StockEmpty
}

/**
 * 재고 상태별 라벨
 */
fun getStockLabel(status: StockStatus): String = when (status) {
    StockStatus.HIGH -> "재고 충분"
    StockStatus.MEDIUM -> "재고 보통"
    StockStatus.LOW -> "품절 임박"
    StockStatus.EMPTY -> "품절"
}

/**
 * Apple 스타일 재고 배지 (Premium)
 */
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
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.1f)
                    )
                )
            )
            .animateContentSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (status == StockStatus.EMPTY) "품절" else "${stockCount}개 남음",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = color
        )
    }
}
