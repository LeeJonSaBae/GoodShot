package com.ijonsabae.data.retrofit

import android.util.Log
import com.ijonsabae.domain.repository.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "TokenInterceptor_싸피"

class TokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response

        runBlocking {
            val accessToken = tokenRepository.getLocalAccessToken()
            val accessTokenCreatedTime = tokenRepository.getLocalTokenCreatedTime()
            if (shouldRefreshToken(accessToken, accessTokenCreatedTime)){
                refreshToken()
            }
            val newAccessToken = tokenRepository.getLocalAccessToken()
            val newTokenValue = "Bearer $newAccessToken"
            Log.d(TAG, "intercept: $newTokenValue")

            response = if(newAccessToken.isNullOrBlank()){
                // 비로그인 상태
                val request = chain.request().newBuilder()
                    .build()
                chain.proceed(request)
            }else{
                /** 로그인 상태 **/
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", newTokenValue)
                    .build()
                chain.proceed(request)
            }
        }

        return response
    }

    private fun isAccessTokenWillInValid(accessTokenCreatedTime: Long): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifference = currentTimeMillis - accessTokenCreatedTime
        val baseTime = 3600000L // 60분
        Log.i("isAccessTokenValid", (timeDifference >= baseTime).toString())
        return timeDifference >= baseTime
    }

    private fun isRefreshTokenWillInValid(accessTokenCreatedTime: Long): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifference = currentTimeMillis - accessTokenCreatedTime
        val baseTime = 3600000L * 24L * 7L // 1주
        Log.i("isRefreshTokenValid", (timeDifference >= baseTime).toString())
        return timeDifference >= baseTime
    }

    private fun shouldRefreshToken(accessToken: String?, accessTokenCreatedTime: Long?): Boolean {
        val isLogin = !(accessToken.isNullOrBlank() || accessTokenCreatedTime == null)

        return if (isLogin) {
            (accessTokenCreatedTime?.let
            { isAccessTokenWillInValid(it) }
                ?: false) || (accessTokenCreatedTime?.let { isRefreshTokenWillInValid(it) }
                ?: false)
        } else {
            false
        }
    }

    private suspend fun refreshToken() {
        val refreshResponse = tokenRepository.reissueRemoteToken()
        refreshResponse.onSuccess {
            tokenRepository.setLocalToken(it.data)
        }.onFailure {throwable ->
            //다른 기기로 로그인 기존 token 변경됐거나 refresh Token마저 유효기간이 끝난 경우
            Log.e(TAG, "refreshToken: $throwable", )
            Log.i("Interceptor Refresh", "failed..")
            logout()
        }
    }

    private suspend fun logout(){
        tokenRepository.clearToken()
    }
}