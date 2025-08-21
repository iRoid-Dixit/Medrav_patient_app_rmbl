package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.data.source.remote.dto.StrengthLog
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.IconsPaths.BackArrow.path
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.greyE9
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.OutlineTextFieldWithTrailing
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.component.TextFieldFilledWithTrailing
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrengthLogSheet(
    uiState: ProgramAndWorkOutUiState,
    modifier: Modifier = Modifier,
    shouldShowSheet: Boolean = false,
    event: ProgramAndWorkOutEvent,
    onDismissRequest: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(shouldShowSheet) {
        if (shouldShowSheet) sheetState.show() else sheetState.hide()
    }

    if (shouldShowSheet)
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = white,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    pagerState.animateScrollToPage(0)
                }.invokeOnCompletion {
                    onDismissRequest()
                }
            },
            modifier = modifier
        ) {

            VStack(
                spaceBy = 8.dp,
                modifier = Modifier.animateContentSize()
            ) {

                Text(
                    text = if (pagerState.currentPage == 0) "Strength Logs" else "Add Strength Log",
                    fontSize = 20.sp,
                    fontFamily = outFit,
                    fontWeight = Bold,
                    lineHeight = 25.sp,
                    color = black25
                )

                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = false,
                    modifier = Modifier
                        .heightIn(min = 350.dp)

                ) { pageNo ->
                    when (pageNo) {
                        0 -> StrengthLog(
                            uiState = uiState,
                            modifier = Modifier,
                            event = event
                        ) {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }

                        1 -> AddStrengthLog(
                            uiState = uiState.addLog,
                            modifier = Modifier.weight(1f),
                            event = event
                        ) {
                            scope.launch {
                                sheetState.hide()
                                pagerState.animateScrollToPage(0)
                            }.invokeOnCompletion {
                                onDismissRequest()
                            }
                        }
                    }
                }
            }
        }
}


