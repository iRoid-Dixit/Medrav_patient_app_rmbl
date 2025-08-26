package com.medrevpatient.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700


@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    icon: Int = R.drawable.ic_back,
    titleText: String = "BMI & Health Check",
    isBackVisible: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(AppThemeColor, SteelGray)
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )

    ) {

        if (isBackVisible){
            IconButton(
                onClick = onClick,
                modifier = Modifier
                    .padding(start = 20.dp, top = 60.dp)
                    .size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "Icon",
                )
            }
        }
        Text(
            text = titleText,
            color = White,
            fontSize = 20.sp,
            fontFamily = nunito_sans_700,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 60.dp)
        )
    }
}

@Preview
@Composable
fun TopBarComponentPreview() {
    TopBarComponent(
        modifier = Modifier.requiredHeight(100.dp),
        onClick = {},
        icon = R.drawable.ic_back,
        titleText = "BMI & Health Check",

    )
}

