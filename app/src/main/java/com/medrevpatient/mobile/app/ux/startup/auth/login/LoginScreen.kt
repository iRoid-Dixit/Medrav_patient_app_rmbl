package com.medrevpatient.mobile.app.ux.startup.auth.login
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_800
import com.medrevpatient.mobile.app.ui.theme.Gray50

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
                        message = stringResource(R.string.don_t_have_an_account),
                        actionText = stringResource(R.string.sign_up),
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
        horizontalAlignment = Alignment.CenterHorizontally,
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 22.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        ScreenTitleComponent()
        Spacer(modifier = Modifier.height(35.dp))
        
        // Email Input Field
        AppInputTextField(
            value = loginUiState?.email ?: "",
            onValueChange = { event(LoginUiEvent.EmailValueChange(it)) },
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
            onValueChange = { event(LoginUiEvent.PasswordValueChanges(it)) },
            errorMessage = loginUiState?.passwordErrorMsg,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            header = stringResource(R.string.password),
            trailingIcon = if (passwordVisible) R.drawable.ic_app_icon else R.drawable.ic_app_icon,
            leadingIcon = R.drawable.ic_app_icon,
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.forgot_your_password),
            fontFamily = WorkSans,
            color = Color(0xFF6B46C1), // Purple color matching the design
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            modifier = Modifier
                .align(alignment = Alignment.End)
                .clickable {
                    event(LoginUiEvent.ForgetPassword)
                }
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Login Button
        AppButtonComponent(
            onClick = {
                event(LoginUiEvent.DoLogin)
            },
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.sign_in),
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Divider with "or" text
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
            Text(
                text = "or",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = Color(0xFF6B7280),
                fontSize = 14.sp,
                fontFamily = WorkSans
            )
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color(0xFFE5E7EB))
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Social Login Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Google Login Button
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(White)
                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    .clickable { /* Handle Google login */ }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Google icon - you'll need to add the actual icon
                Text(
                    text = "G",
                    color = Color(0xFF4285F4),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Facebook Login Button
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(White)
                    .border(1.dp, Color(0xFFE5E7EB), CircleShape)
                    .clickable { /* Handle Facebook login */ }
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for Facebook icon - you'll need to add the actual icon
                Text(
                    text = "f",
                    color = Color(0xFF1877F2),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ScreenTitleComponent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(50.dp))
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