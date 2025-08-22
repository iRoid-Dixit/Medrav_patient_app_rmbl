package com.griotlegacy.mobile.app.ux.container.block

import android.content.Context
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.domain.validation.ValidationUseCase
import com.griotlegacy.mobile.app.model.domain.response.block.BlockUserResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showWaringMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.ux.container.addPeople.AddPeopleRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBlockListUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val blockListDataFlow = MutableStateFlow(BlockListDataState())
    private val blockListListFlow =
        MutableStateFlow<PagingData<BlockUserResponse>>(PagingData.empty())

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        tribeId: String,
        tribeName: String,
        navigate: (NavigationAction) -> Unit,
    ): BlockListUiState {
        getBlockDataList(coroutineScope)
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        blockListDataFlow.update { state ->
            state.copy(
                tribeName = tribeName
            )
        }
        return BlockListUiState(
            blockListDataFlow = blockListDataFlow,
            blockListFlow = blockListListFlow,
            event = { aboutUsEvent ->
                blockListUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    tribeId = tribeId
                )
            }
        )
    }
    private fun blockListUiEvent(
        tribeId: String,
        event: BlockListUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            BlockListUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is BlockListUiEvent.OnAddMemberClick -> {
                navigate(
                    NavigationAction.Navigate(
                        AddPeopleRoute.createRoute(
                            tribeId = tribeId,
                            groupId = ""
                        )
                    )
                )
            }

            is BlockListUiEvent.GetContext -> {
                this.context = event.context
            }

            BlockListUiEvent.BlockListClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            blockListDataFlow.value.tribeName,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val messageValidationResult = emptyFieldValidation(
                            blockListDataFlow.value.tribeName,
                            context.getString(R.string.please_enter_message)
                        )

                        val hasError = listOf(
                            nameValidationResult,
                            messageValidationResult
                        ).any { !it.isSuccess }
                        //  **Update all error messages in one go**
                        blockListDataFlow.update { state ->
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

            is BlockListUiEvent.UnblockUserDialog -> {
                blockListDataFlow.update { state ->
                    state.copy(
                        showUnblockDialog = event.show,
                        userId = event.userId
                    )
                }

            }

            BlockListUiEvent.UnblockUser -> {
                unBlockUser(coroutineScope = coroutineScope)

            }
        }
    }

    private fun getBlockDataList(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getBlockList(type = Constants.FriendType.BLOCK_USER.toString())
                .cachedIn(this).collect { pagingData ->
                blockListListFlow.value = pagingData

            }
        }
    }

    private fun unBlockUser(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.innerCircleTribeBlockAndLeave(
                tribeId = "",
                type = Constants.FriendType.BLOCK_USER.toString(),
                userId = blockListDataFlow.value.userId
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val unblockedUserId = it.data?.data?.userId
                        if (unblockedUserId != null) {
                            blockListListFlow.update { pagingData ->
                                pagingData.filter { it1 ->
                                    it1.id != unblockedUserId
                                }
                            }
                        }
                        showSuccessMessage(
                            context = context,
                            it.data?.message ?: "Something went wrong!"
                        )
                        blockListDataFlow.value.showUnblockDialog = false

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
    private fun showOrHideLoader(showLoader: Boolean) {
        blockListDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

}


