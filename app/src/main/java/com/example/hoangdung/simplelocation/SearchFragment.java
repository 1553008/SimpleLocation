package com.example.hoangdung.simplelocation;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private GoogleMap mGoogleMap;
    private AppCompatActivity mActivity;
    private String mSearchString;
    private LatLng mSearchLocation;
    private Toolbar mToolbar;
    private Drawer mDrawer;
    private final int defaultZoom = 15;
    public void setmSearchString(String mSearchString) {
        this.mSearchString = mSearchString;
    }

    public void setmSearchLocation(LatLng mSearchLocation) {
        this.mSearchLocation = mSearchLocation;
    }

    public String getmSearchString() {

        return mSearchString;
    }

    public LatLng getmSearchLocation() {
        return mSearchLocation;
    }

    public SearchFragment() {
        // Required empty public constructor
    }



    public static SearchFragment newInstance(GoogleMap googleMap, AppCompatActivity activity,Drawer drawer) {
        SearchFragment fragment = new SearchFragment();
        fragment.mGoogleMap = googleMap;
        fragment.mActivity = activity;
        fragment.mDrawer = drawer;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mToolbar = (Toolbar) inflater.inflate(R.layout.fragment_search, container, false);
        if(savedInstanceState!=null)
            restoreSate(savedInstanceState);
        return mToolbar;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            restoreSate(savedInstanceState);
        }
    }
    private void saveState(Bundle outState){
        outState.putString("SearchString",mSearchString);
        outState.putDouble("SearchLocationLat",mSearchLocation.latitude);
        outState.putDouble("SearchLocationLng",mSearchLocation.longitude);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void restoreSate(Bundle saveInstanceState){
        mSearchString = saveInstanceState.getString("SearchString");
        double Lat = saveInstanceState.getDouble("SearchLocationLat");
        double Lng = saveInstanceState.getDouble("SearchLocationLng");
        mSearchLocation = new LatLng(Lat,Lng);
        updateUI();
    }
    private void updateUI(){
        if(mSearchLocation == null){
            mToolbar.setTitle("");
            mGoogleMap.clear();
        }
        else{
            mToolbar.setTitle(mSearchString);
            mGoogleMap.addMarker(new MarkerOptions().position(mSearchLocation));
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSearchLocation,defaultZoom));
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDrawer.setToolbar(mActivity,mToolbar);
    }
}
