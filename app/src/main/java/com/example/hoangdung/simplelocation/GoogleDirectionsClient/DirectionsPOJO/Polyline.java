package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Polyline implements Parcelable{
    @SerializedName("points")
    @Expose
    public String points;

    protected Polyline(Parcel in) {
        points = in.readString();
    }

    public static final Creator<Polyline> CREATOR = new Creator<Polyline>() {
        @Override
        public Polyline createFromParcel(Parcel in) {
            return new Polyline(in);
        }

        @Override
        public Polyline[] newArray(int size) {
            return new Polyline[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(points);
    }
}
