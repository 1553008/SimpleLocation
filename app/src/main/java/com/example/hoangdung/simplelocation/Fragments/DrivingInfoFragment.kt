package com.example.hoangdung.simplelocation.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.method.SingleLineTransformationMethod
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse
import com.example.hoangdung.simplelocation.MyApplication

import com.example.hoangdung.simplelocation.R
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.driving_info_layout.*
import kotlinx.android.synthetic.main.fragment_directions.*
import kotlinx.android.synthetic.main.fragment_directions.view.*
import kotlinx.android.synthetic.main.fragment_driving_info.*
import kotlinx.android.synthetic.main.fragment_info_tab.*


/**
 * A simple [Fragment] subclass.
 */
class DrivingInfoFragment() : Fragment() {

    lateinit var mAdapter : Adapter
    lateinit var mContext : Context
    var directionsReponse: DirectionsResponse? = null
        set(value) {
            field = value
            distanceText?.text = value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.distance?.text
            durationText?.text = value?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.duration?.text
            mAdapter.directionResponse = value
        }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_driving_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Step Recycler view Setup
        stepRecyclerView.layoutManager = LinearLayoutManager(mContext)
        stepRecyclerView.setHasFixedSize(true)
        mAdapter = Adapter(mContext)
        stepRecyclerView.adapter = mAdapter

        //Setting up panel
        var slidingUpLayoutParams = DrivingSlidingUpPanel.layoutParams as FrameLayout.LayoutParams
        slidingUpLayoutParams.bottomMargin = MyApplication.getNavigationBarHeight(context,context.resources.configuration.orientation)
        DrivingSlidingUpPanel.layoutParams = slidingUpLayoutParams
        drivingInfoHeader.viewTreeObserver.addOnGlobalLayoutListener{
            DrivingSlidingUpPanel?.panelHeight = drivingInfoHeader?.height!!
        }

        //Setting up floating action button
        var btnLayoutParams = activity.findViewById<FloatingActionButton>(R.id.floating_btn).layoutParams as CoordinatorLayout.LayoutParams
        btnLayoutParams.bottomMargin = MyApplication.getNavigationBarHeight(context,resources.configuration.orientation)+
                DrivingSlidingUpPanel.panelHeight +
                context.resources.getDimension(R.dimen.myplaceButtonMarginBottom).toInt()



        //Set Swipable for Custom Viewpager

        drivingInfoBlank.setOnTouchListener { v, event ->
            parentFragment?.viewPager?.swipable = false
            return@setOnTouchListener false
        }
        drivingInfoLayout.setOnTouchListener{v, event ->

            Log.d("MapsActivity","onTouchListener" + event.toString());
            parentFragment?.viewPager?.swipable = true
            return@setOnTouchListener false
        }

    }

    companion object {
        fun newInstance(context: Context) : DrivingInfoFragment{
            var fragment = DrivingInfoFragment()
            fragment.mContext = context
            return fragment
        }
    }
    //DrivingInfoFragment's Adapter for RecyclerView
    class Adapter(var context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>(){
        var directionResponse: DirectionsResponse? = null
            set(value) {
                Log.d("MapsActivity","directionResponse Received")
                field = value
                notifyDataSetChanged()
            }
        override fun getItemCount(): Int {
            return directionResponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.steps?.size ?: 0

        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            //Parse step instruction into ViewHolder
            val step = directionResponse?.routes?.getOrNull(0)?.legs?.getOrNull(0)?.steps?.getOrNull(position)
            holder?.stepText?.text = Html.fromHtml(step?.htmlInstructions?:"")
            holder?.beforeheadDistance?.text = step?.distance?.text
            holder?.durationStepText?.text = step?.duration?.text

            val maneuver = step?.maneuver
            //Assign Icon directions
            if(maneuver?.equals("turn-right") ?: false)
                holder?.directionIcon?.setImageResource(R.drawable.icon_turn_right)
            else if(maneuver?.equals("turn-left") ?: false)
                holder?.directionIcon?.setImageResource(R.drawable.icon_turn_left)
            else
                holder?.directionIcon?.setImageResource(R.drawable.icon_straight_ahead)

        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            var inflater = LayoutInflater.from(context)
            var view = inflater.inflate(R.layout.driving_info_item,parent,false)
            var viewHolder = ViewHolder(view)
            return viewHolder
        }


        class ViewHolder(var item: View) : RecyclerView.ViewHolder(item){
            var stepText = item.findViewById<TextView>(R.id.stepText)
            var beforeheadDistance = item.findViewById<TextView>(R.id.beforeheadDistance)
            var directionIcon = item.findViewById<ImageView>(R.id.directionIcon)
            var durationStepText = item.findViewById<TextView>(R.id.durationStepText)
        }
    }

}// Required empty public constructor
