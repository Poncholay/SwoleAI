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
import com.guillaumewilmot.swoleai.model.Nullable
import com.guillaumewilmot.swoleai.model.SessionModel
import com.guillaumewilmot.swoleai.model.asNullable
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
        val weekDay =
            application.getString(
                R.string.app_session_day_name,
                selectedSession.value.day.toString()
            )
        val weekName = "${currentWeek.value.name} - $weekDay"
        val sessionName =
            application.getString(R.string.app_session_day_name, selectedSession.value.name)

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
            View.INVISIBLE
        }
    }

    private val _sessionStatusText: Flowable<Nullable<String>> =
        selectedSession.map { selectedSession ->
            when (selectedSession.value?.status) {
                SessionModel.Status.ACTIVE ->
                    application.getString(R.string.app_session_status_active)
                SessionModel.Status.SKIPPED ->
                    application.getString(R.string.app_session_status_skipped)
                SessionModel.Status.COMPLETE ->
                    application.getString(R.string.app_session_status_completed)
                else -> null
            }.asNullable()
        }

    private val _sessionStatusTextColor: Flowable<Nullable<Int>> = Flowable.combineLatest(
        selectedSession,
        _currentBlock
    ) { selectedSession, currentBlock ->
        val textColorCompleted = application.getColor(R.color.textTertiary)
        val textColorSkipped = application.getColor(R.color.textQuaternary)
        val textColorActive = currentBlock.value?.type?.colorId?.let {
            application.getColor(it)
        } ?: application.getColor(R.color.textPrimary)

        when (selectedSession.value?.status) {
            SessionModel.Status.ACTIVE -> textColorActive
            SessionModel.Status.SKIPPED -> textColorSkipped
            SessionModel.Status.COMPLETE -> textColorCompleted
            else -> null
        }.asNullable()
    }

    private val _sessionStatusBackgroundColor: Flowable<Nullable<Int>> =
        _sessionStatusTextColor.map {
            it.value?.let { color ->
                ColorUtils.setAlphaComponent(color, 0x88)
            }.asNullable()
        }

    val sessionStatusState: Flowable<SessionStatusState> = Flowable.combineLatest(
        _sessionStatusVisibility,
        _sessionStatusText,
        _sessionStatusTextColor,
        _sessionStatusBackgroundColor
    ) { visibility, text, color, backgroundColor ->
        SessionStatusState(visibility, text.value, color.value, backgroundColor.value)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class SessionStatusState(
        val visibility: Int,
        val text: String?,
        val textColor: Int?,
        val backgroundColor: Int?
    )

    val goToActiveSessionButtonVisibility: Flowable<Int> = Flowable.combineLatest(
        selectedSession,
        activeSession
    ) { selectedSession, activeSession ->
        if (activeSession.value != null && selectedSession.value != null &&
            activeSession.value.id != selectedSession.value.id
        ) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    private val _startButtonText: Flowable<String> = selectedSession.map { selectedSession ->
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

    private val _previewButtonText: Flowable<String> = selectedSession.map { selectedSession ->
        when (selectedSession.value?.status == SessionModel.Status.COMPLETE) {
            true -> application.getString(
                R.string.app_home_session_summary_preview_button_text_completed
            )
            else -> application.getString(R.string.app_home_session_summary_preview_button_text)
        }
    }

    private val _skipButtonVisibility: Flowable<Int> = selectedSession.map { selectedSession ->
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

    private val _previewButtonVisibility: Flowable<Int> = selectedSession.map { selectedSession ->
        if (selectedSession.value?.status == SessionModel.Status.ACTIVE) View.GONE else View.VISIBLE
    }

    val actionButtonsState: Flowable<ActionButtonsState> = Flowable.zip(
        _startButtonText,
        _previewButtonText,
        _skipButtonVisibility,
        _previewButtonVisibility
    ) { startButtonText, previewButtonText, skipButtonVisibility, previewButtonVisibility ->
        ActionButtonsState(
            startButtonText = startButtonText,
            startButtonVisibility = View.VISIBLE,
            previewButtonText = previewButtonText,
            previewButtonVisibility = previewButtonVisibility,
            skipButtonText = application.getString(R.string.app_home_session_summary_skip_button_text),
            skipButtonVisibility = skipButtonVisibility,
        )
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class ActionButtonsState(
        val startButtonText: String,
        val startButtonVisibility: Int,
        val previewButtonText: String,
        val previewButtonVisibility: Int,
        val skipButtonText: String,
        val skipButtonVisibility: Int
    )

    //FIXME : TMP hardcoded data for now
    val sessionExercises: Flowable<List<ExerciseSummaryAdapter.ViewDataModel>> =
        Flowable.combineLatest(
            selectedSession,
            _currentBlock
        ) { selectedSession, currentBlock ->
            if (selectedSession.value?.name?.contains("Lower") == true) {
                listOf(
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString(
                            "New rep max attempt\nCompetition deadlift"
                        ).withSpans(
                            "New rep max attempt",
                            RelativeSizeSpan(0.8f),
                            ForegroundColorSpan(
                                application.getColor(
                                    currentBlock.value?.type?.colorId ?: R.color.hypertrophy
                                )
                            )
                        ),
                        backgroundColor = ColorUtils.setAlphaComponent(
                            application.getColor(
                                currentBlock.value?.type?.colorId ?: R.color.hypertrophy
                            ), 0x88
                        ),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Pause front squat"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Pendulum squat"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Hamstring curl"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Lunge"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Calve raise"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    )
                )
            } else {
                listOf(
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString(
                            "New rep max attempt\nCompetition bench press"
                        ).withSpans(
                            "New rep max attempt",
                            RelativeSizeSpan(0.8f),
                            ForegroundColorSpan(
                                application.getColor(
                                    currentBlock.value?.type?.colorId ?: R.color.hypertrophy
                                )
                            )
                        ),
                        backgroundColor = ColorUtils.setAlphaComponent(
                            application.getColor(
                                currentBlock.value?.type?.colorId ?: R.color.hypertrophy
                            ), 0x88
                        ),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("DB rows"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("Seated military press"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("DB bench"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("DB curls"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    ),
                    ExerciseSummaryAdapter.ViewDataModel(
                        nameText = SpannableString("DB skullcrushers"),
                        backgroundColor = application.getColor(R.color.transparent),
                        infoButtonVisibility = View.VISIBLE,
                        swapButtonVisibility = View.VISIBLE
                    )
                )
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * LOGIC
     */

    private fun storeSelectedSessionById(transformId: (Int?) -> Int): Completable =
        Flowable.combineLatest(
            programSessions,
            selectedSession
        ) { sessions, selectedSession ->
            Pair(sessions, selectedSession)
        }
            .linkToLoader(this)
            .take(1)
            .switchMapCompletable { (sessions, selectedSession) ->
                val newId = transformId(selectedSession.value?.id)
                sessions.find {
                    it.id == newId
                }?.let { nextSession ->
                    dataStorage.toStorage(DataDefinition.SELECTED_SESSION_ID, nextSession.id)
                } ?: Completable.complete()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * Ensures there is always a selected session.
     * The selected session will be the first to match one of these conditions in order:
     * Already selected session
     * Active session
     * First incomplete session in the program
     */
    fun refreshSelectedSession(): Completable = Flowable.combineLatest(
        activeSession,
        selectedSession,
        programSessions
    ) { activeSession, selectedSession, programSessions ->
        Triple(activeSession, selectedSession, programSessions)
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { (activeSession, selectedSession, programSessions) ->
            val sessionId = when {
                selectedSession.value?.id != null -> selectedSession.value.id
                activeSession.value?.id != null -> activeSession.value.id
                else -> programSessions.find { !it.isComplete }?.id ?: 1
            }
            dataStorage.toStorage(DataDefinition.SELECTED_SESSION_ID, sessionId)
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun goToActiveSession(): Completable = selectActiveSession()
        .linkToLoader(this)

    fun goToNextSession(): Completable = storeSelectedSessionById { currentId ->
        currentId?.let { it + 1 } ?: 1
    }

    fun goToPreviousSession(): Completable = storeSelectedSessionById { currentId ->
        currentId?.let { it - 1 } ?: 1
    }

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
                day = selectedSession.day,
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
                day = selectedSession.day,
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