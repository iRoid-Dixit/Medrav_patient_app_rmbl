package com.medrevpatient.mobile.app.ux.startup.auth.login
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarData
import com.medrevpatient.mobile.app.navigation.scaffold.AppNavBarType
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.AppButtonComponent
import com.medrevpatient.mobile.app.ui.compose.common.AppInputTextField
import com.medrevpatient.mobile.app.ui.compose.common.LogInSignInNavText
import com.medrevpatient.mobile.app.ui.compose.common.OrDivider
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_800
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    uiState.event(LoginUiEvent.GetContext(context))
    val loginUiState by uiState.loginDataFlow.collectAsStateWithLifecycle()
    AppScaffold(
        containerColor = White,
        navBarData = AppNavBarData(
            appNavBarType = AppNavBarType.NAV_BAR,
            navBar = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LogInSignInNavText(
                        message = stringResource(R.string.already_have_an_account),
                        actionText = stringResource(R.string.login),
                        onClick = {
                            uiState.event(LoginUiEvent.SignUp)
                        },
                    )
                }
            },
        )
    ) {
        LoginScreeContent(uiState = uiState, event = uiState.event)
    }
    if (loginUiState?.showLoader == true) {
        CustomLoader()
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
            .clickable {
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
@Composable
private fun LoginViewContent(uiState: LoginUiState, event: (LoginUiEvent) -> Unit) {
    val loginUiState by uiState.loginDataFlow.collectAsStateWithLifecycle()
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 22.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        ScreenTitleComponent()
        Spacer(modifier = Modifier.height(35.dp))
        
        // Email Input Field
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

        // Password Input Field
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
                    event(LoginUiEvent.ForgetPassword)
                }
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        AppButtonComponent(
            onClick = {
                event(LoginUiEvent.DoLogin)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_in),
            isLoading = loginUiState?.showLoader == true,

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

    }
}

@Composable
fun ScreenTitleComponent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(60.dp))
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