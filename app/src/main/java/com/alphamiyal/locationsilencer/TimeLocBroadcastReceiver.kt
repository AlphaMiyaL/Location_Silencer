package com.alphamiyal.locationsilencer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import java.util.*

private const val TAG = "TimeLocBroadcastReceiver"

class TimeLocBroadcastReceiver: BroadcastReceiver(){
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val type = intent.getIntExtra("Type", -1)
        val id = intent.getStringExtra("id")
        val lat = intent.getStringExtra("lat")?.toDouble()
        val long = intent.getStringExtra("lng")?.toDouble()
        val radius = intent.getStringExtra("radius")?.toDouble()

        if(type%2 == 0){
            Log.d(TAG, "Starting Time")
            var geoIntent = Intent(context,GeofenceForegroundService()::class.java)
            geoIntent.putExtra("id", id)
            geoIntent.putExtra("lat", lat)
            geoIntent.putExtra("long", long)
            geoIntent.putExtra("radius", radius)
            context.startService(geoIntent)
            setNextAlarm(context, type, intent)
        }
        else if(type%2 == 1){
            Log.d(TAG, "Ending Time")
            SilenceLocation.get().removeGeofence(UUID.fromString(id))
            setNextAlarm(context, type, intent)
        }
    }

    private fun setNextAlarm(context:Context, type: Int, intent: Intent){
        var alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0)
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}