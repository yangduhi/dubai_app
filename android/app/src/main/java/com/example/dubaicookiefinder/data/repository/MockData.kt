package com.example.dubaicookiefinder.data.repository

import com.example.dubaicookiefinder.data.model.Store
import com.example.dubaicookiefinder.data.model.StoreBrand

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
            brand = StoreBrand.CU,
            branchName = "강남점",
            latitude = 37.4979,
            longitude = 127.0276,
            stockCount = 15,
            lastUpdated = "2026-02-09T10:30:00"
        ),
        Store(
            id = "store_002",
            brand = StoreBrand.CU,
            branchName = "역삼점",
            latitude = 37.5000,
            longitude = 127.0365,
            stockCount = 7,
            lastUpdated = "2026-02-09T09:45:00"
        ),
        Store(
            id = "store_003",
            brand = StoreBrand.CU,
            branchName = "선릉점",
            latitude = 37.5045,
            longitude = 127.0490,
            stockCount = 3,
            lastUpdated = "2026-02-09T11:00:00"
        ),
        Store(
            id = "store_004",
            brand = StoreBrand.CU,
            branchName = "삼성점",
            latitude = 37.5089,
            longitude = 127.0630,
            stockCount = 0,  // 품절
            lastUpdated = "2026-02-09T08:00:00"
        ),
        Store(
            id = "store_005",
            brand = StoreBrand.CU,
            branchName = "청담점",
            latitude = 37.5205,
            longitude = 127.0470,
            stockCount = 22,
            lastUpdated = "2026-02-09T11:30:00"
        ),
        Store(
            id = "store_006",
            brand = StoreBrand.CU,
            branchName = "압구정점",
            latitude = 37.5270,
            longitude = 127.0280,
            stockCount = 11,
            lastUpdated = "2026-02-09T10:00:00"
        ),
        Store(
            id = "store_007",
            brand = StoreBrand.CU,
            branchName = "신사점",
            latitude = 37.5165,
            longitude = 127.0200,
            stockCount = 5,
            lastUpdated = "2026-02-09T09:30:00"
        ),
        Store(
            id = "store_008",
            brand = StoreBrand.CU,
            branchName = "논현점",
            latitude = 37.5110,
            longitude = 127.0320,
            stockCount = 1,
            lastUpdated = "2026-02-09T11:15:00"
        )
    )
}
