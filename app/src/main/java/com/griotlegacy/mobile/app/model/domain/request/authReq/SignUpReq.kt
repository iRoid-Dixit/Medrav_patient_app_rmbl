package com.griotlegacy.mobile.app.model.domain.request.authReq


import com.google.gson.annotations.SerializedName

data class SignUpReq(
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("countryCode") val countryCode: String? = null,
    @SerializedName("mobileNumber") val mobileNumber: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerializedName("gender") val gender: Int? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("confirmPassword") val confirmPassword: String? = null,

)

data class AppUpdateRequest(
    @SerializedName("version") val version: String,
    @SerializedName("type") val type: Int
)