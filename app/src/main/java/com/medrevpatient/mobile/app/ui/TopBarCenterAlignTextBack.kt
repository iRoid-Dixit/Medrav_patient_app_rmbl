package com.medrevpatient.mobile.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.black25


@Preview(showBackground = true)
@Composable
private fun TopBarCenterAlignTextBackPreview() {
    TopBarCenterAlignTextBack(
        title = "All Programs",
        onBackPress = {},
    )

}

@Composable
fun TopBarCenterAlignTextBack(
    title: String,
    modifier: Modifier = Modifier,
    isTrailingIconVisible: Boolean = false,
    isBackIconVisible: Boolean = true,
    trailingIcon: Int = R.drawable.option_menu,
    leadingIcon: Int = R.drawable.back,
    textColor: Color = MineShaft,
    tint: Color = black25,
    onTrailIconPress: () -> Unit = {},
    onBackPress: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        if (isBackIconVisible) {
            IconButton(
                onClick = onBackPress,
                enabled = true,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(start = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        /*.background(color = tint.copy(alpha = 0.05f))*/,
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = "Back",
                        tint = tint
                    )
                }
            }
        } else {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .padding(start = 14.dp)
            ) {
            }
        }

        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = textColor,
            modifier = Modifier
                .weight(1f)
        )

        if (isTrailingIconVisible) {
            IconButton(
                onClick = {onTrailIconPress()},
                enabled = true,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .padding(end = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(color = tint.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = "",
                        tint = tint
                    )
                }
            }
        } else {
            IconButton(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .padding(end = 14.dp)
            ) {
            }
        }
    }
}