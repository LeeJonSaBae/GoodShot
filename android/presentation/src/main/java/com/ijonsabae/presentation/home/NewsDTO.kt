package com.ijonsabae.presentation.home

import android.graphics.drawable.Drawable
import com.ijonsabae.domain.model.YouTubeResponse

data class NewsDTO(
    val title: String,
    val link: String?,
    val description: String,
    val thumbnail: Drawable
)