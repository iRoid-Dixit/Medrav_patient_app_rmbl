package com.medrevpatient.mobile.app.ux.main.videoLoad

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer

import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.OnLifecycleEvent
import com.medrevpatient.mobile.app.ui.theme.OrientationChangeListener
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.ux.main.videoLoad.ImageVideoPlayerViewModel.Companion.getFormatedDuration
import kotlinx.coroutines.delay

@Composable
fun ImageVideoPlayerScreen(
    viewModel: ImageVideoPlayerViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLandscape by remember { mutableStateOf(false) }

    val activity = LocalActivity.current as ImageVideoPlayerActivity

    OrientationChangeListener { isLandscape = it }

    PlayerUiController(
        activity = activity,
        isLandscape = isLandscape
    )

    if (isLandscape) {
        PlayerLandscapeMode(
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            uiState = uiState,
            onBackPress = { activity.finish() }
        )
    } else {
        PlayerPortraitMode(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            viewModel = viewModel,
            uiState = uiState,
            onBackPress = { activity.finish() }
        )
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                viewModel.eventHandler(PlayerUiEvent.PlayerControl.Pause)
            }

            else -> {}
        }
    }
}


@Composable
fun PlayerPortraitMode(
    modifier: Modifier = Modifier,
    viewModel: ImageVideoPlayerViewModel,
    uiState: PlayerUiState,
    onBackPress: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = AppThemeColor,
        topBar = {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_app_icon),
                contentDescription = "Back",
                modifier = Modifier
                    .padding(20.dp)
                    .noRippleClickable {
                        onBackPress()
                    }
            )
        },
    ) { innerPadding ->
        PlayerPortraitContent(
            exoplayer = viewModel.exoPlayer,
            event = viewModel::eventHandler,
            uiState = uiState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun PlayerLandscapeMode(
    modifier: Modifier = Modifier,
    viewModel: ImageVideoPlayerViewModel,
    uiState: PlayerUiState,
    onBackPress: () -> Unit
) {
    PlayerLandScapeContent(
        exoplayer = viewModel.exoPlayer,
        event = viewModel::eventHandler,
        uiState = uiState,
        modifier = modifier,
        onBackPress = onBackPress
    )
}


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerLandScapeContent(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    exoplayer: ExoPlayer,
    event: (PlayerUiEvent) -> Unit,
    onBackPress: () -> Unit
) {


}

@Composable
fun PlayerPortraitContent(
    modifier: Modifier = Modifier,
    uiState: PlayerUiState,
    exoplayer: ExoPlayer,
    event: (PlayerUiEvent) -> Unit
) {

    var shouldShowControls by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {



        AnimatedVisibility(
            visible = shouldShowControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
            // .aspectRatio(16 / 9f)
        ) {
            PlayerControlsOverlay(
                uiState = uiState,
                event = event,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Black.copy(alpha = 0.5f))
            )
        }
    }

    LaunchedEffect(shouldShowControls, uiState.isPlaying) {
        if (shouldShowControls && uiState.isPlaying) {
            delay(5000)
            shouldShowControls = false
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerControlsOverlay(
    uiState: PlayerUiState,
    event: (PlayerUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    var isSliding by remember { mutableStateOf(false) }
    var sliderValueHolder by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Image(
            imageVector = if (uiState.isPlaying) {
                ImageVector.vectorResource(id = R.drawable.ic_app_icon)
            } else {
                ImageVector.vectorResource(id = R.drawable.ic_app_icon)
            },
            contentDescription = "Play/Pause",
            modifier = Modifier
                .align(Alignment.Center)
                .noRippleClickable {
                    event(PlayerUiEvent.PlayerControl.PlayPause)
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.currentPosition.getFormatedDuration(),
                    fontFamily = WorkSans,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppThemeColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(com.medrevpatient.mobile.app.ui.theme.White)
                        .padding(horizontal = 12.dp)
                )
                Text(
                    text = uiState.duration.getFormatedDuration(),
                    fontFamily = WorkSans,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Black,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(White)
                        .padding(horizontal = 12.dp)
                )
            }

            val currentProgress =
                if (isSliding) sliderValueHolder else if (uiState.duration > 0) uiState.currentPosition.toFloat() else 0f

            Slider(
                value = currentProgress,
                valueRange = 0f..(uiState.duration.takeIf { it > 0 }?.toFloat() ?: 1f), // Avoid NaN
                onValueChange = {
                    isSliding = true
                    sliderValueHolder = it
                },
                onValueChangeFinished = {
                    if (uiState.duration > 0) { // Ensure valid duration
                        event(PlayerUiEvent.PlayerControl.SeekTo(sliderValueHolder / uiState.duration.toFloat()))
                    }
                    isSliding = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = com.medrevpatient.mobile.app.ui.theme.White,
                    activeTrackColor = com.medrevpatient.mobile.app.ui.theme.White,
                    inactiveTrackColor = com.medrevpatient.mobile.app.ui.theme.White.copy(alpha = 0.5f),
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )


        }
    }
}
