package com.medrevpatient.mobile.app.ux.main.programandworkout.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.neonNazar
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack

@Composable
fun ProgramItemComponent(
    programItem: ProgramsItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    programItem.apply {

        Box(
            modifier = modifier
                .height(220.dp)
                .clip(RoundedCornerShape(30.dp))
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = url,
                contentDescription = null,
                placeholder = painterResource(R.drawable.img_landscape_placeholder),
                error = painterResource(R.drawable.img_landscape_placeholder),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                black25,
                            )
                        )
                    )
            ) {

                VStack(
                    spaceBy = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(18.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White
                    )

                    HStack(8.dp, modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        IconTextHStack(
                            spaceBy = 4.dp,
                            icon = R.drawable.filled_time, text = time, tint = neonNazar,
                            style = TextStyle(color = Color.White)
                        )
                        Spacer(Modifier.weight(1f))
                        //TODO:Remove this on clearance
                        /*IconTextHStack(
                            spaceBy = 4.dp,
                            icon = R.drawable.calorie, text = calories, tint = neonNazar,
                            style = TextStyle(color = Color.White)
                        )*/
                    }
                }
            }
        }

    }
}


data class ProgramsItem(
    val url: String,
    val title: String,
    val time: String,
    val calories: String
)


@Composable
fun ProgramItemComponentCarousalEffect(
    programItem: ProgramsItem,
    modifier: Modifier = Modifier
) {
    var shouldShowDescription by remember { mutableStateOf(false) }

    programItem.apply {

        Box(
            modifier = modifier
                .height(220.dp)
                .clip(RoundedCornerShape(30.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = url,
                contentDescription = null,
                alignment = Alignment.TopCenter,
                placeholder = painterResource(drawable.img_landscape_placeholder),
                error = painterResource(drawable.img_portrait_placeholder),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    shouldShowDescription = true
                },
                modifier = Modifier.fillMaxSize()
            )

            if (shouldShowDescription)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Transparent,
                                    black25,
                                )
                            )
                        )
                ) {
                    VStack(
                        8.dp, modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(18.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.White
                        )

                        HStack(8.dp, modifier = Modifier.fillMaxWidth()) {
                            Spacer(Modifier.weight(1f))
                            IconTextHStack(
                                spaceBy = 4.dp,
                                icon = R.drawable.filled_time, text = time, tint = neonNazar,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = white
                                )
                            )
                            Spacer(Modifier.weight(1f))

                            //TODO: REMOVE THIS
                            /*IconTextHStack(
                                spaceBy = 4.dp,
                                icon = R.drawable.calorie, text = calories, tint = neonNazar,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = white
                                )
                            )*/
                        }
                    }
                }
        }
    }
}


@Preview
@Composable
private fun ProgramItemComponentPreview() {
    ProgramItemComponent(
        modifier = Modifier.width(200.dp),
        programItem = ProgramsItem(
            "",
            "12 Minute Abs Workout", "12 Mins", "525 KCAL"
        )
    )
}
