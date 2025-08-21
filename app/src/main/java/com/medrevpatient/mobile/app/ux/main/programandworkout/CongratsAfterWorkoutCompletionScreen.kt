package com.medrevpatient.mobile.app.ux.main.programandworkout

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medrevpatient.mobile.app.data.source.remote.dto.ExerciseCompleted
import com.medrevpatient.mobile.app.domain.response.ApiResponse
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25A50
import com.medrevpatient.mobile.app.ui.theme.black25A80
import com.medrevpatient.mobile.app.ui.theme.neonNazar
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.ux.main.component.RoundedImageWithRowDescription
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.CenterAlignContentWrapper

//TODO: use simple clap icon instead clap ray

@Preview
@Composable
private fun CongratsAfterWorkoutCompletionScreenPreview(
    modifier: Modifier = Modifier
) {
    CongratsAfterWorkoutCompletionContent(
        uiState = TodayRoutineUiState(
            congratulationsScreenUiState = ApiResponse(
                data = ExerciseCompleted(exercises = 8)
            )
        ),
        event = {},
        modifier = Modifier
            .fillMaxSize()

    )
}

@Composable
fun CongratsAfterWorkoutCompletionScreen(
    todayRoutineUiState: TodayRoutineUiState,
    event: TodayRoutineEvent,
    modifier: Modifier = Modifier
) {

    CongratulationSpotLightBackground {

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = modifier
                        .statusBarsPadding()
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    IconButton(
                        onClick = { event(TodayRoutineUIEvent.CloseCongratulationScreen) },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(drawable.close_with_bg),
                            contentDescription = "back",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { innerPadding ->
            CongratsAfterWorkoutCompletionContent(
                uiState = todayRoutineUiState,
                event = event,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .align(Alignment.Center)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CongratsAfterWorkoutCompletionContent(
    uiState: TodayRoutineUiState,
    event: TodayRoutineEvent,
    modifier: Modifier = Modifier
) {

    uiState.congratulationsScreenUiState?.apply {

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = modifier
        ) {

            //TODO:REMOVE it after clearance

            /* item(
                 span = {
                     GridItemSpan(maxLineSpan)
                 }
             ) {
                 Header(message = message)
             }*/

            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                FlowRow(
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProgressItemComponent(
                        icon = drawable.filled_time,
                        value = AppUtils.formatTimeToHHMMSSInt(data?.spentTime ?: 0L),
                        title = "Spend Time"
                    )

                    ProgressItemComponent(
                        icon = drawable.exercise,
                        value = data?.exercises.toString(),
                        title = "Exercise"
                    )
                }
            }


            item(
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                CenterAlignContentWrapper(
                    title = "EXPLORE MORE RECIPES",
                    style = TextStyle(
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                ) {}
            }

            data?.recipes?.let {
                items(it) { item ->
                    RoundedImageWithRowDescription(
                        image = item.image,
                        modifier = Modifier
                            .height(162.dp)
                            .clickable {
                                event(
                                    TodayRoutineUIEvent.NavigateTo(
                                        RouteMaker.ViewRecipe.createRoute(
                                            item.id
                                        )
                                    )
                                )
                            }
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = SemiBold,
                            lineHeight = 16.sp,
                            color = white,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Header(
    message: String = "You have completed your first workout, you got a new streak.",
    modifier: Modifier = Modifier
) {

    VStack(8.dp, modifier) {

        Image(
            painter = painterResource(drawable.appreciate),
            contentDescription = null,
            modifier = Modifier
                .padding(79.dp)
                .size(94.dp)
        )

        Text(
            text = message,
            style = TextStyle(
                color = Color.White,
                fontFamily = outFit,
                fontWeight = Bold,
                fontSize = 14.sp,
                letterSpacing = 2.sp,
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 296.dp)
        )
    }
}


@Preview
@Composable
private fun ProgressItemComponentPreview() {
    ProgressItemComponent(
        drawable.filled_time,
        value = "38 Mins",
        title = "Spend Time"
    )
}

@Composable
private fun ProgressItemComponent(
    @DrawableRes icon: Int,
    value: String,
    title: String,
    modifier: Modifier = Modifier,
    tint: Color = neonNazar,
) {
    HStack(
        8.dp,
        modifier = modifier
            .sizeIn(minWidth = 168.dp, minHeight = 92.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(
                color = black25A50
            )
            .padding(8.dp)
    ) {

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = black25A80
                )
                .padding(14.dp)
        )



        VStack(
            spaceBy = 8.dp,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = outFit,
                    fontWeight = Bold,
                    fontSize = 16.sp,
                )
            )
            Text(
                text = title,
                style = TextStyle(
                    color = Color.White,
                    fontFamily = outFit,
                    fontWeight = SemiBold,
                    fontSize = 12.sp,
                )
            )
        }
    }
}


@Composable
private fun CongratulationSpotLightBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color.Black
            )
            .drawBehind {
                val width = size.width
                val height = size.height

                drawCircle(
                    alpha = 0.1f,
                    radius = height / 2f,
                    brush = Brush.radialGradient(
                        listOf(Color.White, Color.Transparent),
                        center = Offset(
                            x = width / 2f,
                            y = 0f
                        )
                    ),
                    center = Offset(
                        x = width / 2f,
                        y = 0f
                    ),
                )
            },
    ) {
        content()
    }
}