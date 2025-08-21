package com.medrevpatient.mobile.app.ux.main.myprogress.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white

@Preview
@Composable
private fun IconTextSwappablePreview() {
    IconTextSwappableComponent(
        text = "Calories",
        icon = drawable.calorie,
        shouldSwapIcon = true,
        isSelected = true,
        modifier = Modifier
            .padding(18.dp)
            .fillMaxWidth()
    ) {}
}


@Composable
fun IconTextSwappableComponent(
    text: String,
    @DrawableRes icon: Int?,
    isSelected: Boolean,
    isEnable: Boolean = false,
    shouldSwapIcon: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier = modifier.heightIn(24.dp),
        onClick = { if (isEnable) onClick() },
        color = white,
        contentColor = if (isEnable) grey94 else contentColorFor(white),
        shape = RoundedCornerShape(25),
    ) {
        HStack(
            8.dp,
            modifier = Modifier.then(
                Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors =
                            if (isSelected)
                                listOf(aliceBlue.copy(alpha = 0.7f), aliceBlue, aliceBlue)
                            else
                                listOf(black25.copy(alpha = 0.05f), black25.copy(alpha = 0.05f))
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            )
        ) {
            if (!shouldSwapIcon && icon != null)
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    contentDescription = null,
                    tint = if (isEnable) black25 else grey94
                )

            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )

            if (shouldSwapIcon && icon != null)
                Icon(
                    imageVector = ImageVector.vectorResource(icon),
                    contentDescription = null,
                    tint = if (isEnable) black25 else grey94
                )
        }
    }
}