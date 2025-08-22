package com.medrevpatient.mobile.app.ux.container.addPeople

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.addMember.AddMemberRequest
import com.medrevpatient.mobile.app.model.domain.request.addMember.GroupMemberRequest
import com.medrevpatient.mobile.app.model.domain.response.searchPeople.SearchPeopleResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.PopResultKeyValue
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
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

class GetAddPeopleUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val tribeListDataFlow = MutableStateFlow(AddPeopleDataState())
    private val allPeopleListFlow =
        MutableStateFlow<PagingData<SearchPeopleResponse>>(PagingData.empty())
    private var searchJob: Job? = null
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        tribeId: String,
        groupId: String,
        navigate: (NavigationAction) -> Unit,
    ): AddPeopleUiState {
        android.util.Log.d("TAG", "screenName: $groupId")
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        tribeListDataFlow.update { state ->
            state.copy(
                screen = tribeId
            )
        }
        if (tribeId == Constants.AppScreen.GROUP_MEMBER_SCREEN) {

            addGroupMember(coroutineScope = coroutineScope, groupId = groupId, search = null)
        } else {
            getAllPeopleDataList(
                coroutineScope,
                tribeId = if (tribeId == Constants.AppScreen.CREATE_CIRCLE_SCREEN) "" else tribeId
            ) // empty string for get all list

        }
        return AddPeopleUiState(
            allPeopleDataFlow = tribeListDataFlow,
            allPeopleListFlow = allPeopleListFlow,
            event = { event ->
                tribeListUiEvent(
                    tribeId = tribeId,
                    event = event,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    groupId = groupId
                )
            }
        )
    }

    @OptIn(UnstableApi::class)
    private fun tribeListUiEvent(
        tribeId: String,
        event: AddPeopleUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        groupId: String,
    ) {
        when (event) {
            AddPeopleUiEvent.BackClick -> {
                navigate(NavigationAction.Pop())
            }

            is AddPeopleUiEvent.SelectedMember -> {
                tribeListDataFlow.update { state ->
                    val selectedMembers = state.selectedMembers.toMutableList()
                    val memberId = event.memberId
                    if (selectedMembers.any { it == memberId }) {
                        selectedMembers.removeIf { it == memberId }
                    } else {
                        selectedMembers.add(memberId)
                    }
                    Log.d("checkMemberList", "selectedMembers: ${selectedMembers.size}")
                    state.copy(
                        selectedMembers = selectedMembers
                    )
                }
            }

            is AddPeopleUiEvent.OnDoneButtonClick -> {
                if (tribeId == Constants.AppScreen.CREATE_CIRCLE_SCREEN) {

                    val memberListJson = Gson().toJson(tribeListDataFlow.value.selectedMembers)
                    navigate(
                        NavigationAction.PopWithResult(
                            resultValues = listOf(
                                PopResultKeyValue("memberList", memberListJson),
                            )
                        )
                    )
                } else {
                    if (tribeListDataFlow.value.selectedMembers.isNotEmpty()) {
                        if (tribeListDataFlow.value.screen == Constants.AppScreen.GROUP_MEMBER_SCREEN) {
                            addRemoveGroupMember(
                                coroutineScope = coroutineScope,
                                groupId = groupId,
                                navigate = navigate
                            )
                        } else {
                            addPeopleApiCall(
                                tribeId = tribeId,
                                context = this.context,
                                coroutineScope = coroutineScope,
                                navigate = navigate
                            )
                        }
                    } else {
                        showWaringMessage(
                            context = this.context,
                            message = context.getString(R.string.please_select_at_least_one_member)
                        )
                    }

                }
            }

            is AddPeopleUiEvent.OnGetAddPeople -> {
                //getTribeDataList(coroutineScope, tribeId = "")
            }

            is AddPeopleUiEvent.GetContext -> {
                this.context = event.context
            }

            is AddPeopleUiEvent.SearchMember -> {
                tribeListDataFlow.update { state ->
                    state.copy(
                        searchMember = event.searchMember
                    )
                }
                searchJob?.cancel()
                searchJob = coroutineScope.launch {
                    delay(500)
                    if (event.searchMember.isEmpty()) {
                        // Reset to show all members when search is cleared
                        addGroupMember(
                            coroutineScope = coroutineScope,
                            groupId = groupId,
                            search = null
                        )
                    } else {
                        // Perform search
                        addGroupMember(
                            coroutineScope = coroutineScope,
                            groupId = groupId,
                            search = event.searchMember
                        )
                    }
                }
            }

        }
    }

    private fun addPeopleApiCall(
        tribeId: String,
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val addMemberRequest = AddMemberRequest(
                tribeId = tribeId,
                members = tribeListDataFlow.value.selectedMembers,
            )
            apiRepository.addMemberData(addMemberRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            message = it.message.toString()
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(
                            context = context,
                            message = it.data?.message.toString()
                        )
                        navigate(NavigationAction.Pop())
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showWaringMessage(
                            context = context,
                            message = it.message.toString()
                        )
                    }
                }
            }
        }
    }


    private fun addRemoveGroupMember(
        coroutineScope: CoroutineScope,
        groupId: String,
        navigate: (NavigationAction) -> Unit,
    ) {
        coroutineScope.launch {
            val groupMemberRequest = GroupMemberRequest(
                type = Constants.GroupMember.ADD_MEMBER.toString(),
                groupId = groupId,
                members = tribeListDataFlow.value.selectedMembers

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

                        coroutineScope.launch {
                            delay(1000) // Adjust delay if needed
                            navigate(
                                NavigationAction.PopWithResult(
                                    resultValues = listOf(
                                        PopResultKeyValue(
                                            Constants.BundleKey.MEMBER_ADDED,
                                            Constants.BundleKey.MEMBER_ADDED
                                        ),

                                        )
                                )
                            )
                        }


                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun getAllPeopleDataList(
        coroutineScope: CoroutineScope,
        tribeId: String
    ) {
        coroutineScope.launch {
            apiRepository.getAllPeopleList(searchName = "", tribeId = tribeId)
                .cachedIn(this)
                .collect { pagingData ->
                    allPeopleListFlow.value = pagingData
                }
        }
    }

    private fun addGroupMember(
        coroutineScope: CoroutineScope,
        groupId: String,
        search: String?
    ) {
        coroutineScope.launch {
            apiRepository.getAddGroupMember(type = 1.toString(), name = search, groupId = groupId)
                .cachedIn(this)
                .collect { pagingData ->
                    allPeopleListFlow.value = pagingData
                }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        tribeListDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}


