package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/23/17.
 */

public class TransitDetails implements Parcelable {


    @SerializedName("arrival_stop")
    @Expose
    public Stop arrivalStop;

    @SerializedName("departure_stop")
    @Expose
    public Stop departureStop;

    @SerializedName("arrival_time")
    @Expose
    public Time arrivalTime;

    @SerializedName("departure_time")
    @Expose
    public Time departTime;

    @SerializedName("line")
    @Expose
    public Line line;


    static public class Stop implements Parcelable {
        @SerializedName("location")
        @Expose
        public Location location;

        @SerializedName("name")
        public String name;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.location, flags);
            dest.writeString(this.name);
        }

        public Stop() {
        }

        protected Stop(Parcel in) {
            this.location = in.readParcelable(Location.class.getClassLoader());
            this.name = in.readString();
        }

        public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() {
            @Override
            public Stop createFromParcel(Parcel source) {
                return new Stop(source);
            }

            @Override
            public Stop[] newArray(int size) {
                return new Stop[size];
            }
        };
    }

    static public class Line implements Parcelable {
        @SerializedName("name")
        @Expose
        public String name;

        @SerializedName("vehicle")
        @Expose
        public Vehicle vehicle;
        public static class Vehicle implements Parcelable {
            @SerializedName("name")
            @Expose
            public String name;

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeString(this.name);
            }

            public Vehicle() {
            }

            protected Vehicle(Parcel in) {
                this.name = in.readString();
            }

            public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
                @Override
                public Vehicle createFromParcel(Parcel source) {
                    return new Vehicle(source);
                }

                @Override
                public Vehicle[] newArray(int size) {
                    return new Vehicle[size];
                }
            };
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeParcelable(this.vehicle, flags);
        }

        public Line() {
        }

        protected Line(Parcel in) {
            this.name = in.readString();
            this.vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        }

        public static final Creator<Line> CREATOR = new Creator<Line>() {
            @Override
            public Line createFromParcel(Parcel source) {
                return new Line(source);
            }

            @Override
            public Line[] newArray(int size) {
                return new Line[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.arrivalStop, flags);
        dest.writeParcelable(this.departureStop, flags);
        dest.writeParcelable(this.arrivalTime, flags);
        dest.writeParcelable(this.departTime, flags);
        dest.writeParcelable(this.line, flags);
    }

    public TransitDetails() {
    }

    protected TransitDetails(Parcel in) {
        this.arrivalStop = in.readParcelable(Stop.class.getClassLoader());
        this.departureStop = in.readParcelable(Stop.class.getClassLoader());
        this.arrivalTime = in.readParcelable(Time.class.getClassLoader());
        this.departTime = in.readParcelable(Time.class.getClassLoader());
        this.line = in.readParcelable(Line.class.getClassLoader());
    }

    public static final Parcelable.Creator<TransitDetails> CREATOR = new Parcelable.Creator<TransitDetails>() {
        @Override
        public TransitDetails createFromParcel(Parcel source) {
            return new TransitDetails(source);
        }

        @Override
        public TransitDetails[] newArray(int size) {
            return new TransitDetails[size];
        }
    };
}
