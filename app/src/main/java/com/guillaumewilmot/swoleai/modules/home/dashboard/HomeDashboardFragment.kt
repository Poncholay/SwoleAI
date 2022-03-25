package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeDashboardBinding
import com.guillaumewilmot.swoleai.util.extension.toDp
import com.guillaumewilmot.swoleai.util.extension.withSpans
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeDashboardFragment : ParentFragment() {

    private var binding: FragmentHomeDashboardBinding? = null
    private val viewModel: HomeDashboardViewModel by viewModels()

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
        ui()
        setupBlueprintFatigueChart()
    }

    //FIXME : TMP just some hardcoded UI blueprint
    private fun ui() {
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

        binding?.fatigueRatingValue?.text = 20.00.toString()
    }

    //FIXME : TMP just some hardcoded UI blueprint
    private fun setupBlueprintFatigueChart() {
        binding?.fatigueChart?.let { chart ->
            chart.setTouchEnabled(false)
            chart.setPinchZoom(false)
            chart.isDragEnabled = false
            chart.description.isEnabled = false
            chart.legend.isEnabled = false

            chart.minOffset = 0f //Remove start end padding
            chart.extraLeftOffset = 1f //Show left axis
            chart.extraRightOffset = 1f

            chart.axisLeft.axisMaximum = 25f
            chart.axisLeft.axisMinimum = 5f
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawAxisLine(false)
            chart.axisLeft.setDrawLabels(false)

            chart.axisRight.isEnabled = false

            val lineWidth = activity.toDp(1f)
            val textColor = chart.context.getColor(R.color.textPrimary)
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setDrawAxisLine(true)
            chart.xAxis.axisMaximum = 3f
            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisLineWidth = lineWidth
            chart.xAxis.setAvoidFirstLastClipping(true)
            chart.xAxis.textColor = textColor
            chart.xAxis.axisLineColor = textColor

            listOf(LimitLine(0f), LimitLine(1f), LimitLine(2f), LimitLine(3f)).forEach {
                it.lineWidth = lineWidth
                it.lineColor = textColor
                chart.xAxis.addLimitLine(it)
            }
            chart.xAxis.setDrawLimitLinesBehindData(true)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Wed", "Fri", "Sun"))

            chart.data = LineData(LineDataSet(
                listOf(
                    Entry(0f, 17.500f), // Mon
                    Entry(1f, 18.200f), // Wed
                    Entry(2f, 20.300f), // Fri
                    Entry(3f, 21.000f), // Sun
                ), ""
            ).apply {
                this.lineWidth = 3f
                setDrawCircles(false)
                setDrawValues(false)
                setDrawFilled(true)
                fillFormatter = IFillFormatter { _, _ ->
                    chart.axisLeft.axisMinimum
                }
                context?.let { context ->
                    this.color = context.getColor(R.color.colorPrimary)
                    fillDrawable =
                        ContextCompat.getDrawable(context, R.drawable.background_fatigue_chart)
                }
            })

            chart.invalidate()
        }
    }
}