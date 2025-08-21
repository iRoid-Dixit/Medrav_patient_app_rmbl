package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.data.source.TempDataSource.tabList
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ux.main.component.InfoComponent
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.component.getCreateGoalLogInfo
import com.medrevpatient.mobile.app.ux.main.component.getIcon
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.myprogress.component.IconTextSwappableComponent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.UploadPhotoComponent
import timber.log.Timber

@Preview(showBackground = true)
@Composable
private fun CreateGoalSheetContentPreview() {

    CreateGoalSheetContent(
        event = {},
        uiState = SheetUiState.CreateGoal(),
        modifier = Modifier,
        onNegative = {}
    )
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGoalSheetContent(
    event: SheetEvent,
    uiState: SheetUiState.CreateGoal,
    modifier: Modifier = Modifier,
    onNegative: () -> Unit,
) {

    var selectedIndex by remember(uiState.element) { mutableIntStateOf(tabList.indexOf(uiState.element)) }

    val textStyle =
        MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)

    VStack(
        spaceBy = 12.dp,
        modifier = modifier
            .heightIn(max = 400.dp)
            .verticalScroll(rememberScrollState())
    ) {

        //Header
        Text(
            text = "Create New Goal",
            style = textStyle
        )

        Spacer(Modifier.padding(1.dp))


        Text(
            text = "Choose Goal Type",
            style = textStyle.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                lineHeight = 1.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )

        //Body
        val row = 3

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = row,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            val numberOfItemRequireToFillWidth = row - (tabList.size % row)
            val totalItems = tabList.size + numberOfItemRequireToFillWidth

            repeat(totalItems) { index ->
                if (index < tabList.size) {
                    val item = tabList[index]
                    val shouldDisable = !uiState.elementToDisable.contains(item)

                    IconTextSwappableComponent(
                        text = item,
                        icon = item.getIcon(),
                        isEnable = shouldDisable,
                        isSelected = (selectedIndex == index) && shouldDisable,
                        onClick = {
                            selectedIndex = index
                            event(
                                SheetEvents.CreateGoal(
                                    element = item,
                                    value = uiState.value,
                                    images = emptyList(), //Used uiState because, we want to retain other values when element is changed
                                    elementToDisable = uiState.elementToDisable
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }

        val selectedElement = tabList[selectedIndex]

        VStack(
            spaceBy = 8.dp,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth()
        ) {

            DualHeaderTextFieldComponent(
                value = uiState.value,
                value2 = uiState.value2,
                onValueChange = {
                    event(
                        SheetEvents.CreateGoal(
                            element = selectedElement,
                            value = it,
                            value2 = uiState.value2,
                            images = uiState.images
                        )
                    )
                },
                onValueChange2 = {
                    event(
                        SheetEvents.CreateGoal(
                            element = selectedElement,
                            value = uiState.value,
                            value2 = it,
                            images = uiState.images
                        )
                    )
                },
                header = if (selectedElement.getUnit()
                        .lowercase() == "hours"
                ) "Weekly $selectedElement Goal" else "$selectedElement Goal",
                description = "Value that you wish to set for $selectedElement Goal",
                element = selectedElement,
            )

            if (selectedElement == "Weight")
                UploadPhotoComponent(
                    getImages = uiState.images,
                    deleteImage = { imgFile ->
                        event(
                            SheetEvents.CreateGoal(
                                element = selectedElement,
                                value = uiState.value,
                                images = uiState.images - imgFile
                            )
                        )
                    },
                    navigateToCamera = {
                        Timber.d("Navigate to camera")
                        event(SheetEvents.NavigateTo(RouteMaker.WeightCamera.createRoute()))
                    },
                    pickerFiles = { fList ->
                        event(
                            SheetEvents.CreateGoal(
                                element = selectedElement,
                                value = uiState.value,
                                images = fList
                            )
                        )
                    }
                )

            Spacer(Modifier.padding(12.dp))

            //Footer
            VStack(
                0.dp,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(128.dp)
            ) {

                InfoComponent(description = selectedElement.getCreateGoalLogInfo())

                HStack(16.dp) {

                    SkaiButton(
                        text = "Cancel",
                        borderStroke = BorderStroke(1.dp, black25),
                        color = White,
                        textStyle = SkaiButtonDefault.textStyle.copy(color = black25),
                        modifier = Modifier.weight(1f)
                    ) {
                        onNegative()
                    }

                    SkaiButton(
                        text = "Add Goal",
                        enable = !uiState.elementToDisable.contains(selectedElement),
                        modifier = Modifier.weight(1f)
                    ) {
                        event(SheetEvents.Positive(SheetType.CREATE_GOAL))
                    }
                }

                Spacer(Modifier.padding(8.dp))
            }
        }
    }
}