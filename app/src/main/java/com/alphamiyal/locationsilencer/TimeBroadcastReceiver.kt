package com.alphamiyal.locationsilencer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

private const val TAG = "TimeBroadcastReceiver"

class TimeBroadcastReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if(intent.getIntExtra("Type", 2) == 0){
                am.ringerMode = AudioManager.RINGER_MODE_SILENT
                Log.d(TAG, "Phone Silenced")
            }
            else if(intent.getIntExtra("Type", 2) == 1){
                am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                Log.d(TAG, "Phone not silenced")
            }
        }
}