package com.ijonsabae.presentation.util

import android.content.Context
import android.util.TypedValue

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

fun makeHeaderByAccessToken(accessToken: String): String {
    return "Bearer $accessToken"
}



