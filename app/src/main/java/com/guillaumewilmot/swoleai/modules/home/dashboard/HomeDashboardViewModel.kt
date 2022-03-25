package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.app.Application
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.controller.ParentViewModel
import com.guillaumewilmot.swoleai.modules.home.session.SessionAdapter
import com.guillaumewilmot.swoleai.util.DateHelper
import com.guillaumewilmot.swoleai.util.DateHelper.DATE_FORMAT_DAY_OF_WEEK_SHORT
import com.guillaumewilmot.swoleai.util.DateHelper.minusDays
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

    //FIXME : TMP hardcoded data for blueprints
    private val _fatigueData: Flowable<List<FatigueValue>> = Flowable.create({
        val now = Date()
        it.onNext(
            listOf(
                FatigueValue(now.minusDays(3), 17.500f),
                FatigueValue(now.minusDays(2), 18.200f),
                FatigueValue(now.minusDays(1), 20.300f),
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
            this.lineWidth = 3f
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(true)
            this.color = application.getColor(R.color.colorPrimary)
            fillDrawable =
                ContextCompat.getDrawable(application, R.drawable.background_fatigue_chart)
        }
    }

    private val _fatigueChartLimitLines: Flowable<List<LimitLine>> =
        _fatigueData.map { fatigueData ->
            fatigueData.mapIndexed { i, _ ->
                LimitLine(i.toFloat()).apply {
                    lineWidth = application.pixelToDp(1f)
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

    //FIXME : TMP hardcoded data for blueprints */

}