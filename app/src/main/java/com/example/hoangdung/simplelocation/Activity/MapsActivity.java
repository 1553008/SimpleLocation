package com.example.hoangdung.simplelocation.Activity;

import android.app.Activity;
import android.content.Context;
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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Fragments.DirectionsFragment;
import com.example.hoangdung.simplelocation.Fragments.InfoTabFragment;
import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Leg;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Route;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.GoogleDirectionsQuery;
import com.example.hoangdung.simplelocation.MyApplication;
import com.example.hoangdung.simplelocation.MyPlace;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
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
import java.util.Arrays;
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

    private SearchFragment mSearchFragment;
    private DirectionsFragment mDirectionFragment;

    private FragmentManager mFragmentManager;
    @BindView(R.id.toolbar_container)
    public FrameLayout mToolbarContainer;

    @BindView(R.id.progressBar)
    public ProgressBar mProgressBar;
    private Drawer mDrawer;

    private DirectionsResponse drivingReponse;
    private DirectionsResponse busResponse;

    private Context mContext;

    @BindView(R.id.floating_btn)
    public FloatingActionButton mLocateBtn;

    //My Place Search
    Place mSearchPlace;
    //Google Map model
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    //Fused Location Provider
    private FusedLocationProviderClient mLocationProvider;

    //Last known location
    private Location mLastknownLocation;
    private GeoDataClient mGeoDataClient;
    //Choose places on Maps

    private ArrayList<LatLng> mMarkerPlaces = new ArrayList<>();
    //Activity Request Code
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private int FOOD_FINDER_REQUEST_CODE = 2;
    private int SEARCH_ACTIVITY_REQUEST_CODE = 3;
    private  boolean returnedFromMyPlaceList = false;
    private FirebaseCenter.Location chosenPlaceFromMyPlaceList;
    //-----------------------------------------Activity LifeCycles---------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        mContext = getApplicationContext();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.map);
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

        // if user chooses a place in MyPlacesActivity
        if (resultCode == RESULT_OK && requestCode == 0)
        {
            if (data.hasExtra("chosenPlace"))
            {
                // these info will be used in onsearchFragmentResumed() to show directions
                chosenPlaceFromMyPlaceList = FirebaseCenter.getInstance().getMyPlaces().get(data.getIntExtra("chosenPlace", 0));
                returnedFromMyPlaceList = true;
            }
        }
        // if the result is from search activity
        if(requestCode == SEARCH_ACTIVITY_REQUEST_CODE){
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
        mGeoDataClient = Places.getGeoDataClient(this,null);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"GoogleApiClient:connected");
        //showLastknownLocation();
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
                        mMap.animateCamera(CameraUpdateFactory
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

        // construct my places item
        PrimaryDrawerItem myPlaceItem= new PrimaryDrawerItem()
                .withIdentifier(this.getResources().getInteger(R.integer.my_place_drawer_item_id))
                .withName("My places");

        // construct i'm hungry item
        PrimaryDrawerItem hungryItem = new PrimaryDrawerItem()
                .withIdentifier(this.getResources().getInteger(R.integer.hungry_drawer_item_id))
                .withName("I'm Hungry");
        //Drawer setup
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                //.withToolbar(toolbar)
                .addDrawerItems(myPlaceItem,hungryItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == MapsActivity.this.getResources().getInteger(R.integer.my_place_drawer_item_id))
                        {
                            // Fire activity Choose My Plases and show my place list, which
                            // is gotten from firebaseCenter

                            final Intent intent = new Intent(MapsActivity.this, MyPlacesActivity.class);
                            ArrayList<FirebaseCenter.Location> locList = FirebaseCenter.getInstance().getMyPlaces();
                            intent.putParcelableArrayListExtra("place", locList);
                            mDrawer.closeDrawer();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivityForResult(intent, 0);
                                }
                            },100);
                        }
                        else if(position == MapsActivity.this.getResources().getInteger(R.integer.hungry_drawer_item_id)){
                            final Intent intent = new Intent(MapsActivity.this,FoodFinderActivity.class);
                            mDrawer.closeDrawer();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivityForResult(intent,FOOD_FINDER_REQUEST_CODE);
                                }
                            },100);
                        }
                        return true;
                    }
                })
                .withAccountHeader(accountHeader)
                .withFullscreen(true)
                .build();
        /*if(Build.VERSION.SDK_INT >= 19){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        if(Build.VERSION.SDK_INT >= 21){
            setWindowFlags(this,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if(Build.VERSION.SDK_INT >= 19){
            mDrawer.getDrawerLayout().setFitsSystemWindows(false);
        }*/
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

    private void locateButtonSetup(){
        //mLocateBtn = findViewBId(R.id.floating_btn);
        mLocateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLastknownLocation();
            }
        });
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mLocateBtn.getLayoutParams();
        params.bottomMargin = (int)getApplicationContext().getResources().getDimension(R.dimen.myplaceButtonMarginBottom);
        params.rightMargin = (int)getApplicationContext().getResources().getDimension(R.dimen.myplaceButtonMarginRight);
    }

    @Override
    public void onBackPressed() {
        //When Directions Fragment is visible, return to search fragment and updateUI
        if(mDirectionFragment != null && mDirectionFragment.isVisible())
        {
            super.onBackPressed();
            mMap.clear();
            if(mSearchPlace!=null){
                addMarkers(Arrays.asList(mSearchPlace.getLatLng()),R.drawable.ic_normal_marker);
                updateCamera(Arrays.asList(mSearchPlace.getLatLng()));
            }
            updateSearchToolbar();
            return;
        }
        //Because when user search for a place successully, we allow them to press back button to return to current location
        if(mSearchPlace != null){
            mSearchPlace = null;
            mMap.clear();
            showLastknownLocation();
            updateSearchToolbar();
        }
        else
            super.onBackPressed();
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
        mSearchFragment = searchFragmentHolder;
        searchFragmentHolder.setOnSearchFragmentCallback(new SearchFragment.OnSearchFragmentCallback() {

            //Set SearchFragment's toolbar as Activity's toolbar
            @Override
            public void onSearchFragmentUIReady(SearchFragment searchFragment) {
                updateSearchToolbar();
            }
            //Start Search Activity
            @Override
            public void onSearchFragmentClicked(SearchFragment searchFragment) {
                Intent intent = new Intent(MapsActivity.this,SearchActivity.class);
                startActivityForResult(intent,SEARCH_ACTIVITY_REQUEST_CODE);
            }
            //1.Automatically Update UI when SearchFragment comebacks to displayoolbar
            //2.Start Directions Fragment When users back from MyPlaceActivity
            @Override
            public void onSearchFragmentResumed(SearchFragment searchFragment) {
                if(mMap!=null)
                {

                    // if return from myplacesActivity
                    if (returnedFromMyPlaceList)
                    {
                        returnedFromMyPlaceList = false;

                        // current location
                        MyPlace firstLocation = new MyPlace();
                        firstLocation.setLatlng(new LatLng(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude()));
                        firstLocation.setFullName("Your location");

                        // chosen location
                        MyPlace secondLocation = new MyPlace();
                        secondLocation.setLatlng(new LatLng(chosenPlaceFromMyPlaceList.lat, chosenPlaceFromMyPlaceList.lng));
                        secondLocation.setFullName(chosenPlaceFromMyPlaceList.address);

                        // start find direction
                        startDirectionsFragment(firstLocation,secondLocation);
                    }
                    else
                    {
                        showLastknownLocation();
                        mMarkerPlaces.clear();
                    }


                    Log.d(TAG, "onSearchFragmentResumed");
                    Log.d(TAG, "showLastKnowLocation from onsearchFragmentResumed");
                }
            }
            /**
             * This callback will be called when Find Direction Icon is clicked
             * It will create DirectionsFragment
             * We need to provide two locations first, which is represented by {@link MyPlace MyPlace}
             * @param searchFragment
             */
            @Override
            public void onSearchFragmentFindDirectionsClicked(SearchFragment searchFragment) {
                MyPlace firstLocation = new MyPlace();
                firstLocation.setLatlng(new LatLng(mLastknownLocation.getLatitude(),mLastknownLocation.getLongitude()));
                firstLocation.setFullName("Your location");
                MyPlace secondLocation = new MyPlace();
                secondLocation.setPlace(mSearchPlace);
                startDirectionsFragment(firstLocation,secondLocation);
            }

        });
    }



    private void handleSearchResult(int resultCode, Intent data){
        //if users choose one place
        if(resultCode == RESULT_OK){
            //Use the placeID passed from Search Activity to query search place
            final String placeID = data.getStringExtra("PlaceID");
            Task<PlaceBufferResponse> task = mGeoDataClient.getPlaceById(placeID);
            task.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    //If place is received, save the data. Also add markers to map, and update toolbar
                    if(task.isSuccessful() && task.getResult()!= null){

                        //Get place result, copy it and release buffer
                        mSearchPlace = task.getResult().get(0).freeze();
                        task.getResult().release();
                        //Update camera
                        mMap.clear();
                        addMarkers(Arrays.asList(mSearchPlace.getLatLng()),R.drawable.ic_normal_marker);
                        updateCamera(Arrays.asList(mSearchPlace.getLatLng()));
                        //Update search toolbar
                        updateSearchToolbar();
                    }
                }
            });
        }
        else if(resultCode == RESULT_CANCELED){

        }


    }

    private void updateSearchToolbar(){
        if(mSearchPlace == null){
            mSearchFragment.setTitle("Search here");
            mSearchFragment.showFindDirections(false);
        }
        else{
            mSearchFragment.setTitle(mSearchPlace.getName());
            mSearchFragment.showFindDirections(true);
        }
        mDrawer.setToolbar(this,mSearchFragment.mToolbar,true);
    }

    /**
     * This function update camera based on List of LatLngs
     * @param latLngs array list of LatLngs, which will be used to move camera over specific bound area
     */
    private void updateCamera(List<LatLng> latLngs){
        if(latLngs == null || latLngs.size() == 0)
            return;

        final LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
        for(LatLng latLng : latLngs){
            boundBuilder.include(latLng);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),100));
            }
        },50);
    }

    /**
     * This function add markers on the map
     * @param markers array list of LatLng, which will be used to display markers
     * @param drawable drawable icon
     */
    private void addMarkers(List<LatLng> markers,int drawable){
        if(markers == null)
            return;
        for(LatLng marker : markers){
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marker.latitude,marker.longitude))
                    .icon(BitmapDescriptorFactory.fromResource(drawable)));
        }
    }
    //----------------------------------------Directions Functionality--------------------------------------

    /**
     * When the user clicks find directions icon from the search fragment, this function will be called
     * This will replace the search fragment with direction fragment
     * Using place categories of search fragment as destination
     * Initialize neccessary listeners to observe changes in searching lists and tab changed
     */
    private void startDirectionsFragment(@NotNull MyPlace firstLocation, @NonNull MyPlace secondLocation){
        Log.d("MapsActivity","startDirectionsFragment");
        //Create InfoTabFragment and add listener for changes
        final InfoTabFragment infoTabFragment = new InfoTabFragment();
        infoTabFragment.setListeners(new InfoTabFragment.OnTabListener() {
            @Override
            public void onTabChanged(int position, DirectionsResponse response) {
                updateUIFromDirectionsResponse(position,response);
            }
        });
        //Create Directions Fragment and add listener for changes
        DirectionsFragment directionsFragment = DirectionsFragment.Companion.newInstance(this,firstLocation,secondLocation);
        mDirectionFragment = directionsFragment;
        directionsFragment.setDirectionsFragmentCallback(new DirectionsFragment.DirectionsFragmentCallback() {
            //When the fragment is ready
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
                GoogleDirectionsQuery drivingQuery = getDirectionsQuery(locationList, GoogleDirectionsQuery.TRAVEL_MODE.DRIVING);
                drivingQuery.query(new GoogleDirectionsQuery.OnDirectionsResultListener() {
                    @Override
                    public void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode) {
                        Log.d("MapsActivity","onDirectionsResult");
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(resultCode == GoogleDirectionsQuery.RESPONSE_SUCCESS){
                            //Save the response
                            drivingReponse = directionsResponse;
                            infoTabFragment.setDrivingReponse(drivingReponse);
                            if(infoTabFragment.getCurTabs() == 0)
                            {
                                updateUIFromDirectionsResponse(infoTabFragment.getCurTabs(),directionsResponse);
                            }
                        }
                        else if( resultCode == GoogleDirectionsQuery.RESPONSE_FAILURE){
                            Toast.makeText(MapsActivity.this,"Something wrong, Try again later!",Toast.LENGTH_LONG).show();
                        }
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                });

                //Query Bus Mode
                GoogleDirectionsQuery busQuery = getDirectionsQuery(locationList, GoogleDirectionsQuery.TRAVEL_MODE.TRANSIT);
                busQuery.query(new GoogleDirectionsQuery.OnDirectionsResultListener() {
                    @Override
                    public void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode) {
                        Log.d("MapsActivity","onDirectionsResult");
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(resultCode == GoogleDirectionsQuery.RESPONSE_SUCCESS){
                            //Save the response
                            busResponse = directionsResponse;
                            infoTabFragment.setBusReponse(busResponse);
                            if(infoTabFragment.getCurTabs() == 1)
                            {
                                updateUIFromDirectionsResponse(infoTabFragment.getCurTabs(),directionsResponse);
                            }
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
    private GoogleDirectionsQuery getDirectionsQuery(@NotNull ArrayList<MyPlace> locationList, final GoogleDirectionsQuery.TRAVEL_MODE travelMode){
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ArrayList<LatLng> waypoints = new ArrayList<>();
        LatLng origin;
        LatLng destination;
        //if it only contains the LatLng
        if(locationList.get(0).getPlace() == null)
            origin = locationList.get(0).getLatlng();
        else // if contains Place
            origin = locationList.get(0).getPlace().getLatLng();

        if(locationList.get(locationList.size()-1).getPlace() == null)
            destination = locationList.get(locationList.size()-1).getLatlng();
        else
            destination = locationList.get(locationList.size()-1).getPlace().getLatLng();

        for(int i = 1 ; i <= locationList.size()-2 ; ++i){
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
                .withTravelMode(travelMode)
                .withWaypoints(waypoints)
                .buid();
        return mDirectionsQuery;

    }
    private void updateUIFromDirectionsResponse(int curTabPosition, final DirectionsResponse response){
        //If the current tab belongs to driving mode

        final int stroke = 20; //width of pen
        if(response == null)
            return;
        mMap.clear();
        List<PatternItem> patternItems = null;
        int color = 0;
        //Driving tab
        if(curTabPosition == 0)
        {
            color = mContext.getResources().getColor(R.color.colorPolylineDirectionsDriving);
        }
        else if(curTabPosition == 1) //Bus tab
        {
            //Declare Route Pattern
            color = mContext.getResources().getColor(R.color.colorPolylineDirectionsDriving);
            final int PATTERN_DASH_LENGTH = 20;
            final int PATTERN_GAP_LENGTH = 20;
            PatternItem dot = new Dot();
            PatternItem dash = new Dash(PATTERN_DASH_LENGTH);
            PatternItem gap = new Gap(PATTERN_GAP_LENGTH);
            patternItems = Arrays.asList(dot,gap,dash,gap);
        }

        for (final Route route: response.routes){
            final List<LatLng> polyline = PolyUtil.decode(route.overviewPolyline.points);
            mMap.addPolyline(new PolylineOptions()
                    .addAll(polyline)
                    .color(color)
                    .pattern(patternItems)
                    .jointType(JointType.ROUND)
                    .width(stroke)
                    .geodesic(true));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //if bound exists
                    if(route.bound != null)
                    {
                        // If there are more than two points exist, animate camera
                        if(polyline.size() > 1)
                        {
                            LatLng northeast = new LatLng(route.bound.northeast.lat,route.bound.northeast.lng);
                            LatLng southwest = new LatLng(route.bound.southwest.lat,route.bound.southwest.lng);
                            LatLngBounds latLngBounds = new LatLngBounds(southwest,northeast);
                            final int padding = 50;
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngBounds(latLngBounds,padding));

                            //add Markers at destination
                            addMarkers(Arrays.asList(polyline.get(polyline.size()-1)),R.drawable.ic_destination_marker);

                            //If bus mode is selected, add marker for bus stops

                        }
                        else //If search directions contain only origin, show last known location
                        {
                            showLastknownLocation();
                        }

                    }
                }
            }, 50);
        }
    }

}
