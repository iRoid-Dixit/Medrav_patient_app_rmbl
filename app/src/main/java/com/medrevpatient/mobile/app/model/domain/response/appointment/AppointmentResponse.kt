package com.medrevpatient.mobile.app.model.domain.response.appointment

import com.google.gson.annotations.SerializedName

data class AppointmentResponse(
    @SerializedName("id")  val id: Int,
    @SerializedName("displayId") val displayId: String,
    @SerializedName("status") val status: Int,
    @SerializedName("appointmentTimestamp") val appointmentTimestamp: Long,
    @SerializedName("appointmentType") val appointmentType: Int,
    @SerializedName("subject") val subject: String,
    @SerializedName("doctorInfo") val doctorInfo: DoctorInfo,
    @SerializedName("joinVideoCall") val joinVideoCall: Boolean,
    @SerializedName("reschedule") val reschedule: Boolean,
    @SerializedName("cancelAppointment") val cancelAppointment: Boolean,
    @SerializedName("postAppointment") val postAppointment: Boolean
)
data class DoctorInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("specialization") val specialization: String,
    @SerializedName("experience") val experience: Int,
    @SerializedName("qualification") val qualification: String,
    @SerializedName("affiliatedHospital") val affiliatedHospital: String,
    @SerializedName("bio") val bio: String,
    @SerializedName("isVerified")  val isVerified: Int,
    @SerializedName("profileImage")  val profileImage: String
)