package com.medrevpatient.mobile.app.ux.main.myprogress

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.medrevpatient.mobile.app.data.source.TempDataSource
import com.medrevpatient.mobile.app.data.source.remote.dto.ViewGoal
import com.medrevpatient.mobile.app.navigation.NavigationAction
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.FullSizeCenterBox
import com.medrevpatient.mobile.app.ui.FullSizeCircularLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.canvas.RoundedRectangularProgressIndicator
import com.medrevpatient.mobile.app.ui.theme.MedrevPatientTheme
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ui.transformToLazyPagingItems
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.ux.main.MainActivity
import com.medrevpatient.mobile.app.ux.main.component.HeaderContentWrapper
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault.textStyle
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getIcon
import com.medrevpatient.mobile.app.ux.main.component.getTypeByElement
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.component.getUnitWithSortForm
import com.medrevpatient.mobile.app.ux.main.myprogress.component.SheetLauncher
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import timber.log.Timber
import java.io.File

@Preview
@Composable
private fun ViewGoalScreenPreview(
    modifier: Modifier = Modifier
) {
    MedrevPatientTheme {
        Scaffold(
            modifier = modifier.statusBarsPadding(),
            containerColor = white,
            topBar = {
                TopBarCenterAlignTextAndBack(
                    title = "View Goal",
                    onBackPress = {

                    },
                    onTrailingPress = {

                    }
                )
            }
        ) { innerPadding ->
            MyGoalsDetailsContent(
                uiState = MyGoalUiState(),
                lazyPagingItems = TempDataSource.viewGoalPagingData.transformToLazyPagingItems(),
                event = {},
                element = "Calories",
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
fun ViewGoalScreen(
    navController: NavHostController,
    viewModel: MyGoalViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val goalLogPaging = viewModel.goal.collectAsLazyPagingItems()
    val context = viewModel.context

    Scaffold(
        modifier = modifier.statusBarsPadding(),
        containerColor = white,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "View Goal",
                onBackPress = {
                    if (viewModel.isFromNotification) {
                        val intent = Intent(context, MainActivity::class.java)
                        viewModel.navigate(NavigationAction.NavigateIntent(intent, finishCurrentActivity = true))
                    } else {
                        viewModel.popBackStack()
                    }
                },
                onTrailingPress = {
                    viewModel.event(MyGoalsUiEvent.SheetEvent(SheetEvents.OptionMenu(SheetType.OPTION_MENU)))
                }
            )
        }
    ) { innerPadding ->
        MyGoalsDetailsContent(
            uiState = uiState,
            event = viewModel::event,
            element = uiState.viewGoal.data.goal.goalType.getElementByType(),
            lazyPagingItems = goalLogPaging,
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }

    getFromBackStack<List<File>>(navController, RouteMaker.Keys.FILE_PATHS)?.let {
        viewModel.event(
            MyGoalsUiEvent.SheetEvent(
                SheetEvents.AddLogToSpecific(
                    element = uiState.addLogToSpecific.element,
                    images = it,
                    value = uiState.addLogToSpecific.value
                )
            )
        )
    }
}

@Composable
fun MyGoalsDetailsContent(
    uiState: MyGoalUiState,
    lazyPagingItems: LazyPagingItems<ViewGoal.Data.Log>,
    element: String,
    event: MyGoalsEvent,
    modifier: Modifier = Modifier
) {


    PagingResultHandler(lazyPagingState = lazyPagingItems) { _ ->
        uiState.apply {

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        top = 18.dp,
                        bottom = 85.dp,
                        end = 18.dp
                    ),
                ) {

                    item {
                        Header(viewGoal = viewGoal)
                    }


                    item {
                        VStack(0.dp) {
                            HeaderContentWrapper("All Logs")
                            if (lazyPagingItems.itemCount <= 0) {
                                FullSizeCenterBox {
                                    Text(
                                        "No data found!",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }

                    items(lazyPagingItems.itemCount) { index ->
                        val log = lazyPagingItems[index] ?: return@items
                        ViewGoalItem(
                            element = element,
                            value = log.value.formatLogValue(element.getTypeByElement()),
                            date = log.formatedDate,
                            time = log.formatedTime
                        )
                    }

                    when (val appendState = lazyPagingItems.loadState.append) {
                        is LoadState.Loading -> {
                            item {
                                FullSizeCircularLoader(modifier = Modifier.height(56.dp))
                            }
                        }

                        is LoadState.Error -> {
                            item {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = textStyle.toSpanStyle()) {
                                            append(
                                                appendState.error.localizedMessage
                                                    ?: "something went wrong!"
                                            )
                                        }
                                        withStyle(
                                            style = textStyle.toSpanStyle()
                                                .copy(fontWeight = FontWeight.SemiBold)
                                        ) {
                                            append("\nRetry")
                                        }
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            lazyPagingItems.retry()
                                        }
                                )
                            }
                        }

                        is LoadState.NotLoading -> {}
                    }
                }

                SkaiButton(
                    text = "Add new log",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -(20.dp))
                ) {
                    event(
                        MyGoalsUiEvent.SheetEvent(
                            SheetEvents.AddLogToSpecific(
                                element = viewGoal.data.goal.goalType.getElementByType(),
                                value = ""
                            )
                        )
                    )

                    Timber.d("Add new log $viewGoal ")
                }

                SheetLauncher(
                    event = { event(MyGoalsUiEvent.SheetEvent(it)) },
                    sheetType = sheetType,
                    shouldShowSheet = isSheetVisible,
                    uiState = SheetUiState(
                        shouldShowSuccessSheet = shouldShowSuccessSheet,
                        addLogToSpecific = addLogToSpecific,
                        editGoal = editGoal,
                        deleteGoal = deleteGoal
                    ),
                    onSuccess = {
                        lazyPagingItems.refresh()
                    }
                )
            }
        }
    }
}


