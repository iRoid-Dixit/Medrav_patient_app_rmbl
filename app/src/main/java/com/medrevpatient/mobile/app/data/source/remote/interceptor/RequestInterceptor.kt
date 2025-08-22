package com.medrevpatient.mobile.app.data.source.remote.interceptor

import android.util.Log
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.App
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.repository.ServiceGenerator
import com.medrevpatient.mobile.app.data.source.remote.repository.TokenApi
import com.medrevpatient.mobile.app.model.domain.request.authReq.RefreshTokenReq
import com.medrevpatient.mobile.app.utils.AppUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class RequestInterceptor
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        try {
            runBlocking {
                val token = appPreferenceDataStore.getUserAuthData()?.accessToken ?: ""
                Log.d("TAG", "token: $token")
                val tokenStoreTime = appPreferenceDataStore.getAccessTokenStoreTime() ?: 0L
                if (token.isNotEmpty()) {
                    if (tokenStoreTime != 0L && AppUtils.getCurrentTimeInSeconds() >= tokenStoreTime) {
                        // App.instance?.restartApp()
                        fetchNewSession(builder)
                    } else {
                        builder.addHeader("Authorization", "Bearer $token")
                    }
                }
            }
        } catch (e: Exception) {
            Logger.e("exception")
        }

        return chain.proceed(builder.build())
    }

    private suspend fun fetchNewSession(builder: Request.Builder) {
        try {
            val userAuthData = appPreferenceDataStore.getUserTokenData()
            val refreshToken = userAuthData?.refreshToken ?: ""
            if (refreshToken.isEmpty()) {
                return
            }
            val refreshTokenReq = RefreshTokenReq(
                refreshToken = refreshToken
            )
            Logger.e("Fetching new session: API Call: userAuthData -> $userAuthData")
            Logger.e("Fetching new session: API Call: refreshTokenReq -> $refreshTokenReq")
            // Call API directly
            val tokenApi = ServiceGenerator.generate(TokenApi::class.java)
            val response = tokenApi.getRefreshToken(refreshTokenReq)
            if (response.isSuccessful && response.body() != null) {
                val tokenData = response.body()?.data
                if (tokenData != null) {
                    val newToken = tokenData.accessToken
                    builder.addHeader("Authorization", "Bearer $newToken")
                   /* userData?.copy(token = tokenData)
                        ?.let { updatedUserData -> appPreferenceDataStore.saveUserData(updatedUserData) }*/
                    appPreferenceDataStore.saveUserTokenData(tokenData)
                    appPreferenceDataStore.saveAccessTokenStoreTime(
                        AppUtils.getCurrentTimeInSeconds().plus(tokenData.expiresIn ?: 0)
                    )
                    if (newToken.isEmpty()) {
                        Log.d("TAG", "fetchNewSession: appRestart")
                        App.instance?.restartApp()
                    } else {
                        Log.d("TAG", "fetchNewSession: appNotRestart")
                        builder.addHeader("Authorization", "Bearer $newToken")
                    }
                }
            } else {
                Logger.e("Error fetching authentication session: ${response.errorBody()?.string()}")
                builder.addHeader("Authorization", "Bearer ${userAuthData?.accessToken}")
            }
        } catch (error: Exception) {
            Logger.e("Error fetching authentication session: $error")
        }
    }
}