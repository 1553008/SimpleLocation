package com.example.hoangdung.simplelocation.Fragments


import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Step

import com.example.hoangdung.simplelocation.R
import kotlinx.android.synthetic.main.bus_info_layout.*
import kotlinx.android.synthetic.main.fragment_bus_info.*
import kotlinx.android.synthetic.main.fragment_info_tab.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class BusTabFragment : Fragment() {

    lateinit var mAdapter: BusTabFragment.Adapter
    lateinit var mContext: Context
    var directionsReponse: DirectionsResponse? = null
        set(value) {
            field = value
            BusDistanceText?.text = directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.distance?.text
            BusDurationText?.text = directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.durationInTraffic?.text
            DepartureAndArriveTime?.text = "(" + directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.departTime?.text + " - " +  value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.arriveTime?.text + ")"
            mAdapter?.steps = value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.steps
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_bus_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Step Recycler view Setup
        mAdapter = Adapter()
        BusStepListView.adapter = mAdapter
        BusStepListView.layoutManager = LinearLayoutManager(context)
        BusStepListView.setHasFixedSize(true)

        //Setting up panel
        var slidingUpLayoutParams = BusSlidingUpPanel.layoutParams as FrameLayout.LayoutParams
        BusSlidingUpPanel.layoutParams = slidingUpLayoutParams
        BusSlidingUpPanel.viewTreeObserver.addOnGlobalLayoutListener {
            BusSlidingUpPanel?.panelHeight = busInfoHeader?.height!!
        }
        //Setting up floating action button
        var btnLayoutParams = activity!!.findViewById<FloatingActionButton>(R.id.floating_btn).layoutParams as CoordinatorLayout.LayoutParams
        btnLayoutParams.bottomMargin =
                BusSlidingUpPanel.panelHeight +
                context!!.resources.getDimension(R.dimen.myplaceButtonMarginBottom).toInt()

        BusSlidingUpPanel.setFadeOnClickListener { v: View? ->

        }
        //Set Swipable for Custom Viewpager
        busInfoBlank.setOnTouchListener { v, event ->
            parentFragment?.viewPager?.swipable = false
            BusSlidingUpPanel?.swipeable = false
            return@setOnTouchListener false
        }
        busInfoLayout.setOnTouchListener { v, event ->
            parentFragment?.viewPager?.swipable = true
            BusSlidingUpPanel?.swipeable = true
            return@setOnTouchListener false
        }
    }

    companion object {
        fun newInstance(context: Context): BusTabFragment {
            var fragment = BusTabFragment()
            fragment.mContext = context
            return fragment
        }
    }

    class Adapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        companion object {
            val BUS_NORMAL_HOLDER = 3
            val BUS_ENHANCE_HOLDER = 4
        }
        var steps: ArrayList<Step>? = null
            set(value) {
                Log.d("MapsActivity", "directionResponse Received")
                field = value
                notifyDataSetChanged()
            }
        override fun getItemCount(): Int {
            return steps?.size?:0
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            //Parse step instruction into ViewHolder
            var step = steps?.getOrNull(position)
            if(getItemViewType(position) == BUS_NORMAL_HOLDER)
            {
                var holder = holder as BusNormalViewHolder
                holder?.stepText?.text = Html.fromHtml(step?.htmlInstructions)
                holder?.beforeheadDistance?.text = step?.distance?.text
                holder?.durationStepText?.text = step?.duration?.text
                holder?.icon?.setImageResource(R.drawable.ic_walking)
            }
            else{
                var holder = holder as BusEnhancedViewHolder
                holder?.stepInstruction.text = Html.fromHtml(step?.htmlInstructions)
                holder?.lineName?.text = step?.transitDetails?.line?.name
                holder?.transitName?.text = step?.transitDetails?.line?.vehicle?.name
                holder?.departureName?.text = step?.transitDetails?.departureStop?.name
                holder?.departureTime?.text = step?.transitDetails?.departTime?.text
                holder?.arrivalName?.text = step?.transitDetails?.arrivalStop?.name
                holder?.arrivalTime?.text = step?.transitDetails?.arrivalTime?.text
                holder?.distance?.text = step?.distance?.text
                holder?.icon?.setImageResource(R.drawable.ic_bus)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var inflater = LayoutInflater.from(parent?.context)
            var view: View? = null
            var viewholder: RecyclerView.ViewHolder? = null
            if(viewType == BUS_NORMAL_HOLDER)
            {
                view = inflater.inflate(R.layout.bus_info_item_normal,parent,false)
                viewholder = BusNormalViewHolder(view)
            }
            else
            {
                view = inflater.inflate(R.layout.bus_info_item_enhance,parent,false)
                viewholder = BusEnhancedViewHolder(view)
            }
            return viewholder
        }


        class BusNormalViewHolder(var item: View) : RecyclerView.ViewHolder(item) {
            var stepText = item.findViewById<TextView>(R.id.stepText)
            var beforeheadDistance = item.findViewById<TextView>(R.id.beforeheadDistance)
            var durationStepText = item.findViewById<TextView>(R.id.durationStepText)
            var icon = item.findViewById<ImageView>(R.id.iconView)
        }

        class BusEnhancedViewHolder(var item: View) : RecyclerView.ViewHolder(item){
            var stepInstruction = item.findViewById<TextView>(R.id.stepInstruction)
            var lineName = item.findViewById<TextView>(R.id.lineName)
            var transitName = item.findViewById<TextView>(R.id.transitType)
            var departureTime = item.findViewById<TextView>(R.id.DepartureTime)
            var departureName = item.findViewById<TextView>(R.id.DepartureName)
            var arrivalTime = item.findViewById<TextView>(R.id.ArrivalTime)
            var arrivalName = item.findViewById<TextView>(R.id.ArrivalName)
            var distance = item.findViewById<TextView>(R.id.BusDistanceText)
            var icon = item.findViewById<ImageView>(R.id.iconView)
        }
        override fun getItemViewType(position: Int): Int {
            if(steps?.getOrNull(position)?.transitDetails == null)
                return BUS_NORMAL_HOLDER
            else
                return BUS_ENHANCE_HOLDER
        }

    }// Required empty public constructor

}
