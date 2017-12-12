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
    public int shopID;

    @SerializedName("averageRatings")
    @Expose
    public float averageRatings;

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
    public int numOfRatings;

    @SerializedName("numOfPhotos")
    @Expose
    public int numOfPhotos;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(this.shopID);
        dest.writeFloat(this.averageRatings);
        dest.writeString(this.avartar);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeInt(this.numOfRatings);
        dest.writeInt(this.numOfPhotos);
    }

    public FoodShop() {
    }

    protected FoodShop(Parcel in) {
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.shopID = in.readInt();
        this.averageRatings = in.readFloat();
        this.avartar = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.numOfRatings = in.readInt();
        this.numOfPhotos = in.readInt();
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
