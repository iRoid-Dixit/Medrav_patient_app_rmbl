package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W200
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.aliceBlue
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.TitleAndBulletText

@Preview
@Composable
private fun StartWorkoutScreenPreview() {
    MaterialTheme {
        StartWorkoutScreen(
            uiState = TodayRoutineUiState.WorkoutScreenUiState(),
            modifier = Modifier,
            event = {}
        )
    }
}

@Composable
fun StartWorkoutScreen(
    uiState: TodayRoutineUiState.WorkoutScreenUiState,
    event: TodayRoutineEvent,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier,
        containerColor = Color.White,
        topBar = {
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (!uiState.isLoading) {
                            event(TodayRoutineUIEvent.WorkoutUIEvent.CloseScreen)
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(painterResource(R.drawable.close_with_bg), contentDescription = "back")
                }
            }
        }
    ) { innerPadding ->
        StartWorkoutContent(
            uiState = uiState,
            event = event,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun StartWorkoutContent(
    uiState: TodayRoutineUiState.WorkoutScreenUiState,
    event: TodayRoutineEvent,
    modifier: Modifier = Modifier
) {

    uiState.apply {

        VStack(
            spaceBy = 8.dp,
            modifier = modifier
                .padding(18.dp)
        ) {

            TitleAndBulletText(
                items = titleValue.second,
                title = titleValue.first,
                horizontal = Alignment.CenterHorizontally
            )

            val style = MaterialTheme.typography.headlineLarge.copy(fontWeight = Medium)

            Box(
                modifier = Modifier
                    .padding(9.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .weight(1f)
                    .fillMaxWidth()
                    .background(
                        brush =
                        Brush.verticalGradient(listOf(aliceBlue.copy(0.4f), aliceBlue))
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                VStack(
                    spaceBy = 0.dp,
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {

                    if (uiState.isInPreviewMode) {
                        Text(
                            text = "Preview Mode".uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = SemiBold,
                            lineHeight = 20.sp,
                            letterSpacing = 2.sp,
                            color = black25,
                        )
                    } else {
                        VStack(8.dp) {
                            Text(
                                text = uiState.timerValue,
                                fontSize = 64.sp,
                                fontFamily = outFit,
                                fontWeight = W200,
                                lineHeight = 80.sp,
                                color = black25,
                            )

                            Text(
                                text = "Today’s Total exercise time".uppercase(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = SemiBold,
                                lineHeight = 20.sp,
                                letterSpacing = 2.sp,
                                color = black25,
                            )
                        }

                    }

                    player?.let { exo ->
                        ExerciseVideoPlayer(
                            exoPlayer = exo,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    VStack(22.dp) {

                        if (!uiState.isInPreviewMode) {
                            IconButton(
                                onClick = {
                                    event(TodayRoutineUIEvent.WorkoutUIEvent.StartStop)
                                },
                                modifier = Modifier.size(80.dp)
                            ) {
                                Icon(
                                    painterResource(
                                        if (isTimerRunning) drawable.filled_pause else drawable.filled_play
                                    ),
                                    contentDescription = "start/stop",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxSize()
                                )
                            }
                        }

                        HStack(
                            8.dp,
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(25))
                                .shadow(elevation = 2.dp, shape = RoundedCornerShape(25))
                                .clickable {
                                    event(TodayRoutineUIEvent.WorkoutUIEvent.Complete)
                                }
                                .background(
                                    brush = Brush.horizontalGradient(
                                        listOf(
                                            white,
                                            aliceBlue,
                                            aliceBlue,
                                        )
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(R.drawable.log_added),
                                contentDescription = null,
                            )

                            val compText =
                                if (uiState.isExerciseCompleted) "Exercise Competed" else "complete"

                            Text(
                                text = compText.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseVideoPlayer(
    exoPlayer: ExoPlayer,
    modifier: Modifier = Modifier
) {

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.pause()
            exoPlayer.seekTo(0)
            exoPlayer.clearMediaItems()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = false // Hide controls
            }
        },
        update = { it.player = exoPlayer },
        modifier = modifier
    )
}


/*

@Preview(showBackground = true)
@Composable
private fun CompletedExerciseDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onClick: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {

        VStack(
            spaceBy = 14.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(25.dp))
                .background(
                    brush =
                    Brush.horizontalGradient(
                        listOf(white, aliceBlue)
                    )
                )
                .padding(18.dp)
        ) {

            Icon(
                painter = painterResource(drawable.exercise),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(60.dp)
            )

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.White,
                                black25,
                                Color.White
                            )
                        )
                    )
                    .fillMaxWidth()
            )


            Text(
                text = "Great!",
                fontSize = 24.sp,
                fontFamily = outFit,
                fontWeight = W800,
                lineHeight = 42.sp,
                color = black25,
            )

            Text(
                text = "You have completed your first Exercise.",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = Bold,
                lineHeight = 19.sp,
                letterSpacing = 2.sp,
                color = black25,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Are you ready for your next exercise?",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = Normal,
                lineHeight = 20.sp,
                color = black25,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.padding(1.dp))

            VStack(8.dp) {
                SkaiButton(
                    innerPadding = PaddingValues(horizontal = 28.dp, vertical = 20.dp),
                    text = "next exercise",
                    textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 12.sp),
                    onClick = onClick
                )

                Text(
                    text = "CLOSE",
                    style = MaterialTheme.typography.labelMedium,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = W800,
                    lineHeight = 15.sp,
                    letterSpacing = 2.sp,
                    color = black25,
                    modifier = Modifier
                        .clip(RoundedCornerShape(25.dp))
                        .clickable {
                            onDismiss()
                        }
                )

            }

        }
    }
}*/
