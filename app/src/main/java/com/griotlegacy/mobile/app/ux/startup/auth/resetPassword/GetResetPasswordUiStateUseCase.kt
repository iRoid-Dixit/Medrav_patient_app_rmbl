package com.griotlegacy.mobile.app.ux.startup.auth.resetPassword
import android.content.Context
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.domain.validation.ValidationResult
import com.griotlegacy.mobile.app.domain.validation.ValidationUseCase
import com.griotlegacy.mobile.app.model.domain.request.authReq.ResetPasswordReq
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showWaringMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.ux.startup.auth.login.LoginRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetResetPasswordUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val networkMonitor: NetworkMonitor
) {
    private val isOffline = MutableStateFlow(false)
    private val resetPasswordUiDataState = MutableStateFlow(ResetPasswordUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        email: String,
        navigate: (NavigationAction) -> Unit,
    ): ForgetPasswordUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        resetPasswordUiDataState.update { state ->
            state.copy(
                email = email,
            )
        }

        return ForgetPasswordUiState(
            resetPasswordUiDataState = resetPasswordUiDataState,
            event = { authUiEvent ->
                authEvent(
                    event = authUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                    context = context
                )

            }
        )
    }
    private fun authEvent(
        event: ResetPasswordUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        context: Context

    ) {
        when (event) {
            is ResetPasswordUiEvent.GetContext -> {
                this.context = event.context
            }
            is ResetPasswordUiEvent.ConfirmPasswordValueChange ->{
                resetPasswordUiDataState.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordErrorMsg = confirmPasswordValidation(
                            resetPasswordUiDataState.value.newPassword,
                            event.confirmPassword,
                            context
                        ).errorMsg
                    )
                }
            }
            ResetPasswordUiEvent.ResetPasswordSubmit -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val newValidationResult =
                            passwordValidation(resetPasswordUiDataState.value.newPassword, context)
                        val confirmPasswordResult = confirmPasswordValidation(
                            resetPasswordUiDataState.value.newPassword,
                            resetPasswordUiDataState.value.confirmPassword,
                            context
                        )
                        val hasError = listOf(
                            newValidationResult,
                            confirmPasswordResult,
                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        resetPasswordUiDataState.update { state ->
                            state.copy(
                                newPasswordErrorMsg = newValidationResult.errorMsg?:"",
                                confirmPasswordErrorMsg = confirmPasswordResult.errorMsg,
                            )
                        }
                        if (hasError) return
                    }
                    //navigate(NavigationAction.Navigate(VerifyOtpRoute.createRoute(forgetPasswordUiDataState.value.email, screenName = Constants.AppScreen.FORGET_PASSWORD_SCREEN)))
                    doResetPassword(
                        coroutineScope = coroutineScope,
                        navigate = navigate
                    )
                } else {
                    showWaringMessage(context,
                        context.getString(R.string.please_check_your_internet_connection_first))
                }
            }

            ResetPasswordUiEvent.OnBackClick -> {
                navigate(NavigationAction.Pop())
            }

            is ResetPasswordUiEvent.NewPasswordValueChange ->{
                resetPasswordUiDataState.update { state ->
                    state.copy(
                        newPassword = event.newPassword,
                        newPasswordErrorMsg = validationUseCase.passwordValidation(
                            password = event.newPassword,
                            context = context
                        ).errorMsg
                    )
                }

            }
        }

    }
    private fun doResetPassword(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit

        ) {
        coroutineScope.launch {
            val resetPasswordRequest = ResetPasswordReq(
                newPassword = resetPasswordUiDataState.value.newPassword,
                confirmPassword = resetPasswordUiDataState.value.confirmPassword,
                email = resetPasswordUiDataState.value.email
            )
            apiRepository.resetPassword(resetPasswordRequest).collect {
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
                        navigate(NavigationAction.Navigate(LoginRoute.createRoute()))
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
        resetPasswordUiDataState.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
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


}