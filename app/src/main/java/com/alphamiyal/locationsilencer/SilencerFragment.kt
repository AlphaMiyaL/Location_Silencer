package com.alphamiyal.locationsilencer


import android.icu.util.Calendar
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.maps.model.LatLng
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val TAG = "SilencerFragment"
private const val ARG_SILENCER_ID = "silencer_id"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0

class SilencerFragment: Fragment(), TimePickerFragment.Callbacks {
    private lateinit var silencer: Silencer
    private lateinit var titleField: EditText
    private lateinit var addressField: AutoCompleteTextView
    private lateinit var cityField: EditText
    private lateinit var stateField: EditText
    private lateinit var zipcodeField: EditText
    private lateinit var radiusField: EditText
    private lateinit var unitDropdown: Spinner
    private lateinit var latitudeField: TextView
    private lateinit var longitudeField: TextView
    private lateinit var mapButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var endTimeButton: Button
    private lateinit var locCheckBox: CheckBox
    private lateinit var timeCheckBox: CheckBox
    private lateinit var adView: AdView
    private lateinit var saveButton: Button
    private lateinit var locGroup: Group
    private lateinit var timeGroup: Group

    private var tempAddress = ""
    private var tempLatitude = 0.0
    private var tempLongitude = 0.0
    private var tempThoroughfare = ""
    private var tempSubthoroughfare = ""
    private var tempLocatlity = ""
    private var tempAdminArea = ""
    private var tempPostalCode = ""


    private var startTimeSelect: Boolean = true
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
        addressField = view.findViewById(R.id.silencer_address) as AutoCompleteTextView
        cityField = view.findViewById(R.id.silencer_city) as EditText
        stateField = view.findViewById(R.id.silencer_state) as EditText
        zipcodeField = view.findViewById(R.id.silencer_zipcode) as EditText
        radiusField = view.findViewById(R.id.silencer_radius) as EditText
        unitDropdown = view.findViewById(R.id.units) as Spinner
        latitudeField = view.findViewById(R.id.silencer_latitude) as TextView
        longitudeField = view.findViewById(R.id.silencer_longitude) as TextView
        startTimeButton = view.findViewById(R.id.silencer_time_start) as Button
        endTimeButton = view.findViewById(R.id.silencer_time_end) as Button
        mapButton = view.findViewById(R.id.button_id) as Button
        locCheckBox = view.findViewById(R.id.loc_checkbox) as CheckBox
        timeCheckBox = view.findViewById(R.id.time_checkbox) as CheckBox
        adView = view.findViewById(R.id.ad_view) as AdView
        saveButton = view.findViewById(R.id.save_button) as Button
        locGroup = view.findViewById(R.id.loc_group) as Group
        timeGroup = view.findViewById(R.id.time_group) as Group
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

    @RequiresApi(Build.VERSION_CODES.N)
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

