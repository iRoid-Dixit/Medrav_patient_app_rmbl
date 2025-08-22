package com.medrevpatient.mobile.app.ux.container.myCircle

import android.content.Context
import android.util.Log
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.mainReq.ContactUsReq
import com.medrevpatient.mobile.app.model.domain.response.tribe.TribeResponse
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.container.createTribeOrInnerCircle.CreateTribeOrInnerCircleRoute
import com.medrevpatient.mobile.app.ux.container.tribeList.TribeListRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetMyCircleUiStateUseCase
@Inject constructor(
    private val validationUseCase: ValidationUseCase,
    private val networkMonitor: NetworkMonitor,
    private val apiRepository: ApiRepository,
) {
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    private val myCircleDataFlow = MutableStateFlow(MyCircleDataState())
    private val tribeListFlow = MutableStateFlow<PagingData<TribeResponse>>(PagingData.empty())
    private val innerCircleListFlow =
        MutableStateFlow<PagingData<TribeResponse>>(PagingData.empty())
    operator fun invoke(
        context: Context,
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): MyCircleUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return MyCircleUiState(
            myCircleDataFlow = myCircleDataFlow,
            tribeListFlow = tribeListFlow,
            innerCircleListFlow = innerCircleListFlow,
            event = { aboutUsEvent ->
                myCircleUiEvent(
                    event = aboutUsEvent,
                    context = context,
                    navigate = navigate,
                    coroutineScope = coroutineScope
                )
            }
        )
    }
    private fun myCircleUiEvent(
        event: MyCircleUiEvent,
        context: Context,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope
    ) {
        when (event) {
            MyCircleUiEvent.BackClick -> {
                navigate(NavigationAction.PopIntent)
            }

            is MyCircleUiEvent.FullNameValueChange -> {
                myCircleDataFlow.update { state ->
                    state.copy(
                        fullName = event.fullName,
                        fullNameErrorMsg = validationUseCase.emptyFieldValidation(
                            event.fullName,
                            context.getString(R.string.please_enter_your_full_name)
                        ).errorMsg
                    )
                }
            }

            is MyCircleUiEvent.OnAddTribeClick -> {
                navigate(
                    NavigationAction.Navigate(
                        CreateTribeOrInnerCircleRoute.createRoute(
                            Constants.AppScreen.CREATE_INNER_CIRCLE_OR_TRIBE,
                            messageData = "none",
                        )
                    )
                )
            }
            is MyCircleUiEvent.OnMemberClick -> {
                navigate(NavigationAction.Navigate(TribeListRoute.createRoute(event.tribeId,event.tribeName)))
            }
            is MyCircleUiEvent.OnGetTribeList -> {
                getTribeDataList(coroutineScope)
                getInnerCircleDataList(coroutineScope)
            }
            is MyCircleUiEvent.GetContext -> {
                this.context = event.context
            }

            is MyCircleUiEvent.MessageValueChange -> {
                myCircleDataFlow.update { state ->
                    state.copy(
                        message = event.message,
                        messageErrorMsg = validationUseCase.emptyFieldValidation(
                            event.message,
                            context.getString(R.string.please_enter_your_full_name)
                        ).errorMsg
                    )
                }
            }

            MyCircleUiEvent.MyCircleClick -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val nameValidationResult = emptyFieldValidation(
                            myCircleDataFlow.value.fullName,
                            context.getString(R.string.please_enter_your_full_name)
                        )
                        val messageValidationResult = emptyFieldValidation(
                            myCircleDataFlow.value.fullName,
                            context.getString(R.string.please_enter_message)
                        )

                        val hasError = listOf(
                            nameValidationResult,
                            messageValidationResult).any { !it.isSuccess }
                        //  **Update all error messages in one go**
                        myCircleDataFlow.update { state ->
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

    private fun getTribeDataList(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getTribeList(type = 1).cachedIn(this).collect { pagingData ->
                tribeListFlow.value = pagingData
                Log.d("TAG", "getTribeDataList: $pagingData")
            }
        }
    }

    private fun getInnerCircleDataList(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            apiRepository.getTribeList(type = 2).cachedIn(this).collect { pagingData ->
                innerCircleListFlow.value = pagingData
                Log.d("TAG", "getTribeDataList: $pagingData")
            }
        }
    }

    private fun doContactUs(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val contactUsRequest = ContactUsReq(
                name = myCircleDataFlow.value.fullName,
                message = myCircleDataFlow.value.message
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
        myCircleDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }


}


