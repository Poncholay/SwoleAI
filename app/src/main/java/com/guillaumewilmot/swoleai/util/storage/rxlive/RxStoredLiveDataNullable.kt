package com.guillaumewilmot.swoleai.util.storage.rxlive

import android.content.Context
import kotlin.reflect.KFunction1

open class RxStoredLiveDataNullable<T : Any>(
    key: String,
    nonNullInitializer: KFunction1<Context?, T?>
) : RxStoredLiveData<Optional<T>>(
    key = key,
    initializer = { context ->
        nonNullInitializer(context).carry()
    }
)

data class Optional<T>(val value: T?)

fun <T> T?.carry() = Optional(this)