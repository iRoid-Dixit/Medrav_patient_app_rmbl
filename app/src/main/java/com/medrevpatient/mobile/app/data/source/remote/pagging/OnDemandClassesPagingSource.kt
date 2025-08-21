package com.medrevpatient.mobile.app.data.source.remote.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiServices
import com.medrevpatient.mobile.app.utils.Constants
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class OnDemandClassesPagingSource(
    private val type: Int,
    private val apiServices: ApiServices
) : PagingSource<Int, OnDemandClasses>() {

    companion object {
        private const val STARTING_KEY = 1
    }

    override fun getRefreshKey(state: PagingState<Int, OnDemandClasses>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OnDemandClasses> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response = apiServices.getOnDemandClasses(
                page = pageNo, limit = Constants.Paging.PER_PAGE,
                type = type
            )
            val nextKey = if (response.body()?.data.isNullOrEmpty()) null else pageNo + 1

            val responseBody = response.body()
            if (responseBody?.data.isNullOrEmpty()
                && responseBody?.status !in 200..299
            ) {
                return LoadResult.Error(
                    Exception(
                        response.body()?.message ?: "Something went wrong!"
                    )
                )
            }

            LoadResult.Page(
                data = response.body()?.data ?: emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            Timber.e(e)
            return LoadResult.Error(IOException("Network Failure"))
        } catch (e: HttpException) {
            Timber.e(e)
            return LoadResult.Error(e)
        } catch (exception: Exception) {
            Timber.e(exception)
            return LoadResult.Error(exception)
        }
    }
}