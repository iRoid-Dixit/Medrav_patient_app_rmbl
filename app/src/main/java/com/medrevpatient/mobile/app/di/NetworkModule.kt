package com.medrevpatient.mobile.app.di

import com.medrevpatient.mobile.app.BuildConfig
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {


    @Singleton
    @Provides
    fun providesRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(EndPoints.URLs.BASE_URL)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(localManager: LocalManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(RequestInterceptor(localManager))
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): ApiServices {
        return retrofitBuilder.client(okHttpClient).build().create(ApiServices::class.java)
    }


    private val httpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Timber.i("Retrofit: $message") }
            .setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.BODY   //change to NONE after testing
            )

}

