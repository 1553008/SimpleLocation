package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/23/17.
 */

public class Bound {
    @SerializedName("northeast")
    @Expose
    Location northeast;

    @SerializedName("southwest")
    @Expose
    Location southwest;

    public Location getNortheast() {
        return northeast;
    }

    public void setNortheast(Location northeast) {
        this.northeast = northeast;
    }

    public Location getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Location southwest) {
        this.southwest = southwest;
    }
}
