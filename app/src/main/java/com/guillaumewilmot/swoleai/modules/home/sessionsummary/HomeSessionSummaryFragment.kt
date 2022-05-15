package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.autoDispose
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeSessionSummaryBinding
import com.guillaumewilmot.swoleai.modules.home.activesession.HomeActiveSessionFragment
import com.guillaumewilmot.swoleai.util.extension.dpToPixel
import com.guillaumewilmot.swoleai.util.extension.withFragmentManager
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstack
import com.guillaumewilmot.swoleai.view.EqualSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeSessionSummaryFragment : ParentFragment<FragmentHomeSessionSummaryBinding>() {

    @Inject
    lateinit var fragmentBackstack: FragmentBackstack

    private val viewModel: HomeSessionSummaryViewModel by viewModels()
    private val exerciseSummaryAdapter: ExerciseSummaryAdapter?
        get() = binding?.exerciseSummary?.adapter as? ExerciseSummaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeSessionSummaryBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener(RestartSessionDialog.REQUEST_KEY)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()

        setFragmentResultListener(RestartSessionDialog.REQUEST_KEY) { _, result ->
            when (result.getString(RestartSessionDialog.ACTION)) {
                RestartSessionDialog.ACTION_RESTART -> restartSession()
                else -> Unit
            }
        }

        viewModel.toolbarCurrentSessionText
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.appBar?.currentSessionText?.text = it
            }

        viewModel.sessionStatusState
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.appBar?.sessionStatus?.setCardBackgroundColor(it.backgroundColor)
                binding?.appBar?.sessionStatusText?.text = it.text
                binding?.appBar?.sessionStatusText?.setTextColor(it.textColor)
                binding?.appBar?.sessionStatus?.visibility = it.visibility
            }

        viewModel.sessionExercises
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                exerciseSummaryAdapter?.data = it
            }

        viewModel.startButtonText
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.startButton?.text = it
            }

        viewModel.previewButtonText
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.previewButton?.text = it
            }

        viewModel.skipButtonVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.skipButton?.visibility = it
            }

        viewModel.previewButtonVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                binding?.previewButton?.visibility = it
            }
    }

    private fun ui() {
        binding?.appBar?.toolbarContent?.toolbarTitle?.text = getString(
            R.string.app_home_session_summary_toolbar_text
        )

        binding?.appBar?.nextSessionButton?.setOnClickListener {
            viewModel.nextSession()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe()
        }
        binding?.appBar?.previousSessionButton?.setOnClickListener {
            viewModel.previousSession()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe()
        }

        binding?.startButton?.setOnClickListener {
            startSession()
        }
        binding?.skipButton?.setOnClickListener {
            viewModel.skipSession()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe()
        }
        binding?.previewButton?.setOnClickListener {
            //TODO
        }

        binding?.exerciseSummary?.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(this.context.dpToPixel(8f).toInt()))
            adapter = ExerciseSummaryAdapter()
        }
    }

    private fun startSession() = viewModel.startSession()
        .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
        .subscribe({
            if (isAdded) {
                fragmentBackstack.push(
                    parentFragmentManager,
                    HomeActiveSessionFragment(),
                    FragmentBackstack.Animate.FORWARD
                )
            }
        }, { exception ->
            withFragmentManager { fm ->
                when (exception) {
                    is HomeSessionSummaryViewModel.CannotStartCompletedSessionException ->
                        RestartSessionDialog(RestartSessionDialog.Status.COMPLETED).show(fm, name())
                    is HomeSessionSummaryViewModel.CannotStartSkippedSessionException ->
                        RestartSessionDialog(RestartSessionDialog.Status.SKIPPED).show(fm, name())
                    else -> Unit
                }
            }
        })

    private fun restartSession() = viewModel.restartSession()
        .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
        .subscribe {
            if (isAdded) {
                fragmentBackstack.push(
                    parentFragmentManager,
                    HomeActiveSessionFragment(),
                    FragmentBackstack.Animate.FORWARD
                )
            }
        }
}