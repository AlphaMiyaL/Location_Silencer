package com.alphamiyal.locationsilencer

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.*

private const val TAG = "GeocodeWorker"

class GeocoderWorker(appContext: Context,workerParameters: WorkerParameters): Worker(appContext, workerParameters) {
    override fun doWork(): Result {
        Log.d(TAG, "Worker started")
        val context = applicationContext
        val inputAddress = inputData.getString("address_string")

        val gcd = Geocoder(context, Locale.getDefault())
        var add: List<Address>? = null

        try {
            add = gcd.getFromLocationName(inputAddress, 1)
        }catch (e: Exception){
            e.printStackTrace()
        }

        var fullAddress = ""
        var latitude = 0.0
        var longitude = 0.0
        var thoroughfare = ""
        var subThoroughfare = ""
        var locality = ""
        var adminArea = ""
        var postalCode = ""

        if (add != null){
            if (add.isNotEmpty()){
                val address = add[0]
                if (address.thoroughfare != null){
                    thoroughfare = address.thoroughfare
                }
                if (address.subThoroughfare != null){
                    subThoroughfare = address.subThoroughfare
                }
                if (address.locality != null){
                    locality = address.locality
                }
                if (address.adminArea != null){
                    adminArea = address.adminArea
                }
                if (address.postalCode != null){
                    postalCode = address.postalCode
                }
                fullAddress = address.getAddressLine(0)
                latitude = address.latitude
                longitude = address.longitude
            }
        }

        var outputData = Data.Builder()
            .putString("address", fullAddress)
            .putString("thoroughfare", thoroughfare)
            .putString("subThoroughfare", subThoroughfare)
            .putString("locality", locality)
            .putString("adminArea", adminArea)
            .putString("postalCode", postalCode)
            .putDouble("latitude", latitude)
            .putDouble("longitude", longitude)
            .build()

        return Result.success(outputData)
    }
}