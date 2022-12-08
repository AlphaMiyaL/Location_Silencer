package com.alphamiyal.locationsilencer

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
    private var existingPendingIntents = mutableMapOf<Int, Any?>()
    var idDictionary = mutableMapOf<Int, Any?>()
    var latDictionary = mutableMapOf<Int, Any?>()
    var longDictionary = mutableMapOf<Int, Any?>()
    var radiusDictionary = mutableMapOf<Int, Any?>()

    private fun initSilenceTime(a:Activity){
        alarmManager = a.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun addTimeSilencer(type: Int, calendar: Calendar){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
//        Log.d(TAG, "HEY3" + calendar.timeInMillis)
        val pendingIntent= PendingIntent.getBroadcast(context, type, intent, 0)
        //Log.d(TAG, calendar.timeInMillis.toString())
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            //AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun  deleteTimeSilencer(type: Int){
        val intent = Intent(context, TimeBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        val pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    fun addTimeAndLocSilencer(type: Int, calendar: Calendar, id: UUID, lat: Double, lng: Double, radius: Double){
        var intent = Intent(context, TimeLocBroadcastReceiver::class.java)
        intent.putExtra("Type", type)
        idDictionary[type] = id.toString()
        latDictionary[type] = lat
        longDictionary[type] = lng
        radiusDictionary[type] = radius

        Log.d(TAG, "lat: " + lat)
        Log.d(TAG, "lng: " + lng)
        Log.d(TAG, intent.getDoubleExtra("lat", 0.0).toString())
        Log.d(TAG, intent.getDoubleExtra("long", 0.0).toString())

        var pendingIntent= PendingIntent.getBroadcast(context, type, intent, 0)
        existingPendingIntents[type] = pendingIntent
        //Log.d(TAG, calendar.timeInMillis.toString())
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            //AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun  deleteTimeAndLoc(type: Int){
//        val intent = Intent(context, TimeLocBroadcastReceiver::class.java)
//        intent.putExtra("Type", type)
//        intent.putExtra("id", id.toString())
//        intent.putExtra("lat", lat.toString())
//        intent.putExtra("long", lng.toString())
//        intent.putExtra("radius", radius.toString())

        //LocalBroadcastManager.getInstance(context).unregisterReceiver(timeLocBroadcastReceiver)
        val pendingIntent = existingPendingIntents[type] as PendingIntent
        //val pendingIntent = PendingIntent.getBroadcast(context, type, intent, 0)
        alarmManager.cancel(pendingIntent)
    }
}