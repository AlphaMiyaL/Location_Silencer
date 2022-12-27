package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*


private const val TAG = "SilencerLocation"

class SilenceLocation(a: Activity){
    companion object {
        private var INSTANCE: SilenceLocation? = null

        fun initialize(a: Activity) {
            if (INSTANCE == null) {
                INSTANCE = SilenceLocation(a)
                INSTANCE!!.initGeofencing(a)
            }
        }

        fun get(): SilenceLocation {
            return INSTANCE ?:
            throw IllegalStateException("SilenceLocation must be initialized")
        }
    }

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper
    private var activity = a
    private var testUUID = UUID.randomUUID()
    private var geofenceSuccess = true

    private fun initGeofencing(a: Activity){
        Log.d(TAG, "GeofenceHelper initialized ")
        geofencingClient = LocationServices.getGeofencingClient(a)
        geofenceHelper = GeofenceHelper(a)
    }

    fun addGeofence(id: UUID, lat: Double, lng: Double, radius: Double){
//        Checking permission
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }




        //Creating Geofence
        val geofence = geofenceHelper.getGeofence(id, lat, lng, radius)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()

        //Adding Geofence to geofenceClient
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Success: Geofence Added")
                geofenceSuccess = true
            }
            .addOnFailureListener {
                geofenceSuccess = false
                Log.d(TAG, "Failed: Geofence not Added")
                Log.d(TAG, it.toString())
            }
    }

     fun removeGeofence(id: UUID) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        var list: List<String> = listOf(id.toString())
        geofencingClient.removeGeofences(list)
            .addOnSuccessListener {
                Log.d(TAG, "Geofence Removed")
            }
            .addOnFailureListener { e: Exception ->
                e.printStackTrace()
            }
    }

    fun testGeofencing(): Boolean{
        Log.d(TAG, "TESTING GEOFENCE")
        addGeofence(testUUID, 0.0, 0.0, 1.0)
        if(geofenceSuccess){
            removeGeofence(testUUID)
            return true
        }
        return false
    }

}