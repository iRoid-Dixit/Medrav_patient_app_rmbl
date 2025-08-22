package com.app.musicplayerdemo.service


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService


const val TAG = "MSessionService"

class MusicPlayerService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    private val playersBackground: MutableMap<String, ExoPlayer> = mutableMapOf()

    override fun onCreate() {
        super.onCreate()
        player = createPlayer(this, true)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.playWhenReady = true

        mediaSession = MediaSession.Builder(this, player).build()
        synchronizedMainPlayers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        intent?.let { handleIntents(it) }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player!!

        if (player.playWhenReady) {
            player.pause()
        }

        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED
        ) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            cleanUpBgPlayers()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }


    /**________________________METHODS_________________________________*/

    private fun createPlayer(context: Context, isMainPlayer: Boolean = false): ExoPlayer {

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, isMainPlayer)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
    }

    private fun addBackGroundMusics(mediaUrls: List<String>) {
        mediaUrls.map {
            val player = createPlayer(this)  // Create a new player
            val mediaItem = MediaItem.fromUri(it)  // Create a media item from URI
            player.setMediaItem(mediaItem)  // Add media item to player
            player.prepare()  // Prepare the player
            player.repeatMode = Player.REPEAT_MODE_ONE
            playersBackground[it] = player  // Store the player in the map
        }
    }

    /*private fun handleIntents(intent: Intent) {

        when (intent.action) {

            MEDIA_URIS -> {

                val mediaUrls = intent.getStringArrayListExtra(MEDIA_URIS)

                mediaUrls?.let { urls ->
                    cleanUpBgPlayers()
                    addBackGroundMusics(urls)
                    synchronizedBGPlayers()
                }
                Log.d(TAG, "handleIntents: MEDIA_URIS")
            }

            PRIM_VOLUME -> {
                val primeVolume = intent.getFloatExtra(PRIM_VOLUME, 1f)
                player.volume = primeVolume
            }

            BG_SOUND -> {
                val itemKey = intent.getStringExtra(BG_SOUND_INDEX)
                val itemVolume = intent.getFloatExtra(BG_SOUND_RANGE, 0f)
                playersBackground[itemKey]?.volume = itemVolume
            }

            REPEAT_ALL -> {
                player.repeatMode = Player.REPEAT_MODE_ONE
            }

            else -> {}
        }
    }*/

    private fun cleanUpBgPlayers() {
        if (playersBackground.isNotEmpty()) {
            playersBackground.map { it.value.release() }
            playersBackground.clear()
        }
    }

    private fun synchronizedMainPlayers() {

        val mainListener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                playersBackground.map { if (isPlaying) it.value.play() else it.value.pause() }
                /*             if (isPlaying) {
                                 playAllPlayers()
                             }*/

                Log.d(TAG, "onIsPlayingChanged: $isPlaying")
            }

            override fun onPositionDiscontinuity(oldPosition: Player.PositionInfo, newPosition: Player.PositionInfo, reason: Int) {
                // This event we are using to sync main and other player seek time line.
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

                val durationMs = player.contentDuration
                if (durationMs > 0) {
                    val percentPosition: Float = (newPosition.positionMs.toFloat() / durationMs) //0.5 but got trim to 0
                    playersBackground.map {
                        it.value.apply {
                            val calPosition = (contentDuration * percentPosition).toLong()
                            seekTo(calPosition)
                            Log.d(TAG, "onPositionDiscontinuity: $calPosition/ ${it.value.contentDuration}")
                        }
                    }
                }
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                Log.d(TAG, "onPlayWhenReadyChanged: $playWhenReady")
                if (!playWhenReady) {
                    pauseAllPlayers() // Pause all players when main player is paused
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {

                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "onIsPlayingChanged: STATE_BUFFERING")
                        rePreparePlayersIfNecessary()
                    }

                    Player.STATE_ENDED -> {

                    }

                    Player.STATE_IDLE -> {
                        Log.d(TAG, "onIsPlayingChanged:STATE_IDLE")
                    }

                    Player.STATE_READY -> {
                        if (playersBackground.all { it.value.playbackState == Player.STATE_READY }) {
                            playAllPlayers()
                        }
                        Log.d(TAG, "onPlaybackStateChanged: STATE_READY")
                    }
                }
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)
                if (!timeline.isEmpty) {
                    val durationMs = timeline.getPeriod(0, Timeline.Period()).durationMs
                    Log.d(TAG, "onTimelineChanged: $durationMs")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                pauseAllPlayers()
            }
        }

        player.addListener(mainListener)
    }

    /*-------------*/


    private fun synchronizedBGPlayers() {

        val bgListener = object : Player.Listener {

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (!isPlaying) {
                    player.pause()
                }
                Log.i("MSessionService", "onIsPlayingChanged:BG_EVENT $isPlaying ")
            }

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                super.onPlayWhenReadyChanged(playWhenReady, reason)
                Log.i(TAG, "onPlayWhenReadyChanged: $playWhenReady")
                if (!playWhenReady && player.isPlaying) {
                    player.pause() // Pause main player if any other player pauses
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                pauseAllPlayers()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when (playbackState) {

                    Player.STATE_BUFFERING -> {
                        Log.i("MSessionService", "onIsPlayingChanged:BG_EVENT STATE_BUFFERING")
                    }

                    Player.STATE_ENDED -> {}

                    Player.STATE_IDLE -> {}

                    Player.STATE_READY -> {
                        Log.i("MSessionService", "onIsPlayingChanged:BG_EVENT STATE_READY")
                        if (playersBackground.all { it.value.playbackState == Player.STATE_READY }) {
                            playAllPlayers()
                        }
                    }
                }
            }
        }
        playersBackground.forEach { it.value.addListener(bgListener) }
    }


    // Function to play all players
    fun playAllPlayers() {
        if (player.playbackState == Player.STATE_READY) {
            playersBackground.forEach { players ->
                if (players.value.playbackState == Player.STATE_READY) {
                    players.value.play()
                    Log.d(TAG, "playAllPlayers: isREADY")
                } else {
                    Log.e(TAG, "playAllPlayers: isNOTREADY")
                }
            }
            player.play()
        }
    }

    // Function to pause all players
    fun pauseAllPlayers() {
        player.pause()
        playersBackground.forEach { player ->
            player.value.pause()
        }
    }

    fun rePreparePlayersIfNecessary() {
        playersBackground.forEach { map ->
            val player = map.value
            if (player.playbackState != Player.STATE_READY && player.playbackState != Player.STATE_BUFFERING) {
                player.prepare()  // Reprepare player to ensure media is ready
            }
        }
    }

}

