package com.example.hoangdung.simplelocation.NearestPlacesClient;

import com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO.NearestPlacesResponse;
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
 * Created by hoangdung on 12/8/17.
 */

public class NearestPlacesQuery {
    public static final int RESPONSE_SUCCESS = 1;
    public static final int RESPONSE_FAILURE = 0;
    private static String url = "https://us-central1-simplelocation-1508805530347.cloudfunctions.net/";
    private RetroNearestPlacesClient placesClient;
    private Retrofit retrofit;
    private LatLng latLng;
    private ArrayList<String> tags;

    public static final int timeout = 2;
    private NearestPlacesQuery(){

    }

    public void query(final OnResponseListener responseListener){
        Call<NearestPlacesResponse> call = placesClient.getNearestPlaces(String.valueOf(latLng.latitude),
                String.valueOf(latLng.longitude),
                NearestPlacesParamsBuilder.getFoodCategoriesParams(tags));
        call.enqueue(new Callback<NearestPlacesResponse>() {
            @Override
            public void onResponse(Call<NearestPlacesResponse> call, Response<NearestPlacesResponse> response) {
                //if response is successful
                if(response.code() == 200 && response.body().shops!= null && response.body().shops.size() !=0)
                {
                    responseListener.onResponseComplete(response.body(),RESPONSE_SUCCESS);
                }
                else
                {
                    responseListener.onResponseComplete(null,RESPONSE_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<NearestPlacesResponse> call, Throwable t) {
                responseListener.onResponseComplete(null,RESPONSE_FAILURE);
            }
        });
    }
    public interface OnResponseListener{
        void onResponseComplete(NearestPlacesResponse nearestPlacesResponse,int resultCode);
    }

    public static class Builder{
        NearestPlacesQuery query = new NearestPlacesQuery();
        public Builder with(LatLng latLng){
            query.latLng = latLng;
            return this;
        }
        public Builder with(ArrayList<String> tags){
            query.tags = tags;
            return this;
        }
        public NearestPlacesQuery build(){
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(timeout, TimeUnit.MINUTES)
                    .readTimeout(timeout,TimeUnit.MINUTES)
                    .writeTimeout(timeout,TimeUnit.MINUTES)
                    .build();
            query.retrofit = new Retrofit
                    .Builder()
                    .baseUrl(url)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            query.placesClient = query.retrofit.create(RetroNearestPlacesClient.class);
            return query;
        }
    }
}
