package com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W500
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.medrevpatient.mobile.app.ui.compose.common.OtpTextField
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White80
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.YellowB2
import com.medrevpatient.mobile.app.ui.theme.YellowDF
@Composable
fun VerifyOtpScreen(
    navController: NavController = rememberNavController(),
    viewModel: VerifyViewModel = hiltViewModel()
) {
    val uiState = viewModel.splashUiState
    val verifyOtpUiState by uiState.verifyOtpUiDataState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    uiState.event(VerifyOtpUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        topAppBar = {
            TopBarComponent(
                header = stringResource(R.string.verify_otp),
                isBackVisible = true,
                onClick = {
                    uiState.event(VerifyOtpUiEvent.OnBackClick)
                },

                )
        }
    ) {
        VerifyScreenContent(uiState = uiState, event = uiState.event)
    }
    if (verifyOtpUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}
@Composable
private fun VerifyScreenContent(
    uiState: VerifyOtpUiState,
    event: (VerifyOtpUiEvent) -> Unit
) {
    val verifyOtpUiState by uiState.verifyOtpUiDataState.collectAsStateWithLifecycle()
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
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = White80)) {
                    append(stringResource(R.string.enter_the_code_send_to_you_via_email))
                }
                withStyle(style = SpanStyle(color = YellowDF)) {
                    append(verifyOtpUiState?.email)
                }
            },
            fontFamily = WorkSans,
            fontWeight = FontWeight.W400,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(55.dp))
        Text(
            text = stringResource(R.string.enter_code),
            fontFamily = WorkSans,
            fontWeight = W500,
            fontSize = 14.sp, color = White,
            modifier = Modifier.padding(horizontal = 8.dp)

        )
        Spacer(modifier = Modifier.height(12.dp))
        OtpTextField(
            otpText = verifyOtpUiState?.otpValue ?: "",
            onOtpTextChange = { otp ->
                val filteredValue = otp.filter { it.isDigit() }
                event(VerifyOtpUiEvent.OtpTextValueChange(filteredValue))
            },
            errorMessage = verifyOtpUiState?.otpErrorMsg,
            modifier = Modifier.padding(horizontal = 8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val resendText = "Resend"
            val annotatedString = buildAnnotatedString {
                append(stringResource(R.string.did_not_receive_otp)) // Fixed part
                if (verifyOtpUiState?.isResendVisible == true) {
                    append("  ") // Space before Resend
                    val startIndex = length
                    withStyle(
                        style = SpanStyle(
                            color = White,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = W600,
                            fontFamily = WorkSans
                        )
                    ) {
                        append(resendText)
                    }
                    addStringAnnotation(
                        tag = "resend",
                        annotation = "resend_clicked",
                        start = startIndex,
                        end = length
                    )
                }
            }
            ClickableText(
                text = annotatedString,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = WorkSans,
                    fontWeight = W500,
                    color = White
                ),
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "resend",
                        start = offset,
                        end = offset
                    )
                        .firstOrNull()?.let {
                            if (verifyOtpUiState?.isResendVisible == true) {
                                event(VerifyOtpUiEvent.ResendOtp)
                            }
                        }
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = verifyOtpUiState?.remainingTimeFlow ?: "00:00",
                color = YellowB2,
                fontFamily = WorkSans,
                fontWeight = W500,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(60.dp))
        AppButtonComponent(
            onClick = {
                event(VerifyOtpUiEvent.OtpVerify)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.verify),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    val uiState = VerifyOtpUiState()
    Surface {
        VerifyScreenContent(uiState = uiState, event = {})
    }
}