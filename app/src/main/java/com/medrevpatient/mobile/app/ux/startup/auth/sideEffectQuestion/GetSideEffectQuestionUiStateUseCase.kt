package com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion

import android.content.Context
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.data.source.remote.repository.ApiRepository
import com.medrevpatient.mobile.app.domain.validation.ValidationUseCase
import com.medrevpatient.mobile.app.model.domain.response.sideEffect.SideEffectQuestion
import com.medrevpatient.mobile.app.model.domain.request.sideEffect.SideEffectAnswerRequest
import com.medrevpatient.mobile.app.model.domain.request.sideEffect.SideEffectAnswer
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.NavigationAction.*
import com.medrevpatient.mobile.app.utils.AppUtils.showErrorMessage
import com.medrevpatient.mobile.app.utils.AppUtils.showSuccessMessage
import com.medrevpatient.mobile.app.utils.connection.NetworkMonitor
import com.medrevpatient.mobile.app.ux.main.home.HomeUiDataState
import com.medrevpatient.mobile.app.ux.startup.auth.weightTracker.WeightTrackerRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class GetSideEffectQuestionUiStateUseCase
@Inject constructor(
    private val networkMonitor: NetworkMonitor,
    private val validationUseCase: ValidationUseCase,
    private val apiRepository: ApiRepository,
) {

    private val sideEffectUiDataFlow = MutableStateFlow(SideEffectQuestionData())
    private val questionList = MutableStateFlow<List<SideEffectQuestion>>(emptyList())
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
        getQuestionData(
            coroutineScope = coroutineScope,
            navigate = navigate
        )
        return SideEffectQuestionUiState(
            sideEffectQuestionDataFlow = sideEffectUiDataFlow,
            questionList = questionList,
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
                val currentData = sideEffectUiDataFlow.value
                val updatedAnswers = currentData.selectedAnswers.toMutableList()

                // Ensure the list is large enough
                while (updatedAnswers.size <= event.questionIndex) {
                    updatedAnswers.add(-1)
                }
                updatedAnswers[event.questionIndex] = event.answerIndex
                sideEffectUiDataFlow.value = currentData.copy(selectedAnswers = updatedAnswers)
            }

            is SideEffectQuestionUiEvent.SubmitAssessment -> {
                submitSideEffectAnswers(
                    coroutineScope = coroutineScope,
                    navigate = navigate
                )
            }

            SideEffectQuestionUiEvent.BackClick -> {
                navigate(PopIntent)
            }
        }
    }

    private fun getQuestionData(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        coroutineScope.launch {
            apiRepository.getSideEffectQuestions().collect {
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
                        questionList.value =it.data?.data?:emptyList()
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

    private fun submitSideEffectAnswers(
        coroutineScope: CoroutineScope,
        navigate: (NavigationAction) -> Unit
    ) {
        val currentData = sideEffectUiDataFlow.value
        val questions = questionList.value
        val answers = questions.mapIndexed { index, question ->
            val selectedAnswerIndex = if (index < currentData.selectedAnswers.size) currentData.selectedAnswers[index] else -1
            val selectedOption = if (selectedAnswerIndex >= 0 && selectedAnswerIndex < question.options.size) {
                question.options[selectedAnswerIndex]
            } else null
            SideEffectAnswer(
                questionId = question.id,
                optionId = selectedOption?.id ?: 0
            )
        }
        val request = SideEffectAnswerRequest(answers = answers)
        coroutineScope.launch {
            apiRepository.submitSideEffectAnswers(request).collect {
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
                        showSuccessMessage(context = context, it.data?.message ?: "Something went wrong!")
                        navigate(Navigate(WeightTrackerRoute.createRoute()))
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
        sideEffectUiDataFlow.update { state ->
            state.copy(
                showLoader = showLoader
            )
        }
    }
}
