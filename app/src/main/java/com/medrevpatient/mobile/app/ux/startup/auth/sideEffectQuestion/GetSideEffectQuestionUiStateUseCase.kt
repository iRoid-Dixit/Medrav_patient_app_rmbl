package com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion

import android.content.Context
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightTrackerRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSideEffectQuestionUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
) {
    private val dietChallengeDataFlow = MutableStateFlow(
        SideEffectQuestionData(selectedAnswers = List(5) { -1 })
    )
    private val isOffline = MutableStateFlow(false)
    private lateinit var context: Context

    operator fun invoke(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit,
    ): SideEffectQuestionUiState {
        coroutineScope.launch {
            networkMonitor.isOnline.map(Boolean::not).stateIn(
                scope = coroutineScope,
                started = WhileSubscribed(5_000),
                initialValue = false,
            ).collect {
                isOffline.value = it
            }
        }

        return SideEffectQuestionUiState(
            sideEffectQuestionDataFlow = dietChallengeDataFlow,
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
        event: SideEffectQuestionUiEvent,
        navigate: (NavigationAction) -> Unit,
        coroutineScope: CoroutineScope,
    ) {
        when (event) {
            is SideEffectQuestionUiEvent.GetContext -> {
                this.context = event.context
            }

            is SideEffectQuestionUiEvent.UpdateAnswer -> {
                val currentData = dietChallengeDataFlow.value
                val updatedAnswers = currentData.selectedAnswers.toMutableList().apply {
                    this[event.questionIndex] = event.answerIndex
                }
                dietChallengeDataFlow.value = currentData.copy(selectedAnswers = updatedAnswers)
            }

            is SideEffectQuestionUiEvent.SubmitAssessment -> {
                navigate(Navigate(WeightTrackerRoute.createRoute()))

                /*val currentData = dietChallengeDataFlow.value
                // Check if all questions are answered
                val allAnswered = currentData.selectedAnswers.all { it != -1 }
                if (allAnswered) {
                    dietChallengeDataFlow.value = currentData.copy(isSubmitted = true)
                    // Here you can add logic to send data to backend or navigate to next screen
                }*/
            }

            SideEffectQuestionUiEvent.BackClick -> {
                navigate(PopIntent)
            }
        }
    }
}
