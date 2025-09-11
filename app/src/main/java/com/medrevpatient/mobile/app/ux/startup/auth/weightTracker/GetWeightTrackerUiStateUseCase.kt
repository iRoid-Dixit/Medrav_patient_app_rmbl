package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import android.content.Context
import android.content.Intent
import android.util.Log
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage

import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetWeightTrackerUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
) {
    private val weightTrackerDataFlow = MutableStateFlow(WeightTrackerData())
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): WeightTrackerUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        getWeightTrackerAPICall(
            coroutineScope = coroutineScope,
            navigate = navigate
        )

        return WeightTrackerUiState(
            weightTrackerDataFlow = this@GetWeightTrackerUiStateUseCase.weightTrackerDataFlow,
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
        event: WeightTrackerUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is WeightTrackerUiEvent.GetContext -> {
                this.context = event.context
            }


            is WeightTrackerUiEvent.UpdateUnit -> {
                this@GetWeightTrackerUiStateUseCase.weightTrackerDataFlow.update { currentData ->
                    currentData.copy(weightUnit = event.unit)
                }
            }

            is WeightTrackerUiEvent.SubmitWeight -> {
                navigateToMainActivityScreens(context, navigate, screenName = Constants.AppScreen.HOME_SCREEN)

            }

            is WeightTrackerUiEvent.ScheduleCheckIn -> {
                // Handle scheduling check-in
            }

            WeightTrackerUiEvent.OnBackClick -> {
                navigate(Pop())
            }
        }
    }

    private fun navigateToMainActivityScreens(
        context: Context,
        navigate: (NavigationAction) -> Unit,
        screenName: String,
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(Constants.IS_COME_FOR, screenName)
        navigate(NavigateIntent(intent = intent, finishCurrentActivity = false))
    }

    private fun getWeightTrackerAPICall(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.getWeightTrackerData().collect {
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

                             weightTrackerDataFlow.update { state ->
                                 state.copy(
                                     // Current weight values
                                     currentWeightKg = it.data?.data?.currentWeightKg?.toString() ?: "0",
                                      currentWeightLbs = it.data?.data?.currentWeightLbs?.toString() ?: "0",
                                     
                                     // Last recorded weight values
                                     lastRecordedWeightKg = it.data?.data?.lastRecordedWeightKg,
                                     lastRecordedWeightLbs = it.data?.data?.lastRecordedWeightLbs,

                                     
                                     // Weekly change values
                                     sinceLastWeekLossKg = it.data?.data?.sinceLastWeekLossKg ?: 0.0,
                                     sinceLastWeekLossLbs = it.data?.data?.sinceLastWeekLossLbs ?: 0.0,
                                     
                                     // Total loss values
                                     totalLossKg = it.data?.data?.totalLossKg ?: 0.0,
                                     totalLossLbs = it.data?.data?.totalLossLbs ?: 0.0,
                                     
                                     // Body weight percentage
                                     bodyWeightPercentage = it.data?.data?.bodyWeightPercentage ?: 0.0,
                                     
                                      // Chart data - convert API chart data to UI chart data
                                      chartData = it.data?.data?.chartData?.map { apiChartData ->
                                          com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightChartData(
                                              week = apiChartData.week,
                                              weight = apiChartData.weight
                                          )
                                      } ?: emptyList(),
                                      
                                      // Weekly weight loss

                                      // Text content
                                      doseRecommendationText = it.data?.data?.doseRecommendationText ?: "",
                                      clinicalNoteText = it.data?.data?.clinicalNoteText ?: "",
                                      
                                      // Monthly target

                                      
                                      // Convert chart data to weight history


                                 )
                             }

                         showSuccessMessage(context = context, it.data?.message ?: "Data loaded successfully")
                     }

                    is NetworkResult.UnAuthenticated -> {
                        showOrHideLoader(false)
                        showErrorMessage(context = context, it.message ?: "Authentication failed!")
                    }
                }
            }
        }
    }

    private fun showOrHideLoader(showLoader: Boolean) {
        weightTrackerDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
}







