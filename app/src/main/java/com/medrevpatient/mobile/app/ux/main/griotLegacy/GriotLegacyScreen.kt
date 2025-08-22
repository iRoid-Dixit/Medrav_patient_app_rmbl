package com.medrevpatient.mobile.app.ux.main.griotLegacy
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.tabber.GriotLegacyTabSliderBar
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ux.main.griotLegacy.legacy.AllLegacyScreen

@SuppressLint("ContextCastToActivity")
@ExperimentalMaterial3Api
@Composable
fun GriotLegacyScreen(
    navController: NavController,
    viewModel: GriotLegacyViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val activity = LocalContext.current as? Activity
    BackHandler(onBack = { activity?.finishAffinity() })
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            Column(
                modifier = Modifier
                    .background(AppThemeColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .requiredHeight(54.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painterResource(id = R.drawable.ic_app_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            uiState.event(GriotLegacyUiEvent.NavigateToNotification)
                        }
                    )
                }
            }
        },
        navBarData = null
    ) {
        GriotLegacyScreenContent(uiState = uiState, event = uiState.event)
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun GriotLegacyScreenContent(
    uiState: GriotLegacyUiState,
    event: (GriotLegacyUiEvent) -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.all),
        stringResource(R.string.innercircle),
        stringResource(R.string.tribe),
        stringResource(R.string.village),
        stringResource(R.string.justme)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppThemeColor)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        GriotLegacyTabSliderBar(
            tabs = tabs,
            initialTabIndex = currentPage,
            onTabSelected = { page ->
                Log.d("TAG", "Tab selected: $page")
                currentPage = page
                event(GriotLegacyUiEvent.TabClick(page))
            }
        ) {
            // This will only be called once per page change
            AllLegacyScreen(uiState)
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = GriotLegacyUiState()
    Surface {
        GriotLegacyScreenContent(uiState = uiState, event = {})
    }
}