package com.example.hoangdung.simplelocation.Fragments


import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse

import com.example.hoangdung.simplelocation.R
import kotlinx.android.synthetic.main.driving_info_layout.*
import kotlinx.android.synthetic.main.fragment_info_tab.*


/**
 * A simple [Fragment] subclass.
 */
class InfoTabFragment : Fragment() {


    private val numOfTabs = 2;
    var drivingInfoFragment : DrivingInfoFragment? = null
    var busInfoFragment: BusInfoFragment? =null
    var drivingReponse: DirectionsResponse? = null
        set(value) {
            field = value
            //When reponse is change the InfoTabFragment UI needs also to be updated

            //Changing DrivingInfoFragment UI
            Log.d("MapsActivity",value?.routes?.get(0)?.legs?.get(0)?.distance?.text)
            Log.d("MapsActivity",value?.routes?.get(0)?.legs?.get(0)?.durationInTraffic?.text)
            drivingInfoFragment?.distanceText?.text = value?.routes?.get(0)?.legs?.get(0)?.distance?.text
            drivingInfoFragment?.durationText?.text = value?.routes?.get(0)?.legs?.get(0)?.durationInTraffic?.text
            drivingInfoFragment?.mAdapter?.directionResponse = field
        }
    var busReponse: DirectionsResponse? = null
        set(value) {
            field = value
            //When response is changed the InfoTabFragment UI needs also to be updated
            //Changing BusInfoFragment UI

        }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_info_tab, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout.addTab(tabLayout.newTab().setText("Driving"))
        tabLayout.addTab(tabLayout.newTab().setText("Bus"))
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager){
            val title = arrayOf("Driving","Bus")
            override fun getItem(position: Int): Fragment {
                //Driving Info Tab
                if(position==0)
                {
                    drivingInfoFragment = DrivingInfoFragment.newInstance(context)
                    return drivingInfoFragment!!
                }
                else if(position == 1) // Bus Info Tab
                {
                    busInfoFragment = BusInfoFragment()
                    return busInfoFragment!!
                }
                else
                    return Fragment()
            }

            override fun getCount(): Int {
                return numOfTabs
            }

            override fun getPageTitle(position: Int): CharSequence {
                return title[position]
            }
        }
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.d("Maps","onTabSelected:position" + tab?.position)
            }
        })
    }//onViewCreated


}// Required empty public constructor
