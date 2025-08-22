package com.griotlegacy.mobile.app.ux.main.message

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.model.domain.response.chat.MessageTabResponse
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.ux.container.ContainerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMessageUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
) {
    private val messageUiDataState = MutableStateFlow(MessageUiDataState())
    private val messageList =
        MutableStateFlow<PagingData<MessageTabResponse>>(PagingData.empty())

    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MessageUiState {
        return MessageUiState(
            messageUiDataState = messageUiDataState,
            messageList = messageList,
            event = {
                chatUiEvent(
                    context = context,
                    event = it,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun chatUiEvent(
        context: Context,
        event: MessageUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            MessageUiEvent.NavigateToAddGroupMember -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screen = Constants.AppScreen.ADD_GROUP_MEMBER
                )
            }
            MessageUiEvent.MessageListAPICall -> {
                getMessage(coroutineScope)
            }
            /* MessageUiEvent.NavigateToChatScreen -> {
                 navigateToContainerScreens(
                     context = context,
                     navigate = navigate,
                     screen = Constants.AppScreen.CHAT_SCREEN
                 )
             }*/
            is MessageUiEvent.NavigateToChatScreen -> {
                Log.d("TAG", "messageTabResponse: ${event.messageTabResponse}")
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screen = Constants.AppScreen.CHAT_SCREEN,
                    messageTabResponse = event.messageTabResponse
                )
            }

            else -> {}
        }
    }
    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screen: String,
        messageTabResponse: MessageTabResponse? = null
    ) {
        val bundle = Bundle()
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screen)
        intent.putExtra(Constants.IS_FORM, bundle)
        bundle.putString(Constants.BundleKey.MESSAGE_RESPONSE, Gson().toJson(messageTabResponse))
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getMessage(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getMessage().cachedIn(this).collect { pagingData ->
                messageList.value = pagingData
                messageUiDataState.update { state ->
                    state.copy(
                        isAPIStatus = true
                    )
                }
            }
        }
    }
}