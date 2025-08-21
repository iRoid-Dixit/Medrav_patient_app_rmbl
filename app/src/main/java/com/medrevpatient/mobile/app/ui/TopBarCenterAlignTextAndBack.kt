package com.medrevpatient.mobile.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.black25


@Preview(showBackground = true)
@Composable
private fun TopBarCenterAlignTextBackPreview() {
    TopBarCenterAlignTextAndBack(
        title = "All Programs",
        onBackPress = {}
    ){}

}

@Composable
fun TopBarCenterAlignTextAndBack(
    title: String,
    modifier: Modifier = Modifier,
    tint: Color = black25,
    trailingIcon: Int = drawable.option_menu,
    onBackPress: (() -> Unit)? = null,
    onTrailingPress: (() -> Unit)? = null
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {

        IconButton(
            onClick = { onBackPress?.invoke() },
            enabled = onBackPress != null,
            modifier = Modifier
                .fillMaxHeight()
                .alpha(if (onBackPress == null) 0f else 1f)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(color = tint.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = drawable.back),
                    contentDescription = "Back",
                    tint = tint
                )
            }
        }


        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
        )


        IconButton(
            onClick = { onTrailingPress?.invoke() },
            enabled = onTrailingPress != null,
            modifier = Modifier.alpha(if (onTrailingPress == null) 0f else 1f)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(trailingIcon),
                contentDescription = null, tint = tint,
            )
        }
    }
}