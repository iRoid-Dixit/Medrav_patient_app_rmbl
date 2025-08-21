package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.W800
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94


@Preview(showBackground = true)
@Composable
private fun AddLogTextFieldPreview() {
    var value by remember { mutableStateOf("") }

    UnitSeparatorTextField(
        value = value,
        unit = "hours",
        errorMsg = "error",
        onValueChange = { value = it },
        modifier = Modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSeparatorTextField(
    modifier: Modifier = Modifier,
    value: String,
    value2: String = "",
    unit: String,
    onValueChange: (String) -> Unit,
    onValueChange2: (String) -> Unit = {},
    errorMsg: String = "",
) {

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        textAlign = TextAlign.Center,
        fontWeight = W800,
        fontSize = 26.sp,
        lineHeight = 26.sp
    )

    val textFieldColor = TextFieldDefaults.colors().copy(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = Color.Black,
        textSelectionColors = TextSelectionColors(
            handleColor = black25,
            backgroundColor = black25.copy(alpha = .1f)
        )
    )

    val hmModifier = if (unit.lowercase() == "hours") modifier
        .height(108.dp)
        .padding(horizontal = 18.dp) else
        modifier
            .height(108.dp)
            .width(198.dp)


    val interactionSource = remember { MutableInteractionSource() }

    VStack(
        4.dp,
        modifier = hmModifier

    ) {

        CompositionLocalProvider(LocalTextSelectionColors provides textFieldColor.textSelectionColors) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(25))
                    .border(1.dp, Color.Black, RoundedCornerShape(25))
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                HStack(0.dp) {

                    // Editable TextField
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = textStyle,
                        singleLine = true,
                        modifier = Modifier.weight(.6f),
                        cursorBrush = SolidColor(textFieldColor.cursorColor),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        interactionSource = interactionSource,
                    ) { innerTextField ->
                        TextFieldDefaults.DecorationBox(
                            value = value,
                            visualTransformation = VisualTransformation.None,
                            innerTextField = innerTextField,
                            enabled = true,
                            placeholder = {
                                BasicText(
                                    text = "0",
                                    style = textStyle.copy(
                                        textAlign = TextAlign.Center,
                                        color = grey94
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            singleLine = true,
                            colors = textFieldColor,
                            shape = RoundedCornerShape(25),
                            interactionSource = interactionSource,
                            contentPadding = PaddingValues(horizontal = 8.dp), // this is how you can remove the padding
                        )
                    }

                    if (unit.trim().isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .padding(vertical = 18.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black,
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Text(
                            text = unit,
                            modifier = Modifier.weight(0.4f),
                            style = textStyle.copy(
                                fontSize = 16.sp,
                                fontWeight = Medium,
                                textAlign = TextAlign.Center
                            ),
                        )
                    }

                    if (unit.lowercase() == "hours") {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // Editable TextField
                            BasicTextField(
                                value = value2,
                                onValueChange = onValueChange2,
                                textStyle = textStyle,
                                singleLine = true,
                                modifier = Modifier.weight(.6f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                interactionSource = interactionSource,
                            ) { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = value2,
                                    visualTransformation = VisualTransformation.None,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    placeholder = {
                                        BasicText(
                                            text = "0",
                                            style = textStyle.copy(
                                                textAlign = TextAlign.Center,
                                                color = grey94
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    },
                                    singleLine = true,
                                    colors = textFieldColor,
                                    shape = RoundedCornerShape(25),
                                    interactionSource = interactionSource,
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(1.dp)
                                    .padding(vertical = 18.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            Text(
                                text = "Minutes",
                                modifier = Modifier.weight(0.4f),
                                style = textStyle.copy(
                                    fontSize = 16.sp,
                                    fontWeight = Medium,
                                    textAlign = TextAlign.Center
                                ),
                            )
                        }
                    }
                }
            }
        }

        Text(
            text = errorMsg,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.error),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 18.dp)
        )
    }
}
