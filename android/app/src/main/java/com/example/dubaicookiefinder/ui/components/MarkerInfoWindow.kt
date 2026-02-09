package com.example.dubaicookiefinder.ui.components

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
 * 지도 마커 InfoWindow 컴포넌트
 * 
 * 마커 클릭 시 표시되는 정보 팝업
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
                text = store.fullName,
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
