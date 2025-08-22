package com.griotlegacy.mobile.app.model.domain.request.addMember


import com.google.gson.annotations.SerializedName

data class AddMemberRequest(
    @SerializedName("tribeId")
    val tribeId: String? = null,
    @SerializedName("members")
    val members: List<String?>? = null
)

data class GroupMemberRequest(
    @SerializedName("type")
    val type: String,
    @SerializedName("groupId")
    val groupId: String,
    @SerializedName("members")
    val members: List<String>
)

