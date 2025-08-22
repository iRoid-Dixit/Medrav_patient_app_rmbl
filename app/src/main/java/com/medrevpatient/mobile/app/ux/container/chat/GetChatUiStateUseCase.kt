package com.medrevpatient.mobile.app.ux.container.chat

import android.content.Context
import android.util.Log
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.response.chat.ChatResponse
import com.medrevpatient.mobile.app.model.domain.response.chat.MessageTabResponse
import com.medrevpatient.mobile.app.model.domain.response.chat.NewMessageResponse
import com.medrevpatient.mobile.app.model.domain.response.tribe.MemberResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.medrevpatient.mobile.app.utils.socket.OnSocketEventsListener
import com.medrevpatient.mobile.app.utils.socket.SocketClass
import com.medrevpatient.mobile.app.utils.socket.SocketClass.loggerE
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleRoute
import com.medrevpatient.mobile.app.ux.container.groupMember.GroupMemberRoute
import io.socket.client.Ack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class GetChatUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) : OnSocketEventsListener {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val chatDataFlow = MutableStateFlow(ChatDataState())
    private var accessToken: String = ""
    private var userProfile: String = ""
    private var senderId: String = ""
    private var roomId: String = ""
    private var currentPage = 1
    private val chatDataList = MutableStateFlow<List<ChatResponse>>(emptyList())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        chatData: String,
        screen: String,
        navigate: (NavigationAction) -> Unit,
    ): ChatUiState {

        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        accessToken = getAccessToken()
        senderId = getSenderId()
        userProfile = getUserProfile()
        chatDataFlow.update {
            val data: MemberResponse? =
                Gson().fromJson(chatData, MemberResponse::class.java)
            if (data != null) {
                it.copy(
                    chatData = data
                )
            } else {
                it // Return the unchanged state if parsing fails
            }
        }
        chatDataFlow.update { state ->
            state.copy(
                userId = senderId,
                screen = screen

            )
        }
        Log.d("TAG", "accessToken: $screen,$chatData")
        return ChatUiState(
            chatDataFlow = chatDataFlow,
            chatDataList = chatDataList,
            event = { aboutUsEvent ->
                blockListUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    screen = screen

                )
            }
        )
    }
    private fun blockListUiEvent(
        event: ChatUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        screen: String
    ) {
        when (event) {
            ChatUiEvent.BackClick -> {
                if (chatDataFlow.value.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) {
                    navigate(NavigationAction.Pop())
                } else {
                    navigate(NavigationAction.PopIntent)

                }
                SocketClass.getSocket(accessToken)
                    ?.emit(Constants.Socket.USER_BACK_TO_HOME, roomDisconnect())
            }

            is ChatUiEvent.GetContext -> {
                this.context = event.context
                if (screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) {
                    initSocketListener(event.context)
                }
            }
            is ChatUiEvent.MessageResponse -> {
                chatDataFlow.update {
                    val data: MessageTabResponse? =
                        Gson().fromJson(event.messageResponse, MessageTabResponse::class.java)
                    if (data != null) {
                        it.copy(
                            messageResponse = data
                        )
                    } else {
                        it // Return the unchanged state if parsing fails
                    }
                }
                initSocket(coroutineScope = coroutineScope)
                callSingleGroupChatListAPI(coroutineScope = coroutineScope, screen = screen)
            }

            is ChatUiEvent.SendMessage -> {
                chatDataFlow.update {
                    it.copy(
                        sendText = event.sendMessage
                    )
                }
            }
            ChatUiEvent.OnMessageSendButtonClick -> {
                if (chatDataFlow.value.sendText.trim().isNotEmpty()) {
                    sendMessageEmitFire(coroutineScope = coroutineScope)

                }
            }
            is ChatUiEvent.InitSocketListener -> {
                initSocketListener(event.context)
            }
            ChatUiEvent.OnLoadNextPage -> {
                callSingleGroupChatListAPI(coroutineScope = coroutineScope, screen = screen)
            }
            ChatUiEvent.GroupDetailsUpdate -> {
                val data = Gson().toJson(chatDataFlow.value.messageResponse)
                navigate(
                    NavigationAction.Navigate(
                        CreateTribeOrInnerCircleRoute.createRoute(
                            screen = Constants.AppScreen.CHAT_SCREEN,
                            messageData = data
                        )
                    )
                )
            }
            ChatUiEvent.AddNewMember -> {
                navigate(
                    NavigationAction.Navigate(
                        GroupMemberRoute.createRoute(
                            chatDataFlow.value.messageResponse?.groupId ?: ""
                        )
                    )
                )
            }
        }
    }
    private fun roomDisconnect(): JSONObject {
        val userObject = JSONObject()
        userObject.put(Constants.Socket.ROOM_ID, roomId)
        userObject.put(Constants.Socket.GROUP_ID, chatDataFlow.value.messageResponse?.groupId)
        userObject.put(Constants.Socket.SENDER_ID, senderId)
        Log.e("Socket roomDisconnect ", userObject.toString())
        return userObject
    }
    override fun onRoomConnected(roomID: String) {
        loggerE("Connected: roomId:onRoom $roomID")
        this.roomId = roomID
    }

    private fun initSocket(coroutineScope: CoroutineScope) {
        SocketClass.getSocket(accessToken)?.let { socket ->
            if (!socket.connected()) {
                SocketClass.connectSocket(accessToken)
            }
            socket.emit(Constants.Socket.CREATE_ROOM,
                createRoom(),
                Ack { data ->
                    Log.d("socketUpdateApi", data.toString())
                    Log.d("socketUpdateApi", data.first().toString())
                    val connectUser =
                        Gson().fromJson(data.first().toString(), SocketCallback::class.java)
                    coroutineScope.launch {
                        when (connectUser.status) {
                            200 -> {
                            }
                            412 -> showErrorMessage(context, connectUser.message)
                            404 -> showErrorMessage(context, connectUser.message)
                            500 -> showErrorMessage(context, connectUser.message)
                        }
                    }
                })
        }
    }
    private fun callSingleGroupChatListAPI(
        coroutineScope: CoroutineScope,
        screen: String,

        ) {
        //  isLoader.value = page != 1

        coroutineScope.launch {
            apiRepository.getChatMessage(
                type = (if (screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) 1 else chatDataFlow.value.messageResponse?.type.toString()).toString(),
                receiverId = if (chatDataFlow.value.messageResponse?.type == 1) chatDataFlow.value.messageResponse?.receiverId
                    ?: "" else if (chatDataFlow.value.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) chatDataFlow.value.chatData?.id
                    ?: "" else null,
                groupId = if (chatDataFlow.value.messageResponse?.type == 2) chatDataFlow.value.messageResponse?.groupId
                    ?: "" else null,
                page = currentPage

            ).collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        isShowLoader(false)
                        showErrorMessage(context, result.message.toString())

                    }
                    is NetworkResult.Loading -> {
                        if (currentPage == 1) showOrHideLoader(true) else isShowLoader(true)

                    }
                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        isShowLoader(false)
                        val newItems = result.data?.data ?: emptyList()
                        chatDataList.value += newItems
                        chatDataFlow.update { state ->
                            state.copy(
                                isApiStatus = true
                            )
                        }
                    }
                    is NetworkResult.UnAuthenticated -> {
                        showErrorMessage(context, result.message.toString())
                        showOrHideLoader(false)

                    }
                }
            }
            currentPage++
        }
    }
    private fun createRoom(): JSONObject {
        val senderReceiverKey =
            if (chatDataFlow.value.messageResponse?.type == 1) Constants.Socket.RECEIVER_ID else if (chatDataFlow.value.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) Constants.Socket.RECEIVER_ID else Constants.Socket.GROUP_ID
        val senderReceiverId =
            if (chatDataFlow.value.messageResponse?.type == 1) chatDataFlow.value.messageResponse?.receiverId else if (chatDataFlow.value.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) chatDataFlow.value.chatData?.id
                ?: "" else chatDataFlow.value.messageResponse?.groupId
        val userObject = JSONObject()
        userObject.put(Constants.Socket.SENDER_ID, senderId)
        userObject.put(senderReceiverKey, senderReceiverId)
        Log.e("Socket createRoom ", userObject.toString())
        return userObject
    }
    data class SocketCallback(
        val success: Boolean,
        val status: Int,
        val message: String
    )
    private fun sendMessageEmitFire(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            SocketClass.getSocket(accessToken)?.let { it1 ->
                if (!it1.connected()) {
                    SocketClass.connectSocket(accessToken)
                }
                it1.emit(Constants.Socket.SEND_MESSAGE, sendMessageEmit())
                chatDataList.value = listOf(
                    ChatResponse(
                        senderId = senderId,
                        profileImage = userProfile,
                        message = chatDataFlow.value.sendText,
                        createdAt = System.currentTimeMillis()
                    )
                ) + chatDataList.value
                chatDataFlow.value.sendText = ""
            }
        }
    }
    private fun initSocketListener(context: Context) {
        (context.requireActivity() as ContainerActivity).initSocketListener(this)
    }
    override fun onNewMessageReceived(data: JSONObject) {
        loggerE("message response: $data")
        try {
            val gson = Gson()
            data.let {
                val roomConnectedObject =
                    gson.fromJson(data.toString(), NewMessageResponse::class.java)
                val currentType = chatDataFlow.value.messageResponse?.type
                val currentReceiverId = chatDataFlow.value.messageResponse?.receiverId
                val currentGroupId = chatDataFlow.value.messageResponse?.groupId

                // Validate message belongs to the current chat
                val isValidMessage = when (currentType) {
                    1 -> roomConnectedObject.senderId == currentReceiverId // Single chat
                    2 -> roomConnectedObject.groupId == currentGroupId     // Group chat
                    else -> false
                }
                if (isValidMessage) {
                    chatDataList.value = listOf(
                        ChatResponse(
                            senderId = roomConnectedObject.senderId,
                            message = roomConnectedObject.message,
                            profileImage = roomConnectedObject.profileImage,
                            createdAt = System.currentTimeMillis()
                        )
                    ) + chatDataList.value
                }
            }
        } catch (e: JSONException) {
            loggerE("JSONException: ${e.message}")
        } catch (e: Exception) {
            loggerE("chatException: ${e.message}")
        }
    }
    private fun sendMessageEmit(): JSONObject {
        val msgObject = JSONObject()
        msgObject.put(Constants.Socket.ROOM_ID, roomId)
        msgObject.put(Constants.Socket.SENDER_ID, senderId)
        if (chatDataFlow.value.messageResponse?.type == Constants.MessageType.SINGLE_CHAT) {
            msgObject.put(
                Constants.Socket.RECEIVER_ID,
                chatDataFlow.value.messageResponse?.receiverId
            )
        } else {
            if (chatDataFlow.value.screen == Constants.AppScreen.TRIBE_INNER_USER_SCREEN) {
                msgObject.put(
                    Constants.Socket.RECEIVER_ID,
                    chatDataFlow.value.chatData?.id
                )
            } else {
                msgObject.put(
                    Constants.Socket.GROUP_ID,
                    chatDataFlow.value.messageResponse?.groupId
                )

            }
        }
        msgObject.put(Constants.Socket.MESSAGE, chatDataFlow.value.sendText)

        Log.e("TAG", "sendMessageEmit: $msgObject")
        return msgObject
    }

    private fun getAccessToken(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore.getUserAuthData()?.accessToken ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("exception${e.message}")
        }
        return accessToken
    }

    private fun getSenderId(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore.getUserData()?.id ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("exception${e.message}")

        }
        return accessToken
    }

    private fun getUserProfile(): String {
        try {
            runBlocking {
                val token = appPreferenceDataStore.getUserData()?.profileImage ?: ""
                accessToken = token
            }
        } catch (e: Exception) {
            Logger.e("exception${e.message}")
        }
        return accessToken
    }


    private fun showOrHideLoader(showLoader: Boolean) {
        chatDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun isShowLoader(showLoader: Boolean) {
        chatDataFlow.update { state ->
            state.copy(
                isLoading = showLoader
            )
        }
    }


}


