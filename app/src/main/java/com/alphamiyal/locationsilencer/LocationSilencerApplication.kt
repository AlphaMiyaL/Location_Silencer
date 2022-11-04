package com.alphamiyal.locationsilencer

import android.app.Application

class LocationSilencerApplication: Application() {
    //similar to Activity onCreate, called when app is first loaded into memory
    //  good for one-time initializations
    // However only created when app is launched and destroyed when app process is destroyed
    override fun onCreate(){
        super.onCreate()
        SilencerRepository.initialize(this)
    }
}