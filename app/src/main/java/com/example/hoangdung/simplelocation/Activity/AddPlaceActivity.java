package com.example.hoangdung.simplelocation.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private TextView textViewAddress;


    private GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mLocationProvider;
    private GeoDataClient mGeoDataClient;

    private void initGoogleApiClient(){
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient
                    .Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        textViewAddress = (TextView)findViewById(R.id.textViewAddress);

        GoogleMap googleMap;
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        mGeoDataClient = Places.getGeoDataClient(this, null);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        initGoogleApiClient();
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK)
        {
            googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(19.649054, 102.590332))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_normal_marker)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(19.649054, 102.590332), 15));

            Task<PlaceBufferResponse> task = mGeoDataClient.getPlaceById("ChIJ6_n85kCuEmsR0eBW__dFWAQ");
            task.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    textViewAddress.setText(task.getResult().get(0).getAddress().toString());
                }
            });

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
}
