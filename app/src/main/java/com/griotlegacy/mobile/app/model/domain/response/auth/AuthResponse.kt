package com.griotlegacy.mobile.app.model.domain.response.auth


import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("is_email_verified") val isEmailVerified: Boolean
)