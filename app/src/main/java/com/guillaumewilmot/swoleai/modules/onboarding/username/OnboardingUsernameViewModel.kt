package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.app.Application
import android.text.Editable
import android.view.View
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.validation.FieldValidator
import com.guillaumewilmot.swoleai.util.validation.ValidatorNotBlank
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingUsernameViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application), HasLoader by HasLoaderImpl() {

    private val _user by lazy { dataStorage.dataHolder.userField }

    private val _usernameValidator = FieldValidator(
        validator = ValidatorNotBlank(application),
        startValidateWhenEmpty = false,
        startValidateWhenInFocus = true
    )

    /**
     * UI
     */

    val titleText = application.getString(R.string.app_onboarding_username_title_text)
    val nextButtonText = application.getString(
        R.string.app_onboarding_username_continue_button_text
    )

    val nextButtonEnabled: Observable<Boolean> = _usernameValidator.fieldValidity
        .observeOn(AndroidSchedulers.mainThread())

    val usernameFieldError: Observable<Nullable<String>> = _usernameValidator.fieldError
        .observeOn(AndroidSchedulers.mainThread())

    /**
     * Logic
     */

    val usernameFieldChangeListener: (Editable?) -> Unit = { s ->
        _usernameValidator.onFieldChanged(s?.toString() ?: "")
    }

    val usernameFieldFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        _usernameValidator.onFieldFocusChanged(hasFocus)
    }

    fun onNext(): Completable? {
        val username = _usernameValidator.fieldValue.value.takeIf {
            it?.isNotBlank() == true
        } ?: return null

        return _user.take(1)
            .linkToLoader(this)
            .switchMapCompletable { user ->
                val newUser = user.value ?: UserModel()
                dataStorage.toStorage(DataDefinition.USER, newUser.apply {
                    this.username = username.trim()
                })
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}