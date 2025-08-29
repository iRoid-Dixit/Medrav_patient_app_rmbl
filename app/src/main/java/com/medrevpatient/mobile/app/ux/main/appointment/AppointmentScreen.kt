package com.medrevpatient.mobile.app.ux.main.appointment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable

@ExperimentalMaterial3Api
@Composable
fun AppointmentScreen(
    navController: NavController,
    viewModel: AppointmentViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = White,
        topAppBar = {



        },
        navBarData = null
    ) {
        AppointmentScreenContent(uiState = uiState, event = uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun AppointmentScreenContent(uiState: AppointmentsUiState, event: (AppointmentsUiEvent) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Appointment Screen")

    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = AppointmentsUiState()
    Surface {
        AppointmentScreenContent(uiState = uiState, event = {})
    }
}