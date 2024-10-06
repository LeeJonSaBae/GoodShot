package com.ijonsabae.presentation.config

import android.Manifest
import android.os.Build
import androidx.core.content.ContextCompat

class Const {
    companion object{
        val GalleryPermission = when{
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                 arrayOf(
                     Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            else -> {
                arrayOf(
                )
            }
        }

        const val BACKSWING = 0
        const val DOWNSWING = 1

    }
}