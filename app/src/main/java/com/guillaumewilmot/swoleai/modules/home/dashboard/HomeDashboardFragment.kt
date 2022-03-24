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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
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

    private fun ui() {
        //FIXME : TMP hardcoded UI blueprint
        binding?.toolbarLayout?.apply {
            context?.let { context ->
                this.iconAction.visibility = View.VISIBLE
                this.iconAction.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.icon_settings
                    )
                )

                this.toolbarTitle.text = SpannableString("Swole AI").apply {
                    withSpans("AI", ForegroundColorSpan(context.getColor(R.color.main)))
                }
            }
        }

        binding?.fatigueRatingValue?.text = 20.00.toString()

        binding?.fatigueChart?.apply {
            description.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            setPinchZoom(false)
            axisLeft.axisMaximum = 30f
            axisLeft.axisMinimum = 0f
            axisRight.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            axisRight.setDrawAxisLine(false)
            axisLeft.setDrawAxisLine(false)
            xAxis.setDrawAxisLine(false)

            legend.isEnabled = false

            this.data = LineData(getDataSet(this)) //listOf("Mon", "Wed", "Fri", "Sun"),
        }
    }

    private fun getDataSet(chart: LineChart) = LineDataSet(
        listOf(
            Entry(1f, 19.500f), // Mon
            Entry(2f, 19.000f), // Wed
            Entry(3f, 20.300f), // Fri
            Entry(4f, 21.000f), // Sun
        ), ""
    ).apply {
        lineWidth = 3f
        setDrawCircles(false)
        setDrawValues(false)
        setDrawFilled(true)
        fillFormatter = IFillFormatter { dataSet, dataProvider ->
            chart.axisLeft.axisMinimum
        }
        context?.let { context ->
            val color = context.getColor(R.color.main)
            this.color = color
            setCircleColor(color)
            fillDrawable = ContextCompat.getDrawable(context, R.drawable.background_fatigue_chart)
        }
    }
}