package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class CompleteRestDay(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("user")
    val user: String = "",
    @SerializedName("day")
    val day: Int = 0,
    @SerializedName("program")
    val program: String = "",
    @SerializedName("createdAt")
    val createdAt: Long = 0,
    @SerializedName("updatedAt")
    val updatedAt: Long = 0,
)