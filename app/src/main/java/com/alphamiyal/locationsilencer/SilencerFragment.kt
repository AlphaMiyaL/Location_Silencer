package com.alphamiyal.locationsilencer

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Observer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.text.DateFormat
import java.util.*

private const val TAG = "SilencerFragment"
private const val ARG_SILENCER_ID = "silencer_id"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0

class SilencerFragment: Fragment(), TimePickerFragment.Callbacks {
    private lateinit var silencer: Silencer
    private lateinit var titleField: EditText
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
        val mapFrag: Fragment = MapFragment()

        //Open map fragment
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.map_view, mapFrag)
            .commit()

        silencerDetailViewModel.loadSilencer(silencerId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_silencer, container, false)

        titleField = view.findViewById(R.id.silencer_title) as EditText
        //TODO
        //location = view.findViewById() as ...

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
        titleField.addTextChangedListener(titleWatcher)
    }

    override fun onStop() {
        super.onStop()
        silencerDetailViewModel.saveSilencer(silencer)
    }

    private fun updateUI() {
        titleField.setText(silencer.title)
    }

    override fun onTimeSelected(calendar: Calendar) {
        TODO("Not yet implemented")
    }
}