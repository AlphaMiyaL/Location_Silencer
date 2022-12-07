package com.alphamiyal.locationsilencer

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val TAG = "GlobalID"

class GlobalID (){
    fun getID(context: Context): Int{
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val globalID = sharedPreferences.getInt("GlobalID", 0)
        val editor = sharedPreferences.edit()
        editor.apply{
            putInt("GlobalID", globalID + 2)
        }.apply()
        //Log.d(TAG, "Current ID " + globalID)
        return globalID
    }
}