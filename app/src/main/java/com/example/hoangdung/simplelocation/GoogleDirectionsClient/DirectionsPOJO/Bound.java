package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/23/17.
 */

public class Bound implements Parcelable {
    @SerializedName("northeast")
    @Expose
    public Location northeast;

    @SerializedName("southwest")
    @Expose
    public Location southwest;

    protected Bound(Parcel in) {
        northeast = in.readParcelable(Location.class.getClassLoader());
        southwest = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<Bound> CREATOR = new Creator<Bound>() {
        @Override
        public Bound createFromParcel(Parcel in) {
            return new Bound(in);
        }

        @Override
        public Bound[] newArray(int size) {
            return new Bound[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(northeast, flags);
        dest.writeParcelable(southwest, flags);
    }
}
