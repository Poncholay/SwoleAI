package com.guillaumewilmot.swoleai.modules.home.setting

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeSettingsViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application), HasLoader by HasLoaderImpl() {
    fun deleteAccount() = dataStorage.toStorage(DataDefinition.USER, null)
        ?.observeOn(AndroidSchedulers.mainThread())
        ?.linkToLoader(this)
}