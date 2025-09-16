package com.medrevpatient.mobile.app.model.domain.response.home

import com.google.gson.annotations.SerializedName

data class HomeScreenData(
    @SerializedName("assignedDoctor") val assignedDoctor: AssignedDoctor?=null, // null in your example, so nullable String
    @SerializedName("goalProgress") val goalProgress: GoalProgress?=null,
    @SerializedName("displaySideEffectCheckIn") val displaySideEffectCheckIn: Boolean?=null,
    @SerializedName("showBmiCheckIn") val showBmiCheckIn: Boolean?=null,
    @SerializedName("displayDailyDietChallenge")val displayDailyDietChallenge: Boolean?=null,
    @SerializedName("recentActivities") val recentActivities: List<RecentActivity> = emptyList()
)

data class GoalProgress(
    @SerializedName("initialWeightKg") val initialWeightKg: String?=null,       // Changed to String to handle "40.0 kg" format
    @SerializedName("currentWeightKg") val currentWeightKg: String?=null,      // Changed to String to handle "40.0 kg" format
    @SerializedName("goalWeightKg") val goalWeightKg: String?=null,            // Changed to String to handle "40.0 kg" format
    @SerializedName("progressPercentage") val progressPercentage: Double?=null,
    @SerializedName("lastWeekWeightKg") val lastWeekWeightKg: Double?=null,
    @SerializedName("weeklyChangeKg") val weeklyChangeKg: Double?=null,
    @SerializedName("weeklyChangeDisplay")  val weeklyChangeDisplay: String?=null
)
data class RecentActivity(
    @SerializedName("type") val type: Int,
    @SerializedName("title") val title: String,
    @SerializedName("time")  val time: Long
)
data class AssignedDoctor(
    @SerializedName("fullName")  val fullName: String?=null,
    @SerializedName("profileImage") val profileImage: String?=null,
    @SerializedName("specialist") val specialist: String?=null
)