package com.medrevpatient.mobile.app.ux.main.setting

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.model.domain.request.authReq.LogoutReq
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.ux.container.ContainerActivity
import com.medrevpatient.mobile.app.ux.startup.StartupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSettingUiStateUseCase
@Inject constructor(
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val apiRepository: ApiRepository,
) {
    private val settingUiDataFlow = MutableStateFlow(SettingUiDataState())
    private var userData: UserAuthResponse? = null
    private lateinit var context: Context
    private var deepLink: String = ""
    private val inviteMessage = "Join me on Legacy Cache! Download the app here: "
    operator fun invoke(
        context: Context,
        @Suppress("UnusedPrivateProperty")
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SettingUiState {
        coroutineScope.launch {
            deepLink = appPreferenceDataStore.getUserData()?.deepLink ?: ""
        }
        return SettingUiState(
            settingUiDataFlow = settingUiDataFlow,
            event = { profileUiEvent ->
                profileUiEvent(
                    event = profileUiEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }

    private fun updateSettingUiDataFlow(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            userData = appPreferenceDataStore.getUserData()
            userData?.let {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        name = it.name ?: "",
                        userEmail = it.email ?: "",
                        userProfile = it.profileImage ?: "",
                        notificationOnOffFlag = it.isNotificationEnabled == true,
                        publicPrivateProfileOnOffFlag = it.isProfilePrivate == true,
                    )
                }
            }
        }
    }

    private fun profileUiEvent(
        event: SettingUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            is SettingUiEvent.NotificationClick -> {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        notification = event.notification
                    )
                }

                if (event.notification) {
                    callNotificationOfOff(
                        coroutineScope = coroutineScope,
                    )
                } else {
                    // API call for toggling OFF (Offline)

                    callNotificationOfOff(
                        coroutineScope = coroutineScope,

                        )
                }
            }
            SettingUiEvent.AboutUsClick -> {
                callGetTermsAPI(coroutineScope, "1", navigate, screenName = Constants.AppScreen.ABOUT_US)
            }
            SettingUiEvent.PrivacyPolicyClick -> {
                callGetTermsAPI(coroutineScope, "2", navigate, screenName = Constants.AppScreen.PRIVACY_POLICY_SCREEN)
            }

            SettingUiEvent.TermAndConditionClick -> {
                callGetTermsAPI(coroutineScope, "3", navigate, screenName = Constants.AppScreen.TERM_AND_CONDITION_SCREEN)
            }

            SettingUiEvent.ChangePasswordClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.CHANGE_PASSWORD_SCREEN
                )
            }

            SettingUiEvent.FriendsClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.MY_CIRCLE_SCREEN
                )
            }

            SettingUiEvent.ContactUsClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.CONTACT_US_SCREEN
                )
            }

            SettingUiEvent.FaqClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.FQA_SCREEN
                )
            }

            SettingUiEvent.EditProfileClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.EDIT_PROFILE_SCREEN
                )
            }

            SettingUiEvent.BlockClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.BLOCK_LIST_SCREEN
                )
            }

            SettingUiEvent.GetDataFromPref -> {
                coroutineScope.launch {
                    if (appPreferenceDataStore.isProfilePicUpdated()) {
                        appPreferenceDataStore.setIsProfilePicUpdated(false)
                        userData = appPreferenceDataStore.getUserData()
                        userData?.let {
                            settingUiDataFlow.update { profileUiDataState ->
                                profileUiDataState.copy(
                                    name = it.name ?: "",
                                    userEmail = it.email ?: "",
                                    userProfile = it.profileImage ?: "",
                                )
                            }
                        }

                    }
                    userData = appPreferenceDataStore.getUserData()
                    userData?.let {
                        settingUiDataFlow.update { settingUiDataState ->
                            settingUiDataState.copy(
                                name = it.name ?: "",
                                userEmail = it.email ?: "",
                                userProfile = it.profileImage ?: "",
                                notificationOnOffFlag = it.isNotificationEnabled == true,
                                publicPrivateProfileOnOffFlag = it.isProfilePrivate == true,
                            )
                        }
                    }

                }
            }

            SettingUiEvent.LogoutClick -> {
                logout(
                    context = context,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )


            }

            is SettingUiEvent.LogoutDialog -> {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        showDialog = event.show
                    )
                }
            }

            is SettingUiEvent.GetContext -> {
                this.context = event.context
            }

            is SettingUiEvent.DeleteDialog -> {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        showDeleteDialog = event.show
                    )
                }
            }

            SettingUiEvent.DeleteAccountClick -> {
                doDeleteAccount(
                    context = context,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }

            SettingUiEvent.StorageClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.STORAGE_SCREEN
                )
            }

            SettingUiEvent.AdvertisementClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.ADVERTISEMENT_SCREEN
                )
            }

            is SettingUiEvent.PublicPrivateProfileClick -> {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        publicPrivateProfile = event.publicPrivateProfile
                    )
                }
                if (event.publicPrivateProfile) {
                    callPublicPrivateProfileOfOff(
                        coroutineScope = coroutineScope,
                    )
                } else {
                    callPublicPrivateProfileOfOff(
                        coroutineScope = coroutineScope,
                    )
                }
            }

            SettingUiEvent.UpdateSettingUiDataFlow -> {
                updateSettingUiDataFlow(
                    coroutineScope = coroutineScope
                )
            }

            is SettingUiEvent.OnShowData -> {
                settingUiDataFlow.update { settingUiDataState ->
                    settingUiDataState.copy(
                        showData = event.showData
                    )
                }
            }

            is SettingUiEvent.OnSendInvitationDialog -> {
                settingUiDataFlow.update { state ->
                    state.copy(
                        showSendInvitationDialog = event.invitationDialog
                    )
                }
            }

            SettingUiEvent.EmailClick -> {
                Log.d("TAG", "deepLink: $deepLink")
                openEmailClient(this.context, "$inviteMessage$deepLink")
            }

            SettingUiEvent.SmsClick -> {
                openSmsClient(this.context, "$inviteMessage$deepLink")

            }

            SettingUiEvent.LegacyReflectionClick -> {
                navigateToContainerScreens(
                    context = context,
                    navigate = navigate,
                    screenName = Constants.AppScreen.LEGACY_REFLECTION_SCREEN
                )
            }
        }
    }

    // Update the utility functions
    private fun openEmailClient(context: Context, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "Invitation to join Legacy Cache")
                putExtra(Intent.EXTRA_TEXT, message)
            }
            context.startActivity(Intent.createChooser(intent, "Send invitation via email"))
            settingUiDataFlow.update { state ->
                state.copy(showSendInvitationDialog = false)
            }
        } catch (e: ActivityNotFoundException) {
            Log.e("TAG", "No email app installed", e)
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
            settingUiDataFlow.update { state ->
                state.copy(showSendInvitationDialog = false)
            }
        } catch (e: ActivityNotFoundException) {
            Log.e("TAG", "No SMS app installed", e)
            showErrorMessage(context, "No SMS app installed")
        }
    }


    private fun logout(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val appDeviceId = AppUtils.getDeviceId(c = context)
            if (appDeviceId.isNotEmpty()) {
                val logoutReq = LogoutReq(
                    deviceId = appDeviceId
                )
                apiRepository.doLogout(logoutReq).collect {
                    when (it) {
                        is NetworkResult.Error -> {
                            showErrorMessage(
                                context = this@GetSettingUiStateUseCase.context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }

                        is NetworkResult.Loading -> {
                            showOrHideLoader(true)
                        }

                        is NetworkResult.Success -> {
                            showOrHideLoader(false)
                            appPreferenceDataStore.clearAll()
                            showSuccessMessage(
                                context = this@GetSettingUiStateUseCase.context,
                                it.data?.message ?: ""
                            )
                            coroutineScope.launch {
                                delay(1000) // Adjust delay if needed
                                //appPreferenceDataStore.saveStartUpStartDestination(Constants.AppScreen.SIGN_IN)
                                val intent = Intent(context, StartupActivity::class.java)
                                intent.putExtra(Constants.IS_COME_FOR, Constants.AppScreen.SIGN_IN)
                                navigate(
                                    NavigationAction.NavigateIntent(
                                        intent,
                                        finishCurrentActivity = true
                                    ),
                                )
                            }
                        }

                        is NetworkResult.UnAuthenticated -> {
                            showErrorMessage(
                                context = this@GetSettingUiStateUseCase.context,
                                it.message ?: "Something went wrong!"
                            )
                            showOrHideLoader(false)
                        }
                    }
                }
            }
        }
    }

    private fun callNotificationOfOff(
        coroutineScope: CoroutineScope,

        ) {
        coroutineScope.launch {
            apiRepository.notificationOnOff().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, it.message ?: "")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context, it.data?.message ?: "")
                        storeResponseToNotification(
                            data = it.data?.data ?: UserAuthResponse(),
                            coroutineScope = coroutineScope,

                            )
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, it.message.toString())

                    }
                }
            }
        }
    }

    private fun callPublicPrivateProfileOfOff(
        coroutineScope: CoroutineScope,

        ) {
        coroutineScope.launch {
            apiRepository.publicPrivateProfileOnOff().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, it.message ?: "")
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context, it.data?.message ?: "")
                        storeResponseToProfile(
                            data = it.data?.data ?: UserAuthResponse(),
                            coroutineScope = coroutineScope,

                            )

                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, it.message.toString())

                    }
                }
            }
        }
    }

    private fun storeResponseToProfile(
        data: UserAuthResponse,
        coroutineScope: CoroutineScope,

        ) {
        coroutineScope.launch {
            val existingData = appPreferenceDataStore.getUserData()
            val updatedUserData = existingData?.copy(
                isProfilePrivate = data.isProfilePrivate
            )
            appPreferenceDataStore.saveUserData(updatedUserData ?: UserAuthResponse())
        }
    }

    private fun storeResponseToNotification(
        data: UserAuthResponse,
        coroutineScope: CoroutineScope,

        ) {
        coroutineScope.launch {
            val existingData = appPreferenceDataStore.getUserData()
            val updatedUserData = existingData?.copy(
                isNotificationEnabled = data.isNotificationEnabled
            )
            appPreferenceDataStore.saveUserData(updatedUserData ?: UserAuthResponse())
        }
    }

    private fun doDeleteAccount(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {

            apiRepository.deleteAccount().collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        showErrorMessage(
                            context = this@GetSettingUiStateUseCase.context,
                            it.message ?: "Something went wrong!"
                        )
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showSuccessMessage(
                            context = this@GetSettingUiStateUseCase.context,
                            it.data?.message ?: ""
                        )
                        coroutineScope.launch {
                            delay(1000) // Adjust delay if needed
                            appPreferenceDataStore.clearAll()
                            appPreferenceDataStore.saveStartUpStartDestination(Constants.AppScreen.SIGN_IN)
                            val intent = Intent(context, StartupActivity::class.java)
                            navigate(NavigationAction.NavigateIntentWithFinishAffinity(intent))
                        }
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                    }
                }
            }
        }
    }

    private fun navigateToContainerScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
        uri: String = ""
    ) {
        val bundle = Bundle()
        bundle.putString(Constants.BundleKey.URL, uri)
        val intent = Intent(context, ContainerActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        intent.putExtra(Constants.IS_FORM, bundle)
        navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        settingUiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun callGetTermsAPI(coroutineScope: CoroutineScope, type: String, navigate: (NavigationAction) -> Unit, screenName: String) {
        coroutineScope.launch {
            apiRepository.getTermsAndConditions(type).collect { result ->
                when (result) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context, result.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        // showErrorMessage(context, result.data?.message ?:"Something went wrong!")
                        navigateToContainerScreens(
                            context = context,
                            navigate = navigate,
                            screenName = screenName,
                            uri = result.data?.data?.url ?: ""
                        )
                        showOrHideLoader(false)
                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context, result.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

}


