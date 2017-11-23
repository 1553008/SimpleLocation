package com.example.hoangdung.simplelocation.GoogleDirectionsClient;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/12/17.
 * This is Utility class for building URL's params from Java Object
 */

public class GoogleDirectionsParamsBuilder {

     public static String getParamsByLatLng(LatLng latLng){
        if(latLng == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(latLng.latitude));
        stringBuilder.append(',');
        stringBuilder.append(String.valueOf(latLng.longitude));
        return stringBuilder.toString();
    }

    static String getParamsByLatLngArr(ArrayList<LatLng> latLngArrayList,boolean encoded){
        if(latLngArrayList == null || latLngArrayList.isEmpty())
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        if(encoded)
        {
            stringBuilder.append("via:");
            stringBuilder.append("enc:");
            stringBuilder.append(PolyUtil.encode(latLngArrayList));
            stringBuilder.append(':');
            return stringBuilder.toString();
        }

        for(LatLng latLng : latLngArrayList){
            stringBuilder.append(getParamsByLatLng(latLng));
            stringBuilder.append('|');
        }
        return stringBuilder.toString();
    }

    static String getParamsByTravelMode(GoogleDirectionsQuery.TRAVEL_MODE travelMode){
        switch (travelMode){
            case DRIVING:
                return "driving";
            case BICYCLING:
                return "bicycling";
            case TRANSIT:
                return "transit";
        }
        return "";
    }

    static String getParamsByTransitMode(GoogleDirectionsQuery.TRANSIT_MODE transitMode){
        switch (transitMode){
            case BUS:
                return "bus";
        }
        return "";
    }
    static String getParamsByTransitPreference(GoogleDirectionsQuery.TRANSIT_ROUTING_PREFERENCE transitRoutingPreference){
        switch (transitRoutingPreference){
            case LESS_WALKING:
                return "less_walking";
            case FEWER_TRANSFERS:
                return "fewer_transfers";
        }
        return "";
    }
    static String getParamsByTrafficModel(GoogleDirectionsQuery.TRAFFIC_MODEL traffic_model){
        switch (traffic_model){
            case BEST_GUESS:
                return "best_guess";
            case OPTIMISTIC:
                return "optimistic";
            case PESSIMISTIC:
                return "pessimistic";
        }
        return "";
    }

}
