package com.medrevpatient.mobile.app.domain.response

import com.google.gson.annotations.SerializedName

data class CommonMessageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class OnBoardSelection(
    val data: String = "",
    var isSelected: Boolean = false
)
