package com.example.hoangdung.simplelocation;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private AppCompatActivity mContext;
    public @BindView(R.id.toolbar_search)Toolbar mToolbar;
    //public @BindView(R.id.search_textview) TextView mSearchTextView;
    private Unbinder mUnbinder;
    public Place mSearchPlace;
    public GeoDataClient mGeoClient;
    public Menu mMenu;
    private OnSearchFragmentCallback mSearchFragmentCallback;

    public SearchFragment() {
        // Required empty public constructor
    }
    public interface OnSearchFragmentCallback{
        void onSearchFragmentUIReady(SearchFragment searchFragment);
        void onSearchFragmentClicked(SearchFragment searchFragment);
        void onSearchFragmentResumed(SearchFragment searchFragment);
        void onSearchResult(Place searchPlace, SearchFragment searchFragment);
    }
    void setOnSearchFragmentCallback(OnSearchFragmentCallback callback){
        mSearchFragmentCallback = callback;
    }
    public static SearchFragment newInstance(AppCompatActivity context) {
        SearchFragment fragment = new SearchFragment();
        fragment.mContext = context;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeoClient = Places.getGeoDataClient(getActivity(),null);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_search,container,false);
        mUnbinder = ButterKnife.bind(this,view);
        mToolbar.inflateMenu(R.menu.search_toolbar_menu);
        mMenu = mToolbar.getMenu();
        mMenu.findItem(R.id.find_direction).setVisible(false);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    mSearchFragmentCallback.onSearchFragmentClicked(SearchFragment.this);
                }
                catch (Exception e){

                }
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchFragmentCallback.onSearchFragmentUIReady(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MapsActivity","SearchFragment:onActivityResult");
        if(data!=null)
        {
            String placeID = data.getStringExtra("PlaceID");
            Task<PlaceBufferResponse> task = mGeoClient.getPlaceById(placeID);
            task.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if(task.isSuccessful() && task.getResult()!= null){
                        Log.d("MapsActivity","Place complete");
                        mSearchPlace = task.getResult().get(0).freeze();
                        mSearchFragmentCallback.onSearchResult(mSearchPlace,SearchFragment.this);
                        task.getResult().release();
                        mMenu.findItem(R.id.find_direction).setVisible(true);
                    }
                }
            });
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mSearchFragmentCallback!=null)
            mSearchFragmentCallback.onSearchFragmentResumed(this);
    }

    public void startSearching(){
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivityForResult(intent,1);
    }
    /* public void setText(String text){
            mSearchTextView.setText(text);
        }*/
}
