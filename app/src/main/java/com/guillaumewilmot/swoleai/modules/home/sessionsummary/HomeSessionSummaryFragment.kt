package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import autodispose2.androidx.lifecycle.autoDispose
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

    private val exerciseSummaryAdapter by lazy {
        ExerciseSummaryAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.refreshSelectedSession()
            .autoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearFragmentResultListener(RestartSessionDialog.REQUEST_KEY)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeSessionSummaryBinding.inflate(
        inflater,
        container,
        false
    ).also { binding ->
        setFragmentResultListener(RestartSessionDialog.REQUEST_KEY) { _, result ->
            when (result.getString(RestartSessionDialog.ACTION)) {
                RestartSessionDialog.ACTION_RESTART -> startSession()
                else -> Unit
            }
        }

        viewModel.loaderVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.appBar?.toolbarContent?.loader?.visibility = it
            }

        viewModel.toolbarSelectedSessionText
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.appBar?.selectedSessionText?.text = it
            }

        viewModel.sessionStatusState
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.appBar?.sessionStatus?.let { statusView ->
                    TransitionManager.beginDelayedTransition(statusView, AutoTransition().apply {
                        duration = 150
                    })

                    it.text?.let {
                        this.binding?.appBar?.sessionStatusText?.text = it
                    }
                    it.textColor?.let {
                        this.binding?.appBar?.sessionStatusText?.setTextColor(it)
                    }
                    it.backgroundColor?.let {
                        statusView.setCardBackgroundColor(it)
                    }
                    statusView.visibility = it.visibility
                }
            }

        viewModel.goToActiveSessionButtonVisibility
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.appBar?.goToActiveSessionButton?.visibility = it
            }

        viewModel.sessionExercises
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                exerciseSummaryAdapter.setDataset(it)
            }

        viewModel.actionButtonsState
            .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
            .subscribe {
                this.binding?.buttonContainer?.let { buttonContainer ->
                    TransitionManager.beginDelayedTransition(
                        buttonContainer,
                        AutoTransition().apply {
                            duration = 150
                        })

                    this.binding?.startButton?.visibility = it.startButtonVisibility
                    this.binding?.startButton?.text = it.startButtonText

                    this.binding?.previewButton?.visibility = it.previewButtonVisibility
                    this.binding?.previewButton?.text = it.previewButtonText

                    this.binding?.skipButton?.visibility = it.skipButtonVisibility
                    this.binding?.skipButton?.text = it.skipButtonText
                }
            }

        this.binding = binding
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    private fun ui() {
        binding?.appBar?.toolbarContent?.toolbarTitle?.text = getString(
            R.string.app_home_session_summary_toolbar_text
        )

        binding?.appBar?.goToActiveSessionButton?.setOnClickListener {
            viewModel.goToActiveSession()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe()
        }

        binding?.appBar?.nextSessionButton?.setOnClickListener {
            viewModel.goToNextSession()
                .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                .subscribe()
        }
        binding?.appBar?.previousSessionButton?.setOnClickListener {
            viewModel.goToPreviousSession()
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
            //TODO: See reps and sets
        }

        binding?.exerciseSummary?.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(this.context.dpToPixel(8f).toInt()))
            adapter = exerciseSummaryAdapter.apply {
                getIndexClickedObservable()
//                    .switchMap { indexClicked ->
//                        viewModel.onExerciseSelected(indexClicked).toObservable()
//                    }
                    .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                    .subscribe {
                        Log.d(name(), "Click $it")
                        //TODO : Expand sets and reps
                    }

                getIndexInfoClickedObservable()
//                    .switchMap { indexClicked ->
//                        viewModel.onExerciseSelected(indexClicked).toObservable()
//                    }
                    .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                    .subscribe {
                        Log.d(name(), "Click info $it")
                        //TODO : Open exercise popup
                    }

                getIndexSwapClickedObservable()
//                    .switchMap { indexClicked ->
//                        viewModel.onExerciseSelected(indexClicked).toObservable()
//                    }
                    .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
                    .subscribe {
                        Log.d(name(), "Click swap $it")
                        //TODO : Open exercise list popup
                    }
            }
        }
    }

    private fun startSession() = viewModel.startSession()
        .autoDispose(AndroidLifecycleScopeProvider.from(viewLifecycleOwner))
        .subscribe({
            withFragmentManager { fm ->
                fragmentBackstack.push(fm, HomeActiveSessionFragment())
            }
        }, { exception ->
            when (exception) {
                is HomeSessionSummaryViewModel.CannotStartCompletedSessionException ->
                    RestartSessionDialog.Status.COMPLETED
                is HomeSessionSummaryViewModel.CannotStartSkippedSessionException ->
                    RestartSessionDialog.Status.SKIPPED
                is HomeSessionSummaryViewModel.CannotStartSessionWhileActiveSessionExistsException ->
                    RestartSessionDialog.Status.ACTIVE
                else -> null
            }?.let { status ->
                withFragmentManager { fm ->
                    RestartSessionDialog(status).show(fm, name())
                }
            }
        })
}