package com.towyservice.mobile.app.ui.common.sheetContent


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.OtpTextField
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.Gray60
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_800
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.startup.auth.login.LoginUiEvent
import com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpUiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.Boolean


@Composable
fun ResetPasswordSheetContent(
    emailFlow: String = "",
    onEmailValueChange: (String) -> Unit = {},
    emailErrorFlow: String? = null,
    onProceedClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},

    ) {
    ResetPasswordSheetWrapper(
        emailFlow = emailFlow,
        onEmailValueChange = onEmailValueChange,
        emailErrorFlow = emailErrorFlow,
        onProceedClick = onProceedClick,
        onBackToLoginClick = onBackToLoginClick

    )
}

@Preview
@Composable
fun EmailVerificationSheetContent(
    countDown: String = "",
    resendSendCodeClick: () -> Unit = {},
    isResendVisible: Boolean? = false,
    resendEmail: String = "",
    editEmailClick: () -> Unit = {},
    otpValue: String = "",
    otpErrorFlow: String? = null,
    onOtpValueChange: (String) -> Unit = {},
    verifyClick: () -> Unit = {},
) {

    EmailVerificationSheetWrapper(
        countDown = countDown,
        resendSendCodeClick = resendSendCodeClick,
        isResendVisible = isResendVisible,
        resendEmail = resendEmail,
        otpValue = otpValue,
        onOtpValueChange = onOtpValueChange,
        editEmailClick = editEmailClick,
        verifyClick = verifyClick,
        otpErrorFlow = otpErrorFlow

    )
}

@Preview
@Composable
fun SetNewPasswordSheetContent(
    newPassword: String = "",
    newPasswordError: String? = null,
    onNewPasswordChange: (String) -> Unit = {},
    confirmPassword: String = "",
    confirmPasswordError: String? = null,
    onConfirmPasswordChange: (String) -> Unit = {},
    confirmClick: () -> Unit = {}
) {

    SetNewPasswordSheetWrapper(
        newPassword = newPassword,
        newPasswordError = newPasswordError,
        onNewPasswordChange = onNewPasswordChange,
        confirmPassword = confirmPassword,
        confirmPasswordError = confirmPasswordError,
        onConfirmPasswordChange = onConfirmPasswordChange,
        confirmClick = confirmClick
    )
}

@Preview
@Composable
fun SuccessSheetContent(
    proceedClick: () -> Unit = {}

) {

    SuccessSheetWrapper(
        proceedClick = proceedClick
    )
}

@Composable
private fun SuccessSheetWrapper(proceedClick: () -> Unit = {}) {

    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Image(painter = painterResource(id = R.drawable.ic_success_icon), contentDescription = null)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Success!",
            fontFamily = nunito_sans_800,
            color = SteelGray,
            fontSize = 30.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Your new password is live now",
            fontFamily = nunito_sans_400,
            color = Gray60,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppButtonComponent(
            onClick = proceedClick,
            modifier = Modifier.fillMaxWidth(),
            text = "Proceed",
            isLoading = false,
        )
    }
}


@Composable
private fun SetNewPasswordSheetWrapper(
    newPassword: String,
    onNewPasswordChange: (String) -> Unit = {},
    newPasswordError: String? = null,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit = {},
    confirmPasswordError: String? = null,
    confirmClick: () -> Unit = {}

) {
    var newPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmVisible by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Set a New Password",
            fontFamily = nunito_sans_800,
            color = SteelGray,
            fontSize = 30.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Choose a new strong password that hasn't been used previously",
            fontFamily = nunito_sans_400,
            color = Gray60,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(30.dp))
        AppInputTextField(
            value = newPassword,
            onValueChange = onNewPasswordChange,
            errorMessage = newPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isTrailingIconVisible = true,
            onTogglePasswordVisibility = { newPasswordVisible = !newPasswordVisible },
            visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = "Create a new password",
            trailingIcon = if (newPasswordVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password,
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppInputTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            errorMessage = confirmPasswordError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isTrailingIconVisible = true,
            onTogglePasswordVisibility = { confirmVisible = !confirmVisible },
            visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = "Confirm password",
            trailingIcon = if (confirmVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password,
        )
        Spacer(modifier = Modifier.height(35.dp))
        AppButtonComponent(
            onClick = confirmClick,
            modifier = Modifier.fillMaxWidth(),
            text = "Confirm",
            isLoading = false,
        )

    }
}

