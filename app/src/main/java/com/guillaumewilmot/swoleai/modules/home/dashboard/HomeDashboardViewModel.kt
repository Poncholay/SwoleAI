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
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.*
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.ProgramBlockModel
import com.guillaumewilmot.swoleai.model.ProgramWeekModel
import com.guillaumewilmot.swoleai.model.asOptional
import com.guillaumewilmot.swoleai.modules.home.HomeActivity
import com.guillaumewilmot.swoleai.util.DateHelper
import com.guillaumewilmot.swoleai.util.DateHelper.DATE_FORMAT_DAY_OF_WEEK_SHORT
import com.guillaumewilmot.swoleai.util.DateHelper.isSameWeek
import com.guillaumewilmot.swoleai.util.DateHelper.minusDays
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import com.guillaumewilmot.swoleai.util.extension.getUserLocale
import com.guillaumewilmot.swoleai.util.extension.pixelToDp
import com.guillaumewilmot.swoleai.util.extension.withSpans
import com.guillaumewilmot.swoleai.util.fragmentBackstack.FragmentBackstack
import com.guillaumewilmot.swoleai.util.loading.HasLoader
import com.guillaumewilmot.swoleai.util.loading.HasLoaderImpl
import com.guillaumewilmot.swoleai.util.loading.linkToLoader
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
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
    dataStorage: DataStorage
) : ParentViewModel(application), HasLoader by HasLoaderImpl() {

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

    //FIXME : TMP hardcoded data for fatigue chart
    private val _programBlocksData: Flowable<List<ProgramBlockModel>> = Flowable.create({
        it.onNext(FakeProgram.fakeProgram)
    }, BackpressureStrategy.LATEST)

    private val _programWeeks: Flowable<List<ProgramWeekModel>> = _programBlocksData.map {
        it.flatMap { block ->
            block.weeks
        }
    }

    //FIXME : TMP hardcoded data, should be stored
    private val _currentWeekIndex = BehaviorSubject.createDefault(2)
    private val _currentWeekIndexFlowable =
        _currentWeekIndex.toFlowable(BackpressureStrategy.LATEST)

    private val _currentWeek: Flowable<Optional<ProgramWeekModel>> = Flowable.combineLatest(
        _programWeeks,
        _currentWeekIndexFlowable
    ) { programWeeks, currentWeekIndex ->
        programWeeks.getOrNull(currentWeekIndex).asOptional()
    }.distinctUntilChanged()

    private val _currentBlock: Flowable<Optional<ProgramBlockModel>> = Flowable.combineLatest(
        _programBlocksData,
        _currentWeek
    ) { programBlocks, currentWeek ->
        programBlocks.find {
            it.id == currentWeek.value?.blockId
        }.asOptional()
    }.distinctUntilChanged()

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
            fillDrawable =
                ContextCompat.getDrawable(application, R.drawable.background_fatigue_chart)
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
        Flowable.combineLatest(_user, _programWeeks) { user, programWeeks ->
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

    data class ProgramSummaryState(
        val daysRemainingText: String,
        val endDateText: String
    )

    private val _programChartIntensityDataSet: Flowable<LineData> =
        _programBlocksData.map { programBlocks ->
            LineData().apply {
                addDataSet(LineDataSet(
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
        _programBlocksData,
        _currentWeek
    ) { programBlocks, currentWeek ->
        BarData().apply {
            var index = 0
            fun createDataset(
                weeksData: List<ProgramWeekModel>,
                barColor: Int,
                barBorderColor: Int
            ) = BarDataSet(
                weeksData.map { programWeek ->
                    BarEntry((index++).toFloat(), programWeek.volume)
                },
                ""
            ).apply {
                setDrawValues(false)
                color = barColor
                barBorderWidth = application.pixelToDp(3f)
                this.barBorderColor = barBorderColor
            }

            val pivotDate = currentWeek.value?.date ?: Date(0)

            programBlocks.forEach { programBlock ->
                val currentColor = application.getColor(
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
                    val pastColor = application.getColor(
                        when (programBlock.type) {
                            ProgramBlockModel.BlockType.HYPERTROPHY -> R.color.hypertrophyPast
                            ProgramBlockModel.BlockType.STRENGTH -> R.color.strengthPast
                            ProgramBlockModel.BlockType.PEAKING -> R.color.peakingPast
                        }
                    )
                    addDataSet(createDataset(it, pastColor, currentColor))
                }

                //Current
                programBlock.weeks.filter {
                    it.date.isSameWeek(pivotDate)
                }.takeIf {
                    it.isNotEmpty()
                }?.let {
                    addDataSet(createDataset(it, currentColor, currentColor))
                }

                //Future
                programBlock.weeks.filter {
                    it.date.after(pivotDate) && it.date.isSameWeek(pivotDate).not()
                }.takeIf {
                    it.isNotEmpty()
                }?.let {
                    val futureColor = application.getColor(R.color.transparent)
                    addDataSet(createDataset(it, futureColor, currentColor))
                }
            }

            barWidth = 0.8f
        }
    }

    private val _programChartLegend: Flowable<List<LegendEntry>> =
        _programBlocksData.map { programBlocks ->
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
                            application.getString(R.string.app_home_dashboard_program_summary_chart_legend_intensity),
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
            _currentWeek,
            _currentBlock
        ) { currentWeek, currentBlock ->
            if (currentBlock.value == null || currentWeek.value == null) {
                return@combineLatest SpannableString("Empty")
            }

            val now = currentWeek.value.date
            val blockType = application.getString(currentBlock.value.type.nameId)
            val currentDate = DateHelper.withFormat(
                now,
                DateHelper.DATE_FORMAT_MONTH_DAY,
                application.getUserLocale()
            )
            val weekNumber = currentWeek.value.name

            val title = "$blockType\n$currentDate\n$weekNumber"
            SpannableString(title)
                .withSpans(
                    title,
                    ForegroundColorSpan(application.getColor(currentBlock.value.type.colorId))
                )
                .withSpans(
                    weekNumber,
                    StyleSpan(Typeface.BOLD),
                    RelativeSizeSpan(1.4f)
                )
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private val _weekSummaryIsCompleteIconVisibility: Flowable<Int> = _currentWeek.map {
        if (it.value?.isComplete == true) View.VISIBLE else View.GONE
    }

    private val _weekSummaryIsCompleteIconColor: Flowable<Int> = _currentBlock.map {
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

    val weekSessions: Flowable<List<SessionAdapter.SessionViewHolder.ViewModel>> =
        _currentWeek.map { currentWeek ->
            val textColor = application.getColor(R.color.textPrimary)
            val textColorCompleted = application.getColor(R.color.textTertiary)
            fun callback() = object : ParentActivity.AdapterCallback {
                override fun onClick(activity: ParentActivity, fragment: ParentFragment) {
                    (activity as? HomeActivity)?.selectTab(FragmentBackstack.Tab.SESSION)
                }
            }

            currentWeek.value?.sessions?.map { session ->
                SessionAdapter.SessionViewHolder.ViewModel(
                    nameText = session.name,
                    nameTextColor = if (session.isComplete) textColorCompleted else textColor,
                    isCompleteIconVisibility = if (session.isComplete) View.VISIBLE else View.GONE,
                    callback = callback()
                )
            } ?: listOf()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    private val _weekSummaryCompleteButtonText: Flowable<String> = _currentWeek.map {
        if (it.value?.isComplete == true) {
            application.getString(R.string.app_home_dashboard_week_summary_complete_week_button_text_complete)
        } else {
            application.getString(R.string.app_home_dashboard_week_summary_complete_week_button_text_incomplete)
        }
    }

    private val _weekSummaryCompleteButtonBackgroundId: Flowable<Int> = _currentWeek.map {
        if (it.value?.isComplete == true) {
            R.drawable.background_button_success
        } else {
            R.drawable.background_button_primary
        }
    }

    private val _weekSummaryCompleteButtonIsEnabled: Flowable<Boolean> = _currentWeek.map {
        //TODO: All sessions of the week need to be complete
        //TODO: Might need to replace zip below if more dependencies needed to calculate
        true
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
     * USER EXISTS LOGIC
     */

    /**
     * LOGIC
     */

    fun goToPreviousWeek() {
        _currentWeekIndex.take(1)
            .linkToLoader(this)
            .subscribe { currentWeekIndex ->
                if (currentWeekIndex > 0) {
                    _currentWeekIndex.onNext(currentWeekIndex - 1)
                }
            }
    }

    fun goToNextWeek() {
        Flowable.combineLatest(
            _currentWeekIndexFlowable,
            _programWeeks
        ) { currentWeekIndex, programWeeks ->
            Pair(currentWeekIndex, programWeeks)
        }.take(1)
            .linkToLoader(this)
            .subscribe { (currentWeekIndex, programWeeks) ->
                if (currentWeekIndex + 1 < programWeeks.size) {
                    _currentWeekIndex.onNext(currentWeekIndex + 1)
                }
            }
    }

    fun completeWeek() {

    }
}