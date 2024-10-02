package com.ijonsabae.data.datastore.remote

import com.ijonsabae.data.retrofit.ConsultService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.ExpertList
import javax.inject.Inject

class ExpertRemoteDataSource @Inject constructor(
    private val consultService: ConsultService,
) {
  suspend fun getConsultantList(pageNo: Int, pageSize: Int) : Result<CommonResponse<ExpertList>> {
      return consultService.getConsultantList(pageNo, pageSize)
  }

}