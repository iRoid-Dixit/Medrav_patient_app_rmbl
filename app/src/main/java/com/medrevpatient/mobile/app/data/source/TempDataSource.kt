package com.medrevpatient.mobile.app.data.source


import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises.Equipment
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.data.source.remote.dto.Program.Day
import com.medrevpatient.mobile.app.data.source.remote.dto.ViewGoal.Data.Log
import com.medrevpatient.mobile.app.ui.canvas.graph.MyProgressBarChartDefaults
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChart
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChart.Item
import com.medrevpatient.mobile.app.utils.AppUtils.formatNumberInK
import kotlin.random.Random

object TempDataSource {

    data class LastWeekStats(
        val header: List<Pair<Int, String>>,
        val graph: ProgressBarChart,
        val footer: Footer
    ) {
        data class Footer(
            val icon: Int,
            val title: String,
            val value: String
        )
    }

    val progressBarData = List(8) { index ->
        Item(
            score = index * 25,
            displayScoreFormat = (index * 25).formatNumberInK(),
            month = "Jan",
            date = "0${index + 1}"
        )
    }

    val daysList = List(15) { day ->
        Day(
            day = day + 1,
            completedAt = System.currentTimeMillis(),
            isRestDay = Random.nextBoolean(),
            isDayCompleted = day < 8
        )
    }

    val lastWeekStats = LastWeekStats(
        header = listOf(
            273 to "Mon",
            109 to "Tue",
            40019 to "Wed",
        ),
        graph = ProgressBarChart(
            item = progressBarData,
            monthStyle = MyProgressBarChartDefaults.monthStyle.copy(
                fontSize = 6.sp
            ),
            dateStyle = MyProgressBarChartDefaults.dateStyle.copy(
                fontSize = 10.sp
            ),
            scoreStyle = MyProgressBarChartDefaults.scoreStyle.copy(
                fontSize = 8.sp
            )
        ),
        footer = LastWeekStats.Footer(
            icon = R.drawable.calorie,
            title = "Calories",
            value = "563"
        )
    )


    val tabList = listOf("Calories", "Water", "Sleep", "Protein", "Steps", "Exercise", "Weight")


    val samplePrograms = listOf(
        Program(id = "1", name = "Program 1", image = "url1"),
        Program(id = "2", name = "Program 2", image = "url2"),
        Program(id = "3", name = "Program 3", image = "url3")
    )

    val dayExercises = DayExercises(
        exercises = listOf(
            DayExercises.Exercise(
                name = "Push Up",
                type = "Chest",
                sets = 3,
                reps = 10,
                rest = 30
            ),
            DayExercises.Exercise(
                name = "Pull Up",
                type = "Back",
                sets = 3,
                reps = 10,
                rest = 30
            ),
            DayExercises.Exercise(
                name = "Squats",
                type = "Abs",
                sets = 3,
                reps = 10,
                rest = 30
            ),
            DayExercises.Exercise(
                name = "Crunches",
                type = "Abs",
                sets = 3,
                reps = 10,
                rest = 30
            ),
        ),
        totalExercise = 4,
        idealTime = "30 Minutes",
        equipments = listOf(
            Equipment(name = "Dumbbell", icon = ""),
            Equipment(name = "Bench", icon = ""),
            Equipment(name = "Barbell", icon = ""),
            Equipment(name = "Kettlebell", icon = ""),
        )
    )

    val viewGoalPagingData = listOf(
        Log(
            value = 100,
            createdAt = 0L,
        ),
        Log(
            value = 150,
            createdAt = 0L,
        )
    )

    val sampleNotes: List<Note> = List(5) { index ->
        Note(
            id = index.toString(),
            title = "Note $index",
            body = "This is the body of note $index."
        )
    }

}

//TODO:(below To Hide Top bar)

// This is a sample using NestedScroll and Pager. // We use the toolbar offset changing example from // androidx. compose. ui. samples. NestedScrollConnectionSample
/*
val pagerState = rememberPagerState { 10 }
val toolbarHeight = 48.dp
val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
val nestedScrollConnection = remember {
    object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val newOffset =
                toolbarOffsetHeightPx.value + delta toolbarOffsetHeightPx . value = newOffset . coerceIn (-toolbarHeightPx, 0f)             return Offset. Zero
        }
    }
} Box (modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
    TopAppBar(
        modifier = Modifier.height(toolbarHeight)
            .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
        title = { Text("Toolbar offset is ${toolbarOffsetHeightPx.value}") })
    val paddingOffset =
        toolbarHeight + with(LocalDensity.current) { toolbarOffsetHeightPx.value.toDp() } HorizontalPager (modifier =
            Modifier.fillMaxSize(), state = pagerState, contentPadding = PaddingValues(top = paddingOffset)) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        repeat(20) {
            Box(
                modifier = Modifier.fillMaxWidth().height(64.dp).padding(4.dp)
                    .background(if (it % 2 == 0) Color.Black else Color.Yellow),
                contentAlignment = Alignment.Center
            ) { Text(text = it.toString(), color = if (it % 2 != 0) Color.Black else Color.Yellow) }
        }
    }
}
}*/
