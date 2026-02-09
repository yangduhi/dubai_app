package com.example.dubaicookiefinder.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Apple Style Typography
 * 
 * Design Reference:
 * - Large Title: 굵고 크게 (SF Pro Display 느낌)
 * - Body: 가독성 높은 17sp
 * - 시스템 폰트 사용 (San Francisco 대체)
 */
val AppleTypography = Typography(
    // Display Large - Large Title
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 41.sp,
        letterSpacing = 0.25.sp
    ),
    
    // Display Medium
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    
    // Display Small
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    
    // Headline Large - Title 1
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    
    // Headline Medium - Title 2
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    
    // Headline Small - Title 3
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    
    // Title Large
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    ),
    
    // Title Medium
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    
    // Title Small
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    
    // Body Large - Body
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    
    // Body Medium - Callout
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 21.sp
    ),
    
    // Body Small - Subhead
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    
    // Label Large - Caption 1
    labelLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    
    // Label Medium
    labelMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 14.sp
    ),
    
    // Label Small - Caption 2
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 13.sp
    )
)

// Alias for backward compatibility
val Typography = AppleTypography
