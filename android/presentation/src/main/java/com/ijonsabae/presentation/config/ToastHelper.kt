package com.ijonsabae.presentation.config

import android.content.Context
import android.widget.Toast

class ToastHelper (private val context: Context) {
    fun showToastShort(message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
    fun showToastLong(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}