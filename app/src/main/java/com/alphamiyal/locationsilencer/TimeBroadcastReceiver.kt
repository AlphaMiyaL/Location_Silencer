package com.alphamiyal.locationsilencer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import java.util.*

private const val TAG = "TimeBroadcastReceiver"

class TimeBroadcastReceiver : BroadcastReceiver(){
        //private lateinit var audioManager: AudioManager
        //private lateinit var alarmManager: AlarmManager

        override fun onReceive(context: Context, intent: Intent) {
            //if (audioManager == null){
                var audioManager = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            //}
            var type = intent.getIntExtra("Type", -1)
            if(type%2 == 0){
                audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
                setNextAlarm(context, type, intent)
                Log.d(TAG, "Phone Silenced")
            }
            else if(type%2 == 1){
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                setNextAlarm(context, type, intent)
                Log.d(TAG, "Phone Un-Silenced")
            }
        }

        private fun setNextAlarm(context:Context, type: Int, intent: Intent){
            //if (alarmManager == null){
                var alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            //}
            var calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            val pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0 or PendingIntent.FLAG_MUTABLE)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
}