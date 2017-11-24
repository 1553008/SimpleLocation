package com.example.hoangdung.simplelocation.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hoangdung.simplelocation.Adapter.RecyclerViewAdapterMyPlace;
import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.R;

import java.util.ArrayList;
import java.util.List;


public class MyPlacesActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerViewAdapterMyPlace mRcvAdapter;
    List<FirebaseCenter.Location> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view_my_place);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            /* Show place list */

            // get location list from intent
            ArrayList<FirebaseCenter.Location> loc = receivedIntent.getParcelableArrayListExtra("place");


            mRcvAdapter = new RecyclerViewAdapterMyPlace(loc);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mRcvAdapter);



        }
    }
}
