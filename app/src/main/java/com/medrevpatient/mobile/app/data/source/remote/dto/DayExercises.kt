package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName

data class DayExercises(
    @SerializedName("_id")
    val id: String = "",
    @SerializedName("day")
    val day: Int = 0,
    @SerializedName("equipments")
    val equipments: List<Equipment> = emptyList(),
    @SerializedName("exercises")
    val exercises: List<Exercise> = emptyList(),
    @SerializedName("idealTime")
    val idealTime: String = "",
    @SerializedName("totalExercise")
    val totalExercise: Int = 0,
    @SerializedName("programId")
    val programId: ProgramId = ProgramId()
) {

    data class Equipment(
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("icon")
        val icon: String = ""
    )

    data class Exercise(
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("sets")
        val sets: Int = 0,
        @SerializedName("reps")
        val reps: Int = 0,
        @SerializedName("rest")
        val rest: Long = 0,
        @SerializedName("type")
        val type: String = "",
        @SerializedName("gif")
        val gif: String = "",
        @SerializedName("isCompleted")
        val isCompleted: Boolean = false,
        @SerializedName("time")
        val spendTime: Long = 0
    ) {

        private val restDuration get() = formatTimeFromSecondsToHHMMSSShort(seconds = rest)
        val bulletPoints
            get() = listOf(
                "$sets Sets",
                "$reps Reps",
                restDuration + "Rest"
            )
    }

    data class ProgramId(
        @SerializedName("_id")
        val id: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("kcal")
        val kcal: String = "",
        @SerializedName("days")
        val days: String = "",
        @SerializedName("description")
        val description: String = ""
    )
}


fun formatTimeFromSecondsToHHMMSSShort(seconds: Long): String {
    val durationFormat = mutableListOf("")
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val extractedSecond = seconds % 60

    if (hours > 0) {
        durationFormat.add("$hours Hrs")
    }

    if (minutes > 0) {
        durationFormat.add("$minutes Mins")
    }

    if (extractedSecond > 0) {
        durationFormat.add("$extractedSecond Secs")
    }
    //Output for 3700 :- "1 Hrs 1 Mins 40 Secs"
    return durationFormat.joinToString(" ")
}