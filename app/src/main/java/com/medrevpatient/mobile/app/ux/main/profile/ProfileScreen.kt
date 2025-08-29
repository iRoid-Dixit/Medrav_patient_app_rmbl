package com.medrevpatient.mobile.app.ux.main.profile
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavBarNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.Gray94
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.annotation.DrawableRes
import com.medrevpatient.mobile.app.navigation.HandleNavigation

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
        },
        navBarData = null
    ) {
        ProfileScreenContent(uiState, uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ProfileScreenContent(
    uiState: ProfileUiState,
    event: (ProfileUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Header
        ProfileHeader()
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Account Details Section
        AccountDetailsSection(event)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Developer Credit
        DeveloperCredit()
        
        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom navigation
    }
}

@Composable
private fun ProfileHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = AppThemeColor,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
    ) {
        // Back Button
        Icon(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "Back",
            tint = White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 16.dp)
                .size(24.dp)
        )
        
        // Title
        Text(
            text = stringResource(id = R.string.profile_management),
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = nunito_sans_700,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
        
        // Profile Picture
        Image(
            painter = painterResource(id = R.drawable.ic_profile_placeholder),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .clip(CircleShape)
        )
        
        // Profile Info
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sarah Johnson",
                color = Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = nunito_sans_700
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Patient ID: MR-78542",
                color = Gray94,
                fontSize = 14.sp,
                fontFamily = nunito_sans_600
            )
        }
    }
}

@Composable
private fun AccountDetailsSection(event: (ProfileUiEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.account_details),
            color = Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = nunito_sans_700,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        val profileItems = listOf(
            ProfileItem(
                icon = R.drawable.ic_edit_profile,
                title = R.string.edit_profile,
                isArrowVisible = true
            ),
            ProfileItem(
                icon = R.drawable.ic_lock,
                title = R.string.change_password,
                isArrowVisible = true
            ),
            ProfileItem(
                icon = R.drawable.ic_delete,
                title = R.string.delete_account,
                isArrowVisible = true
            ),
            ProfileItem(
                icon = R.drawable.ic_logout,
                title = R.string.logout,
                isArrowVisible = true
            )
        )
        
        profileItems.forEachIndexed { index, profileItem ->
            ProfileItemRow(
                profileItem = profileItem,
                onClick = { handleProfileItemClick(profileItem.title, event) },
                index = index
            )
            // Add divider except for the last item
            if (index < profileItems.size - 1) {
                HorizontalDivider(thickness = 1.dp, color = MineShaft)
            }
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
            .padding(top = 25.dp, bottom = 15.dp)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = profileItem.icon),
            contentDescription = stringResource(id = profileItem.title),
            tint = AppThemeColor,
            modifier = Modifier
                .size(24.dp)
                .padding(start = 10.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = stringResource(id = profileItem.title),
            fontSize = 18.sp,
            fontFamily = WorkSans,
            fontWeight = W500,
            color = Black,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        if (profileItem.isArrowVisible) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}

@Composable
private fun HorizontalDivider(
    thickness: androidx.compose.ui.unit.Dp,
    color: androidx.compose.ui.graphics.Color
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Composable
private fun DeveloperCredit() {
    Text(
        text = "Developed by ",
        color = Black,
        fontSize = 14.sp,
        fontFamily = nunito_sans_600
    )
}

private fun handleProfileItemClick(titleResId: Int, event: (ProfileUiEvent) -> Unit) {
    when (titleResId) {
        R.string.edit_profile -> event(ProfileUiEvent.EditProfile)
        R.string.change_password -> event(ProfileUiEvent.ChangePassword)
        R.string.delete_account -> event(ProfileUiEvent.DeleteAccount)
        R.string.logout -> event(ProfileUiEvent.Logout)
    }
}

data class ProfileItem(
    @DrawableRes val icon: Int,
    val title: Int,
    val isArrowVisible: Boolean = true
)

@Preview
@Composable
private fun Preview() {
    Surface {
        ProfileScreenContent(
            uiState = ProfileUiState(),
            event = {}
        )
    }
}