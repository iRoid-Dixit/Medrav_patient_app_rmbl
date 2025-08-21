package com.medrevpatient.mobile.app.ux.main.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.OnLifecycleEvent
import com.medrevpatient.mobile.app.ui.OrientationChangeListener
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.ux.main.player.PlayerUiEvent.PlayerControl
import com.medrevpatient.mobile.app.ux.main.player.PlayerViewModel.Companion.getFormatedDuration
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.TitleAndBulletText
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isLandscape by remember { mutableStateOf(false) }

    val activity = LocalContext.current as PlayerActivity

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
                viewModel.eventHandler(PlayerControl.Pause)
            }

            else -> {}
        }
    }
}


@Composable
fun PlayerPortraitMode(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel,
    uiState: PlayerUiState,
    onBackPress: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Black,
        topBar = {
            TopBarCenterAlignTextAndBack(
                "",
                tint = white,
                onBackPress = onBackPress
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
    viewModel: PlayerViewModel,
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

    var toggleResizeMode by remember { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoplayer
                    useController = false
                    resizeMode = toggleResizeMode
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            event(PlayerUiEvent.ToggleControlsVisibility)
                        },
                        onDoubleTap = {
                            when (toggleResizeMode) {
                                AspectRatioFrameLayout.RESIZE_MODE_FIT -> {
                                    toggleResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                }

                                AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> {
                                    toggleResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                }

                            }
                        }
                    )
                }
        ) { player ->
            player.resizeMode = toggleResizeMode
        }

        AnimatedVisibility(
            visible = uiState.shouldShowControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .background(Color.Black.copy(alpha = 0.5f))
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                PlayerControlsOverlay(
                    uiState = uiState,
                    event = event,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                )

                TopBarCenterAlignTextAndBack(
                    title = "",
                    tint = white, modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 18.dp, vertical = 28.dp),
                    onBackPress = onBackPress
                )
            }
        }
    }
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

        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoplayer
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            shouldShowControls = !shouldShowControls
                        }
                    )
                }
        )

        AnimatedVisibility(
            visible = shouldShowControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        ) {
            PlayerControlsOverlay(
                uiState = uiState,
                event = event,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.5f))
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationGraphicsApi::class)
@Composable
private fun PlayerControlsOverlay(
    uiState: PlayerUiState,
    event: (PlayerUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    var isSliding by remember { mutableStateOf(false) }
    var sliderValueHolder by remember { mutableFloatStateOf(0f) }

    val muteUnMuteVector = rememberAnimatedVectorPainter(
        animatedImageVector = AnimatedImageVector.animatedVectorResource(id = R.drawable.avd_mute_unmute),
        atEnd = uiState.isMute
    )

    VStack(
        spaceBy = 8.dp,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {

        Spacer(Modifier.padding(28.dp))

        IconButton(
            onClick = {
                event(PlayerControl.PlayPause)
            },
        ) {
            Icon(
                painter = painterResource(if (uiState.isPlaying) R.drawable.pause else R.drawable.play),
                contentDescription = "Play_Pause",
                tint = white
            )
        }

        VStack(
            spaceBy = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
        ) {

            HStack(
                spaceBy = 8.dp,
            ) {

                TitleAndBulletText(
                    spaceBy = 2.dp,
                    titleColor = white,
                    items = listOf(uiState.durationLabel, uiState.level),
                    title = uiState.title,
                    horizontal = Alignment.Start,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = uiState.currentPosition.getFormatedDuration() + "/" + uiState.duration.getFormatedDuration(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                IconButton(
                    onClick = {
                        event(PlayerControl.MuteUnMute)
                    },
                ) {
                    Icon(
                        painter = muteUnMuteVector,
                        contentDescription = "Volume",
                        tint = Color.White
                    )
                }
            }

            val currentProgress =
                if (isSliding) sliderValueHolder else uiState.currentPosition.toFloat()


            Slider(
                value = currentProgress,
                valueRange = 0f..uiState.duration.toFloat(),
                onValueChange = {
                    isSliding = true
                    sliderValueHolder = it
                },
                onValueChangeFinished = {
                    event(PlayerControl.SeekTo(sliderValueHolder / uiState.duration.toFloat()))
                    isSliding = false
                },
                colors = SliderDefaults.colors(
                    thumbColor = white,
                    activeTrackColor = white,
                    inactiveTrackColor = white.copy(alpha = .6f),
                ),
                thumb = {},
                track = { sliderState ->
                    val fraction by remember {
                        derivedStateOf {
                            (sliderState.value - sliderState.valueRange.start) /
                                    (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                        }
                    }

                    Box(Modifier.fillMaxWidth()) {
                        val trackHeight = 3.dp
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(color = white.copy(alpha = .6f))
                                .fillMaxWidth()
                                .height(trackHeight)
                        )
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .fillMaxWidth(fraction)
                                .height(trackHeight)
                                .background(white)
                        )
                    }
                },
                modifier = Modifier
                    .background(Color.Transparent)
                    .height(28.dp)
            )
        }
    }
}
