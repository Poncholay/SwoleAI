package com.guillaumewilmot.swoleai.modules.onboarding.username

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import autodispose2.androidx.lifecycle.autoDispose
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

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        listeners()
    }

    override fun onResume() {
        super.onResume()

        viewModel.nextButtonEnabled.autoDispose(this)
            .subscribe {
                binding?.continueButton?.isEnabled = it
            }

        viewModel.usernameFieldError
            .autoDispose(this)
            .subscribe {
                binding?.usernameLayout?.error = it.value
                binding?.usernameLayout?.errorContentDescription = it.value
            }
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

        binding?.continueButton?.setOnClickListener {
            viewModel.onNext()
        }
    }

    private fun listeners() {
        binding?.usernameInput?.let { usernameInput ->
            usernameInput.doAfterTextChanged(viewModel.usernameFieldChangeListener)
            usernameInput.text.takeIf { text -> text.isNullOrEmpty().not() }?.let { text ->
                //Trigger verification if returning to the screen with prefilled data
                viewModel.usernameFieldChangeListener(text)
                viewModel.usernameFieldFocusChangeListener.onFocusChange(usernameInput, false)
            }
            usernameInput.onFocusChangeListener = viewModel.usernameFieldFocusChangeListener
        }
    }

    private val parent get() = (activity as? OnboardingUsernameFragmentListener)

    interface OnboardingUsernameFragmentListener : AttachViewPagerIndicator {
        fun userOnboardingUsernameNext()
    }
}