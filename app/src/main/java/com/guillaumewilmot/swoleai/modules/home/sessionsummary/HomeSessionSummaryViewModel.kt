package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.app.Application
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.graphics.ColorUtils
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
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
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage),
    HasLoader by HasLoaderImpl() {

    private val _currentSession = dataStorage.dataHolder.currentSessionField
    private val _currentWeek = getProgramWeekFromSession(_currentSession)
    private val _currentBlock = getProgramBlockFromProgramWeek(_currentWeek)

    private val _activeSession = dataStorage.dataHolder.activeSessionField
    private val _currentSessionIsActive: Flowable<Boolean> = Flowable.combineLatest(
        _currentSession,
        _activeSession
    ) { currentSession, activeSession ->
        val currentSessionId = currentSession.value?.id
        val activeSessionId = activeSession.value?.id
        activeSessionId != null && currentSessionId == activeSessionId
    }

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

    private val _sessionStatusVisibility: Flowable<Int> = Flowable.combineLatest(
        _currentSession,
        _currentSessionIsActive
    ) { currentSession, sessionIsActive ->
        if (
            sessionIsActive ||
            currentSession.value?.isSkipped == true ||
            currentSession.value?.isComplete == true
        ) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private val _sessionStatusText: Flowable<String> = Flowable.combineLatest(
        _currentSession,
        _currentSessionIsActive
    ) { currentSession, sessionIsActive ->
        when {
            sessionIsActive -> application.getString(R.string.app_session_status_active)
            currentSession.value?.isSkipped == true -> application.getString(R.string.app_session_status_skipped)
            currentSession.value?.isComplete == true -> application.getString(R.string.app_session_status_completed)
            else -> ""
        }
    }

    private val _sessionStatusTextColor: Flowable<Int> = Flowable.combineLatest(
        _currentSession,
        _currentSessionIsActive,
        _currentBlock
    ) { currentSession, sessionIsActive, currentBlock ->
        val textColor = application.getColor(R.color.textPrimary)
        val textColorCompleted = application.getColor(R.color.textTertiary)
        val textColorSkipped = application.getColor(R.color.textQuaternary)
        val textColorActive = currentBlock.value?.type?.colorId?.let {
            application.getColor(it)
        } ?: textColor

        when {
            sessionIsActive -> textColorActive
            currentSession.value?.isSkipped == true -> textColorSkipped
            currentSession.value?.isComplete == true -> textColorCompleted
            else -> textColor
        }
    }

    private val _sessionStatusBackgroundColor: Flowable<Int> = _sessionStatusTextColor.map {
        ColorUtils.setAlphaComponent(it, 0x88)
    }

    val sessionStatusState: Flowable<SessionStatusState> = Flowable.combineLatest(
        _sessionStatusVisibility,
        _sessionStatusText,
        _sessionStatusTextColor,
        _sessionStatusBackgroundColor
    ) { visibility, text, color, backgroundColor ->
        SessionStatusState(visibility, text, color, backgroundColor)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class SessionStatusState(
        val visibility: Int,
        val text: String,
        val textColor: Int,
        val backgroundColor: Int
    )

    val startButtonText: Flowable<String> = Flowable.combineLatest(
        _currentSession,
        _currentSessionIsActive
    ) { currentSession, sessionIsActive ->
        when {
            sessionIsActive -> application.getString(R.string.app_home_session_summary_start_button_text_active)
            currentSession.value?.isSkipped == true -> application.getString(R.string.app_home_session_summary_start_button_text_skipped)
            currentSession.value?.isComplete == true -> application.getString(R.string.app_home_session_summary_start_button_text_completed)
            else -> application.getString(R.string.app_home_session_summary_start_button_text_default)
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val previewButtonText: Flowable<String> = _currentSession.map { currentSession ->
        when (currentSession.value?.isComplete) {
            true -> application.getString(R.string.app_home_session_summary_preview_button_text_completed)
            else -> application.getString(R.string.app_home_session_summary_preview_button_text)
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val skipButtonVisibility: Flowable<Int> = Flowable.combineLatest(
        _currentSession,
        _currentSessionIsActive
    ) { currentSession, sessionIsActive ->
        if (
            sessionIsActive ||
            currentSession.value?.isSkipped == true ||
            currentSession.value?.isComplete == true
        ) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val previewButtonVisibility: Flowable<Int> = _currentSessionIsActive.map { sessionIsActive ->
        if (sessionIsActive) View.GONE else View.VISIBLE
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

    private fun storeCurrentSessionById(transformId: (Int) -> Int): Completable =
        Flowable.combineLatest(
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
                        dataStorage.toStorage(DataDefinition.SELECTED_SESSION, nextSession)
                    }
                }
                Completable.complete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun nextSession(): Completable = storeCurrentSessionById { currentId -> currentId + 1 }
    fun previousSession(): Completable = storeCurrentSessionById { currentId -> currentId - 1 }

    fun restartSession(): Completable = _currentSession
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable {
            val currentSession = it.value ?: return@switchMapCompletable Completable.complete()

            val newActiveSession = SessionModel(
                id = currentSession.id,
                weekId = currentSession.weekId,
                name = currentSession.name,
                isComplete = false,
                isSkipped = false,
                exercises = currentSession.exercises
            )

            val completable1 = insertSession(newActiveSession)
            val completable2 = dataStorage.toStorage(
                DataDefinition.ACTIVE_SESSION,
                newActiveSession
            )
            val completable3 = dataStorage.toStorage(
                DataDefinition.SELECTED_SESSION,
                newActiveSession
            )

            Completable.mergeArray(completable1, completable2, completable3)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun startSession(): Completable = _currentSession
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable {
            val currentSession = it.value ?: return@switchMapCompletable Completable.complete()

            if (currentSession.isComplete) {
                return@switchMapCompletable Completable.error(
                    CannotStartCompletedSessionException()
                )
            }
            if (currentSession.isSkipped) {
                return@switchMapCompletable Completable.error(
                    CannotStartSkippedSessionException()
                )
            }

            dataStorage.toStorage(DataDefinition.ACTIVE_SESSION, currentSession)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun skipSession(): Completable = _currentSession
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable {
            val currentSession = it.value ?: return@switchMapCompletable Completable.complete()

            val newActiveSession = SessionModel(
                id = currentSession.id,
                weekId = currentSession.weekId,
                name = currentSession.name,
                isComplete = false,
                isSkipped = true,
                exercises = currentSession.exercises
            )

            val completable1 = insertSession(newActiveSession)
            val completable2 = dataStorage.toStorage(
                DataDefinition.SELECTED_SESSION,
                newActiveSession
            )

            Completable.mergeArray(completable1, completable2)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    class CannotStartCompletedSessionException : Exception()
    class CannotStartSkippedSessionException : Exception()
}