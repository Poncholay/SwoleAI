package com.guillaumewilmot.swoleai.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.guillaumewilmot.swoleai.R

class LockableViewPager : androidx.viewpager.widget.ViewPager {
    private var smooth: Boolean = true
    private var swipeable: Boolean = true

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.LockableViewPager)
            try {
                this.smooth = a.getBoolean(R.styleable.LockableViewPager_smooth, true)
                this.swipeable = a.getBoolean(R.styleable.LockableViewPager_swipeable, true)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                a.recycle()
            }
        }
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, smooth)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = if (swipeable) {
        super.onTouchEvent(event)
    } else false

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = try {
        if (swipeable) {
            super.onInterceptTouchEvent(event)
        } else false
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        false
    }
}