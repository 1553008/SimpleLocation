package com.example.hoangdung.simplelocation.GooglePlacesClient;

import android.util.Log;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.RetroGoogleDirectionsClient;
import com.example.hoangdung.simplelocation.GooglePlacesClient.GooglePlacesGeoPOJO.PlacesGeoResponse;
import com.google.android.gms.maps.model.LatLng;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hoangdung on 11/27/17.
 */

public class GooglePlacesGeoQuery {
    public static final int RESPONSE_FAILURE = 0;
    public static final int RESPONE_SUCCESS = 1;
    private static String url = "https://maps.googleapis.com/maps/";
    private LatLng latLng;
    private RetroGooglePlacesGeoClient googlePlacesGeoClient;
    private Retrofit retrofit;
    private GooglePlacesGeoQuery(){}
    public interface OnPlacesResponseListener{
        public void onPlacesResponse(String placeID,int resultCode);
    }
    public void query(final OnPlacesResponseListener listener){
        Call<PlacesGeoResponse> call = googlePlacesGeoClient
                .getPlacesGeo(GooglePlacesGeoParamsBuilder.getLatLng(latLng));
        call.enqueue(new Callback<PlacesGeoResponse>() {
            @Override
            public void onResponse(Call<PlacesGeoResponse> call, Response<PlacesGeoResponse> response) {
                if(response.isSuccessful()){
                    if(listener!=null)
                        listener.onPlacesResponse(response.body().results.get(0).placeID,RESPONE_SUCCESS);
                }
            }

            @Override
            public void onFailure(Call<PlacesGeoResponse> call, Throwable t) {
                Log.d("MapsActivity","DirectionsQuery:failure");
                if(listener != null)
                    listener.onPlacesResponse(null,RESPONSE_FAILURE);
            }
        });

    }
    public static class Builder{
        GooglePlacesGeoQuery query = new GooglePlacesGeoQuery();
        public Builder withLatLng(LatLng latLng){
            query.latLng = latLng;
            return this;
        }
        public GooglePlacesGeoQuery build(){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            query.retrofit = new Retrofit
                    .Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            query.googlePlacesGeoClient = query.retrofit.create(RetroGooglePlacesGeoClient.class);
            return query;
        }
    }
}
