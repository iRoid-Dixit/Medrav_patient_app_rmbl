package com.medrevpatient.mobile.app.ux.startup.auth.login

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.LogInSignInNavText
import com.medrevpatient.mobile.app.ui.compose.common.OrDivider
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_800
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.towyservice.mobile.app.ui.common.sheetContent.EmailVerificationSheetContent
import com.towyservice.mobile.app.ui.common.sheetContent.ModelSheetLauncher
import com.towyservice.mobile.app.ui.common.sheetContent.ResetPasswordSheetContent
import com.towyservice.mobile.app.ui.common.sheetContent.SetNewPasswordSheetContent
import com.towyservice.mobile.app.ui.common.sheetContent.SuccessSheetContent

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    uiState.event(LoginUiEvent.GetContext(context))

    AppScaffold(
        containerColor = White,
        navBarData = null

    ) {
        LoginScreeContent(uiState = uiState, event = uiState.event)
    }

    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun LoginScreeContent(uiState: LoginUiState, event: (LoginUiEvent) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(color = White),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LoginViewContent(uiState = uiState) { authUiEvent ->
            event(authUiEvent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginViewContent(uiState: LoginUiState, event: (LoginUiEvent) -> Unit) {
    val loginUiState by uiState.loginDataFlow.collectAsStateWithLifecycle()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        ScreenTitleComponent()
        Spacer(modifier = Modifier.height(35.dp))
        AppInputTextField(
            value = loginUiState?.email ?: "",
            onValueChange = { event(LoginUiEvent.EmailValueChange(it)) },
            title = "Email Address",
            isTitleVisible = true,
            errorMessage = loginUiState?.emailErrorMsg,
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
            isTitleVisible = true,
            onValueChange = { event(LoginUiEvent.PasswordValueChanges(it)) },
            title = "Password",
            errorMessage = loginUiState?.passwordErrorMsg,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isTrailingIconVisible = true,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = stringResource(R.string.password),
            trailingIcon = if (passwordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.forgot_your_password),
            fontFamily = nunito_sans_700,
            color = AppThemeColor, // Purple color matching the design
            fontSize = 14.sp,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .clickable {
                    event(LoginUiEvent.ResentSheetVisibility(true))
                }
        )

        Spacer(modifier = Modifier.height(40.dp))
        AppButtonComponent(
            onClick = {
                event(LoginUiEvent.DoLogin)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.log_in),
            isLoading = loginUiState?.showLoader == true
        )
        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally)
        ) {
            listOf(
                stringResource(R.string.google) to R.drawable.ic_google,
                stringResource(R.string.facebook) to R.drawable.ic_facebook
            ).forEach { (provider, icon) ->
                Box(
                    modifier = Modifier
                        .clickable {
                            when (provider) {
                                context.getString(R.string.google) -> {

                                }

                                context.getString(R.string.facebook) -> {

                                    /*  AppUtils.showMessage(
                                          context,
                                          context.getString(R.string.feature_in_progress_coming_soon)
                                      )*/
                                }
                            }
                        }
                        .padding(13.dp)
                ) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = "$provider login"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            LogInSignInNavText(
                message = "Don't have an account?",
                actionText = "Register",
                onClick = {
                    uiState.event(LoginUiEvent.SignUp)
                },
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
    ModelSheetLauncher(
        shouldShowSheet = loginUiState?.resetSheetVisible == true,
        onDismissRequest = {
            uiState.event(LoginUiEvent.ResentSheetVisibility(false))
        }
    ) { state, scope ->
        ResetPasswordSheetContent(
            emailErrorFlow = loginUiState?.resendEmailErrorMsg,
            emailFlow = loginUiState?.resendEmail ?: "",
            onEmailValueChange = {
                event(LoginUiEvent.ResendValueChange(it))
            },
            onProceedClick = {
                event(LoginUiEvent.ProceedClick(sheetState = state, scope))
            },
            onBackToLoginClick = {
                event(LoginUiEvent.ResentSheetVisibility(false))
            }
        )
    }
    ModelSheetLauncher(
        shouldShowSheet = loginUiState?.setPasswordVisible == true,
        onDismissRequest = {
            uiState.event(LoginUiEvent.SetPasswordSheetVisibility(false))
        }
    ) { state, scope ->
        SetNewPasswordSheetContent(
            newPassword = loginUiState?.newPassword ?: "",
            newPasswordError = loginUiState?.newPasswordErrorMsg,
            onNewPasswordChange = {
                event(LoginUiEvent.NewPasswordValueChange(it))
            },
            confirmPassword = loginUiState?.confirmPassword ?: "",
            onConfirmPasswordChange = {
                event(LoginUiEvent.ConfirmPasswordValueChange(it))
            },
            confirmPasswordError = loginUiState?.confirmPasswordErrorMsg,
            confirmClick = {
                event(LoginUiEvent.ConfirmClick(sheetState = state, scope))
            }
        )
    }
    ModelSheetLauncher(
        shouldShowSheet = loginUiState?.emailVerificationSheetVisible == true,
        onDismissRequest = {
            uiState.event(LoginUiEvent.EmailVerificationSheetVisibility(false))
        }
    ) { state, scope ->
        EmailVerificationSheetContent(
            countDown = loginUiState?.remainingTimeFlow ?: "00:00",
            resendSendCodeClick = {
                event(LoginUiEvent.ResendCode)
            },
            isResendVisible = loginUiState?.isResendVisible,
            resendEmail = loginUiState?.resendEmail ?: "",
            otpValue = loginUiState?.otpValue ?: "",
            onOtpValueChange = {
                event(LoginUiEvent.OtpValueChange(it))
            },
            editEmailClick = {
                event(LoginUiEvent.EditEmailClick(sheetState = state, scope = scope))
            },
            verifyClick = {
                event(LoginUiEvent.VerifyClick(sheetState = state, scope = scope))
            },
            otpErrorFlow = loginUiState?.otpErrorMsg
        )
    }
    ModelSheetLauncher(
        shouldShowSheet = loginUiState?.successSheetVisible == true,
        onDismissRequest = {
            uiState.event(LoginUiEvent.SuccessSheetVisibility(false))
        }
    ) { state, scope ->
        SuccessSheetContent(
            proceedClick = {
                event(LoginUiEvent.ProceedClickSuccess(sheetState = state, scope))

            }

        )
    }
}

@Composable
fun ScreenTitleComponent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Text(
            text = "Welcome Back!",
            fontFamily = nunito_sans_800,
            color = SteelGray,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Log in to continue your journey.",
            fontFamily = nunito_sans_400,
            color = Gray50,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = LoginUiState()
    Surface {
        LoginScreeContent(uiState = uiState, event = {})
    }
}