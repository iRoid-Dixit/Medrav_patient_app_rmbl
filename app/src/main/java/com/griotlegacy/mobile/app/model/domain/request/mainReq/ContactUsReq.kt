package com.griotlegacy.mobile.app.model.domain.request.mainReq

import com.google.gson.annotations.SerializedName

class ContactUsReq(
    @SerializedName("name") val name: String? = null,
    @SerializedName("message") val message: String? = null,

)
