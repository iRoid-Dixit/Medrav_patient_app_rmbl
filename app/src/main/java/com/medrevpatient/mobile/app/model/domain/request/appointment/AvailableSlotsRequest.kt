package com.medrevpatient.mobile.app.model.domain.request.appointment

import com.google.gson.annotations.SerializedName

data class AvailableSlotsRequest(
    @SerializedName("date")
    val date: String,
    @SerializedName("timePeriod")
    val timePeriod: Int
)


