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


fun convertConsultant(consultant: Consultant): com.ijonsabae.presentation.consult.model.Consultant{
    return com.ijonsabae.presentation.consult.model.Consultant(
        name = consultant.name,
        profileImage = consultant.profileImage,
        career = consultant.career,
        course = consultant.course,
        expertise = consultant.expertise,
        certification = consultant.certification,
        topic = consultant.topic
    )
}



