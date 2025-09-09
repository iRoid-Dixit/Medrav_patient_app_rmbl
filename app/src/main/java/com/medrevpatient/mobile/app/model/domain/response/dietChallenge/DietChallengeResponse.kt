package com.medrevpatient.mobile.app.model.domain.response.dietChallenge

import com.google.gson.annotations.SerializedName



data class DietChallengeResponse(
    @SerializedName("totalQuestions")
    val totalQuestions: Int?=null,
    @SerializedName("correctAnswers")
    val correctAnswers: Int?=null,
    @SerializedName("incorrectAnswers")
    val incorrectAnswers: Int?=null,
    @SerializedName("accuracyPercentage")
    val accuracyPercentage: Int?=null,
    @SerializedName("currentQuestion")
    val currentQuestion: CurrentQuestion?=null,
    @SerializedName("availableCategories")
    val availableCategories: List<AvailableCategory?> = emptyList(),
    @SerializedName("isCompleted")
    val isCompleted: Boolean=false,
    @SerializedName("questionsRemaining")
    val questionsRemaining: Int?=null
)

data class CurrentQuestion(
    @SerializedName("foodId")
    val foodId: Int?=null,
    @SerializedName("foodName")
    val foodName: String?=null,
    @SerializedName("foodImage")
    val foodImage: String?=null
)

data class AvailableCategory(
    @SerializedName("id")
    val id: Int?=null,
    @SerializedName("name")
    val name: String?=null,
    @SerializedName("image")
    val image: String?=null
)
