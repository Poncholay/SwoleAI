package com.guillaumewilmot.swoleai.util.loading

import android.view.View
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class HasLoaderImpl : HasLoader {
    private val _loader = BehaviorSubject.createDefault(0)

    override fun pushLoading() {
        _loader.onNext(1)
    }

    override fun popLoading() {
        _loader.onNext(-1)
    }

    override val loaderIsLoading: Observable<Boolean> = _loader.scan { sum, item -> sum + item }
        .map { sum -> sum > 0 }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    override val loaderVisibility: Observable<Int> = loaderIsLoading.map {
        if (it) View.VISIBLE else View.GONE
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T : Any> Flowable<T>.linkToLoader(source: HasLoader): Flowable<T> {
    return this.doOnSubscribe {
        source.pushLoading()
    }.doFinally {
        source.popLoading()
    }
}

fun <T : Any> Observable<T>.linkToLoader(source: HasLoader): Observable<T> {
    return this.doOnSubscribe {
        source.pushLoading()
    }.doFinally {
        source.popLoading()
    }
}

fun <T : Any> Single<T>.linkToLoader(source: HasLoader): Single<T> {
    return this.doOnSubscribe {
        source.pushLoading()
    }.doFinally {
        source.popLoading()
    }
}