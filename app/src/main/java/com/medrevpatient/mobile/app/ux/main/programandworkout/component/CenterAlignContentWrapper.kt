package com.medrevpatient.mobile.app.ux.main.programandworkout.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.utils.ext.makeUpperCase

@Composable
fun CenterAlignContentWrapper(
    title: String,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    modifier: Modifier = Modifier,
    upperCase: Boolean = true,
    spaceBy: Dp = 18.dp,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    VStack(
        spaceBy = spaceBy,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title.makeUpperCase(upperCase),
            textAlign = TextAlign.Center,
            style = style,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            letterSpacing = 4.sp,
            overflow = TextOverflow.Ellipsis
        )
        content?.invoke(this)
    }
}