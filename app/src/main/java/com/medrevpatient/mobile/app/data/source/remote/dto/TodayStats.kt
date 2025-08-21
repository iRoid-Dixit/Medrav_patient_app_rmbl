package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.ux.main.component.FormatType
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getUnitWithSortForm

data class TodayStats(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("type")
    val goalType: Int = 0,
    @SerializedName("value")
    val goal: Long = 0,
    @SerializedName("totalCompletedValue")
    val completedGoal: Long = 0
) {

    val element get() = goalType.getElementByType()
    private val unit get() = element.getUnitWithSortForm()

    val log get() = completedGoal.formatLogValue(type = FormatType.TIME, goalType = goalType)
    val goalFormatted get() = goal.formatLogValue(goalType) + " $unit"
}


val elementsPlaceholder = listOf(
    TodayStats(goalType = 1, goal = 0, completedGoal = 0),
    TodayStats(goalType = 2, goal = 0, completedGoal = 0),
    TodayStats(goalType = 3, goal = 0, completedGoal = 0),
    TodayStats(goalType = 4, goal = 0, completedGoal = 0),
    TodayStats(goalType = 5, goal = 0, completedGoal = 0),
    TodayStats(goalType = 6, goal = 0, completedGoal = 0),
)