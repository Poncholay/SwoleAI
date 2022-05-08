package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingUsernameBinding
import com.guillaumewilmot.swoleai.modules.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingUsernameFragment : ParentFragment<FragmentOnboardingUsernameBinding>() {

    private val viewModel: OnboardingUsernameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentOnboardingUsernameBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()

        viewModel.nextButtonEnabled
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.continueButton?.isEnabled = it
            }

        viewModel.usernameFieldError
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.usernameLayout?.error = it.value
                binding?.usernameLayout?.errorContentDescription = it.value
            }

        viewModel.loaderVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.loader?.visibility = it
            }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        binding?.usernameInput?.doAfterTextChanged(viewModel.usernameFieldChangeListener)
        binding?.usernameInput?.onFocusChangeListener = viewModel.usernameFieldFocusChangeListener
    }

    private fun ui() {
        binding?.viewPagerDots?.let {
            parent?.attachIndicator(it)
        }

        binding?.titleText?.text = viewModel.titleText

        binding?.continueButton?.text = viewModel.nextButtonText
        binding?.continueButton?.setOnClickListener {
            viewModel.onNext()
                ?.autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                ?.subscribe {
                    parent?.userOnboardingUsernameNext()
                }
        }
    }

    private val parent get() = (activity as? OnboardingUsernameFragmentListener)

    interface OnboardingUsernameFragmentListener : AttachViewPagerIndicator {
        fun userOnboardingUsernameNext()
    }
}