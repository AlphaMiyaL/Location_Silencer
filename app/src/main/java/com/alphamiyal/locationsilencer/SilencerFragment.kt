package com.alphamiyal.locationsilencer

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*

private const val TAG = "SilencerFragment"
private const val ARG_SILENCER_ID = "silencer_id"
private const val DIALOG_TIME = "DialogTime"
private const val REQUEST_TIME = 0

class SilencerFragment: Fragment(), TimePickerFragment.Callbacks {
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

    override fun onTimeSelected(calendar: Calendar) {
        TODO("Not yet implemented")
    }
}