package com.medrevpatient.mobile.app.data.source.remote.dto


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.utils.DateTimeUtils
import com.medrevpatient.mobile.app.utils.DateTimeUtils.DateFormats.ddMMyyyy
import com.medrevpatient.mobile.app.ux.main.programandworkout.model.StrengthTable

data class StrengthLog(
    @SerializedName("createdAt")
    val createdAt: String = "",

    @SerializedName("date")
    val date: Long = 0L,

    @SerializedName("exercisesId")
    val exercisesId: ExercisesId? = ExercisesId(),

    @SerializedName("_id")
    val id: String = "",

    @SerializedName("lb")
    val lb: Int = 0,

    @SerializedName("reps")
    val reps: Int = 0,

    @SerializedName("sets")
    val sets: Int = 0,

    @SerializedName("userId")
    val userId: String = "",

    @SerializedName("__v")
    val v: Int = 0
) {

    data class ExercisesId(
        @SerializedName("_id")
        val Id: String = "",
        @SerializedName("name")
        val name: String = ""
    )

    fun toStrengthTable(): StrengthTable {
        return StrengthTable(
            date = DateTimeUtils.formatUTCToDateTime(date, ddMMyyyy.label),
            exercise = exercisesId?.name ?: "Unknown",
            sets = sets.toString(),
            lb = lb.toString(),
            reps = reps.toString()
        )
    }

}