
package com.medrevpatient.mobile.app.ux.main.setting
import CustomSwitch
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.UserData
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.dialog.ConfirmationDialog
import com.medrevpatient.mobile.app.ui.compose.common.dialog.InviteFriendsDialog
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Green09
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.Scorpion
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@ExperimentalMaterial3Api
@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val settingUiState by uiState.settingUiDataFlow.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        uiState.event(SettingUiEvent.GetDataFromPref)
    }

    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            TopBarComponent(
                textAlign = TextAlign.Start,
                header = "Settings",
                )
        },
        navBarData = null
    ) {
        uiState.event(SettingUiEvent.GetContext(context))

        SettingScreenContent(uiState,uiState.event)

    }
    /* val lifecycleOwner = LocalLifecycleOwner.current
     val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
     LaunchedEffect(lifecycleState) {
         when (lifecycleState) {
             Lifecycle.State.RESUMED -> {
                uiState.event(SettingUiEvent.GetDataFromPref)
                // uiState.event(SettingUiEvent.UpdateSettingUiDataFlow)
             }
             else -> {}
         }
     }*/
    if (settingUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun SettingScreenContent(
    uiState: SettingUiState,
    event: (SettingUiEvent) -> Unit,

) {
    val settingUiState by uiState.settingUiDataFlow.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeColor)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Surface(
            shape = RoundedCornerShape(100),
            modifier = Modifier.size(107.dp),
            border = BorderStroke(3.dp, Scorpion)
        ) {
            AsyncImage(
                model = settingUiState?.userProfile ?: "",
                contentDescription = "Profile",
                placeholder = painterResource(id = R.drawable.ic_app_icon),
                error = painterResource(id = R.drawable.ic_app_icon),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize() // Ensure image fills surface
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        // Profile Name & Email
        ProfileText(text = settingUiState?.name ?:"")
        ProfileText(text = settingUiState?.userEmail ?:"")
        // Settings Options
        OptionsContent(items = UserData.settingScreenListingData,settingUiState,event)
        Spacer(modifier = Modifier.height(25.dp))
    }
    if (settingUiState?.showSendInvitationDialog == true) {
        InviteFriendsDialog(
            onDismiss = {
                event(SettingUiEvent.OnSendInvitationDialog(false))
            },
            onSmsClick = {
                event(SettingUiEvent.SmsClick)
            },
            onEmailClick = {
                event(SettingUiEvent.EmailClick)
            }
        )

    }
}

@Composable
private fun ProfileText(text: String) {
    Text(
        text = text,
        fontFamily = WorkSans,
        fontWeight = W400,
        color = White,
        lineHeight = 18.sp,
        fontSize = 14.sp
    )
}

@Composable
private fun OptionsContent(
    items: List<UserData.SettingData>,
    settingUiState: SettingUiDataState?,
    event: (SettingUiEvent) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp),
        horizontalAlignment = Alignment.Start
    ) {
        items.forEachIndexed { index, setting ->
            SettingItemRow(
                settingOptions = setting,
                onClick = { handleSettingClick(setting.title, event) },
                index = index,
                settingUiState,
                event
            )
            // Add divider except for the last item
            if (index < items.size - 1) {
                HorizontalDivider(thickness = 1.dp, color = MineShaft)
            }
        }
    }
    if (settingUiState?.showDialog==true) {
        ConfirmationDialog(
            onDismissRequest = { event(SettingUiEvent.LogoutDialog(false)) },
            title = "Logout",
            description = stringResource(R.string.are_you_sure_you_want_to_logout),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(id = R.string.logout),
            onPositiveClick = {
                event(SettingUiEvent.LogoutClick)
            },
        )
    }
    if (settingUiState?.showDeleteDialog==true) {
        ConfirmationDialog(
            onDismissRequest = { event(SettingUiEvent.DeleteDialog(false)) },
            title = "Delete account",
            description = "Are you sure you want to delete the account? You will lose all records, and you cannot restore it again later.",
            negativeText = stringResource(R.string.no),
            positiveText = stringResource(id = R.string.yes),
            onPositiveClick = {
                event(SettingUiEvent.DeleteAccountClick)
            },
        )
    }

}
@Composable
private fun SettingItemRow(
    settingOptions: UserData.SettingData,
    onClick: () -> Unit,
    index: Int,
    settingUiState: SettingUiDataState?,
    event: (SettingUiEvent) -> Unit,
) {
    val localNotificationState =
        remember { mutableStateOf(settingUiState?.notificationOnOffFlag == true) }
    val localPublicPrivateState =
        remember { mutableStateOf(settingUiState?.publicPrivateProfileOnOffFlag == true) }
    LaunchedEffect(
        settingUiState?.notificationOnOffFlag,
        settingUiState?.publicPrivateProfileOnOffFlag
    ) {
        localNotificationState.value = settingUiState?.notificationOnOffFlag == true
        localPublicPrivateState.value = settingUiState?.publicPrivateProfileOnOffFlag == true
    }

    Log.d("TAG", "SettingItemRow:notification ${settingUiState?.notificationOnOffFlag}")
    Log.d("TAG", "SettingItemRow:profile ${settingUiState?.publicPrivateProfileOnOffFlag}")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp, bottom = 15.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = stringResource(id = settingOptions.title),
            fontSize = 18.sp,
            fontFamily = WorkSans,
            fontWeight = W500,
            color = colorResource(id = settingOptions.colors),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (settingOptions.isArrowVisible) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = null,
            )
        }else{
            if (index == 4 || index == 6) {
                CustomSwitch(
                    isChecked = if (index == 4) localNotificationState.value
                    else localPublicPrivateState.value,
                    height = 24.dp,
                    width = 48.dp,
                    onCheckedChange = {
                        if (index == 4) {
                            val newState = !localNotificationState.value
                            localNotificationState.value = newState
                            event(SettingUiEvent.NotificationClick(newState))
                        } else {
                            val newState = !localPublicPrivateState.value
                            localPublicPrivateState.value = newState
                            event(SettingUiEvent.PublicPrivateProfileClick(newState))
                        }
                    },
                    thumbColor = if (index == 4) {
                        if (localNotificationState.value) White else AppThemeColor
                    } else {
                        if (localPublicPrivateState.value) White else AppThemeColor
                    },
                    checkedTrackColor = Green09,
                    uncheckedTrackColor = White,
                )

            }
        }
    }
}

