package com.medrevpatient.mobile.app.ui

import androidx.compose.runtime.Composable
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

@Composable
fun <T : Any> List<T>.transformToLazyPagingItems(): LazyPagingItems<T> {
    val pagingData = PagingData.from(data = this)
    return flowOf(pagingData).collectAsLazyPagingItems()
}