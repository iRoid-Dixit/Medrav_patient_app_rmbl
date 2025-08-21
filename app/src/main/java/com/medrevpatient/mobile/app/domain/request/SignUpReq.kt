package com.medrevpatient.mobile.app.domain.request


import android.net.Uri
import com.google.gson.annotations.SerializedName

data class SignUpReq(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("gender") val gender: Int,
)

data class VerifyOTPReq(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: Int,
    @SerializedName("verifyFor") val verifyFor: Int,
)

data class VerifyOTPForUpdateReq(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: Int,
)

data class SendOTPReq(
    @SerializedName("email") val email: String,
    @SerializedName("otpFor") val otpFor: Int
)

data class LoginReq(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class ResetPasswordReq(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class UpdatePasswordReq(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class UpdateProfileReq(
    var isFrom: String = "",
    @SerializedName("birthDate") var birthDate: String = "",
    @SerializedName("heightInFeet") var heightInFeet: Int = 0,
    @SerializedName("heightInInch") var heightInInch: Int = 0,
    @SerializedName("weight") var weight: Int = 0,
    @SerializedName("bodyType") var bodyType: Int = 0,
    @SerializedName("energyLevel") var energyLevel: Int = 0,
    @SerializedName("lifeStyle") var lifeStyle: Int = 0,
    @SerializedName("fitnessLevel") var fitnessLevel: Int = 0,
    @SerializedName("goals") var goals: Int = 0,
    var profileImage: Uri? = null,
    var firstName: String = "",
    var lastName: String = "",
)

data class CreateUpdateReminderReq(
    @SerializedName("programGoal") val programGoalId: String,
    @SerializedName("reminderTime") val reminderTime: String,
    @SerializedName("repeatFrequency") val repeatFrequency: ArrayList<Int>
)

data class RegisterForPushRequest(
    @SerializedName("token") var token: String = "",
    @SerializedName("deviceId") var deviceId: String = "",
    @SerializedName("platform") var platform: Int = 1
)

data class ReportPostRequest(
    @SerializedName("postId") val postId: String = "",
    @SerializedName("reason") val reason: Int = 0,
    @SerializedName("otherReasonText") val otherReasonText: String,
)