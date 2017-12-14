package com.example.hoangdung.simplelocation.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hoangdung.simplelocation.R;
import com.example.hoangdung.simplelocation.Activity.SearchActivity;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private AppCompatActivity mContext;
    public @BindView(R.id.toolbar_search)Toolbar mToolbar;
    public @BindView(R.id.find_direction)ImageView mImageView;
    private Unbinder mUnbinder;
    public GeoDataClient mGeoClient;
    private OnSearchFragmentCallback mSearchFragmentCallback;
    public SearchFragment() {
        // Required empty public constructor
    }
    public interface OnSearchFragmentCallback{
        void onSearchFragmentUIReady(SearchFragment searchFragment);
        void onSearchFragmentClicked(SearchFragment searchFragment);
        void onSearchFragmentResumed(SearchFragment searchFragment);
        void onSearchFragmentFindDirectionsClicked(SearchFragment searchFragment);
    }
    public void setOnSearchFragmentCallback(OnSearchFragmentCallback callback){
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
        Log.d("MapsActivity","SearchFragment:onCreate");
        mGeoClient = Places.getGeoDataClient(getActivity(),null);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MapsActivity","SearchFragment:onCreateView");
        View view  = inflater.inflate(R.layout.fragment_search,container,false);
        mUnbinder = ButterKnife.bind(this,view);
        mImageView.setVisibility(View.INVISIBLE);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mSearchFragmentCallback!=null)
            mSearchFragmentCallback.onSearchFragmentUIReady(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {

    }
    @Override
    public void onResume() {
        super.onResume();
        if(mSearchFragmentCallback != null)
            mSearchFragmentCallback.onSearchFragmentResumed(this);

    }


    @OnClick(R.id.find_direction)
    void onFindDirectionsClicked(View view){
        mSearchFragmentCallback.onSearchFragmentFindDirectionsClicked(this);
    }
    @OnClick(R.id.toolbar_search)
    void onSearchToolbarClicked(View view){
        mSearchFragmentCallback.onSearchFragmentClicked(this);
    }

    public void showFindDirections(boolean shouldShown){
        mImageView.setVisibility(shouldShown ? View.VISIBLE : View.INVISIBLE);
    }

    public void setTitle(CharSequence title){
        mToolbar.setTitle(title);
    }
}
