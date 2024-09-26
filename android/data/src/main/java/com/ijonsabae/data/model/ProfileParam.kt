package com.ijonsabae.data.model

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "굿샷_ProfileParam"

@Serializable
data class ProfileParam(
    val imageExtension: String
) {
    fun toRequestBody(): RequestBody {
        Log.d(TAG, "toRequestBody: ${Json.encodeToString(this)}")
        return Json.encodeToString(this).toRequestBody()
    }
}