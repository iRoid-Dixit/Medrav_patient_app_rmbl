package com.medrevpatient.mobile.app.ux.startup.auth.signin

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.LoginReq
import com.medrevpatient.mobile.app.domain.request.ResetPasswordReq
import com.medrevpatient.mobile.app.domain.request.SendOTPReq
import com.medrevpatient.mobile.app.domain.request.VerifyOTPReq
import com.medrevpatient.mobile.app.domain.response.Auth
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSignInUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager,
    private val networkMonitor: NetworkMonitor
) {

    val _state = MutableStateFlow(SignInUiStateValues())
    private val isOffline = MutableStateFlow(false)
    private val showVerificationBottomSheet = MutableStateFlow(false)
    private val showVerifiedBottomSheet = MutableStateFlow(false)
    private val showResetEmailBottomSheet = MutableStateFlow(false)
    private val showNewPasswordBottomSheet = MutableStateFlow(false)
    private val counter = MutableStateFlow(60)
    private val otpUiState = MutableStateFlow(OtpUiState())
    private val resetPasswordUiState = MutableStateFlow(ResetPasswordUiState())
    private val isFromResetPassword = MutableStateFlow(false)
    private var job: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SignInUiState {

        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return SignInUiState(
            showVerificationBottomSheet = showVerificationBottomSheet,
            showVerifiedBottomSheet = showVerifiedBottomSheet,
            showResetEmailBottomSheet = showResetEmailBottomSheet,
            showNewPasswordBottomSheet = showNewPasswordBottomSheet,
            resetPasswordUiState = resetPasswordUiState,
            counter = counter,
            isFromResetPassword = isFromResetPassword,
            otpUiState = otpUiState,
            state = _state,
            event = { authUiEvent ->
                authEvent(
                    context = context,
                    event = authUiEvent,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun authEvent(
        context: Context,
        event: SignInUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is SignInUiEvent.EmailValueChange -> {
                _state.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(event.email).errorMsg
                    )
                }
            }

            is SignInUiEvent.ResetEmailValueChange -> {
                resetPasswordUiState.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(event.email).errorMsg
                    )
                }
            }

            is SignInUiEvent.PasswordValueChange -> {
                _state.update { state ->
                    state.copy(
                        password = event.password,
                        passwordErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.password,
                            errorMsg = context.getString(R.string.empty_password)
                        ).errorMsg
                    )
                }
            }

            is SignInUiEvent.ResetPasswordValueChange -> {
                resetPasswordUiState.update { state ->
                    state.copy(
                        password = event.password,
                        passwordErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.password,
                            errorMsg = context.getString(R.string.empty_password)
                        ).errorMsg
                    )
                }
            }

            is SignInUiEvent.ResetConfirmPasswordValueChange -> {
                resetPasswordUiState.update { state ->
                    state.copy(
                        confirmPassword = event.password,
                        confirmPasswordErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.password,
                            errorMsg = context.getString(R.string.empty_password)
                        ).errorMsg
                    )
                }
            }

            is SignInUiEvent.MoveToSignUpScreen -> {
                navigate(NavigationAction.Navigate(RouteMaker.SignupRoute.createRoute()))
            }

            is SignInUiEvent.MoveToForgotPasswordScreen -> {
                isFromResetPassword.value = true
                showResetEmailBottomSheet.value = true
            }

            is SignInUiEvent.PerformLogin -> {
                if (!isOffline.value) {
                    callLoginApi(coroutineScope = coroutineScope, context = context, navigate)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignInUiEvent.OpenOrCloseOTPDialog -> {
                showVerificationBottomSheet.value = event.isOpen
            }

            is SignInUiEvent.OpenOrCloseVerifiedDialog -> {
                showVerifiedBottomSheet.value = event.isOpen
            }

            is SignInUiEvent.OpenOrCloseResetEmailDialog -> {
                showResetEmailBottomSheet.value = event.isOpen
            }

            is SignInUiEvent.OpenOrCloseNewPasswordDialog -> {
                showNewPasswordBottomSheet.value = event.isOpen
            }

            is SignInUiEvent.OnOTPValueInsert -> {
                otpUiState.update { state ->
                    state.copy(
                        otp = event.value,
                        errorMsg = null
                    )
                }
            }

            is SignInUiEvent.PerformVerifyOtp -> {
                //call verify otp API
                if (!isOffline.value) {
                    if (isFromResetPassword.value) {
                        verifyOTP(coroutineScope = coroutineScope, context, 2, navigate)
                    } else {
                        verifyOTP(coroutineScope = coroutineScope, context, 1, navigate)
                    }
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignInUiEvent.PerformResendOTP -> {
                if (!isOffline.value) {
                    sendOTP(coroutineScope = coroutineScope, context = context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignInUiEvent.PerformGetStarted -> {
                if (isFromResetPassword.value) {
                    isFromResetPassword.value = false
                    showVerifiedBottomSheet.value = false
                } else {
                    getStarted(navigate)
                }
            }

            is SignInUiEvent.PerformSetNewPassword -> {
                if (!isOffline.value) {
                    callResetPasswordApi(coroutineScope = coroutineScope, context = context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }
        }
    }

    private fun callLoginApi(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        val signInUIState = _state.value
        validationUseCase.apply {
            val email = emailValidation(signInUIState.email)
            val password = emptyFieldValidation(
                fieldValue = signInUIState.password,
                errorMsg = context.getString(R.string.empty_password)
            )

            val hasError = listOf(
                email,
                password
            ).any {
                !it.isSuccess
            }

            if (hasError) {
                _state.update { state ->
                    state.copy(
                        emailErrorMsg = email.errorMsg,
                        passwordErrorMsg = password.errorMsg
                    )
                }
                return
            }
        }

        coroutineScope.launch {
            //login API call
            val signInReq = LoginReq(
                email = signInUIState.email,
                password = signInUIState.password,
            )

            apiRepository.login(signInReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        if (it.data?.data?.isVerified == false) {
                            _state.update { state ->
                                state.copy(
                                    email = signInUIState.email
                                )
                            }
                            isFromResetPassword.value = false
                            showVerificationBottomSheet.value = true
                            callCounter(coroutineScope = coroutineScope)
                        } else {
                            //redirect to Home, need to change afterwards
                            storeUserData(
                                coroutineScope = coroutineScope,
                                userAuthResponse = it.data?.data,
                                context = context,
                                navigate = navigate,
                                isNavigateToHome = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showOrHideLoader(isLoading: Boolean) {
        _state.update { state ->
            state.copy(
                isLoading = isLoading
            )
        }
    }

    private fun callCounter(coroutineScope: CoroutineScope) {
        job?.cancel()
        counter.value = 60
        job = coroutineScope.launch {
            decreaseCounter()
        }
    }

    private suspend fun decreaseCounter() {
        while (counter.value > 0) {
            delay(1000)
            counter.value -= 1
        }
    }

    private fun verifyOTP(coroutineScope: CoroutineScope, context: Context, otpType: Int, navigate: (NavigationAction) -> Unit) {
        val otpState = otpUiState.value
        validationUseCase.apply {
            val otpHasError =
                emptyFieldValidation(otpState.otp, context.getString(R.string.empty_otp))
            if (!otpHasError.isSuccess) {
                otpUiState.update { state ->
                    state.copy(errorMsg = otpHasError.errorMsg)
                }
                return
            }
        }

        coroutineScope.launch {
            val verifyOTPReq = VerifyOTPReq(
                email = if (otpType == 1) _state.value.email else resetPasswordUiState.value.email,
                otp = otpState.otp.toInt(),
                verifyFor = otpType
            )

            apiRepository.verifyOTP(verifyOTPReq = verifyOTPReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        otpUiState.update { state ->
                            state.copy(
                                errorMsg = it.message ?: "Something went wrong!",
                                otp = ""
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        if (otpType == 1) {
                            storeUserData(
                                coroutineScope = coroutineScope,
                                userAuthResponse = it.data?.data,
                                context = context,
                                navigate = navigate,
                                isNavigateToHome = false
                            )
                        } else {
                            storeUserToken(
                                coroutineScope = coroutineScope,
                                token = it.data?.data?.auth
                            )
                        }
                    }
                }
            }
        }
    }

    private fun storeUserData(coroutineScope: CoroutineScope, userAuthResponse: AuthResponse?, context: Context, navigate: (NavigationAction) -> Unit, isNavigateToHome: Boolean) {
        coroutineScope.launch {
            userAuthResponse?.let { data ->
                val tokenData = data.auth
                localManager.saveUserData(data)
                if (tokenData != null) {
                    localManager.saveUserTokenData(tokenData)
                }
            }
            showVerificationBottomSheet.value = false
            showVerifiedBottomSheet.value = true
            if (isNavigateToHome) {
                val intent = Intent(context, MainActivity::class.java)
                navigate(NavigationAction.NavigateIntent(intent = intent, finishCurrentActivity = true))
            }
        }
    }

    private fun storeUserToken(coroutineScope: CoroutineScope, token: Auth?) {
        coroutineScope.launch {
            token?.let { data ->
                localManager.saveUserTokenData(data)
            }
            showNewPasswordBottomSheet.value = true
            showVerificationBottomSheet.value = false
        }
    }

    private fun getStarted(navigate: (NavigationAction) -> Unit) {
        showVerifiedBottomSheet.value = false
        showVerificationBottomSheet.value = false
        navigate(NavigationAction.PopAndNavigate(RouteMaker.SubsRoute.createRoute()))
    }

    private fun sendOTP(coroutineScope: CoroutineScope, context: Context) {
        validationUseCase.apply {
            val email =
                emailValidation(if (isFromResetPassword.value) resetPasswordUiState.value.email else _state.value.email)
            val hasError = listOf(
                email
            ).any {
                !it.isSuccess
            }

            if (hasError) {
                if (isFromResetPassword.value) {
                    resetPasswordUiState.update { state ->
                        state.copy(
                            emailErrorMsg = email.errorMsg
                        )
                    }
                } else {
                    _state.update { state ->
                        state.copy(
                            emailErrorMsg = email.errorMsg
                        )
                    }
                }
                return
            }
        }
        coroutineScope.launch {
            val sendOTPReq = SendOTPReq(
                email = if (isFromResetPassword.value) resetPasswordUiState.value.email else _state.value.email,
                otpFor = if (isFromResetPassword.value) 2 else 1
            )

            apiRepository.sendOTP(sendOTPReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val msg: String = it.data?.message ?: "OTP send to your register email!"
                        AppUtils.Toast(context, msg).show()
                        showResetEmailBottomSheet.value = false
                        showVerificationBottomSheet.value = true
                        callCounter(coroutineScope = coroutineScope)
                    }
                }
            }
        }
    }

    private fun callResetPasswordApi(coroutineScope: CoroutineScope, context: Context) {
        validationUseCase.apply {
            val password = emptyFieldValidation(
                fieldValue = resetPasswordUiState.value.password,
                errorMsg = context.getString(R.string.empty_password)
            )

            val confirmPassword = emptyFieldValidation(
                fieldValue = resetPasswordUiState.value.confirmPassword,
                errorMsg = context.getString(R.string.empty_password)
            )

            val passwordMatch = passwordMatchValidation(
                resetPasswordUiState.value.password,
                resetPasswordUiState.value.confirmPassword
            )

            val hasError = listOf(
                password,
                confirmPassword,
                passwordMatch
            ).any {
                !it.isSuccess
            }

            if (hasError) {
                resetPasswordUiState.update { state ->
                    state.copy(
                        passwordErrorMsg = password.errorMsg,
                        confirmPasswordErrorMsg = confirmPassword.errorMsg
                    )
                }
                return
            }
        }
        coroutineScope.launch {
            val resetPasswordReq = ResetPasswordReq(
                email = resetPasswordUiState.value.email,
                password = resetPasswordUiState.value.password,
                confirmPassword = resetPasswordUiState.value.confirmPassword
            )

            apiRepository.changePassword(resetPasswordReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideLoader(false)
                        val msg: String = it.data?.message ?: "Password changed successfully!"
                        AppUtils.Toast(context, msg).show()
                        showNewPasswordBottomSheet.value = false
                        showVerifiedBottomSheet.value = true
                    }
                }
            }
        }
    }
}
