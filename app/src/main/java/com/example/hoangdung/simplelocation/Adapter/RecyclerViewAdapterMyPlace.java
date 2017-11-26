package com.example.hoangdung.simplelocation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.Interface.ItemClickListener;
import com.example.hoangdung.simplelocation.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 11/24/2017.
 */

public class RecyclerViewAdapterMyPlace extends RecyclerView.Adapter<RecyclerViewAdapterMyPlace.RecyclerViewHolder>
{

    Context mContext;
    final String LABEL_FONT = "HelveticaNeue-Roman.otf";
    final String ADDRESS_FONT = "HelveticaNeue-Roman.otf";
    private List<FirebaseCenter.Location> data = new ArrayList<>();
    private ItemClickListener itemClickListener;
    public RecyclerViewAdapterMyPlace(List<FirebaseCenter.Location> data, ItemClickListener itemClickListener)
    {
        this.data = data;
        this.itemClickListener = itemClickListener;
    }



    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_my_places_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.label.setText(data.get(position).label);
        holder.address.setText(data.get(position).address);
        Typeface lableFont = Typeface.createFromAsset(mContext.getAssets(),LABEL_FONT);
        holder.label.setTypeface(lableFont);
        Typeface addressFont = Typeface.createFromAsset(mContext.getAssets(),ADDRESS_FONT);
        holder.setItemClickListener(itemClickListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView label;
        TextView address;
        private ItemClickListener itemClickListener;


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition());
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            label = (TextView)itemView.findViewById(R.id.my_place_row_label);
            address = (TextView)itemView.findViewById(R.id.my_place_row_address);

            itemView.setOnClickListener(this);
        }
    }
}
