package com.guillaumewilmot.swoleai.modules.home

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.storage.UserStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _user = dataStorage.dataHolder.userField

    /**
     * UI
     */

    val titleText: Flowable<String> = _user
        .map {
            "${application.getString(R.string.app_onboarding_greeting_text)} ${it.value?.name ?: ""}"
        }
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())

    val redirectToOnboarding: Flowable<Boolean> = _user
        .map { it.value == null }
//        .filter { true }
        .filter { false } //TODO : remove and uncomment
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())

    init {
        updateUserWithTimer()
    }

    private fun updateUserWithTimer() {
        var userIndex = 1
        Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
            .subscribe {
                dataStorage.toStorage(UserStorage.USER, UserModel(name = "Message ${userIndex++}"))
            }
    }
}