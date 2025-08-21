package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.grey94
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import com.medrevpatient.mobile.app.utils.ext.makeUpperCase


@Preview(showBackground = true)
@Composable
private fun SkaiButtonPreview() {

    SkaiButton(
        text = "START WORKOUT",
        color = Color.White,
        textStyle = SkaiButtonDefault.textStyle.copy(color = black25),
        borderStroke = BorderStroke(1.dp, Color.Black),
        modifier = Modifier,
        isLoading = true
    ) {}
}


/**
 * @param borderStroke : This apply border stroke to button, but the effect comes in visibility when you change the color of button and may be text
 * @param makeUpperCase : This will make the text uppercase
 */

@Composable
fun SkaiButton(
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    glowingText: Boolean = false,
    enable: Boolean = true,
    makeUpperCase: Boolean = true,
    isLoading: Boolean = false,
    color: Color = SkaiButtonDefault.color,
    borderStroke: BorderStroke? = null,
    innerPadding: PaddingValues = SkaiButtonDefault.defaultPadding,
    textStyle: TextStyle = SkaiButtonDefault.textStyle,
    elevation: Dp = SkaiButtonDefault.elevation,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    val alpha = if (isLoading) 0f else 1f

    Surface(
        modifier = modifier,
        color = if (enable) color else grey94,
        enabled = enable,
        shape = RoundedCornerShape(25),
        tonalElevation = elevation,
        shadowElevation = elevation,
        border = borderStroke,
        onClick = { if(!isLoading) onClick() },
    ) {

        Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {

            HStack(
                spaceBy = 8.dp,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(innerPadding)
                    .alpha(alpha)
            ) {

                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(Modifier.padding(start = 8.dp))
                }

                Box(
                    modifier = Modifier,
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = text.makeUpperCase(makeUpperCase),
                        style = textStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )

                    glowingText.ifTrue {
                        Text(
                            text = text.makeUpperCase(makeUpperCase),
                            style = textStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .blur(
                                    radius = 3.dp,
                                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                                )
                        )
                    }
                }
            }
            if (isLoading)
                CircularProgressIndicator(
                    color = textStyle.color,
                    strokeWidth = 4.dp,
                    trackColor = Color.Transparent,
                    modifier = Modifier
                        .size(18.dp)
                        .alpha(1f - alpha),
                )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun SkaiButtonGlowingPreview() {
    SkaiButton(
        text = "START WORKOUT",
        isLoading = true,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.filled_play),
                contentDescription = null,
                tint = Color.White,
            )
        },
        glowingText = true,
        modifier = Modifier
    ) {

    }
}


object SkaiButtonDefault {
    val textStyle = TextStyle(
        fontWeight = FontWeight.W800,
        color = Color.White,
        fontFamily = outFit,
        fontSize = 16.sp,
        letterSpacing = 1.sp
    )
    val elevation = 8.dp
    val defaultPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp)
    val color: Color = Color(0xFF252525)
}
