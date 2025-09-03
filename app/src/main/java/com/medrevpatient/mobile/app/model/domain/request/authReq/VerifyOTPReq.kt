package com.medrevpatient.mobile.app.model.domain.request.authReq


import com.google.gson.annotations.SerializedName

data class VerifyOTPReq(
    @SerializedName("email") val email: String? = null,
    @SerializedName("otp") val otp: String? = null,
    @SerializedName("otpType") val otpType: Int? = null
)

data class ResendOTPReq(
    @SerializedName("otpType") val otpType: Int? = null,
    @SerializedName("email") val email: String? = null,
)

data class ForgetPasswordReq(
    @SerializedName("email") val email: String? = null,
    @SerializedName("otpType") val otpType: Int? = null,

)
data class ResetPasswordReq(
    @SerializedName("email") val email: String? = null,
    @SerializedName("newPassword") val newPassword: String? = null,
    @SerializedName("confirmPassword") val confirmPassword: String? = null,
)