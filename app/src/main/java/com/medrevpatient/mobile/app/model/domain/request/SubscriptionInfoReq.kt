package com.medrevpatient.mobile.app.model.domain.request


import com.google.gson.annotations.SerializedName

data class SubscriptionInfoReq(
    @SerializedName("advertisementId") val advertisementId: String? = null,
    @SerializedName("productId") val productId: String,
    @SerializedName("token") val token: String,
    @SerializedName("platform") val platform: Int,
    @SerializedName("isTestEnvironment") val isTestEnvironment: Boolean,

)

data class CheckSubscriptionReq(
    @SerializedName("device_type") val deviceType: String
)