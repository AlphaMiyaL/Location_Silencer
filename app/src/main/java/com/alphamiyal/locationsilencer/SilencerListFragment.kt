package com.alphamiyal.locationsilencer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.behavior.SwipeDismissBehavior
import kotlinx.android.synthetic.main.fragment_silencer.*
import kotlinx.android.synthetic.main.list_item_silencer.view.*
import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.*


private const val TAG = "LocationListFragment"
class SilencerListFragment: Fragment() {
    //interface for hosting activities
    interface Callbacks{
        fun onSilencerSelected(silencerId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var silencerRecyclerView:RecyclerView
    private var adapter: SilencerAdapter? = SilencerAdapter(emptyList())

    private val silencerListViewModel: SilencerListViewModel by lazy {
        ViewModelProvider(this)[SilencerListViewModel::class.java]
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_silencer_list, container, false)
        silencerRecyclerView = view.findViewById(R.id.silencer_recycler_view) as RecyclerView
        //RecyclerView requires a layoutManager to function, else it crash
        silencerRecyclerView.layoutManager = LinearLayoutManager(context)
        silencerRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        silencerListViewModel.silencerListLiveData.observe(
            viewLifecycleOwner,
            Observer { silencers ->
                silencers?.let {
                    Log.i(TAG, "Got silencers ${silencers.size}")
                    updateUI(silencers)
                }
            })
        val swipeToDeleteCallback = object: SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                Log.d(TAG, "Position " + position)
                val currSilencer = adapter?.silencers?.get(position)

                if (currSilencer != null) {
                    silencerListViewModel.deleteSilencer(currSilencer)
                }
                 //   .adapter?.silencers.
                adapter?.notifyItemRemoved(position)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(silencerRecyclerView)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks=null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_silencer_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.new_silencer -> {
                val silencer = Silencer()
                val globalID = GlobalID()
                silencer.idInt  = context?.let { globalID.getID(it) }!!
                silencerListViewModel.addSilencer(silencer)
                callbacks?.onSilencerSelected(silencer.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private inner class SilencerHolder(view: View):RecyclerView.ViewHolder(view), View.OnClickListener{
        private lateinit var silencer: Silencer

        private val titleTextView: TextView = itemView.findViewById(R.id.silencer_title)
        //private val radiusTextView: TextView = itemView.findViewById(R.id.radius_title)
        private val addressTextView: TextView = itemView.findViewById(R.id.silencer_address)
        private val onOffSwitch: Switch = itemView.findViewById(R.id.onOff)


        init {
            itemView.setOnClickListener(this)
        }

        @RequiresApi(Build.VERSION_CODES.N)
        fun bind(silencer: Silencer) {
            this.silencer = silencer
            titleTextView.text = this.silencer.title
            addressTextView.text = this.silencer.address
            onOffSwitch.isChecked = this.silencer.on

            onOffSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    Log.d(TAG,"button turn on")
                    // The toggle is enabled
                    silencer.on = true
                    SilenceLocation.get().addGeofence(
                        silencer.id,
                        silencer.latitude,
                        silencer.longitude,
                        silencer.radius)
                    silencerListViewModel.saveSilencer(silencer)
                } else {
                    // The toggle is disabled
                    Log.d(TAG,"button turned off")
                    silencer.on = false
                    SilenceLocation.get().removeGeofence(silencer.id)
                    silencerListViewModel.saveSilencer(silencer)
                }
            }


        }

        override fun onClick(v: View?) {
            callbacks?.onSilencerSelected(silencer.id)
        }
    }

    //RecyclerView is responsible for asking adapter to create new ViewHolders, and asking to bind them to items
    //Adapter is responsible for creating necessary ViewHolders when asked, binding them to model layer
    private inner class SilencerAdapter(var silencers: List<Silencer>):RecyclerView.Adapter<SilencerHolder>(){
        //responsible for creating view, wrapping view in ViewHolder, returning result
        //here we inflate list_item_view.xml and pass inflated view to new instance of SilencerHolder
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SilencerHolder {
            val view = layoutInflater.inflate(R.layout.list_item_silencer, parent, false)
            return SilencerHolder(view)
        }
        //responsible for populating given holder w/ silencer given position
        //here we get silencer from silencer list at given position
        //Be efficient on this or scrolling can be laggy
        @RequiresApi(Build.VERSION_CODES.N)
        override fun onBindViewHolder(holder: SilencerHolder, position: Int) {
            val silencer =silencers[position]
            holder.bind(silencer)
        }

        override fun getItemCount()=silencers.size

    }

    private fun updateUI(silencers:List<Silencer>){
        adapter = SilencerAdapter(silencers)
        silencerRecyclerView.adapter = adapter
    }

    companion object{
        fun newInstance(): SilencerListFragment{
            return SilencerListFragment()
        }
    }
}