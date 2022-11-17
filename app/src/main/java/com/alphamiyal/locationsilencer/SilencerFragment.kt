package com.alphamiyal.locationsilencer

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*


private const val TAG = "SilencerFragment"
private const val ARG_SILENCER_ID = "silencer_id"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0
private const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
private const val ERROR_DIALOG_REQUEST = 9001
private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002
private const val PERMISSIONS_REQUEST_ENABLE_GPS = 9003

class SilencerFragment: Fragment(), TimePickerFragment.Callbacks, OnMapReadyCallback {
    private lateinit var silencer: Silencer
    private lateinit var titleField: EditText
    private lateinit var addressField: EditText
    private lateinit var radiusField: EditText
    private lateinit var latitudeField: TextView
    private lateinit var longitudeField: TextView

    private lateinit var mapView: MapView
    private val silencerDetailViewModel: SilencerDetailViewModel by lazy{
        ViewModelProvider(this)[SilencerDetailViewModel::class.java]
    }

    companion object{
        fun newInstance(silencerId: UUID): SilencerFragment{
            val args = Bundle().apply{
                putSerializable(ARG_SILENCER_ID, silencerId)
            }
            return SilencerFragment().apply {
                arguments=args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        silencer = Silencer()
        val silencerId: UUID = arguments?.getSerializable(ARG_SILENCER_ID) as UUID

        silencerDetailViewModel.loadSilencer(silencerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_silencer, container, false)

        titleField = view.findViewById(R.id.silencer_title) as EditText
        addressField = view.findViewById(R.id.silencer_address) as EditText
        radiusField = view.findViewById(R.id.silencer_radius) as EditText
        latitudeField = view.findViewById(R.id.silencer_latitude) as TextView
        longitudeField = view.findViewById(R.id.silencer_longitude) as TextView
        mapView = view.findViewById(R.id.map_view)
        initGoogleMap(savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        silencerDetailViewModel.silencerLiveData.observe(
            viewLifecycleOwner, Observer { silencer->
                silencer?.let {
                    this.silencer = silencer
                    updateUI()
                }
            })
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
                        updateUI()
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

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                silencer.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val radiusWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                //silencer.radius = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        val addressWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO list out possible addresses
                //gcd.getFromLocationName(sequence.toString(), 5)
                silencer.address = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        addressField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val loc: String = silencer.address.trim()
                if(loc == null || loc == "") {
                    Toast.makeText(context, "provide location", Toast.LENGTH_SHORT).show()
                }
                else{
                    val gcd: Geocoder = Geocoder(context, Locale.getDefault())
                    var add: List<Address>? = null
                    loop@ for(i in 1..10){
                        try {
                            add = gcd.getFromLocationName(silencer.address, 1)
                        }catch (e: Exception){
                            e.printStackTrace()
                        }

                        if(add != null){
                            silencer.address = add!![0].getAddressLine(0)
                            val latLng = LatLng(add!![0].latitude, add!![0].longitude)
                            silencer.latitude = latLng.latitude
                            silencer.longitude = latLng.longitude
//                            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
//                            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                            updateUI()
                            break@loop
                        }
                    }
                }
            }
        }
        titleField.addTextChangedListener(titleWatcher)
        addressField.addTextChangedListener(addressWatcher)
        //radiusField.addTextChangedListener(radiusWatcher)
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        silencerDetailViewModel.saveSilencer(silencer)
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun updateUI() {
        titleField.setText(silencer.title)
        addressField.setText(silencer.address)
        radiusField.setText(silencer.radius.toString())
        latitudeField.setText(silencer.latitude.toString())
        longitudeField.setText(silencer.longitude.toString())


    //TODO update UI
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

    override fun onTimeSelected(calendar: Calendar) {
        TODO("Not yet implemented")
    }
}