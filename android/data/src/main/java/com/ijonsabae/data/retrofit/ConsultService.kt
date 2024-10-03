package com.ijonsabae.data.retrofit

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.ExpertDetail
import com.ijonsabae.domain.model.ExpertList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ConsultService {
    @GET("experts")
    suspend fun getConsultantList(
        @Query("pageNo") pageNo: Int,
        @Query("pageSize") pageSize: Int,
    ): Result<CommonResponse<ExpertList>>

    @GET("experts/{id}")
    suspend fun getConsultantInfo(
        @Path("id") id: Int
    ): Result<CommonResponse<ExpertDetail>>
}