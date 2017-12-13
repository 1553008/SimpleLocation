package com.example.hoangdung.simplelocation.NearestPlacesClient.NearestPlacesPOJO;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 12/8/17.
 */

public class FoodShop implements Parcelable {
    @SerializedName("lat")
    @Expose
    public double lat;

    @SerializedName("lng")
    @Expose
    public double lng;

    @SerializedName("shop_id")
    @Expose
    public long shopID;

    @SerializedName("averageRatings")
    @Expose
    public double averageRatings;

    @SerializedName("avartar")
    @Expose
    public String avartar;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("address")
    @Expose
    public String address;

    @SerializedName("numOfRatings")
    @Expose
    public long numOfRatings;

    @SerializedName("numOfPhotos")
    @Expose
    public long numOfPhotos;


    public FoodShop() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeLong(this.shopID);
        dest.writeDouble(this.averageRatings);
        dest.writeString(this.avartar);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeLong(this.numOfRatings);
        dest.writeLong(this.numOfPhotos);
    }

    protected FoodShop(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.shopID = in.readLong();
        this.averageRatings = in.readDouble();
        this.avartar = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.numOfRatings = in.readLong();
        this.numOfPhotos = in.readLong();
    }

    public static final Creator<FoodShop> CREATOR = new Creator<FoodShop>() {
        @Override
        public FoodShop createFromParcel(Parcel source) {
            return new FoodShop(source);
        }

        @Override
        public FoodShop[] newArray(int size) {
            return new FoodShop[size];
        }
    };
}
