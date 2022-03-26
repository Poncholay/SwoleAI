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
import com.google.gson.annotations.SerializedName
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.model.Optional
import com.guillaumewilmot.swoleai.model.asOptional
import com.guillaumewilmot.swoleai.modules.home.session.SessionAdapter
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
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.Serializable
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application), HasLoader by HasLoaderImpl() {

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

    //FIXME : This will not be stored like this
    data class ProgramWeekValue(
        @SerializedName("id")
        val id: Int,
        @SerializedName("blockId")
        val blockId: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("date")
        val date: Date,
        @SerializedName("intensity")
        val intensity: Float,
        @SerializedName("volume")
        val volume: Float,
        @SerializedName("isComplete")
        val isComplete: Boolean,
    ) : Serializable

    data class ProgramBlock(
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type: BlockType,
        @SerializedName("weeks")
        val weeks: List<ProgramWeekValue>
    ) : Serializable {
        enum class BlockType(val nameId: Int, val colorId: Int) {
            HYPERTROPHY(R.string.app_block_type_hypertrophy, R.color.hypertrophy),
            STRENGTH(R.string.app_block_type_strength, R.color.strength),
            PEAKING(R.string.app_block_type_peaking, R.color.peaking);
        }
    }

    //FIXME : TMP hardcoded data for fatigue chart
    private val _programBlocksData: Flowable<List<ProgramBlock>> = Flowable.create({
        it.onNext(
            listOf(
                ProgramBlock(
                    1,
                    ProgramBlock.BlockType.HYPERTROPHY,
                    listOf(
                        ProgramWeekValue(1, 1, "Week 1", Date().minusDays(2 * 7), 0.5f, 1.5f, true),
                        ProgramWeekValue(2, 1, "Week 2", Date().minusDays(1 * 7), 2.5f, 2f, true),
                        ProgramWeekValue(3, 1, "Week 3", Date(), 3f, 3f, false),
                        ProgramWeekValue(4, 1, "Week 4", Date().plusDays(1 * 7), 2.5f, 4f, false),
                        ProgramWeekValue(5, 1, "Week 5", Date().plusDays(2 * 7), 1.8f, 1f, false)
                    )
                ),
                ProgramBlock(
                    2,
                    ProgramBlock.BlockType.HYPERTROPHY,
                    listOf(
                        ProgramWeekValue(6, 2, "Week 6", Date().plusDays(3 * 7), 2f, 2f, false),
                        ProgramWeekValue(7, 2, "Week 7", Date().plusDays(4 * 7), 2.8f, 3f, false),
                        ProgramWeekValue(8, 2, "Week 8", Date().plusDays(5 * 7), 2.5f, 4f, false),
                        ProgramWeekValue(9, 2, "Week 9", Date().plusDays(6 * 7), 1.8f, 1f, false)
                    )
                ),
                ProgramBlock(
                    3,
                    ProgramBlock.BlockType.STRENGTH,
                    listOf(
                        ProgramWeekValue(10, 3, "Week 10", Date().plusDays(7 * 7), 4f, 3.5f, false),
                        ProgramWeekValue(
                            11,
                            3,
                            "Week 11",
                            Date().plusDays(8 * 7),
                            4.5f,
                            2.5f,
                            false
                        ),
                        ProgramWeekValue(12, 3, "Week 12", Date().plusDays(9 * 7), 5f, 1.5f, false),
                        ProgramWeekValue(13, 3, "Week 13", Date().plusDays(10 * 7), 4f, 1f, false)
                    )
                ),
                ProgramBlock(
                    4,
                    ProgramBlock.BlockType.PEAKING,
                    listOf(
                        ProgramWeekValue(
                            14,
                            4,
                            "Week 14",
                            Date().plusDays(11 * 7),
                            7.5f,
                            2.5f,
                            false
                        ),
                        ProgramWeekValue(15, 4, "Week 15", Date().plusDays(12 * 7), 8f, 2f, false),
                        ProgramWeekValue(
                            16,
                            4,
                            "Week 16",
                            Date().plusDays(13 * 7),
                            4f,
                            1.5f,
                            false
                        ),
                    )
                )
            )
        )
    }, BackpressureStrategy.LATEST)

    private val _programWeeks: Flowable<List<ProgramWeekValue>> = _programBlocksData.map {
        it.flatMap { block ->
            block.weeks
        }
    }

    //FIXME : TMP hardcoded data, should be stored
    private val _currentWeekIndex = BehaviorSubject.createDefault(3)
    private val _currentWeekIndexFlowable =
        _currentWeekIndex.toFlowable(BackpressureStrategy.LATEST)

    private val _currentWeek: Flowable<Optional<ProgramWeekValue>> = Flowable.combineLatest(
        _programWeeks,
        _currentWeekIndexFlowable
    ) { programWeeks, currentWeekIndex ->
        programWeeks.getOrNull(currentWeekIndex).asOptional()
    }

    private val _currentBlock: Flowable<Optional<ProgramBlock>> = Flowable.combineLatest(
        _programBlocksData,
        _currentWeek
    ) { programBlocks, currentWeek ->
        programBlocks.find {
            it.id == currentWeek.value?.blockId
        }.asOptional()
    }

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
                weeksData: List<ProgramWeekValue>,
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
                        ProgramBlock.BlockType.HYPERTROPHY -> R.color.hypertrophy
                        ProgramBlock.BlockType.STRENGTH -> R.color.strength
                        ProgramBlock.BlockType.PEAKING -> R.color.peaking
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
                            ProgramBlock.BlockType.HYPERTROPHY -> R.color.hypertrophyPast
                            ProgramBlock.BlockType.STRENGTH -> R.color.strengthPast
                            ProgramBlock.BlockType.PEAKING -> R.color.peakingPast
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
                    LegendEntry(
                        application.getString(R.string.app_home_dashboard_program_summary_chart_legend_intensity),
                        Legend.LegendForm.LINE,
                        Float.NaN,
                        1f,
                        DashPathEffect(floatArrayOf(5f, 5f), 0f),
                        application.getColor(R.color.textTertiary)
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

    private val _weekSummaryTitle: Flowable<SpannableString> =
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

    private val _weekSummaryIsCompleteIconVisibility: Flowable<Int> = _currentWeek.map {
        if (it.value?.isComplete == true) View.VISIBLE else View.GONE
    }

    private val _weekSummaryIsCompleteIconColor: Flowable<Int> = _currentBlock.map {
        application.getColor(it.value?.type?.colorId ?: R.color.textPrimary)
    }

    //FIXME : TMP hardcoded data for blueprints */
    private val _weekSessions: Flowable<List<SessionAdapter.SessionViewHolder.ViewModel>> =
        Flowable.create({
            val textColor = application.getColor(R.color.textPrimary)
            val textColorCompleted = application.getColor(R.color.textTertiary)
            val callback = object : ParentActivity.AdapterCallback {
                override fun onClick(activity: ParentActivity, fragment: ParentFragment) {

                }
            }

            it.onNext(
                listOf(
                    SessionAdapter.SessionViewHolder.ViewModel(
                        nameText = "Day 1 - Lower body",
                        nameTextColor = textColorCompleted,
                        isCompleteIconVisibility = View.VISIBLE,
                        callback = callback
                    ),
                    SessionAdapter.SessionViewHolder.ViewModel(
                        nameText = "Day 2 - Upper body",
                        nameTextColor = textColorCompleted,
                        isCompleteIconVisibility = View.VISIBLE,
                        callback = callback
                    ),
                    SessionAdapter.SessionViewHolder.ViewModel(
                        nameText = "Day 3 - Lower body",
                        nameTextColor = textColor,
                        isCompleteIconVisibility = View.GONE,
                        callback = callback
                    ),
                    SessionAdapter.SessionViewHolder.ViewModel(
                        nameText = "Day 4 - Upper body",
                        nameTextColor = textColor,
                        isCompleteIconVisibility = View.GONE,
                        callback = callback
                    ),
                )
            )
        }, BackpressureStrategy.LATEST)

    val weekSummaryState: Flowable<WeekSummaryState> = Flowable.combineLatest(
        _weekSummaryTitle,
        _weekSummaryIsCompleteIconVisibility,
        _weekSummaryIsCompleteIconColor,
        _weekSessions
    ) { title, isCompleteIconVisibility, isCompleteIconColor, sessions ->
        WeekSummaryState(title, isCompleteIconVisibility, isCompleteIconColor, sessions)
    }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    data class WeekSummaryState(
        val title: SpannableString,
        val isCompleteIconVisibility: Int,
        val isCompleteIconColor: Int,
        val sessions: List<SessionAdapter.SessionViewHolder.ViewModel>
    )

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
}