package com.griotlegacy.mobile.app.model.domain.response.chat

import com.google.gson.annotations.SerializedName

data class MessageTabResponse(
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("groupImage") val groupImage: String? = null,
    @SerializedName("groupName") val groupName: String? = null,
    @SerializedName("isAdmin") val isAdmin: Boolean? = false,
    @SerializedName("lastMessage") val lastMessage: String? = null,
    @SerializedName("lastMessageTime") val lastMessageTime: Long? = null,
    @SerializedName("receiverId") val receiverId: String? = null,
    @SerializedName("receiverName") val receiverName: String? = null,
    @SerializedName("receiverProfile") val receiverProfile: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("unreadMessageCount") val unreadMessageCount: Int? = null

)
