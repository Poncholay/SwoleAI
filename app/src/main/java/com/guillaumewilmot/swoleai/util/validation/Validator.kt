package com.guillaumewilmot.swoleai.util.validation

import android.content.Context
import com.guillaumewilmot.swoleai.R

open class Validator(
    private val validator: (s: String) -> Boolean,
    private val error: String
) : ValidatesString {
    override fun validate(string: String): Boolean = validator(string)
    override fun error(): String = error
}

class ValidatorNotBlank(context: Context) : Validator(
    validator = { s: String -> s.isNotBlank() },
    error = context.getString(R.string.app_field_validator_not_blank_error)
)

class ValidatorIsDigits(context: Context) : Validator(
    validator = { s: String -> s.toIntOrNull() != null },
    error = context.getString(R.string.app_field_validator_not_number_error)
)
