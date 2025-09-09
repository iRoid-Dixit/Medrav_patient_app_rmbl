package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import android.content.Context
import android.content.Intent
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge.DietChallengeRoute
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
) {
    private val bmiDataFlow = MutableStateFlow(WeightTrackerData())
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
        return WeightTrackerUiState(
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
        event: WeightTrackerUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is WeightTrackerUiEvent.GetContext -> {
                this.context = event.context
            }
            is WeightTrackerUiEvent.UpdateWeight -> {
                bmiDataFlow.update { currentData ->
                    currentData?.copy(currentWeight = event.weight) ?: WeightTrackerData(currentWeight = event.weight)
                }
            }
            is WeightTrackerUiEvent.UpdateUnit -> {
                bmiDataFlow.update { currentData ->
                    currentData?.copy(weightUnit = event.unit) ?: WeightTrackerData(weightUnit = event.unit)
                }
            }
            is WeightTrackerUiEvent.SubmitWeight -> {
                val intent = Intent(context, MainActivity::class.java)
                navigate(
                    NavigateIntent(
                        intent = intent,
                        finishCurrentActivity = true
                    )
                )
                // Handle weight submission
               /* bmiDataFlow.update { currentData ->
                    currentData?.copy(
                        lastRecordedWeight = currentData.currentWeight,
                        lastRecordedDate = "Today"
                    ) ?: WeightTrackerData()
                }*/
            }
            is WeightTrackerUiEvent.ScheduleCheckIn -> {
                // Handle scheduling check-in
            }

            WeightTrackerUiEvent.OnBackClick -> {
                navigate(Pop())
            }
        }
    }
}
