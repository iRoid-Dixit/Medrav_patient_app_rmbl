package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.FullSizeCenterBox
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.LoadingError
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.neonNazar
import com.medrevpatient.mobile.app.ui.theme.orangeYellow
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.utils.alias.string
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.component.findFirstIncompleteDay
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ConfirmationDialog
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.WeeklyProgress


@Composable
fun ViewProgramScreen(
    navController: NavHostController,
    viewModel: ViewProgramViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkResultHandler(
        networkResult = uiState.program,
        onBackPress = {
            viewModel.popBackStack()
        },
        onRetry = {
            viewModel.event(ViewProgramUIEvent.Refresh)
        }
    ) {
        BackGroundImageGradient(it.data?.image) {
            Scaffold(
                modifier = modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                containerColor = Color.Transparent,
                topBar = {
                    BackWithPinTopBar(
                        isLoader = uiState.isLoading,
                        isPin = uiState.isPin,
                        event = viewModel::event,
                        onBackPress = { viewModel.popBackStack() }
                    )
                }
            ) { innerPadding ->
                ViewProgramContent(
                    uiState = uiState,
                    program = it.data,
                    event = viewModel::event,
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                )
            }
        }
    }

    getFromBackStack<Boolean>(navController = navController, key = RouteMaker.Keys.REFRESH)?.let {
        viewModel.event(ViewProgramUIEvent.Refresh)
    }

}

@Composable
fun BackWithPinTopBar(
    isLoader: Boolean,
    isPin: Boolean,
    event: ViewProgramEvent,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier
) {

    val pinToggle =
        if (isPin) Triple(
            drawable.pin__filled_,
            "Pinned",
            orangeYellow
        ) else Triple(drawable.pin__outline_, "Un-Pinned", white)

    HStack(8.dp, modifier) {
        TopBarCenterAlignTextAndBack(
            title = "",
            tint = white,
            onBackPress = onBackPress,
            modifier = Modifier.weight(0.8f)
        )

        IconButton(
            onClick = {
                if (isLoader)
                    return@IconButton

                event(ViewProgramUIEvent.TogglePinProgram)
            },
            modifier = Modifier
                .padding(end = 4.dp)
                .clip(RoundedCornerShape(25))
                .size(36.dp)
                .padding(4.dp),
        ) {

            Icon(
                imageVector = ImageVector.vectorResource(pinToggle.first),
                contentDescription = pinToggle.second,
                tint = pinToggle.third
            )

        }
    }
}

@Composable
private fun ViewProgramContent(
    uiState: ViewProgramUiState,
    program: Program?,
    event: ViewProgramEvent,
    modifier: Modifier = Modifier
) {

    var shouldShowSkipDialog by remember { mutableStateOf<Int?>(null) }

    program?.apply {

        val weeklyDays = dayDistribution.chunked(7)

        VStack(
            18.dp, modifier = modifier
                .padding(horizontal = 18.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.5f),
                contentAlignment = Alignment.BottomCenter
            ) {

                Header(
                    header = Header(
                        title = name,
                        duration = days,
                        calories = kcal,
                        mediaUrl = image,
                        description = description
                    ),
                    onClick = {
                        dayDistribution.findFirstIncompleteDay()?.let { day ->
                            if (day.isRestDay) {
                                shouldShowSkipDialog = day.day
                            } else {
                                event(
                                    ViewProgramUIEvent.NavigateTo(
                                        RouteMaker.TodayRoutine.createRoute(
                                            id = program.id,
                                            day = day.day.toString()
                                        )
                                    )
                                )
                            }
                        }
                    },
                    modifier = Modifier
                )
            }
            WeeklyProgress(
                weeks = weeklyDays,
                onClickCell = {
                    event(
                        ViewProgramUIEvent.NavigateTo(
                            RouteMaker.TodayRoutine.createRoute(
                                id = program.id,
                                day = it.day.toString(),
                            )
                        )
                    )
                },
                modifier = Modifier.weight(0.5f)
            )
        }

        //Rest Day Skip Dialog
        shouldShowSkipDialog?.let {
            ConfirmationDialog(
                description = "It's time to take your rest day! Rest and recovery are key to progress.",
                positiveText = "Take Rest",
                negativeText = "Skip Rest",
                negative = {
                    event(
                        ViewProgramUIEvent.SkipRestDay(
                            day = shouldShowSkipDialog.toString(),
                            programId = program.id
                        )
                    )
                },
                positive = {
                    shouldShowSkipDialog = null
                },
            )
        }

    } ?: FullSizeCenterBox {
        LoadingError("No data Found")
    }

}


@Composable
private fun Header(
    header: Header = Header(
        title = "Step It Up Fitness Boot Camp",
        duration = "60 day",
        calories = "530 KCAL",
        mediaUrl = "",
        description = "A comprehensive fitness program tailored to an individual typically focuses on one or more specific skills, and on age- or health-related."
    ),
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    header.apply {
        Box(
            modifier = modifier.then(
                Modifier
                    .heightIn(132.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(
                        color = black25,
                        shape = RoundedCornerShape(25.dp)
                    )
            )
        ) {
            HStack(
                8.dp,
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 20.dp),
                verticalAlignment = Alignment.Top
            ) {
                VStack(
                    8.dp, horizontalAlignment = Alignment.Start,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    HStack(
                        spaceBy = 8.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        val textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = white
                        )
                        IconTextHStack(
                            icon = R.drawable.filled_time,
                            text = stringResource(string.append_days, duration),
                            tint = neonNazar,
                            style = textStyle
                        )
                        IconTextHStack(
                            icon = R.drawable.calorie,
                            text = stringResource(string.append_kcal, calories),
                            tint = neonNazar,
                            style = textStyle
                        )
                    }

                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.W300,
                        color = Color.White,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(70.dp)
                ) {
                    Icon(
                        painter = painterResource(drawable.filled_play),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    )
                }

            }
        }
    }
}

data class Header(
    val title: String,
    val duration: String,
    val calories: String,
    val mediaUrl: String,
    val description: String
)


@Composable
private fun BackGroundImageGradient(
    image: String?,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {},
) {

    Box(modifier = modifier) {
        VStack(
            0.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(0.5f)
            ) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.img_portrait_placeholder),
                    error = painterResource(R.drawable.img_portrait_placeholder),
                    alignment = Alignment.TopCenter,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White
                                )
                            )
                        )
                )
            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .background(color = Color.White)
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(200.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
        )

        content()
    }

}

@Preview(showBackground = true)
@Composable
private fun ViewProgramPreview() {
    BackGroundImageGradient(null) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = Color.Transparent,
            topBar = {
                BackWithPinTopBar(
                    isLoader = false,
                    isPin = false,
                    event = {},
                    onBackPress = { }
                )
            }
        ) { innerPadding ->
            ViewProgramContent(
                uiState = ViewProgramUiState(),
                program = Program(completedDays = 0),
                event = {},
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            )
        }
    }
}