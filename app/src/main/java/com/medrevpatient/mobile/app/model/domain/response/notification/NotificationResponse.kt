package com.medrevpatient.mobile.app.model.domain.response.notification

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("notificationType") val notificationType: Int? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("tribeId") val tribeId: String? = null,
    @SerializedName("userDetails") val userDetails: UserDetails? = null
)

data class UserDetails(
    @SerializedName("name") val name: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null
)

