package com.griotlegacy.mobile.app.ui.compose.common.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import co.touchlab.kermit.Logger
import com.griotlegacy.mobile.app.BuildConfig
import com.griotlegacy.mobile.app.data.source.Constants
import com.griotlegacy.mobile.app.utils.AppUtils

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class PhotoPickerManager(
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry,
    private val onPhotoPicked: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onCameraErrorClick:()->Unit={},
) {
    private val pickImageLauncher = activityResultRegistry.register(
        "pick_image",
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        handlePhotoResult(uri)
    }
    private val captureImageLauncher = activityResultRegistry.register(
        "capture_image",
        ActivityResultContracts.TakePicture()
    ) { captured ->
        if (captured) {
            capturedImageUri?.let { handlePhotoResult(it) }
        } else {
            onCameraErrorClick()
        }
    }
    private val permissionLauncher = activityResultRegistry.register(
        "camera_permission",
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            createUriForCaptureImg(context)
        } else {
            onError("Camera permission denied")
        }
    }
    private var capturedImageUri: Uri? = null
    fun pickPhotoFromGallery() {
        pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
    fun capturePhoto() {
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            createUriForCaptureImg(context)
        } else {
            onError("Camera permission denied")
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    /*private fun startCameraCapture() {
        val file = File(context.externalCacheDir, "photo_${System.currentTimeMillis()}.jpg")
        capturedImageUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        captureImageLauncher.launch(capturedImageUri)
    }*/

    private fun handlePhotoResult(uri: Uri?) {
        if (uri != null) {
            val photoPath = AppUtils.getFileFromContentUri(
                context, uri,
                Constants.AppInfo.DIR_NAME.plus(System.currentTimeMillis())
            )?.absolutePath ?: ""

            onPhotoPicked(photoPath)
        } else {
            //  onError("No image selected")
        }
    }

    private fun createUriForCaptureImg(context: Context) {
        val file = context.createImageFile()
        val uri = FileProvider.getUriForFile(
            Objects.requireNonNull(context),
            BuildConfig.APPLICATION_ID + ".provider", file
        )
        capturedImageUri = uri
        captureImageLauncher.launch(uri)

        Logger.e("capture uri: $uri")
    }

    @SuppressLint("SimpleDateFormat")
    fun Context.createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            externalCacheDir      /* directory */
        )
        return image
    }
}
