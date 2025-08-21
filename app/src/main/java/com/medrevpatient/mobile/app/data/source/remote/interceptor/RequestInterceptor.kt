package com.medrevpatient.mobile.app.data.source.remote.interceptor

import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.utils.AppUtils
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject

class RequestInterceptor @Inject constructor(
    private val localManager: LocalManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Accept", "application/json")
        try {
            runBlocking {
                val token = localManager.getUserTokenData()?.accessToken ?: ""
                val refreshToken = localManager.getUserTokenData()?.refreshToken ?: ""
                val tokenStoreTime = localManager.getAccessTokenStoreTime() ?: 0L

                if (token.isNotEmpty()) {
                    if (tokenStoreTime != 0L && AppUtils.getCurrentTimeInSeconds() >= tokenStoreTime) {
                        builder.addHeader("Authorization", "Bearer $refreshToken")
                    } else {
                        builder.addHeader("Authorization", "Bearer $token")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e("exception")
        }


        val response = chain.proceed(builder.build())

        //Use for log response
        val respString = getResponseString(response)
        Timber.tag("NetworkModule").d("intercept: $respString")

        return response
    }

    private fun getResponseString(response: Response): String? {
        val responseBody = response.body
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source?.buffer
        var charset: Charset? = Charset.forName("UTF-8")
        val contentType = responseBody?.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))
        }
        if (charset == null) {
            return ""
        }
        return buffer?.clone()?.readString(charset)
    }


}
