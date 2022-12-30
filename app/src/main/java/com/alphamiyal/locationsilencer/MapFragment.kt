package com.alphamiyal.locationsilencer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


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

    override fun onMapReady(map: GoogleMap) {
        map.setOnMapLoadedCallback {
            if(!(silencer.latitude == 0.0 && silencer.longitude == 0.0)) {
                val latLng = LatLng(silencer.latitude, silencer.longitude)
                setMarkerLocation(map, latLng)
                if(silencer.radius!=0.0){
                    setCircleLocation(map, latLng, silencer.radius)
                }
            }
            else{
                map.isMyLocationEnabled = true
                val location = map.myLocation
                val myLocation = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15F))
            }
        }
        map.setOnMapClickListener { latLng ->
            setMarkerLocation(map, latLng)
            if(silencer.radius!=0.0){
                setCircleLocation(map, latLng, silencer.radius)
            }
            //Save lat and long in silencer
            silencer.latitude = latLng.latitude
            silencer.longitude = latLng.longitude
            //Using Geocoder to find address of latLng
            markerLoop@ for (i in 1..2) {
                try {
                    val gcd: Geocoder = Geocoder(context)
                    var loc = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    if (loc.isNotEmpty()) {
                        silencer.address = loc[0].getAddressLine(0)
                    }
                    break@markerLoop
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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

    private fun enableUserLocation(map:GoogleMap){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED){
            map.isMyLocationEnabled = true
        }
        else{
            //Ask for permission
            //...TODO
            map.isMyLocationEnabled = true
        }
    }

    private fun setMarkerLocation(map: GoogleMap, latLng: LatLng){
        //Initialize marker options
        val markerOptions: MarkerOptions = MarkerOptions()
        //Set position of marker
        markerOptions.position(latLng)
        //Set title of marker
        markerOptions.title(silencer.title + " : " +
                round(latLng.latitude) + ", " +
                round(latLng.longitude))
        //Remove all previous markers
        map.clear()
        //Animate zoom to the marker
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 10F
            )
        )
        //Add marker on map
        map.addMarker(markerOptions)
    }

    private fun setCircleLocation(map: GoogleMap, latLng: LatLng, radius: Double){
        //Initialize circle options
        val circleOptions: CircleOptions = CircleOptions()
        //update to meters
        val newRadius = changeToMeters(radius, silencer.unit)
        //Set position of marker
        circleOptions.center(latLng)
        circleOptions.radius(newRadius)
        circleOptions.strokeColor(Color.argb(255, 200, 0, 200))
        circleOptions.fillColor(Color.argb(64, 200, 0, 200))
        circleOptions.strokeWidth(4F)
        val circle:Circle = map.addCircle(circleOptions)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
            circleOptions.getCenter()!!, getZoomLevel(circle)))
    }

    private fun getZoomLevel(circle: Circle?): Float {
        var zoomLevel = 11F
        if (circle != null) {
            val radius = circle.radius + circle.radius / 2
            val scale = radius / 500
            zoomLevel = (15.5 - Math.log(scale) / Math.log(2.0)).toFloat() //.toInt()
        }
        return zoomLevel
    }

    private fun changeToMeters(radius: Double, unit: String): Double {
        when(unit){
            "Meters" -> return radius
            "Kilometers" -> return radius * 1000
            "FEET" -> return radius * .305
            "Miles" -> return radius * 1600
        }
        return 0.1
    }

    private fun round(num: Double): Double{
        return (num*1000)/1000.0
    }
}