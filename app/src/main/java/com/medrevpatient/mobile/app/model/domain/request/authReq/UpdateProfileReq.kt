package com.medrevpatient.mobile.app.model.domain.request.authReq


import com.google.gson.annotations.SerializedName

data class UpdateProfileReq(
    @SerializedName("is_send_otp") val isSendOtp: Int,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("otp") val otp: String?
)