package com.guillaumewilmot.swoleai.modules.onboarding

import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.RxImmediateSchedulerRule
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.modules.onboarding.greeting.OnboardingGreetingViewModel
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import io.mockk.every
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class OnboardingGreetingViewModelTest : BaseUnitTest() {

    @Rule
    @JvmField
    val rxRule = RxImmediateSchedulerRule()

    private lateinit var viewModel: OnboardingGreetingViewModel

    @Before
    fun setup() {
        every { application.getString(R.string.app_onboarding_greeting_title_text) } returns GREETING
        viewModel =
            spyk(OnboardingGreetingViewModel(application, dataStorage), recordPrivateCalls = true)
    }

    @Test
    fun givenNullUser_verifyGreetingText() {
        testObserver(viewModel.titleTextTest).assertValue(GREETING)
    }

    @Test
    fun givenUserExists_verifyGreetingTextContainsUsername() {
        val userName = "User"
        dataStorage.toStorage(DataDefinition.USER, UserModel(name = userName))

        testObserver(viewModel.titleTextTest).assertValue("$GREETING $userName")
    }

    companion object {
        const val GREETING = "Hi"
    }
}