package com.alphamiyal.locationsilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import java.util.*

private const val TAG = "TimeLocBroadcastReceiver"

class TimeLocBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val id = intent.getStringExtra("id")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("lng", 0.0)
        val radius = intent.getDoubleExtra("radius", 1.0)

        if(intent.getIntExtra("Type", 2) == 0){
            am.ringerMode = AudioManager.RINGER_MODE_SILENT
            SilenceLocation.get().addGeofence(UUID.fromString(id), lat, long, radius)
        }
        else if(intent.getIntExtra("Type", 2) == 1){
            am.ringerMode = AudioManager.RINGER_MODE_NORMAL
            SilenceLocation.get().removeGeofence(UUID.fromString(id))
        }
    }
}