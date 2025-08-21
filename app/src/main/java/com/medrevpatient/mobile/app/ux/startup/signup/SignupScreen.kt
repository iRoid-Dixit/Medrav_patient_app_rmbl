package com.medrevpatient.mobile.app.ux.startup.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.R.string.of_the_skai_fitness_app
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.common.BottomButtonComponent
import com.medrevpatient.mobile.app.ui.common.GradientOrDivider
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.common.VerifiedBottomSheet
import com.medrevpatient.mobile.app.ui.theme.AppThemeBlue
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft3
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.ext.requireActivity
import com.medrevpatient.mobile.app.ux.startup.emailVerification.OTPVerificationBottomSheet
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(navController: NavController, viewModel: SignupViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState
    MedrevPatientTheme {
        WindowCompat.setDecorFitsSystemWindows(LocalContext.current.requireActivity().window, true)
        SignupScreenContent(uiState, uiState.event)
        val signupUiState by uiState.signUpUiStateFlow.collectAsStateWithLifecycle()
        if (signupUiState?.isLoading == true) {
            DialogLoader()
        }
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
fun SignupScreenContent(uiState: SignupUiState, event: (SignupUiEvent) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val signupUiState by uiState.signUpUiStateFlow.collectAsStateWithLifecycle()
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
                .padding(top = 100.dp),

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
                .padding(top = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
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
                    .background(White),
            ) {
                Text(
                    text = stringResource(R.string.sign_up),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp),
                    color = AppThemeBlue,
                    fontFamily = outFit,
                    fontWeight = FontWeight.W800,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.lets_begin),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = MineShaft,
                    fontFamily = outFit,
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(15.dp))
                AppInputTextField(
                    fieldValue = signupUiState?.firstName ?: "",
                    fieldErrorValue = signupUiState?.firstNameErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_user,
                    fieldHint = stringResource(R.string.first_name),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    isEnable = true,
                    onInputTextChange = { event(SignupUiEvent.FirstNameValueChange(it)) },
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.padding(6.dp))
                AppInputTextField(
                    fieldValue = signupUiState?.lastName ?: "",
                    fieldErrorValue = signupUiState?.lastNameErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_user,
                    fieldHint = stringResource(R.string.last_name),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    isEnable = true,
                    onInputTextChange = { event(SignupUiEvent.LastNameValueChange(it)) }
                )
                Spacer(modifier = Modifier.padding(6.dp))
                AppInputTextField(
                    fieldValue = signupUiState?.email ?: "",
                    fieldErrorValue = signupUiState?.emailErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_email,
                    fieldHint = stringResource(R.string.email),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email,
                    isEnable = true,
                    onInputTextChange = { event(SignupUiEvent.EmailValueChange(it)) }
                )
                Spacer(modifier = Modifier.padding(6.dp))
                AppInputTextField(
                    fieldValue = signupUiState?.password ?: "",
                    fieldErrorValue = signupUiState?.passwordErrorMsg ?: "",
                    fieldIconId = R.drawable.ic_password,
                    fieldHint = stringResource(R.string.password),
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Password,
                    isEnable = true,
                    onInputTextChange = { event(SignupUiEvent.PasswordValueChange(it)) },
                    isTrailingIconVisible = true,
                    trailingIconId = if (passwordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onTogglePasswordVisibility = { passwordVisible = !passwordVisible }
                )
                Spacer(modifier = Modifier.padding(6.dp))
                AppInputTextField(
                    fieldValue = signupUiState?.confirmPassword ?: "",
                    fieldErrorValue = signupUiState?.passwordNotMatch ?: "",
                    fieldIconId = R.drawable.ic_password,
                    fieldHint = stringResource(R.string.confirm_password),
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password,
                    isEnable = true,
                    onInputTextChange = { event(SignupUiEvent.ConfirmPasswordValueChange(it)) },
                    isTrailingIconVisible = true,
                    trailingIconId = if (confirmPasswordVisible) R.drawable.ic_show_pass else R.drawable.ic_pass_close,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    onTogglePasswordVisibility = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    }
                )
                //Spacer(modifier = Modifier.padding(6.dp))
                //GenderRadioButtonComponent(uiState)
                Spacer(modifier = Modifier.padding(5.dp))
                TermsConditionComponent(uiState)
                Spacer(modifier = Modifier.padding(20.dp))
                BottomButtonComponent(
                    text = stringResource(R.string.sign_up).uppercase(),
                    onClick = { event(SignupUiEvent.PerformSignUp) },
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
                    titlePart1 = stringResource(id = R.string.existing_user),
                    titlePart2 = stringResource(id = R.string.existing_user_login),
                    onClick = { event(SignupUiEvent.MoveToLoginScreen) }
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
                }
            }
        }
    }
}

