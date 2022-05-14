package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.guillaumewilmot.swoleai.controller.ParentDialog
import com.guillaumewilmot.swoleai.databinding.DialogRestartSessionBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class RestartSessionDialog : ParentDialog<DialogRestartSessionBinding>() {

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

        binding?.restartButton?.setOnClickListener {
            setFragmentResult(name(), bundleOf(ACTION to ACTION_RESTART))
        }

        binding?.cancelButton?.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        const val ACTION = "action"
        const val ACTION_RESTART = "action_restart"
        fun name(): String = this::class.java.simpleName
    }
}