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
    String status;

    @SerializedName("geocoded_waypoints")
    @Expose
    ArrayList<GeocodedWaypoint> geocodedWaypoints = new ArrayList<>();

    @SerializedName("routes")
    @Expose
    ArrayList<Route> routes = new ArrayList<>();

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(ArrayList<GeocodedWaypoint> geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }
}
