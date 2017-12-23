package com.example.hoangdung.simplelocation.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.hoangdung.simplelocation.Fragments.FoodShopDirectionFragment;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Polyline;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

public class FoodShopDirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private DirectionsResponse directionsResponse;
    private LatLng currentLocation;
    private LatLng foodShopLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_shop_direction);
        Intent intent = getIntent();
        directionsResponse = intent.getParcelableExtra("direction");
        currentLocation = intent.getParcelableExtra("source");
        foodShopLocation = intent.getParcelableExtra("dest");
        //Init Google Map
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        setSupportActionBar((Toolbar) findViewById(R.id.food_result_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void displayDirection() {

        FoodShopDirectionFragment foodShopDirectionFragment = FoodShopDirectionFragment.newInstance(FoodShopDirectionActivity.this,
                directionsResponse);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragment_container, foodShopDirectionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Display Direction
        displayDirection();
        //Display Google Map UI
        this.googleMap = googleMap;
        displayMap();

    }

    @SuppressLint("MissingPermission")
    private void displayMap() {
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
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.setMyLocationEnabled(true);
        googleMap.addPolyline(new PolylineOptions()
                .addAll(PolyUtil.decode(directionsResponse.routes.get(0).overviewPolyline.points))
                .color(getApplicationContext().getResources().getColor(R.color.colorPolylineDirectionsDriving))
        );
        googleMap.addMarker(new MarkerOptions().position(this.foodShopLocation)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_shop)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
