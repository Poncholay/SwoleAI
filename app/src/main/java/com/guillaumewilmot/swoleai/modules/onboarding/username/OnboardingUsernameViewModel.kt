package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.app.Application
import android.text.Editable
import android.view.View
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import com.guillaumewilmot.swoleai.util.validation.FieldValidator
import com.guillaumewilmot.swoleai.util.validation.ValidatorNotEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class OnboardingUsernameViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application) {

    private val _usernameValidator = FieldValidator(
        validator = ValidatorNotEmpty(application),
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
        .subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())

    val usernameFieldError: Observable<Optional<String>> = _usernameValidator.fieldError
        .subscribeOn(AndroidSchedulers.mainThread())
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

    fun onNext() {
        val username = _usernameValidator.fieldValue.value.takeIf {
            it?.isNotBlank() == true
        } ?: return

        val user = UserModel(name = username)
        dataStorage.toStorage(DataStorage.DataDefinition.USER, user)
    }
}