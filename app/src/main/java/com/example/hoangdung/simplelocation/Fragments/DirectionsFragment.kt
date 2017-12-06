package com.example.hoangdung.simplelocation.Fragments


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hoangdung.simplelocation.Activity.SearchActivity
import com.example.hoangdung.simplelocation.Adapter.DirectionsAdapter
import com.example.hoangdung.simplelocation.MyPlace

import com.example.hoangdung.simplelocation.R
import com.google.android.gms.location.places.PlaceBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_directions.*


/**
 * DirectionsFragment shows up when user presses button to find directionsResponse
 * It handles all UI operations that user interacts and notify listener when the list of locations changes
 */
class DirectionsFragment constructor(): Fragment() {

    lateinit var directionsAdapter: DirectionsAdapter
    var positionClicked : Int = -1
    var directionsFragmentCallback:  DirectionsFragmentCallback? = null
    lateinit var mContext: Context
    lateinit var toolbar: Toolbar
    interface DirectionsFragmentCallback{
        fun onDirectionsFragmentUIReady(directionsFragment: DirectionsFragment)
        fun onLocationChanged(locationList: ArrayList<MyPlace>, directionsFragment: DirectionsFragment)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater!!.inflate(R.layout.fragment_directions, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        directionsRecycler.adapter = directionsAdapter
        directionsRecycler.setHasFixedSize(true)
        directionsRecycler.layoutManager = LinearLayoutManager(activity)
        toolbar = directionsToolbar
        directionsFragmentCallback?.onDirectionsFragmentUIReady(this)
        directionsFragmentCallback?.onLocationChanged(directionsAdapter.mPlacesList,this)
    }

    companion object {
        fun newInstance(context: Context,firstLocation: MyPlace, secondLocation: MyPlace) : DirectionsFragment {
            var fragment = DirectionsFragment()
            fragment.mContext = context
            var directionsAdapter = DirectionsAdapter(context,firstLocation,secondLocation)
            directionsAdapter.itemClickListenerCallback = object : DirectionsAdapter.OnItemClickListener{
                //Item Click Listener
                override fun onItemClick(position: Int) {
                    var intent = Intent(context,SearchActivity::class.java);
                    fragment.positionClicked = position
                    fragment.startActivityForResult(intent,0);
                }
                //Item Delete Listener
                override fun onItemDelete(position: Int) {
                    directionsAdapter.mPlacesList.removeAt(position)
                    directionsAdapter.notifyDataSetChanged()
                    fragment.directionsFragmentCallback?.onLocationChanged(directionsAdapter.mPlacesList,fragment)
                }

            }
            fragment.directionsAdapter = directionsAdapter
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Receive result successful
        if(resultCode == Activity.RESULT_OK){
            activity!!.findViewById<View>(R.id.progressBar).visibility = View.VISIBLE
            var placeID = data?.getStringExtra("PlaceID")
            var task =  Places.getGeoDataClient(activity!!,null).getPlaceById(placeID)
            task.addOnCompleteListener{ responseTask: Task<PlaceBufferResponse> ->
                if(responseTask.isSuccessful()){
                    if(responseTask.result[0] != null)
                    {
                        activity!!.findViewById<View>(R.id.progressBar).visibility = View.GONE
                        var myPlace = MyPlace()
                        myPlace.place = responseTask.result[0]
                        //if new position added
                        if(positionClicked == directionsAdapter.mPlacesList.size){
                            //add it to adapter and notify changes
                            directionsAdapter.mPlacesList.add(myPlace)
                            directionsAdapter.notifyDataSetChanged()
                            directionsFragmentCallback?.onLocationChanged(directionsAdapter.mPlacesList,this)
                        }
                        else //if old position is changed
                        {
                            //if the old place is different than new one
                            if(directionsAdapter.mPlacesList[positionClicked].place?.id != responseTask.result[0].id)
                            {
                                //replace the old one and notify changes
                                directionsAdapter.mPlacesList[positionClicked] = myPlace
                                directionsAdapter.notifyDataSetChanged()
                                directionsFragmentCallback?.onLocationChanged(directionsAdapter.mPlacesList,this)
                            }
                        }
                    }
                }
            }
        }
        else if(resultCode == Activity.RESULT_CANCELED){

        }
    }
}// Required empty public constructor
