package com.guillaumewilmot.swoleai.util.storage.rxlive

import android.content.Context
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlin.reflect.KFunction1

open class RxStoredLiveData<T : Any>(
    val key: String,
    protected val initializer: (Context) -> T
) {
    constructor(
        key: String,
        initializer: KFunction1<Context?, T>
    ) : this(key, { context -> initializer(context) })

    private lateinit var field: BehaviorSubject<T>
    private fun isInitialized(): Boolean = ::field.isInitialized

    protected open fun initialize(context: Context) = initializer(context)

    fun get(context: Context): BehaviorSubject<T> = when (isInitialized()) {
        true -> field
        false -> BehaviorSubject.createDefault(initializer(context)).also {
            field = it
        }
    }

    fun reload(context: Context) {
        if (isInitialized()) {
            field.onNext(initializer(context))
        }
    }
}