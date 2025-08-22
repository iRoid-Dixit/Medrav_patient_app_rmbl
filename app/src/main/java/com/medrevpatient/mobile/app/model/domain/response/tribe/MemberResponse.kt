package com.medrevpatient.mobile.app.model.domain.response.tribe


import com.google.gson.annotations.SerializedName

data class MemberResponse(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("isOnline")
    val isOnline: Boolean? = null,

    @SerializedName("profileImage")
    val profileImage: String? = null
)