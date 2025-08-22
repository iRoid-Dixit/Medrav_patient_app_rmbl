package com.medrevpatient.mobile.app.ux.container.contactUs

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextFieldMultipleLine
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor

@ExperimentalMaterial3Api
@Composable
fun ContactUsScreen(
    navController: NavController,
    viewModel: ContactUsViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val changePasswordUiState by uiState.contactUsDataFlow.collectAsStateWithLifecycle()
    uiState.event(ContactUsUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                header = "Contact Us",
                isBackVisible = true,
                isLineVisible = true,
                onClick = {
                    uiState.event(ContactUsUiEvent.BackClick)
                },

                )
        },
        navBarData = null
    ) {
        ContactUsScreenContent(uiState, uiState.event)
    }
    if (changePasswordUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun ContactUsScreenContent(
    uiState: ContactUsUiState,
    event: (ContactUsUiEvent) -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val contactUsUiState by uiState.contactUsDataFlow.collectAsStateWithLifecycle()
    Column(

        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable {
                keyboardController?.hide()
            }
            .fillMaxSize(),
    ) {

        Spacer(modifier = Modifier.height(10.dp))
        AppInputTextField(
            value = contactUsUiState?.fullName ?: "",
            onValueChange = { event(ContactUsUiEvent.FullNameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),

            errorMessage = contactUsUiState?.fullNameErrorMsg,
            header = stringResource(R.string.full_name),
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(20.dp))

        AppInputTextFieldMultipleLine(
            value = contactUsUiState?.message ?: "",
            errorMessage = contactUsUiState?.messageErrorMsg,
            onValueChange = { event(ContactUsUiEvent.MessageValueChange(it)) },
            header = stringResource(R.string.enter_your_message_here),
        )
        Spacer(modifier = Modifier.height(25.dp))
        AppButtonComponent(
            onClick = {
                event(ContactUsUiEvent.ContactUsClick)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.submit),

            )

    }
}


@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = ContactUsUiState()
    ContactUsScreenContent(uiState = uiState, event = uiState.event)

}






