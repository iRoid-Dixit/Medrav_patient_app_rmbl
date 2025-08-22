package com.medrevpatient.mobile.app.ux.container.storage

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W400
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.model.domain.response.container.storege.StorageResponse
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.compose.common.progress.HalfCircularProgress
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.Gray5A
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils
import java.util.Locale

@ExperimentalMaterial3Api
@Composable
fun StorageScreen(
    navController: NavController,
    viewModel: StorageViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val userStorage by uiState.userStorageData.collectAsStateWithLifecycle()
    val changePasswordUiState by uiState.storageDataFlow.collectAsStateWithLifecycle()
    uiState.event(StorageUiEvent.GetContext(context))
    val navBackStackEntry = navController.currentBackStackEntry
    val subscriptionStatus = navBackStackEntry?.savedStateHandle?.get<Boolean>("subscriptionStatus")
    LaunchedEffect(subscriptionStatus) {
        uiState.event(StorageUiEvent.SubscriptionStatus(subscriptionStatus == true))
    }
    Log.d("TAG", "subscriptionStatus: $subscriptionStatus")
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(id = R.string.storage),
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.event(StorageUiEvent.BackClick)
                },
            )
        },
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppButtonComponent(
                        onClick = {
                            uiState.event(StorageUiEvent.SubscriptionClick)
                        },
                        buttonBackgroundColor = AppThemeColor,
                        textColors = White,
                        borderColors = White,
                        fontWeight = W400,
                        fonsSize = 16,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 20.dp),
                        text = "Upgrade Storage"

                    )
                    Spacer(modifier = Modifier.height(15.dp))

                }
            }
        )
    ) {
        StorageScreenContent(userStorage)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun StorageScreenContent(
    userStorage: StorageResponse?,


    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            HalfCircularProgress(
                progress = (userStorage?.percentageUsed ?: 0.0).toFloat() / 100f,
                modifier = Modifier.size(135.dp),
                backgroundColor = Gray5A,
                progressColor = White
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    String.format(
                        Locale.US,
                        "%.2f",
                        (userStorage?.percentageUsed ?: 0.0)
                    ) + "%",
                    color = White,
                    fontWeight = FontWeight.W600,
                    fontFamily = WorkSans,
                    fontSize = 18.sp
                )
                Text(
                    text = String.format(
                        Locale.US,
                        "%.2f of %.2f",
                        (userStorage?.totalStorageUsedGB ?: 0.0),
                        (userStorage?.storageLimit ?: 0.0)
                    ),
                    color = White,
                    fontSize = 14.sp,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W500
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(R.string.file_type),
            color = White,
            fontSize = 20.sp,
            fontWeight = W400,
            fontFamily = WorkSans
        )

        Spacer(modifier = Modifier.height(16.dp))
        StorageItem(
            label = stringResource(R.string.photos),
            value = AppUtils.formatStorageSize(userStorage?.totalPhotoKB ?: 0.0),
            progress = ((userStorage?.photoProgress ?: 0.0) / 100.0).toFloat()
        )
        Spacer(modifier = Modifier.height(16.dp))
        StorageItem(
            label = stringResource(id = R.string.video),
            value = AppUtils.formatStorageSize(userStorage?.totalVideoKB ?: 0.0),
            progress = ((userStorage?.videoProgress ?: 0.0) / 100.0).toFloat()
        )
    }
}

@Composable
fun StorageItem(label: String, value: String, progress: Float) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Gray2F, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 18.dp, horizontal = 25.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = White,
                fontFamily = WorkSans,
                fontSize = 18.sp,
                fontWeight = W400
            )
            Text(
                text = value,
                color = White,
                fontFamily = WorkSans,
                fontSize = 18.sp,
                fontWeight = W400
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = White,
            trackColor = White50,
        )
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val storage = StorageResponse()
    StorageScreenContent(userStorage = storage)
}






