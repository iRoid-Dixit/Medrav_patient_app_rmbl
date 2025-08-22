package com.medrevpatient.mobile.app.ui.compose.common.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.medrevpatient.mobile.app.BuildConfig
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.utils.AppUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class VideoPickerManager(
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry,
    private val videoDurationRestriction: Int = 300,
    private val onVideoPicked: (Boolean, String, Bitmap?) -> Unit,
    private val onError: (String) -> Unit,
    private val onLoaderStateChange: (Boolean) -> Unit = {}, // Add loader callback
) {
    private var pendingVideos = mutableListOf<Pair<String, Bitmap?>>()
    private var isProcessingMultipleVideos = false
    private val lengthError = String.format(
        context.getString(R.string.error_video_length),
        videoDurationRestriction
    )
    private val pickMultipleMediaLauncher = activityResultRegistry.register(
        "pick_multiple_media",
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        handleMultipleMediaResults(uris)
    }
    private val captureVideoLauncher = activityResultRegistry.register(
        "capture_video",
        ActivityResultContracts.CaptureVideo()
    ) { captured ->
        if (captured) {
            handleMediaResult(capturedVideoUri)
        } else {
            onError("Video capture failed")
        }
    }
    private val permissionLauncher = activityResultRegistry.register(
        "camera_permission",
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            createUriForCaptureVideo(context)
        } else {
            onError("Camera permission denied")
        }
    }
    private var capturedVideoUri: Uri? = null
    fun pickMediaFromGallery() {
        pickMultipleMediaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
        )
    }

    fun captureVideo() {
        val permissionCheckResult =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            createUriForCaptureVideo(context)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun handleMultipleMediaResults(uris: List<Uri>) {
        if (uris.isNotEmpty()) {
            isProcessingMultipleVideos = true
            pendingVideos.clear()

            // Count videos to process
            val videoUris = uris.filter { uri ->
                val mimeType = context.contentResolver.getType(uri) ?: ""
                mimeType.startsWith("video")
            }

            if (videoUris.isNotEmpty()) {
                onLoaderStateChange(true) // Show loader for multiple videos
            }

            var processedCount = 0
            val totalVideos = videoUris.size
            
            uris.forEach { uri ->
                val mimeType = context.contentResolver.getType(uri) ?: ""
                if (mimeType.startsWith("video")) {
                    handleVideo(uri) { videoPath, bitmap ->
                        pendingVideos.add(videoPath to bitmap)
                        processedCount++

                        // When all videos are processed, return them as batch
                        if (processedCount == totalVideos) {
                            isProcessingMultipleVideos = false
                            onLoaderStateChange(false) // Hide loader
                            pendingVideos.forEach { (path, thumb) ->
                                onVideoPicked(false, path, thumb)
                            }
                            pendingVideos.clear()
                        }
                    }
                } else {
                    handleImage(uri)
                }
            }
        } else {
            onError("No media selected")
        }
    }
    private fun handleMediaResult(uri: Uri?) {
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri) ?: ""
            if (mimeType.startsWith("video")) {
                handleVideo(uri) // Single video, use normal flow
            } else {
                handleImage(uri)
            }
        } else {
            onError("Invalid media")
        }
    }

    private fun handleVideo(uri: Uri, onComplete: ((String, Bitmap?) -> Unit)? = null) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, uri)
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            val durationSec = durationMs / 1000

            if (durationSec <= videoDurationRestriction) {
                Logger.e("Picked video URI: $uri, Duration: $durationSec seconds")

                val bitmap: Bitmap? =
                    retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST)

                // Get real path of the video
                val recordedVideoPath = AppUtils.getFileFromContentUri(
                    context, uri,
                    Constants.AppInfo.FILE_PREFIX_NAME.plus(System.currentTimeMillis())
                )?.absolutePath ?: ""

                Logger.e("Recorded video path: $recordedVideoPath")

                // 🔁 Compress the video using VideoCompressor


            } else {
                onError(lengthError)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Profile", "Error retrieving video duration")
        } finally {
            retriever.release()
        }
    }



    private fun handleImage(uri: Uri) {
        val imagePath = AppUtils.getFileFromContentUri(
            context, uri,
            Constants.AppInfo.FILE_PREFIX_NAME.plus(System.currentTimeMillis())
        )?.absolutePath ?: ""
        onVideoPicked(true, imagePath, null)
    }
    private fun createUriForCaptureVideo(context: Context) {
        val file = context.createVideoFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        Logger.e("capture uri: $uri")
        capturedVideoUri = uri
        captureVideoLauncher.launch(uri)
    }
    @SuppressLint("SimpleDateFormat")
    fun Context.createVideoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val videoFileName = "MP4_" + timeStamp + "_"
        return File.createTempFile(
            videoFileName,
            ".mp4",
            externalCacheDir
        )
    }
}

