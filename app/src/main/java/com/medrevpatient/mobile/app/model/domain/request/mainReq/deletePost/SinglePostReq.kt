package com.medrevpatient.mobile.app.model.domain.request.mainReq.deletePost

import com.google.gson.annotations.SerializedName

class SinglePostReq(
    @SerializedName("postId") val postId: String? = null,
    @SerializedName("mediaId") val mediaId: String? = null,
)

class DeleteLegacyPostReq(
    @SerializedName("postId") val postId: String? = null,

    )

