package com.alphamiyal.locationsilencer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel

class SilencerListViewModel: ViewModel() {
    private val silencerRepository = SilencerRepository.get()
    val silencerListLiveData = silencerRepository.getSilencers()

    fun addSilencer(silencer: Silencer){
        silencerRepository.addSilencer(silencer)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun saveSilencer(silencer: Silencer){
        silencerRepository.updateSilencer(silencer)
    }
}