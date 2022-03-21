package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.app.Application
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingGreetingViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application) {

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_greeting_title_text)
    val descriptionText = application.getString(R.string.app_onboarding_greeting_description_text)
    val nextButtonText =
        application.getString(R.string.app_onboarding_greeting_continue_button_text)

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

    private var userIndex = 1

    init {
        _user.first(Optional(null)).subscribe { initialUser ->
            userIndex = initialUser.value?.name?.filter { it.isDigit() }?.toIntOrNull() ?: 1
        }
    }

    val userTimer: Observable<UserModel> = Observable.interval(1000, 1000, TimeUnit.MILLISECONDS)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .map { UserModel(name = "Message ${userIndex++}") }

    fun updateUser(user: UserModel) {
        dataStorage.toStorage(DataStorage.DataDefinition.USER, user)
    }
}