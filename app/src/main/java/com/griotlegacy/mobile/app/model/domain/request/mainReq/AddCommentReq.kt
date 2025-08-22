package com.griotlegacy.mobile.app.model.domain.request.mainReq

import com.google.gson.annotations.SerializedName

class AddCommentReq (
    @SerializedName("comment") val comment: String? = null,

)