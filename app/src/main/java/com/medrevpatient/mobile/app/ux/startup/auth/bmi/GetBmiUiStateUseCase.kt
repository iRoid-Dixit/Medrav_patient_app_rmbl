package com.medrevpatient.mobile.app.ux.startup.auth.bmi

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.request.authReq.ResendOTPReq
import com.medrevpatient.mobile.app.model.domain.request.bmi.BmiCalculateRequest
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge.DietChallengeRoute
import com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion.SideEffectQuestionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetBmiUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
) {
    private val bmiDataFlow = MutableStateFlow(BmiData())
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): BmiUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return BmiUiState(
            bmiDataFlow = bmiDataFlow,
            event = { bmiUiEvent ->
                bmiEvent(
                    event = bmiUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,

                )
            }
        )
    }

    private fun bmiEvent(
        event: BmiUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,

    ) {
        when (event) {
            is BmiUiEvent.HeightValueChange -> {
                bmiDataFlow.update { state ->
                    state.copy(
                        heightInput = event.height,
                        heightErrorFlow = validationUseCase.emptyFieldValidation(
                            event.height,
                            "Please enter the height"
                        ).errorMsg

                    )
                }
            }
            is BmiUiEvent.WeightValueChange -> {
                bmiDataFlow.update { state ->
                    state.copy(
                        weightInput = event.weight,
                        weightErrorFlow = validationUseCase.emptyFieldValidation(
                            event.weight,
                            "Please enter the weight"
                        ).errorMsg
                    )
                }
            }

            is BmiUiEvent.CalculateBmi -> {
                if (!isOffline.value) {
                    validationUseCase.apply {
                        val heightInputValidationResult = emptyFieldValidation(
                            bmiDataFlow.value.heightInput,
                            "Please enter the height"
                        )
                        val weightInputValidationResult = emptyFieldValidation(
                            bmiDataFlow.value.weightInput,
                            "Please enter the weight"
                        )
                        val hasError = listOf(
                            heightInputValidationResult,
                            weightInputValidationResult,
                            
                        ).any { !it.isSuccess }
                        bmiDataFlow.update { state ->
                            state.copy(
                                heightErrorFlow = heightInputValidationResult.errorMsg,
                                weightErrorFlow = weightInputValidationResult.errorMsg,
                            )
                        }
                        if (hasError) return
                        doUserEmailVerifyIn(
                            coroutineScope = coroutineScope,
                            navigate = navigate
                        )
                    }
                } else {
                    showWaringMessage(
                        context,
                        context.getString(R.string.please_check_your_internet_connection_first)
                    )
                }
                
            }
            is BmiUiEvent.GetContext -> {
                this.context = event.context
            }
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    private fun doUserEmailVerifyIn(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            val bmiRequest = BmiCalculateRequest(
                weightKg = bmiDataFlow.value.weightInput.toDouble(),
                heightCm = bmiDataFlow.value.heightInput.toDouble()
            )
            apiRepository.calculateBmi(bmiRequest).collect {
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
                        navigate(PopIntent)
                        showSuccessMessage(context = context, it.data?.message ?: "")
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
        bmiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
}
