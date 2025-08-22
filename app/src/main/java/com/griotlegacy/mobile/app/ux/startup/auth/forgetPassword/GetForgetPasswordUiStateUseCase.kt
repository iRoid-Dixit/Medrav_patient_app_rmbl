package com.griotlegacy.mobile.app.ux.startup.auth.forgetPassword
import android.content.Context
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.data.source.remote.helper.NetworkResult
import com.griotlegacy.mobile.app.data.source.remote.repository.ApiRepository
import com.griotlegacy.mobile.app.domain.validation.ValidationUseCase
import com.griotlegacy.mobile.app.model.domain.request.authReq.ForgetPasswordReq
import com.griotlegacy.mobile.app.navigation.NavigationAction
import com.griotlegacy.mobile.app.utils.AppUtils.showErrorMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showSuccessMessage
import com.griotlegacy.mobile.app.utils.AppUtils.showWaringMessage
import com.griotlegacy.mobile.app.utils.connection.NetworkMonitor
import com.griotlegacy.mobile.app.ux.startup.auth.verifyOtp.VerifyOtpRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetForgetPasswordUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
    private val networkMonitor: NetworkMonitor
) {
    private val isOffline = MutableStateFlow(false)
    private val forgetPasswordUiDataState = MutableStateFlow(ForgetPasswordUiDataState())
    private lateinit var context: Context
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
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

        return ForgetPasswordUiState(
            forgetPasswordUiDataState = forgetPasswordUiDataState,
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
        event: ForgetPasswordUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
        context: Context

    ) {
        when (event) {
            is ForgetPasswordUiEvent.GetContext -> {
                this.context = event.context
            }
            is ForgetPasswordUiEvent.EmailValueChange ->{
                forgetPasswordUiDataState.update { state ->
                    state.copy(
                        email = event.email,
                        emailErrorMsg = validationUseCase.emailValidation(emailAddress = event.email, context = context).errorMsg
                    )
                }
            }

            ForgetPasswordUiEvent.ForgetPasswordClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val emailValidationResult = emailValidation(forgetPasswordUiDataState.value.email, context = context)
                        val hasErrorEmail = !emailValidationResult.isSuccess
                        // 🔹 **Update both email and password errors in one go**
                        forgetPasswordUiDataState.update { state ->
                            state.copy(
                                emailErrorMsg = emailValidationResult.errorMsg,

                                )
                        }
                        if (hasErrorEmail ) {
                            return
                        }
                    }
                    //navigate(NavigationAction.Navigate(VerifyOtpRoute.createRoute(forgetPasswordUiDataState.value.email, screenName = Constants.AppScreen.FORGET_PASSWORD_SCREEN)))
                    doForgetPassword(
                        coroutineScope = coroutineScope,
                        navigate = navigate
                    )
                } else {
                    showWaringMessage(context,
                        context.getString(R.string.please_check_your_internet_connection_first))
                }
            }

            ForgetPasswordUiEvent.OnBackClick -> {
                navigate(NavigationAction.Pop())
            }
        }

    }
    private fun doForgetPassword(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit

        ) {
        coroutineScope.launch {
            val forgetPasswordRequest = ForgetPasswordReq(
                email = forgetPasswordUiDataState.value.email,
            )
            apiRepository.forgetPassword(forgetPasswordRequest).collect {
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
                        navigate(NavigationAction.Navigate(VerifyOtpRoute.createRoute(forgetPasswordUiDataState.value.email, screenName = Constants.AppScreen.FORGET_PASSWORD_SCREEN)))
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
        forgetPasswordUiDataState.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }


}