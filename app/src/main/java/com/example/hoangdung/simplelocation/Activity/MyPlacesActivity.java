package com.example.hoangdung.simplelocation.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.Adapter.RecyclerViewAdapterMyPlace;
import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Fragments.SearchFragment;
import com.example.hoangdung.simplelocation.Interface.ItemClickListener;
import com.example.hoangdung.simplelocation.MyApplication;
import com.example.hoangdung.simplelocation.MyPlace;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MyPlacesActivity extends AppCompatActivity {


    public @BindView(R.id.recycler_view_my_place) RecyclerView mRecyclerView;
    public @BindView(R.id.add_place_button) FloatingActionButton mAddBtn;
    public @BindView(R.id.my_place_toolbar) Toolbar mToolbar;
    RecyclerViewAdapterMyPlace mRcvAdapter;
    List<FirebaseCenter.Location> data;

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
        ButterKnife.bind(this);

        //Prepare UI for Toolbar
        final int statusBarHeight = MyApplication.getStatusBarHeight(getApplicationContext());
        RelativeLayout.LayoutParams toolbarParams = (RelativeLayout.LayoutParams) mToolbar.getLayoutParams();
        toolbarParams.topMargin +=statusBarHeight;
        mToolbar.setLayoutParams(toolbarParams);

        //Prepare UI for Add Place Button

        final int navigationBarHeight = MyApplication.getNavigationBarHeight(getApplicationContext(),
                getApplicationContext()
                        .getResources()
                        .getConfiguration()
                        .orientation);
        if(MyApplication.hasSoftNavBar(getApplicationContext())){
            RelativeLayout.LayoutParams addBtnParams = (RelativeLayout.LayoutParams) mAddBtn.getLayoutParams();
            addBtnParams.bottomMargin = (int) (navigationBarHeight +
                    getApplicationContext()
                            .getResources()
                            .getDimension(R.dimen.addMyPlaceButtonMarginBottom));
            addBtnParams.rightMargin = (int) getApplicationContext().getResources().getDimension(R.dimen.addMyPlaceButtonMarginRight);
        }
        setSupportActionBar(mToolbar);



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


    public void onClickAddPlaceButton(View view) {
        Intent intent = new Intent(this, AddPlaceActivity.class);
        startActivity(intent);
    }
}
