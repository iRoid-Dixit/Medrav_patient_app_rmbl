package com.medrevpatient.mobile.app.data.source.remote.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiServices
import com.medrevpatient.mobile.app.domain.response.CommunityPosts
import com.medrevpatient.mobile.app.utils.Constants
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class CommunityPostPagingSource(
    private val apiServices: ApiServices,
    private val type: String = Constants.Paging.TYPE_ALL_POST
) : PagingSource<Int, CommunityPosts>() {

    companion object {
        private const val STARTING_KEY = 1
    }

    override fun getRefreshKey(state: PagingState<Int, CommunityPosts>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommunityPosts> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response = if (type == Constants.Paging.TYPE_ALL_POST) {
                apiServices.getAllCommunityPost(
                    page = pageNo,
                    limit = Constants.Paging.PER_PAGE,
                )
            } else {
                apiServices.getMyPost(
                    page = pageNo,
                    limit = Constants.Paging.PER_PAGE,
                )
            }
            val nextKey = if (response.body()?.data.isNullOrEmpty()) null else pageNo + 1

            val responseBody = response.body()
            if (responseBody == null && response.code() == 403) {
                val errorBody = response.errorBody()?.string()
                if (errorBody != null) {
                    try {
                        val jsonObject = JSONObject(errorBody)
                        val message = jsonObject.getString("message")
                        return LoadResult.Error(Exception(message))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } else if (responseBody?.data.isNullOrEmpty() && responseBody?.status !in 200..299) {
                return LoadResult.Error(Exception(response.body()?.message ?: "Something went wrong!"))
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