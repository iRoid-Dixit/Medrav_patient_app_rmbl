package com.griotlegacy.mobile.app.ux.container.notification
import android.content.Context
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.model.domain.response.notification.NotificationResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetNotificationUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val blockListDataFlow = MutableStateFlow(NotificationDataState())

    private val notificationList =
        MutableStateFlow<PagingData<NotificationResponse>>(PagingData.empty())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): NotificationUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        getNotification(coroutineScope = coroutineScope)
        return NotificationUiState(
            notificationDataFlow = blockListDataFlow,
            notificationList = notificationList,
            event = { aboutUsEvent ->
                blockListUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                )
            }
        )
    }

    private fun blockListUiEvent(
        event: NotificationUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            NotificationUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is NotificationUiEvent.GetContext -> {
                this.context = event.context
            }

            is NotificationUiEvent.RejectNotificationClick -> {
                acceptRejectNotificationAPICall(
                    coroutineScope = coroutineScope,
                    tribeId = event.tribeId,
                    type = Constants.NotificationType.REJECT_NOTIFICATION.toString()
                )
            }

            is NotificationUiEvent.AcceptNotificationClick -> {
                acceptRejectNotificationAPICall(
                    coroutineScope = coroutineScope,
                    tribeId = event.tribeId,
                    type = Constants.NotificationType.ACCEPT_NOTIFICATION.toString()
                )
            }
        }
    }

    private fun getNotification(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getNotification().cachedIn(this).collect { pagingData ->
                notificationList.value = pagingData
            }
        }
    }

    private fun acceptRejectNotificationAPICall(
        coroutineScope: CoroutineScope,
        tribeId: String,
        type: String

    ) {
        coroutineScope.launch {
            apiRepository.acceptRejectNotification(type = type, tribeId = tribeId)
                .collect {
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
                            showSuccessMessage(context = context, it.data?.message ?: "")
                            val unblockedUserId = it.data?.data?.message
                            if (unblockedUserId != null) {
                                notificationList.update { pagingData ->
                                    pagingData.filter { it1 ->
                                        it1.id != unblockedUserId
                                    }
                                }
                            }

                        }

                        is NetworkResult.UnAuthenticated -> {
                            showOrHideLoader(false)
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


