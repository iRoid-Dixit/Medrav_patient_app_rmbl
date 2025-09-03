package com.medrevpatient.mobile.app.model.domain.request.authReq

import com.google.gson.annotations.SerializedName

data class LogInRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("role") val role: Int? = null,
)

data class LogoutReq(
    @SerializedName("refresh") val refresh: String,
)
data class RefreshTokenReq(
    @SerializedName("refresh_token") val refreshToken: String
)

