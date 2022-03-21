package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingGreetingBinding
import com.guillaumewilmot.swoleai.modules.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingGreetingFragment :
    ParentFragment<FragmentOnboardingGreetingBinding>(FragmentOnboardingGreetingBinding::inflate) {

    private val viewModel: OnboardingGreetingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()

        viewModel.apply {
            this.titleTextTest.compose(lifecycleProvider.bindToLifecycle())
                .subscribe { binding?.titleText?.text = it }
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.apply {
            userTimer.compose(lifecycleProvider.bindToLifecycle())
                .subscribe {
                    updateUser(it)
                }
        }
    }

    private fun ui() {
        binding?.viewPagerDots?.let {
            parent?.attachIndicator(it)
        }

        binding?.titleText?.text = viewModel.titleText
        binding?.descriptionText?.text = viewModel.descriptionText
        binding?.continueButton?.text = viewModel.nextButtonText

        binding?.continueButton?.setOnClickListener {
            parent?.userOnboardingGreetingNext()
        }
    }

    private val parent get() = (activity as? OnboardingGreetingFragmentListener)

    interface OnboardingGreetingFragmentListener : AttachViewPagerIndicator {
        fun userOnboardingGreetingNext()
    }
}