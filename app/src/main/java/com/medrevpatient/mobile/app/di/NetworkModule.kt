package com.medrevpatient.mobile.app.di

import android.util.Log
import com.medrevpatient.mobile.app.BuildConfig
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.EndPoints
import com.medrevpatient.mobile.app.data.source.remote.interceptor.AuthInterceptor
import com.medrevpatient.mobile.app.data.source.remote.interceptor.RequestInterceptor
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val httpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Log.e("Retrofit", message) }
            .setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.BODY   //change to NONE after testing
            )
    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(EndPoints.URLs.BASE_URL)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(appPreferenceDataStore: AppPreferenceDataStore): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(RequestInterceptor(appPreferenceDataStore))
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofitBuilder: Retrofit.Builder, okHttpClient: OkHttpClient): ApiServices {
        return retrofitBuilder.client(okHttpClient).build().create(ApiServices::class.java)
    }

}

