package com.medrevpatient.mobile.app.ux.startup.auth.login

import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.SignInRequest
import com.medrevpatient.mobile.app.model.domain.response.auth.Auth
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.startup.auth.bmi.BmiRoute
import com.medrevpatient.mobile.app.ux.startup.auth.register.RegisterRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
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
    private lateinit var context: Context
    private var countdownJob: Job? = null

    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): LoginUiState {
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

    @OptIn(ExperimentalMaterial3Api::class)
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
                navigate(Navigate(RegisterRoute.createRoute()))

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
                        navigate(NavigationAction.Navigate(BmiRoute.createRoute()))
                    }

                } else {
                    showWaringMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }


            is LoginUiEvent.ResentSheetVisibility -> {
                loginDataFlow.update { state ->
                    state.copy(
                        resetSheetVisible = event.isVisible
                    )
                }
            }

            is LoginUiEvent.ResendValueChange -> {
                loginDataFlow.update { state ->
                    state.copy(
                        resendEmail = event.resendEmail,
                        resendEmailErrorMsg = validationUseCase.emailValidation(
                            emailAddress = event.resendEmail,
                            context = context
                        ).errorMsg

                    )
                }
            }

            LoginUiEvent.BackToLoginClick -> {

            }

            is LoginUiEvent.OtpValueChange -> {
                loginDataFlow.update { state ->
                    state.copy(
                        otpValue = event.otp,
                        otpErrorMsg = otpValidation(event.otp, context = context).errorMsg
                    )
                }
            }

            is LoginUiEvent.ResendCode -> {
                startCountdown(coroutineScope)

            }

            is LoginUiEvent.ProceedClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val emailValidationResult =
                            emailValidation(loginDataFlow.value.resendEmail, context = context)
                        val hasErrorEmail = !emailValidationResult.isSuccess
                        // 🔹 **Update both email and password errors in one go**
                        loginDataFlow.update { state ->
                            state.copy(
                                resendEmailErrorMsg = emailValidationResult.errorMsg,
                            )
                        }
                        if (hasErrorEmail) {
                            return
                        }
                        event.scope.launch {
                            event.sheetState.hide()
                            startCountdown(coroutineScope)
                        }.invokeOnCompletion {
                            loginDataFlow.update { state ->
                                state.copy(
                                    resetSheetVisible = false,
                                    emailVerificationSheetVisible = true
                                )

                            }
                        }
                    }

                } else {
                    showWaringMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is LoginUiEvent.EmailVerificationSheetVisibility -> {
                loginDataFlow.update { state ->
                    state.copy(
                        emailVerificationSheetVisible = event.isVisible
                    )
                }

            }

            is LoginUiEvent.EditEmailClick -> {
                event.scope.launch {
                    event.sheetState.hide()

                }.invokeOnCompletion {
                    loginDataFlow.update { state ->
                        state.copy(
                            resetSheetVisible = true,
                            emailVerificationSheetVisible = false
                        )

                    }
                }
            }


            is LoginUiEvent.VerifyClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val otpValidationResult = otpValidation(
                            loginDataFlow.value.otpValue,
                            context = context
                        )
                        val hasErrorOtp = !otpValidationResult.isSuccess
                        loginDataFlow.update { state ->
                            state.copy(
                                otpErrorMsg = otpValidationResult.errorMsg
                            )
                        }
                        if (hasErrorOtp) {
                            return
                        }
                        event.scope.launch {
                            event.sheetState.hide()

                        }.invokeOnCompletion {
                            loginDataFlow.update { state ->
                                state.copy(
                                    emailVerificationSheetVisible = false,
                                    setPasswordVisible = true
                                )

                            }
                        }
                    }
                } else {
                    showWaringMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }

            is LoginUiEvent.SetPasswordSheetVisibility -> {
                loginDataFlow.update { state ->
                    state.copy(
                        setPasswordVisible = event.isVisible
                    )
                }
            }

            is LoginUiEvent.ConfirmPasswordValueChange -> {
                loginDataFlow.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordErrorMsg = confirmPasswordValidation(
                            loginDataFlow.value.newPassword,
                            event.confirmPassword,
                            context
                        ).errorMsg
                    )
                }
            }
            is LoginUiEvent.NewPasswordValueChange -> {
                loginDataFlow.update { state ->
                    state.copy(
                        newPassword = event.newPassword,
                        newPasswordErrorMsg = validationUseCase.passwordValidation(
                            password = event.newPassword,
                            context = context
                        ).errorMsg
                    )
                }
            }

            is LoginUiEvent.ConfirmClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val newValidationResult =
                            passwordValidation(loginDataFlow.value.newPassword, context)
                        val confirmPasswordResult = confirmPasswordValidation(
                            loginDataFlow.value.newPassword,
                            loginDataFlow.value.confirmPassword,
                            context
                        )
                        val hasError = listOf(
                            newValidationResult,
                            confirmPasswordResult,
                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        loginDataFlow.update { state ->
                            state.copy(
                                newPasswordErrorMsg = newValidationResult.errorMsg?:"",
                                confirmPasswordErrorMsg = confirmPasswordResult.errorMsg,
                            )
                        }
                        if (hasError) return
                        event.scope.launch {
                            event.sheetState.hide()
                        }.invokeOnCompletion {
                            loginDataFlow.update { state ->
                                state.copy(
                                    successSheetVisible = true,
                                    setPasswordVisible = false
                                )

                            }
                        }
                    }

                } else {
                    showWaringMessage(context,
                        context.getString(R.string.please_check_your_internet_connection_first))
                }
            }
            is LoginUiEvent.SuccessSheetVisibility -> {
                loginDataFlow.update { state ->
                    state.copy(
                        successSheetVisible = event.isVisible
                    )
                }
            }

            is LoginUiEvent.ProceedClickSuccess -> {
                event.scope.launch {
                    event.sheetState.hide()
                }.invokeOnCompletion {
                    loginDataFlow.update { state ->
                        state.copy(
                            successSheetVisible = false,
                        )

                    }
                }
            }
        }
    }


    private fun otpValidation(otp: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !otp.isNullOrBlank() && otp.length == 4,
            errorMsg = when {
                otp.isNullOrBlank() -> context.getString(R.string.please_provide_otp_for_verification)
                otp.length != 4 -> context.getString(R.string.the_otp_filed_must_be_4_digits)
                else -> null
            }
        )
    }
    private fun confirmPasswordValidation(
        password: String,
        confirmPassword: String,
        context: Context
    ): ValidationResult {
        return ValidationResult(
            isSuccess = confirmPassword.isNotBlank() && confirmPassword == password,
            errorMsg = when {
                confirmPassword.isBlank() -> context.getString(R.string.please_confirm_your_password)
                confirmPassword != password -> context.getString(R.string.passwords_do_not_match)
                else -> null
            }
        )
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

    }
    private fun showOrHideLoader(showLoader: Boolean) {
        loginDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }

    private fun startCountdown(coroutineScope: CoroutineScope) {
        countdownJob?.cancel()
        countdownJob = coroutineScope.launch(Dispatchers.IO) {
            loginDataFlow.value = loginDataFlow.value.copy(
                isResendVisible = false
            )
            for (n in 60 downTo 0) {
                withContext(Dispatchers.Main) {
                    loginDataFlow.value = loginDataFlow.value.copy(
                        remainingTimeFlow = String.format(Locale.getDefault(), "00:%02d", n)
                    )
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                loginDataFlow.value = loginDataFlow.value.copy(
                    remainingTimeFlow = "00:00",
                    isResendVisible = true
                )
            }
        }
    }
}