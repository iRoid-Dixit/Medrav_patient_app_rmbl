package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.InfoComponent
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.component.getAddLogInfo
import com.medrevpatient.mobile.app.ux.main.component.getElementByType
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.UnitSeparatorTextField

@Preview(showBackground = true)
@Composable
private fun AddElementLogSheetContentPreview(
    modifier: Modifier = Modifier,
) {
    AddElementLogSheetContent(modifier = modifier,
        uiState = SheetUiState.AddElementLog(), event = {}, onNegative = {})
}

@Composable
fun AddElementLogSheetContent(
    event: SheetEvent,
    uiState: SheetUiState.AddElementLog,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier
) {

    val tabs = uiState.tabs.map { it.getElementByType() }
//    val tabs = uiState.tabs.map { it.getElementByType() }
    var state by remember { mutableIntStateOf(tabs.indexOf(uiState.element)) } //optional : we can move this in uiState

    uiState.apply {

        VStack(
            spaceBy = 8.dp,
            modifier = modifier.fillMaxWidth()
        ) {
            if (tabs.size > 4) {
                ScrollableTabRow(
                    containerColor = white,
                    edgePadding = 0.dp,
                    selectedTabIndex = state,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            color = Color.Black,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state])
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            color = grey94,
                            thickness = 3.dp
                        )
                    },
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            modifier = Modifier.fillMaxWidth(),
                            selected = state == index,
                            selectedContentColor = black25,
                            unselectedContentColor = grey94,
                            onClick = {
                                state = index
                                event(
                                    SheetEvents.AddElementLog(
                                        element = tabs[state],
                                        value = value,
                                        minute = minute
                                    )
                                )
                            },
                            text = {
                                Text(
                                    text = title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                    color = if (state == index) black25 else grey94
                                )
                            },
                        )
                    }
                }
            } else {
                TabRow(
                    containerColor = white,
                    selectedTabIndex = state,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            color = Color.Black,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state])
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            color = grey94,
                            thickness = 3.dp
                        )
                    },
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            modifier = Modifier.fillMaxWidth(),
                            selected = state == index,
                            selectedContentColor = black25,
                            unselectedContentColor = grey94,
                            onClick = {
                                state = index
                                event(
                                    SheetEvents.AddElementLog(
                                        element = tabs[state],
                                        value = value,
                                        minute = minute
                                    )
                                )
                            },
                            text = {
                                Text(
                                    text = title, maxLines = 2, overflow = TextOverflow.Ellipsis,
                                    color = if (state == index) black25 else grey94
                                )
                            },
                        )
                    }
                }
            }

            //Values TextField
            Spacer(Modifier.padding(20.dp))
            UnitSeparatorTextField(
                value = value,
                value2 = minute,
                onValueChange = {
                    event(
                        SheetEvents.AddElementLog(
                            element = tabs[state],
                            value = it,
                            minute = minute
                        )
                    )
                },
                onValueChange2 = {
                    event(
                        SheetEvents.AddElementLog(
                            element = tabs[state],
                            value = value,
                            minute = it
                        )
                    )
                },
                errorMsg = uiState.isError ?: "",
                unit = tabs[state].lowercase().getUnit(),
                modifier = Modifier
            )
            Spacer(Modifier.padding(16.dp))

            //Footer
            VStack(
                0.dp,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(138.dp)
                    .padding(horizontal = 12.dp)
            ) {

                InfoComponent(description = tabs[state].getAddLogInfo())

                HStack(16.dp) {
                    SkaiButton(
                        text = "Cancel",
                        borderStroke = BorderStroke(1.dp, black25),
                        color = White,
                        textStyle = SkaiButtonDefault.textStyle.copy(color = black25),
                        modifier = Modifier.weight(1f),
                        onClick = onNegative
                    )

                    SkaiButton(
                        isLoading = isLoading,
                        text = "Add Log",
                        borderStroke = BorderStroke(1.dp, black25),
                        modifier = Modifier.weight(1f)
                    ) {
                        event(SheetEvents.Positive(SheetType.ADD_ELEMENT_LOG))
                    }
                }
            }
        }
    }
}