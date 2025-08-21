package com.medrevpatient.mobile.app.domain.response


import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChart
import com.medrevpatient.mobile.app.utils.DateTimeUtils
import com.medrevpatient.mobile.app.utils.ext.extractNumberString
import com.medrevpatient.mobile.app.utils.ext.removeNumberString
import com.medrevpatient.mobile.app.ux.main.component.fromSecToMin
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import java.io.Serializable

data class HomeScreenResponse(
    @SerializedName("goalLogs")
    val goalLogs: GoalLogs? = null,
    @SerializedName("pinData")
    val pinData: PinData? = null,
    @SerializedName("popularItems")
    val popularItems: ArrayList<PopularItem> = arrayListOf(),
    @SerializedName("specificGoals")
    val specificGoals: ArrayList<SpecificGoal> = arrayListOf()
) : Serializable

data class GoalLogs(
    @SerializedName("chartData")
    val chartData: ArrayList<ChartData> = arrayListOf(),
    @SerializedName("elementType")
    val elementType: Int = 0,
    @SerializedName("footer")
    val footer: Footer? = null
) : Serializable {

    fun toProgressBarChart(): ProgressBarChart {
        return ProgressBarChart(
            item = chartData.map {

                val formatDate = DateTimeUtils.formatUTCToDateTime((it.date) / 1000, "dd MMM")

                val value = if (elementType.getElementByType()
                        .lowercase() == "hours"
                ) it.value.toLong().fromSecToMin() else it.value

                ProgressBarChart.Item(
                    score = value,
                    date = formatDate.extractNumberString() ?: "-1",
                    month = formatDate.removeNumberString() ?: "Unknown"
                )
            },
        )
    }
}

data class PinData(
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("kcal")
    val kcal: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("days")
    val days: String? = null
) : Serializable

data class PopularItem(
    @SerializedName("type")
    val type: Int? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("programId")
    val programId: String? = null
) : Serializable

data class PopularItemData(
    @SerializedName("description")
    val description: String?,
    @SerializedName("difficultyLevel")
    val difficultyLevel: Int?,
    @SerializedName("exercises")
    val exercises: Exercise? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("nutritionalCategory")
    val nutritionalCategory: ArrayList<String> = arrayListOf(),
    @SerializedName("programId")
    val programId: ProgramId? = null,
) : Serializable

data class ProgramId(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("image")
    val image: String? = null
) : Serializable

data class SpecificGoal(
    @SerializedName("type")
    val goalType: Int? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("value")
    val value: Long? = null,
    @SerializedName("totalLogValue")
    val totalLogValue: Long? = null
) : Serializable

data class Goal(
    @SerializedName("type")
    val goalType: Int? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("value")
    val value: Int? = null
) : Serializable

data class ChartData(
    @SerializedName("date")
    val date: Long = 0L,
    @SerializedName("value")
    val value: Int = 0
) : Serializable

data class Ingredient(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("item")
    val item: String? = null,
    @SerializedName("quantity")
    val quantity: String? = null,
    @SerializedName("unit")
    val unit: String? = null
) : Serializable

data class Footer(
    @SerializedName("metric")
    val metric: String? = null,
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("unit")
    val unit: String? = null
) : Serializable

data class Exercise(
    @SerializedName("gif")
    val gif: String? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("reps")
    val reps: Int? = null,
    @SerializedName("rest")
    val rest: Int? = null,
    @SerializedName("sets")
    val sets: Int? = null,
    @SerializedName("type")
    val type: String? = null
) : Serializable