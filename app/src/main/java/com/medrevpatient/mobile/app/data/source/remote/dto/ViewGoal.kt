package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.domain.response.Meta
import com.medrevpatient.mobile.app.utils.DateTimeUtils

data class ViewGoal(
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val data: Data = Data()
) {
    data class Data(
        @SerializedName("goal")
        val goal: Goal = Goal(),
        @SerializedName("logs")
        val logs: List<Log> = emptyList(),
        @SerializedName("Completed")
        val completed: Long = 0,
        @SerializedName("meta")
        val meta: Meta = Meta()
    ) {

        data class Log(
            @SerializedName("value")
            val value: Long = 0,
            @SerializedName("createdAt")
            val createdAt: Long = 0L,
        ) {

            val formatedDate
                get() = DateTimeUtils.formatUTCToDateTime(
                    timeStamp = createdAt / 1000,
                    targetDateFormat = "MMM dd, yyyy"
                )

            val formatedTime
                get() = DateTimeUtils.formatUTCToDateTime(
                    timeStamp = createdAt / 1000,
                    targetDateFormat = "hh:mm a"
                )
        }
    }
}