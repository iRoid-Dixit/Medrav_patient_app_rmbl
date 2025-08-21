package com.medrevpatient.mobile.app.ux.main.myprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.remote.dto.Goal
import com.medrevpatient.mobile.app.data.source.remote.dto.LastWeekStats
import com.medrevpatient.mobile.app.data.source.remote.dto.MyProgress
import com.medrevpatient.mobile.app.data.source.remote.dto.elementsPlaceholder
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.RouteMaker.MyGoals
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.PullToRefresh
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.canvas.graph.ProgressBarChartWithTextIndicator
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black25A25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.utils.ext.toJsonString
import com.medrevpatient.mobile.app.ux.main.component.HeaderContentWrapper
import com.medrevpatient.mobile.app.ux.main.component.NoItemPlaceHolder
import com.medrevpatient.mobile.app.ux.main.component.RoundedRectangularProgressComponent
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getIcon
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.myprogress.component.SheetLauncher
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import timber.log.Timber
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProgressScreen(
    navController: NavHostController,
    viewModel: MyProgressViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefresh(
        isRefreshing = uiState.myProgress is NetworkResult.Loading,
        onRefresh = { viewModel.event(MyProgressUiEvent.Refresh) },
    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = white,
            topBar = {
                //TODO: Change navigation path name and screen name also, from My Progress to Health Tracking
                TopBarCenterAlignTextAndBack(title = "Health Tracking")
            },
        ) { innerPadding ->
            MyProgressContent(
                uiState = uiState,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                event = viewModel::event
            )
        }
    }

    getFromBackStack<List<File>>(
        navController,
        RouteMaker.Keys.FILE_PATHS
    )?.let {
        when (uiState.sheetType) {
            SheetType.CREATE_GOAL -> {
                viewModel.event(
                    MyProgressUiEvent.SheetEvent(
                        SheetEvents.CreateGoal(
                            element = uiState.createGoal.element,
                            images = it,
                            value = uiState.createGoal.value
                        )
                    )
                )
            }

            SheetType.ADD_LOG_TO_SPECIFIC_GOAL -> {
                viewModel.event(
                    MyProgressUiEvent.SheetEvent(
                        SheetEvents.AddLogToSpecific(
                            element = uiState.addLogToSpecific.element,
                            images = it,
                            value = uiState.addLogToSpecific.value
                        )
                    )
                )
            }

            else -> {}
        }
    }

    getFromBackStack<Boolean>(
        navController,
        RouteMaker.Keys.REFRESH_MY_PROGRESS
    )?.let {
        Timber.d("Refresh: $it")
        viewModel.event(MyProgressUiEvent.Refresh)
    }
}

@Composable
fun MyProgressContent(
    uiState: MyProgressUiState,
    event: ProgressEvent,
    modifier: Modifier = Modifier
) {

    NetworkResultHandler(
        networkResult = uiState.myProgress,
        onRetry = { event(MyProgressUiEvent.Refresh) }
    ) { onSuccess ->

        onSuccess.data?.apply {

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = modifier
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    PagerLastWeekStats(
                        lastWeekStats = lastWeekStats,
                        modifier = Modifier.height(350.dp)
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    HeaderContentWrapper(
                        title = "Add New Logs".uppercase(),
                        spaceBy = 18.dp,
                        wrapperPadding = 0.dp
                    ) {}
                }


                items(6) { idx ->

                    val todayStats =
                        todayStates.plus(elementsPlaceholder).groupBy { it.goalType }
                            .map { (_, value) -> value.first() }.getOrNull(idx)

                    todayStats?.let { stats ->

                        val progress = stats.completedGoal / stats.goal.toFloat()

                        RoundedRectangularProgressComponent(
                            current = stats.log,
                            total = if (stats.goal <= 0) null else stats.goalFormatted,
                            progress = progress,
                            description = stats.element,
                            icon = stats.element.getIcon(),
                            modifier = Modifier.fillMaxSize(),
                            onAddClick = {
                                if (todayStates.map { it.id }.contains(stats.id)) {
                                    event(
                                        MyProgressUiEvent.SheetEvent(
                                            SheetEvents.AddElementLog(
                                                element = stats.element,
                                                value = "",
                                                minute = ""
                                            )
                                        )
                                    )
                                } else {
                                    event(
                                        MyProgressUiEvent.SheetEvent(
                                            SheetEvents.CreateGoal(
                                                element = stats.element,
                                                value = "",
                                                value2 = "",
                                                images = emptyList(),
                                                elementToDisable = todayStates.map { it.element }
                                            )
                                        )
                                    )
                                }
                            }
                        )
                    }
                }


                item(span = { GridItemSpan(maxLineSpan) }) {
                    Goal(
                        goal = myGoal.firstOrNull() ?: Goal(),
                        event = event,
                        modifier = Modifier.fillMaxWidth(),
                        onAllClick = {
                            myGoal.toJsonString()?.let {
                                event(MyProgressUiEvent.NavigateTo(MyGoals.createRoute()))
                            }
                        }
                    )
                }
            }
        }

        SheetLauncher(
            event = { event(MyProgressUiEvent.SheetEvent(it)) },
            sheetType = uiState.sheetType,
            shouldShowSheet = uiState.isSheetVisible,
            uiState = SheetUiState(
                shouldShowSuccessSheet = uiState.shouldShowSuccessSheet,
                addElementLog = uiState.addElementLog.copy(tabs = onSuccess.data?.todayStates?.map { it.goalType }
                    ?: emptyList()),
                createGoal = uiState.createGoal,
                addLogToSpecific = uiState.addLogToSpecific
            ),
            onSheetDismiss = { }
        )
    }
}

