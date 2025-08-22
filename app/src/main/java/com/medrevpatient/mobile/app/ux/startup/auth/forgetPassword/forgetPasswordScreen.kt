package com.medrevpatient.mobile.app.ux.startup.auth.forgetPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White80
import com.medrevpatient.mobile.app.ui.theme.WorkSans
@Preview
@Composable
fun ForgetPasswordScreen(
    navController: NavController = rememberNavController(),
    viewModel: ForgetPasswordViewModel = hiltViewModel()
) {

    val uiState = viewModel.splashUiState
    val verifyOtpUiState by uiState.forgetPasswordUiDataState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    uiState.event(ForgetPasswordUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.forgot_password),
                isBackVisible = true,
                onClick = {
                    uiState.event(ForgetPasswordUiEvent.OnBackClick)
                },

                )
        }
    ) {
        ForgetPasswordScreenContent(uiState = uiState, event = uiState.event)
    }
    if (verifyOtpUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ForgetPasswordScreenContent(
    uiState: ForgetPasswordUiState,
    event: (ForgetPasswordUiEvent) -> Unit
) {
    val forgetPasswordUiState by uiState.forgetPasswordUiDataState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .background(color = AppThemeColor)
            .padding(horizontal = 16.dp),
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.please_enter_your_email_id_to_receive_an_otp_to_reset_your_password),
            fontFamily = WorkSans,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            color = White80,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(70.dp))
        AppInputTextField(
            value = forgetPasswordUiState?.email ?: "",
            onValueChange = { event(ForgetPasswordUiEvent.EmailValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),

            errorMessage = forgetPasswordUiState?.emailErrorMsg,
            header = stringResource(R.string.enter_email_id),
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(50.dp))
        AppButtonComponent(
            onClick = {
                event(ForgetPasswordUiEvent.ForgetPasswordClick)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.submit),

            )
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = ForgetPasswordUiState()
    Surface {
        ForgetPasswordScreenContent(uiState = uiState, event = {})
    }
}