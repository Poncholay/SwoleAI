package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import autodispose2.androidx.lifecycle.autoDispose
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeDashboardBinding
import com.guillaumewilmot.swoleai.modules.home.session.SessionAdapter
import com.guillaumewilmot.swoleai.util.DateHelper
import com.guillaumewilmot.swoleai.util.DateHelper.plusDays
import com.guillaumewilmot.swoleai.util.extension.dpToPixel
import com.guillaumewilmot.swoleai.util.extension.getUserLocale
import com.guillaumewilmot.swoleai.util.extension.pixelToDp
import com.guillaumewilmot.swoleai.util.extension.withSpans
import com.guillaumewilmot.swoleai.view.EqualSpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.lang.Float.max
import java.util.*


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeDashboardFragment : ParentFragment() {

    private var binding: FragmentHomeDashboardBinding? = null
    private val viewModel: HomeDashboardViewModel by viewModels()
    private val sessionAdapter: SessionAdapter by lazy {
        SessionAdapter(adapterCallbackWrapper)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentHomeDashboardBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        ui()
        setupFatigueChart()
        setupProgramChart()

        /**
         * Fatigue chart
         */

        viewModel.currentFatigueValue
            .autoDispose(this, Lifecycle.Event.ON_PAUSE)
            .subscribe { fatigue ->
                binding?.fatigueRatingValue?.text = fatigue.toString()
            }

        viewModel.fatigueChartState
            .autoDispose(this, Lifecycle.Event.ON_PAUSE)
            .subscribe { state ->
                binding?.fatigueChart?.apply {
                    val padding = (state.dataset.yMax - state.dataset.yMin) * 0.5f

                    axisLeft.axisMaximum = state.dataset.yMax + padding
                    axisLeft.axisMinimum = max(0f, state.dataset.yMin - padding)

                    xAxis.axisMaximum = state.dataset.xMax
                    xAxis.axisMinimum = state.dataset.xMin

                    xAxis.valueFormatter = IndexAxisValueFormatter(state.xAxisValues)

                    data = LineData(state.dataset.apply {
                        fillFormatter = IFillFormatter { _, _ ->
                            axisLeft?.axisMinimum ?: 0f
                        }
                    })

                    state.limitLines.forEach { limitLine ->
                        binding?.fatigueChart?.xAxis?.addLimitLine(limitLine)
                    }

                    invalidate()
                    visibility = View.VISIBLE
                }
            }

        /**
         * Week summary
         */

        viewModel.weekSessions
            .autoDispose(this, Lifecycle.Event.ON_PAUSE)
            .subscribe {
                sessionAdapter.data = it
            }

        /**
         * Program summmary
         */

        viewModel.programChartState
            .autoDispose(this, Lifecycle.Event.ON_PAUSE)
            .subscribe { state ->
                binding?.programChart?.apply {
                    legend.setCustom(state.legend)
                    legend.isWordWrapEnabled = true

                    axisLeft.axisMaximum = max(state.intensityData.yMax, state.volumeData.yMax)
                    axisLeft.axisMinimum = -1f //Avoid bar borders getting cutoff

                    data = CombinedData().apply {
                        setData(state.volumeData)
                        setData(state.intensityData)
                    }

                    //This allows first and last bar to not be half cutoff + borders
                    xAxis.axisMinimum = -barData.barWidth / 2f - 1f
                    xAxis.axisMaximum = state.volumeData.xMax + barData.barWidth / 2f + 1f

                    invalidate()
                    visibility = View.VISIBLE
                }
            }
    }

    /**
     * FIXME : Below is temporary hardcoded UI blueprint
     * Includes a fake program summary
     * Includes a fake fatigue chart
     */

    private fun setupToolbar() {
        binding?.toolbarLayout?.toolbarContent?.apply {
            this.iconAction.visibility = View.VISIBLE
            this.iconAction.setImageDrawable(
                ContextCompat.getDrawable(
                    this.iconAction.context,
                    R.drawable.icon_settings
                )
            )

            this.toolbarTitle.text = SpannableString("Swole AI").withSpans(
                "AI",
                ForegroundColorSpan(this.iconAction.context.getColor(R.color.secondary))
            )
        }
    }

    private fun ui() {
        binding?.fatigueRatingValue?.text = 20.00.toString()

        binding?.weekTitle?.text = SpannableString("Hypertrophy\nMarch 21st\nWeek 3").withSpans(
            "Week 3",
            StyleSpan(Typeface.BOLD),
            RelativeSizeSpan(1.4f)
        )

        binding?.weekSessions?.apply {
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            addItemDecoration(EqualSpacingItemDecoration(this.context.dpToPixel(8f).toInt()))
            adapter = sessionAdapter
        }

        val daysOut = 172
        val fakeProgramEnd = Date().plusDays(daysOut)
        binding?.programEndDaysRemaining?.text = resources.getQuantityString(
            R.plurals.app_home_dashboard_program_summary_days_remaining_text,
            daysOut,
            daysOut.toString()
        )
        binding?.programEndDate?.text = DateHelper.withFormat(
            fakeProgramEnd,
            DateHelper.DATE_FORMAT_FULL_DATE,
            context.getUserLocale()
        )
    }

    private fun setupFatigueChart() {
        binding?.fatigueChart?.let { chart ->
            val textColor = chart.context.getColor(R.color.textPrimary)

            chart.visibility = View.INVISIBLE
            chart.setTouchEnabled(false)
            chart.setPinchZoom(false)
            chart.isDragEnabled = false
            chart.description.isEnabled = false
            chart.legend.isEnabled = false
            chart.minOffset = 0f //Remove start end padding
            chart.extraTopOffset = 5f

            chart.axisLeft.isEnabled = true
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawAxisLine(false)
            chart.axisLeft.setDrawLabels(false)

            chart.axisRight.isEnabled = true
            chart.axisRight.setDrawGridLines(false)
            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(true)
            chart.axisRight.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            chart.axisRight.textColor = textColor

            chart.xAxis.textColor = textColor
            chart.xAxis.axisLineColor = textColor
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setDrawAxisLine(true)
            chart.xAxis.axisLineWidth = activity.pixelToDp(2f)
            chart.xAxis.setAvoidFirstLastClipping(true)
            chart.xAxis.setDrawLimitLinesBehindData(false)
        }
    }

    private fun setupProgramChart() {
        binding?.programChart?.let { chart ->
            chart.visibility = View.INVISIBLE
            chart.setTouchEnabled(false)
            chart.setPinchZoom(false)
            chart.isDragEnabled = false
            chart.description.isEnabled = false
            chart.legend.isEnabled = true
            chart.legend.textColor = chart.context.getColor(R.color.textSecondary)
            chart.legend.xOffset = 15f

            chart.minOffset = 0f //Remove start end padding

            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawAxisLine(false) //true
            chart.axisLeft.setDrawLabels(false)

            chart.axisRight.isEnabled = false
            chart.xAxis.isEnabled = false

            chart.drawOrder = arrayOf(
                DrawOrder.LINE, DrawOrder.BAR
            )
        }
    }
}