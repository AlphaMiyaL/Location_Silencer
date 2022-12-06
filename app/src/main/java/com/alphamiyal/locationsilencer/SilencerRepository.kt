package com.alphamiyal.locationsilencer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.alphamiyal.locationsilencer.database.SilencerDatabase
import java.lang.Exception
import java.time.Month
import java.time.Year
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.Executors

//This class is a singleton: There will only ever be one instance of it in app process
//  exists as long as application stays in memory(properties are saved through lifecycle changes)
//  not solution for long term data storage(android can remove app from memory)
//  private constructor, initialize fun(for new instance), access repo fun

private const val DATABASE_NAME = "silencer-database"
private const val TAG = "Silence Repository"

//repository class encapsulates logic for accessing data from sources
//  determines how to fetch or store set of data, whether database or remote server
class SilencerRepository private constructor(context: Context){
    //  Context object: need since database accessing file system
    //  database class that you want Room to create
    //  name of database file you want Room to create
    private val database: SilencerDatabase = Room.databaseBuilder(
        context.applicationContext,
        SilencerDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val c = context
    private val silencerDao = database.silencerDao()
    private lateinit var silenceLocation:SilenceLocation
    private lateinit var silenceTime: SilenceTime
    private val executor = Executors.newSingleThreadExecutor()

    fun getSilencers(): LiveData<List<Silencer>>{
        var silencers: LiveData<List<Silencer>> = silencerDao.getSilencers()
        silencers.observeForever {
            silencers ->
            silencers?.let {
                for(silencer in it){
                    if(silencer.on){
                        deleteOldSilencer(silencer)
                        if(silencer.useLoc && silencer.useTime){
                            addTimeAndLocSilencer(silencer)
                        }
                        else if(silencer.useLoc){
                            addGeofenceSilencer(silencer)
                        }
                        else if(silencer.useTime){
                            addTimeSilencer(silencer)
                        }
                    }
                }
            }
        }
        return silencers
    }

    fun getSilencer(id: UUID): LiveData<Silencer?> = silencerDao.getSilencer(id)

    fun updateSilencer(silencer: Silencer) {
        executor.execute {
            silencerDao.updateSilencer(silencer)
        }
        if(silencer.on){
            deleteOldSilencer(silencer)
            if(silencer.useTime && silencer.useLoc){
                addTimeAndLocSilencer(silencer)
            }
            else if(silencer.useLoc){
                addGeofenceSilencer(silencer)
            }
            else if(silencer.useTime){
                addTimeSilencer(silencer)
            }
        }
    }
    fun addSilencer(silencer: Silencer) {
        executor.execute {
            silencerDao.addSilencer(silencer)
        }
        if(silencer.on){
            silenceLocation.addGeofence(
                silencer.id,
                silencer.latitude,
                silencer.longitude,
                silencer.radius)
        }
    }

    private fun changeToMeters(radius: Double, unit: String): Double {
        when(unit){
            "Meters" -> return radius
            "Kilometers" -> return radius * 1000
            "FEET" -> return radius * .305
            "Miles" -> return radius * 1600
        }
        return 0.1
    }

    private fun deleteOldSilencer(silencer: Silencer){
        Log.d(TAG, "Check Delete Time and Loc Silencer")
        var newRadius = changeToMeters(silencer.radius, silencer.unit)
        try{
            silenceLocation.removeGeofence(silencer.id)
            silenceTime.deleteTimeAndLoc(0,
                silencer.startTime.time,
                silencer.id,
                silencer.latitude,
                silencer.longitude,
                newRadius)
            silenceTime.deleteTimeAndLoc(1,
                silencer.startTime.time,
                silencer.id,
                silencer.latitude,
                silencer.longitude,
                newRadius)
        }
        catch (e: Exception){
            Log.d(TAG, "Old time-geofence silencer didn't exist or was erased.")
        }

        Log.d(TAG, "Check Delete Geofence Silencer")
        try{
            silenceLocation.removeGeofence(silencer.id)
        }
        catch (e: Exception){
            Log.d(TAG, "Old geofence silencer didn't exist or was erased.")
        }

        Log.d(TAG, "Check Delete Time Silencer")
        try{
            silenceTime.deleteTimeSilencer(0, silencer.startTime.time)
            silenceTime.deleteTimeSilencer(1, silencer.endTime.time)
        }
        catch (e: Exception){
            Log.d(TAG, "Old time silencer didn't exist or was erased.")
        }
    }

    private fun addTimeAndLocSilencer(silencer:Silencer){
        var newRadius = changeToMeters(silencer.radius, silencer.unit)
        silenceTime.addTimeAndLocSilencer(0,
            silencer.startTime.time,
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            newRadius)
        silenceTime.addTimeAndLocSilencer(1,
            silencer.startTime.time,
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            newRadius)
        Log.d(TAG, "Finished adding time-loc silencer")
    }

    private fun addGeofenceSilencer(silencer: Silencer){
        var newRadius = changeToMeters(silencer.radius, silencer.unit)
        silenceLocation.addGeofence(
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            newRadius)
        Log.d(TAG, "Finished adding fence")
    }

    private fun addTimeSilencer(silencer: Silencer){
        var am = c.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //If between time, silence
        var current = Date()
        if(current.after(silencer.startTime) && current.before(silencer.endTime)){
            am.ringerMode = AudioManager.RINGER_MODE_SILENT
        }
        //Add Time Silencer
        silenceTime.addTimeSilencer(0, changeDateToToday(silencer.startTime).time)
        silenceTime.addTimeSilencer(1, changeDateToToday(silencer.endTime).time)
        Log.d(TAG, "Finished adding time silencer")
    }

    private fun changeDateToToday(date: Date): Date{
        var calendar: Calendar = Calendar.getInstance()
        var currentCalendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(YEAR, currentCalendar.get(Calendar.YEAR))
        calendar.set(MONTH, currentCalendar.get(Calendar.MONTH))
        calendar.set(DAY_OF_MONTH, currentCalendar.get(Calendar.DAY_OF_MONTH))
        Log.d(TAG, calendar.time.time.toString())
        return calendar.time
    }

    companion object {
        private var INSTANCE: SilencerRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = SilencerRepository(context)
            }
        }

        fun get(): SilencerRepository {
            return INSTANCE ?:
            throw IllegalStateException("SilencerRepository must be initialized")
        }

        fun setSilenceLocation(){
            INSTANCE?.silenceLocation = SilenceLocation.get()
        }

        fun setSilenceTime(){
            INSTANCE?.silenceTime = SilenceTime.get()
        }
    }
}