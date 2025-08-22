package com.griotlegacy.mobile.app.ui.compose.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.ui.theme.appTextColor
import com.griotlegacy.mobile.app.ui.theme.WorkSans


@Composable
fun TextWithClickableSubText(
    description: String,
    destinationName: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 12.sp,
    isEnable: Boolean = true,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = description,
            fontFamily = WorkSans,
            fontWeight = FontWeight.Light,
            color = appTextColor,
            fontSize = textSize
        )
        Text(
            text = destinationName,
            fontFamily = WorkSans,
            fontWeight = if (isEnable) {
                FontWeight.Medium
            } else {
                FontWeight.Light
            },
            color = appTextColor,
            textDecoration = TextDecoration.Underline,
            fontSize = textSize,
            modifier = Modifier
                .clickable {
                    onClick()
                }
        )
    }
}

@Preview
@Composable
private fun AuthNavigationTextPreview() {
    TextWithClickableSubText(
        description = "Already have an account?",
        destinationName = stringResource(id = R.string.login),
        isEnable = true,
        onClick = {}
    )
}