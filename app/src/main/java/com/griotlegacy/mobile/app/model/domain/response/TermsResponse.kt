package com.griotlegacy.mobile.app.model.domain.response

import com.google.gson.annotations.SerializedName

data class TermsResponse(
    @SerializedName("title") val title: String? = null,
    @SerializedName("url") val url: String? = null
) 