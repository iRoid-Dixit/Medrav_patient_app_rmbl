package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.main.component.getIcon
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvent
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetEvents
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetType
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.SheetUiState

@Preview(showBackground = true)
@Composable
private fun DeleteGoalSheetContentPreview() {
    DeleteGoalSheetContent(
        deleteGoal = SheetUiState.DeleteGoal(),
        modifier = Modifier,
        event = {},
        onNegative = {}
    )
}

@Composable
fun DeleteGoalSheetContent(
    event: SheetEvent,
    deleteGoal: SheetUiState.DeleteGoal,
    onNegative: () -> Unit,
    modifier: Modifier = Modifier
) {

    deleteGoal.apply {

        val textStyle = MaterialTheme.typography.headlineSmall.copy(
            color = black25,
            fontSize = 26.sp,
            fontWeight = FontWeight.W800
        )

        VStack(8.dp, modifier = modifier) {

            Header(
                value,
                element,
                " Oct 18, 2024",
            )

            Spacer(
                Modifier
                    .padding(16.dp)
            )

            Text(
                text = "Are you sure, you want to delete this Goal?",
                style = textStyle.copy(textAlign = TextAlign.Center),
            )

            Text(
                text = "You will not be able to see this Goal’s stats after deletion.",
                style = textStyle.copy(
                    fontWeight = W300,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(
                Modifier
                    .padding(16.dp)
            )

            HStack(8.dp) {

                SkaiButton(
                    text = "No",
                    borderStroke = BorderStroke(1.dp, black25),
                    color = Color.White,
                    textStyle = SkaiButtonDefault.textStyle.copy(color = black25),
                    modifier = Modifier.weight(1f),
                    onClick = onNegative
                )

                SkaiButton("Yes", modifier = Modifier.weight(1f)) {
                    event(SheetEvents.Positive(SheetType.DELETE_GOAL))
                }
            }

        }
    }
}


@Composable
private fun Header(
    value: String,
    element: String,
    createdOn: String,
    modifier: Modifier = Modifier
) {

    val textStyle = MaterialTheme.typography.headlineSmall.copy(
        color = black25,
        fontSize = 26.sp,
        fontWeight = FontWeight.W800
    )

    val gradient = Brush.verticalGradient(
        listOf(
            aliceBlue.copy(alpha = 0.7f),
            aliceBlue
        )
    )

    Box(
        modifier = modifier
            .height(108.dp)
            .fillMaxWidth(0.7f)
            .clip(RoundedCornerShape(25))
            .background(
                brush = gradient,
            ),
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
                    .offset(x = 35.dp, y = (12).dp)
                    .clip(RoundedCornerShape(bottomEnd = 25.dp))
                    .rotate(35f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradient),
            contentAlignment = Alignment.Center
        ) {

            VStack(spaceBy = 0.dp, modifier = Modifier.padding(18.dp)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = textStyle.toSpanStyle()) {
                            append(value)
                        }
                        withStyle(style = textStyle.copy(fontSize = 18.sp).toSpanStyle()) {
                            append(" ${element.getUnit()}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )


                Text(
                    text = element,
                    style = textStyle.copy(fontSize = 16.sp, lineHeight = 16.sp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Created on $createdOn",
                    style = textStyle.copy(fontWeight = W300, fontSize = 12.sp, lineHeight = 12.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


    }
}
