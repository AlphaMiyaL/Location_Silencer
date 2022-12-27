package com.alphamiyal.locationsilencer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PermissionsFragment:Fragment(){
    //interface for hosting activities
    interface Callbacks{
        fun onContinueSelected()
        fun onLocSelected()
        fun onDNDSelected()
        fun onHighAccSelected()
    }

    private var callbacks: Callbacks? = null
    private lateinit var locButton: Button
    private lateinit var highAccButton: Button
    private lateinit var dndButton: Button
    private lateinit var doneButton: Button

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
        return view
    }

    override fun onStart() {
        super.onStart()

        locButton.setOnClickListener { view: View ->
            callbacks?.onLocSelected()
        }

        highAccButton.setOnClickListener { view: View ->
            callbacks?.onHighAccSelected()
        }

        dndButton.setOnClickListener { view: View ->
            callbacks?.onDNDSelected()
        }

        doneButton.setOnClickListener { view: View ->
            callbacks?.onContinueSelected()
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