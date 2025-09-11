package com.medrevpatient.mobile.app.model.domain.response.weightTracker

import com.google.gson.annotations.SerializedName


data class WeightTrackerResponse(
    @SerializedName("currentWeightKg")
    val currentWeightKg: Int? = null,
    @SerializedName("currentWeightLbs")
    val currentWeightLbs: Int? = null,
    @SerializedName("lastRecordedWeightKg")
    val lastRecordedWeightKg: Double? = null,
    @SerializedName("lastRecordedWeightLbs")
    val lastRecordedWeightLbs: Double? = null,
    @SerializedName("lastRecordedTimestamp")
    val lastRecordedTimestamp: String? = null,
    @SerializedName("sinceLastWeekLossKg")
    val sinceLastWeekLossKg: Double? = null,
    @SerializedName("sinceLastWeekLossLbs")
    val sinceLastWeekLossLbs: Double? = null,
    @SerializedName("totalLossKg")
    val totalLossKg: Double? = null,
    @SerializedName("totalLossLbs")
    val totalLossLbs: Double? = null,
    @SerializedName("bodyWeightPercentage")
    val bodyWeightPercentage: Double? = null,
    @SerializedName("chartData")
    val chartData: List<WeightChartData> = emptyList(),
    @SerializedName("weeklyWeightLossLbs")
    val weeklyWeightLossLbs: Double? = null,
    @SerializedName("weeklyWeightLossPercentage")
    val weeklyWeightLossPercentage: Double? = null,
    @SerializedName("doseRecommendationText")
    val doseRecommendationText: String? = null,
    @SerializedName("clinicalNoteText")
    val clinicalNoteText: String? = null,
    @SerializedName("monthlyWeightLossTargetPercentage")
    val monthlyWeightLossTargetPercentage: Double? = null
)

data class WeightChartData(
    @SerializedName("week")
    val week: Int,
    @SerializedName("weight")
    val weight: Double?
)