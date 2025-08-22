package com.medrevpatient.mobile.app.ux.container.groupMember
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.addMember.GroupMemberRequest
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.addPeople.AddPeopleRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
class GetGroupMemberUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val groupMemberDataFlow = MutableStateFlow(GroupMemberDataState())
    private val groupMemberListFlow =
        MutableStateFlow<PagingData<SearchPeopleResponse>>(PagingData.empty())
    private var searchJob: Job? = null
    operator fun invoke(
        coroutineScope: CoroutineScope,
        groupId: String,
        navigate: (NavigationAction) -> Unit,
    ): GroupMemberUiState {
        getGroupMemberDataList(coroutineScope = coroutineScope, groupId = groupId, name = "")
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        coroutineScope.launch {
            groupMemberDataFlow.update { state ->
                state.copy(
                    userId = appPreferenceDataStore.getUserData()?.id ?: ""
                )
            }
        }
        return GroupMemberUiState(
            groupMemberDataFlow = groupMemberDataFlow,
            groupMemberListFlow = groupMemberListFlow,
            event = { event ->
                groupMemberUiEvent(
                    groupId = groupId,
                    event = event,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }

    @OptIn(UnstableApi::class)
    private fun groupMemberUiEvent(
        groupId: String,
        event: GroupMemberUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            GroupMemberUiEvent.BackClick -> {
                navigate(NavigationAction.Pop())
            }

            is GroupMemberUiEvent.SelectedMember -> {
                groupMemberDataFlow.update { state ->
                    val selectedMembers = state.selectedMembers.toMutableList()
                    val memberId = event.memberId
                    if (selectedMembers.any { it == memberId }) {
                        selectedMembers.removeIf { it == memberId }
                    } else {
                        selectedMembers.add(memberId)
                    }
                    state.copy(
                        selectedMembers = selectedMembers
                    )
                }
            }

            is GroupMemberUiEvent.AddMemberButtonClick -> {
                navigate(
                    NavigationAction.Navigate(
                        AddPeopleRoute.createRoute(
                            tribeId = Constants.AppScreen.GROUP_MEMBER_SCREEN,
                            groupId = groupId
                        )
                    )
                )

            }

            is GroupMemberUiEvent.OnGetAddPeople -> {
                //getTribeDataList(coroutineScope, tribeId = "")
            }

            is GroupMemberUiEvent.GetContext -> {
                this.context = event.context
            }

            is GroupMemberUiEvent.SearchMember -> {
                groupMemberDataFlow.update { state ->
                    state.copy(
                        searchMember = event.search
                    )
                }
                searchJob?.cancel()
                // Start a new job with a debounce delay
                searchJob = coroutineScope.launch {
                    delay(500)
                    // Trigger API call even if the search is empty
                    getGroupMemberDataList(
                        coroutineScope = coroutineScope,
                        groupId = groupId,
                        name = event.search
                    )
                }
            }

            is GroupMemberUiEvent.RemoveUserDialog -> {
                groupMemberDataFlow.update { state ->
                    state.copy(
                        removeMemberDialog = event.show,
                        memberId = event.memberId
                    )
                }
            }

            GroupMemberUiEvent.GroupMemberAPICall -> {
                getGroupMemberDataList(
                    coroutineScope = coroutineScope,
                    groupId = groupId,
                    name = ""
                )
            }

            GroupMemberUiEvent.RemoveMember -> {
                addRemoveGroupMember(coroutineScope = coroutineScope, groupId = groupId)

            }
        }
    }


    private fun addRemoveGroupMember(
        coroutineScope: CoroutineScope,
        groupId: String,

    ) {
        coroutineScope.launch {
            val groupMemberRequest = GroupMemberRequest(
                type = Constants.GroupMember.REMOVE_MEMBER.toString(),
                groupId = groupId,
                members = listOf(groupMemberDataFlow.value.memberId)

            )
            apiRepository.addRemoveGroupMember(groupMemberRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                        val unblockedUserId = it.data?.data?.userId
                        if (unblockedUserId != null) {
                            groupMemberListFlow.update { pagingData ->
                                pagingData.filter { it1 ->
                                    it1.id != unblockedUserId
                                }
                            }
                        }
                        groupMemberDataFlow.value.removeMemberDialog = false

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }


    private fun getGroupMemberDataList(
        coroutineScope: CoroutineScope,
        groupId: String,
        name: String
    ) {
        coroutineScope.launch {
            apiRepository.getGroupMember(groupId = groupId, name = name)
                .cachedIn(this)
                .collect { pagingData ->
                    groupMemberListFlow.value = pagingData
                }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        groupMemberDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}


