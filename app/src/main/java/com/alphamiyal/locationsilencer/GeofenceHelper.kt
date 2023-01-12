package com.alphamiyal.locationsilencer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import java.util.*

private const val TAG = "GeofenceHelper"

class GeofenceHelper(base: Context?) : ContextWrapper(base) {
    private var pendingIntent: PendingIntent? = null

    fun getGeofence(id: UUID, lat: Double, lng: Double, radius: Double): Geofence{
        return Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(id.toString())
            // Set the circular region of this geofence
            .setCircularRegion(
                lat,
                lng,
                radius.toFloat() //IN METERS
            )
            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(1)
            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry dwell and exit
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            // Create the geofence.
            .build()
    }

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .addGeofence(geofence)
//            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .build()
    }


    fun getPendingIntent(): PendingIntent {
        if (pendingIntent != null) {
            return pendingIntent!!
        }
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
//            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else{
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }


    }
}