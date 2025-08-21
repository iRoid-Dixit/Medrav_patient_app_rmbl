package com.medrevpatient.mobile.app.ux.main.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsDialog(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowAccountSettingsDialog(false)) },
            isSheetVisible = { uiState.showAccountSettingsDialog },
            title = stringResource(R.string.account_settings)
        ) {
            AccountSettingsContent(modifier, event)
        }
    }
}

@Composable
fun AccountSettingsContent(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit) {
    VStack(
        spaceBy = 0.dp, modifier = modifier
            .padding(20.dp)
            .background(white)
    ) {
        IconTextHStack(
            icon = R.drawable.logout,
            text = stringResource(R.string.log_out),
            spaceBy = 10.dp,
            tint = MineShaft,
            style = TextStyle(
                fontWeight = Medium,
                fontSize = 14.sp,
                color = MineShaft
            ),
            iconSize = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    event(ProfileUiEvent.ShowAccountSettingsDialog(false))
                    event(ProfileUiEvent.ShowLogoutDialog(true))
                }
        )
        Spacer(modifier = Modifier.padding(top = 20.dp))
        IconTextHStack(
            icon = R.drawable.delete_account,
            text = stringResource(R.string.delete_account),
            spaceBy = 10.dp,
            tint = MineShaft,
            style = TextStyle(
                fontWeight = Medium,
                fontSize = 14.sp,
                color = MineShaft
            ),
            iconSize = 24.dp,
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable {
                    event(ProfileUiEvent.ShowAccountSettingsDialog(false))
                    event(ProfileUiEvent.ShowDeleteAccountDialog(true))
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountDialog(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowDeleteAccountDialog(false)) },
            isSheetVisible = { uiState.showDeleteAccountDialog }
        ) {
            DeleteAccountContent(modifier, event)
        }
    }
}

@Preview
@Composable
fun DeleteAccountContent(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit = {}) {
    VStack(
        spaceBy = 0.dp, modifier = modifier
            .padding(20.dp)
            .background(white)
    ) {
        Image(
            painter = painterResource(R.drawable.delete_account_1), contentDescription = "",
            modifier = Modifier
                .padding(top = 20.dp)
                .size(70.dp)
        )

        Text(
            text = stringResource(R.string.are_you_sure_you_want_to_delete),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        Text(
            text = stringResource(R.string.by_deleting),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )
        Spacer(modifier = Modifier.padding(top = 40.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.no),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = {
                    event(ProfileUiEvent.ShowDeleteAccountDialog(false))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.yes),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                event(ProfileUiEvent.PerformDeleteAccountClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountLogoutDialog(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit, uiState: ProfileUiState) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowLogoutDialog(false)) },
            isSheetVisible = { uiState.showLogoutDialog }
        ) {
            AccountLogoutContent(modifier, event)
        }
    }
}

@Composable
fun AccountLogoutContent(modifier: Modifier = Modifier, event: (ProfileUiEvent) -> Unit) {
    VStack(
        spaceBy = 0.dp, modifier = modifier
            .padding(20.dp)
            .background(white)
    ) {
        Image(
            painter = painterResource(R.drawable.logout_1), contentDescription = "",
            modifier = Modifier
                .padding(top = 20.dp)
                .size(70.dp)
        )

        Text(
            text = stringResource(R.string.are_you_sure_or_logout),
            fontFamily = outFit,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        /*Text(
            text = stringResource(R.string.you_need_to_login_again),
            fontFamily = outFit,
            fontWeight = FontWeight.Light,
            fontSize = 16.sp,
            color = MineShaft,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
        )*/
        Spacer(modifier = Modifier.padding(top = 40.dp))
        HStack(8.dp, modifier = Modifier.padding(horizontal = 0.dp)) {
            SkaiButton(
                text = stringResource(R.string.no),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = {
                    event(ProfileUiEvent.ShowLogoutDialog(false))
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.yes),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) {
                event(ProfileUiEvent.PerformLogoutClick)
            }
        }
    }
}

