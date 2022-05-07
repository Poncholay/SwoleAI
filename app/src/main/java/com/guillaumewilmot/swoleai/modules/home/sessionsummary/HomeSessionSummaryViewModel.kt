package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.app.Application
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.program.CanLookupProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanLookupProgramImpl
import com.guillaumewilmot.swoleai.util.extension.withSpans
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataDefinition
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeSessionSummaryViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    CanLookupProgram by CanLookupProgramImpl(dataStorage),
    HasLoader by HasLoaderImpl() {
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
            it.onNext(
                listOf(
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("New rep max attempt\nCompetition deadlift").withSpans(
                            "New rep max attempt",
                            RelativeSizeSpan(0.8f),
                            ForegroundColorSpan(application.getColor(R.color.hypertrophy))
                        ),
                        backgroundColor = application.getColor(R.color.hypertrophyPast),
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Pause front squat"),
                        backgroundColor = application.getColor(R.color.transparent)
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Pendulum squat"),
                        backgroundColor = application.getColor(R.color.transparent)
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Hamstring curl"),
                        backgroundColor = application.getColor(R.color.transparent)
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Lunge"),
                        backgroundColor = application.getColor(R.color.transparent)
                    ),
                    ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewModel(
                        nameText = SpannableString("Calve raise"),
                        backgroundColor = application.getColor(R.color.transparent)
                    )
                )
            )
        }, BackpressureStrategy.LATEST)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * LOGIC
     */

    private fun storeSessionById(transformId: (Int) -> Int): Completable = Flowable.combineLatest(
        programSessions,
        _currentSession
    ) { sessions, currentSession ->
        Pair(sessions, currentSession)
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { (sessions, currentSession) ->
            currentSession.value?.id?.let { currentId ->
                val newId = transformId(currentId)
                sessions.find {
                    it.id == newId
                }?.let { nextSession ->
                    dataStorage.toStorage(DataDefinition.CURRENT_SESSION, nextSession)
                }
            }
            Completable.complete()
        }

    fun nextSession(): Completable = storeSessionById { currentId -> currentId + 1 }
    fun previousSession(): Completable = storeSessionById { currentId -> currentId - 1 }
}