@Composable
fun GenderRadioButtonComponent(uiState: SignupUiState) {
    val radioOptions = listOf("Male", "Female")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
    Row(modifier = Modifier.padding(horizontal = 9.dp)) {
        radioOptions.forEach { text ->
            Row(Modifier
                .fillMaxWidth()
                .weight(1f)
                .selectable(
                    selected = (text == selectedOption),
                    onClick = {
                        onOptionSelected(text)
                        uiState.event(SignupUiEvent.GenderClick(text))
                    }
                )
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color = MineShaft3)
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = {
                        onOptionSelected(text)
                        uiState.event(SignupUiEvent.GenderClick(text))
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MineShaft,
                        unselectedColor = MineShaft
                    ),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = outFit,
                    color = MineShaft,
                    modifier = Modifier
                        .padding(start = 5.dp, top = 5.dp, bottom = 5.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }

}

@Composable
fun TermsConditionComponent(uiState: SignupUiState) {
    val context = LocalContext.current
    val startString = stringResource(R.string.by_registering).plus(" ")
    val endString = stringResource(of_the_skai_fitness_app)
    val termsAndConditions = stringResource(R.string.terms_condition)
    val annotatedString = remember {
        buildAnnotatedString {
            append(startString)
            val termsAndConditionsStart = length
            append(termsAndConditions)
            addStyle(
                style = SpanStyle(
                    color = MineShaft, fontFamily = outFit, fontWeight = FontWeight.W700,
                    textDecoration = TextDecoration.Underline
                ),
                start = termsAndConditionsStart,
                end = termsAndConditionsStart + termsAndConditions.length,

                )
            addStringAnnotation(
                tag = "URL",
                annotation = context.getString(R.string.terms_condition),
                start = termsAndConditionsStart,
                end = termsAndConditionsStart + termsAndConditions.length,

                )
            append(" ".plus(endString))
        }
    }
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomCheckBox(uiState)
        Spacer(modifier = Modifier.padding(6.dp))
        Text(
            text = annotatedString,
            style = TextStyle(
                color = MineShaft,
                fontSize = 12.sp,
                lineHeight = 15.sp,
                fontFamily = outFit,
                fontWeight = FontWeight.W400,
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            modifier = Modifier.clickable {
                // Handle click

            }
        )
    }
}

@Composable
fun CustomCheckBox(uiState: SignupUiState) {
    var checked by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        if (checked) MineShaft else Color.Transparent,
        label = "color"
    )

    Box(modifier = Modifier
        .border(1.dp, MineShaft, RoundedCornerShape(6.dp))
        .size(20.dp)
        .drawBehind {
            val cornerRadius =
                6.dp.toPx()
            drawRoundRect(
                color = animatedColor,
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
        .clip(RoundedCornerShape(6.dp))
        .clickable {
            checked = !checked
            uiState.event(SignupUiEvent.TermsConditionClick(if (checked) 1 else 0))
        }
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            AnimatedVisibility(
                checked,
                enter = scaleIn(initialScale = 0.5f),
                exit = shrinkOut(shrinkTowards = Alignment.Center)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = "checked",
                    tint = White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpVerificationDialog(
    uiState: SignupUiState,
    event: (SignupUiEvent) -> Unit
) {
    val showVerificationBottomSheet by uiState.showVerificationBottomSheet.collectAsStateWithLifecycle()
    val otpState by uiState.otpUiState.collectAsStateWithLifecycle()
    val signupUiState by uiState.signUpUiStateFlow.collectAsStateWithLifecycle()
    val counter by uiState.counter.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, confirmValueChange = { value ->
            if (value == SheetValue.Expanded) {
                showVerificationBottomSheet
            } else {
                false
            }
        }
    )

    val email = signupUiState?.email ?: ""

    Column {
        OTPVerificationBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(SignupUiEvent.OpenOrCloseOTPDialog(false)) },
            onChangeEmailClick = { event(SignupUiEvent.OpenOrCloseOTPDialog(false)) },
            isSheetVisible = { showVerificationBottomSheet },
            otpValue = otpState?.otp ?: "",
            onOtpTextChange = { string, _ ->
                event(SignupUiEvent.OnOTPValueInsert(string))
            },
            seconds = { counter },
            onResendCodeClick = {
                if (counter == 0) {
                    event(SignupUiEvent.PerformResendOTP)
                }
            },
            isLoading = otpState?.isLoading ?: false,
            isOTPResend = otpState?.isOTPResend ?: false,
            isFromReset = false,
            email = email,
            errorMsg = otpState?.errorMsg,
            onVerifyClick = {
                event(SignupUiEvent.PerformVerifyOtp)
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
                        event(SignupUiEvent.OpenOrCloseOTPDialog(false))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OTPSuccessDialog(uiState: SignupUiState, event: (SignupUiEvent) -> Unit) {

    val showVerifiedBottomSheet by uiState.showVerifiedBottomSheet.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true, confirmValueChange = { value ->
            if (value == SheetValue.Expanded) {
                showVerifiedBottomSheet
            } else {
                false
            }
        }
    )

    Column {
        VerifiedBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(SignupUiEvent.OpenOrCloseVerifiedDialog(false))
            },
            isSheetVisible = { showVerifiedBottomSheet },
            onClickOfGetStarted = {
                event(SignupUiEvent.PerformGetStarted)
            },
            isFromResetPassword = false
        )
    }
}

@Preview
@Composable
private fun Preview() {
    Surface {
        SignupScreenContent(uiState = SignupUiState(), event = {})
    }
}