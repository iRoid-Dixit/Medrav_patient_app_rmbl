package com.medrevpatient.mobile.app.model.domain.response.chat

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    /* @SerializedName("_id")
     val id: String? = "",
     @SerializedName("senderId")
     val senderId: String? = "",
     @SerializedName("receiverId")
     val receiverId: String? = "",
     @SerializedName("message")
     val message: String? = "",
     @SerializedName("createdAt")
     val createdAt: Long? = 0,*/
    @SerializedName("id") val id: String? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("groupId") val groupId: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("senderId") val senderId: String? = null
)

data class NewMessageResponse(
    @SerializedName("type")
    val type: Int? = 0,
    @SerializedName("roomId")
    val roomId: String? = "",
    @SerializedName("senderId")
    val senderId: String? = "",
    @SerializedName("receiverId")
    val receiverId: String? = "",
    @SerializedName("adminId")
    val adminId: String? = "",
    @SerializedName("message")
    val message: String? = "",
    @SerializedName("senderName")
    val senderName: String? = "",
    @SerializedName("receiverName")
    val receiverName: String? = "",
    @SerializedName("profileImage")
    val profileImage: String? = "",
    @SerializedName("groupId")
    val groupId: String? = "",


)