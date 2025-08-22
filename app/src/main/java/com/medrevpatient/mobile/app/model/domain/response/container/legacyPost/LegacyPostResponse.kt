package com.medrevpatient.mobile.app.model.domain.response.container.legacyPost

import com.google.gson.annotations.SerializedName

data class LegacyPostResponse(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("userId") val userId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("profileImage") val profileImage: String? = null,
    @SerializedName("albumName") val albumName: String? = null,
    @SerializedName("commentCount") var commentCount: Int? = null,
    @SerializedName("ownLike") var ownLike: Boolean? = null,
    @SerializedName("createdAt") val createdAt: Long? = null,
    @SerializedName("legacyText") val legacyText: String? = null,
    @SerializedName("likeCount") var likeCount: Int? = null,
    @SerializedName("media") val media: List<Media> = emptyList(),
    @SerializedName("mediaType") val mediaType: Int? = null,
    @SerializedName("tribeList") val tribeList: List<String> = emptyList(),
    @SerializedName("type") val type: Int? = null,
) {
    override fun equals(other: Any?): Boolean {
        return (other as? LegacyPostResponse)?.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class Media(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("filename") val filename: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("type") val type: Int? = null,
    val isEdited: Boolean = false
)

data class AddImageLegacyPostResponse(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("type") val type: Int? = null,
    @SerializedName("filename") val filename: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,

    )
