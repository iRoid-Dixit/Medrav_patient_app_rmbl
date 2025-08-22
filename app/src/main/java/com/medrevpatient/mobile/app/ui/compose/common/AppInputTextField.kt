package com.medrevpatient.mobile.app.ui.compose.common
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Gray40

import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.Gray5
import com.medrevpatient.mobile.app.ui.theme.Gray50
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import androidx.compose.ui.graphics.Color

@Composable
fun AppInputTextField(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    onTrailingIconClick: () -> Unit = {},
    isTrailingIconClickable: Boolean = false,
    isEnable: Boolean = true,
    isReadOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    ) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = Gray5, shape = RoundedCornerShape(10))
                .fillMaxWidth()
                .clip(RoundedCornerShape(10))
        ) {
            if (isLeadingIconVisible && leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = "User",
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                readOnly = isReadOnly,
                interactionSource = interactionSource,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF7A42F4),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Gray40,
                    disabledIndicatorColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                ),
                maxLines = 1,
                enabled = isEnable,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                placeholder = {
                    Text(
                        text = header,
                        fontFamily = nunito_sans_400,
                        fontSize = 14.sp,
                        color = Gray40,
                    )
                }
            )

            if (isTrailingIconVisible && trailingIcon != null) {
                IconButton(onClick = {
                    if(isTrailingIconClickable) onTrailingIconClick() else onTogglePasswordVisibility()
                }) {
                    Image(
                        painter = painterResource(trailingIcon),
                        colorFilter = if(isTrailingIconClickable) ColorFilter.tint(Color(0xFF7A42F4)) else ColorFilter.tint(Gray40),
                        contentDescription = "User",
                    )
                }
            }
        }
        if (errorMessage?.isNotEmpty()==true){
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
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