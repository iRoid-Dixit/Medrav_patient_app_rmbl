package com.medrevpatient.mobile.app.ux.main.programandworkout.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault

@Preview
@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    description: String = "Desc",
    positiveText: String = "Ok",
    negativeText: String = "Cancel",
    onDismiss: () -> Unit = {},
    positive: () -> Unit = {},
    negative: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        VStack(
            spaceBy = 26.dp,
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12))
                .padding(horizontal = 20.dp, vertical = 35.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(drawable.filled_time),
                contentDescription = null,
                modifier = Modifier.size(70.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
            )

            HStack(18.dp) {
                SkaiButton(
                    text = negativeText,
                    borderStroke = BorderStroke(1.dp, black25),
                    color = white,
                    onClick = negative,
                    textStyle = SkaiButtonDefault.textStyle.copy(color = black25, fontSize = 12.sp),
                    modifier = Modifier.weight(1f)
                )

                SkaiButton(
                    text = positiveText,
                    onClick = positive,
                    textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 12.sp),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}