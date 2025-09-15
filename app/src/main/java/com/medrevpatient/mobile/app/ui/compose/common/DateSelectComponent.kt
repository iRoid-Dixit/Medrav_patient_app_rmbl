package com.medrevpatient.mobile.app.ui.compose.common
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.Gray5
import com.medrevpatient.mobile.app.ui.theme.Gray40
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_400
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_600

@Composable
fun DateSelectComponent(
    value: String,
    valueTextColor: Color = Color.Black,
    header: String? = null,
    errorMessage: String? = null,
    @DrawableRes trailingIcon: Int? = null,
    @DrawableRes leadingIcon: Int? = null,
    onClick: () -> Unit = {},
    endPadding: Dp = 0.dp,
    isTitleVisible: Boolean = false,
    backGroundColor: Color = Gray5,
    borderColors: Color = Color.Transparent,
    title: String = "",
) {
    val borderColor = when {
        errorMessage?.isNotEmpty() == true -> MaterialTheme.colorScheme.error
        else -> Color.Transparent
    }
    Column {
        if (isTitleVisible) {
            Text(
                text = title,
                fontFamily = nunito_sans_600,
                color = SteelGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = backGroundColor, shape = RoundedCornerShape(15))
                .fillMaxWidth()
                .border(width = 1.dp, color = borderColors, shape = RoundedCornerShape(15))
                .heightIn(50.dp)
                .clickable { onClick() }
                .clip(RoundedCornerShape(15))
        ) {

            if (leadingIcon != null)
                Image(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = null,
                    modifier = Modifier.padding(start = 16.dp)
                )

            (value.ifEmpty { header })?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    fontFamily = if (value.isNotBlank()) nunito_sans_600 else nunito_sans_400,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (value.isNotBlank()) valueTextColor else Gray40,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 15.dp)
                )
            }
            if (trailingIcon != null)
                IconButton(onClick = onClick, modifier = Modifier.padding()) {
                    Image(
                        painter = painterResource(id = trailingIcon),
                        contentDescription = trailingIcon.toString(),
                    )
                }
            Spacer(modifier = Modifier.width(endPadding))
        }
        if (errorMessage?.isNotEmpty() == true) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontFamily = nunito_sans_600,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp)
            )
        }
    }
}
@Preview
@Composable
fun ClickInputComponentPreview() {
    DateSelectComponent(
        value = "9.20 AM",
        header = "Create",
        trailingIcon = R.drawable.ic_app_icon
    )
}