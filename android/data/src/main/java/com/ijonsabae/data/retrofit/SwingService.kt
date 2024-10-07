package com.ijonsabae.data.retrofit

import com.ijonsabae.domain.model.SwingFeedbackSyncRoomData
import retrofit2.http.Body
import retrofit2.http.POST

interface SwingService {
    @POST("swings/sync")
    suspend fun syncSwingFeedback(@Body syncData: List<SwingFeedbackSyncRoomData>)
}