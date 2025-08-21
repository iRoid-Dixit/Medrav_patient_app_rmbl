package com.medrevpatient.mobile.app.ux.startup.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.common.BottomButtonComponent
import com.medrevpatient.mobile.app.ui.common.GradientOrDivider
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.common.VerifiedBottomSheet
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft50
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.medrevpatient.mobile.app.ux.startup.emailVerification.OTPVerificationBottomSheet
import com.medrevpatient.mobile.app.ux.startup.emailVerification.customImePadding
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, true)
        LoginScreenContent(uiState, uiState.event)
        val signInUiState by uiState.state.collectAsStateWithLifecycle()
        if (signInUiState.isLoading) {
            DialogLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun LoginScreenContent(uiState: SignInUiState, event: (SignInUiEvent) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val signInUiState by uiState.state.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TitleDualFont(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 130.dp),

            color = AppThemeBlue,
            fontWeightBold = FontWeight.Bold,
            fontWeightRegular = FontWeight.Light,
            fontSize = 30,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = stringResource(id = R.string.skai),
            titlePart2 = stringResource(id = R.string.fitness)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0x4759D6DF),
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 70.dp, bottom = 20.dp)
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(Color.White)
                    .align(Alignment.BottomCenter)

            ) {
                Text(
                    text = stringResource(R.string.log_in),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 25.dp),
                    color = AppThemeBlue,
                    fontFamily = outFit,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )

                Text(
                    text = stringResource(R.string.welcome_back),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp),
                    color = MineShaft,
                    fontFamily = outFit,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.padding(15.dp))
                AppInputTextField(
                    fieldValue = signInUiState.email,
                    fieldErrorValue = signInUiState.emailErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_email,
                    fieldHint = stringResource(R.string.email),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    isEnable = true,
                    onInputTextChange = { event(SignInUiEvent.EmailValueChange(it)) },
                    trailingIconId = null
                )
                Spacer(modifier = Modifier.padding(10.dp))
                AppInputTextField(
                    fieldValue = signInUiState.password,
                    fieldErrorValue = signInUiState.passwordErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_password,
                    fieldHint = stringResource(R.string.password),
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                    isEnable = true,
                    onInputTextChange = { event(SignInUiEvent.PasswordValueChange(it)) },
                    isTrailingIconVisible = true,
                    trailingIconId = if (passwordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.padding(15.dp))
                BottomButtonComponent(
                    text = stringResource(R.string.log_in).uppercase(),
                    onClick = {
                        event(SignInUiEvent.PerformLogin)
                    },
                    modifier = Modifier.padding(horizontal = 20.dp),
                    buttonColors = ButtonDefaults.buttonColors(containerColor = MineShaft),
                    fontFamily = outFit
                )
                Spacer(modifier = Modifier.padding(12.dp))
                GradientOrDivider()
                Spacer(modifier = Modifier.padding(7.dp))
                TitleDualFont(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),

                    color = MineShaft,
                    fontWeightBold = FontWeight.Light,
                    fontWeightRegular = FontWeight.SemiBold,
                    fontSize = 13,
                    fontFamilyBold = outFit,
                    fontFamilyRegular = outFit,
                    titlePart1 = stringResource(id = R.string.forgot_password),
                    titlePart2 = stringResource(id = R.string.reset),
                    onClick = { event(SignInUiEvent.MoveToForgotPasswordScreen) }
                )
                Spacer(modifier = Modifier.padding(7.dp))
                TitleDualFont(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 25.dp),

                    color = MineShaft,
                    fontWeightBold = FontWeight.Light,
                    fontWeightRegular = FontWeight.SemiBold,
                    fontSize = 13,
                    fontFamilyBold = outFit,
                    fontFamilyRegular = outFit,
                    titlePart1 = stringResource(id = R.string.if_you_are_new),
                    titlePart2 = stringResource(id = R.string.sign_up),
                    onClick = { event(SignInUiEvent.MoveToSignUpScreen) }
                )
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    OtpVerificationDialog(uiState = uiState) { authUiEvent ->
                        event(authUiEvent)
                    }

                    OTPSuccessDialog(uiState = uiState) { authUiEvent ->
                        event(authUiEvent)
                    }
                    ResetPasswordDialog(uiState = uiState) { authUiEvent ->
                        event(authUiEvent)
                    }
                    NewPasswordDialog(uiState = uiState) { authUiEvent ->
                        event(authUiEvent)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpVerificationDialog(
    uiState: SignInUiState,
    event: (SignInUiEvent) -> Unit
) {
    val showVerificationBottomSheet by uiState.showVerificationBottomSheet.collectAsStateWithLifecycle()
    val otpState by uiState.otpUiState.collectAsStateWithLifecycle()
    val resetUiState by uiState.resetPasswordUiState.collectAsStateWithLifecycle()
    val signInUiState by uiState.state.collectAsStateWithLifecycle()
    val counter by uiState.counter.collectAsStateWithLifecycle()
    val isFromReset by uiState.isFromResetPassword.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, confirmValueChange = { value ->
            if (value == SheetValue.Expanded) {
                showVerificationBottomSheet
            } else {
                false
            }
        }
    )

    val email = if (resetUiState?.email?.isNotEmpty() == true) {
        resetUiState?.email ?: ""
    } else {
        signInUiState.email
    }

    Column {
        OTPVerificationBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(SignInUiEvent.OpenOrCloseOTPDialog(false)) },
            onChangeEmailClick = { event(SignInUiEvent.OpenOrCloseOTPDialog(false)) },
            isSheetVisible = { showVerificationBottomSheet },
            otpValue = otpState?.otp ?: "",
            onOtpTextChange = { string, _ ->
                event(SignInUiEvent.OnOTPValueInsert(string))
            },
            seconds = { counter },
            onResendCodeClick = {
                if (counter == 0) {
                    event(SignInUiEvent.PerformResendOTP)
                }
            },
            isLoading = otpState?.isLoading ?: false,
            isOTPResend = otpState?.isOTPResend ?: false,
            isFromReset = isFromReset,
            email = email,
            errorMsg = otpState?.errorMsg,
            onVerifyClick = {
                event(SignInUiEvent.PerformVerifyOtp)
            }
        )
    }

    LaunchedEffect(key1 = otpState?.isSuccess) {
        otpState?.let {
            if (it.isSuccess) {
                launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        event(SignInUiEvent.OpenOrCloseOTPDialog(false))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OTPSuccessDialog(uiState: SignInUiState, event: (SignInUiEvent) -> Unit) {

    val showVerifiedBottomSheet by uiState.showVerifiedBottomSheet.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Column {
        VerifiedBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(SignInUiEvent.OpenOrCloseVerifiedDialog(false))
            },
            isSheetVisible = { showVerifiedBottomSheet },
            onClickOfGetStarted = { event(SignInUiEvent.PerformGetStarted) },
            isFromResetPassword = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordDialog(
    uiState: SignInUiState,
    event: (SignInUiEvent) -> Unit
) {

    val showResetEmailBottomSheet by uiState.showResetEmailBottomSheet.collectAsStateWithLifecycle()
    val resetPasswordState by uiState.resetPasswordUiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val focus = LocalFocusManager.current

    Column {
        if (showResetEmailBottomSheet) {
            focus.clearFocus()
            ModalBottomSheet(
                onDismissRequest = { event(SignInUiEvent.OpenOrCloseResetEmailDialog(false)) },
                sheetState = sheetState,
                contentWindowInsets = { BottomSheetDefaults.windowInsets },
                /*modifier = Modifier.then(Modifier.navigationBarsPadding()),*/
                containerColor = white,
                dragHandle = null
            ) {
                // Sheet content
                ResetPasswordSheetContent(
                    onSendOTPClicked = {
                        event(SignInUiEvent.PerformResendOTP)
                    },
                    onLoginClick = {
                        event(SignInUiEvent.OpenOrCloseResetEmailDialog(false))
                    },
                    email = resetPasswordState?.email ?: "",
                    errorMsg = resetPasswordState?.emailErrorMsg,
                    event = event
                )
            }
        }
    }
}

@Composable
fun ResetPasswordSheetContent(
    modifier: Modifier = Modifier,
    onSendOTPClicked: () -> Unit,
    onLoginClick: () -> Unit,
    email: String,
    errorMsg: String?,
    event: (SignInUiEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
            .customImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 7.dp)
        )

        Text(
            text = stringResource(R.string.reset_your_password),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        Column(
            modifier = Modifier.padding(top = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.please_enter_your_registered_email_to_reset_your_password),
                fontFamily = outFit,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MineShaft50,
                lineHeight = 1.2.em,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        }

        AppInputTextField(
            fieldValue = email,
            fieldErrorValue = errorMsg,
            fieldIconId = R.drawable.ic_email,
            fieldHint = stringResource(R.string.email),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Email,
            isEnable = true,
            onInputTextChange = { event(SignInUiEvent.ResetEmailValueChange(it)) },
            trailingIconId = null,
            horizontalPadding = 1,
            modifier = Modifier.padding(top = 25.dp)
        )
        BottomButtonComponent(
            onClick = {
                onSendOTPClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            text = stringResource(R.string.send_otp).uppercase(),
        )
        Spacer(modifier = Modifier.padding(top = 22.dp))
        GradientOrDivider()
        TitleDualFont(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 7.dp)
                .align(Alignment.CenterHorizontally),

            color = MineShaft,
            fontWeightBold = FontWeight.Light,
            fontWeightRegular = FontWeight.SemiBold,
            fontSize = 12,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = stringResource(id = R.string.remember_password),
            titlePart2 = stringResource(id = R.string.log_in),
            onClick = {
                onLoginClick()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordDialog(
    uiState: SignInUiState,
    event: (SignInUiEvent) -> Unit
) {

    val showNewPasswordBottomSheet by uiState.showNewPasswordBottomSheet.collectAsStateWithLifecycle()
    val resetPasswordState by uiState.resetPasswordUiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val focus = LocalFocusManager.current
    Column {
        if (showNewPasswordBottomSheet) {
            focus.clearFocus()
            ModalBottomSheet(
                onDismissRequest = { event(SignInUiEvent.OpenOrCloseNewPasswordDialog(false)) },
                sheetState = sheetState,
                contentWindowInsets = { BottomSheetDefaults.windowInsets },
                modifier = Modifier.then(Modifier.navigationBarsPadding()),
                containerColor = white,
                dragHandle = null
            ) {
                // Sheet content
                NewPasswordSheetContent(
                    onSetPasswordClicked = { event(SignInUiEvent.PerformSetNewPassword) },
                    onLoginClick = { event(SignInUiEvent.OpenOrCloseNewPasswordDialog(false)) },
                    password = resetPasswordState?.password ?: "",
                    passwordErrorMsg = resetPasswordState?.passwordErrorMsg,
                    confirmPassword = resetPasswordState?.confirmPassword ?: "",
                    confirmPasswordErrorMsg = resetPasswordState?.confirmPasswordErrorMsg ?: "",
                    event = event
                )
            }
        }
    }
}

@Composable
fun NewPasswordSheetContent(
    modifier: Modifier = Modifier,
    onSetPasswordClicked: () -> Unit,
    onLoginClick: () -> Unit,
    password: String,
    passwordErrorMsg: String?,
    confirmPassword: String,
    confirmPasswordErrorMsg: String?,
    event: (SignInUiEvent) -> Unit
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(rememberScrollState())
            .customImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 7.dp)
        )

        Text(
            text = stringResource(R.string.set_new_password),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        Column(
            modifier = Modifier.padding(top = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.new_pass_must_be_different),
                fontFamily = outFit,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MineShaft50,
                lineHeight = 1.2.em,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 35.dp)
            )
        }
        AppInputTextField(
            fieldValue = password,
            fieldErrorValue = passwordErrorMsg ?: "",
            fieldIconId = R.drawable.ic_password,
            fieldHint = stringResource(R.string.password),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password,
            isEnable = true,
            onInputTextChange = { event(SignInUiEvent.ResetPasswordValueChange(it)) },
            horizontalPadding = 1,
            isTrailingIconVisible = true,
            trailingIconId = if (passwordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            modifier = Modifier.padding(top = 25.dp)
        )
        AppInputTextField(
            fieldValue = confirmPassword,
            fieldErrorValue = confirmPasswordErrorMsg ?: "",
            fieldIconId = R.drawable.ic_password,
            fieldHint = stringResource(R.string.confirm_password),
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password,
            isEnable = true,
            onInputTextChange = { event(SignInUiEvent.ResetConfirmPasswordValueChange(it)) },
            isTrailingIconVisible = true,
            horizontalPadding = 1,
            trailingIconId = if (confirmPasswordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            modifier = Modifier.padding(top = 10.dp)
        )
        BottomButtonComponent(
            onClick = {
                onSetPasswordClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 22.dp),
            text = stringResource(R.string.set_password).uppercase(),
        )
        Spacer(modifier = Modifier.padding(top = 22.dp))
        GradientOrDivider()
        TitleDualFont(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 13.dp, bottom = 7.dp)
                .align(Alignment.CenterHorizontally),

            color = MineShaft,
            fontWeightBold = FontWeight.Light,
            fontWeightRegular = FontWeight.SemiBold,
            fontSize = 12,
            fontFamilyBold = outFit,
            fontFamilyRegular = outFit,
            titlePart1 = stringResource(id = R.string.remember_password),
            titlePart2 = stringResource(id = R.string.log_in),
            onClick = {
                onLoginClick()
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        LoginScreenContent(uiState = SignInUiState(), event = {})
    }
}