package com.griotlegacy.mobile.app.ux.container.addPeople

import android.content.Context
import androidx.paging.PagingData
import com.griotlegacy.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AddPeopleUiState(
    //data
    val allPeopleDataFlow: StateFlow<AddPeopleDataState?> = MutableStateFlow(null),
    val allPeopleListFlow: StateFlow<PagingData<SearchPeopleResponse>> = MutableStateFlow(PagingData.empty()),
    //event
    val event: (AddPeopleUiEvent) -> Unit = {}
)

data class AddPeopleDataState(
    val showLoader: Boolean = false,
    val tribeName: String = "",
    val screen: String = "",
    val selectedMembers: List<String> = emptyList(),
    val message: String = "",
    val messageErrorMsg: String? = null,
    val searchMember: String = "",

    )

sealed interface AddPeopleUiEvent {
    data class GetContext(val context: Context) : AddPeopleUiEvent
    data object BackClick : AddPeopleUiEvent
    data class SelectedMember(val memberId: String) : AddPeopleUiEvent
    data object OnGetAddPeople : AddPeopleUiEvent
    data object OnDoneButtonClick : AddPeopleUiEvent
    data class SearchMember(val searchMember: String) : AddPeopleUiEvent

}