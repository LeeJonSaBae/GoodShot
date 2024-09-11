package com.ijonsabae.presentation.config

import android.graphics.Canvas
import android.graphics.RectF
import android.util.Log
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil
import kotlin.math.min

private const val TAG = "BarChartRenderer_싸피"

// 직접 차트 커스텀 하기 위해 BarChartRenderer 상속 받아서 설정
class BarChartRender(
    aChart: BarChart, aAnimator: ChartAnimator,
    aViewPortHandler: ViewPortHandler,

    ) : BarChartRenderer(aChart, aAnimator, aViewPortHandler) {
    //둥글게 설정할 크기 설정
    private val mRadius = 1130F
    private val mBarShadowRectBuffer = RectF()
    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) { // 내부적으로 그래프 그릴 때 호출되는 함수
        val trans = mChart.getTransformer(dataSet.axisDependency)
        mShadowPaint.color = dataSet.barShadowColor
        Log.d(TAG, "drawDataSet: $mShadowPaint.color")
        val phaseX = mAnimator.phaseX //애니메이션(막대바 데이터에 맞게 바 길이 늘어나는 애니메이션)의 x축에 대한 진행 정도
        val phaseY = mAnimator.phaseY //애니메이션(막대바 데이터에 맞게 바 길이 늘어나는 애니메이션)의 y축에 대한 진행 정도

        if (mBarBuffers != null) {
            // Initialize the buffer
            val buffer = mBarBuffers[index] // mBarBuffers는 막대 바를 가지는 array로 판단됨
            // 해당 막대의 애니메이션 진행도를 설정
            // ex: 막대바 길이가 100이고 phase가 0.5로 설정되어 있으면 50은 이미 그려진 상태에서 애니메이션 시작
            buffer.setPhases(phaseX, phaseY)
            //barWidth 등
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.feed(dataSet) // 주어진 데이터 바탕으로 버퍼 구축 후 막대바에게 데이터 공급이 끝나면 버퍼를 초기화 함
            trans.pointValuesToPixel(buffer.buffer) //buffer 안에 값을 pixel단위로 바꿔주는 듯 함 확실 X


            // draw the bar shadow before the values
            if (mChart.isDrawBarShadowEnabled) {
                mShadowPaint.color = dataSet.barShadowColor

                val barData = mChart.barData

                val barWidth = barData.barWidth
                val barWidthHalf = barWidth / 2.0f
                var x: Float

                var i = 0
                val count = min(ceil((dataSet.entryCount.toFloat() * phaseX).toDouble()).toInt().toDouble(), dataSet.entryCount.toDouble()).toInt()
                while (i < count
                ) {
                    val e = dataSet.getEntryForIndex(i)

                    x = e.x

                    mBarShadowRectBuffer.left = x - barWidthHalf
                    mBarShadowRectBuffer.right = x + barWidthHalf

                    trans.rectValueToPixel(mBarShadowRectBuffer)

                    if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                        i++
                        continue
                    }

                    if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break

                    mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                    mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()

                    c.drawRoundRect(mBarShadowRectBuffer, mRadius, mRadius, mShadowPaint)
                    i++
                }
            }

            // If multiple colors
            if (dataSet.colors.size > 1) {
                var j = 0
                //막대가 뷰포트(화면에 표시된 차트 영역)내에서 좌측 경계, 우측 경계 내에 있는지를 확인해서 조건 처리
                while (j < buffer.size()) {
                    mRenderPaint.color = dataSet.colors[(j/4) % 2]
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                        break
                    }

                    if (mRadius > 0) { // 굴곡 값도 설정되어 있다면
                        c.drawRoundRect(
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3],
                            mRadius, mRadius,
                            mRenderPaint
                        )
                    } else {
                        c.drawRect( // 그냥 그리기
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3],
                            mRenderPaint
                        )
                    }
                    j += 4
                }
            } else { //컬러가 한개일 때
                mRenderPaint.color = dataSet.color
                var j = 0
                while (j < buffer.size()) {
                    if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) {
                        break
                    }

                    if (mRadius > 0) {
                        c.drawRoundRect(
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3],
                            mRadius, mRadius,
                            mRenderPaint
                        )
                    } else { //설정 안했을 때
                        c.drawRect(
                            buffer.buffer[j],
                            buffer.buffer[j + 1],
                            buffer.buffer[j + 2],
                            buffer.buffer[j + 3],
                            mRenderPaint
                        )
                    }
                    j += 4 //=> 한 entry에 상하좌우 4개를 사용하므로 4씩 건너 뛰어야 함
                }
            }
        }
    }
}