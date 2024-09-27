package com.ijonsabae.data.datastore.remote

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ijonsabae.data.retrofit.UserService
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.Token
import javax.inject.Inject

class TokenRemoteDataSource @Inject constructor(private val userService: UserService) {
    suspend fun getToken(refreshToken: String) : Result<CommonResponse<Token>> {
        return userService.reissue(refreshToken)
    }
}