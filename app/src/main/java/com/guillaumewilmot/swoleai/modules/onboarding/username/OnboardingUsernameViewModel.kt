package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingUsernameViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application) {

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_username_title_text)
    val nextButtonText =
        application.getString(R.string.app_onboarding_username_continue_button_text)

}