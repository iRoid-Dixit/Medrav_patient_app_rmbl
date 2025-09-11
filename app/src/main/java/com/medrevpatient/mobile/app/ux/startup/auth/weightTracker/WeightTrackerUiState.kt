package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WeightTrackerUiState(
    val weightTrackerDataFlow: StateFlow<WeightTrackerData?> = MutableStateFlow(null),
    val event: (WeightTrackerUiEvent) -> Unit = {}
)

data class WeightTrackerData(
    val showLoader: Boolean = false,
    val currentWeight: String = "",
    val weightUnit: WeightUnit = WeightUnit.LBS,


    // API Response fields
    val currentWeightKg: String? = null,
    val currentWeightLbs: String? = null,
    val lastRecordedWeightKg: Double? = null,
    val lastRecordedWeightLbs: Double? = null,
    val sinceLastWeekLossKg: Double = 0.0,
    val sinceLastWeekLossLbs: Double = 0.0,
    val totalLossKg: Double = 0.0,
    val totalLossLbs: Double = 0.0,
    val bodyWeightPercentage: Double = 0.0,
    val chartData: List<WeightChartData> = emptyList(),


    val doseRecommendationText: String = "",
    val clinicalNoteText: String = "",

)

data class WeightDataPoint(
    val week: Int,
    val weight: Int
)

data class WeightChartData(
    val week: Int,
    val weight: Double?
)

enum class WeightUnit {
    LBS, KG
}

sealed interface WeightTrackerUiEvent {
    data class GetContext(val context: Context) : WeightTrackerUiEvent
    data class UpdateUnit(val unit: WeightUnit) : WeightTrackerUiEvent
    object SubmitWeight : WeightTrackerUiEvent
    object ScheduleCheckIn : WeightTrackerUiEvent
    object OnBackClick : WeightTrackerUiEvent
}
