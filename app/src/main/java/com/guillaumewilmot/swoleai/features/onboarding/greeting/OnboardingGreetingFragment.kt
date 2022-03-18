package com.guillaumewilmot.swoleai.features.onboarding.greeting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingGreetingBinding
import com.guillaumewilmot.swoleai.features.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingGreetingFragment :
    ParentFragment<FragmentOnboardingGreetingBinding>(FragmentOnboardingGreetingBinding::inflate) {

    private val viewModel: OnboardingGreetingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    private fun ui() {
        binding?.viewPagerDots?.let {
            parent?.attachIndicator(it)
        }

        binding?.greetingText?.text = viewModel.greetingText
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