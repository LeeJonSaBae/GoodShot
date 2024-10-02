package com.ijonsabae.data.retrofit

import com.ijonsabae.domain.model.ExpertList
import com.ijonsabae.domain.model.CommonResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultService {
    @GET("experts")
    suspend fun getConsultantList(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int,
    ): Result<CommonResponse<ExpertList>>
}