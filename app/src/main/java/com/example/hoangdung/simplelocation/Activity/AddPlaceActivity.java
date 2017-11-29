package com.example.hoangdung.simplelocation.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Fragments.InputPlaceLabelDialog;
import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        InputPlaceLabelDialog.DialogListener // implement what to do after input dialog ends
{
    private TextView textViewAddress;

    private FloatingActionButton floatingActionButtonAdd;

    private  FloatingActionButton floatingActionButtonSearch;

    private GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mLocationProvider;
    private GeoDataClient mGeoDataClient;
    private Place currentChosenPlace;

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
        floatingActionButtonAdd = (FloatingActionButton)findViewById(R.id.floating_btn_add);
        floatingActionButtonSearch= (FloatingActionButton)findViewById(R.id.floating_btn_search);




        textViewAddress.setVisibility(View.INVISIBLE);
        floatingActionButtonAdd.setVisibility(View.INVISIBLE);

        // fragment map
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        mGeoDataClient = Places.getGeoDataClient(this, null);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        initGoogleApiClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if user did choose a location
        if (requestCode == 0 && resultCode == RESULT_OK)
        {

            textViewAddress.setVisibility(View.VISIBLE);
            floatingActionButtonAdd.setVisibility(View.VISIBLE);


            // get place information
            Task<PlaceBufferResponse> task = mGeoDataClient.getPlaceById(data.getStringExtra("PlaceID"));
            task.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    currentChosenPlace = task.getResult().get(0);

                    // show address
                    textViewAddress.setText(currentChosenPlace.getAddress().toString());

                    // locate address on map
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions()
                    .position(currentChosenPlace.getLatLng())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_normal_marker)));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentChosenPlace.getLatLng(), 15));
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

    public void onClickAddplaceSearchButton(View view) {
        // let user choose location
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onClickAddplaceAddButton(View view)
    {
        Bundle bundle = new Bundle();
        bundle.putString("placeName", currentChosenPlace.getName().toString());

        // create and show input dialog
        DialogFragment dialog = new InputPlaceLabelDialog();
        dialog.setArguments(bundle); // send place name for dialog to set to edit text
        dialog.show(getFragmentManager(), "InputPlaceLabelDialog1");
    }

    @Override
    public void onDialogPositiveClick(String input)
    {
        // add new place to my place list in database
        FirebaseCenter.getInstance().addPlace(FirebaseCenter.getInstance().getUserID(), new FirebaseCenter.Location(currentChosenPlace.getId(),
                input, currentChosenPlace.getAddress().toString(), currentChosenPlace.getLatLng().latitude,
                currentChosenPlace.getLatLng().longitude), new DatabaseReference.CompletionListener()
        {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
            {
                if (databaseError != null)
                    Toast.makeText(AddPlaceActivity.this, "Could not add to place list", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(AddPlaceActivity.this, "Add to place list successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
