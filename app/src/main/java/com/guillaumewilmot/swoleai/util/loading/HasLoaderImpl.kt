package com.guillaumewilmot.swoleai.util.loading

import android.view.View
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class HasLoaderImpl : HasLoader {
    private val _loader = BehaviorSubject.createDefault(0)

    override fun pushLoading() {
        _loader.onNext(1)
    }

    override fun popLoading() {
        _loader.onNext(-1)
    }

    override val _loaderIsLoading: Observable<Boolean> = _loader.scan { sum, item -> sum + item }
        .map { sum -> sum > 0 }

    override val _loaderVisibility: Observable<Int> = _loaderIsLoading.map {
        if (it) View.VISIBLE else View.GONE
    }
}

fun <T : Any> Flowable<T>.linkToLoader(source: HasLoader): Flowable<T> {
    return this.doOnSubscribe {
        source.pushLoading()
    }.doFinally {
        source.popLoading()
    }
}
