package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType.FOR_GENERAL

data class OnDemandClasses(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("videoUrl")
    val videoUrl: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("duration")
    val duration: String = "",
    @SerializedName("level")
    val level: Int = 0,
    @SerializedName("videoTitle")
    val videoTitle: String = "",
    @SerializedName("type")
    val type: Int = FOR_GENERAL.value
) {

    val levelDescription
        get() = when (level) {
            1 -> "All Level"
            2 -> "Beginner"
            3 -> "Intermediate"
            4 -> "Expert"
            else -> "All Level"
        }
}