package com.guillaumewilmot.swoleai.modules.home

import android.app.Application
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.FakeProgram
import com.guillaumewilmot.swoleai.modules.onboarding.OnboardingActivity
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application) {

    init {
        //FIXME : TMP the program is reinitialized with hardcoded data
        dataStorage.toStorage(DataDefinition.PROGRAM, FakeProgram.fakeProgram)
    }

    private val _user = dataStorage.dataHolder.userField

    /**
     * UI
     */

    val redirectToOnboarding: Flowable<Boolean> = _user
        .map { user ->
            OnboardingActivity.onboardingSteps(user.value).isNotEmpty()
        }
        .filter { it == true }
        .observeOn(AndroidSchedulers.mainThread())
}