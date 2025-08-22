package com.medrevpatient.mobile.app.ux.container.userProfile

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import co.touchlab.kermit.Logger
import com.google.gson.Gson
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.addMember.AddMemberRequest
import com.medrevpatient.mobile.app.model.domain.response.container.friendInfo.FriendInfoResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.imageDisplay.ImageDisplayActivity
import com.medrevpatient.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetFriendProfileUiStateUseCase
@Inject constructor(
    private val apiRepository: ApiRepository,
    private val networkMonitor: NetworkMonitor,
    private val appPreferenceDataStore: AppPreferenceDataStore,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val userProfileDataFlow = MutableStateFlow(FriendProfileDataState())
    private var deepLink: String = ""
    private val inviteMessage = "Join me on Legacy Cache! Download the app here: "
    private var currentPage = 1
    private val friendProfileData = MutableStateFlow(FriendInfoResponse())
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): FriendProfileUiState {
        coroutineScope.launch {
            deepLink = appPreferenceDataStore.getUserData()?.deepLink ?: ""
        }
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return FriendProfileUiState(
            friendProfileDataFlow = userProfileDataFlow,
            friendInfoList = friendProfileData,
            event = { aboutUsEvent ->
                friendProfileUiEvent(
                    event = aboutUsEvent,
                    navigate = navigate,
                    coroutineScope

                    )
            }
        )
    }

    private fun friendProfileUiEvent(
        event: FriendProfileUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,

        ) {
        when (event) {
            FriendProfileUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is FriendProfileUiEvent.GetContext -> {
                this.context = event.context

            }

            is FriendProfileUiEvent.ImageDisplay -> {
                /*val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IMAGE_URI, event.image)
                navigate(NavigationAction.NavigateIntent(intent = intent))*/
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigationAction.NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            is FriendProfileUiEvent.VideoPreviewClick -> {
                /* val intent = Intent(context, ImageVideoPlayerActivity::class.java).apply {
                     putExtra(Constants.Values.VIDEO_LINK, event.videoLink)
                     addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                 }
                 context.startActivity(intent)*/
                val bundle = Bundle()
                val intent = Intent(context, ImageDisplayActivity::class.java)
                intent.putExtra(Constants.IS_FORM, bundle)
                bundle.putString(Constants.BundleKey.MEDIA_LIST, Gson().toJson(event.mediaList))
                navigate(
                    NavigationAction.NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = false
                    )
                )
            }

            is FriendProfileUiEvent.GetUserId -> {
                userProfileDataFlow.update { state ->
                    state.copy(
                        userId = event.userId
                    )
                }
                getFriendInformation(coroutineScope = coroutineScope, userId = event.userId)

            }

            FriendProfileUiEvent.OnPostNextPage -> {
                getFriendInformation(
                    coroutineScope = coroutineScope,
                    userId = userProfileDataFlow.value.userId
                )
            }
            FriendProfileUiEvent.AddInnerCircle -> {
                addInnerCircleAPICall(
                    coroutineScope = coroutineScope,
                    innerCircleId = friendProfileData.value.innerCircleId ?: ""
                )
            }
            is FriendProfileUiEvent.BlockUserDialog -> {
                userProfileDataFlow.update { state ->
                    state.copy(
                        showDialog = event.show
                    )
                }
            }

            FriendProfileUiEvent.BlockUser -> {
                userBlockAndRemove(
                    coroutineScope = coroutineScope,
                    userId = userProfileDataFlow.value.userId,
                    navigate
                )
            }

            FriendProfileUiEvent.EmailClick -> {

                openEmailClient(this.context, "$inviteMessage$deepLink")
            }

            is FriendProfileUiEvent.OnSendInvitationDialog -> {
                userProfileDataFlow.update { state ->
                    state.copy(
                        showSendInvitationDialog = event.invitationDialog
                    )

                }
            }

            FriendProfileUiEvent.SmsClick -> {
                openSmsClient(this.context, "$inviteMessage$deepLink")

            }
        }
    }

    private fun openEmailClient(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "Invitation to join Legacy Cache")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(intent, "Send invitation via email"))
            userProfileDataFlow.update { state ->
                state.copy(showSendInvitationDialog = false)
            }
        } catch (e: ActivityNotFoundException) {
            Logger.e("Error opening email client: ${e.message}")
            showErrorMessage(context, "No email app installed")
        }
    }

    private fun openSmsClient(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:")
                putExtra("sms_body", message)
            }
            context.startActivity(Intent.createChooser(intent, "Send invitation via SMS"))
            userProfileDataFlow.update { state ->
                state.copy(showSendInvitationDialog = false)
            }
        } catch (e: ActivityNotFoundException) {
            Logger.e("No SMS app installed: ${e.message}")
            showErrorMessage(context, "No SMS app installed")
        }
    }

    private fun userBlockAndRemove(
        coroutineScope: CoroutineScope,
        userId: String,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.innerCircleTribeBlockAndLeave(
                tribeId = null,
                type = 1.toString(),
                userId = userId
            ).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        userProfileDataFlow.value.showDialog = false

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
                        userProfileDataFlow.value.showDialog = false
                        navigateToMainScreens(
                            context = context,
                            navigate = navigate,

                            )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        userProfileDataFlow.value.showDialog = false
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                    }
                }
            }
        }

    }

    private fun navigateToMainScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,

        ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.SEARCH_SCREEN)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getFriendInformation(
        coroutineScope: CoroutineScope,
        userId: String,

        ) {
        coroutineScope.launch {
            apiRepository.getFriendProfile(
                userId = userId,
                page = currentPage
            ).collect {

                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                        showIsLoading(false)

                    }

                    is NetworkResult.Loading -> {
                        // if (currentPage == 1) showLoader.value = true else isLoader.value = true
                        if (currentPage == 1) showOrHideLoader(true) else showIsLoading(true)
                    }

                    is NetworkResult.Success -> {
                        if (it.data?.data?.posts?.isEmpty() == true) {
                            userProfileDataFlow.update { state ->
                                state.copy(
                                    noDataFound = true
                                )
                            }
                        }
                        friendProfileData.value = it.data?.data ?: FriendInfoResponse()
                        showOrHideLoader(false)
                        showIsLoading(false)
                        userProfileDataFlow.update { state ->
                            state.copy(
                                isAPISuccess = true
                            )
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showErrorMessage(
                            context = context,
                            it.message ?: "Something went wrong!"
                        )
                        showOrHideLoader(false)
                        showIsLoading(false)
                    }
                }
            }
            currentPage++
        }
    }

    private fun addInnerCircleAPICall(
        coroutineScope: CoroutineScope,
        innerCircleId: String
    ) {
        coroutineScope.launch {
            val addMemberRequest = AddMemberRequest(
                tribeId = innerCircleId,
                members = listOf(userProfileDataFlow.value.userId),
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
                        friendProfileData.value.isMemberOfInnerCircle = true

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

    private fun showOrHideLoader(showLoader: Boolean) {
        userProfileDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
    private fun showIsLoading(isLoading: Boolean) {
        userProfileDataFlow.update { state ->
            state.copy(
                isLoading = isLoading
            )
        }
    }

}


