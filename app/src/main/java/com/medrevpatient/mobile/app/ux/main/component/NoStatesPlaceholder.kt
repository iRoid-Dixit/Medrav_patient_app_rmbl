package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.white

@Preview
@Composable
fun NoStatesPlaceholder(
    modifier: Modifier = Modifier,
    description: String? = null,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally
) {
    VStack(
        spaceBy = 0.dp,
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(drawable.grey_progress),
            contentDescription = null,
            tint = grey94,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = description ?: "No Stats!",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.W200,
            fontSize = 8.sp,
            maxLines = 1,
            overflow = Ellipsis,
            color = white
        )
    }
}