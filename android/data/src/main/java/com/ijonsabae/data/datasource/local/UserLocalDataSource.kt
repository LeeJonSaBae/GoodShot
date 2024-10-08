package com.ijonsabae.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.autoLoginDataStore: DataStore<Preferences> by preferencesDataStore(name="auto_login_datastore")
class UserLocalDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    companion object
    {
        private val AUTO_LOGIN_STATUS = booleanPreferencesKey("auto_login")
    }
    suspend fun getAutoLoginStatus(): Boolean{
        return context.autoLoginDataStore.data.map { it[AUTO_LOGIN_STATUS] }.firstOrNull() ?: false
    }

    suspend fun setAutoLoginStatus(autoLogin: Boolean){
        context.autoLoginDataStore.edit { pref ->
            pref[AUTO_LOGIN_STATUS] = autoLogin
        }
    }
}