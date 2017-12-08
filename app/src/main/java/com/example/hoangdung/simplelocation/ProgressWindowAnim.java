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

import java.util.ArrayList;

/**
 * Created by hoangdung on 12/8/17.
 */

public class ProgressWindowAnim {

    Context mContext;
    WindowManager windowManager;
    WindowManager.LayoutParams windowParams;
    public static ProgressWindowAnim instance;

    ProgressWindowAnim(Context context){
        mContext = context;
        setupView();
    }
    public static ProgressWindowAnim getInstance(Context context){
        synchronized (ProgressWindowAnim.class){
            if(instance == null)
                instance = new ProgressWindowAnim(context);
        }
        return instance;
    }

    FrameLayout mProgressBarContainer;
    DotsProgressIndicator mProgressBar;
    View mProgressLayout;
    ArrayList<Animator> defaultShowAnimator;
    ArrayList<Animator> defaultHideAnimator;
    private void setupView(){
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mProgressLayout = LayoutInflater.from(mContext).inflate(R.layout.progress_window_layout,null);

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
        defaultShowAnimator  =  new ArrayList<>();
        defaultShowAnimator.add(ObjectAnimator.ofFloat(mProgressBarContainer,"alpha",0f,1f));
        defaultHideAnimator = new ArrayList<>();
        defaultHideAnimator.add(ObjectAnimator.ofFloat(mProgressBarContainer,"alpha",1f,0f));
    }

    AnimatorSet showAnimatorSet = new AnimatorSet();
    AnimatorSet hideAnimatorSet = new AnimatorSet();
    public void setCofig(ProgressWindowConfiguration configuration){
        //Set background color
        mProgressLayout.setBackgroundColor(configuration.backgroundColor);

    }
    public void showProgress(){
        windowManager.addView(mProgressLayout,windowParams);
        mProgressBar.setVisibility(View.VISIBLE);
        showAnimatorSet.playTogether(defaultShowAnimator);
    }

    public void hideProgress(){
        windowManager.removeViewImmediate(mProgressLayout);
        mProgressBar.setVisibility(View.INVISIBLE);
        hideAnimatorSet.playTogether(defaultHideAnimator);
    }

    public static class ProgressWindowConfiguration{
        public @ColorInt int backgroundColor;

    }
}
