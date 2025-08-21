package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class MyProgress(
    @SerializedName("lastweeksatats")
    val lastWeekStats: List<LastWeekStats> = listOf(),
    @SerializedName("todayStates")
    val todayStates: List<TodayStats> = listOf(),
    @SerializedName("mygoal")
    val myGoal: List<Goal> = listOf()
)