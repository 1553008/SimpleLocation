package com.example.hoangdung.simplelocation.Adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.Interface.OnShopClickListener;
import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.FoodShop;
import com.example.hoangdung.simplelocation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * Created by hoangdung on 12/11/17.
 */

public class FoodShopListAdapter extends RecyclerView.Adapter<FoodShopListAdapter.FoodShopViewHolder> {
    ArrayList<FoodShop> foodShopArrayList;
    Context mContext;
    public static final int DISPLAY_INFO = 0;
    public static final int FIND_DIRECTION = 1;
    public OnShopClickListener listener;
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

        @BindView(R.id.view_foreground)
        public RelativeLayout foreground;

        @BindView(R.id.view_go_to_shop)
        public RelativeLayout goToShopBackground;

        @BindView(R.id.view_find_shop_direction)
        public RelativeLayout findShopBackground;

        public FoodShopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public class FoodShopListItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

        public FoodShopListItemTouchHelperCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            listener.onClick(foodShopArrayList.get(viewHolder.getAdapterPosition()),
                    direction == ItemTouchHelper.LEFT ? DISPLAY_INFO : FIND_DIRECTION);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if(viewHolder!=null){
                final View foreground = ((FoodShopListAdapter.FoodShopViewHolder)viewHolder).foreground;
                getDefaultUIUtil().onSelected(foreground);
            }
        }

        @Override
        public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).foreground;
            if(dX < 0){
                ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).goToShopBackground.setVisibility(View.VISIBLE);
                ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).findShopBackground.setVisibility(View.INVISIBLE);
            }
            else{
                ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).goToShopBackground.setVisibility(View.INVISIBLE);
                ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).findShopBackground.setVisibility(View.VISIBLE);
            }
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                    actionState, isCurrentlyActive);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final View foregroundView = ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final View foregroundView = ((FoodShopListAdapter.FoodShopViewHolder) viewHolder).foreground;
            getDefaultUIUtil().onDraw(c,recyclerView,foregroundView,dX,dY,actionState,isCurrentlyActive);

        }
    }

}
