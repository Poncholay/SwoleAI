package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.app.Application
import android.graphics.DashPathEffect
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.*
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgram
import com.guillaumewilmot.swoleai.modules.home.program.CanInteractWithProgramImpl
import com.guillaumewilmot.swoleai.util.DateHelper
import com.guillaumewilmot.swoleai.util.DateHelper.DATE_FORMAT_DAY_OF_WEEK_SHORT
import com.guillaumewilmot.swoleai.util.DateHelper.isSameWeek
import com.guillaumewilmot.swoleai.util.DateHelper.minusDays
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import com.guillaumewilmot.swoleai.util.extension.getUserLocale
import com.guillaumewilmot.swoleai.util.extension.pixelToDp
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
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    application: Application,
    private val dataStorage: DataStorage
) : ParentViewModel(application),
    HasLoader by HasLoaderImpl(),
    CanInteractWithProgram by CanInteractWithProgramImpl(dataStorage) {

    private val _user = dataStorage.dataHolder.userField

    val userDashboardVisibility: Flowable<Int> = _user.map {
        if (it.value == null) View.GONE else View.VISIBLE
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val noUserDashboardVisibility: Flowable<Int> = _user.map {
        if (it.value == null) View.VISIBLE else View.GONE
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    /**
     * Data
     */

    //FIXME : Fatigue will be mapped from completed sessions
    data class FatigueValue(
        val date: Date,
        val value: Float
    )

    //FIXME : TMP hardcoded data for fatigue chart
    private val _fatigueData: Flowable<List<FatigueValue>> = Flowable.create({
        val now = Date()
        it.onNext(
            listOf(
                FatigueValue(now.minusDays(3), 17.500f),
                FatigueValue(now.minusDays(2), 18.200f),
                FatigueValue(now.minusDays(1), 25.300f),
                FatigueValue(now, 21.000f)
            )
        )
    }, BackpressureStrategy.LATEST)

    private val _viewedWeekIdSubject = BehaviorSubject.createDefault(1)
    private val _viewedWeekId = _viewedWeekIdSubject.toFlowable(BackpressureStrategy.LATEST)

    private val _viewedWeek: Flowable<Nullable<ProgramWeekModel>> = Flowable.combineLatest(
        programWeeks,
        _viewedWeekId
    ) { programWeeks, viewedWeekIndex ->
        programWeeks.find { it.id == viewedWeekIndex }.asNullable()
    }.distinctUntilChanged()

    private val _viewedBlock = getProgramBlockFromProgramWeek(_viewedWeek)

    /**
     * FATIGUE CHART
     */

    private val _fatigueChartDataSet: Flowable<LineDataSet> = _fatigueData.map { fatigueData ->
        LineDataSet(
            fatigueData.mapIndexed { i, fatigueEntry ->
                Entry(i.toFloat(), fatigueEntry.value)
            },
            ""
        ).apply {
            lineWidth = 3f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(true)
            color = application.getColor(R.color.colorPrimary)

            //This drawable does not depend on a theme
            fillDrawable = ContextCompat.getDrawable(
                application, R.drawable.background_fatigue_chart
            )
        }
    }

    private val _fatigueChartLimitLines: Flowable<List<LimitLine>> =
        _fatigueData.map { fatigueData ->
            fatigueData.mapIndexed { i, _ ->
                LimitLine(i.toFloat()).apply {
                    lineWidth = application.pixelToDp(2f)
                    lineColor = application.getColor(R.color.textPrimary)
                }
            }
        }

    private val _fatigueChartxAxisValues: Flowable<List<String>> = _fatigueData.map { fatigueData ->
        fatigueData.map {
            DateHelper.withFormat(
                it.date,
                DATE_FORMAT_DAY_OF_WEEK_SHORT,
                application.getUserLocale()
            )
        }
    }

    val currentFatigueValue: Flowable<Float> = _fatigueData.map { fatigueData ->
        fatigueData.lastOrNull()?.value ?: 0f
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    val fatigueChartState: Flowable<FatigueChartState> = Flowable.zip(
        _fatigueChartDataSet,
        _fatigueChartLimitLines,
        _fatigueChartxAxisValues
    ) { dataset, limitLines, xAxisValues ->
        FatigueChartState(dataset, limitLines, xAxisValues)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class FatigueChartState(
        val dataset: LineDataSet,
        val limitLines: List<LimitLine>,
        val xAxisValues: List<String>
    )

    /**
     * PROGRAM SUMMARY
     */

    val programSummaryState: Flowable<ProgramSummaryState> =
        Flowable.combineLatest(_user, programWeeks) { user, programWeeks ->
            val endDate = programWeeks.lastOrNull()?.date?.plusDays(7) ?: Date()
            val daysRemaining = TimeUnit.DAYS.convert(
                endDate.time - Date().time,
                TimeUnit.MILLISECONDS
            ).toInt()

            ProgramSummaryState(
                daysRemainingText = application.resources.getQuantityString(
                    R.plurals.app_home_dashboard_program_summary_days_remaining_text,
                    daysRemaining,
                    user.value?.username ?: "",
                    daysRemaining.toString()
                ),
                endDateText = DateHelper.withFormat(
                    endDate,
                    DateHelper.DATE_FORMAT_WEEKDAY_DAY_MONTH_YEAR,
                    application.getUserLocale()
                )
            )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    data class ProgramSummaryState(
        val daysRemainingText: String,
        val endDateText: String
    )

    private val _programChartIntensityDataSet: Flowable<LineData> =
        programBlocks.map { programBlocks ->
            LineData().apply {
                addDataSet(
                    LineDataSet(
                        programBlocks.flatMap {
                            it.weeks
                        }.mapIndexed { i, programWeek ->
                            Entry(i.toFloat(), programWeek.intensity)
                        },
                        ""
                    ).apply {
                        lineWidth = 1f
                        setDrawCircles(false)
                        setDrawValues(false)
                        setDrawFilled(false)
                        enableDashedLine(5f, 5f, 0f)
                        color = application.getColor(R.color.textTertiary)
                    })
            }
        }

    private val _programChartVolumeDataSet: Flowable<BarData> = Flowable.combineLatest(
        programBlocks,
        _viewedWeek
    ) { programBlocks, viewedWeek ->
        BarData().apply {
            var index = 0
            fun createDataset(
                weeksData: List<ProgramWeekModel>,
                barBackgroundColor: Int,
                barBorderColor: Int
            ) = BarDataSet(
                weeksData.map { programWeek ->
                    BarEntry((index++).toFloat(), programWeek.volume)
                },
                ""
            ).apply {
                setDrawValues(false)
                color = barBackgroundColor
                barBorderWidth = application.pixelToDp(3f)
                this.barBorderColor = barBorderColor
            }

            val pivotDate = viewedWeek.value?.date ?: Date(0)

            programBlocks.forEach { programBlock ->
                val borderColor = application.getColor(
                    when (programBlock.type) {
                        ProgramBlockModel.BlockType.HYPERTROPHY -> R.color.hypertrophy
                        ProgramBlockModel.BlockType.STRENGTH -> R.color.strength
                        ProgramBlockModel.BlockType.PEAKING -> R.color.peaking
                    }
                )

                //Past
                programBlock.weeks.filter {
                    it.date.before(pivotDate) && it.date.isSameWeek(pivotDate).not()
                }.takeIf {
                    it.isNotEmpty()
                }?.let {
                    val backgroundColor = ColorUtils.setAlphaComponent(borderColor, 0x88)
                    addDataSet(createDataset(it, backgroundColor, borderColor))
                }

                //Current
                programBlock.weeks.filter {
                    it.date.isSameWeek(pivotDate)
                }.takeIf {
                    it.isNotEmpty()
                }?.let {
                    addDataSet(createDataset(it, barBackgroundColor = borderColor, borderColor))
                }

                //Future
                programBlock.weeks.filter {
                    it.date.after(pivotDate) && it.date.isSameWeek(pivotDate).not()
                }.takeIf {
                    it.isNotEmpty()
                }?.let {
                    val backgroundColor = application.getColor(R.color.transparent)
                    addDataSet(createDataset(it, backgroundColor, borderColor))
                }
            }

            barWidth = 0.8f
        }
    }

    private val _programChartLegend: Flowable<List<LegendEntry>> =
        programBlocks.map { programBlocks ->
            programBlocks.distinctBy { it.type }
                .map { block ->
                    LegendEntry(
                        application.getString(block.type.nameId),
                        Legend.LegendForm.SQUARE,
                        5f,
                        Float.NaN,
                        null,
                        application.getColor(block.type.colorId)
                    )
                }
                .toMutableList()
                .apply {
                    add(
                        LegendEntry(
                            application.getString(
                                R.string.app_home_dashboard_program_summary_chart_legend_intensity
                            ),
                            Legend.LegendForm.LINE,
                            Float.NaN,
                            1f,
                            DashPathEffect(floatArrayOf(5f, 5f), 0f),
                            application.getColor(R.color.textTertiary)
                        )
                    )
                }
        }

    val programChartState: Flowable<ProgramChartState> = Flowable.combineLatest(
        _programChartVolumeDataSet,
        _programChartIntensityDataSet,
        _programChartLegend
    ) { volumeDataset, intensityDataset, programChartLegend ->
        ProgramChartState(volumeDataset, intensityDataset, programChartLegend)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class ProgramChartState(
        val volumeData: BarData,
        val intensityData: LineData,
        val legend: List<LegendEntry>,
    )

    /**
     * WEEK SUMMARY
     */

    val weekSummaryTitle: Flowable<SpannableString> =
        Flowable.combineLatest(
            _viewedWeek,
            _viewedBlock
        ) { viewedWeek, viewedBlock ->
            if (viewedBlock.value == null || viewedWeek.value == null) {
                return@combineLatest SpannableString("Empty")
            }

            val now = viewedWeek.value.date
            val blockType = application.getString(viewedBlock.value.type.nameId)
            val currentDate = DateHelper.withFormat(
                now,
                DateHelper.DATE_FORMAT_MONTH_DAY,
                application.getUserLocale()
            )
            val weekNumber = viewedWeek.value.name

            val title = "$blockType\n$currentDate\n$weekNumber"
            SpannableString(title)
                .withSpans(
                    title,
                    ForegroundColorSpan(application.getColor(viewedBlock.value.type.colorId))
                )
                .withSpans(
                    weekNumber,
                    StyleSpan(Typeface.BOLD),
                    RelativeSizeSpan(1.4f)
                )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private val _weekSummaryIsCompleteIconVisibility: Flowable<Int> = _viewedWeek.map {
        if (it.value?.isComplete == true) View.VISIBLE else View.GONE
    }

    private val _weekSummaryIsCompleteIconColor: Flowable<Int> = _viewedBlock.map {
        application.getColor(it.value?.type?.colorId ?: R.color.textPrimary)
    }

    val weekSummaryIsCompleteIconState: Flowable<WeekSummaryIsCompleteIconState> =
        Flowable.combineLatest(
            _weekSummaryIsCompleteIconVisibility,
            _weekSummaryIsCompleteIconColor
        ) { iconVisibility, iconColor ->
            WeekSummaryIsCompleteIconState(iconVisibility, iconColor)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    data class WeekSummaryIsCompleteIconState(
        val visibility: Int,
        val iconColor: Int
    )

    val goToActiveWeekButtonVisibility: Flowable<Int> = Flowable.combineLatest(
        _viewedWeek,
        activeSession
    ) { viewedWeek, activeSession ->
        if (activeSession.value != null && viewedWeek.value != null &&
            activeSession.value.weekId != viewedWeek.value.id
        ) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())


    val weekSessions: Flowable<List<SessionAdapter.SessionViewHolder.ViewDataModel>> =
        Flowable.combineLatest(
            _viewedWeek,
            _viewedBlock
        ) { viewedWeek, viewedBlock ->
            val textColor = application.getColor(R.color.textPrimary)
            val textColorCompleted = application.getColor(R.color.textTertiary)
            val textColorSkipped = application.getColor(R.color.textQuaternary)
            val textColorActive = viewedBlock.value?.type?.colorId?.let {
                application.getColor(it)
            } ?: textColor

            viewedWeek.value?.sessions?.map { session ->
                val sessionName =
                    application.getString(R.string.app_session_day_name, session.fullName)
                SessionAdapter.SessionViewHolder.ViewDataModel(
                    nameText = when (session.status) {
                        SessionModel.Status.ACTIVE -> application.getString(
                            R.string.app_home_dashboard_week_summary_session_active_text,
                            sessionName
                        )
                        SessionModel.Status.SKIPPED -> application.getString(
                            R.string.app_home_dashboard_week_summary_session_skipped_text,
                            sessionName
                        )
                        else -> sessionName
                    },
                    nameTextColor = when (session.status) {
                        SessionModel.Status.COMPLETE -> textColorCompleted
                        SessionModel.Status.SKIPPED -> textColorSkipped
                        SessionModel.Status.ACTIVE -> textColorActive
                        else -> textColor
                    },
                    iconId = when (session.status) {
                        SessionModel.Status.COMPLETE -> R.drawable.icon_check_circle
                        SessionModel.Status.SKIPPED -> R.drawable.icon_cross_circle
                        else -> null
                    },
                    isLoading = session.status == SessionModel.Status.ACTIVE
                )
            } ?: listOf()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private val _weekSummaryCompleteButtonText: Flowable<String> = _viewedWeek.map {
        if (it.value?.isComplete == true) {
            application.getString(
                R.string.app_home_dashboard_week_summary_complete_week_button_text_completed
            )
        } else {
            application.getString(
                R.string.app_home_dashboard_week_summary_complete_week_button_text_incomplete
            )
        }
    }

    private val _weekSummaryCompleteButtonBackgroundId: Flowable<Int> = _viewedWeek.map {
        if (it.value?.isComplete == true) {
            R.drawable.background_button_success
        } else {
            R.drawable.background_button_primary
        }
    }

    private val _weekSummaryCompleteButtonIsEnabled: Flowable<Boolean> = _viewedWeek.map {
        it.value?.sessions?.all { session ->
            session.status == SessionModel.Status.COMPLETE
        } == true
    }

    val weekSummaryCompleteButtonState: Flowable<WeekSummaryCompleteButtonState> = Flowable.zip(
        _weekSummaryCompleteButtonText,
        _weekSummaryCompleteButtonBackgroundId,
        _weekSummaryCompleteButtonIsEnabled
    ) { buttonText, buttonBackgroundId, buttonIsEnabled ->
        WeekSummaryCompleteButtonState(buttonText, buttonBackgroundId, buttonIsEnabled)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class WeekSummaryCompleteButtonState(
        val text: String,
        val backgroundId: Int,
        val isEnabled: Boolean
    )

    /**
     * LOGIC
     */

    fun goToActiveWeek(): Completable = activeSession
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { activeSession ->
            activeSession.value?.weekId?.let {
                _viewedWeekIdSubject.onNext(it)
            }
            Completable.complete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())

    fun goToPreviousWeek(): Completable = _viewedWeekIdSubject
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { viewedWeekIndex ->
            if (viewedWeekIndex > 1) {
                _viewedWeekIdSubject.onNext(viewedWeekIndex - 1)
            }
            Completable.complete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())

    fun goToNextWeek(): Completable = Flowable.combineLatest(
        _viewedWeekId,
        programWeeks
    ) { viewedWeekIndex, programWeeks ->
        Pair(viewedWeekIndex, programWeeks)
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { (viewedWeekIndex, programWeeks) ->
            if (viewedWeekIndex + 1 <= programWeeks.maxOf { it.id }) {
                _viewedWeekIdSubject.onNext(viewedWeekIndex + 1)
            }
            Completable.complete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(Schedulers.io())

    fun onSessionSelected(index: Int): Flowable<Boolean> = Flowable.combineLatest(
        _viewedWeek,
        activeSession
    ) { viewedWeek, activeSession ->
        Pair(viewedWeek, activeSession)
    }
        .linkToLoader(this)
        .take(1)
        .switchMap { (viewedWeek, activeSession) ->
            val session = viewedWeek.value?.sessions?.getOrNull(index)
            val activeSessionId = activeSession.value?.id
            val shouldGoBackToRoot = activeSessionId == null || activeSessionId != session?.id
            dataStorage.toStorage(DataDefinition.SELECTED_SESSION_ID, session?.id)
                .andThen(Flowable.just(shouldGoBackToRoot))
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun completeWeek(): Completable = Completable.complete() //TODO

    /**
     * Ensures the selected viewed week is always up to date.
     * The selected week will be the first to match one of these conditions in order:
     * Week contains selected session
     * Week contains active session
     * Week is not complete
     */
    fun refreshViewedWeek(): Completable = Flowable.combineLatest(
        activeSession,
        selectedSession,
        programWeeks
    ) { activeSession, selectedSession, programWeeks ->
        Triple(activeSession, selectedSession, programWeeks)
    }
        .linkToLoader(this)
        .take(1)
        .switchMapCompletable { (activeSession, selectedSession, programWeeks) ->
            when {
                selectedSession.value != null -> _viewedWeekIdSubject.onNext(
                    selectedSession.value.weekId
                )
                activeSession.value != null -> _viewedWeekIdSubject.onNext(
                    activeSession.value.weekId
                )
                else -> programWeeks.find {
                    !it.isComplete
                }?.id?.let { id ->
                    _viewedWeekIdSubject.onNext(id)
                }
            }
            Completable.complete()
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}