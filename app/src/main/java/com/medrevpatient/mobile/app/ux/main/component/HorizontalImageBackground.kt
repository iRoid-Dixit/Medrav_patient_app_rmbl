package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.TitleAndBulletText


@Preview
@Composable
private fun ImageBackGroundItemComponentPreview(modifier: Modifier = Modifier) {
    HorizontalImageGradientItem(
        imageBackGroundItemComponent = ImageBackGroundItemComponent(
            items = listOf("4 Sets", "12 Reps", "30 Sec"),
            title = "Leg Press"
        )
    )
}

@Stable
data class ImageBackGroundItemComponent(
    val title: String,
    val items: List<String> = emptyList(),
)

@Composable
fun HorizontalImageGradientItem(
    imageBackGroundItemComponent: ImageBackGroundItemComponent,
    modifier: Modifier = Modifier
) {

    imageBackGroundItemComponent.apply {

        HorizontalImageGradientBackground(
            backgroundUrl = "",
            modifier = modifier
                .height(98.dp)
                .clip(RoundedCornerShape(25.dp))
        ) {

            TitleAndBulletText(
                title = title,
                items = items,
                horizontal = Alignment.Start
            )
        }
    }
}


@Composable
fun HorizontalImageGradientBackground(
    backgroundUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {

        HStack(0.dp) {
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .aspectRatio(16f / 9f)
                    .background(
                        color = aliceBlue
                    )
            )

            Box(
                modifier = Modifier
                    .aspectRatio(16f / 9f)
                    .weight(0.5f)
            ) {

                AsyncImage(
                    model = backgroundUrl,
                    contentScale = ContentScale.Crop,
                    clipToBounds = true,
                    placeholder = painterResource(R.drawable.img_landscape_placeholder),
                    error = painterResource(R.drawable.img_landscape_placeholder_transparent),
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(16f / 9f)
                )

                Box(
                    modifier = Modifier
                        .aspectRatio(16f / 9f)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    aliceBlue,
                                    Color.Transparent
                                )
                            )
                        )

                )

            }
        }
        content()
    }
}