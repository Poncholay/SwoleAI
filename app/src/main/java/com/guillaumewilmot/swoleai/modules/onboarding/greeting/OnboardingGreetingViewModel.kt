package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingGreetingViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _user = dataStorage.dataHolder.userField

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_greeting_title_text)
    val descriptionText = application.getString(R.string.app_onboarding_greeting_description_text)
    val nextButtonText = application.getString(
        R.string.app_onboarding_greeting_continue_button_text
    )

    val titleTextTest: Flowable<String> = _user
        .observeOn(AndroidSchedulers.mainThread())
        .map { user ->
            val greeting = application.getString(R.string.app_onboarding_greeting_title_text)
            "$greeting${user.value?.username?.let { " $it" } ?: ""}"
        }
}