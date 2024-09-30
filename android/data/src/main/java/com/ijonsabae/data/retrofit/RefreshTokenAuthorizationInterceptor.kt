package com.ijonsabae.data.retrofit

import com.ijonsabae.data.datastore.local.TokenLocalDataSource
import com.ijonsabae.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RefreshTokenAuthorizationInterceptor @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val refreshToken = runBlocking { tokenLocalDataSource.getRefreshToken() }
        val tokenValue = "Bearer $refreshToken"
        val request = chain.request().newBuilder()
            .addHeader("Authorization", tokenValue)
            .build()
        return chain.proceed(request)
    }
}