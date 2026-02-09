package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.model.StoreBrand
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

/**
 * Mock 매장 Repository
 * 
 * 현실적인 편의점/베이커리 매장 데이터 생성
 */
class MockStoreRepository {
    
    companion object {
        // 서울 시청 좌표 (기본 중심)
        val SEOUL_CITY_HALL = LatLng(37.5665, 126.9780)
        
        // 지점명 풀
        private val BRANCH_NAMES_CONVENIENCE = listOf(
            "강남대로점", "역삼역점", "시청역점", "광화문점", "을지로점",
            "명동점", "종로점", "삼성역점", "선릉역점", "교대역점",
            "잠실역점", "여의도점", "홍대입구점", "신촌점", "건대입구점",
            "강남역점", "서울역점", "용산역점", "이태원점", "청담점"
        )
        
        private val BRANCH_NAMES_BAKERY = listOf(
            "강남본점", "역삼점", "시청점", "광화문점", "을지로점",
            "명동점", "종로본점", "삼성점", "선릉점", "교대점"
        )
    }
    
    private var cachedStores: List<Store>? = null
    private var lastGeneratedLocation: LatLng? = null
    
    /**
     * 현재 위치 기준 매장 목록 조회
     */
    fun getStoresNearLocation(currentLocation: LatLng): List<Store> {
        val lastLoc = lastGeneratedLocation
        
        // 위치가 500m 이상 변경되면 새로 생성
        if (lastLoc == null || calculateDistance(lastLoc, currentLocation) > 500) {
            cachedStores = generateRealisticStores(currentLocation, 20)
            lastGeneratedLocation = currentLocation
        }
        
        return cachedStores ?: emptyList()
    }
    
    /**
     * 거리순 정렬
     */
    fun getStoresSortedByDistance(currentLocation: LatLng): List<Store> {
        return getStoresNearLocation(currentLocation).sortedBy { store ->
            calculateDistance(currentLocation, store.latLng)
        }
    }
    
    /**
     * 현실적인 매장 데이터 생성
     * 
     * - 품절 확률 40%
     * - 반경 1km 내 분포
     */
    private fun generateRealisticStores(
        centerLocation: LatLng,
        count: Int
    ): List<Store> {
        val random = Random(System.currentTimeMillis())
        val usedBranches = mutableSetOf<String>()
        
        return (1..count).map { index ->
            // 브랜드 배정 (편의점 70%, 베이커리 30%)
            val brand = when {
                random.nextDouble() < 0.25 -> StoreBrand.CU
                random.nextDouble() < 0.50 -> StoreBrand.GS25
                random.nextDouble() < 0.65 -> StoreBrand.SEVEN_ELEVEN
                random.nextDouble() < 0.75 -> StoreBrand.EMART24
                random.nextDouble() < 0.85 -> StoreBrand.PARIS_BAGUETTE
                random.nextDouble() < 0.95 -> StoreBrand.TOUS_LES_JOURS
                else -> StoreBrand.BAKERY
            }
            
            // 지점명 선택 (중복 방지)
            val branchPool = if (brand == StoreBrand.BAKERY || 
                               brand == StoreBrand.PARIS_BAGUETTE || 
                               brand == StoreBrand.TOUS_LES_JOURS) {
                BRANCH_NAMES_BAKERY
            } else {
                BRANCH_NAMES_CONVENIENCE
            }
            
            var branchName: String
            do {
                branchName = branchPool[random.nextInt(branchPool.size)]
            } while ("${brand.name}_$branchName" in usedBranches && usedBranches.size < branchPool.size)
            usedBranches.add("${brand.name}_$branchName")
            
            // 위치 생성 (반경 1km 내)
            val angle = random.nextDouble() * 2 * Math.PI
            val distance = random.nextDouble() * 0.009 // 약 1km
            val lat = centerLocation.latitude + distance * Math.cos(angle)
            val lng = centerLocation.longitude + distance * Math.sin(angle) / 
                      Math.cos(Math.toRadians(centerLocation.latitude))
            
            // 재고 (품절 40%, 1~5개 30%, 6~15개 30%)
            val stockCount = when {
                random.nextDouble() < 0.40 -> 0  // 40% 품절
                random.nextDouble() < 0.70 -> random.nextInt(1, 6)  // 30% 적음
                else -> random.nextInt(6, 16) // 30% 많음
            }
            
            // 업데이트 시간
            val updateTime = listOf(
                "방금 전", "5분 전", "10분 전", "30분 전", "1시간 전"
            )[random.nextInt(5)]
            
            Store(
                id = "store_${String.format("%03d", index)}",
                brand = brand,
                branchName = branchName,
                latitude = lat,
                longitude = lng,
                stockCount = stockCount,
                lastUpdated = updateTime
            )
        }
    }
    
    /**
     * 거리 계산 (Haversine)
     */
    fun calculateDistance(from: LatLng, to: LatLng): Double {
        val earthRadius = 6371000.0
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val deltaLat = Math.toRadians(to.latitude - from.latitude)
        val deltaLng = Math.toRadians(to.longitude - from.longitude)
        
        val a = kotlin.math.sin(deltaLat / 2) * kotlin.math.sin(deltaLat / 2) +
                kotlin.math.cos(lat1) * kotlin.math.cos(lat2) *
                kotlin.math.sin(deltaLng / 2) * kotlin.math.sin(deltaLng / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
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
