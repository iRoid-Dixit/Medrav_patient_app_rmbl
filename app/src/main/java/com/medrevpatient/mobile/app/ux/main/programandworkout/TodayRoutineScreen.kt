package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.medrevpatient.mobile.app.data.source.TempDataSource
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises
import com.medrevpatient.mobile.app.data.source.remote.dto.DayExercises.Equipment
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.VerticallySliderLayout
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.ux.main.component.IconIMGTextHStack
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.CenterAlignContentWrapper
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ConfirmationDialog
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.TitleAndBulletText
import timber.log.Timber

@Composable
fun TodayRoutineScreen(
    viewModel: TodayRoutineViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkResultHandler(
        networkResult = uiState.todayRoutine,
        barTitle = "Day ${uiState.day} Routine",
        onBackPress = { viewModel.popBackStack() },
        onRetry = { viewModel.event(TodayRoutineUIEvent.Refresh) }
    ) { successState ->
        Scaffold(
            modifier = modifier.statusBarsPadding(),
            containerColor = Color.White,
            topBar = {
                TopBarCenterAlignTextAndBack(
                    title = "Day ${uiState.day} Routine",
                    onBackPress = { viewModel.event(TodayRoutineUIEvent.BackPress) }
                )
            }
        ) { innerPadding ->
            TodayRoutineContent(
                data = successState.data ?: DayExercises(),
                event = viewModel::event,
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
    }

    //StartWorkOut Screen
    VerticallySliderLayout(
        visible = uiState.shouldShowStartWorkOutScreen,
        modifier = Modifier.statusBarsPadding()
    ) {
        StartWorkoutScreen(
            uiState = uiState.workOutUiState,
            event = viewModel::event,
        )
    }

    //CongratsAfterWorkoutCompletionScreen Screen
    VerticallySliderLayout(
        visible = uiState.shouldShowCongratulationsScreen && !uiState.shouldShowStartWorkOutScreen,
        modifier = Modifier
    ) {
        CongratsAfterWorkoutCompletionScreen(
            todayRoutineUiState = uiState,
            event = viewModel::event
        )
    }


    BackHandler(
        enabled = uiState.shouldShowStartWorkOutScreen || uiState.shouldShowCongratulationsScreen,
    ) {
        viewModel.event(TodayRoutineUIEvent.CloseCongratulationScreen)
    }

}

@Composable
private fun TodayRoutineContent(
    data: DayExercises,
    event: TodayRoutineEvent,
    modifier: Modifier = Modifier
) {

    var shouldShowChoiceDialog by remember { mutableStateOf(false) }

    var selectedExercise by remember { mutableStateOf<DayExercises.Exercise?>(null) }

    val exercisesByType = data.exercises.groupBy { it.type } // Grouping exercises by type

    val headerStyle = MaterialTheme.typography.labelLarge.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 4.sp
    )

    LazyColumn(
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxSize(),
    ) {

        item {
            CenterAlignContentWrapper(
                title = "WHAT'S NEEDED",
                spaceBy = 8.dp,
                style = headerStyle,
                modifier = Modifier.padding(8.dp)
            ) {
                WhatsNeedComponent(
                    noOfExercise = data.totalExercise.toString(),
                    duration = data.idealTime,
                    equipment = data.equipments
                )
            }
        }

        exercisesByType.forEach { (type, exercises) ->
            item {
                CenterAlignContentWrapper(
                    title = type,
                    style = headerStyle,
                    modifier = Modifier.padding(6.dp)
                ) {}
            }

            items(exercises) { exercise ->
                ExerciseItemComponent(
                    items = exercise.bulletPoints,
                    title = exercise.name,
                    isCompleted = exercise.isCompleted,
                ) {
                    selectedExercise = exercise
                    shouldShowChoiceDialog = !shouldShowChoiceDialog
                }
            }

            item { Spacer(Modifier.padding(8.dp)) }
        }
    }


    if (shouldShowChoiceDialog) {
        ConfirmationDialog(
            description = "Would you prefer to proceed with this exercise with a timer or without a timer?",
            negativeText = "Without Timer",
            positiveText = "With Timer",
            onDismiss = {
                shouldShowChoiceDialog = !shouldShowChoiceDialog
                selectedExercise = null
                Timber.d("ExerciseConfirmationDialog is dismissed")
            },
            positive = {
                shouldShowChoiceDialog = !shouldShowChoiceDialog
                selectedExercise?.let { event(TodayRoutineUIEvent.StartWorkOut(it)) }
            },
            negative = {
                shouldShowChoiceDialog = !shouldShowChoiceDialog
                selectedExercise?.let { event(TodayRoutineUIEvent.StartWorkOut(it, true)) }
            }
        )
    }

}


@Composable
private fun ExerciseItemComponent(
    title: String,
    items: List<String>,
    isCompleted: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(25))
                .background(
                    brush =
                    Brush.horizontalGradient(listOf(aliceBlue, aliceBlue, Color.White))
                )
                .clickable { onClick() }
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ),
        contentAlignment = Alignment.CenterStart
    ) {
        HStack(8.dp) {
            TitleAndBulletText(
                title = title,
                spaceBy = 4.dp,
                items = items,
                horizontal = Alignment.Start,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onClick
            ) {
                val icon = if (isCompleted) Pair(
                    R.drawable.completed,
                    "Completed"
                ) else Pair(R.drawable.filled_play, "Play")
                Icon(
                    imageVector = ImageVector.vectorResource(id = icon.first),
                    contentDescription = icon.second
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WhatsNeedComponent(
    noOfExercise: String,
    duration: String,
    equipment: List<Equipment>,
    modifier: Modifier = Modifier
) {

    VStack(
        spaceBy = 8.dp,
        modifier = modifier
            .then(Modifier)
            .clip(RoundedCornerShape(25.dp))
            .background(brush = Brush.verticalGradient(listOf(Color.Transparent, aliceBlue)))
            .border(1.dp, Color.Black, RoundedCornerShape(25.dp))
            .padding(vertical = 10.dp, horizontal = 18.dp),
    ) {

        HStack(
            spaceBy = 8.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = black25)
                .padding(8.dp)
        ) {
            IconTextHStack(
                icon = drawable.exercise,
                text = stringResource(R.string.append_exercises, noOfExercise),
                style = TextStyle(color = Color.White),
                iconModifier = Modifier.size(18.dp)
            )
            IconTextHStack(
                icon = drawable.filled_time,
                text = duration,
                style = TextStyle(color = Color.White),
                iconModifier = Modifier.size(18.dp)
            )
        }

        FlowRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 2
        ) {
            repeat(equipment.size) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {

                    IconIMGTextHStack(
                        icon = equipment[index].icon,
                        text = equipment[index].name,
                        tint = null,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = black25
                        ),
                        iconModifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

}


@Preview
@Composable
private fun TodayRoutineScreenPreview() {
    MaterialTheme {

        var toggleScreen by remember { mutableStateOf(false) }

        Scaffold(
            modifier = Modifier,
            containerColor = Color.White,
            topBar = {
                TopBarCenterAlignTextAndBack(
                    title = "Today’s Routine",
                    onBackPress = {}
                )
            }
        ) { innerPadding ->
            TodayRoutineContent(
                data = TempDataSource.dayExercises,
                event = {},
                modifier = Modifier
                    .padding(innerPadding)
                    .clickable {
                        toggleScreen = !toggleScreen
                    }
            )
        }

        //StartWorkOut Screen
        AnimatedVisibility(
            visible = toggleScreen,
            enter = slideInVertically(
                initialOffsetY = { it }, // Start from the bottom of the screen
                animationSpec = tween(durationMillis = 500) // Customize animation duration
            ),
            exit = slideOutVertically(
                targetOffsetY = { it }, // Exit to the bottom of the screen
                animationSpec = tween(durationMillis = 500)
            ),
            modifier = Modifier
                .fillMaxSize()
                .clickable { toggleScreen = !toggleScreen }
        ) {
            StartWorkoutScreen(
                uiState = TodayRoutineUiState.WorkoutScreenUiState(),
                event = {}
            ) // Replace with your composable
        }
    }
}

