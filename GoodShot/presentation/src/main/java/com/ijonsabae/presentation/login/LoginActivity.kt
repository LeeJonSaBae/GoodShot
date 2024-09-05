package com.ijonsabae.presentation.login

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.ijonsabae.presentation.R
import com.ijonsabae.presentation.config.BarChartRender
import com.ijonsabae.presentation.config.BaseActivity
import com.ijonsabae.presentation.config.GolfSwingValueFormatter
import com.ijonsabae.presentation.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

private const val TAG = "LoginActivity_싸피"
@AndroidEntryPoint
class LoginActivity: BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private lateinit var chart: BarChart
    private lateinit var splashScreen: SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen = installSplashScreen().apply {
            setKeepOnScreenCondition{
                // 1초 지연
                runBlocking {
                    delay(1000)
                }
                false
            }

        }
        //startSplash()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.chart.visibility = View.GONE
        //initChart()
        binding.btnForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.btnRegister.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun initChart(){
        chart = binding.chart
        chart.apply {
            val chartAnimator = chart.animator
            val viewPortHandler = chart.viewPortHandler
            renderer = BarChartRender(chart, chartAnimator, viewPortHandler)
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
            xAxis.run{
                isEnabled = true
                valueFormatter = GolfSwingValueFormatter()
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }
        }
        setData(8,100F)
    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values = ArrayList<BarEntry>()
        var i = start

        // 랜덤으로 데이터 만들어내는 로직
        while (i < start + count) {
            val `val` = (Math.random() * (range + 1)).toFloat()
            if (Math.random() * 100 < 25) {
                values.add(BarEntry(i, `val`, ContextCompat.getDrawable(this, R.drawable.ic_android_black_24dp)))
            } else {
                values.add(BarEntry(i.toFloat(), `val`))
            }
            i++
        }

        val barDataSet = BarDataSet(values, "Golf").apply {
            setDrawIcons(false)
            setDrawValues(false)
            colors = listOf(
                ContextCompat.getColor(
                    this@LoginActivity
                    , R.color.dark_green
                ),
                ContextCompat.getColor(
                    this@LoginActivity,
                    R.color.light_green
                )
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
}