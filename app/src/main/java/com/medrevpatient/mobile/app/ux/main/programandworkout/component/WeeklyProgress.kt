package com.medrevpatient.mobile.app.ux.main.programandworkout.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.TempDataSource
import com.medrevpatient.mobile.app.data.source.remote.dto.Program.Day
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.greyE9
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.ext.ifTrue

@Preview(showBackground = true)
@Composable
private fun WeeklyProgressPreview(
    modifier: Modifier = Modifier
) {
    WeeklyProgress(
        weeks = TempDataSource.daysList.chunked(7), onClickCell = {}, modifier = modifier
    )
}


@Composable
fun WeeklyProgress(
    weeks: List<List<Day>>,
    onClickCell: (Day) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier,
    ) {

        itemsIndexed(weeks) { index, item ->
            //Vertical Week Arrangement
            WeeklyProgressItem(
                week = index + 1,
                weekDays = item,
                weeklyProgressCheckPoints = weeklyBarHelper(index, weeks),
                onCellClick = onClickCell
            )
        }
    }
}


@Composable
private fun WeeklyProgressItem(
    week: Int,
    weekDays: List<Day>,
    weeklyProgressCheckPoints: WeeklyProgressCheckPoints,
    onCellClick: (Day) -> Unit,
    modifier: Modifier = Modifier
) {

    HStack(
        spaceBy = 8.dp, modifier = modifier.height(150.dp)
    ) {

        //Vertical Check Marks
        weeklyProgressCheckPoints.apply {

            CheckMark(
                topStickColor = topStickColor,
                bottomStickColor = bottomStickColor,
                isCompleted = isCurrentWeekCompleted,
                isCurrentWeekStarted = isCurrentWeekStarted
            )

        }

        //Horizontal Grid Cell Arrangement
        Box(
            modifier = Modifier
                .weight(5f)
                .padding(8.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(aliceBlue.copy(.6f), aliceBlue)
                    ), RoundedCornerShape(12)
                ), contentAlignment = Alignment.Center
        ) {

            HStack(12.dp) {

                Spacer(Modifier.padding(4.dp))

                WeekIndicator(week)

                VerticalDivider(
                    thickness = 1.dp,
                    color = grey94,
                    modifier = Modifier.fillMaxHeight()
                )

                ProgressGrid(
                    days = weekDays,
                    modifier = Modifier.weight(1f),
                    onCellClick = onCellClick
                )
            }

        }
    }
}


@Composable
private fun CheckMark(
    modifier: Modifier = Modifier,
    topStickColor: Color = black25,
    bottomStickColor: Color = black25,
    isCompleted: Boolean = true,
    isCurrentWeekStarted: Boolean
) {
    VStack(
        spaceBy = 0.dp, modifier = modifier.width(18.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .width(2.dp)
                .background(topStickColor)
        )

        val color = if (isCurrentWeekStarted) black25 else greyE9

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, color, shape = CircleShape)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier.paint(
                    painter = painterResource(R.drawable.filled_checkmark),
                    contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                    colorFilter = if (isCompleted) null else ColorFilter.tint(color)
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .width(2.dp)
                .background(bottomStickColor)
        )
    }
}


@Composable
private fun WeekIndicator(
    week: Int, modifier: Modifier = Modifier
) {

    VStack(
        spaceBy = 8.dp, verticalArrangement = Arrangement.Center, modifier = modifier
    ) {
        "WEEK".forEach { char ->
            Text(
                text = "$char",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                lineHeight = 12.sp,
                modifier = Modifier
            )
        }

        Spacer(Modifier.padding(4.dp))
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(25))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(25))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(25))
                    .background(
                        color = black25, shape = RoundedCornerShape(25)
                    ), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$week",
                    fontFamily = outFit,
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    lineHeight = 1.sp,
                    color = white
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgressGrid(
    days: List<Day>,
    onCellClick: (Day) -> Unit,
    modifier: Modifier = Modifier
) {

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .height(118.dp)
            .padding(8.dp),
        maxItemsInEachRow = 4,
        maxLines = 3,
        horizontalArrangement = Arrangement.SpaceEvenly, // Distributes items evenly
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(days.size) { index ->
            val day = days[index]
            Cell(
                day = day.day,
                isEnable = day.isDayCompleted,
                isStickVisible = (index + 1) % 4 != 0,
                cellTYPE = getCellType(index, days),
                modifier = Modifier.weight(1f)
            ) {
                onCellClick(day)
            }
        }

        Cell(
            day = -1,
            isEnable = days.all { it.isDayCompleted },
            isStickVisible = false,
            cellTYPE = CellType.WEEK_COMPLETED,
            modifier = Modifier.weight(1f)
        ) { }
    }
}


