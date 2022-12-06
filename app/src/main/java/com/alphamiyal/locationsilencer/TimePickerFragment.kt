package com.alphamiyal.locationsilencer


import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.util.*

private const val ARG_DATE = "date"

private const val TAG = "TimePicker"

class TimePickerFragment: DialogFragment() {
    interface Callbacks {
        fun onTimeSelected(calendar: Calendar)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener = TimePickerDialog.OnTimeSetListener{
                _: TimePicker, hour: Int, minute: Int->
            val resultTime: Calendar = GregorianCalendar(0, 0,0, hour, minute)
            Log.d(TAG, resultTime.time.toString())

            targetFragment?.let { fragment ->
                (fragment as Callbacks).onTimeSelected(resultTime)
            }
        }
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        var initialMinute = calendar.get(Calendar.MINUTE)
        Log.d(TAG, calendar.time.toString())

        Log.d(TAG, calendar.get(Calendar.HOUR_OF_DAY).toString())
        Log.d(TAG, calendar.get(Calendar.MINUTE).toString())

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute - 8,
            false)
    }

    companion object{
        fun newInstance(date: Date): TimePickerFragment{
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}