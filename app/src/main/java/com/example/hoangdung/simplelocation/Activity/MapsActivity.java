package com.example.hoangdung.simplelocation.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.Fragments.DirectionsFragment;
import com.example.hoangdung.simplelocation.Fragments.InfoTabFragment;
import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Leg;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Route;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.GoogleDirectionsQuery;
import com.example.hoangdung.simplelocation.MyPlace;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.PolyUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    //UI component
    private String TAG = MapsActivity.class.getSimpleName();

    private int DEFAULT_ZOOM = 15;

    private String mEmail;

    private String mName;

    private String mProfileIconURL;

    private SearchFragment mSearchFragmentHolder;

    private FragmentManager mFragmentManager;
    @BindView(R.id.toolbar_container)
    public FrameLayout mToolbarContainer;

    @BindView(R.id.progressBar)
    public ProgressBar mProgressBar;
    private Drawer mDrawer;

    private DirectionsResponse drivingReponse;
    private DirectionsResponse busResponse;
    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @BindView(R.id.floating_btn)
    public FloatingActionButton mLocateBtn;
    //Google Map model
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String mCountryCode;
    //Fused Location Provider
    private FusedLocationProviderClient mLocationProvider;

    //Last known location
    private Location mLastknownLocation;

    //Choose places on Maps

    private ArrayList<LatLng> mMarkerPlaces = new ArrayList<>();
    //Activity Request Code
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    //-----------------------------------------Activity LifeCycles---------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.map,mapFragment);
        fragmentTransaction.commit();
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
        super.onActivityResult(requestCode,resultCode,data);
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

    private void getCountryCode(){
        Geocoder geocoder = new Geocoder(this,Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude(),1);
            Address address = addressList.get(0);
            mCountryCode = address.getCountryCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        getCountryCode();
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(
                                        new LatLng(mLastknownLocation.getLatitude(), mLastknownLocation.getLongitude())
                                        , DEFAULT_ZOOM));
                        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putLong("LastKnownLocationLat",Double.doubleToRawLongBits(mLastknownLocation.getLatitude()));
                        editor.putLong("LastKnownLocationLng",Double.doubleToRawLongBits(mLastknownLocation.getLongitude()));
                        editor.commit();
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

        // construct draw item
        PrimaryDrawerItem myPlaceItem= new PrimaryDrawerItem()
                .withIdentifier(this.getResources().getInteger(R.integer.my_place_drawer_item_id))
                .withName("My places");

        //Drawer setup
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                //.withToolbar(toolbar)
                .addDrawerItems(myPlaceItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == MapsActivity.this.getResources().getInteger(R.integer.my_place_drawer_item_id))
                        {
                            // TODO: Fire activity Choose My Plases
                        }
                        return true;
                    }
                })
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
        //mLocateBtn = findViewBId(R.id.floating_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastknownLocation();
            }
        });
    }

    //------------------------------------------Search Fragment Callbacks and Search Functionality--------------------------------------------
    //

    /**
     * Initiates Search Fragment Holder, it is actually a Search Fragment but with no state
     * This implementation works, but it is somehow not readable
     * In future I will reimplement this by creating new Holder Fragment acting as a toolbar
     */
    private void toolbarSetup(){
        SearchFragment searchFragmentHolder = SearchFragment.newInstance(this);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.toolbar_container,searchFragmentHolder,SearchFragment.class.getSimpleName());
        fragmentTransaction.commit();
        mSearchFragmentHolder = searchFragmentHolder;
        searchFragmentHolder.setOnSearchFragmentCallback(new SearchFragment.OnSearchFragmentCallback() {
            @Override
            public void onSearchFragmentUIReady(SearchFragment searchFragment) {
                mDrawer.setToolbar(MapsActivity.this,searchFragment.mToolbar);
            }

            @Override
            public void onSearchFragmentClicked(SearchFragment searchFragment) {
                initRealSearchFragment();
            }
            @Override
            public void onSearchFragmentResumed(SearchFragment searchFragment) {
                if(mMap!=null)
                {
                    mMap.clear();
                    showLastknownLocation();
                    mMarkerPlaces.clear();
                }
                mDrawer.setToolbar(MapsActivity.this,searchFragment.mToolbar,true);

            }
            @Override
            public void onSearchFragmentFindDirectionsClicked(SearchFragment searchFragment) {

            }
        });
    }

    /**
     * Create Search Fragment and replace the Holder with this
     */
    private void initRealSearchFragment(){
        SearchFragment realSearchFragment = SearchFragment.newInstance(MapsActivity.this);
        realSearchFragment.setOnSearchFragmentCallback(new SearchFragment.OnSearchFragmentCallback() {
            @Override
            public void onSearchFragmentUIReady(SearchFragment searchFragment) {
                mDrawer.setToolbar(MapsActivity.this,searchFragment.mToolbar,true);
                if(searchFragment.mSearchPlace==null)
                    searchFragment.mToolbar.callOnClick();
            }

            @Override
            public void onSearchFragmentClicked(SearchFragment searchFragment) {
                searchFragment.startSearching();
            }

            @Override
            public void onSearchFragmentResumed(SearchFragment searchFragment) {
                if(mMap!=null){
                    mMap.clear();
                }
                mDrawer.setToolbar(MapsActivity.this,searchFragment.mToolbar,true);
                if(searchFragment.mSearchPlace!= null){
                    handleSearchResult(searchFragment.mSearchPlace,searchFragment,RESULT_OK);
                }
            }
            @Override
            public void onSearchFragmentFindDirectionsClicked(SearchFragment searchFragment) {
                startDirectionsFragment(searchFragment);
            }
        });
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.toolbar_container,realSearchFragment,SearchFragment.class.getSimpleName());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    private void handleSearchResult(final Place searchPlace, final SearchFragment searchFragment, int resultCode){
        if(resultCode == RESULT_OK){
            if(searchPlace!=null){
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(searchPlace.getLatLng()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchPlace.getLatLng(), DEFAULT_ZOOM));

                    }
                },50);
                searchFragment.mToolbar.setTitle(searchPlace.getName());
                searchFragment.mImageView.setVisibility(View.VISIBLE);
            }
        }
        else if( resultCode == RESULT_CANCELED){
            if(searchPlace==null)
                getSupportFragmentManager().popBackStack();
        }
    }

    private void addMarkers(List<LatLng> markers){
        if(markers == null)
            return;
        for(LatLng marker : markers){
            mMap.addMarker(new MarkerOptions().position(new LatLng(marker.latitude,marker.longitude)));
        }
    }
    //----------------------------------------Directions Functionality--------------------------------------

    private void startDirectionsFragment(SearchFragment searchFragment){
        Log.d("MapsActivity","startDirectionsFragment");

        MyPlace firstLocation = new MyPlace();
        firstLocation.setLatlng(new LatLng(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude()));
        firstLocation.setFullName("Your location");

        MyPlace secondLocation = new MyPlace();
        secondLocation.setPlace(searchFragment.mSearchPlace);
        final InfoTabFragment infoTabFragment = new InfoTabFragment();
        DirectionsFragment directionsFragment = DirectionsFragment.Companion.newInstance(this,firstLocation,secondLocation);
        directionsFragment.setDirectionsFragmentCallback(new DirectionsFragment.DirectionsFragmentCallback() {
            @Override
            public void onTabChanged(int position) {

            }

            @Override
            public void onDirectionsFragmentUIReady(@NotNull DirectionsFragment directionsFragment) {
                setSupportActionBar(directionsFragment.toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("");
            }

            @Override
            public void onLocationChanged(@NotNull ArrayList<MyPlace> locationList, @NotNull DirectionsFragment directionsFragment) {
                Log.d("MapsActivity","onLocationChanged");

                //Query Driving Mode
                GoogleDirectionsQuery drivingQuery = getDirectionsQuery(locationList,directionsFragment, GoogleDirectionsQuery.TRAVEL_MODE.DRIVING);
                drivingQuery.query(new GoogleDirectionsQuery.OnDirectionsResultListener() {
                    @Override
                    public void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode) {
                        Log.d("MapsActivity","onDirectionsResult");
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(resultCode == GoogleDirectionsQuery.RESPONSE_SUCCESS){
                            //Save the response
                            drivingReponse = directionsResponse;
                            infoTabFragment.setDrivingReponse(drivingReponse);
                            updateUIFromDirectionsResponse(0);

                        }
                        else if( resultCode == GoogleDirectionsQuery.RESPONSE_FAILURE){
                            Toast.makeText(MapsActivity.this,"Something wrong, Try again later!",Toast.LENGTH_LONG).show();
                        }
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                });

                //Query Bus Mode
                GoogleDirectionsQuery busQuery = getDirectionsQuery(locationList,directionsFragment, GoogleDirectionsQuery.TRAVEL_MODE.TRANSIT);
                busQuery.query(new GoogleDirectionsQuery.OnDirectionsResultListener() {
                    @Override
                    public void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode) {
                        Log.d("MapsActivity","onDirectionsResult");
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(resultCode == GoogleDirectionsQuery.RESPONSE_SUCCESS){
                            //Save the response
                            busResponse = directionsResponse;
                            infoTabFragment.setBusReponse(busResponse);
                            //updateUIFromDirectionsResponse(0);
                        }
                        else if( resultCode == GoogleDirectionsQuery.RESPONSE_FAILURE){
                            Toast.makeText(MapsActivity.this,"Something wrong, Try again later!",Toast.LENGTH_LONG).show();
                        }
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                });


            }
        });

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.toolbar_container,directionsFragment,directionsFragment.getClass().getSimpleName());
        fragmentTransaction.add(R.id.below_toolbar_container,infoTabFragment,infoTabFragment.getClass().getSimpleName());
        fragmentTransaction.addToBackStack(directionsFragment.getClass().getSimpleName());
        fragmentTransaction.commit();
    }
    private GoogleDirectionsQuery getDirectionsQuery(@NotNull ArrayList<MyPlace> locationList, @NotNull final DirectionsFragment directionsFragment, final GoogleDirectionsQuery.TRAVEL_MODE travelMode){
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        final LatLng origin;
        final LatLng destination;
        ArrayList<LatLng> waypoints = new ArrayList<>();
        //if it only contains the LatLng
        if(locationList.get(0).getPlace() == null)
            origin = locationList.get(0).getLatlng();
        else // if contains Place
            origin = locationList.get(0).getPlace().getLatLng();

        if(locationList.get(locationList.size()-1).getPlace() == null)
            destination = locationList.get(locationList.size()-1).getLatlng();
        else
            destination = locationList.get(locationList.size()-1).getPlace().getLatLng();

        for(int i = 1 ; i < locationList.size()-2 ; ++i){
            if(locationList.get(i).getPlace() == null)
                waypoints.add(locationList.get(i).getLatlng());
            else
                waypoints.add(locationList.get(i).getPlace().getLatLng());
        }

        //Create Request to get directionsResponse
        GoogleDirectionsQuery mDirectionsQuery = new GoogleDirectionsQuery
                .Builder()
                .withOrigin(origin)
                .withDestination(destination)
                .withTravelMode(GoogleDirectionsQuery.TRAVEL_MODE.DRIVING)
                .withWaypoints(waypoints)
                .buid();
        return mDirectionsQuery;

    }
    private void updateUIFromDirectionsResponse(int curTabPosition){
        //If the current tab belongs to driving mode

        mMap.clear();
        DirectionsResponse response = null;
        if(curTabPosition == 0)
            response = drivingReponse;
        else if(curTabPosition == 1)
            response = busResponse;

        for (Route route: response.getRoutes()){
            List<LatLng> polyline = PolyUtil.decode(route.getOverviewPolyline().getPoints());
            mMap.addPolyline(new PolylineOptions()
                    .addAll(polyline)
                    .geodesic(true));
        }
    }
}