package com.example.hoangdung.simplelocation;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.hoangdung.simplelocation.GoogleDirectionsClient.DirectionsPOJO.Location;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

/**
 * Created by hoangdung on 11/20/17.
 */

public class CustomSlidingUpPanelLayout extends SlidingUpPanelLayout {
    public CustomSlidingUpPanelLayout(Context context) {
        super(context);
    }

    public CustomSlidingUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSlidingUpPanelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return false;
    }



}
