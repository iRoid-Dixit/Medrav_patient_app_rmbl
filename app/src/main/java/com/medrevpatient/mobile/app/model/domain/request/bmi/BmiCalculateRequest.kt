package com.medrevpatient.mobile.app.model.domain.request.bmi

import com.google.gson.annotations.SerializedName

data class BmiCalculateRequest(
    @SerializedName("weightKg")
    val weightKg: Double,
    @SerializedName("heightCm")
    val heightCm: Double
)



