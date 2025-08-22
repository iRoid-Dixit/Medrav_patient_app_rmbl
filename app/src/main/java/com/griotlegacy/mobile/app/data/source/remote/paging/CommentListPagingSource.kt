package com.griotlegacy.mobile.app.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiServices
import com.griotlegacy.mobile.app.model.domain.response.container.comment.CommentResponse
import retrofit2.HttpException
import java.io.IOException

class CommentListPagingSource(
    private val apiService: ApiServices,
    private var postId: String,
    private val apiCallback: ApiCallback?,
) : PagingSource<Int, CommentResponse>() {
    companion object {
        private const val STARTING_KEY: Int = 1
        private const val PAGE_LIMIT = 40
    }

    override fun getRefreshKey(state: PagingState<Int, CommentResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentResponse> {
        return try {
            val pageNo = params.key ?: STARTING_KEY
            val prevKey = if (pageNo == STARTING_KEY) null else pageNo - 1

            val response =
                apiService.getCommentList(page = pageNo, perPage = PAGE_LIMIT, postId = postId)
            val nextKey =
                if (response.body()?.data.isNullOrEmpty() || pageNo == response.body()?.meta?.last_page) null else pageNo + 1
            if (response.isSuccessful && response.body() != null && pageNo == PAGE_LIMIT) {
                apiCallback?.getMessage(response.body()!!.message)
            }
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

abstract class ApiCallback() {
    abstract fun getMessage(message: String)

}