package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.ColorSwansDown
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.white

@Preview(showBackground = true)
@Composable
private fun RoundedImageWithRowDescriptionPreview(
    modifier: Modifier = Modifier
) {
    RoundedImageWithRowDescription(
        image = "",
        modifier = Modifier.size(200.dp)
    ) {
        Text(
            text = "Description",
        )
    }
}

//It's ReUsable
@Composable
fun RoundedImageWithRowDescription(
    image: String,
    modifier: Modifier = Modifier,
    isImageVisible: Boolean = true,
    isBackgroundGradient: Boolean = false,
    clipPercent: Int = 18,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(clipPercent))
            .background(
                brush = if (isBackgroundGradient) Brush.linearGradient(
                    listOf(white, ColorSwansDown)
                ) else Brush.linearGradient(listOf(black25, black25))
            ),
        contentAlignment = Alignment.Center
    ) {

        VStack(
            0.dp,
        ) {
            if (isImageVisible) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.img_portrait_placeholder_transparent),
                    error = painterResource(R.drawable.img_portrait_placeholder_transparent),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp)
                        .clip(RoundedCornerShape(18))
                        .weight(1f),
                )
            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}
