package com.example.hoangdung.simplelocation.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcgdv.asia.lib.dots.DotsProgressIndicator;
import com.example.hoangdung.simplelocation.Adapter.FoodShopListAdapter;
import com.example.hoangdung.simplelocation.Fragments.FoodListFragment;
import com.example.hoangdung.simplelocation.Fragments.FoodShopDirectionFragment;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Polyline;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.GoogleDirectionsQuery;
import com.example.hoangdung.simplelocation.Interface.OnShopClickListener;
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;
import com.example.hoangdung.simplelocation.ProgressWindowAnim;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.math.Quantiles;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class FoodResultActivity extends AppCompatActivity implements OnMapReadyCallback, OnShopClickListener, GoogleMap.OnInfoWindowClickListener{

    @BindView(R.id.food_result_toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.fragment_container)
    public FrameLayout fragmentContainer;

    ArrayList<FoodShop> foodShopArrayList;
    ArrayList<Marker> foodShopMarkerArray = new ArrayList<>();
    GoogleMap googleMap;
    LatLngBounds.Builder boundBuilder;
    FusedLocationProviderClient fusedLocationProviderClient;
    final static int BOUND_PADDING = 100;
    Location lastLocation;
    ProgressWindowAnim<DotsProgressIndicator> progressWindow;
    com.google.android.gms.maps.model.Polyline polylines;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result);
        ButterKnife.bind(this);
        progressWindow = new ProgressWindowAnim(this,R.layout.progress_window_layout_2);
        //Get Data from Food Finder Activity
        Intent intent = getIntent();
        foodShopArrayList =  intent.getParcelableArrayListExtra("shops");
        Log.d("MapsActivity","Food Shops received from Food Finder Activity");

        //Init Google Map
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment  supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Init Food List Fragment
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        FoodListFragment foodListFragment = FoodListFragment.newInstance(getApplicationContext(),foodShopArrayList);
        foodListFragment.setOnShopClickListener(this);
        transaction.add(R.id.fragment_container,foodListFragment);
        transaction.commit();



    }

    @SuppressLint("MissingPermission")
    private void setupFoodShopsMarkers(){

        progressWindow.showProgress();
        boundBuilder = new LatLngBounds.Builder();
        if(foodShopArrayList.size() > 0)
        {
            for(FoodShop foodShop : foodShopArrayList){

                //Add markers to map
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_shop))
                        .position(new LatLng(foodShop.lat,foodShop.lng))
                );
                marker.setTag(foodShop);
                foodShopMarkerArray.add(marker);
                //Form LatLngBounds from Food shop LatLng
                boundBuilder.include(new LatLng(foodShop.lat,foodShop.lng));
            }
        }
        Task<Location> lastLocationTask = fusedLocationProviderClient.getLastLocation();
        lastLocationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    lastLocation = task.getResult();
                    double lat = task.getResult().getLatitude();
                    double lng = task.getResult().getLongitude();
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.setMyLocationEnabled(true);
                    boundBuilder.include(new LatLng(lat,lng));
                    progressWindow.hideProgress();
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),BOUND_PADDING));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupFoodShopsMarkers();
        this.googleMap.setInfoWindowAdapter(new FoodShopInfoWindowAdapter());
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onClick(FoodShop foodShop, int mode) {
        //If user swipe to see direction
        if(mode == FoodShopListAdapter.FIND_DIRECTION){
            queryFoodShopDirection(foodShop);
        }
        else if(mode == FoodShopListAdapter.DISPLAY_INFO){
            startFoodShopActivity(foodShop);
        }
    }

    private void queryFoodShopDirection(FoodShop foodShop){
        progressWindow.showProgress();
        GoogleDirectionsQuery query = new GoogleDirectionsQuery.Builder()
                .withOrigin(new LatLng(foodShop.lat,foodShop.lng))
                .withDestination(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()))
                .withTravelMode(GoogleDirectionsQuery.TRAVEL_MODE.DRIVING)
                .buid();
        query.query(new GoogleDirectionsQuery.OnDirectionsResultListener() {
            @Override
            public void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode) {

                //Remove old polylines if any
                if(polylines!=null)
                    polylines.remove();
                polylines =  googleMap.addPolyline(new PolylineOptions()
                        .addAll(PolyUtil.decode(directionsResponse.routes.get(0).overviewPolyline.points))
                        .color(getApplicationContext().getResources().getColor(R.color.colorPolylineDirectionsDriving))
                );
                //Replace Food List Fragment with Direction Fragment
                FoodShopDirectionFragment foodShopDirectionFragment = FoodShopDirectionFragment.newInstance(FoodResultActivity.this,
                        directionsResponse);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right);
                transaction.replace(R.id.fragment_container,foodShopDirectionFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                progressWindow.hideProgress();
                //Move Camera
                LatLng northeast = new LatLng(
                        directionsResponse.routes.get(0).bound.northeast.lat,
                        directionsResponse.routes.get(0).bound.northeast.lng
                );
                LatLng southwest = new LatLng(
                        directionsResponse.routes.get(0).bound.southwest.lat,
                        directionsResponse.routes.get(0).bound.southwest.lng
                );
                LatLngBounds.Builder boundBuilder = new LatLngBounds.Builder();
                boundBuilder.include(northeast);
                boundBuilder.include(southwest);
                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                                boundBuilder.build(),
                                100
                    )
                );
            }
        });
    }
    private void startFoodShopActivity(FoodShop foodShop){
        Intent intent = new Intent(FoodResultActivity.this,FoodShopActivity.class);
        intent.putExtra("shop",foodShop);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(polylines!=null)
            polylines.remove();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),BOUND_PADDING));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        queryFoodShopDirection((FoodShop) marker.getTag());
    }

    public class FoodShopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{
        private final View mWindow;

        @BindView(R.id.food_shop_avartar)
        public ImageView foodShopAvar;

        @BindView(R.id.food_shop_name)
        public TextView foodShopName;

        @BindView(R.id.food_shop_address)
        public TextView foodShopAddress;

        public FoodShopInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.food_shop_info_window,null,false);
            ButterKnife.bind(this,mWindow);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            FoodShop foodShop = (FoodShop) marker.getTag();
            Picasso.with(FoodResultActivity.this)
                    .load(foodShop.avartar)
                    .fit()
                    .into(foodShopAvar);
            foodShopName.setText(foodShop.name);
            foodShopAddress.setText(foodShop.address);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
