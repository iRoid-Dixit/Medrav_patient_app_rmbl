package com.medrevpatient.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray5
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInputTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    isVerifyButtonVisible: Boolean = false,
    verifyButtonText: String = "Verify",
    verifyButtonBackgroundColor: Color = AppThemeColor.copy(alpha = 0.1f),
    textColors: Color = AppThemeColor,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {},
    isTrailingIconClickable: Boolean = false,
    isEnable: Boolean = true,
    isReadOnly: Boolean = false,
    verifyText: String="Verify",
    onVerifyButtonClick: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    isTitleVisible: Boolean = false,
    title: String = "",
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column {
        if (isTitleVisible) {
            Text(
                text = title, fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .background(color = Gray5, shape = RoundedCornerShape(15))
                .clip(RoundedCornerShape(15))
        ) {
            if (isLeadingIconVisible && leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = "User",
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            BasicTextField(
                value = value,
                onValueChange = onValueChange,

                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = nunito_sans_600,
                    fontSize = 14.sp,
                    color = Black,
                    lineHeight = 20.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(15.dp)),
                // .fillMaxWidth(),
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                readOnly=isReadOnly,
                visualTransformation = visualTransformation,
                enabled = isEnable,
                singleLine = true
            ) {
                TextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = it,
                    singleLine = true,

                    enabled = isEnable,
                    isError = !errorMessage.isNullOrBlank(),
                    visualTransformation = visualTransformation,
                    placeholder = {
                        if (value.isEmpty()) {
                            Text(
                                text = header,
                                fontFamily = nunito_sans_400,
                                fontSize = 14.sp,
                                color = Gray40,
                            )
                        }
                    },
                    interactionSource = interactionSource,
                    // change the padding
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                        top = 12.dp, bottom = 12.dp, end = 15.dp, start = 15.dp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Color(0xFF7A42F4),
                        errorIndicatorColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Gray40,
                        disabledIndicatorColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                    ),

                    )
            }
            if (isVerifyButtonVisible) {
                VerifyButton(
                    onClick = onVerifyButtonClick,
                    textColors=textColors,
                    verifyButtonBackgroundColor=verifyButtonBackgroundColor,
                    verifyButtonText = verifyButtonText,
                )
                Spacer(modifier = Modifier.width(15.dp))
            }
            if (isTrailingIconVisible && trailingIcon != null) {
                IconButton(onClick = {
                    if (isTrailingIconClickable) onTrailingIconClick() else onTogglePasswordVisibility()
                }) {
                    Image(
                        painter = painterResource(trailingIcon),
                        colorFilter = if (isTrailingIconClickable) ColorFilter.tint(Color(0xFF7A42F4)) else ColorFilter.tint(Gray40),
                        contentDescription = "User",
                    )
                }
            }
        }
        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = nunito_sans_600,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 15.dp, top = 10.dp)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var email by remember { mutableStateOf("") }
    Surface(modifier = Modifier.background(color = White)) {
        AppInputTextField(
            value = email,
            onValueChange = { email = it },
            isTrailingIconVisible = true,
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            header = "Email Address",
            leadingIcon = R.drawable.ic_app_logo,
            trailingIcon = R.drawable.ic_app_logo,
        )
    }
}