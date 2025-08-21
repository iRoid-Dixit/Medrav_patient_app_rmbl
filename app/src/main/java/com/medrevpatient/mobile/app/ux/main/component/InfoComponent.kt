package com.medrevpatient.mobile.app.ux.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.theme.black25


@Preview
@Composable
private fun InfoComponentPreview() {
    InfoComponent(
        description = "This is a description",
    )
}

@Composable
fun InfoComponent(
    description: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
) {

    HStack(
        spaceBy = 8.dp,
        verticalAlignment = Alignment.Top,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(icon ?: drawable.info__outline_),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = W300,
            color = black25,
            lineHeight = 20.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

