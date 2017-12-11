package com.example.hoangdung.simplelocation.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodShopDirectionFragment extends Fragment {

    @BindView(R.id.distanceText)
    public TextView distanceText;

    @BindView(R.id.durationText)
    public TextView durationText;

    @BindView(R.id.stepRecyclerView)
    public RecyclerView stepListView;
    Context context;
    DrivingTabFragment.Adapter adapter;
    DirectionsResponse directionsResponse;
    public static FoodShopDirectionFragment newInstance(Context context, DirectionsResponse directionsResponse){
        FoodShopDirectionFragment foodShopDirectionFragment = new FoodShopDirectionFragment();
        foodShopDirectionFragment.context = context;
        foodShopDirectionFragment.adapter = new DrivingTabFragment.Adapter(context);
        foodShopDirectionFragment.directionsResponse = directionsResponse;
        return foodShopDirectionFragment;
    }
    public FoodShopDirectionFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_shop_direction, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDirectionResponse(this.directionsResponse);


    }

    public void setDirectionResponse(DirectionsResponse directionResponse){
        //Set distance and duration
        distanceText.setText(directionResponse.routes.get(0).legs.get(0).distance.text);
        durationText.setText(directionResponse.routes.get(0).legs.get(0).durationInTraffic.text);
        //Init Step RecyclerView
        stepListView.setLayoutManager(new LinearLayoutManager(context));
        stepListView.setHasFixedSize(true);
        stepListView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        //Init Adapter
        SlideInBottomAnimationAdapter wrapAdapter = new SlideInBottomAnimationAdapter(adapter);
        wrapAdapter.setFirstOnly(false);
        wrapAdapter.setInterpolator( new OvershootInterpolator());
        stepListView.setAdapter(wrapAdapter);
        adapter.setDirectionResponse(directionResponse);

    }


}
