package com.example.hoangdung.simplelocation.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.hoangdung.simplelocation.MyPlace
import com.example.hoangdung.simplelocation.R
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng

/**
 * Created by hoangdung on 11/13/17.
 */
class DirectionsAdapter(var mContext: Context, firstPlace: MyPlace , secondPlace: MyPlace) : RecyclerView.Adapter<DirectionsAdapter.DirectionsViewHolder>() {
    var mPlacesList: ArrayList<MyPlace> = ArrayList()
        set(value){
            field = value
            notifyDataSetChanged()
        }
    var itemClickListenerCallback: OnItemClickListener? = null
    init {
        mPlacesList.add(firstPlace)
        mPlacesList.add(secondPlace)
    }
    interface OnItemClickListener{
        fun onItemClick(position: Int)
        fun onItemDelete(position: Int)
    }
    override fun onBindViewHolder(holder: DirectionsViewHolder?, position: Int) {
        //Deletion on first and last item not allowed
        if(position == 0 || position == mPlacesList.size)
            holder?.deleteDirection?.visibility = View.INVISIBLE
        else
            holder?.deleteDirection?.visibility = View.VISIBLE
        //Set text to another view
        if(position < mPlacesList.size)
            holder?.directionName?.text = if(mPlacesList[position].place != null) mPlacesList[position].place?.name else mPlacesList[position].fullName
        else
            holder?.directionName?.text =""
        holder?.directionName?.setOnClickListener(View.OnClickListener { itemClickListenerCallback?.onItemClick(position) })
        holder?.deleteDirection?.setOnClickListener(View.OnClickListener { itemClickListenerCallback?.onItemDelete(position) })

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DirectionsViewHolder {
        var inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var itemView = inflater.inflate(R.layout.direction_item,parent,false)
        var viewHolder = DirectionsViewHolder(itemView)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return mPlacesList.size + 1
    }

    class DirectionsViewHolder constructor(var myView: View): RecyclerView.ViewHolder(myView){
        var directionName: TextView = myView.findViewById(R.id.directionName)
        var deleteDirection: ImageButton = myView.findViewById(R.id.deleteDirection)
    }
}