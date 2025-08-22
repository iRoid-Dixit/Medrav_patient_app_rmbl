package com.griotlegacy.mobile.app.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiServices
import com.griotlegacy.mobile.app.model.domain.response.tribe.TribeResponse
import retrofit2.HttpException
import java.io.IOException

class TribePagingSource(val type: Int,private val apiService: ApiServices) : PagingSource<Int, TribeResponse>() {

    companion object {
        private const val STARTING_KEY: Int = 1
        private const val PAGE_LIMIT = 10
    }

    override fun getRefreshKey(state: PagingState<Int, TribeResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TribeResponse> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response = apiService.getListInnerCircleAndTribe(type = type ,page = pageNo, perPage = PAGE_LIMIT)
            val nextKey = if (response.body()?.data.isNullOrEmpty() || pageNo == response.body()?.meta?.last_page) null else pageNo + 1

            LoadResult.Page(
                data = response.body()?.data ?: emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: IOException) {
            return LoadResult.Error(IOException(e))
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}