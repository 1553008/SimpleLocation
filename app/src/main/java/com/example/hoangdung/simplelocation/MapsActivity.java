package com.example.hoangdung.simplelocation;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Line;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SearchFragment.SearchFragmentCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //UI component
    private String TAG = MapsActivity.class.getSimpleName();

    private int DEFAULT_ZOOM = 15;

    private String mEmail;

    private String mName;

    private String mProfileIconURL;

    private SearchFragment mSearchFragment;

    @BindView(R.id.toolbar_container)
    public FrameLayout mToolbarContainer;

    private Drawer mDrawer;

    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @BindView(R.id.floating_btn)
    public FloatingActionButton mLocateBtn;
    //Google Map model
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    //Fused Location Provider
    private FusedLocationProviderClient mLocationProvider;

    //Last known location
    private Location mLastknownLocation;

    //Activity Request Code
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //-----------------------------------------Activity LifeCycles---------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initGoogleApiClient();
        drawerLayoutSetup();
        toolbarSetup();
        locateButtonSetup();
    }//onCreate

    @Override
    protected void onStart() {
        super.onStart();
    }//OnStart

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }//OnPause

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            handleSearchResult(resultCode,data);
        }
    }

    //----------------------------------------Google API Setup and Callbacks---------------------------------------------------------
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"GoogleApiClient:connected");
        showLastknownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"GoogleApiClient:suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"GoogleApiClient:connect failure");
        Toast.makeText(this, "Can't connect to Google Api Client", Toast.LENGTH_SHORT).show();
    }

    //----------------------------------------Google Maps Callbacks and Functionality------------------------------------
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapUI();
        showLastknownLocation();
    }//onMapReady
    /**
     * Get the Last Know Location of the user (maybe the current location)
     */
    private void showLastknownLocation() {
        try{
            Task<Location> lastLocation = mLocationProvider.getLastLocation();
            lastLocation.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isComplete() && task.isSuccessful() && task.getResult()!=null) {
                        mLastknownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(
                                        new LatLng(mLastknownLocation.getLatitude(), mLastknownLocation.getLongitude())
                                        , DEFAULT_ZOOM));
                    }
                }
            });
        }
        catch (SecurityException e){

        }

    }// showLastknowLocation

    private void updateMapUI() {
        try{
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        catch (SecurityException e){

        }
    } //updateMapUI



    //--------------------------------------------Activity UI Setup--------------------------------------------
    /**
     * Setup Drawer Layout
     */
    private void drawerLayoutSetup(){
        initUserInfoHeader();
        //Account header
        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .addProfiles(
                        new ProfileDrawerItem().withEmail(mEmail).withName(mName).withIcon(mProfileIconURL)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withHeaderBackground(R.drawable.drawerlayoutbackground)
                .build();
        //Drawer setup
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                //.withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withFullscreen(true)
                .build();
        if(Build.VERSION.SDK_INT >= 19){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        if(Build.VERSION.SDK_INT >= 21){
            setWindowFlags(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if(Build.VERSION.SDK_INT >= 19){
            mDrawer.getDrawerLayout().setFitsSystemWindows(false);
        }
    }

    /**
     * Get Facebook User Profile in SharedPreference
     */
    private void initUserInfoHeader(){
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",0);
        mEmail = sharedPreferences.getString("email","");
        mName = sharedPreferences.getString("first_name","") + sharedPreferences.getString("last_name","");
        mProfileIconURL = sharedPreferences.getString("picture","");
    }

    /**
     * Setup Search Fragment
     */
    private void toolbarSetup(){
        SearchFragment searchFragment = SearchFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.toolbar_container,searchFragment);
        fragmentTransaction.commit();
        mSearchFragment = searchFragment;
    }

    private void setWindowFlags(Activity activity, final int bits, boolean on){
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if(on){
            winParams.flags |= bits;
        }
        else{
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void locateButtonSetup(){
        //mLocateBtn = findViewById(R.id.floating_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastknownLocation();
            }
        });
    }

    //------------------------------------------Search Fragment Callbacks and Search Functionality--------------------------------------------
    @Override
    public void onSearchFragmentViewCreated() {
        mDrawer.setToolbar(this,mSearchFragment.mToolbar);
    }

    @Override
    public void onSearchBarClicked() {
        startSearchActivity();
    }

    @Override
    public void onSearchFragmentDestroy() {

    }

    @Override
    public void onSearchFragmentResume() {

    }

    @Override
    public void onSearchFragmentPause() {

    }

    @Override
    public void onSearchFragmentStart() {

    }

    @Override
    public void onSearchFragmentStop() {

    }
    private void startSearchActivity(){
        try {
            //Search Filter

            //Start Activity
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            Log.d(TAG,e.getMessage());
        }
    }
    private void handleSearchResult(int resultCode,Intent data){
        if(resultCode == RESULT_OK){
            Log.d(TAG,"PlaceAutoComplete:success");
        }
        else if(resultCode == PlaceAutocomplete.RESULT_ERROR){
            Log.d(TAG,"PlaceAutoComplete:error");
        }
        else if(resultCode == RESULT_CANCELED){
            Log.d(TAG,"PlaceAutoComplete:canceled");
        }
    }



}
