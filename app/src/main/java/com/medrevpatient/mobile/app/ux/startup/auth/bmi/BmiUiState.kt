package com.medrevpatient.mobile.app.ux.startup.auth.bmi

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BmiUiState(
    val bmiDataFlow: StateFlow<BmiData?> = MutableStateFlow(null),
    val event: (BmiUiEvent) -> Unit = {}
)

data class BmiData(
    val heightInput: String = "",
    val heightErrorFlow: String? = null,
    val weightInput: String = "",
    val weightErrorFlow: String? = null,
    val showLoader: Boolean = false,
    val bmiResult: BmiResult? = null,
    val errorMessage: String? = null
)

data class BmiResult(
    val bmi: Double,
    val category: Int,
    val categoryName: String,
    val message: String? = null
)

sealed interface BmiUiEvent {
    data class HeightValueChange(val height: String) : BmiUiEvent
    data class WeightValueChange(val weight: String) : BmiUiEvent
    data class GetContext(val context : Context) : BmiUiEvent
    data object CalculateBmi : BmiUiEvent
    data object OnBackClick : BmiUiEvent

}
