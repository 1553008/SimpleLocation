package com.example.hoangdung.simplelocation;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
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

import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, SearchFragment.SearchFragmentCallback{

    //For Debugging
    private String TAG = MapsActivity.class.getSimpleName();
    private int DEFAULT_ZOOM = 15;
    private int statusBarHeight = 24; //dp
    private String mEmail;
    private String mName;
    private String mProfileIconURL;
    private SearchFragment mSearchFragment;
    private FrameLayout mToolbarContainer;
    //Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    @BindView(R.id.floating_btn)
    public FloatingActionButton mLocateBtn;
    //Google Map model
    private GoogleMap mMap;

    //Fused Location Provider
    private FusedLocationProviderClient mLocationProvider;

    //Last known location
    private Location mLastknownLocation;

    //Drawer Layout

    private Drawer mDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this);
        initUserInfoHeader();
        drawerLayoutSetup();
        toolbarSetup();
        locateButtonSetup();
    }//onCreate


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
        updateUI();
        showLastknownLocation();
    }//onMapReady

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
    //----------------------Google Maps Functionality-----------------------
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
    private void updateUI() {
        try{
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        catch (SecurityException e){

        }
    }

    //------------------------------Activity UI Setup--------------------------------------------
    /**
     * Setup Drawer Layout
     */
    private void drawerLayoutSetup(){
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
    /**
     * SearchFragment Interface
     */
    @Override
    public void onSearchFragmentViewCreated() {
        mDrawer.setToolbar(this,mSearchFragment.mToolbar);

    }

    @Override
    public void onSearchBarClicked() {

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
}
