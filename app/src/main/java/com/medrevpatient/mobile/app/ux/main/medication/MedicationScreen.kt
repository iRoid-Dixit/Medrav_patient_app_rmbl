package com.medrevpatient.mobile.app.ux.main.medication
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor

@ExperimentalMaterial3Api
@Composable
fun MedicationScreen(
    navController: NavController = rememberNavController(),
    viewModel: MedicationViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {

        },
        navBarData = null
    ) {
        MedicationScreenContent(uiState = uiState)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationScreenContent(
    uiState: MedicationUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Medication Screen")

    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = MedicationUiState()
    Surface {
        MedicationScreenContent(uiState)
    }
}