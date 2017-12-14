package com.example.hoangdung.simplelocation.Activity;

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
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.Adapter.RecyclerViewAdapterMyPlace;
import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Interface.ItemClickListener;
import com.example.hoangdung.simplelocation.Interface.ItemLongClickListener;
import com.example.hoangdung.simplelocation.MyApplication;
import com.example.hoangdung.simplelocation.R;
import com.example.hoangdung.simplelocation.ScrollAwareFABBehavior;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyPlacesActivity extends AppCompatActivity implements ActionMode.Callback{


    public @BindView(R.id.recycler_view_my_place) RecyclerView mRecyclerView;
    public @BindView(R.id.add_place_button) FloatingActionButton mAddBtn;
    public @BindView(R.id.my_place_toolbar) Toolbar mToolbar;
    RecyclerViewAdapterMyPlace mRcvAdapter;
    List<FirebaseCenter.Location> data = new ArrayList<FirebaseCenter.Location>();
    int chosenPlaceIndex = -1;
    ActionMode actionMode;
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
    protected void onResume()
    {
        super.onResume();
        // update categories
        data = FirebaseCenter.getInstance().getMyPlaces();
        // tell the adapter that categories has been updated
        mRcvAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
        ButterKnife.bind(this);
        //Prepare UI for Add Place Button and RecyclerView
        CoordinatorLayout.LayoutParams addBtnParams = (CoordinatorLayout.LayoutParams) mAddBtn.getLayoutParams();
        addBtnParams.setBehavior(new ScrollAwareFABBehavior());
        LinearLayout.LayoutParams listParams = (LinearLayout.LayoutParams) mRecyclerView.getLayoutParams();
        addBtnParams.bottomMargin = (int)getApplicationContext()
                .getResources()
                .getDimension(R.dimen.addMyPlaceButtonMarginBottom);
        setSupportActionBar(mToolbar);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            /* Show place list */

            // get location list from intent

            data = FirebaseCenter.getInstance().getMyPlaces();
            mRcvAdapter = new RecyclerViewAdapterMyPlace(data, new ItemClickListener()
            {
                @Override
                public void onClick(View view, int position)
                {
                    if (actionMode == null)
                    {
                      chosenPlaceIndex = position;
                      finish();
                    }
                    else
                    {
                        Log.d("khanh","itemClick");
                        Log.d("khanh","position: " + String.valueOf(position));


                        mRcvAdapter.toggleSelection(position);
                        TextView item  = (TextView)findViewById(R.id.remove_place);
                        if (mRcvAdapter.isSelectedItemsListEmpty())
                        {
                             actionMode.finish();
                        }
                        else
                        {
                            item.setTextColor(Color.parseColor("#ffffff"));
                        }


//                        removePlaceItem.setEnabled(mRcvAdapter.isSelectedItemsListEmpty());

                    }

                }
            }, new ItemLongClickListener()
            {
                @Override
                public void onLongClick(View view, int position)
                {
                    if (actionMode != null)
                        return;
                    actionMode = startSupportActionMode(MyPlacesActivity.this);
                    mRcvAdapter.toggleSelection(position);
                    Log.d("khanh", "item long click");
                    Log.d("khanh","position: " + String.valueOf(position));
                }
            });

            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mRcvAdapter);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setItemAnimator(null);

        }
    }


    @OnClick(R.id.add_place_button)
    public void onClickAddPlaceButton(View view) {
        Intent intent = new Intent(this, AddPlaceActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_addplace_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu)
    {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.remove_place:
                FirebaseCenter.getInstance().removePlace(
                        mRcvAdapter.getSelectedItemsLabel()
                        ,
                        new DatabaseReference.CompletionListener()
                        {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                            {
                                Toast.makeText(MyPlacesActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                                actionMode.finish();
                                mRcvAdapter.notifyDataSetChanged();
                            }
                        }
                );
                break;
            case R.id.select_all_place:
                mRcvAdapter.selectAll();
                break;
            default:
                return false;
        }
        return  true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode)
    {
        mRcvAdapter.clearSelections();
        this.actionMode = null;
    }
}
