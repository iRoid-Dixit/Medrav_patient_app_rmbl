package com.medrevpatient.mobile.app.data.source.remote.repository

import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.utils.ext.extractError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

abstract class ApiCallHelper {

    inline fun <T> lazyCall(crossinline request: suspend () -> Response<T>): Flow<NetworkResult<T>> =
        flow<NetworkResult<T>> {
            try {
                val response = request()
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) emit(NetworkResult.Success(data))
                } else {
                    emit(NetworkResult.Error(response.errorBody().extractError()))
                }
            } catch (e: IOException) {
                // IOException for network failures.
                emit(NetworkResult.Error("Network Failure"))
            } catch (e: HttpException) {
                // HttpException for any non-2xx HTTP status codes.
                emit(NetworkResult.Error(e.message))
            }
        }.onStart { emit(NetworkResult.Loading()) }.flowOn(Dispatchers.IO)
            .catch { emit(NetworkResult.Error(it.message)) }

}


abstract class APIPagingCallBack<T>() {
    abstract fun onSuccess(response: T)
}