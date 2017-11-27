package com.example.hoangdung.simplelocation.GooglePlacesClient;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by hoangdung on 11/27/17.
 */

public class GooglePlacesGeoParamsBuilder {
    static String getLatLng(LatLng latlng){
        if(latlng==null)
            return null;
        return String.valueOf(latlng.latitude) + "," + String.valueOf(latlng.longitude);
    }
}
