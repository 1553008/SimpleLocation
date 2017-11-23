package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/11/17.
 */

public class GeocodedWaypoint implements Parcelable{
    @SerializedName("geocoder_status")
    @Expose
    public String geocoderStatus;

    @SerializedName("place_id")
    @Expose
    public String placeID;

    protected GeocodedWaypoint(Parcel in) {
        geocoderStatus = in.readString();
        placeID = in.readString();
    }

    public static final Creator<GeocodedWaypoint> CREATOR = new Creator<GeocodedWaypoint>() {
        @Override
        public GeocodedWaypoint createFromParcel(Parcel in) {
            return new GeocodedWaypoint(in);
        }

        @Override
        public GeocodedWaypoint[] newArray(int size) {
            return new GeocodedWaypoint[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(geocoderStatus);
        dest.writeString(placeID);
    }
}