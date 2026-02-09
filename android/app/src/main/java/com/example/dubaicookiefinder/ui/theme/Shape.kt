package com.example.dubaicookiefinder.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Apple Style Shapes
 * 
 * Design Reference:
 * - 모든 카드와 버튼의 모서리 반경 20dp ~ 24dp
 * - 둥글고 부드러운 느낌
 */
val AppleShapes = Shapes(
    // Card, BottomSheet, Dialog
    extraLarge = RoundedCornerShape(24.dp),
    
    // Button, InfoWindow, Large Card
    large = RoundedCornerShape(20.dp),
    
    // Chip, Tag, Medium Card
    medium = RoundedCornerShape(16.dp),
    
    // Small Button, Badge
    small = RoundedCornerShape(12.dp),
    
    // Very small elements, Indicator
    extraSmall = RoundedCornerShape(8.dp)
)
