package com.roadmate.shop.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.roadmate.shop.utils.PermissionUtils


class AppLocation {

    companion object{
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        var locationCallback: LocationCallback? = null

        fun init(context: Context){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    fun getAppLocation(listener: AppLocationListener, context: Context){
        getLastLocation(context, listener)
    }

    fun checkLocationSettings(activity: Activity, listener: AppLocationListener){
        validateLocationSettings(activity, listener)
    }

    private fun createLocationRequest() : LocationRequest  {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        return locationRequest!!
    }

    private fun validateLocationSettings(activity: Activity, listener: AppLocationListener){
        val builder = LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest() )
        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener { _ ->
            listener.onLocationSettingsSatisfied()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(activity, 1024)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun getLastLocation(context: Context, listener: AppLocationListener){
        if (PermissionUtils.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)){
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if (location != null){
                    listener.onLocationReceived(location)
                }else{
                    requestLocation(context, listener)
                }
            }
        }
    }

    private fun requestLocation(context: Context, listener: AppLocationListener){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    if (location != null){
                        listener.onLocationReceived(location)
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        break
                    }
                }
            }
        }

        if (PermissionUtils.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                locationCallback,
                Looper.getMainLooper())
        }
    }
}