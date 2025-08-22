package com.medrevpatient.mobile.app.ux.startup.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.LogInSignInNavText
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    uiState.event(LoginUiEvent.GetContext(context))
    val loginUiState by uiState.loginDataFlow.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = AppThemeColor,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LogInSignInNavText(
                        message = stringResource(R.string.don_t_have_an_account),
                        actionText = stringResource(R.string.sign_up),
                        onClick = {
                            uiState.event(LoginUiEvent.SignUp)

                        },

                        )
                }

            },

            )
    ) {
        SignInScreenContent(uiState = uiState, event = uiState.event)
    }
    if (loginUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)

}

@Composable
private fun SignInScreenContent(uiState: LoginUiState, event: (LoginUiEvent) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .clickable {
                keyboardController?.hide()
            }
            .background(color = AppThemeColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LoginViewContent(uiState = uiState) { authUiEvent ->
            event(authUiEvent)
        }
    }
}

@Composable
private fun LoginViewContent(uiState: LoginUiState, event: (LoginUiEvent) -> Unit) {
    val loginUiState by uiState.loginDataFlow.collectAsStateWithLifecycle()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 22.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitleComponent()
        Spacer(modifier = Modifier.height(35.dp))
        AppInputTextField(
            value = loginUiState?.email ?: "",
            onValueChange = { event(LoginUiEvent.EmailValueChange(it)) },
            errorMessage = loginUiState?.emailErrorMsg,
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            header = stringResource(id = R.string.email),
            leadingIcon = R.drawable.ic_app_icon,
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppInputTextField(
            value = loginUiState?.password ?: "",
            onValueChange = { event(LoginUiEvent.PasswordValueChanges(it)) },
            errorMessage = loginUiState?.passwordErrorMsg,
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = stringResource(R.string.password),
            isTrailingIconVisible = true,
            trailingIcon = if (passwordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.forgot_your_password),
            fontFamily = WorkSans,
            color = White,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .clickable {
                    event(LoginUiEvent.ForgetPassword)
                }
        )
        Spacer(modifier = Modifier.height(40.dp))
        AppButtonComponent(
            onClick = {
                event(LoginUiEvent.DoLogin)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_in),
        )
    }
}
@Composable
fun ScreenTitleComponent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_app_icon),
            contentDescription = null,
            modifier = Modifier.size(160.dp)
        )

        Text(
            text = stringResource(R.string.welcome),
            fontFamily = WorkSans,
            fontWeight = FontWeight.W500,
            color = White,
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.please_enter_your_details),
            fontFamily = WorkSans,
            fontWeight = FontWeight.W400,
            color = White,
            fontSize = 18.sp,
        )
    }
}
@Preview
@Composable
private fun Preview() {
    val uiState = LoginUiState()
    Surface {
        SignInScreenContent(uiState = uiState, event = {})
    }
}