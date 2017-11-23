package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Leg implements Parcelable{
    @SerializedName("distance")
    @Expose
    public Distance distance;

    @SerializedName("duration")
    @Expose
    public Duration duration;

    @SerializedName("start_location")
    @Expose
    public Location startLocation;

    @SerializedName("end_location")
    @Expose
    public Location endLocation;

    @SerializedName("start_address")
    @Expose
    public String startAddress;

    @SerializedName("end_address")
    @Expose
    public String endAddress;

    @SerializedName("steps")
    @Expose
    public ArrayList<Step> steps = new ArrayList<>();

    @SerializedName("duration_in_traffic")
    @Expose
    public Duration durationInTraffic;

    @SerializedName("arrival_time")
    @Expose
    public Time arriveTime;

    @SerializedName("departure_time")
    @Expose
    public Time departTime;


    protected Leg(Parcel in) {
        distance = in.readParcelable(Distance.class.getClassLoader());
        duration = in.readParcelable(Duration.class.getClassLoader());
        startLocation = in.readParcelable(Location.class.getClassLoader());
        endLocation = in.readParcelable(Location.class.getClassLoader());
        startAddress = in.readString();
        endAddress = in.readString();
        steps = in.createTypedArrayList(Step.CREATOR);
        durationInTraffic = in.readParcelable(Duration.class.getClassLoader());
        arriveTime = in.readParcelable(Time.class.getClassLoader());
        departTime = in.readParcelable(Time.class.getClassLoader());
    }

    public static final Creator<Leg> CREATOR = new Creator<Leg>() {
        @Override
        public Leg createFromParcel(Parcel in) {
            return new Leg(in);
        }

        @Override
        public Leg[] newArray(int size) {
            return new Leg[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(distance, flags);
        dest.writeParcelable(duration, flags);
        dest.writeParcelable(startLocation, flags);
        dest.writeParcelable(endLocation, flags);
        dest.writeString(startAddress);
        dest.writeString(endAddress);
        dest.writeTypedList(steps);
        dest.writeParcelable(durationInTraffic, flags);
        dest.writeParcelable(arriveTime, flags);
        dest.writeParcelable(departTime, flags);
    }
}
