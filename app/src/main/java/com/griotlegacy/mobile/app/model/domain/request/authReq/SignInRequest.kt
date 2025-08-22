package com.griotlegacy.mobile.app.model.domain.request.authReq

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
)

data class LogoutReq(
    @SerializedName("deviceId") val deviceId: String,
)
data class RefreshTokenReq(
    @SerializedName("refresh_token") val refreshToken: String
)

