package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.app.Application
import android.graphics.DashPathEffect
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
import com.guillaumewilmot.swoleai.modules.home.session.SessionAdapter
import com.guillaumewilmot.swoleai.util.DateHelper
import com.guillaumewilmot.swoleai.util.DateHelper.DATE_FORMAT_DAY_OF_WEEK_SHORT
import com.guillaumewilmot.swoleai.util.DateHelper.isSameWeek
import com.guillaumewilmot.swoleai.util.DateHelper.minusDays
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import com.guillaumewilmot.swoleai.util.extension.getUserLocale
import com.guillaumewilmot.swoleai.util.extension.pixelToDp
import com.guillaumewilmot.swoleai.util.storage.DataStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class HomeDashboardViewModel @Inject constructor(
    application: Application,
    dataStorage: DataStorage
) : ParentViewModel(application) {

    /**
     * FATIGUE CHART
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
     * WEEK SUMMARY
     */

    //FIXME : TMP hardcoded data for blueprints */
    val weekSessions: Flowable<List<SessionAdapter.SessionViewHolder.ViewModel>> =
        Flowable.create<List<SessionAdapter.SessionViewHolder.ViewModel>>({
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * PROGRAM SUMMARY
     */

    //FIXME : This will not be stored like this
    data class ProgramWeekValue(
        val date: Date,
        val intensity: Float,
        val volume: Float
    )

    data class ProgramBlock(
        val type: BlockType,
        val weeks: List<ProgramWeekValue>
    ) {
        enum class BlockType {
            HYPERTROPHY,
            STRENGTH,
            PEAKING
        }
    }

    //FIXME : TMP hardcoded data for fatigue chart
    private val _programBlocksData: Flowable<List<ProgramBlock>> = Flowable.create({
        it.onNext(
            listOf(
                ProgramBlock(
                    ProgramBlock.BlockType.HYPERTROPHY,
                    listOf(
                        ProgramWeekValue(Date().minusDays(2 * 7), 0.5f, 1.5f),
                        ProgramWeekValue(Date().minusDays(1 * 7), 2.5f, 2f),
                        ProgramWeekValue(Date(), 3f, 3f),
                        ProgramWeekValue(Date().plusDays(1 * 7), 2.5f, 4f),
                        ProgramWeekValue(Date().plusDays(2 * 7), 1.8f, 1f)
                    )
                ),
                ProgramBlock(
                    ProgramBlock.BlockType.HYPERTROPHY,
                    listOf(
                        ProgramWeekValue(Date().plusDays(3 * 7), 2f, 2f),
                        ProgramWeekValue(Date().plusDays(4 * 7), 2.8f, 3f),
                        ProgramWeekValue(Date().plusDays(5 * 7), 2.5f, 4f),
                        ProgramWeekValue(Date().plusDays(6 * 7), 1.8f, 1f)
                    )
                ),
                ProgramBlock(
                    ProgramBlock.BlockType.STRENGTH,
                    listOf(
                        ProgramWeekValue(Date().plusDays(7 * 7), 4f, 3.5f),
                        ProgramWeekValue(Date().plusDays(8 * 7), 4.5f, 2.5f),
                        ProgramWeekValue(Date().plusDays(9 * 7), 5f, 1.5f),
                        ProgramWeekValue(Date().plusDays(10 * 7), 4f, 1f)
                    )
                ),
                ProgramBlock(
                    ProgramBlock.BlockType.PEAKING,
                    listOf(
                        ProgramWeekValue(Date().plusDays(11 * 7), 7.5f, 2.5f),
                        ProgramWeekValue(Date().plusDays(12 * 7), 8f, 2f),
                        ProgramWeekValue(Date().plusDays(13 * 7), 4f, 1.5f),
                    )
                )
            )
        )
    }, BackpressureStrategy.LATEST)

    //FIXME : TMP hardcoded data for blueprints */
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

    private val _programChartVolumeDataSet: Flowable<BarData> =
        _programBlocksData.map { programBlocks ->
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

                val now = Date()
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
                        it.date.before(now) && it.date.isSameWeek(now).not()
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
                        it.date.isSameWeek(now)
                    }.takeIf {
                        it.isNotEmpty()
                    }?.let {
                        addDataSet(createDataset(it, currentColor, currentColor))
                    }

                    //Future
                    programBlock.weeks.filter {
                        it.date.after(now) && it.date.isSameWeek(now).not()
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
            listOfNotNull(
                programBlocks.find {
                    it.type == ProgramBlock.BlockType.HYPERTROPHY
                }?.let {
                    LegendEntry(
                        application.getString(R.string.app_home_dashboard_program_summary_chart_legend_hypertrophy),
                        Legend.LegendForm.SQUARE,
                        5f,
                        Float.NaN,
                        null,
                        application.getColor(R.color.hypertrophy)
                    )
                },
                programBlocks.find {
                    it.type == ProgramBlock.BlockType.STRENGTH
                }?.let {
                    LegendEntry(
                        application.getString(R.string.app_home_dashboard_program_summary_chart_legend_strength),
                        Legend.LegendForm.SQUARE,
                        5f,
                        Float.NaN,
                        null,
                        application.getColor(R.color.strength)
                    )
                },
                programBlocks.find {
                    it.type == ProgramBlock.BlockType.PEAKING
                }?.let {
                    LegendEntry(
                        application.getString(R.string.app_home_dashboard_program_summary_chart_legend_peaking),
                        Legend.LegendForm.SQUARE,
                        5f,
                        Float.NaN,
                        null,
                        application.getColor(R.color.peaking)
                    )
                },
                LegendEntry(
                    application.getString(R.string.app_home_dashboard_program_summary_chart_legend_intensity),
                    Legend.LegendForm.LINE,
                    Float.NaN,
                    1f,
                    DashPathEffect(floatArrayOf(5f, 5f), 0f),
                    application.getColor(R.color.textTertiary)
                ),
            )
        }

    val programChartState: Flowable<ProgramChartState> = Flowable.zip(
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
}