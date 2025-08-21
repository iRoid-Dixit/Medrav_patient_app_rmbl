package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class ExerciseCompleted(
    @SerializedName("history")
    val history: History = History(),
    @SerializedName("recipes")
    val recipes: List<Recipe> = emptyList(),
    @SerializedName("SpentTime")
    val spentTime: Long = 0,
    @SerializedName("Exercises")
    val exercises: Int = 0,
) {
    data class History(
        @SerializedName("userId")
        val userId: String = "",
        @SerializedName("dayId")
        val dayId: String = "",
        @SerializedName("exerciesId")
        val exerciseId: List<ExerciesId> = emptyList(),
        @SerializedName("isCompleted")
        val isCompleted: Boolean = false,
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("createdAt")
        val createdAt: Long = 0L,
        @SerializedName("updatedAt")
        val updatedAt: Long = 0L,
        @SerializedName("__v")
        val v: Int = 0
    ) {
        data class ExerciesId(
            @SerializedName("exercise")
            var exercise: String? = null,
            @SerializedName("time")
            var time: Int? = null,
            @SerializedName("_id")
            var Id: String? = null
        )
    }

}