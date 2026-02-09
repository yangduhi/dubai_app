package com.example.dubaicookiefinder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes
import com.example.dubaicookiefinder.utils.ExternalAppUtils

/**
 * 매장 UI 모델
 */
data class StoreUiModel(
    val id: String,
    val brandName: String,
    val branchName: String,
    val brandEmoji: String,
    val fullName: String,
    val address: String,
    val distance: String,
    val stockCount: Int,
    val lastUpdated: String
)

/**
 * Apple 스타일 매장 카드 (네이버 지도 아이콘 버전)
 */
@Composable
fun StoreCard(
    store: StoreUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val context = LocalContext.current
    val stockStatus = getStockStatus(store.stockCount)
    val stockColor = getStockColor(stockStatus)
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) stockColor.copy(alpha = 0.08f) else AppleColors.White,
        animationSpec = tween(300),
        label = "bgColor"
    )
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 12.dp else 4.dp,
                shape = AppleShapes.large,
                ambientColor = stockColor.copy(alpha = 0.15f),
                spotColor = stockColor.copy(alpha = 0.1f)
            )
            .clickable(onClick = onClick),
        shape = AppleShapes.large,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 브랜드 이모지 + 재고 수량
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(56.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(AppleShapes.medium)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    stockColor.copy(alpha = 0.15f),
                                    stockColor.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = store.brandEmoji, fontSize = 22.sp)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (store.stockCount > 0) "${store.stockCount}개" else "품절",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = stockColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 매장 정보
            Column(modifier = Modifier.weight(1f)) {
                // 브랜드명 + 거리 + 네이버 지도 아이콘
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = store.brandName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = AppleColors.TextPrimary
                    )
                    
                    // 거리 + 네이버 지도 아이콘
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 거리 배지
                        Surface(
                            shape = AppleShapes.small,
                            color = AppleColors.LightGray
                        ) {
                            Text(
                                text = store.distance,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AppleColors.TextSecondary
                            )
                        }
                        
                        // 🗺️ 네이버 지도 아이콘 버튼
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    ExternalAppUtils.openNaverMap(context, store.fullName)
                                },
                            shape = CircleShape,
                            color = AppleColors.Green
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🗺️",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                
                // 지점명
                Text(
                    text = store.branchName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppleColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // 재고 상태 + 업데이트 시간
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(stockColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = getStockLabel(stockStatus),
                        style = MaterialTheme.typography.labelSmall,
                        color = stockColor
                    )
                    Text(
                        text = " ・ ${store.lastUpdated}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppleColors.TextSecondary
                    )
                }
            }
        }
    }
}
