package com.guillaumewilmot.swoleai.modules.onboarding.greeting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import autodispose2.androidx.lifecycle.autoDispose
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingGreetingBinding
import com.guillaumewilmot.swoleai.modules.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingGreetingFragment : ParentFragment() {

    private var binding: FragmentOnboardingGreetingBinding? = null
    private val viewModel: OnboardingGreetingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentOnboardingGreetingBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()

        viewModel.titleTextTest
            .autoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe {
                binding?.titleText?.text = it
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