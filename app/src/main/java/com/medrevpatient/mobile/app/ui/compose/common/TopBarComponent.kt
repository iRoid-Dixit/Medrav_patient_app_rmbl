package com.medrevpatient.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.SteelGray
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.nunito_sans_700

@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    backIcon: Int = R.drawable.ic_back,
    @DrawableRes actionIcon: Int? = null,
    titleText: String = "BMI & Health Check",
    subTitleText: @Composable (() -> Unit)? = null,
    titleIcon: @Composable (() -> Unit)? = null,
    isBackVisible: Boolean = false,
    isActionVisible: Boolean = false
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
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 20.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            if (isBackVisible) {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = backIcon),
                        contentDescription = "Back",
                    )
                }
            }
            // Title
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                titleIcon?.let { it() }

                Spacer(Modifier.padding(8.dp))

                Column {

                    Text(
                        text = titleText,
                        color = White,
                        fontSize = 20.sp,
                        fontFamily = nunito_sans_700,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    subTitleText?.let { it() }
                }

                if (isActionVisible && actionIcon != null) {
                    IconButton(
                        onClick = onActionClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Image(
                            painter = painterResource(id = actionIcon),
                            contentDescription = "Action",
                            colorFilter = ColorFilter.tint(White)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TopBarComponentPreview() {
    TopBarComponent(
        modifier = Modifier.requiredHeight(100.dp),
        onClick = {},
        isActionVisible = true,
        titleText = "BMI & Health Check",
        titleIcon = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_emoji),
                contentDescription = null
            )
        },
        subTitleText = {
            Text("Good Afternoon, John!", color = White, fontSize = 14.sp)
        }

    )
}

