package com.medrevpatient.mobile.app.model.domain.response.auth


import com.google.gson.annotations.SerializedName

data class AppUpdateResponse(
    @SerializedName("app_link") val appLink: String? = null,
    @SerializedName("response") val response: Int? = null,
    @SerializedName("latestVersion") val latestVersion: String? = null
)