package com.griotlegacy.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun AppInputTextFieldMultipleLine(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    errorMessage: String? = null,
    isLeadingIconVisible: Boolean = false,
    @DrawableRes leadingIcon: Int? = null,
    height: Int = 220,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        isFocused -> White
        else -> Gray2F
    }

    Column(modifier = Modifier.clip(shape = RoundedCornerShape(4.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray2F)
                .clip(shape = RoundedCornerShape(4))
                .height(height.dp)
                .border(
                    width = 1.dp,
                    color = borderColor, // Dynamically set border color
                    shape = RoundedCornerShape(4)
                )

        ) {
            if (isLeadingIconVisible && leadingIcon != null) {
                Image(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 10.dp, top = 18.dp)
                )
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Gray2F), // Ensure consistent background
                shape = RoundedCornerShape(8),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Gray2F,
                    unfocusedContainerColor = Gray2F,
                    cursorColor = White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = false,
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                placeholder = {
                    Text(
                        text = header,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp,
                        color = White50
                    )
                },
                interactionSource = interactionSource // Link interaction source
            )
        }
        if (errorMessage?.isNotEmpty() == true) {
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
fun TextFiledNewPreviewNewMultiple(

) {
    Row {
        val email = remember {
            mutableStateOf("")
        }
        AppInputTextFieldMultipleLine(
            value = email.value,
            onValueChange = { email.value = it },
            isLeadingIconVisible = true,
            leadingIcon = R.drawable.ic_app_icon,
            header = "Type here..",
        )

    }
}

