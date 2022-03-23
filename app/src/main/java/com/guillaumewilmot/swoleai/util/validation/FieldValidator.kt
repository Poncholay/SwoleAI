package com.guillaumewilmot.swoleai.util.validation

import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.asOptional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject


class FieldValidator(
    validators: List<Validator>,
    private val startValidateWhenEmpty: Boolean = true,
    private val startValidateWhenInFocus: Boolean = false
) {

    constructor(
        validator: Validator,
        startValidateWhenEmpty: Boolean = true,
        startValidateWhenInFocus: Boolean = false
    ) : this(listOf(validator), startValidateWhenEmpty, startValidateWhenInFocus)

    private val _validators = validators.toMutableList()
    private var _hasContainedText = false
    private var _hasFocus = false

    private val _canShowErrorField = BehaviorSubject.createDefault(false)
    private val _fieldValue = BehaviorSubject.createDefault("")

    private val _fieldErrors = _fieldValue.distinctUntilChanged().map { fieldValue ->
        _validators.mapNotNull { validator ->
            if (validator.validate(fieldValue)) null else validator.error()
        }
    }

    private val _fieldValidity = _fieldErrors.map { errors ->
        errors.isEmpty()
    }

    private val _fieldError: Observable<Optional<String>> = Observable.combineLatest(
        _fieldErrors,
        _canShowErrorField.distinctUntilChanged()
    ) { fieldError, canShowErrorField ->
        if (canShowErrorField != true) {
            null.asOptional()
        } else {
            fieldError.firstOrNull().asOptional() //Could join errors
        }
    }

    private fun reCalculateCanShowError() {
        val current = _canShowErrorField.value ?: false
        val textCondition = startValidateWhenEmpty || _hasContainedText
        val focusCondition = startValidateWhenInFocus || _hasFocus.not()
        _canShowErrorField.onNext(current || (textCondition && focusCondition))
    }

    /**
     * Interface
     */

    val fieldValue: BehaviorSubject<String> = _fieldValue
    val fieldValidity: Observable<Boolean> = _fieldValidity.subscribeOn(Schedulers.io())
    val fieldError: Observable<Optional<String>> = _fieldError.subscribeOn(Schedulers.io())

    fun onFieldChanged(value: String) {
        _fieldValue.onNext(value)
        _hasContainedText = _hasContainedText || value.isNotBlank()
        reCalculateCanShowError()
    }

    fun onFieldFocusChanged(isFocused: Boolean) {
        _hasFocus = isFocused
        reCalculateCanShowError()
    }

    fun addValidator(validator: Validator) {
        _validators.add(validator)
        onFieldChanged(_fieldValue.value ?: "")
    }
}