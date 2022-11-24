package com.alphamiyal.locationsilencer

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class LocationSilencerApplication: Application() {
    //similar to Activity onCreate, called when app is first loaded into memory
    //  good for one-time initializations
    // However only created when app is launched and destroyed when app process is destroyed
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(){
        super.onCreate()
//        val silenceLocation = SilenceLocation()
//        silenceLocation.initGeofencing()
        //startService( Intent(this, SilenceLocation::class.java))
        SilencerRepository.initialize(this)
    }
}