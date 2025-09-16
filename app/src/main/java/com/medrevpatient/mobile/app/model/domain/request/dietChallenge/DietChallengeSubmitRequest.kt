package com.medrevpatient.mobile.app.model.domain.request.dietChallenge

import com.google.gson.annotations.SerializedName

data class DietChallengeSubmitRequest(
    @SerializedName("foodId")
    val foodId: Int,
    @SerializedName("selectedCategoryId")
    val selectedCategoryId: Int
)
