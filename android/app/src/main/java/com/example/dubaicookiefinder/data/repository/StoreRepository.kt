package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 매장 Repository
 */
class StoreRepository {
    
    private var cachedStores: List<Store>? = null
    private var lastGeneratedLocation: LatLng? = null
    
    /**
     * 현재 위치 기준 매장 목록 조회
     * 위치가 크게 변경되면 새로운 매장 목록 생성
     */
    fun getStoresNearLocation(currentLocation: LatLng): List<Store> {
        val lastLoc = lastGeneratedLocation
        
        // 위치가 1km 이상 변경되면 새로 생성
        if (lastLoc == null || calculateDistance(lastLoc, currentLocation) > 1000) {
            cachedStores = MockDataGenerator.generateStoresNearby(currentLocation, 10, 3.0)
            lastGeneratedLocation = currentLocation
        }
        
        return cachedStores ?: emptyList()
    }
    
    /**
     * 현재 위치 기준 거리순 정렬
     */
    fun getStoresSortedByDistance(currentLocation: LatLng): List<Store> {
        return getStoresNearLocation(currentLocation).sortedBy { store ->
            calculateDistance(currentLocation, store.latLng)
        }
    }
    
    /**
     * 두 좌표 간 거리 계산 (Haversine Formula)
     * @return 거리 (미터)
     */
    fun calculateDistance(from: LatLng, to: LatLng): Double {
        val earthRadius = 6371000.0
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val deltaLat = Math.toRadians(to.latitude - from.latitude)
        val deltaLng = Math.toRadians(to.longitude - from.longitude)
        
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(deltaLng / 2) * sin(deltaLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    /**
     * 거리 포맷팅
     */
    fun formatDistance(from: LatLng, to: LatLng): String {
        val distance = calculateDistance(from, to)
        return when {
            distance < 1000 -> "${distance.toInt()}m"
            else -> String.format("%.1fkm", distance / 1000)
        }
    }
}
