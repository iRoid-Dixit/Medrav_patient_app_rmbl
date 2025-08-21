package com.medrevpatient.mobile.app.data.source.remote.interceptor

import com.medrevpatient.mobile.app.App
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val mainResponse = chain.proceed(chain.request())
        if (mainResponse.code == 401) {
            App.instance?.unAuthorizedAction()
        }
        return mainResponse
    }

}