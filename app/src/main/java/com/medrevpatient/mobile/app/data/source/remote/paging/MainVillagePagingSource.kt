package com.medrevpatient.mobile.app.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiServices
import com.medrevpatient.mobile.app.model.domain.response.container.legacyPost.LegacyPostResponse
import retrofit2.HttpException
import java.io.IOException

class MainVillagePagingSource(private val apiService: ApiServices) :
    PagingSource<Int, LegacyPostResponse>() {
    companion object {
        private const val STARTING_KEY: Int = 1
        private const val PAGE_LIMIT = 40
    }

    override fun getRefreshKey(state: PagingState<Int, LegacyPostResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LegacyPostResponse> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response = apiService.getMainVillagePage(page = pageNo, perPage = PAGE_LIMIT)
            val nextKey =
                if (response.body()?.data.isNullOrEmpty() || pageNo == response.body()?.meta?.last_page) null else pageNo + 1

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