package com.example.hoangdung.simplelocation

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by hoangdung on 11/16/17.
 */

class CustomViewPager : ViewPager {
    var swipable = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return swipable && super.onTouchEvent(ev)
    }
}
