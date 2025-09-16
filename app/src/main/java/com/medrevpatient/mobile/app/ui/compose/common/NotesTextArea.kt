package com.medrevpatient.mobile.app.ui.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.ui.theme.Alto
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.Gray5
import com.medrevpatient.mobile.app.ui.theme.SpunPearl
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Composable
fun NotesTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Add any information you'd like the doctor to know before your appointment...",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Additional Notes (Optional)",
            fontFamily = nunito_sans_600,
            color = SteelGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = nunito_sans_400,
                fontSize = 14.sp,
                color = SteelGray
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = White,
                    shape = RoundedCornerShape(15.dp)
                )
                .border(
                    width = 1.dp,
                    color = Alto,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(16.dp)
        ) { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = nunito_sans_400,
                    color = SpunPearl,
                    fontSize = 12.sp
                )
            }
            innerTextField()
        }
    }
}
@Preview
@Composable
private fun NotesTextAreaPreview() {
    NotesTextArea(
        value = "",
        onValueChange = {}
    )
}


