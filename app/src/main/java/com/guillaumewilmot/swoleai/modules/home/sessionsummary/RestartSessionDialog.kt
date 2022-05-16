package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentDialog
import com.guillaumewilmot.swoleai.databinding.DialogDefaultWithActionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class RestartSessionDialog(
    private val status: Status
) : ParentDialog<DialogDefaultWithActionBinding>() {

    private val viewModel: RestartSessionDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogDefaultWithActionBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loaderVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.loader?.visibility = it
            }

        when (status) {
            Status.COMPLETED -> {
                binding?.titleText?.text =
                    getString(R.string.app_dialog_restart_session_title_text_completed)
                binding?.descriptionText?.text =
                    getString(R.string.app_dialog_restart_session_description_text_completed)
                binding?.acceptButton?.text =
                    getString(R.string.app_dialog_restart_session_confirm_button_text_completed)
                binding?.refuseButton?.text =
                    getString(R.string.app_dialog_restart_session_cancel_button_text_completed)
            }
            Status.SKIPPED -> {
                binding?.titleText?.text =
                    getString(R.string.app_dialog_restart_session_title_text_skipped)
                binding?.descriptionText?.text =
                    getString(R.string.app_dialog_restart_session_description_text_skipped)
                binding?.acceptButton?.text =
                    getString(R.string.app_dialog_restart_session_confirm_button_text_skipped)
                binding?.refuseButton?.text =
                    getString(R.string.app_dialog_restart_session_cancel_button_text_skipped)
            }
            Status.ACTIVE -> {
                binding?.titleText?.text =
                    getString(R.string.app_dialog_restart_session_title_text_active)
                binding?.descriptionText?.text =
                    getString(R.string.app_dialog_restart_session_description_text_active)
                binding?.acceptButton?.text =
                    getString(R.string.app_dialog_restart_session_confirm_button_text_active)
                binding?.refuseButton?.text =
                    getString(R.string.app_dialog_restart_session_cancel_button_text_active)
            }
        }

        binding?.acceptButton?.setOnClickListener {
            viewModel.reinitSession(status)
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe {
                    setFragmentResult(REQUEST_KEY, bundleOf(ACTION to ACTION_RESTART))
                    dismiss()
                }
        }

        binding?.refuseButton?.setOnClickListener {
            dismiss()
        }
    }

    enum class Status {
        COMPLETED,
        SKIPPED,
        ACTIVE;
    }

    companion object {
        const val REQUEST_KEY = "request_RestartSessionDialog"
        const val ACTION = "action"
        const val ACTION_RESTART = "action_restart"
    }
}