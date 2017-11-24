package com.example.hoangdung.simplelocation.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.hoangdung.simplelocation.Adapter.PlaceAutoCompleteAdapter;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlaceAutoCompleteAdapter.OnPlaceClickCallback{

    @BindView(R.id.search_editText)
    public EditText mSearchText;
    @BindView(R.id.recycler_view)
    public RecyclerView mListView;
    @BindView(R.id.search_toolbar)
    public android.support.v7.widget.Toolbar mToolbar;
    private GeoDataClient mGeoDataClient;
    private PlaceAutoCompleteAdapter mAdapter;
    private String mCountryCode;

    //Search Edit text auto complete
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    private Runnable input_finish_checker;
    private Editable editable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        new GoogleApiClient.Builder(this,this,this).addApi(Places.GEO_DATA_API);
        mGeoDataClient = Places.getGeoDataClient(this,null);
        mAdapter = new PlaceAutoCompleteAdapter(this,mGeoDataClient);

        mAdapter.setOnPlaceClickCallback(this);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setHasFixedSize(true);
        mListView.setAdapter(mAdapter);

        //Search Edit Text Setup
        input_finish_checker = new Runnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() > (last_text_edit + delay - 500)){
                    mAdapter.clearData();
                    mAdapter.getFilter().filter(editable);
                }
            }
        };
        mSearchText.requestFocus();
        mSearchText.setCursorVisible(true);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(input_finish_checker);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker,delay);
                    editable = s;
                }
            }
        });
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getCountryCode();
        mAdapter.mCountryCode = mCountryCode;
    }
    private void getCountryCode(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",0);
            double lat = Double.longBitsToDouble(sharedPreferences.getLong("LastKnownLocationLat",0)) ;
            double lng = Double.longBitsToDouble(sharedPreferences.getLong("LastKnownLocationLng",0)) ;
            List<Address> addressList = geocoder.getFromLocation(lat,lng,1);
            if(addressList.size() > 0){
                Address address = addressList.get(0);
                mCountryCode = address.getCountryCode();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceClick(final ArrayList<AutocompletePrediction> predictPlaces, int position) {
        String placeID = predictPlaces.get(position).getPlaceId();
        Intent data = new Intent();
        data.putExtra("PlaceID",placeID);
        setResult(RESULT_OK,data);
        finish();
    }

}
