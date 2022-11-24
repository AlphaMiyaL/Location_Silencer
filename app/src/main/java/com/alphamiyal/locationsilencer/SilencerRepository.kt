package com.alphamiyal.locationsilencer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.alphamiyal.locationsilencer.database.SilencerDatabase
import java.util.*
import java.util.concurrent.Executors

//This class is a singleton: There will only ever be one instance of it in app process
//  exists as long as application stays in memory(properties are saved through lifecycle changes)
//  not solution for long term data storage(android can remove app from memory)
//  private constructor, initialize fun(for new instance), access repo fun

private const val DATABASE_NAME = "silencer-database"

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

    private val silencerDao = database.silencerDao()
    private lateinit var silenceLocation:SilenceLocation
    private val executor = Executors.newSingleThreadExecutor()

    fun getSilencers(): LiveData<List<Silencer>>{
        var silencers: LiveData<List<Silencer>> = silencerDao.getSilencers()
        silencers.observeForever {
                silencers ->
                silencers?.let {
                    for(silencer in it){
                        silenceLocation.addGeofence(
                            silencer.id,
                            silencer.latitude,
                            silencer.longitude,
                            silencer.radius)
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
        silenceLocation.removeGeofence(silencer.id)
        silenceLocation.addGeofence(
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            silencer.radius)
    }
    fun addSilencer(silencer: Silencer) {
        executor.execute {
            silencerDao.addSilencer(silencer)
        }
        silenceLocation.addGeofence(
            silencer.id,
            silencer.latitude,
            silencer.longitude,
            silencer.radius)
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
    }
}