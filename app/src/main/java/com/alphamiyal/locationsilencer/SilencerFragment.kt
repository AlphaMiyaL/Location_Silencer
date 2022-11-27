package com.alphamiyal.locationsilencer

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_silencer.*
import kotlinx.android.synthetic.main.fragment_silencer.view.*
import java.lang.Exception
import java.util.*


private const val TAG = "SilencerFragment"
private const val ARG_SILENCER_ID = "silencer_id"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0

class SilencerFragment: Fragment(), TimePickerFragment.Callbacks {
    private lateinit var silencer: Silencer
    private lateinit var titleField: EditText
    private lateinit var addressField: EditText
    private lateinit var radiusField: EditText
    private lateinit var latitudeField: TextView
    private lateinit var longitudeField: TextView
    private lateinit var mapButton: Button

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
        mapButton = view.findViewById(R.id.button_id) as Button
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

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                silencer.title = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

//        val radiusWatcher = object : TextWatcher {
//            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
//            }
//            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
//                silencer.radius = sequence.toString().toDouble()
//            }
//            override fun afterTextChanged(sequence: Editable?) {
//            }
//        }

        radiusField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                try {
                    silencer.radius = radiusField.text.toString().toDouble()
                    updateUI()
                }
                catch (e: Exception){
                    radiusField.setText(silencer.radius.toString())
                    updateUI()
                }
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

        addressField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val loc: String = silencer.address.trim()
                if(loc == null || loc == "") {
                    Toast.makeText(context, "provide location", Toast.LENGTH_SHORT).show()
                }
                else{
                    val gcd: Geocoder = Geocoder(context, Locale.getDefault())
                    var add: List<Address>? = null
                    loop@ for(i in 1..2){
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

                return@OnKeyListener true
            }
            false
        })

        mapButton.setOnClickListener { view: View ->
//           childFragmentManager?.findFragmentById(R.id.map_view)
//            Log.d(TAG, "Hello2")
//
//            //if fragment doesn't exist, create it
//            if(mapFragment == null){
//                Log.d(TAG, "Hello")
//                val mapFrag = MapFragment.newInstance(silencer)
//                requireParentFragment().childFragmentManager
//                    .beginTransaction()
//                    .add(R.id.map_view, mapFrag)
//                    .commit()
//            }
//            val mMapView = LayoutInflater.from(context).inflate(R.layout.map_window, null)
//            val mBuilder = AlertDialog.Builder(context)
//                .setView(mMapView)
//                .setTitle("Map")
//            val mAlertDialog = mBuilder.show()

//            val mapPopUp = MapFragment(silencer)
//            mapPopUp.show((activity as AppCompatActivity).supportFragmentManager, "")

            MapFragment(silencer).show(childFragmentManager, "MapFragment")

            val mapFrag = MapFragment.newInstance(silencer)
            Log.d(TAG, "Created?")


        }

        titleField.addTextChangedListener(titleWatcher)
        addressField.addTextChangedListener(addressWatcher)
        //radiusField.addTextChangedListener(radiusWatcher)
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        silencerDetailViewModel.saveSilencer(silencer)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    private fun updateUI() {
        titleField.setText(silencer.title)
        addressField.setText(silencer.address)
        radiusField.setText(silencer.radius.toString())
        latitudeField.setText(silencer.latitude.toString())
        longitudeField.setText(silencer.longitude.toString())
    }

    override fun onTimeSelected(calendar: Calendar) {
        TODO("Not yet implemented")
    }

    /**
     * Button stuff
     * val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)

    //if fragment doesn't exist, create it
    if(currentFragment == null){
    val fragment = SilencerListFragment.newInstance()
    supportFragmentManager
    .beginTransaction()
    .add(R.id.fragment_container, fragment)
    .commit()
    }
     */
}