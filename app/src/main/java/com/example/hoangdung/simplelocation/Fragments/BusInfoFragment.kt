package com.example.hoangdung.simplelocation.Fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.hoangdung.simplelocation.R


/**
 * A simple [Fragment] subclass.
 */
class BusInfoFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_bus_info, container, false)
    }

}// Required empty public constructor
