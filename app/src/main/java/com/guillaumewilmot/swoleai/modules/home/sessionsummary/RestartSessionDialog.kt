package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentDialog
import com.guillaumewilmot.swoleai.databinding.DialogRestartSessionBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class RestartSessionDialog(
    private val status: Status
) : ParentDialog<DialogRestartSessionBinding>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogRestartSessionBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (status) {
            Status.COMPLETED -> {
                binding?.titleText?.text =
                    getString(R.string.app_dialog_restart_session_title_text_completed)
                binding?.descriptionText?.text =
                    getString(R.string.app_dialog_restart_session_description_text_completed)
                binding?.cancelButton?.text =
                    getString(R.string.app_dialog_restart_session_cancel_button_text_completed)
            }
            Status.SKIPPED -> {
                binding?.titleText?.text =
                    getString(R.string.app_dialog_restart_session_title_text_skipped)
                binding?.descriptionText?.text =
                    getString(R.string.app_dialog_restart_session_description_text_skipped)
                binding?.cancelButton?.text =
                    getString(R.string.app_dialog_restart_session_cancel_button_text_skipped)
            }
        }

        binding?.restartButton?.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(ACTION to ACTION_RESTART))
            dismiss()
        }

        binding?.cancelButton?.setOnClickListener {
            dismiss()
        }
    }

    enum class Status {
        COMPLETED,
        SKIPPED;
    }

    companion object {
        const val REQUEST_KEY = "request_RestartSessionDialog"
        const val ACTION = "action"
        const val ACTION_RESTART = "action_restart"
    }
}