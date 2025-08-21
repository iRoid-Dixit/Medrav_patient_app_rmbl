package com.medrevpatient.mobile.app.ux.main.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.request.UpdateProfileReq
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.common.IconTitleArrowItem
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelDatePicker
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelPickerDefaults
import com.medrevpatient.mobile.app.utils.wheelPickerComponents.WheelTextPicker
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePreferenceDialog(modifier: Modifier = Modifier, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowManagePersonaliseDialog(false)) },
            isSheetVisible = { uiState.showManagePersonaliseDialog },
            title = stringResource(R.string.manage_personalizes)
        ) {
            ManagePreferenceContent(modifier, uiState, event)
        }
    }
}

@Composable
fun ManagePreferenceContent(modifier: Modifier = Modifier, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val array: ArrayList<Pair<String, Int>> = arrayListOf()
    array.add(Pair(stringResource(R.string.age), R.drawable.age))
    array.add(Pair(stringResource(R.string.height), R.drawable.height))
    array.add(Pair(stringResource(R.string.weight), R.drawable.weight_1))
    array.add(Pair(stringResource(R.string.energy_level), R.drawable.energy_level_s))
    //array.add(Pair(stringResource(R.string.lifestyle), R.drawable.lifestyle))
    array.add(Pair(stringResource(R.string.fitness_level), R.drawable.fitness_level))
    array.add(Pair(stringResource(R.string.goals), R.drawable.goal__outline_))
    VStack(
        0.dp, modifier = modifier
            .fillMaxWidth()
            .background(white)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 15.dp)
        ) {
            items(array) {
                IconTitleArrowItem(
                    text = it.first,
                    icon = it.second,
                    backgroundColor = MineShaft.copy(alpha = 0.02f),
                ) {
                    event(ProfileUiEvent.ManagePersonaliseFor(it.first))
                    //event(ProfileUiEvent.ShowManagePersonaliseDialog(false))
                    event(ProfileUiEvent.ShowUpdatePersonaliseDialog(true))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePersonaliseDialog(modifier: Modifier = Modifier, title: String, uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(ProfileUiEvent.ShowUpdatePersonaliseDialog(false)) },
            isSheetVisible = { uiState.showUpdatePersonaliseDialog },
            title = title
        ) {
            UpdatePersonaliseContent(modifier, title, uiState, event)
        }
    }
}

@Composable
fun UpdatePersonaliseContent(modifier: Modifier = Modifier, title: String = stringResource(R.string.age), uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    VStack(
        spaceBy = 0.dp, modifier = modifier
            .background(white)
            .fillMaxWidth()
    ) {
        when (title) {
            stringResource(R.string.age) -> AgePicker(uiState, event)
            stringResource(R.string.height) -> HeightPicker(uiState, event)
            stringResource(R.string.weight) -> WeightPicker(uiState, event)
            stringResource(R.string.energy_level) -> EnergyLevelPicker(uiState, event)
            stringResource(R.string.fitness_level) -> FitnessLevelPicker(uiState, event)
            stringResource(R.string.goals) -> GoalPicker(uiState, event)
        }
        Spacer(modifier = Modifier.padding(top = 50.dp))
        HStack(8.dp, modifier = Modifier.padding(horizontal = 18.dp)) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier.weight(1f),
                elevation = 0.dp,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                onClick = { event(ProfileUiEvent.ShowUpdatePersonaliseDialog(false)) }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.update),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier.weight(1f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp),
                color = if (checkUpdatedValues(uiState)) MineShaft else MineShaft.copy(alpha = 0.3f),
                elevation = if (checkUpdatedValues(uiState)) SkaiButtonDefault.elevation else 0.dp,
                enable = checkUpdatedValues(uiState)
            ) {
                event(ProfileUiEvent.PerformUpdateClick(UpdateProfileReq(isFrom = uiState.managePersonaliseFor)))
            }
        }
        Spacer(modifier = Modifier.padding(top = 30.dp))
    }
}

@Composable
fun AgePicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    Box(
        modifier = Modifier
            .height(300.dp)
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(15))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        aliceBlue.copy(alpha = 0f),
                        aliceBlue.copy(alpha = 1f),
                        aliceBlue.copy(alpha = 1f)
                    )
                )
            )
    ) {
        WheelDatePicker(
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = outFit,
                fontWeight = FontWeight.Medium,
                color = MineShaft
            ),
            startDate = if (uiState.age.isNotEmpty()) LocalDate.parse(
                uiState.age.split("T")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ) else LocalDate.now(),
            modifier = Modifier.padding(15.dp),
            textColor = MineShaft,
            selectorProperties = WheelPickerDefaults.selectorProperties(
                border = BorderStroke(1.dp, MineShaft)
            ),
        ) { snappedDate ->
            event(ProfileUiEvent.AgeValueChange(snappedDate.toString()))
        }
    }
}

