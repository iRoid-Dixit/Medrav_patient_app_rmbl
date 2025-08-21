package com.medrevpatient.mobile.app.domain.response


import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("auth")
    var auth: Auth? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("gender")
    val gender: Int? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean = false,
    @SerializedName("birthDate")
    val birthDate: Long? = null,
    @SerializedName("heightInFeet")
    val heightInFeet: Int? = null,
    @SerializedName("heightInInch")
    val heightInInch: Int? = null,
    @SerializedName("weight")
    val weight: Int? = null,
    @SerializedName("bodyType")
    val bodyType: Int? = null,
    @SerializedName("energyLevel")
    val energyLevel: Int? = null,
    @SerializedName("lifeStyle")
    val lifeStyle: Int? = null,
    @SerializedName("fitnessLevel")
    val fitnessLevel: Int? = null,
    @SerializedName("goals")
    val goals: Int? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null,
    @SerializedName("isCommunityGuidelineAccepted")
    val isCommunityGuidelineAccepted: Boolean = false
)

data class Auth(
    @SerializedName("accessToken")
    val accessToken: String? = null,
    @SerializedName("expiresIn")
    val expiresIn: Long? = null,
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    @SerializedName("tokenType")
    val tokenType: String? = null
)