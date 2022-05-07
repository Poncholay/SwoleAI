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
    dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _user = dataStorage.dataHolder.userField

    /**
     * UI
     */

    val onboardingSteps: Flowable<List<OnboardingActivity.Step>> = _user
        .take(1)
        .map { user ->
            OnboardingActivity.onboardingSteps(user.value)
        }
        .distinctUntilChanged()
        .observeOn(AndroidSchedulers.mainThread())
}