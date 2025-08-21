package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class  StrengthLogExercises(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("createdAt")
    val createdAt: Long = 0L,
    @SerializedName("updatedAt")
    val updatedAt: Long = 0L,
    @SerializedName("__v")
    val v: Int = 0
)