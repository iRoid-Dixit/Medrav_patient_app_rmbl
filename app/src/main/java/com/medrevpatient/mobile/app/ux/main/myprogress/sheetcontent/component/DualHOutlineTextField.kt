package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight.Companion.W300
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
private fun DualHOutlineTextFieldPreview(modifier: Modifier = Modifier) {
    var value1 by remember { mutableStateOf("") }
    var value2 by remember { mutableStateOf("") }

    DualHOutlineTextField(
        modifier = modifier,
        value = value1,
        value2 = value2,
        unit = "Hours",
        unit2 = "Minute",
        onValueChange = { value1 = it },
        onValueChange2 = { value2 = it },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DualHOutlineTextField(
    modifier: Modifier = Modifier,
    value: String,
    value2: String = "",
    unit: String,
    unit2: String,
    onValueChange: (String) -> Unit,
    onValueChange2: (String) -> Unit = {},
    errorMsg: String = "",
) {

    val textStyle = MaterialTheme.typography.bodySmall.copy(
        textAlign = TextAlign.Center,
        fontWeight = W800,
        fontSize = 22.sp,
        lineHeight = 22.sp
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


    val interactionSource1 = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }

    VStack(
        4.dp,
        modifier = modifier
    ) {

        CompositionLocalProvider(LocalTextSelectionColors provides textFieldColor.textSelectionColors) {

            HStack(14.dp) {

                // Editable TextField 1
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = textStyle.copy(textAlign = TextAlign.Start),
                    singleLine = true,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25))
                        .border(1.dp, Color.Black, RoundedCornerShape(25))
                        .weight(1f),
                    cursorBrush = SolidColor(textFieldColor.cursorColor),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    interactionSource = interactionSource1,
                ) { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value,
                        visualTransformation = VisualTransformation.None,
                        innerTextField = innerTextField,
                        enabled = true,
                        placeholder = {
                            BasicText(
                                text = "HH",
                                style = textStyle.copy(
                                    textAlign = TextAlign.Start,
                                    color = grey94,
                                    fontWeight = W300,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier
                            )
                        },
                        trailingIcon = {
                            BasicText(
                                text = unit,
                                style = textStyle.copy(
                                    textAlign = TextAlign.Center,
                                    color = black25,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        singleLine = true,
                        colors = textFieldColor,
                        shape = RoundedCornerShape(25),
                        interactionSource = interactionSource1,
                        contentPadding = PaddingValues(horizontal = 8.dp), // this is how you can remove the padding
                    )
                }

                // Editable TextField 2
                BasicTextField(
                    value = value2,
                    onValueChange = onValueChange2,
                    textStyle = textStyle.copy(textAlign = TextAlign.Start),
                    singleLine = true,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25))
                        .border(1.dp, Color.Black, RoundedCornerShape(25))
                        .weight(1f),
                    cursorBrush = SolidColor(textFieldColor.cursorColor),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    interactionSource = interactionSource2,
                ) { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = value2,
                        visualTransformation = VisualTransformation.None,
                        innerTextField = innerTextField,
                        enabled = true,
                        placeholder = {
                            BasicText(
                                text = "MM",
                                style = textStyle.copy(
                                    textAlign = TextAlign.Start,
                                    color = grey94,
                                    fontWeight = W300,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier
                            )
                        },
                        trailingIcon = {
                            BasicText(
                                text = unit2,
                                style = textStyle.copy(
                                    textAlign = TextAlign.Center,
                                    color = black25,
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        singleLine = true,
                        colors = textFieldColor,
                        shape = RoundedCornerShape(25),
                        interactionSource = interactionSource2,
                        contentPadding = PaddingValues(horizontal = 8.dp), // this is how you can remove the padding
                    )
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