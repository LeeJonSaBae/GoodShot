package com.ijonsabae.data.datastore.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ijonsabae.domain.model.Token
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.tokenDataStore: DataStore<Preferences> by preferencesDataStore(name="token_datastore")

class TokenLocalDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    companion object{
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    suspend fun setToken(token: Token) {
        context.tokenDataStore.edit { pref ->
            pref[ACCESS_TOKEN] = token.accessToken
            pref[REFRESH_TOKEN] = token.refreshToken
        }
    }

    suspend fun getAccessToken(): String? {
        return context.tokenDataStore.data.map { it[ACCESS_TOKEN] }.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        return context.tokenDataStore.data.map { it[REFRESH_TOKEN] }.firstOrNull()
    }

    suspend fun clear() {
        context.tokenDataStore.edit { it.clear() }
    }
}