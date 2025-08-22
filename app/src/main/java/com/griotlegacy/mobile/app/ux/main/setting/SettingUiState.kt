package com.griotlegacy.mobile.app.ux.main.setting

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SettingUiState(
    //data flow
    val settingUiDataFlow: StateFlow<SettingUiDataState?> = MutableStateFlow(null),
    //event
    val event: (SettingUiEvent) -> Unit = {}
)
data class SettingUiDataState(
    val userProfile: String = "",
    val name: String = "",
    val userEmail: String = "",
    val notification: Boolean = false,
    val publicPrivateProfile: Boolean = false,
    val showDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showLoader: Boolean = false,
    val notificationOnOffFlag: Boolean = false,
    val publicPrivateProfileOnOffFlag: Boolean = false,
    val showData: Boolean = false,
    val showSendInvitationDialog: Boolean = false,


    )
sealed interface SettingUiEvent {
    data class NotificationClick(val notification: Boolean) : SettingUiEvent
    data class LogoutDialog(val show: Boolean) : SettingUiEvent
    data class DeleteDialog(val show: Boolean) : SettingUiEvent
    data object AboutUsClick : SettingUiEvent
    data object PrivacyPolicyClick : SettingUiEvent
    data object ContactUsClick : SettingUiEvent
    data object AdvertisementClick : SettingUiEvent
    data object FaqClick : SettingUiEvent
    data object LegacyReflectionClick : SettingUiEvent
    data object StorageClick : SettingUiEvent
    data object UpdateSettingUiDataFlow : SettingUiEvent
    data object TermAndConditionClick : SettingUiEvent
    data object ChangePasswordClick : SettingUiEvent
    data object FriendsClick : SettingUiEvent
    data object EditProfileClick : SettingUiEvent
    data object GetDataFromPref : SettingUiEvent
    data object LogoutClick : SettingUiEvent
    data object DeleteAccountClick : SettingUiEvent
    data class GetContext(val context: Context): SettingUiEvent
    data class OnShowData(val showData: Boolean) : SettingUiEvent
    data object BlockClick : SettingUiEvent
    data object SmsClick : SettingUiEvent
    data object EmailClick : SettingUiEvent
    data class OnSendInvitationDialog(val invitationDialog: Boolean) : SettingUiEvent

    data class PublicPrivateProfileClick(val publicPrivateProfile: Boolean) : SettingUiEvent
}