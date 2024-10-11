package com.ijonsabae.data.datasource.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ijonsabae.domain.model.Token
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.TimeZone
import javax.inject.Inject

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name="token_datastore")
private const val TAG = "TokenLocalDataSource 싸피"
class TokenLocalDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    companion object{
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = longPreferencesKey("user_id")
        private val TOKEN_CREATED_TIME = longPreferencesKey("token_created_time")
    }

    suspend fun setToken(token: Token) {
        context.tokenDataStore.edit { pref ->
            pref[ACCESS_TOKEN] = token.accessToken
            pref[REFRESH_TOKEN] = token.refreshToken
            pref[USER_ID] = token.userId
        }
    }

    suspend fun setLocalTokenCreatedTime() {
        context.tokenDataStore.edit { pref ->
            // 한국 시간대(Asia/Seoul)로 설정된 캘린더 인스턴스 생성
            val koreanTimeZone = TimeZone.getTimeZone("Asia/Seoul")
            val calendar: Calendar = Calendar.getInstance(koreanTimeZone)

            // 현재 시각을 밀리초로 가져오기 (1970년 1월 1일 기준 밀리초)
            val currentTimeInMillis: Long = calendar.timeInMillis
            pref[TOKEN_CREATED_TIME] = currentTimeInMillis
        }
    }

    suspend fun getLocalTokenCreatedTime(): Long?{
        return context.tokenDataStore.data.map { it[TOKEN_CREATED_TIME] }.firstOrNull()
    }

    suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data.map { it[ACCESS_TOKEN] }.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return context.tokenDataStore.data.map { it[REFRESH_TOKEN] }.firstOrNull()
    }

    suspend fun getUserId(): Long{
        return context.tokenDataStore.data.map{it[USER_ID]}.first() ?: -1
    }

    suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
    }

    suspend fun getLocalAccessTokenFlow(): StateFlow<String?> {
        return context.tokenDataStore.data.map { it[ACCESS_TOKEN] }.stateIn(CoroutineScope(Dispatchers.IO))
    }
}