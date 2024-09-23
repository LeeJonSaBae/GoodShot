package com.ijonsabae.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BarChartRender
import com.ijonsabae.presentation.config.BaseFragment
import com.ijonsabae.presentation.config.GolfSwingValueFormatter
import com.ijonsabae.presentation.databinding.FragmentTotalReportBinding
import com.ijonsabae.presentation.main.MainActivity


class TotalReportFragment : BaseFragment<FragmentTotalReportBinding>(
    FragmentTotalReportBinding::bind,
    R.layout.fragment_total_report
) {
    private lateinit var chart: BarChart
    private val totalReportCommentAdapter by lazy { TotalReportCommentAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (fragmentContext as MainActivity).showAppBar("종합 리포트")
        initTotalScore()
        initChart()
        initTotalReportCommentsRecyclerView()
        initTip()
    }

    private fun initTotalScore() {
        binding.tvTotalScoreName.text = "밍깅이"
        binding.tvTotalScore.text = "48.5점"
    }

    private fun initChart() {
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
        setData(8, 100F)
    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values = ArrayList<BarEntry>()
        var i = start

        // 랜덤으로 데이터 만들어내는 로직
        while (i < start + count) {
            val `val` = (Math.random() * (range + 1)).toFloat()
            if (Math.random() * 100 < 25) {
                values.add(
                    BarEntry(
                        i,
                        `val`,
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_android_black_24dp
                        )
                    )
                )
            } else {
                values.add(BarEntry(i, `val`))
            }
            i++
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

    private fun initTotalReportCommentsRecyclerView() {
        val recyclerView = binding.rvTotalReportComments
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = totalReportCommentAdapter
        recyclerView.setNestedScrollingEnabled(false)

        setTotalReportCommentData()
    }

    private fun setTotalReportCommentData() {
        val list =
            listOf(
                "올바른 무게 중심의 이동 문제 및 생크나 탑핑을 발생시킬 수 있습니다.",
                "골반 회전의 부족으로 팔로스루 시 오른팔을 당기는 스윙과 양손과 몸의 공간이 부족하여 임팩트 시 ‘헤드 업’이 발생할 수 있습니다.",
                "올바른 무게 중심의 이동 문제 및 생크나 탑핑을 발생시킬 수 있습니다.",
                "올바른 무게 중심의 이동 문제 및 생크나 탑핑을 발생시킬 수 있습니다.",
                "올바른 무게 중심의 이동 문제 및 생크나 탑핑을 발생시킬 수 있습니다."
            )

        totalReportCommentAdapter.submitList(list)
    }

    private fun initTip() {
        binding.tvTip.text =
            "다운스윙 시 손과 클럽 보다 빠르게 골반을 오른발 뒤꿈치 바깥쪽으로 열며 회전하는 연습을 해야 합니다. 힘 있는 골반 오픈을 위해서는 전환 동작 시 오른발 쪽으로 약간 주저 앉았다가 (스쿼트 동작) 오픈해 주면 골반의 회전력을 높일 수 있습니다."
    }
}