package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.black25


@Preview(showBackground = true)
@Composable
private fun OutlineTextFieldWithTrailingPreview() {
    OutlineTextFieldWithTrailing(
        value = "",
        onValueChange = {},
        modifier = Modifier.padding(8.dp),
        trailingIcon = {
            Text("Hellossss") //We can use text composable also
        }
    )
}

@Composable
fun OutlineTextFieldWithTrailing(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onDone: () -> Unit = {},
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1,
    shape: RoundedCornerShape = RoundedCornerShape(25)
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        supportingText = supportingText,
        trailingIcon = trailingIcon,
        modifier = modifier,
        shape = shape,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            },
            onDone = {
                onDone()
                focusManager.clearFocus()
            }
        ),
        textStyle = textStyle.copy(lineHeight = 18.sp),
        colors = TextFieldDefaults.colors().copy(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = black25,
            unfocusedIndicatorColor = black25,
            cursorColor = Color.Black,
            textSelectionColors = TextSelectionColors(
                handleColor = black25,
                backgroundColor = black25.copy(alpha = .1f)
            )
        )
    )
}