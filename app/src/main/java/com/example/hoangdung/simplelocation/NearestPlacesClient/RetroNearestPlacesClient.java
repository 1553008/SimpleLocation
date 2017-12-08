package com.example.hoangdung.simplelocation.NearestPlacesClient;

import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.NearestPlacesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by hoangdung on 12/8/17.
 */

public interface RetroNearestPlacesClient {
    @GET("searchNearbyLocation?")
    Call<NearestPlacesResponse> getNearestPlaces(@Query("lat")String lat,
                                                 @Query("lng")String lng,
                                                 @Query("tag")String tag);
}
