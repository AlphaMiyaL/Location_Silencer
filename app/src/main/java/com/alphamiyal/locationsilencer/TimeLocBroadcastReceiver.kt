package com.alphamiyal.locationsilencer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import java.util.*

private const val TAG = "TimeLocBroadcastReceiver"

class TimeLocBroadcastReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val type = intent.getIntExtra("Type", -1)
        val id = intent.getStringExtra("id")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("lng", 0.0)
        //intent.getStringExtra("radius", 1.0)?.let { Log.d(TAG, it) }
        val radius = intent.getDoubleExtra("radius", 1.0)

        if(type%2 == 0){
            var intent = Intent(context,GeofenceForegroundService()::class.java)
            intent.putExtra("id", id)
            intent.putExtra("lat", lat)
            intent.putExtra("long", long)
            intent.putExtra("radius", radius)
            setNextAlarm(context, type, intent)
        }
        else if(type%2 == 1){
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