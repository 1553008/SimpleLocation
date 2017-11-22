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
import com.example.hoangdung.simplelocation.MyApplication

import com.example.hoangdung.simplelocation.R
import kotlinx.android.synthetic.main.bus_info_layout.*
import kotlinx.android.synthetic.main.driving_info_layout.*
import kotlinx.android.synthetic.main.fragment_bus_info.*
import kotlinx.android.synthetic.main.fragment_driving_info.*
import kotlinx.android.synthetic.main.fragment_info_tab.*


/**
 * A simple [Fragment] subclass.
 */
class BusInfoFragment : Fragment() {

    lateinit var mAdapter: BusInfoFragment.Adapter
    lateinit var mContext: Context
    var directionsReponse: DirectionsResponse? = null
        set(value) {
            field = value
            BusdistanceText?.text = directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.distance?.text
            BusdurationText?.text = directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.durationInTraffic?.text
            departureAndArriveTime?.text = "(" + directionsReponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.departTime?.text + " - " +  value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.arriveTime?.text + ")"
            mAdapter.directionResponse = directionsReponse
        }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_bus_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Step Recycler view Setup
        BusstepRecyclerView.layoutManager = LinearLayoutManager(mContext)
        BusstepRecyclerView.setHasFixedSize(true)
        mAdapter = BusInfoFragment.Adapter(mContext)
        BusstepRecyclerView.adapter = mAdapter

        //Setting up panel
        var slidingUpLayoutParams = BusSlidingUpPanel.layoutParams as FrameLayout.LayoutParams
        slidingUpLayoutParams.bottomMargin = MyApplication.getNavigationBarHeight(context, context.resources.configuration.orientation)
        BusSlidingUpPanel.layoutParams = slidingUpLayoutParams
        BusSlidingUpPanel.viewTreeObserver.addOnGlobalLayoutListener {
            BusSlidingUpPanel?.panelHeight = busInfoHeader?.height!!
        }

        //Setting up floating action button
        var btnLayoutParams = activity.findViewById<FloatingActionButton>(R.id.floating_btn).layoutParams as CoordinatorLayout.LayoutParams
        btnLayoutParams.bottomMargin = MyApplication.getNavigationBarHeight(context, resources.configuration.orientation) +
                BusSlidingUpPanel.panelHeight +
                context.resources.getDimension(R.dimen.myplaceButtonMarginBottom).toInt()


        //Set Swipable for Custom Viewpager
        busInfoBlank.setOnTouchListener { v, event ->
            parentFragment?.viewPager?.swipable = false
            return@setOnTouchListener false
        }
        busInfoLayout.setOnTouchListener { v, event ->
            parentFragment?.viewPager?.swipable = true
            return@setOnTouchListener false
        }
    }

    companion object {
        fun newInstance(context: Context): BusInfoFragment {
            var fragment = BusInfoFragment()
            fragment.mContext = context
            return fragment
        }
    }

    class Adapter(var context: Context) : RecyclerView.Adapter<BusInfoFragment.Adapter.ViewHolder>() {
        var directionResponse: DirectionsResponse? = null
            set(value) {
                Log.d("MapsActivity", "directionResponse Received")
                field = value
                notifyDataSetChanged()
                dict.clear()
                var position = 0
                directionResponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.steps?.forEachIndexed { index, steps ->
                    steps?.steps?.forEachIndexed { innerIndex, innerStep ->
                        dict[position] = Pair(index,innerIndex)
                        ++position
                    }
                }
                count = position

            }
        var count = 0;
        var dict: HashMap<Int,Pair<Int,Int>> = HashMap(0);
        override fun getItemCount(): Int {
            return count
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            //Parse step instruction into ViewHolder
            val (index, innerIndex) = dict[position]!!
            val step = directionResponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.steps?.getOrNull(index)?.steps?.getOrNull(innerIndex)
            holder?.stepText?.text = Html.fromHtml(step?.htmlInstructions)
            holder?.beforeheadDistance?.text = step?.distance?.text
            holder?.durationStepText?.text = step?.duration?.text
            val maneuver = step?.maneuver
            if(maneuver?.equals("turn-right") ?: false)
                holder?.directionIcon?.setImageResource(R.drawable.icon_turn_right)
            else if(maneuver?.equals("turn-left") ?: false)
                holder?.directionIcon?.setImageResource(R.drawable.icon_turn_left)
            else
                holder?.directionIcon?.setImageResource(R.drawable.icon_straight_ahead)
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            var inflater = LayoutInflater.from(context)
            var view = inflater.inflate(R.layout.bus_info_item, parent, false)
            var viewHolder = ViewHolder(view)
            return viewHolder
        }


        class ViewHolder(var item: View) : RecyclerView.ViewHolder(item) {
            var stepText = item.findViewById<TextView>(R.id.stepText)
            var beforeheadDistance = item.findViewById<TextView>(R.id.beforeheadDistance)
            var directionIcon = item.findViewById<ImageView>(R.id.directionIcon)
            var durationStepText = item.findViewById<TextView>(R.id.durationStepText)
        }

    }// Required empty public constructor
}
