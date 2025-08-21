package com.medrevpatient.mobile.app.ux.main.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.FullSizeCenterBox
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelPickerDefaults
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelTimePicker
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun ReminderScreen(
    modifier: Modifier = Modifier,
    viewModel: ReminderViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextBack(
                title = stringResource(id = R.string.reminder),
                onBackPress = {
                    viewModel.popBackStack()
                }
            )
        },
        bottomBar = {
            BottomContent(event = viewModel::event)
        }
    ) { innerPadding ->
        if (uiState.isLoading) DialogLoader()
        ReminderContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()),
            uiState = uiState,
            event = viewModel::event
        )
    }
}

@Composable
private fun ReminderContent(modifier: Modifier = Modifier, uiState: ReminderUiState, event: (ReminderUiEvent) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .background(white)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.select_program_goal).uppercase(),
            fontSize = 14.sp,
            fontFamily = outFit,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp),
            letterSpacing = 1.5.sp,
        )
        Box(modifier = Modifier.wrapContentSize()) {
            VStack(4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .clip(RoundedCornerShape(28))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    white,
                                    ColorSwansDown.copy(alpha = 0.8f),
                                    ColorSwansDown
                                )
                            )
                        )
                        .padding(vertical = 18.dp, horizontal = 7.dp)
                        .clickable {
                            expanded = !expanded
                            event(ReminderUiEvent.DisplayReminderValues(LocalTime.now(), false, arrayListOf(), ""))
                        }, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedItem.ifEmpty { stringResource(id = R.string.select_program_goal) },
                        color = MineShaft,
                        fontSize = 14.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.weight(1f),
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_ahead),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(if (expanded) 270f else 90f)
                            .padding(top = 7.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    containerColor = Color.White,
                    modifier = Modifier
                        .heightIn(max = 250.dp)
                        .padding(horizontal = 18.dp)
                        .fillMaxWidth(),
                    onDismissRequest = { expanded = !expanded },
                ) {
                    NetworkResultHandler(
                        networkResult = uiState.programGoalsList,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { onSuccess ->
                        if (onSuccess.data.isEmpty()) {
                            FullSizeCenterBox {
                                Text(
                                    text = "No data found\nRetry",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = Medium),
                                    modifier = Modifier.clickable {
                                    }
                                )
                            }
                        } else {
                            VStack(4.dp) {
                                onSuccess.data.forEach {
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = !expanded
                                            selectedItem = it.name ?: ""
                                            event(ReminderUiEvent.DisplayReminderValues(it.reminderTime?.let { time -> LocalTime.parse(time) } ?: LocalTime.now().atDate(LocalDate.now())
                                                .atZone(ZoneId.systemDefault())
                                                .withZoneSameInstant(ZoneId.of("UTC"))
                                                .toLocalTime(),
                                                true, it.repeatFrequency, it.id ?: ""))
                                        },
                                        enabled = true,
                                        text = {
                                            Text(
                                                text = it.name ?: "",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = stringResource(id = R.string.reminder_time).uppercase(),
            fontSize = 14.sp,
            fontFamily = outFit,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp),
            letterSpacing = 1.5.sp,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(15))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            aliceBlue.copy(alpha = 0.2f),
                            aliceBlue.copy(alpha = 1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isNeedToUpdate) {
                val zone = uiState.selectedItemReminderTime.atDate(LocalDate.now()).atZone(ZoneId.of("UTC")).zone
                val startTime = if (zone == ZoneId.of("UTC")) {
                    uiState.selectedItemReminderTime.atDate(LocalDate.now()).atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalTime()
                } else {
                    uiState.selectedItemReminderTime
                }
                WheelTimePicker(
                    startTime = startTime,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        border = BorderStroke(1.dp, Color(0xFF252525))
                    ),
                    textColor = MineShaft,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = Medium,
                        color = Color.White
                    ),
                ) { snappedTime ->
                    val utcTime = snappedTime.atDate(LocalDate.now())
                        .atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneId.of("UTC"))
                        .toLocalTime()
                    event(ReminderUiEvent.UpdateTimer(utcTime))
                }
            } else {
                WheelTimePicker(
                    startTime = LocalTime.now(),
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        border = BorderStroke(1.dp, Color(0xFF252525))
                    ),
                    textColor = MineShaft,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = Medium,
                        color = Color.White
                    ),
                ) { snappedTime ->
                    println(snappedTime)
                }
            }
        }
        Text(
            text = stringResource(id = R.string.how_often_repeat).uppercase(),
            fontSize = 14.sp,
            fontFamily = outFit,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(18.dp),
            letterSpacing = 1.5.sp,
        )
        if (uiState.isNeedToUpdate) {
            SelectedItemReminderDisplay(uiState, event)
        } else {
            NonSelectedItemReminderDisplay()
        }
    }
}

