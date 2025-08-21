package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class PinRequest(
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("itemId")
    val itemId: String = ""
)