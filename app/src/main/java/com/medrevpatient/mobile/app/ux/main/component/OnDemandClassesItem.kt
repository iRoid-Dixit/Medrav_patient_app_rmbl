package com.medrevpatient.mobile.app.ux.main.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.utils.ext.ifTrue
import kotlin.text.Typography.bullet

@Composable
fun OnDemandClassesItem(
    title: String,
    time: String,
    level: String,
    url: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    var shouldShowPlayIcon by remember { mutableStateOf(false) }


    Box(
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(25))
                .clickable { onClick() }
                .size(150.dp, 198.dp)
        ),
        contentAlignment = Alignment.Center
    ) {

        AsyncImage(
            model = url,
            placeholder = painterResource(R.drawable.img_portrait_placeholder),
            error = painterResource(R.drawable.img_portrait_placeholder),
            onError = {
                shouldShowPlayIcon = false
            },
            onSuccess = {
                shouldShowPlayIcon = true
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        shouldShowPlayIcon.ifTrue {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush =
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black)
                        )
                    )
            )

            IconButton(
                onClick = onClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.filled_play),
                    contentDescription = "play-pause",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            VStack(
                spaceBy = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                HStack(
                    8.dp
                ) {
                    Text(
                        text = "$bullet $time",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$bullet $level",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}