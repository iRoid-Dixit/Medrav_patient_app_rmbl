package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.utils.DateTimeUtils.formatUTCToDateTime
import com.medrevpatient.mobile.app.ux.main.component.decimalHourSplitter
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getTypeByElement
import com.medrevpatient.mobile.app.ux.main.myprogress.MyGoalsItem

data class Goal(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("type")
    val goalType: Int = 0,
    @SerializedName("value")
    val goalValue: Long = 0,
    @SerializedName("image")
    val image: List<Image> = emptyList(),
    @SerializedName("userId")
    val userId: String = "",
    @SerializedName("createdAt")
    val createdAt: Long = 0L,
    @SerializedName("LogValue")
    val logValue: Long = 0,
) {

    val date get() = formatUTCToDateTime(createdAt / 1000, "MMM dd, yyyy")

    val element get() = goalType.getElementByType()
    //    private val unit get() = element.getUnit()

    val log get() = logValue.formatLogValue(goalType)
    val goal get() = goalValue.formatLogValue(goalType)

    fun toGoalItem(): MyGoalsItem {
        return MyGoalsItem(
            id = id,
            value = goalValue.decimalHourSplitter(element.getTypeByElement()).first,
            value2 = goalValue.decimalHourSplitter(element.getTypeByElement()).second,
            element = element,
            creationOn = date,
        )
    }


    data class Image(
        @SerializedName("createdAt")
        val createdAt: Long = 0L,
        @SerializedName("imageUrl")
        val url: String = "",
    ) {
        val date get() = formatUTCToDateTime(createdAt / 1000, "MMM dd, yyyy")

    }
}