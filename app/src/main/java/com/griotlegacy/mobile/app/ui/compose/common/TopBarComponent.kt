package com.griotlegacy.mobile.app.ui.compose.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.griotlegacy.mobile.app.R
import com.griotlegacy.mobile.app.ui.theme.AppThemeColor
import com.griotlegacy.mobile.app.ui.theme.MineShaft
import com.griotlegacy.mobile.app.ui.theme.White
import com.griotlegacy.mobile.app.ui.theme.WorkSans


@Composable
fun TopBarComponent(
    modifier: Modifier = Modifier,
    header: String,
    isLineVisible: Boolean = false,
    isBackVisible: Boolean = false,
    isTrailingIconVisible: Boolean = false,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit = {},
    colorsFilter: ColorFilter? = null,
    onTrailingIconClick: () -> Unit = {},
    textAlign: TextAlign = TextAlign.Center

) {
    Column(
        modifier = modifier
            .background(AppThemeColor)

    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .requiredHeight(54.dp)
                .fillMaxWidth()
                .padding(horizontal = 20.dp)

        ) {
            if(isBackVisible){
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
            Text(
                text = header,
                color = White,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = textAlign,
                modifier = modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 10.dp)

                    .weight(1f), fontFamily = WorkSans,
                fontWeight = FontWeight.W500
            )
            if (isTrailingIconVisible && trailingIcon != null) {

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
fun TopBarComponentPreview() {
    TopBarComponent(
        header = "Title",
        isLineVisible = true,
        isTrailingIconVisible = true
    )
}