@Composable
private fun EmailVerificationSheetWrapper(
    otpValue: String,
    onOtpValueChange: (String) -> Unit = {},
    otpErrorFlow: String? = null,
    countDown: String,
    resendSendCodeClick: () -> Unit = {},
    isResendVisible: Boolean? = false,
    resendEmail: String,
    editEmailClick: () -> Unit = {},
    verifyClick: () -> Unit = {},

    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Email Verification",
            fontFamily = nunito_sans_800,
            color = SteelGray,
            fontSize = 30.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Please enter 4 digit code sent to your\n$resendEmail",
            fontFamily = nunito_sans_400,
            color = Black,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Change Email",
                fontFamily = nunito_sans_400,
                color = SteelGray,
                textDecoration = TextDecoration.Underline,
                fontSize = 14.sp,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.noRippleClickable {
                    editEmailClick()
                }
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        OtpTextField(
            otpText = otpValue,
            onOtpTextChange = { otp ->
                val filteredValue = otp.filter { it.isDigit() }
                onOtpValueChange(filteredValue)
            },
            errorMessage = otpErrorFlow,
            modifier = Modifier.padding(horizontal = 30.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )
        Spacer(modifier = Modifier.height(30.dp))
        AppButtonComponent(
            onClick = verifyClick,
            modifier = Modifier.fillMaxWidth(),
            text = "Verify",
            isLoading = false,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = countDown,
            fontFamily = nunito_sans_400,
            color = Black,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (isResendVisible == true) {
            Text(
                text = "Resend code",
                fontFamily = nunito_sans_700,
                color = AppThemeColor,
                fontSize = 14.sp,
                modifier = Modifier.noRippleClickable {
                    resendSendCodeClick()
                }

            )
        }
    }


}

@Preview
@Composable
private fun ResetPasswordSheetWrapper(
    emailFlow: String = "",
    onEmailValueChange: (String) -> Unit = {},
    emailErrorFlow: String? = null,
    onProceedClick: () -> Unit = {},
    onBackToLoginClick: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                keyboardController?.hide()
            }
            .background(White)
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset your Password",
            fontFamily = nunito_sans_800,
            color = SteelGray,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Please enter your registered Email \nto reset your password",
            fontFamily = nunito_sans_400,
            color = Black,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppInputTextField(
            value = emailFlow,
            onValueChange = onEmailValueChange,
            title = "Email Address",
            isTitleVisible = true,
            errorMessage = emailErrorFlow,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            header = stringResource(id = R.string.email),
        )
        Spacer(modifier = Modifier.height(40.dp))
        AppButtonComponent(
            onClick = onProceedClick,
            modifier = Modifier.fillMaxWidth(),
            text = "Proceed",
            isLoading = false,
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = "back to login?",
            fontFamily = nunito_sans_700,
            color = AppThemeColor,
            fontSize = 14.sp,
            modifier = Modifier.noRippleClickable {
                onBackToLoginClick()
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSheetLauncher(
    modifier: Modifier = Modifier,
    shouldShowSheet: Boolean = false,
    confirmValueChange: Boolean = true,
    onDismissRequest: () -> Unit = {},
    content: @Composable ColumnScope.(SheetState, CoroutineScope) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newState -> !shouldShowSheet || confirmValueChange }
    )
    val scope = rememberCoroutineScope()
    if (shouldShowSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    onDismissRequest()
                }
            },
            modifier = modifier,
            containerColor = White,
            content = { content(sheetState, scope) },
        )
    }
}