@Composable
private fun StrengthLog(
    uiState: ProgramAndWorkOutUiState,
    modifier: Modifier = Modifier,
    event: ProgramAndWorkOutEvent,
    onClick: () -> Unit
) {

    NetworkResultHandler(
        networkResult = uiState.strengthLog,
        loadingFier = Modifier.height(300.dp),
        errorFier = Modifier.height(300.dp),
        onRetry = {
            event(ProgramsAndWorkOutUiEvent.RetryFetchingSLogs)
        }
    ) { onSuccess ->
        VStack(
            spaceBy = 8.dp,
            modifier = modifier
        ) {

            VStack(
                spaceBy = 8.dp,
                modifier = Modifier
                    .heightIn(max = 350.dp)
                    .padding(18.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(aliceBlue.copy(0.5f), aliceBlue)
                        ),
                    )
                    .padding(vertical = 8.dp)
            ) {

                Text(
                    text = "All Strength Logs",
                    fontSize = 20.sp,
                    fontFamily = outFit,
                    fontWeight = Bold,
                    lineHeight = 25.sp,
                    color = black25
                )

                HorizontalDivider(Modifier.fillMaxWidth(), thickness = 2.dp, color = black25)

                StrengthTable(
                    logs = onSuccess.data,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )

            }

            SkaiButton(
                text = "Add New Log",
                makeUpperCase = true,
                isLoading = uiState.isLoading,
                enable = !uiState.isLoading,
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                onClick = onClick
            )
            Spacer(modifier = Modifier.padding(8.dp))

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddStrengthLog(
    uiState: ProgramAndWorkOutUiState.AddStrengthLog,
    modifier: Modifier = Modifier,
    event: ProgramAndWorkOutEvent,
    onCancel: () -> Unit
) {

    Timber.d("${uiState.errors}")

    var expanded by remember { mutableStateOf(false) }

    val painter = rememberVectorPainter(
        defaultWidth = 33.dp,
        defaultHeight = 33.dp,
        viewportWidth = 33f,
        viewportHeight = 33f,
        autoMirror = true
    ) { _, _ ->
        Path(
            path.toNodes(),
            strokeLineWidth = 1f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            stroke = Brush.verticalGradient(listOf(black25, aliceBlue))
        )
    }

    uiState.apply {

        VStack(
            spaceBy = 8.dp,
            modifier = modifier
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(Modifier.padding(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
            ) {

                TextFieldFilledWithTrailing(
                    value = getExerciseById,
                    enable = false,
                    onValueChange = {},
                    placeholder = {
                        Text(
                            text = "Select Exercise",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = W300,
                            color = black25
                        )
                    },
                    trailingIcon = {
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier.rotate(if (expanded) 90f else 270f)
                        )
                    },
                    supportingText = {
                        uiState.errors[0]?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp),
                    containerColor = white,
                    onDismissRequest = { expanded = false }
                ) {

                    NetworkResultHandler(
                        networkResult = uiState.exercises,
                        modifier = Modifier.heightIn(max = 200.dp),
                        onRetry = {
                            event(ProgramsAndWorkOutUiEvent.RetryFetchingExercise)
                        }
                    ) { onSuccess ->
                        val eList = onSuccess.data

                        VStack(
                            spaceBy = 2.dp,
                            modifier = Modifier
                                .heightIn(max = 200.dp)
                                .verticalScroll(rememberScrollState())
                        ) {

                            if (eList.isEmpty()) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "No Exercises Found",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    onClick = { expanded = !expanded },
                                )
                            } else {
                                eList.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            event(ProgramsAndWorkOutUiEvent.exerciseId(item.id))
                                            expanded = !expanded
                                        },
                                        text = {
                                            Text(
                                                text = item.name,
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

            val datePickerState = rememberDatePickerState()
            var datePickerVisible by remember { mutableStateOf(false) }

            TextFieldFilledWithTrailing(
                value = formatedDate,
                enable = false,
                onValueChange = {},
                placeholder = {
                    Text(
                        text = "Choose Date",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = W300,
                        color = black25
                    )
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(drawable.calendar),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                supportingText = {
                    errors[1]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(25))
                    .clickable {
                        datePickerVisible = !datePickerVisible
                    }
            )

            if (datePickerVisible) {
                DatePickerModal(
                    datePickerState = datePickerState,
                    onDateSelected = { date ->
                        date?.let {
                            event(ProgramsAndWorkOutUiEvent.date(date = it))
                        }
                    }) {
                    datePickerVisible = !datePickerVisible
                }
            }

            OutlineTextFieldWithTrailing(
                value = sets,
                onValueChange = {
                    event(ProgramsAndWorkOutUiEvent.set(it))
                },
                placeholder = {
                    Text(
                        text = "Enter Sets",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = W300,
                        color = black25
                    )
                },
                supportingText = {
                    errors[2]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlineTextFieldWithTrailing(
                value = lb,
                onValueChange = {
                    event(ProgramsAndWorkOutUiEvent.lb(it))
                },
                supportingText = {
                    errors[3]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                placeholder = {
                    Text(
                        text = "Enter LB",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = W300,
                        color = black25
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlineTextFieldWithTrailing(
                value = reps,
                onValueChange = {
                    event(ProgramsAndWorkOutUiEvent.reps(it))
                },
                placeholder = {
                    Text(
                        text = "Enter Reps",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = W300,
                        color = black25
                    )
                },
                supportingText = {
                    errors[4]?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )
//TODO: clean-up
//            Spacer(Modifier.padding(9.dp))
//            InfoComponent(description = "Log your offline exercises if you have did any apart from SKAI App.")
            Spacer(Modifier.padding(4.dp))

            HStack(
                8.dp,
            ) {
                SkaiButton(
                    text = "cancel",
                    makeUpperCase = true,
                    color = white,
                    textStyle = SkaiButtonDefault.textStyle.copy(black25),
                    borderStroke = BorderStroke(1.dp, black25),
                    modifier = Modifier.weight(1f),
                    innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                    onClick = {
                        onCancel()
                    }
                )

                SkaiButton(
                    text = "add log",
                    innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                    makeUpperCase = true,
                    isLoading = isLoading,
                    enable = !isLoading,
                    modifier = Modifier.weight(1f),
                    onClick = { event(ProgramsAndWorkOutUiEvent.AddStrengthLog) }
                )
            }

            Spacer(Modifier.padding(9.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    datePickerState: DatePickerState,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors().copy(containerColor = white),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Ok", color = black25)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = black25)
            }
        }
    ) {

        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors().copy(
                selectedDayContentColor = white,
                selectedDayContainerColor = greyE9,
                /*------------------------*/
                dayContentColor = white,
                /*------------------------*/
                todayDateBorderColor = grey94,
                todayContentColor = black25,
                /*------------------------*/
                containerColor = white,
                titleContentColor = black25,
                headlineContentColor = black25,
                weekdayContentColor = black25,
                /*------------------------*/
                currentYearContentColor = black25,
                yearContentColor = black25,
                selectedYearContainerColor = greyE9,
                selectedYearContentColor = black25
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StrengthTable(
    modifier: Modifier = Modifier,
    logs: List<StrengthLog> = listOf(StrengthLog(exercisesId = StrengthLog.ExercisesId()))
) {

    val itemsLog = remember(logs) { logs.sortedBy { it.date }.reversed() }

    // Each cell of a column must have the same weight.
    val column0Weight = .2f // 20%
    val column1Weight = .4f // 40%
    val column2Weight = .1333f // 40%

    val headerStyle = TextStyle(
        fontSize = 12.sp,
        fontFamily = outFit,
        fontWeight = Bold,
        lineHeight = 16.sp,
        color = black25,
        textAlign = TextAlign.Center
    )

    val valueStyle = TextStyle(
        fontSize = 12.sp,
        fontFamily = outFit,
        fontWeight = Medium,
        lineHeight = 16.sp,
        color = grey94,
        textAlign = TextAlign.Center
    )

    VStack(0.dp) {
        HStack(0.dp, Modifier.padding(horizontal = 0.dp)) {
            TableCell(text = "Date", style = headerStyle, weight = column0Weight)
            TableCell(
                text = "Exercise",
                style = headerStyle.copy(textAlign = TextAlign.Start),
                weight = column1Weight
            )
            TableCell(text = "Sets", style = headerStyle, weight = column2Weight)
            TableCell(text = "LB", style = headerStyle, weight = column2Weight)
            TableCell(text = "Reps", style = headerStyle, weight = column2Weight)
        }

        LazyColumn(
            modifier = modifier
        ) {
            items(itemsLog) { log ->

                log.toStrengthTable().apply {

                    HStack(0.dp) {
                        TableCell(text = date, style = valueStyle, weight = column0Weight)
                        TableCell(
                            text = exercise,
                            style = valueStyle.copy(textAlign = TextAlign.Start),
                            weight = column1Weight
                        )
                        TableCell(text = sets, style = valueStyle, weight = column2Weight)
                        TableCell(text = lb, style = valueStyle, weight = column2Weight)
                        TableCell(text = reps, style = valueStyle, weight = column2Weight)
                    }
                }
            }
        }
    }
}


@Composable
fun RowScope.TableCell(
    text: String,
    style: TextStyle = LocalTextStyle.current,
    weight: Float
) {
    Text(
        text = text,
        style = style,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 2.dp, vertical = 4.dp)

    )
}