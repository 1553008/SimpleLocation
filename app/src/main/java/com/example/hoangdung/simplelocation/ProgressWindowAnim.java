package com.example.hoangdung.simplelocation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.bcgdv.asia.lib.dots.DotsProgressIndicator;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by hoangdung on 12/8/17.
 */

public class ProgressWindowAnim<T extends View> {

    Context mContext;
    WindowManager windowManager;
    WindowManager.LayoutParams windowParams;
    public boolean isShowing = false;
    private int layoutID;
    public ProgressWindowAnim(Context context, int layoutID){
        mContext = context;
        this.layoutID = layoutID;
        setupView();
    }

    FrameLayout mProgressBarContainer;
    T mProgressBar;
    View mProgressLayout;

    private void setupView(){
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mProgressLayout = LayoutInflater.from(mContext).inflate(layoutID,null);

        mProgressBarContainer = mProgressLayout.findViewById(R.id.progress_bar_container);
        mProgressBarContainer.setBackgroundColor(Color.TRANSPARENT);

        mProgressBar = mProgressLayout.findViewById(R.id.progress_bar);

        windowParams = new WindowManager.LayoutParams(
                metrics.widthPixels, metrics.heightPixels,
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        windowParams.gravity = Gravity.CENTER;
        mProgressLayout.setBackgroundColor(Color.parseColor("#1A000000"));
    }

    public void setCofig(ProgressWindowConfiguration configuration){
        //Set background color
        mProgressLayout.setBackgroundColor(configuration.backgroundColor);

    }
    public void showProgress(){
        if(!mProgressLayout.isAttachedToWindow()){
            windowManager.addView(mProgressLayout,windowParams);
            mProgressBar.setVisibility(View.VISIBLE);
            isShowing = true;
        }
    }

    public void hideProgress(){
        if(mProgressLayout.isAttachedToWindow())
        {
            windowManager.removeViewImmediate(mProgressLayout);
            mProgressBar.setVisibility(View.INVISIBLE);
            isShowing = false;
        }
    }
    public static class ProgressWindowConfiguration{
        public @ColorInt int backgroundColor;

    }
}
