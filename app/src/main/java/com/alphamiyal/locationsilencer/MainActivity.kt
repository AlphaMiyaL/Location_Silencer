package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import java.util.*

private const val TAG = "MainActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
class MainActivity : AppCompatActivity(), SilencerListFragment.Callbacks, PermissionsFragment.Callbacks  {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCode = 2000
    private val locationCode1 = 2001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SilenceLocation.initialize(this)
        SilencerRepository.setSilenceLocation()

        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if(checkPermissions()){
            //has all required permissions
            //if fragment doesn't exist, create it
            if(currentFragment == null){
                val fragment = SilencerListFragment.newInstance()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
            }
        }
        else{
            //if doesn't have all the required permissions
            if(currentFragment == null){
                val fragment = PermissionsFragment.newInstance()
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()
            }
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

    override fun onContinueSelected(){
        if(checkPermissions()){
            val fragment = SilencerListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
        else{
            Toasty("Permissions are not all enabled.")
        }
    }

    override fun onLocSelected(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Toasty("Location Services are enabled")
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
    }

    override fun onDNDSelected() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted){
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
        else{
            Toasty("Do Not Disturb permissions are enabled")
        }
    }

    override fun onHighAccSelected() {
        //sends user to the closest spot to google location accuracy
        if(SilenceLocation.get().testGeofencing()){
            Toasty("High Accuracy Location is enabled")
        }
        else{
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermissions(): Boolean{
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return !(!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted || !SilenceLocation.get().testGeofencing()))
    }

    private fun Toasty(str: String){
        var toast = Toast.makeText(this,str, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }
}