package com.medrevpatient.mobile.app.ux.main.message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.White
@ExperimentalMaterial3Api
@Composable
fun MessageScreen(
    navController: NavController,
    viewModel: MessageViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val settingUiState by uiState.messageUiDataFlow.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        topAppBar = {
        },
        navBarData = null
    ) {
        MessageScreenContent(uiState, uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun MessageScreenContent(
    uiState: MessageUiState,
    event: (MessageUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "Message Screen")

    }
}


@Preview
@Composable
private fun Preview() {
    Surface {
        MessageScreenContent(
            uiState = MessageUiState(),
            event = {},

            )
    }
}