package com.medrevpatient.mobile.app.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white

@Composable
fun AppInputTextField(
    fieldValue: String = "",
    fieldErrorValue: String? = null,
    fieldIconId: Int,
    fieldHint: String = "",
    isEnable: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    keyboardType: KeyboardType = KeyboardType.Text,
    onInputTextChange: (String) -> Unit,
    isTrailingIconVisible: Boolean = false,
    trailingIconId: Int? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    modifier: Modifier = Modifier,
    horizontalPadding: Int = 18
) {
    val isError = !fieldErrorValue.isNullOrBlank()
    Column {
        Surface(
            modifier = modifier
                .padding(horizontal = horizontalPadding.dp)
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.colorScheme.error else MineShaft,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentColor = White,
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White),
                verticalAlignment = Alignment.Top,
            ) {

                Image(
                    painter = painterResource(fieldIconId), contentDescription = "",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 15.dp)
                )

                OutlinedTextField(
                    modifier = Modifier.weight(1f).offset(x = (-4).dp).background(Transparent),
                    value = fieldValue,
                    onValueChange = { onInputTextChange(it) },
                    enabled = isEnable,
                    placeholder = {
                        Text(
                            text = fieldHint,
                            fontFamily = outFit,
                            fontWeight = FontWeight.W300,
                            color = MineShaft,
                            fontSize = 14.sp
                        )
                    },
                    textStyle = TextStyle(
                        color = MineShaft,
                        fontFamily = outFit,
                        fontWeight = FontWeight.W300,
                        fontSize = 14.sp
                    ),
                    isError = !fieldErrorValue.isNullOrBlank(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = imeAction,
                        keyboardType = keyboardType
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Transparent,
                        unfocusedBorderColor = Transparent,
                        errorBorderColor = Transparent,
                        disabledBorderColor = Transparent,
                        disabledContainerColor = Transparent,
                        errorContainerColor = Transparent,
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                    ),
                    visualTransformation = visualTransformation
                )

                if (isTrailingIconVisible && trailingIconId != null) {
                    IconButton(onClick = { onTogglePasswordVisibility() }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Image(
                            painter = painterResource(trailingIconId), contentDescription = "",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 15.dp)
                        )
                    }
                }
            }
        }
        if (!fieldErrorValue.isNullOrBlank()) {
            Text(
                text = fieldErrorValue ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontFamily = outFit,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = horizontalPadding.dp.plus(12.dp))
            )
        }
    }

}

@Preview
@Composable
private fun Preview() {
    Surface(modifier = Modifier.background(color = white)) {
        AppInputTextField(
            fieldIconId = R.drawable.ic_email,
            fieldHint = stringResource(R.string.email),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            onInputTextChange = {}
        )
    }
}