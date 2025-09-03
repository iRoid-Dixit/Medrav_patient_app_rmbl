package com.medrevpatient.mobile.app.ux.container.notification
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700

@ExperimentalMaterial3Api
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val blockUiState by uiState.notificationDataFlow.collectAsStateWithLifecycle()
    uiState.event(NotificationUiEvent.GetContext(context))
    AppScaffold(
        containerColor = White,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { uiState.event(NotificationUiEvent.BackClick) },
                titleText = "Notifications",
                isBackVisible = true
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
            }
        )
    ) {
        NotificationScreenContent(uiState)
    }
    if (blockUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationScreenContent(uiState: NotificationUiState) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val notificationUiState by uiState.notificationDataFlow.collectAsStateWithLifecycle()
    val notifications = notificationUiState?.notifications ?: emptyList()
    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(top = 10.dp),
        ) {
            items(notifications) { notification ->
                NotificationCard(notification = notification)
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle notification click */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Image(
                painter = painterResource(id = notification.iconRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        fontFamily = nunito_sans_700,
                        fontSize = 13.sp,
                        color = SteelGray,
                    )
                    Text(
                        text = notification.timestamp,
                        fontFamily = nunito_sans_600,
                        fontSize = 9.sp,
                        color = Gray50

                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.description,
                    fontFamily = nunito_sans_600,
                    fontSize = 10.sp,
                    lineHeight = 16.sp,
                    color = SteelGray.copy(alpha = 0.7f)
                )
            }
        }
    }
}


@Preview
@Composable
fun NotificationScreenContentPreview() {
    val uiState = NotificationUiState()
    NotificationScreenContent(uiState)
}

@Preview
@Composable
fun NotificationCardPreview() {
    val sampleNotification = NotificationItem(
        id = "1",
        title = "Schedule Change",
        description = "Your appointment has been rescheduled by your doctor. New appointment time: 04:00 PM.",
        timestamp = "09:13 AM",
        iconRes = R.drawable.ic_appointment
    )
    NotificationCard(notification = sampleNotification)
}






