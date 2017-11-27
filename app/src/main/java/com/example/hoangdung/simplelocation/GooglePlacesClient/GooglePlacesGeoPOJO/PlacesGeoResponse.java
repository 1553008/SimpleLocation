package com.example.hoangdung.simplelocation.GooglePlacesClient.GooglePlacesGeoPOJO;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/27/17.
 */

public class PlacesGeoResponse {
    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("results")
    @Expose
    public ArrayList<Result> results = new ArrayList<>();

    public static class Result{
        @SerializedName("place_id")
        @Expose
        public String placeID;
    }
}
