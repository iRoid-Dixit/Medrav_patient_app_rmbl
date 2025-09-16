package com.medrevpatient.mobile.app.model.domain.response.bmi

import com.google.gson.annotations.SerializedName

data class BmiCalculateResponse(
    @SerializedName("bmi")
    val bmi: Double,
    @SerializedName("category")
    val category: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("message")
    val message: String? = null
) {
    companion object {
        fun getCategoryName(category: Int): String {
            return when (category) {
                1 -> "Underweight"
                2 -> "Normal"
                3 -> "Overweight"
                4 -> "Obese Class 1"
                5 -> "Obese Class 2"
                6 -> "Obese Class 3"
                else -> "Unknown"
            }
        }
    }
}



