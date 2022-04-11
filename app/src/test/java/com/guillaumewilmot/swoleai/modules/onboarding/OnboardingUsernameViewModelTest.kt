package com.guillaumewilmot.swoleai.modules.onboarding

import android.view.View
import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.RxImmediateSchedulerRule
import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.modules.onboarding.username.OnboardingUsernameViewModel
import io.mockk.every
import io.mockk.spyk
import io.mockk.verifySequence
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class OnboardingUsernameViewModelTest : BaseUnitTest() {

    @Rule
    @JvmField
    val rxRule = RxImmediateSchedulerRule()

    private lateinit var viewModel: OnboardingUsernameViewModel

    @Before
    fun setup() {
        every { application.getString(R.string.app_field_validator_not_blank_error) } returns BLANK_ERROR
        viewModel =
            spyk(OnboardingUsernameViewModel(application, dataStorage), recordPrivateCalls = true)
    }

    @Test
    fun testButtonContinueEnabled() {
        val consumer = spyConsumer(viewModel.nextButtonEnabled)

        testObserver(viewModel.nextButtonEnabled).assertValue(false)
        viewModel.usernameFieldChangeListener(editableMockk(""))
        testObserver(viewModel.nextButtonEnabled).assertValue(false)
        viewModel.usernameFieldChangeListener(editableMockk("  "))
        testObserver(viewModel.nextButtonEnabled).assertValue(false)
        viewModel.usernameFieldChangeListener(editableMockk("Gui"))
        testObserver(viewModel.nextButtonEnabled).assertValue(true)
        viewModel.usernameFieldChangeListener(editableMockk(""))
        testObserver(viewModel.nextButtonEnabled).assertValue(false)

        verifySequence {
            consumer.accept(false) //Default
            //Skipping "" due to distinctUntilChanged
            consumer.accept(false) // "  "
            consumer.accept(true) // "Gui"
            consumer.accept(false) // ""
        }
    }

    @Test
    fun testusernameFieldError() {
        val consumer = spyConsumer(viewModel.usernameFieldError)

        testObserver(viewModel.usernameFieldError).assertValue(Nullable(null))
        viewModel.usernameFieldChangeListener(editableMockk(""))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(null))
        viewModel.usernameFieldChangeListener(editableMockk("  "))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(null))
        viewModel.usernameFieldChangeListener(editableMockk("Gui"))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(null))
        viewModel.usernameFieldChangeListener(editableMockk("  "))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(BLANK_ERROR))
        viewModel.usernameFieldChangeListener(editableMockk(""))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(BLANK_ERROR))
        viewModel.usernameFieldChangeListener(editableMockk("Gui"))
        testObserver(viewModel.usernameFieldError).assertValue(Nullable(null))

        viewModel.usernameFieldFocusChangeListener.onFocusChange(null, true) // Nothing happens
        viewModel.usernameFieldFocusChangeListener.onFocusChange(null, false) // Nothing happens

        verifySequence {
            consumer.accept(Nullable(null)) // Default
            //Skipping "" due to distinctUntilChanged
            consumer.accept(Nullable(null)) // "  "
            consumer.accept(Nullable(null)) // "Gui"
            consumer.accept(Nullable(null)) // _canShowErrorField is now true
            consumer.accept(Nullable(BLANK_ERROR)) // "  "
            consumer.accept(Nullable(BLANK_ERROR)) // ""
            consumer.accept(Nullable(null)) // "Gui"
        }
    }

    @Test
    fun testOnNext() {
        val loaderConsumer = spyConsumer(viewModel.loaderVisibility)
        val userConsumer = spyConsumer(dataStorage.dataHolder.userField)

        testObserver(viewModel.nextButtonEnabled).assertValue(false)
        viewModel.onNext() //Nothing happens

        viewModel.usernameFieldChangeListener(editableMockk("Gui"))
        testObserver(viewModel.nextButtonEnabled).assertValue(true)
        testObserver(viewModel.loaderVisibility).assertValue(View.GONE)
        testObserver(dataStorage.dataHolder.userField).assertValue(Nullable(null))

        viewModel.onNext()!!.subscribe()

        testObserver(viewModel.loaderVisibility).assertValue(View.GONE)
        testObserver(dataStorage.dataHolder.userField).assertValue(Nullable(UserModel("Gui")))

        verifySequence {
            loaderConsumer.accept(View.GONE)
            userConsumer.accept(Nullable(null))

            //onNext
            loaderConsumer.accept(View.VISIBLE)
            userConsumer.accept(Nullable(UserModel("Gui")))
            loaderConsumer.accept(View.GONE)
        }
    }

    companion object {
        const val BLANK_ERROR = "Blank"
    }
}