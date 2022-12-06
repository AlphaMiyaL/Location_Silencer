package com.alphamiyal.locationsilencer

import androidx.lifecycle.ViewModel

class SilencerListViewModel: ViewModel() {
    private val silencerRepository = SilencerRepository.get()
    val silencerListLiveData = silencerRepository.getSilencers()

    fun addSilencer(silencer: Silencer){
        silencerRepository.addSilencer(silencer)
    }

    fun saveSilencer(silencer: Silencer){
        silencerRepository.updateSilencer(silencer)
    }
}