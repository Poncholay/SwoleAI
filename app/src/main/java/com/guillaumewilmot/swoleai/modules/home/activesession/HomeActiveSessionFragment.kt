package com.guillaumewilmot.swoleai.modules.home.activesession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeActiveSessionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActiveSessionFragment : ParentFragment<FragmentHomeActiveSessionBinding>() {

    private val viewModel: HomeActiveSessionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeActiveSessionBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()

        viewModel.toolbarCurrentSessionText
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.toolbarLayout?.toolbarContent?.toolbarTitle?.text = it
            }
    }

    private fun ui() {
        binding?.finishButton?.setOnClickListener {

        }
        binding?.cancelButton?.setOnClickListener {

        }

        binding?.toolbarLayout?.toolbarContent?.backButton?.visibility = View.VISIBLE
        binding?.toolbarLayout?.toolbarContent?.backButton?.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}