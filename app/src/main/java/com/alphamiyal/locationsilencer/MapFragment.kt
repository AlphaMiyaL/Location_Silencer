package com.alphamiyal.locationsilencer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment: Fragment(), OnMapReadyCallback {
    private lateinit var googleMap:GoogleMap

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        map_view.onCreate(savedInstanceState)
        map_view.onResume()

        map_view.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onMapReady(map: GoogleMap) {
        map?.let{
            googleMap = it
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
                googleMap.clear()
                //Animate zoom to the marker
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    latLng, 10F
                ))
                //Add marker on map
                googleMap.addMarker(markerOptions)
            }
        })
    }
}