package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingUsernameBinding
import com.guillaumewilmot.swoleai.modules.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingUsernameFragment :
    ParentFragment<FragmentOnboardingUsernameBinding>(FragmentOnboardingUsernameBinding::inflate) {

    private val viewModel: OnboardingUsernameViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    private fun ui() {
        binding?.viewPagerDots?.let {
            parent?.attachIndicator(it)
        }

        binding?.titleText?.text = viewModel.titleText
        binding?.continueButton?.text = viewModel.nextButtonText

        binding?.continueButton?.setOnClickListener {
            parent?.userOnboardingUsernameNext()
        }
    }

    private val parent get() = (activity as? OnboardingUsernameFragmentListener)

    interface OnboardingUsernameFragmentListener : AttachViewPagerIndicator {
        fun userOnboardingUsernameNext()
    }
}