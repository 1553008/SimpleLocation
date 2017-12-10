package com.example.hoangdung.simplelocation.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class FoodResultActivity extends AppCompatActivity implements OnMapReadyCallback{

    @BindView(R.id.food_result_toolbar)
    public Toolbar mToolbar;

    @BindView(R.id.food_result_list)
    public RecyclerView mListView;

    FoodShopListAdapter adapter;
    ArrayList<FoodShop> foodShopArrayList;
    GoogleMap googleMap;
    LatLngBounds.Builder boundBuilder;

    final static int BOUND_PADDING = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_result);
        ButterKnife.bind(this);

        //Get Data from Food Finder Activity
        Intent intent = getIntent();
        foodShopArrayList =  intent.getParcelableArrayListExtra("shops");
        Log.d("MapsActivity","Food Shops received from Food Finder Activity");

        //Init Adapter
        adapter = new FoodShopListAdapter(getApplicationContext(),foodShopArrayList);

        //Init RecyclerView
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mListView.setAdapter(adapter);

        //Init Google Map
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment  supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

    }


    private void setupFoodShopsMarkers(){
        boundBuilder = new LatLngBounds.Builder();
        if(foodShopArrayList.size() > 0)
        {
            for(FoodShop foodShop : foodShopArrayList){

                //Add markers to map
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_shop))
                        .position(new LatLng(foodShop.lat,foodShop.lng))
                );
                //Form LatLngBounds from Food shop LatLng
                boundBuilder.include(new LatLng(foodShop.lat,foodShop.lng));
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundBuilder.build(),BOUND_PADDING));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupFoodShopsMarkers();
    }

    public static class FoodShopListAdapter extends RecyclerView.Adapter<FoodShopListAdapter.FoodShopViewHolder>{
        ArrayList<FoodShop> foodShopArrayList;
        Context mContext;
        public FoodShopListAdapter(Context context,ArrayList<FoodShop> foodShops){
            mContext = context;
            foodShopArrayList = foodShops;
        }
        @Override
        public FoodShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FoodShopViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_shop_item,parent,false)) ;
        }

        @Override
        public void onBindViewHolder(FoodShopViewHolder holder, int position) {
            //Load shop avartar
            Picasso.with(mContext)
                    .load(foodShopArrayList.get(position).avartar)
                    .fit()
                    .into(holder.avartar);
            //Load shop name into textivew
            holder.name.setText(foodShopArrayList.get(position).name);
            //Load shop address into textview
            holder.address.setText(foodShopArrayList.get(position).address);
            //Load shop ratings into ratingbar
            holder.ratingBar.setNumStars(10);
            holder.ratingBar.setRating(foodShopArrayList.get(position).averageRatings);
            //Load shop ratinng into textview
            holder.ratingText.setText(String.valueOf(foodShopArrayList.get(position).averageRatings));
        }

        @Override
        public int getItemCount() {
            if(foodShopArrayList==null)
                return 0;
            return foodShopArrayList.size();
        }

        public class FoodShopViewHolder extends RecyclerView.ViewHolder{
            @BindView(R.id.food_shop_avartar)
            public ImageView avartar;

            @BindView(R.id.food_shop_name)
            public TextView name;

            @BindView(R.id.food_shop_address)
            public TextView address;

            @BindView(R.id.food_shop_ratings)
            public MaterialRatingBar ratingBar;

            @BindView(R.id.food_shop_ratings_text)
            public TextView ratingText;
            public FoodShopViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
