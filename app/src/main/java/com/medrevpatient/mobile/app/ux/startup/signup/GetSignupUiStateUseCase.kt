package com.medrevpatient.mobile.app.ux.startup.signup

import android.content.Context
import android.widget.Toast
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.local.datastore.LocalManager
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.request.SendOTPReq
import com.medrevpatient.mobile.app.domain.request.SignUpReq
import com.medrevpatient.mobile.app.domain.request.VerifyOTPReq
import com.medrevpatient.mobile.app.domain.response.AuthResponse
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import es.dmoral.toasty.Toasty
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
import javax.inject.Inject

class GetSignupUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val localManager: LocalManager,
    private val networkMonitor: NetworkMonitor,
) {

    val _state = MutableStateFlow(SignupUiStateValues())
    private val otpUiState = MutableStateFlow(OtpUiState())
    private val isEmailVerified = MutableStateFlow(true)
    private val showVerificationBottomSheet = MutableStateFlow(false)
    private val showVerifiedBottomSheet = MutableStateFlow(false)
    private val isOffline = MutableStateFlow(false)
    private val counter = MutableStateFlow(60)
    private val genderValue = MutableStateFlow(1)
    private val termsConditionValue = MutableStateFlow(0)
    private var job: Job? = null

    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SignupUiState {

        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return SignupUiState(
            isEmailVerified = isEmailVerified,
            showVerificationBottomSheet = showVerificationBottomSheet,
            showVerifiedBottomSheet = showVerifiedBottomSheet,
            signUpUiStateFlow = _state,
            counter = counter,
            genderValue = genderValue,
            state = _state,
            otpUiState = otpUiState,
            event = { authUiEvent ->
                signupEvent(
                    context = context,
                    event = authUiEvent,
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }
        )
    }

    private fun signupEvent(
        context: Context,
        event: SignupUiEvent,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        when (event) {
            is SignupUiEvent.EmailValueChange -> {
                _state.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(event.email).errorMsg
                    )
                }
            }

            is SignupUiEvent.FirstNameValueChange -> {
                _state.update { state ->
                    state.copy(
                        firstName = event.firstName,
                        firstNameErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.firstName,
                            errorMsg = context.getString(R.string.empty_first_name)
                        ).errorMsg
                    )
                }
            }

            is SignupUiEvent.LastNameValueChange -> {
                _state.update { state ->
                    state.copy(
                        lastName = event.lastname,
                        lastNameErrorMsg = validationUseCase.emptyFieldValidation(
                            fieldValue = event.lastname,
                            errorMsg = context.getString(R.string.empty_last_name)
                        ).errorMsg
                    )
                }
            }

            is SignupUiEvent.PasswordValueChange -> {
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

            is SignupUiEvent.ConfirmPasswordValueChange -> {
                _state.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        passwordNotMatch = if (validationUseCase.emptyFieldValidation(
                                fieldValue = event.confirmPassword,
                                errorMsg = context.getString(R.string.empty_confirm_password)
                            ).isSuccess
                        ) {
                            if (state.password != event.confirmPassword) {
                                context.getString(R.string.password_mismatch)
                            } else {
                                null
                            }
                        } else {
                            context.getString(R.string.empty_confirm_password)
                        }
                    )
                }
            }

            is SignupUiEvent.MoveToLoginScreen -> {
                navigate(NavigationAction.PopAndNavigate(RouteMaker.SignInRoute.createRoute()))
            }

            is SignupUiEvent.OpenOrCloseOTPDialog -> {
                showVerificationBottomSheet.value = event.isOpen
            }

            is SignupUiEvent.OpenOrCloseVerifiedDialog -> {
                showVerifiedBottomSheet.value = event.isOpen
            }

            is SignupUiEvent.OnOTPValueInsert -> {
                otpUiState.update { state ->
                    state.copy(
                        otp = event.value,
                        errorMsg = null
                    )
                }
            }

            is SignupUiEvent.PerformVerifyOtp -> {
                //call verify otp API
                if (!isOffline.value) {
                    verifyOTP(coroutineScope = coroutineScope, context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignupUiEvent.PerformResendOTP -> {
                if (!isOffline.value) {
                    sendOTP(coroutineScope = coroutineScope, context = context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignupUiEvent.PerformSignUp -> {
                //call register API
                if (!isOffline.value) {
                    callSignUpApi(coroutineScope = coroutineScope, context = context)
                } else {
                    AppUtils.Toast(context, context.getString(R.string.internet_connection)).show()
                }
            }

            is SignupUiEvent.PerformGetStarted -> {
                //redirect to on board screen
                getStarted(navigate)
            }

            is SignupUiEvent.GenderClick -> {
                if (event.gender == "Male") {
                    genderValue.value = 1
                } else {
                    genderValue.value = 2
                }
            }

            is SignupUiEvent.TermsConditionClick -> {
                termsConditionValue.value = event.value
            }
        }
    }

    private fun callSignUpApi(coroutineScope: CoroutineScope, context: Context) {
        val signupUIState = _state.value
        validationUseCase.apply {
            val email = emailValidation(signupUIState.email)
            val firstName = emptyFieldValidation(
                fieldValue = signupUIState.firstName,
                errorMsg = context.getString(R.string.empty_first_name)
            )
            val lastName = emptyFieldValidation(
                fieldValue = signupUIState.lastName,
                errorMsg = context.getString(R.string.empty_last_name)
            )
            val password = emptyFieldValidation(
                fieldValue = signupUIState.password,
                errorMsg = context.getString(R.string.empty_password)
            )
            val confirmPassword = emptyFieldValidation(
                fieldValue = signupUIState.confirmPassword,
                errorMsg = context.getString(R.string.empty_confirm_password)
            )

            val passwordNotMatch = passwordMatchValidation(
                password = signupUIState.password,
                confirmPassword = signupUIState.confirmPassword
            )
            val termsCondition = ValidationResult(
                isSuccess = termsConditionValue.value == 1,
                errorMsg = context.getString(R.string.terms_condition_error)
            )

            val hasError = listOf(
                firstName,
                lastName,
                email,
                password,
                confirmPassword,
                passwordNotMatch
            ).any {
                !it.isSuccess
            }

            if (hasError) {
                _state.update { state ->
                    state.copy(
                        firstNameErrorMsg = firstName.errorMsg,
                        lastNameErrorMsg = lastName.errorMsg,
                        emailErrorMsg = email.errorMsg,
                        passwordErrorMsg = password.errorMsg,
                        passwordNotMatch = if (confirmPassword.isSuccess) {
                            passwordNotMatch.errorMsg
                        } else {
                            confirmPassword.errorMsg
                        }
                    )
                }
                return
            }

            if (termsCondition.isSuccess.not()) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toasty.warning(context, termsCondition.errorMsg.toString(), Toast.LENGTH_SHORT, true).show()
                }
                return
            }
        }

        coroutineScope.launch {
            //Sign up API call
            val signUpReq = SignUpReq(
                firstName = signupUIState.firstName,
                lastName = signupUIState.lastName,
                email = signupUIState.email,
                password = signupUIState.password,
                confirmPassword = signupUIState.confirmPassword,
                gender = genderValue.value
            )

            apiRepository.signUp(signUpReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideSignUpLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideSignUpLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideSignUpLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        showVerificationBottomSheet.value = true
                        callCounter(coroutineScope = coroutineScope)
                    }
                }
            }
        }
    }

    private fun showOrHideSignUpLoader(isLoading: Boolean) {
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

    private fun verifyOTP(coroutineScope: CoroutineScope, context: Context) {
        val otpState = otpUiState.value
        validationUseCase.apply {
            val otpHasError = emptyFieldValidation(otpState.otp, context.getString(R.string.empty_otp))
            if (!otpHasError.isSuccess) {
                otpUiState.update { state ->
                    state.copy(errorMsg = otpHasError.errorMsg)
                }
                return
            }
        }

        coroutineScope.launch {
            val verifyOTPReq = VerifyOTPReq(
                email = _state.value.email,
                otp = otpState.otp.toInt(),
                verifyFor = 1
            )

            apiRepository.verifyOTP(verifyOTPReq = verifyOTPReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideSignUpLoader(false)
                        otpUiState.update { state ->
                            state.copy(
                                errorMsg = it.message ?: "Something went wrong!",
                                otp = ""
                            )
                        }
                    }

                    is NetworkResult.Loading -> {
                        showOrHideSignUpLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideSignUpLoader(false)
                        AppUtils.Toast(context, it.data?.message ?: "Success").show()
                        storeUserData(
                            coroutineScope = coroutineScope,
                            userAuthResponse = it.data?.data
                        )
                    }
                }
            }
        }
    }

    private fun storeUserData(coroutineScope: CoroutineScope, userAuthResponse: AuthResponse?) {
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
        }
    }

    private fun getStarted(navigate: (NavigationAction) -> Unit) {
        showVerifiedBottomSheet.value = false
        showVerificationBottomSheet.value = false
        navigate(NavigationAction.PopAndNavigate(RouteMaker.SubsRoute.createRoute()))
    }

    private fun sendOTP(coroutineScope: CoroutineScope, context: Context) {
        coroutineScope.launch {
            val sendOTPReq = SendOTPReq(
                email = _state.value.email,
                otpFor = 1
            )

            apiRepository.sendOTP(sendOTPReq).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showOrHideSignUpLoader(false)
                        AppUtils.Toast(context, it.message ?: "Something went wrong!").show()
                    }

                    is NetworkResult.Loading -> {
                        showOrHideSignUpLoader(true)
                    }

                    is NetworkResult.Success -> {
                        showOrHideSignUpLoader(false)
                        val msg: String = it.data?.message ?: "OTP send to your register email!"
                        AppUtils.Toast(context, msg).show()
                        callCounter(coroutineScope = coroutineScope)
                    }
                }
            }
        }
    }
}
