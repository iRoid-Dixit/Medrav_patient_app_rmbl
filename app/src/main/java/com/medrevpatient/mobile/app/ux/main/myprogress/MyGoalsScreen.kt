package com.medrevpatient.mobile.app.ux.main.myprogress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.medrevpatient.mobile.app.data.source.remote.helper.NetworkResult
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.PullToRefresh
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.IconBackgroundMaker
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.convertHHMMtoSS
import com.medrevpatient.mobile.app.ux.main.component.formatLogValue
import com.medrevpatient.mobile.app.ux.main.component.getTypeByElement
import com.medrevpatient.mobile.app.ux.main.component.getUnitWithSortForm
import com.medrevpatient.mobile.app.ux.main.myprogress.component.SheetLauncher
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import java.io.File


@Preview
@Composable
private fun MyGoalsPreview() {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = white,
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "My Daily Goals",
                onBackPress = { }
            )
        }
    ) { innerPadding ->
        MyGoalsContent(
            uiState = MyGoalUiState(goals = NetworkResult.Error("Error")),
            event = {},
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGoalsScreen(
    navController: NavHostController,
    viewModel: MyGoalViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PullToRefresh(
        isRefreshing = uiState.goals is NetworkResult.Loading,
        onRefresh = { viewModel.event(MyGoalsUiEvent.Refresh) }
    ) {
        Scaffold(
            modifier = modifier.statusBarsPadding(),
            containerColor = white,
            topBar = {
                TopBarCenterAlignTextAndBack(
                    title = "My Daily Goals",
                    onBackPress = { viewModel.event(MyGoalsUiEvent.Back) }
                )
            }
        ) { innerPadding ->
            MyGoalsContent(
                uiState = uiState,
                event = viewModel::event,
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
            )
        }
    }

    getFromBackStack<Boolean>(
        navController,
        REFRESH
    )?.let {
        viewModel.event(MyGoalsUiEvent.Refresh)
    }

    getFromBackStack<List<File>>(
        navController,
        com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.FILE_PATHS
    )?.let {
        viewModel.event(
            MyGoalsUiEvent.SheetEvent(
                SheetEvents.CreateGoal(
                    element = uiState.createGoal.element,
                    images = it,
                    value = uiState.createGoal.value
                )
            )
        )
    }

}


@Composable
fun MyGoalsContent(
    uiState: MyGoalUiState,
    event: MyGoalsEvent,
    modifier: Modifier = Modifier
) {

    NetworkResultHandler(
        networkResult = uiState.goals,
        onRetry = { event(MyGoalsUiEvent.Refresh) },
    ) { onSuccess ->

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 18.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(onSuccess.data) { item ->
                    MyGoalItem(
                        myGoalsItem = item.toGoalItem(),
                        event = event
                    )
                }
            }

            if (onSuccess.data.size < 7) {
                SkaiButton(
                    text = "create new goal",
                    modifier = Modifier
                        .offset(y = -(18.dp))
                        .align(Alignment.BottomCenter)
                ) { event(MyGoalsUiEvent.SheetEvent(SheetEvents.CreateGoal())) }
            }
        }
    }

    SheetLauncher(
        event = { event(MyGoalsUiEvent.SheetEvent(it)) },
        sheetType = uiState.sheetType,
        shouldShowSheet = uiState.isSheetVisible,
        uiState = SheetUiState(
            shouldShowSuccessSheet = uiState.shouldShowSuccessSheet,
            createGoal = uiState.createGoal,
            editGoal = uiState.editGoal,
            deleteGoal = uiState.deleteGoal,
            addLogToSpecific = uiState.addLogToSpecific
        ),
        onSheetDismiss = {}
    )
}

data class MyGoalsItem(
    val id: String,
    val value: String,
    val value2: String,
    val element: String,
    val creationOn: String,
)

@Composable
private fun MyGoalItem(
    myGoalsItem: MyGoalsItem,
    event: MyGoalsEvent,
    modifier: Modifier = Modifier
) {
    val textStyle = MaterialTheme.typography.headlineSmall.copy(
        color = black25,
        fontSize = 26.sp,
        fontWeight = FontWeight.W800
    )

    VStack(
        spaceBy = 0.dp,
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(25))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            aliceBlue.copy(alpha = 0.5f),
                            aliceBlue
                        )
                    )
                )
                .clickable {
                    event(
                        MyGoalsUiEvent.NavigateTo(
                            com.medrevpatient.mobile.app.navigation.RouteMaker.ViewGoal.createRoute(
                                id = myGoalsItem.id
                            )
                        )
                    )
                }
                .padding(18.dp)
        )
    ) {

        myGoalsItem.apply {

            //Header
            HStack(8.dp) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = textStyle.toSpanStyle()) {
                            append(
                                convertHHMMtoSS(element, value, value2).toLong()
                                    .formatLogValue(element.getTypeByElement())
                            )
                        }
                        withStyle(style = textStyle.copy(fontSize = 18.sp).toSpanStyle()) {
                            append(" ${element.getUnitWithSortForm()}")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                IconBackgroundMaker(
                    icon = drawable.edit,
                    onClick = {
                        event(
                            MyGoalsUiEvent.SheetEvent(
                                SheetEvents.EditGoal(
                                    id = myGoalsItem.id,
                                    element = myGoalsItem.element,
                                    value = myGoalsItem.value,
                                    value2 = myGoalsItem.value2
                                )
                            )
                        )
                    },
                )
                IconBackgroundMaker(
                    drawable.delete,
                    onClick = {
                        event(
                            MyGoalsUiEvent.SheetEvent(
                                SheetEvents.DeleteGoal(
                                    id = myGoalsItem.id,
                                    element = myGoalsItem.element,
                                    value = myGoalsItem.value
                                )
                            )
                        )
                    },
                )
            }

            VStack(
                8.dp,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = element,
                    style = textStyle.copy(
                        fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        lineHeight = 1.sp
                    ),
                )
                Text(
                    text = "Created On $creationOn",
                    style = textStyle.copy(
                        fontSize = 12.sp, fontWeight = FontWeight.Medium, color = grey94,
                        lineHeight = 1.sp
                    ),
                )
            }
        }
    }
}



