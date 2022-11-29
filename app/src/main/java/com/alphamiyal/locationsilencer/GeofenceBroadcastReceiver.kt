package com.alphamiyal.locationsilencer

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.media.AudioManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import java.lang.reflect.Constructor


private const val TAG = "GeoBroadcastReceiver"

class GeofenceBroadcastReceiver: BroadcastReceiver() {



    //Called when BroadcastReceiver is receiving
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"Geofence triggered", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Received")

        var geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent)

        //var nm = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        if(geofencingEvent.hasError()){
            Log.d(TAG, "OnReceive: Error receiving geofence event")
            return
        }

        var geofenceList: List<Geofence> = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList){
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }
        var transitionType = geofencingEvent.geofenceTransition
        when(transitionType){
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Log.d(TAG, "Entered Silencing Zone")
                am.ringerMode = AudioManager.RINGER_MODE_SILENT
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Log.d(TAG, "Exited Silencing Zone")
                am.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
        }

    }
}