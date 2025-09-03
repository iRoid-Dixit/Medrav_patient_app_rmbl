package com.medrevpatient.mobile.app.ux.startup.auth.sideEffectQuestion

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SideEffectQuestionUiState(
    val sideEffectQuestionDataFlow: StateFlow<SideEffectQuestionData?> = MutableStateFlow(null),
    val event: (SideEffectQuestionUiEvent) -> Unit = {}
)

data class SideEffectQuestionData(
    val showLoader: Boolean = false,
    val selectedAnswers: List<Int> = List(5) { -1 },
    val isSubmitted: Boolean = false
)

sealed interface SideEffectQuestionUiEvent {
    data class GetContext(val context: Context) : SideEffectQuestionUiEvent
    data class UpdateAnswer(val questionIndex: Int, val answerIndex: Int) : SideEffectQuestionUiEvent
    object SubmitAssessment : SideEffectQuestionUiEvent
    object BackClick : SideEffectQuestionUiEvent
}
