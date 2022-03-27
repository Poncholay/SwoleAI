package com.guillaumewilmot.swoleai.util.validation

import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.RxImmediateSchedulerRule
import com.guillaumewilmot.swoleai.model.Nullable
import io.mockk.every
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FieldValidatorTest : BaseUnitTest() {

    @Rule
    @JvmField
    val rxRule = RxImmediateSchedulerRule()

    @Before
    fun setup() {
        every { application.getString(R.string.app_field_validator_not_blank_error) } returns BLANK_ERROR
        every { application.getString(R.string.app_field_validator_not_number_error) } returns NOT_NUMBER_ERROR
    }

    @Test
    fun givenNotBlankValidator_andStartValidateWhenEmpty_andStartValidateWhenInFocus_testValidation() {
        val validator = FieldValidator(
            validator = ValidatorNotBlank(application),
            startValidateWhenEmpty = true,
            startValidateWhenInFocus = true
        )

        val consumerFieldValue = spyConsumer(validator.fieldValue)
        val consumerFieldError = spyConsumer(validator.fieldError)
        val consumerFieldValidity = spyConsumer(validator.fieldValidity)

        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed until first event
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(true)
        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged("a")
        testObserver(validator.fieldValue).assertValue("a")
        testObserver(validator.fieldError).assertValue(Nullable(null))
        testObserver(validator.fieldValidity).assertValue(true)

        validator.onFieldChanged(" ")
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        verifySequence {
            consumerFieldValue.accept("") // Default
            consumerFieldError.accept(Nullable(null)) // Default
            consumerFieldValidity.accept(false) // Default

            consumerFieldError.accept(Nullable(BLANK_ERROR)) // After gain focus

            consumerFieldValue.accept("a")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(true)

            consumerFieldValue.accept(" ")
            consumerFieldError.accept(Nullable(BLANK_ERROR))
            consumerFieldValidity.accept(false)
        }
    }

    @Test
    fun givenNotBlankValidator_andNotStartValidateWhenEmpty_andStartValidateWhenInFocus_testValidation() {
        val validator = FieldValidator(
            validator = ValidatorNotBlank(application),
            startValidateWhenEmpty = false,
            startValidateWhenInFocus = true
        )

        val consumerFieldValue = spyConsumer(validator.fieldValue)
        val consumerFieldError = spyConsumer(validator.fieldError)
        val consumerFieldValidity = spyConsumer(validator.fieldValidity)

        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed until first event
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(true)
        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed because empty
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged("a")
        testObserver(validator.fieldValue).assertValue("a")
        testObserver(validator.fieldError).assertValue(Nullable(null))
        testObserver(validator.fieldValidity).assertValue(true)

        validator.onFieldChanged(" ")
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        verifySequence {
            consumerFieldValue.accept("") // Default
            consumerFieldError.accept(Nullable(null)) // Default
            consumerFieldValidity.accept(false) // Default

            consumerFieldValue.accept("a")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(true)

            consumerFieldError.accept(Nullable(null)) //_canShowErrorField becomes true

            consumerFieldValue.accept(" ")
            consumerFieldError.accept(Nullable(BLANK_ERROR))
            consumerFieldValidity.accept(false)
        }
    }

    @Test
    fun givenNotBlankValidator_andStartValidateWhenEmpty_andNotStartValidateWhenInFocus_testValidation() {
        val validator = FieldValidator(
            validator = ValidatorNotBlank(application),
            startValidateWhenEmpty = true,
            startValidateWhenInFocus = false
        )

        val consumerFieldValue = spyConsumer(validator.fieldValue)
        val consumerFieldError = spyConsumer(validator.fieldError)
        val consumerFieldValidity = spyConsumer(validator.fieldValidity)

        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed until first event
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(true)
        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed because in focus
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(false)
        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged("a")
        testObserver(validator.fieldValue).assertValue("a")
        testObserver(validator.fieldError).assertValue(Nullable(null))
        testObserver(validator.fieldValidity).assertValue(true)

        validator.onFieldChanged(" ")
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR)) //No error displayed because in focus
        testObserver(validator.fieldValidity).assertValue(false)

        verifySequence {
            consumerFieldValue.accept("") // Default
            consumerFieldError.accept(Nullable(null)) // Default
            consumerFieldValidity.accept(false) // Default

            //Add focus
            //Remove focus

            consumerFieldError.accept(Nullable(BLANK_ERROR)) //_canShowErrorField becomes true

            consumerFieldValue.accept("a")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(true)

            consumerFieldValue.accept(" ")
            consumerFieldError.accept(Nullable(BLANK_ERROR))
            consumerFieldValidity.accept(false)
        }
    }

    @Test
    fun givenNotBlankValidator_andNotStartValidateWhenEmpty_andNotStartValidateWhenInFocus_testValidation() {
        val validator = FieldValidator(
            validator = ValidatorNotBlank(application),
            startValidateWhenEmpty = false,
            startValidateWhenInFocus = false
        )

        val consumerFieldValue = spyConsumer(validator.fieldValue)
        val consumerFieldError = spyConsumer(validator.fieldError)
        val consumerFieldValidity = spyConsumer(validator.fieldValidity)

        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed until first event
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(true)
        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed because empty
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged("a")
        testObserver(validator.fieldValue).assertValue("a")
        testObserver(validator.fieldError).assertValue(Nullable(null))
        testObserver(validator.fieldValidity).assertValue(true)

        validator.onFieldChanged(" ")
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed because in focus
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(false)
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        verifySequence {
            consumerFieldValue.accept("") // Default
            consumerFieldError.accept(Nullable(null)) // Default
            consumerFieldValidity.accept(false) // Default

            consumerFieldValue.accept("a")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(true)

            consumerFieldValue.accept(" ")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(false)

            //Remove focus

            consumerFieldError.accept(Nullable(BLANK_ERROR)) //_canShowErrorField becomes true
        }
    }

    @Test
    fun givenNotBlankAndIsDigitsValidator_andStartValidateWhenEmpty_andStartValidateWhenInFocus_testValidation() {
        val validator = FieldValidator(
            validators = listOf(ValidatorNotBlank(application), ValidatorIsDigits(application)),
            startValidateWhenEmpty = true,
            startValidateWhenInFocus = true
        )

        val consumerFieldValue = spyConsumer(validator.fieldValue)
        val consumerFieldError = spyConsumer(validator.fieldError)
        val consumerFieldValidity = spyConsumer(validator.fieldValidity)

        testObserver(validator.fieldValue).assertValue("")
        testObserver(validator.fieldError).assertValue(Nullable(null)) //No error displayed until first event
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldFocusChanged(true) //Trigger first event

        validator.onFieldChanged("a")
        testObserver(validator.fieldValue).assertValue("a")
        testObserver(validator.fieldError).assertValue(Nullable(NOT_NUMBER_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged("1234")
        testObserver(validator.fieldValue).assertValue("1234")
        testObserver(validator.fieldError).assertValue(Nullable(null))
        testObserver(validator.fieldValidity).assertValue(true)

        validator.onFieldChanged("123 345")
        testObserver(validator.fieldValue).assertValue("123 345")
        testObserver(validator.fieldError).assertValue(Nullable(NOT_NUMBER_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        validator.onFieldChanged(" ")
        testObserver(validator.fieldValue).assertValue(" ")
        testObserver(validator.fieldError).assertValue(Nullable(BLANK_ERROR))
        testObserver(validator.fieldValidity).assertValue(false)

        verifySequence {
            consumerFieldValue.accept("") // Default
            consumerFieldError.accept(Nullable(null)) // Default
            consumerFieldValidity.accept(false) // Default

            //Add focus

            consumerFieldError.accept(Nullable(BLANK_ERROR))

            consumerFieldValue.accept("a")
            consumerFieldError.accept(Nullable(NOT_NUMBER_ERROR))
            consumerFieldValidity.accept(false)

            consumerFieldValue.accept("1234")
            consumerFieldError.accept(Nullable(null))
            consumerFieldValidity.accept(true)

            consumerFieldValue.accept("123 345")
            consumerFieldError.accept(Nullable(NOT_NUMBER_ERROR))
            consumerFieldValidity.accept(false)

            consumerFieldValue.accept(" ")
            consumerFieldError.accept(Nullable(BLANK_ERROR))
            consumerFieldValidity.accept(false)
        }
    }

    companion object {
        const val BLANK_ERROR = "Blank"
        const val NOT_NUMBER_ERROR = "Not number"
    }
}