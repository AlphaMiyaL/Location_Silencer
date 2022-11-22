package com.alphamiyal.locationsilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

private const val TAG = "GeoBroadcastReceive"

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    //Called when BroadcastReceiver is receiving
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"Geofence triggered", Toast.LENGTH_SHORT).show()

        var geofencingEvent: GeofencingEvent = GeofencingEvent.fromIntent(intent)
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
                //TODO SILENCE HERE
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                //TODO UNSILENCE HERE
            }
        }

    }
}