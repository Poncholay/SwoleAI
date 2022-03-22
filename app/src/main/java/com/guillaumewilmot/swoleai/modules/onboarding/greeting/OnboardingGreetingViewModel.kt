package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.app.Application
import com.guillaumewilmot.swoleai.R
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
class OnboardingGreetingViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application) {

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_greeting_title_text)
    val descriptionText = application.getString(R.string.app_onboarding_greeting_description_text)
    val nextButtonText = application.getString(
        R.string.app_onboarding_greeting_continue_button_text
    )

    /**
     * Playground //TODO remove
     */

    private val _user = dataStorage.dataHolder.userField

    val titleTextTest: Flowable<String> = _user
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map {
            "${application.getString(R.string.app_onboarding_greeting_title_text)} ${it.value?.name ?: ""}"
        }
}