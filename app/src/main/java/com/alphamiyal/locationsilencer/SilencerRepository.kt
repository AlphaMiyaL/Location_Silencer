package com.alphamiyal.locationsilencer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.alphamiyal.locationsilencer.database.SilencerDatabase
import java.lang.Exception
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.Executors

//This class is a singleton: There will only ever be one instance of it in app process
//  exists as long as application stays in memory(properties are saved through lifecycle changes)
//  not solution for long term data storage(android can remove app from memory)
//  private constructor, initialize fun(for new instance), access repo fun

private const val DATABASE_NAME = "silencer-database"
private const val TAG = "Silencer Repository"

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

    @RequiresApi(Build.VERSION_CODES.N)
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
    }

    fun deleteSilencer(silencer: Silencer){
        deleteOldSilencer(silencer)
        executor.execute{
            silencerDao.deleteSilencer(silencer)
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
        Log.d(TAG, "Deleting Old Silencer")
        try{
            silenceLocation.removeGeofence(silencer.id)
        } catch (e: Exception){
            Log.d(TAG, "Old geofence silencer didn't exist or was erased.")
        }

        try{
            silenceTime.deleteTimeSilencer(silencer.idInt)
            silenceTime.deleteTimeSilencer(silencer.idInt+1)
        } catch (e: Exception){
            Log.d(TAG, "Old time silencer didn't exist or was erased.")
        }
    }

    private fun addTimeAndLocSilencer(silencer:Silencer){
        var newRadius = changeToMeters(silencer.radius, silencer.unit)
        silenceTime.addTimeAndLocSilencer(
            silencer.idInt,
            currentDayCalendar(silencer.startTime),
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            newRadius)
        silenceTime.addTimeAndLocSilencer(
            silencer.idInt+1,
            currentDayCalendar(silencer.endTime),
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            newRadius)

        Log.d(TAG, "newlat" + silencer.latitude )
        Log.d(TAG, "newlong" + silencer.longitude )
        Log.d(TAG, "Finished adding time-loc silencer")
    }

    private fun addGeofenceSilencer(silencer: Silencer){
        var newRadius = changeToMeters(silencer.radius, silencer.unit)

        var intent = Intent(c,GeofenceForegroundService()::class.java)
        intent.putExtra("id", silencer.id.toString())
        intent.putExtra("lat", silencer.latitude)
        intent.putExtra("long", silencer.longitude)
        intent.putExtra("radius", newRadius)
        c.startService(intent)
        Log.d(TAG, "Finished adding geofence")
    }

    private fun addTimeSilencer(silencer: Silencer){
        //Add Time Silencer
        var startCalendar = currentDayCalendar(silencer.startTime)
        var endCalendar = currentDayCalendar(silencer.endTime)
        silenceTime.addTimeSilencer(silencer.idInt, startCalendar)
        silenceTime.addTimeSilencer(silencer.idInt+1, endCalendar)
        Log.d(TAG, "Finished adding time silencer")
    }

    private fun currentDayCalendar(date: Date): Calendar{
        var calendar: Calendar = getInstance()
        var currentCalendar: Calendar = getInstance()
        calendar.time = date
        currentCalendar.set(HOUR_OF_DAY, calendar.get(HOUR_OF_DAY))
        currentCalendar.set(MINUTE, calendar.get(MINUTE))
        currentCalendar.set(SECOND, 0)
        return currentCalendar
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