package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.medrevpatient.mobile.app.ux.main.component.getEditGoalInfo
import com.medrevpatient.mobile.app.ux.main.component.getIcon
import com.medrevpatient.mobile.app.ux.main.myprogress.component.IconTextSwappableComponent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState

@Preview(showBackground = true)
@Composable
private fun EditGoalSheetContentPreview() {
    EditGoalSheetContent(
        uiState = SheetUiState.EditGoal(),
        event = {},
        onNegative = {}
    )
}

@Composable
fun EditGoalSheetContent(
    uiState: SheetUiState.EditGoal,
    event: SheetEvent,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier
) {

    uiState.apply {

        val textStyle =
            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)

        VStack(12.dp, modifier = modifier) {

            //Header
            Text(
                text = "Edit ${uiState.element} Goal",
                style = textStyle
            )


            Spacer(Modifier.padding(2.dp))

            Text(
                text = "Goal Type",
                style = textStyle.copy(
                    fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                    lineHeight = 1.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )

            IconTextSwappableComponent(
                text = element,
                icon = element.getIcon(),
                shouldSwapIcon = true,
                isSelected = true,
                isEnable = true,
                modifier = Modifier.fillMaxWidth()
            ) {

            }

            Spacer(Modifier.padding(1.dp))

            DualHeaderTextFieldComponent(
                value = value,
                onValueChange = {
                    event(
                        SheetEvents.EditGoal(
                            id = id,
                            element = element,
                            value = it,
                            value2 = value2
                        )
                    )
                },
                value2 = value2,
                onValueChange2 = {
                    event(
                        SheetEvents.EditGoal(
                            id = id,
                            element = element,
                            value = value,
                            value2 = it
                        )
                    )
                },
                header = "$element Goal",
                description = "Value that you wish to set for $element Goal",
                element = element,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.padding(4.dp))

            //Footer
            VStack(
                0.dp,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .height(138.dp)
            ) {

                InfoComponent(
                    description = element.getEditGoalInfo(),
                    modifier = Modifier.fillMaxWidth()
                )

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
                        text = "Update Goal",
                        enable = !isLoading,
                        modifier = Modifier.weight(1f),
                        onClick = { event(SheetEvents.Positive(SheetType.EDIT_GOAL)) }
                    )

                }
            }
        }
    }
}
