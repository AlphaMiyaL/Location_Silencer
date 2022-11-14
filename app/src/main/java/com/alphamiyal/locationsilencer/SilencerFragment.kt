package com.alphamiyal.locationsilencer

import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
        //Initialize map fragment
        //val mapFrag: Fragment = MapFragment()

        //Open map fragment
//        parentFragmentManager
//            .beginTransaction()
//            .replace(R.id.map_view, mapFrag)
//            .commit()

        silencerDetailViewModel.loadSilencer(silencerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_silencer, container, false)

        titleField = view.findViewById(R.id.silencer_title) as EditText
        mapView = view.findViewById(R.id.map_view)
        initGoogleMap(savedInstanceState);
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
        mapView.onStart();

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                silencer.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }
        titleField.addTextChangedListener(titleWatcher)
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