package com.griotlegacy.mobile.app.ux.main.search

import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


data class SearchUiState(
    //data
    val searchUiDataFlow: StateFlow<SearchUiDataState?> = MutableStateFlow(null),
    val allPeopleListFlow: StateFlow<PagingData<SearchPeopleResponse>> = MutableStateFlow(PagingData.empty()),
    //event
    val event: (SearchUiEvent) -> Unit = {}
)


data class SearchUiDataState(
    val search: String = "",



)

sealed interface SearchUiEvent {
    data class SearchValueChange(val search: String) : SearchUiEvent
    data object NavigateToNotification : SearchUiEvent
    data object BackClick : SearchUiEvent

    data class NavigateToUserProfile(val userId: String) : SearchUiEvent
}