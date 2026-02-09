package com.example.dubaicookiefinder.data.model

/**
 * 매장 브랜드 enum
 * 
 * 편의점, 베이커리, 카페 브랜드
 */
enum class StoreBrand(
    val displayName: String,
    val emoji: String
) {
    // 편의점
    CU("CU", "🏪"),
    GS25("GS25", "🏬"),
    SEVEN_ELEVEN("세븐일레븐", "7️⃣"),
    EMART24("이마트24", "🛒"),
    
    // 베이커리
    PARIS_BAGUETTE("파리바게뜨", "🥐"),
    TOUS_LES_JOURS("뚜레쥬르", "🥖"),
    BAKERY("동네 빵집", "🍞"),
    
    // 카페
    STARBUCKS("스타벅스", "☕"),
    PAUL_BASSETT("폴바셋", "☕"),
    TWOSOME("투썸플레이스", "🍰"),
    EDIYA("이디야", "☕"),
    MEGA_COFFEE("메가커피", "🥤"),
    COMPOSE("컴포즈커피", "☕"),
    PAIKS("빽다방", "🧋"),
    HOLLYS("할리스", "☕"),
    ANGELINUS("엔제리너스", "☕"),
    CAFE("카페", "☕"),
    
    // 기타
    OTHER("기타", "🏠")
}
