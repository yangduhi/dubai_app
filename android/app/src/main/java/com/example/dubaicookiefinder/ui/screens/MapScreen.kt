package com.example.dubaicookiefinder.ui.screens

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dubaicookiefinder.ui.components.StoreCard
import com.example.dubaicookiefinder.ui.components.StoreUiModel
import com.example.dubaicookiefinder.ui.components.getStockStatus
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.example.dubaicookiefinder.ui.theme.AppleShapes
import com.example.dubaicookiefinder.ui.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * 메인 지도 화면
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    
    var showSearchButton by remember { mutableStateOf(false) }
    var currentCameraCenter by remember { mutableStateOf<LatLng?>(null) }
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.currentLocation, 14f)
    }
    
    val locationPermissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    
    LaunchedEffect(Unit) {
        if (!locationPermissions.allPermissionsGranted) {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            viewModel.onLocationPermissionGranted()
        }
    }
    
    // 카메라 이동 완료 감지
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val newCenter = cameraPositionState.position.target
            currentCameraCenter = newCenter
            
            val lastLoc = uiState.lastSearchedLocation ?: uiState.currentLocation
            val distance = calculateDistanceSimple(lastLoc, newCenter)
            showSearchButton = distance > 300
        }
    }
    
    // 초기 위치로 카메라 이동
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.currentLocation) {
        if (!isInitialized && uiState.currentLocation != LatLng(37.5665, 126.9780)) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(uiState.currentLocation, 14f)
            isInitialized = true
        }
    }
    
    BottomSheetScaffold(
        scaffoldState = sheetState,
        sheetPeekHeight = 220.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = Color.Transparent,
        sheetShadowElevation = 0.dp,
        sheetDragHandle = null,
        sheetContent = {
            PremiumStoreListSheet(
                stores = uiState.stores,
                selectedStoreId = uiState.selectedStoreId,
                isLoading = uiState.isLoading,
                onStoreClick = { store -> viewModel.selectStore(store.id) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (locationPermissions.allPermissionsGranted) {
                // Google Map
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true,
                        mapType = MapType.NORMAL
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,  // 기본 버튼 비활성화
                        compassEnabled = false
                    )
                ) {
                    uiState.stores.forEach { store ->
                        val position = uiState.storeLocations[store.id] ?: return@forEach
                        
                        val stockStatus = getStockStatus(store.stockCount)
                        val markerColor = when (stockStatus) {
                            com.example.dubaicookiefinder.ui.components.StockStatus.HIGH -> BitmapDescriptorFactory.HUE_GREEN
                            com.example.dubaicookiefinder.ui.components.StockStatus.MEDIUM -> BitmapDescriptorFactory.HUE_ORANGE
                            com.example.dubaicookiefinder.ui.components.StockStatus.LOW -> BitmapDescriptorFactory.HUE_RED
                            com.example.dubaicookiefinder.ui.components.StockStatus.EMPTY -> BitmapDescriptorFactory.HUE_VIOLET
                        }
                        
                        Marker(
                            state = MarkerState(position = position),
                            title = store.fullName,
                            snippet = "${store.stockCount}개 재고 | ${store.distance}",
                            icon = BitmapDescriptorFactory.defaultMarker(markerColor),
                            onClick = {
                                viewModel.selectStore(store.id)
                                false
                            }
                        )
                    }
                }
            } else {
                PermissionDeniedPlaceholder()
            }
            
            // 🔍 "현 지도에서 찾기" 버튼
            AnimatedVisibility(
                visible = showSearchButton && !uiState.isLoading,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                SearchInAreaButton(
                    onClick = {
                        currentCameraCenter?.let { center ->
                            viewModel.searchAtLocation(center)
                            showSearchButton = false
                        }
                    }
                )
            }
            
            // 📍 내 위치로 이동 버튼 (우측 상단, 아래로 내림)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 16.dp)  // 위에서 80dp
                    .size(48.dp)
                    .shadow(8.dp, CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(uiState.currentLocation, 14f)
                            )
                            viewModel.searchAtMyLocation()
                            showSearchButton = false
                        }
                    },
                shape = CircleShape,
                color = AppleColors.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "📍", fontSize = 20.sp)
                }
            }
            
            // 로딩 인디케이터
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp),
                    color = AppleColors.Green
                )
            }
        }
    }
}

/**
 * 🔍 현 지도에서 찾기 버튼
 */
@Composable
private fun SearchInAreaButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .shadow(8.dp, AppleShapes.large)
            .height(44.dp),
        shape = AppleShapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppleColors.Green,
            contentColor = AppleColors.White
        )
    ) {
        Text(
            text = "🔍 현 지도에서 찾기",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

/**
 * 간단한 거리 계산 (미터)
 */
private fun calculateDistanceSimple(from: LatLng, to: LatLng): Double {
    val latDiff = (to.latitude - from.latitude) * 111000
    val lngDiff = (to.longitude - from.longitude) * 111000 * 
                  kotlin.math.cos(Math.toRadians(from.latitude))
    return kotlin.math.sqrt(latDiff * latDiff + lngDiff * lngDiff)
}

/**
 * Premium 하단 매장 리스트 시트
 */
@Composable
private fun PremiumStoreListSheet(
    stores: List<StoreUiModel>,
    selectedStoreId: String?,
    isLoading: Boolean,
    onStoreClick: (StoreUiModel) -> Unit
) {
    val availableCount = stores.count { it.stockCount > 0 }
    val soldOutCount = stores.count { it.stockCount == 0 }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppleColors.White,
                        AppleColors.LightGray.copy(alpha = 0.5f)
                    )
                )
            )
    ) {
        // 드래그 핸들
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AppleColors.TextSecondary.copy(alpha = 0.3f))
        )
        
        // 헤더
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "주변 매장",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = AppleColors.TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = AppleShapes.small,
                    color = AppleColors.StockHigh.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🍪", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "재고 ${availableCount}곳",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = AppleColors.StockHigh
                        )
                    }
                }
                
                Surface(
                    shape = AppleShapes.small,
                    color = AppleColors.StockEmpty.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "❌", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "품절 ${soldOutCount}곳",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = AppleColors.StockEmpty
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppleColors.Green)
            }
        } else if (stores.isEmpty()) {
            EmptyStoreMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stores, key = { it.id }) { store ->
                    StoreCard(
                        store = store,
                        isSelected = store.id == selectedStoreId,
                        onClick = { onStoreClick(store) }
                    )
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyStoreMessage() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🔍", style = MaterialTheme.typography.displayMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "주변에 매장이 없습니다",
            style = MaterialTheme.typography.titleMedium,
            color = AppleColors.TextPrimary
        )
        Text(
            text = "지도를 이동하여 다른 지역을 검색해보세요",
            style = MaterialTheme.typography.bodyMedium,
            color = AppleColors.TextSecondary
        )
    }
}

@Composable
private fun PermissionDeniedPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppleColors.LightGray, AppleColors.White))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(40.dp)) {
            Text(text = "📍", style = MaterialTheme.typography.displayLarge)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "위치 권한이 필요합니다",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = AppleColors.TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "주변 쿠키 매장을 찾으려면\n위치 권한을 허용해주세요.",
                style = MaterialTheme.typography.bodyLarge,
                color = AppleColors.TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
