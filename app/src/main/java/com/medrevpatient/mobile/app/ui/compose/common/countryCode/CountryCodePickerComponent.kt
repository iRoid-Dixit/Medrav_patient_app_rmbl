package com.medrevpatient.mobile.app.ui.compose.common.countryCode
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Gray2F
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.White50
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@Composable
fun CountryCodePickerComponent(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    isLeadingIconVisible: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    setCountryCode: String? = null
) {
    val isError = !errorMessage.isNullOrBlank()
    val leadingIconColor = if (isError) MaterialTheme.colorScheme.error else AppThemeColor
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        isFocused -> White
        else -> Gray2F
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
                    modifier = Modifier.padding(start = 15.dp),
                    colorFilter = ColorFilter.tint(leadingIconColor)
                )
            }
            OutlinedTextField(
                value = value, onValueChange = onValueChange,
                singleLine = true,
                modifier = modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Gray2F,
                    unfocusedContainerColor = Gray2F,
                    cursorColor = White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    disabledIndicatorColor = Color.Transparent
                ),
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                leadingIcon = {
                    KomposeCountryCodePickerNew(
                        modifier = Modifier
                            .size(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Gray2F,
                            unfocusedContainerColor = Gray2F,
                            cursorColor = White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        showOnlyCountryCodePicker = true,
                        showCountryFlag = false,
                        defaultCountryCode = setCountryCode
                    )

                },
                visualTransformation = visualTransformation,
                placeholder = {
                    Text(
                        text = header,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W500,
                        fontSize = 16.sp,
                        color = White50,
                    )
                },
            )
        }
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontFamily = WorkSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

    }
    //  if(isError && !errorMessage.isNullOrBlank()) {

    // }
}

@Preview
@Composable
fun TextFiledNewPreviewNew2(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val email = remember {
            mutableStateOf("")
        }
        CountryCodePickerComponent(
            isLeadingIconVisible = true,
            value = email.value,
            onValueChange = { email.value = it },
            leadingIcon = R.drawable.ic_app_icon,
            header = stringResource(id = R.string.password),
        )

    }
}

/*
@Composable
fun OutlineTextFieldComponent2(
    value: String,
    onValueChange: (String) -> Unit = {},
    header: String,
    title: String,
    onClick: () -> Unit = {},
    leadingIconShow: () -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    isLeadingIconVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTogglePasswordVisibility: () -> Unit = {},
    modifier: Modifier = Modifier,
    textSize: TextUnit = 18.sp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    @DrawableRes leadingIcon: Int? = null,
    isHeaderVisible: Boolean = false
) {
    val isError = !errorMessage.isNullOrBlank()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isHeaderVisible) {
            Text(
                text = title,
                fontFamily = OpenSans,
                fontWeight = FontWeight.W400,
                color = Black23,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 18.dp)
            )
        }
        Surface(
            onClick = onClick,
            modifier = modifier.border(
                width = 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else PinkE9,
                shape = RoundedCornerShape(50.dp)
            ),
            contentColor = White,
            shape = RoundedCornerShape(50.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = White)
                    .fillMaxWidth()
            ) {
                if (isLeadingIconVisible && leadingIcon != null) {
                    Image(
                        painter = painterResource(leadingIcon),
                        contentDescription = "User",
                        modifier = Modifier.padding(start = 15.dp)

                    )
                }
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    modifier = modifier.fillMaxWidth(0.8f),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = White,
                        unfocusedIndicatorColor = White,
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        cursorColor = Black
                    ),
                   leadingIcon ={
                                leadingIconShow
                   } ,
                    visualTransformation = visualTransformation,
                    placeholder = {
                        Text(
                            text = header,
                            fontFamily = OpenSans,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = Black50,
                        )
                    }
                )
                Row {
                    Spacer(modifier = Modifier.padding(5.dp))
                    if (isTrailingIconVisible && trailingIcon != null) {
                        IconButton(onClick = { onTogglePasswordVisibility() }) {
                            Image(
                                painter = painterResource(trailingIcon),
                                contentDescription = "User",
                            )
                        }
                    }
                }
            }
        }
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontFamily = OpenSans,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
@Preview
@Composable
fun TextFiledNewPreviewNew2(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        val email = remember {
            mutableStateOf("")
        }
        OutlineTextFieldComponent(
            isHeaderVisible = true,
            isTrailingIconVisible = true,
            title = stringResource(R.string.password),
            isLeadingIconVisible = true,
            value = email.value,
            errorMessage = "Error",
            textSize = 40.sp,
            onValueChange = { email.value = it },
            trailingIcon = R.drawable.ic_hide_password_key,
            leadingIcon = R.drawable.ic_password,
            header = stringResource(id = R.string.password),
        )

    }
}
*/
