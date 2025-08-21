package com.medrevpatient.mobile.app.data.source.remote.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.medrevpatient.mobile.app.data.source.remote.dto.ViewGoal
import com.medrevpatient.mobile.app.data.source.remote.repository.APIPagingCallBack
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiServices
import com.medrevpatient.mobile.app.utils.Constants
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class ViewGoalPagingSource(
    private val goalId: String,
    private val apiServices: ApiServices,
    private val apiPagingCallBack: APIPagingCallBack<ViewGoal>
) : PagingSource<Int, ViewGoal.Data.Log>() {

    companion object {
        private const val STARTING_KEY = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ViewGoal.Data.Log>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ViewGoal.Data.Log> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response = apiServices.viewGoal(
                page = pageNo, limit = Constants.Paging.PER_PAGE,
                goalId = goalId
            )
            val nextKey = if (response.body()?.data?.logs.isNullOrEmpty()) null else pageNo + 1

            val responseBody = response.body()
            if (responseBody?.data?.logs.isNullOrEmpty() && responseBody?.status !in 200..299) {
                return LoadResult.Error(
                    Exception(
                        response.body()?.message ?: "something went wrong!"
                    )
                )
            }

            if (response.isSuccessful) {
                apiPagingCallBack.onSuccess(response.body()!!)
            }

            LoadResult.Page(
                data = response.body()?.data?.logs ?: emptyList(),
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