package com.example.dubaicookiefinder.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * 외부 앱 연동 유틸리티
 * 
 * 네이버 지도/카카오맵 등 외부 지도 앱 실행
 */
object ExternalAppUtils {
    
    private const val NAVER_MAP_PACKAGE = "com.nhn.android.nmap"
    private const val KAKAO_MAP_PACKAGE = "net.daum.android.map"
    private const val APP_NAME = "com.example.dubaicookiefinder"
    
    /**
     * 네이버 지도로 매장 검색
     * 
     * @param context Android Context
     * @param query 검색어 (예: "CU 강남대로점")
     */
    fun openNaverMap(context: Context, query: String) {
        // 네이버 지도 URL Scheme
        val url = "nmap://search?query=${Uri.encode(query)}&appname=$APP_NAME"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 네이버 지도 미설치 시 Play Store로 이동
            openPlayStore(context, NAVER_MAP_PACKAGE)
        } catch (e: Exception) {
            // 기타 예외 시 Play Store로 이동
            openPlayStore(context, NAVER_MAP_PACKAGE)
        }
    }
    
    /**
     * 네이버 지도로 길찾기
     * 
     * @param context Android Context
     * @param lat 목적지 위도
     * @param lng 목적지 경도
     * @param name 목적지 이름
     */
    fun openNaverMapRoute(context: Context, lat: Double, lng: Double, name: String) {
        // 네이버 지도 길찾기 URL Scheme
        val url = "nmap://route/walk?dlat=$lat&dlng=$lng&dname=${Uri.encode(name)}&appname=$APP_NAME"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            openPlayStore(context, NAVER_MAP_PACKAGE)
        } catch (e: Exception) {
            openPlayStore(context, NAVER_MAP_PACKAGE)
        }
    }
    
    /**
     * 카카오맵으로 매장 검색 (대안)
     */
    fun openKakaoMap(context: Context, query: String) {
        val url = "kakaomap://search?q=${Uri.encode(query)}"
        
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            openPlayStore(context, KAKAO_MAP_PACKAGE)
        } catch (e: Exception) {
            openPlayStore(context, KAKAO_MAP_PACKAGE)
        }
    }
    
    /**
     * Google Maps 웹 (Fallback)
     */
    fun openGoogleMapsWeb(context: Context, query: String) {
        val url = "https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
    
    /**
     * Play Store 페이지 열기
     */
    private fun openPlayStore(context: Context, packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Play Store 앱도 없으면 웹으로
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            )
            context.startActivity(intent)
        }
    }
}
