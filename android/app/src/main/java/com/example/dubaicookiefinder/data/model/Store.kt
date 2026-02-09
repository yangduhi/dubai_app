package com.example.dubaicookiefinder.data.model

import com.google.android.gms.maps.model.LatLng

/**
 * 매장 데이터 모델
 * 
 * 실제 편의점/베이커리 기반 구조
 */
data class Store(
    val id: String,
    val brand: StoreBrand,       // 브랜드 (CU, GS25 등)
    val branchName: String,      // 지점명 (강남대로점, 역삼역점 등)
    val latitude: Double,
    val longitude: Double,
    val stockCount: Int,         // 두바이 쿠키 재고량
    val lastUpdated: String      // 업데이트 시간 (방금 전, 10분 전 등)
) {
    /**
     * UI 표시용 전체 매장명
     * 예: "CU 강남대로점", "GS25 역삼역점"
     */
    val fullName: String
        get() = "${brand.displayName} $branchName"
    
    /**
     * LatLng 객체로 변환
     */
    val latLng: LatLng
        get() = LatLng(latitude, longitude)
    
    /**
     * 브랜드 이모지
     */
    val brandEmoji: String
        get() = brand.emoji
}
