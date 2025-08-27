package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DietChallengeUiState(
    val dietChallengeDataFlow: StateFlow<DietChallengeData?> = MutableStateFlow(null),
    val event: (DietChallengeUiEvent) -> Unit = {}
)

data class DietChallengeData(
    val correctAnswers: Int = 3,
    val incorrectAnswers: Int = 0,
    val currentFoodItem: FoodItem = FoodItem(),
    val showLoader: Boolean = false
)

data class FoodItem(
    val name: String = "White Bread",
    val imageUrl: String = "",
    val correctCategory: String = "Starchy with High Absorption rate"
)

sealed interface DietChallengeUiEvent {
    data class GetContext(val context: Context) : DietChallengeUiEvent
    data class SelectCategory(val category: String) : DietChallengeUiEvent
    object ContinueChallenge : DietChallengeUiEvent
}