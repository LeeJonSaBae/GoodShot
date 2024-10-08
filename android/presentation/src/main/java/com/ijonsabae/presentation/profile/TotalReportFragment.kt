package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ijonsabae.domain.model.TotalReport
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BarChartRender
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.config.GolfSwingValueFormatter
import com.ijonsabae.presentation.databinding.FragmentTotalReportBinding
import com.ijonsabae.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "굿샷_TotalReportFragment"

@AndroidEntryPoint
class TotalReportFragment : BaseFragment<FragmentTotalReportBinding>(
    FragmentTotalReportBinding::bind,
    R.layout.fragment_total_report
) {
    private lateinit var chart: BarChart
    private val totalReportCommentAdapter by lazy { TotalReportCommentAdapter() }
    private val totalReportViewModel: TotalReportViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("종합 리포트")

        lifecycleScope.launch(coroutineExceptionHandler) {
            totalReportViewModel.getTotalReport()

            totalReportViewModel.totalReport.collect { totalReport ->
                totalReport?.let {
                    initTotalScore(totalReport)
                    initChart(totalReport)
                    initTotalReportCommentsRecyclerView(totalReport)
                    initTip(totalReport)
                }

            }
        }

    }

    private fun initTotalScore(totalReport: TotalReport) {
        binding.tvTotalScoreName.text = totalReport.name
        binding.tvTotalScore.text = "${totalReport.score}점"
    }

    private fun initChart(totalReport: TotalReport) {
        chart = binding.chart
        chart.apply {
            val chartAnimator = chart.animator
            val viewPortHandler = chart.viewPortHandler
            renderer = BarChartRender(chart, chartAnimator, viewPortHandler)

            setTouchEnabled(false) // 터치 비활성화
            isHighlightPerTapEnabled = false // 하이라이트 비활성화

            // 남은 공간 채움
            // 확대 막음
            setScaleEnabled(false)
            setDrawBarShadow(true)
            setPinchZoom(false)
            legend.isEnabled = false
            description.isEnabled = false
            chart.axisLeft.run { // 왼쪽 y축
                isEnabled = false
                axisMinimum = 0f // 최소값
                axisMaximum = 100f // 최대값
                setDrawAxisLine(false)
            }
            axisRight.run { // 오른쪽 y축
                isEnabled = false
                axisMinimum = 0f // 최소값
                axisMaximum = 100f // 최대값
                granularity = 1f // 값 만큼 라인선 설정
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
            }
            xAxis.run {
                isEnabled = true
                valueFormatter = GolfSwingValueFormatter()
                position = XAxis.XAxisPosition.BOTTOM
                yOffset = 5f // 차트와 레이블 간 간격 조정
                labelRotationAngle = -45f // 레이블 방향 대각선 45도로
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
        setData(totalReport)
    }

    private fun setData(totalReport: TotalReport) {
        val values = ArrayList<BarEntry>()
        totalReport.similarity.forEachIndexed { index, similarityValue ->
            val barEntryValue = similarityValue.toFloat()
            values.add(BarEntry((index + 1).toFloat(), barEntryValue))
        }

        val barDataSet = BarDataSet(values, "Golf").apply {
            setDrawIcons(false)
            setDrawValues(false)
            colors = listOf(
                ContextCompat.getColor(requireContext(), R.color.dark_green),
                ContextCompat.getColor(requireContext(), R.color.light_green)
            )
        }

        val dataSets = ArrayList<IBarDataSet>().apply {
            add(barDataSet)
        }

        val data = BarData(dataSets).apply {
            barWidth = 0.4f
        }

        chart.apply {
            this.data = data
            invalidate()
        }
    }

    private fun initTotalReportCommentsRecyclerView(totalReport: TotalReport) {
        val recyclerView = binding.rvTotalReportComments
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = totalReportCommentAdapter
        recyclerView.setNestedScrollingEnabled(false)

        setTotalReportCommentData(totalReport)
    }

    private fun setTotalReportCommentData(totalReport: TotalReport) {
        val list = totalReport.problems
        totalReportCommentAdapter.submitList(list)
    }

    private fun initTip(totalReport: TotalReport) {
        if (totalReport.content == "") binding.tvSummary.visibility = View.GONE
        else binding.tvSummary.text = totalReport.content
    }
}