package com.medrevpatient.mobile.app.ux.startup.auth.dietChallenge

import android.content.Context
import com.medrevpatient.mobile.app.model.domain.response.dietChallenge.DietChallengeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class DietChallengeUiState(
    val dietChallengeDataFlow: StateFlow<DietChallengeData?> = MutableStateFlow(null),
    val dietListData: StateFlow<DietChallengeResponse?> = MutableStateFlow(null),

    val event: (DietChallengeUiEvent) -> Unit = {}
)

data class DietChallengeData(
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val currentFoodItem: FoodItem = FoodItem(),
    val availableCategories: List<FoodCategory> = emptyList(),
    val showLoader: Boolean = false,
    val questionsRemaining: Int? = null,
    val isCompleted: Boolean = false,
    val selectedCategoryId: Int? = null
)

data class FoodItem(
    val name: String = "",
    val imageUrl: String = "",
    val correctCategory: String = ""
)

data class FoodCategory(
    val id: Int = 0,
    val name: String = "",
    val image: String = ""
)

sealed interface DietChallengeUiEvent {
    data class GetContext(val context: Context) : DietChallengeUiEvent
    data class SelectCategory(val category: String) : DietChallengeUiEvent
    object ContinueChallenge : DietChallengeUiEvent
    object OnBackClick : DietChallengeUiEvent
    object ResetChallenge : DietChallengeUiEvent
}