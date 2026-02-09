package com.example.dubaicookiefinder

import android.app.Application
import com.google.android.libraries.places.api.Places

/**
 * Application 클래스
 * 
 * Google Places SDK 초기화
 */
class DubaiCookieApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Places SDK 초기화
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }
}