// Handle Click Actions Dynamically
private fun handleSettingClick(
    titleResId: Int,
    event: (SettingUiEvent) -> Unit,
    ) {

    when (titleResId) {
        R.string.edit_profile -> {
            event(SettingUiEvent.EditProfileClick)
        }

        R.string.change_password -> {
            event(SettingUiEvent.ChangePasswordClick)
            // Handle Friend Click
        }

        R.string.friend -> {
            // Handle Friend Click
            event(SettingUiEvent.FriendsClick)
        }

        R.string.invite_friend -> {
            event(SettingUiEvent.OnSendInvitationDialog(true))
        }

        R.string.notification_on -> {
            // Handle Notifications Click
        }

        R.string.block -> {
            // Handle Block Click
            event(SettingUiEvent.BlockClick)
        }

        R.string.profile_profile_privacy -> {

        }

        R.string.about_us -> {
            event(SettingUiEvent.AboutUsClick)
        }

        R.string.term_condition -> {
            event(SettingUiEvent.TermAndConditionClick)
        }

        R.string.privacy_and_policy -> {
            event(SettingUiEvent.PrivacyPolicyClick)
        }

        R.string.advertisement -> {
            event(SettingUiEvent.AdvertisementClick)
        }

        R.string.contact_us -> {
            event(SettingUiEvent.ContactUsClick)
        }

        R.string.faq -> {
            event(SettingUiEvent.FaqClick)
        }
        R.string.legacy_reflection -> {
            event(SettingUiEvent.LegacyReflectionClick)
        }


        R.string.storage -> {
            event(SettingUiEvent.StorageClick)
        }

        R.string.delete_account -> {
            event(SettingUiEvent.DeleteDialog(true))
        }

        R.string.logout -> {
            event(SettingUiEvent.LogoutDialog(true))
        }


    }

}



@Preview
@Composable
private fun Preview() {
    Surface {
        SettingScreenContent(
            uiState = SettingUiState(),
            event = {},

            )
    }
}