package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class DirectionsResponse {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("geocoded_waypoints")
    @Expose
    public ArrayList<GeocodedWaypoint> geocodedWaypoints = new ArrayList<>();

    @SerializedName("routes")
    @Expose
    public ArrayList<Route> routes = new ArrayList<>();

}
