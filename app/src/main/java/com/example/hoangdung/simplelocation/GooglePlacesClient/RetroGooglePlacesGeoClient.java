package com.example.hoangdung.simplelocation.GooglePlacesClient;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.example.hoangdung.simplelocation.GooglePlacesClient.GooglePlacesGeoPOJO.PlacesGeoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hoangdung on 11/27/17.
 */

public interface RetroGooglePlacesGeoClient {

    @GET("api/geocode/json?key=AIzaSyD4Eh3glQzLczWlpMMBboXpF4ed6oCpJcY")
    Call<PlacesGeoResponse> getPlacesGeo(@Query("latlng")String latlng
    );
}
