package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Step implements Parcelable {

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

    @SerializedName("html_instructions")
    @Expose
    public String htmlInstructions;

    @SerializedName("travel_mode")
    @Expose
    public String travelMode;

    @SerializedName("polyline")
    @Expose
    public Polyline polyline;

    @SerializedName("maneuver")
    @Expose
    public String maneuver;

    @SerializedName("steps")
    @Expose
    public ArrayList<Step> steps = new ArrayList<>();

    @SerializedName("transit_details")
    @Expose
    public TransitDetails transitDetails;

    protected Step(Parcel in) {
        distance = in.readParcelable(Distance.class.getClassLoader());
        duration = in.readParcelable(Duration.class.getClassLoader());
        startLocation = in.readParcelable(Location.class.getClassLoader());
        endLocation = in.readParcelable(Location.class.getClassLoader());
        htmlInstructions = in.readString();
        travelMode = in.readString();
        polyline = in.readParcelable(Polyline.class.getClassLoader());
        maneuver = in.readString();
        steps = in.createTypedArrayList(Step.CREATOR);
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
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
        dest.writeString(htmlInstructions);
        dest.writeString(travelMode);
        dest.writeParcelable(polyline, flags);
        dest.writeString(maneuver);
        dest.writeTypedList(steps);
    }


    //-------------------------------Parcelable Parts----------------------------------------------
}
