package com.medrevpatient.mobile.app.data.source.local.datastore

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.medrevpatient.mobile.app.data.source.Constants
import com.google.gson.Gson
import com.medrevpatient.mobile.app.model.domain.response.auth.Auth
import com.medrevpatient.mobile.app.model.domain.response.auth.Token
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.model.domain.response.subscription.SubscriptionResponse
import com.medrevpatient.mobile.app.utils.AppUtils
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferenceDataStore
@Inject constructor(
    private val application: Application
) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "",
        corruptionHandler = ReplaceFileCorruptionHandler {
            emptyPreferences()
        }
    )

    //User Data
    suspend fun saveUserData(userAuthResponse: UserAuthResponse) {
        val userData = Gson().toJson(userAuthResponse, UserAuthResponse::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_DATA] = userData
        }
    }

    suspend fun getUserData(): UserAuthResponse? {
        val userData = application.dataStore.data.firstOrNull()?.get(Keys.USER_DATA)
        if (userData.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(userData, UserAuthResponse::class.java)
    }

    suspend fun saveUserAuthData(authData: Auth) {
        val userAuthData = Gson().toJson(authData, Auth::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_AUTH_DATA] = userAuthData
        }
        // Calculate expiration time and store it
        val expiryTime =
            AppUtils.getCurrentTimeInSeconds() + (authData.expiresIn ?: 3600) // Default 1 hour
        saveAccessTokenExpiry(expiryTime)
        Log.d("TAG", "Saved Token Expiry Time: ${authData.expiresIn}")
    }
    suspend fun getUserAuthData(): Auth? {
        val userAuthData = application.dataStore.data.firstOrNull()?.get(Keys.USER_AUTH_DATA)
        if (userAuthData.isNullOrEmpty()) {
            return null
        }
        return Gson().fromJson(userAuthData, Auth::class.java)
    }



    //User auth data
    suspend fun saveUserTokenData(tokenData: Token) {
        val userTokenData = Gson().toJson(tokenData, Token::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.USER_TOKEN_DATA] = userTokenData
        }
    }

    suspend fun getUserTokenData(): Token? {
        val userTokenData = application.dataStore.data.firstOrNull()?.get(Keys.USER_TOKEN_DATA)
        if (userTokenData.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(userTokenData, Token::class.java)
    }

    //token time
    suspend fun saveAccessTokenStoreTime(storeTime: Long) {
        application.dataStore.edit { pref ->
            pref[Keys.ACCESS_TOKEN_SAVED_TIME] = storeTime
        }
    }

    suspend fun getAccessTokenStoreTime(): Long? {
        return application.dataStore.data.firstOrNull()?.get(Keys.ACCESS_TOKEN_SAVED_TIME)
    }

    //startUp flow start destination
    suspend fun saveStartUpStartDestination(screen: String?) {
        application.dataStore.edit { pref ->
            pref[Keys.START_DESTINATION] = screen ?: Constants.AppScreen.START_UP
        }
    }

    suspend fun getStatUpStartDestination(): String? {
        return application.dataStore.data.firstOrNull()?.get(Keys.START_DESTINATION)
    }
     suspend fun saveAccessTokenExpiry(expiryTime: Long) {
        application.dataStore.edit { pref ->
            pref[Keys.ACCESS_TOKEN_SAVED_TIME] = expiryTime
        }
        Log.d("TAG", "Saved New Token Expiry Time: $expiryTime")
    }

    //goal created
    suspend fun saveIsAnyNewGoalCreated(isAnyNewGoalCreated: Boolean) {
        application.dataStore.edit { pref ->
            pref[Keys.IS_ANY_NEW_GOAL_CREATED] = isAnyNewGoalCreated
        }
    }

    suspend fun getIsAnyNewGoalCreated(): Boolean {
        return application.dataStore.data.firstOrNull()?.get(Keys.IS_ANY_NEW_GOAL_CREATED) ?: false
    }

    //on boarding showed
    suspend fun saveIsOnBoardingShowed(isOnBoardingShowed: Boolean) {
        application.dataStore.edit { pref ->
            pref[Keys.IS_ON_BOARDING_SHOWED] = isOnBoardingShowed
        }
    }

    suspend fun getIsOnBoardingShowed(): Boolean {
        return application.dataStore.data.firstOrNull()?.get(Keys.IS_ON_BOARDING_SHOWED) ?: false
    }

    //script marked as played
    suspend fun saveIsAnyScriptMarkedAsPlayed(isAnyScriptMarkedAsPlayed: Boolean) {
        application.dataStore.edit { pref ->
            pref[Keys.IS_ANY_SCRIPT_MARKED_AS_PLAYED] = isAnyScriptMarkedAsPlayed
        }
    }

    suspend fun getIsAnyScriptMarkedAsPlayed(): Boolean {
        return application.dataStore.data.firstOrNull()?.get(Keys.IS_ANY_SCRIPT_MARKED_AS_PLAYED) ?: false
    }

    //profile updated
    suspend fun setIsProfilePicUpdated(isUpdated: Boolean) {
        application.dataStore.edit { pref ->
            pref[Keys.IS_PROFILE_PIC_UPDATED] = isUpdated
        }
    }

    suspend fun isProfilePicUpdated(): Boolean {
        val isUpdated =
            application.dataStore.data.firstOrNull()?.get(Keys.IS_PROFILE_PIC_UPDATED)
        return isUpdated ?: false
    }
    suspend fun getIsProfileUpdated(): Boolean {
        return application.dataStore.data.firstOrNull()?.get(Keys.IS_PROFILE_UPDATED) ?: false
    }

    //for notification - FCM token
    suspend fun saveFCMToken(token: String?) {
        application.dataStore.edit { pref ->
            pref[Keys.FIREBASE_MESSAGE_TOKEN] = token ?: ""
        }
    }

    suspend fun getFCMToken(): String {
        return application.dataStore.data.firstOrNull()?.get(Keys.FIREBASE_MESSAGE_TOKEN) ?: ""
    }

    //Subscription Info
    suspend fun saveSubscriptionInfo(subscriptionResponse: SubscriptionResponse) {
        val subscriptionInfoData = Gson().toJson(subscriptionResponse, SubscriptionResponse::class.java)
        application.dataStore.edit { pref ->
            pref[Keys.SUBSCRIPTION_INFO] = subscriptionInfoData
        }
    }

    suspend fun getSubscriptionInfo(): SubscriptionResponse? {
        val subscriptionInfoData = application.dataStore.data.firstOrNull()?.get(Keys.SUBSCRIPTION_INFO)
        if (subscriptionInfoData.isNullOrEmpty()) {
            return null
        }

        return Gson().fromJson(subscriptionInfoData, SubscriptionResponse::class.java)
    }


    //clearing whole pref data
    suspend fun clearAll() {
        application.dataStore.edit { it.clear() }
    }

    object Keys {
        val USER_DATA = stringPreferencesKey("userData")
        val USER_TOKEN_DATA = stringPreferencesKey("userTokenData")
        val ACCESS_TOKEN_SAVED_TIME = longPreferencesKey("accessTokenSavedTime")
        val START_DESTINATION = stringPreferencesKey("startDestination")
        val USER_AUTH_DATA = stringPreferencesKey("userAuthData")
        val IS_PROFILE_PIC_UPDATED = booleanPreferencesKey("isProfilePicUpdated")
        val IS_ANY_NEW_GOAL_CREATED = booleanPreferencesKey("isAnyNewGoalCreated")
        val IS_ON_BOARDING_SHOWED = booleanPreferencesKey("isOnboardingScreenShowed")
        val IS_ANY_SCRIPT_MARKED_AS_PLAYED = booleanPreferencesKey("isAnyScriptMarkedAsPlayed")
        val IS_PROFILE_UPDATED = booleanPreferencesKey("isProfileUpdated")
        val FIREBASE_MESSAGE_TOKEN = stringPreferencesKey("firebaseMessageToken")
        val SUBSCRIPTION_INFO = stringPreferencesKey("subscriptionInfo")
    }

}