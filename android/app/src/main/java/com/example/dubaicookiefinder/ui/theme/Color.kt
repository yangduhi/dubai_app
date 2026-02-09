package com.example.dubaicookiefinder.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Apple Style Color Palette
 * 
 * Design Reference:
 * - Background: 순백색 또는 아주 연한 회색
 * - Text: 짙은 검정으로 고대비
 * - Accent: Apple Blue 계열
 */
object AppleColors {
    // Backgrounds
    val White = Color(0xFFFFFFFF)
    val LightGray = Color(0xFFF5F5F7)
    
    // Text
    val TextPrimary = Color(0xFF1D1D1F)
    val TextSecondary = Color(0xFF86868B)
    
    // Accent
    val Blue = Color(0xFF0071E3)
    val Green = Color(0xFF34C759)
    val Red = Color(0xFFFF3B30)
    val Orange = Color(0xFFFF9500)
    
    // Stock Status Colors
    val StockHigh = Color(0xFF34C759)    // 재고 많음 (10개 이상)
    val StockMedium = Color(0xFFFF9500)  // 재고 보통 (5~9개)
    val StockLow = Color(0xFFFF3B30)     // 재고 적음 (1~4개)
    val StockEmpty = Color(0xFF86868B)   // 품절 (0개)
    
    // Glass Effect (Glassmorphism)
    val GlassBackground = Color(0x99FFFFFF)  // 60% white
    val GlassBorder = Color(0x33FFFFFF)      // 20% white
}

// Legacy support for Material Theme
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
