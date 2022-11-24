package com.alphamiyal.locationsilencer

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

private const val TAG = "MainActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
class MainActivity : AppCompatActivity(), SilencerListFragment.Callbacks  {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SilenceLocation.initialize(this)
        SilencerRepository.setSilenceLocation()

        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        //fetchLocation()


        //if fragment doesn't exist, create it
        if(currentFragment == null){
            val fragment = SilencerListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onSilencerSelected(silencerId: UUID) {
        val fragment = SilencerFragment.newInstance(silencerId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }





//    private fun fetchLocation(){
//        val task = fusedLocationProviderClient.lastLocation
//
//        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
//            return
//        }
//        task.addOnSuccessListener {
//            if(it != null){
//                Log.d(TAG, "${it.latitude} ${it.longitude}")
//                Toast.makeText(applicationContext, "${it.latitude} ${it.longitude}", Toast.LENGTH_SHORT).show()
//
//            }
//        }
//
//    }
}