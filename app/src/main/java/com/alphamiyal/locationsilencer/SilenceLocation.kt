package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.lang.Process
import java.util.*


private const val TAG = "SilencerLocation"

class SilenceLocation(a: Activity)/*: Service()*/{
    companion object {
        private var INSTANCE: SilenceLocation? = null

        fun initialize(a: Activity) {
            if (INSTANCE == null) {
                INSTANCE = SilenceLocation(a)
            }
        }

        fun get(): SilenceLocation {
            return INSTANCE ?:
            throw IllegalStateException("SilenceLocation must be initialized")
        }
    }

    private lateinit var geofencingClient: GeofencingClient
    private var geofenceHelper: GeofenceHelper? = null
    private var activity = a

//    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        Log.d(TAG, "onStartCommand()")
//        geofencingClient = LocationServices.getGeofencingClient(this)
//        geofenceHelper = GeofenceHelper(this)
//        // Tells the system not to recreate the service after it's been killed.
//        return START_STICKY
//    }
//
//    override fun onBind(intent: Intent): IBinder? {
//        Log.d(TAG, "onBind()")
//        return null
//    }
//
//    override fun onDestroy() {
//        Log.d(TAG, "onDestroy()")
//        super.onDestroy()
//    }

    fun initGeofencing(a: Activity){
        Log.d(TAG, "GeofenceHelper initiallized ")
        geofencingClient = LocationServices.getGeofencingClient(a)
        geofenceHelper = GeofenceHelper(a)
    }

    fun addGeofence(id: UUID, lat: Double, lng: Double, radius: Double){
//        Checking permission
        Log.d(TAG, "Adding fence 2")
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if(geofenceHelper == null) {
            initGeofencing(activity)
        }
        //Creating Geofence
        val geofence = geofenceHelper!!.getGeofence(id, lat, lng, radius)
        val geofencingRequest = geofenceHelper!!.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper!!.getPendingIntent()

        //Adding Geofence to geofenceClient
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(TAG, "Success: Geofence Added")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed Geofence adding")
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
         if(geofenceHelper == null) {
             initGeofencing(activity)
         }

        var list: List<String> = listOf(id.toString())
        geofencingClient.removeGeofences(list)
            .addOnSuccessListener { aVoid: Void? ->
                Log.e(
                    "TAG",
                    "Geocenfences removed"
                )
            }
            .addOnFailureListener { e: Exception ->
                //val errorMessage: String = geofenceHelper.getErrorString(e)
                //Log.e("TAG", "onFailure: $errorMessage")
//                    Toast.makeText(
//                        applicationContext,
//                        "onFailure: $errorMessage",
//                        Toast.LENGTH_SHORT
//                    ).show()
                e.printStackTrace()
            }
    }

//    else {
//        Log.i(TAG, "no list provided, removing ALL geofences")
//        geofencingClient.removeGeofences(geofenceHelper.getPendingIntent())
//            .addOnSuccessListener { aVoid: Void? ->
//                Log.e(
//                    "TAG",
//                    "Geocenfences removed"
//                )
//            }.addOnFailureListener { e: Exception? ->
//                val errorMessage: String = geofenceHelper.getErrorString(e)
//                Log.e("TAG", "onFailure: $errorMessage")
//                Toast.makeText(
//                    applicationContext,
//                    "onFailure: $errorMessage",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }


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