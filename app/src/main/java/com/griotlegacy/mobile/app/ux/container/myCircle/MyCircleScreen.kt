package com.griotlegacy.mobile.app.ux.container.myCircle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.navigation.HandleNavigation
import com.griotlegacy.mobile.app.navigation.scaffold.AppScaffold
import com.griotlegacy.mobile.app.ui.compose.common.TabSliderBar
import com.griotlegacy.mobile.app.ui.compose.common.TopBarComponent
import com.griotlegacy.mobile.app.ui.compose.common.loader.CustomLoader
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ux.container.myCircle.circleType.InnerCircleScreen
import com.griotlegacy.mobile.app.ux.container.myCircle.circleType.TribeScreen

@ExperimentalMaterial3Api
@Composable
fun MyCircleScreen(
    navController: NavController,
    viewModel: MyCircleViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.myCircleDataFlow.collectAsStateWithLifecycle()
    uiState.event(MyCircleUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.my_circle),
                isBackVisible = true,
                onClick = {
                    uiState.event(MyCircleUiEvent.BackClick)
                },
            )
        },
        navBarData = null
    ) {
        MyCircleScreenContent(uiState, uiState.event)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            Lifecycle.State.RESUMED -> {
                uiState.event(MyCircleUiEvent.OnGetTribeList)
            }

            else -> {}
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyCircleScreenContent(
    uiState: MyCircleUiState,
    event: (MyCircleUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val tabs = listOf(stringResource(R.string.innercircle), stringResource(R.string.tribe))

    Column(
        modifier = Modifier
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Box {
            TabSliderBar(
                tabs = tabs,
                initialTabIndex = 0,
            ) { page ->
                when (page) {
                    0 -> InnerCircleScreen(uiState)
                    1 -> TribeScreen(uiState)
                }
                keyboardController?.hide()
            }
            Image(painter = painterResource(R.drawable.ic_app_icon), contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .clickable {
                        event(MyCircleUiEvent.OnAddTribeClick)
                    })
        }
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = MyCircleUiState()
    MyCircleScreenContent(uiState = uiState, event = uiState.event)

}






