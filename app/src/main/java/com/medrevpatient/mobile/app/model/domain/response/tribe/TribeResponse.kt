package com.medrevpatient.mobile.app.model.domain.response.tribe

import com.google.gson.annotations.SerializedName

data class TribeResponse(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("totalMembers")
    val totalMembers: Int? = 0,
    @SerializedName("members")
    val members: List<Member?>? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("createdAt")
    val createdAt: Long? = null
)

data class Member(
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("invitedStatus")
    val invitedStatus: Int? = null,
    @SerializedName("isBlocked")
    val isBlocked: Boolean? = null,
    @SerializedName("_id")
    val id: String? = null
)
