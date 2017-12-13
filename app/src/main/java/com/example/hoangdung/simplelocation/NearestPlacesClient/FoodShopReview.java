package com.example.hoangdung.simplelocation.NearestPlacesClient;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.PropertyName;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hoangdung on 12/12/17.
 */

public class FoodShopReview implements Parcelable {
    @PropertyName("timestamp")
    public Date timeStamp;
    @PropertyName("user_id")
    public String userID;
    @PropertyName("ratings")
    public double ratings;
    @PropertyName("content")
    public String comment;



    public FoodShopReview() {
    }


    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("content",comment);
        map.put("ratings",ratings);
        map.put("user_id",userID);
        map.put("timestamp", FieldValue.serverTimestamp());
        return map;
    }
    public void fromMap(Map<String,Object> map){
        userID = (String) map.get("user_id");
        ratings = (double) map.get("ratings");
        timeStamp = (Date) map.get("timestamp");
        comment = (String) map.get("content");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.timeStamp != null ? this.timeStamp.getTime() : -1);
        dest.writeString(this.userID);
        dest.writeDouble(this.ratings);
        dest.writeString(this.comment);
    }

    protected FoodShopReview(Parcel in) {
        long tmpTimeStamp = in.readLong();
        this.timeStamp = tmpTimeStamp == -1 ? null : new Date(tmpTimeStamp);
        this.userID = in.readString();
        this.ratings = in.readDouble();
        this.comment = in.readString();
    }

    public static final Creator<FoodShopReview> CREATOR = new Creator<FoodShopReview>() {
        @Override
        public FoodShopReview createFromParcel(Parcel source) {
            return new FoodShopReview(source);
        }

        @Override
        public FoodShopReview[] newArray(int size) {
            return new FoodShopReview[size];
        }
    };
}
