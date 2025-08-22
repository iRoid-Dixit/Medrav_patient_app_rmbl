package com.medrevpatient.mobile.app.ux.startup.auth.verifyOtp
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.local.datastore.AppPreferenceDataStore
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.SendOTPReq
import com.medrevpatient.mobile.app.model.domain.request.authReq.VerifyOTPReq
import com.medrevpatient.mobile.app.model.domain.response.auth.UserAuthResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.startup.auth.resetPassword.ResetPasswordRoute
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

class GetVerifyUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val appPreferenceDataStore: AppPreferenceDataStore,
    private val networkMonitor: NetworkMonitor
) {
    private val isOffline = MutableStateFlow(false)
    private val verifyOtpUiDataState = MutableStateFlow(VerifyOtpUiDataState())
    private lateinit var context: Context
    private var countdownJob: Job? = null
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        email: String,
        screenName: String,
        navigate: (NavigationAction) -> Unit,
    ): VerifyOtpUiState {
        Log.d("TAG", "email: $email,$screenName")
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        startCountdown(coroutineScope)
        verifyOtpUiDataState.update { state ->
            state.copy(
                email = email,
                screenName = screenName
            )
        }
        return VerifyOtpUiState(
            verifyOtpUiDataState = verifyOtpUiDataState,
            event = { authUiEvent ->
                authEvent(
                    event = authUiEvent,
                    coroutineScope,
                    context,
                    navigate

                )
            }
        )
    }

    private fun startCountdown(coroutineScope: CoroutineScope) {
        countdownJob?.cancel()
        countdownJob = coroutineScope.launch(Dispatchers.IO) {
            verifyOtpUiDataState.value = verifyOtpUiDataState.value.copy(
                isResendVisible = false
            )
            for (n in 60 downTo 0) {
                withContext(Dispatchers.Main) {
                    verifyOtpUiDataState.value = verifyOtpUiDataState.value.copy(
                        remainingTimeFlow = String.format(Locale.getDefault(), "00:%02d", n)
                    )
                }
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                verifyOtpUiDataState.value = verifyOtpUiDataState.value.copy(
                    remainingTimeFlow = "00:00",
                    isResendVisible = true
                )
            }
        }
    }


    private fun authEvent(
        event: VerifyOtpUiEvent,
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit

    ) {
        when (event) {
            is VerifyOtpUiEvent.GetContext -> {
                this.context = event.context
            }

            is VerifyOtpUiEvent.OtpTextValueChange -> {
                verifyOtpUiDataState.update { state ->
                    state.copy(
                        otpValue = event.otp,
                        otpErrorMsg = otpValidation(event.otp, context = context).errorMsg
                    )
                }
            }
            VerifyOtpUiEvent.ResendOtp -> {
                doResendOtp(coroutineScope = coroutineScope, context = this.context)

            }
            VerifyOtpUiEvent.OtpVerify -> {
                if (!isOffline.value) {
                        validationUseCase.apply {
                            val otpValidationResult = otpValidation(
                                verifyOtpUiDataState.value.otpValue,
                                context = context
                            )
                            val hasErrorOtp = !otpValidationResult.isSuccess
                            verifyOtpUiDataState.update { state ->
                                state.copy(
                                    otpErrorMsg = otpValidationResult.errorMsg
                                )
                            }
                            if (hasErrorOtp) {
                                return
                            }
                        }

                        doVerifyOtp(
                            coroutineScope = coroutineScope,
                            context = this.context,
                            navigate = navigate
                        )
                    } else {
                        showWaringMessage(
                            context,
                            context.getString(R.string.please_check_your_internet_connection_first)
                        )
                    }
                }

            VerifyOtpUiEvent.OnBackClick -> {
                navigate(NavigationAction.Pop())
            }
        }

    }

    private fun otpValidation(otp: String?, context: Context): ValidationResult {
        return ValidationResult(
            isSuccess = !otp.isNullOrBlank() && otp.length == 6,
            errorMsg = when {
                otp.isNullOrBlank() -> context.getString(R.string.please_provide_otp_for_verification)
                otp.length != 6 -> context.getString(R.string.the_otp_filed_must_be_6_digits)
                else -> null
            }
        )
    }


    private fun doResendOtp(
        coroutineScope: CoroutineScope,
        context: Context,

        ) {
        coroutineScope.launch {
            val sendOtpRequest = SendOTPReq(
                email = verifyOtpUiDataState.value.email,
                type = Constants.OtpVerificationType.USER_REGISTER

            )
            apiRepository.sendOTP(sendOtpRequest).collect {
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
                        startCountdown(coroutineScope)


                    }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }

    private fun doVerifyOtp(
        coroutineScope: CoroutineScope,
        context: Context,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val verifyOtpRequest = VerifyOTPReq(
                email = verifyOtpUiDataState.value.email,
                type = if (verifyOtpUiDataState.value.screenName == Constants.AppScreen.REGISTER_SCREEN) {
                    Constants.OtpVerificationType.USER_REGISTER
                } else {
                    Constants.OtpVerificationType.FORGET_PASSWORD
                },
                otp = verifyOtpUiDataState.value.otpValue
            )
            apiRepository.verifyOTP(verifyOtpRequest).collect {
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
                        if (verifyOtpUiDataState.value.screenName == Constants.AppScreen.REGISTER_SCREEN) {
                            storeResponseToDataStore(coroutineScope = coroutineScope,navigate = navigate, userAuthResponseData = it.data?.data)
                        } else {
                            navigate(NavigationAction.PopAndNavigate(ResetPasswordRoute.createRoute(verifyOtpUiDataState.value.email)))
                        }
                        storeResponseToDataStore(coroutineScope = coroutineScope,navigate = navigate, userAuthResponseData = it.data?.data)

                    }
                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                    }
                }
            }
        }
    }


    private fun showOrHideLoader(showLoader: Boolean) {
        verifyOtpUiDataState.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun storeResponseToDataStore(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
        userAuthResponseData: UserAuthResponse?
    ) {
        coroutineScope.launch {
            userAuthResponseData?.let {
                appPreferenceDataStore.saveUserData(it)
                val intent = Intent(context, MainActivity::class.java)
                    navigate(
                        NavigationAction.NavigateIntent(
                            intent = intent,
                            finishCurrentActivity = true
                        )
                    )

                Log.d("TAG", "getUserData: ${appPreferenceDataStore.getUserData()}")
                Log.d("TAG", "SaveUserData: ${appPreferenceDataStore.saveUserData(it)}")
            }
        }
    }


}