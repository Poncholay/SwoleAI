package com.guillaumewilmot.swoleai.util.extension

import android.app.Activity

fun Activity?.toDp(pixel: Float): Float = this?.let {
    pixel / resources.displayMetrics.density
} ?: 0f