package com.guillaumewilmot.swoleai.modules.home

import com.guillaumewilmot.swoleai.BaseUnitTest
import com.guillaumewilmot.swoleai.RxImmediateSchedulerRule
import com.guillaumewilmot.swoleai.modules.home.dashboard.HomeDashboardViewModel
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule


@ExperimentalCoroutinesApi
class HomeDashboardViewModelTest : BaseUnitTest() {

    @Rule
    @JvmField
    val rxRule = RxImmediateSchedulerRule()

    private lateinit var viewModel: HomeDashboardViewModel

    @Before
    fun setup() {
        viewModel =
            spyk(HomeDashboardViewModel(application, dataStorage), recordPrivateCalls = true)
    }


    companion object {

    }
}