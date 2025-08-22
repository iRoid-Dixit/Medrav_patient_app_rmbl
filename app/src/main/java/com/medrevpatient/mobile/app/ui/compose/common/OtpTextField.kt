package com.medrevpatient.mobile.app.ui.compose.common
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans

@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onOtpTextChange: (String) -> Unit,


    ) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    // Check if there's any error in OTP input

    BasicTextField(
        modifier = modifier .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    keyboardController?.show()
                }
            },
        interactionSource = interactionSource,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text)
            }
        },
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide() // Hide keyboard when "Done" is pressed
            }
        ),
        keyboardOptions = keyboardOptions,
        decorationBox = {
            Column {
                Row(

                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otpText,
                            borderColor = if (otpText.length == index) White else Gray2F,
                        )
                        if (index < otpCount - 1) {
                            Spacer(modifier = Modifier.weight(1f)) // Adjust the space as needed
                        }
                    }
                }
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 8.dp) // Adjust padding as needed
                    )
                }
            }


        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String,
    borderColor: Color,
) {
    val char = text.getOrNull(index)?.toString() ?: ""

    Box(

        modifier = Modifier
            .widthIn(50.dp)
            .heightIn(50.dp)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .background(shape = RoundedCornerShape(8.dp), color = Gray2F),
        contentAlignment = Alignment.Center
    ) {

        if (char == "") {
            TextOtp(char = "0", color = White50)
        } else {
            TextOtp(char = char, color = White)
        }

    }
}

@Composable
fun TextOtp(char: String, color: Color) {
    Text(
        text = char,
        color = color,
        fontFamily = WorkSans,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp
    )
}

@Preview
@Composable
fun CharViewPreview() {
    OtpTextField(otpText = "32", onOtpTextChange = {  })
}
//covert my code
