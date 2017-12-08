package com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 12/8/17.
 */

public class NearestPlacesResponse {

    @SerializedName("shops")
    @Expose
    public ArrayList<FoodShop> shops = new ArrayList<>();



}
