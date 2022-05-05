package com.guillaumewilmot.swoleai.modules.onboarding.stats

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingStatsViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    HasLoader by HasLoaderImpl() {

    private val _user = dataStorage.dataHolder.userField

    private val _isMaleSubject = BehaviorSubject.createDefault(true)
    private val _heightSubject = BehaviorSubject.createDefault(150)
    private val _weightSubject = BehaviorSubject.createDefault(80000)

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_stats_title_text)
    val nextButtonText = application.getString(
        R.string.app_onboarding_stats_continue_button_text
    )

    val isMale: Observable<Boolean> = _isMaleSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    val height: Observable<Int> = _heightSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    val weight: Observable<Int> = _weightSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * LOGIC
     */

    fun setSex(isMale: Boolean) {
        _isMaleSubject.onNext(isMale)
    }

    fun onHeightChanged(height: Int) {
        _heightSubject.onNext(height)
    }

    fun onWeightChanged(weight: Int) {
        _weightSubject.onNext(weight)
    }

    fun init(): Completable = _user.linkToLoader(this)
        .take(1)
        .switchMapCompletable { user ->
            user.value?.isMale?.let { _isMaleSubject.onNext(it) }
            user.value?.height?.let { _heightSubject.onNext(it) }
            user.value?.weight?.let { _weightSubject.onNext(it) }
            Completable.complete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun onNext(): Completable? {
        val isMale = _isMaleSubject.value ?: return null
        val height = _heightSubject.value ?: return null
        val weight = _weightSubject.value ?: return null

        return _user.linkToLoader(this)
            .take(1)
            .switchMapCompletable { user ->
                val newUser = user.value ?: UserModel()
                dataStorage.toStorage(DataDefinition.USER, newUser.apply {
                    this.isMale = isMale
                    this.height = height
                    this.weight = weight
                })
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}