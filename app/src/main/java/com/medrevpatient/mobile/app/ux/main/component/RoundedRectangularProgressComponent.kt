package com.medrevpatient.mobile.app.ux.main.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.canvas.RoundedRectangularProgressIndicator
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.white

@Preview
@Composable
fun RoundedRectangularProgressComponent(
    current: String? = null,
    total: String? = null,
    progress: Float = 0.5f,
    description: String = "Steps",
    @DrawableRes icon: Int? = R.drawable.steps,
    modifier: Modifier = Modifier,
    textSize: Int = 14,
    showTotalValue: Boolean = true,
    onAddClick: (() -> Unit)? = null
) {

    Surface(
        shape = RoundedCornerShape(25),
        color = black25,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {

            HStack(
                0.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                RoundedRectangularProgressIndicator(
                    progress = { progress },
                    roundedRectangularProgressIndicator = RoundedRectangularProgressIndicator(
                        cornerRadius = 0.5f,
                        strokeWidth = 1.dp,
                        contentInside = RoundedRectangularProgressIndicator.Content(
                            padding = PaddingValues(2.dp)
                        )
                    ),
                    modifier = Modifier
                        .size(54.dp)
                ) {
                    VStack(
                        spaceBy = 0.dp,
                        modifier = Modifier.align(Alignment.Center)
                    ) {

                        if (current != null && total != null) {
                            Text(
                                text = current,
                                style = MaterialTheme.typography.labelMedium,
                                color = white,
                                fontSize = textSize.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 1.sp
                            )

                            if (showTotalValue) {
                                Text(
                                    text = "\n/$total",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = white,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 8.sp,
                                    lineHeight = 1.sp
                                )
                            }
                        } else {
                            NoStatesPlaceholder()
                        }
                    }

                }


                Spacer(modifier = Modifier.padding(start = 12.dp))

                icon?.let {
                    Icon(
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = "steps count",
                        modifier = Modifier.size(18.dp),
                        tint = white
                    )
                }

                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                    color = white,
                    maxLines = 1,
                    overflow = Ellipsis,
                )

            }

            onAddClick?.let {
                IconButton(
                    onClick = it,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add),
                        contentDescription = null,
                        tint = white,
                    )
                }
            }

        }
    }

}

