package com.alphamiyal.locationsilencer

import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class SilencerDetailViewModel:ViewModel() {
    private val silencerRepository = SilencerRepository.get()
    private val silencerIdLiveData = MutableLiveData<UUID>() //stores id of crime currently displayed

    var silencerLiveData: LiveData<Silencer?> =
        Transformations.switchMap(silencerIdLiveData){ silencerId ->
            silencerRepository.getSilencer(silencerId)
        }

    fun loadSilencer(silencerId:UUID){
        silencerIdLiveData.value = silencerId
    }

    @RequiresApi(value = 24)
    fun saveSilencer(silencer: Silencer){
        silencerRepository.updateSilencer(silencer)
    }
}