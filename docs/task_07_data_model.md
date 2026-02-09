# task_07_data_model.md

## 1. Goal (목표)
* Store 데이터 모델을 정의한다.
* Kotlin 하드코딩 방식으로 Mock 데이터를 생성한다.
* Repository 패턴을 적용하여 데이터 접근 계층을 분리한다.

---

## 2. Tech Spec & Setup (기술 명세)

### 아키텍처 (MVVM)
```
┌─────────────┐     ┌──────────────┐     ┌────────────────┐
│  UI Layer   │ ←── │  ViewModel   │ ←── │   Repository   │
│ (Compose)   │     │              │     │  (Mock Data)   │
└─────────────┘     └──────────────┘     └────────────────┘
```

### 생성 대상 파일
```
app/src/main/java/com/example/dubaicookiefinder/
├── data/
│   ├── model/
│   │   └── Store.kt              [NEW]
│   └── repository/
│       ├── StoreRepository.kt    [NEW]
│       └── MockData.kt           [NEW]
└── viewmodel/
    └── MapViewModel.kt           [NEW]
```

---

## 3. Step-by-Step Instructions (AI 지시 사항)

### Step 3.1: Store.kt 생성

```kotlin
package com.example.dubaicookiefinder.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * 매장 데이터 모델
 */
data class Store(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val stockCount: Int,
    val lastUpdated: String = ""  // ISO 8601 형식
) {
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
}
```

### Step 3.2: MockData.kt 생성 (하드코딩 방식)

```kotlin
package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store

/**
 * Mock 매장 데이터
 * 
 * MVP 단계에서는 Kotlin 하드코딩으로 관리.
 * 추후 실제 API 연동 시 Repository 인터페이스만 교체하면 됨.
 */
object MockData {
    
    val stores = listOf(
        // 강남역 주변
        Store(
            id = "store_001",
            name = "두바이 쿠키 강남점",
            address = "서울시 강남구 강남대로 396",
            latitude = 37.4979,
            longitude = 127.0276,
            stockCount = 15,
            lastUpdated = "2026-02-09T10:30:00"
        ),
        Store(
            id = "store_002",
            name = "두바이 쿠키 역삼점",
            address = "서울시 강남구 테헤란로 152",
            latitude = 37.5000,
            longitude = 127.0365,
            stockCount = 7,
            lastUpdated = "2026-02-09T09:45:00"
        ),
        Store(
            id = "store_003",
            name = "두바이 쿠키 선릉점",
            address = "서울시 강남구 테헤란로 340",
            latitude = 37.5045,
            longitude = 127.0490,
            stockCount = 3,
            lastUpdated = "2026-02-09T11:00:00"
        ),
        Store(
            id = "store_004",
            name = "두바이 쿠키 삼성점",
            address = "서울시 강남구 삼성로 512",
            latitude = 37.5089,
            longitude = 127.0630,
            stockCount = 0,  // 품절
            lastUpdated = "2026-02-09T08:00:00"
        ),
        Store(
            id = "store_005",
            name = "두바이 쿠키 청담점",
            address = "서울시 강남구 압구정로 443",
            latitude = 37.5205,
            longitude = 127.0470,
            stockCount = 22,
            lastUpdated = "2026-02-09T11:30:00"
        ),
        // 추가 매장
        Store(
            id = "store_006",
            name = "두바이 쿠키 압구정점",
            address = "서울시 강남구 압구정로 161",
            latitude = 37.5270,
            longitude = 127.0280,
            stockCount = 11,
            lastUpdated = "2026-02-09T10:00:00"
        ),
        Store(
            id = "store_007",
            name = "두바이 쿠키 신사점",
            address = "서울시 강남구 도산대로 115",
            latitude = 37.5165,
            longitude = 127.0200,
            stockCount = 5,
            lastUpdated = "2026-02-09T09:30:00"
        ),
        Store(
            id = "store_008",
            name = "두바이 쿠키 논현점",
            address = "서울시 강남구 학동로 234",
            latitude = 37.5110,
            longitude = 127.0320,
            stockCount = 1,
            lastUpdated = "2026-02-09T11:15:00"
        )
    )
}
```

### Step 3.3: StoreRepository.kt 생성

