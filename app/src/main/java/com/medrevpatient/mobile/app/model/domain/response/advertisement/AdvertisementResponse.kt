package com.medrevpatient.mobile.app.model.domain.response.advertisement

import com.google.gson.annotations.SerializedName

data class AdvertisementResponse(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("companyName") val companyName: String? = null,
    @SerializedName("contactPerson") val contactPerson: String? = null,
    @SerializedName("countryCode") val countryCode: String? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("endDate") val endDate: Long? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("isExpired") val isExpired: Boolean = false,
    @SerializedName("link") val link: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("physicalAddress") val physicalAddress: String? = null,
    @SerializedName("purpose") val purpose: String? = null,
    @SerializedName("startDate") val startDate: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("isAdminApproveStatus") val isAdminApproveStatus: Int? = null,
    @SerializedName("advertisementStatus") val advertisementStatus: Int? = null,
    @SerializedName("rejectReason") val rejectReason: String? = null
)