@Composable
private fun SelectedItemReminderDisplay(uiState: ReminderUiState, event: (ReminderUiEvent) -> Unit) {
    val selectedIndexTop = remember {
        mutableIntStateOf(
            when (uiState.repeatFrequency.firstOrNull()) {
                7 -> 0 // "Everyday"
                8 -> 1 // "Mon-Fri"
                9 -> 2 // "Weekends"
                else -> -1
            }
        )
    } // Track selected index
    val selectedIndexBottom = remember {
        mutableStateOf(
            if (uiState.repeatFrequency.firstOrNull() == 10) {
                mutableSetOf(7)
            } else {
                uiState.repeatFrequency.mapNotNull { apiIndex ->
                    if (apiIndex in 0..6) (apiIndex + 6) % 7 else null
                }.toMutableSet()
            }
        )
    } // Track selected index
    val array = arrayOf(stringResource(R.string.everyday), stringResource(R.string.mon_fri), stringResource(R.string.weekends))
    LazyRow(
        modifier = Modifier.padding(horizontal = 18.dp)
    ) {
        itemsIndexed(array) { index, it ->
            SelectionItem(
                title = it, isSelected = selectedIndexTop.intValue == index
            ) {
                selectedIndexBottom.value = mutableSetOf()
                selectedIndexTop.intValue = index
                event(
                    ReminderUiEvent.UpdateRepeatFrequency(
                        arrayListOf(
                            when (index) {
                                0 -> 7
                                1 -> 8
                                2 -> 9
                                else -> -1
                            }
                        )
                    )
                )
            }
        }
    }

    val arrayWeek = stringArrayResource(R.array.week_days)
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .height(130.dp)
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        reverseLayout = false
    ) {
        itemsIndexed(arrayWeek) { index, it ->
            SelectionItem(title = it, isSelected = selectedIndexBottom.value.contains(index)) {
                selectedIndexTop.intValue = -1
                selectedIndexBottom.value = selectedIndexBottom.value.toMutableSet().apply {
                    if (index == arrayWeek.lastIndex) {
                        clear()
                        add(index)
                    } else {
                        remove(arrayWeek.lastIndex)
                        if (contains(index)) remove(index) else add(index)
                    }
                }
                if (selectedIndexBottom.value == mutableSetOf(7)) {
                    event(ReminderUiEvent.UpdateRepeatFrequency(arrayListOf(10)))
                } else {
                    event(
                        ReminderUiEvent.UpdateRepeatFrequency(
                            selectedIndexBottom.value.map { selected ->
                                if (selected in 0..6) (selected + 1) % 7 else 0
                            }.toCollection(arrayListOf())
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NonSelectedItemReminderDisplay() {
    val selectedIndexTop = remember { mutableIntStateOf(-1) } // Track selected index
    val selectedIndexBottom = remember { mutableStateOf(mutableSetOf<Int>()) } // Track selected index
    val array = arrayOf(stringResource(R.string.everyday), stringResource(R.string.mon_fri), stringResource(R.string.weekends))
    LazyRow(
        modifier = Modifier.padding(horizontal = 18.dp)
    ) {
        itemsIndexed(array) { index, it ->
            SelectionItem(
                title = it, isSelected = selectedIndexTop.intValue == index
            ) {
                selectedIndexBottom.value = mutableSetOf()
                selectedIndexTop.intValue = index
            }
        }
    }

    val arrayWeek = stringArrayResource(R.array.week_days)
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .height(130.dp)
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp),
        userScrollEnabled = false,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        reverseLayout = false
    ) {
        itemsIndexed(arrayWeek) { index, it ->
            SelectionItem(title = it, isSelected = selectedIndexBottom.value.contains(index)) {
                selectedIndexTop.intValue = -1
                selectedIndexBottom.value = selectedIndexBottom.value.toMutableSet().apply {
                    if (index == arrayWeek.lastIndex) {
                        clear()
                        add(index)
                    } else {
                        remove(arrayWeek.lastIndex)
                        if (contains(index)) remove(index) else add(index)
                    }
                }
            }
        }
    }
}


@Composable
private fun BottomContent(event: (ReminderUiEvent) -> Unit) {
    Column(
        modifier = Modifier.then(
            Modifier
                .padding(horizontal = 18.dp, vertical = 20.dp)
                .background(Color.Transparent)
        )
    ) {
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = { event(ReminderUiEvent.PerformCancelClick) }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.update),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp)
            ) { event(ReminderUiEvent.PerformUpdateReminderClick) }
        }
    }
}


@Preview
@Composable
fun Preview(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextBack(
                title = stringResource(id = R.string.reminder),
                onBackPress = {
                }
            )
        },
        bottomBar = {
            BottomContent(event = {})
        }
    ) { innerPadding ->
        ReminderContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding(), bottom = innerPadding.calculateBottomPadding()), uiState = ReminderUiState(),
            event = {}
        )
    }
}