package com.guillaumewilmot.swoleai.util.loading

import io.reactivex.rxjava3.core.Observable

@Suppress("PropertyName")
/** We want */
interface HasLoader {
    fun pushLoading()
    fun popLoading()
    val _loaderIsLoading: Observable<Boolean>
    val _loaderVisibility: Observable<Int>
}