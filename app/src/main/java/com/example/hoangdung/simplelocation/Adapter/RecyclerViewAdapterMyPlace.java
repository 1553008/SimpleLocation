package com.example.hoangdung.simplelocation.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.GooglePlacesClient.GooglePlacesGeoQuery;
import com.example.hoangdung.simplelocation.Interface.ItemClickListener;
import com.example.hoangdung.simplelocation.Interface.ItemLongClickListener;
import com.example.hoangdung.simplelocation.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 11/24/2017.
 */

public class RecyclerViewAdapterMyPlace extends RecyclerView.Adapter<RecyclerViewAdapterMyPlace.RecyclerViewHolder>
{
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    Context mContext;
    final String LABEL_FONT = "HelveticaNeue-Roman.otf";
    final String ADDRESS_FONT = "HelveticaNeue-Roman.otf";
    private List<FirebaseCenter.Location> data = new ArrayList<>();
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;
    private GeoDataClient mGeoClient;
    public RecyclerViewAdapterMyPlace(List<FirebaseCenter.Location> data, ItemClickListener itemClickListener,
                                      ItemLongClickListener itemLongClickListener)
    {
        this.data = data;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    public boolean isSelectedItemsListEmpty()
    {
        return selectedItems.size() == 0;
    }
    public List<String> getSelectedItemsLabel()
    {
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < selectedItems.size(); ++i)
        {
            FirebaseCenter.Location location = data.get(selectedItems.keyAt(i));
            labels.add(location.label);
        }
        return labels;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if(mContext==null)
            mContext = parent.getContext();
        if(mGeoClient == null)
            mGeoClient = Places.getGeoDataClient(mContext,null);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_my_places_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        if (selectedItems.get(position))
           holder.itemView.setBackgroundColor(Color.parseColor("#a2f2ab"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.label.setText(data.get(position).label);
        holder.address.setText(data.get(position).address);
        Typeface lableFont = Typeface.createFromAsset(mContext.getAssets(),LABEL_FONT);
        holder.label.setTypeface(lableFont);
        Typeface addressFont = Typeface.createFromAsset(mContext.getAssets(),ADDRESS_FONT);
        holder.setItemClickListener(itemClickListener);
        holder.setItemLongClickListener(itemLongClickListener);
        holder.itemView.setLongClickable(true);
        //Get Place Photo and bind it the ImageView
        String placeID = data.get(position).placeID;
        Log.d("MapsActivity","place ID: " + placeID + " received success" );
        Log.d("MapsActivity","position:" + String.valueOf(position));
        Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoClient.getPlacePhotos(placeID);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // Get the first photo in the list.
                if(photoMetadataBuffer.getCount() !=0){
                    final PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get a scaled bitmap for the photo.

                    final Task<PlacePhotoResponse> photoResponse = mGeoClient.getScaledPhoto(photoMetadata,
                            holder.image.getWidth(),
                            holder.image.getHeight());
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            holder.image.setImageBitmap(bitmap);
                            holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
                            photoMetadataBuffer.release();
                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void toggleSelection(int pos) {
        Log.d("khanh", "Toggle");

        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
        Log.d("khanh",String.valueOf(pos));
        //notifyDataSetChanged();
        Log.d("khanh", selectedItems.toString());
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener{
        TextView label;
        TextView address;
        ImageView image;
        private ItemClickListener itemClickListener;
        private ItemLongClickListener itemLongClickListener;

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            itemLongClickListener.onLongClick(v, getAdapterPosition());
            return true;
        }
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }
        public void setItemLongClickListener(ItemLongClickListener itemLongClickListener)
        {
            this.itemLongClickListener = itemLongClickListener;
        }

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            label = (TextView)itemView.findViewById(R.id.my_place_row_label);
            address = (TextView)itemView.findViewById(R.id.my_place_row_address);
            image = (ImageView) itemView.findViewById(R.id.place_image);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


    }
}
