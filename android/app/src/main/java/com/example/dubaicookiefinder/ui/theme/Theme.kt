package com.example.dubaicookiefinder.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Apple Style Theme
 * 
 * Light 모드 중심 디자인 (Apple Website 스타일)
 * Dark 모드도 지원하되 Light 모드가 기본
 */
private val LightColorScheme = lightColorScheme(
    primary = AppleColors.Blue,
    onPrimary = AppleColors.White,
    primaryContainer = AppleColors.Blue.copy(alpha = 0.1f),
    onPrimaryContainer = AppleColors.Blue,
    
    secondary = AppleColors.TextSecondary,
    onSecondary = AppleColors.White,
    
    background = AppleColors.White,
    onBackground = AppleColors.TextPrimary,
    
    surface = AppleColors.LightGray,
    onSurface = AppleColors.TextPrimary,
    surfaceVariant = AppleColors.LightGray,
    onSurfaceVariant = AppleColors.TextSecondary,
    
    error = AppleColors.Red,
    onError = AppleColors.White,
    
    outline = AppleColors.TextSecondary.copy(alpha = 0.3f)
)

private val DarkColorScheme = darkColorScheme(
    primary = AppleColors.Blue,
    onPrimary = AppleColors.White,
    primaryContainer = AppleColors.Blue.copy(alpha = 0.2f),
    onPrimaryContainer = AppleColors.Blue,
    
    secondary = AppleColors.TextSecondary,
    onSecondary = AppleColors.TextPrimary,
    
    background = AppleColors.TextPrimary,
    onBackground = AppleColors.White,
    
    surface = AppleColors.TextPrimary,
    onSurface = AppleColors.White,
    surfaceVariant = AppleColors.TextPrimary,
    onSurfaceVariant = AppleColors.TextSecondary,
    
    error = AppleColors.Red,
    onError = AppleColors.White,
    
    outline = AppleColors.TextSecondary.copy(alpha = 0.5f)
)

@Composable
fun DubaiCookieFinderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppleTypography,
        shapes = AppleShapes,
        content = content
    )
}