```kotlin
package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

/**
 * 매장 데이터 Repository
 * 
 * 확장성: 추후 API 연동 시 이 인터페이스를 구현하는 
 * ApiStoreRepository를 만들어 교체하면 됨.
 */
interface StoreRepository {
    suspend fun getAllStores(): List<Store>
    suspend fun getStoreById(id: String): Store?
    suspend fun getNearbyStores(location: LatLng, radiusKm: Double): List<Store>
}

class MockStoreRepository : StoreRepository {
    
    override suspend fun getAllStores(): List<Store> {
        return MockData.stores
    }
    
    override suspend fun getStoreById(id: String): Store? {
        return MockData.stores.find { it.id == id }
    }
    
    override suspend fun getNearbyStores(
        location: LatLng, 
        radiusKm: Double
    ): List<Store> {
        return MockData.stores
            .map { store -> 
                store to calculateDistance(location, store.latLng) 
            }
            .filter { (_, distance) -> 
                distance <= radiusKm 
            }
            .sortedBy { (_, distance) -> 
                distance 
            }
            .map { (store, _) -> 
                store 
            }
    }
    
    /**
     * Haversine 공식으로 두 좌표 간 거리 계산 (km)
     */
    private fun calculateDistance(from: LatLng, to: LatLng): Double {
        val earthRadius = 6371.0 // km
        
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        
        val a = sin(dLat / 2).pow(2) + 
                cos(Math.toRadians(from.latitude)) * 
                cos(Math.toRadians(to.latitude)) * 
                sin(dLon / 2).pow(2)
        
        val c = 2 * asin(sqrt(a))
        
        return earthRadius * c
    }
}
```

### Step 3.4: MapViewModel.kt 생성

```kotlin
package com.example.dubaicookiefinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.repository.MockStoreRepository
import com.example.dubaicookiefinder.data.repository.StoreRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 지도 화면 ViewModel
 */
class MapViewModel(
    private val repository: StoreRepository = MockStoreRepository()
) : ViewModel() {
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _selectedStore = MutableStateFlow<Store?>(null)
    val selectedStore: StateFlow<Store?> = _selectedStore.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.asStateFlow()
    
    init {
        loadAllStores()
    }
    
    fun loadAllStores() {
        viewModelScope.launch {
            _isLoading.value = true
            _stores.value = repository.getAllStores()
            _isLoading.value = false
        }
    }
    
    fun loadNearbyStores(location: LatLng, radiusKm: Double = 5.0) {
        viewModelScope.launch {
            _isLoading.value = true
            _currentLocation.value = location
            _stores.value = repository.getNearbyStores(location, radiusKm)
            _isLoading.value = false
        }
    }
    
    fun selectStore(store: Store?) {
        _selectedStore.value = store
    }
    
    fun updateLocation(location: LatLng) {
        _currentLocation.value = location
    }
    
    /**
     * 현재 위치에서 매장까지의 거리 계산 (표시용)
     */
    fun getDistanceText(store: Store): String {
        val current = _currentLocation.value ?: return "--"
        
        val distance = calculateDistance(current, store.latLng)
        
        return when {
            distance < 1.0 -> "${(distance * 1000).toInt()}m"
            else -> String.format("%.1fkm", distance)
        }
    }
    
    private fun calculateDistance(from: LatLng, to: LatLng): Double {
        val earthRadius = 6371.0
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = kotlin.math.sin(dLat / 2).let { it * it } +
                kotlin.math.cos(Math.toRadians(from.latitude)) *
                kotlin.math.cos(Math.toRadians(to.latitude)) *
                kotlin.math.sin(dLon / 2).let { it * it }
        return 2 * kotlin.math.asin(kotlin.math.sqrt(a)) * earthRadius
    }
}
```

---

## 4. Validation (검증)

### 성공 기준 체크리스트
- [ ] Store 데이터 클래스 컴파일 성공
- [ ] MockData.stores 8개 매장 데이터 포함
- [ ] MockStoreRepository.getAllStores() 정상 반환
- [ ] MapViewModel 초기화 시 stores StateFlow에 데이터 로드

---

## 5. Next
✅ 완료 후 → `task_08_markers_events.md` 진행
