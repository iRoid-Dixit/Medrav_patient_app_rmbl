package com.medrevpatient.mobile.app.model.domain.request.report

import com.google.gson.annotations.SerializedName

class ReportUserPostReq(
    @SerializedName("type") val type: Int? = null,
    @SerializedName("id") val id: String? = null,
)
