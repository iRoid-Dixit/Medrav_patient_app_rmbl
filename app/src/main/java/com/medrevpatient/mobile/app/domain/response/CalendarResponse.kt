package com.medrevpatient.mobile.app.domain.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CalendarResponse(
    @SerializedName("date")
    val date: Long? = null,
    @SerializedName("events")
    val events: ArrayList<Event> = arrayListOf(),
    @SerializedName("isCompleted")
    val isCompleted: Boolean? = null
) : Serializable

data class Event(
    @SerializedName("isProgramCompleted")
    val isProgramCompleted: Boolean? = null,
    @SerializedName("subTitle")
    val subTitle: String? = null,
    @SerializedName("title")
    val title: String? = null
) : Serializable