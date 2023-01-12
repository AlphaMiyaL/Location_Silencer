package com.alphamiyal.locationsilencer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

class GeofenceForegroundService: Service() {

    private lateinit var geofenceHelper: GeofenceHelper
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var pendingIntent: PendingIntent
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var context:Context

    private val TAG = "GeofenceForegroundServi"

    override fun onBind(p0: Intent?): IBinder? {
        return null // (._.)
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }


    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        geofenceHelper = GeofenceHelper(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("Geofence", "Geofence Loc", NotificationManager.IMPORTANCE_NONE)
            val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "Geofence")
            .setContentTitle("Geofence Active")
            .setContentText("Location Updating...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(geofenceHelper.getPendingIntent())
            .build()

        val latLng = intent?.getParcelableExtra<LatLng>("LATLNG")
        geofencingClient = LocationServices.getGeofencingClient(this)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        val geofence = geofenceHelper.getGeofence(
            UUID.fromString(intent!!.getStringExtra("id")),
            intent!!.getDoubleExtra("lat", 0.0),
            intent!!.getDoubleExtra("long", 0.0),
            intent!!.getDoubleExtra("radius", 1.0))
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        pendingIntent = geofenceHelper.getPendingIntent()
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
            }
        }
        Looper.myLooper()?.let {
            locationClient.requestLocationUpdates(locationRequest, locationCallback,
                it
            )
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "onSuccess: Geofence Added...")

            }
            addOnFailureListener {
                //Log.d(TAG, "onFailure ${geofenceHelper.getErrorString(it)}")
                Log.d(TAG, "onFailure")
            }
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // was Q
//            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
//        } else {
        //TODO 27 and below, 32 and above currently not adding geofence.

            startForeground(1, notification)
//        }

        return START_STICKY
    }


    override fun onDestroy() {
        Log.i(TAG, "onDestroy: RUN")
        geofencingClient.removeGeofences(pendingIntent)
        locationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(TAG, "onTaskRemoved: RUN")
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}