package com.griotlegacy.mobile.app.ux.container.storage

import android.content.Context
import android.util.Log
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.model.domain.response.container.storege.StorageResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.navigation.NavigationAction.*
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.ux.container.storageSubscription.StorageSubscriptionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetStorageUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val storageDataFlow = MutableStateFlow(StorageDataState())
    private val userStorageData = MutableStateFlow(StorageResponse())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): StorageUiState {
        userStorage(coroutineScope = coroutineScope)
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return StorageUiState(
            storageDataFlow = storageDataFlow,
            userStorageData = userStorageData,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun userStorage(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.userStorage().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        Log.d("TAG", "userStorage: ${it.message}")
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
                        userStorageData.value = it.data?.data ?: StorageResponse()
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }
    private fun contactUsUiEvent(
        event: StorageUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
        ) {
        when (event) {
            is StorageUiEvent.GetContext -> {
                this.context = event.context
            }
            StorageUiEvent.BackClick -> {
                navigate(PopIntent)
            }
            StorageUiEvent.SubscriptionClick -> {
                navigate(Navigate(StorageSubscriptionRoute.createRoute()))
            }
            is StorageUiEvent.SubscriptionStatus -> {
                if (event.subscriptionStatus == true) {
                    userStorage(coroutineScope = coroutineScope)
                }
            }
        }
    }
    private fun showOrHideLoader(showLoader: Boolean) {
        storageDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }


}


