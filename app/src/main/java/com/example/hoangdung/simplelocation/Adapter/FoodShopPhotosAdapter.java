package com.example.hoangdung.simplelocation.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hoangdung.simplelocation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by hoangdung on 12/12/17.
 */

public class FoodShopPhotosAdapter extends RecyclerView.Adapter<FoodShopPhotosAdapter.FoodShopPhotosViewHolder> {

    ArrayList<String> photosUrl = new ArrayList<>();
    Context context;
    @Override
    public FoodShopPhotosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context == null)
            context = parent.getContext();
        return new FoodShopPhotosViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.food_shop_image_item,parent,false));
    }

    @Override
    public void onBindViewHolder(FoodShopPhotosViewHolder holder, int position) {
        Picasso.with(context)
                .load(photosUrl.get(position))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(photosUrl == null)
            return 0;
        return photosUrl.size();
    }

    public class FoodShopPhotosViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public FoodShopPhotosViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

    public ArrayList<String> getPhotosUrl() {
        return photosUrl;
    }

    public void setPhotosUrl(ArrayList<String> photosUrl) {
        this.photosUrl = photosUrl;
    }
}
