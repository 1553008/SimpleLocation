package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/22/17.
 */

public class Time implements Parcelable{
    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("time_zone")
    @Expose
    public String timeZone;

    @SerializedName("value")
    @Expose
    public int value;


    protected Time(Parcel in) {
        text = in.readString();
        timeZone = in.readString();
        value = in.readInt();
    }

    public static final Creator<Time> CREATOR = new Creator<Time>() {
        @Override
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        @Override
        public Time[] newArray(int size) {
            return new Time[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(timeZone);
        dest.writeInt(value);
    }
}
