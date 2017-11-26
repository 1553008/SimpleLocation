package com.example.hoangdung.simplelocation.Activity;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.hoangdung.simplelocation.Adapter.RecyclerViewAdapterMyPlace;
import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Interface.ItemClickListener;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;


public class MyPlacesActivity extends AppCompatActivity implements OnMapReadyCallback {
    RecyclerView mRecyclerView;
    RecyclerViewAdapterMyPlace mRcvAdapter;
    List<FirebaseCenter.Location> data;

    GoogleMap googleMap;
    int chosenPlaceIndex = -1;

    @Override
    public void finish() {
        Intent data = new Intent();
        if (chosenPlaceIndex >= 0)
        {
            data.putExtra("chosenPlace", chosenPlaceIndex);
            setResult(RESULT_OK,data);
        }
        else
            setResult(RESULT_CANCELED);
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_my_place);
        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            /* Show place list */

            // get location list from intent
            ArrayList<FirebaseCenter.Location> loc = receivedIntent.getParcelableArrayListExtra("place");


            mRcvAdapter = new RecyclerViewAdapterMyPlace(loc, new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    chosenPlaceIndex = position;
                    finish();
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mRcvAdapter);



        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
