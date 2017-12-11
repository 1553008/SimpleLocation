package com.example.hoangdung.simplelocation.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.example.hoangdung.simplelocation.Adapter.FoodShopListAdapter;
import com.example.hoangdung.simplelocation.Interface.OnShopClickListener;
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;
import com.example.hoangdung.simplelocation.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoodListFragment extends Fragment{

    @BindView(R.id.food_list_view)
    public RecyclerView mListView;

    FoodShopListAdapter adapter;
    Context context;
    public FoodListFragment() {
        // Required empty public constructor
    }

    static public FoodListFragment newInstance(Context context,ArrayList<FoodShop> foodShops){
        FoodListFragment foodListFragment = new FoodListFragment();
        foodListFragment.context = context;
        foodListFragment.adapter = new FoodShopListAdapter(context,foodShops);
        return foodListFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_list, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Animation for adapter
        ScaleInAnimationAdapter wrapAdapter = new ScaleInAnimationAdapter(adapter);
        wrapAdapter.setFirstOnly(false);
        wrapAdapter.setInterpolator(new OvershootInterpolator());

        //Item Touch Helper for adapter
        FoodShopListAdapter.FoodShopListItemTouchHelperCallback itemTouchHelperCallback = adapter.new FoodShopListItemTouchHelperCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);

        //Init RecyclerView
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mListView.setAdapter(wrapAdapter);
        mListView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mListView);
    }
    public void setOnShopClickListener(OnShopClickListener listener){
        adapter.listener = listener;
    }
}
