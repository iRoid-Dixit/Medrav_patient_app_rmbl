package com.medrevpatient.mobile.app.data.source.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManagerImpl.Keys.USER_DATA
import com.medrevpatient.mobile.app.domain.response.Auth
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.utils.Constants.DataStore.PREFERENCE_NAME
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

/* data store declaration */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

@Singleton
class LocalManagerImpl
@Inject constructor(
    private val context: Context
) : LocalManager {

    override suspend fun saveUserData(userData: AuthResponse) {
        val data = Gson().toJson(userData)
        context.dataStore.edit { pref ->
            pref[USER_DATA] = data
        }
    }

    override suspend fun retrieveUserData(): AuthResponse? {
        val jsonString = context.dataStore.data.firstOrNull()?.get(USER_DATA)
        if (jsonString.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(jsonString, AuthResponse::class.java)
    }

    override suspend fun clearStorage() {
        context.dataStore.edit { it.clear() }
    }

    override suspend fun saveUserTokenData(tokenData: Auth) {
        val userTokenData = Gson().toJson(tokenData, Auth::class.java)
        context.dataStore.edit { pref ->
            pref[Keys.USER_TOKEN_DATA] = userTokenData
        }
    }

    override suspend fun getUserTokenData(): Auth? {
        val userTokenData = context.dataStore.data.firstOrNull()?.get(Keys.USER_TOKEN_DATA)
        if (userTokenData.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(userTokenData, Auth::class.java)
    }

    //token time
    override suspend fun saveAccessTokenStoreTime(storeTime: Long) {
        context.dataStore.edit { pref ->
            pref[Keys.ACCESS_TOKEN_SAVED_TIME] = storeTime
        }
    }

    override suspend fun getAccessTokenStoreTime(): Long? {
        return context.dataStore.data.firstOrNull()?.get(Keys.ACCESS_TOKEN_SAVED_TIME)
    }

    override suspend fun savePushNotificationPermission(value : Boolean) {
        context.dataStore.edit { pref ->
            pref[Keys.PUSH_NOTIFICATIONS] = value
        }
    }

    override suspend fun getPushNotificationPermission(): Boolean? {
        return context.dataStore.data.firstOrNull()?.get(Keys.PUSH_NOTIFICATIONS)
    }


    object Keys {
        val USER_DATA = stringPreferencesKey("userData")
        val USER_TOKEN_DATA = stringPreferencesKey("userTokenData")
        val ACCESS_TOKEN_SAVED_TIME = longPreferencesKey("accessTokenSavedTime")
        val PUSH_NOTIFICATIONS = booleanPreferencesKey("pushNotifications")
    }
}