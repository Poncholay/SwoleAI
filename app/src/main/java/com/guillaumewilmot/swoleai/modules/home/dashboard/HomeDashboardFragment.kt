package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentHomeDashboardBinding
import com.guillaumewilmot.swoleai.util.withSpans
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

        binding?.fatigueChart?.apply {
            setTouchEnabled(false)
            setPinchZoom(false)
            isDragEnabled = false
            description.isEnabled = false
            legend.isEnabled = false

            minOffset = 0f

            axisLeft.axisMaximum = 30f
            axisLeft.axisMinimum = 10f
            axisLeft.setDrawGridLines(false)
            axisLeft.setDrawAxisLine(false)
            axisLeft.setDrawLabels(false)

            axisRight.isEnabled = false

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setDrawAxisLine(true)
            xAxis.setCenterAxisLabels(true)
            xAxis.valueFormatter =
                IndexAxisValueFormatter(listOf("Mon", "Wed", "Fri", "Sun")).apply {

                }
            xAxis.textColor = context.getColor(R.color.textPrimary)


            val dataSet = getDataSet(this)
            this.data = LineData(dataSet)
        }
    }

    private fun getDataSet(chart: LineChart) = LineDataSet(
        listOf(
            Entry(0f, 17.500f), // Mon
            Entry(1f, 18.200f), // Wed
            Entry(2f, 20.300f), // Fri
            Entry(3f, 21.000f), // Sun
        ), ""
    ).apply {
        lineWidth = 3f
        setDrawCircles(false)
        setDrawValues(false)
        setDrawFilled(true)
        fillFormatter = IFillFormatter { _, _ ->
            chart.axisLeft.axisMinimum
        }
        context?.let { context ->
            this.color = context.getColor(R.color.colorPrimary)
            fillDrawable = ContextCompat.getDrawable(context, R.drawable.background_fatigue_chart)
        }
    }
}