//@Preview(showBackground = true)
@Composable
private fun PagerLastWeekStats(
    lastWeekStats: List<LastWeekStats>,
    modifier: Modifier = Modifier
) {


    if (lastWeekStats.isEmpty())
        NoLastWeekStatsPlaceHolder(modifier = modifier.fillMaxSize())

    val pagerState = rememberPagerState(pageCount = { lastWeekStats.size })

    VStack(
        spaceBy = 10.dp, modifier = modifier
    ) {

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            modifier = Modifier.weight(1f)
        ) { page ->

            LastWeekStates(
                lastWeekStats = lastWeekStats[page],
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) black25 else black25A25
                val width = if (pagerState.currentPage == iteration) 30.dp else 10.dp

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .size(width = width, height = 8.dp)
                        .background(color)
                )
                Spacer(modifier = Modifier.padding(12.dp))
            }

        }
    }
}


//@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun LastWeekStatsPreview() {
    LastWeekStates(
        lastWeekStats = LastWeekStats(),
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun LastWeekStates(
    lastWeekStats: LastWeekStats,
    modifier: Modifier = Modifier
) {

    lastWeekStats.apply {

        VStack(
            spaceBy = 18.dp,
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12))
                .background(color = black25)
                .padding(18.dp),
        ) {
            Text(
                text = "Previous Logs",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = W300,
                color = white,
                modifier = Modifier
                    .clip(RoundedCornerShape(35))
                    .background(color = white.copy(alpha = 0.05f))
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            )

            //Title Value Component
            HStack(
                spaceBy = 8.dp,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                VStack(2.dp) {
                    Text(
                        text = highestFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = white,
                        fontWeight = Bold,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Highest",
                        style = MaterialTheme.typography.bodySmall,
                        color = white.copy(alpha = 0.6f),
                        lineHeight = 12.sp
                    )
                }

                VStack(2.dp) {
                    Text(
                        text = averageFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = white,
                        fontWeight = Bold,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Average",
                        style = MaterialTheme.typography.bodySmall,
                        color = white.copy(alpha = 0.6f),
                        lineHeight = 12.sp
                    )
                }

                VStack(2.dp) {
                    Text(
                        text = goalFormatted,
                        style = MaterialTheme.typography.bodyLarge,
                        color = white,
                        fontWeight = Bold,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Goal",
                        style = MaterialTheme.typography.bodySmall,
                        color = white.copy(alpha = 0.6f),
                        lineHeight = 12.sp
                    )
                }
            }

            //Graph
            ProgressBarChartWithTextIndicator(
                progressBarChart = lastWeekStats.toProgressBarChart(),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            //Footer
            HStack(8.dp, modifier = Modifier.fillMaxWidth()) {

                HStack(8.dp) {
                    footer.metric.getIcon()?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null,
                            tint = white
                        )
                    }
                    Text(
                        text = footer.metric,
                        style = MaterialTheme.typography.bodyMedium,
                        color = white,
                        fontWeight = Bold
                    )
                }

                Spacer(Modifier.weight(1f))

                val style = SpanStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = outFit,
                    fontWeight = SemiBold,
                )

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = style) {
                            append(footer.total.formatLogValue(goalType = lastWeekStats.elementType))
                        }
                        withStyle(style = style.copy(fontSize = 12.sp)) {
                            append(" " + footer.unit)
                        }
                    },
                    maxLines = 1,
                    overflow = Ellipsis,
                )
            }
        }
    }
}


