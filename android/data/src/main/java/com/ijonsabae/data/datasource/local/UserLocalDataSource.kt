package com.ijonsabae.data.datasource.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import okhttp3.internal.userAgent
import javax.inject.Inject

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name="user_datastore")
class UserLocalDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    companion object
    {
        private val AUTO_LOGIN_STATUS = booleanPreferencesKey("auto_login")
        private val USER_NAME = stringPreferencesKey("user_name")
    }
    suspend fun getAutoLoginStatus(): Boolean{
        return context.userDataStore.data.map { it[AUTO_LOGIN_STATUS] }.firstOrNull() ?: false
    }

    suspend fun setAutoLoginStatus(autoLogin: Boolean){
        context.userDataStore.edit { pref ->
            pref[AUTO_LOGIN_STATUS] = autoLogin
        }
    }

    suspend fun setUserName(name: String){
        context.userDataStore.edit { pref ->
            pref[USER_NAME] = name
        }
    }

    suspend fun getUserName(): String?{
        return context.userDataStore.data.map{ it[USER_NAME] }.firstOrNull()
    }

    suspend fun clearUserName(){
        context.userDataStore.edit {
            it.remove(USER_NAME)
        }
    }
}