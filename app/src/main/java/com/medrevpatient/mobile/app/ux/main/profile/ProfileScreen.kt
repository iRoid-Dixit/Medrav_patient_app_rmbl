package com.medrevpatient.mobile.app.ux.main.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.data.source.local.UserData
import com.medrevpatient.mobile.app.data.source.local.UserData.ProfileItem
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.theme.AthensGray
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray60
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.towyservice.mobile.app.ui.common.sheetContent.ConfirmationSheetContent
import com.towyservice.mobile.app.ui.common.sheetContent.ModelSheetLauncher
@ExperimentalMaterial3Api
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val profileUiState by uiState.messageUiDataFlow.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
            TopBarComponent(
                titleText = "Profile Management",
                isBackVisible = true
            )
        },
        navBarData = null
    ) {
        uiState.event(ProfileUiEvent.GetContext(context))
        ProfileScreenContent(uiState, uiState.event, profileUiState)
    }
   /* if (profileUiState?.showLoader == true) {
        CustomLoader()
    }*/
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    event: (ProfileUiEvent) -> Unit,
    profileUiState: ProfileUiDataState?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .background(White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader(profileUiState)
        AccountDetailsSection(event)
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Gray60,
                        fontFamily = nunito_sans_600,
                        fontSize = 12.sp
                    )
                ) {
                    append("Developed by ")
                }
                withStyle(
                    style = SpanStyle(
                        color = AppThemeColor,
                        fontFamily = nunito_sans_600,
                        fontSize = 12.sp
                    )
                ) {
                    append("iRoid Solutions")
                }
            }
        )
        Spacer(modifier = Modifier.height(50.dp))
    }
    ModelSheetLauncher(
        shouldShowSheet = profileUiState?.logoutSheetVisible == true,
        onDismissRequest = {
            event(ProfileUiEvent.LogoutSheetVisibility(false))
        }
    ) { state, scope ->
        ConfirmationSheetContent(
            title = "Logout?",
            des = "Are you sure you want to logout?",
            positiveText = "Yes",
            navigateText = "No",
            onDismissRequest = {
                event(ProfileUiEvent.LogoutSheetVisibility(false))
            },
            onPositiveClick = {
                event(ProfileUiEvent.LogoutAPICall)
            },
            icon = R.drawable.ic_logout,
            isLoading = profileUiState?.isLogoutButtonLoading == true
        )
    }
    ModelSheetLauncher(
        shouldShowSheet = profileUiState?.deleteSheetVisible == true,
        onDismissRequest = {
            event(ProfileUiEvent.DeleteSheetVisibility(false))
        }
    ) { state, scope ->
        ConfirmationSheetContent(
            title = "Delete Account?",
            des = "Are you sure you want to delete your account?",
            positiveText = "Yes",
            navigateText = "No",
            onDismissRequest = {
                event(ProfileUiEvent.DeleteSheetVisibility(false))
            },
            onPositiveClick = {
                event(ProfileUiEvent.DeleteAPICall)
            },
            icon = R.drawable.ic_delete,
            isLoading = profileUiState?.isDeleteButtonLoading == true
        )
    }
}
@Composable
private fun ProfileHeader(profileUiState: ProfileUiDataState?) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(modifier = Modifier.height(30.dp))
        AsyncImage(
            model = profileUiState?.userProfile,
            placeholder = painterResource(id = R.drawable.ic_place_holder),
            error = painterResource(id = R.drawable.ic_place_holder),
            contentDescription = stringResource(R.string.profile_picture),
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = profileUiState?.userName?:"",
            color = SteelGray,
            fontSize = 20.sp,
            fontFamily = nunito_sans_600
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
           // text = "Email ID: you123@gmail.com",
            text = "Email ID: ${profileUiState?.userEmail?:""}",
            color = Gray40,
            fontSize = 12.sp,
            fontFamily = nunito_sans_400
        )
    }
}
@Composable
private fun AccountDetailsSection(event: (ProfileUiEvent) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier
            .fillMaxWidth()

    ) {
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(id = R.string.account_details),
            color = SteelGray,
            fontSize = 18.sp,
            fontFamily = nunito_sans_600,
        )

        UserData.profileItems.forEachIndexed { index, profileItem ->
            ProfileItemRow(
                profileItem = profileItem,
                onClick = { handleProfileItemClick(profileItem.title, event) },
                index = index
            )
        }
    }
}

@Composable
private fun ProfileItemRow(
    profileItem: ProfileItem,
    onClick: () -> Unit,
    index: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = AthensGray, shape = RoundedCornerShape(12.dp))
            .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 3.dp)
            .noRippleClickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = profileItem.icon),
            contentDescription = null,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(id = profileItem.title),
            fontSize = 16.sp,
            fontFamily = nunito_sans_600,
            color = SteelGray,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (profileItem.isArrowVisible) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.padding(end = 7.dp)
            )
        }
    }
}

private fun handleProfileItemClick(titleResId: Int, event: (ProfileUiEvent) -> Unit) {
    when (titleResId) {
        R.string.edit_profile -> event(ProfileUiEvent.EditProfile)
        R.string.change_password -> event(ProfileUiEvent.ChangePassword)
        R.string.customer_service -> event(ProfileUiEvent.CustomerService)
        R.string.delete_account -> event(ProfileUiEvent.DeleteAccount)
        R.string.logout -> event(ProfileUiEvent.Logout)
    }
}

@Preview
@Composable
private fun Preview() {
    val profileUiState = ProfileUiDataState()

    Surface {
        ProfileScreenContent(
            uiState = ProfileUiState(),
            event = {},
            profileUiState = profileUiState
        )
    }
}