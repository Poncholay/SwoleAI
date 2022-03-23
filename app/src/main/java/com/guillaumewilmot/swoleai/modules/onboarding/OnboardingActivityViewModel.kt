package com.guillaumewilmot.swoleai.modules.onboarding

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingActivityViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _user by lazy { dataStorage.dataHolder.userField }

    /**
     * UI
     */

    val onboardingSteps: Flowable<List<OnboardingActivity.Step>> = _user
        .map { user ->
            if (user.value == null) {
                listOf(OnboardingActivity.Step.GREETING, OnboardingActivity.Step.ENTER_NAME)
            } else {
                OnboardingActivity.onboardingSteps(user.value)
            }
        }
        .take(1)
        .observeOn(AndroidSchedulers.mainThread())
}