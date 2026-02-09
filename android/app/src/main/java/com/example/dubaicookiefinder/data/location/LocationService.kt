package com.example.dubaicookiefinder.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * 위치 서비스 헬퍼
 * 
 * FusedLocationProviderClient를 사용하여 실제 GPS 위치를 가져옴
 */
class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L // 10초마다 업데이트
    ).apply {
        setMinUpdateIntervalMillis(5000L) // 최소 5초 간격
        setWaitForAccurateLocation(false)
    }.build()
    
    /**
     * 권한 체크
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * 위치 업데이트 Flow
     */
    fun getLocationUpdates(): Flow<LatLng> = callbackFlow {
        if (!hasLocationPermission()) {
            // 권한 없으면 기본 위치 (강남역) 반환
            trySend(LatLng(37.4979, 127.0276))
            return@callbackFlow
        }
        
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(LatLng(location.latitude, location.longitude))
                }
            }
        }
        
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // 권한 없으면 기본 위치 반환
            trySend(LatLng(37.4979, 127.0276))
        }
        
        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
    
    /**
     * 마지막 알려진 위치 가져오기
     */
    fun getLastLocation(onResult: (LatLng) -> Unit) {
        if (!hasLocationPermission()) {
            onResult(LatLng(37.4979, 127.0276))
            return
        }
        
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onResult(LatLng(location.latitude, location.longitude))
                } else {
                    // 위치 없으면 기본값
                    onResult(LatLng(37.4979, 127.0276))
                }
            }.addOnFailureListener {
                onResult(LatLng(37.4979, 127.0276))
            }
        } catch (e: SecurityException) {
            onResult(LatLng(37.4979, 127.0276))
        }
    }
}