@Composable
fun HeightPicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(15))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        aliceBlue.copy(alpha = 0f),
                        aliceBlue.copy(alpha = 1f)
                    )
                )
            ), contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFF252525)),
                    content = {}
                )
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = listOf("1", "2", "3", "4", "5", "6", "7", "8"),
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = MineShaft
                    ),
                    color = MineShaft,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (uiState.height != -1) uiState.height.minus(1) else 0,
                    onScrollFinished = { snappedIndex ->
                        event(ProfileUiEvent.HeightValueChange(snappedIndex.plus(1)))
                        return@WheelTextPicker snappedIndex

                    }
                )
            }
            Text(
                text = stringResource(R.string.feet),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MineShaft
            )
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFF252525)),
                    content = {}
                )
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"),
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = MineShaft
                    ),
                    color = MineShaft,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (uiState.heightInches != -1) uiState.heightInches else 0,
                    onScrollFinished = { snappedIndex ->
                        event(ProfileUiEvent.HeightInchValueChange(snappedIndex))
                        return@WheelTextPicker snappedIndex
                    }
                )
            }
            Text(
                text = stringResource(R.string.inch),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MineShaft
            )
        }
    }
}

@Composable
fun WeightPicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .clip(RoundedCornerShape(15))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        aliceBlue.copy(alpha = 0f),
                        aliceBlue.copy(alpha = 1f)
                    )
                )
            ), contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 90.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size((256 / 3).dp, (228 / 5).dp),
                    shape = RoundedCornerShape(15.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFF252525)),
                    content = {}
                )
                val weightArray = arrayListOf(0..657).flatten().map { it.toString() }
                WheelTextPicker(
                    size = DpSize(
                        width = (256 / 3).dp,
                        height = 210.dp
                    ),
                    texts = weightArray,
                    rowCount = 5,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = outFit,
                        fontWeight = FontWeight.Medium,
                        color = MineShaft
                    ),
                    color = MineShaft,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        enabled = false
                    ),
                    startIndex = if (uiState.weight != -1) uiState.weight else 0,
                    onScrollFinished = { snappedIndex ->
                        event(ProfileUiEvent.WeightValueChange(snappedIndex))
                        return@WheelTextPicker snappedIndex
                    }
                )
            }
            Text(
                text = stringResource(R.string.lbs),
                fontFamily = outFit,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MineShaft
            )
        }
    }
}

@Composable
fun EnergyLevelPicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val arr = stringArrayResource(id = R.array.energy_level_array)
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Spacer(modifier = Modifier.padding(top = 10.dp))
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .padding(top = 10.dp),
        userScrollEnabled = false,
        verticalItemSpacing = 10.dp,
        reverseLayout = false
    ) {
        itemsIndexed(array) { index, it ->
            SelectionItem(
                title = it,
                isSelected = selectedIndex.intValue == index || (uiState.energyLevel != -1 && uiState.energyLevel.minus(1) == index)
            ) {
                selectedIndex.intValue = index
                event(ProfileUiEvent.EnergyLevelValueChange(index.plus(1)))
            }
        }
    }
}

@Composable
fun FitnessLevelPicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val arr = stringArrayResource(id = R.array.fitness_level_array)
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Spacer(modifier = Modifier.padding(top = 10.dp))
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        userScrollEnabled = false,
        verticalItemSpacing = 10.dp,
        reverseLayout = false
    ) {
        itemsIndexed(array) { index, it ->
            SelectionItem(
                title = it,
                isSelected = selectedIndex.intValue == index || (uiState.fitnessLevel != -1 && uiState.fitnessLevel.minus(1) == index)
            ) {
                selectedIndex.intValue = index
                event(ProfileUiEvent.FitnessLevelValueChange(index.plus(1)))
            }
        }
    }
}

@Composable
fun GoalPicker(uiState: ProfileUiState, event: (ProfileUiEvent) -> Unit) {
    val arr = stringArrayResource(id = R.array.goals_array)
    val array = remember { arr }
    val selectedIndex = remember { mutableIntStateOf(-1) } // Track selected index
    Spacer(modifier = Modifier.padding(top = 10.dp))
    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(array) { index, it ->
            SelectionItem(
                title = it,
                width = it.length.times(10),
                isSelected = selectedIndex.intValue == index || (uiState.goals != -1 && uiState.goals.minus(1) == index)
            ) {
                selectedIndex.intValue = index
                event(ProfileUiEvent.GoalsValueChange(index.plus(1)))
            }
        }
    }
}

@Composable
private fun checkUpdatedValues(uiState: ProfileUiState): Boolean {
    when (uiState.managePersonaliseFor) {
        stringResource(R.string.age) -> {
            if (uiState.age != (AppUtils.formatTimeStampForBirthDate(uiState.userData.birthDate ?: 0))) {
                return true
            }
        }

        stringResource(R.string.height) -> {
            if (uiState.height != uiState.userData.heightInFeet || uiState.heightInches != uiState.userData.heightInInch) {
                return true
            }
        }

        stringResource(R.string.weight) -> {
            if (uiState.weight != uiState.userData.weight) {
                return true
            }
        }

        stringResource(R.string.energy_level) -> {
            if (uiState.energyLevel != uiState.userData.energyLevel) {
                return true
            }
        }

        stringResource(R.string.fitness_level) -> {
            if (uiState.fitnessLevel != uiState.userData.fitnessLevel) {
                return true
            }
        }

        stringResource(R.string.goals) -> {
            if (uiState.goals != uiState.userData.goals) {
                return true
            }
        }
    }
    return false
}










