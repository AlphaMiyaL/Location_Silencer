package com.alphamiyal.locationsilencer

import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.Transition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_permissions.*

class PermissionsFragment:Fragment(){
    //interface for hosting activities
    interface Callbacks{
        fun onContinueSelected()
        fun onLocSelected()
        fun onDNDSelected()
        fun onHighAccSelected()
 //       fun onNotifSelected()
    }

    private var callbacks: Callbacks? = null
    private lateinit var locButton: Button
    private lateinit var highAccButton: Button
    private lateinit var dndButton: Button
    private lateinit var notifButton: Button
    private lateinit var doneButton: Button
    private lateinit var locLayout: LinearLayout
    private lateinit var locLayout2: LinearLayout
    private lateinit var highAccLayout: LinearLayout
    private lateinit var highAccLayout2:LinearLayout
    private lateinit var dndLayout: LinearLayout
    private lateinit var notifLayout: LinearLayout
    private lateinit var locDetailsV1: TextView
    private lateinit var locDetailsV2: TextView
    private lateinit var highAccDetailsV1: TextView
    private lateinit var highAccDetailsV2: TextView
    private lateinit var dndDetailsV1: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_permissions, container, false)

        locButton = view.findViewById(R.id.loc_button) as Button
        highAccButton = view.findViewById(R.id.high_acc_button) as Button
        dndButton = view.findViewById(R.id.dnd_button) as Button
        doneButton = view.findViewById(R.id.done_button) as Button
   //     notifButton = view.findViewById(R.id.notif_button) as Button
        locLayout = view.findViewById(R.id.loc_layout) as LinearLayout
        locLayout2 = view.findViewById(R.id.loc_layout2) as LinearLayout
        highAccLayout = view.findViewById(R.id.high_acc_layout) as LinearLayout
        highAccLayout2 = view.findViewById(R.id.high_acc_layout2) as LinearLayout
        dndLayout = view.findViewById(R.id.dnd_layout) as LinearLayout
        notifLayout = view.findViewById(R.id.notification_layout) as LinearLayout
        locDetailsV1 = view.findViewById(R.id.loc_details_v1) as TextView
        locDetailsV2 = view.findViewById(R.id.loc_details_v2) as TextView
        highAccDetailsV1 = view.findViewById(R.id.high_acc_details_v1) as TextView
        highAccDetailsV2 = view.findViewById(R.id.high_acc_details_v2) as TextView
        dndDetailsV1 = view.findViewById(R.id.dnd_details_v1) as TextView

        locLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        locLayout2.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        highAccLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        highAccLayout2.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        dndLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        return view
    }

    override fun onStart() {
        super.onStart()

        locButton.setOnClickListener {
            callbacks?.onLocSelected()
        }

        highAccButton.setOnClickListener {
            callbacks?.onHighAccSelected()
        }

        dndButton.setOnClickListener {
            callbacks?.onDNDSelected()
        }

//        notifButton.setOnClickListener{
//            callbacks?.onNotifSelected()
//        }

        doneButton.setOnClickListener {
            callbacks?.onContinueSelected()
        }

        locLayout.setOnClickListener{
            var visible = if (locDetailsV1.visibility == View.GONE) View.VISIBLE else View.GONE

            TransitionManager.beginDelayedTransition(locLayout, AutoTransition())
            locDetailsV1.visibility = visible
        }

        locLayout2.setOnClickListener{
            var visible = if (locDetailsV2.visibility == View.GONE) View.VISIBLE else View.GONE

            TransitionManager.beginDelayedTransition(locLayout, AutoTransition())
            locDetailsV2.visibility = visible
        }

        highAccLayout.setOnClickListener{
            var visible = if (highAccDetailsV1.visibility == View.GONE) View.VISIBLE else View.GONE

            TransitionManager.beginDelayedTransition(locLayout, AutoTransition())
            highAccDetailsV1.visibility = visible
        }

        highAccLayout2.setOnClickListener{
            var visible = if (highAccDetailsV2.visibility == View.GONE) View.VISIBLE else View.GONE

            TransitionManager.beginDelayedTransition(locLayout, AutoTransition())
            highAccDetailsV2.visibility = visible
        }

        dndLayout.setOnClickListener{
            var visible = if (dndDetailsV1.visibility == View.GONE) View.VISIBLE else View.GONE

            TransitionManager.beginDelayedTransition(locLayout, AutoTransition())
            dndDetailsV1.visibility = visible
        }

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
            notifLayout.visibility = View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

    companion object{
        fun newInstance(): PermissionsFragment{
            return PermissionsFragment()
        }
    }
}