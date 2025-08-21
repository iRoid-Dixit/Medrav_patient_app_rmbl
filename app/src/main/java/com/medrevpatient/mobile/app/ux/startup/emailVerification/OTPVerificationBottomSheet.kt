package com.medrevpatient.mobile.app.ux.startup.emailVerification


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.common.BottomButtonComponent
import com.medrevpatient.mobile.app.ui.common.TitleDualFont
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft50
import com.medrevpatient.mobile.app.ui.theme.outFit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPVerificationBottomSheet(
    sheetState: () -> SheetState,
    onDismissRequest: () -> Unit,
    isSheetVisible: () -> Boolean,
    onVerifyClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    isFromReset: Boolean,
    isLoading: Boolean,
    isOTPResend: Boolean,
    email: String,
    otpValue: String,
    seconds: () -> Int,
    errorMsg: String?,
    onOtpTextChange: (String, Boolean) -> Unit,
    onResendCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
) {
    val focus = LocalFocusManager.current
    if (isSheetVisible()) {
        focus.clearFocus()
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState(),
            contentWindowInsets = { windowInsets },
            modifier = modifier.then(Modifier.navigationBarsPadding()),
            containerColor = White,
            dragHandle = null
        ) {
            // Sheet content
            EmailSheetContent(
                onVerifyClicked = { onVerifyClick() },
                onChangeEmailClick = { onChangeEmailClick() },
                onResendCodeClick = onResendCodeClick,
                isLoading = isLoading,
                isOTPResend = isOTPResend,
                isFromReset = isFromReset,
                seconds = seconds,
                email = email,
                otpValue = otpValue,
                errorMsg = errorMsg,
                onOtpTextChange = onOtpTextChange,
                modifier = Modifier.padding(top = 7.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }
}


@Composable
fun EmailSheetContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isOTPResend: Boolean = false,
    isFromReset: Boolean = true,
    onVerifyClicked: () -> Unit,
    onResendCodeClick: () -> Unit,
    onChangeEmailClick: () -> Unit,
    otpValue: String,
    email: String,
    seconds: () -> Int,
    errorMsg: String?,
    onOtpTextChange: (String, Boolean) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .verticalScroll(rememberScrollState())
            .customImePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
        )

        Text(
            text = stringResource(R.string.verify_email_title),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
        )

        Column(
            modifier = Modifier.fillMaxWidth(0.92f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.code_sent_to_email).plus(" ").plus(maskEmail(email)),
                fontFamily = outFit,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MineShaft50,
                lineHeight = 1.2.em,
                textAlign = TextAlign.Center,
            )
            if (!isFromReset) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onChangeEmailClick()
                        }
                        .padding(top = 5.dp),
                    text = stringResource(R.string.change_email),
                    fontFamily = outFit,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MineShaft,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = onOtpTextChange,
                isError = !errorMsg.isNullOrBlank(),
                isEnable = !isLoading,
                modifier = Modifier.padding(top = 5.dp)
            )
            AnimatedVisibility(visible = !errorMsg.isNullOrBlank()) {
                Text(
                    text = errorMsg ?: "",
                    color = Red,
                    fontSize = 12.sp,
                    fontFamily = outFit,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(start = 4.dp, top = 5.dp)
                        .fillMaxWidth()
                )
            }
        }

        BottomButtonComponent(
            onClick = {
                onVerifyClicked()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            text = stringResource(R.string.verify),
        )
        OTPTimerText(seconds = seconds(), isOTPResend = isOTPResend)
        ResendCodeText(seconds = seconds(), onResendCodeClick = onResendCodeClick)
    }
}

@Composable
fun Modifier.customImePadding(): Modifier {
    val imeInsets = WindowInsets.ime
    val imeBottomPadding = imeInsets.getBottom(LocalDensity.current).dp
    val givenPadding = if (imeBottomPadding >= 200.0.dp) {
        200.0.dp
    } else {
        imeBottomPadding
    }
    return this.padding(
        bottom = givenPadding
    )
}

@SuppressLint("DefaultLocale")
@Composable
private fun OTPTimerText(seconds: Int, isOTPResend: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val format = String.format("%02d", seconds)

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Transparent,
                            Color.Gray
                        )
                    )
                )
        )

        if (isOTPResend) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeCap = StrokeCap.Round,
                    color = MineShaft, modifier = Modifier
                        .size(18.dp)
                )
            }
        } else {
            Text(
                modifier = Modifier.weight(1f),
                text = "00:$format",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = MineShaft,
                fontFamily = outFit,
                fontWeight = FontWeight.Normal
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Gray,
                            Transparent
                        )
                    )
                )
        )
    }
}

@Composable
private fun ResendCodeText(
    seconds: Int,
    onResendCodeClick: () -> Unit
) {
    TitleDualFont(
        color = MineShaft,
        fontWeightBold = FontWeight.Light,
        fontWeightRegular = FontWeight.Bold,
        fontSize = 12,
        fontFamilyBold = outFit,
        fontFamilyRegular = outFit,
        titlePart1 = stringResource(R.string.did_not_receive_code),
        titlePart2 = stringResource(R.string.resend),
        textAlign = TextAlign.Center,
        onClick = {
            onResendCodeClick()
        },
        isFromResend = true,
        isEnable = seconds == 0
    )
}


@Preview(showBackground = true)
@Composable
private fun EmailSheetContentPreview() {
    Column {
        EmailSheetContent(
            modifier = Modifier
                .padding(top = 7.dp, bottom = 25.dp, start = 16.dp, end = 16.dp),
            onVerifyClicked = {},
            onChangeEmailClick = {},
            onOtpTextChange = { _, _ ->

            },
            isFromReset = false,
            onResendCodeClick = { },
            email = "abc@gmail.com",
            otpValue = "",
            seconds = { 20 },
            errorMsg = null
        )
    }
}

private fun maskEmail(email: String): String {
    val atIndex = email.indexOf('@')
    return if (atIndex > 3) {
        val prefix = email.substring(0, 3)
        val domain = email.substring(atIndex)
        "$prefix****$domain"
    } else {
        email // Return the original email if it's too short to mask
    }
}