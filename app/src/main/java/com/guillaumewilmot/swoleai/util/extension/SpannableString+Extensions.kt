package com.guillaumewilmot.swoleai.util.extension

import android.text.SpannableString
import android.text.style.CharacterStyle
import android.util.Log

fun SpannableString.withSpans(text: String, vararg spans: CharacterStyle): SpannableString {
    val indexStart = indexOf(text, ignoreCase = true)
    val indexEnd = indexStart + text.length
    if (indexStart < 0 || indexEnd > length) {
        Log.e("Spannable Extensions", "span text not found!")
        return this
    }
    spans.forEach {
        this.setSpan(it, indexStart, indexEnd, 0)
    }
    return this
}
