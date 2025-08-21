package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.neonNazar


@Preview(showBackground = true)
@Composable
private fun TextFieldFilledWithTrailingPreview() {
    TextFieldFilledWithTrailing(
        value = "Hello World",
        onValueChange = {},
        enable = false,
        modifier = Modifier.padding(18.dp)
    )
}


@Composable
fun TextFieldFilledWithTrailing(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    shape: RoundedCornerShape = RoundedCornerShape(25),
) {

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        supportingText = supportingText,
        trailingIcon = trailingIcon,
        modifier = modifier,
        shape = shape,
        enabled = enable,
        textStyle = textStyle.copy(lineHeight = 18.sp),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = black25.copy(alpha = .1f),
            unfocusedContainerColor = black25.copy(alpha = .1f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = black25,
            cursorColor = neonNazar,
            disabledIndicatorColor = Color.Transparent,
            disabledContainerColor = black25.copy(alpha = .1f),
            textSelectionColors = TextSelectionColors(handleColor = black25, neonNazar)
        )
    )
}