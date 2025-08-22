package com.medrevpatient.mobile.app.data.source.remote.repository

import android.util.Log
import com.medrevpatient.mobile.app.BuildConfig
import com.medrevpatient.mobile.app.data.source.remote.EndPoints
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ServiceGenerator {

    companion object {
        private const val CONNECTION_TIMEOUT = 15L
        private val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Log.e("Retrofit", message) }
                .setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.BODY   //change to NONE after testing
                )

        fun <T> generate(serviceClass: Class<T>): T {
            val okHttpClientBuilder = OkHttpClient().newBuilder()
                .addInterceptor(httpLoggingInterceptor)
            okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            okHttpClientBuilder.readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            val okHttpClient = okHttpClientBuilder.build()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(EndPoints.URLs.BASE_URL)
                .client(okHttpClient)
                .build()

            return retrofit.create(serviceClass)
        }
    }
}