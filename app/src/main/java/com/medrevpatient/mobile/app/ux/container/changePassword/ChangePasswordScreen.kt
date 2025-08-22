package com.medrevpatient.mobile.app.ux.container.changePassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor

@ExperimentalMaterial3Api
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.changePasswordUsDataFlow.collectAsStateWithLifecycle()
    uiState.event(ChangePasswordUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.change_password),
                isBackVisible = true,
                onClick = {
                    uiState.event(ChangePasswordUiEvent.BackClick)
                },

                )
        },
        navBarData = null
    ) {
        ChangePasswordScreenContent(uiState, uiState.event)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ChangePasswordScreenContent(
    uiState: ChangePasswordUiState,
    event: (ChangePasswordUiEvent) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val changePasswordUiState by uiState.changePasswordUsDataFlow.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        ChangePasswordInputField(changePasswordUiState, event)
        Spacer(modifier = Modifier.height(50.dp))
        AppButtonComponent(
            onClick = {
                event(ChangePasswordUiEvent.ChangePasswordSubmit)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.submit),
        )

    }
}

@Composable
fun ChangePasswordInputField(
    changePasswordUiState: ChangePasswordDataState?,
    event: (ChangePasswordUiEvent) -> Unit
) {
    var oldPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        AppInputTextField(
            value = changePasswordUiState?.oldPassword ?: "",
            onValueChange = { event(ChangePasswordUiEvent.OldPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            isTrailingIconVisible = true,
            visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { oldPasswordVisible = !oldPasswordVisible },
            trailingIcon = if (oldPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            errorMessage = changePasswordUiState?.oldPasswordErrorMsg,
            header = stringResource(R.string.old_password),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = changePasswordUiState?.newPassword ?: "",
            onValueChange = { event(ChangePasswordUiEvent.NewPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,

            ),
            isTrailingIconVisible = true,
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
            trailingIcon = if (newPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            errorMessage = changePasswordUiState?.newPasswordErrorMsg,
            header = stringResource(R.string.new_password),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = changePasswordUiState?.confirmPassword ?: "",
            onValueChange = { event(ChangePasswordUiEvent.ConfirmPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,

            ),
            isTrailingIconVisible = true,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            trailingIcon = if (confirmPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            errorMessage = changePasswordUiState?.confirmPasswordErrorMsg,
            header = stringResource(R.string.confirm_password),
            leadingIcon = R.drawable.ic_app_icon,
        )
    }
}


@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = ChangePasswordUiState()
    ChangePasswordScreenContent(uiState = uiState, event = uiState.event)

}






