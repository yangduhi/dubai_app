package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.model.StoreBrand
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

/**
 * Mock 매장 데이터 생성기 (Legacy - MockStoreRepository 사용 권장)
 * 
 * 사용자의 현재 위치를 기준으로 주변에 가상 매장을 동적으로 생성
 */
object MockDataGenerator {
    
    private val branchNames = listOf(
        "강남역점", "역삼역점", "선릉역점", "삼성역점", "청담점",
        "압구정점", "신사점", "논현점", "학동역점", "강남대로점"
    )
    
    /**
     * 현재 위치 기준 주변 매장 생성
     */
    fun generateStoresNearby(
        currentLocation: LatLng,
        count: Int = 8,
        radiusKm: Double = 2.0
    ): List<Store> {
        val random = Random(System.currentTimeMillis())
        val brands = StoreBrand.values()
        
        return (1..count).map { index ->
            val angle = random.nextDouble() * 2 * Math.PI
            val distance = random.nextDouble() * radiusKm / 111.0
            
            val lat = currentLocation.latitude + distance * Math.cos(angle)
            val lng = currentLocation.longitude + distance * Math.sin(angle) / 
                      Math.cos(Math.toRadians(currentLocation.latitude))
            
            val stockCount = when {
                random.nextDouble() < 0.40 -> 0  // 40% 품절
                random.nextDouble() < 0.70 -> random.nextInt(1, 6)
                else -> random.nextInt(6, 16)
            }
            
            Store(
                id = "store_${String.format("%03d", index)}",
                brand = brands[random.nextInt(brands.size)],
                branchName = branchNames[index % branchNames.size],
                latitude = lat,
                longitude = lng,
                stockCount = stockCount,
                lastUpdated = "방금 전"
            )
        }
    }
}
