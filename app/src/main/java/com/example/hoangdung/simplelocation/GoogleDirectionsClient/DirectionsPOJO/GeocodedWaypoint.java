package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/11/17.
 */

public class GeocodedWaypoint {
    @SerializedName("geocoder_status")
    @Expose
    String geocoderStatus;

    @SerializedName("place_id")
    @Expose
    String placeID;

    public String getGeocoderStatus() {
        return geocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        this.geocoderStatus = geocoderStatus;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }
}