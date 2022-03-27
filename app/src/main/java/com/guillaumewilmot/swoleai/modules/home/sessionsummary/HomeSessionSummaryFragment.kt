package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeSessionSummaryBinding
import com.guillaumewilmot.swoleai.model.ProgramBlockModel
import com.guillaumewilmot.swoleai.modules.home.activesession.HomeActiveSessionFragment
import com.guillaumewilmot.swoleai.util.extension.dpToPixel
import com.guillaumewilmot.swoleai.util.extension.withSpans
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
    private val exerciseSummaryAdapter: ExerciseSummaryAdapter by lazy {
        ExerciseSummaryAdapter(adapterCallbackWrapper)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    private fun ui() {
        binding?.appBar?.toolbarContent?.toolbarTitle?.text = getString(
            R.string.app_home_session_summary_toolbar_text
        )

        binding?.startButton?.setOnClickListener {
            fragmentBackstack.push(
                parentFragmentManager,
                HomeActiveSessionFragment(),
                FragmentBackstack.Animate.FORWARD
            )
        }
        binding?.skipButton?.setOnClickListener {

        }
        binding?.previewButton?.setOnClickListener {

        }

        binding?.exerciseSummary?.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(this.context.dpToPixel(8f).toInt()))
            adapter = exerciseSummaryAdapter
        }

        //FIXME : TMP just some hardcoded UI blueprint
        activity?.let { context ->
            val block = ProgramBlockModel.BlockType.HYPERTROPHY
            val blockType = getString(block.nameId)
            val weekNumber = "Week 4"
            val dayName = "Lower body"

            binding?.appBar?.currentSessionText?.text = SpannableString(
                "$blockType\n$weekNumber\n$dayName"
            )
                .withSpans(
                    blockType,
                    ForegroundColorSpan(context.getColor(block.colorId))
                )
                .withSpans(
                    weekNumber,
                    StyleSpan(Typeface.BOLD),
                    RelativeSizeSpan(1.4f)
                )
                .withSpans(
                    dayName,
                    StyleSpan(Typeface.BOLD),
                    RelativeSizeSpan(1.8f)
                )

            val emptyCallback = object : ParentActivity.AdapterCallback {
                override fun onClick(activity: ParentActivity, fragment: ParentFragment<*>) {
                }
            }

            exerciseSummaryAdapter.data = listOf(
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("New rep max attempt\nCompetition deadlift").withSpans(
                        "New rep max attempt",
                        RelativeSizeSpan(0.8f),
                        ForegroundColorSpan(context.getColor(R.color.hypertrophy))
                    ),
                    backgroundColor = context.getColor(R.color.hypertrophyPast),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                ),
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("Pause front squat"),
                    backgroundColor = context.getColor(R.color.transparent),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                ),
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("Pendulum squat"),
                    backgroundColor = context.getColor(R.color.transparent),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                ),
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("Hamstring curl"),
                    backgroundColor = context.getColor(R.color.transparent),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                ),
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("Lunge"),
                    backgroundColor = context.getColor(R.color.transparent),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                ),
                ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                    nameText = SpannableString("Calve raise"),
                    backgroundColor = context.getColor(R.color.transparent),
                    onClickCallback = emptyCallback,
                    infoCallback = emptyCallback,
                    swapCallback = emptyCallback
                )
            )
        }
    }
}