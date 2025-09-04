package com.medrevpatient.mobile.app.model.domain.response.auth


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserAuthResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("role") val role: Int? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerializedName("gender") val gender: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("isEmailVerified") val isEmailVerified: Boolean? = false,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("passwordChangedAt") val passwordChangedAt: Long? = null,
    @SerializedName("street") val street: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("zipCode") val zipCode: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("height") val height: String? = null,
    @SerializedName("heightDisplay") val heightDisplay: String? = null,
    @SerializedName("weight") val weight: String? = null,
    @SerializedName("weightDisplay") val weightDisplay: String? = null,
    @SerializedName("bmi") val bmi: String? = null,
    @SerializedName("bmiCategory") val bmiCategory: String? = null,
    @SerializedName("knownAllergies") val knownAllergies: String? = null,
    @SerializedName("currentMedications") val currentMedications: String? = null,
    @SerializedName("medicalConditions") val medicalConditions: String? = null,
    @SerializedName("emergencyContactName") val emergencyContactName: String? = null,
    @SerializedName("emergencyContactPhone") val emergencyContactPhone: String? = null,
    @SerializedName("authToken") val authToken: Auth? = null,
) : Serializable

data class Auth(
    @SerializedName("tokenType") val tokenType: String? = null,
    @SerializedName("expiresIn") val expiresIn: Long? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("refreshToken") val refreshToken: String? = null
)

data class Token(
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Long,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)