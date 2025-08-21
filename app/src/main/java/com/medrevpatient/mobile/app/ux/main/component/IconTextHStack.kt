package com.medrevpatient.mobile.app.ux.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.theme.outFit

@Composable
fun IconTextHStack(
    @DrawableRes icon: Int,
    text: String,
    spaceBy: Dp = 8.dp,
    tint: Color = Color.White,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    iconModifier: Modifier = Modifier,
    iconSize: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    HStack(spaceBy, modifier = modifier) {
        Icon(
            painterResource(icon), contentDescription = null, tint = tint,
            modifier = iconModifier.size(iconSize)
        )
        Text(
            text = text,
            fontFamily = outFit,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun IconIMGTextHStack(
    icon: String,
    text: String,
    spaceBy: Dp = 8.dp,
    tint: Color? = Color.White,
    style: TextStyle = MaterialTheme.typography.labelMedium,
    iconModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {

    HStack(spaceBy, modifier = modifier) {

        Image(
            painter = rememberAsyncImagePainter(model = icon),
            contentDescription = null, colorFilter = tint?.let { ColorFilter.tint(tint) },
            modifier = iconModifier.size(16.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            fontFamily = outFit,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,

            )
    }
}