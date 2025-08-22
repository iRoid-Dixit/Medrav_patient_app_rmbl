package com.griotlegacy.mobile.app.model.domain.response.container.comment

import com.google.gson.annotations.SerializedName

class CommentResponse(
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("commentId") val commentId: String? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("userId") val userId: String? = null
)