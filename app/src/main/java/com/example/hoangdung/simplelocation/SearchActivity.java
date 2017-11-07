package com.example.hoangdung.simplelocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
        mSearchText.requestFocus();
        mSearchText.setCursorVisible(true);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.clearData();
                mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

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
            Address address = addressList.get(0);
            mCountryCode = address.getCountryCode();
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
