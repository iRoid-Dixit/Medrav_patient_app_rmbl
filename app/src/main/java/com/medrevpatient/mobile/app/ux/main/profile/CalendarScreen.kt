package com.medrevpatient.mobile.app.ux.main.profile

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.Event
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextBack
import com.medrevpatient.mobile.app.ui.calendar.CalendarEvent
import com.medrevpatient.mobile.app.ui.calendar.ComposeCalendar
import com.medrevpatient.mobile.app.ui.theme.ColorOsloGray
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils
import java.time.ZoneId

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextBack(
                title = stringResource(id = R.string.calendar),
                onBackPress = {
                    viewModel.popBackStack()
                }
            )
        },
    ) { innerPadding ->
        CalendarContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            uiState = uiState,
            event = viewModel::event
        )
    }
}

@SuppressLint("NewApi")
@Composable
private fun CalendarContent(
    modifier: Modifier = Modifier,
    uiState: CalendarUiState,
    event: (CalendarUiEvent) -> Unit
) {
    Column(
        modifier = modifier
            .background(white)
            .fillMaxSize()
        /*.verticalScroll(rememberScrollState())*/
    ) {
        val eventList = remember { mutableStateOf<List<Event>>(emptyList()) }
        val isFutureDate = remember { mutableStateOf(false) }
        val selectedDate = remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 5.dp)
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(8))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            white,
                            ColorSwansDown.copy(alpha = 0.4f),
                            ColorSwansDown
                        )
                    )
                )
                .padding(horizontal = 10.dp)
                .padding(top = 10.dp, bottom = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            val calendarEvents: ArrayList<CalendarEvent> = arrayListOf()
            for (i in uiState.calendarData) {
                calendarEvents.add(
                    CalendarEvent(
                        date = AppUtils.convertLongToLocalDate(i.date ?: 0),
                        completedWorkoutIcon = ImageVector.vectorResource(id = R.drawable.ic_right_tick),
                        pendingWorkoutIcon = ImageVector.vectorResource(id = R.drawable.calendar),
                        isCompleted = i.isCompleted ?: false,
                        eventList = i.events,
                        isFutureDate = i.isCompleted != false
                    )
                )
            }

            ComposeCalendar(
                events = calendarEvents,
                onNextClick = {
                    event(
                        CalendarUiEvent.PerformNextPreviousMonthClick(
                            uiState.currentMonth + 1,
                            uiState.currentYear
                        )
                    )
                },
                onPreviousClick = {
                    event(
                        CalendarUiEvent.PerformNextPreviousMonthClick(
                            uiState.currentMonth - 1,
                            uiState.currentYear
                        )
                    )
                },
                onDayClick = { dayDate ->
                    // Handle day click
                    eventList.value =
                        calendarEvents.firstOrNull { it.date == dayDate?.date }?.eventList
                            ?: emptyList()
                    isFutureDate.value = dayDate?.isFutureDate ?: false
                    val timestamp = dayDate?.date?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
                        ?.toEpochMilli() ?: 0L
                    selectedDate.value = AppUtils.formatTimestampForMyPosts(timestamp)

                }
            )
        }
        if (eventList.value.isNotEmpty()) {
            Text(
                text = if (!isFutureDate.value) "Programs Schedule - ${selectedDate.value}" else "Completed Workouts - ${selectedDate.value}",
                style = TextStyle(
                    color = MineShaft,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = outFit
                ),
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .padding(top = 20.dp)
            )
            LazyColumn(modifier = Modifier.padding(top = 13.dp)) {
                items(eventList.value) {
                    EventItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 7.dp), data = it, isFutureDate = isFutureDate.value
                    )
                }
            }
        }

    }
}

@Composable
fun EventItem(modifier: Modifier = Modifier, data: Event, isFutureDate: Boolean = false) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        aliceBlue,
                        aliceBlue.copy(alpha = 0.7f),
                        aliceBlue.copy(alpha = 0f)
                    )
                ),
                shape = RoundedCornerShape(22)
            )
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 17.dp, vertical = 22.dp)
                    .weight(1f)
            ) {
                Text(
                    text = data.title ?: "",
                    style = TextStyle(
                        color = MineShaft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = outFit
                    )
                )
                Text(
                    text = data.subTitle ?: "",
                    style = TextStyle(
                        color = ColorOsloGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = outFit
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (data.isProgramCompleted != true) {
                if (data.subTitle?.contains("resume") != true) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.completed),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 17.dp),
                        colorFilter = ColorFilter.tint(MineShaft)
                    )
                }
            } else {
                Image(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_well_done),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 17.dp),
                    colorFilter = ColorFilter.tint(MineShaft)
                )
            }

        }
    }
}

@Preview
@Composable
private fun PreviewCalendar(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = stringResource(id = R.string.calendar),
                onBackPress = {
                }
            )
        },
    ) { innerPadding ->
        CalendarContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                ), uiState = CalendarUiState(), event = {}
        )
    }
}