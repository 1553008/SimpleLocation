package com.example.hoangdung.simplelocation.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.hoangdung.simplelocation.FirebaseCenter;
import com.example.hoangdung.simplelocation.R;

import java.util.ArrayList;


public class MyPlacesActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null)
        {
            //String[] labels = new String[] {"one", "two", "three"};
//            String[] labels = receivedIntent.getStringArrayExtra("placeLabels");
//            ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1,
//                    labels);
//            listView = (ListView)findViewById(R.id.list_view_my_places);
//            listView.setAdapter(adapter);
            ArrayList<FirebaseCenter.Location> loc = receivedIntent.getParcelableArrayListExtra("place");
            Log.d("Khanh", loc.toString());
            int b = 3;
        }
    }
}
