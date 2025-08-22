package com.medrevpatient.mobile.app.model.domain.request.imagePostionReq

import com.google.gson.annotations.SerializedName

data class ImagePositionReq(
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("position") val position: List<MediaPosition> = emptyList()
)

data class MediaPosition(
    @SerializedName("mediaId") val mediaId: String? = null,
    @SerializedName("position") val position: Int? = null
)
