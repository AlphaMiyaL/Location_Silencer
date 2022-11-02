package com.alphamiyal.locationsilencer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alphamiyal.locationsilencer.Silencer

@Database(entities = [ Silencer::class ], version = 1)
@TypeConverters(SilencerTypeConverter::class)
abstract class SilencerDatabase : RoomDatabase(){
    abstract  fun silencerDao(): SilencerDao
}