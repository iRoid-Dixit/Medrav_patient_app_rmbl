package com.medrevpatient.mobile.app.ux.main.myprogress.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.AddElementLogSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.AddLogToSpecificSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.CreateGoalSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.DeleteGoalSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.EditGoalSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.OptionMenuSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.SuccessSheetContent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetLauncher(
    event: SheetEvent,
    uiState: SheetUiState,
    sheetType: SheetType,
    shouldShowSheet: Boolean,
    modifier: Modifier = Modifier,
    onSheetDismiss: () -> Unit = {},
    onSuccess: (SheetType) -> Unit = {},
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { (!shouldShowSheet || (it != SheetValue.Hidden)) })

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val sheetModifier = Modifier
        .fillMaxWidth()
        .padding(start = 18.dp, end = 18.dp, bottom = 36.dp)

    fun CoroutineScope.hideSheet() {
        this.launch {
            if (sheetState.isVisible) {
                sheetState.hide()
            }
        }.invokeOnCompletion {
            Timber.d("Dismiss")
            onSheetDismiss()
            event(SheetEvents.Negative)
        }
    }

    LaunchedEffect(shouldShowSheet) {
        if (shouldShowSheet && !sheetState.isVisible) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }


    LaunchedEffect(uiState.shouldShowSuccessSheet) {
        val targetPage = if (uiState.shouldShowSuccessSheet) 1 else 0
        if (pagerState.currentPage != targetPage) {
            scope.launch {
                pagerState.animateScrollToPage(targetPage)
            }
        }
    }

    if (shouldShowSheet)
        ModalBottomSheet(
            sheetState = sheetState,
            containerColor = white,
            onDismissRequest = {
                scope.hideSheet()
            },
            modifier = modifier
        ) {

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.animateContentSize()
            ) { pageNo ->

                when (sheetType) {

                    SheetType.CREATE_GOAL -> {
                        when (pageNo) {
                            0 -> CreateGoalSheetContent(
                                modifier = sheetModifier,
                                uiState = uiState.createGoal,
                                event = event,
                                onNegative = { scope.hideSheet() }
                            )

                            1 -> SuccessSheetContent(
                                modifier = sheetModifier,
                                icon = drawable.goal,
                                title = "Goal created successfully.",
                                description = "You can see your created Goal in My Goals screen.",
                                btnText = "See My Goals",
                            ) {
                                scope.hideSheet()
                                onSuccess(SheetType.CREATE_GOAL)
                                event(SheetEvents.Success(SheetType.CREATE_GOAL))
                            }
                        }
                    }

                    SheetType.EDIT_GOAL -> {
                        when (pageNo) {
                            0 -> EditGoalSheetContent(
                                uiState = uiState.editGoal,
                                modifier = sheetModifier,
                                event = event,
                                onNegative = { scope.hideSheet() }
                            )

                            1 -> SuccessSheetContent(
                                modifier = sheetModifier,
                                icon = drawable.goal_updated,
                                title = "Goal updated successfully.",
                                description = "You can see your updated Goal in My Goals screen.",
                                btnText = "See My Goals",
                                onClick = {
                                    scope.hideSheet()
                                    onSuccess(SheetType.CREATE_GOAL)
                                    event(SheetEvents.Success(SheetType.CREATE_GOAL))
                                }
                            )
                        }
                    }

                    SheetType.DELETE_GOAL -> {
                        when (pageNo) {
                            0 -> DeleteGoalSheetContent(
                                deleteGoal = uiState.deleteGoal,
                                modifier = sheetModifier,
                                event = event,
                                onNegative = { scope.hideSheet() },
                            )

                            1 -> SuccessSheetContent(
                                modifier = sheetModifier,
                                icon = drawable.delete_goal,
                                title = "The Goal has been deleted.",
                                description = "You don't have access to this Goal anymore.",
                                btnText = "See My Goals",
                                onClick = {
                                    scope.hideSheet()
                                    onSuccess(SheetType.CREATE_GOAL)
                                    event(SheetEvents.Success(SheetType.CREATE_GOAL))
                                }
                            )
                        }
                    }

                    SheetType.OPTION_MENU -> {
                        OptionMenuSheetContent(
                            event = event,
                            modifier = sheetModifier,
                            edit = {
                                event(SheetEvents.OptionMenu(SheetType.EDIT_GOAL))
                            },
                            delete = {
                                event(SheetEvents.OptionMenu(SheetType.DELETE_GOAL))
                            }
                        )
                    }

                    SheetType.ADD_LOG_TO_SPECIFIC_GOAL -> {
                        when (pageNo) {
                            0 -> AddLogToSpecificSheetContent(
                                uiState = uiState.addLogToSpecific,
                                modifier = sheetModifier,
                                event = event,
                                onNegative = { scope.hideSheet() },
                            )

                            1 -> SuccessSheetContent(
                                modifier = sheetModifier,
                                icon = drawable.log_added,
                                title = "Log added successfully.",
                                description = "The Log effect will be reflected in My Progress screen.",
                                btnText = "See Progress",
                                onClick = {
                                    scope.hideSheet()
                                    onSuccess(SheetType.CREATE_GOAL)
                                    event(SheetEvents.Success(SheetType.CREATE_GOAL))
                                }
                            )
                        }
                    }

                    SheetType.ADD_ELEMENT_LOG -> {
                        when (pageNo) {
                            0 -> AddElementLogSheetContent(
                                uiState = uiState.addElementLog,
                                event = event,
                                modifier = sheetModifier,
                                onNegative = { scope.hideSheet() }
                            )

                            1 -> SuccessSheetContent(
                                modifier = sheetModifier,
                                icon = drawable.log_added,
                                title = "Log added successfully.",
                                description = "The Log effect will be reflected in My Progress screen.",
                                btnText = "See Progress",
                                onClick = {
                                    scope.hideSheet()
                                    onSuccess(SheetType.CREATE_GOAL)
                                    event(SheetEvents.Success(SheetType.CREATE_GOAL))
                                }
                            )
                        }
                    }
                }
            }
        }
}


