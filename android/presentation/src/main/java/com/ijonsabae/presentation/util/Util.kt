package com.ijonsabae.presentation.util

import android.content.Context
import android.util.TypedValue
import com.ijonsabae.domain.model.Consultant

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



