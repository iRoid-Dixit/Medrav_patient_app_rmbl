package com.medrevpatient.mobile.app.data.source.remote.dto


import androidx.compose.ui.unit.sp
import com.google.gson.annotations.SerializedName
import com.medrevpatient.mobile.app.ui.canvas.graph.MyProgressBarChartDefaults
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChart
import com.medrevpatient.mobile.app.utils.AppUtils.formatNumberInK
import com.medrevpatient.mobile.app.utils.DateTimeUtils
import com.medrevpatient.mobile.app.utils.ext.extractNumberString
import com.medrevpatient.mobile.app.utils.ext.removeNumberString
import com.medrevpatient.mobile.app.ux.main.component.fromSecToHour
import com.medrevpatient.mobile.app.ux.main.component.fromSecToMin
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.component.trimToOneDecimal

data class LastWeekStats(
    @SerializedName("elementType")
    val elementType: Int = 0,
    @SerializedName("summary")
    val summary: Summary = Summary(),
    @SerializedName("chartData")
    val chartData: List<ChartData> = listOf(),
    @SerializedName("footer")
    val footer: Footer = Footer()
) {

    val element: String get() = elementType.getElementByType()

    data class Summary(
        @SerializedName("highest")
        val highest: Long = 0,
        @SerializedName("average")
        val average: Long = 0,
        @SerializedName("goal")
        val goal: Long = 0
    )

    val highestFormatted: String get() = getFormatMetrics(element, summary.highest).first

    val averageFormatted: String get() = getFormatMetrics(element, summary.average).first

    val goalFormatted: String get() = getFormatMetrics(element, summary.goal).first

    data class ChartData(
        @SerializedName("date")
        val date: Long = 0L,
        @SerializedName("value")
        val value: Long = 0
    ) {
        val formatDate get() = DateTimeUtils.formatUTCToDateTime(date / 1000, "dd MMM")
    }

    data class Footer(
        @SerializedName("metric")
        val metric: String = "",
        @SerializedName("total")
        val total: Long = 0,
        @SerializedName("unit")
        val unit: String = ""
    )

    fun toProgressBarChart(): ProgressBarChart {
        return ProgressBarChart(
            item = chartData.map {

                val value = if (element.getUnit()
                        .lowercase() == "hours"
                ) it.value.fromSecToMin() else it.value.toInt()

                ProgressBarChart.Item(
                    score = value,
                    displayScoreFormat = getFormatMetrics(element, it.value).second,
                    date = it.formatDate.extractNumberString() ?: "-1",
                    month = it.formatDate.removeNumberString() ?: "Unknown"
                )
            },
            scoreStyle = MyProgressBarChartDefaults.scoreStyle.copy(fontSize = 12.sp),
            dateStyle = MyProgressBarChartDefaults.dateStyle.copy(fontSize = 12.sp),
            monthStyle = MyProgressBarChartDefaults.monthStyle.copy(fontSize = 8.sp),
        )
    }

    private fun getFormatMetrics(
        element: String,
        value: Long,
    ): Pair<String, String> {
        when (element.lowercase()) {
            "sleep" -> {
                return Pair(
                    value.fromSecToHour().trimToOneDecimal() + "Hrs",
                    value.fromSecToHour().trimToOneDecimal()
                )
            }

            "exercise" -> {
                return Pair(
                    value.fromSecToMin().toString() + "Min",
                    value.fromSecToMin().formatNumberInK()
                )
            }

            else -> {
                return Pair(
                    value.toString(),
                    value.toInt().formatNumberInK()
                )
            }
        }
    }
}