        val radiusWatcher = object : TextWatcher{
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                silencerDetailViewModel.rad = sequence.toString()
            }
            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        radiusField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                try {
                    silencer.radius = radiusField.text.toString().toDouble()
                    silencerDetailViewModel.rad = silencer.radius.toString()
                    silencerDetailViewModel.changing = false
                    updateUI()
                }
                catch (e: Exception){
                    //N/A
                }
            }
            else{
                silencerDetailViewModel.changing = true
            }
        }

        val units = resources.getStringArray(R.array.units)
        val adapter = context?.let { ArrayAdapter(it, R.layout.custom_spinner_dropdown, units) }
        unitDropdown.adapter = adapter

        unitDropdown.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
           override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
               silencer.unit = units[position]
               Log.d(TAG, "Unit is " + silencer.unit)

           }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //nothing :p
            }
        }

        locCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                silencer.useLoc = isChecked
                if(isChecked){
                    locGroup.visibility = View.VISIBLE
                }
                else{
                    locGroup.visibility = View.GONE
                }
            }
        }

        timeCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                silencer.useTime = isChecked
                if(isChecked){
                    timeGroup.visibility = View.VISIBLE
                }
                else{
                    timeGroup.visibility = View.GONE
                }
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        val addressWatcher = object : TextWatcher {
            override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
                silencerDetailViewModel.addr = sequence.toString()
                val loc: String = addressField.text.toString().trim()
                if(loc != null && loc != "") {
                    autofillGeocoder(addressField.text.toString().trim())
                }
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        addressField.setOnItemClickListener{parent, view, position, id ->
            if (tempAddress != ""){

                silencer.address = tempAddress
                silencer.thoroughfare = tempThoroughfare
                silencer.subThoroughfare = tempSubthoroughfare
                silencer.locality = tempLocatlity
                silencer.adminArea = tempAdminArea
                silencer.postalCode = tempPostalCode
                silencer.latitude = tempLatitude
                silencer.longitude = tempLongitude
            }
            updateUI()
        }

        addressField.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                silencerDetailViewModel.changing = true
            }
        }

        addressField.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val loc: String = addressField.text.toString().trim()
                if(loc == null || loc == "") {
                    Toast.makeText(context, "provide location", Toast.LENGTH_SHORT).show()
                    silencer.address = ""
                    silencer.thoroughfare = ""
                    silencer.subThoroughfare = ""
                    silencer.locality = ""
                    silencer.adminArea = ""
                    silencer.postalCode = ""
                    silencer.latitude = 0.0
                    silencer.longitude = 0.0
                    updateUI()
                }
                else{
                    enterGeocoder(addressField.text.toString().trim())
                }

                return@OnKeyListener true
            }
            false
        })

        mapButton.setOnClickListener { view: View ->
            MapFragment(silencer, addressField, cityField, stateField, zipcodeField, latitudeField, longitudeField)
                .show(childFragmentManager, "MapFragment")

            val mapFrag = MapFragment.newInstance(silencer, addressField, cityField, stateField, zipcodeField, latitudeField, longitudeField)

        }

        startTimeButton.setOnClickListener {
            TimePickerFragment.newInstance(silencer.startTime).apply {
                setTargetFragment(this@SilencerFragment, REQUEST_TIME)
                show(this@SilencerFragment.parentFragmentManager, DIALOG_TIME)
            }
            startTimeSelect = true
        }
        endTimeButton.setOnClickListener {
            TimePickerFragment.newInstance(silencer.endTime).apply {
                setTargetFragment(this@SilencerFragment, REQUEST_TIME)
                show(this@SilencerFragment.parentFragmentManager, DIALOG_TIME)
            }
            startTimeSelect = false
        }

        saveButton.setOnClickListener{
            activity?.onBackPressed()
        }

        titleField.addTextChangedListener(titleWatcher)
        radiusField.addTextChangedListener(radiusWatcher)
        addressField.addTextChangedListener(addressWatcher)
        updateUI()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStop() {
        super.onStop()
        silencerDetailViewModel.saveSilencer(silencer)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        silencerDetailViewModel.saveSilencer(silencer)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onTimeSelected(calendar: Calendar) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            calendar.add(Calendar.MINUTE, 8)
        }
        if(startTimeSelect){
            silencer.startTime = calendar.time
        }
        else{
            silencer.endTime = calendar.time
        }
        updateUI()
    }

    private fun updateUI() {

        titleField.setText(silencer.title)

        if(silencerDetailViewModel.changing){
            radiusField.setText(silencerDetailViewModel.rad.toString())
        }
        else{
            radiusField.setText(silencer.radius.toString())
        }

        var streetAddress = ""
        if (silencer.thoroughfare != null){
            streetAddress = "${silencer.thoroughfare}"
            Log.d(TAG, silencer.thoroughfare)
        }
        if (silencer.subThoroughfare != null && streetAddress == ""){
            streetAddress += silencer.subThoroughfare
        }
        else if (silencer.subThoroughfare != null && streetAddress != ""){
            streetAddress += " " + silencer.subThoroughfare
        }

        if(silencerDetailViewModel.changing){
            addressField.setText(silencerDetailViewModel.addr)
        }
        else{
            addressField.setText(streetAddress)
        }

        cityField.setText(silencer.locality)
        stateField.setText(silencer.adminArea)
        zipcodeField.setText(silencer.postalCode)


        when(silencer.unit){
            "Meters" -> unitDropdown.setSelection(0)
            "Kilometers" -> unitDropdown.setSelection(1)
            "FEET" -> unitDropdown.setSelection(2)
            "Miles" -> unitDropdown.setSelection(3)
        }

        latitudeField.text = silencer.latitude.toString()
        longitudeField.text = silencer.longitude.toString()
        startTimeButton.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(silencer.startTime)
        endTimeButton.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(silencer.endTime)
        locCheckBox.apply {
            isChecked = silencer.useLoc
            jumpDrawablesToCurrentState()
            if(isChecked){
                locGroup.visibility = View.VISIBLE
            }
            else{
                locGroup.visibility = View.GONE
            }
        }
        timeCheckBox.apply {
            isChecked = silencer.useTime
            jumpDrawablesToCurrentState()
            if(isChecked){
                timeGroup.visibility = View.VISIBLE
            }
            else{
                timeGroup.visibility = View.GONE
            }
        }
    }

    private fun enterGeocoder(newString: String) {
        val data = Data.Builder()
            .putString("address_string", addressField.text.toString())
            .build()

        val addressWorker = OneTimeWorkRequestBuilder<GeocoderWorker>()
            .setInputData(data)
            .build()

        context?.let { WorkManager.getInstance(it).enqueueUniqueWork("auto_correct",
            ExistingWorkPolicy.REPLACE, addressWorker) }

        context?.let {
            WorkManager.getInstance(it).getWorkInfoByIdLiveData(addressWorker.id)
                .observe(this, Observer{ workInfo ->
                    if (workInfo != null && workInfo.state.isFinished){
                        tempAddress = workInfo.outputData.getString("address").toString()
                        if(tempAddress != ""){
                            silencerDetailViewModel.changing = false

                            silencer.address = tempAddress
                            silencer.latitude = workInfo.outputData.getDouble("latitude", 0.0)
                            silencer.longitude = workInfo.outputData.getDouble("longitude", 0.0)
                            silencer.thoroughfare = workInfo.outputData.getString("thoroughfare").toString()
                            silencer.subThoroughfare = workInfo.outputData.getString("subThoroughfare").toString()
                            silencer.locality = workInfo.outputData.getString("locality").toString()
                            silencer.adminArea = workInfo.outputData.getString("adminArea").toString()
                            silencer.postalCode = workInfo.outputData.getString("postalCode").toString()
                        }
                        else{
                            Toast.makeText(context, "invalid location", Toast.LENGTH_SHORT).show()
                            silencer.address = ""
                            silencer.thoroughfare = ""
                            silencer.subThoroughfare = ""
                            silencer.locality = ""
                            silencer.adminArea = ""
                            silencer.postalCode = ""
                            silencer.latitude = 0.0
                            silencer.longitude = 0.0
                        }
                        updateUI()

                    }
                })
        }
    }

    private fun autofillGeocoder(newString: String) {
        val data = Data.Builder()
            .putString("address_string", addressField.text.toString())
            .build()

        val addressWorker = OneTimeWorkRequestBuilder<GeocoderWorker>()
            .setInputData(data)
            .build()

        context?.let { WorkManager.getInstance(it).enqueueUniqueWork("auto_correct",
            ExistingWorkPolicy.REPLACE, addressWorker) }

        context?.let {
            WorkManager.getInstance(it).getWorkInfoByIdLiveData(addressWorker.id)
                .observe(this, Observer{ workInfo ->
                    if (workInfo != null && workInfo.state.isFinished){
                        silencerDetailViewModel.changing = false

                        tempAddress = workInfo.outputData.getString("address").toString()
                        tempLatitude = workInfo.outputData.getDouble("latitude", 0.0)
                        tempLongitude = workInfo.outputData.getDouble("longitude", 0.0)
                        tempThoroughfare = workInfo.outputData.getString("thoroughfare").toString()
                        tempSubthoroughfare = workInfo.outputData.getString("subThoroughfare").toString()
                        tempLocatlity = workInfo.outputData.getString("locality").toString()
                        tempAdminArea = workInfo.outputData.getString("adminArea").toString()
                        tempPostalCode = workInfo.outputData.getString("postalCode").toString()

                        var addListString = mutableListOf<String>()
                        addListString.add(tempAddress)
                        val arrayAdapter = ArrayAdapter<String>(it, android.R.layout.simple_list_item_1, addListString)
                        addressField.setAdapter(arrayAdapter)
                    }
                })
        }
    }


}


