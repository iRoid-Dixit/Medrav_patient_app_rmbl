package com.medrevpatient.mobile.app.model.domain.response.appointment

import com.google.gson.annotations.SerializedName


data class AvailableSlotsData(
    @SerializedName("date")
    val date: String,
    @SerializedName("dayName")
    val dayName: String,
    @SerializedName("timePeriod")
    val timePeriod: Int,
    @SerializedName("isDayPast")
    val isDayPast: Boolean,
    @SerializedName("totalSlots")
    val totalSlots: Int,
    @SerializedName("availableSlots")
    val availableSlots: List<AvailableSlot>
)
data class AvailableSlot(
    @SerializedName("time")
    val time: String,
    @SerializedName("isAvailable")
    val isAvailable: Boolean,
    @SerializedName("availableDoctorsCount")
    val availableDoctorsCount: Int,
    @SerializedName("timestamp")
    val timestamp: Long
)

