package com.guillaumewilmot.swoleai.util.extension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

fun <T> Fragment.withFragmentManager(block: (fm: FragmentManager) -> T): T? = if (isAdded) {
    try {
        block(this.parentFragmentManager)
    } catch (e: Exception) {
        null
    }
} else {
    null
}
