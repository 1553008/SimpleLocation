package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Leg {
    @SerializedName("distance")
    @Expose
    Distance distance;

    @SerializedName("duration")
    @Expose
    Duration duration;

    @SerializedName("start_location")
    @Expose
    Location startLocation;

    @SerializedName("end_location")
    @Expose
    Location endLocation;

    @SerializedName("start_address")
    @Expose
    String startAddress;

    @SerializedName("end_address")
    @Expose
    String endAddress;

    @SerializedName("steps")
    @Expose
    ArrayList<Step> steps = new ArrayList<>();

    @SerializedName("duration_in_traffic")
    @Expose
    Duration durationInTraffic;
    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
}