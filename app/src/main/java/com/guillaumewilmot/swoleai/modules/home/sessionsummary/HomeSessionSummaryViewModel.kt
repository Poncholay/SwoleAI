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

    private val _currentWeek = getProgramWeekFromSession(selectedSession)
    private val _currentBlock = getProgramBlockFromProgramWeek(_currentWeek)

    val toolbarSelectedSessionText: Flowable<SpannableString> = Flowable.combineLatest(
        selectedSession,
        _currentWeek,
        _currentBlock
    ) { selectedSession, currentWeek, currentBlock ->
        if (selectedSession.value == null || currentWeek.value == null || currentBlock.value == null) {
            return@combineLatest SpannableString("")
        }

        val blockType = currentBlock.value.type
        val blockTypeName = application.getString(blockType.nameId)
        val weekName = currentWeek.value.name
        val sessionName = selectedSession.value.name

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

    private val _sessionStatusVisibility: Flowable<Int> = selectedSession.map { selectedSession ->
        if (
            selectedSession.value?.status == SessionModel.Status.ACTIVE ||
            selectedSession.value?.status == SessionModel.Status.SKIPPED ||
            selectedSession.value?.status == SessionModel.Status.COMPLETE
        ) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private val _sessionStatusText: Flowable<String> = selectedSession.map { selectedSession ->
        when (selectedSession.value?.status) {
            SessionModel.Status.ACTIVE ->
                application.getString(R.string.app_session_status_active)
            SessionModel.Status.SKIPPED ->
                application.getString(R.string.app_session_status_skipped)
            SessionModel.Status.COMPLETE ->
                application.getString(R.string.app_session_status_completed)
            else -> ""
        }
    }

    private val _sessionStatusTextColor: Flowable<Int> = Flowable.combineLatest(
        selectedSession,
        _currentBlock
    ) { selectedSession, currentBlock ->
        val textColor = application.getColor(R.color.textPrimary)
        val textColorCompleted = application.getColor(R.color.textTertiary)
        val textColorSkipped = application.getColor(R.color.textQuaternary)
        val textColorActive = currentBlock.value?.type?.colorId?.let {
            application.getColor(it)
        } ?: textColor

        when (selectedSession.value?.status) {
            SessionModel.Status.ACTIVE -> textColorActive
            SessionModel.Status.SKIPPED -> textColorSkipped
            SessionModel.Status.COMPLETE -> textColorCompleted
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

    val startButtonText: Flowable<String> = selectedSession.map { selectedSession ->
        when (selectedSession.value?.status) {
            SessionModel.Status.ACTIVE ->
                application.getString(R.string.app_home_session_summary_start_button_text_active)
            SessionModel.Status.SKIPPED ->
                application.getString(R.string.app_home_session_summary_start_button_text_skipped)
            SessionModel.Status.COMPLETE ->
                application.getString(R.string.app_home_session_summary_start_button_text_completed)
            else ->
                application.getString(R.string.app_home_session_summary_start_button_text_default)
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val previewButtonText: Flowable<String> = selectedSession.map { selectedSession ->
        when (selectedSession.value?.status == SessionModel.Status.COMPLETE) {
            true -> application.getString(
                R.string.app_home_session_summary_preview_button_text_completed
            )
            else -> application.getString(R.string.app_home_session_summary_preview_button_text)
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val skipButtonVisibility: Flowable<Int> = selectedSession.map { selectedSession ->
        if (
            selectedSession.value?.status == SessionModel.Status.ACTIVE ||
            selectedSession.value?.status == SessionModel.Status.SKIPPED ||
            selectedSession.value?.status == SessionModel.Status.COMPLETE
        ) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val previewButtonVisibility: Flowable<Int> = selectedSession.map { selectedSession ->
        if (selectedSession.value?.status == SessionModel.Status.ACTIVE) View.GONE else View.VISIBLE
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

    private fun storeSelectedSessionById(transformId: (Int) -> Int): Completable =
        Flowable.combineLatest(
            programSessions,
            selectedSession
        ) { sessions, selectedSession ->
            Pair(sessions, selectedSession)
        }
            .linkToLoader(this)
            .take(1)
            .switchMapCompletable { (sessions, selectedSession) ->
                selectedSession.value?.id?.let { currentId ->
                    val newId = transformId(currentId)
                    sessions.find {
                        it.id == newId
                    }?.let { nextSession ->
                        dataStorage.toStorage(DataDefinition.SELECTED_SESSION_ID, nextSession.id)
                    }
                }
                Completable.complete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun nextSession(): Completable = storeSelectedSessionById { currentId -> currentId + 1 }
    fun previousSession(): Completable = storeSelectedSessionById { currentId -> currentId - 1 }

    fun startSession(): Completable = Flowable.combineLatest(
        selectedSession,
        activeSession
    ) { selectedSession, activeSession ->
        Pair(selectedSession, activeSession)
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { (nullableSelectedSession, nullableActiveSession) ->
            val selectedSession = nullableSelectedSession.value
                ?: return@switchMapCompletable Completable.error(
                    SessionNotFoundException()
                )

            if (selectedSession.status == SessionModel.Status.COMPLETE) {
                return@switchMapCompletable Completable.error(
                    CannotStartCompletedSessionException()
                )
            }
            if (selectedSession.status == SessionModel.Status.SKIPPED) {
                return@switchMapCompletable Completable.error(
                    CannotStartSkippedSessionException()
                )
            }

            val activeSession = nullableActiveSession.value
            if (activeSession != null) {
                return@switchMapCompletable if (selectedSession.id == activeSession.id) {
                    /** All good, resume the session */
                    Completable.complete()
                } else {
                    Completable.error(CannotStartSessionWhileActiveSessionExistsException())
                }
            }

            val newActiveSession = SessionModel(
                id = selectedSession.id,
                weekId = selectedSession.weekId,
                name = selectedSession.name,
                isComplete = false,
                isSkipped = false,
                isActive = true,
                exercises = selectedSession.exercises
            )

            insertSession(newActiveSession)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun skipSession(): Completable = selectedSession
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable {
            val selectedSession = it.value ?: return@switchMapCompletable Completable.complete()

            val newActiveSession = SessionModel(
                id = selectedSession.id,
                weekId = selectedSession.weekId,
                name = selectedSession.name,
                isComplete = false,
                isSkipped = true,
                isActive = false,
                exercises = selectedSession.exercises
            )

            insertSession(newActiveSession)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    class SessionNotFoundException : Exception()
    class CannotStartCompletedSessionException : Exception()
    class CannotStartSkippedSessionException : Exception()
    class CannotStartSessionWhileActiveSessionExistsException : Exception()
}