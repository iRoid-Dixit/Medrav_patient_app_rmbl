package com.medrevpatient.mobile.app.model.domain.request


import com.google.gson.annotations.SerializedName

data class FeedbackReq(
    @SerializedName("feedback_type") val feedbackType: Int,
    @SerializedName("message") val message: String
)