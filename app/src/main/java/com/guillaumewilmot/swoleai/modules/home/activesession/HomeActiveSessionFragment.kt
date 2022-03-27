package com.guillaumewilmot.swoleai.modules.home.activesession

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeActiveSessionBinding
import com.guillaumewilmot.swoleai.modules.home.sessionsummary.HomeSessionSummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActiveSessionFragment : ParentFragment<FragmentHomeActiveSessionBinding>() {

    private val viewModel: HomeSessionSummaryViewModel by viewModels()

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
    }

    //FIXME : TMP just some hardcoded UI blueprint
    private fun ui() {
        binding?.finishButton?.setOnClickListener {

        }
        binding?.cancelButton?.setOnClickListener {

        }

        binding?.toolbarLayout?.toolbarContent?.toolbarTitle?.text = "Week 1 - day 1"
    }
}