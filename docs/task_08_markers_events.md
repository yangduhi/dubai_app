# task_08_markers_events.md

## 1. Goal (목표)
* 지도에 매장 위치 마커를 표시한다.
* 마커 클릭 시 해당 매장 정보를 BottomSheet에 표시한다.
* 재고량에 따라 마커 색상을 차별화한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 마커 색상 규칙
| 재고 상태 | 색상 | BitmapDescriptor |
|-----------|------|------------------|
| 10개 이상 | Green | HUE_GREEN (120) |
| 5~9개 | Orange | HUE_ORANGE (30) |
| 1~4개 | Red | HUE_RED (0) |
| 0개 | Gray | HUE_AZURE (210) |

### 수정 대상 파일
```
ui/screens/MapScreen.kt    [MODIFY]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: MapScreen.kt 마커 추가

```kotlin
package com.example.dubaicookiefinder.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.ui.components.StoreCard
import com.example.dubaicookiefinder.ui.components.StoreUiModel
import com.example.dubaicookiefinder.ui.components.getStockStatus
import com.example.dubaicookiefinder.ui.components.StockStatus
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes
import com.example.dubaicookiefinder.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val stores by viewModel.stores.collectAsState()
    val selectedStore by viewModel.selectedStore.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    
    // 서울 강남역 기본 위치
    val defaultLocation = LatLng(37.4979, 127.0276)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }
    
    // 위치 권한
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    // 현재 위치로 이동 및 ViewModel 업데이트
    val context = LocalContext.current
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        viewModel.updateLocation(currentLatLng)
                        cameraPositionState.move(
                            CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f)
                        )
                    }
                }
            } catch (e: SecurityException) {
                // 기본 위치 사용
                viewModel.updateLocation(defaultLocation)
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
        sheetContent = {
            StoreListSheet(
                stores = stores,
                viewModel = viewModel,
                selectedStore = selectedStore,
                onStoreClick = { store ->
                    viewModel.selectStore(store)
                    scope.launch {
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(store.latLng, 16f)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
            ) {
                // 매장 마커들
                stores.forEach { store ->
                    StoreMarker(
                        store = store,
                        isSelected = selectedStore?.id == store.id,
                        onClick = {
                            viewModel.selectStore(store)
                            scope.launch {
                                sheetState.bottomSheetState.expand()
                            }
                        }
                    )
                }
            }
            
            // 상단 검색 바
            SearchBarOverlay(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}

/**
 * 재고 상태에 따른 마커 색상 반환
 */
private fun getMarkerHue(stockCount: Int): Float {
    return when (getStockStatus(stockCount)) {
        StockStatus.HIGH -> BitmapDescriptorFactory.HUE_GREEN
        StockStatus.MEDIUM -> BitmapDescriptorFactory.HUE_ORANGE
        StockStatus.LOW -> BitmapDescriptorFactory.HUE_RED
        StockStatus.EMPTY -> BitmapDescriptorFactory.HUE_AZURE
    }
}

@Composable
private fun StoreMarker(
    store: Store,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Marker(
        state = MarkerState(position = store.latLng),
        title = store.name,
        snippet = "재고: ${store.stockCount}개",
        icon = BitmapDescriptorFactory.defaultMarker(getMarkerHue(store.stockCount)),
        alpha = if (isSelected) 1f else 0.85f,
        onClick = {
            onClick()
            false // false: InfoWindow 표시, true: 표시 안함
        }
    )
}

@Composable
private fun StoreListSheet(
    stores: List<Store>,
    viewModel: MapViewModel,
    selectedStore: Store?,
    onStoreClick: (Store) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "주변 매장",
                style = MaterialTheme.typography.headlineMedium,
                color = AppleColors.TextPrimary
            )
            Text(
                text = "${stores.size}개",
                style = MaterialTheme.typography.bodyLarge,
                color = AppleColors.TextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn {
            items(stores, key = { it.id }) { store ->
                val uiModel = StoreUiModel(
                    id = store.id,
                    name = store.name,
                    address = store.address,
                    distance = viewModel.getDistanceText(store),
                    stockCount = store.stockCount
                )
                StoreCard(
                    store = uiModel,
                    onClick = { onStoreClick(store) },
                    modifier = Modifier.then(
                        if (selectedStore?.id == store.id) {
                            Modifier.background(
                                AppleColors.Blue.copy(alpha = 0.1f),
                                AppleShapes.large
                            )
                        } else Modifier
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// DragHandle, SearchBarOverlay 함수들은 기존 유지...
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] 지도에 8개 마커 표시
- [ ] 마커 색상이 재고량에 따라 다름 (초록/주황/빨강/파랑)
- [ ] 마커 클릭 시 InfoWindow 표시
- [ ] 마커 클릭 시 해당 매장이 리스트에서 하이라이트
- [ ] 리스트 카드 클릭 시 해당 마커로 카메라 이동

---

## 5. Next
✅ 완료 후 → `task_09_integration.md` 진행
