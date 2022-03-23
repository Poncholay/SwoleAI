package com.guillaumewilmot.swoleai.modules.home

import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.RxImmediateSchedulerRule
import com.guillaumewilmot.swoleai.model.UserModel
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class HomeActivityViewModelTest : BaseUnitTest() {

    @Rule
    @JvmField
    val rxRule = RxImmediateSchedulerRule()

    private lateinit var viewModel: HomeActivityViewModel

    @Before
    fun setup() {
        viewModel = spyk(HomeActivityViewModel(application, dataStorage), recordPrivateCalls = true)
    }

    @Test
    fun givenNullUser_verifyRedirectToOnboarding() {
        testObserver(viewModel.redirectToOnboarding).assertValue(true)
    }

    @Test
    fun givenUserExists_verifyDontRedirectToOnboarding() {
        dataStorage.toStorage(DataDefinition.USER, UserModel())
        testObserver(viewModel.redirectToOnboarding).assertValue(false)
    }
}