@Composable
private fun Cell(
    day: Int,
    modifier: Modifier = Modifier,
    isStickVisible: Boolean = CellDefaults.STICK_VISIBLE,
    cornerRadius: Int = CellDefaults.CORNER_RADIUS,
    isEnable: Boolean = false,
    cellSize: Dp = CellDefaults.cellSize,
    cellTYPE: CellType = CellDefaults.cellType,
    onClick: () -> Unit
) {

    val color = if (isEnable) CellDefaults.EnableColor else CellDefaults.DisableColor

    HStack(
        0.dp, modifier = modifier
    ) {

        Box(
            modifier = Modifier
                .size(cellSize)
                .clip(RoundedCornerShape(cornerRadius))
                .border(
                    width = 1.dp, color = color, shape = RoundedCornerShape(cornerRadius)
                )
                .clickable(
                    enabled = isEnable && cellTYPE ==
                            CellType.DAY_COMPLETED
                ) { onClick() },
            contentAlignment = Alignment.Center
        ) {

            when (cellTYPE) {
                CellType.DAY_COMPLETED -> {
                    Image(
                        painter = painterResource(R.drawable.completed),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(black25)
                    )
                }

                CellType.REST_DAY -> {
                    Text(
                        text = "REST\nDAY",
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.W900,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp
                    )
                }

                CellType.WEEK_COMPLETED -> {

                    val weekBackgroundColor = if (isEnable) black25 else Transparent

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(weekBackgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.completed),
                            contentDescription = null,
                            colorFilter = if (isEnable) null
                            else ColorFilter.tint(grey94)
                        )
                    }
                }

                CellType.DAY_NUMBER -> {
                    Text(
                        text = day.toString(),
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.W900,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                    )
                }
            }
        }

        isStickVisible.ifTrue {
            HorizontalDivider(
                thickness = 1.dp, color = color, modifier = Modifier
                    .weight(1f)
                    .size(2.dp)
            )
        }

    }
}


object CellDefaults {
    val cellSize: Dp = 48.dp
    const val CORNER_RADIUS: Int = 25
    val EnableColor: Color = black25
    val DisableColor: Color = grey94
    val cellType: CellType = CellType.WEEK_COMPLETED
    const val STICK_VISIBLE: Boolean = true
}

enum class CellType {
    DAY_NUMBER, DAY_COMPLETED, REST_DAY, WEEK_COMPLETED,
}


private fun weeklyBarHelper(
    currentIndex: Int,
    weeks: List<List<Day>>,
): WeeklyProgressCheckPoints {

    val isFirst = currentIndex == 0 //opt
    val isLast = currentIndex == weeks.lastIndex //opt

    val previousWeek = weeks.getOrNull(currentIndex - 1)
    val currentWeek = weeks[currentIndex]
    val nextWeek = weeks.getOrNull(currentIndex + 1)

    val topStickColor = when {
        previousWeek == null -> Transparent
        previousWeek.all { !it.isDayCompleted } || currentWeek.all { !it.isDayCompleted } -> greyE9
        else -> black25
    }

    val bottomStickColor = when {
        nextWeek == null -> Transparent
        nextWeek.all { !it.isDayCompleted } -> greyE9
        else -> black25
    }

    val isCurrentWeekStarted = currentWeek.any { it.isDayCompleted }
    val isCompleted = currentWeek.all { it.isDayCompleted }

    return WeeklyProgressCheckPoints(
        topStickColor = topStickColor,
        bottomStickColor = bottomStickColor,
        isCurrentWeekCompleted = isCompleted,
        isCurrentWeekStarted = isCurrentWeekStarted
    )
}

private fun getCellType(index: Int, days: List<Day>): CellType {
    val day = days[index]
    val weekCompleted = days.all { it.isDayCompleted }

    return when {
        day.isDayCompleted && !day.isRestDay -> CellType.DAY_COMPLETED
        day.isRestDay -> CellType.REST_DAY
        weekCompleted -> CellType.WEEK_COMPLETED
        else -> CellType.DAY_NUMBER
    }
}

data class WeeklyProgressCheckPoints(
    val topStickColor: Color,
    val bottomStickColor: Color,
    val isCurrentWeekCompleted: Boolean,
    val isCurrentWeekStarted: Boolean
)
