package com.medrevpatient.mobile.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.medrevpatient.mobile.app.ui.theme.black25
import com.spr.jetpack_loading.components.indicators.lineScaleIndicator.LineScaleIndicator
import com.spr.jetpack_loading.enums.PunchType

@Preview(showBackground = true)
@Composable
fun DialogLoader(
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false,
    onDismissRequest: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            modifier = modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            LineScaleIndicator(
                color = Color.Black,
                rectCount = 5,
                lineHeight = 80,
                distanceOnXAxis = 26f,
                punchType = PunchType.ACCORDION_PUNCH,
            )
        }
    }
}

@Composable
fun SimpleLoader(
    modifier: Modifier = Modifier,
) {

    FullSizeCenterBox(modifier = modifier) {
        LineScaleIndicator(
            color = Color.Black,
            rectCount = 5,
            lineHeight = 80,
            distanceOnXAxis = 26f,
            punchType = PunchType.ACCORDION_PUNCH,
        )
    }
}


@Composable
fun FullSizeCircularLoader(modifier: Modifier = Modifier) {
    FullSizeCenterBox(modifier = modifier.fillMaxWidth()) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = black25,
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Round,
            modifier = Modifier.size(28.dp)
        )
    }
}


@Composable
fun LoadingError(
    message: String,
    modifier: Modifier = Modifier
) {
    FullSizeCenterBox(modifier = modifier) {
        Text(message, style = MaterialTheme.typography.labelMedium)
    }
}