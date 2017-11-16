package com.example.hoangdung.simplelocation.Fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse

import com.example.hoangdung.simplelocation.R
import kotlinx.android.synthetic.main.driving_info_layout.*
import kotlinx.android.synthetic.main.fragment_directions.*
import kotlinx.android.synthetic.main.fragment_directions.view.*


/**
 * A simple [Fragment] subclass.
 */
class DrivingInfoFragment() : Fragment() {

    lateinit var mAdapter : Adapter
    lateinit var mContext : Context
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_driving_info, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stepRecyclerView.layoutManager = LinearLayoutManager(mContext)
        stepRecyclerView.setHasFixedSize(true)
        stepRecyclerView.adapter = mAdapter
    }

    companion object {
        fun newInstance(context: Context) : DrivingInfoFragment{
            var fragment = DrivingInfoFragment()
            fragment.mAdapter = Adapter(context)
            fragment.mContext = context
            return fragment
        }
    }
    //DrivingInfoFragment's Adapter for RecyclerView
    class Adapter(var context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>(){
        var directionResponse: DirectionsResponse? = null
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        override fun getItemCount(): Int {
            return directionResponse?.routes?.get(0)?.legs?.get(0)?.steps?.size ?: 0

        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder?.stepText?.text = Html.fromHtml(directionResponse?.routes?.get(0)?.legs?.get(0)?.steps?.get(position)?.htmlInstructions)
            holder?.beforeheadDistance?.text = directionResponse?.routes?.get(0)?.legs?.get(0)?.steps?.get(position)?.distance?.text
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            var inflater = LayoutInflater.from(context)
            var view = inflater.inflate(R.layout.direction_item,parent,false)
            var viewHolder = ViewHolder(view)
            return viewHolder
        }


        class ViewHolder(var item: View) : RecyclerView.ViewHolder(item){
            var stepText = item?.findViewById<TextView>(R.id.stepText)
            var beforeheadDistance = item?.findViewById<TextView>(R.id.beforeheadDistance)
            var directionIcon = item?.findViewById<ImageView>(R.id.directionIcon)
        }
    }

}// Required empty public constructor
