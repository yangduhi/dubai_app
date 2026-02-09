package com.example.dubaicookiefinder.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dubaicookiefinder.data.location.LocationService
import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.repository.PlacesRepository
import com.example.dubaicookiefinder.ui.components.StoreUiModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 지도 화면 UI 상태
 */
data class MapUiState(
    val stores: List<StoreUiModel> = emptyList(),
    val storeLocations: Map<String, LatLng> = emptyMap(),
    val selectedStoreId: String? = null,
    val currentLocation: LatLng = LatLng(37.5665, 126.9780),
    val lastSearchedLocation: LatLng? = null,
    val isLoading: Boolean = false,
    val isLocationAvailable: Boolean = false,
    val isManualSearch: Boolean = false,  // 수동 검색 모드 (위치 자동 업데이트 중지)
    val availableCount: Int = 0,
    val soldOutCount: Int = 0,
    val error: String? = null
)

/**
 * 지도 화면 ViewModel
 */
class MapViewModel(application: Application) : AndroidViewModel(application) {
    
    private val placesRepository = PlacesRepository(application.applicationContext)
    private val locationService = LocationService(application.applicationContext)
    
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()
    
    private var locationJob: Job? = null
    
    init {
        fetchCurrentLocation()
    }
    
    private fun fetchCurrentLocation() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        if (locationService.hasLocationPermission()) {
            // 마지막 위치로 초기 검색
            locationService.getLastLocation { location ->
                _uiState.value = _uiState.value.copy(
                    currentLocation = location,
                    isLocationAvailable = true
                )
                // 초기 검색만 실행 (수동 검색 모드가 아닐 때만)
                if (!_uiState.value.isManualSearch) {
                    searchAtLocation(location)
                }
            }
            
            // 위치 업데이트는 currentLocation만 갱신 (자동 재검색 안함)
            locationJob = viewModelScope.launch {
                locationService.getLocationUpdates().collect { location ->
                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        isLocationAvailable = true
                    )
                    // 수동 검색 모드가 아니고, 아직 검색한 적 없으면 검색
                    if (!_uiState.value.isManualSearch && _uiState.value.lastSearchedLocation == null) {
                        searchAtLocation(location)
                    }
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(
                isLocationAvailable = false,
                isLoading = false
            )
        }
    }
    
    fun onLocationPermissionGranted() {
        fetchCurrentLocation()
    }
    
    fun selectStore(storeId: String?) {
        _uiState.value = _uiState.value.copy(selectedStoreId = storeId)
    }
    
    /**
     * 특정 위치에서 매장 검색 (현 지도에서 찾기)
     */
    fun searchAtLocation(location: LatLng) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isManualSearch = true  // 수동 검색 모드 활성화
            )
            
            val stores = placesRepository.searchNearbyStores(location)
            
            val sortedStores = stores.sortedBy { store ->
                placesRepository.calculateDistance(location, store.latLng)
            }
            
            val uiModels = sortedStores.map { store -> store.toUiModel(location) }
            val locationMap = sortedStores.associate { it.id to it.latLng }
            
            val available = sortedStores.count { it.stockCount > 0 }
            val soldOut = sortedStores.count { it.stockCount == 0 }
            
            _uiState.value = _uiState.value.copy(
                stores = uiModels,
                storeLocations = locationMap,
                availableCount = available,
                soldOutCount = soldOut,
                isLoading = false,
                lastSearchedLocation = location,
                error = if (stores.isEmpty()) "주변 매장을 찾을 수 없습니다" else null
            )
        }
    }
    
    /**
     * 내 위치로 돌아가기
     */
    fun searchAtMyLocation() {
        _uiState.value = _uiState.value.copy(isManualSearch = false)
        searchAtLocation(_uiState.value.currentLocation)
    }
    
    /**
     * Store → StoreUiModel 변환
     */
    private fun Store.toUiModel(currentLocation: LatLng): StoreUiModel {
        val distance = placesRepository.formatDistance(currentLocation, this.latLng)
        return StoreUiModel(
            id = this.id,
            brandName = this.brand.displayName,
            branchName = this.branchName.ifEmpty { this.brand.displayName },
            brandEmoji = this.brandEmoji,
            fullName = this.fullName,
            address = this.branchName,
            distance = distance,
            stockCount = this.stockCount,
            lastUpdated = this.lastUpdated
        )
    }
}
