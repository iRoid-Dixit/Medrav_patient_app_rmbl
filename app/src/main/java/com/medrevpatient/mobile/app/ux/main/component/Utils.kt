package com.medrevpatient.mobile.app.ux.main.component

import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import java.text.DecimalFormat
import java.util.Locale

fun String.getUnit(): String {
    val units = mapOf(
        "calories" to "KCAL",
        "water" to "Fl Oz",
        "sleep" to "Hours",
        "protein" to "Grams",
        "exercise" to "Hours",
        "weight" to "LBS",
    )

    //Output: KCAL if key = "calories"
    return units[this.lowercase()] ?: ""
}

fun String.getUnitWithSortForm(): String {
    val units = mapOf(
        "calories" to "KCAL",
        "water" to "Fl Oz",
        "sleep" to "Hrs",
        "protein" to "Gr",
        "exercise" to "Hrs",
        "weight" to "LBS",
    )

    //Output: KCAL if key = "calories"
    return units[this.lowercase()] ?: ""
}


/**
 * pass key in lowercase so it would give accurate result.
 * */
fun String.getIcon(): Int? {
    val icons = mapOf(
        "calories" to drawable.calorie,
        "water" to drawable.water,
        "sleep" to drawable.sleep,
        "steps" to drawable.steps,
        "protein" to drawable.protein,
        "exercise" to drawable.exercise,
        "weight" to drawable.weight,
    )

    //Output: drawable.calories if key = "calories"
    return icons[this.lowercase()]
}


fun Int.getElementByType(): String {
    return when (this) {
        1 -> "Calories"
        2 -> "Water"
        3 -> "Sleep"
        4 -> "Protein"
        5 -> "Steps"
        6 -> "Exercise"
        7 -> "Weight"
        else -> "Unknown"
    }
}

fun String.getTypeByElement(): Int {
    return when (this.lowercase()) {
        "calories" -> 1
        "water" -> 2
        "sleep" -> 3
        "protein" -> 4
        "steps" -> 5
        "exercise" -> 6
        "weight" -> 7
        else -> 0
    }
}


fun String.getCreateGoalLogInfo(): String {
    return when (this.lowercase()) {
        "calories" -> "You will have to add Calories logs when you gain new calories."
        "water" -> "You will have to add Water logs when you drink water."
        "sleep" -> "You will have to add a sleep log for your sleep duration."
        "protein" -> "You will have to log your protein intake during your meals."
        "steps" -> "Your Steps will be counted based on your walking activity."
        "exercise" -> "Your hours will be counted based on your completed workouts."
        "weight" -> "You will have to add Weight logs"
        else -> ""
    }
}

fun String.getAddLogInfo(): String {
    return when (this.lowercase()) {
        "calories" -> "Log your caloric intake to better understand your eating habits."
        "water" -> "Keep a daily track of your water intake, enter in fluid ounces. "
        "sleep" -> "Keep a daily track and enter how many hours you slept last night."
        "protein" -> "Keep a daily track of your protein intake, enter in grams."
        "steps" -> "Add your daily step count to track your activity."
        "exercise" -> "Keep a daily track and enter how many hours you did an exercise today."
        "weight" -> "Keep a daily track and enter how many steps you walked today."
        else -> ""
    }
}


fun String.getEditGoalInfo(): String {
    return when (this.lowercase()) {
        "calories" -> "Log your calories right after your meal to stay on track."
        "water" -> "Log your water intake to stay hydrated."
        "sleep" -> "Log your sleep hours to monitor your rest."
        "protein" -> "Log your protein intake for better muscle recovery."
        "steps" -> "Your step count to stay on top of your activity goals."
        "exercise" -> "Log your workout hours to keep track of your fitness progress."
        "weight" -> "Log your weight to monitor your progress over time."
        else -> ""
    }
}


enum class FormatType {
    HOURS, TIME
}


fun Long.formatLogValue(goalType: Int, type: FormatType = FormatType.HOURS): String {
    val unit = goalType.getElementByType().getUnit().lowercase()
    return when (type) {
        FormatType.HOURS -> {
            if (unit == "hours") {
                val value = (this.toDouble() / 3600)
                formatToOneDecimalOrInteger(value)
            } else {
                this.toString()
            }
        }

        FormatType.TIME -> {
            if (unit == "hours") {
                val hours = this / 3600
                val minutes = (this % 3600) / 60
                String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
            } else {
                this.toString()
            }
        }
    }
}


fun Long.secondsSplitterToHHMM(): Pair<Long, Long> {
    return Pair(this / 3600, (this % 3600) / 60)
}


fun Long.decimalHourSplitter(goalType: Int): Pair<String, String> { //hours and minutes
    val unit = goalType.getElementByType().getUnit().lowercase()
    return if (unit == "hours") {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        return Pair(hours.toString(), minutes.toString())
    } else {
        Pair(this.toString(), "")
    }
}

fun Long.fromSecToMin(): Int {
    return (this / 60).toInt()
}

fun Long.fromSecToHour(): Double {
    return (this / 3600.0)
}

fun Double.trimToOneDecimal(): String {
    val df = DecimalFormat("0.#")
    val formattedNumber = df.format(this)
    return formattedNumber ?: "0"
}


fun String.convertToNumType(element: String): Int {
    val unit = element.getUnit().lowercase()
    return if (unit == "hours") {
        (this.toInt() * 3600)
    } else {
        this.substringBefore('.').toInt()
    }
}

fun convertHHMMtoSS(element: String, value: String, value2: String): Int {
    val unit = element.getUnit()
    val v = value.toIntOrNull() ?: 0
    val v2 = value2.toIntOrNull() ?: 0
    return if (unit.lowercase() == "hours") v * 3600 + v2 * 60 else v
}


fun formatToOneDecimalOrInteger(value: Double): String {
    val formatted = String.format(Locale.getDefault(), "%.1f", value)
    return if (formatted.endsWith(".0")) {
        formatted.substringBefore(".")
    } else {
        formatted
    }
}

fun findUniqueValueInList(fList: List<Int>, sList: List<Int>): List<Int> {
    val uniqueNumbers = (fList + sList)
        .groupingBy { it }
        .eachCount()
        .filter { it.value == 1 }  // Keep only numbers appearing once
        .keys
    return uniqueNumbers.toList()
}


fun List<Program.Day>.findFirstIncompleteDay(): Program.Day? {
    return this.firstOrNull { !it.isDayCompleted }
}