data class MyGoalsDetailsListItem(
    val value: String,
    val date: String,
    val time: String
)


@Preview(showBackground = true)
@Composable
private fun Header(
    modifier: Modifier = Modifier,
    viewGoal: ViewGoal = ViewGoal(),
) {

    val element = viewGoal.data.goal.goalType.getElementByType()
    val completed = viewGoal.data.completed
    val goal = viewGoal.data.goal.goalValue
    val date = viewGoal.data.goal.date.split(',')
    val progress: Float = completed / viewGoal.data.goal.goalValue.toFloat()

    val textStyle = MaterialTheme.typography.headlineSmall.copy(
        color = black25,
        fontSize = 26.sp,
        fontWeight = FontWeight.W800
    )

    Box(
        modifier = Modifier.clip(RoundedCornerShape(25)),
        contentAlignment = Alignment.Center
    ) {

        element.getIcon()?.let {
            Image(
                imageVector = ImageVector.vectorResource(it),
                contentDescription = null,
                colorFilter = ColorFilter.tint(black25),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(144.dp)
                    .offset(x = 35.dp, y = 16.dp)
                    .clip(RoundedCornerShape(bottomEnd = 25.dp))
            )
        }

        VStack(
            18.dp,
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            aliceBlue.copy(alpha = 0.7f),
                            aliceBlue
                        )
                    )
                )
                .padding(vertical = 18.dp)
        ) {
            VStack(0.dp) {

                Text(
                    text = element.uppercase() + " GOAL",
                    style = textStyle.copy(
                        letterSpacing = 5.sp,
                        fontSize = 16.sp,
                    )
                )

                (element.lowercase() == "weight").ifTrue {
                    Text(
                        text = "Ideally you should log your weight every 3 months",
                        style = textStyle.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = black25.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            //Horizontal Gradient Divider
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                black25.copy(alpha = 0.5f), black25,
                                black25.copy(alpha = .5f)
                            )
                        )
                    )
            )

            HStack(
                spaceBy = 8.dp,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(bottom = 18.dp, top = 8.dp)
                    .fillMaxWidth()
            ) {


                RoundedRectangularProgressIndicator(
                    progress = { progress },
                    roundedRectangularProgressIndicator = RoundedRectangularProgressIndicator(
                        trackColor = black25.copy(alpha = .1f),
                        progressColor = black25

                    ),
                    modifier = Modifier.size(70.dp)
                ) {
                    Text(
                        text = completed.formatLogValue(element.getTypeByElement()),
                        style = textStyle.copy(fontSize = 18.sp, fontWeight = FontWeight.W800)
                    )
                }


                Spacer(Modifier.padding(1.dp))

                VStackText(
                    value = completed.formatLogValue(element.getTypeByElement()),
                    unit = element.getUnitWithSortForm(),
                    textStyle = textStyle,
                    description = "Completed"
                )
                VStackText(
                    value = goal.formatLogValue(element.getTypeByElement()),
                    unit = element.getUnitWithSortForm(),
                    textStyle = textStyle,
                    description = "Your Goal"
                )

                VStackText(
                    value = date.first() + ",",
                    unit = date.last(),
                    textStyle = textStyle,
                    description = "Created On"
                )

            }
        }
    }
}


@Composable
private fun VStackText(
    value: String,
    unit: String,
    description: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {

    val spannedText = buildAnnotatedString {
        withStyle(style = textStyle.copy(fontSize = 18.sp).toSpanStyle()) {
            append(value)
        }
        withStyle(style = textStyle.copy(fontSize = 12.sp).toSpanStyle()) {
            append(unit)
        }
    }

    VStack(0.dp, modifier) {
        Text(
            text = spannedText,
            style = textStyle.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                lineHeight = 25.sp,
            ),
        )

        Text(
            text = description,
            style = textStyle.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = grey94,
                lineHeight = 14.sp
            ),
        )
    }
}

@Composable
private fun ViewGoalItem(
    modifier: Modifier = Modifier,
    element: String = "Protein",
    value: String,
    date: String,
    time: String,
) {

    val textStyle = MaterialTheme.typography.headlineSmall.copy(
        color = black25,
        fontSize = 26.sp,
        fontWeight = FontWeight.W800
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(25),
        color = black25.copy(alpha = .02f)
    ) {
        HStack(8.dp, modifier = Modifier.padding(18.dp)) {
            HStack(4.dp) {
                element.getIcon()?.let {
                    Icon(
                        imageVector = ImageVector.vectorResource(it),
                        contentDescription = null,
                        tint = black25
                    )

                    Text(
                        text = value + " " + element.getUnit(),
                        style = textStyle.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                        ),
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HStack(4.dp) {
                Icon(
                    imageVector = ImageVector.vectorResource(drawable.calendar),
                    contentDescription = null,
                    tint = black25,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = date,
                    style = textStyle.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                )
            }

            HStack(4.dp) {
                Icon(
                    imageVector = ImageVector.vectorResource(drawable.time),
                    contentDescription = null,
                    tint = black25,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = time,
                    style = textStyle.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    )
                )
            }
        }
    }
}