@Composable
private fun Goal(
    modifier: Modifier = Modifier,
    goal: Goal,
    onAllClick: () -> Unit,
    event: ProgressEvent
) {

    HeaderContentWrapper(
        spaceBy = 18.dp,
        title = "My Goals".uppercase(),
        trailingText = if (goal.goalValue <= 0) null else "All",
        wrapperPadding = 0.dp,
        onAllClick = onAllClick,
        modifier = modifier
    ) {

        if (goal.goalValue <= 0)
            NoItemPlaceHolder(
                modifier = Modifier.fillMaxWidth(),
                title = "Let’s set-up your fitness goal, to achieve your best!",
                subTitle = "No fitness goal have been created yet.",
                icon = drawable.goal,
                btnText = "Create Goal"
            ) {
                event(MyProgressUiEvent.SheetEvent(SheetEvents.CreateGoal()))
            }
        else
            VStack(
                18.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                aliceBlue.copy(alpha = 0.5f),
                                aliceBlue
                            )
                        )
                    )
                    .padding(18.dp)
            ) {

                //Header
                HStack(8.dp) {
                    Column {
                        Text(
                            text = goal.element,
                            style = MaterialTheme.typography.bodyLarge,
                            color = black25,
                            fontWeight = Bold
                        )
                        Text(
                            text = "Last 90 Days",
                            style = MaterialTheme.typography.bodySmall,
                            color = grey94,
                            fontWeight = Bold
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            event(
                                MyProgressUiEvent.SheetEvent(
                                    SheetEvents.AddLogToSpecific(
                                        element = goal.goalType.getElementByType(),
                                        value = "",
                                    )
                                )
                            )
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = null,
                            tint = black25
                        )
                    }
                }

                //Body
                HStack(8.dp, modifier = Modifier.height(98.dp)) {
                    GoalBodyComponent(
                        value = goal.goal,
                        element = goal.element,
                        description = "Your Goal",
                        modifier = Modifier.weight(1f)
                    )
                    GoalBodyComponent(
                        value = goal.log,
                        element = goal.element,
                        description = if (goal.element.lowercase() == "weight") "Current Weight" else "${goal.element} Logs",
                        modifier = Modifier.weight(1f)
                    )
                }

                goal.image.isNotEmpty().ifTrue {
                    //Footer
                    VStack(
                        spaceBy = 18.dp,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = black25,
                                shape = RoundedCornerShape(12)
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        white.copy(alpha = 0.4f),
                                        aliceBlue,
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {

                        Text(
                            text = "Before Gallery".uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = black25,
                            fontWeight = Bold,
                            letterSpacing = 2.sp
                        )


                        HStack(18.dp, modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            repeat(3) {
                                val imageObj = goal.image.getOrNull(it)
                                GoalFooter(
                                    image = imageObj?.url,
                                    date = imageObj?.date ?: "",
                                    modifier = Modifier.size(136.dp, 186.dp)
                                )
                            }
                        }

                        if (goal.image.size > 3) {
                            Text(
                                text = "After Gallery".uppercase(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = black25,
                                fontWeight = Bold,
                                letterSpacing = 2.sp
                            )

                            HStack(
                                18.dp,
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                repeat(3) {
                                    val imageObj = goal.image.getOrNull(3 + it)
                                    GoalFooter(
                                        image = imageObj?.url,
                                        date = imageObj?.date ?: "",
                                        modifier = Modifier.size(136.dp, 186.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}


@Composable
private fun GoalBodyComponent(
    value: String,
    element: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(25))
            .border(
                width = 1.dp,
                color = black25,
                shape = RoundedCornerShape(25)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(aliceBlue.copy(alpha = 0.5f), aliceBlue),
                )
            )
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {


        val style = SpanStyle(
            color = black25,
            fontSize = 40.sp,
            fontFamily = outFit,
            fontWeight = W800,
        )

        if (value.isNotEmpty()) {
            Column {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = style) {
                            append(value)
                        }
                        withStyle(style = style.copy(fontSize = 18.sp)) {
                            append(element.getUnit())
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = black25,
                    fontWeight = Bold,
                    maxLines = 1,
                    overflow = Ellipsis
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = black25.copy(alpha = 0.6f),
                    fontWeight = Medium
                )
            }
        } else {
            VStack(
                spaceBy = 0.dp,
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(drawable.grey_progress),
                    contentDescription = null,
                    tint = black25.copy(.8f),
                    modifier = Modifier.size(42.dp)
                )

                Text(
                    text = "No $element log added!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = black25.copy(alpha = 0.6f),
                    fontWeight = Medium,
                    maxLines = 2,
                    overflow = Ellipsis
                )
            }
        }
    }
}


@Composable
private fun GoalFooter(
    image: String?,
    date: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(188.dp)
            .clip(RoundedCornerShape(18)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = image,
            placeholder = painterResource(id = R.drawable.img_portrait_placeholder),
            error = painterResource(id = R.drawable.img_landscape_placeholder),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black
                        ),
                    )
                )
        )

        Text(
            text = date,
            style = MaterialTheme.typography.bodySmall,
            color = white,
            fontWeight = W300,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        )
    }
}


@Composable
private fun NoLastWeekStatsPlaceHolder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        VStack(
            spaceBy = 0.dp,
            modifier = Modifier,
        ) {

            Icon(
                imageVector = ImageVector.vectorResource(drawable.grey_progress),
                contentDescription = null,
                tint = grey94,
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = "No Stats!",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = W300,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = Ellipsis,
                color = black25
            )
        }
    }
}


@Preview(showBackground = true, heightDp = 1200, widthDp = 500)
@Composable
private fun MyProgressPreview() {
    MaterialTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            containerColor = white,
            topBar = {
                TopBarCenterAlignTextAndBack(title = "Health Tracking")
            },
        ) { innerPadding ->
            MyProgressContent(
                uiState = MyProgressUiState(
                    myProgress = NetworkResult.Success(
                        ApiResponse(
                            MyProgress()
                        )
                    )
                ),
                event = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
