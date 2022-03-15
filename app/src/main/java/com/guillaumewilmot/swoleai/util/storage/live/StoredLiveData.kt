package com.guillaumewilmot.swoleai.util.storage.live

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.reflect.KFunction1

open class StoredLiveData<T : Any?>(
    val key: String,
    protected val initializer: KFunction1<Context?, T>
) {
    private lateinit var field: MutableLiveData<T>
    private fun isInitialized(): Boolean = ::field.isInitialized

    fun get(context: Context): LiveData<T> = when (isInitialized()) {
        true -> field
        false -> MutableLiveData<T>().apply {
            value = initializer(context)
            field = this
        }
    }

    fun reload(context: Context) {
        if (isInitialized()) {
            field.value = initializer(context)
        }
    }
}