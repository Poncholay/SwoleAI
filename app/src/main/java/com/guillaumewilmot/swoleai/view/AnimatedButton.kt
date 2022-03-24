package com.guillaumewilmot.swoleai.view

import android.content.Context
import android.graphics.LightingColorFilter
import android.util.AttributeSet
import android.view.MotionEvent

class AnimatedButton : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        enableClickAnimation()
    }

    fun disableClickAnimation() {
        background?.clearColorFilter().apply { invalidate() }
        setOnTouchListener { _, _ -> false }
    }

    fun enableClickAnimation() {
        setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.background.colorFilter = LightingColorFilter(-0x777778, 0x000000).apply { invalidate() }
                MotionEvent.ACTION_UP -> v.background.clearColorFilter().apply { invalidate() }
                MotionEvent.ACTION_CANCEL -> v.background.clearColorFilter().apply { invalidate() }
            }
            false
        }
    }
}