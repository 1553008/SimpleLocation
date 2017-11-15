package com.example.hoangdung.simplelocation.GoogleDirectionsClient;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.DirectionsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hoangdung on 11/12/17.
 * Application Google Directions Query object
 * This object is built on top Retrofit API
 * Used when there is a need to query Google Directions
 * Execute asynchronously
 * Return DirectionsResponse JAVA object
 */

public class GoogleDirectionsQuery {


    public enum TRAVEL_MODE{
        DRIVING,
        BICYCLING,
        TRANSIT
    };
    public enum TRANSIT_MODE{
        BUS
    };
    public enum TRANSIT_ROUTING_PREFERENCE{
        LESS_WALKING,
        FEWER_TRANSFERS
    };
    public enum TRAFFIC_MODEL{
        BEST_GUESS,
        PESSIMISTIC,
        OPTIMISTIC
    };
    public final static int RESPONSE_FAILURE = 0;
    public final static int RESPONSE_SUCCESS = 1;
    private Retrofit retrofit;
    private static String url = "https://maps.googleapis.com/maps/";
    private LatLng origin;
    private LatLng destination;
    private ArrayList<LatLng> waypoints;
    private TRAVEL_MODE travelMode = TRAVEL_MODE.DRIVING;
    private TRANSIT_MODE transit_mode = TRANSIT_MODE.BUS;
    private TRANSIT_ROUTING_PREFERENCE transitRoutingPreference = TRANSIT_ROUTING_PREFERENCE.FEWER_TRANSFERS;
    private TRAFFIC_MODEL trafficModel = TRAFFIC_MODEL.BEST_GUESS;
    private String departureTime = "now";
    private RetroGoogleDirectionsClient googleDirectionsClient;
    private GoogleDirectionsQuery(){

    }
    public interface OnDirectionsResultListener{
        void onDirectionsResult(DirectionsResponse directionsResponse, int resultCode);
    }

    public void query(final OnDirectionsResultListener directionsResultListener){
        Call<DirectionsResponse> responseCall =  googleDirectionsClient.getDirections(
                GoogleDirectionsParamsBuilder.getParamsByLatLng(origin),
                GoogleDirectionsParamsBuilder.getParamsByLatLng(destination),
                GoogleDirectionsParamsBuilder.getParamsByLatLngArr(waypoints,true),
                GoogleDirectionsParamsBuilder.getParamsByTravelMode(travelMode),
                GoogleDirectionsParamsBuilder.getParamsByTransitMode(transit_mode),
                GoogleDirectionsParamsBuilder.getParamsByTrafficModel(trafficModel),
                GoogleDirectionsParamsBuilder.getParamsByTransitPreference(transitRoutingPreference),
                departureTime
        );
        responseCall.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if(response.isSuccessful()){
                    Log.d("MapsActivity","DirectionsQuery:success");
                    if(directionsResultListener!=null)
                        directionsResultListener.onDirectionsResult(response.body(),RESPONSE_SUCCESS);
                }
            }
            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.d("MapsActivity","DirectionsQuery:failure");
                if(directionsResultListener != null)
                    directionsResultListener.onDirectionsResult(null,RESPONSE_FAILURE);
            }
        });
    }
    public static class Builder{
        GoogleDirectionsQuery googleDirectionsQuery = new GoogleDirectionsQuery();
        public Builder withOrigin(@NonNull LatLng origin){
            googleDirectionsQuery.origin = origin;
            return this;
        }

        public Builder withDestination(@NonNull LatLng destination){
            googleDirectionsQuery.destination = destination;
            return this;
        }

        public Builder withTransitMode(@NonNull GoogleDirectionsQuery.TRANSIT_MODE transitMode){
            googleDirectionsQuery.transit_mode = transitMode;
            return this;
        }
        public Builder withTravelMode(@NonNull GoogleDirectionsQuery.TRAVEL_MODE travelMode){
            googleDirectionsQuery.travelMode = travelMode;
            return this;
        }
        public Builder withTransitPreference(@NonNull GoogleDirectionsQuery.TRANSIT_ROUTING_PREFERENCE transitPreference){
            googleDirectionsQuery.transitRoutingPreference = transitPreference;
            return this;
        }
        public Builder withWaypoints(ArrayList<LatLng> latLngArrayList){
            googleDirectionsQuery.waypoints = latLngArrayList;
            return this;
        }

        public GoogleDirectionsQuery buid(){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            googleDirectionsQuery.retrofit = new Retrofit
                    .Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            googleDirectionsQuery.googleDirectionsClient = googleDirectionsQuery.retrofit.create(RetroGoogleDirectionsClient.class);
            return googleDirectionsQuery;
        }
    }
}
