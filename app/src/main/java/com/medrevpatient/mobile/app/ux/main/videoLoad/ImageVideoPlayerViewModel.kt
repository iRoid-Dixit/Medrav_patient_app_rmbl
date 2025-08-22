package com.medrevpatient.mobile.app.ux.main.videoLoad

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ImageVideoPlayerViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel() {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var job: Job? = null
    private var positionUpdateJob: Job? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun handleIntent(videoLink: String) {
        Log.e("TAG", "handleIntent:>>>> $videoLink")
        initializePlayer(videoLink)
    }


    fun eventHandler(event: PlayerUiEvent) {
        when (event) {
            PlayerUiEvent.PlayerControl.PlayPause -> {
                playPause()
            }

            PlayerUiEvent.PlayerControl.MuteUnMute -> {
                exoPlayer.volume = if (exoPlayer.volume == 0f) 1f else 0f
                _uiState.update {
                    it.copy(isMute = !it.isMute)
                }
                playerControlVisibilityToggle()
            }

            is PlayerUiEvent.PlayerControl.SeekTo -> {
                val currentPosition = (event.progress * exoPlayer.duration).toLong()
                exoPlayer.seekTo(currentPosition)
                updateCurrentPosition()
                playerControlVisibilityToggle()
            }

            PlayerUiEvent.PlayerControl.Pause -> {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                }
            }

            PlayerUiEvent.ToggleControlsVisibility -> {
                _uiState.update {
                    it.copy(shouldShowControls = !it.shouldShowControls)
                }
                playerControlVisibilityToggle()
            }
        }
    }

    private fun playerControlVisibilityToggle() {
        job?.cancel()
        job = viewModelScope.launch {
            delay(5000)
            _uiState.update {
                it.copy(shouldShowControls = false)
            }
        }
    }

    private fun initializePlayer(mediaUri: String) {
        val mediaItem = MediaItem.Builder()
            .setUri(mediaUri)
            .build()

        exoPlayer.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        playerEventListener()
    }

    private fun setMetaData() {
        exoPlayer.apply {
            _uiState.update {
                it.copy(
                    title = mediaMetadata.title.toString(),
                    duration = duration,
                    currentPosition = currentPosition,
                )
            }
        }
    }

    private fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }


    private fun playerEventListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    startPositionUpdates()
                }

                _uiState.update {
                    it.copy(isPlaying = isPlaying)
                }
            }

            override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
                super.onDeviceVolumeChanged(volume, muted)
                _uiState.update { it.copy(isMute = muted || volume == 0) }
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    Player.STATE_READY -> {
                        setMetaData()
                        startPositionUpdates()
                    }

                    Player.STATE_BUFFERING -> {
                        // Handle buffering state
                    }

                    Player.STATE_ENDED -> {
                        // Handle ended state
                        stopPositionUpdates()
                    }

                    Player.STATE_IDLE -> {
                        // Handle idle state
                        stopPositionUpdates()
                    }
                }
            }
        })
    }
    private fun updateCurrentPosition() {
        val currentPosition = exoPlayer.currentPosition
        val duration = exoPlayer.duration

        if (duration > 0) { // Ensure duration is valid
            val progress = currentPosition.toFloat() / duration.toFloat()
            _uiState.update {
                it.copy(
                    currentPosition = currentPosition,
                    progress = progress
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    currentPosition = currentPosition,
                    progress = 0f // Default progress to 0 if duration is invalid
                )
            }
        }
    }

    /* private fun updateCurrentPosition() {
         val currentPosition = exoPlayer.currentPosition
         val progress = currentPosition.toFloat() / exoPlayer.duration.toFloat()
         _uiState.update {
             it.copy(
                 currentPosition = currentPosition,
                 progress = progress
             )
         }
     }*/

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer.apply {
            pause()
            stop()
            release()
        }
        stopPositionUpdates()
    }

    companion object {
        fun Long.getFormatedDuration(): String {
            val totalSeconds = this / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
        }
    }


    private fun startPositionUpdates() {
        positionUpdateJob?.cancel()


        positionUpdateJob = viewModelScope.launch {
            while (exoPlayer.isPlaying) {
                updateCurrentPosition()
                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        updateCurrentPosition()
    }

}


data class PlayerUiState(
    val title: String = "Unknown",
    val duration: Long = 0,
    val durationLabel: String = "",
    val level: String = "",
    val currentPosition: Long = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val shouldShowControls: Boolean = false,
    val isMute: Boolean = false
)

sealed interface PlayerUiEvent {
    object ToggleControlsVisibility : PlayerUiEvent
    object PlayerControl {
        object PlayPause : PlayerUiEvent
        object MuteUnMute : PlayerUiEvent
        object Pause : PlayerUiEvent
        data class SeekTo(val progress: Float) : PlayerUiEvent
    }
}

