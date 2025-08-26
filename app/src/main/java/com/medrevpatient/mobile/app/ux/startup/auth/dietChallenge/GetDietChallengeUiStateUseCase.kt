package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import android.content.Context
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showWaringMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion.SideEffectQuestionRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetDietChallengeUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
) {
    private val dietChallengeDataFlow = MutableStateFlow(DietChallengeData())
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context
    
    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): DietChallengeUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }
        return DietChallengeUiState(
            dietChallengeDataFlow = dietChallengeDataFlow,
            event = { dietChallengeUiEvent ->
                dietChallengeEvent(
                    event = dietChallengeUiEvent,
                    navigate = navigate,
                    coroutineScope = coroutineScope,
                )
            }
        )
    }

    private fun dietChallengeEvent(
        event: DietChallengeUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is DietChallengeUiEvent.GetContext -> {
                this.context = event.context
            }
            is DietChallengeUiEvent.SelectCategory -> {
                // Handle category selection
                dietChallengeDataFlow.update { currentData ->
                    currentData?.copy(
                        currentFoodItem = currentData.currentFoodItem.copy(
                            correctCategory = event.category
                        )
                    )
                }
            }
            is DietChallengeUiEvent.ContinueChallenge -> {
                // Handle continue action
                // Navigate to next screen or show results
            }
        }
    }
}
