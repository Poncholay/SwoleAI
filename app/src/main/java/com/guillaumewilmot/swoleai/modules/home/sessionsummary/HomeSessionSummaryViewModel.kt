package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.app.Application
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.CanLookupProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanLookupProgramImpl
import com.guillaumewilmot.swoleai.util.extension.withSpans
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeSessionSummaryViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application), CanLookupProgram by CanLookupProgramImpl() {
    private val _currentSession = dataStorage.dataHolder.currentSessionField
    private val _currentWeek = getProgramWeekFromSession(_currentSession)
    private val _currentBlock = getProgramBlockFromProgramWeek(_currentWeek)

    val toolbarCurrentSessionText: Flowable<SpannableString> = Flowable.combineLatest(
        _currentSession,
        _currentWeek,
        _currentBlock
    ) { currentSession, currentWeek, currentBlock ->
        if (currentSession.value == null || currentWeek.value == null || currentBlock.value == null) {
            return@combineLatest SpannableString("")
        }

        val blockType = currentBlock.value.type
        val blockTypeName = application.getString(blockType.nameId)
        val weekName = currentWeek.value.name
        val sessionName = currentSession.value.name

        SpannableString("$blockTypeName\n$weekName\n$sessionName")
            .withSpans(
                blockTypeName,
                ForegroundColorSpan(application.getColor(blockType.colorId))
            )
            .withSpans(
                weekName,
                StyleSpan(Typeface.BOLD),
                RelativeSizeSpan(1.4f)
            )
            .withSpans(
                sessionName,
                StyleSpan(Typeface.BOLD),
                RelativeSizeSpan(1.8f)
            )
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    //FIXME : TMP hardcoded data for now
    val sessionExercises: Flowable<List<ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel>> =
        Flowable.create<List<ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel>>({
            val emptyCallback = object : ParentActivity.AdapterCallback {
                override fun onClick(activity: ParentActivity, fragment: ParentFragment<*>) {
                }
            }

            it.onNext(
                listOf(
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("New rep max attempt\nCompetition deadlift").withSpans(
                            "New rep max attempt",
                            RelativeSizeSpan(0.8f),
                            ForegroundColorSpan(application.getColor(R.color.hypertrophy))
                        ),
                        backgroundColor = application.getColor(R.color.hypertrophyPast),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Pause front squat"),
                        backgroundColor = application.getColor(R.color.transparent),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Pendulum squat"),
                        backgroundColor = application.getColor(R.color.transparent),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Hamstring curl"),
                        backgroundColor = application.getColor(R.color.transparent),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Lunge"),
                        backgroundColor = application.getColor(R.color.transparent),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Calve raise"),
                        backgroundColor = application.getColor(R.color.transparent),
                        onClickCallback = emptyCallback,
                        infoCallback = emptyCallback,
                        swapCallback = emptyCallback
                    )
                )
            )
        }, BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}