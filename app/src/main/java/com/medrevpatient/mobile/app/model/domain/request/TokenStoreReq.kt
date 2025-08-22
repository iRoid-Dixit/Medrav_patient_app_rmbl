package com.medrevpatient.mobile.app.model.domain.request


import com.google.gson.annotations.SerializedName

data class TokenStoreReq(
    @SerializedName("type") val type: Int,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("token") val token: String
)