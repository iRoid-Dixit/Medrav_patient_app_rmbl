package com.medrevpatient.mobile.app.data.source.local.datastore

import com.medrevpatient.mobile.app.domain.response.Auth
import com.medrevpatient.mobile.app.domain.response.AuthResponse

interface LocalManager {
    suspend fun retrieveUserData(): AuthResponse?
    suspend fun saveUserData(userData: AuthResponse)
    suspend fun clearStorage()
    suspend fun saveUserTokenData(tokenData: Auth)
    suspend fun getUserTokenData(): Auth?
    suspend fun saveAccessTokenStoreTime(storeTime: Long)
    suspend fun getAccessTokenStoreTime(): Long?
    suspend fun savePushNotificationPermission(value : Boolean)
    suspend fun getPushNotificationPermission(): Boolean?
}