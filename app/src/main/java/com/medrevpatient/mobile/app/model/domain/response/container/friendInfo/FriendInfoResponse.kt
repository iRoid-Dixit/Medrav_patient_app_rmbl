package com.medrevpatient.mobile.app.model.domain.response.container.friendInfo

import com.google.gson.annotations.SerializedName

data class FriendInfoResponse(
    @SerializedName("dateOfBirth") val dateOfBirth: Long? = null,
    @SerializedName("gender") val gender: Int? = null,
    @SerializedName("innerCircleId") val innerCircleId: String? = null,
    @SerializedName("isMemberOfInnerCircle") var isMemberOfInnerCircle: Boolean? = false,
    @SerializedName("isProfilePrivate") val isProfilePrivate: Boolean? = false,
    @SerializedName("name") val name: String? = null,
    @SerializedName("posts") val posts: List<Post> = emptyList(),
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("tribeId") val tribeId: String? = null
)

data class Post(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("albumName") val albumName: String? = null,
    @SerializedName("commentCount") val commentCount: Int? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("isOwner") val isOwner: Boolean? = null,
    @SerializedName("legacyText") val legacyText: String? = null,
    @SerializedName("likeCount") val likeCount: Int? = null,
    @SerializedName("media") val media: List<Media> = emptyList(),
    @SerializedName("mediaType") val mediaType: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("ownLike") val ownLike: Boolean = false,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("tribeList") val tribeList: List<String> = emptyList(),
    @SerializedName("type") val type: Int? = null,
    @SerializedName("userId") val userId: String? = null
)

data class Media(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("filename") val filename: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("type") val type: Int? = null
)
