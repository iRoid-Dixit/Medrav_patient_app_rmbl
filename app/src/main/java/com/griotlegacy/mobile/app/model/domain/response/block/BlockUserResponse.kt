package com.griotlegacy.mobile.app.model.domain.response.block


import com.google.gson.annotations.SerializedName

data class BlockUserResponse(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profileImage")
    val profileImage: String? = null
)

data class UnblockResponse(
    @SerializedName("userId")
    val userId: String? = null,
)

