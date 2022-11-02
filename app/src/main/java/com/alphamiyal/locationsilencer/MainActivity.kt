package com.alphamiyal.locationsilencer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), SilencerListFragment.Callbacks  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =supportFragmentManager.findFragmentById(R.id.fragment_container)

        //if fragment doesn't exist, create it
        if(currentFragment == null){
            val fragment = SilencerListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onSilencerSelected(silencerId: UUID) {
        val fragment = SilencerFragment.newInstance(silencerId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}