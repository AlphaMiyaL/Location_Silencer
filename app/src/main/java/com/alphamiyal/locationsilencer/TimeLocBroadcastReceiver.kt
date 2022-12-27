package com.alphamiyal.locationsilencer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

private const val TAG = "TimeLocBroadcastReceiver"

class TimeLocBroadcastReceiver: BroadcastReceiver(){
    @SuppressLint("LongLogTag")
    override fun onReceive(context: Context, intent: Intent) {
        val silenceTime = SilenceTime.get()
        val type = intent.getIntExtra("Type", -1)
        val id = silenceTime.idDictionary[type] as String
        val lat = silenceTime.latDictionary[type] as Double
        val long =  silenceTime.longDictionary[type] as Double
        val radius = silenceTime.radiusDictionary[type] as Double

        Log.d(TAG, lat.toString())
        Log.d(TAG, long.toString())
        Log.d(TAG, radius.toString())

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