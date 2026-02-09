package com.example.dubaicookiefinder.data.repository

import android.content.Context
import android.util.Log
import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.model.StoreBrand
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.random.Random

/**
 * Google Places API Repository
 * 
 * 실제 주변 편의점/베이커리/카페 데이터를 가져옴
 * 재고 수량은 Mock 데이터로 생성
 */
class PlacesRepository(context: Context) {
    
    private val placesClient: PlacesClient = Places.createClient(context)
    private val random = Random(System.currentTimeMillis())
    
    companion object {
        private const val TAG = "PlacesRepository"
        private const val SEARCH_RADIUS_METERS = 1000.0 // 1km
        
        // 검색할 장소 타입
        private val PLACE_TYPES = listOf(
            "convenience_store",
            "bakery",
            "cafe"
        )
        
        // 가져올 필드
        private val PLACE_FIELDS = listOf(
            Place.Field.ID,
            Place.Field.DISPLAY_NAME,
            Place.Field.FORMATTED_ADDRESS,
            Place.Field.LOCATION,
            Place.Field.TYPES
        )
    }
    
    /**
     * 주변 매장 검색 (suspend 함수)
     */
    suspend fun searchNearbyStores(location: LatLng): List<Store> {
        return suspendCancellableCoroutine { continuation ->
            try {
                val circularBounds = CircularBounds.newInstance(
                    com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude),
                    SEARCH_RADIUS_METERS
                )
                
                val request = SearchNearbyRequest.builder(circularBounds, PLACE_FIELDS)
                    .setIncludedTypes(PLACE_TYPES)
                    .setMaxResultCount(20)
                    .build()
                
                placesClient.searchNearby(request)
                    .addOnSuccessListener { response ->
                        val stores = response.places.mapIndexed { index, place ->
                            placeToStore(place, index, location)
                        }
                        Log.d(TAG, "Found ${stores.size} places nearby")
                        continuation.resume(stores)
                    }
                    .addOnFailureListener { exception ->
                        Log.e(TAG, "Nearby search failed", exception)
                        // 실패 시 빈 리스트 반환
                        continuation.resume(emptyList())
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Search error", e)
                continuation.resume(emptyList())
            }
        }
    }
    
    /**
     * Place → Store 변환
     */
    private fun placeToStore(place: Place, index: Int, userLocation: LatLng): Store {
        val placeLocation = place.location
        val lat = placeLocation?.latitude ?: userLocation.latitude
        val lng = placeLocation?.longitude ?: userLocation.longitude
        
        // 브랜드 추출
        val (brand, branchName) = extractBrandAndBranch(place.displayName ?: "Unknown")
        
        // 재고 Mock 생성 (40% 품절)
        val stockCount = when {
            random.nextDouble() < 0.40 -> 0
            random.nextDouble() < 0.70 -> random.nextInt(1, 6)
            else -> random.nextInt(6, 16)
        }
        
        // 업데이트 시간 Mock
        val updateTimes = listOf("방금 전", "5분 전", "10분 전", "30분 전", "1시간 전")
        
        return Store(
            id = place.id ?: "place_${index}",
            brand = brand,
            branchName = branchName,
            latitude = lat,
            longitude = lng,
            stockCount = stockCount,
            lastUpdated = updateTimes[random.nextInt(updateTimes.size)]
        )
    }
    
    /**
     * 매장명에서 브랜드와 지점명 추출
     */
    private fun extractBrandAndBranch(displayName: String): Pair<StoreBrand, String> {
        val name = displayName.uppercase()
        
        return when {
            // 편의점
            name.contains("CU") || name.contains("씨유") -> 
                StoreBrand.CU to displayName.replace(Regex("(?i)CU|씨유"), "").trim()
            
            name.contains("GS25") || name.contains("지에스") -> 
                StoreBrand.GS25 to displayName.replace(Regex("(?i)GS25|GS 25|지에스"), "").trim()
            
            name.contains("세븐일레븐") || name.contains("7-ELEVEN") || name.contains("SEVEN") -> 
                StoreBrand.SEVEN_ELEVEN to displayName.replace(Regex("(?i)세븐일레븐|7-ELEVEN|SEVEN.?ELEVEN"), "").trim()
            
            name.contains("이마트24") || name.contains("EMART") -> 
                StoreBrand.EMART24 to displayName.replace(Regex("(?i)이마트24|EMART.?24"), "").trim()
            
            // 베이커리
            name.contains("파리바게뜨") || name.contains("PARIS") -> 
                StoreBrand.PARIS_BAGUETTE to displayName.replace(Regex("(?i)파리바게뜨|PARIS.?BAGUETTE"), "").trim()
            
            name.contains("뚜레쥬르") || name.contains("TOUS") -> 
                StoreBrand.TOUS_LES_JOURS to displayName.replace(Regex("(?i)뚜레쥬르|TOUS.?LES.?JOURS"), "").trim()
            
            // 카페 - 주요 프랜차이즈
            name.contains("스타벅스") || name.contains("STARBUCKS") -> 
                StoreBrand.STARBUCKS to displayName.replace(Regex("(?i)스타벅스|STARBUCKS"), "").trim()
            
            name.contains("폴바셋") || name.contains("PAUL") && name.contains("BASSETT") -> 
                StoreBrand.PAUL_BASSETT to displayName.replace(Regex("(?i)폴바셋|PAUL.?BASSETT"), "").trim()
            
            name.contains("투썸") || name.contains("TWOSOME") || name.contains("TWO SOME") -> 
                StoreBrand.TWOSOME to displayName.replace(Regex("(?i)투썸플레이스|투썸|TWOSOME|TWO.?SOME"), "").trim()
            
            name.contains("이디야") || name.contains("EDIYA") -> 
                StoreBrand.EDIYA to displayName.replace(Regex("(?i)이디야|EDIYA"), "").trim()
            
            name.contains("메가") || name.contains("MEGA") -> 
                StoreBrand.MEGA_COFFEE to displayName.replace(Regex("(?i)메가커피|메가MGC커피|MEGA"), "").trim()
            
            name.contains("컴포즈") || name.contains("COMPOSE") -> 
                StoreBrand.COMPOSE to displayName.replace(Regex("(?i)컴포즈커피|컴포즈|COMPOSE"), "").trim()
            
            name.contains("빽다방") || name.contains("PAIK") -> 
                StoreBrand.PAIKS to displayName.replace(Regex("(?i)빽다방|PAIK"), "").trim()
            
            name.contains("할리스") || name.contains("HOLLYS") -> 
                StoreBrand.HOLLYS to displayName.replace(Regex("(?i)할리스|HOLLYS"), "").trim()
            
            name.contains("엔제리너스") || name.contains("ANGEL") -> 
                StoreBrand.ANGELINUS to displayName.replace(Regex("(?i)엔제리너스|ANGELINUS|ANGEL.?IN.?US"), "").trim()
            
            // 일반 카페/베이커리 키워드
            name.contains("카페") || name.contains("CAFE") || name.contains("커피") || name.contains("COFFEE") -> 
                StoreBrand.CAFE to displayName
            
            name.contains("베이커리") || name.contains("BAKERY") || name.contains("빵집") || name.contains("제과") -> 
                StoreBrand.BAKERY to displayName
            
            else -> StoreBrand.OTHER to displayName
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
