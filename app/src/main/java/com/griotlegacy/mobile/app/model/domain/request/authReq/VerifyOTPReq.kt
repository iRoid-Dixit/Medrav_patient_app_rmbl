package com.griotlegacy.mobile.app.model.domain.request.authReq


import com.google.gson.annotations.SerializedName

data class VerifyOTPReq(
    @SerializedName("type") val type: Int? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("otp") val otp: String? = null
)

data class SendOTPReq(
    @SerializedName("type") val type: Int? = null,
    @SerializedName("email") val email: String? = null,
)

data class ForgetPasswordReq(
    @SerializedName("email") val email: String? = null,
)

data class ResetPasswordReq(
    @SerializedName("email") val email: String? = null,
    @SerializedName("newPassword") val newPassword: String? = null,
    @SerializedName("confirmPassword") val confirmPassword: String? = null,
)