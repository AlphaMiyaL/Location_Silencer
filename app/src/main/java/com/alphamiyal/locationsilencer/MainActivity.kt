package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

private const val TAG = "MainActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
class MainActivity : AppCompatActivity(), SilencerListFragment.Callbacks  {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCode = 2000
    private val locationCode1 = 2001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationCode)
            }
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),locationCode)
            }
        }


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
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if(requestCode==locationCode){
//            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                if(ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )!=PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(
//                        this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    return
//                }
//            }
//        }
//        if(requestCode==locationCode1){
//            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                if(ActivityCompat.checkSelfPermission(
//                        this, Manifest.permission.ACCESS_BACKGROUND_LOCATION
//                    ) !=PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                ){
//                    return
//                }
//                Toast.makeText(this@MainActivity, "You Can Add Geofences", Toast.LENGTH_LONG).show()
//            }
//        }
//    }




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