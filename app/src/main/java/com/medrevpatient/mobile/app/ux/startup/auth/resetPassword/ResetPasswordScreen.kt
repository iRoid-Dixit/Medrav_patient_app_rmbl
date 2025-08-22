package com.medrevpatient.mobile.app.ux.startup.auth.resetPassword

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun ResetPasswordScreen(
    navController: NavController = rememberNavController(),
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val uiState = viewModel.resetUiState
    val resetOtpUiState by uiState.resetPasswordUiDataState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    uiState.event(ResetPasswordUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.reset_password),
                isBackVisible = true,
                onClick = {
                    uiState.event(ResetPasswordUiEvent.OnBackClick)
                },
                )
        }
    ) {
        ResetPasswordScreenContent(uiState = uiState, event = uiState.event)
    }
    if (resetOtpUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ResetPasswordScreenContent(
    uiState: ForgetPasswordUiState,
    event: (ResetPasswordUiEvent) -> Unit
) {

    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val resetPasswordUiState by uiState.resetPasswordUiDataState.collectAsStateWithLifecycle()
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
            text = stringResource(R.string.your_new_password_must_be_different_from_previously_used_password),
            fontFamily = WorkSans,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            color = White80,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(70.dp))
        AppInputTextField(
            value = resetPasswordUiState?.newPassword?:"",
            onValueChange = { event(ResetPasswordUiEvent.NewPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            errorMessage = resetPasswordUiState?.newPasswordErrorMsg,
            isTrailingIconVisible = true,
            trailingIcon = if (newPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = "New Password",
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppInputTextField(
            value = resetPasswordUiState?.confirmPassword?:"",
            onValueChange = { event(ResetPasswordUiEvent.ConfirmPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            errorMessage = resetPasswordUiState?.confirmPasswordErrorMsg,
            isTrailingIconVisible = true,
            trailingIcon = if (confirmPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = "Confirm Password",
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(50.dp))
        AppButtonComponent(
            onClick = {
                event(ResetPasswordUiEvent.ResetPasswordSubmit)
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
        ResetPasswordScreenContent(uiState = uiState, event = {})
    }
}