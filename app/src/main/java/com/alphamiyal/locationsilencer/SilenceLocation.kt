package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location

import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

private const val TAG = "SilencerLocation"

class SilenceLocation: Service(){
//class SilenceLocation: Service(){
//    private var configurationChange = false
//    private var serviceRunningInForeground = false
//    private val localBinder = LocalBinder()
//    private lateinit var locationRequest: LocationRequest
//    private lateinit var locationCallback: LocationCallback
//    private lateinit var silencerRepository: SilencerRepository
//    private lateinit var silencerListFragment: Silence

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val silencerRepository = SilencerRepository.get()
    private val silencerListLiveData = silencerRepository.getSilencers()

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    override fun onCreate() {
        super.onCreate()

        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    fun addGeofence(id: UUID, lat: Double, lng: Double, radius: Double){
        val geofence = geofenceHelper.getGeofence(id, lat, lng, radius)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Success: Geofence Added")
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun removeGeofence(){
        //TODO()
    }

//    override fun onCreate()
//    {
//        Log.d(TAG, "Created")
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//        locationRequest = LocationRequest.create().apply {
//            interval = TimeUnit.SECONDS.toMillis(60)
//            fastestInterval = TimeUnit.SECONDS.toMillis(30)
//            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                super.onLocationResult(locationResult)
//                currentLocation = locationResult.lastLocation
//            }
//        }
//
//    }
//    override fun onCreate(savedInstanceState: Bundle?){
//        super.onCreate(savedInstanceState)
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//    }this

//    fun getCurrentLocation(context: Context){
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ){
//            return
//        }
//        fusedLocationProviderClient.lastLocation
//            .addOnSuccessListener { location : Location? ->
//                // Got last known location. In some rare situations this can be null.
//                if(location != null){
//                    val textLatitude = "Latitude " + location.latitude.toString()
//                    val textLongitude = "longitude " + location.longitude.toString()
//                    Log.d(TAG, textLatitude)
//                    Log.d(TAG, textLongitude)
//                }
//            }
//    }

//    /**
//     * Class used for the client Binder.  Since this service runs in the same process as its
//     * clients, we don't need to deal with IPC.
//     */
//    inner class LocalBinder : Binder() {
//        internal val service: SilenceLocation
//            get() = this@SilenceLocation
//    }
//
//    override fun onBind(p0: Intent?): IBinder? {
//        Log.d(TAG, "onBind()")
//
//        return localBinder
//    }

}