package com.ijonsabae.presentation.config

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.ijonsabae.presentation.R

class HalfHighlightTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val highlightPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.highlight) // 하이라이트 색상 설정
        style = Paint.Style.FILL // 채우기 스타일
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        val layout = layout // 텍스트 레이아웃 객체 가져오기

        // 텍스트 라인 수만큼 하이라이트 처리
        for (i in 0 until layout.lineCount) {
            // 각 라인의 시작, 끝 좌표 구하기
            val lineStart = layout.getLineStart(i)
            val lineEnd = layout.getLineEnd(i)

            // 라인 내 텍스트 추출
            val lineText = text.substring(lineStart, lineEnd)

            // 해당 라인의 텍스트 위치와 크기 계산
            val lineLeft = layout.getLineLeft(i)
            val lineTop = layout.getLineTop(i).toFloat()
            val lineBottom = layout.getLineBottom(i).toFloat()

            // 라인의 높이 절반 계산 (하이라이트 적용 범위)
            val highlightTop = (lineTop + lineBottom) / 2

            // 하이라이트 그리기 (하단 절반)
            canvas.drawRect(
                lineLeft,  // 텍스트의 시작 x 좌표
                highlightTop,  // 텍스트 중앙
                layout.getLineRight(i),  // 텍스트의 끝 x 좌표
                lineBottom,  // 텍스트의 끝 y 좌표 (하단)
                highlightPaint
            )
        }

        // 기본 텍스트 그리기 (super 호출)
        super.onDraw(canvas)
    }
}
