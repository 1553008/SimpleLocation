package com.example.hoangdung.simplelocation.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 11/24/2017.
 */

public class RecyclerViewAdapterMyPlace extends RecyclerView.Adapter<RecyclerViewAdapterMyPlace.RecyclerViewHolder> {

    private List<FirebaseCenter.Location> data = new ArrayList<>();

    public RecyclerViewAdapterMyPlace(List<FirebaseCenter.Location> data)
    {
        this.data = data;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycler_view_my_places_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.label.setText(data.get(position).label);
        holder.address.setText(data.get(position).address);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView label;
        TextView address;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            label = (TextView)itemView.findViewById(R.id.my_place_row_label);
            address = (TextView)itemView.findViewById(R.id.my_place_row_address);

        }
    }
}
