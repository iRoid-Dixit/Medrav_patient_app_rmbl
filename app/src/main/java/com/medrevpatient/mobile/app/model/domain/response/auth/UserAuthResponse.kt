package com.medrevpatient.mobile.app.model.domain.response.auth


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserAuthResponse(
   @SerializedName("_id") val id: String?=null,
   @SerializedName("auth") val auth: Auth?=null,
   @SerializedName("countryCode") val countryCode: String?=null,
   @SerializedName("dateOfBirth") val dateOfBirth: Long?=null,
   @SerializedName("email") val email: String?=null,
   @SerializedName("gender") val gender: Int?=null,

   @SerializedName("isDeleted") val isDeleted: Boolean?=false,
   @SerializedName("isNotificationEnabled") val isNotificationEnabled: Boolean?=false,
   @SerializedName("isProfileCompleted") val isProfileCompleted: Boolean?=false,
   @SerializedName("isProfilePrivate") val isProfilePrivate: Boolean?=false,
   @SerializedName("isVerify") val isVerify: Boolean?=false,
   @SerializedName("mobileNumber") val mobileNumber: String?=null,
   @SerializedName("name") val name: String?=null,
   @SerializedName("profileImage") val profileImage: String?=null,
   @SerializedName("deepLink") val deepLink: String? = null,

): Serializable

data class Auth(
    @SerializedName("accessToken") val accessToken: String?=null,
    @SerializedName("expiresIn") val expiresIn: Long?=null,
    @SerializedName("refreshToken") val refreshToken: String?=null,
    @SerializedName("tokenType") val tokenType: String?=null
)
data class Token(
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)