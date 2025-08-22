package com.medrevpatient.mobile.app.ux.startup.auth.login

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.SignInRequest
import com.medrevpatient.mobile.app.model.domain.response.auth.Auth
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.startup.auth.forgetPassword.ForgetPasswordRoute
import com.medrevpatient.mobile.app.ux.startup.auth.register.RegisterRoute
import com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetLoginUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val networkMonitor: NetworkMonitor
) {
    private val loginDataFlow = MutableStateFlow(LoginData())
    private val isOffline = MutableStateFlow(false)
    private var appNavigate: (NavigationAction) -> Unit = {}
    private lateinit var context: Context
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LoginUiState {
        appNavigate = navigate
        coroutineScope.launch {

            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return LoginUiState(
            loginDataFlow = loginDataFlow,

            event = { authUiEvent ->
                authEvent(
                    event = authUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }

        )
    }

    private fun authEvent(
        event: LoginUiEvent,

        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            is LoginUiEvent.EmailValueChange -> {
                loginDataFlow.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(
                            emailAddress = event.email,
                            context = context
                        ).errorMsg
                    )
                }
            }

            is LoginUiEvent.GetContext -> {
                this.context = event.context
            }

            is LoginUiEvent.PasswordValueChanges -> {
                loginDataFlow.update { state ->
                    state.copy(
                        password = event.password,
                        passwordErrorMsg = validationUseCase.passwordValidation(
                            password = event.password,
                            context = context
                        ).errorMsg
                    )

                }
            }

            is LoginUiEvent.SignUp -> {
                navigate(NavigationAction.Navigate(RegisterRoute.createRoute()))

            }

            is LoginUiEvent.DoLogin -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val emailValidationResult =
                            emailValidation(loginDataFlow.value.email, context = context)
                        val passwordValidationResult =
                            passwordValidation(loginDataFlow.value.password, context = context)

                        val hasErrorEmail = !emailValidationResult.isSuccess
                        val hasErrorPassword = !passwordValidationResult.isSuccess

                        // 🔹 **Update both email and password errors in one go**
                        loginDataFlow.update { state ->
                            state.copy(
                                emailErrorMsg = emailValidationResult.errorMsg,
                                passwordErrorMsg = passwordValidationResult.errorMsg
                            )
                        }
                        if (hasErrorEmail || hasErrorPassword) {
                            return
                        }
                    }
                    doUserSignIn(
                        coroutineScope = coroutineScope,
                        navigate = navigate
                    )
                } else {
                    showWaringMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            LoginUiEvent.ForgetPassword -> {
                navigate(NavigationAction.Navigate(ForgetPasswordRoute.createRoute()))
            }
        }
    }

    private fun doUserSignIn(
        coroutineScope: CoroutineScope,

        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val signInRequest = SignInRequest(
                email = loginDataFlow.value.email,
                password = loginDataFlow.value.password

            )
            apiRepository.doLogin(signInRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)

                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        storeResponseToDataStore(
                            coroutineScope = coroutineScope,
                            navigate = navigate,
                            userAuthResponseData = it.data?.data
                        )

                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponse?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveUserData(it)
                it.auth.let { it1 -> appPreferenceDataStore.saveUserAuthData(it1 ?: Auth()) }
                navigateToNextScreen(
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        }
    }

    private fun navigateToNextScreen(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        coroutineScope.launch {
            val userData = appPreferenceDataStore.getUserData()
            if (userData != null) {
                if (userData.isVerify == true) {
                    val intent = Intent(context, MainActivity::class.java)
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = true
                        )
                    )

                } else {
                    navigate(
                        NavigationAction.Navigate(
                            VerifyOtpRoute.createRoute(
                                email = loginDataFlow.value.email,
                                screenName = Constants.AppScreen.REGISTER_SCREEN
                            )
                        )
                    )

                }
            } else {
                navigate(NavigationAction.PopAndNavigate(LoginRoute.createRoute()))
            }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        loginDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
}