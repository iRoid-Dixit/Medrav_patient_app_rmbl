package com.medrevpatient.mobile.app.ux.container.changePassword
import android.content.Context
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationResult
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ChangePasswordReq
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetChangePasswordUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val changePasswordUsDataFlow = MutableStateFlow(ChangePasswordDataState())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ChangePasswordUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return ChangePasswordUiState(
            changePasswordUsDataFlow = changePasswordUsDataFlow,
            event = { aboutUsEvent ->
                changePasswordUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }

    private fun changePasswordUiEvent(
        event: ChangePasswordUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            ChangePasswordUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }
            is ChangePasswordUiEvent.ConfirmPasswordValueChange -> {
                changePasswordUsDataFlow.update { state ->
                    state.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordErrorMsg = confirmPasswordValidation(
                            changePasswordUsDataFlow.value.newPassword,
                            event.confirmPassword,
                            context
                        ).errorMsg
                    )
                }
            }
            is ChangePasswordUiEvent.NewPasswordValueChange -> {
                val newPasswordValidationResult = newPasswordValidation(
                    event.newPassword,
                    context
                )
                changePasswordUsDataFlow.update { state ->
                    state.copy(
                        newPassword = event.newPassword,
                        newPasswordErrorMsg = newPasswordValidationResult.errorMsg
                    )
                }
            }
            is ChangePasswordUiEvent.OldPasswordValueChange -> {
                val oldPasswordValidationResult = oldPasswordValidation(
                    event.oldPassword,
                    context
                )
                changePasswordUsDataFlow.update { state ->
                    state.copy(
                        oldPassword = event.oldPassword,
                        oldPasswordErrorMsg = oldPasswordValidationResult.errorMsg
                    )
                }
            }
            ChangePasswordUiEvent.ChangePasswordSubmit -> {
            if (!isOffline.value) {
                validationUseCase.apply {
                    val oldPasswordValidationResult =
                        oldPasswordValidation(changePasswordUsDataFlow.value.oldPassword, context)

                    val newPasswordValidationResult =
                        passwordValidation(changePasswordUsDataFlow.value.newPassword, context)
                    val confirmPasswordResult = confirmPasswordValidation(
                        changePasswordUsDataFlow.value.newPassword,
                        changePasswordUsDataFlow.value.confirmPassword,
                        context
                    )
                    val hasError = listOf(
                        oldPasswordValidationResult,
                        newPasswordValidationResult,
                        confirmPasswordResult,
                    ).any { !it.isSuccess }
                    // 🔹 **Update all error messages in one go**
                    changePasswordUsDataFlow.update { state ->
                        state.copy(
                            oldPasswordErrorMsg = oldPasswordValidationResult.errorMsg,
                            newPasswordErrorMsg = newPasswordValidationResult.errorMsg,
                            confirmPasswordErrorMsg = confirmPasswordResult.errorMsg,
                        )
                    }
                    if (hasError) return //  Stop if any validation failed
                }
                doChangePassword(coroutineScope =coroutineScope, navigate = navigate)

            } else {
                showWaringMessage(
                    this.context,
                    context.getString(R.string.please_check_your_internet_connection_first)
                )
            }

        }

            is ChangePasswordUiEvent.GetContext -> {
                this.context = event.context
            }
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

    private fun newPasswordValidation(
        password: String,
        context: Context
    ): ValidationResult {
        val uppercasePattern = Regex("[A-Z]")
        val specialCharPattern = Regex("[!@#\$%^&*(),.?\":{}|<>]")
        val digitPattern = Regex("\\d")
        return ValidationResult(
            isSuccess = password.isNotBlank() && password.length >= 8 &&
                    uppercasePattern.containsMatchIn(password) &&
                    specialCharPattern.containsMatchIn(password) &&
                    digitPattern.containsMatchIn(password),
            errorMsg =when {
                password.isBlank() -> context.getString(R.string.please_enter_the_new_password)
                password.length < 8 -> context.getString(R.string.password_text)
                !uppercasePattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                !specialCharPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                !digitPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                else -> null
            }
        )
    }
    private fun oldPasswordValidation(
        password: String,
        context: Context
    ): ValidationResult {
        val uppercasePattern = Regex("[A-Z]")
        val specialCharPattern = Regex("[!@#\$%^&*(),.?\":{}|<>]")
        val digitPattern = Regex("\\d")
        return ValidationResult(
            isSuccess = password.isNotBlank() && password.length >= 8 &&
                    uppercasePattern.containsMatchIn(password) &&
                    specialCharPattern.containsMatchIn(password) &&
                    digitPattern.containsMatchIn(password),
            errorMsg =when {
                password.isBlank() -> context.getString(R.string.please_enter_the_old_password)
                password.length < 8 -> context.getString(R.string.password_text)
                !uppercasePattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                !specialCharPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                !digitPattern.containsMatchIn(password) -> context.getString(R.string.password_text)
                else -> null
            }
        )
    }

    private fun doChangePassword(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit

    ) {
        coroutineScope.launch {
            val changePasswordRequest =ChangePasswordReq(
                oldPassword = changePasswordUsDataFlow.value.oldPassword,
                newPassword = changePasswordUsDataFlow.value.newPassword,
                confirmPassword = changePasswordUsDataFlow.value.confirmPassword
            )
            apiRepository.doChangePassword(changePasswordRequest).collect {
                when (it) {
                    is NetworkResult.Error -> {
                        showErrorMessage(context = context, it.message ?: "Something went wrong!")
                        showOrHideLoader(false)
                    }
                    is NetworkResult.Loading -> {
                        showOrHideLoader(true)
                    }
                    is NetworkResult.Success -> {
                        showSuccessMessage(context = context, it.data?.message ?: "")
                        showOrHideLoader(false)
                        coroutineScope.launch {
                            delay(500) // Adjust delay if needed
                            navigate(NavigationAction.PopIntent)
                        }
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
        changePasswordUsDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }




}


