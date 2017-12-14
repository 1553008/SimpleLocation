package com.example.hoangdung.simplelocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import fr.arnaudguyon.smartfontslib.FontManager;

/**
 * Created by hoangdung on 12/13/17.
 */

@SuppressLint("AppCompatCustomView")
public class CircularTextview extends TextView {
    private float strokeWidth;
    int strokeColor,solidColor;
    public CircularTextview(Context context) {
        super(context);
    }

    public CircularTextview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontManager.getInstance().applyFont(this, attrs);
    }

    public CircularTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public void draw(Canvas canvas) {

        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int  h = this.getHeight();
        int  w = this.getWidth();

        int diameter = ((h > w) ? h : w);
        int radius = diameter/2;

        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2 , diameter / 2, radius, strokePaint);

        canvas.drawCircle(diameter / 2, diameter / 2, radius-strokeWidth, circlePaint);

        super.draw(canvas);
    }

    public void setStrokeWidth(int dp)
    {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp*scale;

    }

    public void setStrokeColor(String color)
    {
        strokeColor = Color.parseColor(color);
    }

    public void setSolidColor(String color)
    {
        solidColor = Color.parseColor(color);

    }
    public void setSolidColor(int color){
        solidColor = color;
    }
    public void setStrokeColor(int color){
        strokeColor = color;
    }
}

