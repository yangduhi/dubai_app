package com.example.dubaicookiefinder.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dubaicookiefinder.ui.theme.AppleColors
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * 커스텀 원형 마커 생성기
 * 
 * Apple 스타일의 깔끔한 원형 마커
 */
object CustomMarkerFactory {
    
    private const val MARKER_SIZE = 80
    private const val STROKE_WIDTH = 6f
    private const val TEXT_SIZE = 28f
    
    /**
     * 재고 상태에 따른 원형 마커 생성
     */
    fun createMarker(stockCount: Int): BitmapDescriptor {
        val bitmap = Bitmap.createBitmap(MARKER_SIZE, MARKER_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val (backgroundColor, textColor) = getColorsForStock(stockCount)
        
        // 외곽선 (그림자 효과)
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.parseColor("#40000000")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(
            MARKER_SIZE / 2f,
            MARKER_SIZE / 2f + 2,
            MARKER_SIZE / 2f - 4,
            shadowPaint
        )
        
        // 배경 원
        val backgroundPaint = Paint().apply {
            isAntiAlias = true
            color = backgroundColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(
            MARKER_SIZE / 2f,
            MARKER_SIZE / 2f,
            MARKER_SIZE / 2f - 6,
            backgroundPaint
        )
        
        // 흰색 테두리
        val strokePaint = Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = STROKE_WIDTH
        }
        canvas.drawCircle(
            MARKER_SIZE / 2f,
            MARKER_SIZE / 2f,
            MARKER_SIZE / 2f - 6,
            strokePaint
        )
        
        // 텍스트 (재고 수량 또는 X)
        val textPaint = Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = TEXT_SIZE
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        
        val text = if (stockCount > 0) "$stockCount" else "X"
        val textY = MARKER_SIZE / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, MARKER_SIZE / 2f, textY, textPaint)
        
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    
    /**
     * 재고 상태에 따른 색상 반환
     */
    private fun getColorsForStock(stockCount: Int): Pair<Int, Int> {
        return when {
            stockCount >= 6 -> {
                // 재고 많음 - 초록색
                android.graphics.Color.parseColor("#34C759") to android.graphics.Color.WHITE
            }
            stockCount >= 3 -> {
                // 재고 보통 - 주황색
                android.graphics.Color.parseColor("#FF9500") to android.graphics.Color.WHITE
            }
            stockCount >= 1 -> {
                // 재고 적음 - 빨간색
                android.graphics.Color.parseColor("#FF3B30") to android.graphics.Color.WHITE
            }
            else -> {
                // 품절 - 회색
                android.graphics.Color.parseColor("#8E8E93") to android.graphics.Color.WHITE
            }
        }
    }
}
