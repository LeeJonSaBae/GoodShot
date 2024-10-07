package com.ijonsabae.presentation.util

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
fun formatTDateFromLongKorea(timeInMillis: Long): String {
    val instant = Instant.ofEpochMilli(timeInMillis)   // 밀리초를 Instant로 변환
    val seoulZone = ZoneId.of("Asia/Seoul")            // 서울 타임존
    val seoulDateTime = LocalDateTime.ofInstant(instant, seoulZone) // Instant를 LocalDateTime으로 변환
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Z 표기 유지
    return seoulDateTime.format(formatter)             // 포맷팅
}
//TODO 문현 : swingcode 주어졌을 때 Thumbnail, viddeo, swingimages 경로 반환하는 함수 만들기


