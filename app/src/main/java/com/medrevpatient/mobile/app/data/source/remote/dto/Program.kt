package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramsItem

data class Program(
    @SerializedName("completedDays")
    val completedDays: Int = 0,
    @SerializedName("days")
    val days: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("image")
    val image: String = "",
    @SerializedName("kcal")
    val kcal: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("week")
    val week: String = "",
    @SerializedName("isPin")
    val isPin: Boolean = false,
    @SerializedName("dayDistribution")
    val dayDistribution: List<Day> = emptyList()
) {
    fun toProgramsItem() = ProgramsItem(
        url = image,
        title = name,
        calories = "$kcal KCAL",
        time = "$days days",
    )

    data class Day(
        @SerializedName("day")
        val day: Int = 0,
        @SerializedName("completedAt")
        val completedAt: Long = 0L,
        @SerializedName("isRestDay")
        val isRestDay: Boolean = false,
        @SerializedName("isDayCompleted")
        val isDayCompleted: Boolean = false
    )

}