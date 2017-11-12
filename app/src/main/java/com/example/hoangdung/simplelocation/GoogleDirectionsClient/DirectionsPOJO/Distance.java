package com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hoangdung on 11/11/17.
 */

public class Distance {
    @SerializedName("text")
    @Expose
    String text;

    @SerializedName("value")
    @Expose
    int value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
