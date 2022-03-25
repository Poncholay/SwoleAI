package com.guillaumewilmot.swoleai.util.extension

import android.content.Context
import android.util.TypedValue
import java.util.*

fun Context?.pixelToDp(pixel: Float): Float = this?.let {
    pixel / resources.displayMetrics.density
} ?: 0f

fun Context?.dpToPixel(dp: Float): Float = this?.let {
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
} ?: 0f

fun Context?.getUserLocale(): Locale {
    this?.let {
        try {
            return resources.configuration.locales.get(0)
        } catch (e: Exception) {
        }
    }
    return Locale.ENGLISH
}