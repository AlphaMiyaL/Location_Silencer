package com.alphamiyal.locationsilencer

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
private const val ERROR_DIALOG_REQUEST = 9001
private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002
private const val PERMISSIONS_REQUEST_ENABLE_GPS = 9003

class MapFragment(s: Silencer): DialogFragment(), OnMapReadyCallback {
    var silencer = s
    private lateinit var mapView: MapView

    companion object{
        const val TAG = "MapFragment"

        fun newInstance(s: Silencer): MapFragment{
            return MapFragment(s)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_window, container, false)

        mapView = view.findViewById(R.id.map_view_popup)
        initGoogleMap(savedInstanceState)
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        //Set up marker location for current silencer if exists
        if(!(silencer.latitude == 0.0 && silencer.longitude == 0.0)){
            val markerOptions: MarkerOptions = MarkerOptions()
            val latLng = LatLng(silencer.latitude, silencer.longitude)
            markerOptions.position(latLng)
            map.addMarker(markerOptions)
            markerOptions.title("" + silencer.latitude + " : " + silencer.longitude)
            //Animate zoom to the marker
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    latLng, 10F
                ))
        }
        map.setOnMapClickListener(object : GoogleMap.OnMapClickListener{
            override fun onMapClick(latLng: LatLng) {
                //Initialize marker options
                val markerOptions: MarkerOptions = MarkerOptions()
                //Set position of marker
                markerOptions.position(latLng)
                //Set title of marker
                markerOptions.title("" + latLng.latitude + " : " + latLng.longitude)
                //Remove all previous markers
                map.clear()
                //Animate zoom to the marker
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        latLng, 10F
                    ))
                //Add marker on map
                map.addMarker(markerOptions)
                //Save lat and long in silencer
                silencer.latitude = latLng.latitude
                silencer.longitude = latLng.longitude
                //Using Geocoder to find address of latLng
                markerLoop@ for(i in 1..10){
                    try{
                        val gcd: Geocoder = Geocoder(context)
                        var loc = gcd.getFromLocation( latLng.latitude,  latLng.longitude, 1)
                        if(loc.isNotEmpty()){
                            silencer.address = loc[0].getAddressLine(0)
                        }
                        //updateUI()
                        break@markerLoop
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        })
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true
    }


}