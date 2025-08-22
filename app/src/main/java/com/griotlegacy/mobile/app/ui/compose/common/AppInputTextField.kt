package com.griotlegacy.mobile.app.ui.compose.common
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
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.ui.theme.Gray2F
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.White50
import com.griotlegacy.mobile.app.ui.theme.WorkSans

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
    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        isFocused -> White
        else -> Gray2F // 🔘 Default state
    }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = Gray2F, shape = RoundedCornerShape(10))
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(10)
                )
                .clip(RoundedCornerShape(10))
        ) {
            if (isLeadingIconVisible && leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = "User",
                    modifier = Modifier.padding(start = 10.dp),
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
                shape = RoundedCornerShape(15),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Gray2F,
                    unfocusedIndicatorColor = Gray2F,
                    focusedContainerColor = Gray2F,
                    unfocusedContainerColor = Gray2F,
                    cursorColor = White,
                    focusedTextColor = White,
                    unfocusedTextColor =White,
                    disabledTextColor = White,
                    disabledIndicatorColor = Gray2F,
                    disabledContainerColor = Gray2F,
                ),
                maxLines = 1,
                enabled = isEnable,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                placeholder = {
                    Text(
                        text = header,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp,
                        color = White50,
                    )
                }
            )

            if (isTrailingIconVisible && trailingIcon != null) {
                IconButton(onClick = {
                    if(isTrailingIconClickable) onTrailingIconClick() else onTogglePasswordVisibility()
                }) {
                    Image(
                        painter = painterResource(trailingIcon),
                        colorFilter = if(isTrailingIconClickable) ColorFilter.tint(White) else ColorFilter.tint(White50),
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
    var email by remember { mutableStateOf("asd") }
    Surface(modifier = Modifier.background(color = White)) {
        AppInputTextField(
            value = email,
            onValueChange = { email = it },
            isTrailingIconVisible = true,
            isLeadingIconVisible = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            header = stringResource(id = R.string.email),
            leadingIcon = R.drawable.ic_app_icon,
        )
    }
}