package com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.ux.main.component.OutlineTextFieldWithTrailing
import com.medrevpatient.mobile.app.ux.main.component.getUnit
import com.medrevpatient.mobile.app.ux.main.myprogress.sheetcontent.component.DualHOutlineTextField


@Preview(showBackground = true)
@Composable
private fun DualHeaderTextFieldComponentPreview() {
    DualHeaderTextFieldComponent(
        value = "",
        onValueChange = {},
        value2 = "",
        onValueChange2 = {},
        header = "Header",
        description = "Description",
        element = "Calories"
    )
}


@Composable
fun DualHeaderTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    value2: String,
    onValueChange2: (String) -> Unit,
    header: String,
    description: String,
    element: String,
    modifier: Modifier = Modifier,
    placeHolder: String? = null,
) {
    val textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = Bold)

    VStack(
        spaceBy = 8.dp,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.fillMaxWidth()
    ) {

        Text(
            text = header,
            style = textStyle.copy(
                fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                lineHeight = 1.sp
            ),
        )

        description.isNotEmpty().ifTrue {
            Text(
                text = description,
                style = textStyle.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = grey94,
                    lineHeight = 1.sp
                ),
            )
        }

        if (element.getUnit().lowercase() == "hours") {
            DualHOutlineTextField(
                value = value,
                value2 = value2,
                unit = element.getUnit(),
                unit2 = "Minutes",
                onValueChange = onValueChange,
                onValueChange2 = onValueChange2
            )
        } else {
            OutlineTextFieldWithTrailing(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        text = placeHolder ?: "Enter $element",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = W300,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                trailingIcon = {
                    Text(
                        text = element.getUnit(),
                        style = textStyle.copy(
                            fontWeight = Bold,
                            fontSize = 14.sp,
                            color = black25,
                            textAlign = TextAlign.Center
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
