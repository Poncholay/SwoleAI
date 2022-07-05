package com.guillaumewilmot.swoleai.modules.home.activesession

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
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
) : ParentViewModel(application),
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage) {

    val toolbarActiveSessionText: Flowable<String> = activeSession.map { activeSession ->
        activeSession.value?.let {
            application.getString(R.string.app_session_day_name, it.fullName)
        } ?: ""
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}