package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Route implements Parcelable {

    @SerializedName("copyrights")
    @Expose
    public String copyrights;

    @SerializedName("legs")
    @Expose
    public ArrayList<Leg> legs = new ArrayList<>();

    @SerializedName("overview_polyline")
    @Expose
    public Polyline overviewPolyline;

    @SerializedName("bounds")
    @Expose
    public Bound bound;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.copyrights);
        dest.writeTypedList(this.legs);
        dest.writeParcelable(this.overviewPolyline, flags);
        dest.writeParcelable(this.bound, flags);
    }

    public Route() {
    }

    protected Route(Parcel in) {
        this.copyrights = in.readString();
        this.legs = in.createTypedArrayList(Leg.CREATOR);
        this.overviewPolyline = in.readParcelable(Polyline.class.getClassLoader());
        this.bound = in.readParcelable(Bound.class.getClassLoader());
    }

    public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel source) {
            return new Route(source);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
}
