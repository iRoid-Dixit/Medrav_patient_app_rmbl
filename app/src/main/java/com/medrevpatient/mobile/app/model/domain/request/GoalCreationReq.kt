package com.medrevpatient.mobile.app.model.domain.request


import com.google.gson.annotations.SerializedName

data class GoalCreationReq(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("goal_name") val goalName: String,
    @SerializedName("affirmation") val affirmation: List<Affirmation>,
    @SerializedName("reminder_time") val reminderTime: String
)

data class Affirmation(
    @SerializedName("id") val id: Int,
    @SerializedName("text") val text: String
)

data class NamePronunciationReq(
    @SerializedName("first_name") val firstName: String
)