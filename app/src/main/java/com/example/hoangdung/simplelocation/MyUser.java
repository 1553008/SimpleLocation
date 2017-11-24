package com.example.hoangdung.simplelocation;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hoangdung on 11/25/17.
 */

@IgnoreExtraProperties
public class MyUser {
    public String first_name;
    public String last_name;
    public String photo_url;
    public String email;
    public MyUser(){}



    @Exclude
    public void parseJSON(JSONObject json){
        if(json == null)
            return;
        try {
            first_name = json.getString("first_name");
            last_name = json.getString("last_name");
            email = json.getString("email");
            photo_url = json.getJSONObject("picture").getJSONObject("data").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
