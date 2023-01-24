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
        val context = applicationContext
        val inputAddress = inputData.getString("input_address")

        val gcd = Geocoder(context, Locale.getDefault())
        var add: List<Address>? = null

        try {
            add = gcd.getFromLocationName(inputAddress, 1)
        }catch (e: Exception){
            e.printStackTrace()
        }


        var outputData = Data.Builder()
            .putString("address_list", "")
            .build()

        if (add != null) {
            if (add.isNotEmpty()) {
                val address = add[0]
                Log.d(TAG, "This is the addresss" + address.toString())
                outputData = Data.Builder()
                    .putString("address_list", address.toString())
                    .build()
            }
        }
        Log.d(TAG, "This is something")

        return Result.success(outputData)
    }
}