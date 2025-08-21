package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.utils.DateTimeUtils
import java.time.Instant

data class Note(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("body")
    val body: String = "",
    @SerializedName("createdAt")
    val createdAt: Long = 0,
    @SerializedName("updatedAt")
    val updatedAt: Long = 0
) {
    val formattedDate
        get() = DateTimeUtils.formatUTCToDateTime(
            (if (updatedAt <= 0) Instant.now().toEpochMilli() else updatedAt) / 1000, "dd MMM yyyy"
        )
}
