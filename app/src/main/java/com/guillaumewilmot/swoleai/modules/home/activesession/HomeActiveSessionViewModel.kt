package com.guillaumewilmot.swoleai.modules.home.activesession

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeActiveSessionViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _currentSession = dataStorage.dataHolder.activeSessionField

    val toolbarCurrentSessionText: Flowable<String> = _currentSession.map { currentSession ->
        currentSession.value?.name ?: ""
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}