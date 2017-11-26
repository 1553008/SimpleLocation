package com.example.hoangdung.simplelocation.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class AddPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        GoogleMap googleMap;
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

}
