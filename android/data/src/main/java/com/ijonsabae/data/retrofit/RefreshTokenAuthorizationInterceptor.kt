package com.ijonsabae.data.retrofit

import android.util.Log
import com.ijonsabae.data.datasource.local.TokenLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "RefreshTokenAuthorizationInterceptor_싸피"
class RefreshTokenAuthorizationInterceptor @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val refreshToken = runBlocking { tokenLocalDataSource.getRefreshToken() }
        val tokenValue = "$refreshToken"
        val request = chain.request().newBuilder()
            .addHeader("refreshToken", tokenValue)
            .build()
        return chain.proceed(request)
    }
}