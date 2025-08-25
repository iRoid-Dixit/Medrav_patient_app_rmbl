package com.medrevpatient.mobile.app.ux.startup.auth.register

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import com.medrevpatient.mobile.app.ui.compose.common.DatePickerWithDialog
import com.medrevpatient.mobile.app.ui.compose.common.DateSelectComponent
import com.medrevpatient.mobile.app.ui.compose.common.DropdownField
import com.medrevpatient.mobile.app.ui.compose.common.LogInSignInNavText
import com.medrevpatient.mobile.app.ui.compose.common.TermsAndConditionsText
import com.medrevpatient.mobile.app.ui.compose.common.countryCode.CountryCodePickerComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState = viewModel.registerUiState
    val registerUiState by uiState.registerUiDataState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    uiState.event(RegisterUiEvent.GetContext(context))
    AppScaffold(containerColor = AppThemeColor) {
        RegisterScreenContent(event = uiState.event, registerUiState)
    }
    if (registerUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun RegisterScreenContent(
    event: (RegisterUiEvent) -> Unit,
    registerUiState: RegisterUiDataState?
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .background(color = AppThemeColor)
            .fillMaxSize()
            .clickable {
                keyboardController?.hide()
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.create_new_account),
            fontFamily = WorkSans,
            fontWeight = FontWeight.W500,
            color = White,
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.create_new_account_and_get_started_today),
            fontFamily = WorkSans,
            fontWeight = FontWeight.W400,
            color = White,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(40.dp))
        RegisterInputField(registerUiState, event)
        Spacer(modifier = Modifier.height(20.dp))
        TermsAndConditionsText(onTermsAndConditionsClick = {
            event(RegisterUiEvent.TermAndCondition)

        }, onPrivacyPolicyClick = {
            event(RegisterUiEvent.PrivacyPolicy)
        },
            onCheckChange = { checked ->
                event(RegisterUiEvent.OnCheckedChange(checked))
            },
            errorMessage = registerUiState?.isTermAndConditionCheckedErrorMsg
        )
        Spacer(modifier = Modifier.height(18.dp))
        AppButtonComponent(
            onClick = {
                event(RegisterUiEvent.DoSignUp)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.sign_in),
        )
        Spacer(modifier = Modifier.height(60.dp))
        LogInSignInNavText(
            message = stringResource(R.string.create_new_account),
            actionText = stringResource(id = R.string.sign_in),
            onClick = {
                event(RegisterUiEvent.DoSignIn)
            },
        )
    }
}

@Composable
fun RegisterInputField(registerUiState: RegisterUiDataState?, event: (RegisterUiEvent) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var temp by rememberSaveable { mutableStateOf<Long?>(null) }
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        AppInputTextField(
            value = registerUiState?.name ?: "",
            onValueChange = { event(RegisterUiEvent.NameValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Sentences,
            ),
            errorMessage = registerUiState?.nameErrorMsg,
            header = stringResource(R.string.full_name),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = registerUiState?.email ?: "",
            onValueChange = { event(RegisterUiEvent.EmailValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            errorMessage = registerUiState?.emailErrorMsg,
            header = stringResource(id = R.string.email),
            leadingIcon = R.drawable.ic_app_icon,
        )
        CountryCodePickerComponent(
            value = registerUiState?.phoneNumber ?: "",
            onValueChange = { newValue ->
                val filteredValue = newValue.filter { it.isDigit() }
                event(RegisterUiEvent.PhoneNumberValueChange(filteredValue))
            },

            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Next
            ),
            errorMessage = registerUiState?.phoneNumberErrorMsg,
            header = stringResource(R.string.mobile_number),
            setCountryCode = registerUiState?.defaultCountryCode
        )

        DateSelectComponent(
            value = registerUiState?.dateSelected ?: "",
            header = "Date of Birth",
            trailingIcon = R.drawable.ic_app_icon,
            errorMessage = registerUiState?.dateOfBirthValidationMsg,
            onClick = {
                showDatePickerDialog = true
            },
        )
        DropdownField(
            list = listOf(
                stringResource(R.string.male),
                stringResource(R.string.female),
                stringResource(R.string.non_binary)
            ),
            expanded = expanded,
            selectedRole = registerUiState?.selectGender ?: "",
            onRoleDropDownExpanded = {
                expanded = it
            },
            errorMessage = registerUiState?.selectGanderErrorMsg,
            onUserRoleValue = { event(RegisterUiEvent.RoleDropDownExpanded(it))},
        )
        AppInputTextField(
            value = registerUiState?.password ?: "",
            onValueChange = { event(RegisterUiEvent.PasswordValueChange(it)) },
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            errorMessage = registerUiState?.passwordErrorMsg,
            isTrailingIconVisible = true,
            trailingIcon = if (passwordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = stringResource(id = R.string.password),
            leadingIcon = R.drawable.ic_app_icon,
        )
        AppInputTextField(
            value = registerUiState?.confirmPassword ?: "",
            onValueChange = { event(RegisterUiEvent.ConfirmPasswordValueChange(it)) },
            isLeadingIconVisible = true,
            isTrailingIconVisible = true,
            errorMessage = registerUiState?.confirmPasswordErrorMsg,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = if (confirmPasswordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            onTogglePasswordVisibility = { confirmPasswordVisible = !confirmPasswordVisible },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            header = stringResource(R.string.confirm_password),
            leadingIcon = R.drawable.ic_app_icon,
        )
    }
    if (showDatePickerDialog) {
        DatePickerWithDialog(
            onSelectedDate = temp,
            onDateSelected = { dateString ->
                event(RegisterUiEvent.OnClickOfDate(dateString))
                Log.d("TAG", "RegisterInputField: $dateString")
            },
            onDismiss = {
                showDatePickerDialog = false
            },
            onDateSelectedLong = {
                temp = it
            }
        )
    }
}

@Preview
@Composable
private fun RegisterScreenContentPreview() {
    val registerUiDataState = RegisterUiDataState()

    Surface {
        RegisterScreenContent(event = {}, registerUiState = registerUiDataState)
    }
}