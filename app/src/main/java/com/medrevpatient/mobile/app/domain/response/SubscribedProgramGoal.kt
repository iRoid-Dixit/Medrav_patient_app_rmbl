package com.medrevpatient.mobile.app.domain.response


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SubscribedProgramGoal(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("programGoal")
    val programGoal: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("reminderTime")
    val reminderTime: String? = null,
    @SerializedName("repeatFrequency")
    val repeatFrequency: ArrayList<Int> = arrayListOf(),
) : Serializable