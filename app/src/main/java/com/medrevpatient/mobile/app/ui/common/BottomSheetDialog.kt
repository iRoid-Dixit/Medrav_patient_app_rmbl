package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifiedBottomSheet(
    sheetState: () -> SheetState,
    onDismissRequest: () -> Unit,
    isSheetVisible: () -> Boolean,
    modifier: Modifier = Modifier,
    onClickOfGetStarted: () -> Unit,
    windowInsets: WindowInsets = BottomSheetDefaults.windowInsets,
    isFromResetPassword: Boolean,
    isFromUpdateProfile: Boolean = false,
    titleText: String = ""
) {

    val focus = LocalFocusManager.current

    if (isSheetVisible()) {
        focus.clearFocus()
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState(),
            contentWindowInsets = { windowInsets },
            modifier = modifier.then(Modifier.navigationBarsPadding()),
            containerColor = white,
            dragHandle = null
        ) {
            // Sheet content
            if (isFromResetPassword) {
                ResetPasswordSuccessSheetContent(
                    modifier = Modifier.padding(
                        top = 7.dp,
                        bottom = 25.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    onClickOfGetStarted = onClickOfGetStarted
                )
            } else {
                if (isFromUpdateProfile) {
                    ProfileUpdateSuccessSheetContent(
                        modifier = Modifier.padding(
                            top = 7.dp,
                            bottom = 25.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        titleText = titleText,
                        onClickOfGetStarted = onClickOfGetStarted,
                    )
                } else {
                    EmailVerifiedSheetContent(
                        modifier = Modifier.padding(
                            top = 7.dp,
                            bottom = 25.dp,
                            start = 16.dp,
                            end = 16.dp
                        ),
                        onClickOfGetStarted = {
                            onClickOfGetStarted()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmailVerifiedSheetContent(
    modifier: Modifier = Modifier,
    onClickOfGetStarted: () -> Unit
) {

    Column(
        modifier = modifier.padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
        )

        Image(
            painter = painterResource(R.drawable.ic_email_verified), contentDescription = "",
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.congratulations),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(
            text = stringResource(R.string.email_has_been_verified),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
        )

        BottomButtonComponent(
            onClick = {
                onClickOfGetStarted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            text = stringResource(R.string.get_started).uppercase(),
        )
    }
}

@Composable
fun ResetPasswordSuccessSheetContent(
    modifier: Modifier = Modifier,
    onClickOfGetStarted: () -> Unit
) {

    Column(
        modifier = modifier.padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
        )

        Image(
            painter = painterResource(R.drawable.ic_email_verified),
            contentDescription = "",
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.great),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(
            text = stringResource(R.string.reset_successful),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
        )

        BottomButtonComponent(
            onClick = {
                onClickOfGetStarted()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            text = stringResource(R.string.continue_txt).uppercase(),
        )
    }
}

@Composable
fun ProfileUpdateSuccessSheetContent(
    modifier: Modifier = Modifier,
    onClickOfGetStarted: () -> Unit,
    titleText: String = "",
) {

    Column(
        modifier = modifier.padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.ic_top_handle),
            contentDescription = "",
        )

        Image(
            painter = painterResource(R.drawable.ic_email_verified),
            contentDescription = "",
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.great),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 30.dp)
        )

        Text(
            text = String.format(stringResource(R.string.update_profile_success_msg), titleText),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 14.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.6f)
        )

        SkaiButton(
            text = stringResource(R.string.go_back),
            innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
            makeUpperCase = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .padding(horizontal = 70.dp),
            textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
        ) {
            onClickOfGetStarted()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailSheetContentPreview() {
    Column {
        EmailVerifiedSheetContent(
            modifier = Modifier.padding(top = 7.dp, bottom = 25.dp, start = 16.dp, end = 16.dp),
            onClickOfGetStarted = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetSuccessSheetContentPreview() {
    Column {
        ResetPasswordSuccessSheetContent(
            modifier = Modifier.padding(top = 7.dp, bottom = 25.dp, start = 16.dp, end = 16.dp),
            onClickOfGetStarted = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UpdateSuccessSheetContentPreview() {
    Column {
        ProfileUpdateSuccessSheetContent(
            modifier = Modifier.padding(top = 7.dp, bottom = 25.dp, start = 16.dp, end = 16.dp),
            onClickOfGetStarted = {}
        )
    }
}

