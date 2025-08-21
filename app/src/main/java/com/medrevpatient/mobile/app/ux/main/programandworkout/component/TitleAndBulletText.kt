package com.medrevpatient.mobile.app.ux.main.programandworkout.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.black94
import com.medrevpatient.mobile.app.ui.theme.outFit
import kotlin.text.Typography.bullet

@Composable
fun TitleAndBulletText(
    title: String,
    items: List<String>,
    horizontal: Alignment.Horizontal,
    titleColor: Color = black25,
    spaceBy: Dp = 0.dp,
    modifier: Modifier = Modifier
) {

    val bulletTextStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = outFit,
        color = black94
    )

    VStack(
        spaceBy = 0.dp,
        horizontalAlignment = horizontal,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = titleColor,
            fontSize = 16.sp,
            overflow = TextOverflow.Ellipsis

        )

        Spacer(Modifier.padding(spaceBy))

        HStack(8.dp) {
            items.forEach { item ->
                Text(
                    text = "$bullet $item",
                    style = bulletTextStyle
                )
            }
        }
    }

}