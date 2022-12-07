package com.alphamiyal.locationsilencer

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val TAG = "GlobalID"

class GlobalID (){
    fun getID(activity:Activity): Int{
        val sharedPreferences: SharedPreferences = activity!!.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val globalID = sharedPreferences.getInt("GlobalID", 0)
        val editor = sharedPreferences.edit()
        editor.apply{
            putInt("GlobalID", globalID + 1)
        }.apply()
        Log.d(TAG, "Current ID" + globalID)
        return globalID
    }
}