package com.medrevpatient.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans


@Composable
fun ChatTopBarComponent(
    modifier: Modifier = Modifier,
    isLineVisible: Boolean = false,
    isBackVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit = {},
    colorsFilter: ColorFilter? = null,
    onTrailingIconClick: () -> Unit = {},
    userName: String,
    userProfile: String,


    ) {
    Column(
        modifier = modifier
            .background(AppThemeColor)

    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)

        ) {
            if (isBackVisible) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = null,
                    colorFilter = colorsFilter,

                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onClick()
                        }
                )
            }
            Spacer(modifier = modifier.width(10.dp))
            AsyncImage(
                model = userProfile,
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_app_icon),
                placeholder = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = stringResource(id = R.string.profile_image),
                modifier = Modifier
                    .size(45.dp)
                    .clip(shape = CircleShape)
            )
            Spacer(modifier = modifier.width(10.dp))
            Text(
                text = userName,
                color = White,
                fontFamily = WorkSans,
                fontWeight = FontWeight.W500,
                fontSize = 20.sp,
            )
            if (isTrailingIconVisible && trailingIcon != null) {
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = trailingIcon),
                    contentDescription = null,
                    modifier = modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onTrailingIconClick()
                        }
                )
            }
        }
        if (isLineVisible) {
            HorizontalDivider(thickness = 1.dp, color = MineShaft)
        }
    }
}

@Preview
@Composable
fun ChatTopBarComponentPreview() {
    ChatTopBarComponent(
        userName = "Group Name",
        userProfile = "",
        isLineVisible = true,
        isBackVisible = true,
        trailingIcon = R.drawable.ic_app_icon,
        isTrailingIconVisible = true
    )
}

