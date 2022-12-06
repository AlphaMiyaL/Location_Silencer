package com.alphamiyal.locationsilencer

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.lang.Exception
import java.util.*

private const val TAG = "SilenceTime"

class SilenceTime(a: Activity, context: Context) {
    companion object {
        private var INSTANCE: SilenceTime? = null

        fun initialize(a: Activity, context: Context) {
            if (INSTANCE == null) {
                INSTANCE = SilenceTime(a, context)
                INSTANCE!!.initSilenceTime(a)
            }
        }

        fun get(): SilenceTime {
            return INSTANCE ?:
            throw IllegalStateException("SilenceTime must be initialized")
        }
    }

    private var activity: Activity = a
    private var context: Context = context
    private lateinit var alarmManager: AlarmManager

    private fun initSilenceTime(a:Activity){
        alarmManager = a.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun addTimeSilencer(type: Int, timeInMillis: Long){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        //TODO Changed current Time Millis here to id or something other later
        val pendingIntent = PendingIntent.getBroadcast(context, timeInMillis.toInt(), intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun  deleteTimeSilencer(type: Int, timeInMillis: Long){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        val pendingIntent = PendingIntent.getBroadcast(context, timeInMillis.toInt(), intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    fun addTimeAndLocSilencer(type: Int, timeInMillis: Long, id: UUID, lat: Double, lng: Double, radius: Double){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        intent.putExtra("id", id.toString())
        intent.putExtra("lat", lat)
        intent.putExtra("long", lng)
        intent.putExtra("radius", radius)

        val pendingIntent = PendingIntent.getBroadcast(context, timeInMillis.toInt(), intent, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun  deleteTimeAndLoc(type: Int, timeInMillis: Long, id: UUID, lat: Double, lng: Double, radius: Double){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        intent.putExtra("id", id.toString())
        intent.putExtra("lat", lat.toString())
        intent.putExtra("long", lng.toString())
        intent.putExtra("radius", radius.toString())

        val pendingIntent = PendingIntent.getBroadcast(context, timeInMillis.toInt(), intent, 0)
        alarmManager.cancel(pendingIntent)
    }
}