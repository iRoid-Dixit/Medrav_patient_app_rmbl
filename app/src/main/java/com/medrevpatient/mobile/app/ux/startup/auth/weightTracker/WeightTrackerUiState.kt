package com.medrevpatient.mobile.app.ux.startup.auth.weightTracker

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class WeightTrackerUiState(
    val bmiDataFlow: StateFlow<WeightTrackerData?> = MutableStateFlow(null),
    val event: (WeightTrackerUiEvent) -> Unit = {}
)

data class WeightTrackerData(
    val showLoader: Boolean = false,
    val currentWeight: String = "183",
    val weightUnit: WeightUnit = WeightUnit.LBS,
    val lastRecordedWeight: String = "185",
    val lastRecordedDate: String = "7 days ago",
    val weeklyChange: String = "-2",
    val weeklyChangePercentage: String = "1.1%",
    val totalLost: String = "-12",
    val bodyWeightPercentage: String = "6.1%",
    val weightHistory: List<WeightDataPoint> = listOf(
        WeightDataPoint(1, 325),
        WeightDataPoint(2, 245),
        WeightDataPoint(3, 215),
        WeightDataPoint(4, 190),
        WeightDataPoint(5, 175)
    )
)

data class WeightDataPoint(
    val week: Int,
    val weight: Int
)

enum class WeightUnit {
    LBS, KG
}

sealed interface WeightTrackerUiEvent {
    data class GetContext(val context: Context) : WeightTrackerUiEvent
    data class UpdateWeight(val weight: String) : WeightTrackerUiEvent
    data class UpdateUnit(val unit: WeightUnit) : WeightTrackerUiEvent
    object SubmitWeight : WeightTrackerUiEvent
    object ScheduleCheckIn : WeightTrackerUiEvent
    object OnBackClick : WeightTrackerUiEvent
}
