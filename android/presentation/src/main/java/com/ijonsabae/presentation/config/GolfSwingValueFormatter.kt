package com.ijonsabae.presentation.config

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class GolfSwingValueFormatter : ValueFormatter() {
    private var index = 0

    override fun getFormattedValue(value: Float): String {
        index = value.toInt()
        return when (index) {
            1 -> "어드레스"
            2 -> "테이크백"
            3 -> "백스윙"
            4 -> "탑스윙"
            5 -> "다운스윙"
            6 -> "임팩트"
            7 -> "팔로스루"
            8 -> "피니시"
            else -> throw IndexOutOfBoundsException("index out")
        }
    }

    override fun getBarStackedLabel(value: Float, stackedEntry: BarEntry?): String {
        return super.getBarStackedLabel(value, stackedEntry)
    }
}