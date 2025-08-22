package com.griotlegacy.mobile.app.ux.container.tribeList
import android.content.Context
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.google.gson.Gson
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.domain.validation.ValidationUseCase
import com.griotlegacy.mobile.app.model.domain.response.tribe.MemberResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showWaringMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.utils.socket.OnSocketEventsListener
import com.griotlegacy.mobile.app.ux.container.addPeople.AddPeopleRoute
import com.griotlegacy.mobile.app.ux.container.chat.ChatRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
class GetTribeListUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) : OnSocketEventsListener {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val tribeListDataFlow = MutableStateFlow(TribeListDataState())
    private val tribeMemberListFlow = MutableStateFlow<PagingData<MemberResponse>>(PagingData.empty())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        tribeId: String,
        tribeName: String,
        navigate: (NavigationAction) -> Unit,
    ): TribeListUiState {


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
                tribeName = tribeName
            )
        }
        return TribeListUiState(
            tribeListDataFlow = tribeListDataFlow,
            tribeMemberListFlow = tribeMemberListFlow,
            event = { aboutUsEvent ->
                tribeListUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    tribeId = tribeId
                )
            }
        )
    }

    private fun tribeListUiEvent(
        tribeId: String,
        event: TribeListUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            TribeListUiEvent.BackClick -> {
                navigate(NavigationAction.Pop())
            }

            is TribeListUiEvent.OnAddMemberClick -> {
                navigate(
                    NavigationAction.Navigate(
                        AddPeopleRoute.createRoute(
                            tribeId = tribeId,
                            groupId = "none"
                        )
                    )
                )
            }

            is TribeListUiEvent.OnGetTribeList -> {
                getTribeDataList(coroutineScope, tribeId = tribeId)
            }

            is TribeListUiEvent.GetContext -> {
                this.context = event.context
            }

            TribeListUiEvent.TribeListClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            tribeListDataFlow.value.tribeName,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val messageValidationResult = emptyFieldValidation(
                            tribeListDataFlow.value.tribeName,
                            context.getString(R.string.please_enter_message)
                        )

                        val hasError = listOf(
                            nameValidationResult,
                            messageValidationResult
                        ).any { !it.isSuccess }
                        //  **Update all error messages in one go**
                        tribeListDataFlow.update { state ->
                            state.copy(
                                messageErrorMsg = messageValidationResult.errorMsg,
                            )
                        }
                        if (hasError) return //  Stop if any validation failed
                    }
                } else {
                    showWaringMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is TribeListUiEvent.BlockUser -> {
                userBlockAndRemove(
                    coroutineScope = coroutineScope,
                    tribeId = tribeId,
                    type = Constants.FriendType.BLOCK_USER.toString(),
                    userId = tribeListDataFlow.value.userId
                )

            }

            is TribeListUiEvent.RemoveUser -> {
                userBlockAndRemove(
                    coroutineScope = coroutineScope,
                    tribeId = tribeId,
                    type = Constants.FriendType.REMOVE_USER.toString(),
                    userId = tribeListDataFlow.value.userId
                )

            }

            is TribeListUiEvent.BlockUserDialog -> {
                tribeListDataFlow.update { state ->
                    state.copy(
                        showDialog = event.show,
                        userId = event.userId
                    )
                }
            }

            is TribeListUiEvent.RemoveUserDialog -> {
                tribeListDataFlow.update { state ->
                    state.copy(
                        showRemoveDialog = event.show,
                        userId = event.userId
                    )
                }
            }

            is TribeListUiEvent.NavigateToChatScreen -> {
                val data = Gson().toJson(event.chatData)
                navigate(
                    NavigationAction.Navigate(
                        ChatRoute.createRoute(
                            data,
                            screeName = Constants.AppScreen.TRIBE_INNER_USER_SCREEN
                        )
                    )
                )
            }
        }
    }

    private fun userBlockAndRemove(
        coroutineScope: CoroutineScope,
        tribeId: String,
        type: String,
        userId: String
    ) {
        coroutineScope.launch {
            apiRepository.innerCircleTribeBlockAndLeave(
                tribeId = tribeId,
                type = type,
                userId = userId
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        tribeListDataFlow.value.showDialog = false
                        tribeListDataFlow.value.showRemoveDialog = false
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )
                        val unblockedUserId = it.data?.data?.userId
                        if (unblockedUserId != null) {
                            tribeMemberListFlow.update { pagingData ->
                                pagingData.filter { it1 ->
                                    it1.id != unblockedUserId
                                }
                            }
                        }
                        tribeListDataFlow.value.showDialog = false
                        tribeListDataFlow.value.showRemoveDialog = false
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }
        }

    }
    private fun getTribeDataList(coroutineScope: CoroutineScope,tribeId: String) {
        coroutineScope.launch {
            apiRepository.getTribeMemberList(tribeId = tribeId).cachedIn(this).collect { pagingData ->
                tribeMemberListFlow.value = pagingData
                tribeListDataFlow.update { state ->
                    state.copy(
                        apiStatus = true
                    )
                }
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


