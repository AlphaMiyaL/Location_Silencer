package com.alphamiyal.locationsilencer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import java.util.*

private const val TAG = "TimeBroadcastReceiver"

class TimeBroadcastReceiver : BroadcastReceiver(){
    //private lateinit var am: AudioManager
        override fun onReceive(context: Context, intent: Intent) {
            var am = context!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            var type = intent.getIntExtra("Type", -1)
            if(type%2 == 0){
                am.ringerMode = AudioManager.RINGER_MODE_SILENT
                setNextAlarm(context, type, intent)
                Log.d(TAG, "Phone Silenced")
            }
            else if(type%2 == 1){
                am.ringerMode = AudioManager.RINGER_MODE_NORMAL
                setNextAlarm(context, type, intent)
                Log.d(TAG, "Phone Un-Silenced")
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