package com.example.hoangdung.simplelocation.GoogleDirectionsClient;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hoangdung on 11/11/17.
 * This is Application Google Directions API implemented using Retrofit HTTP Client
 * Google Directions API from Google doesn't have Android API version, only RestAPI
 * So we developed this Component for Android Version by wrapping Google's RestAPI
 */

public interface RetroGoogleDirectionsClient {
    @GET("api/directions/json?key=AIzaSyD4Eh3glQzLczWlpMMBboXpF4ed6oCpJcY")
    Call<DirectionsResponse> getDirections(@Query("origin")String origin,
                                           @Query("destination")String destination,
                                           @Query("waypoints:via")String waypoints,
                                           @Query("mode")String mode,
                                           @Query("transit_mode")String transitMode,
                                           @Query("traffic_model")String trafficModel,
                                           @Query("transit_routing_preference")String transitPreference,
                                           @Query("departure_time")String departureTime);
}
