package com.medrevpatient.mobile.app.ux.container.contactUs
import android.content.Context
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ContactUsReq
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

class GetContactUsUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,

    ) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val contactUsDataFlow = MutableStateFlow(ContactUsDataState())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): ContactUsUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return ContactUsUiState(
            contactUsDataFlow = contactUsDataFlow,
            event = { aboutUsEvent ->
                contactUsUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope

                )
            }
        )
    }

    private fun contactUsUiEvent(
        event: ContactUsUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            ContactUsUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }
            is ContactUsUiEvent.FullNameValueChange -> {
                contactUsDataFlow.update { state->
                    state.copy(
                        fullName = event.fullName,
                        fullNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.fullName,
                            context.getString(R.string.please_enter_your_full_name)
                        ).errorMsg
                    )

                }
            }
            is ContactUsUiEvent.GetContext -> {
                this.context = event.context
            }
            is ContactUsUiEvent.MessageValueChange ->{
                contactUsDataFlow.update { state->
                    state.copy(
                        message = event.message,
                                messageErrorMsg = validationUseCase.emptyFieldValidation(
                                event.message,
                        context.getString(R.string.please_enter_your_full_name)
                    ).errorMsg
                    )

                }

            }

            ContactUsUiEvent.ContactUsClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            contactUsDataFlow.value.fullName,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val messageValidationResult =
                            emptyFieldValidation(contactUsDataFlow.value.message, context.getString(R.string.please_enter_message))

                        val hasError = listOf(
                            nameValidationResult,
                           messageValidationResult
                        ).any { !it.isSuccess }
                        // 🔹 **Update all error messages in one go**
                        contactUsDataFlow.update { state ->
                            state.copy(
                                fullNameErrorMsg = nameValidationResult.errorMsg,
                                messageErrorMsg = messageValidationResult.errorMsg,
                            )
                        }
                        if (hasError) return //  Stop if any validation failed
                    }
                    doContactUs(coroutineScope = coroutineScope, navigate = navigate)
                } else {
                    showWaringMessage(
                        this.context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
            }
        }
    }
    private fun doContactUs(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit

    ) {
        coroutineScope.launch {
            val contactUsRequest =ContactUsReq(
                name = contactUsDataFlow.value.fullName,
                message = contactUsDataFlow.value.message
            )
            apiRepository.doContactUs(contactUsRequest).collect {
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
                            delay(1000) // Adjust delay if needed
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
        contactUsDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }




}


