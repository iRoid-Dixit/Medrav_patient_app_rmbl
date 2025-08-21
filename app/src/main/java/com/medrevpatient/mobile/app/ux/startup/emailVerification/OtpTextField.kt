package com.medrevpatient.mobile.app.ux.startup.emailVerification


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit


@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 4,
    isEnable: Boolean = true,
    isError: Boolean = false,
    onOtpTextChange: (String, Boolean) -> Unit
) {

    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        enabled = isEnable,
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword).copy(imeAction = ImeAction.Done),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText,
                        isError = isError
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
    isError: Boolean,
) {
//    val isFocused = text.length == index
    val normalBorderColor = MineShaft
    val errorBorderColor = MineShaft
    val borderColor = if (isError) errorBorderColor else normalBorderColor

    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> text[index].toString()
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .border(
                1.dp,
                borderColor,
                RoundedCornerShape(14.dp)
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            color = MineShaft,
            fontFamily = outFit,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
fun CharViewPreview() {
    CharView(index = 0, text = "1",isError = false)
}

