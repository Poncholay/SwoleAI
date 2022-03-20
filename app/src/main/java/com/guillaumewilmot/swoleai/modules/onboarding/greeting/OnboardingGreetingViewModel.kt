package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingGreetingViewModel @Inject constructor(
    application: Application,
) : ParentViewModel(application) {
    /**
     * UI
     */

    val greetingText = application.getString(R.string.app_onboarding_greeting_text)
    val descriptionText = application.getString(R.string.app_onboarding_description_text)
    val nextButtonText = application.getString(R.string.app_onboarding_greeting_continue_button_text)
}