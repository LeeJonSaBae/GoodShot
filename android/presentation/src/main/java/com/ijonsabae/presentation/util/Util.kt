package com.ijonsabae.presentation.util

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun dpToPx(dp: Float, context: Context): Float {
    val density = context.resources.displayMetrics.density
    return dp * density
}

fun spToPx(sp: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        context.resources.displayMetrics
    )
}
fun formatDateFromLongKorea(timeInMillis: Long): String {
    val date = Date(timeInMillis)
    val format = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("Asia/Seoul") // 한국 시간대 설정
    return format.format(date)
}
//TODO 문현 : swingcode 주어졌을 때 Thumbnail, viddeo, swingimages 경로 반환하는 함수 만들기


