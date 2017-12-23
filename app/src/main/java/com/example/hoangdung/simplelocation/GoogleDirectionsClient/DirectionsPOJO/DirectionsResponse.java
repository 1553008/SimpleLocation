package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class DirectionsResponse implements Parcelable {

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("geocoded_waypoints")
    @Expose
    public ArrayList<GeocodedWaypoint> geocodedWaypoints = new ArrayList<>();

    @SerializedName("routes")
    @Expose
    public ArrayList<Route> routes = new ArrayList<>();


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeTypedList(this.geocodedWaypoints);
        dest.writeTypedList(this.routes);
    }

    public DirectionsResponse() {
    }

    protected DirectionsResponse(Parcel in) {
        this.status = in.readString();
        this.geocodedWaypoints = in.createTypedArrayList(GeocodedWaypoint.CREATOR);
        this.routes = in.createTypedArrayList(Route.CREATOR);
    }

    public static final Parcelable.Creator<DirectionsResponse> CREATOR = new Parcelable.Creator<DirectionsResponse>() {
        @Override
        public DirectionsResponse createFromParcel(Parcel source) {
            return new DirectionsResponse(source);
        }

        @Override
        public DirectionsResponse[] newArray(int size) {
            return new DirectionsResponse[size];
        }
    };
}
