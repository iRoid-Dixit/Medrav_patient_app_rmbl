package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ux.main.component.InfoComponent
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.component.getAddLogInfo
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.UploadPhotoComponent

@Preview(showBackground = true)
@Composable
private fun AddLogToSpecificScreenPreview() {
    AddLogToSpecificSheetContent(
        uiState = SheetUiState.AddLogToSpecific(),
        onNegative = {},
        event = {}
    )
}

@Composable
fun AddLogToSpecificSheetContent(
    uiState: SheetUiState.AddLogToSpecific,
    onNegative: () -> Unit,
    event: SheetEvent,
    modifier: Modifier = Modifier
) {

    uiState.apply {

        val textStyle =
            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)

        VStack(
            12.dp,
            modifier = modifier
        ) {

            //Header
            Text(
                text = "Add $element Log",
                style = textStyle
            )

            DualHeaderTextFieldComponent(
                header = textFieldTitle ?: "$element Value",
                description = textFieldDescription,
                placeHolder = textFieldPlaceHolder,
                value = value,
                value2 = value2,
                element = element,
                onValueChange = {
                    event(
                        SheetEvents.AddLogToSpecific(
                            element = element,
                            value = it,
                            value2 = uiState.value2,
                            images = uiState.images
                        )
                    )
                },
                onValueChange2 = {
                    event(
                        SheetEvents.AddLogToSpecific(
                            element = element,
                            value = uiState.value,
                            value2 = it,
                            images = uiState.images
                        )
                    )
                }
            )

            if (element == "Weight")
                UploadPhotoComponent(
                    getImages = uiState.images,
                    navigateToCamera = {
                        event(SheetEvents.NavigateTo(com.medrevpatient.mobile.app.navigation.RouteMaker.WeightCamera.createRoute()))
                    },
                    deleteImage = { file ->
                        event(
                            SheetEvents.AddLogToSpecific(
                                element = element,
                                value = uiState.value,
                                images = uiState.images - file
                            )
                        )
                    },
                    pickerFiles = { fList ->
                        event(
                            SheetEvents.AddLogToSpecific(
                                element = element,
                                value = uiState.value,
                                images = fList
                            )
                        )
                    }
                )


            Spacer(Modifier.padding(4.dp))

            //Footer
            VStack(
                spaceBy = 0.dp,
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.wrapContentHeight()

            ) {

                InfoComponent(description = element.getAddLogInfo())

                Spacer(Modifier.padding(12.dp))

                HStack(16.dp, modifier = Modifier.height(56.dp)) {

                    SkaiButton(
                        text = "Cancel",
                        borderStroke = BorderStroke(1.dp, black25),
                        color = White,
                        textStyle = SkaiButtonDefault.textStyle.copy(color = black25),
                        modifier = Modifier.weight(1f),
                        onClick = onNegative
                    )

                    SkaiButton(
                        text = "Add Log",
                        isLoading = isLoading,
                        borderStroke = BorderStroke(1.dp, black25),
                        modifier = Modifier.weight(1f)
                    ) { event(SheetEvents.Positive(sheetType = SheetType.ADD_LOG_TO_SPECIFIC_GOAL)) }
                }
            }
        }
    }
}